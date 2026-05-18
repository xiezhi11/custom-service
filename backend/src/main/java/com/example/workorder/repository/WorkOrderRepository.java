package com.example.workorder.repository;

import com.example.workorder.entity.WorkOrder;
import com.example.workorder.enums.Priority;
import com.example.workorder.enums.ProblemType;
import com.example.workorder.enums.WorkOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long>, JpaSpecificationExecutor<WorkOrder> {

    Optional<WorkOrder> findByOrderNo(String orderNo);

    List<WorkOrder> findByCreatorOrderByPriorityOrderDescCreateTimeDesc(String creator);

    List<WorkOrder> findByProcessorOrderByPriorityOrderDescCreateTimeDesc(String processor);

    List<WorkOrder> findAllByOrderByPriorityOrderDescCreateTimeDesc();

    long countByStatus(WorkOrderStatus status);

    @Query("SELECT MAX(w.orderNo) FROM WorkOrder w WHERE w.orderNo LIKE ?1")
    String findMaxOrderNoByPrefix(String prefix);
}
