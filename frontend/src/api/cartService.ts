import api from '@/api/api'
import type { ApiResponse, Cart, AddToCartRequest, UpdateCartItemRequest } from '@/types'

export const cartService = {
  async getCart(): Promise<ApiResponse<Cart>> {
    const response = await api.get<ApiResponse<Cart>>('/cart')
    return response.data
  },

  async addToCart(req: AddToCartRequest): Promise<ApiResponse<Cart>> {
    const response = await api.post<ApiResponse<Cart>>('/cart/items', req)
    return response.data
  },

  async updateCartItem(
    productId: string,
    req: UpdateCartItemRequest
  ): Promise<ApiResponse<Cart>> {
    const response = await api.put<ApiResponse<Cart>>(`/cart/items/${productId}`, req)
    return response.data
  },

  async removeCartItem(productId: string): Promise<ApiResponse<Cart>> {
    const response = await api.delete<ApiResponse<Cart>>(`/cart/items/${productId}`)
    return response.data
  },

  async clearCart(): Promise<ApiResponse<void>> {
    const response = await api.delete<ApiResponse<void>>('/cart')
    return response.data
  },
}
