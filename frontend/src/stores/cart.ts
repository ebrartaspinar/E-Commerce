import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { cartService } from '@/api/cartService'
import type { Cart } from '@/types'

export const useCartStore = defineStore('cart', () => {
  // State
  const cart = ref<Cart | null>(null)
  const loading = ref(false)

  // Getters
  const itemCount = computed(() => cart.value?.totalItems ?? 0)
  const totalPrice = computed(() => cart.value?.finalPrice ?? 0)
  const isEmpty = computed(() => !cart.value || cart.value.items.length === 0)

  // Actions
  async function fetchCart() {
    loading.value = true
    try {
      const response = await cartService.getCart()
      cart.value = response.data
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  async function addItem(productId: string, quantity: number = 1) {
    loading.value = true
    try {
      const response = await cartService.addToCart({ productId, quantity })
      cart.value = response.data
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  async function updateQuantity(productId: string, quantity: number) {
    loading.value = true
    try {
      const response = await cartService.updateCartItem(productId, { quantity })
      cart.value = response.data
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  async function removeItem(productId: string) {
    loading.value = true
    try {
      const response = await cartService.removeCartItem(productId)
      cart.value = response.data
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  async function clearCart() {
    loading.value = true
    try {
      await cartService.clearCart()
      cart.value = null
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  return {
    // State
    cart,
    loading,
    // Getters
    itemCount,
    totalPrice,
    isEmpty,
    // Actions
    fetchCart,
    addItem,
    updateQuantity,
    removeItem,
    clearCart,
  }
})
