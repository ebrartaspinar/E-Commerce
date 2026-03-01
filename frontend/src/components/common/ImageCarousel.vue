<template>
  <div class="flex flex-col gap-3">
    <!-- Main Image -->
    <div class="relative border-3 border-cozy-brown/30 bg-white shadow-pixel overflow-hidden">
      <img
        :src="currentImage?.url"
        :alt="currentImage?.altText || 'Product image'"
        class="w-full h-64 sm:h-80 md:h-96 object-contain transition-opacity duration-200"
        :key="currentIndex"
      />

      <!-- Navigation Arrows -->
      <button
        v-if="images.length > 1"
        type="button"
        class="absolute left-2 top-1/2 -translate-y-1/2 w-10 h-10 flex items-center justify-center bg-cozy-cream/90 border-3 border-cozy-brown/40 shadow-pixel text-cozy-brown hover:bg-cozy-peach hover:shadow-pixel-hover hover:translate-y-[calc(-50%+2px)] transition-all font-retro text-2xl"
        @click="prev"
        aria-label="Previous image"
      >
        &lt;
      </button>
      <button
        v-if="images.length > 1"
        type="button"
        class="absolute right-2 top-1/2 -translate-y-1/2 w-10 h-10 flex items-center justify-center bg-cozy-cream/90 border-3 border-cozy-brown/40 shadow-pixel text-cozy-brown hover:bg-cozy-peach hover:shadow-pixel-hover hover:translate-y-[calc(-50%+2px)] transition-all font-retro text-2xl"
        @click="next"
        aria-label="Next image"
      >
        &gt;
      </button>

      <!-- Image counter -->
      <div
        v-if="images.length > 1"
        class="absolute bottom-2 right-2 px-2 py-0.5 bg-cozy-dark/70 text-cozy-cream font-pixel text-[8px]"
      >
        {{ currentIndex + 1 }} / {{ images.length }}
      </div>
    </div>

    <!-- Thumbnails -->
    <div
      v-if="images.length > 1"
      class="flex gap-2 overflow-x-auto pb-1"
    >
      <button
        v-for="(image, index) in images"
        :key="image.id"
        type="button"
        class="shrink-0 w-16 h-16 border-3 transition-all overflow-hidden"
        :class="index === currentIndex
          ? 'border-primary-500 shadow-pixel'
          : 'border-cozy-brown/20 hover:border-cozy-brown/40 opacity-70 hover:opacity-100'"
        @click="currentIndex = index"
        :aria-label="`View image ${index + 1}`"
      >
        <img
          :src="image.url"
          :alt="image.altText || `Thumbnail ${index + 1}`"
          class="w-full h-full object-cover"
          loading="lazy"
        />
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import type { ProductImage } from '@/types'

const props = defineProps<{
  images: ProductImage[]
}>()

const currentIndex = ref(0)

const currentImage = computed(() => {
  return props.images[currentIndex.value] || props.images[0]
})

function prev() {
  currentIndex.value = currentIndex.value > 0
    ? currentIndex.value - 1
    : props.images.length - 1
}

function next() {
  currentIndex.value = currentIndex.value < props.images.length - 1
    ? currentIndex.value + 1
    : 0
}
</script>
