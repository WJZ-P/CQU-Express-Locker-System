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

const lineChartRef = ref()
const pieChartRef = ref()

const stats = reactive({
  lockerCount: 128,
  expressCount: 1256,
  userCount: 8964,
  courierCount: 156
})

const lockerList = ref([
  { id: 'L001', location: '重庆大学A区1号门', totalCompartments: 8, usedCompartments: 5, electricity: 2.3, status: '正常' },
  { id: 'L002', location: '重庆大学A区2号门', totalCompartments: 8, usedCompartments: 8, electricity: 2.8, status: '正常' },
  { id: 'L003', location: '重庆大学B区食堂', totalCompartments: 6, usedCompartments: 3, electricity: 1.9, status: '正常' },
  { id: 'L004', location: '重庆大学C区图书馆', totalCompartments: 8, usedCompartments: 6, electricity: 2.5, status: '正常' },
  { id: 'L005', location: '重庆大学D区宿舍', totalCompartments: 8, usedCompartments: 0, electricity: 0, status: '故障' }
])

onMounted(() => {
  initLineChart()
  initPieChart()
})

const initLineChart = () => {
  const chart = echarts.init(lineChartRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['入柜', '取件', '寄存'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: { type: 'value' },
    series: [
      { name: '入柜', type: 'line', smooth: true, data: [120, 132, 101, 134, 90, 230, 210] },
      { name: '取件', type: 'line', smooth: true, data: [110, 125, 95, 128, 85, 220, 200] },
      { name: '寄存', type: 'line', smooth: true, data: [20, 32, 18, 24, 15, 45, 38] }
    ]
  })
}

const initPieChart = () => {
  const chart = echarts.init(pieChartRef.value)
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
        { value: 68, name: '已使用' },
        { value: 32, name: '空闲' }
      ]
    }]
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
