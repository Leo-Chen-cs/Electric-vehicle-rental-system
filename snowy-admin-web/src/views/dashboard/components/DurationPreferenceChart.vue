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
  // 定义与条形图完全一致的颜色映射关系
  const typeColorMap = {
    '1h rental': '#5470c6',  // 蓝色
    '4h rental': '#91cc75',  // 绿色
    '1d rental': '#fac858',  // 黄色
    '1w rental': '#ee6666'   // 红色
  };
  
  // 处理数据，确保颜色映射正确
  const processedData = props.chartData.map(item => {
    return {
      ...item,
      itemStyle: {
        color: typeColorMap[item.name] || '#5470c6' // 使用映射的颜色，默认蓝色
      }
    };
  });
  
  const option = {
    title: {
      text: 'User Duration Preference Analysis',
      subtext: 'Statistics by Time Periods',
      left: 'center',
      top: '-1%',
      textStyle: {
        fontSize: 18,
        fontWeight: 'bold'
      },
      subtextStyle: {
        fontSize: 12
      }
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)',
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
      bottom: '-1%',
      itemWidth: 10,
      itemHeight: 10,
      textStyle: {
        fontSize: 12
      },
      icon: 'circle',
      formatter: function(name) {
        return name;
      }
    },
    series: [
      {
        name: 'Duration Preference',
        type: 'pie',
        radius: ['45%', '75%'],
        center: ['50%', '50%'],
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
          formatter: '{b}\n{c} ({d}%)',
          fontSize: 12,
          color: '#666'
        },
        labelLine: {
          length: 15,
          length2: 10,
          smooth: true
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
        data: processedData,
        animationType: 'scale',
        animationEasing: 'elasticOut',
        animationDelay: idx => idx * 100
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
