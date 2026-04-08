package com.demo.ailoan.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * JSON output từ LLM classify step.
 * @JsonIgnoreProperties để tránh lỗi nếu LLM thêm field thừa.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IntentDto(
        String intent,   // find | count | list | update | copy | reset | unknown
        String scheme,   // A | B | C | null
        String from,     // dùng cho copy: scheme nguồn
        String to,       // dùng cho copy: scheme đích
        String customerName,
        String loanId,
        String amount,
        String maxAmount,
        String interestRate,
        String tenorMonths,
        String serviceFee
) {
    // Normalize null string từ LLM ("null" string → Java null)
    public String schemeNormalized() {
        return isNullStr(scheme) ? null : scheme;
    }

    public String fromNormalized() {
        return isNullStr(from) ? null : from;
    }

    public String toNormalized() {
        return isNullStr(to) ? null : to;
    }

    public String amountNormalized() {
        return isNullStr(amount) ? "" : amount;
    }

    public String customerNameNormalized() {
        return isNullStr(customerName) ? "" : customerName.trim();
    }

    public Long loanIdNormalized() {
        if (isNullStr(loanId)) return null;
        try {
            return Long.parseLong(loanId.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public String maxAmountNormalized() {
        return isNullStr(maxAmount) ? "" : maxAmount;
    }

    public String interestRateNormalized() {
        return isNullStr(interestRate) ? "" : interestRate;
    }

    public String tenorMonthsNormalized() {
        return isNullStr(tenorMonths) ? "" : tenorMonths;
    }

    public String serviceFeeNormalized() {
        return isNullStr(serviceFee) ? "" : serviceFee;
    }

    private boolean isNullStr(String s) {
        return s == null || s.isBlank() || s.equalsIgnoreCase("null");
    }
}