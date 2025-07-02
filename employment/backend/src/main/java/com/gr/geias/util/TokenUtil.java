package com.gr.geias.util;

import io.jsonwebtoken.Claims;

public class TokenUtil {
    public static Claims extractClaims(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("无效的Authorization头");
        }
        String token = authHeader.substring(7);
        return JwtUtil.parseAccessToken(token);
    }
}