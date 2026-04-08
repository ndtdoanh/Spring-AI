package com.demo.ailoan.ai;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Normalize câu lệnh tự nhiên → câu rõ ràng hơn trước khi gửi LLM classify.
 *
 * Lý do cần: model 3b không biết "lãi suất" / "hạn mức" map vào
 * field config nào vì đó là domain knowledge của hệ thống này.
 * Java biết mapping đó → normalize trước, LLM chỉ cần classify cú pháp đơn giản.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommandNormalizer {

    /**
     * Map từ ngữ tự nhiên → tên field nghiệp vụ.
     * Thêm mapping tùy theo domain thực tế của bạn.
     *
     * Ví dụ:
     *   "cập nhật số tiền scheme A thành 100"
     *   → "cập nhật amount = 100 cho scheme A"
     */
    public static String normalize(String command) {
        if (command == null) return "";

        String result = command.trim();

        // --- Map field alias → tên field chuẩn ---
        result = result
                // Loan amount aliases
                .replaceAll("(?i)số\\s*tiền\\s*(khoản\\s*vay)?|loan\\s*amount", "amount")
                // Scheme config aliases
                .replaceAll("(?i)hạn\\s*mức|limit|max\\s*amount", "maxAmount")
                .replaceAll("(?i)lãi\\s*suất|interest\\s*rate|rate", "interestRate")
                .replaceAll("(?i)kỳ\\s*hạn|term|tenure", "tenorMonths")
                .replaceAll("(?i)phí\\s*dịch\\s*vụ|service\\s*fee|fee", "serviceFee");

        // --- Normalize cú pháp "thành X" → "= X" để LLM dễ parse hơn ---
        // "amount thành 100" → "amount = 100"
        result = result.replaceAll("(?i)(amount|maxAmount|interestRate|tenorMonths|serviceFee)\\s+thành\\s+", "$1 = ");
        result = result.replaceAll("(?i)(amount|maxAmount|interestRate|tenorMonths|serviceFee)\\s+bằng\\s+", "$1 = ");

        // --- Normalize "tất cả scheme X" → "scheme X" (bỏ "tất cả" thừa) ---
        result = result.replaceAll("(?i)tất\\s*cả\\s+scheme", "scheme");

        return result;
    }
}