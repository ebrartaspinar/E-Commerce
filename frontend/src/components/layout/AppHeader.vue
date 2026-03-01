<template>
  <header class="bg-cozy-cream border-b-3 border-cozy-brown sticky top-0 z-50">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex items-center justify-between h-16">
        <!-- Logo -->
        <router-link to="/" class="flex items-center gap-2 shrink-0">
          <span class="text-xl text-cozy-brown" aria-hidden="true">&#9876;</span>
          <span class="font-pixel text-xs sm:text-sm text-primary-600 tracking-tight">
            TrendyolClone
          </span>
        </router-link>

        <!-- Search Bar (hidden on mobile) -->
        <div class="hidden md:flex flex-1 max-w-xl mx-6">
          <SearchBar
            :model-value="searchQuery"
            @update:model-value="searchQuery = $event"
            @search="handleSearch"
          />
        </div>

        <!-- Desktop Actions -->
        <nav class="hidden md:flex items-center gap-4">
          <!-- Notifications -->
          <button
            class="relative p-2 text-cozy-brown hover:text-primary-500 transition-colors"
            @click="$emit('toggleNotifications')"
            aria-label="Notifications"
          >
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
            </svg>
            <span
              v-if="notificationCount > 0"
              class="absolute -top-1 -right-1 bg-cozy-red text-white text-[10px] font-pixel min-w-[18px] h-[18px] flex items-center justify-center border-2 border-cozy-cream rounded-sm"
            >
              {{ notificationCount > 99 ? '99+' : notificationCount }}
            </span>
          </button>

          <!-- Cart -->
          <router-link
            to="/cart"
            class="relative p-2 text-cozy-brown hover:text-primary-500 transition-colors"
            aria-label="Shopping cart"
          >
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 100 4 2 2 0 000-4z" />
            </svg>
            <span
              v-if="cartItemCount > 0"
              class="absolute -top-1 -right-1 bg-primary-500 text-white text-[10px] font-pixel min-w-[18px] h-[18px] flex items-center justify-center border-2 border-cozy-cream rounded-sm"
            >
              {{ cartItemCount > 99 ? '99+' : cartItemCount }}
            </span>
          </router-link>

          <!-- Auth -->
          <template v-if="!isAuthenticated">
            <router-link
              to="/login"
              class="font-retro text-lg text-cozy-brown hover:text-primary-500 transition-colors px-3 py-1 border-3 border-transparent hover:border-cozy-brown"
            >
              Login
            </router-link>
            <router-link
              to="/register"
              class="font-retro text-lg bg-primary-500 text-white px-3 py-1 border-3 border-cozy-brown shadow-pixel hover:shadow-pixel-hover hover:translate-y-[2px] transition-all"
            >
              Register
            </router-link>
          </template>

          <!-- Account Dropdown -->
          <div v-else class="relative" ref="dropdownRef">
            <button
              @click="showDropdown = !showDropdown"
              class="flex items-center gap-2 font-retro text-lg text-cozy-brown hover:text-primary-500 transition-colors px-3 py-1"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
              <span>{{ userName }}</span>
              <svg class="w-4 h-4 transition-transform" :class="{ 'rotate-180': showDropdown }" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
              </svg>
            </button>

            <Transition name="dropdown">
              <div
                v-if="showDropdown"
                class="absolute right-0 mt-2 w-48 bg-cozy-cream border-3 border-cozy-brown shadow-pixel-lg z-50"
              >
                <router-link
                  to="/account"
                  class="block px-4 py-2 font-retro text-lg text-cozy-brown hover:bg-cozy-peach transition-colors"
                  @click="showDropdown = false"
                >
                  My Account
                </router-link>
                <router-link
                  to="/orders"
                  class="block px-4 py-2 font-retro text-lg text-cozy-brown hover:bg-cozy-peach transition-colors"
                  @click="showDropdown = false"
                >
                  My Orders
                </router-link>
                <router-link
                  to="/addresses"
                  class="block px-4 py-2 font-retro text-lg text-cozy-brown hover:bg-cozy-peach transition-colors"
                  @click="showDropdown = false"
                >
                  Addresses
                </router-link>
                <hr class="border-cozy-brown/30" />
                <button
                  @click="handleLogout"
                  class="w-full text-left px-4 py-2 font-retro text-lg text-cozy-red hover:bg-cozy-peach transition-colors"
                >
                  Logout
                </button>
              </div>
            </Transition>
          </div>
        </nav>

        <!-- Mobile Menu Button -->
        <button
          class="md:hidden p-2 text-cozy-brown hover:text-primary-500"
          @click="mobileMenuOpen = !mobileMenuOpen"
          aria-label="Toggle menu"
        >
          <svg v-if="!mobileMenuOpen" class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
          </svg>
          <svg v-else class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <!-- Mobile Search -->
      <div class="md:hidden pb-3">
        <SearchBar
          :model-value="searchQuery"
          @update:model-value="searchQuery = $event"
          @search="handleSearch"
        />
      </div>
    </div>

    <!-- Mobile Menu -->
    <Transition name="slide-down">
      <div
        v-if="mobileMenuOpen"
        class="md:hidden bg-cozy-cream border-t-3 border-cozy-brown"
      >
        <div class="px-4 py-3 space-y-2">
          <template v-if="!isAuthenticated">
            <router-link
              to="/login"
              class="block font-retro text-xl text-cozy-brown hover:text-primary-500 py-2"
              @click="mobileMenuOpen = false"
            >
              Login
            </router-link>
            <router-link
              to="/register"
              class="block font-retro text-xl text-cozy-brown hover:text-primary-500 py-2"
              @click="mobileMenuOpen = false"
            >
              Register
            </router-link>
          </template>
          <template v-else>
            <router-link
              to="/account"
              class="block font-retro text-xl text-cozy-brown hover:text-primary-500 py-2"
              @click="mobileMenuOpen = false"
            >
              My Account
            </router-link>
            <router-link
              to="/orders"
              class="block font-retro text-xl text-cozy-brown hover:text-primary-500 py-2"
              @click="mobileMenuOpen = false"
            >
              My Orders
            </router-link>
            <router-link
              to="/cart"
              class="block font-retro text-xl text-cozy-brown hover:text-primary-500 py-2"
              @click="mobileMenuOpen = false"
            >
              Cart ({{ cartItemCount }})
            </router-link>
            <button
              @click="handleLogout"
              class="block font-retro text-xl text-cozy-red hover:text-cozy-red/80 py-2"
            >
              Logout
            </button>
          </template>
        </div>
      </div>
    </Transition>
  </header>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import SearchBar from '@/components/common/SearchBar.vue'

