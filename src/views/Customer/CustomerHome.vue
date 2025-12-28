<script setup>
import { ref, onMounted, computed } from 'vue'
import { RouterLink } from 'vue-router'
import { Camera, X, TrendingUp, Calendar, Award, Ticket, Activity, Clock, Dumbbell, Heart } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/useAuthStore'
import api from '@/services/api'

const authStore = useAuthStore()

// ===================== STATE =====================
const rating = ref(0)
const hoverRating = ref(0)
const comment = ref('')
const images = ref([])
const isLoading = ref(true)

// Personal Stats
const personalStats = ref({
  activeMembership: null,
  upcomingClasses: 0,
  totalCheckIns: 0,
  availableCoupons: 0,
  membershipDaysLeft: 0
})

const recentActivities = ref([])

// ===================== FETCH DATA =====================
const fetchPersonalStats = async () => {
  isLoading.value = true
  
  try {
    const memberId = authStore.user?.id
    if (!memberId) {
      console.error('No member ID found')
      isLoading.value = false
      return
    }

    // Fetch membership info
    const membershipRes = await api.get('/membership')
    const allMemberships = membershipRes.data || []
    const userMembership = allMemberships.find(m => m.member?.id === memberId && m.status === 'Active')
    
    if (userMembership) {
      personalStats.value.activeMembership = {
        tier: userMembership.membershipPlan?.name || 'Basic',
        startDate: userMembership.startDate,
        endDate: userMembership.endDate
      }
      
      const endDate = new Date(userMembership.endDate)
      const today = new Date()
      const daysLeft = Math.ceil((endDate - today) / (1000 * 60 * 60 * 24))
      personalStats.value.membershipDaysLeft = daysLeft > 0 ? daysLeft : 0
    }

    // Fetch upcoming classes (from member registrations)
    try {
      const registrationsRes = await api.get(`/member-registrations/member/${memberId}`)
      const allRegistrations = registrationsRes.data || []
      
      // Filter for future classes only
      const now = new Date()
      const upcomingClasses = allRegistrations.filter(r => {
        const classDate = new Date(r.classSchedule?.startTime)
        return classDate > now && r.classSchedule?.status === 'OPEN'
      })
      
      personalStats.value.upcomingClasses = upcomingClasses.length
    } catch (error) {
      console.error('Error fetching class registrations:', error)
      personalStats.value.upcomingClasses = 0
    }

    // Fetch available coupons
    try {
      const couponsRes = await api.get(`/issued-coupons/find?memberId=${memberId}`)
      const allCoupons = couponsRes.data || []
      // Filter for available coupons with remaining uses and valid dates
      const now = new Date()
      const userCoupons = allCoupons.filter(c => 
        c.status === 'AVAILABLE' && 
        c.remainingUses > 0 &&
        new Date(c.coupon?.endDate) > now
      )
      personalStats.value.availableCoupons = userCoupons.length
    } catch (error) {
      console.error('Error fetching coupons:', error)
      personalStats.value.availableCoupons = 0
    }

    // Mock check-ins for now (TODO: implement real API)
    personalStats.value.totalCheckIns = 18
    
    // Mock recent activities (TODO: implement real activity log API)
    recentActivities.value = [
      { type: 'checkin', action: 'Đã check-in vào phòng gym', time: '2 giờ trước', icon: Activity },
      { type: 'class', action: 'Tham gia lớp Yoga buổi sáng', time: '1 ngày trước', icon: Dumbbell },
      { type: 'coupon', action: 'Nhận mã giảm giá mới', time: '2 ngày trước', icon: Ticket },
      { type: 'workout', action: 'Hoàn thành buổi tập cardio', time: '3 ngày trước', icon: Heart }
    ]
  } catch (error) {
    console.error('Error fetching personal stats:', error)
  } finally {
    isLoading.value = false
  }
}

// ===================== FEEDBACK =====================
const setRating = (star) => {
  rating.value = star
}

const previewImages = (event) => {
  const files = Array.from(event.target.files)
  images.value.push(
    ...files.map((file) => ({
      file,
      url: URL.createObjectURL(file)
    }))
  )
}

const removeImage = (index) => {
  images.value.splice(index, 1)
}

const submitFeedback = async () => {
  if (!rating.value) {
    alert('Vui lòng chọn số sao đánh giá!')
    return
  }
  
  if (!comment.value.trim()) {
    alert('Vui lòng nhập nội dung đánh giá!')
    return
  }
  
  try {
    const formData = new FormData()
    formData.append('rating', rating.value)
    formData.append('comment', comment.value)
    formData.append('memberId', authStore.user?.id || 1)

    images.value.forEach((img) => {
      formData.append('images', img.file)
    })

    await api.post('/feedbacks', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })

    alert('Cảm ơn bạn đã gửi đánh giá!')
    rating.value = 0
    comment.value = ''
    images.value = []
  } catch (error) {
    console.error('Error submitting feedback:', error)
    alert('Lỗi khi gửi đánh giá. Vui lòng thử lại!')
  }
}

