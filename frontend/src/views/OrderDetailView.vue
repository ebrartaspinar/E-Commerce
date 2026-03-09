<template>
  <div class="page-container">
    <!-- Loading -->
    <div v-if="orderStore.loading && !order" class="animate-pulse space-y-6">
      <div class="bg-gray-200 h-8 w-1/3 rounded"></div>
      <div class="pixel-card p-6">
        <div class="bg-gray-200 h-4 w-1/2 mb-3 rounded"></div>
        <div class="bg-gray-200 h-4 w-1/3 rounded"></div>
      </div>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="text-center py-16">
      <div class="font-pixel text-4xl text-gray-300 mb-4">&#128220;</div>
      <h3 class="font-pixel text-sm text-cozy-dark mb-2">Order Not Found</h3>
      <router-link
        to="/orders"
        class="inline-block pixel-border bg-primary-500 text-white font-pixel text-xs py-2 px-6 shadow-pixel"
      >
        Back to Orders
      </router-link>
    </div>

    <!-- Order Content -->
    <div v-else-if="order">
      <!-- Header -->
      <div class="flex flex-wrap items-center justify-between gap-4 mb-6">
        <div>
          <router-link to="/orders" class="font-pixel text-xs text-primary-500 hover:underline mb-2 inline-block">
            &lt; Back to Orders
          </router-link>
          <h1 class="font-pixel text-base text-cozy-dark">
            Order #{{ order.orderNumber }}
          </h1>
          <p class="font-body text-sm text-gray-500">
            Placed on {{ formatDate(order.createdAt) }}
          </p>
        </div>
        <span
          class="pixel-badge text-sm"
          :class="getStatusClass(order.status)"
        >
          {{ order.status }}
        </span>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- Main Column -->
        <div class="lg:col-span-2 space-y-6">
          <!-- Order Timeline -->
          <div class="pixel-card p-6">
            <h2 class="font-pixel text-xs text-cozy-brown mb-4">&#9203; Order Timeline</h2>
            <div class="space-y-0">
              <div
                v-for="(change, idx) in order.statusHistory"
                :key="idx"
                class="flex gap-3"
              >
                <div class="flex flex-col items-center">
                  <div
                    class="w-3 h-3 rounded-full border-2 flex-shrink-0"
                    :class="idx === 0 ? 'bg-primary-500 border-primary-500' : 'bg-white border-cozy-brown'"
                  ></div>
                  <div
                    v-if="idx < order.statusHistory.length - 1"
                    class="w-0.5 h-8 bg-gray-300"
                  ></div>
                </div>
                <div class="pb-4">
                  <p class="font-pixel text-xs text-cozy-dark">{{ change.status }}</p>
                  <p class="font-body text-xs text-gray-400">
                    {{ formatDateTime(change.timestamp) }}
                  </p>
                  <p v-if="change.note" class="font-body text-xs text-gray-500 mt-1">
                    {{ change.note }}
                  </p>
                </div>
              </div>
            </div>
          </div>

          <!-- Items -->
          <div class="pixel-card p-6">
            <h2 class="font-pixel text-xs text-cozy-brown mb-4">&#128230; Items</h2>
            <div class="space-y-3">
              <div
                v-for="item in order.items"
                :key="item.id"
                class="flex items-center gap-4 p-3 bg-cozy-cream border border-gray-200"
              >
                <div class="w-16 h-16 flex-shrink-0 bg-white border border-gray-200 overflow-hidden">
                  <img
                    v-if="item.productImage"
                    :src="item.productImage"
                    :alt="item.productName"
                    class="w-full h-full object-cover"
                  />
                  <div v-else class="w-full h-full flex items-center justify-center font-pixel text-lg text-gray-300">
                    &#128247;
                  </div>
                </div>
                <div class="flex-1 min-w-0">
                  <p class="font-body text-sm font-semibold text-cozy-dark truncate">
                    {{ item.productName }}
                  </p>
                  <p class="font-body text-xs text-gray-400">
                    Seller: {{ item.sellerName }}
                  </p>
                  <p class="font-body text-xs text-gray-500">
                    Qty: {{ item.quantity }} x {{ formatPrice(item.unitPrice) }} TL
                  </p>
                </div>
                <span class="font-pixel text-xs text-cozy-dark">
                  {{ formatPrice(item.totalPrice) }} TL
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- Sidebar -->
        <div class="space-y-6">
          <!-- Shipping Address -->
          <div class="pixel-card p-4">
            <h3 class="font-pixel text-xs text-cozy-brown mb-3">&#127968; Shipping Address</h3>
            <div class="font-body text-sm text-gray-600 space-y-1">
              <p class="font-semibold">{{ order.shippingAddress.fullName }}</p>
              <p>{{ order.shippingAddress.fullAddress }}</p>
              <p>{{ order.shippingAddress.district }}, {{ order.shippingAddress.city }}</p>
              <p>{{ order.shippingAddress.zipCode }}</p>
              <p class="text-gray-400">{{ order.shippingAddress.phoneNumber }}</p>
            </div>
          </div>

          <!-- Payment Summary -->
          <div class="pixel-card p-4">
            <h3 class="font-pixel text-xs text-cozy-brown mb-3">&#128176; Payment Summary</h3>
            <div class="space-y-2">
              <div class="flex justify-between font-body text-sm">
                <span class="text-gray-500">Subtotal</span>
                <span>{{ formatPrice(order.totalAmount) }} TL</span>
              </div>
              <div v-if="order.discount > 0" class="flex justify-between font-body text-sm">
                <span class="text-gray-500">Discount</span>
                <span class="text-cozy-green">-{{ formatPrice(order.discount) }} TL</span>
              </div>
              <div class="flex justify-between font-body text-sm">
                <span class="text-gray-500">Shipping</span>
                <span v-if="order.shippingFee > 0">{{ formatPrice(order.shippingFee) }} TL</span>
                <span v-else class="text-cozy-green">Free</span>
              </div>
              <div class="border-t-2 border-cozy-brown pt-2">
                <div class="flex justify-between font-pixel text-sm">
                  <span>Total</span>
                  <span class="text-primary-600">{{ formatPrice(order.finalAmount) }} TL</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Cancel Button -->
          <button
            v-if="canCancel"
            class="w-full pixel-border bg-cozy-red text-white font-pixel text-xs py-3 shadow-pixel hover:opacity-90 transition-opacity disabled:opacity-50"
            :disabled="cancelling"
            @click="handleCancel"
          >
            <span v-if="cancelling" class="animate-pulse">Cancelling...</span>
            <span v-else>&#10007; Cancel Order</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useOrderStore } from '@/stores/order'
