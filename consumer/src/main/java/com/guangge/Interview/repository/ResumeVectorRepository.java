package com.guangge.Interview.repository;

import com.guangge.Interview.data.ResumeVector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResumeVectorRepository  extends JpaRepository<ResumeVector, Long> {
    @Query(value = "SELECT * FROM resume_vectors where embedding <=> CAST(:embedding AS vector) < 1 ORDER BY embedding <=> CAST(:embedding AS vector) LIMIT 5",
            nativeQuery = true)
    List<ResumeVector> findNearest(@Param("embedding") float[] embedding);

    @Query(value = "SELECT * FROM resume_vectors where resume_id = :resumeId",nativeQuery = true)
    ResumeVector findByResumeId(@Param("resumeId") Long resumeId);
}
