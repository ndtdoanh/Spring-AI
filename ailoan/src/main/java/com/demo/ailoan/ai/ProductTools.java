package com.demo.ailoan.ai;

import com.demo.ailoan.entity.Product;
import com.demo.ailoan.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.ai.tool.annotation.Tool;

public class ProductTools {

    private final ProductRepository productRepository;
    private final ProductHolder holder;

    public ProductTools(ProductRepository productRepository, ProductHolder holder) {
        this.productRepository = productRepository;
        this.holder = holder;
    }

    @Tool(description = "Get product detail by id")
    public Product getProductById(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
        holder.set(p);
        return p;
    }

    @Transactional
    @Tool(description = "Update product by id with new name, price, status")
    public Product updateProduct(Long id, String name, Double price, String status) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
        product.setName(name);
        product.setPrice(price);
        product.setStatus(status);
        Product saved = productRepository.save(product);
        holder.set(saved);
        return saved;
    }
}
