package com.demo.ailoan.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiOrchestrator {

    private final ChatClient chatClient;
    private final LoanTools loanTools;
    private final ObjectMapper objectMapper;

    public OrchestratorResult runConversationTurn(String command) {
        // Bước 0: Normalize câu lệnh — map từ tự nhiên → field chuẩn
        // Ví dụ: "số tiền" → "infoA", "thành 100" → "= 100"
        String normalizedCommand = CommandNormalizer.normalize(command);
        log.debug("Normalized command: '{}' → '{}'", command, normalizedCommand);

        // Bước 1: LLM classify — 1 call duy nhất, output JSON ~20 token
        String raw = chatClient
                .prompt()
                .user(normalizedCommand)
                .call()
                .content();

        log.debug("LLM classify raw output: {}", raw);

        // Bước 2: Parse intent
        IntentDto intent = parseIntent(raw, normalizedCommand);
        log.debug("Parsed intent: {}", intent);

        // Bước 3: Java dispatch
        return dispatch(intent, command); // truyền command gốc cho error message
    }

    // ---------------------------------------------------------------
    // Parse
    // ---------------------------------------------------------------

    private IntentDto parseIntent(String raw, String normalizedCommand) {
        try {
            String cleaned = extractJson(raw);
            return objectMapper.readValue(cleaned, IntentDto.class);
        } catch (Exception e) {
            log.warn("Không parse được intent JSON: '{}', dùng heuristic. Error: {}", raw, e.getMessage());
            return heuristicFallback(normalizedCommand);
        }
    }

    private String extractJson(String raw) {
        if (raw == null) return "{}";
        String s = raw.trim();
        int start = s.indexOf('{');
        int end = s.lastIndexOf('}');
        if (start >= 0 && end > start) return s.substring(start, end + 1);
        return "{}";
    }

    private IntentDto heuristicFallback(String cmd) {
        String c = cmd.toLowerCase();
        String scheme = extractSchemeHeuristic(c);

        if (c.contains("đếm") || c.contains("count") || c.contains("bao nhiêu"))
            return new IntentDto("count", scheme, null, null, null, null, null, null);
        if (c.contains("tìm") || c.contains("khoản vay"))
            return new IntentDto("find", scheme, null, null, null, null, null, null);
        if (c.contains("liệt kê") || c.contains("tất cả scheme"))
            return new IntentDto("list", null, null, null, null, null, null, null);
        if (c.contains("copy") || c.contains("sao chép"))
            return new IntentDto("copy", null, null, null, null, null, null, null);
        if (c.contains("reset") || c.contains("xóa config"))
            return new IntentDto("reset", scheme, null, null, null, null, null, null);
        if (c.contains("cập nhật") || c.contains("update") || c.contains("đổi") || c.contains("sửa"))
            return new IntentDto("update", scheme, null, null, null, null, null, null);
        return new IntentDto("unknown", null, null, null, null, null, null, null);
    }

    private String extractSchemeHeuristic(String cmd) {
        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("scheme\\s*[:\\-]?\\s*([abc])", java.util.regex.Pattern.CASE_INSENSITIVE)
                .matcher(cmd);
        if (m.find()) return m.group(1).toUpperCase();
        return null;
    }

    // ---------------------------------------------------------------
    // Dispatch
    // ---------------------------------------------------------------

    private OrchestratorResult dispatch(IntentDto intent, String originalCommand) {
        String scheme = intent.schemeNormalized();

        return switch (intent.intent() != null ? intent.intent() : "unknown") {
            case "find" -> {
                if (scheme == null)
                    yield OrchestratorResult.error("Vui lòng chỉ rõ scheme (A, B hoặc C).");
                yield OrchestratorResult.of("findLoansByScheme", loanTools.findLoansByScheme(scheme));
            }
            case "count" -> {
                if (scheme == null)
                    yield OrchestratorResult.error("Vui lòng chỉ rõ scheme (A, B hoặc C).");
                yield OrchestratorResult.of("countLoansByScheme", loanTools.countLoansByScheme(scheme));
            }
            case "list" -> OrchestratorResult.of("listAllSchemes", loanTools.listAllSchemes());

            case "copy" -> {
                String from = intent.fromNormalized();
                String to = intent.toNormalized();
                if (from == null || to == null)
                    yield OrchestratorResult.error(
                            "Vui lòng chỉ rõ scheme nguồn và đích. Ví dụ: \"Copy config scheme A sang scheme B\"");
                yield OrchestratorResult.of("copySchemeConfig", loanTools.copySchemeConfig(from, to));
            }

            case "reset" -> {
                if (scheme == null)
                    yield OrchestratorResult.error("Vui lòng chỉ rõ scheme cần reset (A, B hoặc C).");
                yield OrchestratorResult.of("resetSchemeConfig", loanTools.resetSchemeConfig(scheme));
            }

            case "update" -> handleUpdate(intent, originalCommand);

            default -> OrchestratorResult.error(
                    "Không hiểu yêu cầu: \"" + originalCommand + "\". Vui lòng thử lại rõ hơn.");
        };
    }

    private OrchestratorResult handleUpdate(IntentDto intent, String originalCommand) {
        String scheme = intent.schemeNormalized();
        if (scheme == null)
            return OrchestratorResult.error("Vui lòng chỉ rõ scheme cần cập nhật (A, B hoặc C).");

        String infoA = intent.infoANormalized();
        String infoB = intent.infoBNormalized();
        String infoC = intent.infoCNormalized();
        String infoD = intent.infoDNormalized();

        boolean anyProvided = !infoA.isBlank() || !infoB.isBlank()
                || !infoC.isBlank() || !infoD.isBlank();

        if (!anyProvided) {
            return OrchestratorResult.error(
                    "Vui lòng chỉ rõ trường và giá trị cần cập nhật.\n" +
                            "Ví dụ: \"Cập nhật infoA = 500 cho scheme A\"\n" +
                            "Hoặc dùng tên trường: \"Cập nhật số tiền = 500 cho scheme A\"");
        }

        // Lấy current values để merge — tránh xóa trắng các trường không đổi
        String currentJson = loanTools.listAllSchemes();
        try {
            com.fasterxml.jackson.databind.JsonNode root = objectMapper.readTree(currentJson);
            com.fasterxml.jackson.databind.JsonNode schemes = root.path("uiPayload").path("schemes");
            for (com.fasterxml.jackson.databind.JsonNode s : schemes) {
                if (scheme.equalsIgnoreCase(s.path("name").asText())) {
                    if (infoA.isBlank()) infoA = s.path("infoA").asText("");
                    if (infoB.isBlank()) infoB = s.path("infoB").asText("");
                    if (infoC.isBlank()) infoC = s.path("infoC").asText("");
                    if (infoD.isBlank()) infoD = s.path("infoD").asText("");
                    break;
                }
            }
        } catch (Exception e) {
            log.warn("Không đọc được current scheme values: {}", e.getMessage());
        }

        return OrchestratorResult.of("updateSchemeConfig",
                loanTools.updateSchemeConfig(scheme, infoA, infoB, infoC, infoD));
    }
}