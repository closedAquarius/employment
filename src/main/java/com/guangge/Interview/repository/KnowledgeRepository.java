package com.guangge.Interview.repository;

import com.guangge.Interview.data.Knowledge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KnowledgeRepository extends JpaRepository<Knowledge, Long>  {

    @Query(value = "SELECT * FROM Knowledge WHERE language = :language AND type = :type ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Knowledge> findRandomRecords(@Param("language") String language,@Param("type") String type,@Param("limit") int limit);
}
