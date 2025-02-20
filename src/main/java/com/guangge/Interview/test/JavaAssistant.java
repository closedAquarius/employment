package com.guangge.Interview.test;

import com.guangge.Interview.services.LoggingAdvisor;
import lombok.SneakyThrows;
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

import java.nio.charset.Charset;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * JAVA笔试助手
 */
@Service
public class JavaAssistant {

    private final ChatClient chatClient;
    @SneakyThrows
    public JavaAssistant(ChatClient.Builder modelBuilder, VectorStore vectorStore, ChatMemory chatMemory,
                         @Value("classpath:prompt/Interview-System-Prompt.st") Resource systemText,
                         @Value("classpath:prompt/Java-System-Prompt.st") Resource userTextAdvisors) {

        this.chatClient = modelBuilder.defaultSystem(systemText)
                .defaultAdvisors(
                        new PromptChatMemoryAdvisor(chatMemory),
                        new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults(),userTextAdvisors.getContentAsString(Charset.defaultCharset())),
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

        return this.chatClient.prompt()
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

        return this.chatClient.prompt()
                .user(userMessageContent)
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call().content();
    }
}
