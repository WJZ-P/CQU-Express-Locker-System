<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
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
              <div class="stat-value">{{ stats.expressCount }}</div>
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
          <el-table :data="lockerList" style="width: 100%">
            <el-table-column prop="id" label="柜号" width="80" />
            <el-table-column prop="location" label="位置" />
            <el-table-column prop="totalCompartments" label="仓门数" width="100" />
            <el-table-column prop="usedCompartments" label="已用" width="80" />
            <el-table-column prop="electricity" label="今日用电(kWh)" width="120" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === '正常' ? 'success' : 'danger'">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default>
                <el-button type="primary" link>查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import * as echarts from 'echarts'
import { getDashboardStats, getExpressTrend, getUsageRate } from '@/api/statistics'
import { getLockerList } from '@/api/locker'

const lineChartRef = ref()
const pieChartRef = ref()

const stats = reactive({
  lockerCount: 0,
  expressCount: 0,
  userCount: 0,
  courierCount: 0
})

const lockerList = ref([])

onMounted(async () => {
   await loadData()
   initLineChart()
   initPieChart()
})

const loadData = async () => {
    // 1. Dashboard Stats
    try {
        const statsRes = await getDashboardStats()
        if(statsRes.data) {
           Object.assign(stats, statsRes.data)
        }
    } catch(e) { console.error(e) }

    // 2. Locker List for table
    try {
        const lockerRes = await getLockerList({ page: 1, pageSize: 5 })
        lockerList.value = lockerRes.data.list
    } catch(e) { console.error(e) }
}


const initLineChart = async () => {
  const chart = echarts.init(lineChartRef.value)
  let seriesData = {
      dates: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      values: [120, 132, 101, 134, 90, 230, 210] // Fallback
  }
  
  try {
      const res = await getExpressTrend()
      if(res.data && res.data.dates) {
          seriesData = res.data
      }
  } catch(e) { console.error(e) }

  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['快递量'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: seriesData.dates
    },
    yAxis: { type: 'value' },
    series: [
      { name: '快递量', type: 'line', smooth: true, data: seriesData.values }
    ]
  })
}

const initPieChart = async () => {
    const chart = echarts.init(pieChartRef.value)
    let data = [
        { value: 60, name: '已用' },
        { value: 40, name: '空闲' }
    ]
    try {
        const res = await getUsageRate()
        if(res.data) data = res.data
    } catch(e) {}

    chart.setOption({
    tooltip: { trigger: 'item' },
    series: [
      {
        type: 'pie',
        radius: ['50%', '70%'],
        data: data
      }
    ]
  })
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
