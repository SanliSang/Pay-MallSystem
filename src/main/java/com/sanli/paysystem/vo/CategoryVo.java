package com.sanli.paysystem.vo;

import com.sanli.paysystem.pojo.Category;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Data
public class CategoryVo {
    private Integer id;

    private Integer parentId;

    private String name;

    private Integer sortOrder;

    private List<CategoryVo> subCategories;

    public static CategoryVo toCategoryVo(Category category){
        CategoryVo categoryVo = new CategoryVo();
        BeanUtils.copyProperties(category,categoryVo);
        return categoryVo;
    }
}
