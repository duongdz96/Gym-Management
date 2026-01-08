<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '@/services/api'
import { useAuthStore } from '@/stores/useAuthStore'
import { useToast } from 'vue-toastification'

const authStore = useAuthStore()
const toast = useToast()

const sessions = ref([])
const currentDate = new Date()
const selectedYear = ref(currentDate.getFullYear())
const selectedMonthNum = ref(currentDate.getMonth() + 1) // 1-12
const selectedStatus = ref('all') // all, Completed, Scheduled, Cancelled

const selectedMonth = computed(() => {
  return `${selectedYear.value}-${String(selectedMonthNum.value).padStart(2, '0')}`
})

const months = [
  { value: 1, label: 'Tháng 1' },
  { value: 2, label: 'Tháng 2' },
  { value: 3, label: 'Tháng 3' },
  { value: 4, label: 'Tháng 4' },
  { value: 5, label: 'Tháng 5' },
  { value: 6, label: 'Tháng 6' },
  { value: 7, label: 'Tháng 7' },
  { value: 8, label: 'Tháng 8' },
  { value: 9, label: 'Tháng 9' },
  { value: 10, label: 'Tháng 10' },
  { value: 11, label: 'Tháng 11' },
  { value: 12, label: 'Tháng 12' }
]

const years = computed(() => {
  const current = new Date().getFullYear()
  const yearList = []
  for (let i = current - 2; i <= current + 1; i++) {
    yearList.push(i)
  }
  return yearList
})

// Fetch sessions from API
async function fetchSessions() {
  try {
    const res = await api.get('/appointment')
    const data = Array.isArray(res.data) ? res.data : []
    
    // Filter appointments for the current PT
    const ptAppointments = data.filter(appt => 
      appt.ptPackageIssued?.pt?.id === authStore.user.id
    )
    
    // Map to session format
    sessions.value = ptAppointments.map(appt => {
      const start = new Date(appt.startTime)
      const end = new Date(appt.endTime)
      const duration = Math.round((end - start) / 1000 / 60) // in minutes
      
      return {
        id: appt.id,
        studentName: appt.ptPackageIssued?.member?.fullName || 'N/A',
        date: appt.startTime.split('T')[0],
        startTime: appt.startTime,
        endTime: appt.endTime,
        status: appt.status, // 'Scheduled', 'In Progress', 'Completed', 'Cancelled'
        duration: duration,
        raw: appt
      }
    })
  } catch (error) {
    console.error('Error fetching sessions:', error)
    toast.error('Không thể tải danh sách buổi tập!')
    sessions.value = []
  }
}

const filteredSessions = computed(() => {
  const filtered = sessions.value.filter(session => {
    // Filter by selected month
    const sessionMonth = session.date.slice(0, 7)
    if (sessionMonth !== selectedMonth.value) return false
    
    // Filter by status
    if (selectedStatus.value !== 'all' && session.status !== selectedStatus.value) return false
    
    return true
  })
  
  // Sort sessions: future dates first, then past dates, sorted by time
  const now = new Date()
  now.setHours(0, 0, 0, 0) // Set to start of today
  
  return filtered.sort((a, b) => {
    const dateA = new Date(a.startTime)
    const dateB = new Date(b.startTime)
    
    const isAFuture = dateA >= now
    const isBFuture = dateB >= now
    
    // If one is future and one is past, future comes first
    if (isAFuture && !isBFuture) return -1
    if (!isAFuture && isBFuture) return 1
    
    // Both are in same category (both future or both past), sort by time
    return dateA - dateB
  })
})

// Computed for different status counts
const completedSessions = computed(() => 
  filteredSessions.value.filter(s => s.status === 'Completed')
)

const scheduledSessions = computed(() => 
  filteredSessions.value.filter(s => s.status === 'Scheduled')
)

const cancelledSessions = computed(() => 
  filteredSessions.value.filter(s => s.status === 'Cancelled')
)

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  try {
    return new Date(dateString).toLocaleDateString('vi-VN')
  } catch {
    return dateString
  }
}

