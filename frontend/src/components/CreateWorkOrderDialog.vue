<template>
  <el-dialog
    v-model="dialogVisible"
    title="创建工单"
    width="500px"
    :close-on-click-modal="false"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="客户名称" prop="customerName">
        <el-input v-model="form.customerName" placeholder="请输入客户名称" />
      </el-form-item>
      <el-form-item label="联系方式" prop="contactInfo">
        <el-input v-model="form.contactInfo" placeholder="请输入联系方式" />
      </el-form-item>
      <el-form-item label="问题类型" prop="problemType">
        <el-select v-model="form.problemType" placeholder="请选择" style="width: 100%">
          <el-option
            v-for="item in problemTypes"
            :key="item.name"
            :label="item.description"
            :value="item.name"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="问题描述" prop="problemDescription">
        <el-input
          v-model="form.problemDescription"
          type="textarea"
          :rows="4"
          placeholder="请详细描述问题"
        />
      </el-form-item>
      <el-form-item label="优先级" prop="priority">
        <el-select v-model="form.priority" placeholder="请选择" style="width: 100%">
          <el-option
            v-for="item in priorities"
            :key="item.name"
            :label="item.description"
            :value="item.name"
          />
        </el-select>
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
  problemTypes: {
    type: Array,
    default: () => []
  },
  priorities: {
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
  customerName: '',
  contactInfo: '',
  problemType: '',
  problemDescription: '',
  priority: ''
})

const rules = {
  customerName: [{ required: true, message: '请输入客户名称', trigger: 'blur' }],
  contactInfo: [{ required: true, message: '请输入联系方式', trigger: 'blur' }],
  problemType: [{ required: true, message: '请选择问题类型', trigger: 'change' }],
  problemDescription: [{ required: true, message: '请输入问题描述', trigger: 'blur' }],
  priority: [{ required: true, message: '请选择优先级', trigger: 'change' }]
}

watch(() => props.visible, (val) => {
  if (val) {
    form.customerName = props.currentUser || ''
    form.contactInfo = ''
    form.problemType = ''
    form.problemDescription = ''
    form.priority = ''
    formRef.value?.clearValidate()
  }
})

async function handleSubmit() {
  try {
    await formRef.value.validate()
    loading.value = true
    await workOrderApi.create(form, { currentUser: props.currentUser })
    emit('success')
  } catch (e) {
    if (e !== false) {
      console.error('创建工单失败', e)
    }
  } finally {
    loading.value = false
  }
}
</script>
