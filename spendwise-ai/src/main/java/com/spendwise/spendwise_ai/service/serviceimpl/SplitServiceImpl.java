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
import com.spendwise.spendwise_ai.service.EmailService;
import com.spendwise.spendwise_ai.service.SplitService;

@Service
public class SplitServiceImpl implements SplitService {

    @Autowired
    private SplitGroupRepository groupRepository;

    @Autowired
    private SplitExpenseRepository expenseRepository;

    @Autowired
    private SplitShareRepository shareRepository;

    @Autowired
    private EmailService emailService;


    // =========================================================
    // CREATE GROUP
    // =========================================================

    @Override
    public SplitGroup createGroup(String groupName, String createdBy) {

        SplitGroup group = new SplitGroup();

        group.setGroupName(groupName);
        group.setCreatedBy(createdBy);

        return groupRepository.save(group);
    }


    // =========================================================
    // ADD SPLIT EXPENSE
    // =========================================================

    @Override
    public void addSplitExpense(SplitExpenseRequestDTO request) {

        if (request == null) {
            throw new RuntimeException("Request cannot be null");
        }

        if (request.getSplits() == null ||
                request.getSplits().isEmpty()) {

            throw new RuntimeException(
                    "Members list cannot be empty"
            );
        }

        if (request.getTotalAmount() <= 0) {

            throw new RuntimeException(
                    "Total amount must be greater than zero"
            );
        }

        if (request.getPaidBy() == null ||
                request.getPaidBy().trim().isEmpty()) {

            throw new RuntimeException(
                    "PaidBy cannot be empty"
            );
        }

        String paidBy = request.getPaidBy().trim();

        List<SplitMemberDTO> members =
                request.getSplits();


        // =====================================================
        // VALIDATE MEMBERS
        // =====================================================

        for (SplitMemberDTO member : members) {

            if (member == null) {

                throw new RuntimeException(
                        "Invalid member"
                );
            }

            if (member.getEmail() == null ||
                    member.getEmail().trim().isEmpty()) {

                throw new RuntimeException(
                        "Member email cannot be empty"
                );
            }

            if (member.getName() == null ||
                    member.getName().trim().isEmpty()) {

                throw new RuntimeException(
                        "Member name cannot be empty"
                );
            }
        }


        // =====================================================
        // CHECK PAID BY MEMBER
        // =====================================================

        boolean paidByExists =
                members.stream()
                        .anyMatch(member ->
                                member.getEmail()
                                        .trim()
                                        .equalsIgnoreCase(paidBy)
                        );

        if (!paidByExists) {

            throw new RuntimeException(
                    "PaidBy must be part of members"
            );
        }


        // =====================================================
        // GET PAYER NAME
        // =====================================================

        String paidByName =
                getMemberName(
                        members,
                        paidBy
                );


        // =====================================================
        // CREATE EXPENSE
        // =====================================================

        SplitExpense expense =
                new SplitExpense();

        expense.setGroupId(
                request.getGroupId()
        );

        expense.setDescription(
                request.getDescription() != null &&
                !request.getDescription().trim().isEmpty()
                        ? request.getDescription().trim()
                        : "Expense"
        );

        expense.setTotalAmount(
                request.getTotalAmount()
        );

        expense.setPaidBy(paidBy);

        expense.setMembers(
                members.size()
        );

        SplitExpense savedExpense =
                expenseRepository.save(expense);


        // =====================================================
        // EQUAL SPLIT
        // =====================================================

        if ("EQUAL".equalsIgnoreCase(
                request.getSplitType())) {

            double equalAmount =
                    request.getTotalAmount()
                            / members.size();

            equalAmount =
                    Math.round(
                            equalAmount * 100.0
                    ) / 100.0;

            for (SplitMemberDTO member : members) {

         saveShare(
                savedExpense,
                member.getEmail(),
                member.getName(),
                equalAmount,
                paidBy
                );      
        }


        } else {

            // =================================================
            // EXACT SPLIT
            // =================================================

            double totalSplitAmount = 0.0;

            for (SplitMemberDTO member : members) {

                if (member.getAmount() < 0) {

                    throw new RuntimeException(
                            "Split amount cannot be negative"
                    );
                }

                totalSplitAmount +=
                        member.getAmount();
            }

            double difference =
                    Math.abs(
                            totalSplitAmount -
                            request.getTotalAmount()
                    );

            if (difference > 0.01) {

                throw new RuntimeException(
                        "Split amounts must equal total amount. "
                                + "Total: ₹"
                                + request.getTotalAmount()
                                + ", Split: ₹"
                                + totalSplitAmount
                );
            }

            for (SplitMemberDTO member : members) {

                double amount =
                        Math.round(
                                member.getAmount() * 100.0
                        ) / 100.0;

                saveShare(
                        savedExpense,
                        member.getEmail(),
                        member.getName(),
                        amount,
                        paidBy
                );
            }
        }


        // =====================================================
        // SEND EMAILS
        // =====================================================

        sendSplitEmails(
                savedExpense,
                members,
                paidBy,
                paidByName
        );
    }


