package com.example.workorder.enums;

import lombok.Getter;

@Getter
public enum Priority {

    HIGH("高", 3),
    MEDIUM("中", 2),
    LOW("低", 1);

    private final String description;
    private final int order;

    Priority(String description, int order) {
        this.description = description;
        this.order = order;
    }
}
