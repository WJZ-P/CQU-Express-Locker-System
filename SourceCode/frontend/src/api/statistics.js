import request from '@/utils/request'

// 获取首页统计数据
export function getDashboardStats() {
  return request.get('/statistics/dashboard')
}

// 获取快递趋势数据
export function getExpressTrend(params) {
  return request.get('/statistics/express-trend', { params })
}

// 获取用电量统计
export function getElectricityStats() {
  return request.get('/statistics/electricity')
}

// 获取快递公司占比
export function getCompanyRatio() {
  return request.get('/statistics/company-ratio')
}

// 获取仓门使用率
export function getUsageRate() {
  return request.get('/statistics/usage-rate')
}