    // =========================================================
    // SAVE SHARE
    // =========================================================
private void saveShare(
        SplitExpense expense,
        String memberEmail,
        String memberName,
        double amount,
        String paidBy) {

    if (memberEmail == null ||
            memberEmail.trim().isEmpty()) {

        throw new RuntimeException(
                "Member email cannot be empty"
        );
    }

    if (memberName == null ||
            memberName.trim().isEmpty()) {

        throw new RuntimeException(
                "Member name cannot be empty"
        );
    }

    SplitShare share =
            new SplitShare();

    share.setExpenseId(
            expense.getId()
    );

    share.setMemberEmail(
            memberEmail.trim()
    );

    // IMPORTANT:
    // Save the exact name entered in Split Expense
    share.setMemberName(
            memberName.trim()
    );

    share.setAmount(amount);

    // PAYER = PAID
    // OTHER MEMBERS = PENDING
    if (memberEmail.trim()
            .equalsIgnoreCase(paidBy)) {

        share.setStatus("PAID");

    } else {

        share.setStatus("PENDING");
    }

    shareRepository.save(share);
}


    // =========================================================
    // SEND SPLIT EMAILS
    // =========================================================

    private void sendSplitEmails(
            SplitExpense expense,
            List<SplitMemberDTO> members,
            String paidBy,
            String paidByName) {

        double payerShare = 0.0;

        double totalToReceive = 0.0;

        List<String> peopleWhoNeedToPay =
                new ArrayList<>();


        // =====================================================
        // SEND EMAIL TO EACH MEMBER
        // =====================================================

        for (SplitMemberDTO member : members) {

            if (member == null ||
                    member.getEmail() == null) {

                continue;
            }

            String memberEmail =
                    member.getEmail().trim();

            double amount =
                    getMemberShare(
                            expense.getId(),
                            memberEmail
                    );


            // =================================================
            // PAYER
            // =================================================

            if (memberEmail.equalsIgnoreCase(paidBy)) {

                payerShare = amount;

            } else {

                totalToReceive += amount;

                String memberName =
                        member.getName();

                if (memberName == null ||
                        memberName.trim().isEmpty()) {

                    memberName =
                            getDisplayName(
                                    memberEmail
                            );
                }

                memberName =
                        memberName.trim();

                peopleWhoNeedToPay.add(
                        memberName
                );


                // =================================================
                // MEMBER EMAIL
                // =================================================

                try {

                    emailService.sendMemberSplitEmail(
                            memberEmail,
                            memberName,
                            paidByName,
                            expense.getTotalAmount(),
                            amount
                    );

                    System.out.println(
                            "MEMBER EMAIL SENT TO: "
                                    + memberEmail
                    );

                } catch (Exception e) {

                    System.out.println(
                            "MEMBER EMAIL FAILED: "
                                    + memberEmail
                                    + " -> "
                                    + e.getMessage()
                    );
                }
            }
        }


        // =====================================================
        // PAYER EMAIL
        // =====================================================

        if (totalToReceive > 0) {

            String memberNames =
                    String.join(
                            ", ",
                            peopleWhoNeedToPay
                    );

            try {

                emailService.sendPayerSplitEmail(
                        paidBy,
                        paidByName,
                        memberNames,
                        expense.getTotalAmount(),
                        payerShare,
                        totalToReceive
                );

                System.out.println(
                        "PAYER EMAIL SENT TO: "
                                + paidBy
                );

            } catch (Exception e) {

                System.out.println(
                        "PAYER EMAIL FAILED: "
                                + paidBy
                                + " -> "
                                + e.getMessage()
                );
            }
        }
    }


