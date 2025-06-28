package com.gr.geias.util;

import io.jsonwebtoken.*;

import java.util.Date;

public class JwtUtil {

    private static final String ACCESS_SECRET_KEY = "access-secret-key";
    private static final String REFRESH_SECRET_KEY = "refresh-secret-key";

    // accessToken 有效时间：30分钟
    private static final long ACCESS_EXPIRE_TIME = 1000 * 60 * 30;
    // refreshToken 有效时间：7天
    private static final long REFRESH_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 7;

    //Access Token
    public static String generateAccessToken(Integer userId, String username,Integer roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("roles",roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS256, ACCESS_SECRET_KEY)
                .compact();
    }

    public static Claims parseAccessToken(String token) throws JwtException {
        return Jwts.parser()
                .setSigningKey(ACCESS_SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    //Refresh Token
    public static String generateRefreshToken(Integer userId, String username,Integer roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("roles",roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS256, REFRESH_SECRET_KEY)
                .compact();
    }

    public static Claims parseRefreshToken(String token) throws JwtException {
        return Jwts.parser()
                .setSigningKey(REFRESH_SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    //校验方法
    public static boolean validateAccessToken(String token) {
        try {
            parseAccessToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public static boolean validateRefreshToken(String token) {
        try {
            parseRefreshToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
