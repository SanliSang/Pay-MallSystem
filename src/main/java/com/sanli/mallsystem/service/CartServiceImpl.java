package com.sanli.mallsystem.service;

import com.google.gson.Gson;
import com.sanli.mallsystem.dao.ProductMapper;
import com.sanli.mallsystem.form.CartAddForm;
import com.sanli.mallsystem.form.CartUpdateForm;
import com.sanli.mallsystem.pojo.Cart;
import com.sanli.mallsystem.pojo.Product;
import com.sanli.mallsystem.vo.CartProductVo;
import com.sanli.mallsystem.vo.CartVo;
import com.sanli.mallsystem.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.sanli.mallsystem.enums.ProductStatusEnum.DELETE;
import static com.sanli.mallsystem.enums.ProductStatusEnum.OFF_SELL;
import static com.sanli.mallsystem.enums.ResponseStatusEnum.*;

@Slf4j
@Service
public class CartServiceImpl implements ICartService{

    private static final String CART_REDIS_KEY = "cart_%d"; // 定义购物车key的格式化

    private Gson gson = new Gson(); // 序列化对象

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ProductMapper productMapper;

    /**
     *
     * @param uid 客户uid
     * @param form 购物车提交表单
     * @return
     */
    public ResponseVo<CartVo> add(Integer uid , CartAddForm form){
        Integer quantity = 1; // 购物车添加数量（默认为1）
        Product product = productMapper.selectByPrimaryKey(form.getProductId());
        // 商品是否存在
        if (product == null){
            return ResponseVo.error(PRODUCT_NOT_EXIST);
        }
        // 商品是否在售
        if (product.getStatus().equals(OFF_SELL.getStatus()) || product.getStatus().equals(DELETE.getStatus())){
            return ResponseVo.error(PRODUCT_OFF_SELL_OF_DELETE);
        }
        // 商品库存是否充足
        if (product.getStock() <= 0){
            return ResponseVo.error(EMPTY_STOCK);
        }

        /**
         * redis购物车应该采用hash结构存储，因为用户的购物车不仅有一个商品，若采用string结构存储这部分数据，将大量生成string进行存储，
         * 其次，当用户购物车中的指定productId商品时需要遍历cartProductVoList中所有的等于productId的CartProductVo，性能较低。
         * 若采用hash的方式存储，key采用cart_uid，而field采用productId，value存储cart的Json对象即可，获取指定product直接调用get(filed)直接获取cart，性能较高
         */

        /**
         * 为什么不将productId，quantity，selected作为field，其value采用其对应的值，而是采用field只有productId，而value作为为整个cart的Json对象字符串
         * 首先，因为若将cart的属性拆解成hash的field-value，这样无法在一个hash中存储购物车中多个商品信息（除非再给field添加其他标识）
         * 其次，若拆解属性，再次获取时，需要自己封装cart购物车对象（反序列化）。类似的，当要存储对象的时候，可以考虑使用序列化的方式存储Json对象的方式存储
         */

        String redisKey = String.format(CART_REDIS_KEY, uid);
        /**
         * 第一个String为key
         * 第二个String为field
         * 第三个String为value
         */
        HashOperations<String, String, String> hash = redisTemplate.opsForHash();
        // 往hash添加商品是应该首先判断是否存在该商品，若不存在才去创建，若已存在则直接数量+1即可
        String value = hash.get(redisKey , String.valueOf(product.getId())); // 查询hash的field（注意类型转换）
        Cart cart = null;
        if (value == null){ // uid用户的购物车中没有productId的商品，需要直接创建
            cart = new Cart(product.getId(),quantity,form.getSelected()); // 新建购物车

        }else { // 已经存在，则数量购买数量+1
            // TODO 优化更新redis的数据
            cart = gson.fromJson(value, Cart.class); // 取出value并反序列化得到购物车对象，这里还可以继续优化，因为当value数据量较大时，反序列化消耗的时间较多
            cart.setQuantity(cart.getQuantity() + quantity); // 数量+1
        }
        // 更新（新建或数量累加）购物车
        hash.put(String.format(CART_REDIS_KEY,uid),
                String.valueOf(product.getId()),
                gson.toJson(cart));
        return list(uid);
    }

