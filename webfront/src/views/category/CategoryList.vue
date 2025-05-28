<template>
  <div class="category-list-container">
    <el-card class="category-card">
      <template #header>
        <div class="card-header">
          <span>分类管理</span>
          <el-button type="primary" @click="handleAdd">添加分类</el-button>
        </div>
      </template>
      
      <!-- 分类树形表格 -->
      <el-table
        :data="categoryList"
        row-key="id"
        border
        default-expand-all
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      >
        <el-table-column prop="name" label="分类名称" min-width="180" />
        <el-table-column prop="code" label="分类编码" width="150" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              link
              @click="handleAddChild(row)"
            >
              添加子分类
            </el-button>
            <el-button
              type="warning"
              link
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              type="danger"
              link
              @click="handleDelete(row.id)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 添加/编辑分类对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="500px"
      append-to-body
    >
      <el-form
        ref="categoryFormRef"
        :model="categoryForm"
        :rules="categoryRules"
        label-width="100px"
      >
        <el-form-item label="上级分类">
          <el-tree-select
            v-model="categoryForm.parentId"
            :data="categoryOptions"
            check-strictly
            node-key="id"
            :render-after-expand="false"
            :props="{ label: 'name', children: 'children' }"
            placeholder="请选择上级分类"
            clearable
          />
        </el-form-item>
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="分类编码" prop="code">
          <el-input v-model="categoryForm.code" placeholder="请输入分类编码" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="categoryForm.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="categoryForm.description"
            type="textarea"
            rows="3"
            placeholder="请输入分类描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCategoryTree, addCategory, updateCategory, deleteCategory } from '../../api/category'
import { ElMessage, ElMessageBox } from 'element-plus'

// 分类列表
const categoryList = ref([])
// 分类选项（用于选择上级分类）
const categoryOptions = ref([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('添加分类')
const categoryFormRef = ref()
const categoryForm = reactive({
  id: undefined,
  parentId: undefined,
  name: '',
  code: '',
  sort: 0,
  description: ''
})

// 表单校验规则
const categoryRules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入分类编码', trigger: 'blur' }]
}

// 获取分类树
const getCategoryData = async () => {
  try {
    const res = await getCategoryTree()
    categoryList.value = res.data
    
    // 构建分类选项
    const options = [
      { id: 0, name: '顶级分类', children: [] }
    ]
    if (res.data && res.data.length > 0) {
      options[0].children = [...res.data]
    }
    categoryOptions.value = options
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

// 处理添加分类
const handleAdd = () => {
  resetForm()
  dialogTitle.value = '添加分类'
  dialogVisible.value = true
}

// 处理添加子分类
const handleAddChild = (row) => {
  resetForm()
  categoryForm.parentId = row.id
  dialogTitle.value = `添加"${row.name}"的子分类`
  dialogVisible.value = true
}

// 处理编辑分类
const handleEdit = (row) => {
  resetForm()
  Object.assign(categoryForm, row)
  dialogTitle.value = '编辑分类'
  dialogVisible.value = true
}

// 处理删除分类
const handleDelete = (id) => {
  ElMessageBox.confirm('确认删除该分类吗? 如果存在子分类或关联图书将无法删除', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteCategory(id)
      ElMessage.success('删除成功')
      getCategoryData()
    } catch (error) {
      console.error('删除分类失败:', error)
    }
  }).catch(() => {})
}

// 提交表单
const submitForm = () => {
  categoryFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    try {
      if (categoryForm.id) {
        await updateCategory(categoryForm.id, categoryForm)
        ElMessage.success('编辑成功')
      } else {
        await addCategory(categoryForm)
        ElMessage.success('添加成功')
      }
      dialogVisible.value = false
      getCategoryData()
    } catch (error) {
      console.error('操作失败:', error)
    }
  })
}

// 重置表单
const resetForm = () => {
  categoryForm.id = undefined
  categoryForm.parentId = undefined
  categoryForm.name = ''
  categoryForm.code = ''
  categoryForm.sort = 0
  categoryForm.description = ''
}

onMounted(() => {
  getCategoryData()
})
</script>

<style scoped>
.category-list-container {
  padding: 20px;
}

.category-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style> 