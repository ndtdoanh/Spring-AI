package com.demo.ailoan.ai;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SystemPromptConfig {

    /**
     * Prompt classify-only: LLM chỉ cần output JSON nhỏ (~20 token).
     * Không cần giải thích tool, không cần hướng dẫn dài.
     * Giảm từ ~300 token xuống ~80 token input → nhanh hơn đáng kể.
     */
    public static final String CLASSIFY_PROMPT =
            """
            Phân loại lệnh thành JSON. Chỉ trả JSON, không thêm gì khác.
            Format bắt buộc:
            {"intent":"find|count|list|update|copy|reset|unknown","scheme":"A|B|C|null","from":"A|B|C|null","to":"A|B|C|null","amount":"null","maxAmount":"null","interestRate":"null","tenorMonths":"null","serviceFee":"null"}
            Các intent:
            find=tìm khoản vay, count=đếm khoản vay, list=liệt kê scheme,
            update=cập nhật config scheme, copy=sao chép config, reset=xóa config
            """;
}