<template>
  <div class="p-6">
    <!-- Header -->
    <div class="flex justify-between items-center mb-8">
      <div class="flex items-center gap-3">
        <Dumbbell class="w-10 h-10 text-emerald-600" />
        <h1 class="text-3xl font-bold bg-gradient-to-r from-emerald-600 to-emerald-700 bg-clip-text text-transparent">
          Quản Lý Lớp Học
        </h1>
      </div>
      <button
        @click="showCreateForm = true"
        class="px-6 py-3 bg-gradient-to-r from-emerald-600 to-emerald-700 text-white rounded-xl font-semibold hover:shadow-lg hover:-translate-y-0.5 transition-all duration-300 flex items-center gap-2"
      >
        <Plus class="w-5 h-5" />
        Tạo Lớp Mới
      </button>
    </div>

    <!-- Filters -->
    <div class="flex gap-4 mb-6">
      <div class="relative">
        <Filter class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
        <select
          v-model="filterStatus"
          class="pl-10 pr-4 py-3 border-2 border-gray-200 rounded-xl focus:border-emerald-600 focus:ring-2 focus:ring-emerald-100 outline-none transition-all appearance-none bg-white"
        >
          <option value="">Tất cả trạng thái</option>
          <option value="draft">Nháp</option>
          <option value="pending_teacher">Chờ giáo viên</option>
          <option value="waiting_approval">Chờ duyệt</option>
          <option value="open">Đang mở</option>
          <option value="completed">Hoàn thành</option>
          <option value="cancelled">Đã hủy</option>
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
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5 mb-8">
      <div 
        v-for="cls in filteredClasses" 
        :key="cls.id" 
        class="bg-white rounded-2xl shadow-md hover:shadow-xl transition-all duration-300 hover:-translate-y-1 overflow-hidden"
        :class="{
          'border-2 border-green-400': cls.status === 'open',
          'border-2 border-orange-400': cls.status === 'waiting_approval'
        }"
      >
        <!-- Card Header -->
        <div class="p-5 bg-gradient-to-r from-emerald-600 to-emerald-700 text-white">
          <div class="flex justify-between items-start">
            <h3 class="text-xl font-bold truncate">{{ cls.name }}</h3>
            <span class="px-3 py-1 bg-white/20 rounded-full text-xs font-semibold flex items-center gap-1">
              <component :is="getStatusIcon(cls.status)" class="w-3 h-3" />
              {{ getStatusText(cls.status) }}
              <!-- {{ (cls.status) }} -->
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
              <span class="text-gray-500 font-semibold block flex items-center gap-1"><Calendar class="w-4 h-4" /> Số buổi học:</span>
              <span class="text-gray-800">{{ getSessionCount(cls.id) }}</span>
            </div>
            <div>
              <span class="text-gray-500 font-semibold block flex items-center gap-1"><MapPin class="w-4 h-4" /> Phòng:</span>
              <span class="text-gray-800">{{ getRoomName(cls.roomId) }}</span>
            </div>
            <div>
              <span class="text-gray-500 font-semibold block flex items-center gap-1"><GraduationCap class="w-4 h-4" /> Giáo viên:</span>
              <span class="text-gray-800">{{ getTeacherName(cls.teacherId) }}</span>
            </div>
            <div>
              <span class="text-gray-500 font-semibold block flex items-center gap-1"><Clock class="w-4 h-4" /> Thời gian:</span>
              <span class="text-gray-800">{{ cls.startTime }} - {{ cls.endTime }}</span>
            </div>
            <div>
              <span class="text-gray-500 font-semibold block flex items-center gap-1"><Calendar class="w-4 h-4" /> Lịch:</span>
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
          <button 
            @click="openEditClass(cls)" 
            class="flex-1 px-4 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600 font-semibold transition-all flex items-center justify-center gap-2"
          >
            <Edit class="w-4 h-4" /> Sửa
          </button>
          <button 
            v-if="cls.status === 'waiting_approval'" 
            @click="approveTeacher(cls)" 
            class="flex-1 px-4 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600 font-semibold transition-all flex items-center justify-center gap-2"
          >
            <CheckCircle class="w-4 h-4" /> Duyệt
          </button>
          <button
            @click="deleteClass(cls.id)"
            class="px-4 py-2 bg-emerald-500 text-white rounded-lg hover:bg-emerald-600 font-semibold transition-all flex items-center justify-center"
          >
            <Trash2 class="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <div v-if="totalPages > 1" class="flex items-center justify-between mt-8 px-4">
      <div class="text-sm text-gray-600">
        Hiển thị {{ (currentPage * pageSize) + 1 }} - {{ Math.min((currentPage + 1) * pageSize, totalElements) }} trong tổng số {{ totalElements }} lớp học
      </div>
      
      <div class="flex items-center gap-2">
        <button
          @click="prevPage"
          :disabled="currentPage === 0"
          class="px-4 py-2 rounded-lg border transition-all duration-200"
          :class="currentPage === 0 
            ? 'bg-gray-100 text-gray-400 cursor-not-allowed' 
            : 'bg-white text-gray-700 hover:bg-emerald-50 hover:border-emerald-300'"
        >
          ← Trước
        </button>
        
        <div class="flex gap-1">
          <button
            v-for="page in totalPages"
            :key="page"
            @click="goToPage(page - 1)"
            class="px-4 py-2 rounded-lg transition-all duration-200"
            :class="currentPage === page - 1
              ? 'bg-emerald-600 text-white font-semibold shadow-md'
              : 'bg-white text-gray-700 hover:bg-emerald-50 border'"
          >
            {{ page }}
          </button>
        </div>
        
        <button
          @click="nextPage"
          :disabled="currentPage >= totalPages - 1"
          class="px-4 py-2 rounded-lg border transition-all duration-200"
          :class="currentPage >= totalPages - 1
            ? 'bg-gray-100 text-gray-400 cursor-not-allowed' 
            : 'bg-white text-gray-700 hover:bg-emerald-50 hover:border-emerald-300'"
        >
          Sau →
        </button>
      </div>
    </div>

    <!-- Create Class Modal -->
    <AddClassModal 
      :show="showCreateForm"
      :rooms="roomsData"
      @close="showCreateForm = false"
      @created="handleClassCreated"
    />
    <!-- Details Modal -->
    <ManagerClassDetail
      :show="showDetailsModal"
      :fitness-class="selectedClass"
      :sessions="selectedClassSessions"
      :students="selectedClassStudents"
      :rooms="roomsData"
      :teachers="teachersData"
      :registrations="registrationsData"
      :loading="loadingSessionsDetails"
      @close="showDetailsModal = false"
      @edit-session="openEditSession"
    />

    <!-- Edit Modals -->
    <EditClassModal 
      :show="showEditClassModal" 
      :fitness-class="editingClass"
      @close="showEditClassModal = false"
      @updated="handleClassUpdated"
    />
    
    <EditSessionModal 
      :show="showEditSessionModal" 
      :session="editingSession"
      @close="showEditSessionModal = false"
      @updated="handleSessionUpdated"
    />
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useToast } from 'vue-toastification';
import unifiedApi from '@/services/unifiedClassApi.js';
import { formatDate } from '@/views/Test/dateUtils.js';
import api from '@/services/api';
import { 
  Dumbbell, 
  Plus, 
  Search, 
  FileText, 
  UserPlus, 
  Clock, 
  CheckCircle, 
  XCircle, 
  BarChart3, 
  MapPin, 
  GraduationCap, 
  Calendar, 
  Eye, 
  Trash2,
  Filter,
  Edit
} from 'lucide-vue-next';
import EditClassModal from './EditClassModal.vue';
import EditSessionModal from './EditSessionModal.vue';
import ManagerClassDetail from './ManagerClassDetail.vue';
import AddClassModal from './AddClassModal.vue';

