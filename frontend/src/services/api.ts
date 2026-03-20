import axios from 'axios'
import type { AuthResponse, Product, Category, CartItem, Order, Favorite, Page } from '../types'
import router from '../router'

// Axios instance
const api = axios.create({
  baseURL: 'http://localhost:8080/api'
})

// Request interceptor - JWT token ekle
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Response interceptor - 401 hatalarini yakala
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      router.push('/login')
    }
    return Promise.reject(error)
  }
)

// ---- Auth API ----
export const authApi = {
  register(data: { email: string; password: string; firstName: string; lastName: string }) {
    return api.post<AuthResponse>('/auth/register', data)
  },

  login(data: { email: string; password: string }) {
    return api.post<AuthResponse>('/auth/login', data)
  },

  getMe() {
    return api.get('/auth/me')
  }
}

// ---- Product API ----
export const productApi = {
  getAll(params: { page?: number; size?: number; search?: string; categoryId?: number }) {
    return api.get<Page<Product>>('/products', { params })
  },

  getById(id: number) {
    return api.get<Product>(`/products/${id}`)
  }
}

// ---- Category API ----
export const categoryApi = {
  getAll() {
    return api.get<Category[]>('/categories')
  }
}

// ---- Cart API ----
export const cartApi = {
  getCart() {
    return api.get<CartItem[]>('/cart')
  },

  addItem(productId: number, quantity: number) {
    return api.post<CartItem>('/cart', { productId, quantity })
  },

  updateQuantity(id: number, quantity: number) {
    return api.put<CartItem>(`/cart/${id}`, { quantity })
  },

  removeItem(id: number) {
    return api.delete(`/cart/${id}`)
  },

  clearCart() {
    return api.delete('/cart')
  }
}

// ---- Order API ----
export const orderApi = {
  create(shippingAddress: string) {
    return api.post<Order>('/orders', { shippingAddress })
  },

  getAll() {
    return api.get<Order[]>('/orders')
  },

  getById(id: number) {
    return api.get<Order>(`/orders/${id}`)
  },

  cancel(id: number) {
    return api.put<Order>(`/orders/${id}/cancel`)
  }
}

// ---- Favorite API ----
export const favoriteApi = {
  getAll() {
    return api.get<Favorite[]>('/favorites')
  },

  add(productId: number) {
    return api.post<Favorite>(`/favorites/${productId}`)
  },

  remove(productId: number) {
    return api.delete(`/favorites/${productId}`)
  },

  check(productId: number) {
    return api.get<{ isFavorite: boolean }>(`/favorites/${productId}/check`)
  }
}

export default api
