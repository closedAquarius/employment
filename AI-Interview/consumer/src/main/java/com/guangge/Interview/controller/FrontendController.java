package com.guangge.Interview.controller;

import com.guangge.Interview.data.Resume;
import com.guangge.Interview.mail.MailService;
import com.guangge.Interview.record.CandidateRecord;
import com.guangge.Interview.record.InterViewRecord;
import com.guangge.Interview.services.CandidatesService;
import com.guangge.Interview.services.ResumeService;
import com.guangge.Interview.services.ResumeVectorService;
import com.guangge.Interview.util.CommonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/frontend")
public class FrontendController {

    private static final Logger logger = LoggerFactory.getLogger(FrontendController.class);

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private MailService mailService;

    private final ResumeVectorService resumeVectorService;
    private final CandidatesService candidatesService;

    public FrontendController(ResumeVectorService resumeVectorService,
                             CandidatesService candidatesService) {
        this.resumeVectorService = resumeVectorService;
        this.candidatesService = candidatesService;
    }

    @GetMapping("/getInterView")
    public Resume getInterView(@RequestParam("name") String name) {
        return resumeService.findInterView(name);
    }

    @GetMapping("/getInterViews")
    public CommonResult<List<InterViewRecord>> getInterViews() {
        return CommonResult.success(resumeService.getInterViews());
    }

    /**
     * 获取所有面试记录
     * 这个接口与前端的/frontend/interView对应
     */
    @GetMapping("/interView")
    public List<InterViewRecord> interView() {
        logger.info("获取所有面试记录");
        return resumeService.getInterViews();
    }

    @GetMapping("/getInterViewByEmail")
    public Resume getInterViewByEmail(@RequestParam("email") String email) {
        // 这个方法在ResumeService中不存在，需要另外实现或修改
        return resumeService.findInterView(email);
    }

    /**
     * 发送邮件
     * @param name 姓名
     * @param email 邮箱（可选）
     */
    @PostMapping(value = "/sendMail")
    public CommonResult<String> sendMail(@RequestParam("name") String name, @RequestParam(value = "email", required = false) String email) {
        try {
            logger.info("开始发送邮件给: {}, 邮箱: {}", name, email);
            
            // 如果提供了邮箱，先更新候选人邮箱
            if (email != null && !email.isEmpty()) {
                resumeService.updateEmail(name, email);
                logger.info("已更新候选人 {} 的邮箱为: {}", name, email);
            }
            
            // 发送邮件
            mailService.sendMailForAttachment(name);
            logger.info("邮件发送成功");
            
            return CommonResult.success("发送邮件成功");
        } catch (Exception e) {
            logger.error("发送邮件失败: {}", e.getMessage(), e);
            return CommonResult.failed("发送邮件失败: " + e.getMessage());
        }
    }

    @GetMapping("/findInterView")
    public List<InterViewRecord> findInterView(@RequestParam("question") String question) {
        return resumeVectorService.findInterViewsByQuestion(question);
    }

    @GetMapping("/candidates")
    public List<CandidateRecord> candidates() {
        return candidatesService.getCandidates();
    }
}

/**
 * 直接路径访问候选人信息（用于测试）
 */
@RestController
class CandidatesTestController {
    private final CandidatesService candidatesService;
    
    public CandidatesTestController(CandidatesService candidatesService) {
        this.candidatesService = candidatesService;
    }
    
    @GetMapping(value = "/candidates")
    public List<CandidateRecord> getCandidates() {
        return this.candidatesService.getCandidates();
    }
}
