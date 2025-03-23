package com.guangge.Interview.services;

import com.guangge.Interview.assistant.ResumeRewriterAssistant;
import com.guangge.Interview.util.MarkdownToPdfConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResumeRewriterService {
    private static final Logger logger = LoggerFactory.getLogger(ResumeRewriterService.class);

    @Value("${python.script.extract_text.path}")
    private String pythonScriptPath;
    @Value("${python.script.html_to_pdf.path}")
    private String html2PdfPath;

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
     * @param jdText 需求
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public String processResume(MultipartFile resumeFile, String jdText) throws IOException, InterruptedException {
        // 1. 提取简历文本
        //String resumeText = extractTextFromPDF(resumeFile);
        //logger.info("提取的简历文本: " + resumeText);

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

        String outputPath = PDF_STORAGE_PATH + resumeFile.getOriginalFilename(); // 生成的PDF文件路径
        //PDFGenerator.generatePDF(rewrittenResume, outputPath);
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
        html2Pdf(html, outputPath);
        //generateMarkdown(questions);

        return "pdf/" + resumeFile.getOriginalFilename();
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

    private void html2Pdf(String html,String outputPath) throws IOException, InterruptedException {
        // 调用 extract_text.py 提取文本
        ProcessBuilder processBuilder = new ProcessBuilder("python", html2PdfPath, html, outputPath);
        processBuilder.redirectErrorStream(true); // 合并标准输出和错误输出
        Process process = processBuilder.start();

        // 读取输出
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        // 等待进程结束
        int exitCode = process.waitFor();
        System.out.println("Python script executed with exit code: " + exitCode);
    }
}
