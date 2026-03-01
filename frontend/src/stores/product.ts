import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { productService } from '@/api/productService'
import type { Product, Category, ProductFilter } from '@/types'

export const useProductStore = defineStore('product', () => {
  // State
  const products = ref<Product[]>([])
  const currentProduct = ref<Product | null>(null)
  const categories = ref<Category[]>([])
  const loading = ref(false)
  const filters = ref<ProductFilter>({
    page: 0,
    size: 20,
  })
  const pagination = ref({
    page: 0,
    size: 20,
    totalElements: 0,
    totalPages: 0,
  })

  // Actions
  async function fetchProducts() {
    loading.value = true
    try {
      const response = await productService.getProducts(filters.value)
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

  async function fetchProduct(id: string) {
    loading.value = true
    try {
      const response = await productService.getProduct(id)
      currentProduct.value = response.data
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  async function fetchProductBySlug(slug: string) {
    loading.value = true
    try {
      const response = await productService.getProductBySlug(slug)
      currentProduct.value = response.data
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  async function searchProducts(query: string) {
    loading.value = true
    try {
      const response = await productService.searchProducts(
        query,
        filters.value.page,
        filters.value.size
      )
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

  async function fetchCategories() {
    loading.value = true
    try {
      const response = await productService.getCategories()
      categories.value = response.data
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  function setFilter(filter: Partial<ProductFilter>) {
    filters.value = { ...filters.value, ...filter, page: 0 }
  }

  function resetFilters() {
    filters.value = { page: 0, size: 20 }
  }

  function nextPage() {
    if (pagination.value.page < pagination.value.totalPages - 1) {
      filters.value = { ...filters.value, page: (filters.value.page ?? 0) + 1 }
    }
  }

  function prevPage() {
    if ((filters.value.page ?? 0) > 0) {
      filters.value = { ...filters.value, page: (filters.value.page ?? 0) - 1 }
    }
  }

  return {
    // State
    products,
    currentProduct,
    categories,
    loading,
    filters,
    pagination,
    // Actions
    fetchProducts,
    fetchProduct,
    fetchProductBySlug,
    searchProducts,
    fetchCategories,
    setFilter,
    resetFilters,
    nextPage,
    prevPage,
  }
})
