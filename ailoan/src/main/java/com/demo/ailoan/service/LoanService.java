package com.demo.ailoan.service;

import com.demo.ailoan.entity.Loan;
import com.demo.ailoan.repository.LoanRepository;
import com.demo.ailoan.util.SchemeNameUtil;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;

    @Transactional(readOnly = true)
    public List<Loan> findAll() {
        return loanRepository.findAllFetched();
    }

    @Transactional(readOnly = true)
    public List<Loan> findBySchemeName(String schemeName) {
        if (schemeName == null || schemeName.isBlank()) {
            return loanRepository.findAllFetched();
        }
        return loanRepository.findBySchemeNameFetched(schemeName.trim());
    }

    @Transactional(readOnly = true)
    public int countBySchemeName(String schemeName) {
        if (schemeName == null || schemeName.isBlank()) {
            return findAll().size();
        }
        return Math.toIntExact(loanRepository.countBySchemeName(schemeName.trim()));
    }

    @Transactional
    public int updateAmountBySchemeName(String schemeName, BigDecimal amount) {
        String normalizedScheme = SchemeNameUtil.normalize(schemeName);
        return loanRepository.updateAmountBySchemeName(normalizedScheme, amount);
    }

    @Transactional
    public int updateAmountByFilters(String schemeName, String customerName, Long loanId, BigDecimal amount) {
        List<Loan> loans = loanRepository.findAllFetched();
        String normalizedScheme = (schemeName == null || schemeName.isBlank()) ? null : SchemeNameUtil.normalize(schemeName);
        String customerNeedle = (customerName == null || customerName.isBlank()) ? null : customerName.trim().toLowerCase(Locale.ROOT);

        List<Loan> matched = loans.stream()
                .filter(l -> normalizedScheme == null || normalizedScheme.equalsIgnoreCase(l.getScheme().getName()))
                .filter(l -> loanId == null || loanId.equals(l.getId()))
                .filter(l -> customerNeedle == null
                        || l.getCustomerName().toLowerCase(Locale.ROOT).contains(customerNeedle))
                .toList();

        matched.forEach(l -> l.setAmount(amount));
        loanRepository.saveAll(matched);
        return matched.size();
    }
}
