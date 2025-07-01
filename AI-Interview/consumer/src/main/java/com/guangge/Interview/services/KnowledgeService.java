package com.guangge.Interview.services;

import com.guangge.Interview.data.Knowledge;
import com.guangge.Interview.repository.KnowledgeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KnowledgeService {
    private final KnowledgeRepository knowledgeRepository;

    public KnowledgeService(KnowledgeRepository knowledgeRepository) {
        this.knowledgeRepository = knowledgeRepository;
    }

    public List<Knowledge> getQuestions(String language, String type, int limit) {
        return this.knowledgeRepository.findRandomRecords(language, type, limit);
    }
}
