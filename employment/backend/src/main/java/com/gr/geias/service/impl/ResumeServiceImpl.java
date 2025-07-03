package com.gr.geias.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gr.geias.dto.ResumeDTO;
import com.gr.geias.model.JobPosition;
import com.gr.geias.model.Resume;
import com.gr.geias.model.ResumeDetail;
import com.gr.geias.model.ResumeDelivery;
import com.gr.geias.repository.JobPositionRepository;
import com.gr.geias.repository.ResumeDeliveryRepository;
import com.gr.geias.repository.ResumeDetailRepository;
import com.gr.geias.repository.ResumeRepository;
import com.gr.geias.service.ResumeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Key;
import java.util.*;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.spec.SecretKeySpec;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Base64;
import javax.crypto.SecretKey;

/**
 * 简历服务实现类
 */
@Service
public class ResumeServiceImpl implements ResumeService {

    private static final Logger logger = LoggerFactory.getLogger(ResumeServiceImpl.class);

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private ResumeDetailRepository resumeDetailRepository;

    @Autowired
    private JobPositionRepository jobPositionRepository;

    @Autowired
    private ResumeDeliveryRepository resumeDeliveryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ai-interview.api.url:http://localhost:8080}")
    private String aiInterviewApiUrl;

    @Value("${ai-interview.api.token:}")
    private String aiInterviewApiToken;

    @Value("${resume.attachment.path:uploads/resumes}")
    private String resumeAttachmentPath;

    @Override
    public List<ResumeDTO> getUserResumes(Long userId) {
        List<Resume> resumes = resumeRepository.findByUserId(userId);
        List<ResumeDTO> resumeDTOs = new ArrayList<>();

        for (Resume resume : resumes) {
            ResumeDTO dto = convertToDTO(resume, false);
            resumeDTOs.add(dto);
        }

        return resumeDTOs;
    }

    @Override
    public ResumeDTO getResumeDetail(Integer resumeId, Long userId) {
        Resume resume = resumeRepository.findByResumeId(resumeId);
        if (resume == null || !resume.getUserId().equals(userId)) {
            return null;
        }

        return convertToDTO(resume, true);
    }

    @Override
    @Transactional
    public Integer createResume(ResumeDTO resumeDTO, Long userId) {
        // 创建简历基本信息
        Resume resume = new Resume();
        resume.setUserId(userId);
        resume.setResumeName(resumeDTO.getResumeName());
        resume.setStatus(resumeDTO.getStatus() != null ? resumeDTO.getStatus() : 0); // 默认为草稿状态
        resume.setIsDefault(resumeDTO.getIsDefault() != null && resumeDTO.getIsDefault());

        // 如果设为默认简历，先重置其他简历的默认状态
        if (Boolean.TRUE.equals(resume.getIsDefault())) {
            resumeRepository.resetDefaultResumes(userId);
        }

        // 保存简历基本信息
        resumeRepository.save(resume);
        Integer resumeId = resume.getResumeId();

        // 创建简历详情
        ResumeDetail resumeDetail = new ResumeDetail();
        resumeDetail.setResumeId(resumeId);

        try {
            // 转换各部分为JSON字符串
            if (resumeDTO.getBasicInfo() != null) {
                resumeDetail.setBasicInfo(objectMapper.writeValueAsString(resumeDTO.getBasicInfo()));
            }
            if (resumeDTO.getEducation() != null) {
                String educationJson = objectMapper.writeValueAsString(resumeDTO.getEducation());
                resumeDetail.setEducation(educationJson);
            }
            if (resumeDTO.getWorkExperience() != null) {
                resumeDetail.setWorkExperience(objectMapper.writeValueAsString(resumeDTO.getWorkExperience()));
            }
            if (resumeDTO.getProjectExperience() != null) {
                resumeDetail.setProjectExperience(objectMapper.writeValueAsString(resumeDTO.getProjectExperience()));
            }
            if (resumeDTO.getSkills() != null) {
                resumeDetail.setSkills(objectMapper.writeValueAsString(resumeDTO.getSkills()));
            }
            if (resumeDTO.getCertificates() != null) {
                resumeDetail.setCertificates(objectMapper.writeValueAsString(resumeDTO.getCertificates()));
            }
        } catch (JsonProcessingException e) {
            logger.error("Error converting resume data to JSON", e);
            throw new RuntimeException("Error creating resume", e);
        }

        resumeDetail.setSelfEvaluation(resumeDTO.getSelfEvaluation());
        resumeDetail.setAttachmentUrl(resumeDTO.getAttachmentUrl());

        // 保存简历详情
        resumeDetailRepository.save(resumeDetail);

        return resumeId;
    }

