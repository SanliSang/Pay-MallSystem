package com.sanli.mallsystem.controller;

import com.sanli.mallsystem.controller.CheckGroup.login;
import com.sanli.mallsystem.controller.CheckGroup.registry;
import com.sanli.mallsystem.form.UserForm;
import com.sanli.mallsystem.pojo.User;
import com.sanli.mallsystem.service.UserServiceImpl;
import com.sanli.mallsystem.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequestMapping
public class UserController {
    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/user/registry")
    @ResponseBody
    // 参数验证结果移交给统一异常进行处理了
    public ResponseVo registry(@Validated(value = registry.class) @RequestBody UserForm userForm){
        // 将UserFrom的数据拷贝到User中就无需修改Service与Dao层了
        User user = new User();
        BeanUtils.copyProperties(userForm,user);
        return userService.registry(user);
    }

    /**
     * @param userForm 用户表单
     * @param session 由服务器自动创建，springboot注入的HttpSession对象
     * @return
     */
    @PostMapping("/user/login")
    @ResponseBody
    public ResponseVo login(@Validated(value = login.class) @RequestBody UserForm userForm , HttpSession session){
        User user = new User();
        BeanUtils.copyProperties(userForm,user);
        return userService.login(user,session);
    }

    @GetMapping("/user")
    @ResponseBody
    public ResponseVo getUserMessage(HttpSession session){
        return userService.getUserMessage(session);
    }

    @GetMapping("/user/logout")
    @ResponseBody
    public ResponseVo logout(HttpSession session){
        log.info("/user/logout ===> session Id -{}" , session.getId());
        return userService.logout(session);
    }
}
