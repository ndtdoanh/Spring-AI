package com.demo.ailoan.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder, LoanTools loanTools) {
        return chatClientBuilder
                .defaultSystem(SystemPromptConfig.SYSTEM_PROMPT)
                .defaultTools(loanTools)
                .build();
    }
}
