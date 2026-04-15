package com.spendwise.spendwise_ai.controller;

import com.spendwise.spendwise_ai.dto.DailySummaryDTO;
import com.spendwise.spendwise_ai.dto.DashboardSummaryDTO;
import com.spendwise.spendwise_ai.dto.InsightDTO;
import com.spendwise.spendwise_ai.service.DashboardService;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;


import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

  @GetMapping("/summary")
    public DashboardSummaryDTO getSummary(
        @RequestParam int year,
        @RequestParam int month) {

    String email = SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getName();

    return dashboardService.getDashboardSummary(year, month, email);
}
  @GetMapping("/daily")
public DailySummaryDTO getDailySummary(@RequestParam String date) {

    String email = SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getName();

    LocalDate localDate = LocalDate.parse(date);

    return dashboardService.getDailySummary(localDate, email);
}
   @GetMapping("/insights")
    public List<InsightDTO> getInsights(
        @RequestParam int year,
        @RequestParam int month) {

    String email = SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getName();

        return dashboardService.generateInsights(year, month, email);
    }
    @GetMapping("/ai/invisible-expenses")
    public ResponseEntity<String> detectInvisible(
        @RequestParam int year,
        @RequestParam int month) {

    String email = SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getName();

    return ResponseEntity.ok(
            dashboardService.detectInvisibleExpenses(year, month, email)
        );
    }

}
