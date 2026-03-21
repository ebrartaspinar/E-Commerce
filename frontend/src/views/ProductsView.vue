<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { productApi, categoryApi } from '../services/api'
import ProductCard from '../components/ProductCard.vue'
import type { Product, Category } from '../types'

const route = useRoute()
const router = useRouter()

const products = ref<Product[]>([])
const categories = ref<Category[]>([])
const loading = ref(true)
const searchQuery = ref((route.query.search as string) || '')
const selectedCategory = ref<number | null>(
  route.query.categoryId ? Number(route.query.categoryId) : null
)
const currentPage = ref(0)
const totalPages = ref(0)
const pageSize = 12

async function fetchProducts() {
  loading.value = true
  try {
    const params: any = { page: currentPage.value, size: pageSize }
    if (searchQuery.value.trim()) params.search = searchQuery.value.trim()
    if (selectedCategory.value) params.categoryId = selectedCategory.value

    const response = await productApi.getAll(params)
    products.value = response.data.content
    totalPages.value = response.data.totalPages
  } catch (error) {
    console.error('Failed to load products:', error)
  } finally {
    loading.value = false
  }
}

async function fetchCategories() {
  try {
    const response = await categoryApi.getAll()
    categories.value = response.data
  } catch (error) {
    console.error('Failed to load categories:', error)
  }
}

function handleSearch() {
  currentPage.value = 0
  updateQueryParams()
  fetchProducts()
}

function selectCategory(categoryId: number | null) {
  selectedCategory.value = categoryId
  currentPage.value = 0
  updateQueryParams()
  fetchProducts()
}

function changePage(page: number) {
  currentPage.value = page
  fetchProducts()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function updateQueryParams() {
  const query: any = {}
  if (searchQuery.value.trim()) query.search = searchQuery.value.trim()
  if (selectedCategory.value) query.categoryId = selectedCategory.value
  router.replace({ query })
}

watch(
  () => route.query,
  (newQuery) => {
    searchQuery.value = (newQuery.search as string) || ''
    selectedCategory.value = newQuery.categoryId ? Number(newQuery.categoryId) : null
    currentPage.value = 0
    fetchProducts()
  }
)

onMounted(() => {
  fetchCategories()
  fetchProducts()
})
</script>

<template>
  <div class="max-w-7xl mx-auto px-4 py-6">
    <h1 class="text-lg font-bold text-yellow-300 mb-6" style="font-family: 'Press Start 2P', cursive;">Shop Items</h1>

    <!-- Search & Filters -->
    <div class="mb-6 space-y-4">
      <!-- Search bar -->
      <form @submit.prevent="handleSearch" class="flex max-w-lg">
        <input
          v-model="searchQuery"
          type="text"
          placeholder="Search items..."
          class="flex-1 px-4 py-2 border-2 border-[#5c3a1e] bg-[#f4e4c1] text-[#2d1b00] focus:outline-none focus:ring-2 focus:ring-yellow-600 text-xs"
          style="font-family: 'Press Start 2P', cursive; font-size: 8px;"
        />
        <button
          type="submit"
          class="pixel-btn px-6 py-2"
        >
          Go
        </button>
      </form>

      <!-- Category filters -->
      <div class="flex flex-wrap gap-2">
        <button
          @click="selectCategory(null)"
          :class="[
            'px-4 py-1.5 text-xs transition border-2',
            selectedCategory === null
              ? 'bg-[#e8a83e] text-[#2d1b00] border-[#5c3a1e]'
              : 'bg-[#f4e4c1] border-[#5c3a1e] text-[#5c3a1e] hover:bg-[#e8a83e]'
          ]"
          style="font-family: 'Press Start 2P', cursive; font-size: 8px;"
        >
          All Categories
        </button>
        <button
          v-for="cat in categories"
          :key="cat.id"
          @click="selectCategory(cat.id)"
          :class="[
            'px-4 py-1.5 text-xs transition border-2',
            selectedCategory === cat.id
              ? 'bg-[#e8a83e] text-[#2d1b00] border-[#5c3a1e]'
              : 'bg-[#f4e4c1] border-[#5c3a1e] text-[#5c3a1e] hover:bg-[#e8a83e]'
          ]"
          style="font-family: 'Press Start 2P', cursive; font-size: 8px;"
        >
          {{ cat.name }}
        </button>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="flex justify-center py-16">
      <div class="animate-spin rounded-full h-10 w-10 border-4 border-yellow-500 border-t-transparent"></div>
    </div>

    <!-- Empty state -->
    <div v-else-if="products.length === 0" class="text-center text-yellow-200 py-16">
      <p class="text-sm" style="font-family: 'Press Start 2P', cursive; font-size: 9px;">No items found.</p>
      <p class="text-xs mt-2" style="font-family: 'Press Start 2P', cursive; font-size: 7px;">Try a different search term or category.</p>
    </div>

    <!-- Product grid -->
    <div v-else>
      <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
        <ProductCard v-for="product in products" :key="product.id" :product="product" />
      </div>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="flex justify-center items-center gap-2 mt-8">
        <button
          @click="changePage(currentPage - 1)"
          :disabled="currentPage === 0"
          class="px-3 py-2 border-2 border-[#5c3a1e] bg-[#f4e4c1] text-[#2d1b00] hover:bg-[#e8a83e] disabled:opacity-30 disabled:cursor-not-allowed transition text-xs"
          style="font-family: 'Press Start 2P', cursive; font-size: 8px;"
        >
          Prev
        </button>

        <template v-for="page in totalPages" :key="page">
          <button
            @click="changePage(page - 1)"
            :class="[
              'w-10 h-10 transition border-2 border-[#5c3a1e] text-xs',
              currentPage === page - 1
                ? 'bg-[#e8a83e] text-[#2d1b00]'
                : 'bg-[#f4e4c1] text-[#5c3a1e] hover:bg-[#e8a83e]'
            ]"
            style="font-family: 'Press Start 2P', cursive; font-size: 9px;"
          >
            {{ page }}
          </button>
        </template>

        <button
          @click="changePage(currentPage + 1)"
          :disabled="currentPage >= totalPages - 1"
          class="px-3 py-2 border-2 border-[#5c3a1e] bg-[#f4e4c1] text-[#2d1b00] hover:bg-[#e8a83e] disabled:opacity-30 disabled:cursor-not-allowed transition text-xs"
          style="font-family: 'Press Start 2P', cursive; font-size: 8px;"
        >
          Next
        </button>
      </div>
    </div>
  </div>
</template>
