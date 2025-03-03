package com.guangge.Interview.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.guangge.Interview.Application;
import com.guangge.Interview.auth.AuthConstant;
import com.guangge.Interview.auth.Sessions;
import com.guangge.Interview.auth.Sign;
import com.guangge.Interview.data.Interviewer;
import com.guangge.Interview.services.InterviewerService;
import com.guangge.Interview.util.CommonResult;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    private final InterviewerService interviewerService;

    @Value("${auth.token.secret}")
    private String secret;

    public LoginController(InterviewerService interviewerService) {
        this.interviewerService = interviewerService;
    }


    @PostMapping(value = "/login")
    public CommonResult<String> login(@RequestParam("name") String name,
                                             @RequestParam("code") String code,
                                             HttpServletResponse response  ) throws Exception {
        Interviewer interviewer = this.interviewerService.longin(name,code);
        Sessions.loginUser(interviewer.getName(),
                true,
                secret,
                response);
        return CommonResult.success("ok");
    }

    @PostMapping(value = "/auth/verify-token")
    public CommonResult<String> verifyToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String token = "";
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(AuthConstant.COOKIE_NAME)) {
                token = cookie.getValue();
                break;
            }
        }
        //token = "Bearer " + token;
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
