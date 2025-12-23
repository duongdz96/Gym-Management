<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import api from '@/services/api'

type Teacher = {
  id: number
  email: string
  password: string
  fullName: string
  dob: string
  gender: string
  phone: string
  role: string
  position: string | null
  specialize: string | null
  deleted: boolean
}

// --- State ---
const route = useRoute()
const teacherId = route.params.id as string
const teacher = ref<Teacher | null>(null)
const isLoading = ref(true)

// --- Lifecycle ---
onMounted(async () => {
  try {
    isLoading.value = true
    const res = await api.get(`/teachers/${teacherId}`)
    teacher.value = res.data
  } catch (err) {
    console.error('Error fetching teacher:', err)
  } finally {
    isLoading.value = false
  }
})

// --- Helpers ---
const formatDate = (dateString: string) => {
  if (!dateString) return 'N/A'
  try {
    return new Date(dateString).toLocaleDateString('vi-VN')
  } catch { return dateString }
}

const getGenderText = (gender: string) => {
  return gender === 'Male' ? 'Nam' : gender === 'Female' ? 'Nữ' : gender
}
</script>

<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <!-- Header -->
    <div class="flex justify-between items-center mb-6">
      <div class="flex items-center gap-3">
        <button 
          @click="$router.back()" 
          class="p-2 bg-white border border-gray-200 rounded-lg hover:bg-gray-100 text-gray-600 transition shadow-sm"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m15 18-6-6 6-6"/></svg>
        </button>
        <div>
          <h1 class="text-xl font-bold text-gray-800">Chi Tiết Giáo Viên</h1>
          <p class="text-xs text-gray-500">Thông tin chi tiết về giáo viên</p>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="isLoading" class="flex justify-center items-center h-64">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-emerald-600"></div>
    </div>

    <!-- Teacher Detail -->
    <div v-else-if="teacher" class="space-y-6">
      <!-- Profile Card -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
        <div class="flex items-center gap-6">
          <div class="w-20 h-20 rounded-full bg-green-100 text-green-600 flex items-center justify-center font-bold text-2xl">
            {{ teacher.fullName.charAt(0).toUpperCase() }}
          </div>
          <div class="flex-1">
            <h2 class="text-2xl font-bold text-gray-900">{{ teacher.fullName }}</h2>
            <p class="text-gray-600">{{ teacher.email }}</p>
            <div class="flex gap-4 mt-2">
              <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-emerald-100 text-emerald-800">
                {{ teacher.role }}
              </span>
              <span v-if="teacher.position" class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-purple-100 text-purple-800">
                {{ teacher.position }}
              </span>
              <span v-if="teacher.specialize" class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-indigo-100 text-indigo-800">
                {{ teacher.specialize }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Details Grid -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <!-- Personal Information -->
        <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Thông Tin Cá Nhân</h3>
          <div class="space-y-3">
            <div class="flex justify-between">
              <span class="text-gray-600">Họ và tên:</span>
              <span class="font-medium text-gray-900">{{ teacher.fullName }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-600">Email:</span>
              <span class="font-medium text-gray-900">{{ teacher.email }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-600">Số điện thoại:</span>
              <span class="font-medium text-gray-900">{{ teacher.phone }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-600">Giới tính:</span>
              <span class="font-medium text-gray-900">{{ getGenderText(teacher.gender) }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-600">Ngày sinh:</span>
              <span class="font-medium text-gray-900">{{ formatDate(teacher.dob) }}</span>
            </div>
          </div>
        </div>

        <!-- Professional Information -->
        <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Thông Tin Chuyên Môn</h3>
          <div class="space-y-3">
            <div class="flex justify-between">
              <span class="text-gray-600">Vị trí:</span>
              <span class="font-medium text-gray-900">{{ teacher.position || 'Chưa cập nhật' }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-600">Chuyên môn:</span>
              <span class="font-medium text-gray-900">{{ teacher.specialize || 'Chưa cập nhật' }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-600">Vai trò:</span>
              <span class="font-medium text-gray-900">{{ teacher.role }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-600">Trạng thái:</span>
              <span class="font-medium text-gray-900">{{ teacher.deleted ? 'Đã xóa' : 'Hoạt động' }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Not Found -->
    <div v-else class="text-center text-gray-500 py-12">
      <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round" class="mx-auto mb-4 text-gray-300"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M22 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
      <p>Không tìm thấy thông tin giáo viên</p>
    </div>
  </div>
</template>