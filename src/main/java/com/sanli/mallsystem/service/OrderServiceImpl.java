package com.sanli.mallsystem.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.sanli.mallsystem.dao.OrderItemMapper;
import com.sanli.mallsystem.dao.OrderMapper;
import com.sanli.mallsystem.dao.ProductMapper;
import com.sanli.mallsystem.dao.ShippingMapper;
import com.sanli.mallsystem.enums.PaymentStatusEnum;
import com.sanli.mallsystem.enums.PaymentTypeEnum;
import com.sanli.mallsystem.enums.ProductStatusEnum;
import com.sanli.mallsystem.pojo.*;
import com.sanli.mallsystem.utils.PayUtils;
import com.sanli.mallsystem.vo.OrderItemVo;
import com.sanli.mallsystem.vo.OrderVo;
import com.sanli.mallsystem.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.sanli.mallsystem.enums.ResponseStatusEnum.*;

@Slf4j
@Service
public class OrderServiceImpl implements IOrderService{

    private static final String CART_REDIS_KEY = "cart_%d"; // 定义购物车key的格式化

    private Gson gson = new Gson(); // 序列化对象

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ShippingMapper shippingMapper;

    @Autowired
    private CartServiceImpl cartService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderMapper orderMapper;

    /**
     *
     * @param uid
     * @param shippingId
     * @return
     */
    @Override
    @Transactional // 默认当出现RuntimeException时，事务将回滚
    public ResponseVo<OrderVo> create(Integer uid, Integer shippingId) {
        // 检验收获地址是否正确
        Shipping shipping = shippingMapper.selectByUidAndShippingId(uid, shippingId);
        if (shipping == null){
            return ResponseVo.error(SHIPPING_NOT_EXIST);
        }
        // 获取并检验购物车选中商品的库存与状态
        // 获取指定用户的购物车的所有商品（只有被选中的商品才会被选择出）
        List<Cart> carts = cartService.listForCart(uid).stream().
                filter(Cart::getProductSelected).
                collect(Collectors.toList());
        // 将购物车中选中的商品查找对应地商品信息（这部分商品信息需要入库，所以需要查出）
        // TODO 若在循环中调用查询数据库十分低效，能否一次通过一次查询直接获取到对应地商品
        /**
         * 应该先遍历购物车获取所有productId，然后存储到Set中，再传入Set遍历商品列表得到返回对应地商品信息
         */
        // 将carts中的productId抽取出来形成只有productId的Set集合
        Set<Integer> productIdSet = carts.stream().map(Cart::getProductId).collect(Collectors.toSet());

        // 获取到所有选中的且在售的商品信息
        List<Product> products = productMapper.selectByProductIdSet(productIdSet);
        // 其实就是将products的Set转化成productMap的Map类型（因为Map类型方便查找根据productId，无需遍历查找）
        Map<Integer, Product> productMap = products.stream().collect(Collectors.toMap(Product::getId, product -> product));

        // 购物车商品总价
        BigDecimal productsTotalPrice = BigDecimal.ZERO;
        Long orderNo = PayUtils.generatorOrderNo();
        LinkedList<OrderItem> orderItemList = new LinkedList<>();

        // 遍历购物车车中的商品信息与对应地详细信息核对
        for (Cart cart : carts) {
            Product product = productMap.get(cart.getProductId()); // 必须先判断查出来是否存在该商品才能判断库存与状态
            // TODO 这里product为null还有可能是因为商品状态下架或被删除了，因为selectByProductIdSet查出的商品默认status为在售的，这里业务判断逻辑重合了，最好将各种业务情况分离开来
            // 可以直接删除mapper中的status=1的默认条件

            // 判断库存与商品在售状态
            if (product == null) return ResponseVo.error(PRODUCT_NOT_EXIST,"productId:"+cart.getProductId()+"不存在");
            if (product.getStock() < cart.getQuantity()) return ResponseVo.error(LACK_OF_STOCK,product.getName()+" 库存不足");
            if (ProductStatusEnum.OFF_SELL.getStatus() == product.getStatus() || ProductStatusEnum.DELETE.getStatus() == product.getStatus()) return ResponseVo.error(PRODUCT_OFF_SELL_OF_DELETE,product.getName()+"被删除或已下架");

            // 计算实时总价，若没有价格就是商品表的价格（商品表的价格作为依据，实际价格有可能会变动，比如打折，所以实际支付看具体情况而看，往往商品表的价格不会变动，而是做一个参考）
            productsTotalPrice = productsTotalPrice.add(product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));

            // 构造OrderItem对象
            OrderItem orderItem = buildOrderItem(uid, orderNo , cart.getQuantity(), product);
            orderItemList.add(orderItem);
        }

