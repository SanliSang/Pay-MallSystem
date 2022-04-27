package com.sanli.paysystem.vo;

import com.sanli.paysystem.pojo.OrderItem;
import com.sanli.paysystem.pojo.Shipping;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderVo {

    private Long orderNo;

    private BigDecimal payment;

    private Integer paymentType;

    private Integer postage;

    private Integer status;

    private Date paymentTime;

    private Date sendTime;

    private Date endTime;

    private Date closeTime;

    private Date createTime;

    private List<OrderItemVo> orderItemVoList;
    
    private Integer shippingId;

    // 因为Shipping与ShippingVo相同，所以这里就直接复用Shipping看作ShippingVo了，实际上应该需要分离的
    private Shipping shippingVo;

}
