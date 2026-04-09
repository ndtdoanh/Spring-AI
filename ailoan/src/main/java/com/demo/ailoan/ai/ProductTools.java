package com.demo.ailoan.ai;

import com.demo.ailoan.entity.Product;
import com.demo.ailoan.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class ProductTools {

    private final ProductRepository productRepository;
    private Product lastResult;

    public ProductTools(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Tool(description = "Get product detail by id")
    public Product getProductById(Long id) {
        lastResult = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
        return lastResult;
    }

    @Transactional
    @Tool(description = "Update product by id with new name, price, status")
    public Product updateProduct(Long id, String name, Double price, String status) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
        product.setName(name);
        product.setPrice(price);
        product.setStatus(status);
        lastResult = productRepository.save(product);
        return lastResult;
    }
}
