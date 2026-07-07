import axios from 'axios'
import { ElMessage } from 'element-plus'

// 后端统一成功码
const CODE_SUCCESS = '0'
// 未认证错误码
const CODE_UNAUTHENTICATED = 'AUTH_UNAUTHENTICATED'

// 创建 Axios 实例
const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 请求拦截器：自动带上 JWT 令牌
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 统一处理未认证：清除本地登录态并跳转登录页
function handleUnauthenticated() {
  localStorage.removeItem('token')
  localStorage.removeItem('loginName')
  localStorage.removeItem('role')
  localStorage.removeItem('tenantId')
  if (window.location.pathname !== '/login') {
    window.location.href = '/login'
  }
}

// 响应拦截器：解析 RestApiResultVo，成功返回 data、失败弹出错误码消息
request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res && res.code === CODE_SUCCESS) {
      return res.data
    }
    const message = (res && res.message) || '请求失败'
    // 业务层返回的未认证
    if (res && res.code === CODE_UNAUTHENTICATED) {
      handleUnauthenticated()
    }
    ElMessage.error(message)
    return Promise.reject(new Error(message))
  },
  (error) => {
    // HTTP 401：未认证/令牌过期
    if (error.response && error.response.status === 401) {
      ElMessage.error('未登录或登录已过期')
      handleUnauthenticated()
      return Promise.reject(error)
    }
    const message = error.message || '网络异常，请稍后重试'
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default request
