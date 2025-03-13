package com.guangge.Interview.mail;


import com.guangge.Interview.data.Resume;
import com.guangge.Interview.services.ResumeService;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Date;

@Service
public class MailService {

    @Value("${spring.mail.username}")
    private String mailUsername;
    @Resource
    private JavaMailSender javaMailSender;

    @Autowired
    private ResumeService resumeService;
    /**
     * 发送附件邮件
     * @param name 姓名
     */
    public void sendMailForAttachment(String name){
        Resume resume = this.resumeService.findInterView(name);

        //创建邮件对象
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);

            message.setSubject("[光哥面试]" + name +"面试结果");
            message.setFrom(mailUsername);
            message.setTo(resume.getEmail());
            message.setSentDate(new Date());
            String content = "<h3>" + name +":您好</h3>" +
                             "<br>" +
                             "<br>" +
                             "<h5>附件里的录音使我们面试官对您的评价，请注意查收！</h5>";

            message.setText(content,true);

            String filePath = resume.getMp3Path();
            if (StringUtils.hasText(filePath)) {
                FileSystemResource file = new FileSystemResource(new File(filePath));
                String fileName = "面试结果.mp3";
                message.addAttachment(fileName,file);
            }
            /**
             * 真实场景，可能不是一个固定文件，而需要由前端传入文件或base64转文件等方式。根据上面代码自己灵活变通 转为File类型即可。
             */

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        //邮件发送
        javaMailSender.send(mimeMessage);
    }
}
