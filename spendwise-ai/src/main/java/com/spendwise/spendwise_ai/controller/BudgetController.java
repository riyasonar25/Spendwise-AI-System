package com.spendwise.spendwise_ai.controller;

import com.spendwise.spendwise_ai.dto.BudgetAlertDTO;
import com.spendwise.spendwise_ai.dto.BudgetStatusDTO;
import com.spendwise.spendwise_ai.model.Budget;
import com.spendwise.spendwise_ai.service.BudgetService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    // ✅ Create Budget
    @PostMapping
    public Budget setBudget(@RequestBody Budget budget,
                            Principal principal) {

        return budgetService.setBudget(
                budget,
                principal.getName()   // 👈 email from JWT
        );
    }

    // ✅ Get Single Budget
    @GetMapping("/{category}/{year}/{month}")
    public Budget getBudget(@PathVariable String category,
                            @PathVariable int year,
                            @PathVariable int month,
                            Principal principal) {

        return budgetService.getBudget(
                category,
                year,
                month,
                principal.getName()
        );
    }

    // ✅ Get All Budgets
    @GetMapping
    public List<Budget> getAllBudgets(Principal principal) {
        return budgetService.getAllBudgets(
                principal.getName()
        );
    }

    // ✅ Budget Status
    @GetMapping("/status/{year}/{month}")
    public List<BudgetStatusDTO> getBudgetStatus(
            @PathVariable int year,
            @PathVariable int month,
            Principal principal) {

        return budgetService.checkBudgetStatus(
                year,
                month,
                principal.getName()
        );
    }

    // ✅ Budget Alerts
    @GetMapping("/alerts/{year}/{month}")
    public List<BudgetAlertDTO> getBudgetAlerts(
            @PathVariable int year,
            @PathVariable int month,
            Principal principal) {

        return budgetService.getBudgetAlerts(
                year,
                month,
                principal.getName()
        );
    }
}
