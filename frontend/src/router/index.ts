import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(),
  scrollBehavior(_to, _from, savedPosition) {
    if (savedPosition) return savedPosition
    return { top: 0 }
  },
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { guestOnly: true },
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/RegisterView.vue'),
      meta: { guestOnly: true },
    },
    {
      path: '/products',
      name: 'products',
      component: () => import('@/views/ProductListView.vue'),
    },
    {
      path: '/products/:slug',
      name: 'product-detail',
      component: () => import('@/views/ProductDetailView.vue'),
    },
    {
      path: '/search',
      name: 'search',
      component: () => import('@/views/SearchResultsView.vue'),
    },
    {
      path: '/cart',
      name: 'cart',
      component: () => import('@/views/CartView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/checkout',
      name: 'checkout',
      component: () => import('@/views/CheckoutView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/orders',
      name: 'orders',
      component: () => import('@/views/OrderHistoryView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/orders/:id',
      name: 'order-detail',
      component: () => import('@/views/OrderDetailView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('@/views/ProfileView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/seller',
      name: 'seller-dashboard',
      component: () => import('@/views/SellerDashboard.vue'),
      meta: { requiresAuth: true, sellerOnly: true },
    },
    {
      path: '/seller/products',
      name: 'seller-products',
      component: () => import('@/views/SellerProducts.vue'),
      meta: { requiresAuth: true, sellerOnly: true },
    },
    {
      path: '/seller/products/new',
      name: 'seller-product-new',
      component: () => import('@/views/SellerProductForm.vue'),
      meta: { requiresAuth: true, sellerOnly: true },
    },
    {
      path: '/seller/products/:id/edit',
      name: 'seller-product-edit',
      component: () => import('@/views/SellerProductForm.vue'),
      meta: { requiresAuth: true, sellerOnly: true },
    },
    {
      path: '/seller/orders',
      name: 'seller-orders',
      component: () => import('@/views/SellerOrders.vue'),
      meta: { requiresAuth: true, sellerOnly: true },
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('@/views/NotFoundView.vue'),
    },
  ],
})

router.beforeEach((to, _from, next) => {
  const authStore = useAuthStore()

  // Guest-only routes: redirect logged-in users to home
  if (to.meta.guestOnly && authStore.isAuthenticated) {
    next({ name: 'home' })
    return
  }

  // Auth-required routes: redirect unauthenticated users to login
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next({ name: 'login', query: { redirect: to.fullPath } })
    return
  }

  // Seller-only routes: redirect non-sellers
  if (to.meta.sellerOnly && authStore.user?.role !== 'SELLER') {
    next({ name: 'home' })
    return
  }

  next()
})

export default router
