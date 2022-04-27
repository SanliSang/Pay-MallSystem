package com.sanli.paysystem.controller;

import com.github.pagehelper.PageInfo;
import com.sanli.paysystem.form.ShippingForm;
import com.sanli.paysystem.pojo.User;
import com.sanli.paysystem.service.ShippingServiceImpl;
import com.sanli.paysystem.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

import java.util.Map;

import static com.sanli.paysystem.coust.UserStatusConst.CURRENT_USER;

@RestController
public class ShippingController {

    @Autowired
    private ShippingServiceImpl shippingService;

    @PostMapping("/shippings")
    public ResponseVo<Map<String, Integer>> addShipping(HttpSession session ,
                                                        @Validated @RequestBody ShippingForm form){
        User user = (User) session.getAttribute(CURRENT_USER);
        return shippingService.add(user.getId(), form);
    }

    @DeleteMapping("/shippings/{shippingId}")
    public ResponseVo deleteShipping(HttpSession session ,
                                     @PathVariable("shippingId") Integer shippingId){
        User user = (User) session.getAttribute(CURRENT_USER);
        return shippingService.delete(user.getId(), shippingId);
    }

    @PutMapping("/shippings/{shippingId}")
    public ResponseVo updateShipping(HttpSession session ,
                                     @PathVariable("shippingId") Integer shippingId,
                                     @Validated @RequestBody ShippingForm form){
        User user = (User) session.getAttribute(CURRENT_USER);
        return shippingService.update(user.getId(),shippingId,form);
    }

    @GetMapping("/shippings")
    public ResponseVo<PageInfo> list(HttpSession session ,
                                     @RequestParam(value = "pageNum",required = false , defaultValue = "1") Integer pageNum ,
                                     @RequestParam(value = "pageSize" , required = false , defaultValue = "10") Integer pageSize){
        User user = (User) session.getAttribute(CURRENT_USER);
        return shippingService.list(user.getId(),pageNum,pageSize);
    }
}
