package com.gr.geias.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gr.geias.dto.ResumeDTO;
import com.gr.geias.model.JobPosition;
import com.gr.geias.model.Resume;
import com.gr.geias.model.ResumeDetail;
import com.gr.geias.repository.JobPositionRepository;
import com.gr.geias.repository.ResumeRepository;
import com.gr.geias.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 简历服务实现类
 */
@Service
public class ResumeServiceImpl implements ResumeService {
    
    @Autowired
    private ResumeRepository resumeRepository;
    
    @Autowired
    private JobPositionRepository jobPositionRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Value("${file.upload.path}")
    private String fileUploadPath;
    
    @Value("${ai-interview.api.url:http://localhost:8081/api}")
    private String aiInterviewApiUrl;

    /**
     * 创建简历
     */
    @Override
    @Transactional
    public ResumeDTO createResume(ResumeDTO resumeDTO) {
        try {
            // 创建简历基本信息
            Resume resume = new Resume();
            resume.setUserId(resumeDTO.getUserId());
            resume.setResumeName(resumeDTO.getResumeName());
            resume.setIsDefault(resumeDTO.getIsDefault());
            resume.setStatus(resumeDTO.getStatus());
            
            // 如果设置为默认简历，先清除其他默认状态
            if (Boolean.TRUE.equals(resume.getIsDefault())) {
                resumeRepository.clearDefaultStatus(resume.getUserId());
            }
            
            // 保存简历基本信息
            resumeRepository.saveResume(resume);
            
            // 创建简历详细内容
            ResumeDetail resumeDetail = new ResumeDetail();
            resumeDetail.setResumeId(resume.getResumeId());
            resumeDetail.setBasicInfo(objectMapper.writeValueAsString(resumeDTO.getBasicInfo()));
            resumeDetail.setEducation(objectMapper.writeValueAsString(resumeDTO.getEducation()));
            resumeDetail.setWorkExperience(objectMapper.writeValueAsString(resumeDTO.getWorkExperience()));
            resumeDetail.setProjectExperience(objectMapper.writeValueAsString(resumeDTO.getProjectExperience()));
            resumeDetail.setSkills(objectMapper.writeValueAsString(resumeDTO.getSkills()));
            resumeDetail.setCertificates(objectMapper.writeValueAsString(resumeDTO.getCertificates()));
            resumeDetail.setSelfEvaluation(resumeDTO.getSelfEvaluation());
            resumeDetail.setAttachmentUrl(resumeDTO.getAttachmentUrl());
            
            // 保存简历详细内容
            resumeRepository.saveResumeDetail(resumeDetail);
            
            // 设置返回DTO的ID
            resumeDTO.setResumeId(resume.getResumeId());
            
            return resumeDTO;
        } catch (Exception e) {
            throw new RuntimeException("创建简历失败", e);
        }
    }

    /**
     * 更新简历
     */
    @Override
    @Transactional
    public ResumeDTO updateResume(ResumeDTO resumeDTO) {
        try {
            // 查询原简历
            Resume resume = resumeRepository.findResumeById(resumeDTO.getResumeId());
            if (resume == null) {
                throw new RuntimeException("简历不存在");
            }
            
            // 更新简历基本信息
            resume.setResumeName(resumeDTO.getResumeName());
            resume.setStatus(resumeDTO.getStatus());
            
            // 如果设置为默认简历，先清除其他默认状态
            if (Boolean.TRUE.equals(resumeDTO.getIsDefault()) && !Boolean.TRUE.equals(resume.getIsDefault())) {
                resumeRepository.clearDefaultStatus(resume.getUserId());
                resume.setIsDefault(true);
            }
            
            // 更新简历基本信息
            resumeRepository.updateResume(resume);
            
            // 查询原简历详情
            ResumeDetail resumeDetail = resumeRepository.findResumeDetailByResumeId(resume.getResumeId());
            if (resumeDetail == null) {
                resumeDetail = new ResumeDetail();
                resumeDetail.setResumeId(resume.getResumeId());
            }
            
            // 更新简历详细内容
            resumeDetail.setBasicInfo(objectMapper.writeValueAsString(resumeDTO.getBasicInfo()));
            resumeDetail.setEducation(objectMapper.writeValueAsString(resumeDTO.getEducation()));
            resumeDetail.setWorkExperience(objectMapper.writeValueAsString(resumeDTO.getWorkExperience()));
            resumeDetail.setProjectExperience(objectMapper.writeValueAsString(resumeDTO.getProjectExperience()));
            resumeDetail.setSkills(objectMapper.writeValueAsString(resumeDTO.getSkills()));
            resumeDetail.setCertificates(objectMapper.writeValueAsString(resumeDTO.getCertificates()));
            resumeDetail.setSelfEvaluation(resumeDTO.getSelfEvaluation());
            
            // 如果上传了新附件，更新附件URL
            if (StringUtils.hasText(resumeDTO.getAttachmentUrl())) {
                resumeDetail.setAttachmentUrl(resumeDTO.getAttachmentUrl());
            }
            
            // 更新简历详细内容
            if (resumeDetail.getDetailId() == null) {
                resumeRepository.saveResumeDetail(resumeDetail);
            } else {
                resumeRepository.updateResumeDetail(resumeDetail);
            }
            
            return resumeDTO;
        } catch (Exception e) {
            throw new RuntimeException("更新简历失败", e);
        }
    }

