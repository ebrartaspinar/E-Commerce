<template>
  <div class="page-container">
    <h1 class="section-title">&#9876; Checkout</h1>

    <!-- Step Indicator -->
    <div class="flex items-center justify-center mb-8">
      <div
        v-for="(stepLabel, idx) in steps"
        :key="idx"
        class="flex items-center"
      >
        <div
          class="flex items-center justify-center w-8 h-8 pixel-border font-pixel text-xs transition-colors"
          :class="currentStep >= idx
            ? 'bg-primary-500 text-white border-primary-500'
            : 'bg-white text-gray-400'"
        >
          {{ idx + 1 }}
        </div>
        <span
          class="ml-2 font-pixel text-xs hidden sm:inline"
          :class="currentStep >= idx ? 'text-cozy-dark' : 'text-gray-400'"
        >
          {{ stepLabel }}
        </span>
        <div
          v-if="idx < steps.length - 1"
          class="w-8 sm:w-16 h-0.5 mx-2"
          :class="currentStep > idx ? 'bg-primary-500' : 'bg-gray-300'"
        ></div>
      </div>
    </div>

    <!-- Step 1: Address Selection -->
    <div v-if="currentStep === 0" class="max-w-2xl mx-auto">
      <div class="pixel-card p-6">
        <h2 class="font-pixel text-sm text-cozy-dark mb-4">&#127968; Shipping Address</h2>

        <!-- Loading -->
        <div v-if="loadingAddresses" class="space-y-3">
          <div v-for="i in 2" :key="i" class="animate-pulse p-3 border-2 border-gray-200">
            <div class="bg-gray-200 h-4 w-1/2 mb-2 rounded"></div>
            <div class="bg-gray-200 h-3 w-3/4 rounded"></div>
          </div>
        </div>

        <!-- Address List -->
        <div v-else-if="addresses.length > 0" class="space-y-3 mb-4">
          <div
            v-for="addr in addresses"
            :key="addr.id"
            class="p-3 border-2 cursor-pointer transition-colors"
            :class="selectedAddressId === addr.id
              ? 'border-primary-500 bg-primary-50'
              : 'border-cozy-brown hover:border-primary-300'"
            @click="selectedAddressId = addr.id"
          >
            <div class="flex items-center justify-between mb-1">
              <span class="font-pixel text-xs text-cozy-dark">{{ addr.title }}</span>
              <span v-if="addr.isDefault" class="pixel-badge bg-cozy-gold text-white text-xs">Default</span>
            </div>
            <p class="font-body text-sm text-gray-600">{{ addr.fullName }}</p>
            <p class="font-body text-xs text-gray-500">
              {{ addr.fullAddress }}, {{ addr.district }}, {{ addr.city }} {{ addr.zipCode }}
            </p>
            <p class="font-body text-xs text-gray-400">{{ addr.phoneNumber }}</p>
          </div>
        </div>

        <!-- No Addresses -->
        <div v-else class="text-center py-8">
          <p class="font-retro text-lg text-gray-400 mb-4">
            No saved addresses found. Please add an address in your profile.
          </p>
          <router-link
            to="/profile"
            class="inline-block pixel-border bg-primary-500 text-white font-pixel text-xs py-2 px-6 shadow-pixel"
          >
            Go to Profile
          </router-link>
        </div>

        <button
          v-if="addresses.length > 0"
          class="w-full pixel-border bg-primary-500 text-white font-pixel text-xs py-3 shadow-pixel hover:bg-primary-600 transition-colors disabled:opacity-50"
          :disabled="!selectedAddressId"
          @click="currentStep = 1"
        >
          Continue to Payment &gt;
        </button>
      </div>
    </div>

    <!-- Step 2: Payment -->
    <div v-if="currentStep === 1" class="max-w-2xl mx-auto">
      <div class="pixel-card p-6">
        <h2 class="font-pixel text-sm text-cozy-dark mb-4">&#128179; Payment Details</h2>

        <form @submit.prevent="currentStep = 2" class="space-y-4">
          <div>
            <label class="font-pixel text-xs text-cozy-brown block mb-1">Card Number</label>
            <input
              v-model="paymentForm.cardNumber"
              type="text"
              placeholder="1234 5678 9012 3456"
              class="pixel-input"
              maxlength="19"
              required
            />
          </div>

          <div>
            <label class="font-pixel text-xs text-cozy-brown block mb-1">Card Holder</label>
            <input
              v-model="paymentForm.cardHolder"
              type="text"
              placeholder="Full Name"
              class="pixel-input"
              required
            />
          </div>

          <div class="grid grid-cols-2 gap-3">
            <div>
              <label class="font-pixel text-xs text-cozy-brown block mb-1">Expiry Date</label>
              <input
                v-model="paymentForm.expiryDate"
                type="text"
                placeholder="MM/YY"
                class="pixel-input"
                maxlength="5"
                required
              />
            </div>
            <div>
              <label class="font-pixel text-xs text-cozy-brown block mb-1">CVV</label>
              <input
                v-model="paymentForm.cvv"
                type="password"
                placeholder="123"
                class="pixel-input"
                maxlength="4"
                required
              />
            </div>
          </div>

          <div class="font-retro text-sm text-gray-400 text-center p-2 bg-gray-50 border border-gray-200">
            &#128274; This is a mock payment form. No real charges will be made.
          </div>

          <div class="flex gap-3">
            <button
              type="button"
              class="flex-1 pixel-border bg-gray-100 font-pixel text-xs py-3 hover:bg-gray-200 transition-colors"
              @click="currentStep = 0"
            >
              &lt; Back
            </button>
            <button
              type="submit"
              class="flex-1 pixel-border bg-primary-500 text-white font-pixel text-xs py-3 shadow-pixel hover:bg-primary-600 transition-colors"
            >
              Review Order &gt;
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Step 3: Review & Confirm -->
    <div v-if="currentStep === 2" class="max-w-2xl mx-auto">
      <div class="pixel-card p-6">
        <h2 class="font-pixel text-sm text-cozy-dark mb-4">&#128220; Order Review</h2>

        <!-- Shipping Address Summary -->
        <div class="mb-4 p-3 bg-cozy-cream border-2 border-cozy-brown">
          <h3 class="font-pixel text-xs text-cozy-brown mb-1">Shipping Address</h3>
          <p class="font-body text-sm" v-if="selectedAddress">
            {{ selectedAddress.fullName }}<br />
            {{ selectedAddress.fullAddress }}<br />
            {{ selectedAddress.district }}, {{ selectedAddress.city }} {{ selectedAddress.zipCode }}<br />
            {{ selectedAddress.phoneNumber }}
          </p>
        </div>

        <!-- Payment Summary -->
        <div class="mb-4 p-3 bg-cozy-cream border-2 border-cozy-brown">
          <h3 class="font-pixel text-xs text-cozy-brown mb-1">Payment Method</h3>
          <p class="font-body text-sm">
            Card ending in ****{{ paymentForm.cardNumber.slice(-4) }}
          </p>
        </div>

        <!-- Cart Items Summary -->
        <div class="mb-4">
          <h3 class="font-pixel text-xs text-cozy-brown mb-2">Items ({{ cartStore.itemCount }})</h3>
          <div class="space-y-2 max-h-60 overflow-y-auto">
            <div
              v-for="item in cartStore.cart?.items"
              :key="item.productId"
              class="flex items-center gap-3 p-2 bg-white border border-gray-200"
            >
              <div class="w-12 h-12 flex-shrink-0 bg-gray-100 overflow-hidden">
                <img
                  v-if="item.productImage"
                  :src="item.productImage"
                  :alt="item.productName"
                  class="w-full h-full object-cover"
                />
              </div>
              <div class="flex-1 min-w-0">
                <p class="font-body text-sm truncate">{{ item.productName }}</p>
                <p class="font-body text-xs text-gray-400">Qty: {{ item.quantity }}</p>
              </div>
              <span class="font-pixel text-xs text-cozy-dark">
                {{ formatPrice((item.discountPrice ?? item.price) * item.quantity) }} TL
              </span>
            </div>
          </div>
        </div>

        <!-- Total -->
        <div class="border-t-2 border-cozy-brown pt-3 mb-4 space-y-2">
          <div class="flex justify-between font-body text-sm">
            <span class="text-gray-500">Subtotal</span>
            <span>{{ formatPrice(cartStore.cart?.totalPrice ?? 0) }} TL</span>
          </div>
          <div v-if="cartStore.cart && cartStore.cart.totalDiscount > 0" class="flex justify-between font-body text-sm">
            <span class="text-gray-500">Discount</span>
            <span class="text-cozy-green">-{{ formatPrice(cartStore.cart.totalDiscount) }} TL</span>
          </div>
          <div class="flex justify-between font-body text-sm">
            <span class="text-gray-500">Shipping</span>
            <span class="text-cozy-green">Free</span>
          </div>
          <div class="flex justify-between font-pixel text-sm pt-2 border-t border-gray-200">
            <span>Total</span>
            <span class="text-primary-600">{{ formatPrice(cartStore.cart?.finalPrice ?? 0) }} TL</span>
          </div>
        </div>

        <!-- Order Notes -->
        <div class="mb-4">
          <label class="font-pixel text-xs text-cozy-brown block mb-1">Order Notes (optional)</label>
          <textarea
            v-model="orderNotes"
            rows="2"
            placeholder="Any special instructions..."
            class="pixel-input resize-none"
          ></textarea>
        </div>

        <div class="flex gap-3">
          <button
            type="button"
            class="flex-1 pixel-border bg-gray-100 font-pixel text-xs py-3 hover:bg-gray-200 transition-colors"
            @click="currentStep = 1"
          >
            &lt; Back
          </button>
          <button
            class="flex-1 pixel-border bg-cozy-green text-white font-pixel text-xs py-3 shadow-pixel hover:opacity-90 active:shadow-pixel-hover active:translate-x-0.5 active:translate-y-0.5 transition-all disabled:opacity-50"
            :disabled="placingOrder"
            @click="handlePlaceOrder"
          >
            <span v-if="placingOrder" class="animate-pulse">Placing Order...</span>
            <span v-else>&#9733; Place Order &#9733;</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { useOrderStore } from '@/stores/order'
