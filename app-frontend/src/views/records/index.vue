<template>
  <div class="records-container">
    <t-tabs v-model="activeTab" @change="handleTabChange" class="custom-tabs">
      <t-tab-panel value="current" label="Current">
        <div class="records-list">
          <template v-if="loading">
            <div class="loading-container">
              <t-loading size="medium" text="Loading records..." />
            </div>
          </template>
          <template v-else-if="activeRecords.length === 0">
            <div class="empty-state">
              <t-empty description="No active rentals" />
              <t-button theme="primary" size="medium" shape="round" @click="fetchRecords">
                <template #icon><t-icon name="refresh" /></template>
                Refresh
              </t-button>
            </div>
          </template>
          <template v-else>
            <div v-for="record in activeRecords" :key="record.id" class="record-card">
              <div class="header">
                <span class="scooter-id">
                  <t-icon name="scooter" size="20px" />
                  Scooter #{{ record.scooterId }}
                </span>
                <t-tag theme="success" variant="light-outline" class="status-tag">Active</t-tag>
              </div>
              <div class="details">
                <div class="info-row">
                  <span class="label"><t-icon name="time" /> Start Time</span>
                  <span class="value">{{ formatTime(record.startTime) }}</span>
                </div>
                <div class="info-row">
                  <span class="label"><t-icon name="time" /> End Time</span>
                  <span class="value">{{ formatTime(record.endTime) }}</span>
                </div>
                <div class="info-row">
                  <span class="label"><t-icon name="money-circle" /> Cost</span>
                  <span class="value price">￡{{ record.totalCost }}</span>
                </div>
              </div>
              <div class="action-buttons">
                <t-button variant="outline" class="action-button extend-button" @click="handleExtend(record)">
                  Extend
                </t-button>
                <t-button variant="outline" class="action-button feedback-button" @click="handleFeedback(record)">
                  Feedback
                </t-button>
              </div>
            </div>
          </template>
        </div>
      </t-tab-panel>

      <t-tab-panel value="waiting" label="Waiting">
        <div class="records-list">
          <template v-if="loading">
            <div class="loading-container">
              <t-loading size="medium" text="Loading records..." />
            </div>
          </template>
          <template v-else-if="waitingRecords.length === 0">
            <div class="empty-state">
              <t-empty description="No waiting rentals" />
              <t-button theme="primary" size="medium" shape="round" @click="fetchRecords">
                <template #icon><t-icon name="refresh" /></template>
                Refresh
              </t-button>
            </div>
          </template>
          <template v-else>
            <div v-for="record in waitingRecords" :key="record.id" class="record-card">
              <div class="header">
                <span class="scooter-id">
                  <t-icon name="scooter" size="20px" />
                  Scooter #{{ record.scooterId }}
                </span>
                <t-tag theme="danger" variant="light-outline" class="status-tag">Waiting</t-tag>
              </div>
              <div class="details">
                <div class="info-row">
                  <span class="label"><t-icon name="time" /> Start Time</span>
                  <span class="value">{{ formatTime(record.startTime) }}</span>
                </div>
                <div class="info-row">
                  <span class="label"><t-icon name="time" /> Duration</span>
                  <span class="value">{{ record.duration }} hours</span>
                </div>
                <div class="info-row">
                  <span class="label"><t-icon name="money-circle" /> Cost</span>
                  <span class="value price">￡{{ record.totalCost }}</span>
                </div>
              </div>
              <div class="action-buttons">
                <t-button variant="outline" theme="success" class="action-button" @click="handleExtend(record)">
                  Extend
                </t-button>
                <t-button variant="outline" theme="danger" class="action-button" @click="handleCancel(record)">
                  Cancel
                </t-button>
              </div>
            </div>
          </template>
        </div>
      </t-tab-panel>

      <t-tab-panel value="history" label="History">
        <div class="records-list">
          <template v-if="loading">
            <div class="loading-container">
              <t-loading size="medium" text="Loading records..." />
            </div>
          </template>
          <template v-else-if="historyRecords.length === 0">
            <div class="empty-state">
              <t-empty description="No rental history" />
              <t-button theme="primary" size="medium" shape="round" @click="fetchRecords">
                <template #icon><t-icon name="refresh" /></template>
                Refresh
              </t-button>
            </div>
          </template>
          <template v-else>
            <div v-for="record in historyRecords" :key="record.id" class="record-card history-card">
              <div class="header">
                <span class="scooter-id">
                  <t-icon name="scooter" size="20px" />
                  Scooter #{{ record.scooterId }}
                </span>
                <t-tag :theme="record.status === 'completed' ? 'success' : 'default'" variant="light-outline"
                  class="status-tag">
                  {{ record.status === 'completed' ? 'Completed' : 'Cancelled' }}
                </t-tag>
              </div>
              <div class="details">
                <div class="info-row">
                  <span class="label"><t-icon name="time" /> Start Time</span>
                  <span class="value">{{ formatTime(record.startTime) }}</span>
                </div>
                <div class="info-row">
                  <span class="label"><t-icon name="time" /> End Time</span>
                  <span class="value">{{ formatTime(record.endTime) }}</span>
                </div>
                <div class="info-row">
                  <span class="label"><t-icon name="money-circle" /> Cost</span>
                  <span class="value price">￡{{ record.totalCost }}</span>
                </div>
              </div>
            </div>
          </template>
        </div>
      </t-tab-panel>
    </t-tabs>

    <!-- 支付弹窗组件 -->
    <t-dialog v-model:visible="showPaymentDialog" :title="paymentTitle" :content="paymentContent" confirm-btn="Confirm"
      cancel-btn="Cancel" @confirm="processPayment" @cancel="cancelPayment">
      <div class="payment-form">
        <div v-if="discountRate > 0" class="discount-info">
          <t-tag theme="success">{{ discountRate < 1 ? `Discount: ${discountRate * 10} Fold` : `No Discount` }}</t-tag>
              <div class="price-comparison">
                <span class="original-price">￡{{ currentPaymentAmount }}</span>
                <span class="discounted-price">￡{{ (currentPaymentAmount * discountRate).toFixed(2) }}</span>
              </div>
        </div>
        <t-input v-if="!savedCard" label="Card Number" placeholder="Enter your card number" />
        <t-input v-if="!savedCard" label="Expiry Date" placeholder="MM/YY" />
        <t-input v-if="!savedCard" label="CVV" placeholder="CVV" />
        <div v-else class="saved-card">
          <span>Using saved card ending in **** {{ savedCard.bankCardId.slice(-4) }}</span>
        </div>
      </div>
    </t-dialog>

    <!-- 取消租赁确认弹窗 -->
    <t-dialog v-model:visible="cancelDialog.visible" title="Confirm Cancellation"
      :content="`Are you sure you want to cancel the reservation for Scooter #${cancelDialog.scooterId}?`"
      cancel-btn="No, Keep It" confirm-btn="Yes, Cancel" @confirm="confirmCancelRental"
      @cancel="cancelDialog.visible = false" />

    <!-- 添加扩展租赁组件 -->
    <extend-rental ref="extendRentalRef" :rental-id="currentRecord?.id" :scooter-id="currentRecord?.scooterId"
      :end-time="currentRecord?.endTime"></extend-rental>

    <t-back-top visibilityHeight="200" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, inject } from 'vue'
