<template>
  <a-modal :visible="visible" title="Book Scooter" @ok="handleOk" @cancel="onClose" :confirmLoading="confirmLoading">
    <a-form :model="formData" :rules="formRules" ref="formRef">
      <a-form-item label="Select Scooter" name="scooterId">
        <a-select v-model:value="formData.scooterId" placeholder="Please select a scooter"
          @change="handleScooterChange">
          <a-select-option v-for="scooter in scooterList" :key="scooter.id" :value="scooter.id">
            <span>
              ID:{{ scooter.id }}
              <a-tag :color="getPriorityColor(scooter.rating)">
                {{ getPriorityLabel(scooter.rating) }}
              </a-tag>
              - Location:{{ scooter.latitude + ',' + scooter.longitude }} - Battery:{{ scooter.batteryLevel }}%
            </span>
          </a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item label="Start Time" name="startTime">
        <a-date-picker v-model:value="formData.startTime" show-time format="YYYY-MM-DD HH:mm:ss"
          :disabledDate="disabledDate" />
      </a-form-item>

      <a-form-item label="Rental Duration" name="period">
        <a-select v-model:value="formData.period" placeholder="Please select rental duration"
          @change="handleDurationChange">
          <a-select-option v-for="duration in durations" :key="duration.hour" :value="duration.hour">
            {{ duration.hour }} hours (${{ duration.price }})
          </a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item label="Estimated Cost">
        <span>${{ formData.totalCost }}</span>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import tool from '@/utils/tool'
import rentalApi from '@/api/rental/rentalApi'
import scooterApi from '@/api/scooter/scooterApi'
import dayjs from 'dayjs'

const visible = ref(false)
const confirmLoading = ref(false)
const emit = defineEmits({ successful: null })
const formRef = ref()
let userRecord = null
import { message } from 'ant-design-vue'

const durations = ref([
  { hour: 1, price: 20 },
  { hour: 2, price: 35 },
  { hour: 4, price: 60 },
  { hour: 8, price: 100 }
])

const formData = reactive({
  scooterId: '',
  startTime: null,
  period: null,
  totalCost: 0,
  userId: '',
  startLocationLongitude: null, // 改为null,等待选择滑板车后设置
  startLocationLatitude: null // 改为null,等待选择滑板车后设置
})

const formRules = {
  scooterId: [{ required: true, message: 'Please enter scooter ID' }],
  startTime: [{ required: true, message: 'Please select start time' }],
  period: [{ required: true, message: 'Please select rental duration' }]
}

const handleDurationChange = (value) => {
  const selectedDuration = durations.value.find((d) => d.hour === value)
  if (selectedDuration) {
    formData.totalCost = selectedDuration.price
  }
}

const disabledDate = (current) => {
  return current && current < dayjs().startOf('day')
}

const scooterList = ref([])

// 获取可用滑板车列表
const getScooterList = async () => {
  try {
    const data = await scooterApi.getAvailableScooters() // 解构出 data
    if (data) {
      scooterList.value = Array.isArray(data) ? data : data.records || []
    } else {
      scooterList.value = []
    }
  } catch (error) {
    console.error('获取滑板车列表错误:', error)
    tool.message.error('获取滑板车列表失败')
    scooterList.value = []
  }
}

// 监听滑板车选择 - 更新经纬度逻辑
const handleScooterChange = (scooterId) => {
  const selectedScooter = scooterList.value.find((s) => s.id === scooterId)
  if (selectedScooter) {
    // 使用滑板车的经纬度信息
    formData.startLocationLongitude = selectedScooter.longitude
    formData.startLocationLatitude = selectedScooter.latitude
  }
}

const onOpen = async (record) => {
  visible.value = true
  userRecord = record
  formData.userId = record.id
  formRef.value?.resetFields()
  // 打开弹窗时获取滑板车列表
  await getScooterList()
}

const onClose = () => {
  visible.value = false
  formRef.value?.resetFields()
}

const handleOk = () => {
  formRef.value?.validate().then(() => {
    confirmLoading.value = true
    const params = {
      id: formData.scooterId,
      userId: formData.userId,
      startTime: dayjs(formData.startTime).valueOf(),
      period: formData.period,
      totalCost: formData.totalCost,
      startLocationLongitude: formData.startLocationLongitude,
      startLocationLatitude: formData.startLocationLatitude
    }

    rentalApi
      .rentalForUser(
        params.id,
        params.userId,
        params.startTime,
        params.period,
        params.totalCost,
        params.startLocationLongitude,
        params.startLocationLatitude
      )
      .then(() => {
        visible.value = false
        emit('successful')
        message.success('Booking successful')
      })
      .finally(() => {
        confirmLoading.value = false
      })
  })
}

// 添加优先级相关方法
const getPriorityColor = (rating) => {
  const colors = {
    1: '#f50', // 红色 - 高优先级
    2: '#fa8c16', // 橙色 - 中优先级
    3: '#52c41a', // 绿色 - 低优先级
    default: '#d9d9d9' // 灰色 - 默认
  }
  return colors[rating] || colors.default
}

const getPriorityLabel = (rating) => {
  const labels = {
    1: 'High Priority',
    2: 'Medium Priority',
    3: 'Low Priority'
  }
  return labels[rating] || 'Unknown Priority'
}

defineExpose({
  onOpen
})
</script>

<style scoped>
.ant-tag {
  margin-left: 8px;
  border-radius: 4px;
}
</style>
