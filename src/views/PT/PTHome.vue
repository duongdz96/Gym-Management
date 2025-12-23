<script setup>
import { ref, onMounted, computed } from 'vue'
import { RouterLink } from 'vue-router'
import { Calendar, Users, TrendingUp, Clock, CheckCircle, BookOpen, Dumbbell, Award, User, BicepsFlexed } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/useAuthStore'
import api from '@/services/api'

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
  
  // Fetch real stats from API
  try {
    // Get student profiles for this PT
    const studentProfilesRes = await api.get('/studentprofile');
    const studentProfiles = Array.isArray(studentProfilesRes.data) ? studentProfilesRes.data : [];
    const ptStudents = studentProfiles.filter(profile => profile.pt && profile.pt.id === authStore.user.id);

    stats.value.totalStudents = ptStudents.length;

    // Get appointments for this PT
    const appointmentsRes = await api.get('/appointment');
    const appointments = Array.isArray(appointmentsRes.data) ? appointmentsRes.data : [];
    const ptAppointments = appointments.filter(appt => appt.pt && appt.pt.id === authStore.user.id);

    // Calculate sessions this week
    const now = new Date();
    const weekStart = new Date(now);
    weekStart.setDate(now.getDate() - now.getDay()); // Start of week (Sunday)
    weekStart.setHours(0, 0, 0, 0);
    const weekEnd = new Date(weekStart);
    weekEnd.setDate(weekEnd.getDate() + 6); // End of week (Saturday)
    weekEnd.setHours(23, 59, 59, 999);

    stats.value.sessionsThisWeek = ptAppointments.filter(appt => {
      const apptDate = new Date(appt.startTime);
      return apptDate >= weekStart && apptDate <= weekEnd;
    }).length;

    // Calculate completion rate from completed appointments
    const completedAppointments = ptAppointments.filter(appt => appt.status === 'Completed').length;
    const totalAppointments = ptAppointments.length;
    stats.value.completionRate = totalAppointments > 0 ? Math.round((completedAppointments / totalAppointments) * 100) : 0;

    // Count upcoming appointments
    stats.value.upcomingAppointments = ptAppointments.filter(appt =>
      new Date(appt.startTime) > new Date() && appt.status === 'Scheduled'
    ).length;

  } catch (error) {
    console.error('Error fetching stats:', error);
    // Fallback to mock data
    stats.value.totalStudents = 15;
    stats.value.sessionsThisWeek = 12;
    stats.value.completionRate = 92;
    stats.value.upcomingAppointments = 4;
  }
  
  // Fetch today's sessions from API
  try {
    const appointmentsRes = await api.get('/appointment');
    const appointments = Array.isArray(appointmentsRes.data) ? appointmentsRes.data : [];
    const ptAppointments = appointments.filter(appt => appt.pt && appt.pt.id === authStore.user.id);

    // Filter for today's appointments
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const tomorrow = new Date(today);
    tomorrow.setDate(tomorrow.getDate() + 1);

    const todayAppointments = ptAppointments.filter(appt => {
      const apptDate = new Date(appt.startTime);
      return apptDate >= today && apptDate < tomorrow;
    });

    todaySessions.value = todayAppointments.map(appt => ({
      id: appt.id,
      studentName: appt.ptPackageIssued?.member?.fullName || 'Unknown',
      type: appt.ptPackageIssued?.ptPackage?.name || 'Tập cá nhân',
      time: new Date(appt.startTime).toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' }),
      status: appt.status === 'confirmed' ? 'Đã xác nhận' : appt.status === 'pending' ? 'Chờ xác nhận' : 'Đã xác nhận'
    }));

  } catch (error) {
    console.error('Error fetching today sessions:', error);
    // Fallback to mock data
    todaySessions.value = [
      { id: 1, studentName: 'Nguyễn Văn A', type: 'Tập cá nhân', time: '08:00', status: 'Đã xác nhận' },
      { id: 2, studentName: 'Trần Thị B', type: 'Tư vấn dinh dưỡng', time: '10:30', status: 'Đã xác nhận' },
      { id: 3, studentName: 'Lê Văn C', type: 'Tập cá nhân', time: '14:00', status: 'Chờ xác nhận' },
      { id: 4, studentName: 'Phạm Thị D', type: 'Đánh giá tiến độ', time: '16:30', status: 'Đã xác nhận' }
    ];
  }

  // Fetch student progress from API
  try {
    const studentProfilesRes = await api.get('/studentprofile');
    const studentProfiles = Array.isArray(studentProfilesRes.data) ? studentProfilesRes.data : [];

    const appointmentsRes = await api.get('/appointment');
    const appointments = Array.isArray(appointmentsRes.data) ? appointmentsRes.data : [];

    // Filter profiles for the current PT
    const ptStudents = studentProfiles.filter(profile => profile.pt && profile.pt.id === authStore.user.id);

    studentProgress.value = ptStudents.slice(0, 4).map(profile => {
      // Find appointments for this member
      const memberAppointments = appointments.filter(appt =>
        appt.ptPackageIssued?.member?.id === profile.member.id &&
        appt.pt?.id === authStore.user.id
      );

      // Calculate completed sessions
      const completedSessions = memberAppointments.filter(appt => appt.status === 'Completed').length;

      // Get package info from the most recent appointment
      const latestAppointment = memberAppointments
        .filter(appt => appt.ptPackageIssued)
        .sort((a, b) => new Date(b.startTime) - new Date(a.startTime))[0];

      const totalSessions = latestAppointment?.ptPackageIssued?.ptPackage?.sessions || 12;
      const remainingSessions = latestAppointment?.ptPackageIssued?.remainingSessions || totalSessions;

      // Calculate progress based on used sessions
      const usedSessions = totalSessions - remainingSessions;
      const progress = totalSessions > 0 ? Math.round((usedSessions / totalSessions) * 100) : 0;

      // Find last session date
      const completedAppts = memberAppointments
        .filter(appt => appt.status === 'Completed')
        .sort((a, b) => new Date(b.startTime) - new Date(a.startTime));

      let lastSession = 'Chưa có buổi tập';
      if (completedAppts.length > 0) {
        const lastApptDate = new Date(completedAppts[0].startTime);
        const now = new Date();
        const diffTime = Math.abs(now - lastApptDate);
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

        if (diffDays === 1) {
          lastSession = '1 ngày trước';
        } else if (diffDays < 7) {
          lastSession = `${diffDays} ngày trước`;
        } else {
          lastSession = lastApptDate.toLocaleDateString('vi-VN');
        }
      }

      return {
        name: profile.member?.fullName || 'Unknown',
        progress: Math.max(0, Math.min(100, progress)), // Ensure progress is between 0-100
        sessions: completedSessions,
        lastSession: lastSession,
        totalSessions: totalSessions,
        remainingSessions: remainingSessions
      };
    });
  } catch (error) {
    console.error('Error fetching student progress:', error);
    // Fallback to mock data if API fails
    studentProgress.value = [
      { name: 'Nguyễn Văn A', progress: 85, sessions: 12, lastSession: '2 ngày trước' },
      { name: 'Trần Thị B', progress: 92, sessions: 15, lastSession: '1 ngày trước' },
      { name: 'Lê Văn C', progress: 68, sessions: 8, lastSession: '3 ngày trước' },
      { name: 'Phạm Thị D', progress: 78, sessions: 10, lastSession: '1 ngày trước' }
    ];
  }

  // Mock data for upcoming appointments
  upcomingAppointments.value = [
    { student: 'Hoàng Văn E', time: '09:00', type: 'Tập cá nhân', date: 'Hôm nay' },
    { student: 'Đặng Thị F', time: '14:00', type: 'Tư vấn dinh dưỡng', date: 'Hôm nay' },
    { student: 'Vũ Văn G', time: '10:00', type: 'Tập cá nhân', date: 'Ngày mai' },
    { student: 'Bùi Thị H', time: '15:30', type: 'Đánh giá tiến độ', date: 'Ngày mai' }
  ];
  
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
  <div class="min-h-screen bg-gradient-to-br from-red-50 via-red-50 to-red-50 py-8">
    <div class="mx-auto max-w-7xl px-6">
      <!-- Header -->
      <div class="mb-8">
        <h1 class="text-4xl font-bold bg-gradient-to-r from-red-600 to-red-600 bg-clip-text text-transparent">
          Bảng điều khiển PT 💪
        </h1>
        <p class="text-gray-600 mt-2">Quản lý học viên và buổi tập của bạn</p>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading" class="flex justify-center items-center h-64">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-red-600"></div>
      </div>

      <div v-else class="grid grid-cols-1 lg:grid-cols-4 gap-6">
        <!-- Left Sidebar - Stats -->
        <div class="lg:col-span-1 space-y-6">
          <!-- Stats Cards -->
          <div class="bg-white/80 backdrop-blur-lg rounded-2xl shadow-lg p-6 border border-red-100">
            <div class="w-12 h-12 rounded-full bg-gradient-to-br from-red-500 to-red-500 flex items-center justify-center mb-4">
              <Users class="w-6 h-6 text-white" />
            </div>
            <p class="text-gray-600 text-sm font-medium">Tổng học viên</p>
            <p class="text-3xl font-bold text-gray-900 mt-1">{{ stats.totalStudents }}</p>
          </div>

          <div class="bg-white/80 backdrop-blur-lg rounded-2xl shadow-lg p-6 border border-red-100">
            <div class="w-12 h-12 rounded-full bg-gradient-to-br from-red-500 to-red-500 flex items-center justify-center mb-4">
              <Calendar class="w-6 h-6 text-white" />
            </div>
            <p class="text-gray-600 text-sm font-medium">Buổi tập tuần này</p>
            <p class="text-3xl font-bold text-gray-900 mt-1">{{ stats.sessionsThisWeek }}</p>
          </div>

          <div class="bg-white/80 backdrop-blur-lg rounded-2xl shadow-lg p-6 border border-red-100">
            <div class="w-12 h-12 rounded-full bg-gradient-to-br from-red-500 to-red-500 flex items-center justify-center mb-4">
              <TrendingUp class="w-6 h-6 text-white" />
            </div>
            <p class="text-gray-600 text-sm font-medium">Tỷ lệ hoàn thành</p>
            <p class="text-3xl font-bold text-gray-900 mt-1">{{ stats.completionRate }}%</p>
          </div>
        </div>

        <!-- Main Content Area -->
        <div class="lg:col-span-3 space-y-6">
          <!-- Weekly Calendar -->
          <div class="bg-white/80 backdrop-blur-lg rounded-2xl shadow-lg p-6 border border-red-100">
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
                       ? 'bg-gradient-to-br from-red-500 to-red-500 text-white shadow-lg scale-105' 
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
                     class="flex items-center justify-between p-4 bg-gradient-to-r from-red-50 to-red-50 rounded-xl border border-red-100">
                  <div class="flex items-center gap-3">
                    <div class="w-10 h-10 rounded-full bg-gradient-to-br from-red-500 to-red-500 flex items-center justify-center">
                      <Dumbbell class="w-5 h-5 text-white" />
                    </div>
                    <div>
                      <p class="font-semibold text-gray-900">{{ session.studentName }}</p>
                      <p class="text-sm text-gray-600">{{ session.type }}</p>
                    </div>
                  </div>
                  <div class="text-right">
                    <p class="font-semibold text-red-600">{{ session.time }}</p>
                    <span class="text-xs px-2 py-1 bg-green-100 text-green-700 rounded-full">{{ session.status }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Student Progress & Upcoming Appointments -->
          <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <!-- Student Progress -->
            <div class="bg-white/80 backdrop-blur-lg rounded-2xl shadow-lg p-6 border border-red-100">
              <div class="flex items-center gap-2 mb-6">
                <Award class="w-5 h-5 text-red-600" />
                <h3 class="text-xl font-bold text-gray-900">Xem thông tin học viên</h3>
              </div>
              <div class="space-y-4">
                <div v-for="student in studentProgress" :key="student.name" class="p-4 bg-gradient-to-r from-red-50 to-red-50 rounded-xl">
                  <div class="flex items-center justify-between mb-2">
                    <p class="font-semibold text-gray-900">{{ student.name }}</p>
                    <span class="text-sm font-bold text-red-600">{{ student.progress }}%</span>
                  </div>
                  <div class="w-full bg-gray-200 rounded-full h-2 mb-2">
                    <div class="bg-gradient-to-r from-red-500 to-red-500 h-2 rounded-full transition-all" :style="{ width: student.progress + '%' }"></div>
                  </div>
                  <div class="flex items-center justify-between text-xs text-gray-600">
                    <span>{{ student.sessions }} buổi tập</span>
                    <span>Lần cuối: {{ student.lastSession }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- Upcoming Appointments -->
            <div class="bg-white/80 backdrop-blur-lg rounded-2xl shadow-lg p-6 border border-red-100">
              <div class="flex items-center gap-2 mb-6">
                <BookOpen class="w-5 h-5 text-red-600" />
                <h3 class="text-xl font-bold text-gray-900">Lịch hẹn sắp tới</h3>
              </div>
              <div class="space-y-3">
                <div v-for="(apt, index) in upcomingAppointments" :key="index" 
                     class="flex items-center justify-between p-4 bg-gradient-to-r from-red-50 to-red-50 rounded-xl border border-red-100">
                  <div class="flex items-center gap-3">
                    <div class="w-10 h-10 rounded-full bg-gradient-to-br from-red-500 to-red-500 flex items-center justify-center">
                      <User class="w-5 h-5 text-white" />
                    </div>
                    <div>
                      <p class="font-semibold text-gray-900">{{ apt.student }}</p>
                      <p class="text-xs text-gray-600">{{ apt.type }}</p>
                    </div>
                  </div>
                  <div class="text-right">
                    <p class="font-semibold text-red-600">{{ apt.time }}</p>
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
