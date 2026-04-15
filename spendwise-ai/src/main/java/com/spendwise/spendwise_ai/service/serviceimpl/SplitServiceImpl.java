package com.spendwise.spendwise_ai.service.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spendwise.spendwise_ai.dto.BalanceDTO;
import com.spendwise.spendwise_ai.dto.SplitExpenseRequestDTO;
import com.spendwise.spendwise_ai.dto.SplitMemberDTO;
import com.spendwise.spendwise_ai.model.SplitExpense;
import com.spendwise.spendwise_ai.model.SplitGroup;
import com.spendwise.spendwise_ai.model.SplitShare;
import com.spendwise.spendwise_ai.repository.SplitExpenseRepository;
import com.spendwise.spendwise_ai.repository.SplitGroupRepository;
import com.spendwise.spendwise_ai.repository.SplitShareRepository;
import com.spendwise.spendwise_ai.service.SplitService;

@Service
public class SplitServiceImpl implements SplitService {

    @Autowired
    private SplitGroupRepository groupRepository;

    @Autowired
    private SplitExpenseRepository expenseRepository;

    @Autowired
    private SplitShareRepository shareRepository;

    // ===============================
    // CREATE GROUP
    // ===============================
    @Override
    public SplitGroup createGroup(String groupName, String createdBy) {

        SplitGroup group = new SplitGroup();
        group.setGroupName(groupName);
        group.setCreatedBy(createdBy);

        return groupRepository.save(group);
    }

    // ===============================
    // ADD EXPENSE (EQUAL + EXACT)
    // ===============================
    @Override
    public void addSplitExpense(SplitExpenseRequestDTO request) {

        if (request.getSplits() == null || request.getSplits().isEmpty()) {
            throw new RuntimeException("Members list cannot be empty");
        }

        // Validate paidBy present in members
        boolean exists = request.getSplits().stream()
                .anyMatch(m -> m.getEmail().equals(request.getPaidBy()));

        if (!exists) {
            throw new RuntimeException("PaidBy must be part of members");
        }

        SplitExpense expense = new SplitExpense();
        expense.setGroupId(request.getGroupId());
        expense.setDescription(request.getDescription());
        expense.setTotalAmount(request.getTotalAmount());
        expense.setPaidBy(request.getPaidBy());

        SplitExpense savedExpense = expenseRepository.save(expense);

        List<SplitMemberDTO> members = request.getSplits();

        // 🔥 EQUAL SPLIT
        if ("EQUAL".equalsIgnoreCase(request.getSplitType())) {

            double equalAmount = request.getTotalAmount() / members.size();

            for (SplitMemberDTO m : members) {

                SplitShare share = new SplitShare();
                share.setExpenseId(savedExpense.getId());
                share.setMemberEmail(m.getEmail());
                share.setAmount(equalAmount);

                shareRepository.save(share);
            }

        } else {
            // EXACT SPLIT
            for (SplitMemberDTO m : members) {

                SplitShare share = new SplitShare();
                share.setExpenseId(savedExpense.getId());
                share.setMemberEmail(m.getEmail());
                share.setAmount(m.getAmount());

                shareRepository.save(share);
            }
        }
    }

    // ===============================
    // CALCULATE BALANCE
    // ===============================
    @Override
    public List<BalanceDTO> calculateBalance(Long groupId) {

        List<SplitExpense> expenses = expenseRepository.findByGroupId(groupId);

        Map<String, Double> balanceMap = new HashMap<>();

        for (SplitExpense exp : expenses) {

            String paidBy = exp.getPaidBy();
            double totalAmount = exp.getTotalAmount();

            balanceMap.put(paidBy,
                    balanceMap.getOrDefault(paidBy, 0.0) + totalAmount);

            List<SplitShare> shares = shareRepository.findByExpenseId(exp.getId());

            for (SplitShare share : shares) {

                String member = share.getMemberEmail();
                double amount = share.getAmount();

                balanceMap.put(member,
                        balanceMap.getOrDefault(member, 0.0) - amount);
            }
        }

        List<Map.Entry<String, Double>> creditors = new ArrayList<>();
        List<Map.Entry<String, Double>> debtors = new ArrayList<>();

        for (Map.Entry<String, Double> entry : balanceMap.entrySet()) {

            if (entry.getValue() > 0) {
                creditors.add(entry);
            } else if (entry.getValue() < 0) {
                debtors.add(entry);
            }
        }

        // 🔥 SORT (better accuracy)
        creditors.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        debtors.sort((a, b) -> Double.compare(a.getValue(), b.getValue()));

        List<BalanceDTO> result = new ArrayList<>();

        int i = 0, j = 0;

        while (i < debtors.size() && j < creditors.size()) {

            Map.Entry<String, Double> debtor = debtors.get(i);
            Map.Entry<String, Double> creditor = creditors.get(j);

            double debtAmount = -debtor.getValue();
            double creditAmount = creditor.getValue();

            double settledAmount = Math.min(debtAmount, creditAmount);

            // 🔥 ROUNDING
            settledAmount = Math.round(settledAmount * 100.0) / 100.0;

            // 🔥 MESSAGE
            String message = debtor.getKey() + " pays " + creditor.getKey() + " ₹" + settledAmount;

            result.add(new BalanceDTO(
                    debtor.getKey(),
                    creditor.getKey(),
                    settledAmount,
                    message
            ));

            debtor.setValue(debtor.getValue() + settledAmount);
            creditor.setValue(creditor.getValue() - settledAmount);

            if (Math.abs(debtor.getValue()) < 0.01) i++;
            if (Math.abs(creditor.getValue()) < 0.01) j++;
        }

        return result;
    }
}