package com.spendwise.spendwise_ai.service;

public interface EmailService {

    // Member ko email:
    // "Aapko ₹500 Riya ko dene hain"
    void sendMemberSplitEmail(
            String to,
            String memberName,
            String payerName,
            double totalAmount,
            double shareAmount
    );

    // Payer ko email:
    // "Aapko Amit se ₹500 receive karne hain"
    void sendPayerSplitEmail(
            String to,
            String payerName,
            String memberName,
            double totalAmount,
            double payerShare,
            double receiveAmount
    );
}