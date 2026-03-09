<template>
  <div class="page-container">
    <div class="flex flex-col lg:flex-row gap-6">
      <!-- Mobile Filter Toggle -->
      <button
        class="lg:hidden pixel-border bg-white font-pixel text-xs py-2 px-4 shadow-pixel flex items-center gap-2"
        @click="showMobileFilters = true"
      >
        &#9881; Filters
        <span v-if="activeFilterCount > 0" class="pixel-badge bg-primary-500 text-white ml-1">
          {{ activeFilterCount }}
        </span>
      </button>

      <!-- Sidebar Filters (Desktop) -->
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

          <!-- Min Rating -->
          <div class="mb-5">
            <label class="font-pixel text-xs text-cozy-brown block mb-2">Min Rating</label>
            <div class="flex gap-1">
              <button
                v-for="star in 5"
                :key="star"
                class="text-lg transition-colors"
                :class="(localFilters.minRating ?? 0) >= star ? 'text-cozy-gold' : 'text-gray-300'"
                @click="setRating(star)"
              >
                &#9733;
              </button>
            </div>
          </div>

          <!-- In Stock -->
          <div class="mb-5">
            <label class="flex items-center gap-2 cursor-pointer">
              <input
                type="checkbox"
                v-model="localFilters.inStock"
                class="w-4 h-4 accent-primary-500"
                @change="applyFilters"
              />
              <span class="font-body text-sm">In stock only</span>
            </label>
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

      <!-- Mobile Filter Drawer -->
      <teleport to="body">
        <transition name="page-fade">
          <div
            v-if="showMobileFilters"
            class="fixed inset-0 bg-black bg-opacity-50 z-40"
            @click="showMobileFilters = false"
          ></div>
        </transition>
        <transition name="slide-right">
          <div
            v-if="showMobileFilters"
            class="fixed top-0 left-0 bottom-0 w-80 bg-cozy-cream z-50 overflow-y-auto p-4"
          >
            <div class="flex items-center justify-between mb-4">
              <h3 class="font-pixel text-sm text-cozy-dark">&#9881; Filters</h3>
              <button
                class="font-pixel text-xs text-cozy-brown hover:text-cozy-dark"
                @click="showMobileFilters = false"
              >
                X Close
              </button>
            </div>

            <!-- Category -->
            <div class="mb-5">
              <label class="font-pixel text-xs text-cozy-brown block mb-2">Category</label>
              <select v-model="localFilters.categoryId" class="pixel-input text-sm">
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
                />
                <span class="text-gray-400">-</span>
                <input
                  v-model.number="localFilters.maxPrice"
                  type="number"
                  placeholder="Max"
                  min="0"
                  class="pixel-input text-sm w-1/2"
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
              />
            </div>

            <!-- Min Rating -->
            <div class="mb-5">
              <label class="font-pixel text-xs text-cozy-brown block mb-2">Min Rating</label>
              <div class="flex gap-1">
                <button
                  v-for="star in 5"
                  :key="star"
                  class="text-lg transition-colors"
                  :class="(localFilters.minRating ?? 0) >= star ? 'text-cozy-gold' : 'text-gray-300'"
                  @click="setRating(star)"
                >
                  &#9733;
                </button>
              </div>
            </div>

            <!-- In Stock -->
            <div class="mb-5">
              <label class="flex items-center gap-2 cursor-pointer">
                <input
                  type="checkbox"
                  v-model="localFilters.inStock"
                  class="w-4 h-4 accent-primary-500"
                />
                <span class="font-body text-sm">In stock only</span>
              </label>
            </div>

            <div class="flex gap-2">
              <button
                class="flex-1 pixel-border bg-primary-500 text-white font-pixel text-xs py-2 shadow-pixel"
                @click="applyFilters(); showMobileFilters = false"
              >
                Apply
              </button>
              <button
                class="flex-1 pixel-border bg-gray-100 font-pixel text-xs py-2"
                @click="resetFilters(); showMobileFilters = false"
              >
                Reset
              </button>
            </div>
          </div>
        </transition>
      </teleport>

      <!-- Main Content -->
      <div class="flex-1">
        <!-- Sort & Results count -->
        <div class="flex items-center justify-between mb-4">
          <p class="font-body text-sm text-gray-500">
            {{ productStore.pagination.totalElements }} products found
          </p>
          <select
            v-model="sortOption"
            class="pixel-input w-auto text-sm"
            @change="handleSort"
          >
            <option value="">Sort by: Default</option>
            <option value="price-asc">Price: Low to High</option>
            <option value="price-desc">Price: High to Low</option>
            <option value="rating-desc">Highest Rated</option>
            <option value="createdAt-desc">Newest First</option>
          </select>
        </div>

        <!-- Loading Skeletons -->
        <div v-if="productStore.loading" class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-3 gap-4">
          <div v-for="i in 9" :key="i" class="pixel-card animate-pulse">
            <div class="bg-gray-200 h-40 mb-3"></div>
            <div class="bg-gray-200 h-4 w-3/4 mb-2 rounded"></div>
            <div class="bg-gray-200 h-3 w-1/2 mb-2 rounded"></div>
            <div class="bg-gray-200 h-5 w-1/3 rounded"></div>
          </div>
        </div>

        <!-- Products Grid -->
        <div
          v-else-if="productStore.products.length > 0"
          class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-3 gap-4"
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
          <h3 class="font-pixel text-sm text-cozy-dark mb-2">No Products Found</h3>
          <p class="font-retro text-xl text-gray-400 mb-4">
            The treasure chest is empty! Try adjusting your filters.
          </p>
          <button
            class="pixel-border bg-primary-500 text-white font-pixel text-xs py-2 px-6 shadow-pixel hover:bg-primary-600 transition-colors"
            @click="resetFilters"
          >
            Clear Filters
          </button>
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

          <template v-for="page in visiblePages" :key="page">
            <button
              v-if="page >= 0"
              class="pixel-border font-pixel text-xs py-2 px-3 shadow-pixel transition-colors"
              :class="page === productStore.pagination.page
                ? 'bg-primary-500 text-white'
                : 'bg-white hover:bg-gray-50'"
              @click="goToPage(page)"
            >
              {{ page + 1 }}
            </button>
            <span v-else class="font-pixel text-xs text-gray-400">...</span>
          </template>

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
import { useRoute, useRouter } from 'vue-router'
import { useProductStore } from '@/stores/product'
import ProductCard from '@/components/common/ProductCard.vue'
import type { ProductFilter } from '@/types'

