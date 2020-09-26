package com.example.demo.config;

import com.example.demo.interceptors.JWTInterceptors;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer{
	
	@Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JWTInterceptors())
                .addPathPatterns("/user/changePwd")         //需要token验证的其他接口
                .addPathPatterns("/user/setPhone")
                .addPathPatterns("/user/setPwd")
                .addPathPatterns("/user/changePhone")
                .excludePathPatterns("/user/sendVerifyCode")//放行发送验证码接口
                .excludePathPatterns("/user/login");        //所有用户都放行登录接口
    }

}
