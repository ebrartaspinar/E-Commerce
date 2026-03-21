<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { orderApi } from '../services/api'
import type { Order } from '../types'

const router = useRouter()
const orders = ref<Order[]>([])
const loading = ref(true)

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

onMounted(async () => {
  try {
    const response = await orderApi.getAll()
    orders.value = response.data
  } catch (error) {
    console.error('Failed to load orders:', error)
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="max-w-4xl mx-auto px-4 py-6">
    <h1 class="text-lg font-bold text-yellow-300 mb-6" style="font-family: 'Press Start 2P', cursive;">My Orders</h1>

    <!-- Loading -->
    <div v-if="loading" class="flex justify-center py-16">
      <div class="animate-spin rounded-full h-10 w-10 border-4 border-yellow-500 border-t-transparent"></div>
    </div>

    <!-- Empty -->
    <div v-else-if="orders.length === 0" class="text-center text-yellow-200 py-16">
      <p class="text-sm mb-4" style="font-family: 'Press Start 2P', cursive; font-size: 9px;">No orders yet.</p>
      <router-link to="/products" class="pixel-btn px-6 py-2 text-xs inline-block">
        Browse Items
      </router-link>
    </div>

    <!-- Orders list -->
    <div v-else class="space-y-4">
      <div
        v-for="order in orders"
        :key="order.id"
        @click="router.push(`/orders/${order.id}`)"
        class="bg-[#f4e4c1] pixel-border p-5 cursor-pointer hover:bg-[#edd9a3] transition"
      >
        <div class="flex items-center justify-between mb-2">
          <div>
            <span class="text-xs text-[#8b5e14]" style="font-family: 'Press Start 2P', cursive; font-size: 7px;">Order #{{ order.id }}</span>
            <span class="text-xs text-[#8b5e14] ml-4" style="font-family: 'Press Start 2P', cursive; font-size: 7px;">{{ formatDate(order.createdAt) }}</span>
          </div>
          <span :class="['text-xs font-medium px-3 py-1 border-2', statusColor(order.status)]" style="font-family: 'Press Start 2P', cursive; font-size: 7px;">
            {{ statusLabel(order.status) }}
          </span>
        </div>
        <div class="flex items-center justify-between">
          <span class="text-xs text-[#5c3a1e]" style="font-family: 'Press Start 2P', cursive; font-size: 7px;">{{ order.orderItems?.length || 0 }} items</span>
          <span class="text-sm font-bold text-yellow-700" style="font-family: 'Press Start 2P', cursive;">{{ formatPrice(order.totalAmount) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>
