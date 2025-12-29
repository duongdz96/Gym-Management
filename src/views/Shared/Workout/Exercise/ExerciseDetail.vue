<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, Dumbbell, Edit, Lightbulb } from 'lucide-vue-next'
import { exerciseApi } from '@/services/workoutApi'
import { useAuthStore } from '@/stores/useAuthStore'
import { useToast } from 'vue-toastification'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const toast = useToast();

// ==================== STATE ====================
const exercise = ref(null)
const isLoading = ref(false)

// ==================== METHODS ====================
const fetchExercise = async () => {
  isLoading.value = true
  try {
    const response = await exerciseApi.getExerciseById(route.params.id)
    exercise.value = response.data
  } catch (error) {
    console.error('Lỗi khi tải bài tập:', error)
    toast.error('Không thể tải thông tin bài tập')
    goBack()
  } finally {
    isLoading.value = false
  }
}

const goBack = () => {
  const basePath = authStore.user?.role === 'PT' ? '/pt' : '/customer'
  router.push(`${basePath}/workout/exercises`)
}

const goToEdit = () => {
  const basePath = authStore.user?.role === 'PT' ? '/pt' : '/customer'
  router.push(`${basePath}/workout/exercise/edit/${exercise.value.id}`)
}

const isMyExercise = () => {
  return exercise.value?.creator?.id === authStore.user?.id
}

// ==================== LIFECYCLE ====================
onMounted(() => {
  fetchExercise()
})
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-emerald-50 via-white to-orange-50 py-8">
    <div class="mx-auto max-w-4xl px-6">
      <!-- Back Button -->
      <button
        @click="goBack"
        class="flex items-center gap-2 text-gray-600 hover:text-emerald-600 mb-6 transition-colors"
      >
        <ArrowLeft class="w-5 h-5" />
        <span class="font-medium">Quay lại danh sách</span>
      </button>

      <!-- Loading State -->
      <div v-if="isLoading" class="flex justify-center items-center h-64">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-emerald-600"></div>
      </div>

      <!-- Exercise Detail -->
      <div v-else-if="exercise" class="bg-white rounded-2xl shadow-lg overflow-hidden">
        <!-- Header -->
        <div class="bg-gradient-to-r from-emerald-500 to-emerald-600 p-8 text-white">
          <div class="flex items-start justify-between">
            <div class="flex items-center gap-4">
              <div class="w-16 h-16 rounded-full bg-white/20 backdrop-blur-sm flex items-center justify-center">
                <Dumbbell class="w-8 h-8 text-white" />
              </div>
              <div>
                <h1 class="text-3xl font-bold mb-2">{{ exercise.name }}</h1>
                <div class="flex items-center gap-3">
                  <span class="px-3 py-1 bg-white/20 backdrop-blur-sm rounded-full text-sm font-medium">
                    {{ exercise.muscleGroup }}
                  </span>
                  <span v-if="isMyExercise()" class="px-3 py-1 bg-white/20 backdrop-blur-sm rounded-full text-sm font-medium">
                    Của tôi
                  </span>
                </div>
              </div>
            </div>
            
            <button
              v-if="isMyExercise()"
              @click="goToEdit"
              class="flex items-center gap-2 bg-white text-emerald-600 px-4 py-2 rounded-lg font-semibold hover:bg-emerald-50 transition-colors"
            >
              <Edit class="w-4 h-4" />
              Sửa
            </button>
          </div>
        </div>

        <!-- Content -->
        <div class="p-8">
          <!-- Creator Info -->
          <div class="mb-8 pb-6 border-b border-gray-200">
            <h2 class="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-2">
              Thông Tin
            </h2>
            <div class="flex items-center gap-2 text-gray-700">
              <span class="font-medium">Tạo bởi:</span>
              <span>{{ exercise.creator ? exercise.creator.fullName : 'Hệ thống' }}</span>
            </div>
          </div>

          <!-- Description -->
          <div class="mb-8">
            <h2 class="text-xl font-bold text-gray-900 mb-4">
              Hướng Dẫn Thực Hiện
            </h2>
            <div class="prose max-w-none">
              <p class="text-gray-700 leading-relaxed whitespace-pre-wrap">{{ exercise.description }}</p>
            </div>
          </div>

          <!-- Tips Section (Optional Enhancement) -->
          <div class="bg-emerald-50 border border-emerald-200 rounded-xl p-6">
            <h3 class="text-lg font-bold text-emerald-900 mb-3 flex items-center gap-2">
              <Lightbulb class="w-5 h-5" />
              Lưu Ý
            </h3>
            <ul class="space-y-2 text-emerald-800">
              <li class="flex items-start gap-2">
                <span class="text-emerald-600 mt-1">•</span>
                <span>Khởi động kỹ trước khi thực hiện bài tập</span>
              </li>
              <li class="flex items-start gap-2">
                <span class="text-emerald-600 mt-1">•</span>
                <span>Tập trung vào kỹ thuật đúng hơn là tăng trọng lượng</span>
              </li>
              <li class="flex items-start gap-2">
                <span class="text-emerald-600 mt-1">•</span>
                <span>Nghỉ ngơi đầy đủ giữa các set</span>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.prose {
  font-size: 1rem;
  line-height: 1.75;
}
</style>
