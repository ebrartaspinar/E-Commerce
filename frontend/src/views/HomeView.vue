<template>
  <div>
    <!-- Hero Banner -->
    <section class="bg-gradient-to-br from-primary-500 via-primary-600 to-warm-700 text-white py-12 sm:py-20">
      <div class="page-container text-center">
        <div class="font-pixel text-3xl mb-4 animate-bounce-pixel">&#9733; &#9876; &#9733;</div>
        <h1 class="font-pixel text-xl sm:text-2xl md:text-3xl mb-4 leading-relaxed">
          Welcome to TrendyolClone
        </h1>
        <p class="font-retro text-2xl sm:text-3xl text-primary-100 mb-8 max-w-2xl mx-auto">
          Your quest for the best deals starts here! Explore legendary items, rare finds, and epic discounts.
        </p>
        <router-link
          to="/products"
          class="inline-block pixel-border border-white bg-white text-primary-600 font-pixel text-sm py-3 px-8 hover:bg-primary-50 active:translate-x-0.5 active:translate-y-0.5 transition-all shadow-pixel"
        >
          &#128270; Begin Your Quest
        </router-link>
      </div>
    </section>

    <!-- Featured Categories -->
    <section class="page-container">
      <div class="flex items-center gap-3 mb-6">
        <span class="font-pixel text-sm text-primary-500">&#9878;</span>
        <h2 class="section-title mb-0">Explore Realms</h2>
      </div>

      <div v-if="categoriesLoading" class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-5 gap-4">
        <div v-for="i in 10" :key="i" class="pixel-card animate-pulse h-24">
          <div class="bg-gray-200 h-8 w-8 mx-auto mb-2 rounded"></div>
          <div class="bg-gray-200 h-3 w-20 mx-auto rounded"></div>
        </div>
      </div>

      <div v-else class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-5 gap-4">
        <router-link
          v-for="category in displayCategories"
          :key="category.id"
          :to="`/products?categoryId=${category.id}`"
          class="pixel-card text-center hover:shadow-pixel-lg hover:-translate-y-1 transition-all group"
        >
          <div class="text-3xl mb-2 group-hover:animate-bounce-pixel">
            {{ getCategoryIcon(category.name) }}
          </div>
          <p class="font-pixel text-xs text-cozy-dark truncate">{{ category.name }}</p>
          <p class="font-retro text-sm text-gray-400 mt-1">{{ category.productCount }} items</p>
        </router-link>
      </div>
    </section>

    <!-- Popular Products -->
    <section class="page-container">
      <div class="flex items-center justify-between mb-6">
        <div class="flex items-center gap-3">
          <span class="font-pixel text-sm text-cozy-gold">&#9733;</span>
          <h2 class="section-title mb-0">Popular Products</h2>
        </div>
        <router-link
          to="/products?sortBy=averageRating&sortDir=desc"
          class="font-pixel text-xs text-primary-500 hover:underline"
        >
          View All &gt;
        </router-link>
      </div>

      <div v-if="productsLoading" class="grid grid-cols-2 md:grid-cols-4 gap-4">
        <div v-for="i in 8" :key="i" class="pixel-card animate-pulse">
          <div class="bg-gray-200 h-40 mb-3"></div>
          <div class="bg-gray-200 h-4 w-3/4 mb-2 rounded"></div>
          <div class="bg-gray-200 h-3 w-1/2 mb-2 rounded"></div>
          <div class="bg-gray-200 h-5 w-1/3 rounded"></div>
        </div>
      </div>

      <div v-else class="grid grid-cols-2 md:grid-cols-4 gap-4">
        <ProductCard
          v-for="product in popularProducts"
          :key="product.id"
          :product="product"
        />
      </div>

      <div v-if="!productsLoading && popularProducts.length === 0" class="text-center py-8">
        <p class="font-retro text-xl text-gray-400">No popular products yet. The marketplace awaits new inventory!</p>
      </div>
    </section>

    <!-- New Arrivals -->
    <section class="bg-white py-8">
      <div class="page-container">
        <div class="flex items-center justify-between mb-6">
          <div class="flex items-center gap-3">
            <span class="font-pixel text-sm text-cozy-green">&#9670;</span>
            <h2 class="section-title mb-0">New Arrivals</h2>
          </div>
          <router-link
            to="/products?sortBy=createdAt&sortDir=desc"
            class="font-pixel text-xs text-primary-500 hover:underline"
          >
            View All &gt;
          </router-link>
        </div>

        <div v-if="productsLoading" class="grid grid-cols-2 md:grid-cols-4 gap-4">
          <div v-for="i in 4" :key="i" class="pixel-card animate-pulse">
            <div class="bg-gray-200 h-40 mb-3"></div>
            <div class="bg-gray-200 h-4 w-3/4 mb-2 rounded"></div>
            <div class="bg-gray-200 h-5 w-1/3 rounded"></div>
          </div>
        </div>

        <div v-else class="grid grid-cols-2 md:grid-cols-4 gap-4">
          <ProductCard
            v-for="product in newArrivals"
            :key="product.id"
            :product="product"
          />
        </div>
      </div>
    </section>

    <!-- Best Deals -->
    <section class="page-container">
      <div class="flex items-center justify-between mb-6">
        <div class="flex items-center gap-3">
          <span class="font-pixel text-sm text-cozy-red">&#10084;</span>
          <h2 class="section-title mb-0">Best Deals</h2>
        </div>
        <router-link
          to="/products?sortBy=discountPrice&sortDir=asc"
          class="font-pixel text-xs text-primary-500 hover:underline"
        >
          View All &gt;
        </router-link>
      </div>

      <div v-if="productsLoading" class="grid grid-cols-2 md:grid-cols-4 gap-4">
        <div v-for="i in 4" :key="i" class="pixel-card animate-pulse">
          <div class="bg-gray-200 h-40 mb-3"></div>
          <div class="bg-gray-200 h-4 w-3/4 mb-2 rounded"></div>
          <div class="bg-gray-200 h-5 w-1/3 rounded"></div>
        </div>
      </div>

      <div v-else class="grid grid-cols-2 md:grid-cols-4 gap-4">
        <ProductCard
          v-for="product in bestDeals"
          :key="product.id"
          :product="product"
        />
      </div>

      <div v-if="!productsLoading && bestDeals.length === 0" class="text-center py-8">
        <p class="font-retro text-xl text-gray-400">No deals at the moment. Check back soon, brave shopper!</p>
      </div>
    </section>

    <!-- Bottom CTA -->
    <section class="bg-secondary-700 text-white py-12 mt-8">
      <div class="page-container text-center">
        <h2 class="font-pixel text-lg mb-4">Ready to Start Selling?</h2>
        <p class="font-retro text-2xl text-secondary-200 mb-6">
          Join our merchant guild and reach thousands of adventurers!
        </p>
        <router-link
          to="/register"
          class="inline-block pixel-border border-white bg-white text-secondary-700 font-pixel text-sm py-3 px-8 hover:bg-secondary-50 active:translate-x-0.5 active:translate-y-0.5 transition-all shadow-pixel"
        >
          &#9878; Become a Seller
        </router-link>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useProductStore } from '@/stores/product'
