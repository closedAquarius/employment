package com.guangge.Interview.repository;

import com.guangge.Interview.data.Interviewer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewerRepository extends JpaRepository<Interviewer, Long> {
    Interviewer findByNameAndCode(String name,String code);
}
