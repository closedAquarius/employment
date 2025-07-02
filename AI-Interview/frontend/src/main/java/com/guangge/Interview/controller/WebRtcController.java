package com.guangge.Interview.controller;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.core.io.InputStreamResource;

import java.time.Duration;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/webrtc")
public class WebRtcController {

    private static final Logger logger = Logger.getLogger(WebRtcController.class.getName());
    
    private final WebClient pythonClient;
    
    @Value("${python.azure.openai.service.url:http://localhost:7860}")
    private String pythonServiceUrl;

    public WebRtcController(WebClient.Builder webClientBuilder,@Value("${python.azure.openai.service.url}") String url) {
        logger.info("初始化WebRTC控制器，Python服务URL: " + url);
        
        // 构建具有较长超时时间的WebClient
        HttpClient httpClient = HttpClient.create()
            .responseTimeout(Duration.ofMinutes(30))
            .keepAlive(true)
            .wiretap(true); // 开启详细日志
            
        this.pythonClient = webClientBuilder
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
                
        // 测试连接
        testConnection();
    }
    
    /**
     * 测试与Python服务的连接
     */
    private void testConnection() {
        pythonClient.get()
            .uri("/health")
            .retrieve()
            .bodyToMono(String.class)
            .subscribe(
                response -> logger.info("Python服务连接测试成功: " + response),
                error -> logger.severe("Python服务连接测试失败: " + error.getMessage())
            );
    }

    // 代理Python的offer接口
    @PostMapping("/offer")
    public Mono<ResponseEntity<String>> createOffer(@RequestBody Map<String, Object> request) {
        logger.info("收到WebRTC offer请求: " + request);
        return pythonClient.post()
                .uri("/webrtc/offer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> logger.info("WebRTC offer响应: " + response))
                .map(response -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Access-Control-Allow-Origin", "*")
                        .body(response))
                .onErrorResume(e -> {
                    logger.severe("WebRTC offer错误: " + e.getMessage());
                    e.printStackTrace();
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body("{\"error\": \"" + e.getMessage().replace("\"", "'") + "\"}"));
                })
                .timeout(Duration.ofSeconds(30), Mono.just(ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\": \"请求超时，请检查Python服务是否正常运行\"}")));
    }

    // 代理Python的SSE输出流
    @GetMapping(value = "/outputs", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<String>> streamOutputs(@RequestParam String webrtc_id) {
        logger.info("建立WebRTC输出流连接，ID: " + webrtc_id);
        try {
            Flux<String> result = pythonClient.get()
                    .uri(uri -> uri.path("/outputs")
                            .queryParam("webrtc_id", webrtc_id)
                            .build())
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .retrieve()
                    .bodyToFlux(String.class)
                    .doOnNext(event -> {
                        if (!event.contains("heartbeat")) {  // 不打印心跳事件
                            logger.info("SSE事件: " + event);
                        }
                    })
                    .onErrorResume(e -> {
                        logger.severe("输出流错误: " + e.getMessage());
                        e.printStackTrace();
                        return Flux.just("event: error\ndata: {\"error\": \"" + e.getMessage().replace("\"", "'") + "\"}\n\n");
                    })
                    .doFinally(signal -> logger.info("输出流结束: " + signal));
                    
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_EVENT_STREAM)
                    .body(result);
        } catch (Exception e) {
            logger.severe("输出流总体错误: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok().body(Flux.just("event: error\ndata: {\"error\": \"" + e.getMessage().replace("\"", "'") + "\"}\n\n"));
        }
    }

    @PostMapping("/personality")
    public Mono<ResponseEntity<String>> personality(@RequestBody Map<String, Object> request) {
        logger.info("收到personality请求: " + request);
        
        // 获取请求来源页面类型，默认为oral（口语练习）
        String pageType = request.containsKey("page_type") ? request.get("page_type").toString() : "oral";
        
        return pythonClient.post()
                .uri("/input_hook")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                    "webrtc_id", request.get("webrtc_id"),
                    "language", "英语",
                    "select_type", request.getOrDefault("personality", "1").toString(),
                    "voice_name", "Puck",
                    "page_type", pageType
                ))
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> logger.info("Personality响应: " + response))
                .map(response -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response))
                .onErrorResume(e -> {
                    logger.severe("Personality错误: " + e.getMessage());
                    e.printStackTrace();
                    return Mono.just(ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body("{\"status\": \"error\", \"message\": \"" + e.getMessage().replace("\"", "'") + "\"}"));
                })
                .timeout(Duration.ofSeconds(30), Mono.just(ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"status\": \"error\", \"message\": \"请求超时，请检查Python服务\"}")));
    }
    
    // 代理gemini-voice接口或使用qianwen-voice
    @GetMapping("/gemini-voice")
    public Mono<ResponseEntity<String>> getGeminiVoice() {
        logger.info("请求语音接口页面");
        
        // 尝试访问千问语音接口
        String uriPath = "/qianwen-voice";
        
        return pythonClient.get()
                .uri(uriPath)
                .accept(MediaType.TEXT_HTML)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> logger.info("语音页面响应成功，长度: " + response.length()))
                .map(response -> ResponseEntity.ok()
                        .contentType(MediaType.TEXT_HTML)
                        .body(response))
                .onErrorResume(e -> {
                    logger.severe("语音页面错误: " + e.getMessage());
                    e.printStackTrace();
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("<html><body><h1>Error</h1><p>" + e.getMessage() + "</p></body></html>"));
                })
                .timeout(Duration.ofSeconds(10), Mono.just(ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                        .body("<html><body><h1>超时</h1><p>Python服务无响应，请检查服务是否正常运行</p></body></html>")));
    }
    
    // 代理input_hook接口
    @PostMapping("/input_hook")
    public Mono<ResponseEntity<String>> inputHook(@RequestBody Map<String, Object> request) {
        logger.info("Input hook请求: " + request);
        return pythonClient.post()
                .uri("/input_hook")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> logger.info("Input hook响应: " + response))
                .map(response -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Access-Control-Allow-Origin", "*")
                        .body(response))
                .onErrorResume(e -> {
                    logger.severe("Input hook错误: " + e.getMessage());
                    e.printStackTrace();
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body("{\"error\": \"" + e.getMessage().replace("\"", "'") + "\"}"));
                })
                .timeout(Duration.ofSeconds(30), Mono.just(ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\": \"请求超时，请检查Python服务\"}")));
    }
}
