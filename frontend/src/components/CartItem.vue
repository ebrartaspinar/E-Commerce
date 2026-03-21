<script setup lang="ts">
import { useCartStore } from '../stores/cart'
import type { CartItem } from '../types'

const props = defineProps<{
  item: CartItem
}>()

const cartStore = useCartStore()

function formatPrice(price: number): string {
  return price.toLocaleString() + 'g'
}

function increaseQuantity() {
  cartStore.updateQuantity(props.item.id, props.item.quantity + 1)
}

function decreaseQuantity() {
  if (props.item.quantity > 1) {
    cartStore.updateQuantity(props.item.id, props.item.quantity - 1)
  }
}

function removeItem() {
  cartStore.removeItem(props.item.id)
}
</script>

<template>
  <div class="flex items-center gap-4 bg-[#f4e4c1] p-4 pixel-border">
    <!-- Product image placeholder -->
    <div class="w-20 h-20 bg-[#3a5c28] flex items-center justify-center flex-shrink-0 border-2 border-[#5c3a1e]">
      <span class="text-yellow-300 text-2xl font-bold" style="font-family: 'Press Start 2P', cursive;">{{ item.product.name.charAt(0).toUpperCase() }}</span>
    </div>

    <!-- Product info -->
    <div class="flex-1 min-w-0">
      <h3 class="text-xs font-medium text-[#2d1b00] truncate" style="font-family: 'Press Start 2P', cursive; font-size: 8px;">{{ item.product.name }}</h3>
      <p class="text-xs text-[#8b5e14]" style="font-family: 'Press Start 2P', cursive; font-size: 7px;">Unit price: {{ formatPrice(item.product.price) }}</p>
    </div>

    <!-- Quantity controls -->
    <div class="flex items-center gap-2">
      <button
        @click="decreaseQuantity"
        :disabled="item.quantity <= 1"
        class="w-8 h-8 border-2 border-[#5c3a1e] bg-[#e8a83e] flex items-center justify-center hover:bg-[#f0c060] disabled:opacity-30 transition text-[#2d1b00]"
      >
        -
      </button>
      <span class="w-8 text-center font-medium text-[#2d1b00]" style="font-family: 'Press Start 2P', cursive; font-size: 9px;">{{ item.quantity }}</span>
      <button
        @click="increaseQuantity"
        class="w-8 h-8 border-2 border-[#5c3a1e] bg-[#e8a83e] flex items-center justify-center hover:bg-[#f0c060] transition text-[#2d1b00]"
      >
        +
      </button>
    </div>

    <!-- Subtotal -->
    <div class="text-right min-w-[80px]">
      <p class="font-bold text-yellow-700" style="font-family: 'Press Start 2P', cursive; font-size: 9px;">{{ formatPrice(item.product.price * item.quantity) }}</p>
    </div>

    <!-- Remove button -->
    <button
      @click="removeItem"
      class="text-[#5c3a1e] hover:text-red-600 transition ml-2"
      title="Remove"
    >
      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
      </svg>
    </button>
  </div>
</template>
