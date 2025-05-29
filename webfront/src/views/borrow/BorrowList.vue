<template>
  <div class="borrow-list-container">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <div class="search-form">
        <el-form :inline="true" :model="queryParams">
          <el-form-item>
            <el-input
              v-model="queryParams.keyword"
              placeholder="图书名称/作者"
              clearable
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item>          <el-select v-model="queryParams.status" placeholder="借阅状态" clearable>
              <el-option label="借阅中" :value="0" />
              <el-option label="已归还" :value="1" />
              <el-option label="逾期未还" :value="2" />
              <el-option label="逾期已还" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 借阅列表 -->
    <el-card class="list-card">
      <template #header>
        <div class="card-header">
          <span>借阅记录列表</span>
        </div>
      </template>
      
      <el-table :data="borrowList" stripe>
        <el-table-column prop="bookName" label="图书名称" min-width="180" />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column label="借阅时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.borrowTime) }}
          </template>
        </el-table-column>
        <el-table-column label="应还时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.dueTime) }}
          </template>
        </el-table-column>
        <el-table-column label="实际归还时间" width="160">
          <template #default="{ row }">
            {{ row.returnTime ? formatDate(row.returnTime) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <!-- 借阅中状态，显示归还和续借按钮 -->
            <template v-if="row.status === 0">
              <el-button
                type="primary"
                link
                @click="handleReturn(row)"
              >
                归还
              </el-button>
              <el-button
                v-if="row.renewCount === 0"
                type="success"
                link
                @click="handleRenew(row)"
              >
                续借
              </el-button>
            </template>
            <!-- 逾期未还状态，显示逾期归还按钮 -->
            <template v-else-if="row.status === 2">
              <el-button
                type="warning"
                link
                @click="handleReturn(row)"
              >
                逾期归还
              </el-button>
            </template>
            <!-- 已归还或逾期已还状态，显示横线 -->
            <template v-else>
              <span>-</span>
            </template>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { getBorrowList, returnBook, renewBorrow } from '../../api/borrow'
import { ElMessage, ElMessageBox } from 'element-plus'

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  status: undefined
})

// 借阅列表数据
const borrowList = ref([])
const total = ref(0)

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 获取借阅列表
const getList = async () => {
  try {
    const res = await getBorrowList(queryParams)
    borrowList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('获取借阅列表失败:', error)
  }
}

// 处理归还
const handleReturn = (row) => {
  ElMessageBox.confirm(`确认要归还《${row.bookName}》吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await returnBook(row.id)
      ElMessage.success('归还成功')
      getList()
    } catch (error) {      // 从response中获取错误信息
      const errorMsg = error.response?.data?.message || error.message || '未知错误'
      ElMessage.error('归还失败: ' + errorMsg)
    }
  }).catch(() => {})
}

// 处理续借
const handleRenew = (row) => {
  ElMessageBox.confirm(`确认要续借《${row.bookName}》吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await renewBorrow(row.id)
      ElMessage.success('续借成功')
      getList()
    } catch (error) {
      // 从response中获取错误信息
      const errorMsg = error.response?.data?.message || error.message || '未知错误'
      ElMessage.error('续借失败: ' + errorMsg)
    }
  }).catch(() => {})
}

// 搜索
const handleSearch = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置搜索
const resetQuery = () => {
  queryParams.keyword = ''
  queryParams.status = undefined
  handleSearch()
}  // 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 0: return '借阅中'
    case 1: return '已归还'
    case 2: return '逾期未还'
    case 3: return '逾期已还'
    default: return '未知'
  }
}  // 获取状态标签类型
const getStatusType = (status) => {
  switch (status) {
    case 0: return 'primary'  // 借阅中
    case 1: return 'success'  // 已归还
    case 2: return 'warning'  // 逾期未还
    case 3: return 'danger'   // 逾期已还
    default: return 'info'
  }
}

// 分页大小变化
const handleSizeChange = (size) => {
  queryParams.pageSize = size
  getList()
}

// 页码变化
const handleCurrentChange = (page) => {
  queryParams.pageNum = page
  getList()
}

// 初始化
getList()
</script>

<style scoped>
.borrow-list-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
}

.list-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}
</style>