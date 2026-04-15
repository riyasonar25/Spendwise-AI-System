package com.spendwise.spendwise_ai.controller;

import com.spendwise.spendwise_ai.model.Income;
import com.spendwise.spendwise_ai.service.IncomeService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/income")
public class IncomeController {

    private final IncomeService incomeService;

    public IncomeController(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

    // ✅ ADD INCOME
    @PostMapping
    public Income addIncome(@RequestBody Income income,
                            Authentication authentication) {

        String email = authentication.getName();

        return incomeService.addIncome(income, email);
    }

    // ✅ GET ALL INCOME (USER WISE)
    @GetMapping
    public List<Income> getAllIncome(Authentication authentication) {

        String email = authentication.getName();

        return incomeService.getAllIncome(email);
    }

    // ✅ DELETE INCOME
    @DeleteMapping("/{id}")
    public String deleteIncome(@PathVariable Long id) {

        incomeService.deleteIncome(id);
        return "Income deleted successfully";
    }
}