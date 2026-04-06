package com.demo.ailoan.dto;

import jakarta.validation.constraints.NotBlank;

public record AiCommandRequest(@NotBlank String command) {}
