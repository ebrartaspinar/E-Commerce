<template>
  <div class="page-container">
    <div class="flex items-center justify-between mb-6">
      <h1 class="section-title mb-0">&#128230; My Products</h1>
      <router-link
        to="/seller/products/new"
        class="pixel-border bg-primary-500 text-white font-pixel text-xs py-2 px-4 shadow-pixel hover:bg-primary-600 transition-colors"
      >
        + Add New Product
      </router-link>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="space-y-3">
      <div v-for="i in 5" :key="i" class="pixel-card animate-pulse p-4 flex items-center gap-4">
        <div class="bg-gray-200 w-16 h-16"></div>
        <div class="flex-1 space-y-2">
          <div class="bg-gray-200 h-4 w-1/2 rounded"></div>
          <div class="bg-gray-200 h-3 w-1/3 rounded"></div>
        </div>
        <div class="bg-gray-200 h-6 w-16 rounded"></div>
      </div>
    </div>

    <!-- Products Table (Desktop) / Cards (Mobile) -->
    <div v-else-if="products.length > 0">
      <!-- Desktop Table -->
      <div class="hidden md:block pixel-card overflow-x-auto">
        <table class="w-full">
          <thead>
            <tr class="border-b-2 border-cozy-brown">
              <th class="font-pixel text-xs text-cozy-brown text-left py-3 px-4">Product</th>
              <th class="font-pixel text-xs text-cozy-brown text-left py-3 px-4">Price</th>
              <th class="font-pixel text-xs text-cozy-brown text-left py-3 px-4">Stock</th>
              <th class="font-pixel text-xs text-cozy-brown text-left py-3 px-4">Status</th>
              <th class="font-pixel text-xs text-cozy-brown text-left py-3 px-4">Rating</th>
              <th class="font-pixel text-xs text-cozy-brown text-left py-3 px-4">Sales</th>
              <th class="font-pixel text-xs text-cozy-brown text-right py-3 px-4">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="product in products"
              :key="product.id"
              class="border-b border-gray-200 hover:bg-cozy-cream transition-colors"
            >
              <td class="py-3 px-4">
                <div class="flex items-center gap-3">
                  <div class="w-12 h-12 bg-gray-100 pixel-border overflow-hidden flex-shrink-0">
                    <div class="w-full h-full flex items-center justify-center font-pixel text-lg text-gray-300">
                      &#128230;
                    </div>
                  </div>
                  <div class="min-w-0">
                    <p class="font-body text-sm font-semibold truncate max-w-[200px]">{{ product.name }}</p>
                    <p class="font-body text-xs text-gray-400">
                      Added {{ formatDate(product.createdAt) }}
                    </p>
                  </div>
                </div>
              </td>
              <td class="py-3 px-4">
                <div v-if="product.discountPrice" class="space-y-0.5">
                  <div class="font-pixel text-xs text-cozy-red">{{ formatPrice(product.discountPrice) }} TL</div>
                  <div class="font-body text-xs text-gray-400 line-through">{{ formatPrice(product.price) }} TL</div>
                </div>
                <div v-else>
                  <span class="font-pixel text-xs text-cozy-dark">{{ formatPrice(product.price) }} TL</span>
                </div>
              </td>
              <td class="py-3 px-4">
                <span
                  class="font-body text-sm"
                  :class="product.stockQuantity > 10 ? 'text-cozy-green' :
                          product.stockQuantity > 0 ? 'text-cozy-gold' : 'text-cozy-red'"
                >
                  {{ product.stockQuantity }}
                </span>
              </td>
              <td class="py-3 px-4">
                <span
                  class="pixel-badge text-xs"
                  :class="product.status === 'ACTIVE'
                    ? 'bg-green-50 text-cozy-green border-cozy-green'
                    : product.status === 'OUT_OF_STOCK'
                      ? 'bg-red-50 text-cozy-red border-cozy-red'
                      : 'bg-gray-50 text-gray-500 border-gray-400'"
                >
                  {{ product.status }}
                </span>
              </td>
              <td class="py-3 px-4">
                <div class="flex items-center gap-1">
                  <span class="text-cozy-gold text-sm">&#9733;</span>
                  <span class="font-body text-sm">{{ product.averageRating.toFixed(1) }}</span>
                  <span class="font-body text-xs text-gray-400">({{ product.reviewCount }})</span>
                </div>
              </td>
              <td class="py-3 px-4">
                <span class="font-body text-sm text-gray-600">{{ product.totalSales }}</span>
              </td>
              <td class="py-3 px-4 text-right">
                <div class="flex items-center justify-end gap-2">
                  <router-link
                    :to="`/seller/products/${product.id}/edit`"
                    class="font-pixel text-xs text-primary-500 hover:underline"
                  >
                    Edit
                  </router-link>
                  <button
                    class="font-pixel text-xs text-cozy-red hover:underline"
                    @click="handleDelete(product.id, product.name)"
                  >
                    Delete
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Mobile Cards -->
      <div class="md:hidden space-y-3">
        <div
          v-for="product in products"
          :key="product.id"
          class="pixel-card p-4"
        >
          <div class="flex items-start gap-3 mb-3">
            <div class="w-16 h-16 bg-gray-100 pixel-border overflow-hidden flex-shrink-0">
              <div class="w-full h-full flex items-center justify-center font-pixel text-xl text-gray-300">
                &#128230;
              </div>
            </div>
            <div class="flex-1 min-w-0">
              <p class="font-body text-sm font-semibold truncate">{{ product.name }}</p>
              <div class="flex items-center gap-2 mt-1">
                <span
                  class="pixel-badge text-xs"
                  :class="product.status === 'ACTIVE'
                    ? 'bg-green-50 text-cozy-green border-cozy-green'
                    : 'bg-gray-50 text-gray-500 border-gray-400'"
                >
                  {{ product.status }}
                </span>
                <span class="text-cozy-gold text-xs">&#9733; {{ product.averageRating.toFixed(1) }}</span>
              </div>
            </div>
          </div>

          <div class="grid grid-cols-3 gap-2 text-center mb-3">
            <div>
              <p class="font-pixel text-xs text-primary-600">{{ formatPrice(product.discountPrice ?? product.price) }} TL</p>
              <p class="font-body text-xs text-gray-400">Price</p>
            </div>
            <div>
              <p class="font-pixel text-xs" :class="product.stockQuantity > 0 ? 'text-cozy-green' : 'text-cozy-red'">
                {{ product.stockQuantity }}
              </p>
              <p class="font-body text-xs text-gray-400">Stock</p>
            </div>
            <div>
              <p class="font-pixel text-xs text-gray-600">{{ product.totalSales }}</p>
              <p class="font-body text-xs text-gray-400">Sales</p>
            </div>
          </div>

          <div class="flex gap-2 pt-3 border-t border-gray-200">
            <router-link
              :to="`/seller/products/${product.id}/edit`"
              class="flex-1 text-center pixel-border bg-white font-pixel text-xs py-2 hover:bg-gray-50 transition-colors"
            >
              Edit
            </router-link>
            <button
              class="flex-1 text-center pixel-border bg-red-50 font-pixel text-xs py-2 text-cozy-red hover:bg-red-100 transition-colors"
              @click="handleDelete(product.id, product.name)"
            >
              Delete
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-else class="text-center py-16">
      <div class="font-pixel text-4xl text-gray-300 mb-4">&#128230;</div>
      <h3 class="font-pixel text-sm text-cozy-dark mb-2">No Products Yet</h3>
      <p class="font-retro text-xl text-gray-400 mb-4">
        Your shop is empty! Start adding products to begin your merchant journey.
      </p>
      <router-link
        to="/seller/products/new"
        class="inline-block pixel-border bg-primary-500 text-white font-pixel text-xs py-2 px-6 shadow-pixel hover:bg-primary-600 transition-colors"
      >
        + Add First Product
      </router-link>
    </div>

    <!-- Pagination -->
    <div
      v-if="pagination.totalPages > 1"
      class="flex items-center justify-center gap-2 mt-8"
    >
      <button
        class="pixel-border bg-white font-pixel text-xs py-2 px-3 disabled:opacity-40 hover:bg-gray-50 shadow-pixel"
        :disabled="pagination.page === 0"
        @click="goToPage(pagination.page - 1)"
      >
        &lt; Prev
      </button>

      <button
        v-for="p in Math.min(pagination.totalPages, 5)"
        :key="p - 1"
        class="pixel-border font-pixel text-xs py-2 px-3 shadow-pixel transition-colors"
        :class="(p - 1) === pagination.page
          ? 'bg-primary-500 text-white'
          : 'bg-white hover:bg-gray-50'"
        @click="goToPage(p - 1)"
      >
        {{ p }}
      </button>

      <button
        class="pixel-border bg-white font-pixel text-xs py-2 px-3 disabled:opacity-40 hover:bg-gray-50 shadow-pixel"
        :disabled="pagination.page >= pagination.totalPages - 1"
        @click="goToPage(pagination.page + 1)"
      >
        Next &gt;
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { sellerService } from '@/api/sellerService'
import { useToast } from '@/composables/useToast'
import type { SellerProduct } from '@/types'

