package com.sanli.mallsystem.service;

import com.sanli.mallsystem.dao.UserMapper;
import com.sanli.mallsystem.enums.ResponseStatusEnum;
import com.sanli.mallsystem.pojo.User;
import com.sanli.mallsystem.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;

import static com.sanli.mallsystem.coust.UserStatusConst.CURRENT_USER;
import static com.sanli.mallsystem.enums.ResponseStatusEnum.LOGOUT_SUCCESS;


@Slf4j
@Service
public class UserServiceImpl implements IUserService{

    @Autowired
    private UserMapper userMapper;

    @Override
    public ResponseVo registry(User user) {
        if (userMapper.countByUsername(user.getUsername()) > 0){
            return ResponseVo.error(ResponseStatusEnum.USER_EXIST);
        }

        if (userMapper.countByEmail(user.getEmail()) > 0){
            return ResponseVo.error(ResponseStatusEnum.EMAIL_EXIST);
        }
        // 加密密码
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8)));
        // 想要给前端返回服务器错误，必须要异常，否则会给前端返回乱七八招的报错信息，不要给服务器错误传递给前端，否则前端也会崩溃
        // 可以使用统一异常处理方式，将相同种类的异常捕获，并由统一返回处理错误方式

        // 若调用方法结果以抛异常的形式而不是结果值返回的形式，这段代码根本没有作用
        if (userMapper.insertSelective(user) != 1){
            return ResponseVo.error(ResponseStatusEnum.SERVICE_ERROR);
        }
        return ResponseVo.success(ResponseStatusEnum.REGISTRY_SUCCESS);
    }

    @Override
    public ResponseVo login(User user , HttpSession session) {
        User u = userMapper.selectByUsername(user.getUsername());
        if (u == null || !u.getPassword().equals(DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8)))){
            return ResponseVo.error(ResponseStatusEnum.USERNAME_OR_PASSWORD_ERROR);
        }
        // 执行到此处表示用户成功登录
        u.setPassword(null); // 不保存密码
        session.setAttribute(CURRENT_USER , u); // 将用户状态保存在Session中
        return ResponseVo.success(ResponseStatusEnum.LOGIN_SUCCESS); // 返回登录成功状态
    }

    @Override
    public ResponseVo<User> getUserMessage(HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        // 不要将密码返回（此步骤建议在登陆后存入Session就将密码清除）
        return ResponseVo.success(ResponseStatusEnum.SUCCESS,user);
    }

    @Override
    public ResponseVo logout(HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        // user检测为null（用户状态）已经移交给拦截器解决，无需再次判断，这里直接删除Session即可
        session.removeAttribute(CURRENT_USER);
        return ResponseVo.success(LOGOUT_SUCCESS);
    }
}
