package com.example.workorder.config;

import com.example.workorder.entity.OperationLog;
import com.example.workorder.entity.WorkOrder;
import com.example.workorder.enums.Priority;
import com.example.workorder.enums.ProblemType;
import com.example.workorder.enums.WorkOrderStatus;
import com.example.workorder.repository.OperationLogRepository;
import com.example.workorder.repository.WorkOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private OperationLogRepository operationLogRepository;

    @Override
    public void run(String... args) {
        if (workOrderRepository.count() == 0) {
            initTestData();
        }
    }

    private void initTestData() {
        LocalDateTime now = LocalDateTime.now();

        WorkOrder wo1 = createWorkOrder("WO202605180001", "张三", "13800138001",
                ProblemType.DEVICE_FAULT, "打印机无法打印，出现卡纸现象",
                Priority.HIGH, "张三", WorkOrderStatus.PENDING_ASSIGN, now);

        WorkOrder wo2 = createWorkOrder("WO202605180002", "李四", "13800138002",
                ProblemType.SYSTEM_ABNORMAL, "系统登录时提示错误代码500",
                Priority.MEDIUM, "李四", WorkOrderStatus.PENDING_ASSIGN,
                now.minusHours(1));

        WorkOrder wo3 = createWorkOrder("WO202605180003", "王五", "13800138003",
                ProblemType.OTHER, "需要申请新的账号权限",
                Priority.LOW, "王五", WorkOrderStatus.ASSIGNED,
                now.minusHours(2));
        wo3.setProcessor("处理人A");
        wo3.setAssignTime(now.minusHours(1));
        workOrderRepository.save(wo3);
        recordLog(wo3.getId(), "客服1", "分派工单给处理人：处理人A", WorkOrderStatus.ASSIGNED, now.minusHours(1));

        WorkOrder wo4 = createWorkOrder("WO202605180004", "赵六", "13800138004",
                ProblemType.DEVICE_FAULT, "电脑开机蓝屏，无法进入系统",
                Priority.HIGH, "赵六", WorkOrderStatus.PROCESSING,
                now.minusHours(3));
        wo4.setProcessor("处理人B");
        wo4.setAssignTime(now.minusHours(2).plusMinutes(30));
        wo4.setAcceptTime(now.minusHours(2));
        workOrderRepository.save(wo4);
        recordLog(wo4.getId(), "客服1", "分派工单给处理人：处理人B", WorkOrderStatus.ASSIGNED, now.minusHours(2).plusMinutes(30));
        recordLog(wo4.getId(), "处理人B", "接单", WorkOrderStatus.PROCESSING, now.minusHours(2));

        WorkOrder wo5 = createWorkOrder("WO202605180005", "钱七", "13800138005",
                ProblemType.SYSTEM_ABNORMAL, "报表导出功能无法使用",
                Priority.MEDIUM, "钱七", WorkOrderStatus.PENDING_CONFIRM,
                now.minusHours(5));
        wo5.setProcessor("处理人A");
        wo5.setAssignTime(now.minusHours(4));
        wo5.setAcceptTime(now.minusHours(3).plusMinutes(30));
        wo5.setProcessResult("已修复报表导出功能，是由于数据量过大导致超时，已优化查询语句。");
        workOrderRepository.save(wo5);
        recordLog(wo5.getId(), "客服1", "分派工单给处理人：处理人A", WorkOrderStatus.ASSIGNED, now.minusHours(4));
        recordLog(wo5.getId(), "处理人A", "接单", WorkOrderStatus.PROCESSING, now.minusHours(3).plusMinutes(30));
        recordLog(wo5.getId(), "处理人A", "提交处理结果：已修复报表导出功能...", WorkOrderStatus.PENDING_CONFIRM, now.minusHours(1));

        WorkOrder wo6 = createWorkOrder("WO202605180006", "孙八", "13800138006",
                ProblemType.OTHER, "咨询软件使用方法",
                Priority.LOW, "孙八", WorkOrderStatus.COMPLETED,
                now.minusDays(1));
        wo6.setProcessor("处理人B");
        wo6.setAssignTime(now.minusDays(1).plusHours(1));
        wo6.setAcceptTime(now.minusDays(1).plusHours(2));
        wo6.setProcessResult("已提供详细的使用手册和操作视频。");
        wo6.setCompleteTime(now.minusHours(6));
        workOrderRepository.save(wo6);
        recordLog(wo6.getId(), "客服1", "分派工单给处理人：处理人B", WorkOrderStatus.ASSIGNED, now.minusDays(1).plusHours(1));
        recordLog(wo6.getId(), "处理人B", "接单", WorkOrderStatus.PROCESSING, now.minusDays(1).plusHours(2));
        recordLog(wo6.getId(), "处理人B", "提交处理结果：已提供详细的使用手册...", WorkOrderStatus.PENDING_CONFIRM, now.minusHours(8));
        recordLog(wo6.getId(), "孙八", "确认完成", WorkOrderStatus.COMPLETED, now.minusHours(6));

        WorkOrder wo7 = createWorkOrder("WO202605180007", "周九", "13800138007",
                ProblemType.DEVICE_FAULT, "鼠标左键失灵",
                Priority.LOW, "周九", WorkOrderStatus.CLOSED,
                now.minusDays(2));
        wo7.setCloseReason("用户自行更换了鼠标，问题已解决。");
        workOrderRepository.save(wo7);
        recordLog(wo7.getId(), "客服1", "关闭工单，原因：用户自行更换了鼠标，问题已解决。", WorkOrderStatus.CLOSED, now.minusDays(1).plusHours(12));
    }

    private WorkOrder createWorkOrder(String orderNo, String customerName, String contactInfo,
                                       ProblemType problemType, String problemDescription,
                                       Priority priority, String creator, WorkOrderStatus status,
                                       LocalDateTime createTime) {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setOrderNo(orderNo);
        workOrder.setCustomerName(customerName);
        workOrder.setContactInfo(contactInfo);
        workOrder.setProblemType(problemType);
        workOrder.setProblemDescription(problemDescription);
        workOrder.setPriority(priority);
        workOrder.setCreator(creator);
        workOrder.setStatus(status);
        workOrder.setCreateTime(createTime);
        workOrder.setUpdateTime(createTime);
        workOrder = workOrderRepository.save(workOrder);
        recordLog(workOrder.getId(), creator, "创建工单", status, createTime);
        return workOrder;
    }

    private void recordLog(Long workOrderId, String operator, String content,
                            WorkOrderStatus statusAfter, LocalDateTime time) {
        OperationLog log = new OperationLog();
        log.setWorkOrderId(workOrderId);
        log.setOperator(operator);
        log.setOperationContent(content);
        log.setStatusAfter(statusAfter);
        log.setOperationTime(time);
        operationLogRepository.save(log);
    }
}
