package com.sanli.paysystem.form;

import lombok.Data;

/**
 * 更改购物车Vo
 */
@Data
public class CartUpdateForm {
    private Integer quantity;
    private Boolean selected;

    public CartUpdateForm() {
    }

    public CartUpdateForm(Integer quantity) {
        this.quantity = quantity;
    }

    public CartUpdateForm(Boolean selected) {
        this.selected = selected;
    }

    public CartUpdateForm(Integer quantity, Boolean selected) {
        this.quantity = quantity;
        this.selected = selected;
    }
}
