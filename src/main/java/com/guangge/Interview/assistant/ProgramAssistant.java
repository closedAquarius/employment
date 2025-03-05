package com.guangge.Interview.assistant;

import com.guangge.Interview.advisor.LoggingAdvisor;
import com.guangge.Interview.data.Knowledge;
import com.guangge.Interview.services.KnowledgeService;
import lombok.SneakyThrows;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JAVA编程助手
 */
@Service
public class ProgramAssistant {

    private final ChatClient programChatClient;
    private final ChatClient designChatClient;
    private final ChatClient reviewChatClient;

    private final KnowledgeService knowledgeService;

    @SneakyThrows
    public ProgramAssistant(ChatClient.Builder modelBuilder, VectorStore vectorStore, ChatMemory chatMemory,
                            @Value("classpath:prompt/Design-Prompt.st") Resource designPrompt,
                            @Value("classpath:prompt/Program-Prompt.st") Resource programPrompt,
                            @Value("classpath:prompt/Program-Review-Prompt.st") Resource programReviewPrompt, 
                            KnowledgeService knowledgeService) {

        ChatClient.Builder programBuild = modelBuilder.clone();
        ChatClient.Builder designBuild = modelBuilder.clone();
        ChatClient.Builder reviewBuild = modelBuilder.clone();
        this.knowledgeService = knowledgeService;

        this.programChatClient = programBuild
                .defaultSystem(s -> s.text(programPrompt))
                .defaultAdvisors(
                        new LoggingAdvisor()
                )
                .build();


        this.designChatClient = designBuild
                .defaultSystem(s -> s.text(designPrompt))
                .defaultAdvisors(
                        new LoggingAdvisor()
                )
                .build();

        this.reviewChatClient = reviewBuild
                .defaultSystem(programReviewPrompt)
                .defaultAdvisors(
                        new LoggingAdvisor()
                )
                .build();
    }

    public String makeQuestion(boolean isProgram,String name) throws Exception {
        String content;
        if (isProgram) {
            // 取得算法题
            List<Knowledge> questions = knowledgeService.getQuestions("java", "1", 1);
            String question = questions.get(0).getQuestion();
            content = this.programChatClient.prompt()
                    .system(s -> s.param("question",question))
                    .user(u -> u.text("我是{name},请为我出题").param("name",name))
                    .call().content();
        } else {
            // 取得设计模式题
            List<Knowledge> questions = knowledgeService.getQuestions("java", "2", 1);
            String question2 = questions.get(0).getQuestion();
            content = this.designChatClient.prompt()
                    .system(s -> s.param("question",question2))
                    .user(u -> u.text("我是{name},请为我出题").param("name",name))
                    .call().content();
        }
        return content;
    }

    public Flux<String> reviewQuestion(String question, String input, String output, String code) throws Exception {
        String userContent = """
                                题目：{question},
                                示例输入:{input}
                                示例输出:{output}
                                回答内容:{code}
                                """;

        Map<String, Object> params = new HashMap<>();
        params.put("question",question);
        params.put("input",input);
        params.put("output",output);
        params.put("code",code);
        Flux<String> content = this.reviewChatClient.prompt()
                .user(u -> u.text(userContent).params(params))
                .stream().content();
        return content;
    }
}
