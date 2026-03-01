import api from '@/api/api'
import type { ApiResponse, PagedResponse, Notification } from '@/types'

export const notificationService = {
  async getNotifications(
    page: number = 0,
    size: number = 20
  ): Promise<ApiResponse<PagedResponse<Notification>>> {
    const response = await api.get<ApiResponse<PagedResponse<Notification>>>('/notifications', {
      params: { page, size },
    })
    return response.data
  },

  async markAsRead(id: string): Promise<ApiResponse<Notification>> {
    const response = await api.put<ApiResponse<Notification>>(`/notifications/${id}/read`)
    return response.data
  },

  async markAllAsRead(): Promise<ApiResponse<void>> {
    const response = await api.put<ApiResponse<void>>('/notifications/read-all')
    return response.data
  },

  async getUnreadCount(): Promise<ApiResponse<number>> {
    const response = await api.get<ApiResponse<number>>('/notifications/unread-count')
    return response.data
  },
}