const router = useRouter();
const toast = useToast();

// State
const classes = ref([]);
const roomsData = ref([]);
const teachersData = ref([]);
const studentsData = ref([]);
const registrationsData = ref([]);
const filterStatus = ref('');
const searchQuery = ref('');
const showCreateForm = ref(false);
const showDetailsModal = ref(false);
const selectedClass = ref(null);

// Pagination state
const currentPage = ref(0);
const totalPages = ref(0);
const totalElements = ref(0);
const pageSize = ref(6);
const loadingSessionsDetails = ref(false);
const selectedClassSessions = ref([]);
const classSessionsMap = ref(new Map()); // Map<classId, sessions[]> - lưu sessions cho tất cả classes

// Edit modals state
const showEditClassModal = ref(false);
const showEditSessionModal = ref(false);
const editingClass = ref(null);
const editingSession = ref(null);




// Computed
const filteredClasses = computed(() => {
  let result = classes.value;
  
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(c => 
      c.name.toLowerCase().includes(query) ||
      c.description.toLowerCase().includes(query)
    );
  }
  
  return result;
});



const selectedClassStudents = computed(() => {
  if (!selectedClass.value) return [];
  
  // Lấy tất cả học viên đã đăng ký ít nhất 1 buổi của lớp này
  const registrations = registrationsData.value.filter(r => 
    r.classId === selectedClass.value.id && r.status === 'active'
  );
  
  // Dùng Map để loại bỏ học viên trùng lặp (vì 1 học viên có thể đăng ký nhiều buổi)
  const uniqueStudentsMap = new Map();
  
  registrations.forEach(r => {
    // Chỉ thêm vào Map nếu chưa có, hoặc nếu registeredAt mới hơn
    if (!uniqueStudentsMap.has(r.studentId) || 
        new Date(r.registeredAt) < new Date(uniqueStudentsMap.get(r.studentId).registeredAt)) {
      const student = studentsData.value.find(s => s.id === r.studentId);
      if (student) {
        uniqueStudentsMap.set(r.studentId, {
          ...student,
          registeredAt: r.registeredAt
        });
      }
    }
  });
  
  // Chuyển Map thành Array
  return Array.from(uniqueStudentsMap.values());
});

