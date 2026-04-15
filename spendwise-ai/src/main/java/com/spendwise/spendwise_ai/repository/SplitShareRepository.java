package com.spendwise.spendwise_ai.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.spendwise.spendwise_ai.model.SplitShare;

import java.util.List;

public interface SplitShareRepository extends JpaRepository<SplitShare, Long> {

    List<SplitShare> findByExpenseId(Long expenseId);

}