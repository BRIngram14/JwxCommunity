package com.jwx.community.config;

import com.jwx.community.controll.interceptor.LoginRequiredInterceptor;
import com.jwx.community.controll.interceptor.LoginTicketInterceptor;
import com.jwx.community.controll.interceptor.MessageInterceptor;
import com.jwx.community.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Autowired
    private MessageInterceptor messageInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //添加一个拦截器,拦截除了静态资源外的路径
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg",
                        "/swagger-ui.html","/swagger-resources/**","/webjars/**","/**/*.jpeg","/doc.html");

        registry.addInterceptor(loginRequiredInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg",
                        "/swagger-ui.html","/swagger-resources/**","/webjars/**","/**/*.jpeg","/doc.html");

        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg",
                        "/swagger-ui.html","/swagger-resources/**","/webjars/**","/**/*.jpeg","/doc.html");


    }
}
