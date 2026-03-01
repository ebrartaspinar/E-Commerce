<template>
  <div>
    <!-- Card skeleton -->
    <template v-if="type === 'card'">
      <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
        <div
          v-for="n in count"
          :key="n"
          class="bg-cozy-cream border-3 border-cozy-brown/10 overflow-hidden"
        >
          <div class="w-full h-48 bg-cozy-peach/50 animate-pulse"></div>
          <div class="p-3 space-y-2">
            <div class="h-3 bg-cozy-peach/50 animate-pulse w-1/3"></div>
            <div class="h-4 bg-cozy-peach/60 animate-pulse w-full"></div>
            <div class="h-4 bg-cozy-peach/60 animate-pulse w-3/4"></div>
            <div class="flex gap-1 mt-2">
              <div v-for="s in 5" :key="s" class="h-4 w-4 bg-cozy-peach/40 animate-pulse"></div>
            </div>
            <div class="h-5 bg-cozy-peach/50 animate-pulse w-1/2 mt-2"></div>
            <div class="h-8 bg-cozy-peach/40 animate-pulse w-full mt-3"></div>
          </div>
        </div>
      </div>
    </template>

    <!-- List skeleton -->
    <template v-else-if="type === 'list'">
      <div class="space-y-3">
        <div
          v-for="n in count"
          :key="n"
          class="flex gap-4 p-4 bg-cozy-cream border-3 border-cozy-brown/10"
        >
          <div class="w-20 h-20 bg-cozy-peach/50 animate-pulse shrink-0"></div>
          <div class="flex-1 space-y-2">
            <div class="h-4 bg-cozy-peach/60 animate-pulse w-3/4"></div>
            <div class="h-3 bg-cozy-peach/40 animate-pulse w-1/2"></div>
            <div class="h-3 bg-cozy-peach/40 animate-pulse w-1/4"></div>
          </div>
          <div class="w-20 space-y-2">
            <div class="h-5 bg-cozy-peach/50 animate-pulse"></div>
            <div class="h-3 bg-cozy-peach/30 animate-pulse"></div>
          </div>
        </div>
      </div>
    </template>

    <!-- Detail skeleton -->
    <template v-else-if="type === 'detail'">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div class="bg-cozy-peach/30 animate-pulse h-80 border-3 border-cozy-brown/10"></div>
        <div class="space-y-4">
          <div class="h-3 bg-cozy-peach/40 animate-pulse w-1/4"></div>
          <div class="h-6 bg-cozy-peach/60 animate-pulse w-full"></div>
          <div class="h-6 bg-cozy-peach/60 animate-pulse w-3/4"></div>
          <div class="flex gap-1">
            <div v-for="s in 5" :key="s" class="h-5 w-5 bg-cozy-peach/40 animate-pulse"></div>
          </div>
          <div class="h-8 bg-cozy-peach/50 animate-pulse w-1/3 mt-4"></div>
          <div class="space-y-2 mt-4">
            <div class="h-3 bg-cozy-peach/30 animate-pulse w-full"></div>
            <div class="h-3 bg-cozy-peach/30 animate-pulse w-full"></div>
            <div class="h-3 bg-cozy-peach/30 animate-pulse w-2/3"></div>
          </div>
          <div class="h-12 bg-cozy-peach/40 animate-pulse w-full mt-6"></div>
        </div>
      </div>
    </template>

    <!-- Text skeleton -->
    <template v-else-if="type === 'text'">
      <div class="space-y-3">
        <div
          v-for="n in count"
          :key="n"
          class="h-4 bg-cozy-peach/50 animate-pulse"
          :style="{ width: `${60 + Math.random() * 40}%` }"
        ></div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
withDefaults(defineProps<{
  type?: 'card' | 'list' | 'detail' | 'text'
  count?: number
}>(), {
  type: 'card',
  count: 4,
})
</script>
