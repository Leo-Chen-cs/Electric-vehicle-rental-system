<template>
  <div class="rent-container">
    <!-- 添加导航栏和返回按钮 -->
    <t-navbar title="Rent">
      <template #left>
        <div class="navbar-left-btn" @click="goBack">
          <t-icon name="chevron-left" size="24px" />
        </div>
      </template>
    </t-navbar>

    <div class="info-section">
      <!-- 添加电动车图片显示 -->
      <div class="scooter-image">
        <img :src="getScooterImage(scooterBrand)" alt="Scooter" />
      </div>

      <div class="info-item">
        <span class="label">Scooter ID</span>
        <span class="value">#{{ scooterId }}</span>
      </div>

      <div class="info-item">
        <span class="label">Start Time</span>
        <t-cell title="Select Date" :note="dateText" @click="dateVisible = true" />
        <t-popup v-model="dateVisible" placement="bottom">
          <t-date-time-picker :value="dateValue" mode="date" title="Select Date" :start="new Date(new Date().valueOf() + 1 * 60 * 1000).toString()"
            @confirm="onDateConfirm" @cancel="onDateCancel" format="YYYY-MM-DD" />

        </t-popup>
        <t-cell title="Select Time" :note="timeText" @click="timeVisible = true" />
        <t-popup v-model="timeVisible" :note="timeText" placement="bottom">
          <t-date-time-picker :value="timeValue" :mode="['hour', 'minute']" title="Select Time" @confirm="onTimeConfirm"
            @cancel="onTimeCancel" format="HH:mm" :start="new Date(new Date().valueOf() + 1 * 60 * 1000).toString()" />
        </t-popup>
      </div>

      <div class="info-item">
        <span class="label">Rental Duration</span>
        <div class="duration-select">
          <div v-for="duration in durations" :key="duration.hour" 
            class="duration-option" 
            :class="{ 'active': selectedDuration === duration.hour, 'recommended': isRecommended(duration.hour) }"
            @click="selectedDuration = duration.hour">
            {{ formatDuration(duration.hour) }}
            <span v-if="isRecommended(duration.hour)" class="recommend-tag">Recommended</span>
          </div>
        </div>
      </div>
      <div class="info-item">
        <span class="label">Estimated Cost</span>
        <div class="price-display">
          <!-- 当有折扣时显示折扣信息 -->
          <template v-if="discountRate > 0 && discountRate < 1">
            <div class="price-original">￡{{ calculatePrice }}</div>
            <div class="discount-badge">{{ Math.round((1-discountRate)*100) }}% OFF</div>
            <div class="price-final">￡{{ (calculatePrice * discountRate).toFixed(2) }}</div>
          </template>
          
          <!-- 没有折扣时只显示原价 -->
          <template v-else>
            <div class="price-final">￡{{ calculatePrice }}</div>
          </template>
        </div>
      </div>
    </div>

    <div class="action-section">
      <t-button theme="primary" block @click="handleRent">Confirm Rental</t-button>
    </div>

    <!-- 自定义支付弹窗 -->
    <div v-if="showPaymentDialog" class="custom-payment-modal-overlay" @click="handleOverlayClick">
      <div class="custom-payment-modal" @click.stop>
        <div class="modal-header">
          <span class="modal-title">Payment</span>
          <span class="modal-close" @click="handlePaymentCancel">&times;</span>
        </div>
        <div class="modal-content">
          <PaymentForm 
            :saved-card-data="getSavedCardData()" 
            @payment-success="handlePaymentSuccess" 
            @payment-error="handlePaymentError"
            @payment-cancel="handlePaymentCancel"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPrice } from '@/service/pricing'
import { rent } from '@/service/rent'
import { useUserStore } from '@/store/user'
import { canOfferDiscount } from '@/service/user'
import { inject } from 'vue'
import { LoadingPlugin } from 'tdesign-mobile-vue'
import PaymentForm from './components/PaymentForm.vue'
const $dialog = inject('$dialog')

import dayjs from 'dayjs'
const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const scooterId = ref('')
const scooterBrand = ref('')
const durations = ref([])
const selectedDuration = ref(0)
const showPaymentDialog = ref(false)
const discountRate = ref(-1)

const dateVisible = ref(false)
const timeVisible = ref(false)
const dateTimeString = ref(new Date().toString())
const dateValue = ref(dateTimeString.value)
const timeValue = ref(new Date(new Date().valueOf() + 1 * 60 * 1000).toString())
const dateText = ref(
  dayjs(dateValue.value).format('YYYY-MM-DD')
)
const timeText = ref(
  dayjs(timeValue.value).format('HH:mm')
)

const onDateCancel = () => {
  console.log('cancel');
  dateVisible.value = false;
};

const onDateConfirm = (value) => {
  console.log('confirm: ', value);
  dateValue.value = value;
  dateVisible.value = false;
  dateText.value = value
};

const onTimeCancel = () => {
  console.log('cancel');
  timeVisible.value = false;
};

const onTimeConfirm = (value) => {
  console.log('confirm: ', value);
  timeValue.value = value;
  timeVisible.value = false;
  timeText.value = value
};

