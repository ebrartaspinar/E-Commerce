<template>
  <div class="page-container">
    <h1 class="section-title">&#9878; Seller Dashboard</h1>

    <!-- Loading -->
    <div v-if="loading" class="space-y-6">
      <div class="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <div v-for="i in 4" :key="i" class="pixel-card animate-pulse p-4">
          <div class="bg-gray-200 h-6 w-6 mb-2 rounded"></div>
          <div class="bg-gray-200 h-5 w-1/2 mb-1 rounded"></div>
          <div class="bg-gray-200 h-3 w-2/3 rounded"></div>
        </div>
      </div>
    </div>

    <div v-else-if="stats">
      <!-- Stats Cards -->
      <div class="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <div class="pixel-card p-4 hover:shadow-pixel-lg transition-shadow">
          <div class="font-pixel text-xl mb-2">&#128230;</div>
          <div class="font-pixel text-lg text-primary-600">{{ stats.totalProducts }}</div>
          <div class="font-body text-sm text-gray-500">Total Products</div>
        </div>
        <div class="pixel-card p-4 hover:shadow-pixel-lg transition-shadow">
          <div class="font-pixel text-xl mb-2">&#128220;</div>
          <div class="font-pixel text-lg text-secondary-600">{{ stats.totalOrders }}</div>
          <div class="font-body text-sm text-gray-500">Total Orders</div>
        </div>
        <div class="pixel-card p-4 hover:shadow-pixel-lg transition-shadow">
          <div class="font-pixel text-xl mb-2">&#128176;</div>
          <div class="font-pixel text-lg text-cozy-gold">{{ formatPrice(stats.totalRevenue) }}</div>
          <div class="font-body text-sm text-gray-500">Total Revenue (TL)</div>
        </div>
        <div class="pixel-card p-4 hover:shadow-pixel-lg transition-shadow">
          <div class="font-pixel text-xl mb-2">&#9203;</div>
          <div class="font-pixel text-lg text-cozy-red">{{ stats.pendingOrders }}</div>
          <div class="font-body text-sm text-gray-500">Pending Orders</div>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="flex flex-wrap gap-3 mb-8">
        <router-link
          to="/seller/products/new"
          class="pixel-border bg-primary-500 text-white font-pixel text-xs py-2 px-4 shadow-pixel hover:bg-primary-600 transition-colors"
        >
          + Add Product
        </router-link>
        <router-link
          to="/seller/products"
          class="pixel-border bg-white font-pixel text-xs py-2 px-4 shadow-pixel hover:bg-gray-50 transition-colors"
        >
          &#128230; My Products
        </router-link>
        <router-link
          to="/seller/orders"
          class="pixel-border bg-white font-pixel text-xs py-2 px-4 shadow-pixel hover:bg-gray-50 transition-colors"
        >
          &#128220; All Orders
        </router-link>
      </div>

      <!-- Monthly Revenue Chart -->
      <div class="pixel-card p-6 mb-8">
        <h2 class="font-pixel text-xs text-cozy-brown mb-4">&#128200; Monthly Revenue</h2>
        <div class="flex items-end gap-2 h-48">
          <div
            v-for="(revenue, idx) in stats.monthlyRevenue"
            :key="idx"
            class="flex-1 flex flex-col items-center justify-end"
          >
            <span class="font-retro text-xs text-gray-500 mb-1">
              {{ revenue > 0 ? formatShortPrice(revenue) : '' }}
            </span>
            <div
              class="w-full pixel-border transition-all hover:opacity-80"
              :class="idx === currentMonth ? 'bg-primary-500' : 'bg-primary-200'"
              :style="{ height: getBarHeight(revenue) + '%', minHeight: revenue > 0 ? '8px' : '2px' }"
            ></div>
            <span class="font-pixel text-xs text-gray-400 mt-1">
              {{ months[idx] }}
            </span>
          </div>
        </div>
      </div>

      <!-- Recent Orders -->
      <div class="pixel-card p-6">
        <div class="flex items-center justify-between mb-4">
          <h2 class="font-pixel text-xs text-cozy-brown">&#128220; Recent Orders</h2>
          <router-link
            to="/seller/orders"
            class="font-pixel text-xs text-primary-500 hover:underline"
          >
            View All &gt;
          </router-link>
        </div>

        <div v-if="stats.recentOrders.length > 0" class="overflow-x-auto">
          <table class="w-full">
            <thead>
              <tr class="border-b-2 border-cozy-brown">
                <th class="font-pixel text-xs text-cozy-brown text-left py-2 pr-4">Order #</th>
                <th class="font-pixel text-xs text-cozy-brown text-left py-2 pr-4">Date</th>
                <th class="font-pixel text-xs text-cozy-brown text-left py-2 pr-4">Status</th>
                <th class="font-pixel text-xs text-cozy-brown text-left py-2 pr-4">Items</th>
                <th class="font-pixel text-xs text-cozy-brown text-right py-2">Total</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="order in stats.recentOrders.slice(0, 5)"
                :key="order.id"
                class="border-b border-gray-200 hover:bg-cozy-cream cursor-pointer transition-colors"
                @click="$router.push(`/seller/orders`)"
              >
                <td class="font-body text-sm py-3 pr-4">{{ order.orderNumber }}</td>
                <td class="font-body text-sm text-gray-500 py-3 pr-4">{{ formatDate(order.createdAt) }}</td>
                <td class="py-3 pr-4">
                  <span
                    class="pixel-badge text-xs"
                    :class="getStatusClass(order.status)"
                  >
                    {{ order.status }}
                  </span>
                </td>
                <td class="font-body text-sm text-gray-500 py-3 pr-4">{{ order.items.length }}</td>
                <td class="font-pixel text-xs text-cozy-dark py-3 text-right">
                  {{ formatPrice(order.finalAmount) }} TL
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div v-else class="text-center py-8">
          <p class="font-retro text-lg text-gray-400">No orders yet. Keep stocking your shop!</p>
        </div>
      </div>
    </div>

    <!-- Error -->
    <div v-else class="text-center py-16">
      <div class="font-pixel text-4xl text-gray-300 mb-4">&#128123;</div>
      <h3 class="font-pixel text-sm text-cozy-dark mb-2">Failed to Load Dashboard</h3>
      <button
        class="pixel-border bg-primary-500 text-white font-pixel text-xs py-2 px-6 shadow-pixel"
        @click="fetchDashboard"
      >
        Try Again
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { sellerService } from '@/api/sellerService'
import { useToast } from '@/composables/useToast'
import type { DashboardStats, OrderStatus } from '@/types'

const { showToast } = useToast()

const loading = ref(true)
const stats = ref<DashboardStats | null>(null)

const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
const currentMonth = new Date().getMonth()

function formatPrice(price: number): string {
  return price.toLocaleString('tr-TR', { minimumFractionDigits: 2 })
}

function formatShortPrice(price: number): string {
  if (price >= 1000000) return (price / 1000000).toFixed(1) + 'M'
  if (price >= 1000) return (price / 1000).toFixed(1) + 'K'
  return price.toFixed(0)
}

function formatDate(dateStr: string): string {
  return new Date(dateStr).toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
  })
}

function getBarHeight(revenue: number): number {
  if (!stats.value) return 0
  const maxRevenue = Math.max(...stats.value.monthlyRevenue, 1)
  return (revenue / maxRevenue) * 100
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

async function fetchDashboard() {
  loading.value = true
  try {
    const response = await sellerService.getDashboard()
    stats.value = response.data
  } catch (err: any) {
    showToast('Failed to load dashboard data', 'error')
    stats.value = null
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchDashboard()
})
</script>
