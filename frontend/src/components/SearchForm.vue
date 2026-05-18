<template>
  <el-card class="search-form">
    <el-form :inline="true" :model="form" class="search-form-content">
      <el-form-item label="工单编号">
        <el-input v-model="form.orderNo" placeholder="请输入工单编号" clearable />
      </el-form-item>
      <el-form-item label="问题类型">
        <el-select v-model="form.problemType" placeholder="请选择" clearable>
          <el-option
            v-for="item in problemTypes"
            :key="item.name"
            :label="item.description"
            :value="item.name"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="优先级">
        <el-select v-model="form.priority" placeholder="请选择" clearable>
          <el-option
            v-for="item in priorities"
            :key="item.name"
            :label="item.description"
            :value="item.name"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="处理状态">
        <el-select v-model="form.status" placeholder="请选择" clearable>
          <el-option
            v-for="item in statuses"
            :key="item.name"
            :label="item.description"
            :value="item.name"
          />
        </el-select>
      </el-form-item>
      <el-form-item v-if="showCustomerFilter" label="客户名称">
        <el-input v-model="form.customerName" placeholder="请输入客户名称" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon> 查询
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon> 重置
        </el-button>
        <el-button type="success" @click="$emit('create')">
          <el-icon><Plus /></el-icon> 创建工单
        </el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { reactive } from 'vue'

defineProps({
  problemTypes: {
    type: Array,
    default: () => []
  },
  priorities: {
    type: Array,
    default: () => []
  },
  statuses: {
    type: Array,
    default: () => []
  },
  showCustomerFilter: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['search', 'reset', 'create'])

const form = reactive({
  orderNo: '',
  customerName: '',
  problemType: '',
  priority: '',
  status: ''
})

function handleSearch() {
  emit('search', { ...form })
}

function handleReset() {
  form.orderNo = ''
  form.customerName = ''
  form.problemType = ''
  form.priority = ''
  form.status = ''
  emit('reset')
}
</script>

<style scoped>
.search-form {
  margin-bottom: 20px;
}

.search-form-content {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}
</style>
