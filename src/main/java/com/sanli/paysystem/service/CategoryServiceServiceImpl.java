package com.sanli.paysystem.service;

import com.sanli.paysystem.dao.CategoryMapper;
import com.sanli.paysystem.enums.ResponseStatusEnum;
import com.sanli.paysystem.pojo.Category;
import com.sanli.paysystem.vo.CategoryVo;
import com.sanli.paysystem.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.sanli.paysystem.coust.CategoryConst.ROOT_CATEGORY;

@Service
public class CategoryServiceServiceImpl implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 查询所有商品类目（包括子商品类目）
     * 1、从所有商品类目中查出所有根商品类目
     * 2、根据所有根商品类目，从所有商品类目中查出根商品类目下对应的子商品类目
     * 3、将查出的子商品类目作为根商品类目通过递归的方式调用步骤2，直至查出所有商品类目
     * @return
     */
    @Override
    public ResponseVo<List<CategoryVo>> selectCategories() {
        // 1、从所有商品类目中查出所有根商品类目
        List<Category> categoryList = categoryMapper.selectCategories();
        LinkedList<CategoryVo> list = new LinkedList<>();
        for (Category category : categoryList) {
            // 从所有商品类目中查询根目录的商品类目（根目录商品类目id统一为0）
            if (category.getParentId() == ROOT_CATEGORY){
                CategoryVo categoryVo = new CategoryVo();
                BeanUtils.copyProperties(category,categoryVo);
                list.add(categoryVo);
            }
        }
        // 2、根据所有根商品类目，从所有商品类目中查出根商品类目下对应的子商品类目
        selectCategoriesWithSub(list, categoryList);
        return ResponseVo.success(ResponseStatusEnum.SUCCESS,list);
    }

    /**
     * 从所有商品类别中，根据根商品类目，查出对应其子商品类目（递归调用可获取所有子类目）
     * @param root 根商品类目列表
     * @param data 所有商品类目（数据源）
     * @return 带有对应子类目的根类目列表
     */
    private void selectCategoriesWithSub(List<CategoryVo> root , List<Category> data){
        if (data == null || root == null) return;
        for (CategoryVo rootCategory : root) { // 遍历根目录列表
            LinkedList<CategoryVo> rootOfSub = new LinkedList<>(); // 存储对应根目录的子目录列表
            for (Category category : data) { // 数据源
                if (rootCategory.getId().equals(category.getParentId())){ // 根目录的id恰好是子目录的parent_id时，找到根目录对应的子目录
                    // 将子目录转化成Vo后放入对应地根目录列表中
                    rootOfSub.add(CategoryVo.toCategoryVo(category));
                }
                // 3、将查出的子商品类目作为根商品类目通过递归的方式调用步骤2，直至查出所有商品类目
                selectCategoriesWithSub(rootOfSub,data);
            }
            rootCategory.setSubCategories(rootOfSub); // 将子目录列表存储在对应地根目录上
            // 优先级大的优先排在首位
            rootOfSub.sort((o1, o2) -> {
                return o2.getSortOrder() - o1.getSortOrder(); // 从大到小排列
            });
        }
        // 此时根目录列表已经带有所有的子目录列表
    }

    /**
     * 获取带有categoryId的所有子类目Id集合的Vo
     * @param categoryId 商品类目Id
     * @param results 存储categoryId对应的所有子目录Id的集合
     * @return 返回存储categoryId所有子目录的VO（注意不是返回商品类目对象集合，而是返回商品类目的categoryId集合）
     */
    @Override
    public ResponseVo<Set<Integer>> selectCategoriesSubIdSet(Integer categoryId , Set<Integer> results) {
        // 先获取所有所有商品类目数据（可以考虑缓存，因为会调用多次）
        List<Category> data = categoryMapper.selectCategories(); // 先查出数据源
        // 从所有商品类目中查询指定的商品类目Id集合
        selectCategoriesSubIdSet(categoryId,results,data);
        return ResponseVo.success(ResponseStatusEnum.SUCCESS,results);
    }

    /**
     * 从所有商品类目中查找指定的商品类目categoryId的子商品类目的Id集合（不包括自身categoryId）
     * @param categoryId
     * @param results
     * @param data
     */
    private void selectCategoriesSubIdSet(Integer categoryId , Set<Integer> results , List<Category> data) {
        for (Category category : data) {
            if (category.getParentId().equals(categoryId)){ // 数据源的parent_id等于id
                results.add(category.getId());
                selectCategoriesSubIdSet(category.getId(),results,data);
            }
        }
    }
}
