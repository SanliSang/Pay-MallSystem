package com.sanli.paysystem.controller;

import com.sanli.paysystem.form.CartAddForm;
import com.sanli.paysystem.form.CartUpdateForm;
import com.sanli.paysystem.pojo.User;
import com.sanli.paysystem.service.CartServiceImpl;
import com.sanli.paysystem.vo.CartVo;
import com.sanli.paysystem.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

import static com.sanli.paysystem.coust.UserStatusConst.CURRENT_USER;

@RestController
public class CartController {

    @Autowired
    private CartServiceImpl cartService;


    @GetMapping("/carts")
    public ResponseVo<CartVo> list(HttpSession session){
        User user = (User) session.getAttribute(CURRENT_USER);
        return cartService.list(user.getId());
    }

    @PostMapping("/carts")
    public ResponseVo<CartVo> add(HttpSession session ,
                                  @RequestBody CartAddForm form){
        User user = (User) session.getAttribute(CURRENT_USER);
        return cartService.add(user.getId(),form);

    }
    @DeleteMapping("/carts/{productId}")
    public ResponseVo<CartVo> delete(HttpSession session ,
                                     @Validated @PathVariable("productId") Integer productId){
        User user = (User) session.getAttribute(CURRENT_USER);
        return cartService.delete(user.getId(),productId);
    }

    @PutMapping("/carts/{productId}")
    public ResponseVo<CartVo> update(HttpSession session ,
                                     @Validated @PathVariable("productId") Integer productId ,
                                     @Validated @RequestBody CartUpdateForm form){
        User user = (User) session.getAttribute(CURRENT_USER);
        return cartService.update(user.getId(),productId,form);
    }

    @PutMapping("/carts/selectAll")
    public ResponseVo<CartVo> selectAll(HttpSession session){
        User user = (User) session.getAttribute(CURRENT_USER);
        return cartService.selectAll(user.getId());
    }

    @PutMapping("/carts/unSelectAll")
    public ResponseVo<CartVo> unSelectAll(HttpSession session){
        User user = (User) session.getAttribute(CURRENT_USER);
        return cartService.unSelectAll(user.getId());
    }

    @PutMapping("/carts/productId/sum")
    public ResponseVo<Integer> getProductSum(HttpSession session){
        User user = (User) session.getAttribute(CURRENT_USER);
        return cartService.productSize(user.getId());
    }
}
