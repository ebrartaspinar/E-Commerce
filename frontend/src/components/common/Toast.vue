<template>
  <Teleport to="body">
    <div class="fixed top-4 right-4 z-[100] flex flex-col gap-2 max-w-sm w-full pointer-events-none">
      <TransitionGroup name="toast">
        <div
          v-for="toast in toasts"
          :key="toast.id"
          class="pointer-events-auto border-3 shadow-pixel-lg animate-slide-up"
          :class="toastClasses(toast.type)"
        >
          <div class="flex items-start gap-3 p-3">
            <!-- Icon -->
            <span class="text-lg shrink-0 mt-0.5" aria-hidden="true">
              {{ toastIcon(toast.type) }}
            </span>

            <!-- Content -->
            <div class="flex-1 min-w-0">
              <p
                v-if="toast.title"
                class="font-retro text-lg font-bold leading-tight"
              >
                {{ toast.title }}
              </p>
              <p class="font-body text-sm" :class="toast.title ? 'mt-0.5 opacity-80' : 'font-retro text-lg font-bold leading-tight'">
                {{ toast.message }}
              </p>
            </div>

            <!-- Close button -->
            <button
              type="button"
              class="shrink-0 opacity-60 hover:opacity-100 transition-opacity font-retro text-xl leading-none"
              @click="removeToast(toast.id)"
              aria-label="Dismiss"
            >
              &times;
            </button>
          </div>
        </div>
      </TransitionGroup>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { useToast } from '@/composables/useToast'

const { toasts, removeToast } = useToast()

function toastClasses(type: string): string {
  switch (type) {
    case 'success':
      return 'bg-cozy-green/10 border-cozy-green text-cozy-green'
    case 'error':
      return 'bg-cozy-red/10 border-cozy-red text-cozy-red'
    case 'warning':
      return 'bg-cozy-gold/10 border-cozy-gold text-yellow-800'
    case 'info':
      return 'bg-cozy-sky/10 border-cozy-sky text-blue-800'
    default:
      return 'bg-cozy-cream border-cozy-brown text-cozy-dark'
  }
}

function toastIcon(type: string): string {
  switch (type) {
    case 'success': return '\u2714'
    case 'error': return '\u2716'
    case 'warning': return '\u26A0'
    case 'info': return '\u2139'
    default: return '\u25CF'
  }
}
</script>

<style scoped>
.toast-enter-active {
  transition: all 0.3s ease-out;
}
.toast-leave-active {
  transition: all 0.2s ease-in;
}
.toast-enter-from {
  opacity: 0;
  transform: translateX(100%);
}
.toast-leave-to {
  opacity: 0;
  transform: translateX(100%);
}
.toast-move {
  transition: transform 0.3s ease;
}
</style>
