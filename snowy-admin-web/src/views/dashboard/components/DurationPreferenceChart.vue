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
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    series: [
      {
        name: 'Duration Preference',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          formatter: '{b}: {c} ({d}%)'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '20',
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
