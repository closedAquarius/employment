package com.guangge.Interview.util;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.layout.font.FontProvider;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class MarkdownToPdfConverter {
    /**
     * 将Markdown转换为HTML
     */
    public static String markdownToHtml(String markdown) {
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        Document document = parser.parse(markdown);
        return renderer.render(document);
    }

    /**
     * 将HTML转换为PDF
     */
    public static void htmlToPdf(String html, String outputPdfPath) {
        try (FileOutputStream os = new FileOutputStream(outputPdfPath)) {
            ConverterProperties converterProperties = new ConverterProperties();
            FontProvider fontProvider = new FontProvider();

            InputStream fontStream = MarkdownToPdfConverter.class.getClassLoader().getResourceAsStream("fonts/NotoSerifSC-VariableFont_wght.ttf");
            if (fontStream == null) {
                throw new RuntimeException("字体文件未找到！");
            }

            // 将输入流转换为字节数组
            byte[] fontBytes = fontStream.readAllBytes();
            System.out.println("Font byte array length: " + fontBytes.length);
            FontProgram fontProgram = FontProgramFactory.createFont(fontBytes, true);

            fontProvider.addFont(fontProgram);
            converterProperties.setFontProvider(fontProvider);

            // 设置编码
            converterProperties.setCharset(StandardCharsets.UTF_8.name());

            HtmlConverter.convertToPdf(html, os, converterProperties);
            System.out.println("HTML 已成功转换为 PDF: " + outputPdfPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void html2Pdf(String html,String outputPath) throws IOException, InterruptedException {
        // 调用 extract_text.py 提取文本
        ProcessBuilder processBuilder = new ProcessBuilder("python", "scripts/html2pdf.py", html, outputPath);
        processBuilder.redirectErrorStream(true); // 合并标准输出和错误输出
        Process process = processBuilder.start();

        // 读取输出
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        // 等待进程结束
        int exitCode = process.waitFor();
        System.out.println("Python script executed with exit code: " + exitCode);
    }

    public static void main(String[] args) throws Exception {

        String html = "<html><head><meta charset=\"UTF-8\" /><style>" +
                "body { font-family: 宋体, SimSun; background: #f0f0f0;} " +
                "h1 { color: #333366;} " +
                "h2 { color: #666699;border-bottom: 1px solid #ddd;padding-bottom: 5px; margin-bottom: 20px;  } " +
                "li { font-size: 18px;}" +
                "</style></head><body>" +
                "<h1>张三</h1>" +
                "<h2>联系方式</h2>" +
                "<ul><li>电话: 123-456-7890</li><li>邮箱: zhangsan@example.com</li></ul>" +
                "<h2>工作经历</h2>" +
                "<h3>公司A - 软件工程师</h3>" +
                "<ul><li><strong>时间</strong>: 2020年1月 - 至今</li>" +
                "<li><strong>职责</strong>: 负责开发Java后端服务，优化系统性能。</li>" +
                "<li><strong>成就</strong>: 通过优化数据库查询，将系统响应时间减少了30%。</li></ul>" +
                "<h2>教育背景</h2>" +
                "<h3>某某大学 - 计算机科学与技术</h3>" +
                "<ul><li><strong>时间</strong>: 2016年9月 - 2020年6月</li>" +
                "<li><strong>学位</strong>: 本科</li></ul>" +
                "</body></html>";
        html2Pdf(html, "output/resume.pdf");
    }
}
