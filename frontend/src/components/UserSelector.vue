<template>
  <div class="user-selector">
    <el-select
      v-model="selectedRole"
      placeholder="选择角色"
      style="width: 120px; margin-right: 10px"
      @change="handleRoleChange"
    >
      <el-option label="客户" value="CUSTOMER" />
      <el-option label="客服" value="CUSTOMER_SERVICE" />
      <el-option label="处理人" value="PROCESSOR" />
    </el-select>
    <el-select
      v-model="selectedUser"
      placeholder="选择用户"
      style="width: 120px"
      :disabled="!selectedRole"
      @change="handleUserChange"
    >
      <el-option
        v-for="user in userList"
        :key="user"
        :label="user"
        :value="user"
      />
    </el-select>
    <el-tag v-if="selectedRole && selectedUser" type="success" style="margin-left: 10px">
      {{ getRoleLabel(selectedRole) }} - {{ selectedUser }}
    </el-tag>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  currentRole: String,
  currentUser: String,
  usersByRole: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['change-role', 'change-user'])

const selectedRole = ref('')
const selectedUser = ref('')

const userList = computed(() => {
  if (!selectedRole.value) return []
  return props.usersByRole[selectedRole.value] || []
})

watch(() => props.currentRole, (val) => {
  selectedRole.value = val
})

watch(() => props.currentUser, (val) => {
  selectedUser.value = val
})

function handleRoleChange(val) {
  selectedUser.value = ''
  emit('change-role', val)
}

function handleUserChange(val) {
  emit('change-user', val)
}

function getRoleLabel(role) {
  const labels = {
    CUSTOMER: '客户',
    CUSTOMER_SERVICE: '客服',
    PROCESSOR: '处理人'
  }
  return labels[role] || role
}
</script>

<style scoped>
.user-selector {
  display: flex;
  align-items: center;
}
</style>
