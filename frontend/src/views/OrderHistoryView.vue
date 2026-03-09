<template>
  <div class="page-container">
    <h1 class="section-title">&#128220; Order History</h1>

    <!-- Tab Filters -->
    <div class="flex gap-1 mb-6 overflow-x-auto pb-2">
      <button
        v-for="tab in tabs"
        :key="tab.value"
        class="pixel-border font-pixel text-xs py-2 px-4 whitespace-nowrap transition-colors shadow-pixel"
        :class="activeTab === tab.value
          ? 'bg-primary-500 text-white'
          : 'bg-white text-cozy-brown hover:bg-primary-50'"
        @click="handleTabChange(tab.value)"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- Loading -->
    <div v-if="orderStore.loading" class="space-y-4">
      <div v-for="i in 3" :key="i" class="pixel-card animate-pulse p-4">
        <div class="flex justify-between mb-3">
          <div class="bg-gray-200 h-4 w-1/3 rounded"></div>
          <div class="bg-gray-200 h-4 w-1/5 rounded"></div>
        </div>
        <div class="bg-gray-200 h-3 w-1/2 mb-2 rounded"></div>
        <div class="bg-gray-200 h-3 w-1/4 rounded"></div>
      </div>
    </div>

    <!-- Order List -->
    <div v-else-if="filteredOrders.length > 0" class="space-y-4">
      <div
        v-for="order in filteredOrders"
        :key="order.id"
        class="pixel-card p-4 cursor-pointer hover:shadow-pixel-lg transition-shadow"
        @click="router.push(`/orders/${order.id}`)"
      >
        <div class="flex flex-wrap items-center justify-between gap-2 mb-3">
          <div>
            <span class="font-pixel text-xs text-cozy-dark">
              Order #{{ order.orderNumber }}
            </span>
            <span class="font-body text-xs text-gray-400 ml-2">
              {{ formatDate(order.createdAt) }}
            </span>
          </div>
          <span
            class="pixel-badge"
            :class="getStatusClass(order.status)"
          >
            {{ getStatusIcon(order.status) }} {{ order.status }}
          </span>
        </div>

        <!-- Items preview -->
        <div class="flex items-center gap-3 mb-3">
          <div class="flex -space-x-2">
            <div
              v-for="(item, idx) in order.items.slice(0, 3)"
              :key="item.id"
              class="w-10 h-10 border-2 border-white bg-gray-100 overflow-hidden"
              :style="{ zIndex: 3 - idx }"
            >
              <img
                v-if="item.productImage"
                :src="item.productImage"
                :alt="item.productName"
                class="w-full h-full object-cover"
              />
              <div v-else class="w-full h-full flex items-center justify-center font-pixel text-xs text-gray-300">
                &#128247;
              </div>
            </div>
            <div
              v-if="order.items.length > 3"
              class="w-10 h-10 border-2 border-white bg-gray-200 flex items-center justify-center font-pixel text-xs text-gray-500"
            >
              +{{ order.items.length - 3 }}
            </div>
          </div>
          <span class="font-body text-sm text-gray-500">
            {{ order.items.length }} item{{ order.items.length > 1 ? 's' : '' }}
          </span>
        </div>

        <div class="flex items-center justify-between">
          <span class="font-pixel text-xs text-primary-600">
            {{ formatPrice(order.finalAmount) }} TL
          </span>
          <span class="font-pixel text-xs text-gray-400 hover:text-primary-500">
            View Details &gt;
          </span>
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-else class="text-center py-16">
      <div class="font-pixel text-4xl text-gray-300 mb-4">&#128220;</div>
      <h3 class="font-pixel text-sm text-cozy-dark mb-2">No Orders Found</h3>
      <p class="font-retro text-xl text-gray-400 mb-4">
        <template v-if="activeTab === 'all'">
          You haven't placed any orders yet. Time to go on a shopping quest!
        </template>
        <template v-else>
          No {{ activeTab }} orders found.
        </template>
      </p>
      <router-link
        to="/products"
        class="inline-block pixel-border bg-primary-500 text-white font-pixel text-xs py-2 px-6 shadow-pixel hover:bg-primary-600 transition-colors"
      >
        Start Shopping
      </router-link>
    </div>

    <!-- Pagination -->
    <div
      v-if="orderStore.pagination.totalPages > 1"
      class="flex items-center justify-center gap-2 mt-8"
    >
      <button
        class="pixel-border bg-white font-pixel text-xs py-2 px-3 disabled:opacity-40 hover:bg-gray-50 shadow-pixel"
        :disabled="orderStore.pagination.page === 0"
        @click="goToPage(orderStore.pagination.page - 1)"
      >
        &lt; Prev
      </button>

      <button
        v-for="page in Math.min(orderStore.pagination.totalPages, 5)"
        :key="page - 1"
        class="pixel-border font-pixel text-xs py-2 px-3 shadow-pixel transition-colors"
        :class="(page - 1) === orderStore.pagination.page
          ? 'bg-primary-500 text-white'
          : 'bg-white hover:bg-gray-50'"
        @click="goToPage(page - 1)"
      >
        {{ page }}
      </button>

      <button
        class="pixel-border bg-white font-pixel text-xs py-2 px-3 disabled:opacity-40 hover:bg-gray-50 shadow-pixel"
        :disabled="orderStore.pagination.page >= orderStore.pagination.totalPages - 1"
        @click="goToPage(orderStore.pagination.page + 1)"
      >
        Next &gt;
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useOrderStore } from '@/stores/order'
import type { OrderStatus } from '@/types'

const router = useRouter()
const orderStore = useOrderStore()

const activeTab = ref('all')
const tabs = [
  { label: 'All', value: 'all' },
  { label: 'Active', value: 'active' },
  { label: 'Delivered', value: 'delivered' },
  { label: 'Cancelled', value: 'cancelled' },
]

const filteredOrders = computed(() => {
  if (activeTab.value === 'all') return orderStore.orders
  if (activeTab.value === 'active') {
    return orderStore.orders.filter((o) =>
      ['PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED'].includes(o.status)
    )
  }
  if (activeTab.value === 'delivered') {
    return orderStore.orders.filter((o) => o.status === 'DELIVERED')
  }
  if (activeTab.value === 'cancelled') {
    return orderStore.orders.filter((o) =>
      ['CANCELLED', 'REFUNDED'].includes(o.status)
    )
  }
  return orderStore.orders
})

function formatPrice(price: number): string {
  return price.toLocaleString('tr-TR', { minimumFractionDigits: 2 })
}

function formatDate(dateStr: string): string {
  return new Date(dateStr).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
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

function getStatusIcon(status: OrderStatus): string {
  switch (status) {
    case 'PENDING': return '&#9203;'
    case 'CONFIRMED': return '&#10003;'
    case 'PROCESSING': return '&#9881;'
    case 'SHIPPED': return '&#128666;'
    case 'DELIVERED': return '&#127881;'
    case 'CANCELLED': return '&#10007;'
    case 'REFUNDED': return '&#128176;'
    default: return '&#8226;'
  }
}

function handleTabChange(tab: string) {
  activeTab.value = tab
}

function goToPage(page: number) {
  orderStore.fetchOrders(page)
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(async () => {
  await orderStore.fetchOrders(0)
})
</script>
