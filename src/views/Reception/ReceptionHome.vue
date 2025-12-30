<script setup>
import { ref, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { Users, DollarSign, CheckCircle, AlertCircle, UserPlus, Calendar, Clock, User, GraduationCap } from 'lucide-vue-next'
import api from '@/services/api'
import { useAuthStore } from '@/stores/useAuthStore'

// ===================== STATE =====================
const authStore = useAuthStore()
const isLoading = ref(true)

const todayStats = ref({
  newMemberships: 0,
  totalCheckIns: 0,
  totalRevenue: 0
})

const recentTransactions = ref([])
const upcomingClasses = ref([])

// ===================== FETCH REAL DATA FROM BACKEND =====================
const fetchReceptionData = async () => {
  isLoading.value = true
  
  try {
    // Fetch today's statistics
    const statsRes = await api.get(`v1/stats/reception/today`)
    todayStats.value = statsRes.data
    
    // Fetch recent transactions
    const transactionsRes = await api.get(`v1/stats/reception/recent-transactions`)
    recentTransactions.value = transactionsRes.data || []
    
    // Fetch upcoming classes
    const classesRes = await api.get(`v1/stats/reception/upcoming-classes`)
    upcomingClasses.value = classesRes.data || []
    
  } catch (error) {
    console.error('Error fetching reception data:', error)
    
    // Fallback to empty data on error
    todayStats.value = {
      newMemberships: 0,
      totalCheckIns: 0,
      totalRevenue: 0
    }
    recentTransactions.value = []
    upcomingClasses.value = []
  } finally {
    isLoading.value = false
  }
}

const formatCurrency = (value) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value || 0)
}

