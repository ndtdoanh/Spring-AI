package com.demo.ailoan.ai;

import com.demo.ailoan.entity.Product;
import com.demo.ailoan.repository.ProductRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ProductTools {

    private final ProductRepository productRepository;

    public ProductTools(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Tool(description = "Get product detail by id")
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
    }

    @Transactional
    @Tool(description = "Update product by id with new name, price, status")
    public Product updateProduct(Long id, String name, Double price, String status) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
        product.setName(name);
        product.setPrice(price);
        product.setStatus(status);
        return productRepository.save(product);
    }
}
