<template>
  <div class="system-config">
    <el-card shadow="hover">
      <template #header>系统配置</template>
      <el-form :model="configForm" label-width="150px" style="max-width: 600px">
        <el-form-item label="系统名称">
          <el-input v-model="configForm.systemName" />
        </el-form-item>
        <el-form-item label="取件超时时间(小时)">
          <el-input-number v-model="configForm.pickupTimeout" :min="1" :max="72" />
        </el-form-item>
        <el-form-item label="寄存超时时间(小时)">
          <el-input-number v-model="configForm.storageTimeout" :min="1" :max="48" />
        </el-form-item>
        <el-form-item label="短信通知">
          <el-switch v-model="configForm.smsNotify" />
        </el-form-item>
        <el-form-item label="超时提醒">
          <el-switch v-model="configForm.timeoutReminder" />
        </el-form-item>
        <el-form-item label="人脸识别">
          <el-switch v-model="configForm.faceRecognition" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSave">保存配置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getSystemConfig, updateSystemConfig } from '@/api/system'

const configForm = reactive({
  systemName: '快递柜综合应用系统',
  pickupTimeout: 24,
  storageTimeout: 12,
  smsNotify: true,
  timeoutReminder: true,
  faceRecognition: false
})

onMounted(async () => {
  try {
    const res = await getSystemConfig()
    Object.assign(configForm, res.data)
  } catch(e) { console.error(e) }
})

const handleSave = async () => {
  try {
    await updateSystemConfig(configForm)
    ElMessage.success('配置保存成功')
  } catch(e) {
    ElMessage.error('保存失败')
    console.error(e)
  }
}
</script>

<style lang="scss" scoped>
.system-config {
  max-width: 800px;
}
</style>
