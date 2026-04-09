package com.demo.ailoan.controller;

import com.demo.ailoan.service.AiAdminService;
import com.demo.ailoan.service.AiAdminService.AiResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/ai")
public class AiCommandController {

    private final AiAdminService aiAdminService;

    public AiCommandController(AiAdminService aiAdminService) {
        this.aiAdminService = aiAdminService;
    }

    @PostMapping("/command")
    public AiResult handleCommand(@Valid @RequestBody CommandRequest request) {
        return aiAdminService.handlePrompt(request.prompt());
    }

    public record CommandRequest(@NotBlank String prompt) {
    }
}
