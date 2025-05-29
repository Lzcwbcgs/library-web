import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../layout/Layout.vue'

const routes = [
  {
    path: '/login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    component: () => import('../views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '首页', icon: 'House' }
      },
      {
        path: 'books',
        name: 'Books',
        component: () => import('../views/book/BookList.vue'),
        meta: { title: '图书管理', icon: 'Reading' }
      },
      {
        path: 'books/:id',
        name: 'BookDetail',
        component: () => import('../views/book/BookDetail.vue'),
        meta: { title: '图书详情', hidden: true }
      },
      {
        path: 'borrows',
        name: 'Borrows',
        component: () => import('../views/borrow/BorrowList.vue'),
        meta: { title: '借阅管理', icon: 'List' }
      },
      {
        path: 'categories',
        name: 'Categories',
        component: () => import('../views/category/CategoryList.vue'),
        meta: { title: '分类管理', icon: 'Folder', roles: ['admin', 'super_admin'] }
      },
      {
        path: 'stats',
        name: 'Stats',
        component: () => import('../views/stats/Stats.vue'),
        meta: { title: '统计分析', icon: 'DataAnalysis', roles: ['admin', 'super_admin'] }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('../views/profile/Profile.vue'),
        meta: { title: '个人中心', icon: 'User', hidden: true }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    component: () => import('../views/404.vue'),
    meta: { hidden: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path === '/login' || to.path === '/register') {
    next()
  } else if (!token) {
    next('/login')
  } else {
    next()
  }
})

export default router