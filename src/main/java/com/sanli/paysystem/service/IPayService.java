package com.sanli.paysystem.service;

import com.lly835.bestpay.enums.BestPayPlatformEnum;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayResponse;
import com.sanli.paysystem.pojo.PayInfo;

public interface IPayService {
    /**
     * 创建指定类型的支付订单入口
     * @param orderId 订单号
     * @param orderAmount 订单金额
     * @param bestPayTypeEnum 支付平台类型
     * @return 支付响应
     */
    public PayResponse create(String orderId , String orderAmount , BestPayTypeEnum bestPayTypeEnum);

    /**
     * 订单校验：参数校验 + 签名校验 + 订单号校验 + 金额校验 + 支付状态校验
     * @param body
     * @return
     */
    public BestPayPlatformEnum verify(String body);

    /**
     * 根据订单号查询订单信息与状态
     * @param orderNo
     * @return
     */
    public PayInfo queryOrderByNo(Long orderNo);
}
