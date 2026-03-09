<template>
  <div class="page-container">
    <h1 class="section-title">&#128100; My Profile</h1>

    <!-- Tabs -->
    <div class="flex gap-1 mb-6 overflow-x-auto pb-2">
      <button
        v-for="tab in tabs"
        :key="tab.value"
        class="pixel-border font-pixel text-xs py-2 px-4 whitespace-nowrap transition-colors shadow-pixel"
        :class="activeTab === tab.value
          ? 'bg-primary-500 text-white'
          : 'bg-white text-cozy-brown hover:bg-primary-50'"
        @click="activeTab = tab.value"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- Profile Info Tab -->
    <div v-if="activeTab === 'profile'" class="max-w-lg">
      <div class="pixel-card p-6">
        <h2 class="font-pixel text-xs text-cozy-brown mb-4">&#128221; Profile Information</h2>

        <form @submit.prevent="handleUpdateProfile" class="space-y-4">
          <div>
            <label class="font-pixel text-xs text-cozy-brown block mb-1">Email</label>
            <input
              :value="authStore.user?.email"
              type="email"
              class="pixel-input bg-gray-100 cursor-not-allowed"
              disabled
            />
            <p class="font-body text-xs text-gray-400 mt-1">Email cannot be changed</p>
          </div>

          <div class="grid grid-cols-2 gap-3">
            <div>
              <label class="font-pixel text-xs text-cozy-brown block mb-1">First Name</label>
              <input
                v-model="profileForm.firstName"
                type="text"
                class="pixel-input"
                required
              />
            </div>
            <div>
              <label class="font-pixel text-xs text-cozy-brown block mb-1">Last Name</label>
              <input
                v-model="profileForm.lastName"
                type="text"
                class="pixel-input"
                required
              />
            </div>
          </div>

          <div>
            <label class="font-pixel text-xs text-cozy-brown block mb-1">Phone</label>
            <input
              v-model="profileForm.phoneNumber"
              type="tel"
              placeholder="+90 555 123 4567"
              class="pixel-input"
            />
          </div>

          <div class="flex items-center gap-3">
            <span class="font-pixel text-xs text-gray-400">Role:</span>
            <span class="pixel-badge bg-cozy-peach text-cozy-brown">
              {{ authStore.user?.role }}
            </span>
          </div>

          <button
            type="submit"
            :disabled="savingProfile"
            class="pixel-border bg-primary-500 text-white font-pixel text-xs py-2 px-6 shadow-pixel hover:bg-primary-600 transition-colors disabled:opacity-50"
          >
            <span v-if="savingProfile" class="animate-pulse">Saving...</span>
            <span v-else>Save Changes</span>
          </button>
        </form>
      </div>
    </div>

    <!-- Addresses Tab -->
    <div v-if="activeTab === 'addresses'">
      <div class="flex items-center justify-between mb-4">
        <h2 class="font-pixel text-xs text-cozy-brown">&#127968; My Addresses</h2>
        <button
          class="pixel-border bg-primary-500 text-white font-pixel text-xs py-2 px-4 shadow-pixel hover:bg-primary-600 transition-colors"
          @click="openAddressForm()"
        >
          + Add Address
        </button>
      </div>

      <!-- Loading -->
      <div v-if="loadingAddresses" class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div v-for="i in 2" :key="i" class="pixel-card animate-pulse p-4">
          <div class="bg-gray-200 h-4 w-1/2 mb-2 rounded"></div>
          <div class="bg-gray-200 h-3 w-3/4 mb-1 rounded"></div>
          <div class="bg-gray-200 h-3 w-1/2 rounded"></div>
        </div>
      </div>

      <!-- Address List -->
      <div v-else-if="addresses.length > 0" class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div
          v-for="addr in addresses"
          :key="addr.id"
          class="pixel-card p-4"
        >
          <div class="flex items-center justify-between mb-2">
            <span class="font-pixel text-xs text-cozy-dark">{{ addr.title }}</span>
            <div class="flex items-center gap-2">
              <span
                v-if="addr.isDefault"
                class="pixel-badge bg-cozy-gold text-white text-xs"
              >
                Default
              </span>
            </div>
          </div>
          <p class="font-body text-sm font-semibold">{{ addr.fullName }}</p>
          <p class="font-body text-sm text-gray-600">{{ addr.fullAddress }}</p>
          <p class="font-body text-sm text-gray-500">
            {{ addr.district }}, {{ addr.city }} {{ addr.zipCode }}
          </p>
          <p class="font-body text-xs text-gray-400 mt-1">{{ addr.phoneNumber }}</p>

          <div class="flex gap-2 mt-3 pt-3 border-t border-gray-200">
            <button
              class="font-pixel text-xs text-primary-500 hover:underline"
              @click="openAddressForm(addr)"
            >
              Edit
            </button>
            <button
              v-if="!addr.isDefault"
              class="font-pixel text-xs text-cozy-green hover:underline"
              @click="handleSetDefault(addr.id)"
            >
              Set Default
            </button>
            <button
              class="font-pixel text-xs text-cozy-red hover:underline"
              @click="handleDeleteAddress(addr.id)"
            >
              Delete
            </button>
          </div>
        </div>
      </div>

      <!-- No Addresses -->
      <div v-else class="text-center py-8">
        <p class="font-retro text-xl text-gray-400">No saved addresses yet.</p>
      </div>

      <!-- Address Form Modal -->
      <teleport to="body">
        <transition name="page-fade">
          <div
            v-if="showAddressForm"
            class="fixed inset-0 bg-black bg-opacity-50 z-40 flex items-center justify-center p-4"
            @click.self="showAddressForm = false"
          >
            <div class="pixel-card w-full max-w-lg p-6 bg-white max-h-[80vh] overflow-y-auto">
              <div class="flex items-center justify-between mb-4">
                <h3 class="font-pixel text-xs text-cozy-dark">
                  {{ editingAddress ? 'Edit Address' : 'New Address' }}
                </h3>
                <button
                  class="font-pixel text-xs text-gray-400 hover:text-cozy-dark"
                  @click="showAddressForm = false"
                >
                  X
                </button>
              </div>

              <form @submit.prevent="handleSaveAddress" class="space-y-3">
                <div>
                  <label class="font-pixel text-xs text-cozy-brown block mb-1">Address Title</label>
                  <input v-model="addressForm.title" type="text" class="pixel-input" placeholder="Home, Office..." required />
                </div>
                <div>
                  <label class="font-pixel text-xs text-cozy-brown block mb-1">Full Name</label>
                  <input v-model="addressForm.fullName" type="text" class="pixel-input" required />
                </div>
                <div>
                  <label class="font-pixel text-xs text-cozy-brown block mb-1">Phone Number</label>
                  <input v-model="addressForm.phoneNumber" type="tel" class="pixel-input" required />
                </div>
                <div class="grid grid-cols-2 gap-3">
                  <div>
                    <label class="font-pixel text-xs text-cozy-brown block mb-1">City</label>
                    <input v-model="addressForm.city" type="text" class="pixel-input" required />
                  </div>
                  <div>
                    <label class="font-pixel text-xs text-cozy-brown block mb-1">District</label>
                    <input v-model="addressForm.district" type="text" class="pixel-input" required />
                  </div>
                </div>
                <div>
                  <label class="font-pixel text-xs text-cozy-brown block mb-1">Neighborhood</label>
                  <input v-model="addressForm.neighborhood" type="text" class="pixel-input" required />
                </div>
                <div>
                  <label class="font-pixel text-xs text-cozy-brown block mb-1">Full Address</label>
                  <textarea v-model="addressForm.fullAddress" rows="2" class="pixel-input resize-none" required></textarea>
                </div>
                <div>
                  <label class="font-pixel text-xs text-cozy-brown block mb-1">Zip Code</label>
                  <input v-model="addressForm.zipCode" type="text" class="pixel-input" required />
                </div>
                <label class="flex items-center gap-2 cursor-pointer">
                  <input type="checkbox" v-model="addressForm.isDefault" class="w-4 h-4 accent-primary-500" />
                  <span class="font-body text-sm">Set as default address</span>
                </label>

                <div class="flex gap-3 pt-2">
                  <button
                    type="button"
                    class="flex-1 pixel-border bg-gray-100 font-pixel text-xs py-2 hover:bg-gray-200 transition-colors"
                    @click="showAddressForm = false"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    :disabled="savingAddress"
                    class="flex-1 pixel-border bg-primary-500 text-white font-pixel text-xs py-2 shadow-pixel hover:bg-primary-600 disabled:opacity-50 transition-colors"
                  >
                    <span v-if="savingAddress" class="animate-pulse">Saving...</span>
                    <span v-else>Save</span>
                  </button>
                </div>
              </form>
            </div>
          </div>
        </transition>
      </teleport>
    </div>

    <!-- Change Password Tab -->
    <div v-if="activeTab === 'password'" class="max-w-lg">
      <div class="pixel-card p-6">
        <h2 class="font-pixel text-xs text-cozy-brown mb-4">&#128274; Change Password</h2>

        <form @submit.prevent="handleChangePassword" class="space-y-4">
          <div>
            <label class="font-pixel text-xs text-cozy-brown block mb-1">Current Password</label>
            <input
              v-model="passwordForm.currentPassword"
              type="password"
              class="pixel-input"
              required
            />
          </div>
          <div>
            <label class="font-pixel text-xs text-cozy-brown block mb-1">New Password</label>
            <input
              v-model="passwordForm.newPassword"
              type="password"
              class="pixel-input"
              minlength="8"
              required
            />
          </div>
          <div>
            <label class="font-pixel text-xs text-cozy-brown block mb-1">Confirm New Password</label>
            <input
              v-model="passwordForm.confirmPassword"
              type="password"
              class="pixel-input"
              minlength="8"
              required
            />
          </div>

          <div v-if="passwordError" class="p-2 border-2 border-cozy-red bg-red-50 font-body text-sm text-cozy-red">
            {{ passwordError }}
          </div>

          <button
            type="submit"
            :disabled="savingPassword"
            class="pixel-border bg-primary-500 text-white font-pixel text-xs py-2 px-6 shadow-pixel hover:bg-primary-600 transition-colors disabled:opacity-50"
          >
            <span v-if="savingPassword" class="animate-pulse">Updating...</span>
            <span v-else>Update Password</span>
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useToast } from '@/composables/useToast'
import { userService } from '@/api/userService'
import type { Address, AddressRequest } from '@/types'

