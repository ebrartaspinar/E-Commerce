// ========== Common ==========
export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
  timestamp: string
}

export interface PagedResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  first: boolean
  last: boolean
}

// ========== Auth ==========
export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  firstName: string
  lastName: string
  email: string
  password: string
  phoneNumber?: string
  role: 'BUYER' | 'SELLER'
}

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
  user: UserResponse
}

// ========== User ==========
export interface UserResponse {
  id: string
  firstName: string
  lastName: string
  email: string
  phoneNumber: string | null
  role: 'BUYER' | 'SELLER' | 'ADMIN'
  status: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED'
  avatarUrl: string | null
  createdAt: string
}

export interface UpdateUserRequest {
  firstName?: string
  lastName?: string
  phoneNumber?: string
}

export interface Address {
  id: string
  title: string
  fullName: string
  phoneNumber: string
  city: string
  district: string
  neighborhood: string
  fullAddress: string
  zipCode: string
  isDefault: boolean
}

export interface AddressRequest {
  title: string
  fullName: string
  phoneNumber: string
  city: string
  district: string
  neighborhood: string
  fullAddress: string
  zipCode: string
  isDefault: boolean
}

// ========== Product ==========
export interface Product {
  id: string
  name: string
  slug: string
  description: string
  price: number
  discountPrice: number | null
  stockQuantity: number
  sku: string
  brand: string
  categoryId: string
  categoryName: string
  sellerId: string
  sellerName: string
  status: 'ACTIVE' | 'INACTIVE' | 'OUT_OF_STOCK'
  averageRating: number
  reviewCount: number
  images: ProductImage[]
  createdAt: string
}

export interface ProductImage {
  id: string
  url: string
  altText: string
  displayOrder: number
  isPrimary: boolean
}

export interface Category {
  id: string
  name: string
  slug: string
  description: string
  iconUrl: string | null
  parentId: string | null
  children: Category[]
  productCount: number
}

export interface Review {
  id: string
  productId: string
  userId: string
  userName: string
  rating: number
  comment: string
  status: 'PENDING' | 'APPROVED' | 'REJECTED'
  createdAt: string
}

export interface ReviewRequest {
  rating: number
  comment: string
}

export interface ProductFilter {
  categoryId?: string
  minPrice?: number
  maxPrice?: number
  brand?: string
  sellerId?: string
  search?: string
  inStock?: boolean
  minRating?: number
  sortBy?: string
  sortDir?: string
  page?: number
  size?: number
}

// ========== Cart ==========
export interface CartItem {
  productId: string
  productName: string
  productImage: string
  sellerId: string
  sellerName: string
  price: number
  discountPrice: number | null
  quantity: number
  maxQuantity: number
  inStock: boolean
}

export interface Cart {
  userId: string
  items: CartItem[]
  totalItems: number
  totalPrice: number
  totalDiscount: number
  finalPrice: number
}

export interface AddToCartRequest {
  productId: string
  quantity: number
}

export interface UpdateCartItemRequest {
  quantity: number
}

// ========== Order ==========
export interface Order {
  id: string
  orderNumber: string
  userId: string
  items: OrderItem[]
  shippingAddress: OrderAddress
  status: OrderStatus
  totalAmount: number
  shippingFee: number
  discount: number
  finalAmount: number
  notes: string | null
  createdAt: string
  updatedAt: string
  statusHistory: OrderStatusChange[]
}

export interface OrderItem {
  id: string
  productId: string
  productName: string
  productImage: string
  sellerId: string
  sellerName: string
  quantity: number
  unitPrice: number
  totalPrice: number
}

export interface OrderAddress {
  fullName: string
  phoneNumber: string
  city: string
  district: string
  neighborhood: string
  fullAddress: string
  zipCode: string
}

export type OrderStatus =
  | 'PENDING'
  | 'CONFIRMED'
  | 'PROCESSING'
  | 'SHIPPED'
  | 'DELIVERED'
  | 'CANCELLED'
  | 'REFUNDED'

export interface OrderStatusChange {
  status: OrderStatus
  timestamp: string
  note: string | null
}

export interface CreateOrderRequest {
  shippingAddressId: string
  notes?: string
}

// ========== Payment ==========
export interface Payment {
  id: string
  orderId: string
  orderNumber: string
  amount: number
  method: PaymentMethod
  status: PaymentStatus
  transactionId: string | null
  createdAt: string
}

export type PaymentMethod = 'CREDIT_CARD' | 'DEBIT_CARD' | 'BANK_TRANSFER'
export type PaymentStatus = 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED' | 'REFUNDED'

export interface PaymentRequest {
  orderId: string
  method: PaymentMethod
  cardNumber?: string
  cardHolder?: string
  expiryDate?: string
  cvv?: string
}

// ========== Notification ==========
export interface Notification {
  id: string
  userId: string
  title: string
  message: string
  type: string
  channel: string
  read: boolean
  metadata: Record<string, string>
  createdAt: string
}

// ========== Seller ==========
export interface SellerProduct {
  id: string
  name: string
  price: number
  discountPrice: number | null
  stockQuantity: number
  status: string
  averageRating: number
  reviewCount: number
  totalSales: number
  createdAt: string
}

export interface CreateProductRequest {
  name: string
  description: string
  price: number
  discountPrice?: number
  stockQuantity: number
  sku: string
  brand: string
  categoryId: string
  images: { url: string; altText: string; displayOrder: number; isPrimary: boolean }[]
}

export interface UpdateProductRequest extends Partial<CreateProductRequest> {}

export interface DashboardStats {
  totalProducts: number
  totalOrders: number
  totalRevenue: number
  pendingOrders: number
  monthlyRevenue: number[]
  recentOrders: Order[]
}
