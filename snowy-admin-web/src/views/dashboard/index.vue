<template>
  <div class="dashboard">
    <a-row :gutter="16">
      <a-col :span="12">
        <a-card title="Weekly Income Statistic">
          <WeeklyIncomeChart :chartData="weeklyData" />
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card title="Rent Period Analysis">
          <DurationPreferenceChart :chartData="durationData" />
        </a-card>
      </a-col>
    </a-row>
    <a-row :gutter="16" style="margin-top: 16px">
      <a-col :span="24">
        <a-card title="Timely Data">
          <TimelyIncomeChart :chartData="timelyData" />
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import rentalApi from '@/api/rental/rentalApi'
import pricingApi from '@/api/pricing/pricingApi'
import WeeklyIncomeChart from './components/WeeklyIncomeChart.vue'
import DurationPreferenceChart from './components/DurationPreferenceChart.vue'
import TimelyIncomeChart from './components/TimelyIncomeChart.vue'

const weeklyData = ref({
  categories: [],
  series: []
})

const durationData = ref([])

const timelyData = ref({
  morning: 0, // 6:00-12:00
  afternoon: 0, // 12:00-18:00
  evening: 0 // 18:00-24:00
})

// 添加 pricing 数据存储
const pricingList = ref([])

// 获取价格配置
const getPricingData = async () => {
  try {
    const result = await pricingApi.pricingPage({
      page: 1,
      limit: 100
    })
    if (result && result.records) {
      pricingList.value = result.records.sort((a, b) => a.hour - b.hour)
    }
  } catch (error) {
    console.error('Failed to get pricing configuration:', error)
  }
}

// 处理时长显示的辅助函数
const formatDuration = (hours) => {
  if (hours >= 168) {
    // One week = 7 * 24 = 168 hours
    return `${hours / 168}w rental`
  } else if (hours >= 24) {
    // One day = 24 hours
    return `${hours / 24}d rental`
  } else {
    return `${hours}h rental`
  }
}

// 修改数据处理方法
const processRentalData = (data) => {
  // 初始化周统计数据
  const weeklyStats = {}
  const durationCount = {}
  const timeIncome = {
    morning: 0,
    afternoon: 0,
    evening: 0
  }

  // 根据 pricing 数据初始化统计对象
  pricingList.value.forEach((pricing) => {
    weeklyStats[`${pricing.hour}h`] = new Array(7).fill(0)
    durationCount[formatDuration(pricing.hour)] = 0
  })

  // 处理数据
  data.forEach((rental) => {
    const startDate = new Date(rental.startTime)
    const endDate = new Date(rental.endTime)
    const dayOfWeek = startDate.getDay()
    const hours = (endDate - startDate) / (1000 * 60 * 60)
    const startHour = startDate.getHours()

    // 统计时段收入
    if (startHour >= 6 && startHour < 12) {
      timeIncome.morning += rental.totalCost
    } else if (startHour >= 12 && startHour < 18) {
      timeIncome.afternoon += rental.totalCost
    } else if (startHour >= 18 && startHour < 24) {
      timeIncome.evening += rental.totalCost
    }

    // 找到对应的价格区间
    const pricing = pricingList.value.find((p, index) => {
      const nextPricing = pricingList.value[index + 1]
      return hours <= p.hour || !nextPricing
    })

    if (pricing) {
      const key = `${pricing.hour}h`
      const countKey = formatDuration(pricing.hour)
      weeklyStats[key][dayOfWeek] += rental.totalCost
      durationCount[countKey] = (durationCount[countKey] || 0) + 1
    }
  })

  // 设置柱状图数据
  weeklyData.value = {
    categories: ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],
    series: pricingList.value.map((pricing) => ({
      name: formatDuration(pricing.hour),
      data: weeklyStats[`${pricing.hour}h`],
      itemStyle: {
        color: getColorForHours(pricing.hour)
      }
    }))
  }

  // 设置饼图数据
  durationData.value = Object.entries(durationCount).map(([name, value]) => ({
    name,
    value,
    itemStyle: {
      color: getColorForHours(parseHoursFromLabel(name))
    }
  }))

  // 设置时段收入数据
  timelyData.value = [
    {
      name: 'Morning (6:00-12:00)',
      value: timeIncome.morning,
      itemStyle: { color: '#91cc75' }
    },
    {
      name: 'Afternoon (12:00-18:00)',
      value: timeIncome.afternoon,
      itemStyle: { color: '#fac858' }
    },
    {
      name: 'Evening (18:00-24:00)',
      value: timeIncome.evening,
      itemStyle: { color: '#ee6666' }
    }
  ]
}

// 从显示标签中解析小时数的辅助函数
const parseHoursFromLabel = (label) => {
  if (label.includes('周')) {
    return parseFloat(label.replace('周', '')) * 168
  } else if (label.includes('天')) {
    return parseFloat(label.replace('天', '')) * 24
  } else {
    return parseFloat(label.replace('小时', ''))
  }
}

// 根据小时数获取颜色
const getColorForHours = (hours) => {
  const colors = ['#91cc75', '#fac858', '#ee6666', '#5470c6', '#73c0de', '#3ba272']
  const index = pricingList.value.findIndex((p) => p.hour === hours)
  return colors[index % colors.length]
}

onMounted(async () => {
  await getPricingData() // 先获取价格配置
  try {
    const result = await rentalApi.rentalPage({
      page: 1,
      limit: 1000
    })
    if (result && result.records) {
      processRentalData(result.records)
    }
  } catch (error) {
    console.error('Failed to get rental data:', error)
  }
})
</script>

<style scoped>
.dashboard {
  padding: 24px;
}
</style>
