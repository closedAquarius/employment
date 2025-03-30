package com.guangge.Interview.controller;

import com.guangge.Interview.data.Resume;
import com.guangge.Interview.services.ResumeService;
import com.guangge.Interview.services.ResumeVectorService;
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
    private final ResumeVectorService resumeVectorService;

    public FIleUploadController(ResumeService resumeService, VectorStore vectorStore, ResumeVectorService resumeVectorService) {
        this.resumeService = resumeService;
        this.vectorStore = vectorStore;
        this.resumeVectorService = resumeVectorService;
    }

    /**
     * 面试者简历
     * @param number
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/resume/upload")
    public ResponseEntity<String> uploadJavaDoc(@RequestParam("id") String number, @RequestParam MultipartFile file) throws IOException {
        long id = Long.valueOf(number);
        resumeService.saveResume(id,file);

        Resume resume = resumeService.findInterView(id);

        // 生成向量存储
        resumeVectorService.saveResumeVector(resume);

        return ResponseEntity.ok().body("uploaded");
    }

    /**
     * 知识库上传
     * @param file 知识库文件
     * @return ok
     * @throws IOException
     */
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
