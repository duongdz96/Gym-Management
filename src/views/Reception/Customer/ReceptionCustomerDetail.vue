<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import api from '@/services/api'

// --- Interfaces ---
type StudentProfile = {
  id: number
  height: number
  weight: number
  trainingPlan: string
  member: {
    id: number
    email: string
    password: string
    fullName: string
    dob: string
    gender: string
    phone: string
    role: string
    membership: string
    joinDate: string
    status: string
    faceId: string | null
    cardId: string | null
    deleted: boolean
  }
  pt: {
    id: number
    email: string
    fullName: string
    phone: string
  }
}

// --- State ---
const route = useRoute()
const customerId = route.params.id as string
const profile = ref<StudentProfile | null>(null)
const checkinHistory = ref<Array<{date: string, time: string, status: string}>>([])
const isLoading = ref(true)

// --- Filters ---
const dateFilter = ref('')
const statusFilter = ref('')

// --- Lifecycle ---
onMounted(async () => {
  try {
    isLoading.value = true
    // WARNING: Logic lấy toàn bộ list rồi filter rất tệ cho performance.
    // Nên đổi thành API get detail: api.get(`/studentprofile/${customerId}`)
    const res = await api.get('/studentprofile')
    const profiles: StudentProfile[] = res.data
    profile.value = profiles.find(p => p.member.id === parseInt(customerId)) || null

    // Mock Checkin Data
    checkinHistory.value = [
      { date: '2025-12-14', time: '08:30', status: 'Thành công' },
      { date: '2025-12-13', time: '09:15', status: 'Thành công' },
      { date: '2025-12-12', time: '07:45', status: 'Muộn' },
      { date: '2025-12-11', time: '10:00', status: 'Thành công' },
      { date: '2025-12-10', time: '08:20', status: 'Thành công' },
      { date: '2025-12-09', time: '09:30', status: 'Vắng' },
      { date: '2025-12-08', time: '07:50', status: 'Thành công' },
      { date: '2025-12-07', time: '10:15', status: 'Muộn' },
      { date: '2025-12-06', time: '08:45', status: 'Thành công' },
      { date: '2025-12-05', time: '09:00', status: 'Thành công' }
    ]
  } catch (err) {
    console.error('Error fetching profile:', err)
  } finally {
    isLoading.value = false
  }
})

// --- Computed ---
const filteredCheckinHistory = computed(() => {
  return checkinHistory.value.filter(item => {
    const matchesDate = !dateFilter.value || item.date === dateFilter.value
    const matchesStatus = !statusFilter.value || item.status === statusFilter.value
    return matchesDate && matchesStatus
  })
})

// --- Helpers ---
const formatDate = (dateString: string) => {
  if (!dateString) return 'N/A'
  try {
    return new Date(dateString).toLocaleDateString('vi-VN')
  } catch { return dateString }
}

const getInitials = (name: string) => {
  return name ? name.split(' ').map(n => n[0]).join('').slice(-2).toUpperCase() : 'HV';
}

const getStatusColor = (status: string) => {
  switch (status) {
    case 'Thành công': return 'text-green-600 bg-green-50'
    case 'Muộn': return 'text-orange-600 bg-orange-50'
    case 'Vắng': return 'text-red-600 bg-red-50'
    default: return 'text-gray-600 bg-gray-50'
  }
}
</script>

