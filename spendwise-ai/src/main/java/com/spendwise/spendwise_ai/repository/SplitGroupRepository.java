package com.spendwise.spendwise_ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spendwise.spendwise_ai.model.SplitGroup;

public interface SplitGroupRepository extends JpaRepository<SplitGroup, Long> {
    SplitGroup findByGroupName(String groupName);
}
