<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import unifiedApi from '@/services/unifiedClassApi.js'
import { formatDate } from '@/views/Test/dateUtils.js'
import api from '@/services/api'
import {
  Dumbbell,
  Search,
  FileText,
  Clock,
  CheckCircle,
  XCircle,
  BarChart3,
  Users,
  MapPin,
  GraduationCap,
  Eye,
  Filter
} from 'lucide-vue-next'

const router = useRouter()

// State
const classes = ref([])
const roomsData = ref([])
const teachersData = ref([])
const studentsData = ref([])
const registrationsData = ref([])
const filterStatus = ref('')
const searchQuery = ref('')
const showDetailsModal = ref(false)
const selectedClass = ref(null)
const loadingSessionsDetails = ref(false)
const selectedClassSessions = ref([])

// Computed
const filteredClasses = computed(() => {
  let result = classes.value

  if (filterStatus.value) {
    result = result.filter(c => c.status === filterStatus.value)
  }

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(c =>
      c.name.toLowerCase().includes(query) ||
      c.description.toLowerCase().includes(query)
    )
  }

  return result
})

const selectedClassStudents = computed(() => {
  if (!selectedClass.value) return []
  // Get all student registrations and filter by class
  const registrations = registrationsData.value.filter(r => r.classId === selectedClass.value.id && r.status === 'active')
  return registrations.map(r => {
    const student = studentsData.value.find(s => s.id === r.studentId)
    return {
      ...student,
      registeredAt: r.registeredAt
    }
  })
})

// Methods
const loadData = async () => {
  classes.value = await unifiedApi.getClasses()
  roomsData.value = await unifiedApi.getRooms()
  teachersData.value = await unifiedApi.getTeachers()
  studentsData.value = await unifiedApi.getStudents()

  // Load all registrations (for student list in details)
  // In real app, we'd load per class, but for simplicity load all
  registrationsData.value = []
  for (const student of studentsData.value) {
    const studentRegs = await unifiedApi.getStudentRegistrations(student.id)
    registrationsData.value.push(...studentRegs)
  }
}

const getStatusText = (status) => {
  const statusMap = {
    draft: 'Nháp',
    pending_teacher: 'Chờ giáo viên',
    waiting_approval: 'Chờ duyệt',
    ready_for_students: 'Sẵn sàng',
    cancelled: 'Đã hủy'
  }
  return statusMap[status] || status
}

const getStatusIcon = (status) => {
  const iconMap = {
    draft: FileText,
    pending_teacher: Clock,
    waiting_approval: Clock,
    ready_for_students: CheckCircle,
    cancelled: XCircle
  }
  return iconMap[status] || FileText
}

const getRoomName = (roomId) => {
  const room = roomsData.value.find(r => r.id === roomId)
  return room ? room.name : 'Chưa chọn'
}

const getTeacherName = (teacherId) => {
  if (!teacherId) return 'Chưa có'
  const teacher = teachersData.value.find(t => t.id === teacherId)
  return teacher ? teacher.name : 'Không xác định'
}

const getEnrolledCount = (classId) => {
  return registrationsData.value.filter(r => r.classId === classId && r.status === 'active').length
}

const getScheduleText = (cls) => {
  if (!cls) return ''
  const daysOfWeek = ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7']
  if (cls.patternType === 'weekly') {
    const days = cls.daysOfWeek?.map(d => daysOfWeek[d]).join(', ') || ''
    return `Hàng tuần: ${days}`
  }
  if (cls.patternType === 'monthly') return 'Hàng tháng'
  if (cls.patternType === 'no_repeat') {
    return 'Tùy chọn'
  }
  return 'Tùy chỉnh'
}

const viewDetails = async (cls) => {
  selectedClass.value = cls
  showDetailsModal.value = true
  loadingSessionsDetails.value = true
  selectedClassSessions.value = []

  try {
    const sessions = await unifiedApi.getSessions(cls.id)
    selectedClassSessions.value = sessions.sort((a, b) => new Date(a.date) - new Date(b.date))
  } catch (error) {
    console.error('Error loading sessions:', error)
  } finally {
    loadingSessionsDetails.value = false
  }
}

