import request from '@/utils/request'

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
export function openCompartment(data) {
  return request.post('/express/open', data)
}

// V1 API - 寄件
export function sendExpress(data) {
  return request.post('/express/send', data)
}

// 获取快递列表
export function getExpressList(params) {
  return request.get('/express/list', { params })
}

// 获取快递详情
export function getExpressDetail(id) {
  return request.get(`/express/${id}`)
}

// 获取寄存记录列表
export function getStorageList(params) {
  return request.get('/storage/list', { params })
}

// 获取寄存详情
export function getStorageDetail(id) {
  return request.get(`/storage/${id}`)
}
