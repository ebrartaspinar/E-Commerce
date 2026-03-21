<script setup lang="ts">
import { onMounted } from 'vue'
import { useCartStore } from '../stores/cart'
import CartItemComponent from '../components/CartItem.vue'

const cartStore = useCartStore()

function formatPrice(price: number): string {
  return price.toLocaleString() + 'g'
}

onMounted(() => {
  cartStore.fetchCart()
})
</script>

<template>
  <div class="max-w-4xl mx-auto px-4 py-6">
    <h1 class="text-lg font-bold text-yellow-300 mb-6" style="font-family: 'Press Start 2P', cursive;">My Inventory</h1>

    <!-- Loading -->
    <div v-if="cartStore.loading" class="flex justify-center py-16">
      <div class="animate-spin rounded-full h-10 w-10 border-4 border-yellow-500 border-t-transparent"></div>
    </div>

    <!-- Empty cart -->
    <div v-else-if="cartStore.items.length === 0" class="text-center py-16">
      <svg class="w-16 h-16 text-[#5c3a1e] mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 100 4 2 2 0 000-4z" />
      </svg>
      <p class="text-yellow-200 text-sm mb-4" style="font-family: 'Press Start 2P', cursive; font-size: 9px;">Your inventory is empty</p>
      <router-link
        to="/products"
        class="inline-block pixel-btn px-6 py-2 text-xs"
      >
        Browse Items
      </router-link>
    </div>

    <!-- Cart items -->
    <div v-else>
      <div class="space-y-3">
        <CartItemComponent v-for="item in cartStore.items" :key="item.id" :item="item" />
      </div>

      <!-- Summary -->
      <div class="mt-6 bg-[#f4e4c1] pixel-border p-6">
        <div class="flex justify-between items-center mb-4">
          <span class="text-[#5c3a1e] text-xs" style="font-family: 'Press Start 2P', cursive; font-size: 8px;">Total ({{ cartStore.itemCount }} items)</span>
          <span class="text-xl font-bold text-yellow-700" style="font-family: 'Press Start 2P', cursive;">{{ formatPrice(cartStore.totalAmount) }}</span>
        </div>
        <router-link
          to="/checkout"
          class="block w-full pixel-btn text-center py-3 text-xs"
        >
          Place Order
        </router-link>
      </div>
    </div>
  </div>
</template>
