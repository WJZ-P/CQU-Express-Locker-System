import request from '@/utils/request'

// ============ 管理端API ============

// 获取快递柜列表（分页）
export function getLockerList(params) {
  return request.get('/admin/locker/list', { params })
}

// 获取快递柜详情
export function getLockerDetail(id) {
  return request.get(`/admin/locker/${id}`)
}

// 新增快递柜
export function addLocker(data) {
  return request.post('/admin/locker', data)
}

// 更新快递柜
export function updateLocker(id, data) {
  return request.put(`/admin/locker/${id}`, data)
}

// 删除快递柜
export function deleteLocker(id) {
  return request.delete(`/admin/locker/${id}`)
}

// 启用/禁用快递柜
export function toggleLockerEnabled(id, enabled) {
  return request.put(`/admin/locker/${id}/enabled`, null, { params: { enabled } })
}

// ============ 格口管理API ============

// 获取格口列表（分页）
export function getCompartmentList(params) {
  return request.get('/admin/compartment/list', { params })
}

// 获取格口详情
export function getCompartmentDetail(id) {
  return request.get(`/admin/compartment/${id}`)
}

// 新增格口
export function addCompartment(data) {
  return request.post('/admin/compartment', data)
}

// 更新格口
export function updateCompartment(id, data) {
  return request.put(`/admin/compartment/${id}`, data)
}

// 删除格口
export function deleteCompartment(id) {
  return request.delete(`/admin/compartment/${id}`)
}

// 启用/禁用格口
export function toggleCompartmentEnabled(id, enabled) {
  return request.put(`/admin/compartment/${id}/enabled`, null, { params: { enabled } })
}

// 远程开门
export function openCompartment(id) {
  return request.post(`/admin/compartment/${id}/open`)
}

// 标记故障
export function markCompartmentFault(id, reason) {
  return request.post(`/admin/compartment/${id}/fault`, null, { params: { reason } })
}

// 清除故障
export function clearCompartmentFault(id) {
  return request.post(`/admin/compartment/${id}/clear-fault`)
}
