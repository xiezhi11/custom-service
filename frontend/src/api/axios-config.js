import axios from 'axios'
import { ElMessage } from 'element-plus'

const instance = axios.create({
  baseURL: '/api',
  timeout: 10000
})

instance.interceptors.response.use(
  response => response,
  error => {
    if (error.response) {
      const data = error.response.data
      if (data && data.message) {
        ElMessage.error(data.message)
      } else if (typeof data === 'object') {
        const firstError = Object.values(data)[0]
        if (firstError) {
          ElMessage.error(firstError)
        }
      } else {
        ElMessage.error('请求失败，请稍后重试')
      }
    } else {
      ElMessage.error('网络错误，请检查网络连接')
    }
    return Promise.reject(error)
  }
)

export default instance
