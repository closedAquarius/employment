package com.guangge.Interview.services;

import com.guangge.Interview.data.Interviewer;
import com.guangge.Interview.exception.RestException;
import com.guangge.Interview.repository.InterviewerRepository;
import com.guangge.Interview.util.ResultCode;
import org.springframework.stereotype.Service;

@Service
public class InterviewerService {
    private final InterviewerRepository interviewerRepository;

    public InterviewerService(InterviewerRepository interviewerRepository) {
        this.interviewerRepository = interviewerRepository;
    }

    public Interviewer longin(String name,String code) {
        Interviewer interviewer = this.interviewerRepository.findByNameAndCode(name, code);
        if (interviewer == null) {
            throw new RestException(String.valueOf(ResultCode.FORBIDDEN.getCode()),"用户名或密码错误");
        }
        return interviewer;
    }
}
