package com.sanli.mallsystem.listener;

import com.google.gson.Gson;
import com.lly835.bestpay.enums.OrderStatusEnum;
import com.sanli.mallsystem.pojo.PayInfo;
import com.sanli.mallsystem.service.OrderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RabbitListener(queues = "payNotify")
public class PayServiceListener {

    @Autowired
    private OrderServiceImpl orderService;

    private final Gson gson = new Gson();

    // 特别注意：因为RabbitMQ的可靠性传输机制，若中途抛出异常，表示消息处理失败，会将消息重新发送到该方法上继续处理，若仍然出错会导致一直循环下去
    // 注意若采用AmqpTemplate发送的，没有消息头仅有消息体，RabbitHandler接收消息采用Message反而会报错，所以建议采用自定义的RabbitTemplate进行发送消息
    @RabbitHandler
    private void process(String message){
        log.info("message ===> {}",message);
        PayInfo payInfo = gson.fromJson(message, PayInfo.class); // PayInfo中包含详细的订单信息但没有购买的商品信息
        if (payInfo.getPlatformStatus().equals(OrderStatusEnum.SUCCESS.name())){ // 硬编码（这里的PlatformStatus是创建支付是回调中第三方SDK赋值的，知道是SUCCESS就表示成功即可）
            orderService.paid(payInfo.getOrderNo()); // 修改订单状态
        }
    }
}
