<template>
  <div class="inline-flex items-center">
    <button
      type="button"
      :disabled="modelValue <= min"
      class="w-9 h-9 flex items-center justify-center font-retro text-xl text-cozy-brown bg-cozy-cream border-3 border-cozy-brown/40 hover:bg-cozy-peach hover:border-cozy-brown transition-all disabled:opacity-40 disabled:cursor-not-allowed shadow-pixel hover:shadow-pixel-hover hover:translate-y-[1px] active:translate-y-[2px]"
      @click="decrement"
      aria-label="Decrease quantity"
    >
      -
    </button>
    <div
      class="w-12 h-9 flex items-center justify-center font-retro text-xl text-cozy-dark bg-white border-y-3 border-cozy-brown/40 select-none"
    >
      {{ modelValue }}
    </div>
    <button
      type="button"
      :disabled="modelValue >= max"
      class="w-9 h-9 flex items-center justify-center font-retro text-xl text-cozy-brown bg-cozy-cream border-3 border-cozy-brown/40 hover:bg-cozy-peach hover:border-cozy-brown transition-all disabled:opacity-40 disabled:cursor-not-allowed shadow-pixel hover:shadow-pixel-hover hover:translate-y-[1px] active:translate-y-[2px]"
      @click="increment"
      aria-label="Increase quantity"
    >
      +
    </button>
  </div>
</template>

<script setup lang="ts">
const props = withDefaults(defineProps<{
  modelValue: number
  min?: number
  max?: number
}>(), {
  min: 1,
  max: 10,
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: number): void
}>()

function decrement() {
  if (props.modelValue > props.min) {
    emit('update:modelValue', props.modelValue - 1)
  }
}

function increment() {
  if (props.modelValue < props.max) {
    emit('update:modelValue', props.modelValue + 1)
  }
}
</script>
