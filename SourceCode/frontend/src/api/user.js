import request from '@/utils/request'

// V1 API - 获取用户信息
export function getUserProfile() {
  return request.get('/user/profile')
}

// V1 API - 更新用户信息
export function updateUserProfile(data) {
  return request.put('/user/profile', data)
}

// 获取用户列表
export function getUserList(params) {
  return request.get('/user/list', { params })
}

// 新增用户
export function addUser(data) {
  return request.post('/user', data)
}

// 更新用户
export function updateUser(id, data) {
  return request.put(`/user/${id}`, data)
}

// 删除用户
export function deleteUser(id) {
  return request.delete(`/user/${id}`)
}

// 启用/禁用用户
export function toggleUserStatus(id, status) {
  return request.put(`/user/${id}/status`, { status })
}

// 获取快递员列表
export function getCourierList(params) {
  return request.get('/courier/list', { params })
}

// 新增快递员
export function addCourier(data) {
  return request.post('/courier', data)
}

// 更新快递员
export function updateCourier(id, data) {
  return request.put(`/courier/${id}`, data)
}

// 删除快递员
export function deleteCourier(id) {
  return request.delete(`/courier/${id}`)
}
