package com.spendwise.spendwise_ai.dto;

import java.util.List;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SplitRecordDTO {

    private Long expenseId;

    private String groupName;

    private String description;

    private double totalAmount;

    private String paidBy;

    private List<SplitMemberRecordDTO> members;
}