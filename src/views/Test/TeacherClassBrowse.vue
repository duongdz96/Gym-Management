<template>
  <div class="p-6">
    <div class="flex items-center gap-3 mb-8">
      <BookOpen class="w-10 h-10 text-blue-600" />
      <h1 class="text-3xl font-bold bg-gradient-to-r from-blue-600 to-blue-700 bg-clip-text text-transparent">
        Đăng Ký Dạy Lớp
      </h1>
    </div>

    <!-- View Mode Toggle -->
    <div class="flex justify-center mb-8">
      <div class="bg-gray-100 p-1 rounded-xl inline-flex">
        <button 
          @click="viewMode = 'list'"
          class="px-6 py-2 rounded-lg font-bold transition-all flex items-center gap-2"
          :class="viewMode === 'list' 
            ? 'bg-white text-blue-600 shadow-sm' 
            : 'text-gray-500 hover:text-gray-700'"
        >
          <LayoutGrid class="w-5 h-5" />
          Lớp cần nhận
        </button>
        <button 
          @click="viewMode = 'calendar'"
          class="px-6 py-2 rounded-lg font-bold transition-all flex items-center gap-2"
          :class="viewMode === 'calendar' 
            ? 'bg-white text-blue-600 shadow-sm' 
            : 'text-gray-500 hover:text-gray-700'"
        >
          <CalendarIcon class="w-5 h-5" />
          Lịch dạy của tôi
        </button>
      </div>
    </div>

    <!-- Calendar View -->
    <div v-if="viewMode === 'calendar'">
      <schedule-calendar role="teacher" :user-id="currentTeacherId || 0" />
    </div>

    <!-- Class List View -->
    <div v-else>
      <!-- Filters -->
      <div class="flex gap-4 mb-6">
        <div class="relative">
          <Filter class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
          <select 
            v-model="filterDifficulty"
            class="pl-10 pr-4 py-3 border-2 border-gray-200 rounded-xl focus:border-blue-600 focus:ring-2 focus:ring-blue-100 outline-none transition-all appearance-none bg-white"
          >
            <option value="">Tất cả độ khó</option>
            <option value="Beginner">Beginner</option>
            <option value="Intermediate">Intermediate</option>
            <option value="Advanced">Advanced</option>
          </select>
        </div>
        
        <div class="relative flex-1">
          <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
          <input 
            v-model="searchQuery" 
            type="text" 
            placeholder="Tìm kiếm lớp học..." 
            class="w-full pl-10 pr-4 py-3 border-2 border-gray-200 rounded-xl focus:border-blue-600 focus:ring-2 focus:ring-blue-100 outline-none transition-all"
          />
        </div>
      </div>

    <!-- Available Classes -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
      <div 
        v-for="cls in filteredClasses" 
        :key="cls.id" 
        class="bg-white rounded-2xl shadow-md hover:shadow-xl transition-all duration-300 hover:-translate-y-1 overflow-hidden"
      >
        <!-- Card Header -->
        <div class="p-5 bg-gradient-to-r from-blue-600 to-blue-700 text-white">
          <div class="flex justify-between items-start">
            <h3 class="text-xl font-bold">{{ cls.name }}</h3>
            <span 
              class="px-3 py-1 rounded-full text-xs font-semibold"
              :class="{
                'bg-green-500': cls.difficulty === 'Beginner',
                'bg-orange-500': cls.difficulty === 'Intermediate',
                'bg-blue-500': cls.difficulty === 'Advanced'
              }"
            >
              {{ cls.difficulty }}
            </span>
          </div>
        </div>
        
        <!-- Card Body -->
        <div class="p-5 space-y-4">
          <p class="text-gray-600 line-clamp-2">{{ cls.description }}</p>
          
          <div class="grid grid-cols-2 gap-3 text-sm">
            <div>
              <span class="text-gray-500 font-semibold block flex items-center gap-1"><Users class="w-4 h-4" /> Sức chứa:</span>
              <span class="text-gray-800">{{ cls.maxStudents }} học viên</span>
            </div>
            <div>
              <span class="text-gray-500 font-semibold block flex items-center gap-1"><MapPin class="w-4 h-4" /> Phòng:</span>
              <span class="text-gray-800">{{ getRoomName(cls.roomId) }}</span>
            </div>
            <div>
              <span class="text-gray-500 font-semibold block flex items-center gap-1"><Clock class="w-4 h-4" /> Thời gian:</span>
              <span class="text-gray-800">{{ cls.startTime }} - {{ cls.endTime }}</span>
            </div>
            <div>
              <span class="text-gray-500 font-semibold block flex items-center gap-1"><Calendar class="w-4 h-4" /> Lịch:</span>
              <div class="flex items-center gap-2">
                <span class="text-gray-800">{{ getScheduleText(cls) }}</span>
                <button 
                  @click.stop="viewSessions(cls)" 
                  class="text-blue-600 hover:text-blue-800 text-xs font-bold underline flex items-center gap-1"
                >
                  <Eye class="w-3 h-3" /> Chi tiết
                </button>
              </div>
            </div>
            <div>
              <span class="text-gray-500 font-semibold block flex items-center gap-1"><Calendar class="w-4 h-4" /> Bắt đầu:</span>
              <span class="text-gray-800">{{ formatDate(cls.startDate) }}</span>
            </div>
            <div>
              <span class="text-gray-500 font-semibold block flex items-center gap-1"><Calendar class="w-4 h-4" /> Kết thúc:</span>
              <span class="text-gray-800">{{ formatDate(cls.endDate) }}</span>
            </div>
          </div>

          <!-- Conflict Warning -->
          <div v-if="conflictMap[cls.id]" class="p-3 bg-orange-50 border-2 border-orange-200 rounded-xl text-orange-700 font-semibold text-sm">
            <div class="flex items-center gap-2 mb-1">
              <AlertTriangle class="w-4 h-4" />
              Bạn có lịch trùng với lớp này
            </div>
            <div class="mt-2 space-y-1 font-normal ml-6">
              <div v-for="(conflict, idx) in conflictMap[cls.id]" :key="idx" class="flex items-center gap-1">
                <Calendar class="w-3 h-3" />
                {{ conflict.date }}: {{ conflict.time }} - {{ conflict.className }}
              </div>
            </div>
          </div>

          <!-- Application Status -->
          <div 
            v-if="getApplicationStatus(cls.id)" 
            class="p-3 border-2 rounded-xl font-semibold text-sm text-center flex items-center justify-center gap-2 relative group cursor-help transition-all"
            :class="{
              'bg-blue-50 border-blue-200 text-blue-700': getApplicationStatus(cls.id) === 'pending',
              'bg-green-50 border-green-200 text-green-700': getApplicationStatus(cls.id) === 'approved',
              'bg-red-50 border-red-200 text-red-700': getApplicationStatus(cls.id) === 'rejected'
            }"
          >
            <component 
              :is="getApplicationStatus(cls.id) === 'pending' ? Clock : getApplicationStatus(cls.id) === 'approved' ? CheckCircle : XCircle" 
              class="w-4 h-4"
            />
            {{ getApplicationStatusText(cls.id) }}
            
            <!-- Tooltip for Rejection Reason -->
            <div 
              v-if="getApplicationStatus(cls.id) === 'rejected' && getRejectionReason(cls.id)"
              class="absolute bottom-full left-1/2 -translate-x-1/2 mb-2 w-64 p-3 bg-gray-800 text-white text-xs rounded-lg shadow-xl opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none z-10 text-left"
            >
              <div class="font-bold mb-1 flex items-center gap-1">
                <XCircle class="w-3 h-3 text-red-400" />
                Lý do từ chối:
              </div>
              {{ getRejectionReason(cls.id) }}
              <!-- Arrow -->
              <div class="absolute top-full left-1/2 -translate-x-1/2 border-8 border-transparent border-t-gray-800"></div>
            </div>
          </div>
        </div>
        
        <!-- Card Footer -->
        <div class="p-4 bg-gray-50 border-t">
          <button 
            @click="applyToTeach(cls)" 
            :disabled="conflictMap[cls.id] || getApplicationStatus(cls.id)"
            class="w-full px-4 py-3 bg-gradient-to-r from-blue-600 to-blue-700 text-white rounded-xl font-semibold hover:shadow-lg transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
          >
            <component 
              :is="getApplicationStatus(cls.id) ? CheckCircle : Hand" 
              class="w-5 h-5"
            />
            {{ getApplicationStatus(cls.id) ? 'Đã đăng ký' : 'Đăng ký dạy' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-if="filteredClasses.length === 0" class="text-center py-16">
      <Inbox class="w-16 h-16 text-gray-400 mx-auto mb-4" />
      <p class="text-gray-500 text-lg">Không có lớp học nào cần giáo viên</p>
    </div>
  </div>
  <!-- End of v-else for Class List View -->
  <!-- End of v-else for Class List View -->
  <!-- Sessions Detail Modal -->
    <transition
      enter-active-class="transition duration-200 ease-out"
      enter-from-class="opacity-0 scale-95"
      enter-to-class="opacity-100 scale-100"
      leave-active-class="transition duration-150 ease-in"
      leave-from-class="opacity-100 scale-100"
      leave-to-class="opacity-0 scale-95"
    >
      <div 
        v-if="showSessionsModal"
        class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50"
        @click.self="showSessionsModal = false"
      >
        <div class="bg-white rounded-2xl w-full max-w-2xl shadow-2xl overflow-hidden max-h-[80vh] flex flex-col">
          <!-- Modal Header -->
          <div class="p-6 bg-gradient-to-r from-blue-600 to-blue-700 text-white flex justify-between items-center shrink-0">
            <div>
              <h3 class="text-xl font-bold flex items-center gap-2">
                <CalendarIcon class="w-6 h-6" />
                Chi tiết lịch dạy
              </h3>
              <p class="text-blue-100 text-sm mt-1">{{ selectedClass?.name }}</p>
            </div>
            <button 
              @click="showSessionsModal = false"
              class="text-white/80 hover:text-white transition-colors bg-white/10 hover:bg-white/20 p-2 rounded-lg"
            >
              <X class="w-6 h-6" />
            </button>
          </div>
          
          <!-- Modal Body -->
          <div class="p-6 overflow-y-auto flex-1">
            <div v-if="selectedClassSessions.length === 0" class="text-center py-8 text-gray-500">
              Chưa có lịch dạy cụ thể.
            </div>
            
            <div v-else class="grid gap-3">
              <div 
                v-for="(session, idx) in selectedClassSessions" 
                :key="session.id"
                class="flex items-center gap-4 p-4 rounded-xl border border-gray-100 hover:border-blue-200 hover:bg-blue-50/50 transition-all"
              >
                <!-- Index -->
                <div class="w-8 h-8 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center font-bold text-sm shrink-0">
                  {{ idx + 1 }}
                </div>
                
                <!-- Date Info -->
                <div class="flex-1">
                  <div class="font-bold text-gray-800 flex items-center gap-2">
                    <CalendarIcon class="w-4 h-4 text-blue-500" />
                    {{ formatDate(session.date) }}
                  </div>
                  <div class="text-sm text-gray-500 mt-1 flex items-center gap-4">
                    <span class="flex items-center gap-1">
                      <Clock class="w-3 h-3" /> {{ session.startTime }} - {{ session.endTime }}
                    </span>
                    <span class="flex items-center gap-1">
                      <MapPin class="w-3 h-3" /> {{ getRoomName(session.roomId) }}
                    </span>
                  </div>
                </div>
                
                <!-- Status -->
                <div 
                  class="text-xs font-semibold px-2 py-1 rounded"
                  :class="getSessionStatus(session).class"
                >
                  {{ getSessionStatus(session).text }}
                </div>
              </div>
            </div>
          </div>
          
          <!-- Modal Footer -->
          <div class="p-4 border-t bg-gray-50 flex justify-end shrink-0">
            <button 
              @click="showSessionsModal = false"
              class="px-6 py-2 bg-gray-200 text-gray-700 font-bold rounded-xl hover:bg-gray-300 transition-colors"
            >
              Đóng
            </button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useAuthStore } from '@/stores/useAuthStore';
import unifiedApi from './unifiedApi.js';
import { formatDate } from './dateUtils.js';
import ScheduleCalendar from './ScheduleCalendar.vue';

const authStore = useAuthStore();
import { 
  BookOpen, 
  Search, 
  Filter, 
  Users, 
  MapPin, 
  Clock, 
  Calendar as CalendarIcon, 
  AlertTriangle, 
  CheckCircle, 
  XCircle, 
  Hand,
  Inbox,
  LayoutGrid,
  Eye,
  X
} from 'lucide-vue-next';
import { useToast } from 'vue-toastification';

// State
const classes = ref([]);
const roomsData = ref([]);
const applications = ref([]);
const filterDifficulty = ref('');
const searchQuery = ref('');
const currentTeacherId = computed(() => {
  const user = authStore.user;
  return user?.id || null;
});
const viewMode = ref('list'); // 'list' | 'calendar'
const conflictMap = ref({}); // Store conflicts per class
const toast = useToast();

// Sessions Modal state
const showSessionsModal = ref(false);
const selectedClassSessions = ref([]);
const selectedClass = ref(null);

const viewSessions = async (cls) => {
  selectedClass.value = cls;
  try {
    const sessions = await unifiedApi.getSessions(cls.id);
    selectedClassSessions.value = sessions.sort((a, b) => new Date(a.date) - new Date(b.date));
    showSessionsModal.value = true;
  } catch (error) {
    toast.error('Không thể tải lịch học: ');
  }
};

const getSessionStatus = (session) => {
  const now = new Date();
  const sessionDate = new Date(session.date);
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
  const sDate = new Date(sessionDate.getFullYear(), sessionDate.getMonth(), sessionDate.getDate());

  if (sDate < today) {
    return { text: 'Đã diễn ra', class: 'bg-gray-100 text-gray-500' };
  } else if (sDate.getTime() === today.getTime()) {
    return { text: 'Hôm nay', class: 'bg-blue-100 text-blue-700' };
  } else {
    return { text: 'Sắp diễn ra', class: 'bg-green-100 text-green-700' };
  }
};

// Computed
const availableClasses = computed(() => {
  return classes.value.filter(c => 
    c.status === 'pending_teacher' || 
    (c.status === 'waiting_approval' && c.pendingTeacherId !== currentTeacherId.value) ||
    // Also show classes where this teacher has applied (even if rejected or approved) so they can see status
    applications.value.some(a => a.classId === c.id && a.teacherId === currentTeacherId.value)
  );
});

const filteredClasses = computed(() => {
  let result = availableClasses.value;
  
  if (filterDifficulty.value) {
    result = result.filter(c => c.difficulty === filterDifficulty.value);
  }
  
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(c => 
      c.name.toLowerCase().includes(query) ||
      c.description.toLowerCase().includes(query)
    );
  }
  
  return result;
});

