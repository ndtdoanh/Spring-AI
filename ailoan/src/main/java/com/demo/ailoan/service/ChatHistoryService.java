package com.demo.ailoan.service;

import com.demo.ailoan.entity.ChatHistory;
import com.demo.ailoan.repository.ChatHistoryRepository;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatHistoryService {

    private final ChatHistoryRepository chatHistoryRepository;

    @Transactional(readOnly = true)
    public List<ChatHistory> findBySession(String sessionId) {
        return chatHistoryRepository.findBySessionIdOrderByCreatedAtAscIdAsc(sessionId);
    }

    @Transactional
    public void append(String sessionId, String role, String content) {
        chatHistoryRepository.save(
                ChatHistory.builder()
                        .sessionId(sessionId)
                        .role(role)
                        .content(content)
                        .createdAt(Instant.now())
                        .build());
    }
}
