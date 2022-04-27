package com.sanli.mallsystem.service;

import com.github.pagehelper.PageInfo;
import com.sanli.mallsystem.vo.ProductDetailVo;
import com.sanli.mallsystem.vo.ResponseVo;


public interface IProductService {
    /**
     * 根据商品分类categoryId查询指定大小页数的商品信息
     * @param categoryId 商品类目id
     * @param pageNum 分页数
     * @param pageSize 每页商品数
     * @return
     */
    public ResponseVo<PageInfo> productList(Integer categoryId , Integer pageNum , Integer pageSize);

    /**
     * 根据具体商品productId查询产品具体信息
     * @param productId 具体产品Id
     * @return
     */
    public ResponseVo<ProductDetailVo> productById(Integer productId);
}
