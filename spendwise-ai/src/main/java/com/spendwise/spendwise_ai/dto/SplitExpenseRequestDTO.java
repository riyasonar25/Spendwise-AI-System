package com.spendwise.spendwise_ai.dto;

import java.util.List;

import lombok.Data;

@Data
public class SplitExpenseRequestDTO {

    private Long groupId;

    private String description;

    private double totalAmount;

    private String paidBy;
    private String groupName;

    private List<SplitMemberDTO> splits;
    private String splitType; // EQUAL / EXACT
}