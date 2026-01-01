<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Plus, Search, Dumbbell, Eye, Edit, Trash2, Filter } from 'lucide-vue-next'
import { exerciseApi } from '@/services/workoutApi'
import { useAuthStore } from '@/stores/useAuthStore'
import { useToast } from 'vue-toastification'

const authStore = useAuthStore()
const router = useRouter()

// ==================== STATE ====================
const exercises = ref([])
const isLoading = ref(false)
const searchQuery = ref('')

const showDeleteModal = ref(false)
const exerciseToDelete = ref(null)
const toast = useToast();

const muscleGroups = [
  'Tất cả',
  'Ngực',
  'Lưng',
  'Vai',
  'Tay trước',
  'Tay sau',
  'Chân',
  'Bụng',
  'Toàn thân'
]
const selectedMuscleGroup = ref(muscleGroups[0]);
// ==================== COMPUTED ====================
const filteredExercises = computed(() => {
  let result = exercises.value

  // Filter by search query
  if (searchQuery.value) {
    result = result.filter(ex =>
      ex.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      ex.description?.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  }

  // Filter by muscle group
  if (selectedMuscleGroup.value && selectedMuscleGroup.value !== 'Tất cả') {
    result = result.filter(ex => ex.muscleGroup === selectedMuscleGroup.value)
  }

  return result
})

// ==================== METHODS ====================
const fetchExercises = async () => {
  isLoading.value = true
  try {
    const userId = authStore.user?.id
    const response = userId 
      ? await exerciseApi.getMyExercises(userId)
      : await exerciseApi.getAllExercises()
    
    exercises.value = response.data
  } catch (error) {
    toast.error('Không thể tải danh sách bài tập')
  } finally {
    isLoading.value = false
  }
}

const goToCreate = () => {
  const basePath = authStore.user?.role === 'PT' ? '/pt' : '/customer'
  router.push(`${basePath}/workout/exercise/create`)
}

const viewExercise = (exercise) => {
  const basePath = authStore.user?.role === 'PT' ? '/pt' : '/customer'
  router.push(`${basePath}/workout/exercise/${exercise.id}`)
}

const editExercise = (exercise) => {
  const basePath = authStore.user?.role === 'PT' ? '/pt' : '/customer'
  router.push(`${basePath}/workout/exercise/edit/${exercise.id}`)
}

const confirmDelete = (exercise) => {
  exerciseToDelete.value = exercise
  showDeleteModal.value = true
}

const deleteExercise = async () => {
  if (!exerciseToDelete.value) return

  try {
    await exerciseApi.deleteExercise(exerciseToDelete.value.id)
    exercises.value = exercises.value.filter(ex => ex.id !== exerciseToDelete.value.id)
    showDeleteModal.value = false
    exerciseToDelete.value = null
  } catch (error) {
    console.error('Lỗi khi xóa bài tập:', error)
    toast.error('Không thể xóa bài tập này')
  }
}

const cancelDelete = () => {
  showDeleteModal.value = false
  exerciseToDelete.value = null
}

const isMyExercise = (exercise) => {
  return exercise.creator?.id === authStore.user?.id
}

// ==================== LIFECYCLE ====================
onMounted(() => {
  fetchExercises()
})
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-emerald-50 via-white to-orange-50 py-4 sm:py-8">
    <div class="mx-auto max-w-7xl px-4 sm:px-6">
      <!-- Header -->
      <div class="mb-6 sm:mb-8 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h1 class="text-3xl sm:text-4xl font-bold bg-gradient-to-r from-emerald-600 to-emerald-700 bg-clip-text text-transparent">
            Danh Sách Bài Tập
          </h1>
          <p class="text-gray-600 text-sm sm:text-base mt-2">Quản lý và tìm kiếm các bài tập</p>
        </div>
        <button
          @click="goToCreate"
          class="w-full sm:w-auto flex items-center justify-center gap-2 bg-gradient-to-r from-emerald-600 to-emerald-700 text-white px-4 sm:px-6 py-2.5 sm:py-3 text-sm sm:text-base rounded-xl font-semibold hover:shadow-lg transition-all transform hover:scale-105"
        >
          <Plus class="w-5 h-5" />
          Tạo Bài Tập Mới
        </button>
      </div>

      <!-- Search & Filter -->
      <div class="bg-white rounded-xl sm:rounded-2xl shadow-lg p-4 sm:p-6 mb-4 sm:mb-6">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-3 sm:gap-4">
          <!-- Search -->
          <div class="relative">
            <Search class="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 sm:w-5 sm:h-5 text-gray-400" />
            <input
              v-model="searchQuery"
              type="text"
              placeholder="Tìm kiếm bài tập..."
              class="w-full pl-9 sm:pl-10 pr-4 py-2.5 sm:py-3 text-sm sm:text-base border border-gray-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500 focus:outline-none"
            />
          </div>

          <!-- Muscle Group Filter -->
          <div class="relative">
            <Filter class="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 sm:w-5 sm:h-5 text-gray-400" />
            <select
              v-model="selectedMuscleGroup"
              class="w-full pl-9 sm:pl-10 pr-4 py-2.5 sm:py-3 text-sm sm:text-base border border-gray-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500 focus:outline-none appearance-none bg-white"
            >
              <option v-for="group in muscleGroups" :key="group" :value="group">
                {{ group }}
              </option>
            </select>
          </div>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading" class="flex justify-center items-center h-64">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-emerald-600"></div>
      </div>

      <!-- Exercise Grid -->
      <div v-else-if="filteredExercises.length > 0" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 sm:gap-6">
        <div
          v-for="exercise in filteredExercises"
          :key="exercise.id"
          class="bg-white rounded-xl sm:rounded-2xl shadow-lg p-4 sm:p-6 hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1"
        >
          <!-- Exercise Header -->
          <div class="flex items-start justify-between mb-3 sm:mb-4">
            <div class="flex items-center gap-2 sm:gap-3 min-w-0 flex-1">
              <div class="w-10 h-10 sm:w-12 sm:h-12 rounded-full bg-gradient-to-br from-emerald-500 to-emerald-600 flex items-center justify-center flex-shrink-0">
                <Dumbbell class="w-5 h-5 sm:w-6 sm:h-6 text-white" />
              </div>
              <div class="min-w-0 flex-1">
                <h3 class="text-base sm:text-lg font-bold text-gray-900 truncate">{{ exercise.name }}</h3>
                <span class="text-xs sm:text-sm text-emerald-600 font-medium truncate block">{{ exercise.muscleGroup }}</span>
              </div>
            </div>
          </div>

          <!-- Description -->
          <p class="text-gray-600 text-sm mb-4 line-clamp-3 truncate block">
            {{ exercise.description || 'Chưa có mô tả' }}
          </p>

          <!-- Creator Info -->
          <div class="text-xs text-gray-500 mb-4 pb-4 border-b border-gray-200">
            <span v-if="isMyExercise(exercise)" class="text-emerald-600 font-semibold">
              Của tôi
            </span>
            <span v-else>
              {{ exercise.creator ? `Tạo bởi: ${exercise.creator.fullName}` : 'Hệ thống' }}
            </span>
          </div>

          <!-- Actions -->
          <div class="flex gap-2">
            <button
              @click="viewExercise(exercise)"
              class="flex-1 flex items-center justify-center gap-1 sm:gap-2 bg-emerald-50 text-emerald-600 px-3 sm:px-4 py-2 text-xs sm:text-sm rounded-lg hover:bg-emerald-100 transition-colors"
            >
              <Eye class="w-3 h-3 sm:w-4 sm:h-4" />
              <span class="hidden sm:inline">Xem</span>
            </button>
            <template v-if="isMyExercise(exercise)">
              <button
                @click="editExercise(exercise)"
                class="flex-1 flex items-center justify-center gap-1 sm:gap-2 bg-green-50 text-green-600 px-3 sm:px-4 py-2 text-xs sm:text-sm rounded-lg hover:bg-green-100 transition-colors"
              >
                <Edit class="w-3 h-3 sm:w-4 sm:h-4" />
                <span class="hidden sm:inline">Sửa</span>
              </button>
              <button
                @click="confirmDelete(exercise)"
                class="flex items-center justify-center gap-2 bg-red-50 text-red-600 px-2 sm:px-3 py-2 rounded-lg hover:bg-red-100 transition-colors"
              >
                <Trash2 class="w-3 h-3 sm:w-4 sm:h-4" />
              </button>
            </template>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else class="text-center py-16">
        <Dumbbell class="w-16 h-16 text-gray-300 mx-auto mb-4" />
        <h3 class="text-xl font-semibold text-gray-600 mb-2">Không tìm thấy bài tập nào</h3>
        <p class="text-gray-500 mb-6">Thử tìm kiếm với từ khóa khác hoặc tạo bài tập mới</p>
        <button
          @click="goToCreate"
          class="inline-flex items-center gap-2 bg-gradient-to-r from-emerald-600 to-emerald-700 text-white px-6 py-3 rounded-xl font-semibold hover:shadow-lg transition-all"
        >
          <Plus class="w-5 h-5" />
          Tạo Bài Tập Mới
        </button>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div
      v-if="showDeleteModal"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
      @click="cancelDelete"
    >
      <div
        class="bg-white rounded-2xl p-8 max-w-md w-full mx-4 shadow-2xl"
        @click.stop
      >
        <h3 class="text-2xl font-bold text-gray-900 mb-4">Xác nhận xóa</h3>
        <p class="text-gray-600 mb-6 truncate block">
          Bạn có chắc chắn muốn xóa bài tập <strong>{{ exerciseToDelete?.name }}</strong>?
          Hành động này không thể hoàn tác.
        </p>
        <div class="flex gap-4">
          <button
            @click="cancelDelete"
            class="flex-1 px-6 py-3 border border-gray-300 rounded-xl font-semibold hover:bg-gray-50 transition-colors"
          >
            Hủy
          </button>
          <button
            @click="deleteExercise"
            class="flex-1 px-6 py-3 bg-red-600 text-white rounded-xl font-semibold hover:bg-red-700 transition-colors"
          >
            Xóa
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.line-clamp-3 {
  display: -webkit-box;
  line-clamp: 2;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
