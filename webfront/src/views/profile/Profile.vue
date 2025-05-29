<template>
  <div class="profile-container">
    <!-- 用户信息卡片 -->
    <el-card class="profile-card">
      <div class="user-info">
        <div class="avatar-container">
          <el-avatar :size="100" :src="userInfo.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
        </div>
        <div class="user-details">
          <h2>{{ userInfo.username }}</h2>
          <p>{{ userInfo.realName || '未设置真实姓名' }}</p>
          <p>{{ userInfo.email || '未设置邮箱' }}</p>
          <p>角色: {{ formatRole(userInfo.role) }}</p>
          <p>注册时间: {{ userInfo.createTime }}</p>
        </div>
      </div>
      
      <el-divider content-position="center">个人资料</el-divider>
      
      <el-form
        ref="profileFormRef"
        :model="profileForm"
        :rules="profileRules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="profileForm.username" disabled />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="profileForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="头像" prop="avatar">
          <el-input v-model="profileForm.avatar" placeholder="请输入头像URL" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleUpdateProfile">保存资料</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 修改密码卡片 -->
    <el-card class="password-card">
      <template #header>
        <div class="card-header">
          <span>修改密码</span>
        </div>
      </template>
      
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="100px"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            show-password
            placeholder="请输入原密码"
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            show-password
            placeholder="请输入新密码"
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            show-password
            placeholder="请确认新密码"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleUpdatePassword">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 借阅统计卡片 -->
    <el-card class="stats-card">
      <template #header>
        <div class="card-header">
          <span>借阅统计</span>
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6">
          <div class="stat-item">
            <div class="stat-value">{{ userStats.totalBorrows || 0 }}</div>
            <div class="stat-label">总借阅次数</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="stat-item">
            <div class="stat-value">{{ userStats.borrowing || 0 }}</div>
            <div class="stat-label">当前借阅中</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="stat-item">
            <div class="stat-value">{{ userStats.overdue || 0 }}</div>
            <div class="stat-label">逾期未还</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="stat-item">
            <div class="stat-value">{{ userStats.returned || 0 }}</div>
            <div class="stat-label">已归还数量</div>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '../../stores/user'
import { getProfile, updateProfile as apiUpdateProfile, changePassword } from '../../api/user'
import { getMyBorrowStats } from '../../api/borrow'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()

// 用户信息
const userInfo = ref({})
// 用户统计数据
const userStats = ref({})

// 个人资料表单
const profileFormRef = ref()
const profileForm = reactive({
  username: '',
  realName: '',
  email: '',
  phone: '',
  avatar: ''
})

// 修改密码表单
const passwordFormRef = ref()
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单校验规则
const profileRules = {
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

// 密码验证函数
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度必须在6-20个字符之间', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度必须在6-20个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 获取用户信息
const getUserInfo = async () => {
  try {
    const res = await getProfile()
    userInfo.value = res.data
    
    // 填充表单
    profileForm.username = res.data.username || ''
    profileForm.realName = res.data.realName || ''
    profileForm.email = res.data.email || ''
    profileForm.phone = res.data.phone || ''
    profileForm.avatar = res.data.avatar || ''
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

// 获取用户借阅统计
const getUserStats = async () => {
  try {
    const res = await getMyBorrowStats()
    userStats.value = res.data
  } catch (error) {
    console.error('获取用户借阅统计失败:', error)
  }
}

// 更新个人资料
const handleUpdateProfile = () => {
  profileFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    try {
      await apiUpdateProfile(profileForm)
      ElMessage.success('个人资料更新成功')
      getUserInfo()
    } catch (error) {
      console.error('更新个人资料失败:', error)
    }
  })
}

// 修改密码
const handleUpdatePassword = () => {
  passwordFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    try {
      await changePassword(passwordForm)
      ElMessage.success('密码修改成功，请重新登录')
      passwordForm.oldPassword = ''
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
      
      // 退出登录
      setTimeout(() => {
        userStore.logoutAction()
      }, 1500)
    } catch (error) {
      console.error('修改密码失败:', error)
    }
  })
}

// 格式化角色
const formatRole = (role) => {
  if (!role) return '普通用户'
  
  const roleMap = {
    'ROLE_ADMIN': '管理员',
    'ROLE_SUPER_ADMIN': '超级管理员',
    'ROLE_READER': '普通用户'
  }
  
  return roleMap[role] || role
}

onMounted(() => {
  getUserInfo()
  getUserStats()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.profile-card, .password-card, .stats-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.avatar-container {
  margin-right: 30px;
}

.user-details h2 {
  margin-top: 0;
  margin-bottom: 10px;
}

.user-details p {
  margin: 5px 0;
  color: #606266;
}

.stat-item {
  text-align: center;
  padding: 20px 0;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.stat-label {
  margin-top: 5px;
  color: #606266;
}
</style>