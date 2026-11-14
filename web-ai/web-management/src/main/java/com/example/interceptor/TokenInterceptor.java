package com.example.interceptor;

import com.example.utils.CurrentHolder;
import com.example.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

//        //1.获取到请求的路径
//        String path = request.getRequestURI(); // /login
//
//        //2.判断是否是登陆请求,如果这个路径中包含/login,说明是登录操做，放行
//        if(path.contains("/login")){
//            log.info("登录请求，放行");
//            return true;
//        }

        //3.获取请求头中的token
        String token = request.getHeader("token");

        //4.判断token是否存在，如果不存在，说明用户没有登录，返回错误信息（响应401状态码)
        if (token == null || token.isEmpty()){
            log.info("token不存在，说明用户没有登录，返回错误信息（响应401状态码)");
            response.setStatus(401);
            return false;
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
            return false;
        }

        //6.如果校验成功，放行
        log.info("令牌合法，放行");
        return true;


    }
}
