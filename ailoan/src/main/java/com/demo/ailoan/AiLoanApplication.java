package com.demo.ailoan;

import com.demo.ailoan.entity.Product;
import com.demo.ailoan.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AiLoanApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiLoanApplication.class, args);
    }

    @Bean
    CommandLineRunner seedDemoProducts(ProductRepository productRepository) {
        return args -> {
            if (productRepository.count() == 0) {
                productRepository.save(new Product("Laptop", 25000000d, "ACTIVE"));
                productRepository.save(new Product("Phone", 12000000d, "ACTIVE"));
            }
        };
    }
}
