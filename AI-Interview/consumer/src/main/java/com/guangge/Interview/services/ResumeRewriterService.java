package com.guangge.Interview.services;

import com.guangge.Interview.assistant.ResumeRewriterAssistant;
import com.guangge.Interview.record.EducationRecord;
import com.guangge.Interview.record.ProjectExperienceRecord;
import com.guangge.Interview.record.WorkExperienceRecord;
import com.guangge.Interview.util.MarkdownToPdfConverter;
import com.guangge.Interview.vo.CvRequest;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResumeRewriterService {
    private static final Logger logger = LoggerFactory.getLogger(ResumeRewriterService.class);

    @Value("${python.script.extract_text.path}")
    private String pythonScriptPath;
    @Value("${python.script.html_to_pdf.path}")
    private String html2PdfPath;
    @Value("classpath:templates/resume.txt")
    private Resource resume;
    @Value("classpath:templates/education.txt")
    private Resource education;
    @Value("classpath:templates/work.txt")
    private Resource work;
    @Value("classpath:templates/project.txt")
    private Resource project;

    private final JDKeywordExtractor jdKeywordExtractor;
    private final ResumeRewriterAssistant resumeRewriterAssistant;


    public ResumeRewriterService(JDKeywordExtractor jdKeywordExtractor, ResumeRewriterAssistant resumeRewriterAssistant) {
        this.jdKeywordExtractor = jdKeywordExtractor;
        this.resumeRewriterAssistant = resumeRewriterAssistant;
    }

    private static final String PDF_STORAGE_PATH = "external/static/pdf/";

    /**
     *使用Python的PyMuPDF库来解析PDF文件
     * @param resumeFile 简历文件
     * @param jdText 招聘要求
     * @return 简历文件名
     * @throws IOException
     * @throws InterruptedException
     */
    public String processResume(MultipartFile resumeFile, String jdText) throws IOException, InterruptedException {
        // 从IO流中读取文件
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(new InputStreamResource(resumeFile.getInputStream()));

        // 将文本内容划分成更小的块
        List<Document> documents = tikaDocumentReader.get();
        String resumeText = documents.get(0).getText();

        // 2. 提取JD关键词
        String jdKeywords = jdKeywordExtractor.extractKeywords(jdText);
        logger.info("提取的JD关键词: " + jdKeywords);

        // 3. 改写简历
        String rewrittenResume = this.resumeRewriterAssistant.rewirter(jdText, resumeText, jdKeywords);

        String originalFilename = resumeFile.getOriginalFilename();
        // 4.生成pdf
        String output = this.makePdf(originalFilename,rewrittenResume);

        return output;
    }

    /**
     * 生成简历
     * @param cvRequest 简历信息
     * @return 简历文件名
     */
    public String createResume(CvRequest cvRequest) throws IOException, InterruptedException {
        String resumeText = this.makeCvTxt(cvRequest);
        // 2. 提取JD关键词
        String jdKeywords = jdKeywordExtractor.extractKeywords(cvRequest.getJd());
        logger.info("提取的JD关键词: " + jdKeywords);

        // 3. 改写简历
        String rewrittenResume = this.resumeRewriterAssistant.rewirter(cvRequest.getJd(), resumeText, jdKeywords);

        String originalFilename = cvRequest.getName();
        // 4.生成pdf
        String output = this.makePdf(originalFilename,rewrittenResume);

        return output;
    }

    private String extractTextFromPDF(MultipartFile resumeFile) throws IOException, InterruptedException {
        // 将上传的文件保存为临时文件
        File tempFile = File.createTempFile("resume", ".pdf");
        resumeFile.transferTo(tempFile);

        // 调用 extract_text.py 提取文本
        ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath, tempFile.getAbsolutePath());
        processBuilder.redirectErrorStream(true); // 合并标准输出和错误输出
        Process process = processBuilder.start();

        // 读取Python脚本的输出
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
        String resumeText = reader.lines().collect(Collectors.joining("\n"));

        // 删除临时文件
        tempFile.delete();

        return resumeText;
    }

    private String makePdf(String originalFilename,String rewrittenResume) throws IOException, InterruptedException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(dtf);

        String resumePdfName = FilenameUtils.removeExtension(originalFilename) + "_" + timestamp + ".pdf";
        String outputPath = PDF_STORAGE_PATH + resumePdfName; // 生成的PDF文件路径

        String html = MarkdownToPdfConverter.markdownToHtml(rewrittenResume);
        html = "<html><head><meta charset=\\\"UTF-8\\\"/><style>" +
                "body { font-family: 宋体, SimSun;} " +
                "h1 { color: #333366;} " +
                "h2 { color: #666699;border-bottom: 1px solid #ddd;padding-bottom: 5px; margin-bottom: 20px;  } " +
                "ul { list-style-type: square; } " +
                "li { font-size: 18px;}" +
                "</style></head><body>" +
                html +
                "</body></html>";
        MarkdownToPdfConverter.html2Pdf(html, outputPath, html2PdfPath);

        return "pdf/" + resumePdfName;
    }
    /**
     * 根据页面内容生成简历文本
     * @param cvReqeust 简历信息
     * @return 简历文本
     */
    private String makeCvTxt(CvRequest cvReqeust) throws IOException {
        String cv = resume.getContentAsString(Charset.defaultCharset());
        cv = cv
                .replace("{name}", cvReqeust.getName())
                .replace("{sex}", cvReqeust.getSex())
                .replace("{birthDate}", cvReqeust.getBirthDate())
                .replace("{email}", cvReqeust.getEmail())
                .replace("{phone}", cvReqeust.getPhone());

        StringBuilder sb = new StringBuilder();
        List<EducationRecord> educationRecords = cvReqeust.getEducationRecords();
        if (educationRecords != null) {
            educationRecords.forEach(educationRecord -> {
                try {
                    String txt = education.getContentAsString(Charset.defaultCharset());
                    txt = txt
                            .replace("{begin}", educationRecord.getBegin())
                            .replace("{end}", educationRecord.getEnd())
                            .replace("{university}", educationRecord.getUniversity())
                            .replace("{major}", educationRecord.getMajor())
                            .replace("{degree}", educationRecord.getDegree());
                    sb.append(txt);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
        }
        cv = cv.replace("{education}",sb.toString());
        sb.setLength(0);
        List<WorkExperienceRecord> workExperienceRecords = cvReqeust.getWorkExperienceRecords();
        if (workExperienceRecords != null) {
            workExperienceRecords.forEach(workExperienceRecord -> {
                try {
                    String contentAsString = work.getContentAsString(Charset.defaultCharset());
                    contentAsString = contentAsString
                            .replace("{begin}", workExperienceRecord.getBegin())
                            .replace("{end}", workExperienceRecord.getEnd())
                            .replace("{company}", workExperienceRecord.getCompany())
                            .replace("{workContent}", workExperienceRecord.getWorkContent());
                    sb.append(contentAsString);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
        }
        cv = cv.replace("{work}",sb.toString());
        sb.setLength(0);
        List<ProjectExperienceRecord> projectExperienceRecords = cvReqeust.getProjectExperienceRecords();
        if (projectExperienceRecords != null) {
            projectExperienceRecords.forEach(projectExperienceRecord -> {
                try {
                    String contentAsString = project.getContentAsString(Charset.defaultCharset());
                    contentAsString = contentAsString
                            .replace("{begin}", projectExperienceRecord.getBegin())
                            .replace("{end}", projectExperienceRecord.getEnd())
                            .replace("{name}", projectExperienceRecord.getName())
                            .replace("{content}", projectExperienceRecord.getContent())
                            .replace("{skills}", projectExperienceRecord.getSkills());
                    sb.append(contentAsString);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        cv = cv.replace("{project}",sb.toString());
        return cv;
    }
}
