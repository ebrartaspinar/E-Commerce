<template>
  <div class="flex flex-col gap-1">
    <!-- Label -->
    <label
      v-if="label"
      :for="inputId"
      class="font-retro text-lg text-cozy-brown"
    >
      {{ label }}
      <span v-if="required" class="text-cozy-red">*</span>
    </label>

    <!-- Input -->
    <div class="relative">
      <input
        :id="inputId"
        :type="computedType"
        :value="modelValue"
        :placeholder="placeholder"
        :required="required"
        :disabled="disabled"
        :autocomplete="autocomplete"
        class="w-full px-4 py-2 font-body text-base text-cozy-dark bg-white border-3 focus:outline-none transition-colors shadow-pixel-inset"
        :class="inputClasses"
        @input="onInput"
        @blur="$emit('blur', $event)"
        @focus="$emit('focus', $event)"
      />

      <!-- Password toggle -->
      <button
        v-if="type === 'password'"
        type="button"
        class="absolute right-3 top-1/2 -translate-y-1/2 text-cozy-brown/40 hover:text-cozy-brown transition-colors font-retro text-sm"
        @click="showPassword = !showPassword"
        tabindex="-1"
        aria-label="Toggle password visibility"
      >
        {{ showPassword ? 'HIDE' : 'SHOW' }}
      </button>
    </div>

    <!-- Error -->
    <p
      v-if="error"
      class="font-retro text-base text-cozy-red mt-0.5"
    >
      {{ error }}
    </p>

    <!-- Help text -->
    <p
      v-if="helpText && !error"
      class="font-retro text-base text-cozy-brown/50 mt-0.5"
    >
      {{ helpText }}
    </p>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

const props = withDefaults(defineProps<{
  modelValue: string | number
  label?: string
  type?: string
  placeholder?: string
  error?: string
  helpText?: string
  required?: boolean
  disabled?: boolean
  autocomplete?: string
}>(), {
  type: 'text',
  placeholder: '',
  required: false,
  disabled: false,
  autocomplete: 'off',
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
  (e: 'blur', event: FocusEvent): void
  (e: 'focus', event: FocusEvent): void
}>()

const showPassword = ref(false)

const inputId = computed(() => `pixel-input-${Math.random().toString(36).slice(2, 8)}`)

const computedType = computed(() => {
  if (props.type === 'password') {
    return showPassword.value ? 'text' : 'password'
  }
  return props.type
})

const inputClasses = computed(() => {
  const classes: string[] = []

  if (props.error) {
    classes.push('border-cozy-red focus:border-cozy-red bg-cozy-red/5')
  } else {
    classes.push('border-cozy-brown/40 focus:border-primary-500')
  }

  if (props.disabled) {
    classes.push('opacity-50 cursor-not-allowed bg-cozy-peach/20')
  }

  if (props.type === 'password') {
    classes.push('pr-16')
  }

  return classes.join(' ')
})

function onInput(event: Event) {
  emit('update:modelValue', (event.target as HTMLInputElement).value)
}
</script>
