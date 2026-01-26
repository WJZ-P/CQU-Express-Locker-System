import request from '@/utils/request'

// ============ 移动端用户API（保留） ============

// V1 API - 获取用户信息
export function getUserProfile() {
  return request.get('/user/profile')
}

// V1 API - 更新用户信息
export function updateUserProfile(data) {
  return request.put('/user/profile', data)
}

// ============ 管理端用户API ============

// 获取用户列表（分页）
export function getUserList(params) {
  return request.get('/admin/user/list', { params })
}

// 获取用户详情
export function getUserDetail(id) {
  return request.get(`/admin/user/${id}`)
}

// 新增用户
export function addUser(data) {
  return request.post('/admin/user', data)
}

// 更新用户
export function updateUser(id, data) {
  return request.put(`/admin/user/${id}`, data)
}

// 删除用户
export function deleteUser(id) {
  return request.delete(`/admin/user/${id}`)
}

// 启用/禁用用户
export function toggleUserStatus(id, status) {
  return request.put(`/admin/user/${id}/status`, null, { params: { status } })
}

// 重置密码
export function resetUserPassword(id, password) {
  return request.post(`/admin/user/${id}/reset-password`, null, { params: { password } })
}

// ============ 管理端快递员API ============

// 获取快递员列表（分页）
export function getCourierList(params) {
  return request.get('/admin/courier/list', { params })
}

// 获取快递员详情
export function getCourierDetail(id) {
  return request.get(`/admin/courier/${id}`)
}

// 新增快递员
export function addCourier(data) {
  return request.post('/admin/courier', data)
}

// 更新快递员
export function updateCourier(id, data) {
  return request.put(`/admin/courier/${id}`, data)
}

// 删除快递员
export function deleteCourier(id) {
  return request.delete(`/admin/courier/${id}`)
}

// 更新快递员状态（在职/离职）
export function toggleCourierStatus(id, status) {
  return request.put(`/admin/courier/${id}/status`, null, { params: { status } })
}

// 重置快递员密码
export function resetCourierPassword(id) {
  return request.post(`/admin/courier/${id}/reset-password`)
}
