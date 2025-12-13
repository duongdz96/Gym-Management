<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'

type Session = {
  id: number
  studentName: string
  date: string
  startTime: string
  endTime: string
  status: 'completed' | 'ongoing' | 'upcoming'
  duration: number // in minutes
}

const sessions = ref<Session[]>([])
const selectedDate = ref(new Date().toISOString().split('T')[0])

// Mock data
const generateMockSessions = () => {
  const mockSessions: Session[] = [
    {
      id: 1,
      studentName: 'Nguyễn Văn A',
      date: new Date().toISOString().split('T')[0],
      startTime: '08:00',
      endTime: '09:00',
      status: 'completed',
      duration: 60
    },
    {
      id: 2,
      studentName: 'Trần Thị B',
      date: new Date().toISOString().split('T')[0],
      startTime: '09:30',
      endTime: '10:30',
      status: 'completed',
      duration: 60
    },
    {
      id: 3,
      studentName: 'Lê Văn C',
      date: new Date().toISOString().split('T')[0],
      startTime: '14:00',
      endTime: '15:00',
      status: 'ongoing',
      duration: 60
    },
    {
      id: 4,
      studentName: 'Phạm Thị D',
      date: new Date().toISOString().split('T')[0],
      startTime: '16:00',
      endTime: '17:00',
      status: 'upcoming',
      duration: 60
    },
    {
      id: 5,
      studentName: 'Hoàng Văn E',
      date: new Date().toISOString().split('T')[0],
      startTime: '17:30',
      endTime: '18:30',
      status: 'upcoming',
      duration: 60
    }
  ]

  return mockSessions
}

const filteredSessions = computed(() => {
  return sessions.value.filter(session => session.date === selectedDate.value)
})

const formatDate = (dateString: string) => {
  if (!dateString) return 'N/A'
  try {
    return new Date(dateString).toLocaleDateString('vi-VN')
  } catch {
    return dateString
  }
}

const getStatusText = (status: string) => {
  switch (status) {
    case 'completed': return 'Đã hoàn thành'
    case 'ongoing': return 'Đang diễn ra'
    case 'upcoming': return 'Sắp tới'
    default: return status
  }
}

const getStatusColor = (status: string) => {
  switch (status) {
    case 'completed': return 'text-green-600 bg-green-50'
    case 'ongoing': return 'text-blue-600 bg-blue-50'
    case 'upcoming': return 'text-orange-600 bg-orange-50'
    default: return 'text-gray-600 bg-gray-50'
  }
}

const getStatusIcon = (status: string) => {
  switch (status) {
    case 'completed': return '✅'
    case 'ongoing': return '🔄'
    case 'upcoming': return '⏰'
    default: return '❓'
  }
}

onMounted(() => {
  sessions.value = generateMockSessions()
})
</script>

<template>
  <div class="p-6 space-y-6">
    <div class="flex items-center gap-3 mb-8">
      <div class="w-10 h-10 rounded-full bg-green-100 flex items-center justify-center">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-green-600">
          <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"></path>
          <circle cx="9" cy="7" r="4"></circle>
          <path d="M22 21v-2a4 4 0 0 0-3-3.87"></path>
          <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
        </svg>
      </div>
      <div>
        <h1 class="text-3xl font-bold bg-gradient-to-r from-green-600 to-green-700 bg-clip-text text-transparent">
          Buổi Tập Hôm Nay
        </h1>
        <p class="text-gray-600 text-sm">Danh sách học viên và lịch tập trong ngày</p>
      </div>
    </div>

    <!-- Date Filter -->
    <div class="flex gap-4 mb-6">
      <div class="relative">
        <label class="block text-sm font-medium text-gray-700 mb-2">Chọn ngày</label>
        <input
          v-model="selectedDate"
          type="date"
          class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-green-500"
        />
      </div>
    </div>

    <!-- Summary Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
      <div class="bg-white p-4 rounded-xl shadow-sm border border-gray-200">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-600">Tổng buổi tập</p>
            <p class="text-2xl font-bold text-gray-800">{{ filteredSessions.length }}</p>
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
            <p class="text-2xl font-bold text-green-600">{{ filteredSessions.filter(s => s.status === 'completed').length }}</p>
          </div>
          <div class="w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center">
            <span class="text-green-600 text-lg">✅</span>
          </div>
        </div>
      </div>

      <div class="bg-white p-4 rounded-xl shadow-sm border border-gray-200">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-600">Đang diễn ra</p>
            <p class="text-2xl font-bold text-blue-600">{{ filteredSessions.filter(s => s.status === 'ongoing').length }}</p>
          </div>
          <div class="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center">
            <span class="text-blue-600 text-lg">🔄</span>
          </div>
        </div>
      </div>

      <div class="bg-white p-4 rounded-xl shadow-sm border border-gray-200">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-600">Sắp tới</p>
            <p class="text-2xl font-bold text-orange-600">{{ filteredSessions.filter(s => s.status === 'upcoming').length }}</p>
          </div>
          <div class="w-10 h-10 bg-orange-100 rounded-lg flex items-center justify-center">
            <span class="text-orange-600 text-lg">⏰</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Sessions List -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
      <div class="px-6 py-4 border-b border-gray-200 bg-gray-50">
        <h3 class="text-lg font-semibold text-gray-800">Danh sách buổi tập</h3>
        <p class="text-sm text-gray-600">Hiển thị {{ filteredSessions.length }} buổi tập trong ngày {{ formatDate(selectedDate) }}</p>
      </div>

      <div class="divide-y divide-gray-200">
        <div
          v-for="session in filteredSessions"
          :key="session.id"
          class="px-6 py-4 hover:bg-gray-50 transition"
        >
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-4">
              <div class="w-12 h-12 rounded-full bg-green-100 text-green-600 flex items-center justify-center font-bold text-sm">
                {{ session.studentName.charAt(0).toUpperCase() }}
              </div>

              <div>
                <h4 class="text-lg font-semibold text-gray-900">{{ session.studentName }}</h4>
                <p class="text-sm text-gray-600">{{ formatDate(session.date) }}</p>
              </div>
            </div>

            <div class="flex items-center gap-6">
              <div class="text-right">
                <div class="text-sm font-medium text-gray-900">
                  {{ session.startTime }} - {{ session.endTime }}
                </div>
                <div class="text-xs text-gray-500">
                  {{ session.duration }} phút
                </div>
              </div>

              <div class="flex items-center gap-2">
                <span class="text-lg">{{ getStatusIcon(session.status) }}</span>
                <span
                  :class="[
                    'inline-flex items-center px-3 py-1 rounded-full text-xs font-medium',
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
          <p>Không có buổi tập nào trong ngày này</p>
        </div>
      </div>
    </div>
  </div>
</template>