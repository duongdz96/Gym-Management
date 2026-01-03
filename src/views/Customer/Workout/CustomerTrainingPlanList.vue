<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Calendar, Play, CheckCircle, Clock, ListChecks, Eye, TrendingUp, Plus } from 'lucide-vue-next'
import { trainingPlanApi } from '@/services/workoutApi'
import { useAuthStore } from '@/stores/useAuthStore'

const authStore = useAuthStore()
const router = useRouter()

// ==================== STATE ====================
const trainingPlans = ref([])
const isLoading = ref(false)
const selectedFilter = ref('all') // all, pending, completed

// ==================== COMPUTED ====================
const filteredPlans = computed(() => {
  if (selectedFilter.value === 'all') return trainingPlans.value
  return trainingPlans.value.filter(plan => 
    plan.status.toLowerCase() === selectedFilter.value
  )
})

const getStatusColor = (status) => {
  const colors = {
    'PENDING': 'bg-yellow-100 text-yellow-700',
    'COMPLETED': 'bg-emerald-100 text-emerald-700',
    'CANCELLED': 'bg-red-100 text-red-700'
  }
  return colors[status] || 'bg-gray-100 text-gray-700'
}

const getStatusText = (status) => {
  const texts = {
    'PENDING': 'Chưa tập',
    'COMPLETED': 'Hoàn thành',
    'CANCELLED': 'Đã hủy'
  }
  return texts[status] || status
}

// ==================== METHODS ====================
const fetchTrainingPlans = async () => {
  isLoading.value = true
  try {
    const memberId = authStore.user?.id
    if (!memberId) return

    const response = await trainingPlanApi.getMyPlans(memberId)
    trainingPlans.value = response.data.sort((a, b) => 
      new Date(b.date) - new Date(a.date)
    )
  } catch (error) {
    console.error('Lỗi khi tải lịch tập:', error)
  } finally {
    isLoading.value = false
  }
}

const goToCreatePlan = () => {
  router.push('/customer/workout/routines')
}

const startWorkout = (plan) => {
  router.push(`/customer/workout/training-plan/start/${plan.id}`)
}

