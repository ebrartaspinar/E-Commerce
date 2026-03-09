<template>
  <div
    class="inline-flex items-center gap-0.5"
    :class="{ 'cursor-pointer': !readonly }"
    role="group"
    :aria-label="`Rating: ${currentRating} out of ${maxStars} stars`"
  >
    <button
      v-for="star in maxStars"
      :key="star"
      type="button"
      :class="[sizeClass, starColor(star)]"
      :disabled="readonly"
      class="transition-colors duration-100 leading-none disabled:cursor-default"
      @click="!readonly && setRating(star)"
      @mouseenter="!readonly && (hoverRating = star)"
      @mouseleave="!readonly && (hoverRating = 0)"
      :aria-label="`${star} star${star > 1 ? 's' : ''}`"
    >
      {{ displayStar(star) }}
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

const props = withDefaults(defineProps<{
  rating?: number
  maxStars?: number
  readonly?: boolean
  size?: 'sm' | 'md' | 'lg'
}>(), {
  rating: 0,
  maxStars: 5,
  readonly: true,
  size: 'md',
})

const emit = defineEmits<{
  (e: 'update:rating', value: number): void
}>()

const hoverRating = ref(0)

const currentRating = computed(() => {
  return hoverRating.value > 0 ? hoverRating.value : props.rating
})

const sizeClass = computed(() => {
  switch (props.size) {
    case 'sm': return 'text-sm'
    case 'md': return 'text-lg'
    case 'lg': return 'text-2xl'
    default: return 'text-lg'
  }
})

function starColor(star: number): string {
  return star <= currentRating.value ? 'text-cozy-gold' : 'text-cozy-brown/20'
}

function displayStar(star: number): string {
  return star <= currentRating.value ? '\u2605' : '\u2606'
}

function setRating(star: number) {
  emit('update:rating', star)
}
</script>
