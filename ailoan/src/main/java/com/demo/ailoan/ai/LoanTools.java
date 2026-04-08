package com.demo.ailoan.ai;

import com.demo.ailoan.entity.Loan;
import com.demo.ailoan.entity.Scheme;
import com.demo.ailoan.service.AuditLogService;
import com.demo.ailoan.service.LoanService;
import com.demo.ailoan.service.SchemeService;
import com.demo.ailoan.util.SchemeNameUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LoanTools {

    private final LoanService loanService;
    private final SchemeService schemeService;
    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;
    private final AiCommandContext commandContext;

    @Tool(description = "Tìm tất cả khoản vay thuộc một scheme (A, B hoặc C).")
    public String findLoansByScheme(@ToolParam(description = "Mã scheme: A, B hoặc C") String schemeType) {
        Map<String, Object> params = Map.of("schemeType", schemeType);
        try {
            String normalizedScheme = SchemeNameUtil.normalize(schemeType);
            List<Loan> loans = loanService.findBySchemeName(normalizedScheme);
            List<Map<String, Object>> sample =
                    loans.stream()
                            .limit(5)
                            .map(l -> Map.<String, Object>of(
                                    "id", l.getId(),
                                    "customerName", l.getCustomerName(),
                                    "amount", l.getAmount()))
                            .toList();
            ToolResult toolResult = new ToolResult(
                    "Tìm thấy " + loans.size() + " khoản vay thuộc scheme " + normalizedScheme + ".",
                    Map.of("scheme", normalizedScheme, "count", loans.size(), "sampleLoans", sample),
                    loans.size());
            String result = writeJson(toolResult);
            audit("findLoansByScheme", params, result);
            commandContext.recordToolResult("findLoansByScheme", toolResult);
            return result;
        } catch (Exception e) {
            return toolError("findLoansByScheme", e);
        }
    }

    @Tool(description = "Đếm số khoản vay theo scheme (A, B hoặc C). Dùng tool này khi chỉ cần số lượng.")
    public String countLoansByScheme(@ToolParam(description = "Mã scheme: A, B hoặc C") String schemeType) {
        Map<String, Object> params = Map.of("schemeType", schemeType);
        try {
            String normalizedScheme = SchemeNameUtil.normalize(schemeType);
            int count = loanService.countBySchemeName(normalizedScheme);
            ToolResult toolResult = new ToolResult(
                    "Scheme " + normalizedScheme + " có " + count + " khoản vay.",
                    Map.of("scheme", normalizedScheme, "count", count),
                    count);
            String result = writeJson(toolResult);
            audit("countLoansByScheme", params, result);
            commandContext.recordToolResult("countLoansByScheme", toolResult);
            return result;
        } catch (Exception e) {
            return toolError("countLoansByScheme", e);
        }
    }

    @Tool(description = "Cập nhật toàn bộ 4 trường maxAmount, interestRate, tenorMonths, serviceFee của một scheme. Khi chỉ đổi một trường, hãy gọi listAllSchemes trước để giữ nguyên các trường khác.")
    public String updateSchemeConfig(
            @ToolParam(description = "Scheme A, B hoặc C") String schemeType,
            @ToolParam(description = "Giá trị maxAmount") String maxAmount,
            @ToolParam(description = "Giá trị interestRate") String interestRate,
            @ToolParam(description = "Giá trị tenorMonths") String tenorMonths,
            @ToolParam(description = "Giá trị serviceFee") String serviceFee) {
        Map<String, Object> params = Map.of("schemeType", schemeType,
                "maxAmount", maxAmount,
                "interestRate", interestRate,
                "tenorMonths", tenorMonths,
                "serviceFee", serviceFee);
        try {
            Scheme updated = schemeService.updateConfig(schemeType, maxAmount, interestRate, tenorMonths, serviceFee);
            ToolResult toolResult = new ToolResult(
                    "Đã cập nhật config cho scheme " + updated.getName() + ".",
                    schemeMap(updated),
                    1);
            String result = writeJson(toolResult);
            audit("updateSchemeConfig", params, result);
            commandContext.recordToolResult("updateSchemeConfig", toolResult);
            return result;
        } catch (Exception e) {
            return toolError("updateSchemeConfig", e);
        }
    }

    @Tool(description = "Liệt kê tất cả scheme và cấu hình maxAmount, interestRate, tenorMonths, serviceFee hiện tại.")
    public String listAllSchemes() {
        Map<String, Object> params = Map.of();
        try {
            List<Scheme> schemes = schemeService.findAll();
            List<Map<String, Object>> list =
                    schemes.stream().map(this::schemeMap).toList();
            ToolResult toolResult = new ToolResult(
                    "Đã lấy danh sách " + schemes.size() + " scheme.",
                    Map.of("schemes", list),
                    schemes.size());
            String result = writeJson(toolResult);
            audit("listAllSchemes", params, result);
            commandContext.recordToolResult("listAllSchemes", toolResult);
            return result;
        } catch (Exception e) {
            return toolError("listAllSchemes", e);
        }
    }

    @Tool(description = "Sao chép toàn bộ config info từ scheme nguồn sang scheme đích.")
    public String copySchemeConfig(
            @ToolParam(description = "Scheme nguồn A/B/C") String fromScheme,
            @ToolParam(description = "Scheme đích A/B/C") String toScheme) {
        Map<String, Object> params = Map.of("fromScheme", fromScheme, "toScheme", toScheme);
        try {
            Scheme updated = schemeService.copyConfig(fromScheme, toScheme);
            ToolResult toolResult = new ToolResult(
                    "Đã copy config sang scheme " + updated.getName() + ".",
                    schemeMap(updated),
                    1);
            String result = writeJson(toolResult);
            audit("copySchemeConfig", params, result);
            commandContext.recordToolResult("copySchemeConfig", toolResult);
            return result;
        } catch (Exception e) {
            return toolError("copySchemeConfig", e);
        }
    }

    @Tool(description = "Reset toàn bộ maxAmount, interestRate, tenorMonths, serviceFee của scheme về rỗng.")
    public String resetSchemeConfig(@ToolParam(description = "Scheme A/B/C") String schemeType) {
        Map<String, Object> params = Map.of("schemeType", schemeType);
        try {
            Scheme updated = schemeService.resetConfig(schemeType);
            ToolResult toolResult = new ToolResult(
                    "Đã reset config scheme " + updated.getName() + ".",
                    schemeMap(updated),
                    1);
            String result = writeJson(toolResult);
            audit("resetSchemeConfig", params, result);
            commandContext.recordToolResult("resetSchemeConfig", toolResult);
            return result;
        } catch (Exception e) {
            return toolError("resetSchemeConfig", e);
        }
    }

    @Tool(description = "Cập nhật amount cho loans theo bộ lọc: scheme, customerName, loanId. Có thể truyền một hoặc nhiều điều kiện lọc.")
    public String updateLoanAmountByFilters(
            @ToolParam(description = "Scheme A, B hoặc C (optional)") String schemeType,
            @ToolParam(description = "Tên khách hàng hoặc một phần tên (optional)") String customerName,
            @ToolParam(description = "ID khoản vay (optional)") Long loanId,
            @ToolParam(description = "Số tiền mới") String amount) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("schemeType", schemeType);
        params.put("customerName", customerName);
        params.put("loanId", loanId);
        params.put("amount", amount);
        try {
            BigDecimal parsedAmount = new BigDecimal(amount.trim());
            int affected = loanService.updateAmountByFilters(schemeType, customerName, loanId, parsedAmount);
            ToolResult toolResult = new ToolResult(
                    "Đã cập nhật amount = " + parsedAmount + " cho " + affected + " khoản vay.",
                    Map.of(
                            "scheme", schemeType,
                            "customerName", customerName,
                            "loanId", loanId,
                            "amount", parsedAmount,
                            "affected", affected),
                    affected);
            String result = writeJson(toolResult);
            audit("updateLoanAmountByFilters", params, result);
            commandContext.recordToolResult("updateLoanAmountByFilters", toolResult);
            return result;
        } catch (Exception e) {
            return toolError("updateLoanAmountByFilters", e);
        }
    }

    private Map<String, Object> schemeMap(Scheme s) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", s.getId());
        m.put("name", s.getName());
        m.put("maxAmount", s.getMaxAmount());
        m.put("interestRate", s.getInterestRate());
        m.put("tenorMonths", s.getTenorMonths());
        m.put("serviceFee", s.getServiceFee());
        m.put("updatedAt", s.getUpdatedAt().toString());
        return m;
    }

    private void audit(String tool, Map<String, Object> params, String result) throws JsonProcessingException {
        auditLogService.save(
                commandContext.getAdminUser(),
                commandContext.getPrompt(),
                tool,
                writeJson(params),
                result);
    }

    private String toolError(String tool, Exception e) {
        String msg = e instanceof EntityNotFoundException ? e.getMessage() : e.getClass().getSimpleName() + ": " + e.getMessage();
        commandContext.recordTool(tool, 0);
        try {
            return writeJson(Map.of("error", msg));
        } catch (JsonProcessingException ex) {
            return "{\"error\":\"" + msg.replace("\"", "\\\"") + "\"}";
        }
    }

    private String writeJson(Object value) throws JsonProcessingException {
        return objectMapper.writeValueAsString(value);
    }
}