// Methods
const loadData = async () => {
  // Use paginated API
  const pageData = await unifiedApi.getClassesPaged(
    filterStatus.value || null, 
    currentPage.value, 
    pageSize.value
  );
  
  classes.value = pageData.content;
  totalPages.value = pageData.totalPages;
  totalElements.value = pageData.totalElements;
  currentPage.value = pageData.currentPage;
  
  roomsData.value = await unifiedApi.getRooms();
  teachersData.value = await unifiedApi.getTeachers();
  studentsData.value = await unifiedApi.getStudents();
  
  registrationsData.value = [];
  for (const student of studentsData.value) {
    const studentRegs = await unifiedApi.getStudentRegistrations(student.id);
    registrationsData.value.push(...studentRegs);
  }
  console.log('Registrations loaded:', registrationsData.value);
  // Fetch sessions cho tất cả các classes ngay từ đầu
  const sessionsMap = new Map();
  for (const cls of classes.value) {
    try {
      const sessions = await unifiedApi.getSessions(cls.id);
      sessionsMap.set(cls.id, sessions.sort((a, b) => new Date(a.date) - new Date(b.date)));
    } catch (error) {
      sessionsMap.set(cls.id, []);
    }
  }
  classSessionsMap.value = sessionsMap;
};

const goToPage = (page) => {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page;
    loadData();
  }
};

const nextPage = () => {
  if (currentPage.value < totalPages.value - 1) {
    currentPage.value++;
    loadData();
  }
};

const prevPage = () => {
  if (currentPage.value > 0) {
    currentPage.value--;
    loadData();
  }
};



const getStatusText = (status) => {
  const statusMap = {
    draft: 'Nháp',
    pending_teacher: 'Chờ giáo viên',
    waiting_approval: 'Chờ duyệt',
    open: 'Đang mở',
    completed: 'Hoàn thành',
    cancelled: 'Đã hủy'
  };
  return statusMap[status] || status;
};

const getStatusIcon = (status) => {
  const iconMap = {
    draft: FileText,
    pending_teacher: UserPlus,
    waiting_approval: Clock,
    open: CheckCircle,
    completed: CheckCircle,
    cancelled: XCircle
  };
  return iconMap[status] || FileText;
};

const getRoomName = (roomId) => {
  const room = roomsData.value.find(r => r.id === roomId);
  return room ? room.name : 'Chưa chọn';
};

const getTeacherName = (teacherId) => {
  if (!teacherId) return 'Chưa có';
  const teacher = teachersData.value.find(t => t.id === teacherId);
  return teacher ? teacher.name : 'Không xác định';
};


const getSessionCount = (classId) => {
  const sessions = classSessionsMap.value.get(classId);
  return sessions ? sessions.length : 0;
};

const getSessionEnrollment = (sessionId) => {
  return registrationsData.value.filter(r => 
    r.scheduleId === sessionId
  ).length;
};

