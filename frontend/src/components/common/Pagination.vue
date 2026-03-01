<template>
  <nav
    v-if="totalPages > 1"
    class="flex items-center justify-center gap-1"
    aria-label="Pagination"
  >
    <!-- Previous -->
    <button
      type="button"
      :disabled="currentPage <= 1"
      class="w-9 h-9 flex items-center justify-center font-retro text-xl text-cozy-brown bg-cozy-cream border-3 border-cozy-brown/30 shadow-pixel hover:shadow-pixel-hover hover:translate-y-[2px] transition-all disabled:opacity-40 disabled:cursor-not-allowed"
      @click="goToPage(currentPage - 1)"
      aria-label="Previous page"
    >
      &lt;
    </button>

    <!-- First page -->
    <button
      v-if="showFirst"
      type="button"
      class="w-9 h-9 flex items-center justify-center font-retro text-lg border-3 transition-all shadow-pixel hover:shadow-pixel-hover hover:translate-y-[2px]"
      :class="pageButtonClass(1)"
      @click="goToPage(1)"
    >
      1
    </button>
    <span
      v-if="showFirstEllipsis"
      class="w-9 h-9 flex items-center justify-center font-retro text-lg text-cozy-brown/40"
    >
      ...
    </span>

    <!-- Page numbers -->
    <button
      v-for="page in visiblePages"
      :key="page"
      type="button"
      class="w-9 h-9 flex items-center justify-center font-retro text-lg border-3 transition-all shadow-pixel hover:shadow-pixel-hover hover:translate-y-[2px]"
      :class="pageButtonClass(page)"
      @click="goToPage(page)"
      :aria-current="page === currentPage ? 'page' : undefined"
    >
      {{ page }}
    </button>

    <!-- Last ellipsis and page -->
    <span
      v-if="showLastEllipsis"
      class="w-9 h-9 flex items-center justify-center font-retro text-lg text-cozy-brown/40"
    >
      ...
    </span>
    <button
      v-if="showLast"
      type="button"
      class="w-9 h-9 flex items-center justify-center font-retro text-lg border-3 transition-all shadow-pixel hover:shadow-pixel-hover hover:translate-y-[2px]"
      :class="pageButtonClass(totalPages)"
      @click="goToPage(totalPages)"
    >
      {{ totalPages }}
    </button>

    <!-- Next -->
    <button
      type="button"
      :disabled="currentPage >= totalPages"
      class="w-9 h-9 flex items-center justify-center font-retro text-xl text-cozy-brown bg-cozy-cream border-3 border-cozy-brown/30 shadow-pixel hover:shadow-pixel-hover hover:translate-y-[2px] transition-all disabled:opacity-40 disabled:cursor-not-allowed"
      @click="goToPage(currentPage + 1)"
      aria-label="Next page"
    >
      &gt;
    </button>
  </nav>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(defineProps<{
  currentPage: number
  totalPages: number
  siblingCount?: number
}>(), {
  siblingCount: 1,
})

const emit = defineEmits<{
  (e: 'update:currentPage', page: number): void
}>()

const visiblePages = computed(() => {
  const pages: number[] = []
  const start = Math.max(2, props.currentPage - props.siblingCount)
  const end = Math.min(props.totalPages - 1, props.currentPage + props.siblingCount)

  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  return pages
})

const showFirst = computed(() => {
  return !visiblePages.value.includes(1)
})

const showLast = computed(() => {
  return !visiblePages.value.includes(props.totalPages) && props.totalPages > 1
})

const showFirstEllipsis = computed(() => {
  return showFirst.value && visiblePages.value.length > 0 && (visiblePages.value[0] ?? 0) > 2
})

const showLastEllipsis = computed(() => {
  return showLast.value && visiblePages.value.length > 0 && (visiblePages.value[visiblePages.value.length - 1] ?? 0) < props.totalPages - 1
})

function pageButtonClass(page: number): string {
  if (page === props.currentPage) {
    return 'bg-primary-500 text-white border-cozy-brown'
  }
  return 'bg-cozy-cream text-cozy-brown border-cozy-brown/30 hover:bg-cozy-peach hover:border-cozy-brown/50'
}

function goToPage(page: number) {
  if (page >= 1 && page <= props.totalPages && page !== props.currentPage) {
    emit('update:currentPage', page)
  }
}
</script>
