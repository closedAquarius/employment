package com.guangge.Interview.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
public class JDKeywordExtractor {

    @Value("${python.script.extract_keywords.path}")
    private String pythonScriptPath;

    /**
     * Python脚本并提取关键词。
     * @param jdText
     * @return
     */
    public String extractKeywords(String jdText) {
        try {
            // 将参数编码为 UTF-8
            byte[] jdTextBytes = jdText.getBytes(StandardCharsets.UTF_8);
            String jdTextEncoded = new String(jdTextBytes, StandardCharsets.UTF_8);
            // 创建ProcessBuilder并设置命令
            ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath, jdText, jdTextEncoded);
            processBuilder.redirectErrorStream(true); // 合并标准输出和错误输出

            // 启动进程
            Process process = processBuilder.start();

            // 读取Python脚本的输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            // 等待进程结束并获取退出码
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                // 成功执行，返回提取的关键词
                return output.toString();
            } else {
                throw new RuntimeException("Python脚本执行失败，退出码: " + exitCode);
            }

        } catch (Exception e) {
            throw new RuntimeException("提取关键词时发生错误: " + e.getMessage(), e);
        }
    }
}
