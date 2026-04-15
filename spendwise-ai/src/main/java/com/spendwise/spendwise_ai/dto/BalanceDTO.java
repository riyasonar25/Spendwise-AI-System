package com.spendwise.spendwise_ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BalanceDTO {

    private String fromUser;
    private String toUser;
    private double amount;
    private String message;
}
