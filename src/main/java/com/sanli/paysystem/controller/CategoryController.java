package com.sanli.paysystem.controller;

import com.sanli.paysystem.service.CategoryServiceServiceImpl;
import com.sanli.paysystem.vo.CategoryVo;
import com.sanli.paysystem.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class CategoryController {

    @Autowired
    private CategoryServiceServiceImpl categoryService;

    @GetMapping("/categories")
    @ResponseBody
    public ResponseVo<List<CategoryVo>> getCategories(){
        return categoryService.selectCategories();
    }


    @GetMapping("/categories/{id}")
    @ResponseBody
    public ResponseVo<Set<Integer>> getCategoriesById(@PathVariable("id") Integer id){
        return categoryService.selectCategoriesSubIdSet(id,new HashSet<>());
    }
}
