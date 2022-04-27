package com.sanli.mallsystem.service;

import com.github.pagehelper.PageInfo;
import com.sanli.mallsystem.vo.OrderVo;
import com.sanli.mallsystem.vo.ResponseVo;

/**
 * 订单模块：整合支付、购物车模块、用户模块、商品信息模块...的各种共功能，完成带有购买商品的支付功能
 */
public interface IOrderService {

    /**
     * 购物车商品支付
     * 1、验证收货地址
     * 2、获取购物车中选中商品
     * 3、校验选中商品的货存状态信息
     * 4、计算总价
     * 5、生成订单，订单入库（Order与OrderItem，其中Order只存放对应用户与其订单信息的表（纯订单表），没有商品信息，而OrderItem表除了有部分订单信息还有该订单对应购买商品的信息）
     * 6、减少库存
     * @param uid
     * @param shippingId
     * @return
     */
    public ResponseVo<OrderVo> create(Integer uid , Integer shippingId);

    /**
     * 查询订单列表（需要包括订单信息、订单对应地商品列表、订单对应地收获地址），PageInfo需要封装List<OrderItem>
     *     注意：Order记录的是购买记录，而OrderItem记录的是每个购买记录及其对应购买商品信息，
     *     其中OrderItem包括uid、orderNo、productId，而shippingId在Order中
     * @param uid 要查询的用户id
     * @param pageNum 查询的分页数量
     * @param pageSize 查询的分页大小
     * @return
     */
    public ResponseVo<PageInfo> list(Integer uid , Integer pageNum , Integer pageSize);

    /**
     * 查询对用订单细节
     * @param uid
     * @param orderNo
     * @return
     */
    public ResponseVo<OrderVo> detail(Integer uid , Long orderNo);


    /**
     * 取消订单（只有未支付的订单可取消，此外取消订单并非删除订单而是修改订单的状态）
     * @param uid
     * @param orderNo
     * @return
     */
    public ResponseVo cancel(Integer uid , Long orderNo);
}