        /**
         * TODO 为什么需要支付之前减少库存？
         * 因为用户有可能下单但未必立即支付，当用户支付在减少库存时，有可能该最后的商品库存已经被其他人买走了，也就是会产生超卖现象
         */
        // 若以上完美执行，则说明库存足够，这才开始更新库存，若更新库存同时写在上面循环，一旦出错则还需要将库存改回来
        for (Cart cart : carts) {
            Product product = productMap.get(cart.getProductId());
            product.setStock(product.getStock()-cart.getQuantity());
            productMapper.updateStock(product);// 更新对应的库存，这里无需判断，因为在上面循环中已经可以得知库存时存在的
        }

        // 构造Order对象
        Order order = buildOrder(uid, orderNo, shippingId, productsTotalPrice);
        /**
         * TODO 为什么已经存在Order还有由OrderItem？
         * OrderItem表用于连接用户购买的商品以及商品支付订单三个模块连接的表，
         * 目的就是为了方便查看哪个用户支付了什么订单，购买了什么商品，而不需要分别中三个表中查出来再封装，这样反而显得各个表之间没有什么联系一样
         */

        // 订单信息入库（Order与OrderItem），且必须要同时保证两个表之间的
        int rowForOrder = orderMapper.insertSelective(order);
        if (rowForOrder <= 0){
            return ResponseVo.error(); // 订单错误
        }
        int rowForOrderItem = orderItemMapper.batchInsertOrderItemList(orderItemList);
        if (rowForOrderItem <= 0){
            return ResponseVo.error();
        }

        // 更新购物车商品（redis的事务）
        // 更新购物车不能写在隐含错误异常的循环内（比如上面的购物车循环）。当写在循环内时，若中途某个个商品出错，不能复原被删除的商品
        for (Cart cart : carts) {
            cartService.delete(uid,cart.getProductId()); // 删除购物车的商品（即使没有该商品也不至于报错）
        }

