<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import api from '@/services/api'

// --- Interfaces ---
type Staff = {
  id: number
  email: string
  fullName: string
  dob: string
  gender: string
  phone: string
  role: string
  position?: string
  specialize?: string
  deleted: boolean
}

type AttendanceRecord = {
  id: number
  checkInTime: string
  checkOutTime?: string
}

// --- State ---
const route = useRoute()
const staffId = route.params.staffId as string
const staffInfo = ref<Staff | null>(null)
const attendanceHistory = ref<AttendanceRecord[]>([])
const isLoading = ref(true)

// --- Filters ---
const dateFilter = ref('')
const statusFilter = ref('')

// --- Lifecycle ---
onMounted(async () => {
  try {
    isLoading.value = true
    
    // Fetch staff information
    const staffRes = await api.get(`/users/${staffId}`)
    staffInfo.value = staffRes.data

    // Fetch attendance history
    const attendanceRes = await api.get(`/attendance/${staffId}`)
    attendanceHistory.value = attendanceRes.data || []
  } catch (err) {
    console.error('Error fetching staff data:', err)
  } finally {
    isLoading.value = false
  }
})

// --- Computed ---
const filteredAttendanceHistory = computed(() => {
  return attendanceHistory.value.filter(item => {
    const itemDate = new Date(item.checkInTime).toISOString().split('T')[0]
    const matchesDate = !dateFilter.value || itemDate === dateFilter.value
    
    let itemStatus = 'Thành công'
    if (!item.checkOutTime) {
      itemStatus = 'Chưa checkout'
    }
    const matchesStatus = !statusFilter.value || itemStatus === statusFilter.value
    
    return matchesDate && matchesStatus
  })
})

const totalDays = computed(() => attendanceHistory.value.length)

// --- Helpers ---
const formatDate = (dateString: string) => {
  if (!dateString) return 'N/A'
  try {
    return new Date(dateString).toLocaleDateString('vi-VN')
  } catch { return dateString }
}

const formatTime = (timeString: string) => {
  if (!timeString) return 'N/A'
  try {
    return new Date(timeString).toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })
  } catch { return timeString }
}

const getInitials = (name: string) => {
  return name ? name.split(' ').map(n => n[0]).join('').slice(-2).toUpperCase() : 'NV'
}

const getRoleBadgeClass = (role: string) => {
  switch (role) {
    case 'MANAGER': return 'bg-blue-100 text-blue-800 border-blue-200'
    case 'RECEPTIONIST': return 'bg-purple-100 text-purple-800 border-purple-200'
    case 'PT': return 'bg-emerald-100 text-emerald-800 border-emerald-200'
    case 'TEACHER': return 'bg-amber-100 text-amber-800 border-amber-200'
    default: return 'bg-gray-100 text-gray-800 border-gray-200'
  }
}

const getRoleDisplayName = (role: string) => {
  switch (role) {
    case 'MANAGER': return 'Quản lý'
    case 'RECEPTIONIST': return 'Lễ tân'
    case 'PT': return 'PT'
    case 'TEACHER': return 'Giáo viên'
    default: return role
  }
}

