<template>
  <el-dialog
    v-model="dialogVisible"
    title="提交处理结果"
    width="500px"
    :close-on-click-modal="false"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
      <el-form-item label="处理结果" prop="processResult">
        <el-input
          v-model="form.processResult"
          type="textarea"
          :rows="6"
          placeholder="请输入处理结果"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">提交</el-button>
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
  processResult: ''
})

const rules = {
  processResult: [{ required: true, message: '请输入处理结果', trigger: 'blur' }]
}

watch(() => props.visible, (val) => {
  if (val) {
    form.processResult = props.workOrder?.processResult || ''
    formRef.value?.clearValidate()
  }
})

async function handleSubmit() {
  try {
    await formRef.value.validate()
    loading.value = true
    await workOrderApi.submitResult(props.workOrder.id, form, { processor: props.currentUser })
    emit('success')
  } catch (e) {
    if (e !== false) {
      console.error('提交失败', e)
    }
  } finally {
    loading.value = false
  }
}
</script>