        // 构建Vo对象
        return ResponseVo.success(buildOrderVo(order , orderItemList , shipping));
    }

    /**
     *
     * @param uid 要查询的用户id
     * @param pageNum 查询的分页数量
     * @param pageSize 查询的分页大小
     * @return
     */
    @Override
    public ResponseVo<PageInfo> list(Integer uid, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);

        // 获取该用户指定分页的订单List<Order>，并封装到Set集合中，用于获取对应地List<OrderItem>与List<Shipping>
        List<Order> orderList = orderMapper.selectByUid(uid);

        // 根据List<Order>获取对应地订单号集合Set<Long>的orderNoSet
        Set<Long> orderNoSet = orderList.stream().map(Order::getOrderNo).collect(Collectors.toSet());
        // 再根据orderNoSet获取对应地List<OrderItem> orderItemList，即获取所有订单号对应的综合信息（订单号、商品信息及其商品号）
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNoSet(orderNoSet);

        // 最后还需要根据orderNoSet获取所有List<Shipping>每个订单号对应地收货地址shippingIdSet
        Set<Integer> shippingIdSet = orderList.stream().map(Order::getShippingId).collect(Collectors.toSet());
        // 根据shippingIdSet查询所有的收货地址
        List<Shipping> shippingList = shippingMapper.selectByShippingIdSet(shippingIdSet);

        // 到此处只是查出来指定分页Order对应的所有OrderItem与所有Shipping，还需要给每个Order对应OrderItem、Shipping绑定，从而构造OrderVo对象
        // 获取绑定关系需要提前构造Map结构
        /**
         * 构造Map<orderNo,List<OrderItem>>
         * 构造Map<shippingId,Shipping>
         */
        Map<Long,List<OrderItem>> orderItemMap = orderItemList.stream().collect(Collectors.groupingBy(OrderItem::getOrderNo));
        Map<Integer,Shipping> shippingMap = shippingList.stream().collect(Collectors.toMap(Shipping::getId,shipping -> shipping));
        List<OrderVo> orderVoList = new LinkedList<>();
        for (Order order : orderList) {
            OrderVo orderVo = buildOrderVo(order, orderItemMap.get(order.getOrderNo()), shippingMap.get(order.getShippingId()));
            orderVoList.add(orderVo);
        }
        PageInfo<OrderVo> pageInfo = new PageInfo(orderList); // 分页信息应该是OrderList，而分页的List应该是每个OrderList的OrderVoList
        pageInfo.setList(orderVoList);
        // 最终返回的PageInfo内List封装为OrderVo，而OrderVo包括List<OrderItemVo>以及Shipping
        return ResponseVo.success(pageInfo);
    }

    /**
     *
     * @param uid
     * @param orderNo
     * @return
     */
    @Override
    public ResponseVo<OrderVo> detail(Integer uid, Long orderNo) {
        // 根据orderNo查询对应的Order
        /**
         * TODO 这里为什么不使用uid与orderNo一起查询Order？
         * order_no已经建立了索引，若再使用uid进行查询就减缓查询速度，因为需要遍历匹配uid。而uid选择放在Java中进行判断，而不需要在MySQL中作为条件查询
         */
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null || !order.getUserId().equals(uid)){
            return ResponseVo.error(ORDER_ERROR);
        }
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(orderNo);
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId()); // shipping表的id就是shippingId
        OrderVo orderVo = buildOrderVo(order, orderItemList, shipping);
        return ResponseVo.success(orderVo);
    }

    @Override
    public ResponseVo cancel(Integer uid, Long orderNo) {
        // TODO 能否直接更新订单数据，即使该订单号不存在？
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null || !order.getUserId().equals(uid)){
            return ResponseVo.error(ORDER_STATUS_EXCEPTION);
        }else if (!order.getStatus().equals(PaymentStatusEnum.UNPAID.getStatus())){
            return ResponseVo.error(CANNOT_CHANGE_PAID_ORDER);
        }
        order.setStatus(PaymentStatusEnum.CANCELED.getStatus());
        orderMapper.updateByPrimaryKeySelective(order);
        return ResponseVo.success();
    }

    @Override
    public void paid(Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        // 因为调用paid方法之前肯定先调用了支付系统的create方法，此时支付系统中已经写入了订单，调用paid方法需要支付平台异步回调，所以若查不出来则表示异步回调有误
        if (order == null){
            throw new RuntimeException("查无此订单！"+orderNo);
        }
        Integer paymentStatus = order.getStatus();
        if (PaymentStatusEnum.PAID.getStatus().equals(paymentStatus)) return; // 订单已经支付无需修改再次修改
        else if (!PaymentStatusEnum.UNPAID.getStatus().equals(paymentStatus)){ // 只有未支付订单才需要可以修改，其他状态的订单不能修改
            throw new RuntimeException("订单状态异常！修改支付订单失败！");
        }
        order.setStatus(PaymentStatusEnum.PAID.getStatus());
        order.setPaymentTime(new Date());
        order.setCloseTime(new Date());
        int row = orderMapper.updateByPrimaryKeySelective(order);
        if (row <= 0){
            throw new RuntimeException("订单修改失败！");
        }
    }

    /**
     * 特别注意：这里要求传入的order与orderItemList、shipping要求必须是绑定关系，否则构造的OrderVo没有意义
     * @param order
     * @param orderItemList
     * @param shipping
     * @return
     */
    private OrderVo buildOrderVo(Order order , List<OrderItem> orderItemList , Shipping shipping) {
        // 拷贝Order到OrderVo
        OrderVo orderVo = new OrderVo();
        BeanUtils.copyProperties(order,orderVo);

        // 封装List<OrderItemVo>
        LinkedList<OrderItemVo> orderItemVos = new LinkedList<>();
        for (OrderItem orderItem : orderItemList) {
            OrderItemVo orderItemVo = new OrderItemVo();
            BeanUtils.copyProperties(orderItem,orderItemVo);
            orderItemVos.add(orderItemVo);
        }
        orderVo.setOrderItemVoList(orderItemVos);
        // 封装Shipping
        orderVo.setShippingVo(shipping);
        return orderVo;
    }


    private Order buildOrder(Integer uid , Long orderNo, Integer shippingId ,BigDecimal totalPrice) {
        Order order = new Order();
        order.setUserId(uid);
        order.setOrderNo(orderNo);
        order.setShippingId(shippingId);
        order.setPayment(totalPrice);
        order.setPaymentType(PaymentTypeEnum.PAY_ONLINE.getStatus());
        order.setPostage(0); // 运费包邮
        order.setStatus(PaymentStatusEnum.UNPAID.getStatus());
        return order;
    }

    // 类似于适配器（但缺乏接口）
    private OrderItem buildOrderItem(Integer uid , Long orderNo , Integer quantity , Product product) {
        OrderItem item = new OrderItem();
        item.setUserId(uid);
        item.setProductId(product.getId());
        item.setOrderNo(orderNo);
        item.setProductName(product.getName());
        item.setProductImage(product.getMainImage());
        item.setCurrentUnitPrice(product.getPrice());
        item.setQuantity(quantity);
        item.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        return item;
    }
}
