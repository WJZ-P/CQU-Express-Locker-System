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

const initCompanyChart = () => {
  const chart = echarts.init(companyChartRef.value)
  chart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: '5%' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: [
        { value: 35, name: '顺丰' },
        { value: 28, name: '圆通' },
        { value: 22, name: '中通' },
        { value: 15, name: '韵达' }
      ]
    }]
  })
}

const initMonthlyChart = () => {
  const chart = echarts.init(monthlyChartRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: ['1月', '2月', '3月', '4月', '5月', '6月'] },
    yAxis: { type: 'value' },
    series: [{
      data: [820, 932, 901, 1034, 1290, 1330],
      type: 'line',
      smooth: true,
      areaStyle: { opacity: 0.3 }
    }]
  })
}

const initUsageChart = () => {
  const chart = echarts.init(usageChartRef.value)
  chart.setOption({
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
