<template>
  <el-dialog
    v-model="dialogVisible"
    title="关闭工单"
    width="500px"
    :close-on-click-modal="false"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
      <el-form-item label="关闭原因" prop="closeReason">
        <el-input
          v-model="form.closeReason"
          type="textarea"
          :rows="4"
          placeholder="请输入关闭原因"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="danger" @click="handleSubmit" :loading="loading">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, computed } from 'vue'
import { workOrderApi } from '../api/workOrderApi.js'

const props = defineProps({
  visible: Boolean,
  workOrder: Object,
  currentUser: String
})

const emit = defineEmits(['update:visible', 'success'])

const formRef = ref(null)
const loading = ref(false)

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

const form = reactive({
  closeReason: ''
})

const rules = {
  closeReason: [{ required: true, message: '请输入关闭原因', trigger: 'blur' }]
}

watch(() => props.visible, (val) => {
  if (val) {
    form.closeReason = ''
    formRef.value?.clearValidate()
  }
})

async function handleSubmit() {
  try {
    await formRef.value.validate()
    loading.value = true
    await workOrderApi.close(props.workOrder.id, form, { operator: props.currentUser })
    emit('success')
  } catch (e) {
    if (e !== false) {
      console.error('关闭失败', e)
    }
  } finally {
    loading.value = false
  }
}
</script>
