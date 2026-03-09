<template>
  <div class="space-y-0">
    <div
      v-for="(step, index) in timelineSteps"
      :key="step.status"
      class="flex gap-3"
    >
      <!-- Left: dot and line -->
      <div class="flex flex-col items-center">
        <!-- Dot -->
        <div
          class="w-5 h-5 border-3 flex items-center justify-center shrink-0"
          :class="dotClasses(step)"
        >
          <span
            v-if="step.state === 'completed'"
            class="text-white text-[10px] font-bold leading-none"
          >
            &check;
          </span>
          <span
            v-else-if="step.state === 'current'"
            class="w-2 h-2 bg-primary-500 animate-pulse"
          ></span>
        </div>
        <!-- Connecting line -->
        <div
          v-if="index < timelineSteps.length - 1"
          class="w-0.5 h-10 my-1"
          :class="step.state === 'completed' ? 'bg-cozy-green' : 'bg-cozy-brown/20'"
        ></div>
      </div>

      <!-- Right: content -->
      <div class="pb-6">
        <p
          class="font-retro text-lg leading-tight"
          :class="{
            'text-cozy-green': step.state === 'completed',
            'text-primary-600 font-bold': step.state === 'current',
            'text-cozy-brown/40': step.state === 'future',
          }"
        >
          {{ formatStatus(step.status) }}
        </p>
        <p
          v-if="step.timestamp"
          class="font-body text-xs text-cozy-brown/50 mt-0.5"
        >
          {{ formatDate(step.timestamp) }}
        </p>
        <p
          v-if="step.note"
          class="font-body text-xs text-cozy-brown/60 mt-0.5 italic"
        >
          {{ step.note }}
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { OrderStatus, OrderStatusChange } from '@/types'

const props = defineProps<{
  statusHistory: OrderStatusChange[]
  currentStatus: OrderStatus
}>()

const allStatuses: OrderStatus[] = [
  'PENDING',
  'CONFIRMED',
  'PROCESSING',
  'SHIPPED',
  'DELIVERED',
]

const cancelledStatuses: OrderStatus[] = ['CANCELLED', 'REFUNDED']

interface TimelineStep {
  status: OrderStatus
  state: 'completed' | 'current' | 'future'
  timestamp: string | null
  note: string | null
}

const timelineSteps = computed<TimelineStep[]>(() => {
  const isCancelled = cancelledStatuses.includes(props.currentStatus)
  const historyMap = new Map<string, OrderStatusChange>()
  for (const change of props.statusHistory) {
    historyMap.set(change.status, change)
  }

  const statuses = isCancelled
    ? [...allStatuses.slice(0, allStatuses.indexOf(props.currentStatus as OrderStatus) + 1 || allStatuses.length), props.currentStatus]
    : allStatuses

  const currentIdx = allStatuses.indexOf(props.currentStatus)

  return statuses.map((status, index) => {
    const historyEntry = historyMap.get(status)
    let state: 'completed' | 'current' | 'future'

    if (isCancelled && status === props.currentStatus) {
      state = 'current'
    } else if (index < currentIdx) {
      state = 'completed'
    } else if (index === currentIdx) {
      state = 'current'
    } else {
      state = 'future'
    }

    return {
      status,
      state,
      timestamp: historyEntry?.timestamp || null,
      note: historyEntry?.note || null,
    }
  })
})

function dotClasses(step: TimelineStep): string {
  switch (step.state) {
    case 'completed':
      return 'border-cozy-green bg-cozy-green'
    case 'current':
      return 'border-primary-500 bg-cozy-cream'
    case 'future':
      return 'border-cozy-brown/20 bg-cozy-cream'
    default:
      return ''
  }
}

function formatStatus(status: string): string {
  return status
    .replace(/_/g, ' ')
    .split(' ')
    .map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
    .join(' ')
}

function formatDate(dateStr: string): string {
  const date = new Date(dateStr)
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}
</script>
