package com.guangge.Interview.controller;

import com.guangge.Interview.auth.AuthClient;
import com.guangge.Interview.auth.AuthConstant;
import com.guangge.Interview.util.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/interview")
public class LoginController {

    @Autowired
    private AuthClient authClient;

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public CommonResult<String> health() {
        return CommonResult.success("AI Interview Service is running");
    }

    /**
     * 用户登录
     */
    @PostMapping("/login-api")
    public CommonResult<Map<String, Object>> login(@RequestBody Map<String, String> loginInfo) {
        String username = loginInfo.get("username");
        String password = loginInfo.get("password");
        
        Map<String, Object> result = authClient.login(username, password);
        
        if (result != null && result.containsKey("token")) {
            return CommonResult.success(result);
        } else {
            return CommonResult.validateFailed("用户名或密码错误");
        }
    }
    
    /**
     * 验证令牌
     */
    @GetMapping("/validate-token")
    public CommonResult<Map<String, Object>> validateToken(@RequestHeader(AuthConstant.HEADER_AUTH) String authHeader) {
        if (authHeader == null || !authHeader.startsWith(AuthConstant.TOKEN_PREFIX)) {
            return CommonResult.unauthorized("未提供有效的认证信息");
        }
        
        String token = authHeader.substring(AuthConstant.TOKEN_PREFIX.length()).trim();
        Map<String, Object> userInfo = authClient.validateToken(token);
        
        if (userInfo != null) {
            return CommonResult.success(userInfo);
        } else {
            return CommonResult.unauthorized("令牌无效或已过期");
        }
    }

    /**
     * 刷新令牌
     */
    @GetMapping("/refresh-token")
    public CommonResult<Map<String, Object>> refreshToken(@RequestHeader(AuthConstant.HEADER_AUTH) String authHeader) {
        if (authHeader == null || !authHeader.startsWith(AuthConstant.TOKEN_PREFIX)) {
            return CommonResult.unauthorized("未提供有效的认证信息");
        }
        
        String token = authHeader.substring(AuthConstant.TOKEN_PREFIX.length()).trim();
        String newToken = authClient.refreshToken(token);
        
        if (newToken != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("token", newToken);
            return CommonResult.success(result);
        } else {
            return CommonResult.unauthorized("令牌刷新失败");
        }
    }
}
