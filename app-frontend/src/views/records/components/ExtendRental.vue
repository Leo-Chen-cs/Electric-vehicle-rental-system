<template>
  <t-popup v-model:visible="isVisible" placement="bottom" close-on-overlay-click>
    <div class="extend-popup">
      <div class="header">
        <span class="title">Extend Rental</span>
        <t-icon name="close" size="24px" class="close" @click="close" />
      </div>

      <div class="content">
        <div class="subtitle">Select Additional Time</div>
        
        <!-- 优化后的时间段选择 -->
        <div class="duration-grid">
          <div v-for="plan in pricingPlans" :key="plan.hour"
            :class="['duration-option', { active: selectedHours === plan.hour }]" @click="selectPlan(plan)">
            <div class="time-container">
              <div class="hours">{{ formatDuration(plan.hour) }}</div>
              <div class="description">Extend your rental</div>
            </div>
            <div class="price">${{ plan.price }}</div>
          </div>
        </div>

        <!-- 只保留银行卡支付选项 -->
        <div v-if="selectedHours > 0 && hasCardInfo" class="payment-methods">
          <div class="subtitle">Payment Method</div>
          
          <!-- 只保留已保存的银行卡信息 -->
          <div class="saved-card active">
            <div class="card-icon">
              <img src="@/icons/credit-card.svg" alt="Credit Card" class="card-icon-img" onerror="this.src=''; this.classList.add('fallback-icon')"/>
            </div>
            <div class="card-info">
              <div class="card-number">**** **** **** {{ cardLastFour }}</div>
              <div class="card-expiry">Expires: {{ cardData.expiryDate }}</div>
            </div>
            <div class="check-icon">✓</div>
          </div>
        </div>
        
        <!-- 当没有银行卡信息时显示提示 -->
        <div v-if="selectedHours > 0 && !hasCardInfo" class="no-card-info">
          <p>You don't have any saved card information.</p>
          <p>You'll be redirected to add payment details.</p>
        </div>
      </div>

      <div class="footer">
        <t-button block theme="primary" :disabled="!selectedHours" @click="handleConfirm">
          {{ getButtonText() }}
        </t-button>
      </div>
    </div>
  </t-popup>
</template>

<script setup>
import { ref, watch, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getPrice } from '@/service/pricing'
import { formatDuration } from '@/utils/timeFormat'
import { MessagePlugin } from 'tdesign-mobile-vue'
import { useUserStore } from '@/store/user' // Import user store
import { getUserInfo } from '@/service/user' // Import user service methods

const props = defineProps({
  rentalId: { type: String, required: true },
  scooterId: { type: String, required: true },
  endTime: { type: [String, Number], required: false }
})

const router = useRouter()
const userStore = useUserStore()
const isVisible = ref(false)
const pricingPlans = ref([])
const selectedHours = ref(0)
const selectedPrice = ref(0)

// Card data object
const cardData = ref({
  cardNumber: '',
  expiryDate: '',
  cvv: ''
})

// Computed property: check if card info exists
const hasCardInfo = computed(() => {
  return cardData.value.cardNumber.length > 0;
})

// Calculate last four digits of card number
const cardLastFour = computed(() => {
  if (cardData.value.cardNumber) {
    const cardNumberClean = cardData.value.cardNumber.replace(/\s/g, '');
    return cardNumberClean.slice(-4);
  }
  return '';
})

// Get button text based on current selection
const getButtonText = () => {
  if (!selectedHours.value) return 'Select a duration';
  return 'Continue to Payment';
}

// Select pricing plan
const selectPlan = (plan) => {
  selectedHours.value = plan.hour
  selectedPrice.value = plan.price
}

// Load pricing plans
const loadPricingPlans = async () => {
  try {
    const { data } = await getPrice()
    pricingPlans.value = data.records || []
  } catch (error) {
    console.error('Failed to load pricing plans:', error)
    MessagePlugin.error('Failed to load pricing options')
  }
}

// Load user's bank card information
const loadUserCardInfo = async () => {
  try {
    const response = await getUserInfo();
    const userData = response.data;
    
    // Check if user data contains bank card information
    if (userData && userData.bankCardId) {
      // Format card number - add space every 4 digits
      let cardNum = userData.bankCardId;
      let formattedCardNum = '';
      for (let i = 0; i < cardNum.length; i++) {
        if (i > 0 && i % 4 === 0) {
          formattedCardNum += ' ';
        }
        formattedCardNum += cardNum[i];
      }
      cardData.value.cardNumber = formattedCardNum;
      
      // Format expiry date - combine into MM/YY format
      if (userData.bankCardExpiryMonth && userData.bankCardExpiryYear) {
        const month = String(userData.bankCardExpiryMonth).padStart(2, '0');
        const year = String(userData.bankCardExpiryYear).slice(-2);
        cardData.value.expiryDate = `${month}/${year}`;
      }
      
      // Fill in CVV
      if (userData.bankCardCvv) {
        cardData.value.cvv = userData.bankCardCvv;
      }
      
      console.log('User bank card data loaded');
    }
  } catch (error) {
    console.error('Failed to get user information:', error);
    // No special handling on failure, form will remain empty
  }
}

