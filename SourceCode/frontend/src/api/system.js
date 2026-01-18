import request from '@/utils/request'

// 获取系统配置
export function getSystemConfig() {
  return request.get('/system/config')
}

// 更新系统配置
export function updateSystemConfig(data) {
  return request.put('/system/config', data)
}