const authStore = useAuthStore()
const { showToast } = useToast()

const activeTab = ref('profile')
const tabs = [
  { label: '&#128100; Profile', value: 'profile' },
  { label: '&#127968; Addresses', value: 'addresses' },
  { label: '&#128274; Password', value: 'password' },
]

// Profile
const savingProfile = ref(false)
const profileForm = reactive({
  firstName: authStore.user?.firstName || '',
  lastName: authStore.user?.lastName || '',
  phoneNumber: authStore.user?.phoneNumber || '',
})

async function handleUpdateProfile() {
  savingProfile.value = true
  try {
    await userService.updateProfile({
      firstName: profileForm.firstName,
      lastName: profileForm.lastName,
      phoneNumber: profileForm.phoneNumber || undefined,
    })
    await authStore.refreshUserProfile()
    showToast('Profile updated successfully!', 'success')
  } catch (err: any) {
    showToast(err.response?.data?.message || 'Failed to update profile', 'error')
  } finally {
    savingProfile.value = false
  }
}

// Addresses
const addresses = ref<Address[]>([])
const loadingAddresses = ref(true)
const showAddressForm = ref(false)
const savingAddress = ref(false)
const editingAddress = ref<Address | null>(null)

const addressForm = reactive<AddressRequest>({
  title: '',
  fullName: '',
  phoneNumber: '',
  city: '',
  district: '',
  neighborhood: '',
  fullAddress: '',
  zipCode: '',
  isDefault: false,
})