const handleConfirm = () => {
  if (!selectedHours.value) return

  // Build payment data for saved card
  const paymentData = {
    type: 'card',
    provider: 'savedCard'
  }
  
  // Add card information
  if (hasCardInfo.value) {
    paymentData.cardNumber = cardData.value.cardNumber.replace(/\s/g, '');
    paymentData.expiryDate = cardData.value.expiryDate;
    paymentData.cvv = cardData.value.cvv;
  }

  // Close popup
  close()

  // Navigate to payment page
  router.push({
    path: '/payment/extend',
    query: {
      rentalId: props.rentalId,
      scooterId: props.scooterId,
      hours: selectedHours.value,
      price: selectedPrice.value,
      endTime: props.endTime,
      paymentMethod: 'savedCard',
      paymentData: JSON.stringify(paymentData)
    }
  })
}

const show = () => {
  // Reset selection
  selectedHours.value = 0
  selectedPrice.value = 0

  // Show popup
  isVisible.value = true
  loadPricingPlans()
  loadUserCardInfo()
}

const close = () => {
  isVisible.value = false
}

// Load user bank card information when component is mounted
onMounted(async () => {
  await loadUserCardInfo();
})

defineExpose({ show, close })
</script>

<style lang="scss" scoped>
.extend-popup {
  background: #fff;
  border-radius: 16px 16px 0 0;
  padding: 20px;
  min-height: 300px;

  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .title {
      font-size: 18px;
      font-weight: bold;
    }

    .close {
      color: #666;
      cursor: pointer;
    }
  }

  .content {
    .subtitle {
      font-size: 14px;
      color: #666;
      margin-bottom: 16px;
      margin-top: 20px;
    }

    /* Improved duration grid */
    .duration-grid {
      display: flex;
      flex-direction: column;
      gap: 12px;
      margin-bottom: 24px;

      .duration-option {
        display: flex;
        justify-content: space-between;
        align-items: center;
        border: 1px solid #ddd;
        border-radius: 12px;
        padding: 16px;
        cursor: pointer;
        transition: all 0.3s ease;

        &.active {
          border-color: #0052d9;
          background: #eaf4fe;
        }
        
        .time-container {
          display: flex;
          flex-direction: column;
          
          .hours {
            font-size: 16px;
            font-weight: 500;
            color: #333;
          }
          
          .description {
            font-size: 12px;
            color: #666;
            margin-top: 4px;
          }
        }

        .price {
          font-size: 16px;
          color: #f60;
          font-weight: 600;
        }
      }
    }
    
    /* Payment method styles */
    .payment-methods {
      margin-top: 20px;
      
      .saved-card {
        display: flex;
        align-items: center;
        padding: 16px;
        border: 1px solid #0052d9;
        border-radius: 12px;
        margin-bottom: 12px;
        background: #eaf4fe;
        
        .card-icon {
          width: 32px;
          height: 32px;
          margin-right: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          
          .card-icon-img {
            width: 100%;
            height: 100%;
            object-fit: contain;
          }
          
          /* Fallback when image fails to load */
          .fallback-icon::before {
            content: "💳";
            font-size: 24px;
          }
        }
        
        .card-info {
          flex: 1;
          
          .card-number {
            font-size: 15px;
            font-weight: 500;
          }
          
          .card-expiry {
            font-size: 13px;
            color: #666;
            margin-top: 3px;
          }
        }
        
        .check-icon {
          color: #0052d9;
          font-weight: bold;
          font-size: 18px;
        }
      }
    }
    
    /* No card info message */
    .no-card-info {
      margin: 20px 0;
      padding: 16px;
      background: #f8f8f8;
      border-radius: 12px;
      text-align: center;
      
      p {
        margin: 4px 0;
        color: #666;
        font-size: 14px;
        
        &:first-child {
          color: #333;
          font-weight: 500;
        }
      }
    }
  }

  .footer {
    margin-top: 20px;
    
    button {
      height: 48px;
      font-size: 16px;
      font-weight: 500;
    }
  }
}
</style>
