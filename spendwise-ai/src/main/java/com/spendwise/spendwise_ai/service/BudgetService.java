package com.spendwise.spendwise_ai.service;

import com.spendwise.spendwise_ai.dto.BudgetAlertDTO;
import com.spendwise.spendwise_ai.dto.BudgetStatusDTO;
import com.spendwise.spendwise_ai.model.Budget;

import java.util.List;

public interface BudgetService {

    Budget setBudget(Budget budget, String email);

Budget getBudget(String category, int year, int month, String email);

List<Budget> getAllBudgets(String email);

List<BudgetStatusDTO> checkBudgetStatus(int year, int month, String email);

List<BudgetAlertDTO> getBudgetAlerts(int year, int month, String email);





}