const formatTime = (dateTimeString) => {
  if (!dateTimeString) return 'N/A'
  try {
    const date = new Date(dateTimeString)
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    return `${hours}:${minutes}`
  } catch {
    return 'N/A'
  }
}

const getStatusText = (status) => {
  const statusMap = {
    'Scheduled': 'Sắp tới',
    'In Progress': 'Đang diễn ra',
    'Completed': 'Đã hoàn thành',
    'Cancelled': 'Đã hủy'
  }
  return statusMap[status] || status
}

const getStatusColor = (status) => {
  switch (status) {
    case 'Completed': return 'text-purple-600 bg-purple-50'
    case 'In Progress': return 'text-blue-600 bg-blue-50'
    case 'Scheduled': return 'text-green-600 bg-green-50'
    case 'Cancelled': return 'text-red-600 bg-red-50'
    default: return 'text-gray-600 bg-gray-50'
  }
}

const getStatusIcon = (status) => {
  switch (status) {
    case 'Completed': return '✅'
    case 'In Progress': return '🔄'
    case 'Scheduled': return '⏰'
    case 'Cancelled': return '❌'
    default: return '❓'
  }
}

const getMonthYearText = computed(() => {
  if (!selectedMonth.value) return ''
  const [year, month] = selectedMonth.value.split('-')
  return `Tháng ${month}/${year}`
})

onMounted(async () => {
  await fetchSessions()
})
</script>

