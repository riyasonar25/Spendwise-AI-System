package com.spendwise.spendwise_ai.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spendwise.spendwise_ai.model.SplitGroup;

public interface SplitGroupRepository extends JpaRepository<SplitGroup, Long> {

    // Find group by name and creator
    SplitGroup findByGroupNameAndCreatedBy(String groupName, String createdBy);

    // Get all groups created by logged-in user
    List<SplitGroup> findByCreatedBy(String createdBy);

}