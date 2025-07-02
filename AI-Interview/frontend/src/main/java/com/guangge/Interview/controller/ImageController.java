package com.guangge.Interview.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

@Controller
public class ImageController {

    private static final Logger logger = Logger.getLogger(ImageController.class.getName());

    // 项目根目录下的图片目录
    private final String[] imageDirs = {
            "external/static/images",
            "frontend/src/main/resources/static/images",
            "frontend/target/classes/static/images",
            "src/main/resources/static/images",
            "target/classes/static/images"
    };
    
    @GetMapping("/direct-images/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        
        // 项目根目录
        String baseDir = System.getProperty("user.dir");
        
        // 检查每个可能的图片目录
        for (String imgDir : imageDirs) {
            Path filePath = Paths.get(baseDir, imgDir, filename);
            File imageFile = filePath.toFile();
            
            if (imageFile.exists() && imageFile.isFile()) {
                try {
        
                    // 确定MIME类型
                    String contentType = Files.probeContentType(filePath);
                    if (contentType == null) {
                        contentType = determineContentType(filename);
                    }
                    
                    // 构建响应
                    FileSystemResource resource = new FileSystemResource(imageFile);
                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(contentType))
                            .body(resource);
                    
                } catch (IOException e) {
                    logger.warning("处理图片时出错: " + e.getMessage());
                }
            }
        }
        
        // 如果所有目录都没有找到图片
        logger.warning("未找到图片: " + filename);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    
    /**
     * 根据文件名确定内容类型
     */
    private String determineContentType(String filename) {
        filename = filename.toLowerCase();
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (filename.endsWith(".png")) {
            return "image/png";
        } else if (filename.endsWith(".gif")) {
            return "image/gif";
        } else if (filename.endsWith(".svg")) {
            return "image/svg+xml";
        } else {
            return "application/octet-stream";
        }
    }
} 