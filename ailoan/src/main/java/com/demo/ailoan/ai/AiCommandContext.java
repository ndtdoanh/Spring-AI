package com.demo.ailoan.ai;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Component
@RequestScope
public class AiCommandContext {

    private String adminUser = "admin";
    private String prompt = "";
    private final List<String> toolNames = new ArrayList<>();
    private int lastAffectedCount;
    private String lastToolMessage = "";

    public void begin(String adminUser, String prompt) {
        this.adminUser = adminUser != null && !adminUser.isBlank() ? adminUser : "admin";
        this.prompt = prompt != null ? prompt : "";
        toolNames.clear();
        lastAffectedCount = 0;
        lastToolMessage = "";
    }

    public void recordTool(String toolName, int affectedCount) {
        toolNames.add(toolName);
        lastAffectedCount = affectedCount;
    }

    public void recordToolResult(String toolName, ToolResult result) {
        recordTool(toolName, result.affectedCount());
        lastToolMessage = result.message();
    }

    public String getToolCalledSummary() {
        return String.join(", ", toolNames);
    }
}
