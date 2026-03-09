<template>
  <div class="page-container">
    <div class="flex items-center gap-3 mb-6">
      <router-link
        to="/seller/products"
        class="font-pixel text-xs text-primary-500 hover:underline"
      >
        &lt; Back
      </router-link>
      <h1 class="section-title mb-0">
        {{ isEditing ? '&#9998; Edit Product' : '+ New Product' }}
      </h1>
    </div>

    <!-- Loading -->
    <div v-if="loadingProduct" class="max-w-2xl animate-pulse space-y-4">
      <div v-for="i in 6" :key="i" class="pixel-card p-4">
        <div class="bg-gray-200 h-4 w-1/4 mb-2 rounded"></div>
        <div class="bg-gray-200 h-8 w-full rounded"></div>
      </div>
    </div>

    <!-- Form -->
    <form v-else @submit.prevent="handleSubmit" class="max-w-2xl space-y-6">
      <!-- Basic Info -->
      <div class="pixel-card p-6">
        <h2 class="font-pixel text-xs text-cozy-brown mb-4">Basic Information</h2>

        <div class="space-y-4">
          <div>
            <label class="font-pixel text-xs text-cozy-brown block mb-1">Product Name *</label>
            <input
              v-model="form.name"
              type="text"
              class="pixel-input"
              placeholder="Enter product name..."
              required
            />
          </div>

          <div>
            <label class="font-pixel text-xs text-cozy-brown block mb-1">Description *</label>
            <textarea
              v-model="form.description"
              rows="5"
              class="pixel-input resize-none"
              placeholder="Describe your product..."
              required
            ></textarea>
          </div>

          <div>
            <label class="font-pixel text-xs text-cozy-brown block mb-1">Category *</label>
            <select v-model="form.categoryId" class="pixel-input" required>
              <option value="">Select a category</option>
              <option v-for="cat in categories" :key="cat.id" :value="cat.id">
                {{ cat.name }}
              </option>
            </select>
          </div>

          <div>
            <label class="font-pixel text-xs text-cozy-brown block mb-1">Brand *</label>
            <input
              v-model="form.brand"
              type="text"
              class="pixel-input"
              placeholder="Brand name..."
              required
            />
          </div>

          <div>
            <label class="font-pixel text-xs text-cozy-brown block mb-1">SKU *</label>
            <input
              v-model="form.sku"
              type="text"
              class="pixel-input"
              placeholder="SKU-12345"
              required
            />
          </div>
        </div>
      </div>

      <!-- Pricing & Stock -->
      <div class="pixel-card p-6">
        <h2 class="font-pixel text-xs text-cozy-brown mb-4">Pricing & Stock</h2>

        <div class="space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="font-pixel text-xs text-cozy-brown block mb-1">Price (TL) *</label>
              <input
                v-model.number="form.price"
                type="number"
                step="0.01"
                min="0.01"
                class="pixel-input"
                placeholder="0.00"
                required
              />
            </div>
            <div>
              <label class="font-pixel text-xs text-cozy-brown block mb-1">Discount Price (TL)</label>
              <input
                v-model.number="form.discountPrice"
                type="number"
                step="0.01"
                min="0"
                class="pixel-input"
                placeholder="Optional"
              />
            </div>
          </div>

          <div>
            <label class="font-pixel text-xs text-cozy-brown block mb-1">Stock Quantity *</label>
            <input
              v-model.number="form.stockQuantity"
              type="number"
              min="0"
              class="pixel-input"
              placeholder="0"
              required
            />
          </div>
        </div>
      </div>

      <!-- Images -->
      <div class="pixel-card p-6">
        <h2 class="font-pixel text-xs text-cozy-brown mb-4">Product Images</h2>

        <div class="space-y-3">
          <div
            v-for="(img, idx) in form.images"
            :key="idx"
            class="flex items-center gap-3"
          >
            <div class="flex-1 grid grid-cols-1 sm:grid-cols-2 gap-2">
              <input
                v-model="img.url"
                type="url"
                class="pixel-input text-sm"
                placeholder="Image URL..."
                required
              />
              <input
                v-model="img.altText"
                type="text"
                class="pixel-input text-sm"
                placeholder="Alt text..."
              />
            </div>
            <label class="flex items-center gap-1 cursor-pointer whitespace-nowrap">
              <input
                type="radio"
                name="primaryImage"
                :checked="img.isPrimary"
                class="accent-primary-500"
                @change="setPrimaryImage(idx)"
              />
              <span class="font-body text-xs">Primary</span>
            </label>
            <button
              v-if="form.images.length > 1"
              type="button"
              class="font-pixel text-xs text-cozy-red hover:underline"
              @click="removeImage(idx)"
            >
              &#10007;
            </button>
          </div>
        </div>

        <button
          type="button"
          class="mt-3 pixel-border bg-gray-100 font-pixel text-xs py-2 px-4 hover:bg-gray-200 transition-colors"
          @click="addImage"
        >
          + Add Image
        </button>
      </div>

      <!-- Error -->
      <div v-if="error" class="p-3 border-2 border-cozy-red bg-red-50 font-body text-sm text-cozy-red">
        {{ error }}
      </div>

      <!-- Submit -->
      <div class="flex gap-3">
        <router-link
          to="/seller/products"
          class="pixel-border bg-gray-100 font-pixel text-xs py-3 px-6 hover:bg-gray-200 transition-colors text-center"
        >
          Cancel
        </router-link>
        <button
          type="submit"
          :disabled="saving"
          class="flex-1 pixel-border bg-primary-500 text-white font-pixel text-xs py-3 px-6 shadow-pixel hover:bg-primary-600 active:shadow-pixel-hover active:translate-x-0.5 active:translate-y-0.5 transition-all disabled:opacity-50"
        >
          <span v-if="saving" class="animate-pulse">Saving...</span>
          <span v-else>{{ isEditing ? 'Update Product' : 'Create Product' }}</span>
        </button>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useProductStore } from '@/stores/product'
