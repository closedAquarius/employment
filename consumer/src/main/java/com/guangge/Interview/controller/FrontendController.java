package com.guangge.Interview.controller;

import com.guangge.Interview.mail.MailService;
import com.guangge.Interview.record.CandidateRecord;
import com.guangge.Interview.record.InterViewRecord;
import com.guangge.Interview.services.CandidatesService;
import com.guangge.Interview.services.ResumeService;
import com.guangge.Interview.services.ResumeVectorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("frontend")
public class FrontendController {
    private final ResumeService resumeService;
    private final MailService mailService;
    private final ResumeVectorService resumeVectorService;
    private final CandidatesService candidatesService;

    public FrontendController(ResumeService resumeService,
                              MailService mailService,
                              ResumeVectorService resumeVectorService,
                              CandidatesService candidatesService) {
        this.resumeService = resumeService;
        this.mailService = mailService;
        this.resumeVectorService = resumeVectorService;
        this.candidatesService = candidatesService;
    }

    /**
     * 面试信息
     * @return 面试者信息
     */
    @GetMapping(value = "/interView")
    public List<InterViewRecord> getInterView() {return this.resumeService.getInterViews();}

    /**
     * 给面试者发送结果
     * @param name 面试者名
     */
    @PostMapping(value = "/sendMail")
    public void sendMail(@RequestParam("name") String name) {
        this.mailService.sendMailForAttachment(name);
    }

    /**
     * 查询面试信息
     * @param question 问题
     * @return 面试者信息
     */
    @GetMapping(value = "/findInterView")
    public List<InterViewRecord> findInterView(@RequestParam("question") String question) {
        return this.resumeVectorService.findInterViewsByQuestion(question);
    }

    /**
     * 面试者信息
     * @return 面试者
     */
    @GetMapping(value = "/candidates")
    public List<CandidateRecord> getCandidates() {return this.candidatesService.getCandidates();}
}
