<script setup lang="ts">
import { useNotificationStore } from '../stores/notification'

const notification = useNotificationStore()
</script>

<template>
  <div class="fixed top-4 right-4 z-50 space-y-2">
    <transition-group name="toast">
      <div
        v-for="msg in notification.messages"
        :key="msg.id"
        :class="[
          'px-4 py-3 rounded-lg shadow-lg text-white text-sm max-w-sm cursor-pointer',
          msg.type === 'success' ? 'bg-green-500' : 'bg-red-500'
        ]"
        @click="notification.remove(msg.id)"
      >
        {{ msg.text }}
      </div>
    </transition-group>
  </div>
</template>

<style scoped>
.toast-enter-active {
  transition: all 0.3s ease;
}
.toast-leave-active {
  transition: all 0.3s ease;
}
.toast-enter-from {
  opacity: 0;
  transform: translateX(100px);
}
.toast-leave-to {
  opacity: 0;
  transform: translateX(100px);
}
</style>
