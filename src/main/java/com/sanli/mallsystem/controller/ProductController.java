package com.sanli.mallsystem.controller;

import com.github.pagehelper.PageInfo;
import com.sanli.mallsystem.service.ProductServiceImpl;
import com.sanli.mallsystem.vo.ProductDetailVo;
import com.sanli.mallsystem.vo.ResponseVo;
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
