package com.spendwise.spendwise_ai.service;

import com.spendwise.spendwise_ai.model.Income;
import java.util.List;

public interface IncomeService {

    Income addIncome(Income income, String email);

    List<Income> getAllIncome(String email);

    double getTotalIncomeByMonth(int year, int month, String email);

    void deleteIncome(Long id);
}