import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { authService } from '@/api/authService'
import { userService } from '@/api/userService'
import type { UserResponse, LoginRequest, RegisterRequest } from '@/types'

export const useAuthStore = defineStore('auth', () => {
  // State
  const user = ref<UserResponse | null>(null)
  const accessToken = ref<string | null>(null)
  const refreshToken = ref<string | null>(null)
  const loading = ref(false)

  // Getters
  const isAuthenticated = computed(() => !!accessToken.value)
  const isSeller = computed(() => user.value?.role === 'SELLER')
  const isBuyer = computed(() => user.value?.role === 'BUYER')
  const fullName = computed(() =>
    user.value ? `${user.value.firstName} ${user.value.lastName}` : ''
  )

  // Actions
  async function login(req: LoginRequest) {
    loading.value = true
    try {
      const response = await authService.login(req)
      const data = response.data

      accessToken.value = data.accessToken
      refreshToken.value = data.refreshToken
      user.value = data.user

      localStorage.setItem('accessToken', data.accessToken)
      localStorage.setItem('refreshToken', data.refreshToken)
      localStorage.setItem('user', JSON.stringify(data.user))

      return data
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  async function register(req: RegisterRequest) {
    loading.value = true
    try {
      const response = await authService.register(req)
      const data = response.data

      accessToken.value = data.accessToken
      refreshToken.value = data.refreshToken
      user.value = data.user

      localStorage.setItem('accessToken', data.accessToken)
      localStorage.setItem('refreshToken', data.refreshToken)
      localStorage.setItem('user', JSON.stringify(data.user))

      return data
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  async function logout() {
    try {
      await authService.logout()
    } catch {
      // Ignore logout API errors - still clear local state
    } finally {
      accessToken.value = null
      refreshToken.value = null
      user.value = null

      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
      localStorage.removeItem('user')
    }
  }

  function loadFromStorage() {
    const storedToken = localStorage.getItem('accessToken')
    const storedRefresh = localStorage.getItem('refreshToken')
    const storedUser = localStorage.getItem('user')

    if (storedToken) {
      accessToken.value = storedToken
    }
    if (storedRefresh) {
      refreshToken.value = storedRefresh
    }
    if (storedUser) {
      try {
        user.value = JSON.parse(storedUser) as UserResponse
      } catch {
        user.value = null
      }
    }
  }

  async function refreshUserProfile() {
    loading.value = true
    try {
      const response = await userService.getProfile()
      user.value = response.data
      localStorage.setItem('user', JSON.stringify(response.data))
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  return {
    // State
    user,
    accessToken,
    refreshToken,
    loading,
    // Getters
    isAuthenticated,
    isSeller,
    isBuyer,
    fullName,
    // Actions
    login,
    register,
    logout,
    loadFromStorage,
    refreshUserProfile,
  }
})
