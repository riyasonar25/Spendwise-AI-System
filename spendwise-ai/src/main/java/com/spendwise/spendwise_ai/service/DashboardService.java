package com.spendwise.spendwise_ai.service;

import java.time.LocalDate;
import java.util.List;

import com.spendwise.spendwise_ai.dto.DailySummaryDTO;
import com.spendwise.spendwise_ai.dto.DashboardSummaryDTO;
import com.spendwise.spendwise_ai.dto.InsightDTO;

public interface DashboardService {

    DashboardSummaryDTO getDashboardSummary(int year, int month, String email);

    DailySummaryDTO getDailySummary(LocalDate date, String email);

    List<InsightDTO> generateInsights(int year, int month, String email);

    String detectInvisibleExpenses(int year, int month, String email);
}