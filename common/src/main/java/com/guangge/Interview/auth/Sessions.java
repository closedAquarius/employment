package com.guangge.Interview.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Sessions {
    public static final long SHORT_SESSION = TimeUnit.HOURS.toMillis(12);
    public static final long LONG_SESSION = TimeUnit.HOURS.toMillis(30 * 24);

    public static String loginUser(String name,
                                 boolean rememberMe,
                                 String secret) {
        long duration;
        int maxAge;

        if (rememberMe) {
            // "Remember me"
            duration = LONG_SESSION;
        } else {
            duration = SHORT_SESSION;
        }
        maxAge = (int) (duration / 1000);

        return Sign.getToken(name, secret);
    }

    public static String getToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) return null;
        Cookie tokenCookie = Arrays.stream(cookies)
                .filter(cookie -> AuthConstant.COOKIE_NAME.equals(cookie.getName()))
                .findAny().orElse(null);
        if (tokenCookie == null) return null;
        return tokenCookie.getValue();
    }

    public static void logout(String externalApex, HttpServletResponse response) {
        Cookie cookie = new Cookie(AuthConstant.COOKIE_NAME, "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setDomain(externalApex);
        response.addCookie(cookie);
    }
}
