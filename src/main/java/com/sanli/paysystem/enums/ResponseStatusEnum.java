package com.sanli.paysystem.enums;

public enum  ResponseStatusEnum {
    // 偶数成功，奇数失败

    SERVICE_ERROR(-3,"服务器错误"),
    FAIL(-1,"错误"),
    SUCCESS(0,"成功"),
    NOT_LOGGED(1,"用户未登录"),
    USER_EXIST(2,"用户已存在"),
    EMAIL_EXIST(3,"邮箱已绑定"),
    REGISTRY_SUCCESS(10,"注册成功"),
    LOGIN_SUCCESS(11,"登录成功"),
    REGISTRY_FAIL(12,"注册失败"),
    USERNAME_OR_PASSWORD_ERROR(13,"用户名或密码错误"),
    LOGOUT_SUCCESS(14,"登出成功"),

    PARAM_ERROR(15,"参数错误"),
    REQUEST_BODY_MISSING(16,"缺少请求Body参数"),

    PRODUCT_NOT_EXIST(100,"商品不存在"),
    PRODUCT_OFF_SELL_OF_DELETE(101,"产品已下架或被删除"),
    LACK_OF_STOCK(102,"库存不足"),
    EMPTY_STOCK(103,"暂无库存"),

    ADD_SHIPPING_FAIL(110,"添加收货地址失败"),
    DELETE_SHIPPING_FAIL(111,"删除收货地址失败"),
    UPDATE_SHIPPING_FAIL(112,"更新收货地址失败"),

    SHIPPING_NOT_EXIST(120,"收货地址不存在"),
    CART_SELECT_EMPTY(130,"购物车无选中或在售商品"),

    ORDER_NOT_EXIST(140,"订单号不存在"),
    ORDER_ERROR(141,"订单号错误"),
    ORDER_STATUS_EXCEPTION(142,"订单状态异常"),
    CANNOT_CHANGE_PAID_ORDER(143,"无法更改已支付的订单"),
    ;

    int status;
    String desc;

    ResponseStatusEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public int getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
