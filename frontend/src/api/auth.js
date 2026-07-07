import request from './request'

// 认证与平台管理接口
export const authApi = {
  // 登录
  login: (data) => request.post('/auth/login', data),
  // 退出
  logout: () => request.post('/auth/logout')
}

// 平台租户（商家）管理接口
export const platformApi = {
  // 开通商家
  openTenant: (data) => request.post('/platform/tenant', data),
  // 分页查询商家
  page: (params) => request.get('/platform/tenant/page', { params }),
  // 启用商家
  enable: (id) => request.post(`/platform/tenant/${id}/enable`),
  // 停用商家
  disable: (id) => request.post(`/platform/tenant/${id}/disable`),
  // 续期商家
  renew: (id, years) => request.post(`/platform/tenant/${id}/renew?years=${years}`)
}
