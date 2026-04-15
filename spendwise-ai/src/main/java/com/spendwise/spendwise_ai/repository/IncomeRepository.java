package com.spendwise.spendwise_ai.repository;

import com.spendwise.spendwise_ai.model.Income;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findByUserEmail(String email);
    // ✅ Monthly income of logged-in user
    List<Income> findByUserEmailAndDateBetween(
            String email,
            LocalDate startDate,
            LocalDate endDate
        );

}