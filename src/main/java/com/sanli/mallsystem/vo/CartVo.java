package com.sanli.mallsystem.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartVo {
    private List<CartProductVo> cartProductVoList; // 商品列表
    private Boolean selectedAll; // 购物车商品是否全选
    private BigDecimal cartTotalPrice; // 购物车商品总价（全选的总价）
    private Integer cartTotalQuantity; // 购物车商品总数量

    public CartVo() {
    }

    public CartVo(List<CartProductVo> cartProductVoList, Boolean selectedAll, BigDecimal cartTotalPrice, Integer cartTotalQuantity) {
        this.cartProductVoList = cartProductVoList;
        this.selectedAll = selectedAll;
        this.cartTotalPrice = cartTotalPrice;
        this.cartTotalQuantity = cartTotalQuantity;
    }
}
