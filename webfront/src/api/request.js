import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const service = axios.create({
  baseURL: '/api',  // 去掉 baseURL，因为 vite.config.js 已经配置了代理
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 添加详细请求日志
    console.log('Request:', {
      url: config.url,
      method: config.method,
      data: config.data,
      headers: config.headers
    })
    
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('Request Error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    console.log('Response:', res)
    
    // API返回成功
    if (res.code === 200) {
      return res
    }
    
    // 处理特定错误码
    if (res.code === 401) {
      router.push('/login')
    }
    
    // 显示错误消息
    const errorMessage = res.message || '操作失败'
    ElMessage.error(errorMessage)
    
    return Promise.reject(new Error(errorMessage))
  },
  error => {
    console.error('Response Error:', error)
    console.error('Response Error Config:', error.config)
    
    let errorMessage = '请求失败，请稍后再试'
    
    if (error.response) {
      console.error('Response Error Data:', error.response.data)
      
      // 尝试从响应中获取更具体的错误信息
      if (error.response.data) {
        if (error.response.data.message) {
          errorMessage = error.response.data.message
        } else if (typeof error.response.data === 'string') {
          errorMessage = error.response.data
        }
      }
      
      // 处理不同状态码
      switch (error.response.status) {
        case 401:
          errorMessage = '未授权，请重新登录'
          localStorage.removeItem('token')
          router.push('/login')
          break
        case 403:
          errorMessage = '拒绝访问'
          break
        case 404:
          errorMessage = '请求不存在'
          break
        case 500:
          if (!errorMessage || errorMessage === '请求失败，请稍后再试') {
            errorMessage = '服务器内部错误'
          }
          break
        default:
          break
      }
    } else if (error.request) {
      // 请求已发送但没有收到响应
      errorMessage = '服务器无响应，请检查网络连接'
    } else {
      // 请求设置时发生错误
      errorMessage = '请求配置错误: ' + error.message
    }
    
    ElMessage.error(errorMessage)
    return Promise.reject(error)
  }
)

export default service