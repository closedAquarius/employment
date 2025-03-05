package com.guangge.Interview.assistant;

import com.guangge.Interview.advisor.LoggingAdvisor;
import com.guangge.Interview.data.Knowledge;
import com.guangge.Interview.services.KnowledgeService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * JAVA笔试助手
 */
@Service
public class JavaAssistant {

    private final ChatClient chatClient;
    private final KnowledgeService knowledgeService;

    public JavaAssistant(ChatClient.Builder modelBuilder, VectorStore vectorStore, ChatMemory chatMemory,
                         @Value("classpath:prompt/Interview-System-Prompt.st") Resource systemText,
                         @Value("classpath:prompt/Java-System-Prompt.st") Resource userTextAdvisors,
                         KnowledgeService knowledgeService) throws IOException {
        this.knowledgeService = knowledgeService;


        this.chatClient = modelBuilder.defaultSystem(s -> s.text(systemText))
                .defaultAdvisors(
                        new PromptChatMemoryAdvisor(chatMemory),
                        new QuestionAnswerAdvisor(vectorStore, SearchRequest.builder().similarityThreshold(0.5).build(),
                                userTextAdvisors.getContentAsString(Charset.defaultCharset())),
                        new LoggingAdvisor()
                )
                .defaultFunctions("changeTestResult","getInterviewDetails")
                .build();
    }

    /**
     * 流聊天
     * @param chatId 聊天ID
     * @param userMessageContent 用户回答
     * @return AI回答
     */
    public Flux<String> chat(String chatId, String userMessageContent) {
        List<Knowledge> questions = knowledgeService.getQuestions("java", "3", 3);
        Map<String, Object> questionMap = IntStream.range(0, questions.size())
                .boxed()
                .collect(Collectors.toMap(
                        i -> "question" + (i + 1), // 生成 key：question1, question2, question3
                        i -> questions.get(i).getQuestion() // 获取 List 中的值作为 value
                ));

        return this.chatClient.prompt()
                .system(s -> s.params(questionMap))
                .user(userMessageContent)
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .stream().content();
    }

    /**
     * 字符串聊天
     * @param chatId 聊天ID
     * @param userMessageContent 用户回答
     * @return AI回答
     */
    public String chatForString(String chatId, String userMessageContent) {
        List<Knowledge> questions = knowledgeService.getQuestions("java", "3", 3);
        Map<String, Object> questionMap = IntStream.range(0, questions.size())
                .boxed()
                .collect(Collectors.toMap(
                        i -> "question" + (i + 1), // 生成 key：question1, question2, question3
                        i -> questions.get(i).getQuestion() // 获取 List 中的值作为 value
                ));

        return this.chatClient.prompt()
                .system(s -> s.params(questionMap))
                .user(userMessageContent)
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call().content();
    }
}
