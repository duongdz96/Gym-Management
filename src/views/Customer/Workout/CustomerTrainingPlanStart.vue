<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, CheckCircle, Circle, Dumbbell, TrendingUp } from 'lucide-vue-next'
import { trainingPlanApi } from '@/services/workoutApi'

const route = useRoute()
const router = useRouter()

// ==================== STATE ====================
const trainingPlan = ref(null)
const isLoading = ref(false)
const completedExercises = ref(new Set())
const isCompleting = ref(false)

// ==================== COMPUTED ====================
const progress = computed(() => {
  if (!trainingPlan.value?.details) return 0
  const total = trainingPlan.value.details.length
  const completed = completedExercises.value.size
  return Math.round((completed / total) * 100)
})

const allCompleted = computed(() => {
  return trainingPlan.value?.details?.length > 0 && 
         completedExercises.value.size === trainingPlan.value.details.length
})

// ==================== METHODS ====================
const fetchTrainingPlan = async () => {
  isLoading.value = true
  try {
    console.log('Fetching training plan with ID:', route.params.id)
    const response = await trainingPlanApi.getPlanById(route.params.id)
    console.log('Training plan response:', response.data)
    trainingPlan.value = response.data
  } catch (error) {
    console.error('Lỗi khi tải lịch tập:', error)
    console.error('Error details:', error.response?.data)
    alert(`Không thể tải lịch tập. Lỗi: ${error.response?.data?.message || error.message}`)
    goBack()
  } finally {
    isLoading.value = false
  }
}

const toggleExercise = async (exerciseId) => {
  if (completedExercises.value.has(exerciseId)) {
    completedExercises.value.delete(exerciseId)
  } else {
    completedExercises.value.add(exerciseId)
    try {
      await trainingPlanApi.completeExercise(route.params.id, exerciseId)
    } catch (error) {
      console.error('Lỗi khi đánh dấu bài tập:', error)
    }
  }
}

const completePlan = async () => {
  if (!allCompleted.value) {
    alert('Vui lòng hoàn thành tất cả bài tập!')
    return
  }

  isCompleting.value = true
  try {
    await trainingPlanApi.completePlan(route.params.id)
    alert('Chúc mừng! Bạn đã hoàn thành buổi tập!')
    router.push('/customer/workout/training-plans')
  } catch (error) {
    console.error('Lỗi khi hoàn thành lịch tập:', error)
    alert('Không thể hoàn thành lịch tập')
  } finally {
    isCompleting.value = false
  }
}

