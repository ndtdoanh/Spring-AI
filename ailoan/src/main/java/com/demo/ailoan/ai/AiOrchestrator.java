package com.demo.ailoan.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiOrchestrator {

    private final ChatClient chatClient;

    public String runConversationTurn(String command) {
        // Keep prompt short for low latency: one turn, one command.
        return chatClient.prompt().user(command).call().content();
    }
}
