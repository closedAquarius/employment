package com.guangge.Interview.services;

import com.guangge.Interview.audio.services.AudioService;
import com.guangge.Interview.controller.LoginController;
import com.guangge.Interview.data.InterViewStatus;
import com.guangge.Interview.data.IsDoneStatus;
import com.guangge.Interview.data.Resume;
import com.guangge.Interview.record.InterViewRecord;
import com.guangge.Interview.repository.ResumeRepository;
import com.guangge.Interview.util.Dto2Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResumeService {

    private static final Logger logger = LoggerFactory.getLogger(ResumeService.class);

    private final AudioService audioService;
    private final ResumeRepository resumeRepository;

    public ResumeService(AudioService audioService, ResumeRepository resumeRepository) {
        this.audioService = audioService;
        this.resumeRepository = resumeRepository;
    }

    public void saveResume(Long id,MultipartFile file) throws IOException {
        // 从IO流中读取文件
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(new InputStreamResource(file.getInputStream()));

        // 将文本内容划分成更小的块
        List<Document> documents = tikaDocumentReader.get();

        if (!documents.isEmpty()) {
            Resume resume = resumeRepository.getReferenceById(id);
            resume.setRawText(documents.get(0).getText());
            resumeRepository.save(resume);
        }
    }

    /**
     * 返回所有面试者面试信息
     * @return 面试者信息
     */
    public List<InterViewRecord> getInterViews() {
        List<Resume> resume = this.resumeRepository.findAll(Sort.by("id").descending());
        return resume.stream().map(Dto2Record::toInterViewDetails).toList();
    }

    public Resume findInterView(String name) {
        Resume resume = this.resumeRepository.findByName(name);
        if (resume == null) {
            throw new IllegalArgumentException("Interview not found");
        }
        return resume;
    }

    public Resume findInterView(Long id) {
        Optional<Resume> byId = this.resumeRepository.findById(id);
        if (!byId.isPresent()) {
            throw new IllegalArgumentException("Interview not found");
        }
        return byId.get();
    }

    public void changeTestReuslt(String name, int score, String evaluate) {
        var resume = findInterView(name);
        resume.setScore(score);
        switch (score) {
            case 5:
                resume.setInterViewStatus(InterViewStatus.PERFECT);
                break;
            case 4:
                resume.setInterViewStatus(InterViewStatus.EXCELLENT);
                break;
            case 3:
                resume.setInterViewStatus(InterViewStatus.QUALIFIED);
                break;
            default:
                resume.setInterViewStatus(InterViewStatus.ELIMINATE);
                break;
        }
        resume.setEvaluate(evaluate);
        String mp3Path = this.audioService.getSpeech(resume.getId() + name,evaluate);
        resume.setMp3Path(mp3Path);
        resume.setIsDoneStatus(IsDoneStatus.ONE_DONE);

        this.resumeRepository.save(resume);
    }

    public void changeInterview(String name, String evaluate) {
        var resume = findInterView(name);
        resume.setInterviewEvaluate(evaluate);
        resume.setIsDoneStatus(IsDoneStatus.TWO_DONE);
        this.resumeRepository.save(resume);
    }

    public InterViewRecord getInterViewDetails(String name) {
        var resume = findInterView(name);
        return Dto2Record.toInterViewDetails(resume);
    }
}
