import { defineStore } from 'pinia'
import { ref } from 'vue'

interface Notification {
  id: number
  text: string
  type: 'success' | 'error'
}

let nextId = 0

export const useNotificationStore = defineStore('notification', () => {
  const messages = ref<Notification[]>([])

  function showSuccess(text: string) {
    const id = nextId++
    messages.value.push({ id, text, type: 'success' })
    setTimeout(() => remove(id), 3000)
  }

  function showError(text: string) {
    const id = nextId++
    messages.value.push({ id, text, type: 'error' })
    setTimeout(() => remove(id), 3000)
  }

  function remove(id: number) {
    messages.value = messages.value.filter(m => m.id !== id)
  }

  return {
    messages,
    showSuccess,
    showError,
    remove
  }
})
