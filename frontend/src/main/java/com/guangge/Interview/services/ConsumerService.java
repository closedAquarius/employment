package com.guangge.Interview.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class ConsumerService {
    @Value("${consumer-service-endpoint}")
    private String url;

    private final WebClient webClient;

    public ConsumerService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(url).build();
    }

    public Flux<String> checkProgram(String question,String input,String output,String code) {
        return this.webClient.post().uri("/interview/checkProgram")
                .retrieve()
                .bodyToFlux(String.class);
    }

    public Flux<String> interViewChat(String chatId, String userMessage)  {
        return this.webClient.get().uri("/frontend/interViewChat")
                .retrieve()
                .bodyToFlux(String.class);
    }
}
