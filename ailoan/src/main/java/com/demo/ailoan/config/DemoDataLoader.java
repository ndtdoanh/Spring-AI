package com.demo.ailoan.config;

import com.demo.ailoan.entity.Loan;
import com.demo.ailoan.entity.Scheme;
import com.demo.ailoan.repository.LoanRepository;
import com.demo.ailoan.repository.SchemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class DemoDataLoader implements ApplicationRunner {

    private final SchemeRepository schemeRepository;
    private final LoanRepository loanRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (schemeRepository.count() > 0) {
            return;
        }
        Instant t0 = Instant.parse("2024-06-01T00:00:00Z");
        Scheme a =
                schemeRepository.save(
                        Scheme.builder()
                                .name("A")
                                .infoA("100")
                                .infoB("200")
                                .infoC("300")
                                .infoD("400")
                                .updatedAt(t0)
                                .build());
        Scheme b =
                schemeRepository.save(
                        Scheme.builder()
                                .name("B")
                                .infoA("110")
                                .infoB("456")
                                .infoC("310")
                                .infoD("410")
                                .updatedAt(t0)
                                .build());
        Scheme c =
                schemeRepository.save(
                        Scheme.builder()
                                .name("C")
                                .infoA("120")
                                .infoB("220")
                                .infoC("320")
                                .infoD("420")
                                .updatedAt(t0)
                                .build());

        int customer = 1;
        for (Scheme scheme : new Scheme[]{a, b, c}) {
            int n = scheme.getName().equals("C") ? 6 : 7;
            for (int i = 0; i < n; i++) {
                loanRepository.save(
                        Loan.builder()
                                .scheme(scheme)
                                .customerName("Khách hàng " + customer)
                                .amount(BigDecimal.valueOf(500_000_000L + customer * 1_250_000L))
                                .status("ACTIVE")
                                .createdAt(t0.plusSeconds(customer))
                                .build());
                customer++;
            }
        }
    }
}
