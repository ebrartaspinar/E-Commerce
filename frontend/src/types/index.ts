export interface User {
  id: number
  email: string
  firstName: string
  lastName: string
  role: 'BUYER' | 'SELLER' | 'ADMIN'
}

export interface AuthResponse {
  token: string
  email: string
  firstName: string
  role: string
}

export interface Category {
  id: number
  name: string
}

export interface Product {
  id: number
  name: string
  description: string
  price: number
  imageUrl: string
  stock: number
  category: Category
  sellerId: number
  active: boolean
  createdAt: string
}

export interface CartItem {
  id: number
  product: Product
  quantity: number
}

export interface OrderItem {
  id: number
  productId: number
  productName: string
  price: number
  quantity: number
}

export interface Order {
  id: number
  status: 'PENDING' | 'CONFIRMED' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED'
  totalAmount: number
  shippingAddress: string
  orderItems: OrderItem[]
  createdAt: string
}

export interface Favorite {
  id: number
  product: Product
  createdAt: string
}

export interface Page<T> {
  content: T[]
  totalPages: number
  totalElements: number
  number: number
  size: number
}
