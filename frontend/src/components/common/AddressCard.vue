<template>
  <div
    class="bg-cozy-cream border-3 p-4 transition-all"
    :class="[
      selectable ? 'cursor-pointer' : '',
      selected ? 'border-primary-500 shadow-pixel bg-primary-50' : 'border-cozy-brown/30 hover:border-cozy-brown/50',
    ]"
    @click="selectable && $emit('select', address.id)"
  >
    <!-- Header -->
    <div class="flex items-start justify-between mb-2">
      <div class="flex items-center gap-2">
        <h4 class="font-retro text-xl text-cozy-dark font-bold">
          {{ address.title }}
        </h4>
        <span
          v-if="address.isDefault"
          class="font-pixel text-[8px] bg-cozy-green/20 text-cozy-green border-2 border-cozy-green/40 px-1.5 py-0.5"
        >
          DEFAULT
        </span>
      </div>

      <!-- Selection indicator -->
      <div
        v-if="selectable"
        class="w-5 h-5 border-3 flex items-center justify-center shrink-0"
        :class="selected ? 'border-primary-500 bg-primary-500' : 'border-cozy-brown/40 bg-white'"
      >
        <span v-if="selected" class="text-white text-xs font-bold">&check;</span>
      </div>
    </div>

    <!-- Details -->
    <div class="space-y-1">
      <p class="font-body text-sm text-cozy-dark">
        <span class="font-semibold">{{ address.fullName }}</span>
      </p>
      <p class="font-body text-sm text-cozy-brown/70">
        {{ address.phoneNumber }}
      </p>
      <p class="font-body text-sm text-cozy-brown/70 leading-relaxed">
        {{ fullAddressText }}
      </p>
    </div>

    <!-- Actions -->
    <div
      v-if="!selectable"
      class="flex items-center gap-2 mt-3 pt-3 border-t border-cozy-brown/10"
    >
      <button
        type="button"
        class="font-retro text-base text-cozy-brown hover:text-primary-500 transition-colors px-2 py-1 border-2 border-cozy-brown/20 hover:border-primary-400"
        @click.stop="$emit('edit', address.id)"
      >
        Edit
      </button>
      <button
        type="button"
        class="font-retro text-base text-cozy-red hover:text-red-700 transition-colors px-2 py-1 border-2 border-cozy-red/20 hover:border-cozy-red/40"
        @click.stop="$emit('delete', address.id)"
      >
        Delete
      </button>
      <button
        v-if="!address.isDefault"
        type="button"
        class="font-retro text-base text-cozy-green hover:text-green-700 transition-colors px-2 py-1 border-2 border-cozy-green/20 hover:border-cozy-green/40 ml-auto"
        @click.stop="$emit('setDefault', address.id)"
      >
        Set Default
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { Address } from '@/types'

const props = withDefaults(defineProps<{
  address: Address
  selectable?: boolean
  selected?: boolean
}>(), {
  selectable: false,
  selected: false,
})

defineEmits<{
  (e: 'select', id: string): void
  (e: 'edit', id: string): void
  (e: 'delete', id: string): void
  (e: 'setDefault', id: string): void
}>()

const fullAddressText = computed(() => {
  const parts = [
    props.address.fullAddress,
    props.address.neighborhood,
    props.address.district,
    props.address.city,
    props.address.zipCode,
  ].filter(Boolean)
  return parts.join(', ')
})
</script>
