package com.sanli.paysystem.dao;

import com.sanli.paysystem.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    int deleteIdAndShippingId(@Param("uid") int uid , @Param("shippingId") int shippingId);

    List<Shipping> selectByUid(Integer uid);

    Shipping selectByUidAndShippingId(@Param("uid") int uid , @Param("shippingId") int shippingId);

    List<Shipping> selectByShippingIdSet(@Param("shippingIdSet")Set<Integer> shippingIdSet);
}