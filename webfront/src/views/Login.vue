<template>
  <div class="login-container">
    <div class="login-left">
      <div class="overlay">
        <h2>智慧图书管理系统</h2>
        <p>知识的海洋，智慧的殿堂</p>
      </div>
    </div>
    <div class="login-right">
      <div class="login-form">
        <div class="login-title">
          <h1>欢迎登录</h1>
          <p>智慧图书管理系统</p>
        </div>
        
        <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" label-width="0">
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="用户名"
              prefix-icon="User"
              size="large"
            />
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              placeholder="密码"
              prefix-icon="Lock"
              type="password"
              show-password
              size="large"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button
              type="primary"
              :loading="loading"
              class="login-button"
              @click="handleLogin"
              size="large"
              round
            >
              登录
            </el-button>
          </el-form-item>
        </el-form>
        
        <div class="login-options">
          <router-link to="/register">没有账号？立即注册</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'
import { login } from '../api/auth'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 10, message: '用户名长度必须在2-10个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度必须在6-20个字符之间', trigger: 'blur' }
  ]
}

const handleLogin = () => {
  loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    try {
      console.log('提交登录数据:', JSON.stringify(loginForm))
      const response = await login(loginForm)
      console.log('登录响应:', response)
      
      // 保存token
      if (response && response.code === 200 && response.data && response.data.token) {
        localStorage.setItem('token', response.data.token)
        // 更新用户store
        try {
          await userStore.getUserInfo()
          ElMessage.success('登录成功')
          router.push('/')
        } catch (userInfoError) {
          console.error('获取用户信息失败:', userInfoError)
          ElMessage.warning('登录成功，但获取用户信息失败')
          router.push('/')
        }
      } else {
        console.error('返回数据格式不正确:', response)
        let errorMsg = '登录失败：返回数据格式不正确'
        if (response && response.message) {
          errorMsg = response.message
        }
        ElMessage.error(errorMsg)
      }
    } catch (error) {
      console.error('登录失败:', error)
      ElMessage.error(error.message || '登录失败，请稍后重试')
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.login-left {
  flex: 1;
  position: relative;
  overflow: hidden;
}

.library-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url('https://img.freepik.com/free-photo/bookshelf-library-with-open-textbook_23-2149139851.jpg');
  background-size: cover;
  background-position: center;
}

.overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
  padding: 0 5%;
  text-align: center;
  box-sizing: border-box;
}

.overlay h2 {
  font-size: 2.5vw;
  margin-bottom: 1vh;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
}

.overlay p {
  font-size: 1.2vw;
  max-width: 80%;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
}

.login-right {
  width: 35%;
  min-width: 350px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}

.login-box {
  width: 80%;
  max-width: 400px;
  padding: 4vh 5%;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.1);
  box-sizing: border-box;
}

.login-title {
  text-align: center;
  margin-bottom: 3vh;
}

.login-title h1 {
  margin: 0;
  font-size: 1.8rem;
  color: #303133;
  margin-bottom: 0.5vh;
}

.login-title p {
  margin: 0;
  font-size: 1rem;
  color: #909399;
}

.login-button {
  width: 100%;
  margin-top: 1vh;
}

.login-options {
  margin-top: 2vh;
  text-align: center;
}

.login-options a {
  color: #409eff;
  text-decoration: none;
  font-size: 0.9rem;
  transition: color 0.3s;
}

.login-options a:hover {
  color: #66b1ff;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .overlay h2 {
    font-size: 3vw;
  }
  
  .overlay p {
    font-size: 1.5vw;
  }
}

@media (max-width: 900px) {
  .login-container {
    flex-direction: column;
  }
  
  .login-left {
    height: 30vh;
  }
  
  .login-right {
    width: 100%;
    height: 70vh;
  }
  
  .overlay h2 {
    font-size: 5vw;
  }
  
  .overlay p {
    font-size: 2.5vw;
  }
}

/* 确保body和html也没有多余空间 */
:global(body),
:global(html) {
  margin: 0;
  padding: 0;
  overflow: hidden;
  width: 100%;
  height: 100%;
}
</style>