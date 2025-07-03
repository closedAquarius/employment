package com.gr.geias.interceptor;

import com.gr.geias.enums.EnableStatusEnums;
import com.gr.geias.util.JwtUtil;
import com.gr.geias.util.TokenUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        logger.debug("Request path: {}", request.getRequestURI());
        logger.debug("Authorization header: {}", authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                logger.debug("Processing token: {}", token.substring(0, Math.min(token.length(), 20)) + "...");
                
                Claims claims = JwtUtil.parseAccessToken(token);
                logger.debug("Claims parsed successfully");
                
                // 打印所有claims键值
                logger.debug("Token claims: {}", claims.entrySet());

                // 尝试获取userId，兼容不同格式的令牌
                Object userIdObj = claims.get("userId");
                logger.debug("UserId from token (raw): {}, type: {}", userIdObj, 
                          userIdObj != null ? userIdObj.getClass().getName() : "null");
                
                Integer userId = null;
                if (userIdObj instanceof Integer) {
                    userId = (Integer) userIdObj;
                } else if (userIdObj instanceof Long) {
                    userId = ((Long) userIdObj).intValue();
                } else if (userIdObj instanceof String) {
                    userId = Integer.parseInt((String) userIdObj);
                }
                logger.debug("UserId parsed: {}", userId);
                
                String username = claims.getSubject();
                logger.debug("Username: {}", username);
                
                // 尝试获取角色，兼容不同格式的令牌
                Integer role = null;
                Object roleObj = claims.get("roles");
                Object userTypeObj = claims.get("userType");
                
                logger.debug("Role object: {}, type: {}", roleObj, 
                          roleObj != null ? roleObj.getClass().getName() : "null");
                logger.debug("UserType object: {}, type: {}", userTypeObj, 
                          userTypeObj != null ? userTypeObj.getClass().getName() : "null");
                
                if (roleObj != null) {
                    // employment服务的格式
                    role = (Integer) roleObj;
                } else {
                    // auth-service的格式
                    if (userTypeObj instanceof Integer) {
                        role = (Integer) userTypeObj;
                    } else if (userTypeObj instanceof String) {
                        role = Integer.parseInt((String) userTypeObj);
                    }
                }
                
                // 如果无法获取角色，默认为学生角色
                if (role == null) {
                    role = 0; // 学生角色
                    logger.debug("Role not found in token, defaulting to student (0)");
                } else {
                    logger.debug("Role determined: {}", role);
                }
                
                List<GrantedAuthority> authorities = Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + EnableStatusEnums.stateOf(role).name())
                );
                logger.debug("Authorities: {}", authorities);
                
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.debug("Authentication set in SecurityContext");

            } catch (JwtException e) {
                logger.error("JWT validation error", e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            } catch (Exception e) {
                logger.error("Unexpected error processing JWT", e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } else {
            logger.debug("No Authorization header or not Bearer token");
        }

        filterChain.doFilter(request, response);
    }
}
