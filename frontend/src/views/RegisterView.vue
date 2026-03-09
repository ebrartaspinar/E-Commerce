<template>
  <div class="page-container flex items-center justify-center min-h-[70vh]">
    <div class="pixel-card w-full max-w-md p-8">
      <!-- Header -->
      <div class="text-center mb-8">
        <div class="font-pixel text-2xl text-primary-500 mb-2">&#128737; &#9733; &#9876;</div>
        <h1 class="font-pixel text-lg text-cozy-dark mb-2">Join the Guild</h1>
        <p class="font-retro text-xl text-gray-500">Create your account and start your adventure</p>
      </div>

      <!-- Error message -->
      <div
        v-if="error"
        class="mb-4 p-3 border-2 border-cozy-red bg-red-50 font-body text-sm text-cozy-red"
      >
        {{ error }}
      </div>

      <!-- Role Toggle -->
      <div class="flex mb-6 border-3 border-cozy-brown">
        <button
          type="button"
          class="flex-1 py-2 font-pixel text-xs text-center transition-colors"
          :class="form.role === 'BUYER'
            ? 'bg-primary-500 text-white'
            : 'bg-white text-cozy-brown hover:bg-primary-50'"
          @click="form.role = 'BUYER'"
        >
          &#128722; Buyer
        </button>
        <button
          type="button"
          class="flex-1 py-2 font-pixel text-xs text-center transition-colors border-l-3 border-cozy-brown"
          :class="form.role === 'SELLER'
            ? 'bg-secondary-500 text-white'
            : 'bg-white text-cozy-brown hover:bg-secondary-50'"
          @click="form.role = 'SELLER'"
        >
          &#9878; Seller
        </button>
      </div>

      <!-- Form -->
      <form @submit.prevent="handleRegister" class="space-y-4">
        <div class="grid grid-cols-2 gap-3">
          <div>
            <label class="block font-pixel text-xs text-cozy-brown mb-1">First Name</label>
            <input
              v-model="form.firstName"
              type="text"
              placeholder="Hero"
              class="pixel-input"
              required
            />
          </div>
          <div>
            <label class="block font-pixel text-xs text-cozy-brown mb-1">Last Name</label>
            <input
              v-model="form.lastName"
              type="text"
              placeholder="Smith"
              class="pixel-input"
              required
            />
          </div>
        </div>

        <div>
          <label class="block font-pixel text-xs text-cozy-brown mb-1">Email</label>
          <input
            v-model="form.email"
            type="email"
            placeholder="hero@example.com"
            class="pixel-input"
            required
          />
        </div>

        <div>
          <label class="block font-pixel text-xs text-cozy-brown mb-1">Password</label>
          <input
            v-model="form.password"
            type="password"
            placeholder="At least 8 characters..."
            class="pixel-input"
            minlength="8"
            required
          />
        </div>

        <div>
          <label class="block font-pixel text-xs text-cozy-brown mb-1">Phone (optional)</label>
          <input
            v-model="form.phoneNumber"
            type="tel"
            placeholder="+90 555 123 4567"
            class="pixel-input"
          />
        </div>

        <button
          type="submit"
          :disabled="loading"
          class="w-full pixel-border text-white font-pixel text-sm py-3 px-4 active:shadow-pixel-hover active:translate-x-0.5 active:translate-y-0.5 transition-all disabled:opacity-50 disabled:cursor-not-allowed shadow-pixel"
          :class="form.role === 'SELLER' ? 'bg-secondary-500 hover:bg-secondary-600' : 'bg-primary-500 hover:bg-primary-600'"
        >
          <span v-if="loading" class="animate-pulse">Creating...</span>
          <span v-else>&#9733; Create Account &#9733;</span>
        </button>
      </form>

      <!-- Login link -->
      <div class="mt-6 text-center">
        <p class="font-body text-sm text-gray-500">
          Already a guild member?
          <router-link to="/login" class="text-primary-500 font-semibold hover:underline">
            Login
          </router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useToast } from '@/composables/useToast'

const router = useRouter()
const authStore = useAuthStore()
const { showToast } = useToast()

const loading = ref(false)
const error = ref('')

const form = reactive({
  firstName: '',
  lastName: '',
  email: '',
  password: '',
  phoneNumber: '',
  role: 'BUYER' as 'BUYER' | 'SELLER',
})

async function handleRegister() {
  error.value = ''
  loading.value = true

  try {
    await authStore.register({
      firstName: form.firstName,
      lastName: form.lastName,
      email: form.email,
      password: form.password,
      phoneNumber: form.phoneNumber || undefined,
      role: form.role,
    })
    showToast('Welcome to the guild, adventurer!', 'success')
    router.push('/')
  } catch (err: any) {
    error.value =
      err.response?.data?.message || 'Registration failed. Please try again.'
    showToast('Registration failed!', 'error')
  } finally {
    loading.value = false
  }
}
</script>