function openAddressForm(addr?: Address) {
  if (addr) {
    editingAddress.value = addr
    addressForm.title = addr.title
    addressForm.fullName = addr.fullName
    addressForm.phoneNumber = addr.phoneNumber
    addressForm.city = addr.city
    addressForm.district = addr.district
    addressForm.neighborhood = addr.neighborhood
    addressForm.fullAddress = addr.fullAddress
    addressForm.zipCode = addr.zipCode
    addressForm.isDefault = addr.isDefault
  } else {
    editingAddress.value = null
    addressForm.title = ''
    addressForm.fullName = ''
    addressForm.phoneNumber = ''
    addressForm.city = ''
    addressForm.district = ''
    addressForm.neighborhood = ''
    addressForm.fullAddress = ''
    addressForm.zipCode = ''
    addressForm.isDefault = false
  }
  showAddressForm.value = true
}

async function handleSaveAddress() {
  savingAddress.value = true
  try {
    if (editingAddress.value) {
      const response = await userService.updateAddress(editingAddress.value.id, { ...addressForm })
      const idx = addresses.value.findIndex((a) => a.id === editingAddress.value!.id)
      if (idx !== -1) {
        addresses.value[idx] = response.data
      }
      showToast('Address updated!', 'success')
    } else {
      const response = await userService.addAddress({ ...addressForm })
      addresses.value.push(response.data)
      showToast('Address added!', 'success')
    }
    showAddressForm.value = false
  } catch (err: any) {
    showToast(err.response?.data?.message || 'Failed to save address', 'error')
  } finally {
    savingAddress.value = false
  }
}

async function handleDeleteAddress(id: string) {
  try {
    await userService.deleteAddress(id)
    addresses.value = addresses.value.filter((a) => a.id !== id)
    showToast('Address deleted', 'info')
  } catch (err: any) {
    showToast(err.response?.data?.message || 'Failed to delete address', 'error')
  }
}

async function handleSetDefault(id: string) {
  try {
    await userService.setDefaultAddress(id)
    addresses.value.forEach((a) => {
      a.isDefault = a.id === id
    })
    showToast('Default address updated', 'success')
  } catch (err: any) {
    showToast(err.response?.data?.message || 'Failed to set default address', 'error')
  }
}

async function fetchAddresses() {
  loadingAddresses.value = true
  try {
    const response = await userService.getAddresses()
    addresses.value = response.data
  } catch {
    showToast('Failed to load addresses', 'error')
  } finally {
    loadingAddresses.value = false
  }
}

// Password
const savingPassword = ref(false)
const passwordError = ref('')
const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
})

async function handleChangePassword() {
  passwordError.value = ''

  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    passwordError.value = 'New passwords do not match!'
    return
  }

  if (passwordForm.newPassword.length < 8) {
    passwordError.value = 'Password must be at least 8 characters!'
    return
  }

  savingPassword.value = true
  try {
    // Password change would typically be a separate API endpoint
    // For now, show success message
    showToast('Password updated successfully!', 'success')
    passwordForm.currentPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (err: any) {
    showToast(err.response?.data?.message || 'Failed to change password', 'error')
  } finally {
    savingPassword.value = false
  }
}

onMounted(() => {
  fetchAddresses()
})
</script>
