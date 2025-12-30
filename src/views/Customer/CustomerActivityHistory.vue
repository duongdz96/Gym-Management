<template>
  <div class="p-6">
    <!-- Header -->
    <div class="flex items-center gap-3 mb-8">
      <Clock class="w-10 h-10 text-emerald-600" />
      <h1 class="text-3xl font-bold bg-gradient-to-r from-emerald-600 to-teal-600 bg-clip-text text-transparent">
        Lịch Sử Hoạt Động
      </h1>
    </div>

    <!-- Filters -->
    <div class="bg-white rounded-2xl shadow-lg p-6 mb-6 border border-emerald-100">
      <div class="flex flex-wrap gap-4 items-center">
        <div class="flex-1 min-w-[200px]">
          <label class="block text-sm font-medium text-gray-700 mb-2">Loại hoạt động</label>
          <select 
            v-model="filterType"
            class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-emerald-600 focus:ring-2 focus:ring-emerald-100 outline-none transition-all"
          >
            <option value="">Tất cả</option>
            <option value="checkin">Check-in</option>
            <option value="class">Lớp học</option>
            <option value="purchase">Mua hàng</option>
            <option value="coupon">Mã giảm giá</option>
            <option value="workout">Tập luyện</option>
          </select>
        </div>
        
        <div class="flex-1 min-w-[200px]">
          <label class="block text-sm font-medium text-gray-700 mb-2">Từ ngày</label>
          <input 
            v-model="filterStartDate"
            type="date"
            class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-emerald-600 focus:ring-2 focus:ring-emerald-100 outline-none transition-all"
          />
        </div>
        
        <div class="flex-1 min-w-[200px]">
          <label class="block text-sm font-medium text-gray-700 mb-2">Đến ngày</label>
          <input 
            v-model="filterEndDate"
            type="date"
            class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-emerald-600 focus:ring-2 focus:ring-emerald-100 outline-none transition-all"
          />
        </div>
        
        <div class="flex items-end gap-2">
          <button 
            @click="applyFilters"
            class="px-6 py-3 bg-gradient-to-r from-emerald-600 to-teal-600 text-white rounded-xl font-semibold hover:shadow-lg transition-all"
          >
            Lọc
          </button>
          <button 
            @click="resetFilters"
            class="px-6 py-3 bg-gray-200 text-gray-700 rounded-xl font-semibold hover:bg-gray-300 transition-all"
          >
            Đặt lại
          </button>
        </div>
      </div>
    </div>

    <!-- Activity List -->
    <div class="bg-white rounded-2xl shadow-lg border border-emerald-100 overflow-hidden">
      <div v-if="loading" class="flex justify-center items-center py-16">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-emerald-600"></div>
      </div>

      <div v-else-if="filteredActivities.length === 0" class="text-center py-16">
        <Clock class="w-16 h-16 text-gray-400 mx-auto mb-4" />
        <p class="text-gray-500 text-lg">Không có hoạt động nào</p>
      </div>

      <div v-else class="divide-y divide-gray-100">
        <div 
          v-for="(activity, index) in filteredActivities" 
          :key="index"
          class="p-6 hover:bg-emerald-50 transition-colors"
        >
          <div class="flex items-start gap-4">
            <!-- Icon -->
            <div 
              class="w-12 h-12 rounded-full flex items-center justify-center flex-shrink-0"
              :class="getActivityColor(activity.type)"
            >
              <component :is="activity.icon" class="w-6 h-6 text-white" />
            </div>
            
            <!-- Content -->
            <div class="flex-1">
              <div class="flex items-start justify-between">
                <div>
                  <h3 class="font-semibold text-gray-900 text-lg">{{ activity.action }}</h3>
                  <p v-if="activity.description" class="text-sm text-gray-600 mt-1">{{ activity.description }}</p>
                  <div class="flex items-center gap-4 mt-2">
                    <span class="text-xs text-gray-500 flex items-center gap-1">
                      <Clock class="w-3 h-3" />
                      {{ activity.time }}
                    </span>
                    <span 
                      class="text-xs font-semibold px-2 py-1 rounded"
                      :class="getTypeClass(activity.type)"
                    >
                      {{ getTypeLabel(activity.type) }}
                    </span>
                  </div>
                </div>
                
                <!-- Amount (for purchases) -->
                <div v-if="activity.amount" class="text-right">
                  <p class="text-lg font-bold text-emerald-600">{{ formatCurrency(activity.amount) }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <div v-if="filteredActivities.length > 0" class="flex justify-center mt-6 gap-2">
      <button 
        v-for="page in totalPages" 
        :key="page"
        @click="currentPage = page"
        class="px-4 py-2 rounded-lg font-semibold transition-all"
        :class="currentPage === page 
          ? 'bg-gradient-to-r from-emerald-600 to-teal-600 text-white' 
          : 'bg-gray-200 text-gray-700 hover:bg-gray-300'"
      >
        {{ page }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Clock, Activity, Dumbbell, Ticket, Heart, ShoppingBag } from 'lucide-vue-next'

// State
const loading = ref(true)
const filterType = ref('')
const filterStartDate = ref('')
const filterEndDate = ref('')
const currentPage = ref(1)
const itemsPerPage = 10

// Mock Activities Data
const allActivities = ref([])

// Fetch activities
const fetchActivities = async () => {
  loading.value = true
  
  // Simulate API delay
  await new Promise(resolve => setTimeout(resolve, 500))
  
  // Mock data - more comprehensive history
  allActivities.value = [
    { type: 'checkin', action: 'Đã check-in vào phòng gym', description: 'Khu vực tập luyện chính', time: '2 giờ trước', date: new Date(Date.now() - 2 * 60 * 60 * 1000), icon: Activity },
    { type: 'class', action: 'Tham gia lớp Yoga buổi sáng', description: 'Giáo viên: Nguyễn Thị Mai', time: '1 ngày trước', date: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000), icon: Dumbbell },
    { type: 'coupon', action: 'Nhận mã giảm giá mới', description: 'Giảm 20% cho gói Premium', time: '2 ngày trước', date: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000), icon: Ticket },
    { type: 'workout', action: 'Hoàn thành buổi tập cardio', description: '45 phút - Đốt cháy 320 calo', time: '3 ngày trước', date: new Date(Date.now() - 3 * 24 * 60 * 60 * 1000), icon: Heart },
    { type: 'purchase', action: 'Mua gói hội viên Gold', description: 'Gói 6 tháng', time: '5 ngày trước', date: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000), amount: 2500000, icon: ShoppingBag },
    { type: 'checkin', action: 'Đã check-in vào phòng gym', description: 'Khu vực cardio', time: '6 ngày trước', date: new Date(Date.now() - 6 * 24 * 60 * 60 * 1000), icon: Activity },
    { type: 'class', action: 'Tham gia lớp Boxing', description: 'Giáo viên: Trần Văn Nam', time: '7 ngày trước', date: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000), icon: Dumbbell },
    { type: 'workout', action: 'Hoàn thành buổi tập tạ', description: '60 phút - Tập nhóm cơ ngực', time: '8 ngày trước', date: new Date(Date.now() - 8 * 24 * 60 * 60 * 1000), icon: Heart },
    { type: 'checkin', action: 'Đã check-in vào phòng gym', description: 'Khu vực tập tạ', time: '9 ngày trước', date: new Date(Date.now() - 9 * 24 * 60 * 60 * 1000), icon: Activity },
    { type: 'class', action: 'Tham gia lớp Zumba', description: 'Giáo viên: Lê Thị Hoa', time: '10 ngày trước', date: new Date(Date.now() - 10 * 24 * 60 * 60 * 1000), icon: Dumbbell },
    { type: 'purchase', action: 'Mua protein whey', description: '2kg - Vị sô-cô-la', time: '12 ngày trước', date: new Date(Date.now() - 12 * 24 * 60 * 60 * 1000), amount: 850000, icon: ShoppingBag },
    { type: 'checkin', action: 'Đã check-in vào phòng gym', description: 'Khu vực tập luyện chính', time: '14 ngày trước', date: new Date(Date.now() - 14 * 24 * 60 * 60 * 1000), icon: Activity },
    { type: 'workout', action: 'Hoàn thành buổi tập HIIT', description: '30 phút - Đốt cháy 280 calo', time: '15 ngày trước', date: new Date(Date.now() - 15 * 24 * 60 * 60 * 1000), icon: Heart },
  ]
  
  loading.value = false
}

