package com.example.workorder.enums;

import lombok.Getter;

@Getter
public enum WorkOrderStatus {

    PENDING_ASSIGN("待分派"),
    ASSIGNED("已分派"),
    PROCESSING("处理中"),
    PENDING_CONFIRM("待确认"),
    COMPLETED("已完成"),
    RETURNED("已退回"),
    CLOSED("已关闭");

    private final String description;

    WorkOrderStatus(String description) {
        this.description = description;
    }
}
