package com.sanli.mallsystem.enums;

import com.lly835.bestpay.enums.BestPayTypeEnum;

public enum PayPlatformTypeEnum {
    ALIPAY(1,"支付宝平台"),
    WX(2,"微信平台");

    int code;
    String common;

    PayPlatformTypeEnum(int code, String common) {
        this.code = code;
        this.common = common;
    }

    public int getCode() {
        return code;
    }

    public String getCommon() {
        return common;
    }

    // 使用API判断，不要使用源码PayPlatformTypeEnum.xxx的方式获取PayPlatformTypeEnum
    public static PayPlatformTypeEnum getPayPlatformType(BestPayTypeEnum bestPayTypeEnum){
        if (bestPayTypeEnum.getPlatform().name().equals(PayPlatformTypeEnum.WX.name())){
            return PayPlatformTypeEnum.WX;
        }else if (bestPayTypeEnum.getPlatform().name().equals(PayPlatformTypeEnum.ALIPAY.name())){
            return PayPlatformTypeEnum.ALIPAY;
        }

        PayPlatformTypeEnum[] values = PayPlatformTypeEnum.values();
        for (PayPlatformTypeEnum value : values) {
            if (bestPayTypeEnum.getPlatform().name().equals(value.name())){
                return value;
            }
        }
        throw new RuntimeException("错误的支付平台类型: "+bestPayTypeEnum.getPlatform().name());
    }
}
