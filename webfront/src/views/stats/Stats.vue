<template>
    <div class="stats-container">
      <!-- 统计卡片 -->
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6">
          <el-card shadow="hover" class="data-card">
            <div class="data-card-header">
              <span>总借阅量</span>
              <el-icon><DataLine /></el-icon>
            </div>
            <div class="data-card-content">
              <div class="data-card-value">{{ stats.totalBorrows || 0 }}</div>
              <div class="data-card-label">总借阅次数</div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :xs="24" :sm="12" :md="6">
          <el-card shadow="hover" class="data-card">
            <div class="data-card-header">
              <span>当前借阅</span>
              <el-icon><Reading /></el-icon>
            </div>
            <div class="data-card-content">
              <div class="data-card-value">{{ stats.borrowing || 0 }}</div>
              <div class="data-card-label">当前借阅中</div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :xs="24" :sm="12" :md="6">
          <el-card shadow="hover" class="data-card">
            <div class="data-card-header">
              <span>逾期未还</span>
              <el-icon><WarningFilled /></el-icon>
            </div>
            <div class="data-card-content">
              <div class="data-card-value">{{ stats.overdue || 0 }}</div>
              <div class="data-card-label">逾期记录数</div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :xs="24" :sm="12" :md="6">
          <el-card shadow="hover" class="data-card">
            <div class="data-card-header">
              <span>总图书数</span>
              <el-icon><Collection /></el-icon>
            </div>
            <div class="data-card-content">
              <div class="data-card-value">{{ stats.totalBooks || 0 }}</div>
              <div class="data-card-label">图书总数</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <!-- 图表区域 -->
      <el-row :gutter="20">
        <!-- 借阅趋势图 -->
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>借阅趋势</span>
                <el-radio-group v-model="trendType" size="small" @change="initCharts">
                  <el-radio-button label="week">本周</el-radio-button>
                  <el-radio-button label="month">本月</el-radio-button>
                </el-radio-group>
              </div>
            </template>
            <div ref="borrowTrendChart" class="chart-container"></div>
          </el-card>
        </el-col>
        
        <!-- 分类统计图 -->
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>分类统计</span>
                <el-radio-group v-model="categoryType" size="small" @change="initCharts">
                  <el-radio-button label="books">图书数量</el-radio-button>
                  <el-radio-button label="borrows">借阅次数</el-radio-button>
                </el-radio-group>
              </div>
            </template>
            <div ref="categoryChart" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>
      
      <!-- 热门图书排行 -->
      <el-card class="ranking-card">
        <template #header>
          <div class="card-header">
            <span>热门图书排行</span>
          </div>
        </template>
        
        <el-table :data="hotBooks" stripe style="width: 100%">
          <el-table-column type="index" label="排名" width="80" />
          <el-table-column prop="name" label="图书名称" />
          <el-table-column prop="author" label="作者" width="150" />
          <el-table-column prop="categoryName" label="分类" width="120" />
          <el-table-column prop="borrowCount" label="借阅次数" width="100" />
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="{ row }">
              <el-button
                type="primary"
                link
                @click="$router.push(`/books/${row.id}`)"
              >
                查看
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
      
      <!-- 活跃用户排行 -->
      <el-card class="ranking-card">
        <template #header>
          <div class="card-header">
            <span>活跃用户排行</span>
          </div>
        </template>
        
        <el-table :data="activeUsers" stripe style="width: 100%">
          <el-table-column type="index" label="排名" width="80" />
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="realName" label="姓名" width="150" />
          <el-table-column prop="borrowCount" label="借阅次数" width="100" />
          <el-table-column prop="lastBorrowTime" label="最近借阅时间" width="180" />
        </el-table>
      </el-card>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { getBorrowStats, getHotBooks, getActiveUsers, getBorrowTrend, getCategoryStats } from '../../api/stats'
