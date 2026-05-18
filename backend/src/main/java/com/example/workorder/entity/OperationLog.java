package com.example.workorder.entity;

import com.example.workorder.enums.WorkOrderStatus;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "operation_log")
public class OperationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "work_order_id", nullable = false)
    private Long workOrderId;

    @Column(name = "operation_time", nullable = false)
    private LocalDateTime operationTime;

    @Column(name = "operator", nullable = false, length = 64)
    private String operator;

    @Column(name = "operation_content", nullable = false, length = 500)
    private String operationContent;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_after", length = 32)
    private WorkOrderStatus statusAfter;

    @PrePersist
    protected void onCreate() {
        operationTime = LocalDateTime.now();
    }
}
