package com.spendwise.spendwise_ai.service;

import com.spendwise.spendwise_ai.dto.CategorySummaryDTO;
import com.spendwise.spendwise_ai.dto.ExpenseRequestDTO;
import com.spendwise.spendwise_ai.dto.ExpenseResponseDTO;
import com.spendwise.spendwise_ai.model.Expense;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {

    ExpenseResponseDTO createExpense(ExpenseRequestDTO requestDTO, String email);

    Expense getExpenseById(Long id, String email);

    List<Expense> getAllExpenses(String email);

    Expense updateExpense(Long id, Expense expense, String email);

    void deleteExpense(Long id, String email);

    Double getTotalExpenseByDate(LocalDate date, String email);

    Double getTotalExpenseBetweenDates(LocalDate start, LocalDate end, String email);

    List<CategorySummaryDTO> getCategorySummaryBetweenDates(LocalDate start, LocalDate end, String email);

    List<CategorySummaryDTO> getCategorySummaryByMonth(int year, int month, String email);
    List<Expense> getExpensesByDate(LocalDate date, String email);
}
