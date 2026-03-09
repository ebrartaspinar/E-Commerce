<template>
  <div class="page-container">
    <!-- Loading -->
    <div v-if="loading" class="animate-pulse">
      <div class="flex flex-col lg:flex-row gap-8">
        <div class="lg:w-1/2">
          <div class="bg-gray-200 h-96 pixel-border"></div>
        </div>
        <div class="lg:w-1/2 space-y-4">
          <div class="bg-gray-200 h-8 w-3/4 rounded"></div>
          <div class="bg-gray-200 h-4 w-1/2 rounded"></div>
          <div class="bg-gray-200 h-6 w-1/4 rounded"></div>
          <div class="bg-gray-200 h-32 rounded"></div>
        </div>
      </div>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="text-center py-16">
      <div class="font-pixel text-4xl text-gray-300 mb-4">&#128123;</div>
      <h3 class="font-pixel text-sm text-cozy-dark mb-2">Product Not Found</h3>
      <p class="font-retro text-xl text-gray-400 mb-4">This item may have been removed from the marketplace.</p>
      <router-link
        to="/products"
        class="inline-block pixel-border bg-primary-500 text-white font-pixel text-xs py-2 px-6 shadow-pixel"
      >
        Browse Products
      </router-link>
    </div>

    <!-- Product Content -->
    <div v-else-if="product">
      <!-- Breadcrumb -->
      <nav class="flex items-center gap-2 mb-6 font-body text-sm text-gray-500">
        <router-link to="/" class="hover:text-primary-500">Home</router-link>
        <span>/</span>
        <router-link to="/products" class="hover:text-primary-500">Products</router-link>
        <span>/</span>
        <span class="text-cozy-dark">{{ product.name }}</span>
      </nav>

      <div class="flex flex-col lg:flex-row gap-8">
        <!-- Image Gallery -->
        <div class="lg:w-1/2">
          <div class="pixel-card p-2">
            <!-- Main Image -->
            <div class="relative overflow-hidden bg-gray-50 mb-3" style="min-height: 300px;">
              <img
                v-if="selectedImage"
                :src="selectedImage.url"
                :alt="selectedImage.altText || product.name"
                class="w-full h-80 sm:h-96 object-contain"
              />
              <div v-else class="w-full h-80 sm:h-96 flex items-center justify-center font-pixel text-4xl text-gray-300">
                &#128247;
              </div>

              <!-- Discount Badge -->
              <div
                v-if="product.discountPrice"
                class="absolute top-3 left-3 pixel-badge bg-cozy-red text-white"
              >
                -{{ discountPercent }}%
              </div>
            </div>

            <!-- Thumbnail Row -->
            <div v-if="product.images.length > 1" class="flex gap-2 overflow-x-auto pb-2">
              <button
                v-for="(img, idx) in product.images"
                :key="img.id"
                class="flex-shrink-0 w-16 h-16 border-2 transition-colors overflow-hidden"
                :class="selectedImageIndex === idx
                  ? 'border-primary-500'
                  : 'border-cozy-brown hover:border-primary-300'"
                @click="selectedImageIndex = idx"
              >
                <img :src="img.url" :alt="img.altText" class="w-full h-full object-cover" />
              </button>
            </div>
          </div>
        </div>

        <!-- Product Info -->
        <div class="lg:w-1/2">
          <!-- Brand & Seller -->
          <div class="flex items-center gap-3 mb-2">
            <span class="pixel-badge bg-cozy-peach text-cozy-brown">{{ product.brand }}</span>
            <span class="font-body text-sm text-gray-400">by {{ product.sellerName }}</span>
          </div>

          <!-- Name -->
          <h1 class="font-pixel text-base sm:text-lg text-cozy-dark mb-3 leading-relaxed">
            {{ product.name }}
          </h1>

          <!-- Rating -->
          <div class="flex items-center gap-2 mb-4">
            <div class="flex">
              <span
                v-for="star in 5"
                :key="star"
                class="text-lg"
                :class="star <= Math.round(product.averageRating) ? 'text-cozy-gold' : 'text-gray-300'"
              >
                &#9733;
              </span>
            </div>
            <span class="font-body text-sm text-gray-500">
              {{ product.averageRating.toFixed(1) }} ({{ product.reviewCount }} reviews)
            </span>
          </div>

          <!-- Price -->
          <div class="mb-6">
            <div v-if="product.discountPrice" class="flex items-center gap-3">
              <span class="font-pixel text-xl text-cozy-red">
                {{ formatPrice(product.discountPrice) }} TL
              </span>
              <span class="font-body text-lg text-gray-400 line-through">
                {{ formatPrice(product.price) }} TL
              </span>
            </div>
            <div v-else>
              <span class="font-pixel text-xl text-primary-600">
                {{ formatPrice(product.price) }} TL
              </span>
            </div>
          </div>

          <!-- Stock Status -->
          <div class="mb-6">
            <span
              v-if="product.stockQuantity > 0"
              class="pixel-badge bg-green-50 text-cozy-green border-cozy-green"
            >
              &#10003; In Stock ({{ product.stockQuantity }} left)
            </span>
            <span v-else class="pixel-badge bg-red-50 text-cozy-red border-cozy-red">
              &#10007; Out of Stock
            </span>
          </div>

          <!-- Quantity + Add to Cart -->
          <div class="flex items-center gap-4 mb-6">
            <div class="flex items-center pixel-border">
              <button
                class="px-3 py-2 bg-gray-100 hover:bg-gray-200 font-pixel text-xs transition-colors"
                :disabled="quantity <= 1"
                @click="quantity--"
              >
                -
              </button>
              <span class="px-4 py-2 font-body text-center min-w-[3rem] bg-white">
                {{ quantity }}
              </span>
              <button
                class="px-3 py-2 bg-gray-100 hover:bg-gray-200 font-pixel text-xs transition-colors"
                :disabled="quantity >= product.stockQuantity"
                @click="quantity++"
              >
                +
              </button>
            </div>

            <button
              class="flex-1 pixel-border bg-primary-500 text-white font-pixel text-xs py-3 px-4 shadow-pixel hover:bg-primary-600 active:shadow-pixel-hover active:translate-x-0.5 active:translate-y-0.5 transition-all disabled:opacity-50 disabled:cursor-not-allowed"
              :disabled="product.stockQuantity === 0 || addingToCart"
              @click="handleAddToCart"
            >
              <span v-if="addingToCart" class="animate-pulse">Adding...</span>
              <span v-else>&#128722; Add to Cart</span>
            </button>
          </div>

          <!-- Description -->
          <div class="pixel-card bg-cozy-cream p-4 mb-4">
            <h3 class="font-pixel text-xs text-cozy-brown mb-2">Description</h3>
            <p class="font-body text-sm text-gray-600 leading-relaxed whitespace-pre-line">
              {{ product.description }}
            </p>
          </div>

          <!-- Product Details -->
          <div class="pixel-card p-4">
            <h3 class="font-pixel text-xs text-cozy-brown mb-3">Product Details</h3>
            <dl class="space-y-2">
              <div class="flex justify-between font-body text-sm">
                <dt class="text-gray-500">SKU</dt>
                <dd class="text-cozy-dark">{{ product.sku }}</dd>
              </div>
              <div class="flex justify-between font-body text-sm">
                <dt class="text-gray-500">Brand</dt>
                <dd class="text-cozy-dark">{{ product.brand }}</dd>
              </div>
              <div class="flex justify-between font-body text-sm">
                <dt class="text-gray-500">Category</dt>
                <dd class="text-cozy-dark">{{ product.categoryName }}</dd>
              </div>
              <div class="flex justify-between font-body text-sm">
                <dt class="text-gray-500">Seller</dt>
                <dd class="text-cozy-dark">{{ product.sellerName }}</dd>
              </div>
            </dl>
          </div>
        </div>
      </div>

      <!-- Reviews Section -->
      <section class="mt-12">
        <h2 class="section-title">&#9733; Reviews ({{ product.reviewCount }})</h2>

        <!-- Rating Breakdown -->
        <div class="pixel-card mb-6 p-6">
          <div class="flex flex-col sm:flex-row gap-8">
            <!-- Average -->
            <div class="text-center sm:text-left">
              <div class="font-pixel text-3xl text-cozy-gold mb-1">{{ product.averageRating.toFixed(1) }}</div>
              <div class="flex justify-center sm:justify-start mb-1">
                <span
                  v-for="star in 5"
                  :key="star"
                  class="text-lg"
                  :class="star <= Math.round(product.averageRating) ? 'text-cozy-gold' : 'text-gray-300'"
                >
                  &#9733;
                </span>
              </div>
              <p class="font-body text-sm text-gray-500">{{ product.reviewCount }} reviews</p>
            </div>

            <!-- Star bars -->
            <div class="flex-1 space-y-2">
              <div v-for="star in [5, 4, 3, 2, 1]" :key="star" class="flex items-center gap-2">
                <span class="font-pixel text-xs w-8 text-right">{{ star }}&#9733;</span>
                <div class="flex-1 h-3 bg-gray-200 border border-gray-300">
                  <div
                    class="h-full bg-cozy-gold transition-all"
                    :style="{ width: getStarPercentage(star) + '%' }"
                  ></div>
                </div>
                <span class="font-body text-xs text-gray-400 w-8">
                  {{ getStarCount(star) }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- Write Review Form -->
        <div v-if="isAuthenticated" class="pixel-card mb-6 p-6">
          <h3 class="font-pixel text-xs text-cozy-brown mb-3">Write a Review</h3>
          <form @submit.prevent="submitReview">
            <div class="mb-3">
              <label class="font-pixel text-xs text-cozy-brown block mb-1">Rating</label>
              <div class="flex gap-1">
                <button
                  v-for="star in 5"
                  :key="star"
                  type="button"
                  class="text-2xl transition-colors"
                  :class="reviewForm.rating >= star ? 'text-cozy-gold' : 'text-gray-300'"
                  @click="reviewForm.rating = star"
                >
                  &#9733;
                </button>
              </div>
            </div>
            <div class="mb-3">
              <label class="font-pixel text-xs text-cozy-brown block mb-1">Comment</label>
              <textarea
                v-model="reviewForm.comment"
                rows="3"
                placeholder="Share your experience..."
                class="pixel-input resize-none"
                required
              ></textarea>
            </div>
            <button
              type="submit"
              :disabled="reviewForm.rating === 0 || submittingReview"
              class="pixel-border bg-primary-500 text-white font-pixel text-xs py-2 px-6 shadow-pixel hover:bg-primary-600 disabled:opacity-50 transition-colors"
            >
              <span v-if="submittingReview" class="animate-pulse">Submitting...</span>
              <span v-else>Submit Review</span>
            </button>
          </form>
        </div>

        <!-- Review List -->
        <div v-if="reviews.length > 0" class="space-y-4">
          <div v-for="review in reviews" :key="review.id" class="pixel-card p-4">
            <div class="flex items-center justify-between mb-2">
              <div class="flex items-center gap-2">
                <div class="w-8 h-8 pixel-border bg-primary-100 flex items-center justify-center font-pixel text-xs text-primary-600">
                  {{ review.userName.charAt(0).toUpperCase() }}
                </div>
                <div>
                  <p class="font-body text-sm font-semibold">{{ review.userName }}</p>
                  <div class="flex">
                    <span
                      v-for="star in 5"
                      :key="star"
                      class="text-xs"
                      :class="star <= review.rating ? 'text-cozy-gold' : 'text-gray-300'"
                    >
                      &#9733;
                    </span>
                  </div>
                </div>
              </div>
              <span class="font-body text-xs text-gray-400">
                {{ formatDate(review.createdAt) }}
              </span>
            </div>
            <p class="font-body text-sm text-gray-600">{{ review.comment }}</p>
          </div>
        </div>

        <div v-else-if="!loading" class="text-center py-8">
          <p class="font-retro text-xl text-gray-400">No reviews yet. Be the first to review this item!</p>
        </div>

        <!-- Load More Reviews -->
        <div v-if="hasMoreReviews" class="text-center mt-4">
          <button
            class="pixel-border bg-white font-pixel text-xs py-2 px-6 shadow-pixel hover:bg-gray-50"
            :disabled="loadingReviews"
            @click="loadMoreReviews"
          >
            <span v-if="loadingReviews" class="animate-pulse">Loading...</span>
            <span v-else>Load More Reviews</span>
          </button>
        </div>
      </section>

      <!-- Related Products -->
      <section v-if="relatedProducts.length > 0" class="mt-12">
        <h2 class="section-title">&#128161; You Might Also Like</h2>
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
          <ProductCard
            v-for="rp in relatedProducts"
            :key="rp.id"
            :product="rp"
          />
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useProductStore } from '@/stores/product'
import { useCartStore } from '@/stores/cart'
import { useAuthStore } from '@/stores/auth'
import { useToast } from '@/composables/useToast'
import ProductCard from '@/components/common/ProductCard.vue'
import { productService } from '@/api/productService'
import type { Product, Review } from '@/types'

