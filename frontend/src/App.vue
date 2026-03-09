<template>
  <div class="min-h-screen flex flex-col bg-cozy-cream">
    <AppHeader />
    <CategoryBar />

    <main class="flex-1">
      <router-view v-slot="{ Component }">
        <transition name="page-fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <AppFooter />
    <MobileBottomNav />

    <!-- Toast notifications -->
    <div class="fixed top-4 right-4 z-50 flex flex-col gap-2">
      <transition-group name="toast">
        <div
          v-for="toast in toasts"
          :key="toast.id"
          class="pixel-card min-w-[280px] max-w-sm animate-slide-down"
          :class="{
            'border-cozy-green bg-green-50': toast.type === 'success',
            'border-cozy-red bg-red-50': toast.type === 'error',
            'border-cozy-gold bg-yellow-50': toast.type === 'warning',
            'border-cozy-sky bg-blue-50': toast.type === 'info',
          }"
        >
          <div class="flex items-center gap-2">
            <span class="font-pixel text-xs">
              <template v-if="toast.type === 'success'">&#9733;</template>
              <template v-else-if="toast.type === 'error'">&#10007;</template>
              <template v-else-if="toast.type === 'warning'">&#9888;</template>
              <template v-else>&#8505;</template>
            </span>
            <span class="font-body text-sm flex-1">{{ toast.message }}</span>
            <button
              class="font-pixel text-xs hover:opacity-70"
              @click="removeToast(toast.id)"
            >
              X
            </button>
          </div>
        </div>
      </transition-group>
    </div>
  </div>
</template>

<script setup lang="ts">
import AppHeader from '@/components/layout/AppHeader.vue'
import CategoryBar from '@/components/layout/CategoryBar.vue'
import AppFooter from '@/components/layout/AppFooter.vue'
import MobileBottomNav from '@/components/layout/MobileBottomNav.vue'
import { useToast } from '@/composables/useToast'

const { toasts, removeToast } = useToast()
</script>

<style>
.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity 0.2s ease;
}
.page-fade-enter-from,
.page-fade-leave-to {
  opacity: 0;
}

.toast-enter-active {
  transition: all 0.3s ease-out;
}
.toast-leave-active {
  transition: all 0.2s ease-in;
}
.toast-enter-from {
  opacity: 0;
  transform: translateX(30px);
}
.toast-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>