const { showToast } = useToast()

const loading = ref(true)
const products = ref<SellerProduct[]>([])
const pagination = reactive({
  page: 0,
  size: 20,
  totalElements: 0,
  totalPages: 0,
})

function formatPrice(price: number): string {
  return price.toLocaleString('tr-TR', { minimumFractionDigits: 2 })
}

function formatDate(dateStr: string): string {
  return new Date(dateStr).toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
  })
}

async function fetchProducts(page: number = 0) {
  loading.value = true
  try {
    const response = await sellerService.getMyProducts(page, pagination.size)
    products.value = response.data.content
    pagination.page = response.data.page
    pagination.totalElements = response.data.totalElements
    pagination.totalPages = response.data.totalPages
  } catch (err: any) {
    showToast('Failed to load products', 'error')
  } finally {
    loading.value = false
  }
}

async function handleDelete(id: string, name: string) {
  if (!confirm(`Are you sure you want to delete "${name}"?`)) return

  try {
    await sellerService.deleteProduct(id)
    products.value = products.value.filter((p) => p.id !== id)
    showToast(`"${name}" deleted successfully`, 'success')
  } catch (err: any) {
    showToast(err.response?.data?.message || 'Failed to delete product', 'error')
  }
}

function goToPage(page: number) {
  fetchProducts(page)
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => {
  fetchProducts()
})
</script>