// Methods
const loadData = async () => {
  classes.value = await unifiedApi.getClasses();
  roomsData.value = await unifiedApi.getRooms();
  applications.value = await unifiedApi.getApplications();
  
  // Check conflicts for all classes
  if (currentTeacherId.value) {
    for (const cls of classes.value) {
      try {
        const sessions = await unifiedApi.getSessions(cls.id);
        const sessionData = sessions.map(s => ({
          startTime: `${s.date}T${s.startTime}:00`,
          endTime: `${s.date}T${s.endTime}:00`
        }));
        const conflicts = unifiedApi.checkTeacherConflicts(currentTeacherId.value, sessionData, cls.id);
        if (conflicts.length > 0) {
          conflictMap.value[cls.id] = conflicts;
        }
      } catch (error) {
        toast.error('Lỗi khi kiểm tra trùng');
      }
    }
  }
};

const getRoomName = (roomId) => {
  const room = roomsData.value.find(r => r.id === roomId);
  return room ? room.name : 'Không xác định';
};

const getScheduleText = (cls) => {
  const daysOfWeek = ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'];
  if (cls.patternType === 'weekly') {
    const days = cls.daysOfWeek.map(d => daysOfWeek[d]).join(', ');
    return `${days}`;
  }
  if (cls.patternType === 'monthly') return 'Hàng tháng';
  if (cls.patternType === 'no_repeat') return 'Một lần';
  return 'Tùy chỉnh';
};

