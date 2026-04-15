package com.spendwise.spendwise_ai.dto;

public class DashboardSummaryDTO {

    private double totalIncome;
    private double totalExpense;
    private double totalRemainingBudget;
    private double savings;
    private double savingsRate;
    private int overBudgetCount;
    private String topCategory;
    private String financialStatus;

    // Getters and Setters

    public double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public double getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(double totalExpense) {
        this.totalExpense = totalExpense;
    }

    public double getTotalRemainingBudget() {
        return totalRemainingBudget;
    }

    public void setTotalRemainingBudget(double totalRemainingBudget) {
        this.totalRemainingBudget = totalRemainingBudget;
    }

    public double getSavings() {
        return savings;
    }

    public void setSavings(double savings) {
        this.savings = savings;
    }

    public double getSavingsRate() {
        return savingsRate;
    }

    public void setSavingsRate(double savingsRate) {
        this.savingsRate = savingsRate;
    }

    public int getOverBudgetCount() {
        return overBudgetCount;
    }

    public void setOverBudgetCount(int overBudgetCount) {
        this.overBudgetCount = overBudgetCount;
    }

    public String getTopCategory() {
        return topCategory;
    }

    public void setTopCategory(String topCategory) {
        this.topCategory = topCategory;
    }

    public String getFinancialStatus() {
        return financialStatus;
    }

    public void setFinancialStatus(String financialStatus) {
        this.financialStatus = financialStatus;
    }
}
