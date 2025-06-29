package com.guangge.Interview.services;

import com.guangge.Interview.data.Resume;
import com.guangge.Interview.data.ResumeVector;
import com.guangge.Interview.record.InterViewRecord;
import com.guangge.Interview.repository.ResumeVectorRepository;
import com.guangge.Interview.util.Dto2Record;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResumeVectorService {
    private final EmbeddingService embeddingService;
    private final ResumeVectorRepository resumeVectorRepository;

    public ResumeVectorService(EmbeddingService embeddingService, ResumeVectorRepository resumeVectorRepository) {
        this.embeddingService = embeddingService;
        this.resumeVectorRepository = resumeVectorRepository;
    }

    public List<InterViewRecord> findInterViewsByQuestion(String question) {
        float[] embedding = this.embeddingService.generateEmbedding(question);
        System.out.println("Embedding length: "+ embedding.length);
        System.out.println("Embedding values: "+ Arrays.toString(embedding));
        List<ResumeVector> nearest = this.resumeVectorRepository.findNearest(embedding);
        List<Resume> resume = nearest.stream().map(resumeVector -> resumeVector.getResume()).collect(Collectors.toList());
        return resume.stream().map(Dto2Record::toInterViewDetails).toList();
    }

    public ResumeVector findResumeVectorByResumeId(Long resumeId) {
        return this.resumeVectorRepository.findByResumeId(resumeId);
    }

    public void saveResumeVector(Resume resume) {
        ResumeVector resumeVector = this.findResumeVectorByResumeId(resume.getId());
        float[] embedding = embeddingService.generateEmbedding(resume.getRawText());
        if (resumeVector != null) {
            resumeVector.setEmbedding(embedding);
        } else {
            resumeVector = new ResumeVector(embedding, resume);
        }
        this.resumeVectorRepository.save(resumeVector);
    }
}
