package com.demo.ailoan.service;

import com.demo.ailoan.ai.ProductTools;
import com.demo.ailoan.entity.Product;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiAdminService {

    private final ChatClient chatClient;
    private final ProductTools productTools;

    public AiAdminService(ChatClient.Builder builder, ProductTools productTools) {
        this.chatClient = builder.build();
        this.productTools = productTools;
    }

    public AiResult handlePrompt(String prompt) {

        Product product = chatClient.prompt()
                .system("""
                        You are an admin assistant for product management.
                        Only handle: get product or update product.
                        Always call the appropriate tool.
                        """)
                .user(prompt)
                .tools(productTools)
                .call()
                .entity(Product.class);

        return new AiResult("OK", product);
    }

    public record AiResult(String answer, Product product) {
    }
}
