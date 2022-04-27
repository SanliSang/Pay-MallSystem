package com.sanli.paysystem.service;

import com.sanli.paysystem.form.CartAddForm;
import com.sanli.paysystem.form.CartUpdateForm;
import com.sanli.paysystem.vo.CartVo;
import com.sanli.paysystem.vo.ResponseVo;

public interface ICartService {

    /**
     * 根据指定uid添加商品列表
     * @param uid
     * @param cartAddForm
     * @return
     */
    public ResponseVo<CartVo> add(Integer uid , CartAddForm cartAddForm);

    /**
     * 获取指定uid的购物车所有商品信息
     * @param uid
     * @return
     */
    public ResponseVo<CartVo> list(Integer uid);

    /**
     * 更新购物车接口
     * @param uid
     * @param productId
     * @param form
     * @return
     */
    public ResponseVo<CartVo> update(Integer uid , Integer productId , CartUpdateForm form);


    /**
     * 删除购物商品
     * @param uid
     * @param productId
     * @return
     */
    public ResponseVo<CartVo> delete(Integer uid , Integer productId);

    /**
     * 购物车全选
     * @param uid
     * @return
     */
    public ResponseVo<CartVo> selectAll(Integer uid);

    /**
     * 购物车全不选
     * @param uid
     * @return
     */
    public ResponseVo<CartVo> unSelectAll(Integer uid);

    /**
     * 获取商品总数
     * @param uid
     * @return
     */
    public ResponseVo<Integer> productSize(Integer uid);
}