import { getRentalRecords, cancelRental, extendRental } from '@/service/rent'
import { canOfferDiscount } from '@/service/user'
import { useUserStore } from '@/store/user'
import { useRouter } from 'vue-router'
import { getPriceForHours } from '@/service/pricing'
import { LoadingPlugin, MessagePlugin } from 'tdesign-mobile-vue'
import ExtendRental from './components/ExtendRental.vue'

const $dialog = inject('$dialog')
const router = useRouter()
const userStore = useUserStore()
const extendRentalRef = ref(null)

const activeTab = ref('current')
const records = ref([])
const loading = ref(false)

// 支付相关
const showPaymentDialog = ref(false)
const currentPaymentAmount = ref(0)
const discountRate = ref(1)
const savedCard = ref(null)
const paymentPurpose = ref('') // 'extend' 或其他
const currentRecord = ref(null) // 当前操作的记录

// 取消租赁确认对话框
const cancelDialog = ref({
  visible: false,
  rentalId: null,
  scooterId: null
})

// 根据状态分类记录
const activeRecords = computed(() => records.value.filter((r) => r.status === 'active'))
const waitingRecords = computed(() => records.value.filter((r) => r.status === 'waiting'))
const historyRecords = computed(() =>
  records.value.filter((r) => ['completed', 'cancelled'].includes(r.status)),
)

