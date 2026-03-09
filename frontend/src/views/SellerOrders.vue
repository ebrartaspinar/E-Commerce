<template>
  <div class="page-container">
    <h1 class="section-title">&#128220; Manage Orders</h1>

    <!-- Status Filter -->
    <div class="flex flex-wrap items-center gap-3 mb-6">
      <label class="font-pixel text-xs text-cozy-brown">Filter:</label>
      <select
        v-model="statusFilter"
        class="pixel-input w-auto text-sm"
        @change="fetchOrders(0)"
      >
        <option value="">All Statuses</option>
        <option value="PENDING">Pending</option>
        <option value="CONFIRMED">Confirmed</option>
        <option value="PROCESSING">Processing</option>
        <option value="SHIPPED">Shipped</option>
        <option value="DELIVERED">Delivered</option>
        <option value="CANCELLED">Cancelled</option>
      </select>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="space-y-3">
      <div v-for="i in 5" :key="i" class="pixel-card animate-pulse p-4 flex gap-4">
        <div class="bg-gray-200 h-4 w-24 rounded"></div>
        <div class="bg-gray-200 h-4 w-32 rounded"></div>
        <div class="bg-gray-200 h-4 w-20 rounded"></div>
        <div class="flex-1"></div>
        <div class="bg-gray-200 h-4 w-24 rounded"></div>
      </div>
    </div>

    <!-- Orders Table (Desktop) -->
    <div v-else-if="orders.length > 0">
      <div class="hidden md:block pixel-card overflow-x-auto">
        <table class="w-full">
          <thead>
            <tr class="border-b-2 border-cozy-brown">
              <th class="font-pixel text-xs text-cozy-brown text-left py-3 px-4">Order #</th>
              <th class="font-pixel text-xs text-cozy-brown text-left py-3 px-4">Buyer</th>
              <th class="font-pixel text-xs text-cozy-brown text-left py-3 px-4">Items</th>
              <th class="font-pixel text-xs text-cozy-brown text-left py-3 px-4">Total</th>
              <th class="font-pixel text-xs text-cozy-brown text-left py-3 px-4">Status</th>
              <th class="font-pixel text-xs text-cozy-brown text-left py-3 px-4">Date</th>
              <th class="font-pixel text-xs text-cozy-brown text-right py-3 px-4">Action</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="order in orders"
              :key="order.id"
              class="border-b border-gray-200 hover:bg-cozy-cream transition-colors"
            >
              <td class="py-3 px-4 font-body text-sm font-semibold">{{ order.orderNumber }}</td>
              <td class="py-3 px-4 font-body text-sm text-gray-600">
                {{ order.shippingAddress.fullName }}
              </td>
              <td class="py-3 px-4">
                <div class="flex flex-col gap-0.5">
                  <span
                    v-for="item in order.items.slice(0, 2)"
                    :key="item.id"
                    class="font-body text-xs text-gray-500 truncate max-w-[150px]"
                  >
                    {{ item.quantity }}x {{ item.productName }}
                  </span>
                  <span
                    v-if="order.items.length > 2"
                    class="font-body text-xs text-gray-400"
                  >
                    +{{ order.items.length - 2 }} more
                  </span>
                </div>
              </td>
              <td class="py-3 px-4 font-pixel text-xs text-cozy-dark">
                {{ formatPrice(order.finalAmount) }} TL
              </td>
              <td class="py-3 px-4">
                <span
                  class="pixel-badge text-xs"
                  :class="getStatusClass(order.status)"
                >
                  {{ order.status }}
                </span>
              </td>
              <td class="py-3 px-4 font-body text-sm text-gray-500">
                {{ formatDate(order.createdAt) }}
              </td>
              <td class="py-3 px-4 text-right">
                <select
                  v-if="canUpdateStatus(order.status)"
                  class="pixel-input w-auto text-xs py-1"
                  :value="order.status"
                  @change="handleStatusUpdate(order.id, ($event.target as HTMLSelectElement).value as any)"
                >
                  <option :value="order.status" disabled>{{ order.status }}</option>
                  <option
                    v-for="next in getNextStatuses(order.status)"
                    :key="next"
                    :value="next"
                  >
                    {{ next }}
                  </option>
                </select>
                <span v-else class="font-body text-xs text-gray-400">--</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Mobile Cards -->
      <div class="md:hidden space-y-3">
        <div
          v-for="order in orders"
          :key="order.id"
          class="pixel-card p-4"
        >
          <div class="flex items-center justify-between mb-2">
            <span class="font-pixel text-xs text-cozy-dark">{{ order.orderNumber }}</span>
            <span
              class="pixel-badge text-xs"
              :class="getStatusClass(order.status)"
            >
              {{ order.status }}
            </span>
          </div>

          <p class="font-body text-sm text-gray-600 mb-1">{{ order.shippingAddress.fullName }}</p>
          <p class="font-body text-xs text-gray-400 mb-2">{{ formatDate(order.createdAt) }}</p>

          <div class="flex items-center justify-between mb-3">
            <span class="font-body text-sm text-gray-500">
              {{ order.items.length }} item{{ order.items.length > 1 ? 's' : '' }}
            </span>
            <span class="font-pixel text-xs text-primary-600">
              {{ formatPrice(order.finalAmount) }} TL
            </span>
          </div>

          <select
            v-if="canUpdateStatus(order.status)"
            class="pixel-input text-sm w-full"
            :value="order.status"
            @change="handleStatusUpdate(order.id, ($event.target as HTMLSelectElement).value as any)"
          >
            <option :value="order.status" disabled>Update Status...</option>
            <option
              v-for="next in getNextStatuses(order.status)"
              :key="next"
              :value="next"
            >
              {{ next }}
            </option>
          </select>
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-else class="text-center py-16">
      <div class="font-pixel text-4xl text-gray-300 mb-4">&#128220;</div>
      <h3 class="font-pixel text-sm text-cozy-dark mb-2">No Orders Found</h3>
      <p class="font-retro text-xl text-gray-400">
        <template v-if="statusFilter">No orders with "{{ statusFilter }}" status.</template>
        <template v-else>No orders received yet. Keep your shop stocked!</template>
      </p>
    </div>

    <!-- Pagination -->
    <div
      v-if="pagination.totalPages > 1"
      class="flex items-center justify-center gap-2 mt-8"
    >
      <button
        class="pixel-border bg-white font-pixel text-xs py-2 px-3 disabled:opacity-40 hover:bg-gray-50 shadow-pixel"
        :disabled="pagination.page === 0"
        @click="fetchOrders(pagination.page - 1)"
      >
        &lt; Prev
      </button>

      <button
        v-for="p in Math.min(pagination.totalPages, 5)"
        :key="p - 1"
        class="pixel-border font-pixel text-xs py-2 px-3 shadow-pixel transition-colors"
        :class="(p - 1) === pagination.page
          ? 'bg-primary-500 text-white'
          : 'bg-white hover:bg-gray-50'"
        @click="fetchOrders(p - 1)"
      >
        {{ p }}
      </button>

      <button
        class="pixel-border bg-white font-pixel text-xs py-2 px-3 disabled:opacity-40 hover:bg-gray-50 shadow-pixel"
        :disabled="pagination.page >= pagination.totalPages - 1"
        @click="fetchOrders(pagination.page + 1)"
      >
        Next &gt;
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { sellerService } from '@/api/sellerService'
import { useToast } from '@/composables/useToast'
import type { Order, OrderStatus } from '@/types'