    /**
     * 删除简历
     */
    @Override
    @Transactional
    public boolean deleteResume(Integer resumeId, Long userId) {
        // 查询简历
        Resume resume = resumeRepository.findResumeById(resumeId);
        if (resume == null || !resume.getUserId().equals(userId)) {
            return false;
        }
        
        // 删除简历
        resumeRepository.deleteResume(resumeId);
        
        return true;
    }

    /**
     * 获取简历详情
     */
    @Override
    public ResumeDTO getResumeById(Integer resumeId) {
        try {
            // 查询简历基本信息
            Resume resume = resumeRepository.findResumeById(resumeId);
            if (resume == null) {
                return null;
            }
            
            // 查询简历详细内容
            ResumeDetail resumeDetail = resumeRepository.findResumeDetailByResumeId(resumeId);
            if (resumeDetail == null) {
                resumeDetail = new ResumeDetail();
                resumeDetail.setResumeId(resumeId);
            }
            
            // 组装DTO
            ResumeDTO resumeDTO = new ResumeDTO();
            resumeDTO.setResumeId(resume.getResumeId());
            resumeDTO.setUserId(resume.getUserId());
            resumeDTO.setResumeName(resume.getResumeName());
            resumeDTO.setIsDefault(resume.getIsDefault());
            resumeDTO.setStatus(resume.getStatus());
            resumeDTO.setCreateTime(resume.getCreateTime());
            resumeDTO.setUpdateTime(resume.getUpdateTime());
            
            // 解析JSON字段
            if (StringUtils.hasText(resumeDetail.getBasicInfo())) {
                resumeDTO.setBasicInfo(objectMapper.readValue(resumeDetail.getBasicInfo(), Map.class));
            }
            
            if (StringUtils.hasText(resumeDetail.getEducation())) {
                resumeDTO.setEducation(objectMapper.readValue(resumeDetail.getEducation(), List.class));
            }
            
            if (StringUtils.hasText(resumeDetail.getWorkExperience())) {
                resumeDTO.setWorkExperience(objectMapper.readValue(resumeDetail.getWorkExperience(), List.class));
            }
            
            if (StringUtils.hasText(resumeDetail.getProjectExperience())) {
                resumeDTO.setProjectExperience(objectMapper.readValue(resumeDetail.getProjectExperience(), List.class));
            }
            
            if (StringUtils.hasText(resumeDetail.getSkills())) {
                resumeDTO.setSkills(objectMapper.readValue(resumeDetail.getSkills(), List.class));
            }
            
            if (StringUtils.hasText(resumeDetail.getCertificates())) {
                resumeDTO.setCertificates(objectMapper.readValue(resumeDetail.getCertificates(), List.class));
            }
            
            resumeDTO.setSelfEvaluation(resumeDetail.getSelfEvaluation());
            resumeDTO.setAttachmentUrl(resumeDetail.getAttachmentUrl());
            
            return resumeDTO;
        } catch (Exception e) {
            throw new RuntimeException("获取简历详情失败", e);
        }
    }

