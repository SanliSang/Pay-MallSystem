package com.sanli.paysystem.interceptorHandler;

import com.sanli.paysystem.exception.UserUnLoginException;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static com.sanli.paysystem.coust.UserStatusConst.CURRENT_USER;

public class UserLoginInterception implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        if (session.getAttribute(CURRENT_USER)==null){
            throw new UserUnLoginException(); // 若未登录，想要向前端发送未登录状态信息，需要抛出异常的方式，然后通过统一异常处理发送将信息发送给前端
        }
        return true;
    }
}
