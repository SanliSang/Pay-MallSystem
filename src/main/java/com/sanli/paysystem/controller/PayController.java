package com.sanli.paysystem.controller;

import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.enums.BestPayPlatformEnum;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayResponse;
import com.sanli.paysystem.pojo.PayInfo;
import com.sanli.paysystem.service.PayServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;

@Slf4j
@Controller
@RequestMapping("/pay")
public class PayController {

    @Autowired
    private PayServiceImpl payService;

    @Autowired
    WxPayConfig wxPayConfig;

    @GetMapping("/create")
    public ModelAndView create(String orderId , String orderAmount , @RequestParam("payType") BestPayTypeEnum bestPayTypeEnum){
        if (bestPayTypeEnum == BestPayTypeEnum.WXPAY_NATIVE){
            PayResponse response = payService.create(orderId , orderAmount , bestPayTypeEnum); // 注意获取到的response的orderId以及orderAmount都是null
            HashMap<String, String> result = new HashMap<>();
            result.put("OrderNo",orderId);
            log.info("returnUrl = {}",wxPayConfig.getReturnUrl());
            result.put("ReturnUrl",wxPayConfig.getReturnUrl());
            result.put("CodeUrl",response.getCodeUrl());
            return new ModelAndView("WxNativePay",result);
        }else if (bestPayTypeEnum == BestPayTypeEnum.ALIPAY_PC){
            PayResponse response = payService.create(orderId, orderAmount, bestPayTypeEnum);
            HashMap<String, String> result = new HashMap<>();
            result.put("body",response.getBody());
            return new ModelAndView("AliPcPay",result);
        } else {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("message","暂不支持的支付类型！");
            return new ModelAndView("fail.html",hashMap);
        }
    }

    @PostMapping("/notify_url")
    @ResponseBody
    public String notify_url(@RequestBody String body){
        log.info("body={}",body);

        // 验证金额、验证订单号以及支付状态移交给Service层处理，因为这部分内容属于业务处理，不应该由业务层处理
        BestPayPlatformEnum verify = payService.verify(body);

        // 业务层可处理跳转和渲染对应的支付页面
        if (verify == BestPayPlatformEnum.ALIPAY){
            return "success";
        }else if (verify == BestPayPlatformEnum.WX){
            return "<xml>\n" +
                "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                "</xml>";
        }else throw new RuntimeException("错误的回调方式！");
    }

    @GetMapping("/queryOrderByNo")
    @ResponseBody
    public PayInfo queryOrderByNo(Long orderNo){
        return payService.queryOrderByNo(orderNo);
    }
}
