package com.demo.ailoan.dto;

import com.demo.ailoan.entity.Loan;
import java.math.BigDecimal;
import java.time.Instant;

public record LoanResponse(
        long id, String scheme, String customerName, BigDecimal amount, String status, Instant createdAt) {

    public static LoanResponse from(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getScheme().getName(),
                loan.getCustomerName(),
                loan.getAmount(),
                loan.getStatus(),
                loan.getCreatedAt());
    }
}
