import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('../layout/Layout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
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
        path: 'categories',
        name: 'Categories',
        component: () => import('../views/category/CategoryList.vue'),
        meta: { title: '分类管理', icon: 'Folder', roles: ['ADMIN', 'SUPER_ADMIN'] }
      },
      {
        path: 'borrows',
        name: 'Borrows',
        component: () => import('../views/borrow/BorrowList.vue'),
        meta: { title: '借阅管理', icon: 'Document' }
      },
      {
        path: 'stats',
        name: 'Stats',
        component: () => import('../views/stats/Stats.vue'),
        meta: { title: '统计分析', icon: 'DataAnalysis', roles: ['ADMIN', 'SUPER_ADMIN'] }
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
    component: () => import('../views/404.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if ((to.path === '/login' || to.path === '/register') && token) {
    next('/')
  } else {
    next()
  }
})

export default router 