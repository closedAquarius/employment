package com.gr.geias.util;

import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;

/**
 * Token工具类，作为JwtUtil的适配器
 */
@Component
public class TokenUtil {

    /**
     * 从token中提取Claims
     * @param token JWT令牌
     * @return Claims对象
     */
    public static Claims extractClaims(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return JwtUtil.parseAccessToken(token);
    }
} 