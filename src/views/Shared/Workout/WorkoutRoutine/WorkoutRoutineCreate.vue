<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Save, X, Plus, Trash2, ListChecks, Globe, Lock } from 'lucide-vue-next'
import { workoutRoutineApi, exerciseApi } from '@/services/workoutApi'
import { useAuthStore } from '@/stores/useAuthStore'
import { useToast } from 'vue-toastification'

const authStore = useAuthStore()
const router = useRouter()
const route = useRoute()
const toast = useToast()

// ==================== STATE ====================
const formData = ref({
  name: '',
  muscleGroupFocus: '',
  description: '',
  isPublic: false,
  creator: {
    id: authStore.user?.id
  },
  routineDetails: []
})

const exercises = ref([])
const isLoadingExercises = ref(false)
const isSubmitting = ref(false)
const isLoading = ref(false)

const muscleGroups = [
  'Ngực',
  'Lưng',
  'Vai',
  'Tay trước',
  'Tay sau',
  'Chân',
  'Bụng',
  'Toàn thân'
]

// ==================== COMPUTED ====================
const isEditMode = computed(() => !!route.params.id)
const pageTitle = computed(() => isEditMode.value ? 'Sửa Mẫu Lịch Tập' : 'Tạo Mẫu Lịch Tập Mới')
const pageSubtitle = computed(() => isEditMode.value ? 'Cập nhật thông tin mẫu lịch tập' : 'Tạo mẫu lịch tập để sử dụng hoặc chia sẻ')
const submitButtonText = computed(() => isSubmitting.value ? 'Đang lưu...' : (isEditMode.value ? 'Cập Nhật' : 'Lưu Mẫu Lịch Tập'))

// ==================== METHODS ====================
const fetchRoutine = async () => {
  if (!isEditMode.value) return
  
  isLoading.value = true
  try {
    const response = await workoutRoutineApi.getRoutineById(route.params.id)
    const routine = response.data
    formData.value = {
      name: routine.name,
      muscleGroupFocus: routine.muscleGroupFocus,
      description: routine.description || '',
      isPublic: routine.isPublic,
      creator: routine.creator,
      routineDetails: routine.routineDetails.map(detail => ({
        exercise: { id: detail.exercise.id },
        defaultSetCount: detail.defaultSetCount,
        defaultRepCount: detail.defaultRepCount
      }))
    }
  } catch (error) {
    console.error('Lỗi khi tải mẫu lịch tập:', error)
    toast.error('Không thể tải thông tin mẫu lịch tập')
    handleCancel()
  } finally {
    isLoading.value = false
  }
}

const fetchExercises = async () => {
  isLoadingExercises.value = true
  try {
    const userId = authStore.user?.id
    const response = userId
      ? await exerciseApi.getMyExercises(userId)
      : await exerciseApi.getAllExercises()
    
    exercises.value = response.data
  } catch (error) {
    console.error('Lỗi khi tải danh sách bài tập:', error)
  } finally {
    isLoadingExercises.value = false
  }
}

const addExerciseDetail = () => {
  formData.value.routineDetails.push({
    exercise: { id: null },
    defaultSetCount: 3,
    defaultRepCount: '10-12'
  })
}

const removeExerciseDetail = (index) => {
  formData.value.routineDetails.splice(index, 1)
}

const validateForm = () => {
  if (!formData.value.name.trim()) {
    toast.warning('Vui lòng nhập tên mẫu lịch tập')
    return false
  }
  if (!formData.value.muscleGroupFocus) {
    toast.warning('Vui lòng chọn nhóm cơ')
    return false
  }
  if (formData.value.routineDetails.length === 0) {
    toast.warning('Vui lòng thêm ít nhất 1 bài tập')
    return false
  }
  
  // Validate each exercise detail
  for (let i = 0; i < formData.value.routineDetails.length; i++) {
    const detail = formData.value.routineDetails[i]
    if (!detail.exercise.id) {
      toast.warning(`Vui lòng chọn bài tập cho dòng ${i + 1}`)
      return false
    }
    if (!detail.defaultSetCount || detail.defaultSetCount < 1) {
      toast.warning(`Số set phải lớn hơn 0 cho dòng ${i + 1}`)
      return false
    }
    if (!detail.defaultRepCount || !detail.defaultRepCount.trim()) {
      toast.warning(`Vui lòng nhập số rep cho dòng ${i + 1}`)
      return false
    }
  }
  
  return true
}

