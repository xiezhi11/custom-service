<template>
  <el-card>
    <el-table :data="workOrders" stripe style="width: 100%">
      <el-table-column prop="orderNo" label="工单编号" width="160" />
      <el-table-column prop="customerName" label="客户名称" width="100" />
      <el-table-column prop="contactInfo" label="联系方式" width="130" />
      <el-table-column label="问题类型" width="100">
        <template #default="{ row }">
          {{ getProblemTypeLabel(row.problemType) }}
        </template>
      </el-table-column>
      <el-table-column label="优先级" width="80">
        <template #default="{ row }">
          <el-tag :type="getPriorityTagType(row.priority)">
            {{ getPriorityLabel(row.priority) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="处理状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusTagType(row.status)">
            {{ getStatusLabel(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="processor" label="处理人" width="100" />
      <el-table-column prop="createTime" label="创建时间" width="160" />
      <el-table-column label="操作" width="300" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="$emit('view', row)">
            查看
          </el-button>
          <template v-if="canAssign(row)">
            <el-button type="success" link @click="$emit('assign', row)">
              分派
            </el-button>
          </template>
          <template v-if="canAccept(row)">
            <el-button type="success" link @click="$emit('accept', row)">
              接单
            </el-button>
          </template>
          <template v-if="canSubmitResult(row)">
            <el-button type="primary" link @click="$emit('submit', row)">
              提交结果
            </el-button>
          </template>
          <template v-if="canConfirm(row)">
            <el-button type="success" link @click="$emit('confirm', row)">
              确认完成
            </el-button>
          </template>
          <template v-if="canReturn(row)">
            <el-button type="warning" link @click="$emit('return', row)">
              退回
            </el-button>
          </template>
          <template v-if="canClose(row)">
            <el-button type="danger" link @click="$emit('close', row)">
              关闭
            </el-button>
          </template>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
const props = defineProps({
  workOrders: {
    type: Array,
    default: () => []
  },
  currentRole: String,
  currentUser: String
})

defineEmits(['view', 'assign', 'accept', 'submit', 'confirm', 'return', 'close'])

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
  return props.currentRole === 'CUSTOMER_SERVICE' && row.status === 'PENDING_ASSIGN'
}

function canAccept(row) {
  return props.currentRole === 'PROCESSOR' &&
         row.status === 'ASSIGNED' &&
         row.processor === props.currentUser
}

function canSubmitResult(row) {
  return props.currentRole === 'PROCESSOR' &&
         (row.status === 'PROCESSING' || row.status === 'RETURNED') &&
         row.processor === props.currentUser
}

function canConfirm(row) {
  return props.currentRole === 'CUSTOMER' &&
         row.status === 'PENDING_CONFIRM' &&
         row.creator === props.currentUser
}

function canReturn(row) {
  return props.currentRole === 'CUSTOMER' &&
         row.status === 'PENDING_CONFIRM' &&
         row.creator === props.currentUser
}

function canClose(row) {
  return props.currentRole === 'CUSTOMER_SERVICE' &&
         row.status !== 'COMPLETED' &&
         row.status !== 'CLOSED'
}
</script>
