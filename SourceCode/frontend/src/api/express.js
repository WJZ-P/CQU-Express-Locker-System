import request from '@/utils/request'

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
