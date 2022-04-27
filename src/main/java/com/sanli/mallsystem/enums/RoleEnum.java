package com.sanli.mallsystem.enums;

public enum  RoleEnum {
    ADMINISTRATOR(0,"管理员"),
    CUSTOMER(1,"普通用户");

    int code;
    String comment;

    RoleEnum(int code, String comment) {
        this.code = code;
        this.comment = comment;
    }
}
