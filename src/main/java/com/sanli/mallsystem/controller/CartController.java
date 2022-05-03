package com.sanli.mallsystem.controller;

import com.sanli.mallsystem.form.CartAddForm;
import com.sanli.mallsystem.form.CartUpdateForm;
import com.sanli.mallsystem.pojo.User;
import com.sanli.mallsystem.service.CartServiceImpl;
import com.sanli.mallsystem.vo.CartVo;
import com.sanli.mallsystem.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

import static com.sanli.mallsystem.coust.UserStatusConst.CURRENT_USER;

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
                                     @PathVariable("productId") Integer productId){
        User user = (User) session.getAttribute(CURRENT_USER);
        return cartService.delete(user.getId(),productId);
    }

    @PutMapping("/carts/{productId}")
    public ResponseVo<CartVo> update(HttpSession session ,
                                     @PathVariable("productId") Integer productId ,
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
