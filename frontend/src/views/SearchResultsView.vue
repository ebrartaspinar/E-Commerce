<template>
  <div class="page-container">
    <!-- Search Header -->
    <div class="mb-6">
      <h1 class="font-pixel text-base text-cozy-dark mb-2">
        &#128270; Search Results
      </h1>
      <p v-if="query" class="font-body text-sm text-gray-500">
        {{ productStore.pagination.totalElements }} results for
        "<span class="font-semibold text-cozy-dark">{{ query }}</span>"
      </p>
    </div>

    <div class="flex flex-col lg:flex-row gap-6">
      <!-- Sidebar Filters -->
      <aside class="hidden lg:block w-64 flex-shrink-0">
        <div class="pixel-card sticky top-4">
          <h3 class="font-pixel text-sm text-cozy-dark mb-4">&#9881; Filters</h3>

          <!-- Category -->
          <div class="mb-5">
            <label class="font-pixel text-xs text-cozy-brown block mb-2">Category</label>
            <select
              v-model="localFilters.categoryId"
              class="pixel-input text-sm"
              @change="applyFilters"
            >
              <option value="">All Categories</option>
              <option v-for="cat in categories" :key="cat.id" :value="cat.id">
                {{ cat.name }}
              </option>
            </select>
          </div>

          <!-- Price Range -->
          <div class="mb-5">
            <label class="font-pixel text-xs text-cozy-brown block mb-2">Price Range</label>
            <div class="flex gap-2 items-center">
              <input
                v-model.number="localFilters.minPrice"
                type="number"
                placeholder="Min"
                min="0"
                class="pixel-input text-sm w-1/2"
                @change="applyFilters"
              />
              <span class="text-gray-400">-</span>
              <input
                v-model.number="localFilters.maxPrice"
                type="number"
                placeholder="Max"
                min="0"
                class="pixel-input text-sm w-1/2"
                @change="applyFilters"
              />
            </div>
          </div>

          <!-- Brand -->
          <div class="mb-5">
            <label class="font-pixel text-xs text-cozy-brown block mb-2">Brand</label>
            <input
              v-model="localFilters.brand"
              type="text"
              placeholder="Enter brand..."
              class="pixel-input text-sm"
              @change="applyFilters"
            />
          </div>

          <!-- Reset -->
          <button
            class="w-full pixel-border bg-gray-100 font-pixel text-xs py-2 hover:bg-gray-200 transition-colors"
            @click="resetFilters"
          >
            Reset Filters
          </button>
        </div>
      </aside>

      <!-- Main Content -->
      <div class="flex-1">
        <!-- Sort -->
        <div class="flex items-center justify-between mb-4">
          <p class="font-body text-sm text-gray-500">
            {{ productStore.pagination.totalElements }} items
          </p>
          <select
            v-model="sortOption"
            class="pixel-input w-auto text-sm"
            @change="applyFilters"
          >
            <option value="">Sort by: Relevance</option>
            <option value="price-asc">Price: Low to High</option>
            <option value="price-desc">Price: High to Low</option>
            <option value="rating-desc">Highest Rated</option>
            <option value="createdAt-desc">Newest First</option>
          </select>
        </div>

        <!-- Loading Skeletons -->
        <div v-if="productStore.loading" class="grid grid-cols-2 md:grid-cols-3 gap-4">
          <div v-for="i in 6" :key="i" class="pixel-card animate-pulse">
            <div class="bg-gray-200 h-40 mb-3"></div>
            <div class="bg-gray-200 h-4 w-3/4 mb-2 rounded"></div>
            <div class="bg-gray-200 h-3 w-1/2 mb-2 rounded"></div>
            <div class="bg-gray-200 h-5 w-1/3 rounded"></div>
          </div>
        </div>

        <!-- Products Grid -->
        <div
          v-else-if="productStore.products.length > 0"
          class="grid grid-cols-2 md:grid-cols-3 gap-4"
        >
          <ProductCard
            v-for="product in productStore.products"
            :key="product.id"
            :product="product"
          />
        </div>

        <!-- Empty State -->
        <div v-else class="text-center py-16">
          <div class="font-pixel text-4xl text-gray-300 mb-4">&#128270;</div>
          <h3 class="font-pixel text-sm text-cozy-dark mb-2">No Results Found</h3>
          <p class="font-retro text-xl text-gray-400 mb-4">
            No items match your search for "{{ query }}". Try different keywords!
          </p>
          <router-link
            to="/products"
            class="inline-block pixel-border bg-primary-500 text-white font-pixel text-xs py-2 px-6 shadow-pixel hover:bg-primary-600 transition-colors"
          >
            Browse All Products
          </router-link>
        </div>

        <!-- Pagination -->
        <div
          v-if="productStore.pagination.totalPages > 1"
          class="flex items-center justify-center gap-2 mt-8"
        >
          <button
            class="pixel-border bg-white font-pixel text-xs py-2 px-3 disabled:opacity-40 hover:bg-gray-50 shadow-pixel"
            :disabled="productStore.pagination.page === 0"
            @click="goToPage(productStore.pagination.page - 1)"
          >
            &lt; Prev
          </button>

          <button
            v-for="page in Math.min(productStore.pagination.totalPages, 5)"
            :key="page - 1"
            class="pixel-border font-pixel text-xs py-2 px-3 shadow-pixel transition-colors"
            :class="(page - 1) === productStore.pagination.page
              ? 'bg-primary-500 text-white'
              : 'bg-white hover:bg-gray-50'"
            @click="goToPage(page - 1)"
          >
            {{ page }}
          </button>

          <button
            class="pixel-border bg-white font-pixel text-xs py-2 px-3 disabled:opacity-40 hover:bg-gray-50 shadow-pixel"
            :disabled="productStore.pagination.page >= productStore.pagination.totalPages - 1"
            @click="goToPage(productStore.pagination.page + 1)"
          >
            Next &gt;
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useProductStore } from '@/stores/product'
import ProductCard from '@/components/common/ProductCard.vue'
import type { ProductFilter } from '@/types'

const route = useRoute()
const productStore = useProductStore()

const sortOption = ref('')
const categories = computed(() => productStore.categories)
const query = computed(() => (route.query.q as string) || '')

const localFilters = reactive<ProductFilter>({
  categoryId: '',
  minPrice: undefined,
  maxPrice: undefined,
  brand: '',
})

function applyFilters() {
  if (!query.value) return
  productStore.setFilter({
    ...localFilters,
    search: query.value,
    page: 0,
    size: 20,
    ...(sortOption.value ? {
      sortBy: sortOption.value.split('-')[0],
      sortDir: sortOption.value.split('-')[1],
    } : {}),
  })
  productStore.fetchProducts()
}

function resetFilters() {
  localFilters.categoryId = ''
  localFilters.minPrice = undefined
  localFilters.maxPrice = undefined
  localFilters.brand = ''
  sortOption.value = ''
  applyFilters()
}

function goToPage(page: number) {
  productStore.filters.page = page
  productStore.fetchProducts()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

async function doSearch() {
  if (!query.value) return
  productStore.setFilter({
    search: query.value,
    page: 0,
    size: 20,
  })
  await productStore.fetchProducts()
}

onMounted(async () => {
  await productStore.fetchCategories()
  doSearch()
})

watch(
  () => route.query.q,
  () => {
    doSearch()
  }
)
</script>
