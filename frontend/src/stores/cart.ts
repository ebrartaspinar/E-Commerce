import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { cartApi } from '../services/api'
import { useNotificationStore } from './notification'
import type { CartItem } from '../types'

export const useCartStore = defineStore('cart', () => {
  // State
  const items = ref<CartItem[]>([])
  const loading = ref(false)

  // Getters
  const totalAmount = computed(() => {
    return items.value.reduce((sum, item) => sum + item.product.price * item.quantity, 0)
  })

  const itemCount = computed(() => {
    return items.value.reduce((sum, item) => sum + item.quantity, 0)
  })

  // Actions
  async function fetchCart() {
    loading.value = true
    try {
      const response = await cartApi.getCart()
      items.value = response.data
    } catch (error) {
      console.error('Sepet yuklenemedi:', error)
    } finally {
      loading.value = false
    }
  }

  async function addToCart(productId: number, quantity: number = 1) {
    const notification = useNotificationStore()
    try {
      await cartApi.addItem(productId, quantity)
      await fetchCart()
      notification.showSuccess('Urun sepete eklendi!')
    } catch (error: any) {
      notification.showError(error.response?.data?.message || 'Sepete eklenemedi')
    }
  }

  async function updateQuantity(id: number, quantity: number) {
    const notification = useNotificationStore()
    try {
      await cartApi.updateQuantity(id, quantity)
      await fetchCart()
    } catch (error: any) {
      notification.showError(error.response?.data?.message || 'Miktar guncellenemedi')
    }
  }

  async function removeItem(id: number) {
    const notification = useNotificationStore()
    try {
      await cartApi.removeItem(id)
      await fetchCart()
      notification.showSuccess('Urun sepetten kaldirildi')
    } catch (error: any) {
      notification.showError(error.response?.data?.message || 'Urun kaldirilamadi')
    }
  }

  async function clearCart() {
    try {
      await cartApi.clearCart()
      items.value = []
    } catch (error) {
      console.error('Sepet temizlenemedi:', error)
    }
  }

  return {
    items,
    loading,
    totalAmount,
    itemCount,
    fetchCart,
    addToCart,
    updateQuantity,
    removeItem,
    clearCart
  }
})
