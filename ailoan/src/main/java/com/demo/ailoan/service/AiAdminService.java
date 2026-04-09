package com.demo.ailoan.service;

import com.demo.ailoan.ai.ProductTools;
import com.demo.ailoan.entity.Product;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiAdminService {

    private final ChatClient chatClient;
    private final ProductTools productTools;

    public AiAdminService(ChatClient chatClient, ProductTools productTools) {
        this.chatClient = chatClient;
        this.productTools = productTools;
    }

    public Product handlePrompt(String prompt) {
        return chatClient.prompt()
                .system("""
                        You are an admin assistant for product management.
                        You MUST call one of the provided tools to answer.
                        If you do not call a tool, you fail.
                        Never answer by yourself.
                        """)
                .user(prompt)
                .tools(productTools)
                .call()
                .entity(Product.class);
    }
}
