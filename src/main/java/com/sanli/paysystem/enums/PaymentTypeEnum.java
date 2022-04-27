package com.sanli.paysystem.enums;

import lombok.Getter;

@Getter
public enum  PaymentTypeEnum {
    PAY_ONLINE(1,"在线支付")
    ;
    Integer status;
    String comment;

    PaymentTypeEnum(Integer status, String comment) {
        this.status = status;
        this.comment = comment;
    }
}
