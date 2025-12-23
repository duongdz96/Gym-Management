<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Plus, Search, ListChecks, Eye, Edit, Trash2, Globe, Lock, Users } from 'lucide-vue-next'
import { workoutRoutineApi, trainingPlanApi, memberApi } from '@/services/workoutApi'
import { useAuthStore } from '@/stores/useAuthStore'
import { useToast } from 'vue-toastification'

const authStore = useAuthStore()
const router = useRouter()
const toast = useToast()

// ==================== STATE ====================
const routines = ref([])
const isLoading = ref(false)
const searchQuery = ref('')
const filterType = ref('all') // all, mine, public
const showDeleteModal = ref(false)
const showAssignModal = ref(false)
const routineToDelete = ref(null)
const routineToAssign = ref(null)
const selectedMember = ref(null)
const members = ref([])
const isLoadingMembers = ref(false)
const assignDate = ref(new Date().toISOString().split('T')[0])

// ==================== COMPUTED ====================
const filteredRoutines = computed(() => {
  let result = routines.value

  // Filter by search query
  if (searchQuery.value) {
    result = result.filter(routine =>
      routine.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      routine.muscleGroupFocus?.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  }

  // Filter by type
  if (filterType.value === 'mine') {
    result = result.filter(r => r.creator?.id === authStore.user?.id)
  } else if (filterType.value === 'public') {
    // Hiện TẤT CẢ routine công khai (kể cả của mình)
    result = result.filter(r => r.isPublic === true)
  }

  return result
})

const myRoutines = computed(() => {
  return filteredRoutines.value.filter(r => r.creator?.id === authStore.user?.id)
})

const publicRoutines = computed(() => {
  // Trong tab "Tất cả", chỉ hiện public của người khác
  // Trong tab "Công khai", đã được filter ở trên rồi
  if (filterType.value === 'public') {
    return filteredRoutines.value
  }
  return filteredRoutines.value.filter(r => r.isPublic && r.creator?.id !== authStore.user?.id)
})

// ==================== METHODS ====================
const fetchRoutines = async () => {
  isLoading.value = true
  try {
    const userId = authStore.user?.id
    const response = userId
      ? await workoutRoutineApi.getVisibleRoutines(userId)
      : await workoutRoutineApi.getAllRoutines()
    
    routines.value = response.data
    console.log('Loaded routines:', routines.value)
    console.log('Current user ID:', userId)
    console.log('Public routines:', routines.value.filter(r => r.isPublic))
  } catch (error) {
    console.error('Lỗi khi tải danh sách mẫu lịch tập:', error)
    toast.error('Không thể tải danh sách mẫu lịch tập')
  } finally {
    isLoading.value = false
  }
}

const fetchMembers = async () => {
  isLoadingMembers.value = true
  try {
    const response = await memberApi.getAllMembers()
    members.value = response.data
  } catch (error) {
    console.error('Lỗi khi tải danh sách members:', error)
  } finally {
    isLoadingMembers.value = false
  }
}

const goToCreate = () => {
  const basePath = authStore.user?.role === 'PT' ? '/pt' : '/customer'
  router.push(`${basePath}/workout/routine/create`)
}

const viewRoutine = (routine) => {
  const basePath = authStore.user?.role === 'PT' ? '/pt' : '/customer'
  router.push(`${basePath}/workout/routine/${routine.id}`)
}

const editRoutine = (routine) => {
  const basePath = authStore.user?.role === 'PT' ? '/pt' : '/customer'
  router.push(`${basePath}/workout/routine/edit/${routine.id}`)
}

const selectForWorkout = async (routine) => {
  // Customer chọn để tập cho bản thân
  try {
    const data = {
      routineId: routine.id,
      memberId: authStore.user?.id,
      date: new Date().toISOString().split('T')[0]
    }
    await trainingPlanApi.generatePlanFromRoutine(data)
    toast.success('Đã tạo lịch tập thành công!')
    const basePath = authStore.user?.role === 'PT' ? '/pt' : '/customer'
    router.push(`${basePath}/workout/training-plans`)
  } catch (error) {
    console.error('Lỗi khi tạo lịch tập:', error)
    toast.error('Không thể tạo lịch tập')
  }
}

const openAssignModal = (routine) => {
  routineToAssign.value = routine
  showAssignModal.value = true
  if (members.value.length === 0) {
    fetchMembers()
  }
}

const assignToMember = async () => {
  if (!selectedMember.value) {
    toast.warning('Vui lòng chọn học viên')
    return
  }

  try {
    const data = {
      routineId: routineToAssign.value.id,
      memberId: selectedMember.value.id,
      date: assignDate.value,
      assignedBy: authStore.user?.id
    }
    await trainingPlanApi.assignPlanToMember(data)
    toast.success(`Đã gán lịch tập cho ${selectedMember.value.fullName}!`)
    closeAssignModal()
  } catch (error) {
    console.error('Lỗi khi gán lịch tập:', error)
    toast.error('Không thể gán lịch tập')
  }
}

const closeAssignModal = () => {
  showAssignModal.value = false
  routineToAssign.value = null
  selectedMember.value = null
  assignDate.value = new Date().toISOString().split('T')[0]
}

const confirmDelete = (routine) => {
  routineToDelete.value = routine
  showDeleteModal.value = true
}

const deleteRoutine = async () => {
  if (!routineToDelete.value) return

  try {
    await workoutRoutineApi.deleteRoutine(routineToDelete.value.id)
    routines.value = routines.value.filter(r => r.id !== routineToDelete.value.id)
    showDeleteModal.value = false
    routineToDelete.value = null
  } catch (error) {
    console.error('Lỗi khi xóa mẫu lịch tập:', error)
    toast.error('Không thể xóa mẫu lịch tập này')
  }
}

const cancelDelete = () => {
  showDeleteModal.value = false
  routineToDelete.value = null
}

const isMyRoutine = (routine) => {
  return routine.creator?.id === authStore.user?.id
}

// ==================== LIFECYCLE ====================
onMounted(() => {
  fetchRoutines()
})
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-emerald-50 via-white to-orange-50 py-8">
    <div class="mx-auto max-w-7xl px-6">
      <!-- Header -->
      <div class="mb-8 flex items-center justify-between">
        <div>
          <h1 class="text-4xl font-bold bg-gradient-to-r from-emerald-600 to-emerald-700 bg-clip-text text-transparent">
            Cộng Đồng Workout
          </h1>
          <p class="text-gray-600 mt-2">Khám phá và chia sẻ các mẫu lịch tập</p>
        </div>
        <button
          @click="goToCreate"
          class="flex items-center gap-2 bg-gradient-to-r from-emerald-600 to-emerald-700 text-white px-6 py-3 rounded-xl font-semibold hover:shadow-lg transition-all transform hover:scale-105"
        >
          <Plus class="w-5 h-5" />
          Tạo Mẫu Mới
        </button>
      </div>

      <!-- Search & Filter -->
      <div class="bg-white rounded-2xl shadow-lg p-6 mb-6">
        <div class="flex flex-col md:flex-row gap-4">
          <!-- Search -->
          <div class="relative flex-1">
            <Search class="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
            <input
              v-model="searchQuery"
              type="text"
              placeholder="Tìm kiếm mẫu lịch tập..."
              class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500 focus:outline-none"
            />
          </div>

          <!-- Filter Buttons -->
          <div class="flex gap-2">
            <button
              @click="filterType = 'all'"
              :class="filterType === 'all' ? 'bg-emerald-600 text-white' : 'bg-white text-gray-700 border border-gray-300'"
              class="px-4 py-2 rounded-lg font-semibold transition-colors"
            >
              Tất cả
            </button>
            <button
              @click="filterType = 'mine'"
              :class="filterType === 'mine' ? 'bg-emerald-600 text-white' : 'bg-white text-gray-700 border border-gray-300'"
              class="px-4 py-2 rounded-lg font-semibold transition-colors"
            >
              Của tôi
            </button>
            <button
              @click="filterType = 'public'"
              :class="filterType === 'public' ? 'bg-emerald-600 text-white' : 'bg-white text-gray-700 border border-gray-300'"
              class="px-4 py-2 rounded-lg font-semibold transition-colors"
            >
              Công khai
            </button>
          </div>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading" class="flex justify-center items-center h-64">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-emerald-600"></div>
      </div>

      <div v-else class="space-y-8">
        <!-- My Routines Section -->
        <div v-if="myRoutines.length > 0 && (filterType === 'all' || filterType === 'mine')">
          <h2 class="text-2xl font-bold text-gray-900 mb-4 flex items-center gap-2">
            <ListChecks class="w-6 h-6 text-emerald-600" />
            Mẫu Của Tôi ({{ myRoutines.length }})
          </h2>
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <div
              v-for="routine in myRoutines"
              :key="routine.id"
              class="bg-white rounded-2xl shadow-lg p-6 hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1 border-2 border-emerald-100"
            >
              <!-- Routine Header -->
              <div class="flex items-start justify-between mb-4">
                <div class="flex items-center gap-3 flex-1">
                  <div class="w-12 h-12 rounded-full bg-gradient-to-br from-emerald-500 to-emerald-600 flex items-center justify-center flex-shrink-0">
                    <ListChecks class="w-6 h-6 text-white" />
                  </div>
                  <div class="flex-1 min-w-0">
                    <h3 class="text-lg font-bold text-gray-900 truncate">{{ routine.name }}</h3>
                    <span class="text-sm text-emerald-600 font-medium">{{ routine.muscleGroupFocus }}</span>
                  </div>
                </div>
                <component
                  :is="routine.isPublic ? Globe : Lock"
                  :class="routine.isPublic ? 'text-green-600' : 'text-gray-400'"
                  class="w-5 h-5 flex-shrink-0 ml-2"
                  :title="routine.isPublic ? 'Công khai' : 'Riêng tư'"
                />
              </div>

              <!-- Description -->
              <p class="text-gray-600 text-sm mb-4 line-clamp-2">
                {{ routine.description || 'Chưa có mô tả' }}
              </p>

              <!-- Exercise Count -->
              <div class="flex items-center gap-2 text-sm text-gray-500 mb-4 pb-4 border-b border-gray-200">
                <ListChecks class="w-4 h-4" />
                <span>{{ routine.routineDetails?.length || 0 }} bài tập</span>
              </div>

              <!-- Actions -->
              <div class="grid grid-cols-2 gap-2">
                <button
                  @click="viewRoutine(routine)"
                  class="flex items-center justify-center gap-2 bg-emerald-50 text-emerald-600 px-3 py-2 rounded-lg hover:bg-emerald-100 transition-colors text-sm"
                >
                  <Eye class="w-4 h-4" />
                  Xem
                </button>
                <button
                  @click="editRoutine(routine)"
                  class="flex items-center justify-center gap-2 bg-green-50 text-green-600 px-3 py-2 rounded-lg hover:bg-green-100 transition-colors text-sm"
                >
                  <Edit class="w-4 h-4" />
                  Sửa
                </button>
                <button
                  v-if="authStore.user?.role === 'MEMBER'"
                  @click="selectForWorkout(routine)"
                  class="flex items-center justify-center gap-2 bg-red-50 text-red-600 px-3 py-2 rounded-lg hover:bg-red-100 transition-colors text-sm"
                >
                  Chọn tập
                </button>
                <button
                  @click="openAssignModal(routine)"
                  class="flex items-center justify-center gap-2 bg-purple-50 text-purple-600 px-3 py-2 rounded-lg hover:bg-purple-100 transition-colors text-sm"
                >
                  <Users class="w-4 h-4" />
                  Gán
                </button>
                <button
                  @click="confirmDelete(routine)"
                  class="col-span-2 flex items-center justify-center gap-2 bg-red-50 text-red-600 px-3 py-2 rounded-lg hover:bg-red-100 transition-colors text-sm"
                >
                  <Trash2 class="w-4 h-4" />
                  Xóa
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Public Routines Section -->
        <div v-if="publicRoutines.length > 0 && (filterType === 'all' || filterType === 'public')">
          <h2 class="text-2xl font-bold text-gray-900 mb-4 flex items-center gap-2">
            <Globe class="w-6 h-6 text-green-600" />
            Mẫu Công Khai ({{ publicRoutines.length }})
          </h2>
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <div
              v-for="routine in publicRoutines"
              :key="routine.id"
              class="bg-white rounded-2xl shadow-lg p-6 hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1"
            >
              <!-- Routine Header -->
              <div class="flex items-start justify-between mb-4">
                <div class="flex items-center gap-3 flex-1">
                  <div class="w-12 h-12 rounded-full bg-gradient-to-br from-green-500 to-emerald-500 flex items-center justify-center flex-shrink-0">
                    <ListChecks class="w-6 h-6 text-white" />
                  </div>
                  <div class="flex-1 min-w-0">
                    <h3 class="text-lg font-bold text-gray-900 truncate">{{ routine.name }}</h3>
                    <span class="text-sm text-green-600 font-medium">{{ routine.muscleGroupFocus }}</span>
                  </div>
                </div>
              </div>

              <!-- Description -->
              <p class="text-gray-600 text-sm mb-4 line-clamp-2">
                {{ routine.description || 'Chưa có mô tả' }}
              </p>

              <!-- Exercise Count & Creator -->
              <div class="space-y-2 mb-4 pb-4 border-b border-gray-200">
                <div class="flex items-center gap-2 text-sm text-gray-500">
                  <ListChecks class="w-4 h-4" />
                  <span>{{ routine.routineDetails?.length || 0 }} bài tập</span>
                </div>
                <div class="text-xs text-gray-500">
                  Tạo bởi: {{ routine.creator?.fullName || 'Hệ thống' }}
                </div>
              </div>

              <!-- Actions -->
              <div class="grid grid-cols-2 gap-2">
                <button
                  @click="viewRoutine(routine)"
                  class="flex items-center justify-center gap-2 bg-green-50 text-green-600 px-3 py-2 rounded-lg hover:bg-green-100 transition-colors text-sm"
                >
                  <Eye class="w-4 h-4" />
                  Xem
                </button>
                <button
                  v-if="authStore.user?.role === 'MEMBER'"
                  @click="selectForWorkout(routine)"
                  class="flex items-center justify-center gap-2 bg-red-50 text-red-600 px-3 py-2 rounded-lg hover:bg-red-100 transition-colors text-sm"
                >
                  Chọn tập
                </button>
                <button
                  @click="openAssignModal(routine)"
                  class="col-span-2 flex items-center justify-center gap-2 bg-purple-50 text-purple-600 px-3 py-2 rounded-lg hover:bg-purple-100 transition-colors text-sm"
                >
                  <Users class="w-4 h-4" />
                  Gán cho người khác
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-if="filteredRoutines.length === 0" class="text-center py-16">
          <ListChecks class="w-16 h-16 text-gray-300 mx-auto mb-4" />
          <h3 class="text-xl font-semibold text-gray-600 mb-2">Không tìm thấy mẫu lịch tập nào</h3>
          <p class="text-gray-500 mb-6">Thử tìm kiếm với từ khóa khác hoặc tạo mẫu mới</p>
          <button
            @click="goToCreate"
            class="inline-flex items-center gap-2 bg-gradient-to-r from-emerald-600 to-emerald-700 text-white px-6 py-3 rounded-xl font-semibold hover:shadow-lg transition-all"
          >
            <Plus class="w-5 h-5" />
            Tạo Mẫu Mới
          </button>
        </div>
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
        <p class="text-gray-600 mb-6">
          Bạn có chắc chắn muốn xóa mẫu lịch tập <strong>{{ routineToDelete?.name }}</strong>?
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
            @click="deleteRoutine"
            class="flex-1 px-6 py-3 bg-red-600 text-white rounded-xl font-semibold hover:bg-red-700 transition-colors"
          >
            Xóa
          </button>
        </div>
      </div>
    </div>

    <!-- Assign Modal -->
    <div
      v-if="showAssignModal"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
      @click="closeAssignModal"
    >
      <div
        class="bg-white rounded-2xl p-8 max-w-md w-full mx-4 shadow-2xl"
        @click.stop
      >
        <h3 class="text-2xl font-bold text-gray-900 mb-4">Gán lịch tập</h3>
        <p class="text-gray-600 mb-6">
          Gán mẫu <strong>{{ routineToAssign?.name }}</strong> cho học viên
        </p>

        <!-- Date Selection -->
        <div class="mb-4">
          <label class="block text-sm font-semibold text-gray-700 mb-2">
            Ngày tập
          </label>
          <input
            v-model="assignDate"
            type="date"
            class="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-red-500 focus:border-red-500 focus:outline-none"
          />
        </div>

        <!-- Member Selection -->
        <div class="mb-6">
          <label class="block text-sm font-semibold text-gray-700 mb-2">
            Chọn học viên
          </label>
          <div v-if="isLoadingMembers" class="text-center py-4">
            <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-red-600 mx-auto"></div>
          </div>
          <select
            v-else
            v-model="selectedMember"
            class="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-red-500 focus:border-red-500 focus:outline-none"
          >
            <option :value="null">Chọn học viên</option>
            <option v-for="member in members" :key="member.id" :value="member">
              {{ member.fullName }} - {{ member.email }}
            </option>
          </select>
        </div>

        <div class="flex gap-4">
          <button
            @click="closeAssignModal"
            class="flex-1 px-6 py-3 border border-gray-300 rounded-xl font-semibold hover:bg-gray-50 transition-colors"
          >
            Hủy
          </button>
          <button
            @click="assignToMember"
            class="flex-1 px-6 py-3 bg-purple-600 text-white rounded-xl font-semibold hover:bg-purple-700 transition-colors"
          >
            Gán
          </button>
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