const paymentTitle = computed(() => {
  return paymentPurpose.value === 'extend' ? 'Extend Payment' : 'Payment'
})

const paymentContent = computed(() => {
  if (discountRate.value > 0 && discountRate.value < 1) {
    const discountedPrice = (currentPaymentAmount.value * discountRate.value).toFixed(2)
    return `Confirm payment: ￡${discountedPrice} (优惠折扣: ${discountRate.value * 10}折)`
  }
  return `Confirm payment: ￡${currentPaymentAmount.value}`
})

const formatTime = (timestamp) => {
  if (!timestamp) return 'N/A'
  return new Date(timestamp).toLocaleString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    hour12: true,
  })
}

// 获取租赁记录
const fetchRecords = async (force = true) => {
  if (loading.value && !force) return

  loading.value = true
  try {
    const res = await getRentalRecords()
    records.value = res.data
  } catch (error) {
    console.error('Failed to load records:', error)
    import('tdesign-mobile-vue').then(({ DialogPlugin }) => {
      DialogPlugin.confirm({
        title: 'Connection Error',
        content: 'Failed to load records. Would you like to try again?',
        confirmBtn: 'Retry',
        onConfirm: () => {
          fetchRecords()
        },
      })
    })
  } finally {
    loading.value = false
  }
}

// 处理取消预订
const handleCancel = (record) => {
  cancelDialog.value.rentalId = record.id
  cancelDialog.value.scooterId = record.scooterId
  cancelDialog.value.visible = true
}

// 确认取消租赁
const confirmCancelRental = async () => {
  LoadingPlugin(true)
  try {
    await cancelRental(cancelDialog.value.rentalId)
    MessagePlugin.success('Cancelled successfully')
    fetchRecords() // 重新加载记录
  } catch (error) {
    MessagePlugin.error('Failed to cancel')
    console.error('Error cancelling rental:', error)
  } finally {
    LoadingPlugin(false)
    cancelDialog.value.visible = false
  }
}

// 处理延长租赁
const handleExtend = (record) => {
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

  currentRecord.value = record

  // 确保子组件方法存在
  if (extendRentalRef.value && typeof extendRentalRef.value.show === 'function') {
    extendRentalRef.value.show()
  } else {
    console.error('ExtendRental show method not available')
    MessagePlugin.error('Failed to open extend options')
  }
}

// 处理反馈
const handleFeedback = (record) => {
  router.push({
    path: '/feedback',
    query: { rentalId: record.id, scooterId: record.scooterId }
  })
}

// 处理支付流程
const processPayment = async () => {
  LoadingPlugin(true)
  try {
    if (paymentPurpose.value === 'extend') {
      // 计算最终价格（应用优惠）
      const finalPrice = discountRate.value > 0 && discountRate.value < 1
        ? currentPaymentAmount.value * discountRate.value
        : currentPaymentAmount.value

      await extendRental(
        currentRecord.value.id,
        finalPrice.toFixed(2)
      )

      MessagePlugin.success('Rental extended successfully')

      // 刷新数据
      fetchRecords()
    }

    showPaymentDialog.value = false
  } catch (error) {
    console.error('Payment failed', error)
    $dialog.alert({
      title: 'Error',
      content: 'Payment failed. Please try again later.',
    })
  } finally {
    LoadingPlugin(false)
  }
}