    // =========================================================
    // GET MEMBER NAME
    // =========================================================

    private String getMemberName(
            List<SplitMemberDTO> members,
            String email) {

        if (members == null ||
                email == null) {

            return "User";
        }

        for (SplitMemberDTO member : members) {

            if (member == null ||
                    member.getEmail() == null) {

                continue;
            }

            if (member.getEmail()
                    .trim()
                    .equalsIgnoreCase(
                            email.trim()
                    )) {

                if (member.getName() != null &&
                        !member.getName().trim().isEmpty()) {

                    return member.getName().trim();
                }
            }
        }

        return getDisplayName(email);
    }


    // =========================================================
    // GET MEMBER SHARE
    // =========================================================

    private double getMemberShare(
            Long expenseId,
            String email) {

        SplitShare share =
                shareRepository
                        .findByExpenseIdAndMemberEmail(
                                expenseId,
                                email
                        );

        if (share == null) {

            return 0.0;
        }

        return share.getAmount();
    }


    // =========================================================
    // DISPLAY NAME FALLBACK
    // =========================================================

    private String getDisplayName(String email) {

        if (email == null ||
                email.trim().isEmpty()) {

            return "User";
        }

        int atIndex =
                email.indexOf("@");

        if (atIndex <= 0) {

            return "User";
        }

        String name =
                email.substring(
                        0,
                        atIndex
                );

        name = name
                .replace(".", " ")
                .replace("_", " ")
                .replace("-", " ");

        String[] parts =
                name.split("\\s+");

        StringBuilder result =
                new StringBuilder();

        for (String part : parts) {

            if (part.isEmpty()) {
                continue;
            }

            result.append(
                    Character.toUpperCase(
                            part.charAt(0)
                    )
            );

            if (part.length() > 1) {

                result.append(
                        part.substring(1)
                                .toLowerCase()
                );
            }

            result.append(" ");
        }

        String displayName =
                result.toString().trim();

        return displayName.isEmpty()
                ? "User"
                : displayName;
    }


    // =========================================================
    // CALCULATE BALANCE
    // =========================================================

