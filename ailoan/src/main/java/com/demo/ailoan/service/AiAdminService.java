package com.demo.ailoan.service;

import com.demo.ailoan.ai.ProductHolder;
import com.demo.ailoan.ai.ProductTools;
import com.demo.ailoan.entity.Product;
import com.demo.ailoan.repository.ProductRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiAdminService {

    private final ChatClient chatClient;
    private final ProductRepository productRepository;

    public AiAdminService(ChatClient chatClient, ProductRepository productRepository) {
        this.chatClient = chatClient;
        this.productRepository = productRepository;
    }

    public Product handlePrompt(String prompt) {
        ProductHolder holder = new ProductHolder();

        ProductTools tools = new ProductTools(productRepository, holder);

        chatClient.prompt()
                .system("""
                        You are an admin assistant for product management.
                        You MUST call one of the provided tools to answer.
                        Never answer by yourself.
                        """)
                .user(prompt)
                .tools(tools)
                .call()
                .content();

        Product result = holder.get();
        if (result == null) {
            throw new IllegalStateException("AI did not call any tool. Please rephrase your request.");
        }

        return result;
    }
}
