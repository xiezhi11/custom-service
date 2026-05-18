<template>
  <div class="app-container">
    <el-container>
      <el-header class="header">
        <h1>售后工单处理系统</h1>
        <UserSelector
          :currentRole="currentRole"
          :currentUser="currentUser"
          :usersByRole="usersByRole"
          @change-role="handleRoleChange"
          @change-user="handleUserChange"
        />
      </el-header>
      <el-main class="main">
        <StatisticsPanel :statistics="statistics" />

        <SearchForm
          v-if="currentRole"
          :problemTypes="enums.problemTypes"
          :priorities="enums.priorities"
          :statuses="enums.statuses"
          :showCustomerFilter="currentRole === 'CUSTOMER_SERVICE'"
          @search="handleSearch"
          @reset="handleReset"
          @create="showCreateDialog = true"
        />

        <WorkOrderList
          v-if="currentRole"
          :workOrders="workOrders"
          :currentRole="currentRole"
          :currentUser="currentUser"
          @view="handleViewDetail"
          @assign="handleAssign"
          @accept="handleAccept"
          @submit="handleSubmitResult"
          @confirm="handleConfirm"
          @return="handleReturn"
          @close="handleClose"
        />

        <el-empty v-if="!currentRole" description="请先选择角色以查看工单" />
      </el-main>
    </el-container>

    <CreateWorkOrderDialog
      v-model:visible="showCreateDialog"
      :problemTypes="enums.problemTypes"
      :priorities="enums.priorities"
      :currentUser="currentUser"
      @success="handleCreateSuccess"
    />

    <WorkOrderDetailDialog
      v-model:visible="showDetailDialog"
      :workOrder="selectedWorkOrder"
      :logs="operationLogs"
      :currentRole="currentRole"
      :currentUser="currentUser"
      @assign="handleAssign"
      @accept="handleAccept"
      @submit="handleSubmitResult"
      @confirm="handleConfirm"
      @return="handleReturn"
      @close="handleClose"
    />

    <AssignDialog
      v-model:visible="showAssignDialog"
      :workOrder="selectedWorkOrder"
      :processors="usersByRole.PROCESSOR"
      :currentUser="currentUser"
      @success="handleActionSuccess"
    />

    <SubmitResultDialog
      v-model:visible="showSubmitDialog"
      :workOrder="selectedWorkOrder"
      :currentUser="currentUser"
      @success="handleActionSuccess"
    />

    <ReturnDialog
      v-model:visible="showReturnDialog"
      :workOrder="selectedWorkOrder"
      :currentUser="currentUser"
      @success="handleActionSuccess"
    />

    <CloseDialog
      v-model:visible="showCloseDialog"
      :workOrder="selectedWorkOrder"
      :currentUser="currentUser"
      @success="handleActionSuccess"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { workOrderApi } from './api/workOrderApi.js'
import UserSelector from './components/UserSelector.vue'
import StatisticsPanel from './components/StatisticsPanel.vue'
import SearchForm from './components/SearchForm.vue'
import WorkOrderList from './components/WorkOrderList.vue'
import CreateWorkOrderDialog from './components/CreateWorkOrderDialog.vue'
import WorkOrderDetailDialog from './components/WorkOrderDetailDialog.vue'
import AssignDialog from './components/AssignDialog.vue'
import SubmitResultDialog from './components/SubmitResultDialog.vue'
import ReturnDialog from './components/ReturnDialog.vue'
import CloseDialog from './components/CloseDialog.vue'

const currentRole = ref('')
const currentUser = ref('')

const usersByRole = {
  CUSTOMER: ['张三', '李四', '王五', '赵六', '钱七', '孙八', '周九'],
  CUSTOMER_SERVICE: ['客服1', '客服2'],
  PROCESSOR: ['处理人A', '处理人B', '处理人C']
}

const enums = reactive({
  statuses: [],
  problemTypes: [],
  priorities: [],
  roles: []
})

const workOrders = ref([])
const statistics = ref({})
const selectedWorkOrder = ref(null)
const operationLogs = ref([])

const showCreateDialog = ref(false)
const showDetailDialog = ref(false)
const showAssignDialog = ref(false)
const showSubmitDialog = ref(false)
const showReturnDialog = ref(false)
const showCloseDialog = ref(false)

