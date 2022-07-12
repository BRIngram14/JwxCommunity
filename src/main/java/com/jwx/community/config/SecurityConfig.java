package com.jwx.community.config;

import com.jwx.community.util.CommunityConstant;
import com.jwx.community.util.CommunityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {
    @Override
    public void configure(WebSecurity web) throws Exception {
        //忽略静态资源的访问
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
       //授权
        http.authorizeRequests()
                .antMatchers("/user/setting",
                        "/user/upload",
                        "discuss/add","/comment/add/**","/letter/**","/notice/**",
                        "/like","/follow","/unfollow")
                .hasAnyAuthority(
                        AUTHORITY_USER,AUTHORITY_ADMIN,AUTHORITY_MODERATOR
                ).antMatchers(
                        "/discuss/top","/discuss/wonderful")
                .hasAnyAuthority(
                        AUTHORITY_MODERATOR
                ).antMatchers(
                        "/discuss/delete","/data/**","/actuator/**"
        ).hasAnyAuthority(
                AUTHORITY_ADMIN
        )
                .anyRequest().permitAll()
                    .and().csrf().disable();
        //除了上述请求 其他的都直接允许通过 上述这些请求是需要登录后访问 关闭csrf


        //权限不够时的处理
        http.exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    //没有登录的情况
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
                        //判断请求是同步的还是异步的
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if("XMLHttpRequest".equals(xRequestedWith))
                        {
                            //异步请求期待返回一个XML(现在已经被json替代)
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJSONString(403,"你还没有登录！"));
                        }else{
                            response.sendRedirect(request.getContextPath()+"/login");
                        }
                    }
                }).accessDeniedHandler(new AccessDeniedHandler() {
                    //权限不足的情况
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
                //判断请求是同步的还是异步的
                String xRequestedWith = request.getHeader("x-requested-with");
                if("XMLHttpRequest".equals(xRequestedWith))
                {
                    //异步请求期待返回一个XML(现在已经被json替代)
                    response.setContentType("application/plain;charset=utf-8");
                    PrintWriter writer = response.getWriter();
                    writer.write(CommunityUtil.getJSONString(403,"你没有访问此功能的权限！"));
                }else{
                    response.sendRedirect(request.getContextPath()+"/denied");
                }
            }
        });
        //  Security底层默认会拦截/logout请求,进行退出处理.
        //  覆盖它默认的逻辑,才能执行我们自己的退出代码. 故意写一个空的路径 绕过拦截 执行到我们的方法里去
        http.logout().logoutUrl("/securitylogout");
    }
}
