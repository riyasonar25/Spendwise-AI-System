package com.spendwise.spendwise_ai.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.spendwise.spendwise_ai.dto.BalanceDTO;
import com.spendwise.spendwise_ai.dto.SplitExpenseRequestDTO;
import com.spendwise.spendwise_ai.dto.SplitMemberRecordDTO;
import com.spendwise.spendwise_ai.dto.SplitRecordDTO;
import com.spendwise.spendwise_ai.model.SplitExpense;
import com.spendwise.spendwise_ai.model.SplitGroup;
import com.spendwise.spendwise_ai.model.SplitShare;
import com.spendwise.spendwise_ai.repository.SplitExpenseRepository;
import com.spendwise.spendwise_ai.repository.SplitGroupRepository;
import com.spendwise.spendwise_ai.repository.SplitShareRepository;
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

    @Autowired
    private SplitShareRepository shareRepository;


    // =========================================================
    // CREATE GROUP
    // =========================================================

    @PostMapping("/group")
    public SplitGroup createGroup(
            @RequestParam String groupName,
            @RequestParam String createdBy) {

        return splitService.createGroup(
                groupName,
                createdBy
        );
    }


    // =========================================================
    // ADD EXPENSE
    // =========================================================

    @PostMapping("/expense")
    public String addExpense(
            @RequestBody SplitExpenseRequestDTO request) {

        splitService.addSplitExpense(request);

        return "Split expense added successfully";
    }


    // =========================================================
    // GET BALANCE
    // =========================================================

    @GetMapping("/balance/{groupId}")
    public List<BalanceDTO> getBalance(
            @PathVariable Long groupId) {

        return splitService.calculateBalance(groupId);
    }


    // =========================================================
    // GET GROUPS BY USER
    // =========================================================

    @GetMapping("/groups")
    public List<SplitGroup> getGroupsByUser(
            @RequestParam String createdBy) {

        return groupRepository.findByCreatedBy(createdBy);
    }


    // =========================================================
    // GET EXPENSES OF GROUP
    // =========================================================

    @GetMapping("/expenses/{groupId}")
    public List<SplitExpense> getExpenses(
            @PathVariable Long groupId) {

        return expenseRepository.findByGroupId(groupId);
    }


    // =========================================================
    // ADD EXPENSE BY GROUP NAME
    // =========================================================

    @PostMapping("/expense/by-group-name")
    public String addExpenseByGroupName(
            @RequestBody SplitExpenseRequestDTO request,
            org.springframework.security.core.Authentication authentication) {

        String loggedInEmail = authentication.getName();

        SplitGroup group =
                groupRepository.findByGroupNameAndCreatedBy(
                        request.getGroupName(),
                        loggedInEmail
                );

        if (group == null) {

            group = new SplitGroup();

            group.setGroupName(request.getGroupName());
            group.setCreatedBy(loggedInEmail);

            group = groupRepository.save(group);
        }

        request.setGroupId(group.getId());

        splitService.addSplitExpense(request);

        return "Expense Added Successfully";
    }


    // =========================================================
    // GET SPLIT RECORDS
    // =========================================================

    @GetMapping("/record/{groupId}")
    public List<SplitRecordDTO> getSplitRecords(
            @PathVariable Long groupId) {

        List<SplitExpense> expenses =
                expenseRepository.findByGroupId(groupId);

        List<SplitRecordDTO> records =
                new ArrayList<>();

        // Latest expense first
        expenses.sort(
                (a, b) -> Long.compare(
                        b.getId(),
                        a.getId()
                )
        );

        // Get group
        SplitGroup group =
                groupRepository.findById(groupId)
                        .orElse(null);

        String groupName =
                group != null
                        ? group.getGroupName()
                        : "Unknown Group";


        // =====================================================
        // LOOP EXPENSES
        // =====================================================

        for (SplitExpense expense : expenses) {

            SplitRecordDTO record =
                    new SplitRecordDTO();

            record.setExpenseId(
                    expense.getId()
            );

            record.setGroupName(
                    groupName
            );

            record.setDescription(
                    expense.getDescription()
            );

            record.setTotalAmount(
                    expense.getTotalAmount()
            );

            record.setPaidBy(
                    expense.getPaidBy()
            );


            // =================================================
            // GET SHARES
            // =================================================

            List<SplitShare> shares =
                    shareRepository.findByExpenseId(
                            expense.getId()
                    );

            List<SplitMemberRecordDTO> members =
                    new ArrayList<>();


            // =================================================
            // LOOP MEMBERS
            // =================================================

            for (SplitShare share : shares) {

                SplitMemberRecordDTO member =
                        new SplitMemberRecordDTO();

                // EMAIL
                member.setEmail(
                        share.getMemberEmail()
                );


                // =================================================
                // NAME
                // IMPORTANT:
                // Use the name entered in Split Expense
                // =================================================

                String memberName =
                        share.getMemberName();

                if (memberName == null ||
                        memberName.trim().isEmpty()) {

                    memberName = "User";
                }

                member.setName(
                        memberName
                );


                // AMOUNT
                member.setAmount(
                        share.getAmount()
                );


                // =================================================
                // STATUS
                // =================================================

                String status =
                        share.getStatus();

                if (status == null ||
                        status.trim().isEmpty()) {

                    if (share.getMemberEmail() != null &&
                            share.getMemberEmail()
                                    .equalsIgnoreCase(
                                            expense.getPaidBy()
                                    )) {

                        status = "PAID";

                    } else {

                        status = "PENDING";
                    }
                }

                member.setStatus(
                        status.toUpperCase()
                );

                members.add(member);
            }


            record.setMembers(members);

            records.add(record);
        }

        return records;
    }


    // =========================================================
    // UPDATE PAID / PENDING STATUS
    // =========================================================

    @PutMapping("/record/status")
    public String updateSplitStatus(
            @RequestParam Long expenseId,
            @RequestParam String memberEmail,
            @RequestParam String status) {

        SplitShare share =
                shareRepository
                        .findByExpenseIdAndMemberEmail(
                                expenseId,
                                memberEmail
                        );

        if (share == null) {
            return "Split member record not found";
        }


        if (!status.equalsIgnoreCase("PAID")
                && !status.equalsIgnoreCase("PENDING")) {

            return "Invalid status. Use PAID or PENDING";
        }


        share.setStatus(
                status.toUpperCase()
        );

        shareRepository.save(share);

        return "Payment status updated successfully";
    }


    // =========================================================
    // DELETE SPLIT RECORD
    // =========================================================

    @DeleteMapping("/record/{expenseId}")
    public String deleteSplitExpense(
            @PathVariable Long expenseId) {

        return splitService.deleteSplitExpense(
                expenseId
        );
    }
}