// ===================== COMPUTED =====================
const membershipStatusColor = computed(() => {
  if (!personalStats.value.activeMembership) return 'gray'
  if (personalStats.value.membershipDaysLeft > 30) return 'green'
  if (personalStats.value.membershipDaysLeft > 7) return 'yellow'
  return 'red'
})

onMounted(() => {
  fetchPersonalStats()
})
</script>

<template>
  <div class="min-h-screen bg-white py-8">
    <div class="mx-auto max-w-7xl px-6">
      <!-- Header -->
      <div class="mb-8">
        <h1 class="text-4xl font-bold bg-gradient-to-r from-emerald-600 to-emerald-600 bg-clip-text text-transparent">
          Chào mừng trở lại, {{ authStore.user?.fullName || 'Hội viên' }} 👋
        </h1>
        <p class="text-gray-600 mt-2">Tổng quan hành trình tập luyện của bạn</p>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading" class="flex justify-center items-center h-64">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-emerald-600"></div>
      </div>

      <div v-else class="space-y-8">
        <!-- Personal Stats Cards -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <!-- Membership Card -->
          <div class="bg-white/80 backdrop-blur-lg rounded-2xl shadow-lg p-6 border border-emerald-100 hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1">
            <div class="flex items-center justify-between mb-4">
              <div class="w-12 h-12 rounded-full bg-gradient-to-br from-emerald-500 to-emerald-500 flex items-center justify-center">
                <Award class="w-6 h-6 text-white" />
              </div>
              <span :class="`px-3 py-1 rounded-full text-xs font-semibold bg-${membershipStatusColor}-100 text-${membershipStatusColor}-700`">
                {{ personalStats.membershipDaysLeft }} ngày còn lại
              </span>
            </div>
            <h3 class="text-gray-600 text-sm font-medium mb-1">Gói thành viên</h3>
            <p class="text-2xl font-bold text-gray-900">{{ personalStats.activeMembership?.tier || 'Chưa kích hoạt' }}</p>
            <RouterLink to="/customer/membership" class="text-emerald-600 text-sm font-medium hover:underline mt-2 inline-block">
              Xem chi tiết →
            </RouterLink>
          </div>

          <!-- Upcoming Classes -->
          <div class="bg-white/80 backdrop-blur-lg rounded-2xl shadow-lg p-6 border border-white hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1">
            <div class="flex items-center justify-between mb-4">
              <div class="w-12 h-12 rounded-full bg-gradient-to-br from-emerald-500 to-emerald-500 flex items-center justify-center">
                <Calendar class="w-6 h-6 text-white" />
              </div>
            </div>
            <h3 class="text-gray-600 text-sm font-medium mb-1">Lớp học sắp tới</h3>
            <p class="text-2xl font-bold text-gray-900">{{ personalStats.upcomingClasses }}</p>
            <RouterLink to="/customer/class" class="text-emerald-600 text-sm font-medium hover:underline mt-2 inline-block">
              Xem lịch học →
            </RouterLink>
          </div>

          <!-- Check-ins -->
          <div class="bg-white/80 backdrop-blur-lg rounded-2xl shadow-lg p-6 border border-emerald-100 hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1">
            <div class="flex items-center justify-between mb-4">
              <div class="w-12 h-12 rounded-full bg-gradient-to-br from-emerald-500 to-teal-500 flex items-center justify-center">
                <TrendingUp class="w-6 h-6 text-white" />
              </div>
            </div>
            <h3 class="text-gray-600 text-sm font-medium mb-1">Check-in tháng này</h3>
            <p class="text-2xl font-bold text-gray-900">{{ personalStats.totalCheckIns }}</p>
            <p class="text-sm text-gray-500 mt-2">Tiếp tục phát huy! 💪</p>
          </div>

          <!-- Coupons -->
          <div class="bg-white/80 backdrop-blur-lg rounded-2xl shadow-lg p-6 border border-emerald-100 hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1">
            <div class="flex items-center justify-between mb-4">
              <div class="w-12 h-12 rounded-full bg-gradient-to-br from-emerald-500 to-teal-500 flex items-center justify-center">
                <Ticket class="w-6 h-6 text-white" />
              </div>
            </div>
            <h3 class="text-gray-600 text-sm font-medium mb-1">Mã giảm giá khả dụng</h3>
            <p class="text-2xl font-bold text-gray-900">{{ personalStats.availableCoupons }}</p>
            <RouterLink to="/customer/coupon" class="text-emerald-600 text-sm font-medium hover:underline mt-2 inline-block">
              Xem mã giảm giá →
            </RouterLink>
          </div>
        </div>

        <!-- Activity Timeline -->
        <div class="bg-white/80 backdrop-blur-lg rounded-2xl shadow-lg p-6 border border-emerald-100">
          <div class="flex items-center justify-between mb-6">
            <div class="flex items-center gap-2">
              <Clock class="w-5 h-5 text-emerald-600" />
              <h2 class="text-xl font-bold text-gray-900">Hoạt động gần đây</h2>
            </div>
            <RouterLink to="/customer/activity-history" class="text-emerald-600 text-sm font-medium hover:underline flex items-center gap-1">
              Xem tất cả →
            </RouterLink>
          </div>
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            <RouterLink v-for="(activity, index) in recentActivities" :key="index" to="/customer/activity-history" class="flex flex-col items-center gap-3 p-5 bg-gradient-to-r from-emerald-50 to-emerald-50 rounded-xl hover:shadow-md hover:scale-105 transition-all cursor-pointer">
              <div class="w-12 h-12 rounded-full bg-gradient-to-br from-emerald-500 to-emerald-500 flex items-center justify-center">
                <component :is="activity.icon" class="w-6 h-6 text-white" />
              </div>
              <div class="text-center">
                <p class="text-sm font-medium text-gray-900">{{ activity.action }}</p>
                <p class="text-xs text-gray-500 mt-1">{{ activity.time }}</p>
              </div>
            </RouterLink>
          </div>
        </div>

        <!-- Feedback Section -->
        <div class="bg-white/80 backdrop-blur-lg rounded-2xl shadow-lg p-8 border border-emerald-100">
          <h2 class="text-2xl font-bold text-gray-900 mb-6">Chia sẻ đánh giá của bạn</h2>

          <!-- Star Rating -->
          <div class="flex items-center mb-6">
            <template v-for="star in 5" :key="star">
              <svg
                @click="setRating(star)"
                @mouseover="hoverRating = star"
                @mouseleave="hoverRating = 0"
                class="w-8 h-8 cursor-pointer transition-all"
                :class="[(hoverRating >= star || rating >= star) ? 'text-yellow-400' : 'text-gray-300']"
                xmlns="http://www.w3.org/2000/svg"
                fill="currentColor"
                viewBox="0 0 22 20"
              >
                <path d="M20.924 7.625a1.523 1.523 0 0 0-1.238-1.044l-5.051-.734-2.259-4.577a1.534 1.534 0 0 0-2.752 0L7.365 5.847l-5.051.734A1.535 1.535 0 0 0 1.463 9.2l3.656 3.563-.863 5.031a1.532 1.532 0 0 0 2.226 1.616L11 17.033l4.518 2.375a1.534 1.534 0 0 0 2.226-1.617l-.863-5.03L20.537 9.2a1.523 1.523 0 0 0 .387-1.575Z" />
              </svg>
            </template>
          </div>

          <!-- Comment Box -->
          <textarea
            v-model="comment"
            rows="4"
            placeholder="Chia sẻ trải nghiệm của bạn tại phòng gym của chúng tôi"
            class="w-full border border-gray-300 rounded-xl p-4 mb-6 focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500 focus:outline-none"
          ></textarea>

          <!-- Upload Images -->
          <div class="mb-6">
            <label class="block text-sm font-medium text-gray-700 mb-3">Tải ảnh lên (không bắt buộc)</label>
            <div class="flex flex-wrap gap-3">
              <label class="w-24 h-24 border-2 border-dashed border-emerald-300 rounded-xl flex items-center justify-center cursor-pointer hover:border-emerald-500 hover:bg-emerald-50 transition-all">
                <Camera class="w-8 h-8 text-emerald-400" />
                <input type="file" class="hidden" multiple accept="image/png, image/jpeg, image/jpg, image/gif, image/webp" @change="previewImages" />
              </label>

              <div v-for="(img, index) in images" :key="index" class="relative w-24 h-24 rounded-xl overflow-hidden border-2 border-emerald-200">
                <img :src="img.url" class="w-full h-full object-cover" />
                <button @click="removeImage(index)" class="absolute top-1 right-1 bg-black bg-opacity-50 text-white rounded-full p-1 hover:bg-opacity-70">
                  <X class="w-4 h-4" />
                </button>
              </div>
            </div>
          </div>

          <!-- Submit -->
          <button @click="submitFeedback" class="bg-gradient-to-r from-emerald-600 to-emerald-600 text-white px-8 py-3 rounded-xl font-semibold hover:shadow-lg transition-all transform hover:scale-105">
            Gửi đánh giá
          </button>
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

.space-y-8 > * {
  animation: fadeIn 0.5s ease-out;
}
</style>
