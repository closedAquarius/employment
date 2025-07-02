package com.gr.geias.util;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    // 使用与auth-service相同的密钥
    private static final String JWT_SECRET = "eAf6XIz7Q6CmE3N4K5L6M7N8O9P0Q1R2S3T4U5V6W7X8Y9Z0aBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz";

    // accessToken 有效时间：30分钟
    private static final long ACCESS_EXPIRE_TIME = 1000 * 60 * 30;
    // refreshToken 有效时间：7天
    private static final long REFRESH_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 7;
    
    private static SecretKey secretKey;
    
    static {
        try {
            // 确保密钥长度足够，并使用Base64解码
            byte[] keyBytes = Base64.getDecoder().decode(JWT_SECRET.getBytes("UTF-8"));
            secretKey = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
            System.out.println("JWT密钥初始化成功，使用算法: " + SignatureAlgorithm.HS256.getJcaName());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("JWT密钥初始化失败", e);
        }
    }

    //Access Token
    public static String generateAccessToken(Integer userId, String username, Integer roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setId(java.util.UUID.randomUUID().toString())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public static Claims parseAccessToken(String token) throws JwtException {
        try {
            // 如果token带有Bearer前缀，去除前缀
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
        return Jwts.parser()
                    .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        } catch (JwtException e) {
            System.out.println("JWT解析错误: " + e.getMessage() + ", Token: " + (token != null ? token.substring(0, Math.min(10, token.length())) + "..." : "null"));
            throw e;
        }
    }

    //Refresh Token
    public static String generateRefreshToken(Integer userId, String username, Integer roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setId(java.util.UUID.randomUUID().toString())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public static Claims parseRefreshToken(String token) throws JwtException {
        try {
            // 如果token带有Bearer前缀，去除前缀
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
        return Jwts.parser()
                    .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        } catch (JwtException e) {
            System.out.println("JWT解析错误: " + e.getMessage() + ", Token: " + (token != null ? token.substring(0, Math.min(10, token.length())) + "..." : "null"));
            throw e;
        }
    }

    //校验方法
    public static boolean validateAccessToken(String token) {
        try {
            parseAccessToken(token);
            return true;
        } catch (JwtException e) {
            System.out.println("JWT验证失败: " + e.getMessage());
            return false;
        }
    }

    public static boolean validateRefreshToken(String token) {
        try {
            parseRefreshToken(token);
            return true;
        } catch (JwtException e) {
            System.out.println("JWT验证失败: " + e.getMessage());
            return false;
        }
    }
}