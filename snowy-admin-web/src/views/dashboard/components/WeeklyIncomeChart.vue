<template>
  <div ref="chartRef" class="chart-container"></div>
</template>

<script setup>
import { ref, onMounted, watch, onUnmounted } from 'vue'
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
  // 自定义颜色方案
  const colorPalette = [
    '#5470c6', '#91cc75', '#fac858', '#ee6666', 
    '#73c0de', '#3ba272', '#fc8452', '#9a60b4'
  ]
  
  const option = {
    title: {
      text: 'Weekly Income Analysis',
      subtext: 'Income by Rental Duration',
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
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
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
      data: ['1h rental', '4h rental', '1d rental', '1w rental'],
      bottom: '5%',
      itemWidth: 10,
      itemHeight: 10,
      textStyle: {
        fontSize: 12
      },
      icon: 'circle',
      itemGap: 20,
      padding: [10, 20, 0, 20]
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      top: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: props.chartData.categories,
      axisLine: {
        lineStyle: {
          color: '#ddd'
        }
      },
      axisTick: {
        alignWithLabel: true
      },
      axisLabel: {
        color: '#666',
        fontSize: 12,
        interval: 0,
        rotate: props.chartData.categories.length > 7 ? 30 : 0
      }
    },
    yAxis: {
      type: 'value',
      name: 'Income (£)',
      nameTextStyle: {
        color: '#666',
        fontSize: 12,
        padding: [0, 0, 0, 5]
      },
      axisLine: {
        show: true,
        lineStyle: {
          color: '#ddd'
        }
      },
      splitLine: {
        lineStyle: {
          color: '#f5f5f5'
        }
      }
    },
    series: props.chartData.series.map((item, index) => ({
      name: item.name.replace('小时', 'h').replace('天', 'd').replace('周', 'w'),
      type: 'bar',
      data: item.data,
      stack: 'total',
      color: colorPalette[index % colorPalette.length],
      barWidth: '60%',
      emphasis: {
        focus: 'none',
        disabled: true
      },
      itemStyle: {
        borderRadius: [0, 0, 0, 0],
        shadowBlur: 5,
        shadowColor: 'rgba(0, 0, 0, 0.1)'
      },
      animationDelay: idx => idx * 100,
      animationEasing: 'elasticOut'
    }))
  }

  echarts.registerTheme('customTheme', {
    axisPointer: {
      link: {show: true},
      label: {
        backgroundColor: '#777'
      }
    }
  });
  
  chart = echarts.init(chartRef.value, 'customTheme');
  chart.setOption(option);
  
  chart.dispatchAction({
    type: 'downplay',
    seriesIndex: 'all'
  });
  
  chart.on('mouseover', function(params) {
    if (params.componentType === 'xAxis') {
      chart.dispatchAction({
        type: 'showTip',
        dataIndex: params.dataIndex,
        seriesIndex: 0
      });
    }
  });
}

onMounted(() => {
  setTimeout(() => {
    chart = echarts.init(chartRef.value, 'light')
    initChart()
  
    // 优化窗口调整时的重绘
    const resizeHandler = () => {
      if (chart) chart.resize()
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