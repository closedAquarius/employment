package com.guangge.Interview.controller;

import com.guangge.Interview.services.CandidatesService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("candidate")
public class CandidateController {

    private final CandidatesService interviewerService;

    
    public CandidateController(CandidatesService interviewerService) {
        this.interviewerService = interviewerService;
    }

}
