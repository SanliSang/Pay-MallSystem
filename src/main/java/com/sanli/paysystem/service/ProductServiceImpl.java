package com.sanli.paysystem.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sanli.paysystem.dao.ProductMapper;
import com.sanli.paysystem.enums.ResponseStatusEnum;
import com.sanli.paysystem.pojo.Product;
import com.sanli.paysystem.vo.ProductDetailVo;
import com.sanli.paysystem.vo.ProductVo;
import com.sanli.paysystem.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import static com.sanli.paysystem.enums.ProductStatusEnum.*;
import static com.sanli.paysystem.enums.ResponseStatusEnum.PRODUCT_NOT_EXIST;

@Slf4j
@Service
public class ProductServiceImpl implements IProductService{

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryServiceServiceImpl categoryServiceService;

    /**
     * 根据商品类目categoryId查询指定大小页数的商品信息（采用PageInfo封装LinkedList<ProductVo>商品信息）
     * @param categoryId 商品类目id（若商品类目id为null则将所有商品信息）
     * @param pageNum 商品分页数
     * @param pageSize 商品数
     * @return
     */
    @Override
    public ResponseVo<PageInfo> productList(Integer categoryId, Integer pageNum, Integer pageSize) {
        HashSet<Integer> categoryIdSet = new HashSet<>();
        // 1、先查出categoryId下的所有子商品类目（此时并不包含自身商品类目），并存放在集合categoryIdSet中
        /**
         * 注意一种特殊情况：当categoryId为null时，mapper.xml应该不执行带有参数categoryIdSet的SQL语句
         * 应该查询出所有的商品列表的商品列表，但是在categoryIdSet中将包含null元素，导致mapper.xml中添加遍历categoryId不为空的条件
         * 导致没有查询到任何结果，所以需要判断categoryId是否为null后再加入到categoryIdSet内若为null，则查出所有category
         */
        // 若categoryId == null则没必要执行查询categoryId的子商品目录了，若在查询一次则会消耗性能
        if (categoryId != null){
            categoryServiceService.selectCategoriesSubIdSet(categoryId,categoryIdSet);
            // 将categoryId也加入到categoryIdSet中（因为自身商品类目categoryId可能就是最后级的类目，也就是categoryId后面也有可能会有商品）
            categoryIdSet.add(categoryId);
        }

        // 2、根据所有商品类目Id（包括自身）在商品信息表中查询所有商品信息（包括查出自身商品类目categoryId类目的商品）
        // 静态分页查询（将下列sql语句进行分页查询，将查询信息封装在page对象中，但不包含数据信息，仅包含分页信息）
        Page<Product> page = PageHelper.startPage(pageNum, pageSize);
        log.info("page ===> {}",page);

        List<Product> products = productMapper.selectByCategoryIdSet(categoryIdSet);
        log.info("products = {}",products);

        // 封装VO对象
        LinkedList<ProductVo> productVoList = new LinkedList<>();
        for (Product product : products) productVoList.add(ProductVo.toProductVo(product));

        // 这里需要分页查询结果统一采用PageInfo作为模板类返回，因为PageInfo中封装有当前有关的分页信息（主要包括：当前页数、每一页大小）
        /**
         * 这里解释这个迷惑的操作：
         * 因为我们要返回的格式想要包含当前页数、页数总数、每页数量...等信息，这些信息需要从products获取，
         * 而且当前的list并不是List<Product>，而是根据List<Product>计算出List<Page>（这部分可以自己看源码）
         * 但我们需要返回的格式的list部分并不是Product对象，而是ProductVo对象，所以还需要调用setList将之前的Product覆盖成ProductVo
         * 一句话概括就是：使用pageInfo的套皮信息，但内核数据采用自定义的Vo格式
         * 但是仍然并不推荐使用这个，建议使用定指的模板，因为存在一部分不需要使用的数据，但是不太清除为什么传入products然后转型为Page就可以获取到对应地分页数据的这个原理
         * 暂时猜测为：先创建的Page，然后将分页数据放在Page中，然后再转型到List<Product>，最后将数据存储在Product。
         * 这种方式最大的好处就是解耦，不需要List<Product>与Page绑定调用，通过向下转型就可以获取到Page的功能与数据
         * 目前：这一功能自身我是无法实现使用自定义Vo格式就难以获取分页数据了，暂时无法代替使用以下方式作为返回的Vo
         */
        PageInfo pageInfo = new PageInfo(products);
        log.info("pageInfo_1 ===> {}" , pageInfo);
        pageInfo.setList(productVoList);
        log.info("pageInfo_2 ===> {}" , pageInfo);
        return ResponseVo.success(ResponseStatusEnum.SUCCESS,pageInfo);
    }

    /**
     * 根据productId查出具体的product商品
     * @param productId 具体产品Id
     * @return
     */
    @Override
    public ResponseVo<ProductDetailVo> productById(Integer productId) {
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) return ResponseVo.error(PRODUCT_NOT_EXIST);
        Integer status = product.getStatus();
        if (status.equals(OFF_SELL.getStatus()) || status.equals(DELETE.getStatus())){
            return ResponseVo.error(ResponseStatusEnum.PRODUCT_OFF_SELL_OF_DELETE);
        }
        return ResponseVo.success(ProductDetailVo.toProductDetailVo(product));
    }
}