package com.spendwise.spendwise_ai.dto;

import java.util.List;

public class SplitExpenseRequestDTO {

    // =========================================================
    // GROUP NAME
    // =========================================================

    private String groupName;

    // =========================================================
    // CREATED BY
    // =========================================================

    private String createdBy;

    // =========================================================
    // GROUP ID
    // =========================================================

    private Long groupId;

    // =========================================================
    // DESCRIPTION
    // =========================================================

    private String description;

    // =========================================================
    // TOTAL AMOUNT
    // =========================================================

    private double totalAmount;

    // =========================================================
    // PAID BY
    // =========================================================

    private String paidBy;

    // =========================================================
    // SPLIT TYPE
    // =========================================================

    private String splitType;

    // =========================================================
    // SPLITS / MEMBERS
    // =========================================================

    private List<SplitMemberDTO> splits;


    // =========================================================
    // GROUP NAME
    // =========================================================

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }


    // =========================================================
    // CREATED BY
    // =========================================================

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }


    // =========================================================
    // GROUP ID
    // =========================================================

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }


    // =========================================================
    // DESCRIPTION
    // =========================================================

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    // =========================================================
    // TOTAL AMOUNT
    // =========================================================

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }


    // =========================================================
    // PAID BY
    // =========================================================

    public String getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(String paidBy) {
        this.paidBy = paidBy;
    }


    // =========================================================
    // SPLIT TYPE
    // =========================================================

    public String getSplitType() {
        return splitType;
    }

    public void setSplitType(String splitType) {
        this.splitType = splitType;
    }


    // =========================================================
    // SPLITS
    // =========================================================

    public List<SplitMemberDTO> getSplits() {
        return splits;
    }

    public void setSplits(List<SplitMemberDTO> splits) {
        this.splits = splits;
    }
}