const route = useRoute()
const productStore = useProductStore()
const cartStore = useCartStore()
const authStore = useAuthStore()
const { showToast } = useToast()

const loading = ref(true)
const error = ref(false)
const product = computed(() => productStore.currentProduct)
const isAuthenticated = computed(() => authStore.isAuthenticated)

const selectedImageIndex = ref(0)
const selectedImage = computed(() => {
  if (!product.value || product.value.images.length === 0) return null
  return product.value.images[selectedImageIndex.value] || product.value.images[0]
})

const quantity = ref(1)
const addingToCart = ref(false)

// Reviews
const reviews = ref<Review[]>([])
const reviewPage = ref(0)
const hasMoreReviews = ref(false)
const loadingReviews = ref(false)
const submittingReview = ref(false)
const reviewForm = reactive({
  rating: 0,
  comment: '',
})

// Related products
const relatedProducts = ref<Product[]>([])

const discountPercent = computed(() => {
  if (!product.value || !product.value.discountPrice) return 0
  return Math.round(
    ((product.value.price - product.value.discountPrice) / product.value.price) * 100
  )
})

function formatPrice(price: number): string {
  return price.toLocaleString('tr-TR', { minimumFractionDigits: 2 })
}

function formatDate(dateStr: string): string {
  return new Date(dateStr).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })
}

