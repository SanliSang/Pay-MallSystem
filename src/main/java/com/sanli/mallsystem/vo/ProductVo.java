package com.sanli.mallsystem.vo;

import com.sanli.mallsystem.pojo.Product;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

/**
 * 商品VO
 */
@Data
public class ProductVo {
    private Integer id;

    private Integer categoryId;

    private String name;

    private String subtitle;

    private String mainImage;

    private Integer status;

    private BigDecimal price;

    public static ProductVo toProductVo(Product product){
        ProductVo productVo = new ProductVo();
        BeanUtils.copyProperties(product,productVo);
        return productVo;
    }

}