    /**
     * 获取用户的所有简历
     */
    @Override
    public List<Resume> getResumesByUserId(Long userId) {
        return resumeRepository.findResumesByUserId(userId);
    }

    /**
     * 获取用户的默认简历
     */
    @Override
    public Resume getDefaultResumeByUserId(Long userId) {
        return resumeRepository.findDefaultResumeByUserId(userId);
    }

    /**
     * 设置默认简历
     */
    @Override
    @Transactional
    public boolean setDefaultResume(Integer resumeId, Long userId) {
        // 查询简历
        Resume resume = resumeRepository.findResumeById(resumeId);
        if (resume == null || !resume.getUserId().equals(userId)) {
            return false;
        }
        
        // 清除其他默认状态
        resumeRepository.clearDefaultStatus(userId);
        
        // 设置为默认简历
        resumeRepository.setDefaultResume(resumeId);
        
        return true;
    }

    /**
     * 从AI-Interview系统导入简历
     */
    @Override
    @Transactional
    public ResumeDTO importResumeFromAI(Long userId, String aiResumeId) {
        try {
            // 调用AI-Interview API获取简历数据
            String url = aiInterviewApiUrl + "/resume/" + aiResumeId;
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-User-Id", String.valueOf(userId));
            
            ResponseEntity<Map> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                new HttpEntity<>(headers), 
                Map.class
            );
            
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("获取AI简历数据失败");
            }
            
            // 转换为ResumeDTO
            Map<String, Object> aiResumeData = response.getBody();
            
            ResumeDTO resumeDTO = new ResumeDTO();
            resumeDTO.setUserId(userId);
            resumeDTO.setResumeName((String) aiResumeData.getOrDefault("resumeName", "从AI系统导入的简历"));
            resumeDTO.setIsDefault(false);
            resumeDTO.setStatus(1); // 发布状态
            
            // 设置基本信息
            Map<String, Object> basicInfo = new HashMap<>();
            basicInfo.put("name", aiResumeData.getOrDefault("name", ""));
            basicInfo.put("gender", aiResumeData.getOrDefault("gender", ""));
            basicInfo.put("birthday", aiResumeData.getOrDefault("birthday", ""));
            basicInfo.put("phone", aiResumeData.getOrDefault("phone", ""));
            basicInfo.put("email", aiResumeData.getOrDefault("email", ""));
            resumeDTO.setBasicInfo(basicInfo);
            
            // 设置教育经历
            if (aiResumeData.containsKey("education")) {
                resumeDTO.setEducation((List<Map<String, Object>>) aiResumeData.get("education"));
            }
            
            // 设置工作经历
            if (aiResumeData.containsKey("workExperience")) {
                resumeDTO.setWorkExperience((List<Map<String, Object>>) aiResumeData.get("workExperience"));
            }
            
            // 设置项目经历
            if (aiResumeData.containsKey("projectExperience")) {
                resumeDTO.setProjectExperience((List<Map<String, Object>>) aiResumeData.get("projectExperience"));
            }
            
            // 设置技能特长
            if (aiResumeData.containsKey("skills")) {
                resumeDTO.setSkills((List<Map<String, Object>>) aiResumeData.get("skills"));
            }
            
            // 设置自我评价
            if (aiResumeData.containsKey("selfEvaluation")) {
                resumeDTO.setSelfEvaluation((String) aiResumeData.get("selfEvaluation"));
            }
            
