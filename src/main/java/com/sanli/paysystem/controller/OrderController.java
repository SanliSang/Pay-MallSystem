package com.sanli.paysystem.controller;

import com.github.pagehelper.PageInfo;
import com.sanli.paysystem.form.OrderCreateForm;
import com.sanli.paysystem.pojo.User;
import com.sanli.paysystem.service.OrderServiceImpl;
import com.sanli.paysystem.vo.OrderVo;
import com.sanli.paysystem.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

import static com.sanli.paysystem.coust.UserStatusConst.CURRENT_USER;

@RestController
public class OrderController {
    @Autowired
    private OrderServiceImpl orderService;

    @PostMapping("/orders")
    public ResponseVo<OrderVo> create(@Validated @RequestBody OrderCreateForm form , HttpSession session){
        User user = (User) session.getAttribute(CURRENT_USER);
        return orderService.create(user.getId(),form.getShippingId());
    }

    @GetMapping("/orders")
    public ResponseVo<PageInfo> list(@RequestParam("pageNum") Integer pageNum ,
                                     @RequestParam("pageSize") Integer pageSize ,
                                     HttpSession session){
        User user = (User) session.getAttribute(CURRENT_USER);
        return orderService.list(user.getId(),pageNum,pageSize);
    }

    @GetMapping("/orders/{orderNo}")
    public ResponseVo<OrderVo> detail(@PathVariable("orderNo") Long orderNo , HttpSession session){
        User user = (User) session.getAttribute(CURRENT_USER);
        return orderService.detail(user.getId(),orderNo);
    }

    @PutMapping("/orders/{orderNo}")
    public ResponseVo cancel(@PathVariable("orderNo")Long orderNo , HttpSession session){
        User user = (User) session.getAttribute(CURRENT_USER);
        return orderService.cancel(user.getId(),orderNo);
    }

}
