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

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/webrtc")
public class WebRtcController {

    private final WebClient pythonClient;

    public WebRtcController(WebClient.Builder webClientBuilder,@Value("${python.azure.openai.service.url}") String url) {
        this.pythonClient = webClientBuilder
                .baseUrl(url) // Python服务地址
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().responseTimeout(Duration.ofHours(1))
                ))
                .build();
    }

    // 代理Python的offer接口
    @PostMapping("/offer")
    public Mono<String> createOffer(@RequestBody Map<String, Object> request) {
        return pythonClient.post()
                .uri("/webrtc/offer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class);
    }

    // 代理Python的SSE输出流
    @GetMapping(value = "/outputs", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamOutputs(@RequestParam String webrtc_id) {
        return pythonClient.get()
                .uri(uri -> uri.path("/outputs")
                        .queryParam("webrtc_id", webrtc_id)
                        .build())
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(String.class);
    }

    @PostMapping("/personality")
    public Flux<String> personality(@RequestBody Map<String, Object> request) {
        return pythonClient.post()
                .uri("/personality")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class);
    }
}
