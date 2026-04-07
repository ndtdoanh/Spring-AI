package com.demo.ailoan.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder
                // Không đăng ký defaultTools nữa.
                // LLM chỉ làm 1 việc: classify intent → output JSON nhỏ.
                // Java (AiOrchestrator) tự quyết định gọi tool nào.
                .defaultSystem(SystemPromptConfig.CLASSIFY_PROMPT)
                .build();
    }
}