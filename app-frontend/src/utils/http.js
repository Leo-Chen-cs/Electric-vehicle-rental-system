import { useUserStore } from '@/store'
import axios from 'axios'

// 请求基地址
const baseURL = import.meta.env.VITE_SERVER_BASEURL

// 创建axios实例
const service = axios.create({
  baseURL: baseURL,
  timeout: 10000
})

// 添加请求拦截器
service.interceptors.request.use(
  config => {
    // 添加token请求头
    const userStore = useUserStore()
    const token = userStore.userInfo?.token
    if (token) {
      config.headers.token = token
    }

    return config
  },
  error => {
    console.error('请求拦截器错误:', error)
    return Promise.reject(error)
  }
)

// 添加响应拦截器
service.interceptors.response.use(
  response => {
    if (response.data.code !== 200) {
      if (response.data.code === 401 || response.data.code === 403) {
        // 401或403错误 -> 清理用户信息，跳转到登录页
        const userStore = useUserStore()
        userStore.clearUserInfo()
        window.location.href = '/my/login'
      }
      return Promise.reject(new Error(response.data.msg || 'Error'))
    }
    return response.data
  },
  error => {
    if (error.response) {

      if (error.response.data.code === 401 || error.response.data.code === 403) {
        // 401错误 -> 清理用户信息，跳转到登录页
        const userStore = useUserStore()
        userStore.clearUserInfo()
        window.location.href = '/my/login'
      } else {
        console.error('请求错误:', error.response.status)
      }
    } else {
      console.error('网络错误，请检查网络连接')
    }
    return Promise.reject(error)
  }
)


// 使用axios的http请求方法
export const http = service

// 文件上传封装
export const fileUpload = (options) => {
  const formData = new FormData()

  if (options.filePath && options.file) {
    formData.append('file', options.file)
  }

  if (options.formData) {
    Object.keys(options.formData).forEach(key => {
      formData.append(key, options.formData[key])
    })
  }

  return service({
    url: options.url,
    method: 'POST',
    headers: {
      'Content-Type': 'multipart/form-data',
      ...options.header
    },
    data: formData
  })
}

export { service }