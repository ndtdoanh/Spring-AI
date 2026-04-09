package com.demo.ailoan.service;

import com.demo.ailoan.ai.ProductTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class AiAdminService {

    private final ChatClient chatClient;
    private final ProductTools productTools;

    public AiAdminService(ChatModel chatModel, ProductTools productTools) {
        this.chatClient = ChatClient.builder(chatModel).build();
        this.productTools = productTools;
    }

    public String handlePrompt(String prompt) {
        return chatClient.prompt()
                .system("""
                        You are an admin assistant for product management.
                        Use tools when user asks to get or update product.
                        Keep response concise and in Vietnamese.
                        """)
                .user(prompt)
                .tools(productTools)
                .call()
                .content();
    }
}
