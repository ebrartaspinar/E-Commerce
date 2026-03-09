import api from '@/api/api'
import type { ApiResponse, Payment, PaymentRequest } from '@/types'

export const paymentService = {
  async createPayment(req: PaymentRequest): Promise<ApiResponse<Payment>> {
    const response = await api.post<ApiResponse<Payment>>('/payments', req)
    return response.data
  },

  async getPayment(orderId: string): Promise<ApiResponse<Payment>> {
    const response = await api.get<ApiResponse<Payment>>(`/payments/order/${orderId}`)
    return response.data
  },
}