const goBack = () => {
  router.push('/customer/workout/training-plans')
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('vi-VN', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

// ==================== LIFECYCLE ====================
onMounted(() => {
  fetchTrainingPlan()
})
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-red-50 via-white to-orange-50 py-8">
    <div class="mx-auto max-w-4xl px-6">
      <!-- Back Button -->
      <button
        @click="goBack"
        class="flex items-center gap-2 text-gray-600 hover:text-red-600 mb-6 transition-colors"
      >
        <ArrowLeft class="w-5 h-5" />
        <span class="font-medium">Quay lại</span>
      </button>

      <!-- Loading State -->
      <div v-if="isLoading" class="flex justify-center items-center h-64">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-red-600"></div>
      </div>

      <!-- Training Plan Content -->
      <div v-else-if="trainingPlan" class="space-y-6">
        <!-- Header Card -->
        <div class="bg-gradient-to-r from-red-500 to-orange-500 rounded-2xl p-8 text-white shadow-lg">
          <div class="flex items-start justify-between mb-4">
            <div>
              <h1 class="text-3xl font-bold mb-2">Bắt Đầu Tập</h1>
              <p class="text-white/90">{{ formatDate(trainingPlan.date) }}</p>
            </div>
            <div class="text-right">
              <div class="text-4xl font-bold">{{ progress }}%</div>
              <div class="text-sm text-white/90">Hoàn thành</div>
            </div>
          </div>

          <!-- Muscle Group -->
          <div class="flex items-center gap-2 bg-white/20 backdrop-blur-sm rounded-lg px-4 py-2 inline-flex">
            <Dumbbell class="w-5 h-5" />
            <span class="font-semibold">{{ trainingPlan.muscleGroupFocus }}</span>
          </div>

          <!-- Progress Bar -->
          <div class="mt-6">
            <div class="bg-white/20 rounded-full h-3 overflow-hidden">
              <div 
                class="bg-white h-full transition-all duration-500 ease-out"
                :style="{ width: `${progress}%` }"
              ></div>
            </div>
            <p class="text-sm text-white/90 mt-2">
              {{ completedExercises.size }} / {{ trainingPlan.details?.length || 0 }} bài tập
            </p>
          </div>
        </div>

        <!-- Exercise List -->
        <div class="bg-white rounded-2xl shadow-lg p-6">
          <h2 class="text-xl font-bold text-gray-900 mb-6 flex items-center gap-2">
            <Dumbbell class="w-6 h-6 text-red-600" />
            Danh Sách Bài Tập
          </h2>

          <div class="space-y-3">
            <div
              v-for="(detail, index) in trainingPlan.details"
              :key="detail.id"
              @click="toggleExercise(detail.exercise.id)"
              :class="[
                'p-4 rounded-xl border-2 cursor-pointer transition-all',
                completedExercises.has(detail.exercise.id)
                  ? 'border-green-500 bg-green-50'
                  : 'border-gray-200 hover:border-red-300 hover:bg-red-50'
              ]"
            >
              <div class="flex items-start gap-4">
                <!-- Checkbox Icon -->
                <div class="flex-shrink-0 mt-1">
                  <component
                    :is="completedExercises.has(detail.exercise.id) ? CheckCircle : Circle"
                    :class="completedExercises.has(detail.exercise.id) ? 'text-green-600' : 'text-gray-400'"
                    class="w-6 h-6"
                  />
                </div>

                <!-- Exercise Info -->
                <div class="flex-1">
                  <div class="flex items-start justify-between">
                    <div>
                      <h3 :class="[
                        'font-bold text-lg',
                        completedExercises.has(detail.exercise.id) ? 'text-green-900 line-through' : 'text-gray-900'
                      ]">
                        {{ index + 1 }}. {{ detail.exercise.name }}
                      </h3>
                      <p class="text-sm text-gray-600 mt-1">{{ detail.exercise.muscleGroup }}</p>
                    </div>
                    <div class="text-right">
                      <div class="text-red-600 font-bold">
                        {{ detail.setCount }} sets
                      </div>
                      <div class="text-sm text-gray-600">
                        {{ detail.repCount }} reps
                      </div>
                    </div>
                  </div>

                  <!-- Description Preview -->
                  <p class="text-sm text-gray-500 mt-2 line-clamp-2">
                    {{ detail.exercise.description }}
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Complete Button -->
        <div class="bg-white rounded-2xl shadow-lg p-6">
          <button
            @click="completePlan"
            :disabled="!allCompleted || isCompleting"
            :class="[
              'w-full flex items-center justify-center gap-3 px-8 py-4 rounded-xl font-bold text-lg transition-all transform',
              allCompleted && !isCompleting
                ? 'bg-gradient-to-r from-green-600 to-emerald-600 text-white hover:shadow-lg hover:scale-105'
                : 'bg-gray-300 text-gray-500 cursor-not-allowed'
            ]"
          >
            <TrendingUp class="w-6 h-6" />
            {{ isCompleting ? 'Đang xử lý...' : allCompleted ? 'Hoàn Thành Buổi Tập!' : 'Hoàn thành tất cả bài tập để kết thúc' }}
          </button>

          <p v-if="!allCompleted" class="text-center text-sm text-gray-500 mt-3">
            Còn {{ (trainingPlan.details?.length || 0) - completedExercises.size }} bài tập chưa hoàn thành
          </p>
        </div>

        <!-- Motivation Card -->
        <div class="bg-gradient-to-r from-emerald-50 to-teal-50 border border-emerald-200 rounded-xl p-6">
          <h3 class="text-lg font-bold text-emerald-900 mb-3">💪 Động lực</h3>
          <ul class="space-y-2 text-emerald-800">
            <li class="flex items-start gap-2">
              <span class="text-emerald-600 mt-1">•</span>
              <span>Tập trung vào kỹ thuật đúng</span>
            </li>
            <li class="flex items-start gap-2">
              <span class="text-emerald-600 mt-1">•</span>
              <span>Nghỉ ngơi 60-90 giây giữa các set</span>
            </li>
            <li class="flex items-start gap-2">
              <span class="text-emerald-600 mt-1">•</span>
              <span>Uống nước đầy đủ trong quá trình tập</span>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
