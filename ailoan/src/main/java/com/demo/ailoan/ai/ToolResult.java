package com.demo.ailoan.ai;

public record ToolResult(
        String message,
        Object uiPayload,
        int affectedCount) {}

