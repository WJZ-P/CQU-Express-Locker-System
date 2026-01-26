<template>
  <div class="system-config">
    <el-card shadow="hover">
      <template #header>系统配置</template>
      <el-form :model="configForm" label-width="150px" style="max-width: 600px" v-loading="loading">
        <el-form-item label="系统名称">
          <el-input v-model="configForm.systemName" placeholder="请输入系统名称" />
        </el-form-item>
        <el-form-item label="取件超时时间(小时)">
          <el-input-number v-model="configForm.pickupTimeout" :min="1" :max="168" />
          <span class="help-text">超过此时间的订单将标记为超时</span>
        </el-form-item>
        <el-form-item label="取件码有效时长(分钟)">
          <el-input-number v-model="configForm.codeExpireMinutes" :min="5" :max="60" />
          <span class="help-text">取件码的有效期</span>
        </el-form-item>
        <el-form-item label="短信通知">
          <el-switch v-model="configForm.smsNotify" />
          <span class="help-text">开启后会发送短信通知收件人</span>
        </el-form-item>
        <el-form-item label="超时提醒">
          <el-switch v-model="configForm.timeoutReminder" />
          <span class="help-text">在快递即将超时前发送提醒</span>
        </el-form-item>
        <el-form-item label="默认用户密码">
          <el-input v-model="configForm.defaultUserPassword" placeholder="请输入默认密码" type="password" show-password />
          <span class="help-text">重置密码时使用的默认密码</span>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="saving">保存配置</el-button>
          <el-button @click="loadConfig">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card shadow="hover" style="margin-top: 20px">
      <template #header>系统信息</template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="系统版本">v2.0.0</el-descriptions-item>
        <el-descriptions-item label="后端版本">Spring Boot 3.x</el-descriptions-item>
        <el-descriptions-item label="数据库">MySQL</el-descriptions-item>
        <el-descriptions-item label="服务器时间">{{ serverTime }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const saving = ref(false)
const serverTime = ref(new Date().toLocaleString())

const configForm = reactive({
  systemName: '快递柜综合应用系统',
  pickupTimeout: 24,
  codeExpireMinutes: 30,
  smsNotify: true,
  timeoutReminder: true,
  defaultUserPassword: ''
})

const loadConfig = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/config/list')
    if (res.data && Array.isArray(res.data)) {
      res.data.forEach(item => {
        if (item.configKey === 'system.name') {
          configForm.systemName = item.configValue
        } else if (item.configKey === 'pickup.timeout.hours') {
          configForm.pickupTimeout = parseInt(item.configValue) || 24
        } else if (item.configKey === 'code.expire.minutes') {
          configForm.codeExpireMinutes = parseInt(item.configValue) || 30
        } else if (item.configKey === 'sms.notify.enabled') {
          configForm.smsNotify = item.configValue === 'true'
        } else if (item.configKey === 'timeout.reminder.enabled') {
          configForm.timeoutReminder = item.configValue === 'true'
        } else if (item.configKey === 'default.user.password') {
          configForm.defaultUserPassword = item.configValue
        }
      })
    }
  } catch (error) {
    console.error('加载配置失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSave = async () => {
  saving.value = true
  try {
    const configs = [
      { configKey: 'system.name', configValue: configForm.systemName },
      { configKey: 'pickup.timeout.hours', configValue: String(configForm.pickupTimeout) },
      { configKey: 'code.expire.minutes', configValue: String(configForm.codeExpireMinutes) },
      { configKey: 'sms.notify.enabled', configValue: String(configForm.smsNotify) },
      { configKey: 'timeout.reminder.enabled', configValue: String(configForm.timeoutReminder) },
      { configKey: 'default.user.password', configValue: configForm.defaultUserPassword }
    ]
    
    await request.post('/admin/config/batch', configs)
    ElMessage.success('配置保存成功')
  } catch (error) {
    console.error('保存配置失败:', error)
  } finally {
    saving.value = false
  }
}

// 更新服务器时间
const updateServerTime = () => {
  serverTime.value = new Date().toLocaleString()
}

onMounted(() => {
  loadConfig()
  setInterval(updateServerTime, 1000)
})
</script>

<style lang="scss" scoped>
.system-config {
  max-width: 900px;
  
  .help-text {
    margin-left: 10px;
    color: #909399;
    font-size: 12px;
  }
}
</style>
