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

    public Flux<String> interViewChat(String chatId, String userMessage, String token)  {
        ChatReqeust chatReqeust = new ChatReqeust();
        chatReqeust.setChatId(chatId);
        chatReqeust.setUserMessage(userMessage);

        WebClient.RequestHeadersSpec<?> requestSpec = this.webClient.post().uri("/interview/chat")
                .bodyValue(chatReqeust);
                
        // 如果提供了token，添加到请求头
        if (token != null && !token.isEmpty()) {
            requestSpec = requestSpec.header("Authorization", token);
        }

        return requestSpec.retrieve()
                .bodyToFlux(String.class);
    }

    /**
     * 发送邮件
     * @param name 姓名
     * @param email 邮箱（可选）
     * @return void
     */
    public void sendMail(String name, String email) {
        WebClient.RequestHeadersSpec<?> requestSpec;
        
        if (email != null && !email.isEmpty()) {
            requestSpec = this.webClient.post()
                .uri(uriBuilder -> uriBuilder
                    .path("/frontend/sendMail")
                    .queryParam("name", name)
                    .queryParam("email", email)
                    .build());
        } else {
            requestSpec = this.webClient.post()
                .uri(uriBuilder -> uriBuilder
                    .path("/frontend/sendMail")
                    .queryParam("name", name)
                    .build());
        }
        
        requestSpec.retrieve().toBodilessEntity().block();
    }
}