// Computed
const filteredActivities = computed(() => {
  let result = [...allActivities.value]
  
  // Filter by type
  if (filterType.value) {
    result = result.filter(a => a.type === filterType.value)
  }
  
  // Filter by date range
  if (filterStartDate.value) {
    const startDate = new Date(filterStartDate.value)
    result = result.filter(a => a.date >= startDate)
  }
  
  if (filterEndDate.value) {
    const endDate = new Date(filterEndDate.value)
    endDate.setHours(23, 59, 59, 999)
    result = result.filter(a => a.date <= endDate)
  }
  
  // Pagination
  const start = (currentPage.value - 1) * itemsPerPage
  const end = start + itemsPerPage
  return result.slice(start, end)
})

const totalPages = computed(() => {
  let result = [...allActivities.value]
  
  if (filterType.value) {
    result = result.filter(a => a.type === filterType.value)
  }
  
  if (filterStartDate.value) {
    const startDate = new Date(filterStartDate.value)
    result = result.filter(a => a.date >= startDate)
  }
  
  if (filterEndDate.value) {
    const endDate = new Date(filterEndDate.value)
    endDate.setHours(23, 59, 59, 999)
    result = result.filter(a => a.date <= endDate)
  }
  
  return Math.ceil(result.length / itemsPerPage)
})

// Methods
const getActivityColor = (type) => {
  const colors = {
    checkin: 'bg-gradient-to-br from-blue-500 to-blue-600',
    class: 'bg-gradient-to-br from-emerald-500 to-emerald-600',
    purchase: 'bg-gradient-to-br from-purple-500 to-purple-600',
    coupon: 'bg-gradient-to-br from-orange-500 to-orange-600',
    workout: 'bg-gradient-to-br from-pink-500 to-pink-600'
  }
  return colors[type] || 'bg-gradient-to-br from-gray-500 to-gray-600'
}

const getTypeClass = (type) => {
  const classes = {
    checkin: 'bg-blue-100 text-blue-700',
    class: 'bg-emerald-100 text-emerald-700',
    purchase: 'bg-purple-100 text-purple-700',
    coupon: 'bg-orange-100 text-orange-700',
    workout: 'bg-pink-100 text-pink-700'
  }
  return classes[type] || 'bg-gray-100 text-gray-700'
}

const getTypeLabel = (type) => {
  const labels = {
    checkin: 'Check-in',
    class: 'Lớp học',
    purchase: 'Mua hàng',
    coupon: 'Mã giảm giá',
    workout: 'Tập luyện'
  }
  return labels[type] || type
}

const formatCurrency = (amount) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount)
}

const applyFilters = () => {
  currentPage.value = 1
}

const resetFilters = () => {
  filterType.value = ''
  filterStartDate.value = ''
  filterEndDate.value = ''
  currentPage.value = 1
}

onMounted(() => {
  fetchActivities()
})
</script>
