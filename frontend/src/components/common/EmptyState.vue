<template>
  <div class="flex flex-col items-center justify-center py-12 px-4 text-center">
    <!-- Icon -->
    <div class="text-6xl mb-4 opacity-60" aria-hidden="true">
      {{ icon }}
    </div>

    <!-- Title -->
    <h3 class="font-pixel text-sm text-cozy-brown mb-2">
      {{ title }}
    </h3>

    <!-- Message -->
    <p class="font-retro text-xl text-cozy-brown/60 max-w-sm mb-6">
      {{ message }}
    </p>

    <!-- Action Button -->
    <router-link
      v-if="actionText && actionRoute"
      :to="actionRoute"
      class="font-retro text-lg bg-primary-500 text-white px-6 py-2 border-3 border-cozy-brown shadow-pixel hover:shadow-pixel-hover hover:translate-y-[2px] transition-all"
    >
      {{ actionText }}
    </router-link>

    <!-- Slot for custom actions -->
    <slot />
  </div>
</template>

<script setup lang="ts">
withDefaults(defineProps<{
  title: string
  message: string
  icon?: string
  actionText?: string
  actionRoute?: string
}>(), {
  icon: '\uD83D\uDCE6',
})
</script>
