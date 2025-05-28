<template>
    <div class="book-list-container">
      <!-- 搜索区域 -->
      <el-card class="search-card">
        <el-form :inline="true" :model="queryParams" class="search-form">
          <el-form-item>
            <el-input
              v-model="queryParams.keyword"
              placeholder="请输入书名/作者/ISBN"
              clearable
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item>
            <el-select
              v-model="queryParams.categoryId"
              placeholder="图书分类"
              clearable
            >
              <el-option
                v-for="item in categoryOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-select
              v-model="queryParams.status"
              placeholder="图书状态"
              clearable
            >
              <el-option label="在库" :value="1" />
              <el-option label="借出" :value="2" />
              <el-option label="下架" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>
      
      <!-- 图书列表 -->
      <el-card class="list-card">
        <template #header>
          <div class="card-header">
            <span>图书列表</span>
            <el-button
              v-if="hasAdminRole"
              type="primary"
              @click="handleAdd"
            >
              添加图书
            </el-button>
          </div>
        </template>
        
        <!-- 卡片模式/表格模式切换 -->
        <div class="view-toggle">
          <el-radio-group v-model="viewMode" size="large">
            <el-radio-button label="card">
              <el-icon><Grid /></el-icon>
            </el-radio-button>
            <el-radio-button label="table">
              <el-icon><List /></el-icon>
            </el-radio-button>
          </el-radio-group>
        </div>
        
        <!-- 卡片视图 -->
        <div v-if="viewMode === 'card'">
          <el-row :gutter="20">
            <el-col v-for="book in bookList" :key="book.id" :xs="24" :sm="12" :md="8" :lg="6">
              <el-card shadow="hover" class="book-card" @click="handleView(book.id)">
                <div class="book-cover">
                  <img :src="book.coverUrl || 'https://via.placeholder.com/150x200'" alt="封面">
                  <div class="book-status" :class="getStatusClass(book.status)">
                    {{ getStatusText(book.status) }}
                  </div>
                </div>
                <div class="book-info">
                  <h3 class="book-title">{{ book.title }}</h3>
                  <p class="book-author">作者: {{ book.author }}</p>
                  <p class="book-category">分类: {{ book.categoryName }}</p>
                  <div class="book-actions">
                    <el-button
                      v-if="book.status === 1"
                      type="primary"
                      link
                      @click.stop="handleBorrow(book.id)"
                    >
                      借阅
                    </el-button>
                    <el-button
                      type="info"
                      link
                      @click.stop="handleView(book.id)"
                    >
                      详情
                    </el-button>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>
        
        <!-- 表格视图 -->
        <div v-else>
          <el-table :data="bookList" stripe style="width: 100%">
            <el-table-column prop="title" label="书名" min-width="120" />
            <el-table-column prop="author" label="作者" width="120" />
            <el-table-column prop="isbn" label="ISBN" width="120" />
            <el-table-column prop="categoryName" label="分类" width="100" />
            <el-table-column prop="publisher" label="出版社" width="120" />
            <el-table-column prop="publishDate" label="出版日期" width="100" />
            <el-table-column prop="status" label="状态" width="80">
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
                  @click="handleBorrow(row.id)"
                >
                  借阅
                </el-button>
                <el-button
                  type="info"
                  link
                  @click="handleView(row.id)"
                >
                  详情
                </el-button>
                <el-button
                  v-if="hasAdminRole"
                  type="warning"
                  link
                  @click="handleEdit(row.id)"
                >
                  编辑
                </el-button>
                <el-button
                  v-if="hasAdminRole"
                  type="danger"
                  link
                  @click="handleDelete(row.id)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        
        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="queryParams.pageNum"
            v-model:page-size="queryParams.pageSize"
            :total="total"
            :page-sizes="[8, 16, 24, 32]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
      
      <!-- 添加/编辑图书对话框 -->
      <el-dialog
        :title="dialogTitle"
        v-model="dialogVisible"
        width="600px"
        append-to-body
      >
        <el-form
          ref="bookFormRef"
          :model="bookForm"
          :rules="bookRules"
          label-width="100px"
        >
          <el-form-item label="书名" prop="title">
            <el-input v-model="bookForm.title" placeholder="请输入书名" />
          </el-form-item>
          <el-form-item label="作者" prop="author">
            <el-input v-model="bookForm.author" placeholder="请输入作者" />
          </el-form-item>
          <el-form-item label="ISBN" prop="isbn">
            <el-input v-model="bookForm.isbn" placeholder="请输入ISBN" />
          </el-form-item>
          <el-form-item label="分类" prop="categoryId">
            <el-select v-model="bookForm.categoryId" placeholder="请选择分类">
              <el-option
                v-for="item in categoryOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="出版社" prop="publisher">
            <el-input v-model="bookForm.publisher" placeholder="请输入出版社" />
          </el-form-item>
          <el-form-item label="出版日期" prop="publishDate">
            <el-date-picker
              v-model="bookForm.publishDate"
              type="date"
              placeholder="选择出版日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          <el-form-item label="库存" prop="stock">
            <el-input-number v-model="bookForm.stock" :min="0" />
          </el-form-item>
          <el-form-item label="简介" prop="description">
            <el-input
              v-model="bookForm.description"
              type="textarea"
              rows="4"
              placeholder="请输入简介"
            />
          </el-form-item>
          <el-form-item label="封面" prop="coverUrl">
            <el-input v-model="bookForm.coverUrl" placeholder="请输入封面图片URL" />
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitForm">确定</el-button>
          </div>
        </template>
      </el-dialog>
      
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
          <el-form-item label="借阅天数" prop="borrowDays">
            <el-input-number v-model="borrowForm.borrowDays" :min="1" :max="30" />
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
  import { useRouter } from 'vue-router'
  import { useUserStore } from '../../stores/user'
  import { getBookList as getBookPage, addBook, updateBook, deleteBook, getBookDetail } from '../../api/book' // Changed getBookPage to getBookList and aliased to getBookPage
  import { createBorrow } from '../../api/borrow'
  import { getCategoryList } from '../../api/category.js'
  import { ElMessage, ElMessageBox } from 'element-plus'
  
  const router = useRouter()
  const userStore = useUserStore()
  
  // 是否具有管理员角色
  const hasAdminRole = computed(() => {
    const roles = userStore.userInfo.roles || []
    return roles.includes('ADMIN') || roles.includes('SUPER_ADMIN')
  })
  
  // 查询参数
  const queryParams = reactive({
    pageNum: 1,
    pageSize: 8,
    keyword: '',
    categoryId: undefined,
    status: undefined
  })
  
  // 数据列表
  const bookList = ref([])
  const total = ref(0)
  const categoryOptions = ref([])
  const viewMode = ref('card')
  
  // 对话框
  const dialogVisible = ref(false)
  const dialogTitle = ref('添加图书')
  const bookFormRef = ref()
  const bookForm = reactive({
    id: undefined,
    title: '',
    author: '',
    isbn: '',
    categoryId: undefined,
    publisher: '',
    publishDate: '',
    stock: 1,
    description: '',
    coverUrl: ''
  })
  
  // 借阅对话框
  const borrowDialogVisible = ref(false)
  const borrowFormRef = ref()
  const borrowForm = reactive({
    bookId: undefined,
    borrowDays: 14,
    remarks: ''
  })
  
  // 表单校验规则
  const bookRules = {
    title: [{ required: true, message: '请输入书名', trigger: 'blur' }],
    author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
    isbn: [{ required: true, message: '请输入ISBN', trigger: 'blur' }],
    categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
    publisher: [{ required: true, message: '请输入出版社', trigger: 'blur' }],
    publishDate: [{ required: true, message: '请选择出版日期', trigger: 'change' }],
    stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
  }
  
  const borrowRules = {
    borrowDays: [{ required: true, message: '请输入借阅天数', trigger: 'blur' }]
  }
  
  // 获取图书列表
  const getList = async () => {
    try {
      const res = await getBookPage(queryParams)
      bookList.value = res.data.records
      total.value = res.data.total
    } catch (error) {
      console.error('获取图书列表失败:', error)
    }
  }
  
  // 获取分类列表
  const fetchAndSetCategoryOptions = async () => {
    try {
      const res = await getCategoryList()
      categoryOptions.value = res.data
    } catch (error) {
      console.error('获取分类列表失败:', error)
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
    queryParams.categoryId = undefined
    queryParams.status = undefined
    handleSearch()
  }
  
  // 处理添加图书
  const handleAdd = () => {
    resetBookForm()
    dialogTitle.value = '添加图书'
    dialogVisible.value = true
  }
  
  // 处理编辑图书
  const handleEdit = async (id) => {
    resetBookForm()
    dialogTitle.value = '编辑图书'
    
    try {
      const res = await getBookDetail(id)
      Object.assign(bookForm, res.data)
      dialogVisible.value = true
    } catch (error) {
      console.error('获取图书详情失败:', error)
    }
  }
  
  // 处理删除图书
  const handleDelete = (id) => {
    ElMessageBox.confirm('确认删除该图书吗?', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      try {
        await deleteBook(id)
        ElMessage.success('删除成功')
        getList()
      } catch (error) {
        console.error('删除图书失败:', error)
      }
    }).catch(() => {})
  }
  
  // 查看图书详情
  const handleView = (id) => {
    router.push(`/books/${id}`)
  }
  
  // 处理借阅
  const handleBorrow = (id) => {
    borrowForm.bookId = id
    borrowDialogVisible.value = true
  }
  
  // 提交表单
  const submitForm = () => {
    bookFormRef.value.validate(async (valid) => {
      if (!valid) return
      
      try {
        if (bookForm.id) {
          await updateBook(bookForm.id, bookForm)
          ElMessage.success('编辑成功')
        } else {
          await addBook(bookForm)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        getList()
      } catch (error) {
        console.error('操作失败:', error)
      }
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
        getList()
      } catch (error) {
        console.error('借阅失败:', error)
      }
    })
  }
  
  // 重置图书表单
  const resetBookForm = () => {
    bookForm.id = undefined
    bookForm.title = ''
    bookForm.author = ''
    bookForm.isbn = ''
    bookForm.categoryId = undefined
    bookForm.publisher = ''
    bookForm.publishDate = ''
    bookForm.stock = 1
    bookForm.description = ''
    bookForm.coverUrl = ''
  }
  
  // 重置借阅表单
  const resetBorrowForm = () => {
    borrowForm.bookId = undefined
    borrowForm.borrowDays = 14
    borrowForm.remarks = ''
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
    fetchAndSetCategoryOptions()
  })
  </script>
  
  <style scoped>
  .book-list-container {
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
  
  .view-toggle {
    margin-bottom: 20px;
    text-align: right;
  }
  
  .book-card {
    margin-bottom: 20px;
    cursor: pointer;
    transition: all 0.3s;
    height: 100%;
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
    position: relative;
  }
  
  .book-cover img {
    max-width: 100%;
    max-height: 100%;
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
  
  .book-author, .book-category {
    margin: 5px 0;
    font-size: 14px;
    color: #606266;
  }
  
  .book-actions {
    margin-top: 10px;
    text-align: right;
  }
  
  .pagination-container {
    margin-top: 20px;
    text-align: right;
  }
  </style>