const props = withDefaults(defineProps<{
  isAuthenticated?: boolean
  userName?: string
  cartItemCount?: number
  notificationCount?: number
}>(), {
  isAuthenticated: false,
  userName: '',
  cartItemCount: 0,
  notificationCount: 0,
})

const emit = defineEmits<{
  (e: 'logout'): void
  (e: 'search', query: string): void
  (e: 'toggleNotifications'): void
}>()

const router = useRouter()
const searchQuery = ref('')
const showDropdown = ref(false)
const mobileMenuOpen = ref(false)
const dropdownRef = ref<HTMLElement | null>(null)

function handleSearch(query: string) {
  emit('search', query)
  router.push({ path: '/products', query: { search: query } })
  mobileMenuOpen.value = false
}

function handleLogout() {
  showDropdown.value = false
  mobileMenuOpen.value = false
  emit('logout')
}

function handleClickOutside(event: MouseEvent) {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target as Node)) {
    showDropdown.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.dropdown-enter-active,
.dropdown-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}
.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
.slide-down-enter-active,
.slide-down-leave-active {
  transition: max-height 0.3s ease, opacity 0.3s ease;
  overflow: hidden;
}
.slide-down-enter-from,
.slide-down-leave-to {
  max-height: 0;
  opacity: 0;
}
.slide-down-enter-to,
.slide-down-leave-from {
  max-height: 300px;
  opacity: 1;
}
</style>
