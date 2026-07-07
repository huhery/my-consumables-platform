import { defineStore } from 'pinia'

// 角色常量
export const ROLE_PLATFORM_ADMIN = 1
export const ROLE_TENANT_ADMIN = 2

// 登录态存储（token 持久化到 localStorage）
export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    loginName: localStorage.getItem('loginName') || '',
    role: Number(localStorage.getItem('role')) || 0,
    tenantId: localStorage.getItem('tenantId') || ''
  }),
  getters: {
    // 是否已登录
    isLoggedIn: (state) => !!state.token,
    // 是否平台管理员
    isPlatformAdmin: (state) => state.role === ROLE_PLATFORM_ADMIN
  },
  actions: {
    // 保存登录结果
    setAuth(result) {
      this.token = result.token
      this.loginName = result.loginName
      this.role = result.role
      this.tenantId = result.tenantId || ''
      localStorage.setItem('token', this.token)
      localStorage.setItem('loginName', this.loginName)
      localStorage.setItem('role', String(this.role))
      localStorage.setItem('tenantId', this.tenantId)
    },
    // 清除登录态
    clear() {
      this.token = ''
      this.loginName = ''
      this.role = 0
      this.tenantId = ''
      localStorage.removeItem('token')
      localStorage.removeItem('loginName')
      localStorage.removeItem('role')
      localStorage.removeItem('tenantId')
    }
  }
})
