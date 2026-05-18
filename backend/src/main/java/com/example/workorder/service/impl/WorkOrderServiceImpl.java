package com.example.workorder.service.impl;

import com.example.workorder.dto.CreateWorkOrderDTO;
import com.example.workorder.entity.OperationLog;
import com.example.workorder.entity.WorkOrder;
import com.example.workorder.enums.Priority;
import com.example.workorder.enums.ProblemType;
import com.example.workorder.enums.RoleType;
import com.example.workorder.enums.WorkOrderStatus;
import com.example.workorder.repository.OperationLogRepository;
import com.example.workorder.repository.WorkOrderRepository;
import com.example.workorder.service.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class WorkOrderServiceImpl implements WorkOrderService {

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private OperationLogRepository operationLogRepository;

    @Override
    @Transactional
    public WorkOrder createWorkOrder(CreateWorkOrderDTO dto, String currentUser) {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setOrderNo(generateOrderNo());
        workOrder.setCustomerName(dto.getCustomerName());
        workOrder.setContactInfo(dto.getContactInfo());
        workOrder.setProblemType(dto.getProblemType());
        workOrder.setProblemDescription(dto.getProblemDescription());
        workOrder.setPriority(dto.getPriority());
        workOrder.setStatus(WorkOrderStatus.PENDING_ASSIGN);
        workOrder.setCreator(currentUser);

        workOrder = workOrderRepository.save(workOrder);

        recordOperationLog(workOrder.getId(), currentUser, "创建工单", WorkOrderStatus.PENDING_ASSIGN);

        return workOrder;
    }

    @Override
    public WorkOrder getWorkOrderById(Long id, String currentUser, RoleType role) {
        WorkOrder workOrder = workOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("工单不存在"));

        checkPermission(workOrder, currentUser, role);

        return workOrder;
    }

    @Override
    public List<WorkOrder> getWorkOrderList(String currentUser, RoleType role, String orderNo,
                                             String customerName, ProblemType problemType,
                                             Priority priority, WorkOrderStatus status) {
        Specification<WorkOrder> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (RoleType.CUSTOMER.equals(role)) {
                predicates.add(cb.equal(root.get("creator"), currentUser));
            } else if (RoleType.PROCESSOR.equals(role)) {
                predicates.add(cb.equal(root.get("processor"), currentUser));
            }

            if (StringUtils.hasText(orderNo)) {
                predicates.add(cb.like(root.get("orderNo"), "%" + orderNo + "%"));
            }
            if (StringUtils.hasText(customerName)) {
                predicates.add(cb.like(root.get("customerName"), "%" + customerName + "%"));
            }
            if (problemType != null) {
                predicates.add(cb.equal(root.get("problemType"), problemType));
            }
            if (priority != null) {
                predicates.add(cb.equal(root.get("priority"), priority));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            query.orderBy(
                    cb.desc(root.get("priority").get("order")),
                    cb.desc(root.get("createTime"))
            );

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return workOrderRepository.findAll(spec);
    }

    @Override
    @Transactional
    public WorkOrder assignWorkOrder(Long id, String processor, String operator) {
        WorkOrder workOrder = workOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("工单不存在"));

        if (!WorkOrderStatus.PENDING_ASSIGN.equals(workOrder.getStatus())) {
            throw new RuntimeException("只有待分派状态的工单才能分派");
        }

        workOrder.setProcessor(processor);
        workOrder.setStatus(WorkOrderStatus.ASSIGNED);
        workOrder.setAssignTime(LocalDateTime.now());

        workOrder = workOrderRepository.save(workOrder);

        recordOperationLog(workOrder.getId(), operator,
                "分派工单给处理人：" + processor, WorkOrderStatus.ASSIGNED);

        return workOrder;
    }

    @Override
    @Transactional
    public WorkOrder acceptWorkOrder(Long id, String processor) {
        WorkOrder workOrder = workOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("工单不存在"));

        if (!WorkOrderStatus.ASSIGNED.equals(workOrder.getStatus())) {
            throw new RuntimeException("只有已分派状态的工单才能接单");
        }

        if (!processor.equals(workOrder.getProcessor())) {
            throw new RuntimeException("只能处理分派给自己的工单");
        }

        workOrder.setStatus(WorkOrderStatus.PROCESSING);
        workOrder.setAcceptTime(LocalDateTime.now());

        workOrder = workOrderRepository.save(workOrder);

        recordOperationLog(workOrder.getId(), processor, "接单", WorkOrderStatus.PROCESSING);

        return workOrder;
    }

    @Override
    @Transactional
    public WorkOrder submitResult(Long id, String processResult, String processor) {
        WorkOrder workOrder = workOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("工单不存在"));

        if (!WorkOrderStatus.PROCESSING.equals(workOrder.getStatus())
                && !WorkOrderStatus.RETURNED.equals(workOrder.getStatus())) {
            throw new RuntimeException("只有处理中或已退回状态的工单才能提交处理结果");
        }

        if (!processor.equals(workOrder.getProcessor())) {
            throw new RuntimeException("只能处理分派给自己的工单");
        }

        workOrder.setProcessResult(processResult);
        workOrder.setStatus(WorkOrderStatus.PENDING_CONFIRM);

        workOrder = workOrderRepository.save(workOrder);

        recordOperationLog(workOrder.getId(), processor,
                "提交处理结果：" + processResult, WorkOrderStatus.PENDING_CONFIRM);

        return workOrder;
    }

    @Override
    @Transactional
    public WorkOrder confirmWorkOrder(Long id, String operator) {
        WorkOrder workOrder = workOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("工单不存在"));

        if (!WorkOrderStatus.PENDING_CONFIRM.equals(workOrder.getStatus())) {
            throw new RuntimeException("只有待确认状态的工单才能确认");
        }

        if (!operator.equals(workOrder.getCreator())) {
            throw new RuntimeException("只能确认自己提交的工单");
        }

        workOrder.setStatus(WorkOrderStatus.COMPLETED);
        workOrder.setCompleteTime(LocalDateTime.now());

        workOrder = workOrderRepository.save(workOrder);

        recordOperationLog(workOrder.getId(), operator, "确认完成", WorkOrderStatus.COMPLETED);

        return workOrder;
    }

    @Override
    @Transactional
    public WorkOrder returnWorkOrder(Long id, String returnReason, String operator) {
        WorkOrder workOrder = workOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("工单不存在"));

        if (!WorkOrderStatus.PENDING_CONFIRM.equals(workOrder.getStatus())) {
            throw new RuntimeException("只有待确认状态的工单才能退回");
        }

        if (!operator.equals(workOrder.getCreator())) {
            throw new RuntimeException("只能退回自己提交的工单");
        }

        workOrder.setStatus(WorkOrderStatus.RETURNED);
        workOrder.setReturnReason(returnReason);

        workOrder = workOrderRepository.save(workOrder);

        recordOperationLog(workOrder.getId(), operator,
                "退回工单，原因：" + returnReason, WorkOrderStatus.RETURNED);

        return workOrder;
    }

    @Override
    @Transactional
    public WorkOrder closeWorkOrder(Long id, String closeReason, String operator) {
        WorkOrder workOrder = workOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("工单不存在"));

        if (WorkOrderStatus.COMPLETED.equals(workOrder.getStatus())
                || WorkOrderStatus.CLOSED.equals(workOrder.getStatus())) {
            throw new RuntimeException("已完成和已关闭的工单不能关闭");
        }

        workOrder.setStatus(WorkOrderStatus.CLOSED);
        workOrder.setCloseReason(closeReason);

        workOrder = workOrderRepository.save(workOrder);

        recordOperationLog(workOrder.getId(), operator,
                "关闭工单，原因：" + closeReason, WorkOrderStatus.CLOSED);

        return workOrder;
    }

    @Override
    public Map<String, Long> getStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", workOrderRepository.count());
        stats.put("pendingAssign", workOrderRepository.countByStatus(WorkOrderStatus.PENDING_ASSIGN));
        stats.put("processing", workOrderRepository.countByStatus(WorkOrderStatus.PROCESSING));
        stats.put("pendingConfirm", workOrderRepository.countByStatus(WorkOrderStatus.PENDING_CONFIRM));
        stats.put("completed", workOrderRepository.countByStatus(WorkOrderStatus.COMPLETED));
        stats.put("closed", workOrderRepository.countByStatus(WorkOrderStatus.CLOSED));
        return stats;
    }

    public List<OperationLog> getOperationLogs(Long workOrderId) {
        return operationLogRepository.findByWorkOrderIdOrderByOperationTimeAsc(workOrderId);
    }

    private String generateOrderNo() {
        String prefix = "WO" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String maxOrderNo = workOrderRepository.findMaxOrderNoByPrefix(prefix + "%");
        int sequence = 1;
        if (maxOrderNo != null && maxOrderNo.length() > prefix.length()) {
            try {
                sequence = Integer.parseInt(maxOrderNo.substring(prefix.length())) + 1;
            } catch (NumberFormatException e) {
                sequence = 1;
            }
        }
        return prefix + String.format("%04d", sequence);
    }

    private void recordOperationLog(Long workOrderId, String operator, String content, WorkOrderStatus statusAfter) {
        OperationLog log = new OperationLog();
        log.setWorkOrderId(workOrderId);
        log.setOperator(operator);
        log.setOperationContent(content);
        log.setStatusAfter(statusAfter);
        operationLogRepository.save(log);
    }

    private void checkPermission(WorkOrder workOrder, String currentUser, RoleType role) {
        if (RoleType.CUSTOMER.equals(role)) {
            if (!currentUser.equals(workOrder.getCreator())) {
                throw new RuntimeException("无权查看他人的工单");
            }
        } else if (RoleType.PROCESSOR.equals(role)) {
            if (!currentUser.equals(workOrder.getProcessor())) {
                throw new RuntimeException("无权查看分派给他人的工单");
            }
        }
    }
}
