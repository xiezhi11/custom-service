package com.example.workorder.enums;

import lombok.Getter;

@Getter
public enum ProblemType {

    DEVICE_FAULT("设备故障"),
    SYSTEM_ABNORMAL("系统异常"),
    OTHER("其他");

    private final String description;

    ProblemType(String description) {
        this.description = description;
    }
}
