package com.guangge.Interview.assistant;

import com.guangge.Interview.advisor.LoggingAdvisor;
import lombok.SneakyThrows;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * 面试助手
 */
@Service
public class InterviewAssistant {

    private final ChatClient chatClient;
    @SneakyThrows
    public InterviewAssistant(ChatClient.Builder modelBuilder, VectorStore vectorStore, ChatMemory chatMemory,
                              @Value("classpath:prompt/Interview-face2face-System-Prompt.st") Resource systemText) {

        this.chatClient = modelBuilder.defaultSystem(systemText)
                .defaultAdvisors(
                        new PromptChatMemoryAdvisor(chatMemory),
                        new LoggingAdvisor()
                )
                .defaultFunctions("getResumeByName","changeInterView","getResumeById")
                .build();
    }

    public String chat(String chatId, String userMessageContent) {

        return this.chatClient.prompt()
                .user(userMessageContent)
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 20))
                .call().content();
    }

    public Flux<String> chatByStream(String chatId, String userMessageContent) {

        return this.chatClient.prompt()
                .user(userMessageContent)
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 20))
                .stream().content();
    }
}
