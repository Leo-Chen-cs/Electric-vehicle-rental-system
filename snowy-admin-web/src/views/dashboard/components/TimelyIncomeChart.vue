<template>
  <div ref="chartRef" class="chart-container"></div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  chartData: {
    type: Array,
    required: true
  }
})

const chartRef = ref(null)
let chart = null

const initChart = () => {
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: $ {c} ({d}%)'
    },
    legend: {
      top: '5%',
      left: 'center'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    series: [
      {
        name: 'Timely Income',
        type: 'pie',
        radius: ['30%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          position: 'outer',
          formatter: '{b}\n$ {c}'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '16',
            fontWeight: 'bold'
          }
        },
        data: props.chartData
      }
    ]
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
