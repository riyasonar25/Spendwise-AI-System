package com.spendwise.spendwise_ai.service.serviceimpl;

import com.spendwise.spendwise_ai.dto.BudgetAlertDTO;
import com.spendwise.spendwise_ai.dto.BudgetStatusDTO;
import com.spendwise.spendwise_ai.dto.CategorySummaryDTO;
import com.spendwise.spendwise_ai.model.Budget;
import com.spendwise.spendwise_ai.model.User;
import com.spendwise.spendwise_ai.repository.BudgetRepository;
import com.spendwise.spendwise_ai.repository.ExpenseRepository;
import com.spendwise.spendwise_ai.repository.UserRepository;
import com.spendwise.spendwise_ai.service.BudgetService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    // ✅ Constructor Injection
    public BudgetServiceImpl(BudgetRepository budgetRepository,
                             ExpenseRepository expenseRepository,
                             UserRepository userRepository) {
        this.budgetRepository = budgetRepository;
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    // ✅ Set Budget with User
    @Override
    public Budget setBudget(Budget budget, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        budget.setUser(user);

        return budgetRepository.save(budget);
    }

    // ✅ Get Single Budget
    @Override
    public Budget getBudget(String category, int year, int month, String email) {

        return budgetRepository
                .findByCategoryIgnoreCaseAndYearAndMonthAndUserEmail(
                        category, year, month, email)
                .orElse(null);
    }

    // ✅ Get All Budgets of Logged User
    @Override
    public List<Budget> getAllBudgets(String email) {
        return budgetRepository.findByUserEmail(email);
    }

    // ✅ Budget Status
    @Override
    public List<BudgetStatusDTO> checkBudgetStatus(int year, int month, String email) {

        List<Budget> budgets =
                budgetRepository.findByYearAndMonthAndUserEmail(year, month, email);

        List<CategorySummaryDTO> expenses =
                expenseRepository.getCategorySummaryByMonth(year, month, email);

        List<BudgetStatusDTO> result = new ArrayList<>();

        for (Budget budget : budgets) {

            Double spent = expenses.stream()
                    .filter(e -> e.getCategory()
                            .equalsIgnoreCase(budget.getCategory()))
                    .map(CategorySummaryDTO::getTotalAmount)
                    .findFirst()
                    .orElse(0.0);

            Double remaining = budget.getAmount() - spent;

            result.add(new BudgetStatusDTO(
                    budget.getCategory(),
                    budget.getAmount(),
                    spent,
                    remaining,
                    spent > budget.getAmount()
            ));
        }

        return result;
    }

    // ✅ Budget Alerts
    @Override
    public List<BudgetAlertDTO> getBudgetAlerts(int year, int month, String email) {

        List<Budget> budgets =
                budgetRepository.findByYearAndMonthAndUserEmail(year, month, email);

        List<CategorySummaryDTO> expenses =
                expenseRepository.getCategorySummaryByMonth(year, month, email);

        List<BudgetAlertDTO> alerts = new ArrayList<>();

        for (Budget budget : budgets) {

            double spent = expenses.stream()
                    .filter(e -> e.getCategory()
                            .equalsIgnoreCase(budget.getCategory()))
                    .map(CategorySummaryDTO::getTotalAmount)
                    .findFirst()
                    .orElse(0.0);

            double percent = (spent / budget.getAmount()) * 100;

            String message;

            if (percent >= 100) {
                message = "⚠ Budget Exceeded!";
            } else if (percent >= 80) {
                message = "⚠ Warning: 80% budget used";
            } else {
                message = "Budget is safe";
            }

            alerts.add(new BudgetAlertDTO(
                    budget.getCategory(),
                    budget.getAmount(),
                    spent,
                    percent,
                    message
            ));
        }

        return alerts;
    }
}
