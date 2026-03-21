<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useNotificationStore } from '../stores/notification'
import { useCartStore } from '../stores/cart'

const router = useRouter()
const authStore = useAuthStore()
const notification = useNotificationStore()
const cartStore = useCartStore()

const email = ref('')
const password = ref('')
const loading = ref(false)

async function handleLogin() {
  if (!email.value || !password.value) {
    notification.showError('Please fill in all fields')
    return
  }

  loading.value = true
  try {
    await authStore.login(email.value, password.value)
    notification.showSuccess('Successfully logged in!')
    await cartStore.fetchCart()
    router.push('/')
  } catch (error: any) {
    notification.showError(error.response?.data?.message || 'Login failed. Check your credentials.')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-[calc(100vh-200px)] flex items-center justify-center px-4">
    <div class="bg-[#f4e4c1] p-8 pixel-border w-full max-w-md">
      <h1 class="text-lg font-bold text-[#2d1b00] text-center mb-6" style="font-family: 'Press Start 2P', cursive;">Login to StarDrop Shop</h1>

      <form @submit.prevent="handleLogin" class="space-y-4">
        <div>
          <label class="block text-xs font-medium text-[#5c3a1e] mb-1" style="font-family: 'Press Start 2P', cursive; font-size: 8px;">Email</label>
          <input
            v-model="email"
            type="email"
            placeholder="you@email.com"
            class="w-full px-4 py-2 border-2 border-[#5c3a1e] bg-white text-[#2d1b00] focus:outline-none focus:ring-2 focus:ring-yellow-600 text-xs"
            style="font-family: 'Press Start 2P', cursive; font-size: 8px;"
          />
        </div>

        <div>
          <label class="block text-xs font-medium text-[#5c3a1e] mb-1" style="font-family: 'Press Start 2P', cursive; font-size: 8px;">Password</label>
          <input
            v-model="password"
            type="password"
            placeholder="Your password"
            class="w-full px-4 py-2 border-2 border-[#5c3a1e] bg-white text-[#2d1b00] focus:outline-none focus:ring-2 focus:ring-yellow-600 text-xs"
            style="font-family: 'Press Start 2P', cursive; font-size: 8px;"
          />
        </div>

        <button
          type="submit"
          :disabled="loading"
          class="w-full pixel-btn py-2 text-xs"
        >
          {{ loading ? 'Logging in...' : 'Login' }}
        </button>
      </form>

      <p class="text-center text-xs text-[#5c3a1e] mt-6" style="font-family: 'Press Start 2P', cursive; font-size: 7px;">
        Don't have an account?
        <router-link to="/register" class="text-yellow-700 hover:text-yellow-900 font-medium">
          Register
        </router-link>
      </p>
    </div>
  </div>
</template>
