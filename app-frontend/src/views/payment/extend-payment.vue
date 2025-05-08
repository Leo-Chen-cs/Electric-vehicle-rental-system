<template>
  <div class="payment-container">
    <t-navbar title="Confirm Payment" leftIcon="chevron-left" @click-left="goBack" />

    <div class="payment-content">
      <div class="card rental-summary">
        <div class="summary-title">Rental Extension Summary</div>
        <div class="summary-item">
          <span class="label">Scooter ID</span>
          <span class="value">#{{ scooterId }}</span>
        </div>
        <div class="summary-item">
          <span class="label">Extension Duration</span>
          <span class="value">{{ formatDuration(hours) }}</span>
        </div>
        <div class="summary-item">
          <span class="label">Original End Time</span>
          <span class="value">{{ formatTime(originalEndTime) }}</span>
        </div>
        <div class="summary-item">
          <span class="label">New End Time</span>
          <span class="value">{{ calculateNewEndTime() }}</span>
        </div>
      </div>

      <div class="card payment-details">
        <div class="payment-title">Payment Details</div>

        <div v-if="discountRate > 0 && discountRate < 1" class="discount-info">
          <t-tag theme="success">{{ `Discount: ${Math.round((1-discountRate) * 100)}% Off` }}</t-tag>
          <div class="price-comparison">
            <span class="original-price">${{ originalPrice.toFixed(2) }}</span>
            <span class="discounted-price">${{ (originalPrice * discountRate).toFixed(2) }}</span>
          </div>
        </div>

        <div v-if="savedCard" class="saved-card">
          <div class="card-icon">
            <img src="@/icons/credit-card.svg" alt="Credit Card" class="card-icon-img" 
              onerror="this.onerror=null; this.classList.add('fallback-icon')"/>
          </div>
          <div class="card-info">
            <div class="card-number">**** **** **** {{ savedCard.slice(-4) }}</div>
            <div class="card-label">{{ expiryDate ? `Expires: ${expiryDate}` : 'Card on file' }}</div>
          </div>
          <div class="card-badge">Primary</div>
        </div>
        <div v-else class="no-card-warning">
          <t-icon name="warning-circle" size="18px" />
          <span>No saved payment method. Please add a card in your profile.</span>
        </div>

        <div class="total-section">
          <div class="total-label">Total Amount</div>
          <div class="total-amount">
            {{ discountRate < 1 ? '$' + (originalPrice * discountRate).toFixed(2) : '$' + originalPrice.toFixed(2) }}
          </div>
        </div>
      </div>
    </div>

    <div class="payment-footer">
      <t-button theme="default" class="cancel-btn" @click="goBack">Cancel</t-button>
      <t-button theme="primary" class="confirm-btn" @click="confirmPayment" :loading="loading">
        Confirm and Pay
      </t-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { extendRental } from '@/service/rent';
import { useUserStore } from '@/store/user';
import { canOfferDiscount } from '@/service/user';
import { formatDuration } from '@/utils/timeFormat';
import { MessagePlugin } from 'tdesign-mobile-vue';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const rentalId = ref('');
const scooterId = ref('');
const hours = ref(0);
const originalPrice = ref(0);
const discountRate = ref(1);
const originalEndTime = ref(null);
const loading = ref(false);
const savedCard = ref(null);
const expiryDate = ref('');

onMounted(async () => {
  // Get data from route parameters
  if (route.query) {
    rentalId.value = route.query.rentalId || '';
    scooterId.value = route.query.scooterId || '';
    hours.value = Number(route.query.hours) || 0;
    originalPrice.value = Number(route.query.price) || 0;
    originalEndTime.value = route.query.endTime ? new Date(Number(route.query.endTime)) : null;
    
    // Parse payment data if available
    if (route.query.paymentData) {
      try {
        const paymentData = JSON.parse(route.query.paymentData);
        if (paymentData.expiryDate) {
          expiryDate.value = paymentData.expiryDate;
        }
      } catch (e) {
        console.error('Failed to parse payment data', e);
      }
    }
  }

  // Get user's credit card information
  const user = userStore.userInfo;
  if (user && user.bankCardId) {
    savedCard.value = user.bankCardId;
    
    // Get expiry date if available and not already set
    if (!expiryDate.value && user.bankCardExpiryMonth && user.bankCardExpiryYear) {
      const month = String(user.bankCardExpiryMonth).padStart(2, '0');
      const year = String(user.bankCardExpiryYear).slice(-2);
      expiryDate.value = `${month}/${year}`;
    }
  }

  // Get discount information
  try {
    const response = await canOfferDiscount();
    discountRate.value = response.data;
  } catch (error) {
    console.error('Failed to get discount info:', error);
    discountRate.value = 1;
  }
});

