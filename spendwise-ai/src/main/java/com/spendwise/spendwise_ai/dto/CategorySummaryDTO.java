package com.spendwise.spendwise_ai.dto;

public class CategorySummaryDTO {

    private String category;
    private Double totalAmount;

    public CategorySummaryDTO(String category, Double totalAmount) {
        this.category = category;
        this.totalAmount = totalAmount;
    }

    public String getCategory() {
        return category;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }
}