// Star breakdown (estimated from average since we only have aggregate data)
function getStarCount(star: number): number {
  if (!product.value || product.value.reviewCount === 0) return 0
  const totalReviews = reviews.value.length
  return reviews.value.filter((r) => r.rating === star).length
}

function getStarPercentage(star: number): number {
  const total = reviews.value.length
  if (total === 0) return 0
  return (getStarCount(star) / total) * 100
}

async function handleAddToCart() {
  if (!authStore.isAuthenticated) {
    showToast('Please login to add items to cart', 'warning')
    return
  }

  addingToCart.value = true
  try {
    await cartStore.addItem(product.value!.id, quantity.value)
    showToast(`Added ${quantity.value}x ${product.value!.name} to cart!`, 'success')
  } catch (err: any) {
    showToast(err.response?.data?.message || 'Failed to add to cart', 'error')
  } finally {
    addingToCart.value = false
  }
}

async function fetchReviews(page: number = 0) {
  if (!product.value) return
  loadingReviews.value = true
  try {
    const response = await productService.getProductReviews(product.value.id, page, 5)
    if (page === 0) {
      reviews.value = response.data.content
    } else {
      reviews.value.push(...response.data.content)
    }
    hasMoreReviews.value = !response.data.last
    reviewPage.value = page
  } catch {
    // Reviews failed to load
  } finally {
    loadingReviews.value = false
  }
}

