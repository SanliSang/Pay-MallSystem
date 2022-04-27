package com.sanli.mallsystem.service;

import com.sanli.mallsystem.vo.CategoryVo;
import com.sanli.mallsystem.vo.ResponseVo;

import java.util.List;
import java.util.Set;

public interface ICategoryService {
    /**
     * 获取所有商品分类及其子分类
     * @return
     */
    public ResponseVo<List<CategoryVo>> selectCategories();

    /**
     * 获取指定categoryId商品分类及其子分类
     * @param categoryId
     * @param results 存储categoryId商品分类及其子分类的Set集合
     * @return
     */
    public ResponseVo<Set<Integer>> selectCategoriesSubIdSet(Integer categoryId , Set<Integer> results);
}
