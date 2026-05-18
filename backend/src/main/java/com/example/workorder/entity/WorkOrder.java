package com.example.workorder.entity;

import com.example.workorder.enums.Priority;
import com.example.workorder.enums.ProblemType;
import com.example.workorder.enums.WorkOrderStatus;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "work_order")
public class WorkOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_no", unique = true, nullable = false, length = 32)
    private String orderNo;

    @Column(name = "customer_name", nullable = false, length = 64)
    private String customerName;

    @Column(name = "contact_info", nullable = false, length = 64)
    private String contactInfo;

    @Enumerated(EnumType.STRING)
    @Column(name = "problem_type", nullable = false, length = 32)
    private ProblemType problemType;

    @Column(name = "problem_description", nullable = false, length = 1000)
    private String problemDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 16)
    private Priority priority;

    @Column(name = "processor", length = 64)
    private String processor;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private WorkOrderStatus status;

    @Column(name = "process_result", length = 2000)
    private String processResult;

    @Column(name = "complete_time")
    private LocalDateTime completeTime;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    @Column(name = "assign_time")
    private LocalDateTime assignTime;

    @Column(name = "accept_time")
    private LocalDateTime acceptTime;

    @Column(name = "return_reason", length = 500)
    private String returnReason;

    @Column(name = "close_reason", length = 500)
    private String closeReason;

    @Column(name = "creator", nullable = false, length = 64)
    private String creator;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
