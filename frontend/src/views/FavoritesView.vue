<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useFavoriteStore } from '../stores/favorite'
import { useCartStore } from '../stores/cart'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const favoriteStore = useFavoriteStore()
const cartStore = useCartStore()
const authStore = useAuthStore()

onMounted(() => {
  favoriteStore.fetchFavorites()
})

function formatPrice(price: number): string {
  return price.toLocaleString() + 'g'
}

function addToCart(productId: number) {
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }
  cartStore.addToCart(productId)
}

function removeFavorite(productId: number) {
  favoriteStore.toggleFavorite(productId)
}
</script>

<template>
  <div class="max-w-7xl mx-auto px-4 py-8">
    <h1 class="text-lg font-bold text-yellow-300 mb-6" style="font-family: 'Press Start 2P', cursive;">My Wishlist</h1>

    <!-- Loading -->
    <div v-if="favoriteStore.loading" class="text-center py-12">
      <div class="animate-spin w-8 h-8 border-4 border-yellow-500 border-t-transparent rounded-full mx-auto"></div>
      <p class="text-yellow-200 mt-3 text-xs" style="font-family: 'Press Start 2P', cursive; font-size: 8px;">Loading...</p>
    </div>

    <!-- Empty -->
    <div v-else-if="favoriteStore.items.length === 0" class="text-center py-16">
      <svg class="w-16 h-16 mx-auto text-[#5c3a1e] mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
          d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
      </svg>
      <p class="text-yellow-200 text-sm" style="font-family: 'Press Start 2P', cursive; font-size: 9px;">Your wishlist is empty</p>
      <router-link to="/products" class="inline-block mt-4 pixel-btn px-6 py-2 text-xs">
        Browse Items
      </router-link>
    </div>

    <!-- Favorites Grid -->
    <div v-else class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
      <div
        v-for="fav in favoriteStore.items"
        :key="fav.id"
        class="bg-[#f4e4c1] pixel-border overflow-hidden"
      >
        <!-- Image -->
        <div
          class="h-48 bg-[#3a5c28] flex items-center justify-center cursor-pointer relative"
          @click="router.push(`/products/${fav.product.id}`)"
        >
          <div v-if="fav.product.imageUrl" class="h-full w-full flex items-center justify-center">
            <img :src="fav.product.imageUrl" :alt="fav.product.name" class="w-24 h-24 object-contain" style="image-rendering: pixelated;" />
          </div>
          <div v-else class="h-full w-full bg-gradient-to-br from-green-700 to-green-900 flex items-center justify-center">
            <span class="text-yellow-300 text-5xl font-bold opacity-80" style="font-family: 'Press Start 2P', cursive;">{{ fav.product.name.charAt(0) }}</span>
          </div>
          <!-- Remove button -->
          <button
            @click.stop="removeFavorite(fav.product.id)"
            class="absolute top-2 right-2 bg-[#f4e4c1] rounded-full p-2 shadow hover:bg-red-100 transition border-2 border-[#5c3a1e]"
          >
            <svg class="w-5 h-5 text-red-500" fill="currentColor" viewBox="0 0 24 24">
              <path d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
            </svg>
          </button>
        </div>

        <!-- Info -->
        <div class="p-4">
          <p class="text-xs text-[#8b5e14] mb-1" style="font-family: 'Press Start 2P', cursive; font-size: 7px;">{{ fav.product.category?.name }}</p>
          <h3
            class="text-xs font-medium text-[#2d1b00] line-clamp-2 h-10 cursor-pointer hover:text-yellow-700"
            style="font-family: 'Press Start 2P', cursive; font-size: 8px;"
            @click="router.push(`/products/${fav.product.id}`)"
          >
            {{ fav.product.name }}
          </h3>
          <p class="text-sm font-bold text-yellow-700 mt-2" style="font-family: 'Press Start 2P', cursive;">{{ formatPrice(fav.product.price) }}</p>
          <button
            @click="addToCart(fav.product.id)"
            :disabled="fav.product.stock <= 0"
            :class="[
              'w-full mt-3 py-2 text-xs font-medium transition',
              fav.product.stock > 0
                ? 'pixel-btn'
                : 'bg-gray-400 text-gray-600 cursor-not-allowed border-2 border-gray-500'
            ]"
          >
            {{ fav.product.stock > 0 ? 'Add to Cart' : 'Out of Stock' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
