package com.spendwise.spendwise_ai.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.spendwise.spendwise_ai.dto.BalanceDTO;
import com.spendwise.spendwise_ai.dto.SplitExpenseRequestDTO;
import com.spendwise.spendwise_ai.model.SplitExpense;
import com.spendwise.spendwise_ai.model.SplitGroup;

import com.spendwise.spendwise_ai.repository.SplitExpenseRepository;
import com.spendwise.spendwise_ai.repository.SplitGroupRepository;
import com.spendwise.spendwise_ai.service.SplitService;


@RestController
@RequestMapping("/api/split")
public class SplitController {

    @Autowired
    private SplitService splitService;
     @Autowired
private SplitGroupRepository groupRepository;

    @Autowired
     private SplitExpenseRepository expenseRepository;


    @PostMapping("/group")
    public SplitGroup createGroup(
            @RequestParam String groupName,
            @RequestParam String createdBy) {

        return splitService.createGroup(groupName, createdBy);
    }

    @PostMapping("/expense")
    public String addExpense(@RequestBody SplitExpenseRequestDTO request) {

        splitService.addSplitExpense(request);

        return "Split expense added successfully";
    }
    @GetMapping("/balance/{groupId}")
    public List<BalanceDTO> getBalance(@PathVariable Long groupId) {

    return splitService.calculateBalance(groupId);
    }
    @GetMapping("/groups")
    public List<SplitGroup> getAllGroups() {
    return groupRepository.findAll();
    }
    
    @GetMapping("/expenses/{groupId}")
    public List<SplitExpense> getExpenses(@PathVariable Long groupId) {
        return expenseRepository.findByGroupId(groupId);
    }
    @PostMapping("/expense/by-group-name")
    public String addExpenseByGroupName(@RequestBody SplitExpenseRequestDTO request) {

    SplitGroup group = groupRepository.findByGroupName(request.getGroupName());

    if (group == null) {
        throw new RuntimeException("Group not found");
    }

    request.setGroupId(group.getId()); // 🔥 auto set id

    splitService.addSplitExpense(request);

    return "Expense added successfully using group name";
}
}