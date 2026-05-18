<template>
  <el-dialog
    v-model="dialogVisible"
    title="工单详情"
    width="700px"
    :close-on-click-modal="false"
  >
    <el-descriptions v-if="workOrder" :column="2" border>
      <el-descriptions-item label="工单编号">{{ workOrder.orderNo }}</el-descriptions-item>
      <el-descriptions-item label="处理状态">
        <el-tag :type="getStatusTagType(workOrder.status)">
          {{ getStatusLabel(workOrder.status) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="客户名称">{{ workOrder.customerName }}</el-descriptions-item>
      <el-descriptions-item label="联系方式">{{ workOrder.contactInfo }}</el-descriptions-item>
      <el-descriptions-item label="问题类型">
        {{ getProblemTypeLabel(workOrder.problemType) }}
      </el-descriptions-item>
      <el-descriptions-item label="优先级">
        <el-tag :type="getPriorityTagType(workOrder.priority)">
          {{ getPriorityLabel(workOrder.priority) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="处理人">{{ workOrder.processor || '-' }}</el-descriptions-item>
      <el-descriptions-item label="创建人">{{ workOrder.creator }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ workOrder.createTime }}</el-descriptions-item>
      <el-descriptions-item label="更新时间">{{ workOrder.updateTime }}</el-descriptions-item>
      <el-descriptions-item label="分派时间">{{ workOrder.assignTime || '-' }}</el-descriptions-item>
      <el-descriptions-item label="接单时间">{{ workOrder.acceptTime || '-' }}</el-descriptions-item>
      <el-descriptions-item label="完成时间">{{ workOrder.completeTime || '-' }}</el-descriptions-item>
      <el-descriptions-item label="问题描述" :span="2">
        {{ workOrder.problemDescription }}
      </el-descriptions-item>
      <el-descriptions-item label="处理结果" :span="2">
        {{ workOrder.processResult || '-' }}
      </el-descriptions-item>
      <el-descriptions-item v-if="workOrder.returnReason" label="退回原因" :span="2">
        <el-tag type="warning">{{ workOrder.returnReason }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item v-if="workOrder.closeReason" label="关闭原因" :span="2">
        <el-tag type="info">{{ workOrder.closeReason }}</el-tag>
      </el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">处理记录</el-divider>
    <el-timeline>
      <el-timeline-item
        v-for="log in logs"
        :key="log.id"
        :timestamp="log.operationTime"
        placement="top"
      >
        <el-card>
          <h4>{{ log.operator }}</h4>
          <p>{{ log.operationContent }}</p>
          <el-tag size="small" :type="getStatusTagType(log.statusAfter)">
            {{ getStatusLabel(log.statusAfter) }}
          </el-tag>
        </el-card>
      </el-timeline-item>
    </el-timeline>

    <template #footer>
      <el-button @click="dialogVisible = false">关闭</el-button>
      <template v-if="canAssign(workOrder)">
        <el-button type="success" @click="$emit('assign', workOrder)">分派</el-button>
      </template>
      <template v-if="canAccept(workOrder)">
        <el-button type="success" @click="$emit('accept', workOrder)">接单</el-button>
      </template>
      <template v-if="canSubmitResult(workOrder)">
        <el-button type="primary" @click="$emit('submit', workOrder)">提交结果</el-button>
      </template>
      <template v-if="canConfirm(workOrder)">
        <el-button type="success" @click="$emit('confirm', workOrder)">确认完成</el-button>
      </template>
      <template v-if="canReturn(workOrder)">
        <el-button type="warning" @click="$emit('return', workOrder)">退回</el-button>
      </template>
      <template v-if="canClose(workOrder)">
        <el-button type="danger" @click="$emit('close', workOrder)">关闭</el-button>
      </template>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  visible: Boolean,
  workOrder: Object,
  logs: {
    type: Array,
    default: () => []
  },
  currentRole: String,
  currentUser: String
})

const emit = defineEmits(['update:visible', 'assign', 'accept', 'submit', 'confirm', 'return', 'close'])

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

function getProblemTypeLabel(type) {
  const labels = {
    DEVICE_FAULT: '设备故障',
    SYSTEM_ABNORMAL: '系统异常',
    OTHER: '其他'
  }
  return labels[type] || type
}

function getPriorityLabel(priority) {
  const labels = {
    HIGH: '高',
    MEDIUM: '中',
    LOW: '低'
  }
  return labels[priority] || priority
}

function getPriorityTagType(priority) {
  const types = {
    HIGH: 'danger',
    MEDIUM: 'warning',
    LOW: 'info'
  }
  return types[priority] || ''
}

function getStatusLabel(status) {
  const labels = {
    PENDING_ASSIGN: '待分派',
    ASSIGNED: '已分派',
    PROCESSING: '处理中',
    PENDING_CONFIRM: '待确认',
    COMPLETED: '已完成',
    RETURNED: '已退回',
    CLOSED: '已关闭'
  }
  return labels[status] || status
}

function getStatusTagType(status) {
  const types = {
    PENDING_ASSIGN: 'warning',
    ASSIGNED: 'primary',
    PROCESSING: 'info',
    PENDING_CONFIRM: 'danger',
    COMPLETED: 'success',
    RETURNED: 'warning',
    CLOSED: 'info'
  }
  return types[status] || ''
}

function canAssign(row) {
  return row && props.currentRole === 'CUSTOMER_SERVICE' && row.status === 'PENDING_ASSIGN'
}

function canAccept(row) {
  return row && props.currentRole === 'PROCESSOR' &&
         row.status === 'ASSIGNED' &&
         row.processor === props.currentUser
}

function canSubmitResult(row) {
  return row && props.currentRole === 'PROCESSOR' &&
         (row.status === 'PROCESSING' || row.status === 'RETURNED') &&
         row.processor === props.currentUser
}

function canConfirm(row) {
  return row && props.currentRole === 'CUSTOMER' &&
         row.status === 'PENDING_CONFIRM' &&
         row.creator === props.currentUser
}

function canReturn(row) {
  return row && props.currentRole === 'CUSTOMER' &&
         row.status === 'PENDING_CONFIRM' &&
         row.creator === props.currentUser
}

function canClose(row) {
  return row && props.currentRole === 'CUSTOMER_SERVICE' &&
         row.status !== 'COMPLETED' &&
         row.status !== 'CLOSED'
}
</script>
