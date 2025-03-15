package com.guangge.Interview.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guangge.Interview.comsumer.client.ConsumerClient;
import com.guangge.Interview.record.ProgramRecord;
import com.guangge.Interview.services.ConsumerService;
import com.guangge.Interview.vo.CheckProgramRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequestMapping("interview")
public class InterViewController {
    private static final Logger logger = LoggerFactory.getLogger(InterViewController.class);

    private final ConsumerClient consumerClient;
    private final ConsumerService consumerService;

    public InterViewController(ConsumerClient consumerClient, ConsumerService consumerService) {
        this.consumerClient = consumerClient;
        this.consumerService = consumerService;
    }

    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat(@RequestParam("chatId") String chatId, @RequestParam("userMessage") String userMessage)  {
        return this.consumerService.interViewChat(chatId,userMessage);
    }

    @PostMapping(value="/face2faceChat", produces = "audio/wav")
    public ResponseEntity<byte[]> face2faceChat(@RequestParam("chatId") String chatId,@RequestParam(value ="userName", required = false) String userName,
                                                @RequestParam(value = "audio", required = false) MultipartFile audio) {
        return this.consumerClient.face2faceChat(chatId,userName,audio);
    }

    @GetMapping(value="/welcomemp3", produces = "audio/mp3")
    public ResponseEntity<byte[]> welcomemp3() {
        return this.consumerClient.welcomemp3();
    }

    @GetMapping(value = "/makeProgram")
    public ResponseEntity<ProgramRecord> program(@RequestParam("first") Boolean first,
                                                 @RequestParam("name") String name) {
        return this.consumerClient.program(first,name);
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping(value = "/checkProgram", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> checkProgram(@RequestParam("data") String encodedData) throws JsonProcessingException, UnsupportedEncodingException {
        // 1. 解码 Base64
        byte[] decodedBytes = Base64.getDecoder().decode(encodedData);
        String urlEncodedData = new String(decodedBytes, StandardCharsets.UTF_8);

        // 2. 解码 URL 编码
        String jsonParams = URLDecoder.decode(urlEncodedData, StandardCharsets.UTF_8.toString());

        // 3. 将 JSON 字符串转换为对象
        CheckProgramRequest request = objectMapper.readValue(jsonParams, CheckProgramRequest.class);

        logger.debug("Question: " + request.getQuestion());
        logger.debug("Input: " + request.getInput());
        logger.debug("Output: " + request.getOutput());
        logger.debug("Code: " + request.getCode());

        return this.consumerService.checkProgram(request);
    }
}
