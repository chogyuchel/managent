package com.example.filter;

import com.example.utils.CurrentHolder;
import com.example.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
//@WebFilter(urlPatterns = {"/*"})
public class TokenFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1.获取到请求的路径
        String path = request.getRequestURI(); // /login

        //2.判断是否是登陆请求,如果这个路径中包含/login,说明是登录操做，放行
        if(path.contains("/login")){
            log.info("登录请求，放行");
            filterChain.doFilter(request,response);
            return;
        }

        //3.获取请求头中的token
        String token = request.getHeader("token");

        //4.判断token是否存在，如果不存在，说明用户没有登录，返回错误信息（响应401状态码)
        if (token == null || token.isEmpty()){
            log.info("token不存在，说明用户没有登录，返回错误信息（响应401状态码)");
            response.setStatus(401);
            return;
        }

        //5.如果token存在，校验令牌，如果校验失败 -> 返回错误信息（响应401状态码)
        try{
            Claims claims = JwtUtils.parseJWT(token);
            Integer empid = Integer.valueOf(claims.get("id").toString());
            CurrentHolder.setCurrentId(empid); //存入
            log.info("当前登录员工的id：{},将其存入ThreadLocal",empid);

        } catch (Exception e){
            log.info("令牌非法，响应401");
            response.setStatus(401);
            return;
        }

        //6.如果校验成功，放行
        log.info("令牌合法，放行");
        filterChain.doFilter(request,response);

        //7.删除ThreadLocal中的数据
        CurrentHolder.remove();

    }
}
