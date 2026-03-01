<template>
  <span
    :class="[colorClasses, sizeClass]"
    class="inline-flex items-center font-pixel border-3 whitespace-nowrap"
  >
    <span class="mr-1" aria-hidden="true">{{ icon }}</span>
    {{ displayText }}
  </span>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(defineProps<{
  status: string
  type?: 'order' | 'product' | 'payment'
  size?: 'sm' | 'md'
}>(), {
  type: 'order',
  size: 'sm',
})

const sizeClass = computed(() => {
  return props.size === 'sm' ? 'text-[8px] px-2 py-0.5' : 'text-[10px] px-3 py-1'
})

const statusConfig = computed(() => {
  const configs: Record<string, Record<string, { bg: string; text: string; border: string; icon: string }>> = {
    order: {
      PENDING: { bg: 'bg-cozy-gold/20', text: 'text-cozy-gold', border: 'border-cozy-gold/40', icon: '\u23F3' },
      CONFIRMED: { bg: 'bg-cozy-sky/20', text: 'text-blue-600', border: 'border-cozy-sky/40', icon: '\u2714' },
      PROCESSING: { bg: 'bg-primary-100', text: 'text-primary-700', border: 'border-primary-300', icon: '\u2699' },
      SHIPPED: { bg: 'bg-secondary-100', text: 'text-secondary-700', border: 'border-secondary-300', icon: '\uD83D\uDCE6' },
      DELIVERED: { bg: 'bg-cozy-green/20', text: 'text-cozy-green', border: 'border-cozy-green/40', icon: '\u2705' },
      CANCELLED: { bg: 'bg-cozy-red/20', text: 'text-cozy-red', border: 'border-cozy-red/40', icon: '\u2716' },
      REFUNDED: { bg: 'bg-purple-100', text: 'text-purple-700', border: 'border-purple-300', icon: '\u21A9' },
    },
    product: {
      ACTIVE: { bg: 'bg-cozy-green/20', text: 'text-cozy-green', border: 'border-cozy-green/40', icon: '\u2714' },
      INACTIVE: { bg: 'bg-cozy-brown/10', text: 'text-cozy-brown/60', border: 'border-cozy-brown/20', icon: '\u25CB' },
      OUT_OF_STOCK: { bg: 'bg-cozy-red/20', text: 'text-cozy-red', border: 'border-cozy-red/40', icon: '\u2716' },
    },
    payment: {
      PENDING: { bg: 'bg-cozy-gold/20', text: 'text-cozy-gold', border: 'border-cozy-gold/40', icon: '\u23F3' },
      PROCESSING: { bg: 'bg-primary-100', text: 'text-primary-700', border: 'border-primary-300', icon: '\u2699' },
      COMPLETED: { bg: 'bg-cozy-green/20', text: 'text-cozy-green', border: 'border-cozy-green/40', icon: '\u2714' },
      FAILED: { bg: 'bg-cozy-red/20', text: 'text-cozy-red', border: 'border-cozy-red/40', icon: '\u2716' },
      REFUNDED: { bg: 'bg-purple-100', text: 'text-purple-700', border: 'border-purple-300', icon: '\u21A9' },
    },
  }

  const typeConfigs = configs[props.type] ?? configs.order
  return typeConfigs?.[props.status] ?? { bg: 'bg-gray-100', text: 'text-gray-600', border: 'border-gray-300', icon: '\u25CF' }
})

const colorClasses = computed(() => {
  const config = statusConfig.value
  return `${config.bg} ${config.text} ${config.border}`
})

const icon = computed(() => statusConfig.value.icon)

const displayText = computed(() => {
  return props.status.replace(/_/g, ' ')
})
</script>
