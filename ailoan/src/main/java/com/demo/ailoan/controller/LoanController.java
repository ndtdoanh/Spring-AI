package com.demo.ailoan.controller;

import com.demo.ailoan.dto.LoanResponse;
import com.demo.ailoan.service.LoanService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @GetMapping("/loans")
    public List<LoanResponse> listLoans(@RequestParam(value = "scheme", required = false) String scheme) {
        return loanService.findBySchemeName(scheme).stream().map(LoanResponse::from).toList();
    }
}
