package com.spendwise.spendwise_ai.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Income {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double amount;
    private String source;
    private LocalDate date;
    @Column(nullable = false)
    private int month;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Income() {}

    public Long getId() { return id; }

    public double getAmount() { return amount; }

    public String getSource() { return source; }

    public LocalDate getDate() { return date; }

    public User getUser() { return user; }

    public void setId(Long id) { this.id = id; }

    public void setAmount(double amount) { this.amount = amount; }

    public void setSource(String source) { this.source = source; }

    public void setDate(LocalDate date) { this.date = date; }

    public void setUser(User user) { this.user = user; }
    public int getMonth() {
    return month;
}

public void setMonth(int month) {
    this.month = month;
}
}