<template>
  <view class="records-list">
    <view v-if="loading" class="loading-state">
      <wd-loading type="circle" />
      <text>Loading records...</text>
    </view>
    <view v-else-if="records.length === 0" class="empty-state">
      <text>{{ emptyText }}</text>
    </view>
    <view v-else class="card-list">
      <view v-for="record in records" :key="record.id" class="card">
        <view class="card-header">
          <view class="left">
            <view class="scooter-id">#{{ record.scooterId }}</view>
            <wd-tag :type="getStatusType(record.status)" class="status-tag">
              {{ getStatusText(record.status) }}
            </wd-tag>
          </view>
        </view>

        <view class="card-body">
          <view class="info-grid">
            <view class="info-item">
              <text class="label">Start</text>
              <text class="value">{{ formatDateTime(record.startTime) }}</text>
            </view>
            <view class="info-item">
              <text class="label">End</text>
              <text class="value">{{ formatDateTime(record.endTime) }}</text>
            </view>
            <view class="info-item">
              <text class="label">Cost</text>
              <text class="value highlight">${{ record.totalCost }}</text>
            </view>
          </view>
        </view>

        <view v-if="record.status === 'active' || record.status === 'waiting'" class="card-footer">
          <wd-button v-if="record.status === 'active'" size="small" type="primary" @click="$emit('feedback', record)">
            Report Issue
          </wd-button>
          <wd-button v-if="record.status === 'active' || record.status === 'waiting'" size="small" type="warning"
            @click="$emit('extend', record)">
            Extend
          </wd-button>
          <wd-button v-if="record.status === 'waiting'" size="small" type="danger" @click="$emit('cancel', record)">
            Cancel Reservation
          </wd-button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { formatDateTime } from '@/utils/timeFormat'

const props = defineProps({
  records: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  status: { type: String, required: true },
  emptyText: { type: String, default: 'No records' },
})

const emit = defineEmits(['cancel', 'extend', 'refresh', 'feedback'])

const getStatusType = (status) => {
  const types = {
    active: 'success',
    waiting: 'warning',
    completed: 'info',
    cancelled: 'danger',
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    active: 'Active',
    waiting: 'Waiting',
    completed: 'Completed',
    cancelled: 'Cancelled',
  }
  return texts[status] || status
}
</script>

<style lang="scss" scoped>
.records-list {
  padding: 16px;
}

.card-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.card {
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: all 0.3s ease;

  &:active {
    transform: scale(0.98);
  }

  .card-header {
    padding: 16px;
    border-bottom: 1px solid #f0f0f0;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .left {
      display: flex;
      align-items: center;
      gap: 12px;

      .scooter-id {
        font-size: 18px;
        font-weight: 600;
        color: #333;
      }

      .status-tag {
        text-transform: uppercase;
        font-size: 12px;
      }
    }
  }

  .card-body {
    padding: 16px;

    .info-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 16px;

      .info-item {
        display: flex;
        flex-direction: column;
        gap: 4px;

        &:last-child {
          grid-column: 1 / -1;
        }

        .label {
          font-size: 12px;
          color: #666;
          text-transform: uppercase;
        }

        .value {
          font-size: 14px;
          color: #333;
          font-weight: 500;

          &.highlight {
            color: #f60;
            font-size: 18px;
            font-weight: 600;
          }
        }
      }
    }
  }

  .card-footer {
    padding: 12px 16px;
    border-top: 1px solid #f0f0f0;
    display: flex;
    justify-content: flex-end;
    gap: 8px;

    :deep(.wd-button) {
      font-size: 13px;
      padding: 6px 12px;
    }
  }
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 32px 0;
  color: #666;
}

.empty-state {
  text-align: center;
  padding: 32px 0;
  color: #999;
  font-size: 14px;
}

.right {
  display: flex;
  gap: 8px;
}
</style>
