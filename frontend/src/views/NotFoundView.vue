<template>
  <div class="page-container flex items-center justify-center min-h-[60vh]">
    <div class="text-center">
      <!-- Pixel art 404 -->
      <div class="font-pixel text-6xl sm:text-8xl text-primary-500 mb-6 animate-bounce-pixel">
        404
      </div>

      <!-- Pixel art characters -->
      <div class="text-4xl mb-4 space-x-2">
        <span>&#9876;</span>
        <span>&#128123;</span>
        <span>&#128737;</span>
      </div>

      <h1 class="font-pixel text-lg text-cozy-dark mb-4">
        Quest Failed!
      </h1>

      <p class="font-retro text-2xl text-gray-500 mb-8 max-w-md mx-auto">
        The page you seek has vanished into the pixel void!
        Perhaps it was consumed by a wandering slime...
      </p>

      <router-link
        to="/"
        class="inline-block pixel-border bg-primary-500 text-white font-pixel text-sm py-3 px-6 hover:bg-primary-600 active:shadow-pixel-hover active:translate-x-0.5 active:translate-y-0.5 transition-all shadow-pixel"
      >
        &#127968; Return to Homepage
      </router-link>

      <!-- Decorative bottom -->
      <div class="mt-12 font-retro text-xl text-gray-300">
        . . . . . . . . . . . . . . . . . . . .
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// No logic needed for 404 page
</script>
