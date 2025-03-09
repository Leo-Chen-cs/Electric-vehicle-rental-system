<template>
  <div ref="chartRef" class="chart-container"></div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  chartData: {
    type: Object,
    required: true
  }
})

const chartRef = ref(null)
let chart = null

const initChart = () => {
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['1小时', '4小时', '1天', '1周']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: props.chartData.categories
    },
    yAxis: {
      type: 'value',
      name: 'Income ($)'
    },
    series: props.chartData.series.map((item) => ({
      name: item.name,
      type: 'bar',
      data: item.data,
      stack: 'total'
    }))
  }

  chart.setOption(option)
}

onMounted(() => {
  chart = echarts.init(chartRef.value)
  initChart()
  window.addEventListener('resize', () => chart.resize())
})

watch(
  () => props.chartData,
  () => {
    initChart()
  },
  { deep: true }
)
</script>

<style scoped>
.chart-container {
  height: 400px;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
