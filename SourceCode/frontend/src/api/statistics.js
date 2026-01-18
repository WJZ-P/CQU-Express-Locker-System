import request from '@/utils/request'

// 获取首页统计数据
export function getDashboardStats() {
  return request.get('/statistics/dashboard')
}

// 获取快递趋势数据
export function getExpressTrend() {
  return request.get('/statistics/express-trend')
}

// 获取快递公司分布
export function getCompanyDistribution() {
  return request.get('/statistics/company-distribution')
}

// 获取仓门使用率
export function getLockerUsage() {
  return request.get('/statistics/locker-usage')
}
