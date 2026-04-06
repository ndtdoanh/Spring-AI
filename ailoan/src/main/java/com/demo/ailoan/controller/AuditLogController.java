package com.demo.ailoan.controller;

import com.demo.ailoan.entity.AuditLog;
import com.demo.ailoan.service.AuditLogService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping("/audit-log")
    public List<AuditLog> auditLog() {
        return auditLogService.findAllOrderByCreatedAtDesc();
    }
}
