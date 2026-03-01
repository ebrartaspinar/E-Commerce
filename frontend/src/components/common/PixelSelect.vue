<template>
  <div class="flex flex-col gap-1">
    <!-- Label -->
    <label
      v-if="label"
      :for="selectId"
      class="font-retro text-lg text-cozy-brown"
    >
      {{ label }}
      <span v-if="required" class="text-cozy-red">*</span>
    </label>

    <!-- Select -->
    <div class="relative">
      <select
        :id="selectId"
        :value="modelValue"
        :required="required"
        :disabled="disabled"
        class="w-full px-4 py-2 font-body text-base text-cozy-dark bg-white border-3 focus:outline-none transition-colors shadow-pixel-inset appearance-none pr-10 cursor-pointer"
        :class="selectClasses"
        @change="onChange"
      >
        <option
          v-if="placeholder"
          value=""
          disabled
          :selected="!modelValue"
        >
          {{ placeholder }}
        </option>
        <option
          v-for="option in options"
          :key="option.value"
          :value="option.value"
        >
          {{ option.label }}
        </option>
      </select>

      <!-- Custom arrow -->
      <div class="absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none">
        <svg class="w-4 h-4 text-cozy-brown/60" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M19 9l-7 7-7-7" />
        </svg>
      </div>
    </div>

    <!-- Error -->
    <p
      v-if="error"
      class="font-retro text-base text-cozy-red mt-0.5"
    >
      {{ error }}
    </p>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface SelectOption {
  value: string | number
  label: string
}

const props = withDefaults(defineProps<{
  modelValue: string | number | null
  label?: string
  options: SelectOption[]
  error?: string
  placeholder?: string
  required?: boolean
  disabled?: boolean
}>(), {
  placeholder: '',
  required: false,
  disabled: false,
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
}>()

const selectId = computed(() => `pixel-select-${Math.random().toString(36).slice(2, 8)}`)

const selectClasses = computed(() => {
  const classes: string[] = []

  if (props.error) {
    classes.push('border-cozy-red focus:border-cozy-red bg-cozy-red/5')
  } else {
    classes.push('border-cozy-brown/40 focus:border-primary-500')
  }

  if (props.disabled) {
    classes.push('opacity-50 cursor-not-allowed bg-cozy-peach/20')
  }

  return classes.join(' ')
})

function onChange(event: Event) {
  emit('update:modelValue', (event.target as HTMLSelectElement).value)
}
</script>
