package com.demo.ailoan.repository;

import com.demo.ailoan.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query("select distinct l from Loan l join fetch l.scheme order by l.id")
    List<Loan> findAllFetched();

    @Query(
            "select distinct l from Loan l join fetch l.scheme where upper(l.scheme.name) = upper(:schemeName) order by"
                    + " l.id")
    List<Loan> findBySchemeNameFetched(@Param("schemeName") String schemeName);

    @Query("select count(l) from Loan l where upper(l.scheme.name) = upper(:schemeName)")
    long countBySchemeName(@Param("schemeName") String schemeName);
}
