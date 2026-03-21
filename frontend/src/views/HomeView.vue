<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { productApi, categoryApi } from '../services/api'
import ProductCard from '../components/ProductCard.vue'
import type { Product, Category } from '../types'

const products = ref<Product[]>([])
const categories = ref<Category[]>([])
const loading = ref(true)

onMounted(async () => {
  try {
    const [productRes, categoryRes] = await Promise.all([
      productApi.getAll({ page: 0, size: 8 }),
      categoryApi.getAll()
    ])
    products.value = productRes.data.content
    categories.value = categoryRes.data
  } catch (error) {
    console.error('Failed to load data:', error)
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div>
    <!-- Hero Section -->
    <section class="bg-gradient-to-b from-[#3a5c28] to-[#2d1b00] text-white py-16">
      <div class="max-w-7xl mx-auto px-4 text-center">
        <h1 class="text-2xl md:text-3xl font-bold mb-4 text-yellow-300" style="font-family: 'Press Start 2P', cursive;">Welcome to StarDrop Shop</h1>
        <p class="text-sm md:text-base text-green-200 mb-8" style="font-family: 'Press Start 2P', cursive; font-size: 10px;">
          Your one-stop shop for all things Stardew Valley
        </p>
        <router-link
          to="/products"
          class="inline-block pixel-btn px-8 py-3 text-sm"
        >
          Browse All Items
        </router-link>
      </div>
    </section>

    <!-- Categories -->
    <section v-if="categories.length > 0" class="max-w-7xl mx-auto px-4 py-10">
      <h2 class="text-lg font-bold text-yellow-300 mb-6" style="font-family: 'Press Start 2P', cursive;">Categories</h2>
      <div class="flex flex-wrap gap-3">
        <router-link
          v-for="cat in categories"
          :key="cat.id"
          :to="{ path: '/products', query: { categoryId: cat.id } }"
          class="bg-[#f4e4c1] border-2 border-[#5c3a1e] px-5 py-2 text-xs text-[#2d1b00] hover:bg-[#e8a83e] hover:text-[#2d1b00] transition"
          style="font-family: 'Press Start 2P', cursive; font-size: 8px;"
        >
          {{ cat.name }}
        </router-link>
      </div>
    </section>

    <!-- Featured Products -->
    <section class="max-w-7xl mx-auto px-4 py-10">
      <h2 class="text-lg font-bold text-yellow-300 mb-6" style="font-family: 'Press Start 2P', cursive;">Featured Items</h2>

      <div v-if="loading" class="flex justify-center py-12">
        <div class="animate-spin rounded-full h-10 w-10 border-4 border-yellow-500 border-t-transparent"></div>
      </div>

      <div v-else-if="products.length === 0" class="text-center text-yellow-200 py-12" style="font-family: 'Press Start 2P', cursive; font-size: 9px;">
        No items available yet.
      </div>

      <div v-else class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
        <ProductCard v-for="product in products" :key="product.id" :product="product" />
      </div>

      <div class="text-center mt-8">
        <router-link
          to="/products"
          class="inline-block pixel-btn px-6 py-3 text-sm"
        >
          Browse All Items
        </router-link>
      </div>
    </section>
  </div>
</template>
