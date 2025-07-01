package com.employment.auth.util;

import com.employment.auth.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT工具类
 */
@Component
public class JwtTokenUtil {

    private static final String CLAIM_KEY_USER_ID = "userId";
    private static final String CLAIM_KEY_USERNAME = "sub";
    private static final String CLAIM_KEY_USER_TYPE = "userType";
    private static final String CLAIM_KEY_SOURCE_SYSTEM = "sourceSystem";
    private static final String CLAIM_KEY_REAL_NAME = "realName";
    private static final String CLAIM_KEY_EMAIL = "email";
    private static final String CLAIM_KEY_PHONE = "phone";
    private static final String CLAIM_KEY_AVATAR = "avatar";
    private static final String CLAIM_KEY_CREATED = "created";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;
    
    @Value("${spring.application.name}")
    private String issuer;

    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成JWT Token
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USER_ID, user.getId());
        claims.put(CLAIM_KEY_USERNAME, user.getUsername());
        claims.put(CLAIM_KEY_USER_TYPE, user.getUserType());
        claims.put(CLAIM_KEY_SOURCE_SYSTEM, user.getSourceSystem());
        claims.put(CLAIM_KEY_REAL_NAME, user.getRealName());
        claims.put(CLAIM_KEY_EMAIL, user.getEmail());
        claims.put(CLAIM_KEY_PHONE, user.getPhone());
        claims.put(CLAIM_KEY_AVATAR, user.getAvatar());
        claims.put(CLAIM_KEY_CREATED, new Date());
        
        // 根据用户类型设置不同的过期时间
        long expirationTime = expiration;
        if (user.getUserType() != null) {
            int userType = user.getUserType();
            switch (userType) {
                case 0: // 学生
                    expirationTime = expiration * 2; // 学生token有效期延长
                    break;
                case 1: // 教师
                    expirationTime = expiration * 3; // 教师token有效期更长
                    break;
                case 2: // 管理员
                    expirationTime = expiration * 4; // 管理员token有效期最长
                    break;
                case 3: // 企业HR
                    expirationTime = expiration; // 企业HR使用默认有效期
                    break;
                default:
                    expirationTime = expiration;
                    break;
            }
        }
        
        return generateToken(claims, expirationTime);
    }

    /**
     * 根据负载生成JWT Token
     */
    private String generateToken(Map<String, Object> claims, long expirationTime) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate(expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 生成token过期时间
     */
    private Date generateExpirationDate(long expirationTime) {
        return new Date(System.currentTimeMillis() + expirationTime);
    }

    /**
     * 从token中获取JWT中的负载
     */
    private Claims getClaimsFromToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            // 解析失败
        }
        return claims;
    }

    /**
     * 从token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * 从token中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Long userId;
        try {
            Claims claims = getClaimsFromToken(token);
            userId = Long.parseLong(claims.get(CLAIM_KEY_USER_ID).toString());
        } catch (Exception e) {
            userId = null;
        }
        return userId;
    }

    /**
     * 从token中获取用户类型
     */
    public Integer getUserTypeFromToken(String token) {
        Integer userType;
        try {
            Claims claims = getClaimsFromToken(token);
            userType = Integer.parseInt(claims.get(CLAIM_KEY_USER_TYPE).toString());
        } catch (Exception e) {
            userType = null;
        }
        return userType;
    }

    /**
     * 验证token是否有效
     */
    public boolean validateToken(String token, String username) {
        String tokenUsername = getUsernameFromToken(token);
        return tokenUsername != null && tokenUsername.equals(username) && !isTokenExpired(token);
        }

    /**
     * 判断token是否已经失效
     */
    private boolean isTokenExpired(String token) {
        Date expiredDate = getExpiredDateFromToken(token);
        return expiredDate != null && expiredDate.before(new Date());
    }

    /**
     * 从token中获取过期时间
     */
    private Date getExpiredDateFromToken(String token) {
        Date expiration;
        try {
            Claims claims = getClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public String getTokenId(String token) {
        return getClaimFromToken(token, Claims::getId);
    }
    
    public Map<String, Object> getPermissionsFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return (Map<String, Object>) claims.get("permissions");
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
} 