<template>
  <div class="relative w-full" ref="containerRef">
    <!-- Input -->
    <div class="flex">
      <div class="relative flex-1">
        <input
          ref="inputRef"
          type="text"
          :value="modelValue"
          :placeholder="placeholder"
          class="w-full px-4 py-2 pr-10 font-retro text-lg text-cozy-dark bg-white border-3 border-cozy-brown/40 focus:border-primary-500 focus:outline-none placeholder-cozy-brown/40 shadow-pixel-inset"
          @input="onInput"
          @focus="onFocus"
          @keydown.enter="onEnter"
          @keydown.escape="closeDropdown"
          @keydown.down.prevent="highlightNext"
          @keydown.up.prevent="highlightPrev"
          autocomplete="off"
        />
        <svg
          class="absolute right-3 top-1/2 -translate-y-1/2 w-5 h-5 text-cozy-brown/40"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
        </svg>
      </div>
    </div>

    <!-- Dropdown -->
    <Transition name="dropdown">
      <div
        v-if="showDropdown && (suggestions.length > 0 || recentSearches.length > 0)"
        class="absolute top-full left-0 right-0 mt-1 bg-cozy-cream border-3 border-cozy-brown/40 shadow-pixel-lg z-50 max-h-64 overflow-y-auto"
      >
        <!-- Suggestions -->
        <div v-if="suggestions.length > 0">
          <div class="px-3 py-1 font-pixel text-[9px] text-cozy-brown/50 bg-cozy-peach/30 border-b border-cozy-brown/10">
            SUGGESTIONS
          </div>
          <button
            v-for="(suggestion, index) in suggestions"
            :key="'s-' + index"
            class="w-full text-left px-4 py-2 font-retro text-lg text-cozy-dark hover:bg-cozy-peach transition-colors flex items-center gap-2"
            :class="{ 'bg-cozy-peach': highlightedIndex === index }"
            @mousedown.prevent="selectSuggestion(suggestion)"
          >
            <svg class="w-4 h-4 text-cozy-brown/40 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
            <span>{{ suggestion }}</span>
          </button>
        </div>

        <!-- Recent searches -->
        <div v-if="recentSearches.length > 0 && !modelValue">
          <div class="px-3 py-1 font-pixel text-[9px] text-cozy-brown/50 bg-cozy-peach/30 border-b border-cozy-brown/10 flex items-center justify-between">
            <span>RECENT</span>
            <button
              @mousedown.prevent="clearRecentSearches"
              class="text-cozy-red hover:text-cozy-red/80"
            >
              CLEAR
            </button>
          </div>
          <button
            v-for="(recent, index) in recentSearches"
            :key="'r-' + index"
            class="w-full text-left px-4 py-2 font-retro text-lg text-cozy-dark hover:bg-cozy-peach transition-colors flex items-center gap-2"
            :class="{ 'bg-cozy-peach': highlightedIndex === suggestions.length + index }"
            @mousedown.prevent="selectSuggestion(recent)"
          >
            <svg class="w-4 h-4 text-cozy-brown/40 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <span>{{ recent }}</span>
          </button>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, onUnmounted } from 'vue'

const STORAGE_KEY = 'trendyol-recent-searches'
const MAX_RECENT = 5

const props = withDefaults(defineProps<{
  modelValue?: string
  placeholder?: string
  suggestions?: string[]
}>(), {
  modelValue: '',
  placeholder: 'Search products...',
  suggestions: () => [],
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
  (e: 'search', query: string): void
  (e: 'autocomplete', query: string): void
}>()

const inputRef = ref<HTMLInputElement | null>(null)
const containerRef = ref<HTMLElement | null>(null)
const showDropdown = ref(false)
const highlightedIndex = ref(-1)
const recentSearches = ref<string[]>([])
let debounceTimer: ReturnType<typeof setTimeout> | null = null

function loadRecentSearches() {
  try {
    const stored = localStorage.getItem(STORAGE_KEY)
    if (stored) {
      recentSearches.value = JSON.parse(stored)
    }
  } catch {
    recentSearches.value = []
  }
}

function saveRecentSearch(query: string) {
  const trimmed = query.trim()
  if (!trimmed) return
  const filtered = recentSearches.value.filter(s => s !== trimmed)
  filtered.unshift(trimmed)
  recentSearches.value = filtered.slice(0, MAX_RECENT)
  localStorage.setItem(STORAGE_KEY, JSON.stringify(recentSearches.value))
}

function clearRecentSearches() {
  recentSearches.value = []
  localStorage.removeItem(STORAGE_KEY)
}

function onInput(event: Event) {
  const value = (event.target as HTMLInputElement).value
  emit('update:modelValue', value)
  highlightedIndex.value = -1

  if (debounceTimer) clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => {
    if (value.trim().length >= 2) {
      emit('autocomplete', value.trim())
    }
  }, 300)
}

function onFocus() {
  showDropdown.value = true
}

function closeDropdown() {
  showDropdown.value = false
  highlightedIndex.value = -1
}

function onEnter() {
  const allItems = [...props.suggestions, ...(!props.modelValue ? recentSearches.value : [])]
  if (highlightedIndex.value >= 0 && highlightedIndex.value < allItems.length) {
    selectSuggestion(allItems[highlightedIndex.value]!)
  } else if (props.modelValue.trim()) {
    saveRecentSearch(props.modelValue)
    emit('search', props.modelValue.trim())
    closeDropdown()
  }
}

function selectSuggestion(value: string) {
  emit('update:modelValue', value)
  saveRecentSearch(value)
  emit('search', value)
  closeDropdown()
}

function highlightNext() {
  const allItems = [...props.suggestions, ...(!props.modelValue ? recentSearches.value : [])]
  if (highlightedIndex.value < allItems.length - 1) {
    highlightedIndex.value++
  }
}

function highlightPrev() {
  if (highlightedIndex.value > 0) {
    highlightedIndex.value--
  }
}

function handleClickOutside(event: MouseEvent) {
  if (containerRef.value && !containerRef.value.contains(event.target as Node)) {
    closeDropdown()
  }
}

onMounted(() => {
  loadRecentSearches()
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
  if (debounceTimer) clearTimeout(debounceTimer)
})
</script>

<style scoped>
.dropdown-enter-active,
.dropdown-leave-active {
  transition: opacity 0.15s ease, transform 0.15s ease;
}
.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}
</style>