// 获取路由参数
onMounted(() => {
  if (route.params.scooterId) {
    scooterId.value = route.params.scooterId
  }
  if (route.query.brand) {
    scooterBrand.value = route.query.brand
  }
  getPricingPlans()
  checkUserDiscount()
})

// 获取价格方案
const getPricingPlans = async () => {
  try {
    const { data } = await getPrice()
    durations.value = data.records.map((item) => ({
      hour: item.hour,
      price: item.price,
    }))
    if (durations.value.length > 0) {
      selectedDuration.value = durations.value[0].hour
    }
  } catch (error) {
    console.error('获取价格方案失败', error)
    // 使用TDesign的Toast组件替代uni.showToast
    import('tdesign-mobile-vue').then(({ MessagePlugin }) => {
      MessagePlugin.error('获取价格方案失败')
    })
  }
}

// 计算预估费用
const calculatePrice = computed(() => {
  const selectedPlan = durations.value.find((d) => d.hour === selectedDuration.value)
  return selectedPlan ? selectedPlan.price : 0
})

// 格式化时长显示（转换为周和天）
const formatDuration = (hours) => {
  if (hours < 24) {
    return hours === 1 ? '1 hour' : `${hours} hours`
  } else if (hours < 168) { // 不到一周
    const days = Math.floor(hours / 24)
    const remainingHours = hours % 24

    let text = days === 1 ? '1 day' : `${days} days`
    if (remainingHours > 0) {
      text += remainingHours === 1 ? ' 1 hour' : ` ${remainingHours} hours`
    }
    return text
  } else { // 一周或更多
    const weeks = Math.floor(hours / 168)
    const remainingHours = hours % 168
    const days = Math.floor(remainingHours / 24)
    const finalHours = remainingHours % 24

    let text = weeks === 1 ? '1 week' : `${weeks} weeks`

    if (days > 0) {
      text += days === 1 ? ' 1 day' : ` ${days} days`
    }

    if (finalHours > 0) {
      text += finalHours === 1 ? ' 1 hour' : ` ${finalHours} hours`
    }

    return text
  }
}

// 合并日期和时间
const getCombinedDateTime = () => {
  const date = dateText.value
  const time = timeText.value

  return date + ' ' + time + ':00'
}

const savedCard = ref(null)

// 处理租赁确认
const handleRent = async () => {
  if (!userStore.userInfo.token) {
    $dialog.alert({
      title: 'Error',
      content: 'Please log in to continue.',
    })
    setTimeout(() => {
      router.push('/my/login')
    }, 1500)
    return
  }

  try {
    // 获取用户信息
    const data = userStore.userInfo
    savedCard.value = data.bankCardId
      ? {
        bankCardId: data.bankCardId,
        bankCardExpiryMonth: data.bankCardExpiryMonth,
        bankCardExpiryYear: data.bankCardExpiryYear,
        bankCardCvv: data.bankCardCvv,
      }
      : null

    // 检查是否有优惠
    try {
      const discountResponse = await canOfferDiscount()
      discountRate.value = discountResponse.data
    } catch (error) {
      console.error('获取优惠信息失败', error)
      discountRate.value = -1
    }

    $dialog.confirm({
      title: 'Confirm Rental',
      content: `Do you want to rent scooter #${scooterId.value} for ${formatDuration(selectedDuration.value)} starting from ${dateText.value} ${timeText.value}?`,
      confirmBtn: 'Confirm',
      cancelBtn: 'Cancel',
      onConfirm: () => {
        showPaymentDialog.value = true;
      },
      onCancel: () => {
        console.log('cancel')
      },
    })
  } catch (error) {
    console.error('Failed to get user info', error)
    $dialog.alert({
      title: 'Error',
      content: 'Failed to get user info. Please try again later.',
    })
  }
}

// 处理支付取消
const handlePaymentCancel = () => {
  showPaymentDialog.value = false;
  console.log('支付已取消');
}

// 处理支付成功
const handlePaymentSuccess = async (paymentData) => {
  try {
    // 计算最终价格（应用优惠）
    const finalPrice = discountRate.value > 0
      ? calculatePrice.value * discountRate.value
      : calculatePrice.value

    // 发送租赁请求，包含位置信息
    await rent(
      scooterId.value,
      getCombinedDateTime(),
      selectedDuration.value,
      finalPrice
    )
    // 延迟后跳转到首页
    setTimeout(() => {
      router.push('/home')
    }, 1500)
  } catch (error) {
    console.error('Rental failed', error)
    $dialog.alert({
      title: 'Error',
      content: error,
      confirmBtn: 'OK'
    })
  }
}

// 处理支付错误
const handlePaymentError = (errorMessage) => {
  console.error('Payment error:', errorMessage)
  $dialog.alert({
    title: 'Error',
    content: errorMessage,
    confirmBtn: 'OK'
  })
}

// 添加返回按钮的处理方法
const goBack = () => {
  console.log('返回上一页');
  router.push('/home');
}

