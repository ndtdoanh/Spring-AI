package com.demo.ailoan.service;

import com.demo.ailoan.entity.Scheme;
import com.demo.ailoan.repository.SchemeRepository;
import com.demo.ailoan.util.SchemeNameUtil;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SchemeService {

    private final SchemeRepository schemeRepository;

    public List<Scheme> findAll() {
        return schemeRepository.findAll();
    }

    public Scheme requireByName(String schemeType) {
        String code = SchemeNameUtil.normalize(schemeType);
        return schemeRepository
                .findByNameIgnoreCase(code)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy scheme: " + schemeType));
    }

    @Transactional
    public Scheme updateConfig(String schemeType, String maxAmount, String interestRate, String tenorMonths, String serviceFee) {
        Scheme s = requireByName(schemeType);
        s.setMaxAmount(maxAmount);
        s.setInterestRate(interestRate);
        s.setTenorMonths(tenorMonths);
        s.setServiceFee(serviceFee);
        s.setUpdatedAt(Instant.now());
        return schemeRepository.save(s);
    }

    @Transactional
    public Scheme copyConfig(String fromScheme, String toScheme) {
        Scheme src = requireByName(fromScheme);
        Scheme dst = requireByName(toScheme);
        dst.setMaxAmount(src.getMaxAmount());
        dst.setInterestRate(src.getInterestRate());
        dst.setTenorMonths(src.getTenorMonths());
        dst.setServiceFee(src.getServiceFee());
        dst.setUpdatedAt(Instant.now());
        return schemeRepository.save(dst);
    }

    @Transactional
    public Scheme resetConfig(String schemeType) {
        return updateConfig(schemeType, "", "", "", "");
    }
}
