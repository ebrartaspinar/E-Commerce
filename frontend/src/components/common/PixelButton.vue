<template>
  <button
    :type="type"
    :disabled="disabled || loading"
    :class="[baseClasses, variantClasses, sizeClasses, { 'opacity-50 cursor-not-allowed': disabled, 'cursor-wait': loading }]"
    class="font-retro inline-flex items-center justify-center border-3 transition-all duration-150 active:translate-y-[2px] active:shadow-pixel-hover"
    @click="$emit('click', $event)"
  >
    <!-- Loading spinner -->
    <svg
      v-if="loading"
      class="animate-spin mr-2"
      :class="spinnerSizeClass"
      fill="none"
      viewBox="0 0 24 24"
    >
      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" />
      <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
    </svg>

    <slot />
  </button>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(defineProps<{
  variant?: 'primary' | 'secondary' | 'danger' | 'ghost'
  size?: 'sm' | 'md' | 'lg'
  loading?: boolean
  disabled?: boolean
  type?: 'button' | 'submit' | 'reset'
}>(), {
  variant: 'primary',
  size: 'md',
  loading: false,
  disabled: false,
  type: 'button',
})

defineEmits<{
  (e: 'click', event: MouseEvent): void
}>()

const baseClasses = 'shadow-pixel hover:shadow-pixel-hover hover:translate-y-[2px]'

const variantClasses = computed(() => {
  switch (props.variant) {
    case 'primary':
      return 'bg-primary-500 text-white border-cozy-brown hover:bg-primary-600'
    case 'secondary':
      return 'bg-secondary-500 text-white border-cozy-brown hover:bg-secondary-600'
    case 'danger':
      return 'bg-cozy-red text-white border-cozy-brown hover:bg-red-600'
    case 'ghost':
      return 'bg-transparent text-cozy-brown border-cozy-brown/40 hover:bg-cozy-peach hover:border-cozy-brown'
    default:
      return ''
  }
})

const sizeClasses = computed(() => {
  switch (props.size) {
    case 'sm':
      return 'px-3 py-1 text-base'
    case 'md':
      return 'px-5 py-2 text-lg'
    case 'lg':
      return 'px-7 py-3 text-xl'
    default:
      return ''
  }
})

const spinnerSizeClass = computed(() => {
  switch (props.size) {
    case 'sm': return 'w-3 h-3'
    case 'md': return 'w-4 h-4'
    case 'lg': return 'w-5 h-5'
    default: return 'w-4 h-4'
  }
})
</script>