const hasConflict = async (cls) => {
  if (!currentTeacherId.value) return false;
  // Get sessions for this class
  const sessions = await unifiedApi.getSessions(cls.id);
  // Convert to format needed for conflict check
  const sessionData = sessions.map(s => ({
    startTime: `${s.date}T${s.startTime}:00`,
    endTime: `${s.date}T${s.endTime}:00`
  }));
  const conflicts = unifiedApi.checkTeacherConflicts(currentTeacherId.value, sessionData, cls.id);
  return conflicts.length > 0;
};

const getConflicts = async (cls) => {
  if (!currentTeacherId.value) return [];
  const sessions = await unifiedApi.getSessions(cls.id);
  const sessionData = sessions.map(s => ({
    startTime: `${s.date}T${s.startTime}:00`,
    endTime: `${s.date}T${s.endTime}:00`
  }));
  return unifiedApi.checkTeacherConflicts(currentTeacherId.value, sessionData, cls.id);
};

const getApplicationStatus = (classId) => {
  if (!currentTeacherId.value) return null;
  const app = applications.value.find(a => 
    a.classId === classId && a.teacherId === currentTeacherId.value
  );
  return app ? app.status : null;
};

const getRejectionReason = (classId) => {
  if (!currentTeacherId.value) return '';
  const app = applications.value.find(a => 
    a.classId === classId && a.teacherId === currentTeacherId.value
  );
  return app ? app.rejectionReason : '';
};

const getApplicationStatusText = (classId) => {
  const status = getApplicationStatus(classId);
  if (status === 'pending') return 'Đang chờ duyệt';
  if (status === 'approved') return 'Đã được duyệt';
  if (status === 'rejected') return 'Đã bị từ chối';
  return '';
};

const applyToTeach = async (cls) => {
  if (!currentTeacherId.value) {
    toast.error('Vui lòng đăng nhập!');
    return;
  }
  
  if (conflictMap.value[cls.id]) {
    toast.error('Bạn có lịch trùng với lớp này!');
    return;
  }
  
  if (confirm(`Bạn có chắc muốn đăng ký dạy lớp "${cls.name}"?`)) {
    try {
      await unifiedApi.applyToTeach(cls.id, currentTeacherId.value);
      toast.success('Đăng ký thành công! Vui lòng chờ Manager duyệt.');
      await loadData();
    } catch (error) {
      toast.error('Có lỗi xảy ra');
    }
  }
};

// Lifecycle
onMounted(() => {
  loadData();
});
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
