<template>
  <div class="feedback-page">
    <!-- 顶部标题栏 -->
    <div class="page-header">
      <t-icon name="chevron-left" size="24px" class="back-icon" @click="router.back()" />
      <h1 class="page-title">Feedback</h1>
    </div>

    <div class="content-container">
      <!-- 保留原有的issue type部分 -->
      <div class="form-section">
        <div class="section-title">Issue Type</div>
        <t-radio-group v-model="form.type" class="issue-options">
          <t-radio value="other">
            <div class="radio-content">
              <t-icon name="info-circle" /> Minor Issue
            </div>
          </t-radio>
          <t-radio value="damage">
            <div class="radio-content">
              <t-icon name="tools" /> Damage
            </div>
          </t-radio>
          <t-radio value="malfunction">
            <div class="radio-content">
              <t-icon name="error-circle" /> Serious Malfunction
            </div>
          </t-radio>
        </t-radio-group>
      </div>

      <!-- 美化后的Description部分 -->
      <div class="form-section">
        <div class="section-title">Description</div>
        <div class="description-input">
          <t-textarea v-model="form.description" placeholder="Please describe the issue in detail" :maxlength="200"
            indicator />
        </div>
      </div>
      
      <!-- 底部提交按钮 (移到内容区域内) -->
      <div class="button-wrapper">
        <t-button theme="primary" block @click="handleSubmit" :loading="submitting">
          Submit Feedback
        </t-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { feedback } from '@/service/user'
import { useRouter } from 'vue-router'
import { MessagePlugin } from 'tdesign-mobile-vue'

const router = useRouter()
const scooterId = ref('')
const rentalId = ref('')
const submitting = ref(false)

const form = reactive({
  type: 'other',
  description: '',
})

// 根据类型获取评分
const getRatingByType = (type) => {
  const ratings = {
    other: 1, // 轻微问题
    damage: 2, // 损坏
    malfunction: 3, // 严重故障
  }
  return ratings[type] || 1
}

const handleSubmit = async () => {
  if (!form.description.trim()) {
    MessagePlugin.warning('Please describe the issue')
    return
  }

  submitting.value = true
  try {
    await feedback(rentalId.value, getRatingByType(form.type), form.description)

    MessagePlugin.success('Feedback submitted successfully')

    setTimeout(() => {
      router.back()
    }, 1500)
  } catch (error) {
    MessagePlugin.error('Failed to submit feedback')
    console.error('Error submitting feedback:', error)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  const route = router.currentRoute.value

  if (route.query.scooterId) {
    scooterId.value = route.query.scooterId
  }
  if (route.query.rentalId) {
    rentalId.value = route.query.rentalId
  }
})
</script>

<style lang="scss" scoped>
.feedback-page {
  min-height: 100vh;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
}

.page-header {
  background: #fff;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  position: relative;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

  .back-icon {
    position: absolute;
    left: 16px;
    cursor: pointer;
  }

  .page-title {
    flex: 1;
    text-align: center;
    font-size: 18px;
    font-weight: 600;
    margin: 0;
  }
}

.content-container {
  flex: 1;
  padding: 20px 16px;
}

.form-section {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);

  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: #333;
    margin-bottom: 16px;
  }

  .issue-options {
    display: flex;
    flex-direction: column;
    gap: 12px;

    :deep(.t-radio) {
      padding: 12px;
      border: 1px solid rgba(0, 0, 0, 0.08);
      border-radius: 10px;
      transition: all 0.2s ease;

      &:hover {
        border-color: #0052d9;
        background-color: rgba(0, 82, 217, 0.03);
      }

      &.t-is-checked {
        border-color: #0052d9;
        background-color: rgba(0, 82, 217, 0.05);

        .t-radio__content {
          color: #0052d9;
          font-weight: 500;
        }
      }
    }

    .radio-content {
      display: flex;
      align-items: center;
      gap: 8px;
    }
  }

  .description-input {
    :deep(.t-textarea) {
      border-radius: 12px;
      border: 1px solid #e6e6e6;
      background: #fff;
      min-height: 120px;
      
      &:focus {
        border-color: #0052d9;
        box-shadow: 0 0 0 2px rgba(0, 82, 217, 0.1);
      }
      
      .t-textarea__inner {
        padding: 12px;
        font-size: 15px;
      }
    }
  }
}

.button-wrapper {
  margin-top: 24px;
  
  .t-button {
    height: 48px;
    border-radius: 8px;
    font-weight: bold;
    font-size: 16px;
  }
}
</style>

