import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '../services/api'
import router from '../router'

export const useAuthStore = defineStore('auth', () => {
  // State
  const token = ref<string | null>(null)
  const email = ref<string>('')
  const firstName = ref<string>('')
  const role = ref<string>('')

  // Getters
  const isAuthenticated = computed(() => !!token.value)

  // Actions
  function loadFromStorage() {
    const savedToken = localStorage.getItem('token')
    const savedUser = localStorage.getItem('user')
    if (savedToken && savedUser) {
      token.value = savedToken
      const user = JSON.parse(savedUser)
      email.value = user.email
      firstName.value = user.firstName
      role.value = user.role
    }
  }

  async function login(loginEmail: string, password: string) {
    const response = await authApi.login({ email: loginEmail, password })
    const data = response.data
    token.value = data.token
    email.value = data.email
    firstName.value = data.firstName
    role.value = data.role
    localStorage.setItem('token', data.token)
    localStorage.setItem('user', JSON.stringify({
      email: data.email,
      firstName: data.firstName,
      role: data.role
    }))
  }

  async function register(registerData: { email: string; password: string; firstName: string; lastName: string }) {
    const response = await authApi.register(registerData)
    const data = response.data
    token.value = data.token
    email.value = data.email
    firstName.value = data.firstName
    role.value = data.role
    localStorage.setItem('token', data.token)
    localStorage.setItem('user', JSON.stringify({
      email: data.email,
      firstName: data.firstName,
      role: data.role
    }))
  }

  function logout() {
    token.value = null
    email.value = ''
    firstName.value = ''
    role.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    router.push('/')
  }

  return {
    token,
    email,
    firstName,
    role,
    isAuthenticated,
    loadFromStorage,
    login,
    register,
    logout
  }
})
