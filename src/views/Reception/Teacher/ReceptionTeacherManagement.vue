<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import api from '@/services/api'
import { useToast } from 'vue-toastification'

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
const teachers = ref<Teacher[]>([])
const searchQuery = ref('')
const isLoading = ref(true)

// --- Lifecycle ---
onMounted(async () => {
  try {
    isLoading.value = true
    const res = await api.get('/teachers')
    teachers.value = res.data
  } catch (err) {
    console.error('Error fetching teachers:', err)
  } finally {
    isLoading.value = false
  }
})

// --- Computed ---
const filteredTeachers = computed(() => {
  return teachers.value.filter(teacher => {
    const matchesSearch = !searchQuery.value ||
      teacher.fullName.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      teacher.email.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      teacher.phone.includes(searchQuery.value)

    return matchesSearch
  })
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
          <h1 class="text-xl font-bold text-gray-800">Quản Lý Giáo Viên</h1>
          <p class="text-xs text-gray-500">Xem thông tin giáo viên</p>
        </div>
      </div>

      <div class="flex gap-2">
        <span class="px-3 py-1 bg-emerald-50 text-emerald-700 rounded-full text-sm font-semibold">
          {{ filteredTeachers.length }} giáo viên
        </span>
      </div>
    </div>

    <!-- Filters -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6 mb-6">
      <div class="flex gap-4">
        <div class="relative flex-1">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/></svg>
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Tìm kiếm theo tên, email, số điện thoại..."
            class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500"
          />
        </div>
      </div>
    </div>

    <!-- Table -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
      <div v-if="isLoading" class="animate-pulse">
        <div class="h-16 bg-gray-200 mb-4"></div>
        <div class="space-y-3 px-6 pb-6">
          <div v-for="i in 5" :key="i" class="h-12 bg-gray-200 rounded"></div>
        </div>
      </div>

      <div v-else class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Giáo Viên</th>
              <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Liên Hệ</th>
              <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Vị Trí</th>
              <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Chuyên Môn</th>
              <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Ngày Sinh</th>
              <th class="px-6 py-4 text-right text-xs font-semibold text-gray-500 uppercase tracking-wider">Thao Tác</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
            <tr v-for="teacher in filteredTeachers" :key="teacher.id" class="hover:bg-gray-50 transition">
              <td class="px-6 py-4">
                <div class="flex items-center">
                  <div class="w-10 h-10 rounded-full bg-green-100 text-green-600 flex items-center justify-center font-bold text-sm mr-3">
                    {{ teacher.fullName.charAt(0).toUpperCase() }}
                  </div>
                  <div>
                    <div class="text-sm font-semibold text-gray-900">{{ teacher.fullName }}</div>
                    <div class="text-xs text-gray-500">{{ teacher.email }}</div>
                  </div>
                </div>
              </td>

              <td class="px-6 py-4">
                <div class="text-sm text-gray-900">{{ teacher.phone }}</div>
                <div class="text-xs text-gray-500">{{ getGenderText(teacher.gender) }}</div>
              </td>

              <td class="px-6 py-4">
                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-purple-100 text-purple-800">
                  {{ teacher.position || 'Chưa cập nhật' }}
                </span>
              </td>

              <td class="px-6 py-4">
                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-indigo-100 text-indigo-800">
                  {{ teacher.specialize || 'Chưa cập nhật' }}
                </span>
              </td>

              <td class="px-6 py-4 text-sm text-gray-600">
                {{ formatDate(teacher.dob) }}
              </td>

              <td class="px-6 py-4 text-right">
                <div class="flex items-center justify-end gap-2">
                  <RouterLink
                    :to="{ name: 'reception.teacher-detail', params: { id: teacher.id } }"
                    class="inline-flex items-center px-3 py-1.5 bg-blue-50 text-blue-700 rounded-lg text-sm font-medium hover:bg-blue-100 transition"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="mr-1"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                    Xem
                  </RouterLink>
                </div>
              </td>
            </tr>

            <tr v-if="filteredTeachers.length === 0">
              <td colspan="6" class="px-6 py-12 text-center text-gray-500">
                <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round" class="mx-auto mb-4 text-gray-300"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M22 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                <p>Không tìm thấy giáo viên nào phù hợp</p>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>