const handleSubmit = async () => {
  if (!validateForm()) return

  isSubmitting.value = true
  try {
    if (isEditMode.value) {
      await workoutRoutineApi.updateRoutine(route.params.id, formData.value)
      toast.success('Cập nhật mẫu lịch tập thành công!')
    } else {
      await workoutRoutineApi.createRoutine(formData.value)
      toast.success('Tạo mẫu lịch tập thành công!')
    }
    const basePath = authStore.user?.role === 'PT' ? '/pt' : '/customer'
    router.push(`${basePath}/workout/routines`)
  } catch (error) {
    console.error('Lỗi khi lưu mẫu lịch tập:', error)
    toast.error(isEditMode.value ? 'Không thể cập nhật mẫu lịch tập' : 'Không thể tạo mẫu lịch tập')
  } finally {
    isSubmitting.value = false
  }
}

const handleCancel = () => {
  const basePath = authStore.user?.role === 'PT' ? '/pt' : '/customer'
  router.push(`${basePath}/workout/routines`)
}

const getExerciseName = (exerciseId) => {
  const exercise = exercises.value.find(ex => ex.id === exerciseId)
  return exercise ? exercise.name : 'Chọn bài tập'
}

// ==================== LIFECYCLE ====================
onMounted(async () => {
  await fetchExercises()
  await fetchRoutine()
  
  // Add one empty detail by default only in create mode
  if (!isEditMode.value) {
    addExerciseDetail()
  }
})
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-red-50 via-white to-orange-50 py-8">
    <div class="mx-auto max-w-5xl px-6">
      <!-- Header -->
      <div class="mb-8">
        <h1 class="text-4xl font-bold bg-gradient-to-r from-red-600 to-orange-600 bg-clip-text text-transparent">
          {{ pageTitle }}
        </h1>
        <p class="text-gray-600 mt-2">{{ pageSubtitle }}</p>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading" class="flex justify-center items-center h-64">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-red-600"></div>
      </div>

      <!-- Form -->
      <div v-else class="bg-white rounded-2xl shadow-lg p-8">
        <form @submit.prevent="handleSubmit" class="space-y-6">
          <!-- Basic Info Section -->
          <div class="space-y-6">
            <h2 class="text-xl font-bold text-gray-900 border-b pb-3">Thông Tin Cơ Bản</h2>

            <!-- Routine Name -->
            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-2">
                Tên Mẫu Lịch Tập <span class="text-red-500">*</span>
              </label>
              <input
                v-model="formData.name"
                type="text"
                placeholder="VD: Full Body Workout, Leg Day, Upper Body..."
                class="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-red-500 focus:border-red-500 focus:outline-none"
                required
              />
            </div>

            <!-- Muscle Group -->
            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-2">
                Nhóm Cơ Chính <span class="text-red-500">*</span>
              </label>
              <select
                v-model="formData.muscleGroupFocus"
                class="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-red-500 focus:border-red-500 focus:outline-none appearance-none bg-white"
                required
              >
                <option value="" disabled>Chọn nhóm cơ</option>
                <option v-for="group in muscleGroups" :key="group" :value="group">
                  {{ group }}
                </option>
              </select>
            </div>

            <!-- Description -->
            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-2">
                Mô Tả
              </label>
              <textarea
                v-model="formData.description"
                rows="3"
                placeholder="Mô tả về mẫu lịch tập này..."
                class="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-red-500 focus:border-red-500 focus:outline-none resize-none"
              ></textarea>
            </div>

            <!-- Public/Private Toggle -->
            <div class="flex items-center gap-3 p-4 bg-gray-50 rounded-xl">
              <input
                v-model="formData.isPublic"
                type="checkbox"
                id="isPublic"
                class="w-5 h-5 text-red-600 border-gray-300 rounded focus:ring-red-500"
              />
              <label for="isPublic" class="flex items-center gap-2 cursor-pointer">
                <component :is="formData.isPublic ? Globe : Lock" class="w-5 h-5" :class="formData.isPublic ? 'text-green-600' : 'text-gray-400'" />
                <span class="font-medium text-gray-900">
                  {{ formData.isPublic ? 'Công khai - Mọi người có thể xem' : 'Riêng tư - Chỉ mình tôi' }}
                </span>
              </label>
            </div>
          </div>

          <!-- Exercise Details Section -->
          <div class="space-y-6">
            <div class="flex items-center justify-between border-b pb-3">
              <h2 class="text-xl font-bold text-gray-900">Danh Sách Bài Tập</h2>
              <button
                type="button"
                @click="addExerciseDetail"
                class="flex items-center gap-2 bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700 transition-colors"
              >
                <Plus class="w-4 h-4" />
                Thêm Bài Tập
              </button>
            </div>

            <!-- Loading Exercises -->
            <div v-if="isLoadingExercises" class="text-center py-8">
              <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-red-600 mx-auto"></div>
              <p class="text-gray-600 mt-2">Đang tải danh sách bài tập...</p>
            </div>

            <!-- Exercise Details List -->
            <div v-else class="space-y-4">
              <div
                v-for="(detail, index) in formData.routineDetails"
                :key="index"
                class="p-4 border-2 border-gray-200 rounded-xl hover:border-red-300 transition-colors"
              >
                <div class="flex items-start gap-4">
                  <div class="flex-shrink-0 w-8 h-8 rounded-full bg-red-600 text-white flex items-center justify-center font-bold">
                    {{ index + 1 }}
                  </div>

                  <div class="flex-1 grid grid-cols-1 md:grid-cols-3 gap-4">
                    <!-- Exercise Selection -->
                    <div class="md:col-span-2">
                      <label class="block text-xs font-semibold text-gray-600 mb-1">
                        Bài Tập <span class="text-red-500">*</span>
                      </label>
                      <select
                        v-model="detail.exercise.id"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-red-500 focus:outline-none text-sm"
                        required
                      >
                        <option :value="null">Chọn bài tập</option>
                        <option v-for="exercise in exercises" :key="exercise.id" :value="exercise.id">
                          {{ exercise.name }} - {{ exercise.muscleGroup }}
                        </option>
                      </select>
                    </div>

                    <!-- Set Count -->
                    <div>
                      <label class="block text-xs font-semibold text-gray-600 mb-1">
                        Số Set <span class="text-red-500">*</span>
                      </label>
                      <input
                        v-model.number="detail.defaultSetCount"
                        type="number"
                        min="1"
                        max="10"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-red-500 focus:outline-none text-sm"
                        required
                      />
                    </div>

                    <!-- Rep Count -->
                    <div class="md:col-span-2">
                      <label class="block text-xs font-semibold text-gray-600 mb-1">
                        Số Rep <span class="text-red-500">*</span>
                      </label>
                      <input
                        v-model="detail.defaultRepCount"
                        type="text"
                        placeholder="VD: 10-12, 15, Max..."
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-red-500 focus:outline-none text-sm"
                        required
                      />
                    </div>
                  </div>

                  <!-- Remove Button -->
                  <button
                    type="button"
                    @click="removeExerciseDetail(index)"
                    class="flex-shrink-0 p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                    :disabled="formData.routineDetails.length === 1"
                  >
                    <Trash2 class="w-5 h-5" />
                  </button>
                </div>
              </div>

              <!-- Empty State -->
              <div v-if="formData.routineDetails.length === 0" class="text-center py-8 border-2 border-dashed border-gray-300 rounded-xl">
                <ListChecks class="w-12 h-12 text-gray-300 mx-auto mb-2" />
                <p class="text-gray-600">Chưa có bài tập nào</p>
                <button
                  type="button"
                  @click="addExerciseDetail"
                  class="mt-3 text-red-600 hover:text-red-700 font-medium"
                >
                  Thêm bài tập đầu tiên
                </button>
              </div>
            </div>
          </div>

          <!-- Preview Card -->
          <div class="bg-gradient-to-r from-red-50 to-orange-50 rounded-xl p-6 border border-red-200">
            <h3 class="text-sm font-semibold text-gray-700 mb-3">Xem Trước</h3>
            <div class="bg-white rounded-lg p-4">
              <div class="flex items-start justify-between mb-3">
                <div class="flex items-center gap-3">
                  <div class="w-10 h-10 rounded-full bg-gradient-to-br from-red-500 to-orange-500 flex items-center justify-center">
                    <ListChecks class="w-5 h-5 text-white" />
                  </div>
                  <div>
                    <h4 class="font-bold text-gray-900">{{ formData.name || 'Tên mẫu lịch tập' }}</h4>
                    <span class="text-sm text-red-600 font-medium">{{ formData.muscleGroupFocus || 'Nhóm cơ' }}</span>
                  </div>
                </div>
                <component :is="formData.isPublic ? Globe : Lock" class="w-5 h-5" :class="formData.isPublic ? 'text-green-600' : 'text-gray-400'" />
              </div>
              <p class="text-sm text-gray-600 mb-3">{{ formData.description || 'Mô tả...' }}</p>
              <div class="text-sm text-gray-500">
                <ListChecks class="w-4 h-4 inline mr-1" />
                {{ formData.routineDetails.length }} bài tập
              </div>
            </div>
          </div>

          <!-- Action Buttons -->
          <div class="flex gap-4 pt-4">
            <button
              type="button"
              @click="handleCancel"
              class="flex-1 flex items-center justify-center gap-2 px-6 py-3 border border-gray-300 rounded-xl font-semibold hover:bg-gray-50 transition-colors"
            >
              <X class="w-5 h-5" />
              Hủy
            </button>
            <button
              type="submit"
              :disabled="isSubmitting"
              class="flex-1 flex items-center justify-center gap-2 bg-gradient-to-r from-red-600 to-orange-600 text-white px-6 py-3 rounded-xl font-semibold hover:shadow-lg transition-all transform hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <Save class="w-5 h-5" />
              {{ submitButtonText }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