const formatScheduleTime = (dateTime) => {
  if (!dateTime) return ''
  try {
    const date = new Date(dateTime)
    if (isNaN(date.getTime())) return ''
    return date.toTimeString().substring(0, 5)
  } catch (e) {
    if (typeof dateTime === 'string' && dateTime.match(/^\d{2}:\d{2}$/)) {
      return dateTime
    }
    return ''
  }
}

// Lifecycle
onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="p-6">
    <!-- Header -->
    <div class="flex justify-between items-center mb-8">
      <div class="flex items-center gap-3">
        <Dumbbell class="w-10 h-10 text-emerald-600" />
        <h1 class="text-3xl font-bold bg-gradient-to-r from-emerald-600 to-emerald-700 bg-clip-text text-transparent">
          Danh Sách Lớp Học
        </h1>
      </div>
    </div>

    <!-- Filters -->
    <div class="flex gap-4 mb-6">
      <div class="relative">
        <Filter class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
        <select
          v-model="filterStatus"
          class="pl-10 pr-4 py-3 border-2 border-gray-200 rounded-xl focus:border-emerald-600 focus:ring-2 focus:ring-emerald-100 outline-none appearance-none bg-white"
        >
          <option value="">Tất cả trạng thái</option>
          <option value="draft">Nháp</option>
          <option value="pending_teacher">Chờ giáo viên</option>
          <option value="waiting_approval">Chờ duyệt</option>
          <option value="ready_for_students">Sẵn sàng</option>
        </select>
      </div>

      <div class="relative flex-1">
        <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
        <input
          v-model="searchQuery"
          type="text"
          placeholder="Tìm kiếm lớp học..."
          class="w-full pl-10 pr-4 py-3 border-2 border-gray-200 rounded-xl focus:border-emerald-600 focus:ring-2 focus:ring-emerald-100 outline-none transition-all"
        />
      </div>
    </div>

    <!-- Class Grid -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
      <div
        v-for="cls in filteredClasses"
        :key="cls.id"
        class="bg-white rounded-2xl shadow-md hover:shadow-xl transition-all duration-300 hover:-translate-y-1 overflow-hidden"
        :class="{
          'border-2 border-green-400': cls.status === 'ready_for_students',
          'border-2 border-orange-400': cls.status === 'waiting_approval'
        }"
      >
        <!-- Card Header -->
        <div class="p-5 bg-gradient-to-r from-emerald-600 to-emerald-700 text-white">
          <div class="flex justify-between items-start">
            <h3 class="text-xl font-bold">{{ cls.name }}</h3>
            <span class="px-3 py-1 bg-white/20 rounded-full text-xs font-semibold flex items-center gap-1">
              <component :is="getStatusIcon(cls.status)" class="w-3 h-3" />
              {{ getStatusText(cls.status) }}
            </span>
          </div>
        </div>

        <!-- Card Body -->
        <div class="p-5">
          <p class="text-gray-600 mb-4 line-clamp-2">{{ cls.description }}</p>

          <div class="grid grid-cols-2 gap-3 text-sm">
            <div>
              <span class="text-gray-500 font-semibold block flex items-center gap-1"><BarChart3 class="w-4 h-4" /> Độ khó:</span>
              <span class="text-gray-800">{{ cls.difficulty }}</span>
            </div>
            <div>
              <span class="text-gray-500 font-semibold block flex items-center gap-1"><Users class="w-4 h-4" /> Sức chứa:</span>
              <span class="text-gray-800">{{ getEnrolledCount(cls.id) }}/{{ cls.maxStudents }}</span>
            </div>
            <div>
              <span class="text-gray-500 font-semibold block flex items-center gap-1"><MapPin class="w-4 h-4" /> Phòng:</span>
              <span class="text-gray-800">{{ getRoomName(cls.roomId) }}</span>
            </div>
            <div>
              <span class="text-gray-500 font-semibold block flex items-center gap-1"><GraduationCap class="w-4 h-4" /> Giáo viên:</span>
              <span class="text-gray-800">{{ getTeacherName(cls.teacherId) }}</span>
            </div>
            <div class="col-span-2">
              <span class="text-gray-500 font-semibold block flex items-center gap-1"><Clock class="w-4 h-4" /> Lịch học:</span>
              <span class="text-gray-800">{{ getScheduleText(cls) }}</span>
            </div>
          </div>
        </div>

        <!-- Card Footer -->
        <div class="p-4 bg-gray-50 border-t flex gap-2">
          <button
            @click="viewDetails(cls)"
            class="flex-1 px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 font-semibold transition-all flex items-center justify-center gap-2"
          >
            <Eye class="w-4 h-4" /> Chi tiết
          </button>
        </div>
      </div>
    </div>

    <!-- Details Modal -->
    <div
      v-if="showDetailsModal && selectedClass"
      class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
      @click.self="showDetailsModal = false"
    >
      <div class="bg-white rounded-2xl w-full max-w-3xl max-h-[90vh] overflow-y-auto shadow-2xl animate-fadeIn">
        <!-- Modal Header -->
        <div class="sticky top-0 p-6 bg-gradient-to-r from-emerald-600 to-emerald-700 text-white flex justify-between items-start rounded-t-2xl z-50">
          <div>
            <h2 class="text-2xl font-bold mb-1">{{ selectedClass.name }}</h2>
            <div class="flex items-center gap-2 text-emerald-100 text-sm">
              <component :is="getStatusIcon(selectedClass.status)" class="w-4 h-4" />
              {{ getStatusText(selectedClass.status) }}
            </div>
          </div>
          <button
            @click="showDetailsModal = false"
            class="w-10 h-10 bg-white/20 rounded-full hover:bg-white/30 transition-all flex items-center justify-center"
          >
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 6L6 18M6 6l12 12"/></svg>
          </button>
        </div>

        <!-- Modal Body -->
        <div class="p-8 space-y-8">
          <!-- Description -->
          <div>
            <h3 class="text-lg font-bold text-gray-800 mb-3 flex items-center gap-2">
              <FileText class="w-5 h-5 text-emerald-600" />
              Mô tả lớp học
            </h3>
            <p class="text-gray-600 leading-relaxed bg-gray-50 p-4 rounded-xl border border-gray-100">
              {{ selectedClass.description }}
            </p>
          </div>

          <!-- General Info -->
          <div>
            <h3 class="text-lg font-bold text-gray-800 mb-3 flex items-center gap-2">
              <BarChart3 class="w-5 h-5 text-emerald-600" />
              Thông tin chung
            </h3>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="bg-gray-50 p-4 rounded-xl border border-gray-100">
                <span class="text-gray-500 text-sm font-semibold">Độ khó</span>
                <p class="text-gray-800 font-semibold">{{ selectedClass.difficulty }}</p>
              </div>
              <div class="bg-gray-50 p-4 rounded-xl border border-gray-100">
                <span class="text-gray-500 text-sm font-semibold">Sức chứa</span>
                <p class="text-gray-800 font-semibold">{{ getEnrolledCount(selectedClass.id) }}/{{ selectedClass.maxStudents }}</p>
              </div>
              <div class="bg-gray-50 p-4 rounded-xl border border-gray-100">
                <span class="text-gray-500 text-sm font-semibold">Phòng học</span>
                <p class="text-gray-800 font-semibold">{{ getRoomName(selectedClass.roomId) }}</p>
              </div>
              <div class="bg-gray-50 p-4 rounded-xl border border-gray-100">
                <span class="text-gray-500 text-sm font-semibold">Giáo viên</span>
                <p class="text-gray-800 font-semibold">{{ getTeacherName(selectedClass.teacherId) }}</p>
              </div>
            </div>
          </div>

          <!-- Schedule Info -->
          <div>
            <h3 class="text-lg font-bold text-gray-800 mb-3 flex items-center gap-2">
              <Clock class="w-5 h-5 text-emerald-600" />
              Lịch học
            </h3>
            <div class="bg-gray-50 rounded-xl border border-gray-100 p-5 space-y-3">
              <div class="flex justify-between">
                <span class="text-gray-600">Thời gian:</span>
                <span class="font-semibold">{{ selectedClass.startTime }} - {{ selectedClass.endTime }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-gray-600">Lịch:</span>
                <span class="font-semibold">{{ getScheduleText(selectedClass) }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-gray-600">Thời hạn:</span>
                <span class="font-semibold">{{ formatDate(selectedClass.startDate) }} - {{ formatDate(selectedClass.endDate) }}</span>
              </div>
            </div>
          </div>

          <!-- Sessions List -->
          <div>
            <h3 class="text-lg font-bold text-gray-800 mb-3 flex items-center gap-2">
              <Calendar class="w-5 h-5 text-emerald-600" />
              Danh sách buổi học
            </h3>

            <div v-if="loadingSessionsDetails" class="text-center py-8">
                  <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-emerald-600 mx-auto"></div>
              <p class="text-gray-500 mt-2">Đang tải...</p>
            </div>

            <div v-else-if="selectedClassSessions.length > 0" class="space-y-2 max-h-96 overflow-y-auto">
              <div v-for="session in selectedClassSessions" :key="session.id"
                   class="flex items-center justify-between p-3 bg-gray-50 rounded-lg border border-gray-100">
                <div class="flex items-center gap-3">
                  <div class="w-8 h-8 rounded-full bg-emerald-100 text-emerald-600 flex items-center justify-center text-sm font-bold">
                    {{ new Date(session.date).getDate() }}
                  </div>
                  <div>
                    <p class="font-semibold text-gray-900">{{ formatDate(session.date) }}</p>
                    <p class="text-sm text-gray-600">{{ formatScheduleTime(session.startTime) }} - {{ formatScheduleTime(session.endTime) }}</p>
                  </div>
                </div>
                <span :class="session.status === 'completed' ? 'bg-green-100 text-green-700' : 'bg-blue-100 text-blue-700'"
                      class="px-2 py-1 rounded-full text-xs font-semibold">
                  {{ session.status === 'completed' ? 'Hoàn thành' : 'Sắp diễn ra' }}
                </span>
              </div>
            </div>

            <div v-else class="text-center py-8 bg-gray-50 rounded-xl border border-gray-100 border-dashed">
              <Calendar class="w-12 h-12 text-gray-300 mx-auto mb-2" />
              <p class="text-gray-500">Chưa có buổi học nào</p>
            </div>
          </div>

          <!-- Enrolled Students -->
          <div>
            <h3 class="text-lg font-bold text-gray-800 mb-3 flex items-center gap-2">
              <Users class="w-5 h-5 text-emerald-600" />
              Học viên đã đăng ký
            </h3>

            <div v-if="selectedClassStudents.length > 0" class="overflow-x-auto">
              <table class="w-full">
                <thead class="bg-gray-50">
                  <tr>
                    <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 uppercase">Học viên</th>
                    <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 uppercase">Ngày đăng ký</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-gray-100">
                  <tr v-for="student in selectedClassStudents" :key="student.id" class="hover:bg-gray-50">
                    <td class="px-4 py-3">
                      <div class="flex items-center">
                        <div class="w-8 h-8 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center font-bold text-sm mr-3">
                          {{ student.fullName?.charAt(0).toUpperCase() }}
                        </div>
                        <div>
                          <p class="font-semibold text-gray-900">{{ student.fullName }}</p>
                          <p class="text-sm text-gray-600">{{ student.email }}</p>
                        </div>
                      </div>
                    </td>
                    <td class="px-4 py-3 text-sm text-gray-600">
                      {{ formatDate(student.registeredAt) }}
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            <div v-else class="text-center py-8 bg-gray-50 rounded-xl border border-gray-100 border-dashed">
              <Users class="w-12 h-12 text-gray-300 mx-auto mb-2" />
              <p class="text-gray-500">Chưa có học viên nào đăng ký</p>
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
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-fadeIn {
  animation: fadeIn 0.3s ease-out;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>