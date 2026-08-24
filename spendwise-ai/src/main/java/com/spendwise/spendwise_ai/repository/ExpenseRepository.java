package com.spendwise.spendwise_ai.repository;

import com.spendwise.spendwise_ai.dto.CategorySummaryDTO;
import com.spendwise.spendwise_ai.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // ✅ All expenses of logged-in user
    List<Expense> findByUserEmail(String email);

    // ✅ Get expense by id + user (Security safe)
    Optional<Expense> findByIdAndUserEmail(Long id, String email);

    // ✅ Total by date
    @Query("""
        SELECT COALESCE(SUM(e.amount), 0)
        FROM Expense e
        WHERE e.date = :date
        AND e.user.email = :email
    """)
    Double getTotalExpenseByDate(
            @Param("date") LocalDate date,
            @Param("email") String email
    );

    // ✅ Total between dates
    @Query("""
        SELECT COALESCE(SUM(e.amount), 0)
        FROM Expense e
        WHERE e.date BETWEEN :start AND :end
        AND e.user.email = :email
    """)
    Double getTotalExpenseBetweenDates(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("email") String email
    );

    // ✅ Category summary between dates
    @Query("""
        SELECT new com.spendwise.spendwise_ai.dto.CategorySummaryDTO(
            e.category,
            SUM(e.amount)
        )
        FROM Expense e
        WHERE e.date BETWEEN :start AND :end
        AND e.user.email = :email
        GROUP BY e.category
    """)
    List<CategorySummaryDTO> getCategorySummaryBetweenDates(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("email") String email
    );

    // ✅ Monthly category summary
    @Query("""
        SELECT new com.spendwise.spendwise_ai.dto.CategorySummaryDTO(
            e.category,
            SUM(e.amount)
        )
        FROM Expense e
        WHERE YEAR(e.date) = :year
        AND MONTH(e.date) = :month
        AND e.user.email = :email
        GROUP BY e.category
    """)
    List<CategorySummaryDTO> getCategorySummaryByMonth(
            @Param("year") int year,
            @Param("month") int month,
            @Param("email") String email
    );

    // ✅ Monthly expense list
    @Query("""
        SELECT e FROM Expense e
        WHERE YEAR(e.date) = :year
        AND MONTH(e.date) = :month
        AND e.user.email = :email
    """)
    List<Expense> findByYearAndMonthAndUserEmail(
            @Param("year") int year,
            @Param("month") int month,
            @Param("email") String email
    );
      List<Expense> findByUserEmailAndDateBetween(String email, LocalDate startDate, LocalDate endDate);
    @Query("""
    SELECT e FROM Expense e
    WHERE e.user.email = :email
    AND e.date = :date
""")
List<Expense> getExpensesByDate(
        @Param("email") String email,
        @Param("date") LocalDate date
);
      List<Expense> findByUserEmailAndDate(String email, LocalDate date);

}