package com.spendwise.spendwise_ai.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spendwise.spendwise_ai.model.SplitShare;

@Repository
public interface SplitShareRepository
        extends JpaRepository<SplitShare, Long> {

    List<SplitShare> findByExpenseId(
            Long expenseId
    );

    SplitShare findByExpenseIdAndMemberEmail(
            Long expenseId,
            String memberEmail
    );

    void deleteByExpenseId(
            Long expenseId
    );
}