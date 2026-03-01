import { ref } from 'vue'

export interface Toast {
  id: number
  message: string
  title?: string
  type: 'success' | 'error' | 'warning' | 'info'
  duration: number
}

const toasts = ref<Toast[]>([])
let nextId = 0

export function useToast() {
  function showToast(
    message: string,
    type: 'success' | 'error' | 'warning' | 'info' = 'success',
    duration: number = 3000
  ) {
    const id = nextId++
    const toast: Toast = { id, message, type, duration }
    toasts.value.push(toast)

    if (duration > 0) {
      setTimeout(() => {
        removeToast(id)
      }, duration)
    }

    return id
  }

  function removeToast(id: number) {
    const index = toasts.value.findIndex((t) => t.id === id)
    if (index !== -1) {
      toasts.value.splice(index, 1)
    }
  }

  function success(message: string, title?: string) {
    return showToast(message, 'success')
  }

  function error(message: string, title?: string) {
    return showToast(message, 'error')
  }

  function warning(message: string, title?: string) {
    return showToast(message, 'warning')
  }

  function info(message: string, title?: string) {
    return showToast(message, 'info')
  }

  function clearAll() {
    toasts.value = []
  }

  return {
    toasts,
    showToast,
    removeToast,
    success,
    error,
    warning,
    info,
    clearAll,
  }
}
