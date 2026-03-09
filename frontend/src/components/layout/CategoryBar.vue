<template>
  <nav class="bg-gradient-to-r from-warm-50 to-cozy-peach/50 border-b-3 border-cozy-brown/20">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div
        class="flex items-center gap-2 py-2 overflow-x-auto scrollbar-hide"
        ref="scrollContainer"
      >
        <router-link
          v-for="category in categories"
          :key="category.id"
          :to="{ path: '/products', query: { categoryId: category.id } }"
          class="shrink-0 px-4 py-1.5 font-retro text-lg text-cozy-brown bg-cozy-cream border-3 border-cozy-brown/30 shadow-pixel hover:shadow-pixel-hover hover:translate-y-[2px] hover:bg-primary-100 hover:border-primary-400 transition-all whitespace-nowrap"
          active-class="bg-primary-100 border-primary-500 text-primary-700"
        >
          <span v-if="category.iconUrl" class="mr-1">{{ category.iconUrl }}</span>
          {{ category.name }}
        </router-link>

        <div
          v-if="categories.length === 0"
          class="flex gap-2"
        >
          <div
            v-for="n in 8"
            :key="n"
            class="shrink-0 w-24 h-8 bg-cozy-peach/50 animate-pulse border-3 border-cozy-brown/10"
          ></div>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { Category } from '@/types'

const props = withDefaults(defineProps<{
  categories?: Category[]
}>(), {
  categories: () => [],
})

const scrollContainer = ref<HTMLElement | null>(null)
</script>

<style scoped>
.scrollbar-hide {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
.scrollbar-hide::-webkit-scrollbar {
  display: none;
}
</style>