import { sellerService } from '@/api/sellerService'
import { productService } from '@/api/productService'
import { useToast } from '@/composables/useToast'
import type { Category } from '@/types'

const route = useRoute()
const router = useRouter()
const productStore = useProductStore()
const { showToast } = useToast()

const isEditing = computed(() => !!route.params.id)
const productId = computed(() => route.params.id as string)

const loading = ref(false)
const loadingProduct = ref(false)
const saving = ref(false)
const error = ref('')
const categories = ref<Category[]>([])

interface ImageEntry {
  url: string
  altText: string
  displayOrder: number
  isPrimary: boolean
}

const form = reactive({
  name: '',
  description: '',
  price: 0,
  discountPrice: undefined as number | undefined,
  stockQuantity: 0,
  sku: '',
  brand: '',
  categoryId: '',
  images: [
    { url: '', altText: '', displayOrder: 0, isPrimary: true },
  ] as ImageEntry[],
})

function addImage() {
  form.images.push({
    url: '',
    altText: '',
    displayOrder: form.images.length,
    isPrimary: false,
  })
}

function removeImage(idx: number) {
  const wasPrimary = form.images[idx]?.isPrimary
  form.images.splice(idx, 1)
  // Re-index display orders
  form.images.forEach((img, i) => {
    img.displayOrder = i
  })
  // If removed the primary, set first as primary
  if (wasPrimary && form.images.length > 0) {
    form.images[0]!.isPrimary = true
  }
}

function setPrimaryImage(idx: number) {
  form.images.forEach((img, i) => {
    img.isPrimary = i === idx
  })
}

async function handleSubmit() {
  error.value = ''
  saving.value = true

  const validImages = form.images.filter((img) => img.url.trim() !== '')
  if (validImages.length === 0) {
    error.value = 'At least one image URL is required.'
    saving.value = false
    return
  }

  try {
    const payload = {
      name: form.name,
      description: form.description,
      price: form.price,
      discountPrice: form.discountPrice || undefined,
      stockQuantity: form.stockQuantity,
      sku: form.sku,
      brand: form.brand,
      categoryId: form.categoryId,
      images: validImages,
    }

    if (isEditing.value) {
      await sellerService.updateProduct(productId.value, payload)
      showToast('Product updated successfully!', 'success')
    } else {
      await sellerService.createProduct(payload)
      showToast('Product created successfully!', 'success')
    }
    router.push('/seller/products')
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Failed to save product. Please try again.'
    showToast('Failed to save product', 'error')
  } finally {
    saving.value = false
  }
}

async function loadExistingProduct() {
  if (!isEditing.value) return
  loadingProduct.value = true
  try {
    const response = await productService.getProduct(productId.value)
    const product = response.data
    form.name = product.name
    form.description = product.description
    form.price = product.price
    form.discountPrice = product.discountPrice ?? undefined
    form.stockQuantity = product.stockQuantity
    form.sku = product.sku
    form.brand = product.brand
    form.categoryId = product.categoryId

    if (product.images.length > 0) {
      form.images = product.images.map((img) => ({
        url: img.url,
        altText: img.altText,
        displayOrder: img.displayOrder,
        isPrimary: img.isPrimary,
      }))
    }
  } catch {
    showToast('Failed to load product', 'error')
    router.push('/seller/products')
  } finally {
    loadingProduct.value = false
  }
}

onMounted(async () => {
  try {
    await productStore.fetchCategories()
    categories.value = productStore.categories
  } catch {
    // Categories load failed
  }
  loadExistingProduct()
})
</script>
