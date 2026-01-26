<template>
  <div class="statistics">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>快递柜日用电量统计</template>
          <div ref="electricityChartRef" class="chart" v-loading="loadingElectricity"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>各快递公司投递占比</template>
          <div ref="companyChartRef" class="chart" v-loading="loadingCompany"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>月度快递量趋势</template>
          <div ref="monthlyChartRef" class="chart" v-loading="loadingMonthly"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>格口使用率统计</template>
          <div ref="usageChartRef" class="chart" v-loading="loadingUsage"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div class="header-with-filter">
              <span>快递量趋势</span>
              <el-radio-group v-model="trendDays" size="small" @change="loadTrendData">
                <el-radio-button :value="7">近7天</el-radio-button>
                <el-radio-button :value="14">近14天</el-radio-button>
                <el-radio-button :value="30">近30天</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="trendChartRef" class="chart-wide" v-loading="loadingTrend"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getPowerStats, getCompanyStats, getMonthlyStats, getBoxUsageStats, getTrendData } from '@/api/statistics'

const electricityChartRef = ref()
const companyChartRef = ref()
const monthlyChartRef = ref()
const usageChartRef = ref()
const trendChartRef = ref()

const loadingElectricity = ref(false)
const loadingCompany = ref(false)
const loadingMonthly = ref(false)
const loadingUsage = ref(false)
const loadingTrend = ref(false)
const trendDays = ref(7)

onMounted(async () => {
  await nextTick()
  loadElectricityData()
  loadCompanyData()
  loadMonthlyData()
  loadUsageData()
  loadTrendData()
})

const loadElectricityData = async () => {
  loadingElectricity.value = true
  try {
    const res = await getPowerStats()
    initElectricityChart(res.data)
  } catch (error) {
    console.error('加载用电统计失败:', error)
    initElectricityChart(null)
  } finally {
    loadingElectricity.value = false
  }
}

const loadCompanyData = async () => {
  loadingCompany.value = true
  try {
    const res = await getCompanyStats()
    initCompanyChart(res.data)
  } catch (error) {
    console.error('加载公司统计失败:', error)
    initCompanyChart(null)
  } finally {
    loadingCompany.value = false
  }
}

const loadMonthlyData = async () => {
  loadingMonthly.value = true
  try {
    const res = await getMonthlyStats()
    initMonthlyChart(res.data)
  } catch (error) {
    console.error('加载月度统计失败:', error)
    initMonthlyChart(null)
  } finally {
    loadingMonthly.value = false
  }
}

const loadUsageData = async () => {
  loadingUsage.value = true
  try {
    const res = await getBoxUsageStats()
    initUsageChart(res.data)
  } catch (error) {
    console.error('加载使用率统计失败:', error)
    initUsageChart(null)
  } finally {
    loadingUsage.value = false
  }
}

const loadTrendData = async () => {
  loadingTrend.value = true
  try {
    const res = await getTrendData({ days: trendDays.value })
    initTrendChart(res.data)
  } catch (error) {
    console.error('加载趋势数据失败:', error)
    initTrendChart(null)
  } finally {
    loadingTrend.value = false
  }
}

const initElectricityChart = (data) => {
  const chart = echarts.init(electricityChartRef.value)
  const lockers = data?.lockers || ['L001', 'L002', 'L003', 'L004', 'L005']
  const values = data?.values || [0, 0, 0, 0, 0]
  
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: lockers },
    yAxis: { type: 'value', name: 'kWh' },
    series: [{
      data: values,
      type: 'bar',
      itemStyle: { color: '#409EFF' }
    }]
  })
}

const initCompanyChart = (data) => {
  const chart = echarts.init(companyChartRef.value)
  const chartData = data || [
    { value: 0, name: '顺丰' },
    { value: 0, name: '圆通' },
    { value: 0, name: '中通' },
    { value: 0, name: '韵达' }
  ]
  
  chart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: '5%' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: chartData
    }]
  })
}

const initMonthlyChart = (data) => {
  const chart = echarts.init(monthlyChartRef.value)
  const months = data?.months || ['1月', '2月', '3月', '4月', '5月', '6月']
  const values = data?.values || [0, 0, 0, 0, 0, 0]
  
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: months },
    yAxis: { type: 'value' },
    series: [{
      data: values,
      type: 'line',
      smooth: true,
      areaStyle: { opacity: 0.3 }
    }]
  })
}

const initUsageChart = (data) => {
  const chart = echarts.init(usageChartRef.value)
  const sizes = data?.sizes || ['小格', '中格', '大格']
  const values = data?.values || [0, 0, 0]
  
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: sizes },
    yAxis: { type: 'value', max: 100, name: '%' },
    series: [{
      data: values,
      type: 'bar',
      itemStyle: { color: '#67C23A' }
    }]
  })
}

const initTrendChart = (data) => {
  const chart = echarts.init(trendChartRef.value)
  const dates = data?.dates || []
  const depositData = data?.depositData || []
  const pickupData = data?.pickupData || []
  
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['入柜', '取件'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', boundaryGap: false, data: dates },
    yAxis: { type: 'value' },
    series: [
      { name: '入柜', type: 'line', smooth: true, data: depositData, areaStyle: { opacity: 0.3 } },
      { name: '取件', type: 'line', smooth: true, data: pickupData, areaStyle: { opacity: 0.3 } }
    ]
  })
}
</script>

<style lang="scss" scoped>
.statistics {
  .chart {
    height: 300px;
  }
  
  .chart-wide {
    height: 350px;
  }
  
  .header-with-filter {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
