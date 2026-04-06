package com.demo.ailoan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String adminUser;

    @Column(nullable = false, length = 4000)
    private String prompt;

    @Column(nullable = false, length = 128)
    private String toolCalled;

    @Column(length = 4000)
    private String params;

    @Column(length = 8000)
    private String result;

    @Column(nullable = false)
    private Instant createdAt;
}
