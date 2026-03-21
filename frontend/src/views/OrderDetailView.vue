<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { orderApi } from '../services/api'
import { useNotificationStore } from '../stores/notification'
import type { Order } from '../types'

const route = useRoute()
const router = useRouter()
const notification = useNotificationStore()

const order = ref<Order | null>(null)
const loading = ref(true)
const cancelling = ref(false)

function formatPrice(price: number): string {
  return price.toLocaleString() + 'g'
}

function formatDate(dateStr: string): string {
  return new Date(dateStr).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

function statusLabel(status: string): string {
  const labels: Record<string, string> = {
    PENDING: 'Pending',
    CONFIRMED: 'Confirmed',
    SHIPPED: 'Shipped',
    DELIVERED: 'Delivered',
    CANCELLED: 'Cancelled'
  }
  return labels[status] || status
}

function statusColor(status: string): string {
  const colors: Record<string, string> = {
    PENDING: 'bg-yellow-900 text-yellow-300 border-yellow-700',
    CONFIRMED: 'bg-blue-900 text-blue-300 border-blue-700',
    SHIPPED: 'bg-purple-900 text-purple-300 border-purple-700',
    DELIVERED: 'bg-green-900 text-green-300 border-green-700',
    CANCELLED: 'bg-red-900 text-red-300 border-red-700'
  }
  return colors[status] || 'bg-gray-900 text-gray-300 border-gray-700'
}

async function cancelOrder() {
  if (!order.value) return
  cancelling.value = true
  try {
    const response = await orderApi.cancel(order.value.id)
    order.value = response.data
    notification.showSuccess('Order cancelled')
  } catch (error: any) {
    notification.showError(error.response?.data?.message || 'Failed to cancel order')
  } finally {
    cancelling.value = false
  }
}

onMounted(async () => {
  try {
    const id = Number(route.params.id)
    const response = await orderApi.getById(id)
    order.value = response.data
  } catch (error) {
    notification.showError('Order not found')
    router.push('/orders')
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="max-w-3xl mx-auto px-4 py-6">
    <!-- Back -->
    <button @click="router.push('/orders')" class="text-yellow-500 hover:text-yellow-300 mb-6 inline-flex items-center gap-1 text-xs" style="font-family: 'Press Start 2P', cursive; font-size: 8px;">
      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
      </svg>
      Back to Orders
    </button>

    <!-- Loading -->
    <div v-if="loading" class="flex justify-center py-16">
      <div class="animate-spin rounded-full h-10 w-10 border-4 border-yellow-500 border-t-transparent"></div>
    </div>

    <div v-else-if="order">
      <!-- Header -->
      <div class="bg-[#f4e4c1] pixel-border p-6 mb-6">
        <div class="flex items-center justify-between mb-4">
          <h1 class="text-sm font-bold text-[#2d1b00]" style="font-family: 'Press Start 2P', cursive;">Order #{{ order.id }}</h1>
          <span :class="['text-xs font-medium px-3 py-1 border-2', statusColor(order.status)]" style="font-family: 'Press Start 2P', cursive; font-size: 7px;">
            {{ statusLabel(order.status) }}
          </span>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-4 text-xs">
          <div>
            <span class="text-[#8b5e14]" style="font-family: 'Press Start 2P', cursive; font-size: 7px;">Date:</span>
            <p class="text-[#2d1b00]" style="font-family: 'Press Start 2P', cursive; font-size: 8px;">{{ formatDate(order.createdAt) }}</p>
          </div>
          <div>
            <span class="text-[#8b5e14]" style="font-family: 'Press Start 2P', cursive; font-size: 7px;">Total:</span>
            <p class="text-lg font-bold text-yellow-700" style="font-family: 'Press Start 2P', cursive;">{{ formatPrice(order.totalAmount) }}</p>
          </div>
          <div class="md:col-span-2">
            <span class="text-[#8b5e14]" style="font-family: 'Press Start 2P', cursive; font-size: 7px;">Delivery Address:</span>
            <p class="text-[#2d1b00]" style="font-family: 'Press Start 2P', cursive; font-size: 8px;">{{ order.shippingAddress }}</p>
          </div>
        </div>
      </div>

      <!-- Order items -->
      <div class="bg-[#f4e4c1] pixel-border p-6 mb-6">
        <h2 class="text-sm font-semibold text-[#2d1b00] mb-4" style="font-family: 'Press Start 2P', cursive;">Items</h2>

        <div class="space-y-3">
          <div
            v-for="item in order.orderItems"
            :key="item.id"
            class="flex justify-between items-center py-2 border-b-2 border-[#e0d0a0] last:border-0"
          >
            <div>
              <p class="text-[#2d1b00] font-medium text-xs" style="font-family: 'Press Start 2P', cursive; font-size: 8px;">{{ item.productName }}</p>
              <p class="text-xs text-[#8b5e14]" style="font-family: 'Press Start 2P', cursive; font-size: 7px;">{{ formatPrice(item.price) }} x {{ item.quantity }}</p>
            </div>
            <span class="font-medium text-[#2d1b00] text-xs" style="font-family: 'Press Start 2P', cursive; font-size: 8px;">{{ formatPrice(item.price * item.quantity) }}</span>
          </div>
        </div>
      </div>

      <!-- Cancel button -->
      <button
        v-if="order.status === 'PENDING'"
        @click="cancelOrder"
        :disabled="cancelling"
        class="w-full bg-red-800 text-red-200 py-3 border-2 border-red-600 font-medium hover:bg-red-700 transition disabled:opacity-50 text-xs"
        style="font-family: 'Press Start 2P', cursive;"
      >
        {{ cancelling ? 'Cancelling...' : 'Cancel Order' }}
      </button>
    </div>
  </div>
</template>
