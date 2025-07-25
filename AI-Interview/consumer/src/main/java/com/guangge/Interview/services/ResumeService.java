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
        // 修改为使用findAll并按ID排序，然后返回最新的一条记录
        List<Resume> resumes = this.resumeRepository.findAll()
                .stream()
                .filter(r -> r.getName().equals(name))
                .sorted((r1, r2) -> r2.getId().compareTo(r1.getId()))  // 按ID降序排序
                .collect(Collectors.toList());
                
        if (resumes.isEmpty()) {
            throw new IllegalArgumentException("Interview not found");
        }
        
        // 返回最新的一条记录
        return resumes.get(0);
    }

    public Resume findInterView(Long id) {
        Optional<Resume> byId = this.resumeRepository.findById(id);
        if (!byId.isPresent()) {
            throw new IllegalArgumentException("Interview not found");
        }
        return byId.get();
    }

    public void changeTestReuslt(String name, int score, String evaluate) {
        Resume resume = findInterView(name);
        resume.setScore(score);
        resume.setEvaluate(evaluate);
        
        // 根据分数设置面试状态
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
        
        try {
            // 尝试生成音频文件，但不影响主要功能
            String mp3Path = audioService.getSpeech(score + name, evaluate);
        resume.setMp3Path(mp3Path);
        } catch (Exception e) {
            // 记录错误但继续执行
            System.err.println("音频生成失败，但继续保存笔试结果: " + e.getMessage());
        }
        
        // 设置完成状态
        resume.setIsDoneStatus(IsDoneStatus.ONE_DONE);
        resumeRepository.save(resume);
    }

    public void changeInterview(String name, String evaluate) {
        var resume = findInterView(name);
        resume.setInterviewEvaluate(evaluate);
        resume.setIsDoneStatus(IsDoneStatus.TWO_DONE);
        this.resumeRepository.save(resume);
    }

    /**
     * 更新用户邮箱
     * @param name 用户名
     * @param email 邮箱
     */
    public void updateEmail(String name, String email) {
        var resume = findInterView(name);
        resume.setEmail(email);
        this.resumeRepository.save(resume);
        logger.info("用户 {} 邮箱更新为 {}", name, email);
    }

    public InterViewRecord getInterViewDetails(String name) {
        var resume = findInterView(name);
        return Dto2Record.toInterViewDetails(resume);
    }
}
