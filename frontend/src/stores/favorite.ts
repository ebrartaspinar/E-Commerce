import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { favoriteApi } from '../services/api'
import type { Favorite } from '../types'

export const useFavoriteStore = defineStore('favorite', () => {
  const items = ref<Favorite[]>([])
  const loading = ref(false)
  const favoriteProductIds = ref<Set<number>>(new Set())

  const itemCount = computed(() => items.value.length)

  async function fetchFavorites() {
    loading.value = true
    try {
      const { data } = await favoriteApi.getAll()
      items.value = data
      favoriteProductIds.value = new Set(data.map(f => f.product.id))
    } catch {
      items.value = []
      favoriteProductIds.value = new Set()
    } finally {
      loading.value = false
    }
  }

  function isFavorite(productId: number): boolean {
    return favoriteProductIds.value.has(productId)
  }

  async function toggleFavorite(productId: number) {
    if (isFavorite(productId)) {
      favoriteProductIds.value.delete(productId)
      items.value = items.value.filter(f => f.product.id !== productId)
      try {
        await favoriteApi.remove(productId)
      } catch {
        await fetchFavorites()
      }
    } else {
      favoriteProductIds.value.add(productId)
      try {
        await favoriteApi.add(productId)
        await fetchFavorites()
      } catch {
        favoriteProductIds.value.delete(productId)
      }
    }
  }

  function clear() {
    items.value = []
    favoriteProductIds.value = new Set()
  }

  return { items, loading, itemCount, fetchFavorites, isFavorite, toggleFavorite, clear }
})
