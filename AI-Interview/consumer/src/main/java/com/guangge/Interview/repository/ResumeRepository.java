package com.guangge.Interview.repository;

import com.guangge.Interview.data.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  ResumeRepository extends JpaRepository<Resume, Long> {
    Resume findByName(String name);

}
