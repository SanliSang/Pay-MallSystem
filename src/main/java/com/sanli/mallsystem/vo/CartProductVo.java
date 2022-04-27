package com.sanli.mallsystem.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 主要强调的是购物车内的商品
 */
@Data
public class CartProductVo {
    private Integer productId;

    /**
     * 购物车的商品数量
     */
    private Integer quantity;
    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品子标题
     */
    private String productSubtitle;
    /**
     * 商品主图片
     */
    private String productMainImage;
    /**
     * 商品单价
     */
    private BigDecimal productPrice;
    /**
     * 商品状态
     */
    private Integer productStatus;
    /**
     * 购物车的商品总价
     */
    private BigDecimal productTotalPrice;
    /**
     * 购物车的商品状态
     */
    private Integer productStock;
    /**
     * 商品是否被选中
     */
    private Boolean productSelected;

    public CartProductVo() {
    }

    public CartProductVo(Integer productId, Integer quantity, String productName, String productSubtitle, String productMainImage, BigDecimal productPrice, Integer productStatus, BigDecimal productTotalPrice, Integer productStock, Boolean productSelected) {
        this.productId = productId;
        this.quantity = quantity;
        this.productName = productName;
        this.productSubtitle = productSubtitle;
        this.productMainImage = productMainImage;
        this.productPrice = productPrice;
        this.productStatus = productStatus;
        this.productTotalPrice = productTotalPrice;
        this.productStock = productStock;
        this.productSelected = productSelected;
    }
}
