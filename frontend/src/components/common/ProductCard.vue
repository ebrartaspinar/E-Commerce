<template>
  <div
    class="bg-cozy-cream border-3 border-cozy-brown/30 shadow-pixel hover:shadow-pixel-lg hover:-translate-y-1 transition-all duration-200 flex flex-col cursor-pointer group"
    @click="navigateToProduct"
  >
    <!-- Image -->
    <div class="relative overflow-hidden border-b-3 border-cozy-brown/20">
      <img
        :src="primaryImage"
        :alt="product.name"
        class="w-full h-48 object-cover group-hover:scale-105 transition-transform duration-300"
        loading="lazy"
      />
      <!-- Discount badge -->
      <span
        v-if="discountPercentage > 0"
        class="absolute top-2 left-2 bg-cozy-red text-white font-pixel text-[9px] px-2 py-1 border-2 border-cozy-dark/30"
      >
        -{{ discountPercentage }}%
      </span>
      <!-- Out of stock overlay -->
      <div
        v-if="product.status === 'OUT_OF_STOCK'"
        class="absolute inset-0 bg-cozy-dark/60 flex items-center justify-center"
      >
        <span class="font-pixel text-xs text-cozy-cream bg-cozy-dark/80 px-3 py-1 border-2 border-cozy-cream/40">
          OUT OF STOCK
        </span>
      </div>
    </div>

    <!-- Content -->
    <div class="flex flex-col flex-1 p-3">
      <!-- Seller -->
      <span class="font-retro text-sm text-cozy-brown/60 mb-1">
        {{ product.sellerName }}
      </span>

      <!-- Name -->
      <h3 class="font-body text-sm text-cozy-dark font-semibold leading-tight line-clamp-2 mb-2">
        {{ product.name }}
      </h3>

      <!-- Rating -->
      <div class="flex items-center gap-1 mb-2">
        <RatingStars :rating="product.averageRating" size="sm" readonly />
        <span class="font-retro text-sm text-cozy-brown/60">
          ({{ product.reviewCount }})
        </span>
      </div>

      <!-- Price -->
      <div class="mt-auto">
        <PriceDisplay
          :price="product.price"
          :discount-price="product.discountPrice ?? undefined"
        />
      </div>

      <!-- Add to Cart -->
      <PixelButton
        v-if="product.status !== 'OUT_OF_STOCK'"
        variant="primary"
        size="sm"
        class="w-full mt-3"
        @click.stop="handleAddToCart"
        :loading="addingToCart"
      >
        Add to Cart
      </PixelButton>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import type { Product } from '@/types'
import RatingStars from './RatingStars.vue'
import PriceDisplay from './PriceDisplay.vue'
import PixelButton from './PixelButton.vue'

const props = defineProps<{
  product: Product
}>()

const emit = defineEmits<{
  (e: 'addToCart', productId: string): void
}>()

const router = useRouter()
const addingToCart = ref(false)

const primaryImage = computed(() => {
  const primary = props.product.images.find(img => img.isPrimary)
  return primary?.url ?? props.product.images[0]?.url ?? '/placeholder.png'
})

const discountPercentage = computed(() => {
  if (!props.product.discountPrice || props.product.discountPrice >= props.product.price) return 0
  return Math.round(((props.product.price - props.product.discountPrice) / props.product.price) * 100)
})

function navigateToProduct() {
  router.push(`/products/${props.product.slug}`)
}

async function handleAddToCart() {
  addingToCart.value = true
  emit('addToCart', props.product.id)
  setTimeout(() => {
    addingToCart.value = false
  }, 500)
}
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
