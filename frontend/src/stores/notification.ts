import { ref } from 'vue'
import { defineStore } from 'pinia'
import { notificationService } from '@/api/notificationService'
import type { Notification } from '@/types'

export const useNotificationStore = defineStore('notification', () => {
  // State
  const notifications = ref<Notification[]>([])
  const unreadCount = ref(0)
  const loading = ref(false)

  // Actions
  async function fetchNotifications(page: number = 0) {
    loading.value = true
    try {
      const response = await notificationService.getNotifications(page)
      notifications.value = response.data.content
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  async function markRead(id: string) {
    loading.value = true
    try {
      await notificationService.markAsRead(id)

      // Update local state
      const notification = notifications.value.find((n) => n.id === id)
      if (notification) {
        notification.read = true
      }
      if (unreadCount.value > 0) {
        unreadCount.value--
      }
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  async function markAllRead() {
    loading.value = true
    try {
      await notificationService.markAllAsRead()

      // Update local state
      notifications.value.forEach((n) => {
        n.read = true
      })
      unreadCount.value = 0
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  async function fetchUnreadCount() {
    try {
      const response = await notificationService.getUnreadCount()
      unreadCount.value = response.data
    } catch (error) {
      throw error
    }
  }

  return {
    // State
    notifications,
    unreadCount,
    loading,
    // Actions
    fetchNotifications,
    markRead,
    markAllRead,
    fetchUnreadCount,
  }
})
