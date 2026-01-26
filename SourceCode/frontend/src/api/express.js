import request from '@/utils/request'

// ============ 移动端快递API（保留） ============

// V1 API - 获取待取快递列表
export function getPendingExpressList() {
  return request.get('/express/pending')
}

// V1 API - 获取快递详情
export function getExpressDetailV1(expressId) {
  return request.get(`/express/${expressId}`)
}

// V1 API - 取件（验证取件码）
export function pickupExpress(data) {
  return request.post('/express/pickup', data)
}

// V1 API - 开柜（已验证后再次开柜）
export function openExpressCompartment(data) {
  return request.post('/express/open', data)
}

// V1 API - 寄件
export function sendExpress(data) {
  return request.post('/express/send', data)
}

// ============ 管理端快递API ============

// 获取快递订单列表（分页）
export function getExpressList(params) {
  return request.get('/admin/express/list', { params })
}

// 获取订单列表（别名）
export function getOrderList(params) {
  return request.get('/admin/express/list', { params })
}

// 获取快递订单详情
export function getExpressDetail(id) {
  return request.get(`/admin/express/${id}`)
}

// 删除快递订单
export function deleteExpress(id) {
  return request.delete(`/admin/express/${id}`)
}

// 强制取件
export function forcePickup(id) {
  return request.post(`/admin/express/${id}/force-pickup`)
}

// 标记超时
export function markOvertime(id) {
  return request.post(`/admin/express/${id}/mark-overtime`)
}

// 退回快递
export function returnExpress(id) {
  return request.post(`/admin/express/${id}/return`)
}

// ============ 寄存管理API ============

// 获取寄存记录列表
export function getStorageList(params) {
  return request.get('/admin/express/list', { params: { ...params, type: 3 } })
}

// 获取寄存详情
export function getStorageDetail(id) {
  return request.get(`/admin/express/${id}`)
}
