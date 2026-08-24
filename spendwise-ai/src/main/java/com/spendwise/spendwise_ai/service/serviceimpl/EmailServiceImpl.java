package com.spendwise.spendwise_ai.service.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.spendwise.spendwise_ai.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // =========================================================
    // MEMBER EMAIL
    // =========================================================
    // Example:
    // Amit owes ₹500 to Riya
    // =========================================================

    @Override
    public void sendMemberSplitEmail(
            String to,
            String memberName,
            String payerName,
            double totalAmount,
            double shareAmount) {

        System.out.println("======================================");
        System.out.println("TRYING TO SEND MEMBER EMAIL");
        System.out.println("TO      : " + to);
        System.out.println("MEMBER  : " + memberName);
        System.out.println("PAYER   : " + payerName);
        System.out.println("SHARE   : ₹" + shareAmount);

        SimpleMailMessage mail = new SimpleMailMessage();

        mail.setFrom("riyasonar25@gmail.com");
        mail.setTo(to);

        mail.setSubject("💰 SpendWiseAI - You Have a Pending Split");

        mail.setText(
                "👋 Hi " + memberName + ",\n\n" +

                "A new expense has been split for you in SpendWiseAI.\n\n" +

                "━━━━━━━━━━━━━━━━━━━━━━\n" +
                "💰 Total Expense : ₹" + totalAmount + "\n" +
                "💸 Your Share    : ₹" + shareAmount + "\n" +
                "👤 Pay To        : " + payerName + "\n" +
                "━━━━━━━━━━━━━━━━━━━━━━\n\n" +

                "Please settle ₹" + shareAmount +
                " with " + payerName + ".\n\n" +

                "Once you have paid, mark the split as PAID in SpendWiseAI.\n\n" +

                "Thank you for using SpendWiseAI! 💜\n\n" +
                "— SpendWiseAI"
        );

        try {

            mailSender.send(mail);

            System.out.println("======================================");
            System.out.println("✅ MEMBER EMAIL SENT: " + to);
            System.out.println("======================================");

        } catch (Exception e) {

            System.out.println("======================================");
            System.out.println("❌ MEMBER EMAIL FAILED");
            System.out.println("TO: " + to);
            System.out.println("ERROR: " + e.getMessage());
            System.out.println("======================================");

            e.printStackTrace();
        }
    }


    // =========================================================
    // PAYER EMAIL
    // =========================================================
    // Example:
    // Riya paid ₹2000.
    // Her share = ₹1500.
    // Amit owes her ₹500.
    // =========================================================

    @Override
    public void sendPayerSplitEmail(
            String to,
            String payerName,
            String memberName,
            double totalAmount,
            double payerShare,
            double receiveAmount) {

        System.out.println("======================================");
        System.out.println("TRYING TO SEND PAYER EMAIL");
        System.out.println("TO          : " + to);
        System.out.println("PAYER       : " + payerName);
        System.out.println("MEMBER      : " + memberName);
        System.out.println("TOTAL       : ₹" + totalAmount);
        System.out.println("PAYER SHARE : ₹" + payerShare);
        System.out.println("RECEIVE     : ₹" + receiveAmount);

        SimpleMailMessage mail = new SimpleMailMessage();

        mail.setFrom("riyasonar25@gmail.com");
        mail.setTo(to);

        mail.setSubject("💰 SpendWiseAI - You Will Receive Money");

        mail.setText(
                "👋 Hi " + payerName + ",\n\n" +

                "You have paid for a new expense in SpendWiseAI.\n\n" +

                "━━━━━━━━━━━━━━━━━━━━━━\n" +
                "💰 Total Paid       : ₹" + totalAmount + "\n" +
                "👤 Your Share       : ₹" + payerShare + "\n" +
                "💵 Amount to Receive: ₹" + receiveAmount + "\n" +
                "👤 Receive From     : " + memberName + "\n" +
                "━━━━━━━━━━━━━━━━━━━━━━\n\n" +

                memberName + " needs to pay you ₹" +
                receiveAmount + ".\n\n" +

                "You can track the payment status from your SpendWiseAI Split Record.\n\n" +

                "Thank you for using SpendWiseAI! 💜\n\n" +
                "— SpendWiseAI"
        );

        try {

            mailSender.send(mail);

            System.out.println("======================================");
            System.out.println("✅ PAYER EMAIL SENT: " + to);
            System.out.println("======================================");

        } catch (Exception e) {

            System.out.println("======================================");
            System.out.println("❌ PAYER EMAIL FAILED");
            System.out.println("TO: " + to);
            System.out.println("ERROR: " + e.getMessage());
            System.out.println("======================================");

            e.printStackTrace();
        }
    }
}