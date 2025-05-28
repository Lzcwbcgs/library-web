<template>
  <div class="borrow-list-container">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item>
          <el-input
            v-model="queryParams.keyword"
            placeholder="图书名/借阅人"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-select
            v-model="queryParams.status"
            placeholder="借阅状态"
            clearable
          >
            <el-option label="借阅中" :value="1" />
            <el-option label="已归还" :value="2" />
            <el-option label="已逾期" :value="3" />
            <el-option label="已丢失" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 借阅列表 -->
    <el-card class="list-card">
      <template #header>
        <div class="card-header">
          <span>借阅列表</span>
          <div v-if="hasAdminRole">
            <el-button type="warning" @click="handleOverdue">处理逾期</el-button>
          </div>
        </div>
      </template>
      
      <el-table :data="borrowList" stripe style="width: 100%">
        <el-table-column prop="bookName" label="图书名称" min-width="120" />
        <el-table-column prop="userName" label="借阅人" width="120" v-if="hasAdminRole" />
        <el-table-column prop="borrowTime" label="借阅时间" width="180" />
        <el-table-column prop="dueTime" label="应还时间" width="180" />
        <el-table-column prop="returnTime" label="实际归还时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
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
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { useUserStore } from '../../stores/user'
import { getBorrowList, returnBook, renewBorrow, handleOverdueRecords } from '../../api/borrow'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()

// 是否具有管理员角色
const hasAdminRole = computed(() => {
  const roles = userStore.userInfo.roles || []
  return roles.includes('ADMIN') || roles.includes('SUPER_ADMIN')
})

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  status: undefined,
  startDate: '',
  endDate: ''
})

// 日期范围
const dateRange = ref([])

// 借阅列表
const borrowList = ref([])
const total = ref(0)

// 监听日期范围变化
watch(dateRange, (val) => {
  if (val && val.length === 2) {
    queryParams.startDate = val[0]
    queryParams.endDate = val[1]
  } else {
    queryParams.startDate = ''
    queryParams.endDate = ''
  }
})

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

// 搜索
const handleSearch = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置搜索
const resetQuery = () => {
  queryParams.keyword = ''
  queryParams.status = undefined
  dateRange.value = []
  queryParams.startDate = ''
  queryParams.endDate = ''
  handleSearch()
}

// 处理续借
const handleRenew = (id) => {
  ElMessageBox.confirm('确认续借该图书吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(async () => {
    try {
      await renewBorrow(id)
      ElMessage.success('续借成功')
      getList()
    } catch (error) {
      console.error('续借失败:', error)
    }
  }).catch(() => {})
}

// 处理归还
const handleReturn = (id) => {
  ElMessageBox.confirm('确认归还该图书吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(async () => {
    try {
      await returnBook(id)
      ElMessage.success('归还成功')
      getList()
    } catch (error) {
      console.error('归还失败:', error)
    }
  }).catch(() => {})
}

// 处理逾期
const handleOverdue = () => {
  ElMessageBox.confirm('确认处理所有逾期记录吗?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await handleOverdueRecords()
      ElMessage.success('处理成功')
      getList()
    } catch (error) {
      console.error('处理逾期记录失败:', error)
    }
  }).catch(() => {})
}

// 获取状态标签类型
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

onMounted(() => {
  getList()
})
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