<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/useAuthStore'
import api from '@/services/api'

type AttendanceRecord = {
  id: number
  date: string
  checkIn: string
  checkOut: string | null
  status: 'Thành công' | 'Chưa check out'
}

const authStore = useAuthStore()
const attendanceRecords = ref<AttendanceRecord[]>([])
const dateFilter = ref('')
const statusFilter = ref('')
const isLoading = ref(true)

// Fetch attendance data from API
const fetchAttendanceData = async () => {
  try {
    isLoading.value = true
    const userId = authStore.user.id
    const response = await api.get(`/attendance/${userId}`)
    const data = Array.isArray(response.data) ? response.data : []
    
    // Transform API response to match our AttendanceRecord format
    const records: AttendanceRecord[] = data.map((item: any) => {
      const checkInDate = new Date(item.checkInTime)
      const checkOutDate = item.checkOutTime ? new Date(item.checkOutTime) : null
      
      // Extract time strings (local time)
      const checkIn = checkInDate.toTimeString().split(' ')[0].substring(0, 5) // HH:MM format
      const checkOut = checkOutDate ? checkOutDate.toTimeString().split(' ')[0].substring(0, 5) : null
      
      // Extract date in local timezone (avoid timezone conversion issues)
      const year = checkInDate.getFullYear()
      const month = String(checkInDate.getMonth() + 1).padStart(2, '0')
      const day = String(checkInDate.getDate()).padStart(2, '0')
      const dateStr = `${year}-${month}-${day}`
      
      // Determine status - Thành công if has both check-in and check-out, Chưa check out otherwise
      const status: 'Thành công' | 'Chưa check out' = checkOutDate ? 'Thành công' : 'Chưa check out'
      
      return {
        id: item.id,
        date: dateStr,
        checkIn,
        checkOut,
        status
      }
    })
    
    // Sort by date descending (newest first)
    attendanceRecords.value = records.sort((a, b) => 
      new Date(b.date).getTime() - new Date(a.date).getTime()
    )
  } catch (error) {
    console.error('Error fetching attendance data:', error)
    attendanceRecords.value = []
  } finally {
    isLoading.value = false
  }
}

const filteredRecords = computed(() => {
  return attendanceRecords.value.filter(record => {
    const matchesDate = !dateFilter.value || record.date === dateFilter.value
    const matchesStatus = !statusFilter.value || record.status === statusFilter.value
    return matchesDate && matchesStatus
  })
})

const formatDate = (dateString: string) => {
  if (!dateString) return 'N/A'
  try {
    return new Date(dateString).toLocaleDateString('vi-VN')
  } catch {
    return dateString
  }
}

const getStatusColor = (status: string) => {
  switch (status) {
    case 'Thành công': return 'text-green-600 bg-green-50'
    case 'Chưa check out': return 'text-red-600 bg-red-50'
    default: return 'text-gray-600 bg-gray-50'
  }
}

onMounted(() => {
  fetchAttendanceData()
})
</script>

