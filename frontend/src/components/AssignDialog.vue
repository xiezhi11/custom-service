<template>
  <el-dialog
    v-model="dialogVisible"
    title="分派工单"
    width="400px"
    :close-on-click-modal="false"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
      <el-form-item label="处理人" prop="processor">
        <el-select v-model="form.processor" placeholder="请选择处理人" style="width: 100%">
          <el-option
            v-for="p in processors"
            :key="p"
            :label="p"
            :value="p"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, computed } from 'vue'
import { workOrderApi } from '../api/workOrderApi.js'

const props = defineProps({
  visible: Boolean,
  workOrder: Object,
  processors: {
    type: Array,
    default: () => []
  },
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
  processor: ''
})

const rules = {
  processor: [{ required: true, message: '请选择处理人', trigger: 'change' }]
}

watch(() => props.visible, (val) => {
  if (val) {
    form.processor = ''
    formRef.value?.clearValidate()
  }
})

async function handleSubmit() {
  try {
    await formRef.value.validate()
    loading.value = true
    await workOrderApi.assign(props.workOrder.id, form, { operator: props.currentUser })
    emit('success')
  } catch (e) {
    if (e !== false) {
      console.error('分派失败', e)
    }
  } finally {
    loading.value = false
  }
}
</script>
