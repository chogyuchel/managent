import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'
import { clearAuth, getToken } from '../utils/auth'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || '/api',
  timeout: 15000
})

// 请求拦截：后端通过请求头 token 校验登录状态
request.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.token = token
  }
  return config
})

// 响应拦截：统一解包 Result{ code, msg, data }
request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res && typeof res === 'object' && 'code' in res) {
      if (res.code === 1) return res.data
      const msg = res.msg || '操作失败'
      ElMessage.error(msg)
      return Promise.reject(new Error(msg))
    }
    return res
  },
  (error) => {
    const status = error.response?.status
    if (status === 401) {
      clearAuth()
      ElMessage.error('登录已失效，请重新登录')
      router.replace('/login')
    } else if (error.code === 'ECONNABORTED') {
      ElMessage.error('请求超时，请检查后端服务是否启动')
    } else {
      const msg = error.response?.data?.msg || error.message || '网络异常'
      ElMessage.error(msg)
    }
    return Promise.reject(error)
  }
)

export default request