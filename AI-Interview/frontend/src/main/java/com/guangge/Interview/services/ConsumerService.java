package com.guangge.Interview.services;

import com.guangge.Interview.vo.ChatReqeust;
import com.guangge.Interview.vo.CheckProgramRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

@Service
public class ConsumerService {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerService.class);

    private final WebClient webClient;

    public ConsumerService(WebClient.Builder webClientBuilder,@Value("${consumer-service-endpoint}") String url)  {
        this.webClient = webClientBuilder.baseUrl(url).build();
    }

    public Flux<String> checkProgram(CheckProgramRequest request) {
        return this.webClient.post().uri("/interview/checkProgram")
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class);
    }

    public Flux<String> interViewChat(String chatId, String userMessage)  {
        ChatReqeust chatReqeust = new ChatReqeust();
        chatReqeust.setChatId(chatId);
        chatReqeust.setUserMessage(userMessage);

        return this.webClient.post().uri("/interview/chat")
                .bodyValue(chatReqeust)
                .retrieve()
                .bodyToFlux(String.class);
    }
}
