package com.guangge.Interview.controller;

import com.guangge.Interview.auth.Sessions;
import com.guangge.Interview.auth.Sign;
import com.guangge.Interview.data.Interviewer;
import com.guangge.Interview.services.InterviewerService;
import com.guangge.Interview.util.CommonResult;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("candidate")
public class CandidateController {

    private final InterviewerService interviewerService;

    
    public CandidateController(InterviewerService interviewerService) {
        this.interviewerService = interviewerService;
    }

}
