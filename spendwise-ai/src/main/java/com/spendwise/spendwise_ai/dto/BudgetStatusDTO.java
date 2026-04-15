package com.spendwise.spendwise_ai.dto;

public class BudgetStatusDTO {

    private String category;
    private Double budgetAmount;
    private Double spent;
    private Double remaining;
    private boolean exceeded;

    public BudgetStatusDTO() {
    }

    public BudgetStatusDTO(String category,
                           Double budgetAmount,
                           Double spent,
                           Double remaining,
                           boolean exceeded) {
        this.category = category;
        this.budgetAmount = budgetAmount;
        this.spent = spent;
        this.remaining = remaining;
        this.exceeded = exceeded;
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

    public Double getRemaining() {
        return remaining;
    }

    public boolean isExceeded() {
        return exceeded;
    }
}