<template>
  <div class="p-6 space-y-6">
    <div class="flex items-center gap-3 mb-8">
      <div class="w-10 h-10 rounded-full bg-emerald-100 flex items-center justify-center">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-emerald-600">
          <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
          <line x1="16" y1="2" x2="16" y2="6"></line>
          <line x1="8" y1="2" x2="8" y2="6"></line>
          <line x1="3" y1="10" x2="21" y2="10"></line>
        </svg>
      </div>
      <div>
        <h1 class="text-3xl font-bold bg-gradient-to-r from-emerald-600 to-emerald-700 bg-clip-text text-transparent">
          Lịch Sử Chấm Công
        </h1>
        <p class="text-gray-600 text-sm">Theo dõi lịch sử check-in và check-out của bạn</p>
      </div>
    </div>

    <!-- Filters -->
    <div class="flex gap-4 mb-6">
      <div class="relative">
        <label class="block text-sm font-medium text-gray-700 mb-2">Lọc theo ngày</label>
        <input
          v-model="dateFilter"
          type="date"
          class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500"
        />
      </div>

      <div class="relative">
        <label class="block text-sm font-medium text-gray-700 mb-2">Lọc theo trạng thái</label>
        <select
          v-model="statusFilter"
          class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500"
        >
          <option value="">Tất cả trạng thái</option>
          <option value="Thành công">Thành công</option>
          <option value="Chưa check out">Chưa check out</option>
        </select>
      </div>
    </div>

    <!-- Summary Cards -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
      <div class="bg-white p-4 rounded-xl shadow-sm border border-gray-200">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-600">Tổng ngày làm việc</p>
            <p class="text-2xl font-bold text-gray-800">{{ attendanceRecords.length }}</p>
          </div>
          <div class="w-10 h-10 bg-emerald-100 rounded-lg flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-emerald-600">
              <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
              <line x1="16" y1="2" x2="16" y2="6"></line>
              <line x1="8" y1="2" x2="8" y2="6"></line>
              <line x1="3" y1="10" x2="21" y2="10"></line>
            </svg>
          </div>
        </div>
      </div>

      <div class="bg-white p-4 rounded-xl shadow-sm border border-gray-200">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-600">Thành công</p>
            <p class="text-2xl font-bold text-green-600">{{ attendanceRecords.filter(r => r.status === 'Thành công').length }}</p>
          </div>
          <div class="w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-green-600">
              <polyline points="20,6 9,17 4,12"></polyline>
            </svg>
          </div>
        </div>
      </div>

      <div class="bg-white p-4 rounded-xl shadow-sm border border-gray-200">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-600">Chưa check out</p>
            <p class="text-2xl font-bold text-red-600">{{ attendanceRecords.filter(r => r.status === 'Chưa check out').length }}</p>
          </div>
          <div class="w-10 h-10 bg-red-100 rounded-lg flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-red-600">
              <line x1="18" y1="6" x2="6" y2="18"></line>
              <line x1="6" y1="6" x2="18" y2="18"></line>
            </svg>
          </div>
        </div>
      </div>
    </div>

    <!-- Attendance Table -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
      <div class="px-6 py-4 border-b border-gray-200 bg-gray-50">
        <h3 class="text-lg font-semibold text-gray-800">Chi tiết điểm danh</h3>
        <p class="text-sm text-gray-600">Hiển thị {{ filteredRecords.length }}/{{ attendanceRecords.length }} bản ghi</p>
      </div>

      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Ngày</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Check-in</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Check-out</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Trạng thái</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200 bg-white">
            <tr v-for="record in filteredRecords" :key="record.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 text-sm font-medium text-gray-900">{{ formatDate(record.date) }}</td>
              <td class="px-6 py-4 text-sm text-gray-600">
                <span v-if="record.checkIn" class="flex items-center gap-2">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-green-500">
                    <polyline points="20,6 9,17 4,12"></polyline>
                  </svg>
                  {{ record.checkIn }}
                </span>
                <span v-else class="text-gray-400">-</span>
              </td>
              <td class="px-6 py-4 text-sm text-gray-600">
                <span v-if="record.checkOut" class="flex items-center gap-2">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-emerald-500">
                    <polyline points="20,6 9,17 4,12"></polyline>
                  </svg>
                  {{ record.checkOut }}
                </span>
                <span v-else class="text-gray-400">-</span>
              </td>
              <td class="px-6 py-4">
                <span
                  :class="[
                    'inline-flex items-center gap-1 px-2 py-1 rounded-full text-xs font-medium',
                    getStatusColor(record.status)
                  ]"
                >
                  <span
                    :class="[
                      'w-2 h-2 rounded-full',
                      record.status === 'Thành công' ? 'bg-green-500' : 'bg-red-500'
                    ]"
                  ></span>
                  {{ record.status }}
                </span>
              </td>
            </tr>
            <tr v-if="filteredRecords.length === 0">
              <td colspan="4" class="px-6 py-8 text-center text-gray-500">
                Không có dữ liệu điểm danh phù hợp với bộ lọc
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>