// 获取电动车图片
const getScooterImage = (brand) => {
  if (!brand) return ''; // 如果没有品牌信息返回空字符串
  try {
    // 尝试加载对应的图片
    return `/src/images/${brand}.png`;
  } catch (error) {
    console.error('加载电动车图片失败:', error);
    return ''; // 如果加载失败返回空字符串
  }
}

// 检查用户折扣信息
const checkUserDiscount = async () => {
  if (!userStore.userInfo.token) return;
  
  try {
    const discountResponse = await canOfferDiscount()
    discountRate.value = discountResponse.data
    console.log('User discount rate:', discountRate.value)
  } catch (error) {
    console.error('Failed to get discount information', error)
    discountRate.value = -1
  }
}

// 判断是否为推荐选项（1天）
const isRecommended = (hours) => {
  return hours === 24; // 24小时 = 1天
};

// 获取保存的卡数据
const getSavedCardData = () => {
  return savedCard.value;
}

// 添加点击遮罩关闭弹窗的方法
const handleOverlayClick = (event) => {
  // 可选：点击遮罩层关闭弹窗
  // 如果不希望点击遮罩关闭，可以移除这个功能
  showPaymentDialog.value = false;
};
</script>

<style lang="scss" scoped>
.rent-container {
  padding: 0;  /* 修改顶部内边距，为导航栏留出空间 */
  max-width: 600px;
  margin: 0 auto;
  padding-top: 56px; /* 添加顶部内边距以避免导航栏遮挡 */
}

.header {
  margin-bottom: 20px;
  text-align: center;
}

.info-section {
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  margin: 16px;  /* 调整边距 */

  // 添加电动车图片样式
  .scooter-image {
    width: 100%;
    height: 120px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 20px;
    
    img {
      max-width: 100%;
      max-height: 100%;
      object-fit: contain;
    }
  }

  .info-item {
    margin-bottom: 20px;

    .label {
      display: block;
      margin-bottom: 8px;
      color: #000;
      font-weight: bold;
    }

    .value {
      font-size: 16px;
      color: #333;

      &.price {
        font-size: 24px;
        font-weight: bold;
        color: #f60;
      }
    }
  }
}

.duration-select {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 12px;
  
  .duration-option {
    position: relative;
    padding: 10px 16px;
    border-radius: 6px;
    background-color: #f5f5f5;
    color: #333;
    font-size: 15px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s ease;
    border: 2px solid transparent;
    text-align: center;
    min-width: 90px;
    
    &:hover {
      background-color: #e8e8e8;
    }
    
    &.active {
      background-color: #0052d9;
      color: white;
      border-color: #0052d9;
    }
    
    &.recommended {
      border-color: #0052d9;
      
      &:not(.active) {
        color: #0052d9;
      }
    }
    
    .recommend-tag {
      position: absolute;
      top: -10px;
      right: -5px;
      background-color: #ff9800;
      color: white;
      padding: 2px 6px;
      border-radius: 4px;
      font-size: 10px;
      font-weight: bold;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }
  }
}

.action-section {
  margin-top: 30px;
}

/* 自定义支付弹窗样式 */
.custom-payment-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.2s ease;
}

.custom-payment-modal {
  width: 90%;
  max-width: 90%;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  overflow: hidden;
  animation: slideIn 0.3s ease;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #eee;
}

.modal-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.modal-close {
  font-size: 24px;
  color: #999;
  cursor: pointer;
  padding: 0 8px;
}

.modal-content {
  padding: 16px;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideIn {
  from { transform: translateY(20px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

.payment-form {
  padding: 16px 0;
}

.saved-card {
  padding: 10px;
  background-color: #f5f5f5;
  border-radius: 4px;
  text-align: center;
}

.picker-container {
  background-color: #fff;
  padding-bottom: 10px;
}

.picker-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #eee;
}

.picker-title {
  font-size: 16px;
  color: #333;
}

.discount-info {
  margin-bottom: 15px;
  padding: 10px;
  background-color: #f0fff0;
  border-radius: 6px;
  text-align: center;
}

.price-comparison {
  margin-top: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  
  .original-price {
    text-decoration: line-through;
    color: #999;
  }
  
  .arrow-icon {
    margin: 0 10px;
    color: #666;
  }
  
  .discounted-price {
    font-weight: bold;
    color: #ff4d4f;
    font-size: 20px;
  }
}

.price-display {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 5px;
  
  .price-original {
    font-size: 18px;
    color: #999;
    text-decoration: line-through;
    position: relative;
  }
  
  .discount-badge {
    background-color: #ff4d4f;
    color: white;
    padding: 2px 8px;
    border-radius: 12px;
    font-size: 12px;
    font-weight: bold;
  }
  
  .price-final {
    font-size: 28px;
    font-weight: bold;
    color: #ff4d4f;
  }
}

/* 添加导航栏按钮样式 */
.navbar-left-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px;
  margin-left: 8px;
  border-radius: 4px;
  cursor: pointer;
  
  .t-icon {
    color: #333;
    font-weight: bold;
  }
  
  &:active {
    background-color: rgba(0, 0, 0, 0.05);
  }
}
</style>
