package com.sanli.mallsystem.service;

import com.lly835.bestpay.enums.BestPayPlatformEnum;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.enums.OrderStatusEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.service.impl.BestPayServiceImpl;
import com.sanli.mallsystem.dao.PayInfoMapper;
import com.sanli.mallsystem.enums.PayPlatformTypeEnum;
import com.sanli.mallsystem.pojo.PayInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Slf4j
@Service
public class PayServiceImpl implements IPayService{

    @Autowired
    private BestPayServiceImpl bestPayService;

    @Autowired
    private PayInfoMapper payInfoMapper;

    @Override
    public PayResponse create(String orderId , String orderAmount , BestPayTypeEnum bestPayTypeEnum) {
        // 创建请求
        PayRequest request = new PayRequest();
        request.setOrderId(orderId);
        request.setOrderName("Hello World");
        request.setPayTypeEnum(bestPayTypeEnum);
        request.setOrderAmount(Double.valueOf(orderAmount));
        // 发起请求
        PayResponse response = bestPayService.pay(request);
        
        // 写入数据库（可以在异步通知回调处再写入数据库，更进一步，写入数据库的操作可以写入mq移交给其他系统模块处理，然后直接相应异步通知...）
        PayInfo payInfo = new PayInfo(Long.parseLong(orderId),
                PayPlatformTypeEnum.getPayPlatformType(bestPayTypeEnum).getCode(),
                response.getOutTradeNo(),
                OrderStatusEnum.NOTPAY.name(),
                new BigDecimal(orderAmount));
        payInfoMapper.insertSelective(payInfo);

        log.info("response = {}",response);
        return response;
    }

    // 支付验证
    // 这里不建议返回PayResponse，因为Controller并不需要PayResponse的所有内容，只需要知道使用哪一种支付方式跳转到哪一种支付的页面即可
    // 所以应该返回支付平台
    @Override
    public BestPayPlatformEnum verify(String body){
        // 签名验证并将String类型的body响应结果封装成PayResponse对象，方便获取
        PayResponse payResponse = bestPayService.asyncNotify(body);

        // 支付状态校验（从数据库中查出对应订单号）
        PayInfo payInfo = payInfoMapper.selectByOrderNo(Long.parseLong(payResponse.getOrderId()));
        if (payInfo == null){ // 如果查出对应的订单号则标识存在该订单号，否则抛出没有订单好的异常
            throw new RuntimeException("查无此订单号:"+payResponse.getOrderId());
        }

        // 检查支付状态（若已支付则无需修改状态，若未支付则继续判断金额是否一致）
        if (payInfo.getPlatformStatus().equals(OrderStatusEnum.NOTPAY.name())){ // 未支付状态NOTPAY则继续检查金额是否一致
            if (!payInfo.getPayAmount().equals(BigDecimal.valueOf(payResponse.getOrderAmount()))){
                throw new RuntimeException("金额不一致:"+payResponse.getOrderAmount());
            }
            // 执行到此处则修改订单支付状态
            payInfo.setPlatformStatus(OrderStatusEnum.SUCCESS.name());
            payInfo.setPlatformNumber(payResponse.getOutTradeNo());
            payInfoMapper.updateByPrimaryKey(payInfo); // 更新订单状态
        }

        // 判断支付平台，控制跳转
        if (payResponse.getPayPlatformEnum() == BestPayPlatformEnum.ALIPAY){
            return BestPayPlatformEnum.ALIPAY;
        }else if (payResponse.getPayPlatformEnum() == BestPayPlatformEnum.WX){
            return BestPayPlatformEnum.WX;
        }else throw new RuntimeException("错误的支付类型！"); // 建议一开始判断支付平台类型，若修改数据库后再判断，一旦出现错误则需要重新修改数据库
    }


    public PayInfo queryOrderByNo(Long orderNo){
        return payInfoMapper.selectByOrderNo(orderNo);
    }
}
