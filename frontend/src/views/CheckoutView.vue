<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../stores/cart'
import { useNotificationStore } from '../stores/notification'
import { orderApi } from '../services/api'

const router = useRouter()
const cartStore = useCartStore()
const notification = useNotificationStore()

const shippingAddress = ref('')
const loading = ref(false)

function formatPrice(price: number): string {
  return price.toLocaleString() + 'g'
}

async function handleOrder() {
  if (!shippingAddress.value.trim()) {
    notification.showError('Please enter a delivery address')
    return
  }

  loading.value = true
  try {
    const response = await orderApi.create(shippingAddress.value.trim())
    notification.showSuccess('Order placed successfully!')
    await cartStore.fetchCart()
    router.push(`/orders/${response.data.id}`)
  } catch (error: any) {
    notification.showError(error.response?.data?.message || 'Failed to create order')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (cartStore.items.length === 0) {
    cartStore.fetchCart()
  }
})
</script>

<template>
  <div class="max-w-3xl mx-auto px-4 py-6">
    <h1 class="text-lg font-bold text-yellow-300 mb-6" style="font-family: 'Press Start 2P', cursive;">Order Confirmation</h1>

    <!-- Order summary -->
    <div class="bg-[#f4e4c1] pixel-border p-6 mb-6">
      <h2 class="text-sm font-semibold text-[#2d1b00] mb-4" style="font-family: 'Press Start 2P', cursive;">Order Summary</h2>

      <div v-if="cartStore.items.length === 0" class="text-[#5c3a1e] text-center py-4 text-xs" style="font-family: 'Press Start 2P', cursive; font-size: 8px;">
        Your inventory is empty. Add items before placing an order.
      </div>

      <div v-else class="space-y-3">
        <div
          v-for="item in cartStore.items"
          :key="item.id"
          class="flex justify-between items-center text-xs"
        >
          <div>
            <span class="text-[#2d1b00]" style="font-family: 'Press Start 2P', cursive; font-size: 8px;">{{ item.product.name }}</span>
            <span class="text-[#8b5e14] ml-2" style="font-family: 'Press Start 2P', cursive; font-size: 7px;">x{{ item.quantity }}</span>
          </div>
          <span class="font-medium text-[#2d1b00]" style="font-family: 'Press Start 2P', cursive; font-size: 8px;">{{ formatPrice(item.product.price * item.quantity) }}</span>
        </div>

        <div class="border-t-2 border-[#5c3a1e] pt-3 flex justify-between items-center">
          <span class="font-semibold text-[#2d1b00] text-xs" style="font-family: 'Press Start 2P', cursive;">Total</span>
          <span class="text-lg font-bold text-yellow-700" style="font-family: 'Press Start 2P', cursive;">{{ formatPrice(cartStore.totalAmount) }}</span>
        </div>
      </div>
    </div>

    <!-- Shipping address -->
    <div class="bg-[#f4e4c1] pixel-border p-6 mb-6">
      <h2 class="text-sm font-semibold text-[#2d1b00] mb-4" style="font-family: 'Press Start 2P', cursive;">Delivery Address</h2>
      <textarea
        v-model="shippingAddress"
        rows="4"
        placeholder="Enter your delivery address..."
        class="w-full px-4 py-3 border-2 border-[#5c3a1e] bg-white text-[#2d1b00] focus:outline-none focus:ring-2 focus:ring-yellow-600 resize-none text-xs"
        style="font-family: 'Press Start 2P', cursive; font-size: 8px;"
      ></textarea>
    </div>

    <!-- Submit -->
    <button
      @click="handleOrder"
      :disabled="loading || cartStore.items.length === 0"
      class="w-full pixel-btn py-3 text-xs"
    >
      {{ loading ? 'Placing order...' : 'Confirm Order' }}
    </button>
  </div>
</template>
