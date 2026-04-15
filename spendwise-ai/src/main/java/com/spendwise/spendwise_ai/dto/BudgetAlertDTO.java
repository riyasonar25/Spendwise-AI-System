package com.spendwise.spendwise_ai.dto;

public class BudgetAlertDTO {

    private String category;
    private Double budgetAmount;
    private Double spent;
    private Double percentUsed;
    private String message;

    public BudgetAlertDTO() {
    }

    public BudgetAlertDTO(String category,
                          Double budgetAmount,
                          Double spent,
                          Double percentUsed,
                          String message) {
        this.category = category;
        this.budgetAmount = budgetAmount;
        this.spent = spent;
        this.percentUsed = percentUsed;
        this.message = message;
    }

    public String getCategory() {
        return category;
    }

    public Double getBudgetAmount() {
        return budgetAmount;
    }

    public Double getSpent() {
        return spent;
    }

    public Double getPercentUsed() {
        return percentUsed;
    }

    public String getMessage() {
        return message;
    }
}