const formatTime = (timestamp) => {
  if (!timestamp) return 'N/A';
  return new Date(timestamp).toLocaleString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    hour12: true,
  });
};

const calculateNewEndTime = () => {
  if (!originalEndTime.value) return 'N/A';

  const newEndTime = new Date(originalEndTime.value);
  newEndTime.setHours(newEndTime.getHours() + hours.value);

  return formatTime(newEndTime);
};

const confirmPayment = async () => {
  if (!rentalId.value || !hours.value) {
    MessagePlugin.error('Missing rental information');
    return;
  }

  loading.value = true;

  try {
    // Calculate final price (apply discount)
    const finalPrice = discountRate.value < 1
      ? (originalPrice.value * discountRate.value).toFixed(2)
      : originalPrice.value;

    await extendRental(
      rentalId.value,
      hours.value,
      finalPrice
    );

    MessagePlugin.success('Rental extended successfully');

    // Redirect to records page after a delay
    setTimeout(() => {
      router.replace('/records');
    }, 1500);
  } catch (error) {
    console.error('Failed to extend rental:', error);
    MessagePlugin.error('Failed to extend rental. Please try again.');
    loading.value = false;
  }
};

const goBack = () => {
  router.back();
};
</script>

<style lang="scss" scoped>
.payment-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f5f7fa;
  padding-bottom: 80px;
}

.payment-content {
  flex: 1;
  padding: 16px;
  margin-top: 40px;

  .card {
    background: white;
    border-radius: 16px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
    padding: 20px;
    margin-bottom: 20px;
  }

  .rental-summary {
    .summary-title {
      font-size: 18px;
      font-weight: 600;
      color: #222;
      margin-bottom: 20px;
      padding-bottom: 12px;
      border-bottom: 1px solid #eee;
    }

    .summary-item {
      display: flex;
      justify-content: space-between;
      margin-bottom: 16px;

      .label {
        color: #666;
        font-size: 15px;
      }

      .value {
        color: #222;
        font-weight: 500;
        font-size: 15px;
      }
    }
  }

  .payment-details {
    .payment-title {
      font-size: 18px;
      font-weight: 600;
      color: #222;
      margin-bottom: 20px;
      padding-bottom: 12px;
      border-bottom: 1px solid #eee;
    }

    .discount-info {
      background-color: #f0fff4;
      border-radius: 12px;
      padding: 16px;
      margin-bottom: 20px;
      text-align: center;

      .price-comparison {
        margin-top: 10px;
        display: flex;
        justify-content: center;
        align-items: center;
        gap: 16px;
      }

      .original-price {
        text-decoration: line-through;
        color: #999;
        font-size: 16px;
      }

      .discounted-price {
        font-weight: bold;
        color: #18a058;
        font-size: 20px;
      }
    }

    .saved-card {
      display: flex;
      align-items: center;
      background-color: #f9fbfd;
      padding: 16px;
      border-radius: 12px;
      border: 1px solid #e6effc;
      margin-bottom: 20px;
      position: relative;

      .card-icon {
        width: 44px;
        height: 44px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 16px;
        background: #edf2ff;
        border-radius: 12px;
        
        .card-icon-img {
          width: 26px;
          height: 26px;
          object-fit: contain;
        }
        
        .fallback-icon::before {
          content: "💳";
          font-size: 24px;
        }
      }

      .card-info {
        flex: 1;
        
        .card-number {
          font-weight: 600;
          font-size: 16px;
          color: #222;
          margin-bottom: 4px;
        }

        .card-label {
          color: #666;
          font-size: 13px;
        }
      }
      
      .card-badge {
        position: absolute;
        top: -10px;
        right: 10px;
        background: #0052d9;
        color: white;
        font-size: 11px;
        font-weight: 500;
        padding: 3px 8px;
        border-radius: 10px;
        box-shadow: 0 2px 4px rgba(0, 82, 217, 0.2);
      }
    }

    .no-card-warning {
      display: flex;
      align-items: center;
      gap: 10px;
      background-color: #fff8e6;
      padding: 16px;
      border-radius: 12px;
      color: #ff9800;
      font-size: 15px;
      margin-bottom: 20px;
    }
    
    .total-section {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-top: 20px;
      padding-top: 16px;
      border-top: 1px dashed #eee;
      
      .total-label {
        font-size: 16px;
        color: #666;
      }
      
      .total-amount {
        font-size: 22px;
        font-weight: 700;
        color: #0052d9;
      }
    }
  }
}

.payment-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  padding: 16px 20px;
  display: flex;
  gap: 16px;
  box-shadow: 0 -4px 16px rgba(0, 0, 0, 0.05);

  .cancel-btn {
    flex: 1;
    height: 48px;
  }

  .confirm-btn {
    flex: 2;
    height: 48px;
    font-size: 16px;
    font-weight: bold;
  }
}
</style>
