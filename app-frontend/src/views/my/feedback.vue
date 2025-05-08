<template>
  <div class="feedback-list">
    <t-navbar title="Feedback History">
      <template #left>
        <div class="navbar-left-btn" @click="goToMy">
          <t-icon name="chevron-left" size="24px" />
        </div>
      </template>
    </t-navbar>

    <div class="feedback-content">
      <div v-if="loading" class="loading-state">
        <t-loading theme="circular" />
        <span>Loading feedback...</span>
      </div>
      <div v-else-if="feedbacks.length === 0" class="empty-state">
        <t-empty description="No feedback history" />
      </div>
      <div v-else class="feedback-cards">
        <div v-for="item in feedbacks" :key="item.id" class="feedback-card">
          <div class="header">
            <span class="scooter-id">Scooter #{{ item.scooterId }}</span>
            <span class="date">{{ formatDate(item.createdAt) }}</span>
          </div>
          <div class="content">
            <div class="rating">
              <span class="label">Severity:</span>
              <span :class="['value', getSeverityClass(item.rating)]">
                {{ getSeverityText(item.rating) }}
              </span>
            </div>
            <div class="comment">{{ item.comment }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getFeedbackList } from '@/service/feedback'
import { Toast } from 'tdesign-mobile-vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const feedbacks = ref([])

const formatDate = (dateStr) => {
  return new Date(dateStr).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })
}

const getSeverityText = (rating) => {
  const texts = {
    1: 'Minor Issue',
    2: 'Moderate Issue',
    3: 'Serious Issue',
  }
  return texts[rating] || 'Unknown'
}

const getSeverityClass = (rating) => {
  const classes = {
    1: 'minor',
    2: 'moderate',
    3: 'serious',
  }
  return classes[rating] || ''
}

const fetchFeedbackList = async () => {
  loading.value = true
  try {
    const { data } = await getFeedbackList()
    feedbacks.value = data.records || []
  } catch (error) {
    Toast.show({
      message: 'Failed to load feedback',
      theme: 'error',
    })
  } finally {
    loading.value = false
  }
}

const goToMy = () => {
  router.push('/my')
}

onMounted(() => {
  console.log("test")
  fetchFeedbackList()
})
</script>

<style lang="scss">
.feedback-list {
  min-height: 100vh;
  background: #f5f5f5;
}

.feedback-content {
  padding: 16px;
  padding-top: 76px; /* Reserve space for the navbar */
}

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

.feedback-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;

    .scooter-id {
      font-weight: bold;
      color: #333;
    }

    .date {
      font-size: 14px;
      color: #999;
    }
  }

  .content {
    .rating {
      margin-bottom: 8px;

      .label {
        color: #666;
        margin-right: 8px;
      }

      .value {
        &.minor {
          color: var(--td-success-color);
        }

        &.moderate {
          color: var(--td-warning-color);
        }

        &.serious {
          color: var(--td-error-color);
        }
      }
    }

    .comment {
      color: #333;
      line-height: 1.5;
    }
  }
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 40px 0;
  color: #999;
}
</style>