            // 创建简历
            return createResume(resumeDTO);
        } catch (Exception e) {
            throw new RuntimeException("从AI-Interview系统导入简历失败", e);
        }
    }

    /**
     * 生成标准化简历
     */
    @Override
    public ResumeDTO generateStandardResume(ResumeDTO resumeDTO) {
        try {
            // 调用AI-Interview API生成标准化简历
            String url = aiInterviewApiUrl + "/resume/generate";
            
            ResponseEntity<ResumeDTO> response = restTemplate.postForEntity(
                url, 
                resumeDTO, 
                ResumeDTO.class
            );
            
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("生成标准化简历失败");
            }
            
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("生成标准化简历失败", e);
        }
    }

    /**
     * 根据岗位要求优化简历
     */
    @Override
    public ResumeDTO optimizeResumeForJob(Integer resumeId, Integer jobId) {
        try {
            // 获取简历
            ResumeDTO resumeDTO = getResumeById(resumeId);
            if (resumeDTO == null) {
                throw new RuntimeException("简历不存在");
            }
            
            // 获取岗位信息
            JobPosition jobPosition = jobPositionRepository.findJobPositionById(jobId);
            if (jobPosition == null) {
                throw new RuntimeException("岗位不存在");
            }
            
            // 调用AI-Interview API优化简历
            String url = aiInterviewApiUrl + "/resume/optimize";
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("resume", resumeDTO);
            requestBody.put("jobTitle", jobPosition.getTitle());
            requestBody.put("jobDescription", jobPosition.getDescription());
            requestBody.put("jobRequirements", jobPosition.getRequirements());
            
            ResponseEntity<ResumeDTO> response = restTemplate.postForEntity(
                url, 
                requestBody, 
                ResumeDTO.class
            );
            
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("优化简历失败");
            }
            
            // 返回优化后的简历（不保存，由用户决定是否保存）
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("根据岗位要求优化简历失败", e);
        }
    }

    /**
     * 上传简历附件
     */
    @Override
    public String uploadResumeAttachment(Integer resumeId, byte[] fileData, String fileName) {
        try {
            // 查询简历
            Resume resume = resumeRepository.findResumeById(resumeId);
            if (resume == null) {
                throw new RuntimeException("简历不存在");
            }
            
            // 创建上传目录
            Path uploadDir = Paths.get(fileUploadPath, "resume", resumeId.toString());
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            
            // 生成文件名
            String fileExtension = fileName.substring(fileName.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + fileExtension;
            
            // 保存文件
            Path filePath = uploadDir.resolve(newFileName);
            Files.write(filePath, fileData);
            
            // 更新简历附件URL
            String attachmentUrl = "/uploads/resume/" + resumeId + "/" + newFileName;
            
            // 查询简历详情
            ResumeDetail resumeDetail = resumeRepository.findResumeDetailByResumeId(resumeId);
            if (resumeDetail == null) {
                resumeDetail = new ResumeDetail();
                resumeDetail.setResumeId(resumeId);
                resumeDetail.setAttachmentUrl(attachmentUrl);
                resumeRepository.saveResumeDetail(resumeDetail);
            } else {
                // 删除旧文件
                if (StringUtils.hasText(resumeDetail.getAttachmentUrl())) {
                    String oldFilePath = resumeDetail.getAttachmentUrl();
                    if (oldFilePath.startsWith("/uploads/")) {
                        oldFilePath = oldFilePath.substring("/uploads/".length());
                        Path oldFile = Paths.get(fileUploadPath, oldFilePath);
                        if (Files.exists(oldFile)) {
                            Files.delete(oldFile);
                        }
                    }
                }
                
                resumeDetail.setAttachmentUrl(attachmentUrl);
                resumeRepository.updateResumeDetail(resumeDetail);
            }
            
            return attachmentUrl;
        } catch (Exception e) {
            throw new RuntimeException("上传简历附件失败", e);
        }
    }

    /**
     * 下载简历附件
     */
    @Override
    public byte[] downloadResumeAttachment(Integer resumeId) {
        try {
            // 查询简历详情
            ResumeDetail resumeDetail = resumeRepository.findResumeDetailByResumeId(resumeId);
            if (resumeDetail == null || !StringUtils.hasText(resumeDetail.getAttachmentUrl())) {
                throw new RuntimeException("简历附件不存在");
            }
            
            // 获取文件路径
            String attachmentUrl = resumeDetail.getAttachmentUrl();
            if (attachmentUrl.startsWith("/uploads/")) {
                attachmentUrl = attachmentUrl.substring("/uploads/".length());
            }
            
            Path filePath = Paths.get(fileUploadPath, attachmentUrl);
            if (!Files.exists(filePath)) {
                throw new RuntimeException("简历附件文件不存在");
            }
            
            // 读取文件数据
            return Files.readAllBytes(filePath);
        } catch (Exception e) {
            throw new RuntimeException("下载简历附件失败", e);
        }
    }
}