<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <div class="flex items-center justify-between mb-6">
      <div class="flex items-center gap-3">
        <button
          @click="$router.back()"
          class="p-2 bg-white border border-gray-200 rounded-lg hover:bg-gray-100 text-gray-600 transition shadow-sm"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m15 18-6-6 6-6"/></svg>
        </button>
        <div>
          <h1 class="text-xl font-bold text-gray-800">Chi tiết Học viên</h1>
          <p class="text-xs text-gray-500">Quản lý hồ sơ và lịch sử tập luyện</p>
        </div>
      </div>

      <div v-if="profile" class="flex gap-2">
         <span
            :class="[
              'px-3 py-1 rounded-full text-xs font-semibold border',
              profile.member.status === 'Active'
                ? 'bg-green-50 text-green-700 border-green-200'
                : 'bg-red-50 text-red-700 border-red-200'
            ]"
          >
            {{ profile.member.status === 'Active' ? 'Đang hoạt động' : 'Dừng hoạt động' }}
          </span>
      </div>
    </div>

    <div v-if="isLoading" class="animate-pulse grid grid-cols-1 lg:grid-cols-3 gap-6">
       <div class="bg-gray-200 h-96 rounded-xl"></div>
       <div class="bg-gray-200 h-96 rounded-xl lg:col-span-2"></div>
    </div>

    <div v-else-if="profile" class="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">

      <div class="lg:col-span-4 space-y-6">
        <div class="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
          <div class="bg-gradient-to-r from-blue-600 to-indigo-700 h-24"></div>
          <div class="px-6 pb-6">
            <div class="relative flex justify-between items-end -mt-10 mb-4">
              <div class="w-20 h-20 rounded-full border-4 border-white bg-gray-200 flex items-center justify-center text-xl font-bold text-gray-500 shadow-md">
                {{ getInitials(profile.member.fullName) }}
              </div>
            </div>

            <div class="mb-6">
              <h2 class="text-xl font-bold text-gray-900">{{ profile.member.fullName }}</h2>
              <p class="text-sm text-gray-500">{{ profile.member.email }}</p>
            </div>

            <div class="space-y-3 pt-4 border-t border-gray-100">
               <div class="flex justify-between items-center text-sm">
                  <span class="text-gray-500">Số điện thoại</span>
                  <span class="font-medium text-gray-900">{{ profile.member.phone }}</span>
               </div>
               <div class="flex justify-between items-center text-sm">
                  <span class="text-gray-500">Ngày sinh</span>
                  <span class="font-medium text-gray-900">{{ formatDate(profile.member.dob) }}</span>
               </div>
               <div class="flex justify-between items-center text-sm">
                  <span class="text-gray-500">Giới tính</span>
                  <span class="font-medium text-gray-900">{{ profile.member.gender }}</span>
               </div>
               <div class="flex justify-between items-center text-sm">
                  <span class="text-gray-500">Gói tập</span>
                  <span class="inline-block px-2 py-0.5 bg-indigo-50 text-indigo-700 rounded text-xs font-bold uppercase">
                    {{ profile.member.membership }}
                  </span>
               </div>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
           <h3 class="text-sm font-bold text-gray-900 uppercase mb-4 flex items-center gap-2">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-indigo-600"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M22 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
              PT Phụ trách
           </h3>
           <div class="flex items-center gap-3 bg-gray-50 p-3 rounded-lg border border-gray-100">
              <div class="w-10 h-10 rounded-full bg-indigo-100 text-indigo-600 flex items-center justify-center font-bold text-xs">
                PT
              </div>
              <div>
                <p class="text-sm font-semibold text-gray-900">{{ profile.pt.fullName }}</p>
                <p class="text-xs text-gray-500">{{ profile.pt.phone }}</p>
              </div>
           </div>
        </div>
      </div>

      <div class="lg:col-span-8 space-y-6">

        <div class="grid grid-cols-2 gap-4">
           <div class="bg-white p-5 rounded-xl border border-gray-200 shadow-sm flex items-center justify-between">
              <div>
                 <p class="text-xs text-gray-500 uppercase font-semibold">Cân nặng</p>
                 <p class="text-2xl font-bold text-gray-800 mt-1">{{ profile.weight }} <span class="text-sm font-normal text-gray-400">kg</span></p>
              </div>
              <div class="bg-blue-50 p-2 rounded-lg text-blue-600">
                 <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20.42 4.58a5.4 5.4 0 0 0-7.65 0l-.77.78-.77-.78a5.4 5.4 0 0 0-7.65 0C1.46 6.7 1.33 10.28 4 13l8 8 8-8c2.67-2.72 2.54-6.30.42-8.42z"/></svg>
              </div>
           </div>
           <div class="bg-white p-5 rounded-xl border border-gray-200 shadow-sm flex items-center justify-between">
              <div>
                 <p class="text-xs text-gray-500 uppercase font-semibold">Chiều cao</p>
                 <p class="text-2xl font-bold text-gray-800 mt-1">{{ profile.height }} <span class="text-sm font-normal text-gray-400">cm</span></p>
              </div>
              <div class="bg-teal-50 p-2 rounded-lg text-teal-600">
                 <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m21.55 16.55-1.11 1.11a4.13 4.13 0 0 1-5.83 0L12 15l-2.61 2.61a4.13 4.13 0 0 1-5.83-5.83l1.11-1.11"/><path d="m10.89 10.89 1.11 1.11"/></svg>
              </div>
           </div>
        </div>

        <div class="bg-white rounded-xl shadow-sm border border-gray-200 flex flex-col h-[500px]">
          <div class="px-6 py-4 border-b border-gray-100 bg-gray-50 rounded-t-xl">
             <div class="flex justify-between items-center mb-3">
                <h3 class="font-bold text-gray-800">Lịch sử Check-in</h3>
                <span class="text-xs text-gray-500 bg-white px-2 py-1 rounded border">{{ filteredCheckinHistory.length }} buổi</span>
             </div>
             <div class="flex gap-3">
                <div class="flex-1">
                   <input
                      v-model="dateFilter"
                      type="date"
                      class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                      placeholder="Chọn ngày"
                   />
                </div>
                <div class="flex-1">
                   <select
                      v-model="statusFilter"
                      class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                   >
                      <option value="">Tất cả trạng thái</option>
                      <option value="Thành công">Thành công</option>
                      <option value="Muộn">Muộn</option>
                      <option value="Vắng">Vắng</option>
                   </select>
                </div>
             </div>
          </div>

          <div class="overflow-y-auto flex-1 p-0">
             <table class="w-full text-left border-collapse">
                <thead class="bg-gray-50 sticky top-0">
                   <tr>
                      <th class="px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wider">Ngày</th>
                      <th class="px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wider">Giờ</th>
                      <th class="px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wider text-right">Trạng thái</th>
                   </tr>
                </thead>
                <tbody class="divide-y divide-gray-100">
                   <tr v-for="(item, idx) in filteredCheckinHistory" :key="idx" class="hover:bg-gray-50 transition">
                      <td class="px-6 py-3 text-sm text-gray-700 font-medium">{{ formatDate(item.date) }}</td>
                      <td class="px-6 py-3 text-sm text-gray-600">{{ item.time }}</td>
                      <td class="px-6 py-3 text-right">
                         <span :class="['inline-flex items-center gap-1 text-xs font-medium px-2 py-0.5 rounded-full', getStatusColor(item.status)]">
                            <span :class="['w-1.5 h-1.5 rounded-full', item.status === 'Thành công' ? 'bg-green-500' : item.status === 'Muộn' ? 'bg-orange-500' : 'bg-red-500']"></span>
                            {{ item.status }}
                         </span>
                      </td>
                   </tr>
                   <tr v-if="filteredCheckinHistory.length === 0">
                      <td colspan="3" class="px-6 py-8 text-center text-gray-500 text-sm">
                         Không có dữ liệu check-in phù hợp
                      </td>
                   </tr>
                </tbody>
             </table>
          </div>
        </div>

      </div>
    </div>

    <div v-else class="text-center py-20">
      <p class="text-gray-500">Không tìm thấy thông tin học viên.</p>
    </div>
  </div>
</template>