package com.guangge.Interview.controller;

import com.guangge.Interview.data.Resume;
import com.guangge.Interview.services.ResumeService;
import lombok.SneakyThrows;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController()
@RequestMapping("fileupload")
public class FIleUploadController {

    private final ResumeService resumeService;
    private final VectorStore vectorStore;

    public FIleUploadController(ResumeService resumeService, VectorStore vectorStore) {
        this.resumeService = resumeService;
        this.vectorStore = vectorStore;
    }

    @PostMapping("/resume/upload")
    public ResponseEntity<String> uploadJavaDoc(@RequestParam("id") String number, @RequestParam MultipartFile file) throws IOException {
        long id = Long.valueOf(number);
        resumeService.saveResume(id,file);

        Resume resume = resumeService.findInterView(id);

        // 生成向量存储
        resumeService.saveResumeVector(resume);

        return ResponseEntity.ok().body("uploaded");
    }

    @PostMapping("/knowledge/upload")
    public String uploadKnowledge(@RequestParam MultipartFile file) throws IOException {
        // 从IO流中读取文件
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(new InputStreamResource(file.getInputStream()));
        // 将文本内容划分成更小的块
        List<Document> splitDocuments = new TokenTextSplitter()
                .apply(tikaDocumentReader.read());
        vectorStore.write(splitDocuments);
        return "ok";
    }
}
