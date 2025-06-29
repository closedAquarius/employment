package com.guangge.Interview.controller;

import com.guangge.Interview.assistant.InterviewAssistant;
import com.guangge.Interview.assistant.JavaAssistant;
import com.guangge.Interview.audio.services.SpeechToTextService;
import com.guangge.Interview.audio.services.TextToSpeechService;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api")
public class AudioStreamController {

    private final JavaAssistant interViewAgent;
    private final InterviewAssistant interviewAssistant;
    private final TextToSpeechService speechService;
    private final SpeechToTextService speechToTextService;
    private final ResourceLoader resourceLoader;

    public AudioStreamController(JavaAssistant interViewAgent, InterviewAssistant interviewAssistant,
                               TextToSpeechService speechService, SpeechToTextService speechToTextService,
                               ResourceLoader resourceLoader) {
        this.interViewAgent = interViewAgent;
        this.interviewAssistant = interviewAssistant;
        this.speechService = speechService;
        this.speechToTextService = speechToTextService;
        this.resourceLoader = resourceLoader;
    }


    @GetMapping(value = "/stream-audio", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Mono<Void> streamAudio(ServerHttpResponse response) {
        // 设置响应头
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, "audio/mpeg");
        response.getHeaders().set(HttpHeaders.TRANSFER_ENCODING, "chunked");

        // 获取大模型响应
        Flux<String> textStream = interviewAssistant.chatByStream("1","张三");

        // 将文本流转换为音频流
        DataBufferFactory bufferFactory = new DefaultDataBufferFactory();
        Flux<DataBuffer> audioStream = textStream.flatMap(textChunk -> {
            try {
                // 将音频数据转换为 DataBuffer
                return Mono.just(bufferFactory.wrap(speechService.textToSpeech(textChunk)));
            } catch (Exception e) {
                return Mono.error(e);
            }
        });

        // 将音频流写入响应
        return response.writeAndFlushWith(audioStream.map(Flux::just));
    }
}
