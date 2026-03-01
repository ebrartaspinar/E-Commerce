<template>
  <Teleport to="body">
    <Transition name="modal">
      <div
        v-if="show"
        class="fixed inset-0 z-[90] flex items-center justify-center p-4"
      >
        <!-- Overlay -->
        <div
          class="absolute inset-0 bg-cozy-dark/60"
          @click="handleCancel"
        ></div>

        <!-- Dialog -->
        <div
          class="relative bg-cozy-cream border-3 border-cozy-brown shadow-pixel-lg max-w-md w-full animate-slide-up"
          role="dialog"
          aria-modal="true"
          :aria-labelledby="titleId"
        >
          <!-- Header -->
          <div class="px-5 pt-5 pb-2">
            <h3
              :id="titleId"
              class="font-pixel text-sm text-cozy-dark"
            >
              {{ title }}
            </h3>
          </div>

          <!-- Body -->
          <div class="px-5 pb-4">
            <p class="font-retro text-xl text-cozy-brown/80">
              {{ message }}
            </p>
          </div>

          <!-- Footer -->
          <div class="flex items-center justify-end gap-3 px-5 py-4 border-t-3 border-cozy-brown/10 bg-cozy-peach/20">
            <button
              type="button"
              class="font-retro text-lg text-cozy-brown px-5 py-2 border-3 border-cozy-brown/40 bg-cozy-cream shadow-pixel hover:shadow-pixel-hover hover:translate-y-[2px] hover:bg-white transition-all"
              @click="handleCancel"
            >
              {{ cancelText }}
            </button>
            <button
              type="button"
              class="font-retro text-lg text-white px-5 py-2 border-3 border-cozy-brown shadow-pixel hover:shadow-pixel-hover hover:translate-y-[2px] transition-all"
              :class="confirmVariantClass"
              @click="handleConfirm"
            >
              {{ confirmText }}
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { computed, watch } from 'vue'

const props = withDefaults(defineProps<{
  show: boolean
  title: string
  message: string
  confirmText?: string
  cancelText?: string
  variant?: 'primary' | 'danger'
}>(), {
  confirmText: 'Confirm',
  cancelText: 'Cancel',
  variant: 'primary',
})

const emit = defineEmits<{
  (e: 'confirm'): void
  (e: 'cancel'): void
  (e: 'update:show', value: boolean): void
}>()

const titleId = computed(() => `confirm-dialog-title-${Math.random().toString(36).slice(2, 8)}`)

const confirmVariantClass = computed(() => {
  return props.variant === 'danger'
    ? 'bg-cozy-red hover:bg-red-600'
    : 'bg-primary-500 hover:bg-primary-600'
})

function handleConfirm() {
  emit('confirm')
  emit('update:show', false)
}

function handleCancel() {
  emit('cancel')
  emit('update:show', false)
}

watch(() => props.show, (newVal) => {
  if (newVal) {
    document.body.style.overflow = 'hidden'
  } else {
    document.body.style.overflow = ''
  }
})
</script>

<style scoped>
.modal-enter-active {
  transition: opacity 0.2s ease;
}
.modal-leave-active {
  transition: opacity 0.15s ease;
}
.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
</style>
