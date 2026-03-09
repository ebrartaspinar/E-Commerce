import api from '@/api/api'
import type { ApiResponse, PagedResponse, Order, CreateOrderRequest } from '@/types'

export const orderService = {
  async createOrder(req: CreateOrderRequest): Promise<ApiResponse<Order>> {
    const response = await api.post<ApiResponse<Order>>('/orders', req)
    return response.data
  },

  async getOrders(page: number = 0, size: number = 10): Promise<ApiResponse<PagedResponse<Order>>> {
    const response = await api.get<ApiResponse<PagedResponse<Order>>>('/orders', {
      params: { page, size },
    })
    return response.data
  },

  async getOrder(id: string): Promise<ApiResponse<Order>> {
    const response = await api.get<ApiResponse<Order>>(`/orders/${id}`)
    return response.data
  },

  async cancelOrder(id: string): Promise<ApiResponse<Order>> {
    const response = await api.put<ApiResponse<Order>>(`/orders/${id}/cancel`)
    return response.data
  },
}