import * as echarts from 'echarts/core'
import { BarChart, LineChart, PieChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

// 注册必要的组件
echarts.use([
  TitleComponent, TooltipComponent, LegendComponent, GridComponent,
  BarChart, LineChart, PieChart, CanvasRenderer
])

// 统计数据
const stats = ref({})
// 热门图书
const hotBooks = ref([])
// 活跃用户
const activeUsers = ref([])

// 图表类型选择
const trendType = ref('week')
const categoryType = ref('books')

// 图表DOM引用
const borrowTrendChart = ref(null)
const categoryChart = ref(null)

// 图表实例
let borrowTrendInstance = null
let categoryInstance = null

// 获取统计数据
const getStatsData = async () => {
  try {
    const res = await getBorrowStats()
    stats.value = res.data
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 获取热门图书
const getHotBooksData = async () => {
  try {
    const res = await getHotBooks()
    hotBooks.value = res.data
  } catch (error) {
    console.error('获取热门图书失败:', error)
  }
}

// 获取活跃用户
const getActiveUsersData = async () => {
  try {
    const res = await getActiveUsers()
    activeUsers.value = res.data
  } catch (error) {
    console.error('获取活跃用户失败:', error)
  }
}

// 初始化图表
const initCharts = async () => {
  await initBorrowTrendChart()
  await initCategoryChart()
}

// 初始化借阅趋势图表
const initBorrowTrendChart = async () => {
  try {
    // 获取趋势数据
    const res = await getBorrowTrend({ type: trendType.value })
    const { dates, counts } = res.data
    
    // 初始化图表
    if (!borrowTrendInstance) {
      borrowTrendInstance = echarts.init(borrowTrendChart.value)
    }
    
    // 设置图表配置
    const option = {
      title: {
        text: '借阅趋势统计',
        left: 'center'
      },
      tooltip: {
        trigger: 'axis'
      },
      xAxis: {
        type: 'category',
        data: dates
      },
      yAxis: {
        type: 'value',
        minInterval: 1
      },
      series: [
        {
          name: '借阅数',
          type: 'line',
          data: counts,
          smooth: true,
          lineStyle: {
            width: 3
          },
          itemStyle: {
            color: '#409EFF'
          },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                { offset: 0, color: 'rgba(64, 158, 255, 0.7)' },
                { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
              ]
            }
          }
        }
      ],
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      }
    }
    
    borrowTrendInstance.setOption(option)
  } catch (error) {
    console.error('初始化借阅趋势图表失败:', error)
  }
}

// 初始化分类统计图表
const initCategoryChart = async () => {
  try {
    // 获取分类统计数据
    const res = await getCategoryStats({ type: categoryType.value })
    const { categories, values } = res.data
    
    // 转换为饼图数据格式
    const pieData = categories.map((name, index) => {
      return { name, value: values[index] }
    })
    
    // 初始化图表
    if (!categoryInstance) {
      categoryInstance = echarts.init(categoryChart.value)
    }
    
    // 设置图表配置
    const option = {
      title: {
        text: categoryType.value === 'books' ? '图书分类统计' : '借阅分类统计',
        left: 'center'
      },
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left',
        type: 'scroll'
      },
      series: [
        {
          name: categoryType.value === 'books' ? '图书数量' : '借阅次数',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: '14',
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: false
          },
          data: pieData
        }
      ]
    }
    
    categoryInstance.setOption(option)
  } catch (error) {
    console.error('初始化分类统计图表失败:', error)
  }
}

// 处理窗口大小变化
const handleResize = () => {
  borrowTrendInstance && borrowTrendInstance.resize()
  categoryInstance && categoryInstance.resize()
}

onMounted(() => {
  getStatsData()
  getHotBooksData()
  getActiveUsersData()
  
  // 等待DOM渲染完成后初始化图表
  setTimeout(() => {
    initCharts()
  }, 100)
  
  // 监听窗口大小变化
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  // 清除事件监听
  window.removeEventListener('resize', handleResize)
  
  // 销毁图表实例
  borrowTrendInstance && borrowTrendInstance.dispose()
  categoryInstance && categoryInstance.dispose()
})
</script>

<style scoped>
.stats-container {
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

.chart-card, .ranking-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  height: 350px;
}
</style>