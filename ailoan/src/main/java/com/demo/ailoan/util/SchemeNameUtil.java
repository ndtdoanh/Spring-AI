package com.demo.ailoan.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SchemeNameUtil {

    public static String normalize(String raw) {
        if (raw == null) {
            return "";
        }
        String s = raw.trim().toUpperCase();
        if (s.startsWith("SCHEME")) {
            s = s.substring(6).trim();
        }
        return s; // A, B, C
    }
}
