<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <img src="@/assets/logo.svg" alt="logo" class="logo" />
        <h1>快递柜综合应用系统</h1>
        <p>Web管理平台</p>
      </div>
      
      <!-- 登录表单 -->
      <el-form v-if="!showRegister" ref="loginFormRef" :model="loginForm" :rules="loginRules" class="login-form">
        <el-form-item prop="phone">
          <el-input
            v-model="loginForm.phone"
            placeholder="请输入手机号"
            size="large"
            prefix-icon="Phone"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="login-btn"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-button
            text
            type="primary"
            @click="showRegister = true"
          >
            还没有账号？立即注册
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 注册表单 -->
      <el-form v-else ref="registerFormRef" :model="registerForm" :rules="registerRules" class="login-form">
        <el-form-item prop="phone">
          <el-input
            v-model="registerForm.phone"
            placeholder="请输入手机号"
            size="large"
            prefix-icon="Phone"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码（6位以上）"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item prop="verifyCode">
          <div style="display: flex; gap: 10px;">
            <el-input
              v-model="registerForm.verifyCode"
              placeholder="请输入验证码"
              size="large"
              prefix-icon="Message"
              style="flex: 1;"
            />
            <el-button
              size="large"
              :disabled="countdown > 0"
              @click="handleSendCode"
            >
              {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
        <el-form-item prop="userType">
          <el-radio-group v-model="registerForm.userType" size="large">
            <el-radio value="user">普通用户</el-radio>
            <el-radio value="courier">快递员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="login-btn"
            @click="handleRegister"
          >
            注 册
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-button
            text
            type="primary"
            @click="showRegister = false"
          >
            已有账号？立即登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login, register, sendCode } from '@/api/auth'

const router = useRouter()
const loginFormRef = ref()
const registerFormRef = ref()
const loading = ref(false)
const showRegister = ref(false)
const countdown = ref(0)

// 登录表单
const loginForm = reactive({
  phone: '',
  password: ''
})

const loginRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

// 注册表单
const registerForm = reactive({
  phone: '',
  password: '',
  verifyCode: '',
  userType: 'user'
})

const registerRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  verifyCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ],
  userType: [
    { required: true, message: '请选择用户类型', trigger: 'change' }
  ]
}

// 登录
const handleLogin = async () => {
  const valid = await loginFormRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await login(loginForm)
    
    // 保存token和用户信息
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('userId', res.data.userId)
    localStorage.setItem('userType', res.data.userType)
    localStorage.setItem('nickname', res.data.nickname)
    
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}

// 发送验证码
const handleSendCode = async () => {
  // 验证手机号
  if (!registerForm.phone) {
    ElMessage.warning('请输入手机号')
    return
  }
  if (!/^1[3-9]\d{9}$/.test(registerForm.phone)) {
    ElMessage.warning('手机号格式不正确')
    return
  }

  try {
    await sendCode({
      phone: registerForm.phone,
      type: 'register'
    })
    ElMessage.success('验证码已发送，请查看控制台')
    
    // 开始倒计时
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  } catch (error) {
    console.error('发送验证码失败:', error)
  }
}

// 注册
const handleRegister = async () => {
  const valid = await registerFormRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await register(registerForm)
    ElMessage.success('注册成功，请登录')
    
    // 切换到登录表单
    showRegister.value = false
    // 清空注册表单
    registerFormRef.value.resetFields()
  } catch (error) {
    console.error('注册失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;

  .logo {
    width: 64px;
    height: 64px;
    margin-bottom: 16px;
  }

  h1 {
    font-size: 24px;
    color: #303133;
    margin-bottom: 8px;
  }

  p {
    font-size: 14px;
    color: #909399;
  }
}

.login-form {
  .login-btn {
    width: 100%;
  }
}
</style>
