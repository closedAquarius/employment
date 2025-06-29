package com.gr.geias.service;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SpiderService {

    private final RestTemplate rest;
    @Value("${crawler.url:http://localhost:8000}")
    private String crawlerUrl;

    public SpiderService(RestTemplate restTemplate) {
        this.rest = restTemplate;
    }

    public String startSpider(boolean auto) {
        // 构造请求头和 body
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Boolean> body = Collections.singletonMap("auto", auto);
        String url = crawlerUrl + "/crawl";
        System.out.println(">> Java is calling Python at: " + url + "  (auto=" + auto + ")");
        HttpEntity<Map<String, Boolean>> req = new HttpEntity<>(body, headers);

        // 发送 POST 请求到 Python 服务
        return rest.postForObject(crawlerUrl + "/crawl", req, String.class);
    }
}
