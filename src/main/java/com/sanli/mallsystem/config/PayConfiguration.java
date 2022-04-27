package com.sanli.mallsystem.config;

import com.lly835.bestpay.config.AliPayConfig;
import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.service.impl.BestPayServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PayConfiguration {

    @Autowired
    private WxAccountConfig wxAccountConfig;

    @Autowired
    private AliPayAccountConfig aliPayAccountConfig;



    @Bean
    public BestPayServiceImpl getBestPayServiceImpl(WxPayConfig wxPayConfig , AliPayConfig aliPayConfig){
        // BestPayServiceImpl就像一个通用模板类，需要转载对应地配置才可以使用
        BestPayServiceImpl bestPayService = new BestPayServiceImpl();
        bestPayService.setWxPayConfig(wxPayConfig);
        bestPayService.setAliPayConfig(aliPayConfig);
        return bestPayService;
    }

    @Bean
    public WxPayConfig getWxPayConfig(){
        WxPayConfig wxPayConfig = new WxPayConfig();
        // 若appId写错则返回AppID不存在
        // 若appId存在但没有绑定对应地商户id则报错appid和mch_id不匹配
        wxPayConfig.setAppId(wxAccountConfig.getAppId());
        // 若商户id写错则签名错误
        wxPayConfig.setMchId(wxAccountConfig.getMchId());
        // 若商户密钥写错则签名错误
        wxPayConfig.setMchKey(wxAccountConfig.getMchKey());
        // 若没有设置异步通知的回调地址则返回XML格式错误
        wxPayConfig.setNotifyUrl(wxAccountConfig.getNotifyUrl());
        wxPayConfig.setReturnUrl(wxAccountConfig.getReturnUrl());
        return wxPayConfig;
    }

    @Bean
    public AliPayConfig getAliPayConfig(){
        AliPayConfig aliPayConfig = new AliPayConfig();
        aliPayConfig.setAppId(aliPayAccountConfig.getAppId()); // 配置商户id
        aliPayConfig.setAliPayPublicKey(aliPayAccountConfig.getAliPayPublicKey()); // 支付宝公钥（用于签名校验）
        aliPayConfig.setPrivateKey(aliPayAccountConfig.getPrivateKey()); // 配置商户私钥
        aliPayConfig.setReturnUrl(aliPayAccountConfig.getReturnUrl()); // 配置用户支付后3秒后跳转的页面
        aliPayConfig.setNotifyUrl(aliPayAccountConfig.getNotifyUrl()); // 配置异步通知回调的URL
        return aliPayConfig;
    }
}