    @Override
    public List<BalanceDTO> calculateBalance(
            Long groupId) {

        List<SplitExpense> expenses =
                expenseRepository.findByGroupId(
                        groupId
                );

        Map<String, Double> balanceMap =
                new HashMap<>();


        for (SplitExpense expense : expenses) {

            String paidBy =
                    expense.getPaidBy();

            double totalAmount =
                    expense.getTotalAmount();


            // =================================================
            // PERSON WHO PAID
            // =================================================

            balanceMap.put(
                    paidBy,
                    balanceMap.getOrDefault(
                            paidBy,
                            0.0
                    ) + totalAmount
            );


            // =================================================
            // MEMBERS
            // =================================================

            List<SplitShare> shares =
                    shareRepository.findByExpenseId(
                            expense.getId()
                    );

            for (SplitShare share : shares) {

                String member =
                        share.getMemberEmail();

                double amount =
                        share.getAmount();

                balanceMap.put(
                        member,
                        balanceMap.getOrDefault(
                                member,
                                0.0
                        ) - amount
                );
            }
        }


        // =====================================================
        // CREDITORS / DEBTORS
        // =====================================================

        List<Map.Entry<String, Double>> creditors =
                new ArrayList<>();

        List<Map.Entry<String, Double>> debtors =
                new ArrayList<>();

        for (Map.Entry<String, Double> entry :
                balanceMap.entrySet()) {

            if (entry.getValue() > 0) {

                creditors.add(entry);

            } else if (entry.getValue() < 0) {

                debtors.add(entry);
            }
        }


        creditors.sort(
                (a, b) ->
                        Double.compare(
                                b.getValue(),
                                a.getValue()
                        )
        );

        debtors.sort(
                (a, b) ->
                        Double.compare(
                                a.getValue(),
                                b.getValue()
                        )
        );


        // =====================================================
        // CREATE BALANCE RESULT
        // =====================================================

        List<BalanceDTO> result =
                new ArrayList<>();

        int i = 0;
        int j = 0;

        while (
                i < debtors.size() &&
                j < creditors.size()
        ) {

            Map.Entry<String, Double> debtor =
                    debtors.get(i);

            Map.Entry<String, Double> creditor =
                    creditors.get(j);

            double debtAmount =
                    -debtor.getValue();

            double creditAmount =
                    creditor.getValue();

            double settledAmount =
                    Math.min(
                            debtAmount,
                            creditAmount
                    );

            settledAmount =
                    Math.round(
                            settledAmount * 100.0
                    ) / 100.0;

            String message =
                    debtor.getKey()
                            + " pays "
                            + creditor.getKey()
                            + " ₹"
                            + settledAmount;

            result.add(
                    new BalanceDTO(
                            debtor.getKey(),
                            creditor.getKey(),
                            settledAmount,
                            message
                    )
            );


            debtor.setValue(
                    debtor.getValue()
                            + settledAmount
            );

            creditor.setValue(
                    creditor.getValue()
                            - settledAmount
            );


            if (
                    Math.abs(
                            debtor.getValue()
                    ) < 0.01
            ) {

                i++;
            }

            if (
                    Math.abs(
                            creditor.getValue()
                    ) < 0.01
            ) {

                j++;
            }
        }

        return result;
    }


    // =========================================================
    // DELETE SPLIT EXPENSE
    // ONLY WHEN ALL MEMBERS ARE PAID
    // =========================================================
      @Override
public String deleteSplitExpense(Long expenseId) {

    if (expenseId == null) {
        throw new RuntimeException(
                "Expense ID cannot be null"
        );
    }

    // -----------------------------------------------------
    // FIND EXPENSE
    // -----------------------------------------------------

    SplitExpense expense = expenseRepository
            .findById(expenseId)
            .orElseThrow(() ->
                    new RuntimeException(
                            "Split expense not found"
                    )
            );

    // -----------------------------------------------------
    // FIND ALL MEMBERS
    // -----------------------------------------------------

    List<SplitShare> shares =
            shareRepository.findByExpenseId(expenseId);

    if (shares == null || shares.isEmpty()) {
        throw new RuntimeException(
                "No split members found"
        );
    }

    // -----------------------------------------------------
    // CHECK ALL MEMBERS ARE PAID
    // -----------------------------------------------------

    for (SplitShare share : shares) {

        if (share == null) {
            continue;
        }

        String status = share.getStatus();

        if (status == null ||
                !status.equalsIgnoreCase("PAID")) {

            throw new RuntimeException(
                    "Cannot delete split expense. " +
                    "All members must be PAID first."
            );
        }
    }

    // -----------------------------------------------------
    // DELETE SHARES FIRST
    // -----------------------------------------------------

    shareRepository.deleteAll(shares);

    // -----------------------------------------------------
    // DELETE EXPENSE
    // -----------------------------------------------------

    expenseRepository.delete(expense);

    // -----------------------------------------------------
    // SUCCESS MESSAGE
    // -----------------------------------------------------

    return "Split expense deleted successfully";
}
}