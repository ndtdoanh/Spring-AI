package com.demo.ailoan.controller;

import com.demo.ailoan.ai.AiCommandContext;
import com.demo.ailoan.ai.AiOrchestrator;
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
    private final AiCommandContext commandContext;
    private final ChatHistoryService chatHistoryService;

    @PostMapping("/command")
    public ResponseEntity<AiCommandResponse> command(
            @RequestHeader(value = "X-Session-Id", required = false) String sessionIdHeader,
            @RequestHeader(value = "X-Admin-User", required = false) String adminUser,
            @Valid @RequestBody AiCommandRequest body,
            HttpServletResponse response) {
        String sessionId =
                sessionIdHeader != null && !sessionIdHeader.isBlank()
                        ? sessionIdHeader.trim()
                        : UUID.randomUUID().toString();
        response.setHeader("X-Session-Id", sessionId);

        String command = body.command().trim();
        chatHistoryService.append(sessionId, "user", command);
        commandContext.begin(adminUser, command);

        String llmMessage = aiOrchestrator.runConversationTurn(command);
        String message = commandContext.getToolCalledSummary().isBlank()
                ? "Không có tool nào được gọi. Vui lòng thử lại lệnh rõ hơn."
                : commandContext.getLastToolMessage();
        if (message == null || message.isBlank()) {
            message = llmMessage;
        }
        AiCommandResponse res =
                new AiCommandResponse(
                        message, commandContext.getToolCalledSummary(), commandContext.getLastAffectedCount());
        chatHistoryService.append(sessionId, "assistant", message);
        return ResponseEntity.ok(res);
    }
}