    /**
     *
     * @param uid
     * @return
     */
    @Override
    public ResponseVo<CartVo> list(Integer uid) {
        // 获取购物车列表
        String redisKey = String.format(CART_REDIS_KEY,uid);
        HashOperations<String, String, String> hash = redisTemplate.opsForHash();

        // 获取购物车的商品的key（productId）与value（cart）
        Map<String, String> cartMap = hash.entries(redisKey);
        List<CartProductVo> cartList = new ArrayList<>();
        Boolean selectAll = true; // 购物车内商品是否全选
        BigDecimal cartTotalPrice = BigDecimal.ZERO; // 购物车总价
        Integer cartTotalQuantity = 0;
        for (Map.Entry<String, String> entry : cartMap.entrySet()) { // 遍历遍历每个productId并查找出对应地商品信息
            Integer productId = Integer.valueOf(entry.getKey());
            String cartJson = entry.getValue();
            Cart cart = gson.fromJson(cartJson, Cart.class);

            // TODO 在循环中尽量不要查询SQL
            Product product = productMapper.selectByPrimaryKey(productId);

            if (product != null){
                // 对应的商品信息（将product封装到CartProductVo，这里无法使用对象拷贝，因为对象的字段名不同，需要手动封装）
                CartProductVo cartProductVo = new CartProductVo(productId,
                        cart.getQuantity(), // 购物车的购买数量
                        product.getName(),
                        product.getSubtitle(),
                        product.getMainImage(),
                        product.getPrice(),
                        product.getStatus(),
                        product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())),
                        product.getStock(),
                        cart.getProductSelected()); // 商品是否选中状态
                cartList.add(cartProductVo); // 添加的到购物车列表中

                // 判断购物车每个商品是否选择（只要有一个不选就不是全选）
                if (!cart.getProductSelected()){
                    selectAll = false;
                }
                // 只有选中才累加其总价
                if (cart.getProductSelected()){
                    cartTotalPrice = cartTotalPrice.add(cartProductVo.getProductTotalPrice()); // 累加所有选中的商品的总价
                }

                cartTotalQuantity += cart.getQuantity(); // 累加购物车总数
            }
        }
        // 手动封装CartVo
        CartVo cartVo = new CartVo(cartList,selectAll,cartTotalPrice,cartTotalQuantity);
        return ResponseVo.success(cartVo);
    }

    /**
     * 根据uid与productId更新对应的购物车状态（数量、选中情况）
     * @param uid
     * @param productId
     * @param form 表单内更新信息可选，需要单独判断
     * @return
     */
    @Override
    public ResponseVo<CartVo> update(Integer uid, Integer productId, CartUpdateForm form) {
        String redisKey = String.format(CART_REDIS_KEY,uid);
        HashOperations<String, String, String> hash = redisTemplate.opsForHash();
        String cartJson = hash.get(redisKey, String.valueOf(productId));
        if (cartJson == null){
            // 购物车商品不存在错误
            return ResponseVo.error(PRODUCT_NOT_EXIST);
        }
        Cart cart = gson.fromJson(cartJson, Cart.class);

        // 更新选中
        if (form.getSelected() != null){
            cart.setProductSelected(form.getSelected());
        }

        // 更新数量（数量范围为1~n，小于1）
        if (form.getQuantity() != null && form.getQuantity() >= 0){
            cart.setQuantity(form.getQuantity());
        }

        // 更新redis内的购物车
        hash.put(redisKey,String.valueOf(productId),gson.toJson(cart));
        return list(uid); // 复用list将更新后的购物车的所有信息再次返回
    }


    /**
     *
     * @param uid
     * @param productId
     * @return
     */
    public ResponseVo<CartVo> delete(Integer uid , Integer productId){
        String redisKey = String.format(CART_REDIS_KEY,uid);
        HashOperations<String, String, String> hash = redisTemplate.opsForHash();
        // TODO 为什么不直接删除商品而是先判断是否存在？
        hash.delete(redisKey,String.valueOf(productId)); // 删除field
        return list(uid); // TODO 每次用户删除更新都重新返回查询后的数据较为麻烦，修改后的数据展示留给前端修改即可，后端无需在重新查询数据给前端展示
    }

    /**
     *
     * @param uid
     * @return
     */
    @Override
    public ResponseVo<CartVo> selectAll(Integer uid) {
        String redisKey = String.format(CART_REDIS_KEY,uid);
        HashOperations<String, String, String> hash = redisTemplate.opsForHash();
        Map<String, String> entries = hash.entries(redisKey);
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            String productId = entry.getKey();
            String cartJson = entry.getValue();
            Cart cart = gson.fromJson(cartJson, Cart.class);
            cart.setProductSelected(true);

            hash.put(redisKey,productId,gson.toJson(cart));
        }
        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> unSelectAll(Integer uid) {
        String redisKey = String.format(CART_REDIS_KEY,uid);
        HashOperations<String, String, String> hash = redisTemplate.opsForHash();
        Map<String, String> entries = hash.entries(redisKey);
        // 取出购物车中每个商品
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            String productId = entry.getKey();
            String cartJson = entry.getValue();
            // 取出商品并进行反序列化
            Cart cart = gson.fromJson(cartJson, Cart.class);
            cart.setProductSelected(false);
            // 修改商品后不要忘记重新赋值更新商品
            hash.put(redisKey,productId,gson.toJson(cart));
        }
        return list(uid);
    }

    @Override
    public ResponseVo<Integer> productSize(Integer uid) {
        ResponseVo<CartVo> list = list(uid);
        return ResponseVo.success(list.getData().getCartTotalQuantity());
    }


    // 代码复用

    /**
     * 查出指定uid用户的所有购物车列表
     * @param uid
     * @return
     */
    public List<Cart> listForCart(Integer uid){
        String cartId = String.format(CART_REDIS_KEY,uid);
        HashOperations<String, String, String> cartHash = redisTemplate.opsForHash();
        Map<String, String> cartMap = cartHash.entries(cartId);
        LinkedList<Cart> cartList = new LinkedList<>();
        for (Map.Entry<String, String> entry : cartMap.entrySet()) {
            String cartJson = entry.getValue();
            Cart cart = gson.fromJson(cartJson, Cart.class);
            cartList.add(cart);
        }
        return cartList;
    }
}
