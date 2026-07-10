import request from './request'

// 工作台首页聚合接口
export const dashboardApi = {
  // 获取工作台聚合数据（今日收支、欠款、应付、低库存、待送货、本月销售额）
  summary: () => request.get('/dashboard/summary')
}