<template>
  <div class="p-4 sm:p-6 space-y-4 sm:space-y-6">
    <div class="flex flex-col sm:flex-row items-start sm:items-center gap-3 mb-6 sm:mb-8">
      <div class="w-10 h-10 rounded-full bg-green-100 flex items-center justify-center shrink-0">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-green-600">
          <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"></path>
          <circle cx="9" cy="7" r="4"></circle>
          <path d="M22 21v-2a4 4 0 0 0-3-3.87"></path>
          <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
        </svg>
      </div>
      <div>
        <h1 class="text-2xl sm:text-3xl font-bold bg-gradient-to-r from-green-600 to-green-700 bg-clip-text text-transparent">
          Buổi Tập Hôm Nay
        </h1>
        <p class="text-gray-600 text-xs sm:text-sm">Danh sách học viên và lịch tập trong ngày</p>
      </div>
    </div>

    <!-- Month and Status Filter -->
    <div class="flex flex-col sm:flex-row gap-4 mb-4 sm:mb-6">
      <div class="relative w-full sm:w-auto">
        <label class="block text-xs sm:text-sm font-medium text-gray-700 mb-2">Chọn tháng</label>
        <select
          v-model="selectedMonthNum"
          class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-green-500 text-sm"
        >
          <option v-for="month in months" :key="month.value" :value="month.value">
            {{ month.label }}
          </option>
        </select>
      </div>
      <div class="relative w-full sm:w-auto">
        <label class="block text-xs sm:text-sm font-medium text-gray-700 mb-2">Chọn năm</label>
        <select
          v-model="selectedYear"
          class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-green-500 text-sm"
        >
          <option v-for="year in years" :key="year" :value="year">
            Năm {{ year }}
          </option>
        </select>
      </div>
      <div class="relative w-full sm:w-auto">
        <label class="block text-xs sm:text-sm font-medium text-gray-700 mb-2">Trạng thái</label>
        <select
          v-model="selectedStatus"
          class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-green-500 text-sm"
        >
          <option value="all">Tất cả</option>
          <option value="Completed">Đã hoàn thành</option>
          <option value="Scheduled">Sắp tới</option>
          <option value="Cancelled">Đã hủy</option>
        </select>
      </div>
    </div>

    <!-- Summary Cards -->
    <div class="grid grid-cols-2 lg:grid-cols-4 gap-3 sm:gap-4 mb-4 sm:mb-6">
      <div class="bg-white p-4 rounded-xl shadow-sm border border-gray-200">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-600">Tổng buổi tập</p>
            <p class="text-2xl font-bold text-gray-800">{{ filteredSessions.length }}</p>
            <p class="text-xs text-gray-500 mt-1">{{ getMonthYearText }}</p>
          </div>
          <div class="w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-green-600">
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
            <p class="text-sm text-gray-600">Đã hoàn thành</p>
            <p class="text-2xl font-bold text-purple-600">{{ completedSessions.length }}</p>
            <p class="text-xs text-gray-500 mt-1">{{ getMonthYearText }}</p>
          </div>
          <div class="w-10 h-10 bg-purple-100 rounded-lg flex items-center justify-center">
            <span class="text-purple-600 text-lg">✅</span>
          </div>
        </div>
      </div>

      <div class="bg-white p-4 rounded-xl shadow-sm border border-gray-200">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-600">Sắp tới</p>
            <p class="text-2xl font-bold text-green-600">{{ scheduledSessions.length }}</p>
            <p class="text-xs text-gray-500 mt-1">{{ getMonthYearText }}</p>
          </div>
          <div class="w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center">
            <span class="text-green-600 text-lg">⏰</span>
          </div>
        </div>
      </div>

      <div class="bg-white p-4 rounded-xl shadow-sm border border-gray-200">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-600">Đã hủy</p>
            <p class="text-2xl font-bold text-red-600">{{ cancelledSessions.length }}</p>
            <p class="text-xs text-gray-500 mt-1">{{ getMonthYearText }}</p>
          </div>
          <div class="w-10 h-10 bg-red-100 rounded-lg flex items-center justify-center">
            <span class="text-red-600 text-lg">❌</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Sessions List -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
      <div class="px-6 py-4 border-b border-gray-200 bg-gray-50">
        <h3 class="text-lg font-semibold text-gray-800">Danh sách buổi tập</h3>
        <p class="text-sm text-gray-600">Hiển thị {{ filteredSessions.length }} buổi tập trong {{ getMonthYearText }}</p>
      </div>

      <div class="divide-y divide-gray-200">
        <div
          v-for="session in filteredSessions"
          :key="session.id"
          class="px-4 sm:px-6 py-3 sm:py-4 hover:bg-gray-50 transition"
        >
          <div class="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3 sm:gap-0">
            <div class="flex items-center gap-3 sm:gap-4 w-full sm:w-auto">
              <div class="w-12 h-12 rounded-full bg-green-100 text-green-600 flex items-center justify-center font-bold text-sm">
                {{ session.studentName.charAt(0).toUpperCase() }}
              </div>

              <div>
                <h4 class="text-base sm:text-lg font-semibold text-gray-900">{{ session.studentName }}</h4>
                <p class="text-xs sm:text-sm text-gray-600">{{ formatDate(session.startTime) }}</p>
              </div>
            </div>

            <div class="flex flex-col sm:flex-row items-start sm:items-center gap-3 sm:gap-6 w-full sm:w-auto">
              <div class="text-left sm:text-right">
                <div class="text-xs sm:text-sm font-medium text-gray-900">
                  {{ formatTime(session.startTime) }} - {{ formatTime(session.endTime) }}
                </div>
                <div class="text-xs text-gray-500">
                  {{ session.duration }} phút
                </div>
              </div>

              <div class="flex items-center gap-2">
                <span class="text-base sm:text-lg">{{ getStatusIcon(session.status) }}</span>
                <span
                  :class="[
                    'inline-flex items-center px-2 sm:px-3 py-1 rounded-full text-xs font-medium',
                    getStatusColor(session.status)
                  ]"
                >
                  {{ getStatusText(session.status) }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <div v-if="filteredSessions.length === 0" class="px-6 py-12 text-center text-gray-500">
          <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round" class="mx-auto mb-4 text-gray-300">
            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
            <line x1="16" y1="2" x2="16" y2="6"></line>
            <line x1="8" y1="2" x2="8" y2="6"></line>
            <line x1="3" y1="10" x2="21" y2="10"></line>
          </svg>
          <p>Không có buổi tập nào trong tháng này</p>
        </div>
      </div>
    </div>
  </div>
</template>