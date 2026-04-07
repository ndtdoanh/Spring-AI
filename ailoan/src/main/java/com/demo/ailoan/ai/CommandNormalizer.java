package com.demo.ailoan.ai;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Normalize câu lệnh tự nhiên → câu rõ ràng hơn trước khi gửi LLM classify.
 *
 * Lý do cần: model 3b không biết "số tiền" / "lãi suất" / "hạn mức" map vào
 * infoA/B/C/D nào vì đó là domain knowledge của hệ thống này.
 * Java biết mapping đó → normalize trước, LLM chỉ cần classify cú pháp đơn giản.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommandNormalizer {

    /**
     * Map từ ngữ tự nhiên → tên field infoX.
     * Thêm mapping tùy theo domain thực tế của bạn.
     *
     * Ví dụ:
     *   "cập nhật số tiền scheme A thành 100"
     *   → "cập nhật infoA = 100 cho scheme A"
     */
    public static String normalize(String command) {
        if (command == null) return "";

        String result = command.trim();

        // --- Map field alias → tên field chuẩn ---
        result = result
                // infoA aliases
                .replaceAll("(?i)số\\s*tiền|hạn\\s*mức|limit|amount", "infoA")
                // infoB aliases — thêm theo domain của bạn
                .replaceAll("(?i)lãi\\s*suất|interest\\s*rate|rate", "infoB")
                // infoC aliases
                .replaceAll("(?i)kỳ\\s*hạn|term|tenure", "infoC")
                // infoD aliases
                .replaceAll("(?i)phí\\s*dịch\\s*vụ|service\\s*fee|fee", "infoD");

        // --- Normalize cú pháp "thành X" → "= X" để LLM dễ parse hơn ---
        // "infoA thành 100" → "infoA = 100"
        result = result.replaceAll("(?i)(info[abcdABCD])\\s+thành\\s+", "$1 = ");

        // --- Normalize "tất cả scheme X" → "scheme X" (bỏ "tất cả" thừa) ---
        result = result.replaceAll("(?i)tất\\s*cả\\s+scheme", "scheme");

        return result;
    }
}