import ProductCard from '@/components/common/ProductCard.vue'
import type { Product } from '@/types'
import { productService } from '@/api/productService'

const productStore = useProductStore()

const productsLoading = ref(true)
const categoriesLoading = ref(true)
const allProducts = ref<Product[]>([])

const displayCategories = computed(() => {
  return productStore.categories.slice(0, 10)
})

const popularProducts = computed(() => {
  return [...allProducts.value]
    .sort((a, b) => b.averageRating - a.averageRating)
    .slice(0, 8)
})

const newArrivals = computed(() => {
  return [...allProducts.value]
    .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
    .slice(0, 4)
})

const bestDeals = computed(() => {
  return allProducts.value
    .filter((p) => p.discountPrice !== null && p.discountPrice < p.price)
    .sort((a, b) => {
      const discountA = a.discountPrice ? (a.price - a.discountPrice) / a.price : 0
      const discountB = b.discountPrice ? (b.price - b.discountPrice) / b.price : 0
      return discountB - discountA
    })
    .slice(0, 4)
})

const categoryIcons: Record<string, string> = {
  'Electronics': '&#128187;',
  'Fashion': '&#128085;',
  'Home': '&#127968;',
  'Books': '&#128214;',
  'Sports': '&#9917;',
  'Beauty': '&#128132;',
  'Toys': '&#127922;',
  'Automotive': '&#128663;',
  'Food': '&#127828;',
  'Health': '&#128138;',
}

function getCategoryIcon(name: string): string {
  for (const key of Object.keys(categoryIcons)) {
    if (name.toLowerCase().includes(key.toLowerCase())) {
      return categoryIcons[key]!
    }
  }
  return '&#128230;'
}

onMounted(async () => {
  try {
    await productStore.fetchCategories()
  } catch {
    // Categories failed to load, non-critical
  } finally {
    categoriesLoading.value = false
  }

  try {
    const response = await productService.getProducts({ page: 0, size: 20 })
    allProducts.value = response.data.content
  } catch {
    // Products failed to load
  } finally {
    productsLoading.value = false
  }
})
</script>
