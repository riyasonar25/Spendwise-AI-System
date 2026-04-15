package com.spendwise.spendwise_ai.repository;

import com.spendwise.spendwise_ai.dto.CategorySummaryDTO;
import com.spendwise.spendwise_ai.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findByCategoryIgnoreCaseAndYearAndMonth(
            String category,
            int year,
            int month
    );
    @Query("""
    SELECT new com.spendwise.spendwise_ai.dto.CategorySummaryDTO(
        e.category,
        SUM(e.amount)
    )
    FROM Expense e
    WHERE YEAR(e.date) = :year
      AND MONTH(e.date) = :month
    GROUP BY e.category
""")
List<CategorySummaryDTO> getCategorySummaryByMonth(
        @Param("year") int year,
        @Param("month") int month
);
List<Budget> findByYearAndMonthAndUserEmail(
        int year,
        int month,
        String email
);
Optional<Budget> findByCategoryIgnoreCaseAndYearAndMonthAndUserEmail(
        String category,
        int year,
        int month,
        String email
);
List<Budget> findByYearAndMonth(int year, int month);
List<Budget> findByUserEmail(String email);



}
