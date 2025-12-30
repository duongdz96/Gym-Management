<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useAuthStore } from '@/stores/useAuthStore'
import axios from 'axios'
import { useToast } from 'vue-toastification'
import PTHeader from '@/components/PTHeader.vue'
import ReceptionHeader from '@/components/ReceptionHeader.vue'
import ManagerHeader from '@/components/ManagerHeader.vue'
import CustomerHeader from '@/components/CustomerHeader.vue'
import TeacherHeader from '@/components/TeacherHeader.vue'

const authStore = useAuthStore()
const toast = useToast()

// Form data
const fullName = ref('')
const gender = ref('')
const dob = ref('')
const phone = ref('')
const email = ref('') // Read-only

const isLoading = ref(false)
const isEditing = ref(false)

// Determine header component based on user role
const headerComponent = computed(() => {
  const role = authStore.user?.role
  if (role === 'PT') return PTHeader
  if (role === 'RECEPTIONIST') return ReceptionHeader
  if (role === 'MANAGER') return ManagerHeader
  if (role === 'MEMBER') return CustomerHeader
  if (role === 'TEACHER') return TeacherHeader
  return null
})

// Load user data from store
onMounted(() => {
  if (authStore.user) {
    fullName.value = authStore.user.fullName || ''
    gender.value = authStore.user.gender || ''
    phone.value = authStore.user.phone || ''
    email.value = authStore.user.email || ''
    
    // Convert date from backend format
    if (authStore.user.dob) {
      const date = new Date(authStore.user.dob)
      dob.value = date.toISOString().split('T')[0]
    }
  }
})

const handleSubmit = async () => {
  if (!fullName.value || !gender.value || !dob.value || !phone.value) {
    toast.error('Vui lòng điền đầy đủ thông tin')
    return
  }

  isLoading.value = true

  try {
    const response = await axios.patch(
      `http://localhost:8080/api/users/${authStore.user.id}/profile`,
      {
        fullName: fullName.value,
        gender: gender.value,
        dob: new Date(dob.value).toISOString(),
        phone: phone.value
      },
      {
        headers: {
          Authorization: `Bearer ${authStore.accessToken}`
        }
      }
    )

    // Update user in store and localStorage using authStore method
    const updatedUser = {
      ...authStore.user,
      fullName: fullName.value,
      gender: gender.value,
      dob: new Date(dob.value).toISOString(),
      phone: phone.value
    }
    
    authStore.updateUser(updatedUser)

    toast.success('Cập nhật thông tin thành công!')
    isEditing.value = false
  } catch (error) {
    toast.error('Có lỗi xảy ra khi cập nhật thông tin')
  } finally {
    isLoading.value = false
  }
}

const cancelEdit = () => {
  // Reset to original values
  if (authStore.user) {
    fullName.value = authStore.user.fullName || ''
    gender.value = authStore.user.gender || ''
    phone.value = authStore.user.phone || ''
    
    if (authStore.user.dob) {
      const date = new Date(authStore.user.dob)
      dob.value = date.toISOString().split('T')[0]
    }
  }
  isEditing.value = false
}
</script>

