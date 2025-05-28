<template>
  <div class="book-detail-container">
    <el-card class="detail-card">
      <template #header>
        <div class="card-header">
          <el-page-header @back="$router.back()" :title="book.name" />
        </div>
      </template>
      
      <el-row :gutter="20">
        <!-- 图书封面 -->
        <el-col :span="8">
          <div class="book-cover">
            <img :src="book.coverUrl || 'https://via.placeholder.com/300x400'" alt="封面">
            <div class="book-status" :class="getStatusClass(book.status)">
              {{ getStatusText(book.status) }}
            </div>
          </div>
          
          <div class="book-actions">
            <el-button
              v-if="book.status === 1"
              type="primary"
              @click="handleBorrow"
            >
              立即借阅
            </el-button>
            
            <el-button
              v-if="hasAdminRole"
              type="warning"
              @click="handleEdit"
            >
              编辑图书
            </el-button>
          </div>
        </el-col>
        
        <!-- 图书信息 -->
        <el-col :span="16">
          <h1 class="book-title">{{ book.name }}</h1>
          <div class="book-meta">
            <p><strong>作者：</strong>{{ book.author }}</p>
            <p><strong>ISBN：</strong>{{ book.isbn }}</p>
            <p><strong>分类：</strong>{{ book.categoryName }}</p>
            <p><strong>出版社：</strong>{{ book.publisher }}</p>
            <p><strong>出版日期：</strong>{{ book.publishDate }}</p>
            <p><strong>库存：</strong>{{ book.stock }}</p>
            <p><strong>状态：</strong>
              <el-tag :type="getStatusType(book.status)">{{ getStatusText(book.status) }}</el-tag>
            </p>
          </div>
          
          <el-divider content-position="left">图书简介</el-divider>
          <div class="book-description">
            <p>{{ book.description || '暂无简介' }}</p>
          </div>
        </el-col>
      </el-row>
    </el-card>
    
    <!-- 借阅记录 -->
    <el-card v-if="hasAdminRole" class="borrow-record-card">
      <template #header>
        <div class="card-header">
          <span>借阅记录</span>
        </div>
      </template>
      
      <el-table :data="borrowRecords" stripe style="width: 100%">
        <el-table-column prop="userName" label="借阅人" width="120" />
        <el-table-column prop="borrowTime" label="借阅时间" width="180" />
        <el-table-column prop="returnTime" label="应还时间" width="180" />
        <el-table-column prop="actualReturnTime" label="实际归还时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getBorrowStatusType(row.status)">{{ getBorrowStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remarks" label="备注" />
      </el-table>
    </el-card>
    
    <!-- 借阅对话框 -->
    <el-dialog
      title="图书借阅"
      v-model="borrowDialogVisible"
      width="400px"
      append-to-body
    >
      <el-form
        ref="borrowFormRef"
        :model="borrowForm"
        :rules="borrowRules"
        label-width="100px"
      >
        <el-form-item label="借阅天数" prop="days">
          <el-input-number v-model="borrowForm.days" :min="1" :max="30" />
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input
            v-model="borrowForm.remarks"
            type="textarea"
            rows="3"
            placeholder="请输入借阅备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="borrowDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitBorrow">确定借阅</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { getBookDetail, updateBook } from '../../api/book'
import { createBorrow, getBorrowPage } from '../../api/borrow'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const bookId = computed(() => route.params.id)

// 是否具有管理员角色
const hasAdminRole = computed(() => {
  const roles = userStore.userInfo.roles || []
  return roles.includes('ADMIN') || roles.includes('SUPER_ADMIN')
})

// 图书详情
const book = ref({})
// 借阅记录
const borrowRecords = ref([])

// 借阅对话框
const borrowDialogVisible = ref(false)
const borrowFormRef = ref()
const borrowForm = reactive({
  bookId: '',
  days: 14,
  remarks: ''
})

// 表单校验规则
const borrowRules = {
  days: [{ required: true, message: '请输入借阅天数', trigger: 'blur' }]
}

// 获取图书详情
const getDetail = async () => {
  try {
    const res = await getBookDetail(bookId.value)
    book.value = res.data
  } catch (error) {
    console.error('获取图书详情失败:', error)
  }
}

// 获取借阅记录
const getBorrowRecords = async () => {
  if (!hasAdminRole.value) return
  
  try {
    const res = await getBorrowPage({
      bookId: bookId.value,
      pageNum: 1,
      pageSize: 10
    })
    borrowRecords.value = res.data.records
  } catch (error) {
    console.error('获取借阅记录失败:', error)
  }
}

// 处理借阅
const handleBorrow = () => {
  borrowForm.bookId = bookId.value
  borrowDialogVisible.value = true
}

// 处理编辑
const handleEdit = () => {
  router.push({
    path: '/books',
    query: { id: bookId.value, edit: true }
  })
}

// 提交借阅
const submitBorrow = () => {
  borrowFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    try {
      await createBorrow(borrowForm)
      ElMessage.success('借阅成功')
      borrowDialogVisible.value = false
      getDetail()
    } catch (error) {
      console.error('借阅失败:', error)
    }
  })
}

// 获取状态样式类
const getStatusClass = (status) => {
  switch (status) {
    case 1: return 'status-available'  // 可借阅
    case 2: return 'status-borrowed'   // 已借出
    case 0: return 'status-disabled'   // 下架
    default: return ''
  }
}

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 1: return '可借阅'
    case 2: return '已借出'
    case 0: return '已下架'
    default: return '未知'
  }
}

// 获取状态标签类型
const getStatusType = (status) => {
  switch (status) {
    case 1: return 'success'  // 可借阅
    case 2: return 'info'     // 已借出
    case 0: return 'danger'   // 下架
    default: return 'info'
  }
}

// 获取借阅状态标签类型
const getBorrowStatusType = (status) => {
  switch (status) {
    case 1: return 'primary'  // 借阅中
    case 2: return 'success'  // 已归还
    case 3: return 'danger'   // 逾期
    case 4: return 'warning'  // 丢失
    default: return 'info'
  }
}

// 获取借阅状态文本
const getBorrowStatusText = (status) => {
  switch (status) {
    case 1: return '借阅中'
    case 2: return '已归还'
    case 3: return '已逾期'
    case 4: return '已丢失'
    default: return '未知'
  }
}

onMounted(() => {
  getDetail()
  getBorrowRecords()
})
</script>

<style scoped>
.book-detail-container {
  padding: 20px;
}

.detail-card, .borrow-record-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.book-cover {
  text-align: center;
  position: relative;
  margin-bottom: 20px;
}

.book-cover img {
  max-width: 100%;
  max-height: 400px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.book-status {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  color: white;
}

.status-available {
  background-color: #67c23a;
}

.status-borrowed {
  background-color: #909399;
}

.status-disabled {
  background-color: #f56c6c;
}

.book-actions {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-bottom: 20px;
}

.book-title {
  font-size: 24px;
  margin-top: 0;
  margin-bottom: 20px;
  color: #303133;
}

.book-meta p {
  margin: 10px 0;
  font-size: 16px;
  color: #606266;
}

.book-description {
  margin-top: 20px;
  line-height: 1.6;
  color: #606266;
}
</style> 