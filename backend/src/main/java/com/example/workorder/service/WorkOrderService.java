package com.example.workorder.service;

import com.example.workorder.dto.CreateWorkOrderDTO;
import com.example.workorder.entity.WorkOrder;
import com.example.workorder.enums.Priority;
import com.example.workorder.enums.ProblemType;
import com.example.workorder.enums.RoleType;
import com.example.workorder.enums.WorkOrderStatus;

import java.util.List;
import java.util.Map;

public interface WorkOrderService {

    WorkOrder createWorkOrder(CreateWorkOrderDTO dto, String currentUser);

    WorkOrder getWorkOrderById(Long id, String currentUser, RoleType role);

    List<WorkOrder> getWorkOrderList(String currentUser, RoleType role, String orderNo,
                                      String customerName, ProblemType problemType,
                                      Priority priority, WorkOrderStatus status);

    WorkOrder assignWorkOrder(Long id, String processor, String operator);

    WorkOrder acceptWorkOrder(Long id, String processor);

    WorkOrder submitResult(Long id, String processResult, String processor);

    WorkOrder confirmWorkOrder(Long id, String operator);

    WorkOrder returnWorkOrder(Long id, String returnReason, String operator);

    WorkOrder closeWorkOrder(Long id, String closeReason, String operator);

    Map<String, Long> getStatistics();
}