<template>
  <div class="profile-page-wrapper">
    <component v-if="headerComponent" :is="headerComponent" />
    <div class="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 py-8 px-4">
    <div class="max-w-3xl mx-auto">
      <!-- Profile Form -->
      <div class="bg-white rounded-2xl shadow-lg p-8">
        <div class="mb-6">
          <h1 class="text-3xl font-bold text-gray-900">Thông Tin Cá Nhân</h1>
          <p class="text-gray-600 mt-1">Quản lý thông tin tài khoản của bạn</p>
        </div>
        
        <form @submit.prevent="handleSubmit" class="space-y-6">
          <!-- Full Name -->
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2" for="fullName">
              Họ và Tên <span class="text-red-600">*</span>
            </label>
            <input
              v-model="fullName"
              id="fullName"
              type="text"
              :disabled="!isEditing"
              class="w-full rounded-lg border-2 px-4 py-3 text-sm transition-colors outline-none"
              :class="isEditing 
                ? 'border-gray-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-200 bg-white' 
                : 'border-gray-200 bg-gray-50 text-gray-600 cursor-not-allowed'"
              placeholder="Nhập họ và tên"
              required
            />
          </div>

          <!-- Email (Read-only) -->
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2" for="email">
              Email
            </label>
            <div class="relative">
              <input
                v-model="email"
                id="email"
                type="email"
                disabled
                class="w-full rounded-lg border-2 border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-600 cursor-not-allowed outline-none"
              />
              <div class="absolute right-3 top-1/2 transform -translate-y-1/2">
                <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path>
                </svg>
              </div>
            </div>
            <p class="text-xs text-gray-500 mt-1">Email không thể thay đổi</p>
          </div>

          <!-- Gender -->
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2" for="gender">
              Giới Tính <span class="text-red-600">*</span>
            </label>
            <select
              v-model="gender"
              id="gender"
              :disabled="!isEditing"
              class="w-full rounded-lg border-2 px-4 py-3 text-sm transition-colors outline-none"
              :class="isEditing 
                ? 'border-gray-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-200 bg-white' 
                : 'border-gray-200 bg-gray-50 text-gray-600 cursor-not-allowed'"
              required
            >
              <option value="">Chọn giới tính</option>
              <option value="Male">Nam</option>
              <option value="Female">Nữ</option>
              <option value="Other">Khác</option>
            </select>
          </div>

          <!-- Date of Birth -->
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2" for="dob">
              Ngày Sinh <span class="text-red-600">*</span>
            </label>
            <input
              v-model="dob"
              id="dob"
              type="date"
              :disabled="!isEditing"
              class="w-full rounded-lg border-2 px-4 py-3 text-sm transition-colors outline-none"
              :class="isEditing 
                ? 'border-gray-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-200 bg-white' 
                : 'border-gray-200 bg-gray-50 text-gray-600 cursor-not-allowed'"
              required
            />
          </div>

          <!-- Phone -->
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2" for="phone">
              Số Điện Thoại <span class="text-red-600">*</span>
            </label>
            <input
              v-model="phone"
              id="phone"
              type="tel"
              :disabled="!isEditing"
              class="w-full rounded-lg border-2 px-4 py-3 text-sm transition-colors outline-none"
              :class="isEditing 
                ? 'border-gray-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-200 bg-white' 
                : 'border-gray-200 bg-gray-50 text-gray-600 cursor-not-allowed'"
              placeholder="Nhập số điện thoại"
              required
            />
          </div>

          <!-- Action Buttons -->
          <div class="flex gap-4 pt-4">
            <button
              v-if="!isEditing"
              type="button"
              @click="isEditing = true"
              class="flex-1 bg-gradient-to-r from-blue-600 to-blue-700 text-white py-3 rounded-lg font-semibold hover:from-blue-700 hover:to-blue-800 transition-all shadow-lg hover:shadow-xl flex items-center justify-center gap-2"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
              </svg>
              Chỉnh Sửa
            </button>

            <template v-else>
              <button
                type="button"
                @click="cancelEdit"
                :disabled="isLoading"
                class="flex-1 bg-gray-200 text-gray-700 py-3 rounded-lg font-semibold hover:bg-gray-300 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
              >
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                </svg>
                Hủy
              </button>

              <button
                type="submit"
                :disabled="isLoading"
                class="flex-1 bg-gradient-to-r from-green-600 to-green-700 text-white py-3 rounded-lg font-semibold hover:from-green-700 hover:to-green-800 transition-all shadow-lg hover:shadow-xl disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
              >
                <svg v-if="!isLoading" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                </svg>
                <svg v-else class="w-5 h-5 animate-spin" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
                </svg>
                {{ isLoading ? 'Đang lưu...' : 'Lưu Thay Đổi' }}
              </button>
            </template>
          </div>
        </form>
      </div>

      <!-- Info Card -->
      <div class="bg-blue-50 border-2 border-blue-200 rounded-xl p-6 mt-6">
        <div class="flex items-start gap-3">
          <svg class="w-6 h-6 text-blue-600 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
          </svg>
          <div>
            <h3 class="font-semibold text-blue-900 mb-1">Lưu ý</h3>
            <ul class="text-sm text-blue-800 space-y-1">
              <li>• Email không thể thay đổi sau khi đăng ký</li>
              <li>• Để thay đổi mật khẩu, vui lòng sử dụng chức năng "Đổi mật khẩu"</li>
              <li>• Thông tin cá nhân sẽ được bảo mật tuyệt đối</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
  </div>
</template>

<style scoped>
.profile-page-wrapper {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: white;
  z-index: 9999;
  overflow-y: auto;
}
</style>
