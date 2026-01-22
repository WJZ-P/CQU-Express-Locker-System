import request from '@/utils/request'

// 登录
export function login(data) {
  return request.post('/auth/login', data)
}

// 登出
export function logout() {
  return request.post('/auth/logout')
}

// 获取当前用户信息
export function getUserInfo() {
  return request.get('/auth/userinfo')
}

// 发送验证码
export function sendCode(data) {
  return request.post('/auth/send-code', data)
}

// 注册
export function register(data) {
  return request.post('/auth/register', data)
}
