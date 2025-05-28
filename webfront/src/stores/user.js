import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, logout, register } from '../api/auth'
import { getProfile } from '../api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref({})
  
  async function loginAction(loginData) {
    try {
      const res = await login(loginData)
      token.value = res.data.token
      localStorage.setItem('token', res.data.token)
      await getUserInfo()
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }
  
  async function registerAction(registerData) {
    console.log('开始注册用户:', registerData)
    try {
      const res = await register(registerData)
      console.log('注册成功:', res)
      return Promise.resolve(res)
    } catch (error) {
      console.error('注册失败:', error)
      // 如果有服务器返回的错误信息，直接抛出
      if (error.response && error.response.data) {
        return Promise.reject(error)
      }
      // 否则包装一个通用错误
      return Promise.reject(new Error('注册失败，请稍后重试'))
    }
  }
  
  async function logoutAction() {
    try {
      console.log('开始退出登录')
      // 检查token是否存在
      const currentToken = localStorage.getItem('token')
      if (!currentToken) {
        console.warn('退出登录：token不存在，直接清除本地状态')
        resetToken()
        return Promise.resolve()
      }
      
      // 直接使用axios请求退出
      return new Promise((resolve, reject) => {
        import('axios').then(async (axios) => {
          try {
            const response = await axios.default.post('/api/auth/logout', {}, {
              headers: { 'Authorization': `Bearer ${currentToken}` }
            })
            console.log('退出登录响应:', response.data)
            resetToken()
            resolve()
          } catch (axiosError) {
            console.error('退出登录出错:', axiosError)
            // 无论失败与否，都清除本地token
            resetToken()
            
            if (axiosError.response) {
              console.error('退出登录错误响应:', axiosError.response.data)
              // 这里不拒绝，让用户能够退出
              resolve()
            } else {
              reject(new Error('网络错误，请稍后重试'))
            }
          }
        })
      })
    } catch (error) {
      console.error('退出登录异常:', error)
      // 确保无论如何都清除token
      resetToken()
      return Promise.reject(error)
    }
  }
  
  async function getUserInfo() {
    try {
      console.log('开始获取用户信息')
      
      // 检查token是否存在
      const currentToken = localStorage.getItem('token')
      if (!currentToken) {
        console.warn('获取用户信息失败：token不存在')
        return Promise.reject(new Error('未登录'))
      }
      
      // 直接使用axios请求用户信息
      return new Promise((resolve, reject) => {
        import('axios').then(async (axios) => {
          try {
            const response = await axios.default.get('/api/user/profile', {
              headers: { 'Authorization': `Bearer ${currentToken}` }
            })
            console.log('用户信息响应:', response.data)
            
            if (response.data && response.data.code === 200) {
              userInfo.value = response.data.data
              resolve(response.data)
            } else {
              console.error('获取用户信息失败:', response.data)
              reject(new Error(response.data?.message || '获取用户信息失败'))
            }
          } catch (axiosError) {
            console.error('获取用户信息出错:', axiosError)
            if (axiosError.response) {
              // 如果是401错误，清除token
              if (axiosError.response.status === 401) {
                resetToken()
              }
              console.error('用户信息错误响应:', axiosError.response.data)
              reject(new Error(axiosError.response.data?.message || '获取用户信息失败'))
            } else {
              reject(new Error('网络错误，请稍后重试'))
            }
          }
        })
      })
    } catch (error) {
      console.error('获取用户信息异常:', error)
      return Promise.reject(error)
    }
  }
  
  function resetToken() {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
  }
  
  return {
    token,
    userInfo,
    loginAction,
    registerAction,
    logoutAction,
    getUserInfo,
    resetToken
  }
})