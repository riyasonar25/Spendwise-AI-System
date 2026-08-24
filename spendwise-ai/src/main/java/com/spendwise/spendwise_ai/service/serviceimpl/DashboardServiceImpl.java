package com.spendwise.spendwise_ai.service.serviceimpl;

import com.spendwise.spendwise_ai.dto.*;
import com.spendwise.spendwise_ai.model.Budget;
import com.spendwise.spendwise_ai.model.Expense;
import com.spendwise.spendwise_ai.model.Income;
import com.spendwise.spendwise_ai.repository.BudgetRepository;
import com.spendwise.spendwise_ai.repository.ExpenseRepository;
import com.spendwise.spendwise_ai.repository.IncomeRepository;
import com.spendwise.spendwise_ai.service.DashboardService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;
    private final IncomeRepository incomeRepository;

    public DashboardServiceImpl(ExpenseRepository expenseRepository,
                                BudgetRepository budgetRepository,
                                IncomeRepository incomeRepository) {
        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
        this.incomeRepository = incomeRepository;
    }

    @Override
    public DashboardSummaryDTO getDashboardSummary(int year, int month, String email) {

        // 📅 Month Date Range (ONLY ONCE)
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // 1️⃣ Expense Category Summary
        List<CategorySummaryDTO> expenses =
                expenseRepository.getCategorySummaryBetweenDates(
                        startDate, endDate, email);

        double totalExpense = expenses.stream()
                .mapToDouble(CategorySummaryDTO::getTotalAmount)
                .sum();

        // 2️⃣ Budget
        List<Budget> budgets =
                budgetRepository.findByYearAndMonthAndUserEmail(year, month, email);

        double totalBudget = budgets.stream()
                .mapToDouble(Budget::getAmount)
                .sum();

        double totalRemainingBudget = totalBudget - totalExpense;

        // 3️⃣ Income (DateBetween used)
        double totalIncome =
                incomeRepository
                        .findByUserEmailAndDateBetween(email, startDate, endDate)
                        .stream()
                        .mapToDouble(Income::getAmount)
                        .sum();

        // 4️⃣ Savings
        double savings = totalIncome - totalExpense;

        double savingsRate =
                totalIncome == 0 ? 0 :
                        (savings / totalIncome) * 100;

        // 5️⃣ Over Budget Count
        long overBudgetCount = budgets.stream()
                .filter(budget -> {
                    double spent = expenses.stream()
                            .filter(e -> e.getCategory()
                                    .equalsIgnoreCase(budget.getCategory()))
                            .map(CategorySummaryDTO::getTotalAmount)
                            .findFirst()
                            .orElse(0.0);
                    return spent > budget.getAmount();
                })
                .count();

        // 6️⃣ Top Spending Category
        String topCategory = expenses.stream()
                .max((a, b) ->
                        Double.compare(a.getTotalAmount(), b.getTotalAmount()))
                .map(CategorySummaryDTO::getCategory)
                .orElse("None");

        // 7️⃣ Financial Status
        String financialStatus;

        if (savingsRate >= 30) {
            financialStatus = "Excellent";
        } else if (savingsRate >= 10) {
            financialStatus = "Good";
        } else if (savingsRate >= 0) {
            financialStatus = "Warning";
        } else {
            financialStatus = "Critical";
        }

        DashboardSummaryDTO dto = new DashboardSummaryDTO();
        dto.setTotalIncome(totalIncome);
        dto.setTotalExpense(totalExpense);
        dto.setTotalRemainingBudget(totalRemainingBudget);
        dto.setSavings(savings);
        dto.setSavingsRate(savingsRate);
        dto.setOverBudgetCount((int) overBudgetCount);
        dto.setTopCategory(topCategory);
        dto.setFinancialStatus(financialStatus);

        return dto;
    }

    @Override
    public List<InsightDTO> generateInsights(int year, int month, String email) {

        DashboardSummaryDTO summary =
                getDashboardSummary(year, month, email);

        List<InsightDTO> insights = new ArrayList<>();

        if (summary.getSavingsRate() >= 30) {
            insights.add(new InsightDTO(
                    "Great job! Your savings rate is excellent.",
                    "INFO"));
        } else if (summary.getSavingsRate() < 10) {
            insights.add(new InsightDTO(
                    "Your savings rate is low. Try reducing expenses.",
                    "WARNING"));
        }

        if (summary.getOverBudgetCount() > 0) {
            insights.add(new InsightDTO(
                    "You exceeded budget in "
                            + summary.getOverBudgetCount()
                            + " categories.",
                    "CRITICAL"));
        }

        insights.add(new InsightDTO(
                "Your highest spending category is "
                        + summary.getTopCategory(),
                "INFO"));

        return insights;
    }

    @Override
    public String detectInvisibleExpenses(int year, int month, String email) {

        List<Expense> expenses =
                expenseRepository.findByYearAndMonthAndUserEmail(
                        year, month, email);

        long smallExpensesCount = expenses.stream()
                .filter(e -> e.getAmount() < 100)
                .count();

        if (smallExpensesCount > 15) {
            return "⚠ You have many small frequent expenses this month.";
        }

        return "No invisible expense pattern detected.";
    }

    @Override
public DailySummaryDTO getDailySummary(LocalDate date, String email) {

    List<Expense> expenses =
            expenseRepository.getExpensesByDate(email, date);

    double totalAmount = expenses.stream()
            .mapToDouble(Expense::getAmount)
            .sum();

    int totalTransactions = expenses.size();

    String topCategory = expenses.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                    Expense::getCategory,
                    java.util.stream.Collectors.summingDouble(Expense::getAmount)
            ))
            .entrySet()
            .stream()
            .max(java.util.Map.Entry.comparingByValue())
            .map(java.util.Map.Entry::getKey)
            .orElse("None");

    DailySummaryDTO dto = new DailySummaryDTO();
    dto.setDate(date.toString());
    dto.setTotalAmount(totalAmount);
    dto.setTotalTransactions(totalTransactions);
    dto.setTopCategory(topCategory);

    return dto;
}

}