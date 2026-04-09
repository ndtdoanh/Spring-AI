package com.demo.ailoan.controller;

import com.demo.ailoan.entity.Product;
import com.demo.ailoan.service.AiAdminService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Validated
@RestController
@RequestMapping("/api/ai")
public class AiCommandController {

    private final AiAdminService aiAdminService;

    public AiCommandController(AiAdminService aiAdminService) {
        this.aiAdminService = aiAdminService;
    }

    @PostMapping("/command")
    public ResponseEntity<?> handleCommand(@Valid @RequestBody CommandRequest request) {
        try {
            Product product = aiAdminService.handlePrompt(request.prompt());
            return ResponseEntity.ok(product);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    public record CommandRequest(@NotBlank String prompt) {
    }
}