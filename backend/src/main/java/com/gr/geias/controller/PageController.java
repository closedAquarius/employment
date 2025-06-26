package com.gr.geias.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 页面控制器 
 */
@Controller
@RequestMapping("/page")
public class PageController {

    /**
     * 登录页面
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    /**
     * 首页
     */
    @GetMapping("/index")
    public String index() {
        return "index";
    }
    
    /**
     * 仪表盘
     */
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
    
    /**
     * 健康检查
     */
    @GetMapping("/health")
    @ResponseBody
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("message", "服务正常运行");
        return result;
    }
} 