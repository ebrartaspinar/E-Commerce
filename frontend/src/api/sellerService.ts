import api from '@/api/api'
import type {
  ApiResponse,
  PagedResponse,
  DashboardStats,
  SellerProduct,
  Order,
  CreateProductRequest,
  UpdateProductRequest,
  OrderStatus,
} from '@/types'

export const sellerService = {
  async getDashboard(): Promise<ApiResponse<DashboardStats>> {
    const response = await api.get<ApiResponse<DashboardStats>>('/seller/dashboard')
    return response.data
  },

  async getMyProducts(
    page: number = 0,
    size: number = 20
  ): Promise<ApiResponse<PagedResponse<SellerProduct>>> {
    const response = await api.get<ApiResponse<PagedResponse<SellerProduct>>>('/seller/products', {
      params: { page, size },
    })
    return response.data
  },

  async createProduct(req: CreateProductRequest): Promise<ApiResponse<SellerProduct>> {
    const response = await api.post<ApiResponse<SellerProduct>>('/seller/products', req)
    return response.data
  },

  async updateProduct(
    id: string,
    req: UpdateProductRequest
  ): Promise<ApiResponse<SellerProduct>> {
    const response = await api.put<ApiResponse<SellerProduct>>(`/seller/products/${id}`, req)
    return response.data
  },

  async deleteProduct(id: string): Promise<ApiResponse<void>> {
    const response = await api.delete<ApiResponse<void>>(`/seller/products/${id}`)
    return response.data
  },

  async getSellerOrders(
    page: number = 0,
    size: number = 20,
    status?: OrderStatus
  ): Promise<ApiResponse<PagedResponse<Order>>> {
    const response = await api.get<ApiResponse<PagedResponse<Order>>>('/seller/orders', {
      params: { page, size, ...(status && { status }) },
    })
    return response.data
  },

  async updateOrderStatus(id: string, status: OrderStatus): Promise<ApiResponse<Order>> {
    const response = await api.put<ApiResponse<Order>>(`/seller/orders/${id}/status`, null, {
      params: { status },
    })
    return response.data
  },
}
