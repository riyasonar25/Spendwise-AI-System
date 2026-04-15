package com.spendwise.spendwise_ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsightDTO {

    private String message;
    private String level;   // INFO, WARNING, CRITICAL
}
