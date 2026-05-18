<template>
  <el-row :gutter="20" class="statistics-panel">
    <el-col :span="4">
      <el-card shadow="hover" class="stat-card total">
        <div class="stat-label">工单总数</div>
        <div class="stat-value">{{ statistics.total || 0 }}</div>
      </el-card>
    </el-col>
    <el-col :span="4">
      <el-card shadow="hover" class="stat-card pending-assign">
        <div class="stat-label">待分派</div>
        <div class="stat-value">{{ statistics.pendingAssign || 0 }}</div>
      </el-card>
    </el-col>
    <el-col :span="4">
      <el-card shadow="hover" class="stat-card processing">
        <div class="stat-label">处理中</div>
        <div class="stat-value">{{ statistics.processing || 0 }}</div>
      </el-card>
    </el-col>
    <el-col :span="4">
      <el-card shadow="hover" class="stat-card pending-confirm">
        <div class="stat-label">待确认</div>
        <div class="stat-value">{{ statistics.pendingConfirm || 0 }}</div>
      </el-card>
    </el-col>
    <el-col :span="4">
      <el-card shadow="hover" class="stat-card completed">
        <div class="stat-label">已完成</div>
        <div class="stat-value">{{ statistics.completed || 0 }}</div>
      </el-card>
    </el-col>
    <el-col :span="4">
      <el-card shadow="hover" class="stat-card closed">
        <div class="stat-label">已关闭</div>
        <div class="stat-value">{{ statistics.closed || 0 }}</div>
      </el-card>
    </el-col>
  </el-row>
</template>

<script setup>
defineProps({
  statistics: {
    type: Object,
    default: () => ({})
  }
})
</script>

<style scoped>
.statistics-panel {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  border-radius: 8px;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
}

.total .stat-value {
  color: #409eff;
}

.pending-assign .stat-value {
  color: #e6a23c;
}

.processing .stat-value {
  color: #909399;
}

.pending-confirm .stat-value {
  color: #f56c6c;
}

.completed .stat-value {
  color: #67c23a;
}

.closed .stat-value {
  color: #909399;
}
</style>
