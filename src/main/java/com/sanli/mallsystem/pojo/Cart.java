package com.sanli.mallsystem.pojo;


import lombok.Data;

/**
 * 购物车信息（采用redis存储）
 */
@Data
public class Cart {
    private Integer productId;
    private Integer quantity;
    private Boolean productSelected;

    public Cart() {
    }

    public Cart(Integer productId, Integer quantity, Boolean productSelected) {
        this.productId = productId;
        this.quantity = quantity;
        this.productSelected = productSelected;
    }
}
