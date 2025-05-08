<template>
  <div class="register-page">
    <t-navbar title="Register Account">
      <template #left>
        <div class="navbar-left-btn" @click="goToHome">
          <t-icon name="chevron-left" size="24px" />
        </div>
      </template>
    </t-navbar>

    <div style="padding: 10px;">
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
            <t-input v-model="formData.email" placeholder="Enter email address" clearable>
              <template #prefix-icon>
                <t-icon name="mail" />
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
            <t-input v-model="formData.confirmPassword" type="password" placeholder="Confirm your password" clearable>
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
        <t-button theme="primary" block size="large" @click="handleRegister">Register</t-button>
        <div style="text-align: center; margin-top: 24px;">
          <span style="color: #0052d9; cursor: pointer;" @click="goToLogin">Already have an account? Login now</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { register, getPicCaptcha } from '@/service/user'
import { encryptPassword } from '@/utils/sm2'
import { Toast } from 'tdesign-mobile-vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const captchaImage = ref('')
const validCodeReqNo = ref('')

const formData = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  validCode: '',
})

const rules = {
  username: [
    { required: true, message: 'Please enter username' },
    { min: 3, message: 'Username must be at least 3 characters' },
  ],
  email: [
    { required: true, message: 'Please enter email address' },
    {
      pattern: /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/,
      message: 'Please enter a valid email address',
    },
  ],
  password: [
    { required: true, message: 'Please enter password' },
    { min: 6, message: 'Password must be at least 6 characters' },
  ],
  confirmPassword: [
    { required: true, message: 'Please confirm your password' },
    {
      validator: (val: string) => val === formData.password,
      message: 'Passwords do not match',
    },
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

  // 验证邮箱
  if (!formData.email) {
    Toast({ message: 'Please enter email address', theme: 'error' });
    return false;
  }
  const emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
  if (!emailPattern.test(formData.email)) {
    Toast({ message: 'Please enter a valid email address', theme: 'error' });
    return false;
  }

  // 验证密码
  if (!formData.password) {
    Toast({ message: 'Please enter password', theme: 'error' });
    return false;
  }
  if (formData.password.length < 6) {
    Toast({ message: 'Password must be at least 6 characters', theme: 'error' });
    return false;
  }

  // 验证确认密码
  if (!formData.confirmPassword) {
    Toast({ message: 'Please confirm your password', theme: 'error' });
    return false;
  }
  if (formData.password !== formData.confirmPassword) {
    Toast({ message: 'Passwords do not match', theme: 'error' });
    return false;
  }

  // 验证码
  if (!formData.validCode) {
    Toast({ message: 'Please enter verification code', theme: 'error' });
    return false;
  }

  return true;
}

// 处理注册
const handleRegister = async () => {
  if (!validateForm()) return;

  try {
    const encryptedPassword = encryptPassword(formData.password)

    const res = await register(
      formData.username,
      encryptedPassword,
      formData.email,
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

    // 注册成功
    Toast({
      message: 'Registration successful',
      theme: 'success',
    })

    setTimeout(() => {
      goToLogin()
    }, 1500)
  } catch (error: any) {
    refreshCaptcha() // 刷新验证码

    // 显示具体的错误信息
    Toast({
      message: error.data?.msg || 'Registration failed, please try again',
      theme: 'error',
      duration: 2000,
    })

    // 如果是验证码错误，清空验证码输入
    if (error.data?.msg?.includes('验证码')) {
      formData.validCode = ''
    }

    // 如果是用户名已存在，清空用户名输入
    if (error.data?.msg?.includes('用户已存在')) {
      formData.username = ''
    }

    // 处理邮箱相关错误
    if (error.data?.msg?.includes('邮箱')) {
      formData.email = ''
    }
  }
}

const goToLogin = () => {
  router.push('/my/login')
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

onMounted(() => {
  refreshCaptcha()
})
</script>

<style lang="less" scoped>
.register-page {
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
