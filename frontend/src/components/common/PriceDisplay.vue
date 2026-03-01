<template>
  <div class="flex items-baseline gap-2 flex-wrap">
    <!-- Discount Price (main) -->
    <span
      v-if="hasDiscount"
      class="font-body text-lg font-bold text-cozy-red"
    >
      {{ formatPrice(discountPrice!) }}
    </span>

    <!-- Original Price -->
    <span
      :class="hasDiscount
        ? 'font-body text-sm text-cozy-brown/50 line-through'
        : 'font-body text-lg font-bold text-cozy-dark'"
    >
      {{ formatPrice(price) }}
    </span>

    <!-- Discount Percentage Badge -->
    <span
      v-if="hasDiscount && showBadge"
      class="font-pixel text-[9px] bg-cozy-red/10 text-cozy-red px-1.5 py-0.5 border border-cozy-red/30"
    >
      -{{ discountPercentage }}%
    </span>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(defineProps<{
  price: number
  discountPrice?: number
  currency?: string
  showBadge?: boolean
}>(), {
  currency: 'TL',
  showBadge: true,
})

const hasDiscount = computed(() => {
  return props.discountPrice != null && props.discountPrice < props.price
})

const discountPercentage = computed(() => {
  if (!hasDiscount.value) return 0
  return Math.round(((props.price - props.discountPrice!) / props.price) * 100)
})

function formatPrice(value: number): string {
  return `${value.toLocaleString('tr-TR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })} ${props.currency}`
}
</script>
