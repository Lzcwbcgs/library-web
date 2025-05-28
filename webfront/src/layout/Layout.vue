<template>
  <div class="layout-container">
    <!-- 侧边栏 -->
    <el-aside width="220px" class="aside">
      <div class="logo">
        <h1>智慧图书管理系统</h1>
      </div>
      <el-menu
        router
        :default-active="activeMenu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <template v-for="(route, index) in routes" :key="index">
          <el-menu-item v-if="!route.meta.hidden && (!route.meta.roles || hasRole(route.meta.roles))" :index="route.path">
            <el-icon v-if="route.meta.icon">
              <component :is="route.meta.icon"></component>
            </el-icon>
            <template #title>{{ route.meta.title }}</template>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    
    <!-- 主体内容 -->
    <el-container>
      <!-- 顶部导航 -->
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ activeTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown trigger="click" @command="handleCommand">
            <span class="user-dropdown">
              {{ userStore.userInfo.username }}
              <el-icon><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <!-- 内容区域 -->
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 获取路由列表，用于渲染菜单
const routes = computed(() => {
  return router.options.routes.find(r => r.path === '/').children || []
})

// 当前激活的菜单
const activeMenu = computed(() => {
  return '/' + route.path.split('/')[1]
})

// 当前页面标题
const activeTitle = computed(() => {
  const matched = route.matched
  return matched[matched.length - 1]?.meta?.title || ''
})

// 检查用户是否有权限
const hasRole = (roles) => {
  const userRoles = userStore.userInfo.roles || []
  return userRoles.some(role => roles.includes(role))
}

// 处理下拉菜单命令
const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确认退出登录?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      await userStore.logoutAction()
      router.push('/login')
    }).catch(() => {})
  } else if (command === 'profile') {
    router.push('/profile')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
  display: flex;
}

.aside {
  background-color: #304156;
  height: 100%;
  overflow-x: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.logo h1 {
  font-size: 18px;
  margin: 0;
  white-space: nowrap;
}

.header {
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #e6e6e6;
}

.user-dropdown {
  cursor: pointer;
  color: #606266;
  display: flex;
  align-items: center;
}

.header-right {
  margin-right: 20px;
}
</style>