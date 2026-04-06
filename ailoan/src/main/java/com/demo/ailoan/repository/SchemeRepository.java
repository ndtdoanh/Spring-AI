package com.demo.ailoan.repository;

import com.demo.ailoan.entity.Scheme;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchemeRepository extends JpaRepository<Scheme, Long> {

    Optional<Scheme> findByNameIgnoreCase(String name);
}
