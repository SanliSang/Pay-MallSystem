package com.sanli.paysystem.service;


import com.github.pagehelper.PageInfo;
import com.sanli.paysystem.form.ShippingForm;
import com.sanli.paysystem.vo.ResponseVo;

import java.util.Map;

public interface IShippingService {

    /**
     * 给指定用户uid添加收获地址
     * @param uid
     * @param form
     * @return 带有收货地址唯一标识的shippingId的key-value
     */
    public ResponseVo<Map<String , Integer>> add(Integer uid , ShippingForm form);

    /**
     * 给指定用户uid删除指定收货地址
     * @param uid
     * @param shippingId
     * @return
     */
    public ResponseVo delete(Integer uid , Integer shippingId);

    /**
     * 给指定用户uid更新shippingId的收货地址
     * @param uid
     * @param shippingId
     * @param newForm
     * @return
     */
    public ResponseVo update(Integer uid , Integer shippingId , ShippingForm newForm);

    /**
     * 查询指定uid用户的收货地址（支持分页查询）
     * @param uid
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ResponseVo<PageInfo> list(Integer uid , Integer pageNum , Integer pageSize);


}
