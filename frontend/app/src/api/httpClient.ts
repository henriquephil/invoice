import axios from 'axios'
import { useAccountStore } from '../store/accountStore'
import { logout } from './authClient'

const apiClient = axios.create({
  baseURL: '/api',
})

apiClient.interceptors.request.use((config) => {
  const activeAccount = useAccountStore.getState().activeAccount
  if (activeAccount) {
    config.headers['X-Account-Id'] = activeAccount.id
  }

  return config
})

apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response.status === 401) {
      await logout()
      window.location.href = '/login'
    }
    return Promise.reject(error)
  },
)


export default apiClient