onMounted(() => {
  fetchReceptionData()
})
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-emerald-50 via-emerald-50 to-emerald-50 py-8">
    <div class="mx-auto max-w-7xl px-6">
      <!-- Header -->
      <div class="mb-8">
        <h1 class="text-4xl font-bold bg-gradient-to-r from-emerald-600 to-emerald-600 bg-clip-text text-transparent">
          Xin chào, {{ authStore.user?.fullName }} 📋
        </h1>
        <p class="text-gray-600 mt-2">Tổng quan hoạt động hôm nay - {{ new Date().toLocaleDateString('vi-VN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' }) }}</p>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading" class="flex justify-center items-center h-64">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-emerald-600"></div>
      </div>

      <div v-else class="space-y-6">
        <!-- Today's Summary Stats Bar -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <!-- New Memberships -->
          <div class="bg-white/90 backdrop-blur-lg rounded-xl shadow-md p-6 border-l-4 border-emerald-500 hover:shadow-lg transition-all">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-gray-600 text-sm font-medium uppercase">Thành viên mới hôm nay</p>
                <p class="text-4xl font-bold text-gray-900 mt-2">{{ todayStats.newMemberships }}</p>
                <p class="text-sm text-emerald-600 mt-2">↗ Đăng ký mới</p>
              </div>
              <div class="w-16 h-16 rounded-full bg-gradient-to-br from-emerald-500 to-emerald-500 flex items-center justify-center">
                <UserPlus class="w-8 h-8 text-white" />
              </div>
            </div>
          </div>

          <!-- Check-ins -->
          <div class="bg-white/90 backdrop-blur-lg rounded-xl shadow-md p-6 border-l-4 border-emerald-500 hover:shadow-lg transition-all">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-gray-600 text-sm font-medium uppercase">Check-in hôm nay</p>
                <p class="text-4xl font-bold text-gray-900 mt-2">{{ todayStats.totalCheckIns }}</p>
                <p class="text-sm text-gray-500 mt-2">Lượt check-in</p>
              </div>
              <div class="w-16 h-16 rounded-full bg-gradient-to-br from-emerald-500 to-emerald-500 flex items-center justify-center">
                <CheckCircle class="w-8 h-8 text-white" />
              </div>
            </div>
          </div>

          <!-- Revenue -->
          <div class="bg-white/90 backdrop-blur-lg rounded-xl shadow-md p-6 border-l-4 border-emerald-500 hover:shadow-lg transition-all">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-gray-600 text-sm font-medium uppercase">Doanh thu hôm nay</p>
                <p class="text-4xl font-bold text-gray-900 mt-2">{{ (todayStats.totalRevenue / 1000000).toFixed(1) }}M</p>
                <p class="text-sm text-emerald-600 mt-2">↗ VNĐ</p>
              </div>
              <div class="w-16 h-16 rounded-full bg-gradient-to-br from-emerald-500 to-emerald-500 flex items-center justify-center">
                <DollarSign class="w-8 h-8 text-white" />
              </div>
            </div>
          </div>
        </div>

        <!-- Quick Actions Grid -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <!-- View Memberships -->
          <div class="bg-white/90 backdrop-blur-lg rounded-2xl shadow-lg p-6 border border-emerald-100 hover:shadow-xl transition-all transform hover:-translate-y-1">
            <div class="flex items-center justify-between mb-4">
              <div class="w-14 h-14 rounded-full bg-gradient-to-br from-emerald-500 to-emerald-500 flex items-center justify-center">
                <Users class="w-7 h-7 text-white" />
              </div>
              <span class="px-3 py-1 bg-emerald-100 text-emerald-700 rounded-full text-xs font-semibold">Hoạt động</span>
            </div>
            <h3 class="text-xl font-bold text-gray-900 mb-2">Xem thành viên</h3>
            <p class="text-gray-600 text-sm mb-4">Xem và quản lý gói thành viên khách hàng</p>
            <RouterLink :to="{ name: 'reception.customer-management' }" class="inline-flex items-center px-4 py-2 bg-gradient-to-r from-emerald-500 to-emerald-500 text-white text-sm font-semibold rounded-lg hover:shadow-lg transition-all">
              Xem danh sách →
            </RouterLink>
          </div>

          <!-- View Teachers -->
          <div class="bg-white/90 backdrop-blur-lg rounded-2xl shadow-lg p-6 border border-emerald-100 hover:shadow-xl transition-all transform hover:-translate-y-1">
            <div class="flex items-center justify-between mb-4">
              <div class="w-14 h-14 rounded-full bg-gradient-to-br from-emerald-500 to-emerald-500 flex items-center justify-center">
                <GraduationCap class="w-7 h-7 text-white" />
              </div>
              <span class="px-3 py-1 bg-emerald-100 text-emerald-700 rounded-full text-xs font-semibold">Xem</span>
            </div>
            <h3 class="text-xl font-bold text-gray-900 mb-2">Xem giáo viên</h3>
            <p class="text-gray-600 text-sm mb-4">Xem danh sách giáo viên</p>
            <RouterLink :to="{ name: 'reception.teachers' }" class="inline-flex items-center px-4 py-2 bg-gradient-to-r from-emerald-500 to-emerald-500 text-white text-sm font-semibold rounded-lg hover:shadow-lg transition-all">
              Xem danh sách →
            </RouterLink>
          </div>

          <!-- View Classes -->
          <div class="bg-white/90 backdrop-blur-lg rounded-2xl shadow-lg p-6 border border-emerald-100 hover:shadow-xl transition-all transform hover:-translate-y-1">
            <div class="flex items-center justify-between mb-4">
              <div class="w-14 h-14 rounded-full bg-gradient-to-br from-emerald-500 to-emerald-500 flex items-center justify-center">
                <Calendar class="w-7 h-7 text-white" />
              </div>
              <span class="px-3 py-1 bg-emerald-100 text-emerald-700 rounded-full text-xs font-semibold">Xem</span>
            </div>
            <h3 class="text-xl font-bold text-gray-900 mb-2">Xem các lớp học</h3>
            <p class="text-gray-600 text-sm mb-4">Xem danh sách các lớp học</p>
            <RouterLink :to="{ name: 'reception.classes' }" class="inline-flex items-center px-4 py-2 bg-gradient-to-r from-emerald-500 to-emerald-500 text-white text-sm font-semibold rounded-lg hover:shadow-lg transition-all">
              Xem danh sách →
            </RouterLink>
          </div>
        </div>

        <!-- Recent Transactions & Expiring Memberships -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <!-- Recent Transactions -->
          <div class="bg-white/90 backdrop-blur-lg rounded-2xl shadow-lg p-6 border border-emerald-100">
            <div class="flex items-center gap-2 mb-6">
              <Clock class="w-5 h-5 text-emerald-600" />
              <h2 class="text-xl font-bold text-gray-900">Giao dịch gần đây</h2>
            </div>
            
            <div v-if="recentTransactions.length === 0" class="text-center text-gray-400 py-8">
              Chưa có giao dịch hôm nay
            </div>
            
            <div v-else class="overflow-x-auto">
              <table class="w-full">
                <thead class="bg-emerald-50">
                  <tr>
                    <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 uppercase">Khách hàng</th>
                    <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 uppercase">Loại</th>
                    <th class="px-4 py-3 text-right text-xs font-semibold text-gray-600 uppercase">Số tiền</th>
                    <th class="px-4 py-3 text-center text-xs font-semibold text-gray-600 uppercase">Trạng thái</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-gray-100">
                  <tr v-for="tx in recentTransactions" :key="tx.id" class="hover:bg-emerald-50 transition-colors">
                    <td class="px-4 py-3 text-sm font-medium text-gray-900">{{ tx.customerName }}</td>
                    <td class="px-4 py-3 text-sm text-gray-600">{{ tx.type }}</td>
                    <td class="px-4 py-3 text-sm font-semibold text-right text-gray-900">{{ formatCurrency(tx.amount) }}</td>
                    <td class="px-4 py-3 text-center">
                      <span :class="tx.status === 'PAID' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'" 
                            class="px-2 py-1 rounded-full text-xs font-semibold">
                        {{ tx.status === 'PAID' ? 'Đã thanh toán' : 'Chờ xử lý' }}
                      </span>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <!-- Upcoming Classes -->
          <div class="bg-white/90 backdrop-blur-lg rounded-2xl shadow-lg p-6 border border-emerald-100">
            <div class="flex items-center gap-2 mb-6">
              <Calendar class="w-5 h-5 text-emerald-600" />
              <h2 class="text-xl font-bold text-gray-900">Các lớp học sắp tới</h2>
            </div>

            <div v-if="upcomingClasses.length === 0" class="text-center text-gray-400 py-8">
              Không có lớp học nào sắp tới
            </div>

            <div v-else class="space-y-3">
              <div v-for="(cls, index) in upcomingClasses" :key="index"
                   class="flex items-center justify-between p-4 bg-gradient-to-r from-emerald-50 to-emerald-50 rounded-xl border border-emerald-100">
                <div class="flex items-center gap-3">
                  <div class="w-10 h-10 rounded-full bg-gradient-to-br from-emerald-500 to-emerald-500 flex items-center justify-center">
                    <Calendar class="w-5 h-5 text-white" />
                  </div>
                  <div>
                    <p class="font-semibold text-gray-900">{{ cls.className }}</p>
                    <p class="text-xs text-gray-600">Giáo viên: {{ cls.teacher }}</p>
                  </div>
                </div>
                <div class="text-right">
                  <p class="text-sm font-semibold text-emerald-600">{{ cls.daysLeft }} ngày</p>
                  <p class="text-xs text-gray-500">{{ cls.startDate }}</p>
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
