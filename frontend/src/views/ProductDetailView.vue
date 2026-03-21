<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { productApi } from '../services/api'
import { useAuthStore } from '../stores/auth'
import { useCartStore } from '../stores/cart'
import { useNotificationStore } from '../stores/notification'
import type { Product } from '../types'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const cartStore = useCartStore()
const notification = useNotificationStore()

const product = ref<Product | null>(null)
const loading = ref(true)
const quantity = ref(1)

function formatPrice(price: number): string {
  return price.toLocaleString() + 'g'
}

async function fetchProduct() {
  loading.value = true
  try {
    const id = Number(route.params.id)
    const response = await productApi.getById(id)
    product.value = response.data
  } catch (error) {
    notification.showError('Item not found')
    router.push('/products')
  } finally {
    loading.value = false
  }
}

function addToCart() {
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }
  if (product.value) {
    cartStore.addToCart(product.value.id, quantity.value)
  }
}

onMounted(fetchProduct)
</script>

<template>
  <div class="max-w-7xl mx-auto px-4 py-6">
    <!-- Back link -->
    <button @click="router.back()" class="text-yellow-500 hover:text-yellow-300 mb-6 inline-flex items-center gap-1 text-xs" style="font-family: 'Press Start 2P', cursive; font-size: 8px;">
      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
      </svg>
      Go Back
    </button>

    <!-- Loading -->
    <div v-if="loading" class="flex justify-center py-16">
      <div class="animate-spin rounded-full h-10 w-10 border-4 border-yellow-500 border-t-transparent"></div>
    </div>

    <!-- Product detail -->
    <div v-else-if="product" class="grid md:grid-cols-2 gap-8">
      <!-- Image -->
      <div>
        <div
          v-if="product.imageUrl"
          class="w-full h-96 bg-[#3a5c28] pixel-border flex items-center justify-center"
        >
          <img
            :src="product.imageUrl"
            :alt="product.name"
            class="w-48 h-48 object-contain"
            style="image-rendering: pixelated;"
          />
        </div>
        <div
          v-else
          class="w-full h-96 bg-gradient-to-br from-green-700 to-green-900 pixel-border flex items-center justify-center"
        >
          <span class="text-yellow-300 text-8xl font-bold opacity-80" style="font-family: 'Press Start 2P', cursive;">
            {{ product.name.charAt(0).toUpperCase() }}
          </span>
        </div>
      </div>

      <!-- Info -->
      <div class="bg-[#f4e4c1] pixel-border p-6">
        <span class="text-xs text-[#8b5e14] font-medium" style="font-family: 'Press Start 2P', cursive; font-size: 8px;">{{ product.category?.name }}</span>
        <h1 class="text-lg md:text-xl font-bold text-[#2d1b00] mt-1 mb-4" style="font-family: 'Press Start 2P', cursive;">{{ product.name }}</h1>
        <p class="text-2xl font-bold text-yellow-700 mb-4" style="font-family: 'Press Start 2P', cursive;">{{ formatPrice(product.price) }}</p>

        <p class="text-[#5c3a1e] mb-6 leading-relaxed text-xs" style="font-family: 'Press Start 2P', cursive; font-size: 8px;">{{ product.description }}</p>

        <!-- Stock -->
        <div class="mb-6">
          <span
            v-if="product.stock > 0"
            class="inline-block bg-green-800 text-green-200 text-xs px-3 py-1 border-2 border-green-600"
            style="font-family: 'Press Start 2P', cursive; font-size: 8px;"
          >
            In Stock ({{ product.stock }} qty)
          </span>
          <span v-else class="inline-block bg-red-900 text-red-300 text-xs px-3 py-1 border-2 border-red-700" style="font-family: 'Press Start 2P', cursive; font-size: 8px;">
            Out of Stock
          </span>
        </div>

        <!-- Quantity + Add to cart -->
        <div v-if="product.stock > 0" class="flex items-center gap-4">
          <div class="flex items-center border-2 border-[#5c3a1e]">
            <button
              @click="quantity > 1 && quantity--"
              class="px-3 py-2 hover:bg-[#e8a83e] transition bg-[#f4e4c1] text-[#2d1b00]"
            >
              -
            </button>
            <span class="px-4 py-2 border-x-2 border-[#5c3a1e] min-w-[48px] text-center text-[#2d1b00]" style="font-family: 'Press Start 2P', cursive; font-size: 9px;">{{ quantity }}</span>
            <button
              @click="quantity < product.stock && quantity++"
              class="px-3 py-2 hover:bg-[#e8a83e] transition bg-[#f4e4c1] text-[#2d1b00]"
            >
              +
            </button>
          </div>

          <button
            @click="addToCart"
            class="flex-1 pixel-btn py-3 text-xs"
          >
            Add to Cart
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
