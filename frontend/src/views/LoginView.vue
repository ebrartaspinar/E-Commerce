<template>
  <div class="page-container flex items-center justify-center min-h-[70vh]">
    <div class="pixel-card w-full max-w-md p-8">
      <!-- Header -->
      <div class="text-center mb-8">
        <div class="font-pixel text-2xl text-primary-500 mb-2">&#9876; &#9733; &#128737;</div>
        <h1 class="font-pixel text-lg text-cozy-dark mb-2">Welcome Back</h1>
        <p class="font-retro text-xl text-gray-500">Enter your credentials to continue your quest</p>
      </div>

      <!-- Error message -->
      <div
        v-if="error"
        class="mb-4 p-3 border-2 border-cozy-red bg-red-50 font-body text-sm text-cozy-red"
      >
        {{ error }}
      </div>

      <!-- Form -->
      <form @submit.prevent="handleLogin" class="space-y-5">
        <div>
          <label class="block font-pixel text-xs text-cozy-brown mb-2">Email</label>
          <input
            v-model="form.email"
            type="email"
            placeholder="hero@example.com"
            class="pixel-input"
            required
          />
        </div>

        <div>
          <label class="block font-pixel text-xs text-cozy-brown mb-2">Password</label>
          <input
            v-model="form.password"
            type="password"
            placeholder="Your secret spell..."
            class="pixel-input"
            required
          />
        </div>

        <button
          type="submit"
          :disabled="loading"
          class="w-full pixel-border bg-primary-500 text-white font-pixel text-sm py-3 px-4 hover:bg-primary-600 active:shadow-pixel-hover active:translate-x-0.5 active:translate-y-0.5 transition-all disabled:opacity-50 disabled:cursor-not-allowed shadow-pixel"
        >
          <span v-if="loading" class="animate-pulse">Loading...</span>
          <span v-else>&#9876; Login &#9876;</span>
        </button>
      </form>

      <!-- Register link -->
      <div class="mt-6 text-center">
        <p class="font-body text-sm text-gray-500">
          New adventurer?
          <router-link to="/register" class="text-primary-500 font-semibold hover:underline">
            Create an Account
          </router-link>
        </p>
      </div>

      <!-- Decorative footer -->
      <div class="mt-6 text-center font-retro text-lg text-gray-400">
        &#9733; &#9733; &#9733; Your loot awaits &#9733; &#9733; &#9733;
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useToast } from '@/composables/useToast'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const { showToast } = useToast()

const loading = ref(false)
const error = ref('')

const form = reactive({
  email: '',
  password: '',
})

async function handleLogin() {
  error.value = ''
  loading.value = true

  try {
    await authStore.login({
      email: form.email,
      password: form.password,
    })
    showToast('Welcome back, adventurer!', 'success')

    const redirect = route.query.redirect as string
    router.push(redirect || '/')
  } catch (err: any) {
    error.value =
      err.response?.data?.message || 'Login failed. Check your credentials and try again.'
    showToast('Login failed!', 'error')
  } finally {
    loading.value = false
  }
}
</script>
