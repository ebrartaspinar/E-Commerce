import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { useAuthStore } from './stores/auth'
import { useFavoriteStore } from './stores/favorite'
import { useCartStore } from './stores/cart'
import './style.css'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)

// Sayfa yuklendiginde localStorage'dan kullanici bilgisini yukle
const authStore = useAuthStore()
authStore.loadFromStorage()

// Giris yapilmissa favori ve sepet bilgilerini yukle
if (authStore.isAuthenticated) {
  const favoriteStore = useFavoriteStore()
  const cartStore = useCartStore()
  favoriteStore.fetchFavorites()
  cartStore.fetchCart()
}

app.mount('#app')