    @Override
    @Transactional
    public boolean updateResume(ResumeDTO resumeDTO, Long userId) {
        // 检查简历是否存在且属于当前用户
        Resume resume = resumeRepository.findByResumeId(resumeDTO.getResumeId());
        if (resume == null || !resume.getUserId().equals(userId)) {
            return false;
        }

        // 更新简历基本信息
        resume.setResumeName(resumeDTO.getResumeName());
        resume.setStatus(resumeDTO.getStatus());
        
        // 如果设为默认简历，先重置其他简历的默认状态
        if (Boolean.TRUE.equals(resumeDTO.getIsDefault()) && !Boolean.TRUE.equals(resume.getIsDefault())) {
            resumeRepository.resetDefaultResumes(userId);
            resume.setIsDefault(true);
        } else {
            resume.setIsDefault(resumeDTO.getIsDefault());
        }

        // 保存简历基本信息
        resumeRepository.update(resume);

        // 获取并更新简历详情
        ResumeDetail resumeDetail = resumeDetailRepository.findByResumeId(resumeDTO.getResumeId());
        if (resumeDetail == null) {
            resumeDetail = new ResumeDetail();
            resumeDetail.setResumeId(resumeDTO.getResumeId());
        }

        try {
            // 转换各部分为JSON字符串
            if (resumeDTO.getBasicInfo() != null) {
                resumeDetail.setBasicInfo(objectMapper.writeValueAsString(resumeDTO.getBasicInfo()));
            }
            if (resumeDTO.getEducation() != null) {
                String educationJson = objectMapper.writeValueAsString(resumeDTO.getEducation());
                resumeDetail.setEducation(educationJson);
            }
            if (resumeDTO.getWorkExperience() != null) {
                resumeDetail.setWorkExperience(objectMapper.writeValueAsString(resumeDTO.getWorkExperience()));
            }
            if (resumeDTO.getProjectExperience() != null) {
                resumeDetail.setProjectExperience(objectMapper.writeValueAsString(resumeDTO.getProjectExperience()));
            }
            if (resumeDTO.getSkills() != null) {
                resumeDetail.setSkills(objectMapper.writeValueAsString(resumeDTO.getSkills()));
            }
            if (resumeDTO.getCertificates() != null) {
                resumeDetail.setCertificates(objectMapper.writeValueAsString(resumeDTO.getCertificates()));
            }
        } catch (JsonProcessingException e) {
            logger.error("Error converting resume data to JSON", e);
            return false;
        }

        resumeDetail.setSelfEvaluation(resumeDTO.getSelfEvaluation());
        if (resumeDTO.getAttachmentUrl() != null) {
            resumeDetail.setAttachmentUrl(resumeDTO.getAttachmentUrl());
        }

        // 保存简历详情
        if (resumeDetail.getDetailId() == null) {
            resumeDetailRepository.save(resumeDetail);
        } else {
            resumeDetailRepository.update(resumeDetail);
        }

        return true;
    }

    @Override
    @Transactional
    public boolean deleteResume(Integer resumeId, Long userId) {
        // 检查简历是否存在且属于当前用户
        Resume resume = resumeRepository.findByResumeId(resumeId);
        if (resume == null || !resume.getUserId().equals(userId)) {
            return false;
        }

        // 删除简历详情
        resumeDetailRepository.deleteByResumeId(resumeId);
        
        // 删除简历基本信息
        resumeRepository.delete(resumeId);
        
        return true;
    }

    @Override
    @Transactional
    public boolean setDefaultResume(Integer resumeId, Long userId) {
        // 检查简历是否存在且属于当前用户
        Resume resume = resumeRepository.findByResumeId(resumeId);
        if (resume == null || !resume.getUserId().equals(userId)) {
            return false;
        }

        // 重置该用户所有简历的默认状态
        resumeRepository.resetDefaultResumes(userId);
        
        // 设置当前简历为默认
        resume.setIsDefault(true);
        resumeRepository.update(resume);
        
        return true;
    }

