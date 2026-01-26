<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row" v-loading="statsLoading">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">{{ stats.lockerCount }}</div>
              <div class="stat-label">快递柜总数</div>
            </div>
            <el-icon class="stat-icon" style="color: #409EFF"><Box /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">{{ stats.todayOrderCount }}</div>
              <div class="stat-label">今日快递数</div>
            </div>
            <el-icon class="stat-icon" style="color: #67C23A"><Postcard /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">{{ stats.userCount }}</div>
              <div class="stat-label">注册用户数</div>
            </div>
            <el-icon class="stat-icon" style="color: #E6A23C"><User /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">{{ stats.courierCount }}</div>
              <div class="stat-label">快递员数量</div>
            </div>
            <el-icon class="stat-icon" style="color: #F56C6C"><Avatar /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 第二行统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-card-secondary">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingOrderCount }}</div>
              <div class="stat-label">待取件订单</div>
            </div>
            <el-icon class="stat-icon" style="color: #E6A23C"><Clock /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-card-secondary">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">{{ stats.overtimeOrderCount }}</div>
              <div class="stat-label">超时订单</div>
            </div>
            <el-icon class="stat-icon" style="color: #F56C6C"><Warning /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-card-secondary">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">{{ stats.availableBoxCount }}</div>
              <div class="stat-label">空闲格口</div>
            </div>
            <el-icon class="stat-icon" style="color: #67C23A"><Grid /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-card-secondary">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">{{ stats.faultBoxCount }}</div>
              <div class="stat-label">故障格口</div>
            </div>
            <el-icon class="stat-icon" style="color: #909399"><CircleClose /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <span>近7日快递趋势</span>
          </template>
          <div ref="lineChartRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <span>快递柜使用率</span>
          </template>
          <div ref="pieChartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快递柜状态 -->
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <span>快递柜实时状态</span>
          </template>
          <el-table :data="lockerList" style="width: 100%" v-loading="lockerLoading">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="name" label="柜名" width="120" />
            <el-table-column prop="location" label="位置" />
            <el-table-column prop="totalBox" label="格口数" width="100" />
            <el-table-column prop="availableBox" label="空闲" width="80" />
            <el-table-column label="使用率" width="120">
              <template #default="{ row }">
                <el-progress 
                  :percentage="row.totalBox > 0 ? Math.round((row.totalBox - row.availableBox) / row.totalBox * 100) : 0" 
                  :stroke-width="10"
                  :color="getUsageColor"
                />
              </template>
            </el-table-column>
            <el-table-column prop="enabled" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.enabled === 1 ? 'success' : 'danger'">
                  {{ row.enabled === 1 ? '正常' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button type="primary" link @click="goToCompartment(row.id)">查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { getDashboardStats, getTrendData, getDistributionData } from '@/api/statistics'
import { getLockerList } from '@/api/locker'

const router = useRouter()
const lineChartRef = ref()
const pieChartRef = ref()
const statsLoading = ref(false)
const lockerLoading = ref(false)

const stats = reactive({
  lockerCount: 0,
  todayOrderCount: 0,
  userCount: 0,
  courierCount: 0,
  pendingOrderCount: 0,
  overtimeOrderCount: 0,
  availableBoxCount: 0,
  faultBoxCount: 0
})

const lockerList = ref([])

const getUsageColor = (percentage) => {
  if (percentage < 50) return '#67C23A'
  if (percentage < 80) return '#E6A23C'
  return '#F56C6C'
}

const loadDashboardStats = async () => {
  statsLoading.value = true
  try {
    const res = await getDashboardStats()
    Object.assign(stats, res.data)
  } catch (error) {
    console.error('加载统计数据失败:', error)
  } finally {
    statsLoading.value = false
  }
}

const loadLockers = async () => {
  lockerLoading.value = true
  try {
    const res = await getLockerList({ page: 1, pageSize: 10 })
    lockerList.value = res.data.list || []
  } catch (error) {
    console.error('加载快递柜列表失败:', error)
  } finally {
    lockerLoading.value = false
  }
}

const loadTrendData = async () => {
  try {
    const res = await getTrendData({ days: 7 })
    if (res.data) {
      initLineChart(res.data)
    }
  } catch (error) {
    console.error('加载趋势数据失败:', error)
    // 使用默认数据
    initLineChart(null)
  }
}

const loadDistributionData = async () => {
  try {
    const res = await getDistributionData()
    if (res.data) {
      initPieChart(res.data)
    }
  } catch (error) {
    console.error('加载分布数据失败:', error)
    // 使用默认数据
    initPieChart(null)
  }
}

onMounted(async () => {
  await loadDashboardStats()
  await loadLockers()
  await nextTick()
  loadTrendData()
  loadDistributionData()
})

const initLineChart = (data) => {
  const chart = echarts.init(lineChartRef.value)
  const defaultData = {
    dates: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
    depositData: [0, 0, 0, 0, 0, 0, 0],
    pickupData: [0, 0, 0, 0, 0, 0, 0]
  }
  const chartData = data || defaultData
  
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['入柜', '取件'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: chartData.dates
    },
    yAxis: { type: 'value' },
    series: [
      { name: '入柜', type: 'line', smooth: true, data: chartData.depositData, areaStyle: { opacity: 0.3 } },
      { name: '取件', type: 'line', smooth: true, data: chartData.pickupData, areaStyle: { opacity: 0.3 } }
    ]
  })
}

const initPieChart = (data) => {
  const chart = echarts.init(pieChartRef.value)
  const used = data?.usedCount || stats.lockerCount - stats.availableBoxCount || 0
  const available = data?.availableCount || stats.availableBoxCount || 0
  
  chart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: '5%', left: 'center' },
    series: [{
      name: '使用率',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 16, fontWeight: 'bold' } },
      labelLine: { show: false },
      data: [
        { value: used, name: '已使用' },
        { value: available, name: '空闲' }
      ]
    }]
  })
}

const goToCompartment = (lockerId) => {
  router.push({ path: '/locker/compartment', query: { lockerId } })
}
</script>

<style lang="scss" scoped>
.dashboard {
  .stats-row {
    margin-bottom: 20px;
  }

  .stat-card {
    .stat-content {
      display: flex;
      align-items: center;
      justify-content: space-between;
    }

    .stat-value {
      font-size: 28px;
      font-weight: bold;
      color: #303133;
    }

    .stat-label {
      font-size: 14px;
      color: #909399;
      margin-top: 8px;
    }

    .stat-icon {
      font-size: 48px;
      opacity: 0.8;
    }
  }

  .chart-row {
    margin-bottom: 20px;
  }

  .chart {
    height: 300px;
  }
}
</style>
