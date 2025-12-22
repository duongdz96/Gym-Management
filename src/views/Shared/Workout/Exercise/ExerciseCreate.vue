<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Save, X, Dumbbell } from 'lucide-vue-next'
import { exerciseApi } from '@/services/workoutApi'
import { useAuthStore } from '@/stores/useAuthStore'
import { useToast } from 'vue-toastification'

const authStore = useAuthStore()
const router = useRouter()
const route = useRoute()
const toast = useToast()

// ==================== STATE ====================
const formData = ref({
  name: '',
  muscleGroup: '',
  description: '',
  creator: {
    id: authStore.user?.id
  }
})

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
const pageTitle = computed(() => isEditMode.value ? 'Sửa Bài Tập' : 'Tạo Bài Tập Mới')
const pageSubtitle = computed(() => isEditMode.value ? 'Cập nhật thông tin bài tập' : 'Thêm bài tập mới vào thư viện')
const submitButtonText = computed(() => isSubmitting.value ? 'Đang lưu...' : (isEditMode.value ? 'Cập Nhật' : 'Lưu Bài Tập'))

// ==================== METHODS ====================
const fetchExercise = async () => {
  if (!isEditMode.value) return
  
  isLoading.value = true
  try {
    const response = await exerciseApi.getExerciseById(route.params.id)
    const exercise = response.data
    formData.value = {
      name: exercise.name,
      muscleGroup: exercise.muscleGroup,
      description: exercise.description,
      creator: exercise.creator
    }
  } catch (error) {
    console.error('Lỗi khi tải bài tập:', error)
    toast.error('Không thể tải thông tin bài tập')
    handleCancel()
  } finally {
    isLoading.value = false
  }
}

const validateForm = () => {
  if (!formData.value.name.trim()) {
    toast.warning('Vui lòng nhập tên bài tập')
    return false
  }
  if (!formData.value.muscleGroup) {
    toast.warning('Vui lòng chọn nhóm cơ')
    return false
  }
  if (!formData.value.description.trim()) {
    toast.warning('Vui lòng nhập mô tả bài tập')
    return false
  }
  return true
}

const handleSubmit = async () => {
  if (!validateForm()) return

  isSubmitting.value = true
  try {
    if (isEditMode.value) {
      await exerciseApi.updateExercise(route.params.id, formData.value)
      toast.success('Cập nhật bài tập thành công!')
    } else {
      await exerciseApi.createExercise(formData.value)
      toast.success('Tạo bài tập thành công!')
    }
    const basePath = authStore.user?.role === 'PT' ? '/pt' : '/customer'
    router.push(`${basePath}/workout/exercises`)
  } catch (error) {
    console.error('Lỗi khi lưu bài tập:', error)
    toast.error(isEditMode.value ? 'Không thể cập nhật bài tập' : 'Không thể tạo bài tập')
  } finally {
    isSubmitting.value = false
  }
}

const handleCancel = () => {
  const basePath = authStore.user?.role === 'PT' ? '/pt' : '/customer'
  router.push(`${basePath}/workout/exercises`)
}

// ==================== LIFECYCLE ====================
onMounted(() => {
  fetchExercise()
})
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-red-50 via-white to-orange-50 py-8">
    <div class="mx-auto max-w-3xl px-6">
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
          <!-- Exercise Name -->
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">
              Tên Bài Tập <span class="text-red-500">*</span>
            </label>
            <div class="relative">
              <Dumbbell class="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                v-model="formData.name"
                type="text"
                placeholder="VD: Bench Press, Squat, Deadlift..."
                class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-red-500 focus:border-red-500 focus:outline-none"
                required
              />
            </div>
          </div>

          <!-- Muscle Group -->
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">
              Nhóm Cơ <span class="text-red-500">*</span>
            </label>
            <select
              v-model="formData.muscleGroup"
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
              Mô Tả / Hướng Dẫn <span class="text-red-500">*</span>
            </label>
            <textarea
              v-model="formData.description"
              rows="6"
              placeholder="Mô tả cách thực hiện bài tập, kỹ thuật, lưu ý..."
              class="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-red-500 focus:border-red-500 focus:outline-none resize-none"
              required
            ></textarea>
            <p class="text-sm text-gray-500 mt-2">
              Hãy mô tả chi tiết cách thực hiện bài tập để dễ dàng nhớ và thực hiện
            </p>
          </div>

          <!-- Preview Card -->
          <div class="bg-gradient-to-r from-red-50 to-orange-50 rounded-xl p-6 border border-red-200">
            <h3 class="text-sm font-semibold text-gray-700 mb-3">Xem Trước</h3>
            <div class="bg-white rounded-lg p-4">
              <div class="flex items-center gap-3 mb-3">
                <div class="w-10 h-10 rounded-full bg-gradient-to-br from-red-500 to-orange-500 flex items-center justify-center">
                  <Dumbbell class="w-5 h-5 text-white" />
                </div>
                <div>
                  <h4 class="font-bold text-gray-900">{{ formData.name || 'Tên bài tập' }}</h4>
                  <span class="text-sm text-red-600 font-medium">{{ formData.muscleGroup || 'Nhóm cơ' }}</span>
                </div>
              </div>
              <p class="text-sm text-gray-600">
                {{ formData.description || 'Mô tả bài tập sẽ hiển thị ở đây...' }}
              </p>
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
