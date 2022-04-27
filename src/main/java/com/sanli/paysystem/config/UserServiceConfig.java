package com.sanli.paysystem.config;

import com.sanli.paysystem.interceptorHandler.UserLoginInterception;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class UserServiceConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserLoginInterception()) // 以下内容建议在配置文件中进行配置
                .addPathPatterns("/**") // 全局捕获
                .excludePathPatterns("/user/login","/user/registry"); // 除了登录、注册不需要捕获，其他情况都需要先检查用户状态
        // 登出也需要检查用户状态，因为需要先登录才可以登出
    }
}
