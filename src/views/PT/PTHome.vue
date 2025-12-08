<script setup>
import { ref, onMounted, computed } from 'vue'
import { RouterLink } from 'vue-router'
import { Calendar, Users, TrendingUp, Clock, CheckCircle, BookOpen, Dumbbell, Award, User } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/useAuthStore'

const authStore = useAuthStore()

// ===================== STATE =====================
const isLoading = ref(true)
const currentWeek = ref([])
const todaySessions = ref([])

const stats = ref({
  totalStudents: 0,
  sessionsThisWeek: 0,
  completionRate: 0,
  upcomingAppointments: 0
})

const studentProgress = ref([])
const upcomingAppointments = ref([])

// ===================== HELPERS =====================
const getCurrentWeek = () => {
  const today = new Date()
  const dayOfWeek = today.getDay()
  const monday = new Date(today)
  monday.setDate(today.getDate() - (dayOfWeek === 0 ? 6 : dayOfWeek - 1))
  
  const week = []
  for (let i = 0; i < 7; i++) {
    const day = new Date(monday)
    day.setDate(monday.getDate() + i)
    week.push({
      date: day,
      dayName: day.toLocaleDateString('vi-VN', { weekday: 'short' }),
      dayNumber: day.getDate(),
      isToday: day.toDateString() === today.toDateString()
    })
  }
  return week
}

const formatTime = (timeString) => {
  return timeString
}

// ===================== MOCK DATA =====================
const fetchPTData = async () => {
  isLoading.value = true
  
  // Simulate API delay
  await new Promise(resolve => setTimeout(resolve, 800))
  
  // Mock stats
  stats.value.totalStudents = 15
  stats.value.sessionsThisWeek = 12
  stats.value.completionRate = 92
  stats.value.upcomingAppointments = 4
  
  // Mock today's sessions
  todaySessions.value = [
    { id: 1, studentName: 'Nguyễn Văn A', type: 'Tập cá nhân', time: '08:00', status: 'Đã xác nhận' },
    { id: 2, studentName: 'Trần Thị B', type: 'Tư vấn dinh dưỡng', time: '10:30', status: 'Đã xác nhận' },
    { id: 3, studentName: 'Lê Văn C', type: 'Tập cá nhân', time: '14:00', status: 'Chờ xác nhận' },
    { id: 4, studentName: 'Phạm Thị D', type: 'Đánh giá tiến độ', time: '16:30', status: 'Đã xác nhận' }
  ]

  // Mock student progress
  studentProgress.value = [
    { name: 'Nguyễn Văn A', progress: 85, sessions: 12, lastSession: '2 ngày trước' },
    { name: 'Trần Thị B', progress: 92, sessions: 15, lastSession: '1 ngày trước' },
    { name: 'Lê Văn C', progress: 68, sessions: 8, lastSession: '3 ngày trước' },
    { name: 'Phạm Thị D', progress: 78, sessions: 10, lastSession: '1 ngày trước' }
  ]

  // Mock upcoming appointments
  upcomingAppointments.value = [
    { student: 'Hoàng Văn E', time: '09:00', type: 'Tập cá nhân', date: 'Hôm nay' },
    { student: 'Đặng Thị F', time: '14:00', type: 'Tư vấn', date: 'Hôm nay' },
    { student: 'Vũ Văn G', time: '10:00', type: 'Tập cá nhân', date: 'Ngày mai' },
    { student: 'Bùi Thị H', time: '15:30', type: 'Đánh giá', date: 'Ngày mai' }
  ]
  
  isLoading.value = false
  
  /* REAL API CALLS - Commented for mock data
  try {
    const API_BASE_URL = 'http://localhost:8080/api'
    
    // Fetch training sessions
    const sessionsRes = await axios.get(`${API_BASE_URL}/training-sessions/teacher/${authStore.user?.id}`, {
      headers: { Authorization: `Bearer ${authStore.token}` }
    })
    
    const allSessions = sessionsRes.data || []
    const today = new Date().toDateString()
    todaySessions.value = allSessions.filter(s => new Date(s.sessionDate).toDateString() === today)
    
    // Calculate stats
    stats.value.sessionsThisWeek = allSessions.filter(s => {
      const sessionDate = new Date(s.sessionDate)
      const weekStart = currentWeek.value[0]?.date
      const weekEnd = currentWeek.value[6]?.date
      return sessionDate >= weekStart && sessionDate <= weekEnd
    }).length
  } catch (error) {
    console.error('Error fetching PT data:', error)
  } finally {
    isLoading.value = false
  }
  */
}

