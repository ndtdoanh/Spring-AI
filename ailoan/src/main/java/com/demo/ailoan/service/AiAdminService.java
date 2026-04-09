package com.demo.ailoan.service;

import com.demo.ailoan.ai.ProductTools;
import com.demo.ailoan.entity.Product;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AiAdminService {

    private final ProductTools productTools;

    private static final Pattern GET_ID_PATTERN = Pattern.compile("\\bid\\s*(?:=|la|là)?\\s*(\\d+)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern UPDATE_ID_PATTERN = Pattern.compile("\\bcap\\s*nhat\\b.*?\\bid\\s*(?:=|la|là)?\\s*(\\d+)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern NAME_PATTERN = Pattern.compile("\\bten\\b\\s*(?:=|la|là)?\\s*([^,;]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern PRICE_PATTERN = Pattern.compile("\\bgia\\b\\s*(?:=|la|là)?\\s*([\\d.,]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern STATUS_PATTERN = Pattern.compile("\\btrang\\s*thai\\b\\s*(?:=|la|là)?\\s*([a-zA-Z_]+)", Pattern.CASE_INSENSITIVE);

    public AiAdminService(ProductTools productTools) {
        this.productTools = productTools;
    }

    public AiResult handlePrompt(String prompt) {
        String normalized = normalizeVietnamese(prompt).toLowerCase(Locale.ROOT);

        if (normalized.contains("cap nhat")) {
            return handleUpdate(prompt, normalized);
        }
        if (normalized.contains("lay") || normalized.contains("xem") || normalized.contains("get")) {
            Long id = extractRequiredLong(GET_ID_PATTERN, normalized, "Khong tim thay id trong prompt.");
            Product product = productTools.getProductById(id);
            return new AiResult("Da lay thong tin san pham tu DB.", product);
        }

        throw new IllegalArgumentException("Chi ho tro 2 tac vu: get product va update product.");
    }

    private AiResult handleUpdate(String rawPrompt, String normalizedPrompt) {
        Long id = extractRequiredLong(UPDATE_ID_PATTERN, normalizedPrompt, "Khong tim thay id de cap nhat.");
        String name = extractRequiredText(NAME_PATTERN, rawPrompt, "Khong tim thay ten san pham.");
        Double price = extractRequiredPrice(rawPrompt);
        String status = extractRequiredText(STATUS_PATTERN, normalizedPrompt, "Khong tim thay trang thai.");

        Product product = productTools.updateProduct(id, name, price, status.toUpperCase(Locale.ROOT));
        return new AiResult("Da cap nhat san pham theo du lieu DB.", product);
    }

    private static Long extractRequiredLong(Pattern pattern, String input, String errorMessage) {
        Matcher matcher = pattern.matcher(input);
        if (!matcher.find()) {
            throw new IllegalArgumentException(errorMessage);
        }
        return Long.parseLong(matcher.group(1));
    }

    private static String extractRequiredText(Pattern pattern, String input, String errorMessage) {
        Matcher matcher = pattern.matcher(input);
        if (!matcher.find()) {
            throw new IllegalArgumentException(errorMessage);
        }
        return matcher.group(1).trim();
    }

    private static Double extractRequiredPrice(String prompt) {
        Matcher matcher = PRICE_PATTERN.matcher(normalizeVietnamese(prompt).toLowerCase(Locale.ROOT));
        if (!matcher.find()) {
            throw new IllegalArgumentException("Khong tim thay gia san pham.");
        }
        String raw = matcher.group(1).replace(".", "").replace(",", "");
        return Double.parseDouble(raw);
    }

    private static String normalizeVietnamese(String input) {
        return input
                .replace('á', 'a').replace('à', 'a').replace('ả', 'a').replace('ã', 'a').replace('ạ', 'a')
                .replace('ă', 'a').replace('ắ', 'a').replace('ằ', 'a').replace('ẳ', 'a').replace('ẵ', 'a').replace('ặ', 'a')
                .replace('â', 'a').replace('ấ', 'a').replace('ầ', 'a').replace('ẩ', 'a').replace('ẫ', 'a').replace('ậ', 'a')
                .replace('é', 'e').replace('è', 'e').replace('ẻ', 'e').replace('ẽ', 'e').replace('ẹ', 'e')
                .replace('ê', 'e').replace('ế', 'e').replace('ề', 'e').replace('ể', 'e').replace('ễ', 'e').replace('ệ', 'e')
                .replace('í', 'i').replace('ì', 'i').replace('ỉ', 'i').replace('ĩ', 'i').replace('ị', 'i')
                .replace('ó', 'o').replace('ò', 'o').replace('ỏ', 'o').replace('õ', 'o').replace('ọ', 'o')
                .replace('ô', 'o').replace('ố', 'o').replace('ồ', 'o').replace('ổ', 'o').replace('ỗ', 'o').replace('ộ', 'o')
                .replace('ơ', 'o').replace('ớ', 'o').replace('ờ', 'o').replace('ở', 'o').replace('ỡ', 'o').replace('ợ', 'o')
                .replace('ú', 'u').replace('ù', 'u').replace('ủ', 'u').replace('ũ', 'u').replace('ụ', 'u')
                .replace('ư', 'u').replace('ứ', 'u').replace('ừ', 'u').replace('ử', 'u').replace('ữ', 'u').replace('ự', 'u')
                .replace('ý', 'y').replace('ỳ', 'y').replace('ỷ', 'y').replace('ỹ', 'y').replace('ỵ', 'y')
                .replace('đ', 'd')
                .replace('Á', 'A').replace('À', 'A').replace('Ả', 'A').replace('Ã', 'A').replace('Ạ', 'A')
                .replace('Ă', 'A').replace('Ắ', 'A').replace('Ằ', 'A').replace('Ẳ', 'A').replace('Ẵ', 'A').replace('Ặ', 'A')
                .replace('Â', 'A').replace('Ấ', 'A').replace('Ầ', 'A').replace('Ẩ', 'A').replace('Ẫ', 'A').replace('Ậ', 'A')
                .replace('É', 'E').replace('È', 'E').replace('Ẻ', 'E').replace('Ẽ', 'E').replace('Ẹ', 'E')
                .replace('Ê', 'E').replace('Ế', 'E').replace('Ề', 'E').replace('Ể', 'E').replace('Ễ', 'E').replace('Ệ', 'E')
                .replace('Í', 'I').replace('Ì', 'I').replace('Ỉ', 'I').replace('Ĩ', 'I').replace('Ị', 'I')
                .replace('Ó', 'O').replace('Ò', 'O').replace('Ỏ', 'O').replace('Õ', 'O').replace('Ọ', 'O')
                .replace('Ô', 'O').replace('Ố', 'O').replace('Ồ', 'O').replace('Ổ', 'O').replace('Ỗ', 'O').replace('Ộ', 'O')
                .replace('Ơ', 'O').replace('Ớ', 'O').replace('Ờ', 'O').replace('Ở', 'O').replace('Ỡ', 'O').replace('Ợ', 'O')
                .replace('Ú', 'U').replace('Ù', 'U').replace('Ủ', 'U').replace('Ũ', 'U').replace('Ụ', 'U')
                .replace('Ư', 'U').replace('Ứ', 'U').replace('Ừ', 'U').replace('Ử', 'U').replace('Ữ', 'U').replace('Ự', 'U')
                .replace('Ý', 'Y').replace('Ỳ', 'Y').replace('Ỷ', 'Y').replace('Ỹ', 'Y').replace('Ỵ', 'Y')
                .replace('Đ', 'D');
    }

    public record AiResult(String answer, Product product) {
    }
}
