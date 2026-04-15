package com.spendwise.spendwise_ai.model;

import jakarta.persistence.*;

@Entity
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;
    private Double amount;
    private int year;
    private int month;

    public Budget() {
    }

    public Budget(Long id, String category, Double amount, int year, int month) {
        this.id = id;
        this.category = category;
        this.amount = amount;
        this.year = year;
        this.month = month;
    }

    public Long getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public Double getAmount() {
        return amount;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }
     @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    public User getUser() {
    return user;
    }

    public void setUser(User user) {
    this.user = user;
    }
}
