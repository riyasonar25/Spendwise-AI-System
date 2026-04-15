package com.spendwise.spendwise_ai.service;

import java.util.List;

import com.spendwise.spendwise_ai.dto.BalanceDTO;
import com.spendwise.spendwise_ai.dto.SplitExpenseRequestDTO;
import com.spendwise.spendwise_ai.model.SplitGroup;

public interface SplitService {

    SplitGroup createGroup(String groupName, String createdBy);

    void addSplitExpense(SplitExpenseRequestDTO request);

    List<BalanceDTO> calculateBalance(Long groupId);

}