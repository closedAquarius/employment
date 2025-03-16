package com.guangge.Interview.controller;

import com.guangge.Interview.comsumer.client.ConsumerClient;
import com.guangge.Interview.util.CommonResult;
import com.guangge.Interview.vo.UserResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    private final ConsumerClient consumerClient;

    public LoginController(ConsumerClient consumerClient) {
        this.consumerClient = consumerClient;
    }

    @PostMapping(value = "/login")
    public CommonResult<UserResponse> login(@RequestParam("name") String name,
                                            @RequestParam("code") String code) {
        return consumerClient.login(name,code);
    }

    @PostMapping(value = "/auth/verify-token")
    public CommonResult<String> verifyToken(@RequestHeader("token") String token) {
        return consumerClient.verifyToken(token);
    }
}
