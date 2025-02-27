package com.guangge.Interview.client;

import com.guangge.Interview.audio.services.TextToSpeechService;
import com.guangge.Interview.assistant.JavaAssistant;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;

import reactor.core.publisher.Flux;

@BrowserCallable
@AnonymousAllowed
public class AssistantService {

    private final JavaAssistant interViewAgent;
    private final TextToSpeechService speechService;

    public AssistantService(JavaAssistant interViewAgent,
                            TextToSpeechService speechService) {
        this.interViewAgent = interViewAgent;
        this.speechService = speechService;
    }


    public Flux<String> interViewChat(String chatId, String userMessage)  {
        return interViewAgent.chat(chatId, userMessage);
    }

}
