package com.demo.ailoan.controller;

import com.demo.ailoan.ai.AiCommandContext;
import com.demo.ailoan.ai.AiOrchestrator;
import com.demo.ailoan.ai.OrchestratorResult;
import com.demo.ailoan.dto.AiCommandRequest;
import com.demo.ailoan.dto.AiCommandResponse;
import com.demo.ailoan.service.ChatHistoryService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/ai")
@RequiredArgsConstructor
public class AiCommandController {

    private final AiOrchestrator aiOrchestrator;
    private final AiCommandContext commandContext;   // giữ lại để audit vẫn hoạt động
    private final ChatHistoryService chatHistoryService;

    @PostMapping("/command")
    public ResponseEntity<AiCommandResponse> command(
            @RequestHeader(value = "X-Session-Id", required = false) String sessionIdHeader,
            @RequestHeader(value = "X-Admin-User", required = false) String adminUser,
            @Valid @RequestBody AiCommandRequest body,
            HttpServletResponse response) {

        String sessionId = (sessionIdHeader != null && !sessionIdHeader.isBlank())
                ? sessionIdHeader.trim()
                : UUID.randomUUID().toString();
        response.setHeader("X-Session-Id", sessionId);

        String command = body.command().trim();
        chatHistoryService.append(sessionId, "user", command);

        // begin() vẫn cần để LoanTools ghi audit đúng adminUser + prompt
        commandContext.begin(adminUser, command);

        // Chạy orchestrator — trả OrchestratorResult thay vì String
        OrchestratorResult result = aiOrchestrator.runConversationTurn(command);

        // Lấy message trực tiếp từ result, không còn phụ thuộc vào AiCommandContext
        String message = result.message();

        AiCommandResponse res = new AiCommandResponse(
                message,
                result.toolCalled(),
                result.affectedCount());

        chatHistoryService.append(sessionId, "assistant", message);
        return ResponseEntity.ok(res);
    }
}