const getScheduleText = (cls) => {
  if (!cls) return '';
  const daysOfWeek = ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'];
  if (cls.patternType === 'weekly') {
    const days = cls.daysOfWeek?.map(d => daysOfWeek[d]).join(', ') || '';
    return `Hàng tuần: ${days}`;
  }
  if (cls.patternType === 'monthly') return 'Hàng tháng';
  if (cls.patternType === 'no_repeat') {
    return 'Tùy chọn';
  }
  return 'Tùy chỉnh';
};

const handleClassCreated = async () => {
  await loadData();
};


const viewDetails = async (cls) => {
  selectedClass.value = cls;
  showDetailsModal.value = true;
  loadingSessionsDetails.value = false;
  
  // Lấy sessions từ map đã load sẵn
  const sessions = classSessionsMap.value.get(cls.id) || [];
  selectedClassSessions.value = sessions;
};

// Edit handlers
const openEditClass = (cls) => {
  editingClass.value = cls;
  showEditClassModal.value = true;
};

const openEditSession = (session) => {
  // Enrich session with additional info for editing
  editingSession.value = {
    ...session,
    className: selectedClass.value?.name,
    fitnessClassId: selectedClass.value?.id
  };
  showEditSessionModal.value = true;
};

const handleClassUpdated = async (updatedClass) => {
  // Update local data
  const index = classes.value.findIndex(c => c.id === updatedClass.id);
  if (index !== -1) {
    classes.value[index] = { ...classes.value[index], ...updatedClass };
  }
  // If currently viewing this class, update selectedClass
  if (selectedClass.value?.id === updatedClass.id) {
    selectedClass.value = { ...selectedClass.value, ...updatedClass };
  }
  // Reload data to ensure consistency
  await loadData();
  toast.success('Cập nhật lớp học thành công!');
};

const handleSessionUpdated = async (updatedSession) => {
  // Update in selectedClassSessions
  const index = selectedClassSessions.value.findIndex(s => s.id === updatedSession.id);
  if (index !== -1) {
    selectedClassSessions.value[index] = { ...selectedClassSessions.value[index], ...updatedSession };
  }
  toast.success('Cập nhật buổi học thành công!');
};

const formatScheduleTime = (dateTime) => {
  if (!dateTime) return '';
  try {
    const date = new Date(dateTime);
    if (isNaN(date.getTime())) return '';
    return date.toTimeString().substring(0, 5);
  } catch (e) {
    if (typeof dateTime === 'string' && dateTime.match(/^\d{2}:\d{2}$/)) {
      return dateTime;
    }
    return '';
  }
};

const approveTeacher = async (cls) => {
  router.push('/manager/teacher-approval');
};

const deleteClass = async (classId) => {
  const cls = classes.value.find(c => c.id === classId);
  if (!cls) return;
  
  // Check if already inactive
  if (cls.status === 'cancelled' || cls.status === 'completed') {
    toast.warning('Lớp học này đã bị vô hiệu hóa trước đó.');
    return;
  }
  
  if (confirm('Bạn có chắc muốn vô hiệu hóa lớp học này?\n\nLưu ý: Đây là soft delete, lớp học sẽ chuyển sang trạng thái INACTIVE.')) {
    try {
      await api.delete(`/fitness_class/${classId}`);
      
      const index = classes.value.findIndex(c => c.id === classId);
      if (index !== -1) {
        classes.value[index].status = 'cancelled';
      }
      
      // If viewing this class, update selectedClass
      if (selectedClass.value?.id === classId) {
        selectedClass.value.status = 'cancelled';
      }
      
      toast.success('Đã vô hiệu hóa lớp học thành công!');
    } catch (error) {
      toast.error('Lỗi khi vô hiệu hóa lớp học');
    }
  }
};

// Watch filter status to reload data
watch(filterStatus, () => {
  currentPage.value = 0; // Reset to first page when filter changes
  loadData();
});

// Lifecycle
onMounted(() => {
  loadData();
});
</script>

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

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-10px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.animate-fadeIn {
  animation: fadeIn 0.3s ease-out;
}

.animate-slideIn {
  animation: slideIn 0.3s ease-out;
}

.line-clamp-2 {
  display: -webkit-box;
  line-clamp: 2;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
