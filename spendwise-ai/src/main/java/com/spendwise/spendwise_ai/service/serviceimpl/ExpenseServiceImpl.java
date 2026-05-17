package com.spendwise.spendwise_ai.service.serviceimpl;

import com.spendwise.spendwise_ai.dto.CategorySummaryDTO;
import com.spendwise.spendwise_ai.dto.ExpenseRequestDTO;
import com.spendwise.spendwise_ai.dto.ExpenseResponseDTO;
import com.spendwise.spendwise_ai.model.Expense;
import com.spendwise.spendwise_ai.model.User;
import com.spendwise.spendwise_ai.repository.ExpenseRepository;
import com.spendwise.spendwise_ai.repository.UserRepository;
import com.spendwise.spendwise_ai.service.ExpenseService;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import java.util.stream.Collectors;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository,
                              UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    // ✅ CREATE
@Override
public ExpenseResponseDTO createExpense(ExpenseRequestDTO requestDTO, String email) {

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    Expense expense = new Expense();

    expense.setTitle(requestDTO.getTitle());
    expense.setAmount(requestDTO.getAmount());
    expense.setCategory(requestDTO.getCategory());
    expense.setDate(requestDTO.getDate());

    // ✅ ADD THIS
    expense.setMonth(requestDTO.getDate().getMonthValue());
    expense.setYear(requestDTO.getDate().getYear());

    expense.setUser(user);

    Expense saved = expenseRepository.save(expense);

    return new ExpenseResponseDTO(
            saved.getId(),
            saved.getTitle(),
            saved.getAmount(),
            saved.getCategory(),
            saved.getDate()
    );
 }

 // ✅ GET EXPENSES BY DATE
@Override
public List<Expense> getExpensesByDate(LocalDate date, String email) {

    return expenseRepository.findByUserEmailAndDate(email, date);
}
    // ✅ GET BY ID
    @Override
    public Expense getExpenseById(Long id, String email) {

        return expenseRepository.findByIdAndUserEmail(id, email)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
    }

    // ✅ GET ALL
    @Override
    public List<Expense> getAllExpenses(String email) {

        return expenseRepository.findByUserEmail(email);
    }

    // ✅ UPDATE
    @Override
    public Expense updateExpense(Long id, Expense updatedExpense, String email) {

        Expense existing = getExpenseById(id, email);

        existing.setTitle(updatedExpense.getTitle());
        existing.setAmount(updatedExpense.getAmount());
        existing.setCategory(updatedExpense.getCategory());
        existing.setDate(updatedExpense.getDate());

        return expenseRepository.save(existing);
    }

    // ✅ DELETE
    @Override
    public void deleteExpense(Long id, String email) {

        Expense expense = getExpenseById(id, email);
        expenseRepository.delete(expense);
    }

    // ✅ TOTAL BY DATE
    @Override
    public Double getTotalExpenseByDate(LocalDate date, String email) {

        return expenseRepository.findByUserEmail(email)
                .stream()
                .filter(exp -> exp.getDate().equals(date))
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    // ✅ TOTAL BETWEEN DATES
    @Override
    public Double getTotalExpenseBetweenDates(LocalDate start,
                                              LocalDate end,
                                              String email) {

        return expenseRepository.findByUserEmail(email)
                .stream()
                .filter(exp -> !exp.getDate().isBefore(start)
                        && !exp.getDate().isAfter(end))
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    // ✅ CATEGORY SUMMARY BETWEEN DATES
    @Override
    public List<CategorySummaryDTO> getCategorySummaryBetweenDates(
            LocalDate start,
            LocalDate end,
            String email) {

        return expenseRepository.findByUserEmail(email)
                .stream()
                .filter(exp -> !exp.getDate().isBefore(start)
                        && !exp.getDate().isAfter(end))
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)
                ))
                .entrySet()
                .stream()
                .map(entry -> new CategorySummaryDTO(
                        entry.getKey(),
                        entry.getValue()
                ))
                .collect(Collectors.toList());
    }

    // ✅ MONTHLY CATEGORY SUMMARY
    @Override
    public List<CategorySummaryDTO> getCategorySummaryByMonth(
            int year,
            int month,
            String email) {

        return expenseRepository.findByUserEmail(email)
                .stream()
                .filter(exp -> exp.getDate().getYear() == year
                        && exp.getDate().getMonthValue() == month)
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)
                ))
                .entrySet()
                .stream()
                .map(entry -> new CategorySummaryDTO(
                        entry.getKey(),
                        entry.getValue()
                ))
                .collect(Collectors.toList());
    }
}