    @Override
    public ResumeDTO getDefaultResume(Long userId) {
        Resume resume = resumeRepository.findDefaultResumeByUserId(userId);
        if (resume == null) {
            return null;
        }
        
        return convertToDTO(resume, true);
    }

    @Override
    @Transactional
    public Integer importFromAI(Long aiResumeId, Long userId) {
        try {
            // 调用AI面试系统API获取简历数据
            String url = aiInterviewApiUrl + "/api/resumes/" + aiResumeId;
            
            // 创建自定义JWT令牌，与AI面试系统兼容
            String token = generateCompatibleJwtToken(userId);
            logger.info("生成用于AI面试系统的JWT令牌: {}", token.substring(0, Math.min(20, token.length())) + "...");
            
            // 创建带有认证令牌的HTTP头
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            
            // 创建请求实体
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // 发送请求，带有认证令牌
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                logger.error("Failed to fetch resume from AI Interview system: {}", response.getStatusCode());
                return null;
            }
            
            Map<String, Object> aiResumeData = response.getBody();
            
            // 创建简历基本信息
            Resume resume = new Resume();
            resume.setUserId(userId);
            resume.setResumeName("从AI面试系统导入的简历");
            resume.setStatus(0); // 草稿状态
            resume.setIsDefault(false);
            
            // 保存简历基本信息
            resumeRepository.save(resume);
            Integer resumeId = resume.getResumeId();
            
            // 解析AI简历数据并创建简历详情
            ResumeDetail resumeDetail = new ResumeDetail();
            resumeDetail.setResumeId(resumeId);
            
            // 基本信息
            Map<String, Object> basicInfo = new HashMap<>();
            basicInfo.put("name", aiResumeData.get("candidate_name"));
            basicInfo.put("email", aiResumeData.get("email"));
            resumeDetail.setBasicInfo(objectMapper.writeValueAsString(basicInfo));
            
            // 解析原始文本，提取教育、工作、项目经历等
            String rawText = (String) aiResumeData.get("raw_text");
            Map<String, Object> parsedData = parseAiResumeText(rawText);
            
            if (parsedData.containsKey("education")) {
                resumeDetail.setEducation(objectMapper.writeValueAsString(parsedData.get("education")));
            }
            
            if (parsedData.containsKey("work")) {
                resumeDetail.setWorkExperience(objectMapper.writeValueAsString(parsedData.get("work")));
            }
            
            if (parsedData.containsKey("project")) {
                resumeDetail.setProjectExperience(objectMapper.writeValueAsString(parsedData.get("project")));
            }
            
            if (parsedData.containsKey("skills")) {
                resumeDetail.setSkills(objectMapper.writeValueAsString(parsedData.get("skills")));
            }
            
            // 保存简历详情
            resumeDetailRepository.save(resumeDetail);
            
            return resumeId;
        } catch (Exception e) {
            logger.error("Error importing resume from AI Interview system", e);
            return null;
        }
    }

    /**
     * 生成与AI面试系统兼容的JWT令牌
     * @param userId 用户ID
     * @return JWT令牌
     */
    private String generateCompatibleJwtToken(Long userId) {
        try {
            // 使用与AI面试系统相同的密钥
            String secret = "eAf6XIz7Q6CmE3N4K5L6M7N8O9P0Q1R2S3T4U5V6W7X8Y9Z0aBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz";
            
            // 确保密钥长度足够，并使用Base64解码
            byte[] keyBytes = Base64.getDecoder().decode(secret.getBytes("UTF-8"));
            Key secretKey = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
            
            // 创建JWT令牌
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", userId);
            claims.put("sub", "admin"); // 使用管理员用户名
            claims.put("userType", 2); // 管理员类型
            
            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(new Date())
                    .setId(UUID.randomUUID().toString())
                    .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1小时过期
                    .signWith(SignatureAlgorithm.HS256, secretKey)
                    .compact();
        } catch (Exception e) {
            logger.error("Error generating JWT token", e);
            return aiInterviewApiToken; // 失败时使用配置的令牌
        }
    }

    @Override
    public String optimizeForJob(Integer resumeId, Integer jobId, Long userId) {
        // 检查简历是否存在且属于当前用户
        Resume resume = resumeRepository.findByResumeId(resumeId);
        if (resume == null || !resume.getUserId().equals(userId)) {
            return null;
        }
        
        // 获取岗位信息
        JobPosition jobPosition = jobPositionRepository.findByPositionId(jobId);
        if (jobPosition == null) {
            return null;
        }
        
        // 获取简历详情
        ResumeDTO resumeDTO = convertToDTO(resume, true);
        if (resumeDTO == null) {
            return null;
        }
        
        try {
            // 调用AI面试系统API优化简历
            String url = aiInterviewApiUrl + "/api/resume/optimize";
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("resume", resumeDTO);
            requestBody.put("jobTitle", jobPosition.getTitle());
            requestBody.put("jobDescription", jobPosition.getDescription());
            requestBody.put("jobRequirements", jobPosition.getRequirements());
            
            // 创建带有认证令牌的HTTP头
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + aiInterviewApiToken);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // 创建请求实体
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            // 发送请求，带有认证令牌
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                logger.error("Failed to optimize resume: {}", response.getStatusCode());
                return null;
            }
            
            Map<String, Object> responseBody = response.getBody();
            return (String) responseBody.get("optimizedContent");
        } catch (Exception e) {
            logger.error("Error optimizing resume for job", e);
            return null;
        }
    }

    @Override
    public String uploadAttachment(Integer resumeId, byte[] fileBytes, String fileName, Long userId) {
        // 检查简历是否存在且属于当前用户
        Resume resume = resumeRepository.findByResumeId(resumeId);
        if (resume == null || !resume.getUserId().equals(userId)) {
            return null;
        }
        
        try {
            // 确保目录存在
            Path uploadPath = Paths.get(resumeAttachmentPath);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // 生成文件名
            String uniqueFileName = resumeId + "_" + System.currentTimeMillis() + "_" + fileName;
            Path filePath = uploadPath.resolve(uniqueFileName);
            
            // 保存文件
            Files.copy(java.io.ByteArrayInputStream.class.getConstructor(byte[].class).newInstance(fileBytes), 
                    filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // 生成URL
            String fileUrl = "/resumes/" + uniqueFileName;
            
            // 更新简历详情中的附件URL
            ResumeDetail resumeDetail = resumeDetailRepository.findByResumeId(resumeId);
            if (resumeDetail == null) {
                resumeDetail = new ResumeDetail();
                resumeDetail.setResumeId(resumeId);
                resumeDetail.setAttachmentUrl(fileUrl);
                resumeDetailRepository.save(resumeDetail);
            } else {
                resumeDetail.setAttachmentUrl(fileUrl);
                resumeDetailRepository.update(resumeDetail);
            }
            
            return fileUrl;
        } catch (Exception e) {
            logger.error("Error uploading resume attachment", e);
            return null;
        }
    }

    @Override
    @Transactional
    public boolean deliverResume(Integer resumeId, Integer jobId, Long userId) {
        // 检查简历是否存在且属于当前用户
        Resume resume = resumeRepository.findByResumeId(resumeId);
        if (resume == null || !resume.getUserId().equals(userId)) {
            return false;
        }
        
        // 检查岗位是否存在且处于发布状态
        JobPosition jobPosition = jobPositionRepository.findByPositionId(jobId);
        if (jobPosition == null || jobPosition.getStatus() != 1) {
            return false;
        }
        
        // 检查是否已经投递过该岗位
        int existingCount = resumeDeliveryRepository.existsByUserIdAndPositionId(userId, jobId);
        if (existingCount > 0) {
            return false; // 已经投递过
        }
        
        // 创建投递记录
        ResumeDelivery delivery = new ResumeDelivery();
        delivery.setResumeId(resumeId);
        delivery.setJobId(jobId);
        delivery.setCompanyId(jobPosition.getCompanyId());
        delivery.setStatus(0); // 待查看状态
        
        resumeDeliveryRepository.save(delivery);
        
        return true;
    }

    @Override
    public List<ResumeDTO> getUserDeliveryRecords(Long userId) {
        // 获取用户的所有投递记录
        List<ResumeDelivery> deliveries = resumeDeliveryRepository.findByUserId(userId);
        List<ResumeDTO> deliveryRecords = new ArrayList<>();
        
        for (ResumeDelivery delivery : deliveries) {
            // 获取简历信息
            Resume resume = resumeRepository.findByResumeId(delivery.getResumeId());
            if (resume == null) {
                continue;
            }
            
            // 获取岗位信息
            JobPosition jobPosition = jobPositionRepository.findByPositionId(delivery.getJobId());
            if (jobPosition == null) {
                continue;
            }
            
            // 构建投递记录DTO
            ResumeDTO dto = convertToDTO(resume, false);
            dto.setBasicInfo(new HashMap<>());
            dto.getBasicInfo().put("jobId", delivery.getJobId());
            dto.getBasicInfo().put("jobTitle", jobPosition.getTitle());
            dto.getBasicInfo().put("companyId", delivery.getCompanyId());
            dto.getBasicInfo().put("deliveryId", delivery.getDeliveryId());
            dto.getBasicInfo().put("deliveryStatus", delivery.getStatus());
            dto.getBasicInfo().put("deliveryTime", delivery.getDeliveryTime());
            dto.getBasicInfo().put("feedback", delivery.getFeedback());
            
            deliveryRecords.add(dto);
        }
        
        return deliveryRecords;
    }
    
    /**
     * 将Resume实体转换为ResumeDTO
     * @param resume 简历实体
     * @param includeDetail 是否包含详细信息
     * @return 简历DTO
     */
    private ResumeDTO convertToDTO(Resume resume, boolean includeDetail) {
        ResumeDTO dto = new ResumeDTO();
        dto.setResumeId(resume.getResumeId());
        dto.setUserId(resume.getUserId());
        dto.setResumeName(resume.getResumeName());
        dto.setIsDefault(resume.getIsDefault());
        dto.setStatus(resume.getStatus());
        dto.setCreateTime(resume.getCreateTime());
        dto.setUpdateTime(resume.getUpdateTime());
        
        if (includeDetail) {
            ResumeDetail detail = resumeDetailRepository.findByResumeId(resume.getResumeId());
            if (detail != null) {
                try {
                    // 解析JSON字符串为对象
                    if (detail.getBasicInfo() != null) {
                        dto.setBasicInfo(objectMapper.readValue(detail.getBasicInfo(), Map.class));
                    }
                    // 优先使用educationInfo字段，如果为空则尝试使用education字段
                    String educationJson = detail.getEducationInfo();
                    if (educationJson == null) {
                        educationJson = detail.getEducation();
                    }
                    if (educationJson != null) {
                        dto.setEducation(objectMapper.readValue(educationJson, List.class));
                    }
                    if (detail.getWorkExperience() != null) {
                        dto.setWorkExperience(objectMapper.readValue(detail.getWorkExperience(), List.class));
                    }
                    if (detail.getProjectExperience() != null) {
                        dto.setProjectExperience(objectMapper.readValue(detail.getProjectExperience(), List.class));
                    }
                    if (detail.getSkills() != null) {
                        dto.setSkills(objectMapper.readValue(detail.getSkills(), List.class));
                    }
                    if (detail.getCertificates() != null) {
                        dto.setCertificates(objectMapper.readValue(detail.getCertificates(), List.class));
                    }
                } catch (JsonProcessingException e) {
                    logger.error("Error parsing resume detail JSON", e);
                }
                
                dto.setSelfEvaluation(detail.getSelfEvaluation());
                dto.setAttachmentUrl(detail.getAttachmentUrl());
            }
        }
        
        return dto;
    }
    
    /**
     * 解析AI简历系统的原始文本
     * @param rawText 原始文本
     * @return 解析后的数据
     */
    private Map<String, Object> parseAiResumeText(String rawText) {
        Map<String, Object> result = new HashMap<>();
        
        if (rawText == null || rawText.isEmpty()) {
            return result;
        }
        
        try {
            // 教育经历
            List<Map<String, Object>> education = new ArrayList<>();
            if (rawText.contains("【教育背景】")) {
                String educationText = extractSection(rawText, "【教育背景】", "【工作经历】");
                String[] lines = educationText.split("\n");
                for (String line : lines) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.equals("【教育背景】")) {
                        Map<String, Object> edu = new HashMap<>();
                        // 示例格式: 浙江大学 2004.9~2008.7 计算机科学
                        String[] parts = line.split("\\s+");
                        if (parts.length >= 3) {
                            edu.put("university", parts[0]);
                            
                            String[] period = parts[1].split("~");
                            if (period.length == 2) {
                                edu.put("begin", period[0]);
                                edu.put("end", period[1]);
                            }
                            
                            edu.put("major", parts[2]);
                            education.add(edu);
                        }
                    }
                }
            }
            result.put("education", education);
            
            // 工作经历
            List<Map<String, Object>> work = new ArrayList<>();
            if (rawText.contains("【工作经历】")) {
                String workText = extractSection(rawText, "【工作经历】", "【技能】");
                String[] lines = workText.split("\n");
                for (String line : lines) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.equals("【工作经历】")) {
                        Map<String, Object> job = new HashMap<>();
                        // 示例格式: 2008.9~2018.9 大连埃森哲技术有限公司
                        String[] parts = line.split("\\s+");
                        if (parts.length >= 2) {
                            String[] period = parts[0].split("~");
                            if (period.length == 2) {
                                job.put("begin", period[0]);
                                job.put("end", period[1]);
                            }
                            
                            job.put("company", parts[1]);
                            job.put("workContent", "");
                            work.add(job);
                        }
                    }
                }
            }
            result.put("work", work);
            
            // 项目经历
            List<Map<String, Object>> projects = new ArrayList<>();
            if (rawText.contains("【项目经历】")) {
                String projectText = extractSection(rawText, "【项目经历】", null);
                String[] lines = projectText.split("\n");
                
                Map<String, Object> currentProject = null;
                for (String line : lines) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.equals("【项目经历】")) {
                        if (line.matches("\\d{4}\\.\\d{1,2}~.*")) {
                            // 新项目开始
                            if (currentProject != null) {
                                projects.add(currentProject);
                            }
                            
                            currentProject = new HashMap<>();
                            // 示例格式: 2008.9~2010.9 某著名航空公司管理系统
                            String[] parts = line.split("\\s+", 2);
                            if (parts.length >= 2) {
                                String[] period = parts[0].split("~");
                                if (period.length == 2) {
                                    currentProject.put("begin", period[0]);
                                    currentProject.put("end", period[1]);
                                }
                                
                                currentProject.put("name", parts[1]);
                                currentProject.put("content", "");
                                currentProject.put("skills", "");
                            }
                        } else if (line.startsWith("技术：") && currentProject != null) {
                            // 项目技术栈
                            currentProject.put("skills", line.substring(3));
                        } else if (currentProject != null) {
                            // 项目内容
                            String content = (String) currentProject.get("content");
                            currentProject.put("content", content + line + "\n");
                        }
                    }
                }
                
                if (currentProject != null) {
                    projects.add(currentProject);
                }
            }
            result.put("project", projects);
            
            // 技能
            List<String> skills = new ArrayList<>();
            if (rawText.contains("【技能】")) {
                String skillsText = extractSection(rawText, "【技能】", "【项目经历】");
                String[] lines = skillsText.split("\n");
                for (String line : lines) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.equals("【技能】")) {
                        // 示例格式: JAVA，SpringBoot，Python，微服务，Mq
                        String[] skillList = line.split("，|,");
                        for (String skill : skillList) {
                            skills.add(skill.trim());
                        }
                    }
                }
            }
            result.put("skills", skills);
            
        } catch (Exception e) {
            logger.error("Error parsing AI resume text", e);
        }
        
        return result;
    }
    
    /**
     * 从文本中提取指定部分
     * @param text 完整文本
     * @param startMarker 开始标记
     * @param endMarker 结束标记
     * @return 提取的文本
     */
    private String extractSection(String text, String startMarker, String endMarker) {
        int startIndex = text.indexOf(startMarker);
        if (startIndex == -1) {
            return "";
        }
        
        int endIndex;
        if (endMarker == null) {
            endIndex = text.length();
        } else {
            endIndex = text.indexOf(endMarker, startIndex);
            if (endIndex == -1) {
                endIndex = text.length();
            }
        }
        
        return text.substring(startIndex, endIndex);
    }
} 