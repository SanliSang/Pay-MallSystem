package com.sanli.mallsystem.enums;

import lombok.Getter;

@Getter
public enum PaymentStatusEnum {
    /**
     * 订单状态:0-已取消-10-未付款，20-已付款，40-已发货，50-交易成功，60-交易关闭
     */
    CANCELED(0,"已取消"),
    UNPAID(10,"未付款"),
    PAID(20,"已付款"),
    NOT_SHIPPED(30,"未发货"),
    SHIPPED(40,"已发货"),
    TRADE_SUCCESS(50,"交易成功"),
    TRADE_FAIL(60,"交易失败")
    ;

    Integer status;
    String comment;

    PaymentStatusEnum(Integer status, String comment) {
        this.status = status;
        this.comment = comment;
    }
}
