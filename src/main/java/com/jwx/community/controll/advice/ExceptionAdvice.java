package com.jwx.community.controll.advice;

import com.jwx.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

//只扫描带有controller注解的bean
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static final Logger logger= LoggerFactory.getLogger(ExceptionAdvice.class);

    //处理所有的exception异常
    @ExceptionHandler(Exception.class)
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        //记录错误日志
        logger.error("服务器发生异常"+e.getMessage());
        for(StackTraceElement element:e.getStackTrace()){
            logger.error(element.toString());
        }
        //判断请求是普通请求还是异步请求 普通请求返回的是html 异步请求返回的是xml或json等
        String xRequestWith = request.getHeader("x-requested-with");
        if("XMLHttpRequest".equals(xRequestWith)){
            //异步请求，需要response 返回一句话 "服务器异常"
            //我们向浏览器返回一个普通的字符串 也可以是json格式的字符串 浏览器得到后要人为的将字符串转换为js对象
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer=response.getWriter();//获取输出流
            writer.write(CommunityUtil.getJSONString(1,"服务器异常"));
        }else{
            //普通请求直接重定向到error
            response.sendRedirect(request.getContextPath()+"/error");
        }

    }


}
