<template>
  <div class="statistics">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>快递柜日用电量统计</template>
          <div ref="electricityChartRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>各快递公司投递占比</template>
          <div ref="companyChartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>月度快递量趋势</template>
          <div ref="monthlyChartRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>仓门使用率统计</template>
          <div ref="usageChartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { getExpressTrend, getCompanyDistribution, getLockerUsage } from '@/api/statistics'

const electricityChartRef = ref()
const companyChartRef = ref()
const monthlyChartRef = ref()
const usageChartRef = ref()

onMounted(() => {
  initElectricityChart()
  initCompanyChart()
  initMonthlyChart()
  initUsageChart()
})

const initElectricityChart = () => {
  const chart = echarts.init(electricityChartRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: ['L001', 'L002', 'L003', 'L004', 'L005'] },
    yAxis: { type: 'value', name: 'kWh' },
    series: [{
      data: [2.3, 2.8, 1.9, 2.5, 0],
      type: 'bar',
      itemStyle: { color: '#409EFF' }
    }]
  })
}

const initCompanyChart = async () => {
  const chart = echarts.init(companyChartRef.value)
  try {
    const res = await getCompanyDistribution()
    chart.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: '5%' },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        data: res.data
      }]
    })
  } catch(e) { console.error(e) }
}

const initMonthlyChart = async () => {
  const chart = echarts.init(monthlyChartRef.value)
  try {
    const res = await getExpressTrend()
    chart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: res.data.dates },
      yAxis: { type: 'value' },
      series: [{
        data: res.data.values,
        type: 'line',
        smooth: true,
        areaStyle: { opacity: 0.3 }
      }]
    })
  } catch(e) { console.error(e) }
}

const initUsageChart = async () => {
  const chart = echarts.init(usageChartRef.value)
  try {
    const res = await getLockerUsage()
    const lockers = res.data.map(item => item.lockerId)
    const rates = res.data.map(item => item.usageRate)
    
    chart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: lockers },
      yAxis: { type: 'value', max: 100, name: '%' },
      series: [{
        data: rates,
        type: 'bar',
        itemStyle: { color: '#67C23A' }
      }]
    })
  } catch(e) { console.error(e) }
}
</script>
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: ['小仓', '中仓', '大仓'] },
    yAxis: { type: 'value', max: 100, name: '%' },
    series: [{
      data: [85, 72, 58],
      type: 'bar',
      itemStyle: { color: '#67C23A' }
    }]
  })
}
</script>

<style lang="scss" scoped>
.statistics {
  .chart {
    height: 300px;
  }
}
</style>
