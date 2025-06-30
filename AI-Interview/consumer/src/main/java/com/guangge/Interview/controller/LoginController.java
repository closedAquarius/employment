package com.guangge.Interview.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.guangge.Interview.auth.AuthClient;
import com.guangge.Interview.auth.Sessions;
import com.guangge.Interview.auth.Sign;
import com.guangge.Interview.data.Candidates;
import com.guangge.Interview.services.CandidatesService;
import com.guangge.Interview.util.CommonResult;
import com.guangge.Interview.vo.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    private final CandidatesService candidatesService;
    private final AuthClient authClient;

    @Value("${auth.token.secret}")
    private String secret;

    @Autowired
    public LoginController(CandidatesService candidatesService, AuthClient authClient) {
        this.candidatesService = candidatesService;
        this.authClient = authClient;
    }

    /**
     * 登录
     * @param name 登录名
     * @param code 邀请code
     * @return 登录信息
     * @throws Exception
     */
    @PostMapping(value = "/login/direct")
    public CommonResult<UserResponse> login(@RequestParam("name") String name,
                                            @RequestParam("code") String code) throws Exception {
        // 先验证面试候选人信息
        Candidates candidates = this.candidatesService.longin(name, code);
        if (candidates == null) {
            return CommonResult.failed("面试邀请码无效或已过期");
        }
        
        // 同步用户信息到auth-service
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", candidates.getName());
        userInfo.put("password", code); // 使用邀请码作为初始密码
        userInfo.put("realName", candidates.getName());
        userInfo.put("userType", 3); // 面试者类型
        userInfo.put("status", 1); // 启用状态
        
        authClient.syncUserInfo(userInfo);
        
        // 使用统一认证服务登录
        Map<String, Object> loginResult = authClient.login(candidates.getName(), code);
        
        if (loginResult != null && loginResult.containsKey("token")) {
            UserResponse userResponse = new UserResponse();
            userResponse.setUserId(candidates.getId());
            userResponse.setUserName(candidates.getName());
            userResponse.setToken((String) loginResult.get("token"));
            return CommonResult.success(userResponse);
        } else {
            // 如果统一认证失败，回退到原有方式
            String token = Sessions.loginUser(candidates.getName(), true, secret);
            UserResponse userResponse = new UserResponse();
            userResponse.setUserId(candidates.getId());
            userResponse.setUserName(candidates.getName());
            userResponse.setToken(token);
            return CommonResult.success(userResponse);
        }
    }
    
    /**
     * 标准登录接口（用户名密码）
     */
    @PostMapping("/login/standard")
    public CommonResult<UserResponse> standardLogin(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        
        // 调用统一认证服务
        Map<String, Object> loginResult = authClient.login(username, password);
        
        if (loginResult != null && loginResult.containsKey("token")) {
            UserResponse userResponse = new UserResponse();
            userResponse.setUserId(Long.valueOf(loginResult.get("userId").toString()));
            userResponse.setUserName((String) loginResult.get("username"));
            userResponse.setToken((String) loginResult.get("token"));
            return CommonResult.success(userResponse);
        } else {
            return CommonResult.failed("用户名或密码错误");
        }
    }

    /**
     * JWT认证
     * @param token token
     * @return 认证信息
     */
    @PostMapping(value = "/login/verify-token")
    public CommonResult<String> verifyToken(@RequestHeader("token") String token) {
        DecodedJWT decodedJWT = null;
        try {
            decodedJWT = Sign.verifyToken(token, secret);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return CommonResult.unauthorized("Token验证失败");
        }
        String username = decodedJWT.getClaim("userName").asString();
        return CommonResult.success(username);
    }
}
