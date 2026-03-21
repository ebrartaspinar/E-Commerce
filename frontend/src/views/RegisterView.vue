<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useNotificationStore } from '../stores/notification'

const router = useRouter()
const authStore = useAuthStore()
const notification = useNotificationStore()

const firstName = ref('')
const lastName = ref('')
const email = ref('')
const password = ref('')
const loading = ref(false)

async function handleRegister() {
  if (!firstName.value || !lastName.value || !email.value || !password.value) {
    notification.showError('Please fill in all fields')
    return
  }

  loading.value = true
  try {
    await authStore.register({
      firstName: firstName.value,
      lastName: lastName.value,
      email: email.value,
      password: password.value
    })
    notification.showSuccess('Account created successfully!')
    router.push('/')
  } catch (error: any) {
    notification.showError(error.response?.data?.message || 'Registration failed. Please try again.')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-[calc(100vh-200px)] flex items-center justify-center px-4">
    <div class="bg-[#f4e4c1] p-8 pixel-border w-full max-w-md">
      <h1 class="text-lg font-bold text-[#2d1b00] text-center mb-6" style="font-family: 'Press Start 2P', cursive;">Create Account</h1>

      <form @submit.prevent="handleRegister" class="space-y-4">
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-xs font-medium text-[#5c3a1e] mb-1" style="font-family: 'Press Start 2P', cursive; font-size: 8px;">First Name</label>
            <input
              v-model="firstName"
              type="text"
              placeholder="First name"
              class="w-full px-4 py-2 border-2 border-[#5c3a1e] bg-white text-[#2d1b00] focus:outline-none focus:ring-2 focus:ring-yellow-600 text-xs"
              style="font-family: 'Press Start 2P', cursive; font-size: 8px;"
            />
          </div>
          <div>
            <label class="block text-xs font-medium text-[#5c3a1e] mb-1" style="font-family: 'Press Start 2P', cursive; font-size: 8px;">Last Name</label>
            <input
              v-model="lastName"
              type="text"
              placeholder="Last name"
              class="w-full px-4 py-2 border-2 border-[#5c3a1e] bg-white text-[#2d1b00] focus:outline-none focus:ring-2 focus:ring-yellow-600 text-xs"
              style="font-family: 'Press Start 2P', cursive; font-size: 8px;"
            />
          </div>
        </div>

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
            placeholder="Min. 6 characters"
            class="w-full px-4 py-2 border-2 border-[#5c3a1e] bg-white text-[#2d1b00] focus:outline-none focus:ring-2 focus:ring-yellow-600 text-xs"
            style="font-family: 'Press Start 2P', cursive; font-size: 8px;"
          />
        </div>

        <button
          type="submit"
          :disabled="loading"
          class="w-full pixel-btn py-2 text-xs"
        >
          {{ loading ? 'Creating account...' : 'Create Account' }}
        </button>
      </form>

      <p class="text-center text-xs text-[#5c3a1e] mt-6" style="font-family: 'Press Start 2P', cursive; font-size: 7px;">
        Already have an account?
        <router-link to="/login" class="text-yellow-700 hover:text-yellow-900 font-medium">
          Login
        </router-link>
      </p>
    </div>
  </div>
</template>
