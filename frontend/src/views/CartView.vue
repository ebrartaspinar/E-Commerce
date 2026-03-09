<template>
  <div class="page-container">
    <h1 class="section-title">&#128722; Your Cart</h1>

    <!-- Loading -->
    <div v-if="cartStore.loading && !cartStore.cart" class="space-y-4">
      <div v-for="i in 3" :key="i" class="pixel-card animate-pulse flex gap-4">
        <div class="bg-gray-200 w-24 h-24"></div>
        <div class="flex-1 space-y-2">
          <div class="bg-gray-200 h-4 w-3/4 rounded"></div>
          <div class="bg-gray-200 h-3 w-1/2 rounded"></div>
          <div class="bg-gray-200 h-5 w-1/4 rounded"></div>
        </div>
      </div>
    </div>

    <!-- Empty Cart -->
    <div v-else-if="cartStore.isEmpty" class="text-center py-16">
      <div class="font-pixel text-6xl text-gray-300 mb-6">&#128722;</div>
      <h2 class="font-pixel text-base text-cozy-dark mb-3">Your Cart is Empty</h2>
      <p class="font-retro text-xl text-gray-400 mb-6">
        No loot collected yet! Head to the marketplace to find some treasures.
      </p>
      <router-link
        to="/products"
        class="inline-block pixel-border bg-primary-500 text-white font-pixel text-xs py-3 px-8 shadow-pixel hover:bg-primary-600 transition-colors"
      >
        &#128270; Continue Shopping
      </router-link>
    </div>

    <!-- Cart Content -->
    <div v-else class="flex flex-col lg:flex-row gap-6">
      <!-- Cart Items -->
      <div class="flex-1 space-y-4">
        <div
          v-for="item in cartStore.cart?.items"
          :key="item.productId"
          class="pixel-card"
          :class="{ 'opacity-60': !item.inStock }"
        >
          <div class="flex gap-4">
            <!-- Image -->
            <div class="w-24 h-24 flex-shrink-0 pixel-border overflow-hidden bg-gray-50">
              <img
                v-if="item.productImage"
                :src="item.productImage"
                :alt="item.productName"
                class="w-full h-full object-cover"
              />
              <div v-else class="w-full h-full flex items-center justify-center font-pixel text-2xl text-gray-300">
                &#128247;
              </div>
            </div>

            <!-- Item Details -->
            <div class="flex-1 min-w-0">
              <router-link
                :to="`/products/${item.productId}`"
                class="font-body font-semibold text-sm text-cozy-dark hover:text-primary-500 line-clamp-2"
              >
                {{ item.productName }}
              </router-link>
              <p class="font-body text-xs text-gray-400 mt-1">
                Seller: {{ item.sellerName }}
              </p>

              <!-- Out of stock warning -->
              <div v-if="!item.inStock" class="mt-1">
                <span class="pixel-badge bg-red-50 text-cozy-red border-cozy-red text-xs">
                  &#10007; Out of Stock
                </span>
              </div>

              <!-- Price -->
              <div class="mt-2">
                <div v-if="item.discountPrice" class="flex items-center gap-2">
                  <span class="font-pixel text-xs text-cozy-red">
                    {{ formatPrice(item.discountPrice) }} TL
                  </span>
                  <span class="font-body text-xs text-gray-400 line-through">
                    {{ formatPrice(item.price) }} TL
                  </span>
                </div>
                <div v-else>
                  <span class="font-pixel text-xs text-primary-600">
                    {{ formatPrice(item.price) }} TL
                  </span>
                </div>
              </div>
            </div>

            <!-- Quantity & Remove -->
            <div class="flex flex-col items-end justify-between">
              <!-- Remove button -->
              <button
                class="font-pixel text-xs text-gray-400 hover:text-cozy-red transition-colors"
                :disabled="cartStore.loading"
                @click="removeItem(item.productId)"
              >
                &#10007;
              </button>

              <!-- Quantity selector -->
              <div class="flex items-center pixel-border">
                <button
                  class="px-2 py-1 bg-gray-100 hover:bg-gray-200 font-pixel text-xs transition-colors"
                  :disabled="item.quantity <= 1 || cartStore.loading"
                  @click="updateQuantity(item.productId, item.quantity - 1)"
                >
                  -
                </button>
                <span class="px-3 py-1 font-body text-sm bg-white min-w-[2rem] text-center">
                  {{ item.quantity }}
                </span>
                <button
                  class="px-2 py-1 bg-gray-100 hover:bg-gray-200 font-pixel text-xs transition-colors"
                  :disabled="item.quantity >= item.maxQuantity || cartStore.loading"
                  @click="updateQuantity(item.productId, item.quantity + 1)"
                >
                  +
                </button>
              </div>

              <!-- Item subtotal -->
              <span class="font-pixel text-xs text-cozy-dark">
                {{ formatPrice((item.discountPrice ?? item.price) * item.quantity) }} TL
              </span>
            </div>
          </div>
        </div>

        <!-- Clear Cart -->
        <div class="text-right">
          <button
            class="font-pixel text-xs text-gray-400 hover:text-cozy-red transition-colors"
            :disabled="cartStore.loading"
            @click="handleClearCart"
          >
            &#128465; Clear Cart
          </button>
        </div>
      </div>

      <!-- Order Summary Sidebar -->
      <div class="lg:w-80">
        <div class="pixel-card sticky top-4">
          <h3 class="font-pixel text-sm text-cozy-dark mb-4">&#128176; Order Summary</h3>

          <div class="space-y-3 mb-4">
            <div class="flex justify-between font-body text-sm">
              <span class="text-gray-500">Subtotal ({{ cartStore.itemCount }} items)</span>
              <span class="text-cozy-dark">{{ formatPrice(cartStore.cart?.totalPrice ?? 0) }} TL</span>
            </div>
            <div v-if="cartStore.cart && cartStore.cart.totalDiscount > 0" class="flex justify-between font-body text-sm">
              <span class="text-gray-500">Discount</span>
              <span class="text-cozy-green">-{{ formatPrice(cartStore.cart.totalDiscount) }} TL</span>
            </div>
            <div class="flex justify-between font-body text-sm">
              <span class="text-gray-500">Shipping</span>
              <span class="text-cozy-green">Free</span>
            </div>
          </div>

          <div class="border-t-2 border-cozy-brown pt-3 mb-4">
            <div class="flex justify-between">
              <span class="font-pixel text-sm text-cozy-dark">Total</span>
              <span class="font-pixel text-sm text-primary-600">
                {{ formatPrice(cartStore.cart?.finalPrice ?? 0) }} TL
              </span>
            </div>
          </div>

          <router-link
            to="/checkout"
            class="block w-full text-center pixel-border bg-primary-500 text-white font-pixel text-xs py-3 shadow-pixel hover:bg-primary-600 active:shadow-pixel-hover active:translate-x-0.5 active:translate-y-0.5 transition-all"
          >
            &#9876; Proceed to Checkout
          </router-link>

          <router-link
            to="/products"
            class="block w-full text-center font-pixel text-xs text-primary-500 mt-3 hover:underline"
          >
            Continue Shopping
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useCartStore } from '@/stores/cart'
import { useToast } from '@/composables/useToast'

const cartStore = useCartStore()
const { showToast } = useToast()

function formatPrice(price: number): string {
  return price.toLocaleString('tr-TR', { minimumFractionDigits: 2 })
}

async function updateQuantity(productId: string, quantity: number) {
  try {
    await cartStore.updateQuantity(productId, quantity)
  } catch (err: any) {
    showToast(err.response?.data?.message || 'Failed to update quantity', 'error')
  }
}

async function removeItem(productId: string) {
  try {
    await cartStore.removeItem(productId)
    showToast('Item removed from cart', 'info')
  } catch (err: any) {
    showToast(err.response?.data?.message || 'Failed to remove item', 'error')
  }
}

async function handleClearCart() {
  try {
    await cartStore.clearCart()
    showToast('Cart cleared', 'info')
  } catch (err: any) {
    showToast(err.response?.data?.message || 'Failed to clear cart', 'error')
  }
}

onMounted(async () => {
  try {
    await cartStore.fetchCart()
  } catch {
    showToast('Failed to load cart', 'error')
  }
})
</script>
