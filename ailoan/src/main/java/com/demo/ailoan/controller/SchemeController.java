package com.demo.ailoan.controller;

import com.demo.ailoan.entity.Scheme;
import com.demo.ailoan.service.SchemeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SchemeController {

    private final SchemeService schemeService;

    @GetMapping("/schemes")
    public List<Scheme> listSchemes() {
        return schemeService.findAll();
    }
}
