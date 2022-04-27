package com.sanli.mallsystem.enums;

import lombok.Getter;

@Getter
public enum ProductStatusEnum {
    /**
     * 商品状态.1-在售 2-下架 3-删除
     */
    ON_SELL(1,"在售状态"),
    OFF_SELL(2,"下架状态"),
    DELETE(3,"已删除状态");

    private int status;
    private String comment;

    ProductStatusEnum(int status, String comment) {
        this.status = status;
        this.comment = comment;
    }
}