const viewPlanDetail = (plan) => {
  router.push(`/customer/workout/training-plan/start/${plan.id}`)
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  const today = new Date()
  const yesterday = new Date(today)
  yesterday.setDate(yesterday.getDate() - 1)

  if (date.toDateString() === today.toDateString()) {
    return 'Hôm nay'
  } else if (date.toDateString() === yesterday.toDateString()) {
    return 'Hôm qua'
  }
  
  return date.toLocaleDateString('vi-VN', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

// ==================== LIFECYCLE ====================
onMounted(() => {
  fetchTrainingPlans()
})
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-emerald-50 via-white to-teal-50 py-4 sm:py-8">
    <div class="mx-auto max-w-7xl px-4 sm:px-6">
      <!-- Header -->
      <div class="mb-6 sm:mb-8 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h1 class="text-3xl sm:text-4xl font-bold bg-gradient-to-r from-emerald-600 to-teal-600 bg-clip-text text-transparent">
            Lịch Tập Của Tôi
          </h1>
          <p class="text-gray-600 text-sm sm:text-base mt-2">Theo dõi và quản lý lịch tập luyện</p>
        </div>
        <button
          @click="goToCreatePlan"
          class="w-full sm:w-auto flex items-center justify-center gap-2 bg-gradient-to-r from-emerald-600 to-teal-600 text-white px-4 sm:px-6 py-2.5 sm:py-3 text-sm sm:text-base rounded-xl font-semibold hover:shadow-lg transition-all transform hover:scale-105"
        >
          <Plus class="w-5 h-5" />
          Tạo Lịch Tập Hôm Nay
        </button>
      </div>

      <!-- Filter -->
      <div class="bg-white rounded-xl sm:rounded-2xl shadow-lg p-4 sm:p-6 mb-4 sm:mb-6">
        <div class="flex flex-wrap gap-2">
          <button
            @click="selectedFilter = 'all'"
            :class="selectedFilter === 'all' ? 'bg-red-600 text-white' : 'bg-white text-gray-700 border border-gray-300'"
            class="px-3 sm:px-4 py-2 text-sm sm:text-base rounded-lg font-semibold transition-colors"
          >
            Tất cả
          </button>
          <button
            @click="selectedFilter = 'pending'"
            :class="selectedFilter === 'pending' ? 'bg-yellow-600 text-white' : 'bg-white text-gray-700 border border-gray-300'"
            class="px-4 py-2 rounded-lg font-semibold transition-colors"
          >
            Chưa tập
          </button>
          <button
            @click="selectedFilter = 'completed'"
            :class="selectedFilter === 'completed' ? 'bg-green-600 text-white' : 'bg-white text-gray-700 border border-gray-300'"
            class="px-4 py-2 rounded-lg font-semibold transition-colors"
          >
            Hoàn thành
          </button>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading" class="flex justify-center items-center h-64">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-emerald-600"></div>
      </div>

      <!-- Training Plans List -->
      <div v-else-if="filteredPlans.length > 0" class="space-y-3 sm:space-y-4">
        <div
          v-for="plan in filteredPlans"
          :key="plan.id"
          class="bg-white rounded-xl sm:rounded-2xl shadow-lg p-4 sm:p-6 hover:shadow-xl transition-all duration-300"
        >
          <div class="flex flex-col sm:flex-row items-start justify-between gap-3 sm:gap-4">
            <div class="flex-1 w-full sm:w-auto">
              <!-- Date -->
              <div class="flex items-center gap-2 sm:gap-3 mb-2 sm:mb-3 flex-wrap">
                <Calendar class="w-4 h-4 sm:w-5 sm:h-5 text-emerald-600 flex-shrink-0" />
                <h3 class="text-lg sm:text-xl font-bold text-gray-900">{{ formatDate(plan.date) }}</h3>
                <span :class="`px-3 py-1 rounded-full text-xs font-semibold ${getStatusColor(plan.status)}`">
                  {{ getStatusText(plan.status) }}
                </span>
              </div>

              <!-- Muscle Group -->
              <p class="text-emerald-600 font-medium mb-2 flex items-center gap-2">
                <Dumbbell class="w-5 h-5" />
                {{ plan.muscleGroupFocus }}
              </p>

              <!-- Exercise Count -->
              <div class="flex items-center gap-2 text-sm text-gray-500 mb-3">
                <ListChecks class="w-4 h-4" />
                <span>{{ plan.details?.length || 0 }} bài tập</span>
              </div>

              <!-- Notes -->
              <p class="text-gray-600 text-sm">{{ plan.notes }}</p>
            </div>
            
            <!-- Action Button -->
            <div class="w-full sm:w-auto sm:ml-4">
              <button
                v-if="plan.status === 'PENDING'"
                @click="startWorkout(plan)"
                class="w-full flex items-center justify-center gap-2 bg-gradient-to-r from-emerald-600 to-teal-600 text-white px-4 sm:px-6 py-2.5 sm:py-3 text-sm sm:text-base rounded-xl font-semibold hover:shadow-lg transition-all transform hover:scale-105"
              >
                <Play class="w-4 h-4 sm:w-5 sm:h-5" />
                Bắt Đầu Tập
              </button>
              <button
                v-else-if="plan.status === 'COMPLETED'"
                @click="viewPlanDetail(plan)"
                class="w-full flex items-center justify-center gap-2 bg-emerald-50 text-emerald-600 px-4 sm:px-6 py-2.5 sm:py-3 text-sm sm:text-base rounded-xl font-semibold hover:bg-emerald-100 transition-colors"
              >
                <CheckCircle class="w-5 h-5" />
                Đã Hoàn Thành
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else class="text-center py-12 sm:py-16 bg-white rounded-xl sm:rounded-2xl shadow-lg">
        <Calendar class="w-12 h-12 sm:w-16 sm:h-16 text-gray-300 mx-auto mb-4" />
        <h3 class="text-lg sm:text-xl font-semibold text-gray-600 mb-2">Chưa có lịch tập nào</h3>
        <p class="text-sm sm:text-base text-gray-500 mb-4 sm:mb-6">Tạo lịch tập đầu tiên để bắt đầu!</p>
        <button
          @click="goToCreatePlan"
          class="inline-flex items-center gap-2 bg-gradient-to-r from-emerald-600 to-teal-600 text-white px-4 sm:px-6 py-2.5 sm:py-3 text-sm sm:text-base rounded-xl font-semibold hover:shadow-lg transition-all"
        >
          <Plus class="w-5 h-5" />
          Tạo Lịch Tập Hôm Nay
        </button>
      </div>
    </div>
  </div>
</template>
