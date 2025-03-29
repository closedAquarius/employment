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

    public static void html2Pdf(String html, String outputPath, String script) throws IOException, InterruptedException {
        // 调用 extract_text.py 提取文本
        ProcessBuilder processBuilder = new ProcessBuilder("python", script, html, outputPath);
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

        String output = """
                ## 个人信息
                - **姓名:** 张三 \s
                - **教育背景:** 浙江大学，2004.9~2008.7，计算机科学学士 \s
                                
                ---
                                
                ## 工作经历
                                
                ### 阿里巴巴（2018.9~至今）
                #### 职位: 高级软件工程师
                                
                - **背景与挑战:** 在阿里巴巴期间，负责参与阿里千问大模型的性能调优工作，面临模型推理效率低、资源利用率不高的问题。
                - **任务:** 我的任务是通过优化算法和框架结构，提升模型推理速度，并降低计算资源消耗。
                - **行动:**\s
                  - 快速学习并掌握Langchain框架，结合Python实现模型调优。
                  - 引入分布式计算技术，优化模型训练和推理流程。
                  - 与团队成员密切协作，设计并实施多轮测试验证方案。
                - **成果:**\s
                  - 模型推理时间缩短了**30%**，资源使用率提升了**25%**。
                  - 成功支持了多个客户的定制化需求，客户满意度达到**95%**。
                                
                ---
                                
                ### 大连埃森哲技术有限公司（2008.9~2018.9）
                #### 职位: 软件开发工程师
                                
                - **背景与挑战:** 在公司任职期间，参与多个大型IT项目的开发与维护工作，涉及金融、航空等多个领域，需要快速适应不同业务场景。
                - **任务:** 我的主要职责包括系统架构设计、代码开发以及性能优化。
                - **行动:**\s
                  - 使用Java语言进行核心功能模块开发，确保代码质量与可维护性。
                  - 应用Spring系列框架构建高效的服务端架构，提升系统稳定性。
                  - 定期出差至客户现场，快速响应并解决客户需求。
                - **成果:**\s
                  - 在某著名互联网公司私有云项目中，成功将系统可用性提升至**99.99%**。
                  - 带领团队完成**3个以上**重大项目交付，客户反馈评分均高于**4.8/5**。
                                
                ---
                                
                ## 项目经历
                                
                ### 阿里千问大模型调优（2018.9~至今）
                - **背景与挑战:** 需要对大规模语言模型进行性能优化，以满足不同客户场景下的实时推理需求。
                - **任务:** 负责模型算法优化及资源调度策略改进。
                - **行动:**\s
                  - 使用Langchain框架重构部分关键模块，减少内存占用。
                  - 设计并实现基于Hadoop生态圈的数据处理管道，加速模型训练过程。
                - **成果:**\s
                  - 模型训练时间缩短了**40%**，整体性能显著提升。
                  - 支持了超过**10家**客户的个性化部署需求。
                                
                ---
                                
                ### 某著名互联网公司私有云系统（2012.9~2018.9）
                - **背景与挑战:** 为某知名互联网企业提供私有云平台建设服务，要求具备高并发处理能力和灵活扩展性。
                - **任务:** 负责系统后端开发及性能优化工作。
                - **行动:**\s
                  - 使用Spring Boot框架搭建微服务架构，提高系统模块化程度。
                  - 集成RabbitMQ消息队列，优化异步任务处理能力。
                - **成果:**\s
                  - 系统每秒处理请求量（TPS）提升了**50%**，用户访问延迟降低了**30%**。
                                
                ---
                                
                ### 某著名汽车公司财务系统（2010.9~2012.9）
                - **背景与挑战:** 开发一套适用于汽车行业的财务管理信息系统，需兼容复杂业务逻辑。
                - **任务:** 负责数据库设计、后端开发及接口对接工作。
                - **行动:**\s
                  - 使用Spring框架构建稳定的服务端架构，确保数据一致性。
                  - 优化Oracle数据库查询语句，提升系统运行效率。
                - **成果:**\s
                  - 数据查询速度提升了**2倍**，系统稳定性达到**99.9%**。
                                
                ---
                                
                ### 某著名航空公司管理系统（2008.9~2010.9）
                - **背景与挑战:** 参与某航空公司管理系统的开发工作，需支持高并发访问和实时数据分析。
                - **任务:** 主要负责系统后端开发及数据库设计。
                - **行动:**\s
                  - 使用Java语言编写核心业务逻辑，保证代码规范性和可读性。
                  - 优化数据库表结构，提升数据存储与检索效率。
                - **成果:**\s
                  - 系统峰值并发量支持达**1万+**，未出现明显卡顿现象。
                                
                ---
                                
                ## 技能清单
                - **编程语言:** JAVA, Python \s
                - **框架与工具:** Spring Boot, Spring Cloud, RabbitMQ, Langchain \s
                - **数据库:** Oracle, MySQL \s
                - **大数据技术:** Hadoop生态圈相关技术（如HDFS、MapReduce等） \s
                - **其他能力:** 快速学习新知识的能力，良好的沟通与团队协作能力 \s
                                
                ---
                                
                ## 教育背景
                - **浙江大学**, 2004.9~2008.7 \s
                  - 专业: 计算机科学 \s
                  - 学历: 本科 \s
                                
                ---
                                
                ## 其他说明
                - **出差能力:** 能够根据项目需求长期出差至客户现场。 \s
                - **沟通技能:** 具备优秀的跨部门沟通能力，能够与团队成员及客户保持高效交流。
                """;
        String Text = markdownToHtml(output);
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
        //html2Pdf(html, "output/resume.pdf");
    }
}