function loadMoreReviews() {
  fetchReviews(reviewPage.value + 1)
}

async function submitReview() {
  if (!product.value || reviewForm.rating === 0) return
  submittingReview.value = true
  try {
    const response = await productService.createReview(product.value.id, {
      rating: reviewForm.rating,
      comment: reviewForm.comment,
    })
    reviews.value.unshift(response.data)
    reviewForm.rating = 0
    reviewForm.comment = ''
    showToast('Review submitted successfully!', 'success')
  } catch (err: any) {
    showToast(err.response?.data?.message || 'Failed to submit review', 'error')
  } finally {
    submittingReview.value = false
  }
}

async function fetchRelatedProducts() {
  if (!product.value) return
  try {
    const response = await productService.getProducts({
      categoryId: product.value.categoryId,
      size: 4,
      page: 0,
    })
    relatedProducts.value = response.data.content.filter(
      (p) => p.id !== product.value!.id
    )
  } catch {
    // Related products failed
  }
}

async function loadProduct() {
  loading.value = true
  error.value = false
  selectedImageIndex.value = 0
  quantity.value = 1

  try {
    const slug = route.params.slug as string
    await productStore.fetchProductBySlug(slug)

    if (!product.value) {
      error.value = true
      return
    }

    await Promise.all([
      fetchReviews(0),
      fetchRelatedProducts(),
    ])
  } catch {
    error.value = true
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadProduct()
})

watch(
  () => route.params.slug,
  () => {
    if (route.params.slug) {
      loadProduct()
    }
  }
)
</script>
