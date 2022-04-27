package com.sanli.mallsystem.service;

import com.sanli.mallsystem.pojo.User;
import com.sanli.mallsystem.vo.ResponseVo;

import javax.servlet.http.HttpSession;

public interface IUserService {
    /**
     * 注册用户
     * @param user
     * @return
     */
    public ResponseVo registry(User user);

    /**
     * 用户登录并保存登录状态
     * @param user
     * @param session
     * @return
     */
    public ResponseVo login(User user , HttpSession session);

    /**
     * 获取状态信息
     * @param session
     * @return
     */
    public ResponseVo getUserMessage(HttpSession session);

    /**
     * 用户状态登出
     * @param session
     * @return
     */
    public ResponseVo logout(HttpSession session);
}
