package com.jwx.community.controll.interceptor;

import com.jwx.community.annotation.LoginRequired;
import com.jwx.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod)
        {
            //判断拦截的目标是否是一个方法,拦截的是方法就将handler转型成HandlerMethod
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //获取拦截到的method对象
            Method method = handlerMethod.getMethod();
            //得到method对象上的LoginRequired注解
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            if(loginRequired!=null&&hostHolder.getUser()==null)
            {
                //有这个注解且没登录的就不能访问 使用response重定向返回到登录页面,从request中取应用的路径
                response.sendRedirect(request.getContextPath()+"/login");
                return false;
            }
        }
        return true;
    }
}
