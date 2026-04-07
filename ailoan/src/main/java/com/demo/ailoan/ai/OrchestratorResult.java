package com.demo.ailoan.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Kết quả từ AiOrchestrator sau khi dispatch tool.
 * Controller dùng record này thay vì đọc từ AiCommandContext.
 */
public record OrchestratorResult(
        String toolCalled,      // tên tool đã gọi, rỗng nếu lỗi/unknown
        String rawJson,         // JSON trả về từ LoanTools
        String message,         // message rút ra từ ToolResult.message
        int affectedCount,      // affectedCount từ ToolResult
        boolean isError         // true nếu không hiểu lệnh hoặc thiếu param
) {

    /** Tạo result từ tool call thành công — tự parse JSON lấy message + affectedCount. */
    public static OrchestratorResult of(String toolName, String toolJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ToolResultJson parsed = mapper.readValue(toolJson, ToolResultJson.class);
            return new OrchestratorResult(
                    toolName,
                    toolJson,
                    parsed.message() != null ? parsed.message() : toolJson,
                    parsed.affectedCount(),
                    false);
        } catch (Exception e) {
            // JSON không parse được — trả nguyên raw
            return new OrchestratorResult(toolName, toolJson, toolJson, 0, false);
        }
    }

    /** Tạo result lỗi / không hiểu lệnh — không có tool nào được gọi. */
    public static OrchestratorResult error(String message) {
        return new OrchestratorResult("", "", message, 0, true);
    }

    // Inner record để parse JSON từ LoanTools (khớp với ToolResult record)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ToolResultJson(String message, int affectedCount) {}
}