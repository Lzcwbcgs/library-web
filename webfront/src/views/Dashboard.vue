<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <!-- 统计卡片 -->
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="data-card">
          <div class="data-card-header">
            <span>我的借阅</span>
            <el-icon><Reading /></el-icon>
          </div>
          <div class="data-card-content">
            <div class="data-card-value">{{ userStats.totalBorrowsCount || 0 }}</div>
            <div class="data-card-label">总借阅次数</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="data-card">
          <div class="data-card-header">
            <span>待归还</span>
            <el-icon><Clock /></el-icon>
          </div>
          <div class="data-card-content">
            <div class="data-card-value">{{ userStats.borrowing || 0 }}</div>
            <div class="data-card-label">当前借阅中</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="data-card">
          <div class="data-card-header">
            <span>逾期</span>
            <el-icon><WarningFilled /></el-icon>
          </div>
          <div class="data-card-content">
            <div class="data-card-value">{{ userStats.overdue || 0 }}</div>
            <div class="data-card-label">逾期未还</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="data-card">
          <div class="data-card-header">
            <span>已归还</span>
            <el-icon><CircleCheck /></el-icon>
          </div>
          <div class="data-card-content">
            <div class="data-card-value">{{ userStats.returned || 0 }}</div>
            <div class="data-card-label">已归还数量</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 最近借阅记录 -->
    <el-card class="recent-card">
      <template #header>
        <div class="card-header">
          <span>最近借阅记录</span>
          <el-button type="primary" text @click="$router.push('/borrows')">查看更多</el-button>
        </div>
      </template>
      
      <el-table :data="recentBorrows" stripe style="width: 100%">
        <el-table-column prop="bookName" label="图书名称" />
        <el-table-column prop="borrowTime" label="借阅时间" width="180" />
        <el-table-column prop="returnTime" label="应还时间" width="180" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 1"
              type="primary"
              link
              @click="handleRenew(row.id)"
            >
              续借
            </el-button>
            <el-button
              v-if="row.status === 1"
              type="success"
              link
              @click="handleReturn(row.id)"
            >
              归还
            </el-button>
            <el-button
              type="info"
              link
              @click="$router.push(`/borrows/${row.id}`)"
            >
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 图书推荐 -->
    <el-card class="recommend-card">
      <template #header>
        <div class="card-header">
          <span>图书推荐</span>
          <el-button type="primary" text @click="$router.push('/books')">浏览更多</el-button>
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col v-for="book in recommendBooks" :key="book.id" :xs="24" :sm="12" :md="8" :lg="6">
          <el-card shadow="hover" class="book-card" @click="$router.push(`/books/${book.id}`)">
            <div class="book-cover">
              <img :src="book.coverUrl || 'https://via.placeholder.com/150x200'" alt="封面">
            </div>
            <div class="book-info">
              <h3 class="book-title">{{ book.title }}</h3>
              <p class="book-author">作者: {{ book.author }}</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { 
    getMyBorrowStats,  // <--- 修改这里
    getBorrowList as getBorrowListFromApi, // 重命名借阅记录的列表函数
    renewBorrow, 
    returnBook 
} from '../api/borrow'
import { getBookList as getBookListFromBookApi } from '../api/book' // 重命名书籍列表的函数
import { ElMessage } from 'element-plus'

// 用户统计数据
const userStats = ref({})
// 最近借阅记录
const recentBorrows = ref([])
// 推荐图书
const recommendBooks = ref([])

// 获取用户统计数据
const fetchUserStats = async () => {
  try {
    const res = await getMyBorrowStats()
    userStats.value = res.data
  } catch (error) {
    console.error('获取用户统计数据失败:', error)
  }
}

// 获取最近借阅记录
const fetchRecentBorrows = async () => {
  try {
    const res = await getBorrowListFromApi({ // 这个 getBorrowPage 来自 ../api/borrow.js
      pageNum: 1,
      pageSize: 5
    })
    if (res.data && res.data.records) { // 确保 records 存在
        recentBorrows.value = res.data.records
    } else {
        console.warn('获取最近借阅记录API未返回records字段或data为空', res);
        recentBorrows.value = [];
    }
  } catch (error) {
    console.error('获取最近借阅记录失败:', error)
    recentBorrows.value = [];
  }
}

// 获取推荐图书
const fetchRecommendBooks = async () => {
  try {
    const res = await getBookListFromBookApi({ // 修改: 使用 getBookList
      pageNum: 1,
      pageSize: 8
    })
    if (res.data && res.data.records) { // 确保 records 存在
      recommendBooks.value = res.data.records
    } else if (res.data && Array.isArray(res.data)) { // 备选: 如果 data 直接是数组
      recommendBooks.value = res.data;
    } else {
      console.warn('获取推荐图书API未返回records字段或data为空，或格式不正确', res)
      recommendBooks.value = [] // 清空推荐图书以避免错误
    }
  } catch (error) {
    console.error('获取推荐图书失败:', error)
    recommendBooks.value = [] // 出错时清空
  }
}


// 获取状态标签样式
const getStatusType = (status) => {
  switch (status) {
    case 1: return 'primary'  // 借阅中
    case 2: return 'success'  // 已归还
    case 3: return 'danger'   // 逾期
    case 4: return 'warning'  // 丢失
    default: return 'info'
  }
}

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 1: return '借阅中'
    case 2: return '已归还'
    case 3: return '已逾期'
    case 4: return '已丢失'
    default: return '未知'
  }
}

// 处理续借
const handleRenew = async (borrowId) => {
  try {
    await renewBorrow(borrowId)
    ElMessage.success('续借成功')
    fetchRecentBorrows()
  } catch (error) {
    console.error('续借失败:', error)
  }
}

// 处理归还
const handleReturn = async (borrowId) => {
  try {
    await returnBook(borrowId)
    ElMessage.success('归还成功')
    fetchRecentBorrows()
    fetchUserStats()
  } catch (error) {
    console.error('归还失败:', error)
  }
}

onMounted(() => {
  fetchUserStats()
  fetchRecentBorrows()
  fetchRecommendBooks()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.data-card {
  margin-bottom: 20px;
}

.data-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  font-weight: bold;
}

.data-card-content {
  text-align: center;
}

.data-card-value {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
}

.data-card-label {
  margin-top: 5px;
  color: #606266;
}

.recent-card, .recommend-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.book-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.book-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 15px rgba(0, 0, 0, 0.1);
}

.book-cover {
  height: 200px;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
}

.book-cover img {
  max-width: 100%;
  max-height: 100%;
}

.book-info {
  padding: 10px;
}

.book-title {
  margin: 5px 0;
  font-size: 16px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.book-author {
  margin: 5px 0;
  font-size: 14px;
  color: #606266;
}
</style> 