const route = useRoute()
const router = useRouter()
const productStore = useProductStore()

const showMobileFilters = ref(false)
const sortOption = ref('')
const categories = computed(() => productStore.categories)

const localFilters = reactive<ProductFilter>({
  categoryId: '',
  minPrice: undefined,
  maxPrice: undefined,
  brand: '',
  minRating: undefined,
  inStock: undefined,
})

const activeFilterCount = computed(() => {
  let count = 0
  if (localFilters.categoryId) count++
  if (localFilters.minPrice) count++
  if (localFilters.maxPrice) count++
  if (localFilters.brand) count++
  if (localFilters.minRating) count++
  if (localFilters.inStock) count++
  return count
})

const visiblePages = computed(() => {
  const total = productStore.pagination.totalPages
  const current = productStore.pagination.page
  const pages: number[] = []

  if (total <= 7) {
    for (let i = 0; i < total; i++) pages.push(i)
  } else {
    pages.push(0)
    if (current > 2) pages.push(-1) // ellipsis
    for (let i = Math.max(1, current - 1); i <= Math.min(total - 2, current + 1); i++) {
      pages.push(i)
    }
    if (current < total - 3) pages.push(-1) // ellipsis
    pages.push(total - 1)
  }

  return pages
})

function setRating(star: number) {
  if (localFilters.minRating === star) {
    localFilters.minRating = undefined
  } else {
    localFilters.minRating = star
  }
  applyFilters()
}

function applyFilters() {
  const filter: ProductFilter = {
    page: 0,
    size: 20,
  }

  if (localFilters.categoryId) filter.categoryId = localFilters.categoryId
  if (localFilters.minPrice) filter.minPrice = localFilters.minPrice
  if (localFilters.maxPrice) filter.maxPrice = localFilters.maxPrice
  if (localFilters.brand) filter.brand = localFilters.brand
  if (localFilters.minRating) filter.minRating = localFilters.minRating
  if (localFilters.inStock) filter.inStock = true

  // Preserve sort
  if (sortOption.value) {
    const [sortBy, sortDir] = sortOption.value.split('-')
    filter.sortBy = sortBy
    filter.sortDir = sortDir
  }

  productStore.setFilter(filter)
  productStore.fetchProducts()
}

function handleSort() {
  applyFilters()
}

function resetFilters() {
  localFilters.categoryId = ''
  localFilters.minPrice = undefined
  localFilters.maxPrice = undefined
  localFilters.brand = ''
  localFilters.minRating = undefined
  localFilters.inStock = undefined
  sortOption.value = ''
  productStore.resetFilters()
  productStore.fetchProducts()
}

function goToPage(page: number) {
  productStore.filters.page = page
  productStore.fetchProducts()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// Initialize from route query params
function initFromQuery() {
  const q = route.query
  if (q.categoryId) localFilters.categoryId = q.categoryId as string
  if (q.minPrice) localFilters.minPrice = Number(q.minPrice)
  if (q.maxPrice) localFilters.maxPrice = Number(q.maxPrice)
  if (q.brand) localFilters.brand = q.brand as string
  if (q.minRating) localFilters.minRating = Number(q.minRating)
  if (q.sortBy && q.sortDir) sortOption.value = `${q.sortBy}-${q.sortDir}`
}

onMounted(async () => {
  await productStore.fetchCategories()
  initFromQuery()
  applyFilters()
})

// Watch for route query changes
watch(
  () => route.query,
  () => {
    initFromQuery()
    applyFilters()
  }
)
</script>

<style scoped>
.slide-right-enter-active,
.slide-right-leave-active {
  transition: transform 0.3s ease;
}
.slide-right-enter-from,
.slide-right-leave-to {
  transform: translateX(-100%);
}
</style>