import { useToast } from '@/composables/useToast'
import { userService } from '@/api/userService'
import { paymentService } from '@/api/paymentService'
import type { Address } from '@/types'

const router = useRouter()
const cartStore = useCartStore()
const orderStore = useOrderStore()
const { showToast } = useToast()

const steps = ['Address', 'Payment', 'Confirm']
const currentStep = ref(0)

const addresses = ref<Address[]>([])
const loadingAddresses = ref(true)
const selectedAddressId = ref('')
const placingOrder = ref(false)
const orderNotes = ref('')

const selectedAddress = computed(() =>
  addresses.value.find((a) => a.id === selectedAddressId.value)
)

const paymentForm = reactive({
  cardNumber: '',
  cardHolder: '',
  expiryDate: '',
  cvv: '',
})

function formatPrice(price: number): string {
  return price.toLocaleString('tr-TR', { minimumFractionDigits: 2 })
}

async function handlePlaceOrder() {
  if (!selectedAddressId.value) {
    showToast('Please select a shipping address', 'warning')
    currentStep.value = 0
    return
  }

  placingOrder.value = true
  try {
    const order = await orderStore.createOrder({
      shippingAddressId: selectedAddressId.value,
      notes: orderNotes.value || undefined,
    })

    // Process mock payment
    try {
      await paymentService.createPayment({
        orderId: order.id,
        method: 'CREDIT_CARD',
        cardNumber: paymentForm.cardNumber,
        cardHolder: paymentForm.cardHolder,
        expiryDate: paymentForm.expiryDate,
        cvv: paymentForm.cvv,
      })
    } catch {
      // Payment is mock, continue anyway
    }

    showToast('Order placed successfully! Your loot is on its way!', 'success')
    router.push(`/orders/${order.id}`)
  } catch (err: any) {
    showToast(err.response?.data?.message || 'Failed to place order', 'error')
  } finally {
    placingOrder.value = false
  }
}

onMounted(async () => {
  // Ensure cart is loaded
  if (!cartStore.cart) {
    try {
      await cartStore.fetchCart()
    } catch {
      showToast('Failed to load cart', 'error')
      router.push('/cart')
      return
    }
  }

  if (cartStore.isEmpty) {
    showToast('Your cart is empty', 'warning')
    router.push('/cart')
    return
  }

  // Fetch addresses
  try {
    const response = await userService.getAddresses()
    addresses.value = response.data
    // Pre-select default address
    const defaultAddr = addresses.value.find((a) => a.isDefault)
    if (defaultAddr) {
      selectedAddressId.value = defaultAddr.id
    } else if (addresses.value.length > 0) {
      selectedAddressId.value = addresses.value[0]!.id
    }
  } catch {
    showToast('Failed to load addresses', 'error')
  } finally {
    loadingAddresses.value = false
  }
})
</script>
