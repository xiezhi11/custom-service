package com.example.workorder.controller;

import com.example.workorder.dto.*;
import com.example.workorder.entity.OperationLog;
import com.example.workorder.entity.WorkOrder;
import com.example.workorder.enums.Priority;
import com.example.workorder.enums.ProblemType;
import com.example.workorder.enums.RoleType;
import com.example.workorder.enums.WorkOrderStatus;
import com.example.workorder.service.WorkOrderService;
import com.example.workorder.service.impl.WorkOrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/workorders")
public class WorkOrderController {

    @Autowired
    private WorkOrderService workOrderService;

    @Autowired
    private WorkOrderServiceImpl workOrderServiceImpl;

    @PostMapping
    public ResponseEntity<WorkOrder> createWorkOrder(
            @Validated @RequestBody CreateWorkOrderDTO dto,
            @RequestParam String currentUser) {
        WorkOrder workOrder = workOrderService.createWorkOrder(dto, currentUser);
        return ResponseEntity.ok(workOrder);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkOrder> getWorkOrderById(
            @PathVariable Long id,
            @RequestParam String currentUser,
            @RequestParam RoleType role) {
        WorkOrder workOrder = workOrderService.getWorkOrderById(id, currentUser, role);
        return ResponseEntity.ok(workOrder);
    }

    @GetMapping
    public ResponseEntity<List<WorkOrder>> getWorkOrderList(
            @RequestParam String currentUser,
            @RequestParam RoleType role,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) ProblemType problemType,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) WorkOrderStatus status) {
        List<WorkOrder> list = workOrderService.getWorkOrderList(
                currentUser, role, orderNo, customerName, problemType, priority, status);
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<WorkOrder> assignWorkOrder(
            @PathVariable Long id,
            @Validated @RequestBody AssignWorkOrderDTO dto,
            @RequestParam String operator) {
        WorkOrder workOrder = workOrderService.assignWorkOrder(id, dto.getProcessor(), operator);
        return ResponseEntity.ok(workOrder);
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<WorkOrder> acceptWorkOrder(
            @PathVariable Long id,
            @RequestParam String processor) {
        WorkOrder workOrder = workOrderService.acceptWorkOrder(id, processor);
        return ResponseEntity.ok(workOrder);
    }

    @PutMapping("/{id}/submit-result")
    public ResponseEntity<WorkOrder> submitResult(
            @PathVariable Long id,
            @Validated @RequestBody SubmitResultDTO dto,
            @RequestParam String processor) {
        WorkOrder workOrder = workOrderService.submitResult(id, dto.getProcessResult(), processor);
        return ResponseEntity.ok(workOrder);
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<WorkOrder> confirmWorkOrder(
            @PathVariable Long id,
            @RequestParam String operator) {
        WorkOrder workOrder = workOrderService.confirmWorkOrder(id, operator);
        return ResponseEntity.ok(workOrder);
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<WorkOrder> returnWorkOrder(
            @PathVariable Long id,
            @Validated @RequestBody ReturnWorkOrderDTO dto,
            @RequestParam String operator) {
        WorkOrder workOrder = workOrderService.returnWorkOrder(id, dto.getReturnReason(), operator);
        return ResponseEntity.ok(workOrder);
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<WorkOrder> closeWorkOrder(
            @PathVariable Long id,
            @Validated @RequestBody CloseWorkOrderDTO dto,
            @RequestParam String operator) {
        WorkOrder workOrder = workOrderService.closeWorkOrder(id, dto.getCloseReason(), operator);
        return ResponseEntity.ok(workOrder);
    }

    @GetMapping("/{id}/logs")
    public ResponseEntity<List<OperationLog>> getOperationLogs(@PathVariable Long id) {
        List<OperationLog> logs = workOrderServiceImpl.getOperationLogs(id);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Long>> getStatistics() {
        Map<String, Long> stats = workOrderService.getStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/enums")
    public ResponseEntity<Map<String, Object>> getEnums() {
        Map<String, Object> enums = new HashMap<>();
        enums.put("statuses", WorkOrderStatus.values());
        enums.put("problemTypes", ProblemType.values());
        enums.put("priorities", Priority.values());
        enums.put("roles", RoleType.values());
        return ResponseEntity.ok(enums);
    }
}
