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

    public AiResult handlePrompt(String prompt) {
        String response = chatClient.prompt()
                .system("""
                        You are an admin assistant for product management.
                        Only handle: get product or update product.
                        Always call the appropriate tool and return the result as JSON.
                        """)
                .user(prompt)
                .tools(productTools)
                .call()
                .content();

        return new AiResult(response, productTools.getLastResult());
    }

    public record AiResult(String answer, Product product) {
    }
}
