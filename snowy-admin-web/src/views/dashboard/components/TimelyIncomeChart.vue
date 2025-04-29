<template>
  <div ref="chartRef" class="chart-container"></div>
</template>

<script setup>
import { ref, onMounted, watch, onUnmounted } from 'vue'
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
  // 自定义颜色方案
  const colorPalette = [
    '#5470c6', '#91cc75', '#fac858', '#ee6666', 
    '#73c0de', '#3ba272', '#fc8452', '#9a60b4'
  ]
  
  const option = {
    title: {
      text: 'Income Distribution Analysis',
      subtext: 'By Revenue Sources',
      left: 'center',
      top: '0%',
      textStyle: {
        fontSize: 18,
        fontWeight: 'bold'
      },
      subtextStyle: {
        fontSize: 12
      },
      padding: [0, 0, 15, 0]
    },
    tooltip: {
      trigger: 'item',
      formatter: function(params) {
        // 将数值格式化为最多2位小数
        const formattedValue = parseFloat(params.value).toFixed(2);
        const formattedPercent = params.percent.toFixed(2);
        return `${params.seriesName} <br/>${params.name}: £${formattedValue} (${formattedPercent}%)`;
      },
      backgroundColor: 'rgba(255, 255, 255, 0.8)',
      borderColor: '#ccc',
      borderWidth: 1,
      textStyle: {
        color: '#333'
      },
      extraCssText: 'box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);'
    },
    legend: {
      orient: 'horizontal',
      bottom: '0%',
      itemWidth: 10,
      itemHeight: 10,
      textStyle: {
        fontSize: 12
      },
      icon: 'circle',
      itemGap: 20,
      padding: [10, 20, 0, 20]
    },
    series: [
      {
        name: 'Revenue Source',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['50%', '55%'],
        avoidLabelOverlap: true,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2,
          shadowBlur: 10,
          shadowColor: 'rgba(0, 0, 0, 0.2)'
        },
        label: {
          show: true,
          position: 'outside',
          formatter: function(params) {
            // 将数值格式化为最多2位小数
            const formattedValue = parseFloat(params.value).toFixed(2);
            const formattedPercent = params.percent.toFixed(2);
            
            // 如果使用了rich文本格式化
            if (params.name.includes('rental')) {
              return `{bold|${params.name}}\n£${formattedValue} (${formattedPercent}%)`;
            } 
            // 标准格式化（时段收入图表）
            else {
              return `${params.name}\n£${formattedValue} (${formattedPercent}%)`;
            }
          },
          fontSize: 12,
          color: '#666',
          distanceToLabelLine: 5,
          alignTo: 'edge',
          edgeDistance: '10%',
          rich: {
            bold: {
              fontWeight: 'bold',
              fontSize: 13,
              lineHeight: 20
            }
          }
        },
        labelLine: {
          length: 20,
          length2: 30,
          smooth: true,
          minTurnAngle: 120
        },
        labelLayout: {
          hideOverlap: true
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold'
          },
          itemStyle: {
            shadowBlur: 20,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        data: props.chartData,
        color: colorPalette,
        animationType: 'scale',
        animationEasing: 'elasticOut',
        animationDelay: idx => idx * 100
      }
    ]
  }

  chart.setOption(option)
}

onMounted(() => {
  setTimeout(() => {
    chart = echarts.init(chartRef.value, 'light')
    initChart()
  
    // 优化窗口调整时的重绘
    const resizeHandler = () => {
      if (chart) {
        chart.resize()
        // 重新计算布局以避免重叠
        chart.setOption({
          series: [{
            labelLayout: { hideOverlap: true }
          }]
        })
      }
    }
    window.addEventListener('resize', resizeHandler)
  
    // 组件卸载时清理事件监听
    onUnmounted(() => {
      window.removeEventListener('resize', resizeHandler)
      chart && chart.dispose()
    })
  }, 100)
})

watch(
  () => props.chartData,
  () => {
    if (chart) {
      initChart()
    }
  },
  { deep: true }
)
</script>

<style scoped>
.chart-container {
  height: 450px;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 10px;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.chart-container:hover {
  box-shadow: 0 4px 18px rgba(0, 0, 0, 0.1);
}
</style>