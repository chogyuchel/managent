package com.example;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {

    /**
     * 生成JWT
     */
    @Test
    public void testGenerateJwt(){
        Map<String,Object> dataMap = new HashMap<>();
        dataMap.put("id","1");
        dataMap.put("username","admin");
        String jwt = Jwts.builder().signWith(SignatureAlgorithm.HS256,"ZXhhbXBsZQ==")   //指定加密算法，密钥
                .addClaims(dataMap) //添加自定义数据
                .setExpiration(new Date(System.currentTimeMillis() + 3600 * 1000)) //设置过期时间
                .compact(); //生成令牌
        System.out.println(jwt);
    }

    /**
     * 解析JWT
     */
    @Test
    public void testParseJWT(){
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6IjEiLCJ1c2VybmFtZSI6ImFkbWluIiwiZXhwIjoxNzkzNTI1NjUwfQ.1zqUIMxQlh_qraUhW9pqAzw1KRS3H79ClrG0werPdMg";
        Claims claims = Jwts.parser().setSigningKey("ZXhhbXBsZQ==")//指定密钥
                .parseClaimsJws(token)//解析令牌
                .getBody();//获取自定义信息
        System.out.println(claims);
    }
}
