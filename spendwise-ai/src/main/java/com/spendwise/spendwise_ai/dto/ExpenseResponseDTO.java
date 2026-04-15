package com.spendwise.spendwise_ai.dto;

import java.time.LocalDate;

public class ExpenseResponseDTO {

    private Long id;
    private String title;
    private Double amount;
    private String category;
    private LocalDate date;

    // ✅ REQUIRED CONSTRUCTOR
    public ExpenseResponseDTO(Long id, String title, Double amount,
                              String category, LocalDate date) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    // ✅ getters
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public LocalDate getDate() {
        return date;
    }
}