// 添加选项卡切换处理函数
const handleTabChange = (value) => {
  activeTab.value = value
  fetchRecords()
}

onMounted(() => {
  fetchRecords()
})
</script>

<style lang="scss" scoped>
.records-container {
  min-height: 100vh;
  background-color: #fff;
  padding: 16px;
  margin: 0 auto;
  width: 100%;
  max-width: 540px;

  @media (max-width: 768px) {
    padding: 12px 6px;
  }
}

.custom-tabs {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: none;
  margin-bottom: 20px;

  :deep(.t-tabs__nav) {
    background-color: #f8f8fa;
    padding: 6px;
    border-radius: 8px;

    .t-tabs__nav-item {
      border-radius: 6px;
      padding: 10px 16px;

      &.t-is-active {
        background-color: #fff;
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
      }
    }
  }

  :deep(.t-tabs__content) {
    padding: 12px 0;
  }
}

.loading-container {
  display: flex;
  justify-content: center;
  padding: 40px 0;
}

.records-list {
  padding: 10px 0;
}

.empty-state {
  text-align: center;
  padding: 40px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;

  @media (max-width: 768px) {
    padding: 30px 0;
  }
}

.record-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  border-left: 4px solid #0052d9;

  @media (max-width: 768px) {
    padding: 14px;
    margin-bottom: 14px;
  }

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  }

  &.history-card {
    border-left-color: #888;
  }

  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 14px;
    padding-bottom: 10px;
    border-bottom: 1px solid rgba(0, 0, 0, 0.05);

    .scooter-id {
      font-size: 16px;
      font-weight: 600;
      color: #333;
      display: flex;
      align-items: center;
      gap: 6px;

      @media (max-width: 768px) {
        font-size: 15px;
      }
    }

    .status-tag {
      font-weight: 500;
      padding: 2px 10px;

      @media (max-width: 768px) {
        padding: 2px 8px;
        font-size: 12px;
      }
    }
  }

  .details {
    margin-bottom: 14px;

    .info-row {
      display: flex;
      justify-content: space-between;
      margin-bottom: 10px;
      padding: 6px 0;

      @media (max-width: 768px) {
        padding: 4px 0;
        margin-bottom: 8px;
      }

      .label {
        color: #666;
        display: flex;
        align-items: center;
        gap: 6px;
        font-weight: 500;
        font-size: 14px;

        @media (max-width: 768px) {
          font-size: 13px;
        }
      }

      .value {
        color: #333;
        font-weight: 500;
        font-size: 14px;

        @media (max-width: 768px) {
          font-size: 13px;
        }

        &.price {
          color: #d14f19;
          font-weight: 600;
          font-size: 15px;

          @media (max-width: 768px) {
            font-size: 14px;
          }
        }
      }
    }
  }

  .action-buttons {
    display: flex;
    gap: 10px;
    margin-top: 14px;
  }

  .extend-button {
    background-color: #6db9e2;
    color: #fff;
  }

  .feedback-button {
    background-color: #ffed7b;
    color: #000;
  }

  .action-button {
    flex: 1;
    margin-top: 6px;
    height: 40px;
    font-weight: 600;
    border-radius: 8px;
    transition: all 0.2s ease;

    @media (max-width: 768px) {
      height: 38px;
      font-size: 13px;
    }

    &:hover {
      transform: translateY(-1px);
    }
  }
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

.discount-info {
  margin-bottom: 15px;
  padding: 10px;
  background-color: #f0fff0;
  border-radius: 6px;
  text-align: center;
}

.price-comparison {
  margin-top: 8px;
  font-size: 16px;
}

.original-price {
  text-decoration: line-through;
  color: #999;
  margin-right: 10px;
}

.discounted-price {
  font-weight: bold;
  color: #f60;
  font-size: 18px;
}
</style>
