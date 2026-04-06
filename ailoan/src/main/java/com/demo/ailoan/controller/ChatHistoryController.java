package com.demo.ailoan.controller;

import com.demo.ailoan.entity.ChatHistory;
import com.demo.ailoan.service.ChatHistoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatHistoryController {

    private final ChatHistoryService chatHistoryService;

    @GetMapping("/chat-history")
    public List<ChatHistory> history(
            @RequestParam(value = "sessionId", required = false) String sessionIdParam,
            @RequestHeader(value = "X-Session-Id", required = false) String sessionIdHeader) {
        String sid = sessionIdParam != null && !sessionIdParam.isBlank() ? sessionIdParam.trim() : sessionIdHeader;
        if (sid == null || sid.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cần sessionId query hoặc header X-Session-Id");
        }
        return chatHistoryService.findBySession(sid);
    }
}
