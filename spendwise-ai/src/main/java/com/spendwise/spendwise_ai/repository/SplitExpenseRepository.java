package com.spendwise.spendwise_ai.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spendwise.spendwise_ai.model.SplitExpense;

public interface SplitExpenseRepository extends JpaRepository<SplitExpense, Long> {

    List<SplitExpense> findByGroupId(Long groupId);

}