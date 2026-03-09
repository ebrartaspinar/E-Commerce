import { ref } from 'vue'
import { defineStore } from 'pinia'
import { sellerService } from '@/api/sellerService'
import type {
  DashboardStats,
  SellerProduct,
  Order,
  CreateProductRequest,
  UpdateProductRequest,
  OrderStatus,
} from '@/types'

export const useSellerStore = defineStore('seller', () => {
  // State
  const dashboard = ref<DashboardStats | null>(null)
  const products = ref<SellerProduct[]>([])
  const sellerOrders = ref<Order[]>([])
  const loading = ref(false)
  const pagination = ref({
    page: 0,
    size: 20,
    totalElements: 0,
    totalPages: 0,
  })

  // Actions
  async function fetchDashboard() {
    loading.value = true
    try {
      const response = await sellerService.getDashboard()
      dashboard.value = response.data
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  async function fetchProducts(page: number = 0) {
    loading.value = true
    try {
      const response = await sellerService.getMyProducts(page, pagination.value.size)
      const paged = response.data

      products.value = paged.content
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

  async function createProduct(req: CreateProductRequest) {
    loading.value = true
    try {
      const response = await sellerService.createProduct(req)
      products.value.unshift(response.data)
      return response.data
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  async function updateProduct(id: string, req: UpdateProductRequest) {
    loading.value = true
    try {
      const response = await sellerService.updateProduct(id, req)
      const index = products.value.findIndex((p) => p.id === id)
      if (index !== -1) {
        products.value[index] = response.data
      }
      return response.data
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  async function deleteProduct(id: string) {
    loading.value = true
    try {
      await sellerService.deleteProduct(id)
      products.value = products.value.filter((p) => p.id !== id)
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  async function fetchOrders(page: number = 0, status?: OrderStatus) {
    loading.value = true
    try {
      const response = await sellerService.getSellerOrders(page, pagination.value.size, status)
      const paged = response.data

      sellerOrders.value = paged.content
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

  async function updateOrderStatus(orderId: string, status: OrderStatus) {
    loading.value = true
    try {
      const response = await sellerService.updateOrderStatus(orderId, status)
      const index = sellerOrders.value.findIndex((o) => o.id === orderId)
      if (index !== -1) {
        sellerOrders.value[index] = response.data
      }
      return response.data
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  return {
    // State
    dashboard,
    products,
    sellerOrders,
    loading,
    pagination,
    // Actions
    fetchDashboard,
    fetchProducts,
    createProduct,
    updateProduct,
    deleteProduct,
    fetchOrders,
    updateOrderStatus,
  }
})
