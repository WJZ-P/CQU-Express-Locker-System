import request from '@/utils/request'

// 获取快递柜列表
export function getLockerList(params) {
  return request.get('/locker/list', { params })
}

// 获取快递柜详情
export function getLockerDetail(id) {
  return request.get(`/locker/${id}`)
}

// 新增快递柜
export function addLocker(data) {
  return request.post('/locker', data)
}

// 更新快递柜
export function updateLocker(id, data) {
  return request.put(`/locker/${id}`, data)
}

// 删除快递柜
export function deleteLocker(id) {
  return request.delete(`/locker/${id}`)
}

// 启用/禁用快递柜
export function toggleLockerStatus(id, status) {
  return request.put(`/locker/${id}/status`, { status })
}

// 获取仓门列表
export function getCompartmentList(lockerId) {
  return request.get('/compartment/list', { params: { lockerId } })
}

// 更新仓门
export function updateCompartment(id, data) {
  return request.put(`/compartment/${id}`, data)
}

// 远程开门
export function openCompartment(id) {
  return request.post(`/compartment/${id}/open`)
}
