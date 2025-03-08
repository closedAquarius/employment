package com.guangge.Interview.controller;

import com.guangge.Interview.services.InterviewerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("candidate")
public class CandidateController {

    private final InterviewerService interviewerService;

    
    public CandidateController(InterviewerService interviewerService) {
        this.interviewerService = interviewerService;
    }

}
