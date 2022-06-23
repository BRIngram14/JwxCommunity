package com.jwx.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

//@Component
//@Aspect
public class AlphaAspect {
    //连接点的范围
    //第一个*是返回值 包括所有的返回值类型 第二个*代表service下的所有组件 第三个*代表 service组件内的所有方法
    //最后括号的里的..代表所有的参数
    @Pointcut("execution(* com.jwx.community.service.*.*(..))")
    public void pointcut(){

    }

    @Before("pointcut()")//在连接点的一开始地方 连接点就上面定义的pointcut()
    public void before()
    {
        System.out.println("before");
    }
    @After("pointcut()")//在连接点的后面 连接点就上面定义的pointcut()
    public void after()
    {
        System.out.println("after");
    }
    @AfterReturning("pointcut()")//在返回值后 连接点就上面定义的pointcut()
    public void afterReturning()
    {
        System.out.println("afterReturning");
    }
    @AfterThrowing("pointcut()")//在抛出异常后 连接点就上面定义的pointcut()
    public void AfterThrowing()
    {
        System.out.println("AfterThrowing");
    }
    @Around("pointcut()")
    public Object arount(ProceedingJoinPoint joinPoint) throws Throwable{
        System.out.println("around before");
        Object obj = joinPoint.proceed();//调原始对象的方法 在这句话前后输入新织入的逻辑
        System.out.println("around after");
        return obj;
    }




}
