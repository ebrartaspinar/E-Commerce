<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useCartStore } from '../stores/cart'
import { useFavoriteStore } from '../stores/favorite'

const router = useRouter()
const authStore = useAuthStore()
const cartStore = useCartStore()
const favoriteStore = useFavoriteStore()

const searchQuery = ref('')
const mobileMenuOpen = ref(false)

function handleSearch() {
  if (searchQuery.value.trim()) {
    router.push({ path: '/products', query: { search: searchQuery.value.trim() } })
  } else {
    router.push('/products')
  }
  mobileMenuOpen.value = false
}

function toggleMobileMenu() {
  mobileMenuOpen.value = !mobileMenuOpen.value
}
</script>

<template>
  <nav class="bg-[#5c3a1e] shadow-md font-pixel">
    <div class="max-w-7xl mx-auto px-4">
      <div class="flex items-center justify-between h-16">
        <!-- Logo -->
        <router-link to="/" class="text-yellow-300 text-sm font-bold flex-shrink-0" style="font-family: 'Press Start 2P', cursive;">
          StarDrop Shop
        </router-link>

        <!-- Search bar - desktop -->
        <form @submit.prevent="handleSearch" class="hidden md:flex flex-1 max-w-lg mx-6">
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Search items..."
            class="w-full px-4 py-2 rounded-l-none border-2 border-[#3d2510] bg-[#f4e4c1] text-[#2d1b00] focus:outline-none focus:ring-2 focus:ring-yellow-600 text-xs"
            style="font-family: 'Press Start 2P', cursive; font-size: 8px; box-shadow: inset -1px -1px 0 #3d2510, inset 1px 1px 0 #8b6914;"
          />
          <button
            type="submit"
            class="pixel-btn px-4 py-2 rounded-r-none"
          >
            Go
          </button>
        </form>

        <!-- Desktop menu -->
        <div class="hidden md:flex items-center space-x-4 text-xs">
          <router-link to="/products" class="text-white hover:text-yellow-300 transition">
            Shop
          </router-link>

          <template v-if="authStore.isAuthenticated">
            <router-link to="/favorites" class="text-white hover:text-yellow-300 transition relative">
              <span class="flex items-center gap-1">
                <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                </svg>
                Wishlist
              </span>
              <span
                v-if="favoriteStore.itemCount > 0"
                class="absolute -top-2 -right-4 bg-red-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center"
              >
                {{ favoriteStore.itemCount }}
              </span>
            </router-link>
            <router-link to="/cart" class="text-white hover:text-yellow-300 transition relative">
              Inventory
              <span
                v-if="cartStore.itemCount > 0"
                class="absolute -top-2 -right-4 bg-red-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center"
              >
                {{ cartStore.itemCount }}
              </span>
            </router-link>
            <router-link to="/orders" class="text-white hover:text-yellow-300 transition">
              Orders
            </router-link>
            <span class="text-yellow-200 text-xs">{{ authStore.firstName }}</span>
            <button
              @click="authStore.logout()"
              class="text-white hover:text-yellow-300 transition text-xs"
            >
              Logout
            </button>
          </template>

          <template v-else>
            <router-link
              to="/login"
              class="text-white hover:text-yellow-300 transition"
            >
              Login
            </router-link>
            <router-link
              to="/register"
              class="pixel-btn px-3 py-1 text-xs"
            >
              Register
            </router-link>
          </template>
        </div>

        <!-- Mobile hamburger -->
        <button @click="toggleMobileMenu" class="md:hidden text-white">
          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path
              v-if="!mobileMenuOpen"
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M4 6h16M4 12h16M4 18h16"
            />
            <path
              v-else
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M6 18L18 6M6 6l12 12"
            />
          </svg>
        </button>
      </div>

      <!-- Mobile menu -->
      <div v-if="mobileMenuOpen" class="md:hidden pb-4 space-y-2">
        <form @submit.prevent="handleSearch" class="flex">
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Search items..."
            class="w-full px-4 py-2 border-2 border-[#3d2510] bg-[#f4e4c1] text-[#2d1b00] focus:outline-none text-xs"
            style="font-family: 'Press Start 2P', cursive; font-size: 8px;"
          />
          <button
            type="submit"
            class="pixel-btn px-4 py-2"
          >
            Go
          </button>
        </form>

        <router-link
          to="/products"
          class="block text-white hover:text-yellow-300 py-1 text-xs"
          @click="mobileMenuOpen = false"
        >
          Shop
        </router-link>

        <template v-if="authStore.isAuthenticated">
          <router-link to="/favorites" class="block text-white hover:text-yellow-300 py-1 text-xs" @click="mobileMenuOpen = false">
            Wishlist ({{ favoriteStore.itemCount }})
          </router-link>
          <router-link to="/cart" class="block text-white hover:text-yellow-300 py-1 text-xs" @click="mobileMenuOpen = false">
            Inventory ({{ cartStore.itemCount }})
          </router-link>
          <router-link to="/orders" class="block text-white hover:text-yellow-300 py-1 text-xs" @click="mobileMenuOpen = false">
            Orders
          </router-link>
          <div class="flex items-center justify-between pt-2 border-t border-[#8b6914]">
            <span class="text-yellow-200 text-xs">{{ authStore.firstName }}</span>
            <button @click="authStore.logout(); mobileMenuOpen = false" class="text-white text-xs">
              Logout
            </button>
          </div>
        </template>

        <template v-else>
          <router-link to="/login" class="block text-white hover:text-yellow-300 py-1 text-xs" @click="mobileMenuOpen = false">
            Login
          </router-link>
          <router-link to="/register" class="block text-white hover:text-yellow-300 py-1 text-xs" @click="mobileMenuOpen = false">
            Register
          </router-link>
        </template>
      </div>
    </div>
  </nav>
</template>