const searchParams = reactive({
  orderNo: '',
  customerName: '',
  problemType: '',
  priority: '',
  status: ''
})

onMounted(() => {
  loadEnums()
})

watch(currentRole, (newRole) => {
  if (newRole) {
    currentUser.value = usersByRole[newRole][0]
    loadData()
  } else {
    currentUser.value = ''
    workOrders.value = []
  }
})

watch(currentUser, () => {
  if (currentRole.value && currentUser.value) {
    loadData()
  }
})

async function loadEnums() {
  try {
    const res = await workOrderApi.getEnums()
    enums.statuses = res.data.statuses
    enums.problemTypes = res.data.problemTypes
    enums.priorities = res.data.priorities
    enums.roles = res.data.roles
  } catch (e) {
    console.error('加载枚举失败', e)
  }
}

async function loadData() {
  await Promise.all([loadWorkOrders(), loadStatistics()])
}

async function loadWorkOrders() {
  try {
    const params = {
      currentUser: currentUser.value,
      role: currentRole.value,
      ...searchParams
    }
    Object.keys(params).forEach(key => {
      if (!params[key] && params[key] !== 0) delete params[key]
    })
    const res = await workOrderApi.getList(params)
    workOrders.value = res.data
  } catch (e) {
    console.error('加载工单列表失败', e)
  }
}

async function loadStatistics() {
  try {
    const res = await workOrderApi.getStatistics()
    statistics.value = res.data
  } catch (e) {
    console.error('加载统计数据失败', e)
  }
}

function handleRoleChange(role) {
  currentRole.value = role
}

function handleUserChange(user) {
  currentUser.value = user
}

function handleSearch(params) {
  Object.assign(searchParams, params)
  loadWorkOrders()
}

function handleReset() {
  Object.assign(searchParams, {
    orderNo: '',
    customerName: '',
    problemType: '',
    priority: '',
    status: ''
  })
  loadWorkOrders()
}

async function handleViewDetail(workOrder) {
  try {
    const [detailRes, logsRes] = await Promise.all([
      workOrderApi.getDetail(workOrder.id, {
        currentUser: currentUser.value,
        role: currentRole.value
      }),
      workOrderApi.getLogs(workOrder.id)
    ])
    selectedWorkOrder.value = detailRes.data
    operationLogs.value = logsRes.data
    showDetailDialog.value = true
  } catch (e) {
    console.error('加载工单详情失败', e)
  }
}

function handleAssign(workOrder) {
  selectedWorkOrder.value = workOrder
  showAssignDialog.value = true
}

function handleAccept(workOrder) {
  ElMessageBox.confirm('确定要接单吗？', '确认', {
    type: 'warning'
  }).then(async () => {
    await workOrderApi.accept(workOrder.id, { processor: currentUser.value })
    ElMessage.success('接单成功')
    handleActionSuccess()
  }).catch(() => {})
}

function handleSubmitResult(workOrder) {
  selectedWorkOrder.value = workOrder
  showSubmitDialog.value = true
}

function handleConfirm(workOrder) {
  ElMessageBox.confirm('确定要确认完成吗？', '确认', {
    type: 'warning'
  }).then(async () => {
    await workOrderApi.confirm(workOrder.id, { operator: currentUser.value })
    ElMessage.success('确认完成成功')
    handleActionSuccess()
  }).catch(() => {})
}

function handleReturn(workOrder) {
  selectedWorkOrder.value = workOrder
  showReturnDialog.value = true
}

function handleClose(workOrder) {
  selectedWorkOrder.value = workOrder
  showCloseDialog.value = true
}

function handleCreateSuccess() {
  showCreateDialog.value = false
  loadData()
  ElMessage.success('创建工单成功')
}

function handleActionSuccess() {
  showAssignDialog.value = false
  showSubmitDialog.value = false
  showReturnDialog.value = false
  showCloseDialog.value = false
  showDetailDialog.value = false
  loadData()
}
</script>

<style scoped>
.app-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.header {
  background-color: #409eff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header h1 {
  margin: 0;
  font-size: 20px;
}

.main {
  padding: 20px;
}
</style>
