import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

const initState = { token: '', account: '' }

export const useUserStore = defineStore(
  'user',
  () => {
    // 初始化时从localStorage获取token和account
    const initialToken = localStorage.getItem('token') || ''
    const initialAccount = localStorage.getItem('account') || ''
    const userInfo = ref({ token: initialToken, account: initialAccount })

    const setUserInfo = (val) => {
      userInfo.value = val
      // 确保token和account保存到localStorage
      if (val.token) {
        localStorage.setItem('token', val.token)
      }
      if (val.account) {
        localStorage.setItem('account', val.account)
      }
    }

    const clearUserInfo = () => {
      userInfo.value = { ...initState }
      // 清除localStorage中的token和account
      localStorage.removeItem('token')
      localStorage.removeItem('account')
    }

    const reset = () => {
      userInfo.value = { ...initState }
      // 清除localStorage中的token和account
      localStorage.removeItem('token')
      localStorage.removeItem('account')
    }

    const isLogined = computed(() => !!userInfo.value.token)

    return {
      userInfo,
      setUserInfo,
      clearUserInfo,
      isLogined,
      reset,
    }
  },
  {
    persist: true,
  },
)