const { showToast } = useToast()

const loading = ref(true)
const orders = ref<Order[]>([])
const statusFilter = ref<OrderStatus | ''>('')
const pagination = reactive({
  page: 0,
  size: 20,
  totalElements: 0,
  totalPages: 0,
})

function formatPrice(price: number): string {
  return price.toLocaleString('tr-TR', { minimumFractionDigits: 2 })
}

function formatDate(dateStr: string): string {
  return new Date(dateStr).toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
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

function canUpdateStatus(status: OrderStatus): boolean {
  return !['DELIVERED', 'CANCELLED', 'REFUNDED'].includes(status)
}

function getNextStatuses(status: OrderStatus): OrderStatus[] {
  const transitions: Record<string, OrderStatus[]> = {
    PENDING: ['CONFIRMED', 'CANCELLED'],
    CONFIRMED: ['PROCESSING', 'CANCELLED'],
    PROCESSING: ['SHIPPED', 'CANCELLED'],
    SHIPPED: ['DELIVERED'],
  }
  return transitions[status] || []
}

async function handleStatusUpdate(orderId: string, newStatus: OrderStatus) {
  try {
    const response = await sellerService.updateOrderStatus(orderId, newStatus)
    const idx = orders.value.findIndex((o) => o.id === orderId)
    if (idx !== -1) {
      orders.value[idx] = response.data
    }
    showToast(`Order status updated to ${newStatus}`, 'success')
  } catch (err: any) {
    showToast(err.response?.data?.message || 'Failed to update status', 'error')
    // Refresh to get correct state
    fetchOrders(pagination.page)
  }
}

async function fetchOrders(page: number = 0) {
  loading.value = true
  try {
    const response = await sellerService.getSellerOrders(
      page,
      pagination.size,
      statusFilter.value || undefined
    )
    orders.value = response.data.content
    pagination.page = response.data.page
    pagination.totalElements = response.data.totalElements
    pagination.totalPages = response.data.totalPages
  } catch (err: any) {
    showToast('Failed to load orders', 'error')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchOrders()
})
</script>
