package com.guangge.Interview.controller;

import com.guangge.Interview.assistant.JavaAssistant;
import com.guangge.Interview.mail.MailService;
import com.guangge.Interview.record.InterViewRecord;
import com.guangge.Interview.services.ResumeService;
import com.guangge.Interview.services.ResumeVectorService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController()
@RequestMapping("frontend")
public class FrontendController {
    private final JavaAssistant interViewAgent;
    private final ResumeService resumeService;
    private final MailService mailService;
    private final ResumeVectorService resumeVectorService;

    public FrontendController(JavaAssistant interViewAgent, ResumeService resumeService,
                              MailService mailService, ResumeVectorService resumeVectorService) {
        this.interViewAgent = interViewAgent;
        this.resumeService = resumeService;
        this.mailService = mailService;
        this.resumeVectorService = resumeVectorService;
    }

    @GetMapping(value = "/interViewChat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> interViewChat(@RequestParam("chatId") String chatId, @RequestParam("userMessage") String userMessage)  {
        return interViewAgent.chat(chatId, userMessage);
    }

    @GetMapping(value = "/interView")
    public List<InterViewRecord> getInterView() {return this.resumeService.getInterViews();}

    @PostMapping(value = "/sendMail")
    public void sendMail(@RequestParam("name") String name) {
        this.mailService.sendMailForAttachment(name);
    }

    @GetMapping(value = "/findInterView")
    public List<InterViewRecord> findInterView(@RequestParam("question") String question) {
        return this.resumeVectorService.findInterViewsByQuestion(question);
    }
}
