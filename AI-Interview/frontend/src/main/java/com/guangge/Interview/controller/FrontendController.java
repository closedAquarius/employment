package com.guangge.Interview.controller;

import com.guangge.Interview.comsumer.client.ConsumerClient;
import com.guangge.Interview.record.CandidateRecord;
import com.guangge.Interview.record.InterViewRecord;
import com.guangge.Interview.services.ConsumerService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController()
@RequestMapping("frontend")
public class FrontendController {

    private final ConsumerClient consumerClient;
    private final ConsumerService consumerService;

    public FrontendController(ConsumerClient consumerClient, ConsumerService consumerService) {
        this.consumerClient = consumerClient;
        this.consumerService = consumerService;
    }

    @GetMapping(value = "/interView")
    public List<InterViewRecord> getInterView() {
        return this.consumerClient.getInterView();
    }

    @PostMapping(value = "/sendMail")
    public void sendMail(@RequestParam("name") String name) {
        this.consumerClient.sendMail(name);
    }

    @GetMapping(value = "/findInterView")
    public List<InterViewRecord> findInterView(@RequestParam("question") String question) {
        return this.consumerClient.findInterView(question);
    }

    @GetMapping(value = "/candidates")
    public List<CandidateRecord> getCandidates() {
        return this.consumerClient.getCandidates();
    }
}
