<script setup>
import { ref, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { Users, DollarSign, CheckCircle, AlertCircle, UserPlus, Calendar, Clock, User, GraduationCap } from 'lucide-vue-next'

// ===================== STATE =====================
const isLoading = ref(true)

const todayStats = ref({
  newMemberships: 0,
  totalCheckIns: 0,
  totalRevenue: 0
})

const recentTransactions = ref([])
const upcomingClasses = ref([])

// ===================== MOCK DATA =====================
const fetchReceptionData = async () => {
  isLoading.value = true
  
  // Simulate API delay
  await new Promise(resolve => setTimeout(resolve, 800))
  
  // Mock today's stats
  todayStats.value = {
    newMemberships: 8,
    totalCheckIns: 52,
    totalRevenue: 15750000
  }

  // Mock recent transactions
  recentTransactions.value = [
    { id: 1, customerName: 'Nguyễn Văn A', type: 'Gói thành viên', amount: 2500000, status: 'PAID', time: '08:30' },
    { id: 2, customerName: 'Trần Thị B', type: 'Sản phẩm', amount: 350000, status: 'PAID', time: '09:15' },
    { id: 3, customerName: 'Lê Văn C', type: 'PT Package', amount: 5000000, status: 'PENDING', time: '10:00' },
    { id: 4, customerName: 'Phạm Thị D', type: 'Gói thành viên', amount: 1800000, status: 'PAID', time: '11:20' },
    { id: 5, customerName: 'Hoàng Văn E', type: 'Sản phẩm', amount: 450000, status: 'PAID', time: '12:45' }
  ]

  // Mock upcoming classes
  upcomingClasses.value = [
    { className: 'Yoga Sáng', teacher: 'Nguyễn Thị A', startDate: '25/12/2024', daysLeft: 2 },
    { className: 'Gym Cơ Bản', teacher: 'Trần Văn B', startDate: '26/12/2024', daysLeft: 3 },
    { className: 'Boxing', teacher: 'Lê Thị C', startDate: '27/12/2024', daysLeft: 4 },
    { className: 'Pilates', teacher: 'Phạm Văn D', startDate: '28/12/2024', daysLeft: 5 },
    { className: 'Cardio', teacher: 'Hoàng Thị E', startDate: '29/12/2024', daysLeft: 6 }
  ]
  
  isLoading.value = false
  
  /* REAL API CALLS - Commented for mock data
  try {
    const API_BASE_URL = 'http://localhost:8080/api'
    
    // Fetch bills for today's revenue
    const billsRes = await axios.get(`${API_BASE_URL}/bills`)
    const bills = billsRes.data || []
    
    const today = new Date().toDateString()
    const todayBills = bills.filter(b => new Date(b.createdAt).toDateString() === today)
    
    todayStats.value.totalRevenue = todayBills
      .filter(b => b.status === 'PAID')
      .reduce((sum, b) => sum + (b.totalAmount || 0), 0)
    
    todayStats.value.pendingBills = bills.filter(b => b.status === 'PENDING').length

    // Fetch memberships
    const membershipsRes = await axios.get(`${API_BASE_URL}/customer-membership`)
    const memberships = membershipsRes.data || []
    
    todayStats.value.newMemberships = memberships.filter(m => 
      new Date(m.startDate).toDateString() === today
    ).length

    // Recent transactions
    recentTransactions.value = todayBills.slice(0, 5).map(bill => ({
      id: bill.id,
      customerName: bill.customerName || 'Customer',
      type: bill.type || 'Membership',
      amount: bill.totalAmount,
      status: bill.status,
      time: new Date(bill.createdAt).toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })
    }))

    // Expiring memberships
    const nextWeek = new Date()
    nextWeek.setDate(nextWeek.getDate() + 7)
    
    expiringMemberships.value = memberships
      .filter(m => {
        const endDate = new Date(m.endDate)
        return endDate >= new Date() && endDate <= nextWeek
      })
      .slice(0, 5)
      .map(m => ({
        customerName: m.customerName || 'Customer',
        tier: m.tier,
        expiryDate: new Date(m.endDate).toLocaleDateString('vi-VN'),
        daysLeft: Math.ceil((new Date(m.endDate) - new Date()) / (1000 * 60 * 60 * 24))
      }))

  } catch (error) {
    console.error('Error fetching reception data:', error)
  } finally {
    isLoading.value = false
  }
  */
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
          Bảng điều khiển Lễ tân 📋
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
