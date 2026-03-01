import { ref } from 'vue'
import { defineStore } from 'pinia'
import { orderService } from '@/api/orderService'
import type { Order, CreateOrderRequest } from '@/types'

export const useOrderStore = defineStore('order', () => {
  // State
  const orders = ref<Order[]>([])
  const currentOrder = ref<Order | null>(null)
  const loading = ref(false)
  const pagination = ref({
    page: 0,
    size: 10,
    totalElements: 0,
    totalPages: 0,
  })

  // Actions
  async function createOrder(req: CreateOrderRequest) {
    loading.value = true
    try {
      const response = await orderService.createOrder(req)
      currentOrder.value = response.data
      return response.data
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  async function fetchOrders(page: number = 0) {
    loading.value = true
    try {
      const response = await orderService.getOrders(page, pagination.value.size)
      const paged = response.data

      orders.value = paged.content
      pagination.value = {
        page: paged.page,
        size: paged.size,
        totalElements: paged.totalElements,
        totalPages: paged.totalPages,
      }
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  async function fetchOrder(id: string) {
    loading.value = true
    try {
      const response = await orderService.getOrder(id)
      currentOrder.value = response.data
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  async function cancelOrder(id: string) {
    loading.value = true
    try {
      const response = await orderService.cancelOrder(id)
      currentOrder.value = response.data

      // Update the order in the list if it exists
      const index = orders.value.findIndex((o) => o.id === id)
      if (index !== -1) {
        orders.value[index] = response.data
      }
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  return {
    // State
    orders,
    currentOrder,
    loading,
    pagination,
    // Actions
    createOrder,
    fetchOrders,
    fetchOrder,
    cancelOrder,
  }
})
