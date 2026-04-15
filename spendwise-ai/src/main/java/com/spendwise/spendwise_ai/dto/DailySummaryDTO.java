package com.spendwise.spendwise_ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailySummaryDTO {

    private String date;
    private double totalAmount;
    private int totalTransactions;
    private String topCategory;

}
