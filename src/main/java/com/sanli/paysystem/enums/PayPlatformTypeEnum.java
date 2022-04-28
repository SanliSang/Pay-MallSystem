package com.sanli.paysystem.enums;

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
    public static com.sanli.paysystem.enums.PayPlatformTypeEnum getPayPlatformType(BestPayTypeEnum bestPayTypeEnum){
        if (bestPayTypeEnum.getPlatform().name().equals(com.sanli.paysystem.enums.PayPlatformTypeEnum.WX.name())){
            return com.sanli.paysystem.enums.PayPlatformTypeEnum.WX;
        }else if (bestPayTypeEnum.getPlatform().name().equals(com.sanli.paysystem.enums.PayPlatformTypeEnum.ALIPAY.name())){
            return com.sanli.paysystem.enums.PayPlatformTypeEnum.ALIPAY;
        }

        com.sanli.paysystem.enums.PayPlatformTypeEnum[] values = com.sanli.paysystem.enums.PayPlatformTypeEnum.values();
        for (com.sanli.paysystem.enums.PayPlatformTypeEnum value : values) {
            if (bestPayTypeEnum.getPlatform().name().equals(value.name())){
                return value;
            }
        }
        throw new RuntimeException("错误的支付平台类型: "+bestPayTypeEnum.getPlatform().name());
    }
}
