<template>
  <div class="page-container">
    <div class="user-card">
      <div class="user-header">
        <div class="avatar">
          <t-avatar :image="userInfo.avatar || '/profile.png'"
            :name="userInfo.name?.charAt(0) || userInfo.account?.charAt(0) || 'U'" size="large" />
        </div>
        <div class="user-info">
          <div class="info-content">
            <div class="nickname">{{ userInfo.name || userInfo.account || 'Not Logged In' }}</div>
            <div class="email">{{ userInfo.email || 'Click to login' }}</div>
          </div>
        </div>
      </div>
      <div class="user-details">
        <div class="detail-item">
          <span class="label"><t-icon name="user-circle" /> Member Since</span>
          <span class="value">{{ formatDate(userInfo.createTime) }}</span>
        </div>
        <div class="detail-item">
          <span class="label"><t-icon name="time" /> Last Active</span>
          <span class="value">{{ formatDate(userInfo.latestLoginTime) }}</span>
        </div>
      </div>
    </div>

    <div class="menu-section">
      <h3 class="section-title">Account</h3>
      <div class="menu-group">
        <t-cell title="Waiting Feedback" arrow hover description="Check your feedback status"
          @click="navigateToFeedback">
          <template #leftIcon><t-icon name="chat" /></template>
        </t-cell>
        <t-cell title="Email" :note="userInfo.email || 'Not available'" hover>
          <template #leftIcon><t-icon name="mail" /></template>
        </t-cell>
      </div>
    </div>

    <div class="menu-section">
      <h3 class="section-title">Support</h3>
      <div class="menu-group">
        <t-cell title="Help Center" arrow hover @click="navigateToHelpCenter">
          <template #leftIcon><t-icon name="help-circle" /></template>
        </t-cell>
        <t-cell title="About Us" arrow hover @click="navigateToAboutUs">
          <template #leftIcon><t-icon name="info-circle" /></template>
        </t-cell>
      </div>
    </div>

    <div class="logout-section" v-if="userStore.isLogined">
      <t-button theme="danger" variant="outline" size="large" block @click="handleLogout" class="logout-button">
        <template #icon><t-icon name="logout" /></template>
        Log Out
      </t-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store'
import { getUserInfo } from '@/service/user'
import { inject } from 'vue'
import { MessagePlugin } from 'tdesign-mobile-vue'
const $dialog = inject('$dialog')
const router = useRouter()
const userStore = useUserStore()
const userInfo = ref({})

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })
}

// 获取用户详细信息
const fetchUserInfo = async () => {
  try {
    const { data } = await getUserInfo()
    userInfo.value = data
  } catch (error) {
    console.error('Failed to fetch user info:', error)
  }
}

const navigateToFeedback = () => {
  router.push('/my/feedback')
}

const navigateToHelpCenter = () => {
  router.push('/my/helpCenter')
}

const navigateToAboutUs = () => {
  router.push('/my/aboutUs')
}

const handleLogout = () => {
  $dialog.confirm({
    title: 'Confirm Logout',
    content: 'Are you sure you want to log out?',
    cancelBtn: 'Cancel',
    confirmBtn: 'Log Out',
    onConfirm: () => {
      userStore.clearUserInfo()
      MessagePlugin.success('Logged out successfully')
      checkLogin()
    },
  })
}

const checkLogin = () => {
  if (!userStore.isLogined) {
    router.push('/my/login')
    return false
  }
  return true
}

onMounted(() => {
  if (checkLogin()) {
    fetchUserInfo()
  }
})
</script>

<style lang="scss" scoped>
.page-container {
  max-height: 100vh;
  background-color: #fff;
  margin: 0 auto;
  width: 100%;
  max-width: 540px;

}

.section-title {
  font-size: 16px;
  color: #666;
  margin: 24px 0 12px;
  padding-left: 6px;

  @media (max-width: 768px) {
    margin: 20px 0 10px;
    font-size: 15px;
  }
}

.user-card {
  padding: 0;
  margin-bottom: 24px;
  background-color: #fff;
  border-radius: 16px;
  overflow: hidden;
  position: relative;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);

  @media (max-width: 768px) {
    margin-bottom: 20px;
  }

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 90px;
    background: linear-gradient(45deg, #0052d9, #00b7ff);
    z-index: 0;
  }

  .user-header {
    position: relative;
    z-index: 1;
    padding: 24px 20px;
    display: flex;
    align-items: center;



    .avatar {
      margin-right: 16px;

      :deep(.t-avatar) {
        border: 2px solid rgba(255, 255, 255, 0.8);
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
      }
    }
  }

  .user-info {
    display: flex;
    align-items: center;
    z-index: 1;

    .info-content {
      .nickname {
        font-size: 20px;
        font-weight: bold;
        color: #fff;
        margin-bottom: 4px;
        text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);

        @media (max-width: 768px) {
          font-size: 18px;
        }
      }

      .email {
        font-size: 14px;
        color: rgba(255, 255, 255, 0.9);

        @media (max-width: 768px) {
          font-size: 13px;
        }
      }
    }
  }

  .user-details {
    padding: 20px;
    position: relative;
    z-index: 1;
    background-color: #fff;

    @media (max-width: 768px) {
      padding: 16px;
    }

    .detail-item {
      display: flex;
      justify-content: space-between;
      margin-bottom: 14px;
      align-items: center;

      @media (max-width: 768px) {
        margin-bottom: 12px;
      }

      .label {
        color: #666;
        font-size: 14px;
        display: flex;
        align-items: center;
        gap: 6px;

        @media (max-width: 768px) {
          font-size: 13px;
        }

        :deep(.t-icon) {
          color: #0052d9;
        }
      }

      .value {
        color: #333;
        font-size: 14px;
        font-weight: 500;

        @media (max-width: 768px) {
          font-size: 13px;
        }
      }
    }
  }
}

.menu-section {
  margin-bottom: 20px;

  @media (max-width: 768px) {
    margin-bottom: 16px;
  }
}

.menu-group {
  background-color: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);

  :deep(.t-cell) {
    padding: 16px;

    @media (max-width: 768px) {
      padding: 14px;
    }

    .t-icon {
      color: #0052d9;
      font-size: 18px;
    }

    &:active {
      background-color: #f5f7fa;
    }
  }
}

.logout-section {
  margin-top: 36px;
  padding: 0 4px;

  @media (max-width: 768px) {
    margin-top: 30px;
  }

  .logout-button {
    height: 44px;
    font-weight: 500;
    border-radius: 10px;
    transition: all 0.2s ease;

    @media (max-width: 768px) {
      height: 40px;
    }

    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 3px 8px rgba(0, 0, 0, 0.12);
    }

    &:active {
      transform: translateY(0);
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    }
  }
}
</style>