const getStatusInfo = (record: AttendanceRecord) => {
  if (!record.checkOutTime) {
    return { text: 'Chưa checkout', color: 'text-red-600 bg-red-50', dotColor: 'bg-red-500' }
  }
  return { text: 'Thành công', color: 'text-green-600 bg-green-50', dotColor: 'bg-green-500' }
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
          <h1 class="text-xl font-bold text-gray-800">Chi tiết Nhân viên</h1>
          <p class="text-xs text-gray-500">Quản lý hồ sơ và lịch sử chấm công</p>
        </div>
      </div>

      <div v-if="staffInfo" class="flex gap-2">
        <span
          :class="[
            'px-3 py-1 rounded-full text-xs font-semibold border',
            getRoleBadgeClass(staffInfo.role)
          ]"
        >
          {{ getRoleDisplayName(staffInfo.role) }}
        </span>
      </div>
    </div>

    <div v-if="isLoading" class="animate-pulse grid grid-cols-1 lg:grid-cols-3 gap-6">
       <div class="bg-gray-200 h-96 rounded-xl"></div>
       <div class="bg-gray-200 h-96 rounded-xl lg:col-span-2"></div>
    </div>

    <div v-else-if="staffInfo" class="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">

      <div class="lg:col-span-4 space-y-6">
        <div class="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
          <div class="bg-gradient-to-r from-blue-600 to-blue-700 h-24"></div>
          <div class="px-6 pb-6">
            <div class="relative flex justify-between items-end -mt-10 mb-4">
              <div class="w-20 h-20 rounded-full border-4 border-white bg-gray-200 flex items-center justify-center text-xl font-bold text-gray-500 shadow-md">
                {{ getInitials(staffInfo.fullName) }}
              </div>
            </div>

            <div class="mb-6">
              <h2 class="text-xl font-bold text-gray-900">{{ staffInfo.fullName }}</h2>
              <p class="text-sm text-gray-500">{{ staffInfo.email }}</p>
            </div>

            <div class="space-y-3 pt-4 border-t border-gray-100">
               <div class="flex justify-between items-center text-sm">
                  <span class="text-gray-500">Số điện thoại</span>
                  <span class="font-medium text-gray-900">{{ staffInfo.phone }}</span>
               </div>
               <div class="flex justify-between items-center text-sm">
                  <span class="text-gray-500">Ngày sinh</span>
                  <span class="font-medium text-gray-900">{{ formatDate(staffInfo.dob) }}</span>
               </div>
               <div class="flex justify-between items-center text-sm">
                  <span class="text-gray-500">Giới tính</span>
                  <span class="font-medium text-gray-900">{{ staffInfo.gender }}</span>
               </div>
               <div class="flex justify-between items-center text-sm">
                  <span class="text-gray-500">Vai trò</span>
                  <span :class="['inline-block px-2 py-0.5 rounded text-xs font-bold uppercase', getRoleBadgeClass(staffInfo.role)]">
                    {{ getRoleDisplayName(staffInfo.role) }}
                  </span>
               </div>
               <div v-if="staffInfo.position" class="flex justify-between items-center text-sm">
                  <span class="text-gray-500">Chức vụ</span>
                  <span class="font-medium text-gray-900">{{ staffInfo.position }}</span>
               </div>
               <div v-if="staffInfo.specialize" class="flex justify-between items-center text-sm">
                  <span class="text-gray-500">Chuyên môn</span>
                  <span class="font-medium text-gray-900">{{ staffInfo.specialize }}</span>
               </div>
            </div>
          </div>
        </div>

        <!-- Summary Cards -->
        <div class="grid grid-cols-1 gap-3">
          <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-4">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-xs text-gray-500 uppercase font-semibold">Tổng ngày làm việc</p>
                <p class="text-2xl font-bold text-gray-800 mt-1">{{ totalDays }}</p>
              </div>
              <div class="bg-blue-50 p-3 rounded-lg">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-blue-600">
                  <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                  <line x1="16" y1="2" x2="16" y2="6"/>
                  <line x1="8" y1="2" x2="8" y2="6"/>
                  <line x1="3" y1="10" x2="21" y2="10"/>
                </svg>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="lg:col-span-8 space-y-6">
        <div class="bg-white rounded-xl shadow-sm border border-gray-200 flex flex-col h-[600px]">
          <div class="px-6 py-4 border-b border-gray-100 bg-gray-50 rounded-t-xl">
             <div class="flex justify-between items-center mb-3">
                <h3 class="font-bold text-gray-800">Lịch sử Chấm công</h3>
                <span class="text-xs text-gray-500 bg-white px-2 py-1 rounded border">{{ filteredAttendanceHistory.length }} bản ghi</span>
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
                      <option value="Chưa checkout">Chưa checkout</option>
                   </select>
                </div>
             </div>
          </div>

          <div class="overflow-y-auto flex-1 p-0">
             <table class="w-full text-left border-collapse">
                <thead class="bg-gray-50 sticky top-0">
                   <tr>
                      <th class="px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wider">Ngày</th>
                      <th class="px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wider">Check-in</th>
                      <th class="px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wider">Check-out</th>
                      <th class="px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wider text-right">Trạng thái</th>
                   </tr>
                </thead>
                <tbody class="divide-y divide-gray-100">
                   <tr v-for="record in filteredAttendanceHistory" :key="record.id" class="hover:bg-gray-50 transition">
                      <td class="px-6 py-3 text-sm text-gray-700 font-medium">{{ formatDate(record.checkInTime) }}</td>
                      <td class="px-6 py-3 text-sm text-gray-600">
                        <span class="flex items-center gap-2">
                          <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-green-500">
                            <polyline points="20,6 9,17 4,12"/>
                          </svg>
                          {{ formatTime(record.checkInTime) }}
                        </span>
                      </td>
                      <td class="px-6 py-3 text-sm text-gray-600">
                        <span v-if="record.checkOutTime" class="flex items-center gap-2">
                          <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-blue-500">
                            <polyline points="20,6 9,17 4,12"/>
                          </svg>
                          {{ formatTime(record.checkOutTime) }}
                        </span>
                        <span v-else class="text-gray-400">-</span>
                      </td>
                      <td class="px-6 py-3 text-right">
                         <span :class="['inline-flex items-center gap-1 text-xs font-medium px-2 py-0.5 rounded-full', getStatusInfo(record).color]">
                            <span :class="['w-1.5 h-1.5 rounded-full', getStatusInfo(record).dotColor]"></span>
                            {{ getStatusInfo(record).text }}
                         </span>
                      </td>
                   </tr>
                   <tr v-if="filteredAttendanceHistory.length === 0">
                      <td colspan="4" class="px-6 py-8 text-center text-gray-500 text-sm">
                         Không có dữ liệu chấm công phù hợp
                      </td>
                   </tr>
                </tbody>
             </table>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="text-center py-20">
      <p class="text-gray-500">Không tìm thấy thông tin nhân viên.</p>
    </div>
  </div>
</template>
