import request from '@/utils/request'

// 获取仪表盘统计数据
export function getDashboardStats() {
  return request.get('/admin/statistics/dashboard')
}

// 获取订单趋势数据
export function getOrderTrend(days = 7) {
  return request.get('/admin/statistics/order-trend', { params: { days } })
}

// 获取趋势数据（统一接口）
export function getTrendData(params) {
  return request.get('/admin/statistics/order-trend', { params })
}

// 获取分布数据（格口使用分布）
export function getDistributionData() {
  return request.get('/admin/statistics/locker-usage-ranking', { params: { limit: 10 } })
}

// 获取快递公司分布
export function getCompanyDistribution() {
  return request.get('/admin/statistics/company-distribution')
}

// 获取快递公司统计（用于图表）
export function getCompanyStats() {
  return request.get('/admin/statistics/company-distribution')
}

// 获取快递柜使用排行
export function getLockerUsageRanking(limit = 10) {
  return request.get('/admin/statistics/locker-usage-ranking', { params: { limit } })
}

// 获取每日订单统计
export function getDailyOrderStats(startDate, endDate) {
  return request.get('/admin/statistics/daily-orders', { params: { startDate, endDate } })
}

// 获取每小时订单分布
export function getHourlyOrderDistribution() {
  return request.get('/admin/statistics/hourly-distribution')
}

// 获取月度统计数据
export function getMonthlyStats() {
  return request.get('/admin/statistics/monthly-orders')
}

// 获取格口使用率统计
export function getBoxUsageStats() {
  return request.get('/admin/statistics/box-usage')
}

// 获取用电量统计
export function getPowerStats() {
  return request.get('/admin/statistics/power-stats')
}

// ============ 兼容旧接口名称 ============

// 获取快递趋势数据（兼容）
export function getExpressTrend(params) {
  return getOrderTrend(params?.days || 7)
}

// 获取快递公司占比（兼容）
export function getCompanyRatio() {
  return getCompanyDistribution()
}

// 获取仓门使用率（兼容）
export function getUsageRate() {
  return getLockerUsageRanking()
}

// 获取用电量统计（旧名称兼容）
export function getElectricityStats() {
  return getPowerStats()
}
