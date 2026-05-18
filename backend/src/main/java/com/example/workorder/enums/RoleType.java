package com.example.workorder.enums;

import lombok.Getter;

@Getter
public enum RoleType {

    CUSTOMER("客户"),
    CUSTOMER_SERVICE("客服"),
    PROCESSOR("处理人");

    private final String description;

    RoleType(String description) {
        this.description = description;
    }
}
