package com.guangge.Interview.controller;

import com.guangge.Interview.comsumer.client.ConsumerClient;
import com.guangge.Interview.record.ProgramRecord;
import com.guangge.Interview.services.ConsumerService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("interview")
public class InterViewController {
    private final ConsumerClient consumerClient;
    private final ConsumerService consumerService;

    public InterViewController(ConsumerClient consumerClient, ConsumerService consumerService) {
        this.consumerClient = consumerClient;
        this.consumerService = consumerService;
    }

    @PostMapping(value="/face2faceChat", produces = "audio/wav")
    public ResponseEntity<byte[]> face2faceChat(@RequestParam("chatId") String chatId,
                                                @RequestParam("audio") MultipartFile audio) {
        return this.consumerClient.face2faceChat(chatId,audio);
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

    @GetMapping(value = "/checkProgram", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> checkProgram(@RequestParam("question") String question,
                                     @RequestParam("input") String input,
                                     @RequestParam("output") String output,
                                     @RequestParam("code") String code) {
        return this.consumerService.checkProgram(question,input,output,code);
    }
}