onMounted(() => {
  currentWeek.value = getCurrentWeek()
  fetchPTData()
})
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-teal-50 via-cyan-50 to-blue-50 py-8">
    <div class="mx-auto max-w-7xl px-6">
      <!-- Header -->
      <div class="mb-8">
        <h1 class="text-4xl font-bold bg-gradient-to-r from-teal-600 to-cyan-600 bg-clip-text text-transparent">
          Bảng điều khiển PT 💪
        </h1>
        <p class="text-gray-600 mt-2">Quản lý học viên và buổi tập của bạn</p>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading" class="flex justify-center items-center h-64">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-teal-600"></div>
      </div>

      <div v-else class="grid grid-cols-1 lg:grid-cols-4 gap-6">
        <!-- Left Sidebar - Stats -->
        <div class="lg:col-span-1 space-y-6">
          <!-- Stats Cards -->
          <div class="bg-white/80 backdrop-blur-lg rounded-2xl shadow-lg p-6 border border-teal-100">
            <div class="w-12 h-12 rounded-full bg-gradient-to-br from-teal-500 to-cyan-500 flex items-center justify-center mb-4">
              <Users class="w-6 h-6 text-white" />
            </div>
            <p class="text-gray-600 text-sm font-medium">Tổng học viên</p>
            <p class="text-3xl font-bold text-gray-900 mt-1">{{ stats.totalStudents }}</p>
          </div>

          <div class="bg-white/80 backdrop-blur-lg rounded-2xl shadow-lg p-6 border border-cyan-100">
            <div class="w-12 h-12 rounded-full bg-gradient-to-br from-cyan-500 to-blue-500 flex items-center justify-center mb-4">
              <Calendar class="w-6 h-6 text-white" />
            </div>
            <p class="text-gray-600 text-sm font-medium">Buổi tập tuần này</p>
            <p class="text-3xl font-bold text-gray-900 mt-1">{{ stats.sessionsThisWeek }}</p>
          </div>

          <div class="bg-white/80 backdrop-blur-lg rounded-2xl shadow-lg p-6 border border-blue-100">
            <div class="w-12 h-12 rounded-full bg-gradient-to-br from-blue-500 to-indigo-500 flex items-center justify-center mb-4">
              <TrendingUp class="w-6 h-6 text-white" />
            </div>
            <p class="text-gray-600 text-sm font-medium">Tỷ lệ hoàn thành</p>
            <p class="text-3xl font-bold text-gray-900 mt-1">{{ stats.completionRate }}%</p>
          </div>

          <!-- Quick Actions -->
          <div class="bg-white/80 backdrop-blur-lg rounded-2xl shadow-lg p-6 border border-teal-100">
            <h3 class="font-bold text-gray-900 mb-4">Thao tác nhanh</h3>
            <div class="space-y-2">
              <RouterLink to="/pt/schedule" class="block p-3 bg-gradient-to-r from-teal-500 to-cyan-500 text-white rounded-lg hover:shadow-lg transition-all text-sm font-medium text-center">
                Xem lịch đầy đủ
              </RouterLink>
              <RouterLink to="/pt/members" class="block p-3 bg-gradient-to-r from-cyan-500 to-blue-500 text-white rounded-lg hover:shadow-lg transition-all text-sm font-medium text-center">
                Quản lý học viên
              </RouterLink>
            </div>
          </div>
        </div>

        <!-- Main Content Area -->
        <div class="lg:col-span-3 space-y-6">
          <!-- Weekly Calendar -->
          <div class="bg-white/80 backdrop-blur-lg rounded-2xl shadow-lg p-6 border border-teal-100">
            <div class="flex items-center justify-between mb-6">
              <h2 class="text-2xl font-bold text-gray-900">Tuần này</h2>
              <div class="flex items-center gap-2 text-sm text-gray-600">
                <Clock class="w-4 h-4" />
                <span>{{ new Date().toLocaleDateString('vi-VN', { month: 'long', year: 'numeric' }) }}</span>
              </div>
            </div>

            <!-- Week Days -->
            <div class="grid grid-cols-7 gap-2">
              <div v-for="day in currentWeek" :key="day.dayNumber" 
                   :class="[
                     'p-4 rounded-xl text-center transition-all cursor-pointer',
                     day.isToday 
                       ? 'bg-gradient-to-br from-teal-500 to-cyan-500 text-white shadow-lg scale-105' 
                       : 'bg-gray-50 hover:bg-gray-100'
                   ]">
                <p class="text-xs font-medium mb-1">{{ day.dayName }}</p>
                <p class="text-2xl font-bold">{{ day.dayNumber }}</p>
              </div>
            </div>

            <!-- Today's Sessions -->
            <div class="mt-6">
              <h3 class="font-bold text-gray-900 mb-4">Buổi tập hôm nay</h3>
              <div v-if="todaySessions.length === 0" class="text-center text-gray-400 py-8">
                Không có buổi tập nào được lên lịch hôm nay
              </div>
              <div v-else class="space-y-3">
                <div v-for="session in todaySessions" :key="session.id" 
                     class="flex items-center justify-between p-4 bg-gradient-to-r from-teal-50 to-cyan-50 rounded-xl border border-teal-100">
                  <div class="flex items-center gap-3">
                    <div class="w-10 h-10 rounded-full bg-gradient-to-br from-teal-500 to-cyan-500 flex items-center justify-center">
                      <Dumbbell class="w-5 h-5 text-white" />
                    </div>
                    <div>
                      <p class="font-semibold text-gray-900">{{ session.studentName }}</p>
                      <p class="text-sm text-gray-600">{{ session.type }}</p>
                    </div>
                  </div>
                  <div class="text-right">
                    <p class="font-semibold text-teal-600">{{ session.time }}</p>
                    <span class="text-xs px-2 py-1 bg-green-100 text-green-700 rounded-full">{{ session.status }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Student Progress & Upcoming Appointments -->
          <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <!-- Student Progress -->
            <div class="bg-white/80 backdrop-blur-lg rounded-2xl shadow-lg p-6 border border-cyan-100">
              <div class="flex items-center gap-2 mb-6">
                <Award class="w-5 h-5 text-cyan-600" />
                <h3 class="text-xl font-bold text-gray-900">Tiến độ học viên</h3>
              </div>
              <div class="space-y-4">
                <div v-for="student in studentProgress" :key="student.name" class="p-4 bg-gradient-to-r from-cyan-50 to-blue-50 rounded-xl">
                  <div class="flex items-center justify-between mb-2">
                    <p class="font-semibold text-gray-900">{{ student.name }}</p>
                    <span class="text-sm font-bold text-cyan-600">{{ student.progress }}%</span>
                  </div>
                  <div class="w-full bg-gray-200 rounded-full h-2 mb-2">
                    <div class="bg-gradient-to-r from-cyan-500 to-blue-500 h-2 rounded-full transition-all" :style="{ width: student.progress + '%' }"></div>
                  </div>
                  <div class="flex items-center justify-between text-xs text-gray-600">
                    <span>{{ student.sessions }} buổi tập</span>
                    <span>Lần cuối: {{ student.lastSession }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- Upcoming Appointments -->
            <div class="bg-white/80 backdrop-blur-lg rounded-2xl shadow-lg p-6 border border-blue-100">
              <div class="flex items-center gap-2 mb-6">
                <BookOpen class="w-5 h-5 text-blue-600" />
                <h3 class="text-xl font-bold text-gray-900">Lịch hẹn sắp tới</h3>
              </div>
              <div class="space-y-3">
                <div v-for="(apt, index) in upcomingAppointments" :key="index" 
                     class="flex items-center justify-between p-4 bg-gradient-to-r from-blue-50 to-indigo-50 rounded-xl border border-blue-100">
                  <div class="flex items-center gap-3">
                    <div class="w-10 h-10 rounded-full bg-gradient-to-br from-blue-500 to-indigo-500 flex items-center justify-center">
                      <User class="w-5 h-5 text-white" />
                    </div>
                    <div>
                      <p class="font-semibold text-gray-900">{{ apt.student }}</p>
                      <p class="text-xs text-gray-600">{{ apt.type }}</p>
                    </div>
                  </div>
                  <div class="text-right">
                    <p class="font-semibold text-blue-600">{{ apt.time }}</p>
                    <p class="text-xs text-gray-500">{{ apt.date }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.space-y-6 > * {
  animation: fadeIn 0.5s ease-out;
}
</style>
