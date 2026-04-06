package com.demo.ailoan.service;

import com.demo.ailoan.entity.AuditLog;
import com.demo.ailoan.repository.AuditLogRepository;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional(readOnly = true)
    public List<AuditLog> findAllOrderByCreatedAtDesc() {
        return auditLogRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public void save(String adminUser, String prompt, String toolCalled, String params, String result) {
        auditLogRepository.save(
                AuditLog.builder()
                        .adminUser(adminUser != null ? adminUser : "admin")
                        .prompt(prompt != null ? prompt : "")
                        .toolCalled(toolCalled)
                        .params(params)
                        .result(result)
                        .createdAt(Instant.now())
                        .build());
    }
}