import { useToast } from '@/composables/useToast'
import type { OrderStatus } from '@/types'

const route = useRoute()
const orderStore = useOrderStore()
const { showToast } = useToast()

const error = ref(false)
const cancelling = ref(false)
const order = computed(() => orderStore.currentOrder)

const canCancel = computed(() => {
  if (!order.value) return false
  return ['PENDING', 'CONFIRMED'].includes(order.value.status)
})

function formatPrice(price: number): string {
  return price.toLocaleString('tr-TR', { minimumFractionDigits: 2 })
}

function formatDate(dateStr: string): string {
  return new Date(dateStr).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })
}

function formatDateTime(dateStr: string): string {
  return new Date(dateStr).toLocaleString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

function getStatusClass(status: OrderStatus): string {
  switch (status) {
    case 'PENDING':
      return 'bg-yellow-50 text-cozy-gold border-cozy-gold'
    case 'CONFIRMED':
    case 'PROCESSING':
      return 'bg-blue-50 text-blue-600 border-blue-400'
    case 'SHIPPED':
      return 'bg-purple-50 text-purple-600 border-purple-400'
    case 'DELIVERED':
      return 'bg-green-50 text-cozy-green border-cozy-green'
    case 'CANCELLED':
    case 'REFUNDED':
      return 'bg-red-50 text-cozy-red border-cozy-red'
    default:
      return 'bg-gray-50 text-gray-600 border-gray-400'
  }
}

async function handleCancel() {
  if (!order.value) return
  cancelling.value = true
  try {
    await orderStore.cancelOrder(order.value.id)
    showToast('Order cancelled successfully', 'info')
  } catch (err: any) {
    showToast(err.response?.data?.message || 'Failed to cancel order', 'error')
  } finally {
    cancelling.value = false
  }
}

onMounted(async () => {
  const id = route.params.id as string
  try {
    await orderStore.fetchOrder(id)
  } catch {
    error.value = true
  }
})
</script>
