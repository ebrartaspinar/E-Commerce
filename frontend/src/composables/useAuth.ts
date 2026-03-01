import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import type { Router } from 'vue-router'
import type { LoginRequest, RegisterRequest } from '@/types'

export function useAuth() {
  const authStore = useAuthStore()

  const user = computed(() => authStore.user)
  const isAuthenticated = computed(() => authStore.isAuthenticated)
  const isSeller = computed(() => authStore.isSeller)
  const isBuyer = computed(() => authStore.isBuyer)

  async function login(req: LoginRequest) {
    return authStore.login(req)
  }

  async function logout() {
    return authStore.logout()
  }

  async function register(req: RegisterRequest) {
    return authStore.register(req)
  }

  function requireAuth(router: Router) {
    if (!authStore.isAuthenticated) {
      router.push('/login')
      return false
    }
    return true
  }

  return {
    user,
    isAuthenticated,
    isSeller,
    isBuyer,
    login,
    logout,
    register,
    requireAuth,
  }
}
