<template>
  <div class="login-page">
    <t-navbar title="Login">
      <template #left>
        <div class="navbar-left-btn" @click="goToHome">
          <t-icon name="chevron-left" size="24px" />
        </div>
      </template>
    </t-navbar>

    <div style="padding: 20px;">
      <div class="logo-wrapper">
        <t-image src="/scooter2.png" :style="{ width: '72px', height: '72px' }" />
      </div>

      <t-cell-group>
        <t-cell>
          <template #title>
            <t-input v-model="formData.username" placeholder="Enter username" clearable>
              <template #prefix-icon>
                <t-icon name="user" />
              </template>
            </t-input>
          </template>
        </t-cell>

        <t-cell>
          <template #title>
            <t-input v-model="formData.password" type="password" placeholder="Enter password" clearable>
              <template #prefix-icon>
                <t-icon name="user-password" />
              </template>
            </t-input>
          </template>
        </t-cell>

        <t-cell>
          <template #title>
            <div class="captcha-row">
              <t-input v-model="formData.validCode" placeholder="Enter verification code">
                <template #prefix-icon>
                  <t-icon name="verify" />
                </template>
              </t-input>
              <img :src="captchaImage" @click="refreshCaptcha"
                style="width: 120px; height: 40px; margin-left: 12px; border: 1px solid #eee;" />
            </div>
          </template>
        </t-cell>
      </t-cell-group>

      <div style="margin-top: 24px;">
        <t-button theme="primary" block size="large" @click="handleLogin">Login</t-button>
        <div style="text-align: center; margin-top: 24px;">
          <span style="color: #0052d9; cursor: pointer;" @click="goToRegister">New user? Register now</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/store'
import { login, getPicCaptcha } from '@/service/user'
import { encryptPassword } from '@/utils/sm2'
import { Toast } from 'tdesign-mobile-vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const userStore = useUserStore()
const captchaImage = ref('')
const validCodeReqNo = ref('')

const formData = reactive({
  username: '',
  password: '',
  validCode: '',
})

const rules = {
  username: [
    { required: true, message: 'Please enter username' },
    { min: 3, message: 'Username must be at least 3 characters' },
  ],
  password: [
    { required: true, message: 'Please enter password' },
    { min: 3, message: 'Password must be at least 3 characters' },
  ],
  validCode: [{ required: true, message: 'Please enter verification code' }],
}

// 获取验证码
const refreshCaptcha = async () => {
  try {
    const res = await getPicCaptcha()
    captchaImage.value = res.data.validCodeBase64
    validCodeReqNo.value = res.data.validCodeReqNo
  } catch (error) {
    Toast({
      message: 'Failed to get verification code',
      theme: 'error',
    })
  }
}

// 检查登录状态
const checkLoginStatus = () => {
  if (userStore.isLogined) {
    Toast({
      message: 'Already logged in',
      theme: 'info',
    })
    setTimeout(() => {
      router.push('/home')
    }, 1000)
  }
}

// 自定义表单验证
const validateForm = () => {
  // 验证用户名
  if (!formData.username) {
    Toast({ message: 'Please enter username', theme: 'error' });
    return false;
  }
  if (formData.username.length < 3) {
    Toast({ message: 'Username must be at least 3 characters', theme: 'error' });
    return false;
  }

  // 验证密码
  if (!formData.password) {
    Toast({ message: 'Please enter password', theme: 'error' });
    return false;
  }
  if (formData.password.length < 3) {
    Toast({ message: 'Password must be at least 3 characters', theme: 'error' });
    return false;
  }

  // 验证码
  if (!formData.validCode) {
    Toast({ message: 'Please enter verification code', theme: 'error' });
    return false;
  }

  return true;
}

// 处理登录
const handleLogin = async () => {
  if (!validateForm()) return;

  try {
    // 对密码进行 SM2 加密
    const encryptedPassword = encryptPassword(formData.password)

    const res = await login(
      formData.username,
      encryptedPassword, // 使用加密后的密码
      'PC',
      formData.validCode,
      validCodeReqNo.value,
    )
    if (res.code !== 200) {
      Toast({
        message: res.msg,
        theme: 'error',
      })
      return
    }
    // 存储用户信息和token到store
    userStore.setUserInfo({
      token: res.data,
    })

    // 将token保存到localStorage
    localStorage.setItem('token', res.data)

    Toast({
      message: 'Login successful',
      theme: 'success',
    })

    // 触发登录成功事件
    document.dispatchEvent(new Event('loginSuccess'))

    // 添加底部导航栏状态更新
    setTimeout(() => {
      router.push('/home')
      
      // 确保底部导航栏更新为Home选项卡
      setTimeout(() => {
        if (document.querySelector('.t-tab-bar')) {
          document.dispatchEvent(new CustomEvent('updateTabBar', { detail: 'Home' }));
        }
      }, 100);
    }, 1500)
  } catch (error) {
    refreshCaptcha()
    Toast({
      message: 'Login failed, please try again',
      theme: 'error',
    })
  }
}

onMounted(() => {
  checkLoginStatus()
  refreshCaptcha()
})

const goToRegister = () => {
  router.push('/my/register')
}

const goToHome = () => {
  console.log('返回首页');
  router.push('/home');
  
  setTimeout(() => {
    if (document.querySelector('.t-tab-bar')) {
      document.dispatchEvent(new CustomEvent('updateTabBar', { detail: 'Home' }));
    }
  }, 100);
}
</script>

<style lang="less" scoped>
.login-page {
  background-color: #f5f7fa;
  min-height: 100vh;

  .logo-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 40px;
    margin-bottom: 10px;
  }

  .captcha-row {
    display: flex;
    align-items: center;
    width: 100%;
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
}
</style>
