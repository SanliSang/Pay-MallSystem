package com.sanli.mallsystem.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sanli.mallsystem.dao.ShippingMapper;
import com.sanli.mallsystem.form.ShippingForm;
import com.sanli.mallsystem.pojo.Shipping;
import com.sanli.mallsystem.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sanli.mallsystem.enums.ResponseStatusEnum.*;

@Service
public class ShippingServiceImpl implements IShippingService{

    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ResponseVo<Map<String,Integer>> add(Integer uid , ShippingForm form) {
        Shipping shipping = ShippingForm.toShipping(form);
        shipping.setUserId(uid);
        int row = shippingMapper.insertSelective(shipping);
        if (row == 0){ // 添加失败
            return ResponseVo.error(ADD_SHIPPING_FAIL);
        }
        HashMap<String, Integer> map = new HashMap<>();
        // 想要在调用insert中返回插入后自增的id（主键），需要在mybatis中设置，useGeneratedKeys="true",keyProperty="id"，否则返回的shipping中无法带有
        /**
         * 原理：通过insertSelective中传入的Shipping对象中注入主键自增的信息
         * 目的：在插入数据时能够返回自增主键数据，而不再需要重新在数据库中查询一次
         */
        map.put("shippingId",shipping.getId()); // 前端需要根据shippingId做操作标识
        return ResponseVo.success(map);
    }

    @Override
    public ResponseVo delete(Integer uid , Integer shippingId) {
        // 错误写法：int row = shippingMapper.deleteByPrimaryKey(shippingId);
        // 应该先判断uid与shippingId是否由绑定关系再进行删除，否则就会删除其他人的收货地址，所以删除方法的SQL要自己写
        int row = shippingMapper.deleteIdAndShippingId(uid, shippingId);
        if (row == 0){
            return ResponseVo.error(DELETE_SHIPPING_FAIL);
        }
        return ResponseVo.success(); // 删除成功
    }

    @Override
    public ResponseVo update(Integer uid , Integer shippingId , ShippingForm newForm) {
        Shipping shipping = ShippingForm.toShipping(newForm);
        shipping.setUserId(uid);
        shipping.setId(shippingId);
        int row = shippingMapper.updateByPrimaryKeySelective(shipping);
        if (row == 0) {
            return ResponseVo.error(UPDATE_SHIPPING_FAIL);
        }
        return ResponseVo.success();
    }

    @Override
    public ResponseVo<PageInfo> list(Integer uid , Integer pageNum , Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippings = shippingMapper.selectByUid(uid);
        PageInfo<Shipping> pageInfo = new PageInfo<>(shippings);
        return ResponseVo.success(pageInfo);
    }
}
