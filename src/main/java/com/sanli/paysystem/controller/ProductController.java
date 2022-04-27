package com.sanli.paysystem.controller;

import com.github.pagehelper.PageInfo;
import com.sanli.paysystem.controller.CheckGroup.registry;
import com.sanli.paysystem.form.UserForm;
import com.sanli.paysystem.service.ProductServiceImpl;
import com.sanli.paysystem.vo.ProductDetailVo;
import com.sanli.paysystem.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
public class ProductController {
    @Autowired
    private ProductServiceImpl productService;

    @GetMapping("/products")
    public ResponseVo<PageInfo> getProductsByCategoryId(@RequestParam(value = "categoryId" , required = false) Integer categoryId,
                                                        @RequestParam(value = "pageNum" , required = false) Integer pageNum,
                                                        @RequestParam(value = "pageSize" , required = false) Integer pageSize){
        return productService.productList(categoryId, pageNum, pageSize);
    }

    @GetMapping("/product/{productId}")
    public ResponseVo<ProductDetailVo> getProductDetailById(@PathVariable("productId") Integer productId){
        return productService.productById(productId);
    }
}
