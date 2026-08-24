package com.spendwise.spendwise_ai.service;

import java.util.List;

import com.spendwise.spendwise_ai.dto.BalanceDTO;
import com.spendwise.spendwise_ai.dto.SplitExpenseRequestDTO;
import com.spendwise.spendwise_ai.model.SplitGroup;

public interface SplitService {

    // =========================================================
    // CREATE GROUP
    // =========================================================

    SplitGroup createGroup(
            String groupName,
            String createdBy
    );

    // =========================================================
    // ADD SPLIT EXPENSE
    // =========================================================

    void addSplitExpense(
            SplitExpenseRequestDTO request
    );

    // =========================================================
    // CALCULATE BALANCE
    // =========================================================

    List<BalanceDTO> calculateBalance(
            Long groupId
    );

    // =========================================================
    // DELETE SPLIT EXPENSE
    // =========================================================

    String deleteSplitExpense(
            Long expenseId
    );
}