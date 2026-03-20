<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useCartStore } from '../stores/cart'
import { useFavoriteStore } from '../stores/favorite'
import type { Product } from '../types'

const props = defineProps<{
  product: Product
}>()

const router = useRouter()
const authStore = useAuthStore()
const cartStore = useCartStore()
const favoriteStore = useFavoriteStore()

function formatPrice(price: number): string {
  return price.toLocaleString() + 'g'
}

// Earthy/Stardew color palette for placeholder images
const colors = [
  'from-green-700 to-green-900',
  'from-amber-700 to-yellow-900',
  'from-emerald-600 to-teal-800',
  'from-lime-700 to-green-800',
  'from-yellow-600 to-amber-800',
  'from-teal-600 to-emerald-800',
]

function getGradient(name: string): string {
  const index = name.charCodeAt(0) % colors.length
  return colors[index]
}

function goToDetail() {
  router.push(`/products/${props.product.id}`)
}

function handleAddToCart(e: Event) {
  e.stopPropagation()
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }
  cartStore.addToCart(props.product.id)
}

function handleToggleFavorite(e: Event) {
  e.stopPropagation()
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }
  favoriteStore.toggleFavorite(props.product.id)
}
</script>

<template>
  <div
    @click="goToDetail"
    class="bg-[#f4e4c1] pixel-border cursor-pointer overflow-hidden"
  >
    <!-- Image placeholder -->
    <div class="relative">
      <div
        v-if="product.imageUrl"
        class="h-48 bg-[#3a5c28] flex items-center justify-center"
      >
        <img
          :src="product.imageUrl"
          :alt="product.name"
          class="w-24 h-24 object-contain"
          style="image-rendering: pixelated;"
        />
      </div>
      <div
        v-else
        :class="['h-48 bg-gradient-to-br flex items-center justify-center', getGradient(product.name)]"
      >
        <span class="text-white text-5xl font-bold opacity-80" style="font-family: 'Press Start 2P', cursive;">
          {{ product.name.charAt(0).toUpperCase() }}
        </span>
      </div>
      <!-- Favorite heart button -->
      <button
        @click="handleToggleFavorite"
        class="absolute top-2 right-2 bg-[#f4e4c1] rounded-full p-1.5 shadow hover:scale-110 transition pixel-border"
        style="box-shadow: none; border-width: 2px;"
      >
        <svg class="w-5 h-5" :class="favoriteStore.isFavorite(product.id) ? 'text-red-500' : 'text-[#5c3a1e]'" :fill="favoriteStore.isFavorite(product.id) ? 'currentColor' : 'none'" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
        </svg>
      </button>
    </div>

    <!-- Info -->
    <div class="p-4">
      <p class="text-xs text-[#8b5e14] mb-1">{{ product.category?.name }}</p>
      <h3 class="text-xs font-medium text-[#2d1b00] line-clamp-2 h-10" style="font-family: 'Press Start 2P', cursive; font-size: 8px;">{{ product.name }}</h3>
      <p class="text-sm font-bold text-yellow-700 mt-2" style="font-family: 'Press Start 2P', cursive;">{{ formatPrice(product.price) }}</p>

      <button
        @click="handleAddToCart"
        :disabled="product.stock <= 0"
        :class="[
          'w-full mt-3 py-2 text-xs font-medium transition',
          product.stock > 0
            ? 'pixel-btn'
            : 'bg-gray-400 text-gray-600 cursor-not-allowed border-2 border-gray-500'
        ]"
      >
        {{ product.stock > 0 ? 'Add to Cart' : 'Out of Stock' }}
      </button>
    </div>
  </div>
</template>
