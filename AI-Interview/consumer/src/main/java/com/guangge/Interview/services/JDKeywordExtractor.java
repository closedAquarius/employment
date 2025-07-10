package com.guangge.Interview.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
public class JDKeywordExtractor {
    private static final Logger logger = LoggerFactory.getLogger(JDKeywordExtractor.class);

    @Value("${python.script.extract_keywords.path}")
    private String pythonScriptPath;

    @Value("${python.path}")
    private String pythonPath;

    /**
     * Python脚本并提取关键词。
     * @param jdText
     * @return
     */
    public String extractKeywords(String jdText) {
        File tempFile = null;
        try {
            logger.info("开始提取关键词，Python路径: {}, 脚本路径: {}", pythonPath, pythonScriptPath);

            // 将JD文本写入临时文件，避免命令行参数传递问题
            tempFile = File.createTempFile("jd_", ".txt");
            logger.info("创建临时文件: {}", tempFile.getAbsolutePath());

            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(jdText);
            }
            logger.info("JD文本已写入临时文件");

            // 检查文件是否存在
            if (!tempFile.exists()) {
                logger.error("临时文件不存在: {}", tempFile.getAbsolutePath());
                throw new RuntimeException("临时文件创建失败");
            }

            // 检查Python脚本是否存在
            File scriptFile = new File(pythonScriptPath);
            if (!scriptFile.exists()) {
                logger.error("Python脚本不存在: {}", pythonScriptPath);
                throw new RuntimeException("Python脚本不存在: " + pythonScriptPath);
            }

            // 创建ProcessBuilder并设置命令，传递文件路径作为参数
            String[] command = {pythonPath, pythonScriptPath, tempFile.getAbsolutePath()};
            logger.info("执行命令: {}", String.join(" ", command));

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true); // 合并标准输出和错误输出

            // 启动进程
            logger.info("启动Python进程");
            Process process = processBuilder.start();

            // 读取Python脚本的输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line);
                logger.info("Python输出: {}", line);
            }

            // 等待进程结束并获取退出码
            int exitCode = process.waitFor();
            logger.info("Python进程退出码: {}", exitCode);

            // 删除临时文件
            if (tempFile.exists()) {
                boolean deleted = tempFile.delete();
                logger.info("临时文件删除{}: {}", deleted ? "成功" : "失败", tempFile.getAbsolutePath());
            }

            if (exitCode == 0) {
                // 成功执行，返回提取的关键词
                logger.info("成功提取关键词: {}", output.toString());
                return output.toString();
            } else {
                logger.error("Python脚本执行失败，退出码: {}, 输出: {}", exitCode, output.toString());
                throw new RuntimeException("Python脚本执行失败，退出码: " + exitCode);
            }

        } catch (Exception e) {
            logger.error("提取关键词时发生错误", e);
            // 确保临时文件被删除
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
            throw new RuntimeException("提取关键词时发生错误: " + e.getMessage(), e);
        }
    }
}