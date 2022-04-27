package com.sanli.paysystem.dao;

import com.sanli.paysystem.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    int batchInsertOrderItemList(@Param("orderItemList") List<OrderItem> orderItemList);

    List<OrderItem> selectByOrderNoSet(@Param("orderNoSet") Set<Long> orderNoSet);

    List<OrderItem> selectByOrderNo(Long orderNo);
}