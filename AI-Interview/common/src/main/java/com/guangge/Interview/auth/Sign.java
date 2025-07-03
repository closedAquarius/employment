package com.guangge.Interview.auth;

import com.guangge.Interview.exception.RestException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.util.StringUtils;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class Sign {

    private static Map<String, Key> secretKeyMap = new HashMap<>();
    
    // 使用与auth-service相同的密钥
    private static final String DEFAULT_SECRET = "eAf6XIz7Q6CmE3N4K5L6M7N8O9P0Q1R2S3T4U5V6W7X8Y9Z0aBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz";

    public static String getToken(String userName, String secret) {
        if (StringUtils.isEmpty(secret)) {
            secret = DEFAULT_SECRET;
        }
        
        // 创建JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userName); // 使用与auth-service相同的字段名
        claims.put("userId", 1); // 默认用户ID
        claims.put("userType", 2); // 默认管理员类型
        
        // 获取或创建密钥
        Key secretKey = getSecretKey(secret);
        
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setId(java.util.UUID.randomUUID().toString())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1小时过期
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public static Claims verifyToken(String token, String secret) {
        if (StringUtils.isEmpty(token)) {
            throw new RestException("00Z", "Token is empty!");
        }
        
        if (StringUtils.isEmpty(secret)) {
            secret = DEFAULT_SECRET;
        }
        
        try {
            // 如果token带有Bearer前缀，去除前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            // 获取或创建密钥
            Key secretKey = getSecretKey(secret);
            
            // 解析JWT
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
            
            return claims;
        } catch (Exception e) {
            throw new RestException("00Z", "Token is error: " + e.getMessage());
        }
    }
    
    private static Key getSecretKey(String secret) {
        // 使用缓存避免重复创建密钥
        if (!secretKeyMap.containsKey(secret)) {
            try {
                // 确保密钥长度足够，并使用Base64解码
                byte[] keyBytes = Base64.getDecoder().decode(secret.getBytes("UTF-8"));
                Key key = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
                secretKeyMap.put(secret, key);
                System.out.println("JWT密钥创建成功，使用算法: " + SignatureAlgorithm.HS256.getJcaName());
            } catch (Exception e) {
                throw new RestException("00Y", "Failed to create signing key: " + e.getMessage());
            }
        }
        return secretKeyMap.get(secret);
    }
}