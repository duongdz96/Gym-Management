<template>
  <div class="bg-white rounded-2xl shadow-lg border border-gray-100 overflow-hidden">
    <!-- Header -->
    <div class="p-3 sm:p-6 bg-white border-b border-gray-100 flex justify-between items-center">
      <h2 class="text-base sm:text-xl font-bold text-gray-800 flex items-center gap-1 sm:gap-2">
        <CalendarIcon class="w-5 h-5 sm:w-6 sm:h-6 text-green-600" />
        <span class="hidden sm:inline">Lịch {{ role === 'student' ? 'Học' : 'Dạy' }} - Tháng {{ currentMonth + 1 }}/{{ currentYear }}</span>
        <span class="sm:hidden">T{{ currentMonth + 1 }}/{{ currentYear }}</span>
      </h2>
      <div class="flex gap-1 sm:gap-2">
        <button 
          @click="prevMonth"
          class="p-1.5 sm:p-2 hover:bg-gray-100 rounded-lg transition-colors border border-gray-200"
        >
          <ChevronLeft class="w-4 h-4 sm:w-5 sm:h-5 text-gray-600" />
        </button>
        <button 
          @click="nextMonth"
          class="p-1.5 sm:p-2 hover:bg-gray-100 rounded-lg transition-colors border border-gray-200"
        >
          <ChevronRight class="w-4 h-4 sm:w-5 sm:h-5 text-gray-600" />
        </button>
      </div>
    </div>

  </div> 

<div v-if="upcomingSessions.length > 0" class="p-3 sm:p-4 bg-green-50/50 border-b border-green-100">
  <h3 class="text-xs sm:text-sm font-bold text-green-800 uppercase tracking-wide mb-2 sm:mb-3 flex items-center gap-1 sm:gap-2">
    <Clock class="w-3 h-3 sm:w-4 sm:h-4" />
    <span class="hidden sm:inline">Sắp diễn ra ({{ currentMonth + 1 }}/{{ currentYear }})</span>
    <span class="sm:hidden">Sắp diễn ra</span>
  </h3>
  
  <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-2 sm:gap-3">
    <div 
      v-for="session in upcomingSessions" 
      :key="session.id"
      class="bg-white p-2 sm:p-3 rounded-xl border border-green-200 shadow-sm hover:shadow-md transition-all cursor-pointer group"
      @click="openDayDetails(new Date(session.date))"
    >
      <div class="flex justify-between items-start mb-1 sm:mb-2">
        <div class="flex flex-col">
          <span class="text-xs font-bold text-green-600 bg-green-50 px-1.5 sm:px-2 py-0.5 rounded-md w-fit">
            {{ formatDate(new Date(session.date)) }}
          </span>
          <span class="text-base sm:text-lg font-bold text-gray-800 mt-0.5 sm:mt-1">
            {{ session.startTime }}
          </span>
        </div>
        <div class="p-1 sm:p-1.5 bg-gray-50 rounded-lg group-hover:bg-green-500 group-hover:text-white transition-colors">
          <ArrowRight class="w-3 h-3 sm:w-4 sm:h-4 text-gray-400 group-hover:text-white" />
        </div>
      </div>

      <div>
        <div class="text-sm sm:text-base font-semibold text-gray-700 truncate" :title="session.className">
          {{ session.className }}
        </div>
        <div class="flex items-center gap-1 sm:gap-1.5 text-xs text-gray-500 mt-0.5 sm:mt-1">
          <MapPin class="w-3 h-3" />
          <span class="truncate">{{ session.roomName || 'Chưa xếp phòng' }}</span>
        </div>
      </div>
    </div>
  </div>
</div>
<div class="p-2 sm:p-4">

    <!-- Calendar Grid -->
    <div class="p-2 sm:p-4">
      <!-- Days Header -->
      <div class="grid grid-cols-7 mb-1 sm:mb-2">
        <div 
          v-for="day in ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7']" 
          :key="day"
          class="text-center font-semibold text-gray-500 py-1 sm:py-2 text-xs sm:text-sm uppercase tracking-wider"
        >
          {{ day }}
        </div>
      </div>

      <!-- Days Grid -->
      <div class="grid grid-cols-7 gap-1 sm:gap-2">
        <div 
          v-for="(date, index) in calendarDays" 
          :key="index"
          class="min-h-[60px] sm:min-h-[100px] border rounded-lg sm:rounded-xl p-1 sm:p-2 transition-all relative group"
          :class="[
            !date ? 'bg-gray-50/50 border-transparent' : 'bg-white border-gray-100 hover:border-green-300 hover:shadow-md cursor-pointer',
            isToday(date) ? 'ring-1 sm:ring-2 ring-green-500 ring-offset-1' : ''
          ]"
          @click="date && openDayDetails(date)"
        >
          <template v-if="date">
            <span 
              class="text-xs sm:text-sm font-medium block mb-0.5 sm:mb-1"
              :class="isToday(date) ? 'text-green-600 font-bold' : 'text-gray-700'"
            >
              {{ date.getDate() }}
            </span>
            
            <!-- Events Dots -->
            <div class="space-y-0.5 sm:space-y-1">
              <div 
                v-for="(session, sIndex) in getSessionsForDate(date)" 
                :key="sIndex"
                class="text-[10px] sm:text-xs truncate px-1 sm:px-1.5 py-0.5 rounded"
                :class="session.isRegistered ? 'bg-green-50 text-green-700 border border-green-100' : 'bg-green-50 text-green-700 border border-green-100'"
                :title="`${session.startTime} - ${session.className}`"
              >
                <span class="hidden sm:inline">{{ session.startTime }} {{ session.className }}</span>
                <span class="sm:hidden">{{ session.startTime }}</span>
              </div>
            </div>
          </template>
        </div>
      </div>
    </div>

    <!-- Details Modal -->
    <transition
      enter-active-class="transition duration-200 ease-out"
      enter-from-class="opacity-0 scale-95"
      enter-to-class="opacity-100 scale-100"
      leave-active-class="transition duration-150 ease-in"
      leave-from-class="opacity-100 scale-100"
      leave-to-class="opacity-0 scale-95"
    >
      <div 
        v-if="selectedDate"
        class="fixed inset-0 z-50 flex items-center justify-center p-2 sm:p-4 bg-black/50"
        @click.self="selectedDate = null"
      >
        <div class="bg-white rounded-xl sm:rounded-2xl w-full max-w-lg shadow-2xl overflow-hidden">
          <div class="p-3 sm:p-6 border-b border-gray-100 flex justify-between items-center bg-gray-50">
            <h3 class="text-base sm:text-xl font-bold text-gray-800 flex items-center gap-1 sm:gap-2">
              <Clock class="w-4 h-4 sm:w-5 sm:h-5 text-green-600" />
              <span class="hidden sm:inline">Lịch trình ngày {{ formatDate(selectedDate) }}</span>
              <span class="sm:hidden">{{ formatDate(selectedDate) }}</span>
            </h3>
            <button 
              @click="selectedDate = null"
              class="text-gray-400 hover:text-gray-600 transition-colors"
            >
              <X class="w-5 h-5 sm:w-6 sm:h-6" />
            </button>
          </div>
          
          <div class="p-3 sm:p-6 max-h-[60vh] overflow-y-auto">
            <div v-if="selectedDateSessions.length === 0" class="text-center py-6 sm:py-8 text-gray-500">
              <div class="mb-2 text-2xl">😴</div>
              <p class="text-sm sm:text-base">Không có lịch nào trong ngày này</p>
            </div>
            
            <div v-else class="space-y-2 sm:space-y-4">
              <div 
                v-for="session in selectedDateSessions" 
                :key="session.id"
                class="flex gap-2 sm:gap-4 p-2 sm:p-4 rounded-lg sm:rounded-xl border-2 border-gray-100 hover:border-green-200 transition-colors bg-white group"
              >
                <!-- Time Column -->
                <div class="flex flex-col items-center justify-center w-16 sm:w-20 rounded-lg font-bold shrink-0 py-2"
                  :class="session.isRegistered ? 'bg-green-50 text-green-700' : 'bg-green-50 text-green-700'"
                >
                  <span class="text-sm sm:text-lg">{{ session.startTime }}</span>
                  <span class="text-[10px] sm:text-xs font-normal" :class="session.isRegistered ? 'text-green-500' : 'text-green-500'">đến</span>
                  <span class="text-xs sm:text-sm">{{ session.endTime }}</span>
                </div>
                
                <!-- Info Column -->
                <div class="flex-1 min-w-0">
                  <h4 class="font-bold text-gray-800 text-sm sm:text-lg group-hover:text-green-600 transition-colors truncate">
                    {{ session.className }}
                  </h4>
                  <div class="flex items-center gap-1 sm:gap-2 text-xs sm:text-sm text-gray-600 mt-0.5 sm:mt-1">
                    <MapPin class="w-3 h-3 sm:w-4 sm:h-4 text-gray-400 shrink-0" />
                    <span class="truncate">{{ session.roomName }}</span>
                  </div>
                  <div class="flex items-center gap-1 sm:gap-2 text-xs sm:text-sm text-gray-600 mt-0.5 sm:mt-1">
                    <User class="w-3 h-3 sm:w-4 sm:h-4 text-gray-400 shrink-0" />
                    <span class="truncate">{{ session.teacherName }}</span>
                  </div>
                  <!-- Note Display -->
                  <div v-if="session.note" class="mt-1 sm:mt-2 p-1.5 sm:p-2 bg-orange-50 border border-orange-200 rounded-lg">
                    <div class="flex items-start gap-1 sm:gap-2 text-xs sm:text-sm">
                      <AlertTriangle class="w-3 h-3 sm:w-4 sm:h-4 text-orange-600 shrink-0 mt-0.5" />
                      <div class="min-w-0">
                        <div class="font-semibold text-orange-800">Thông báo thay đổi:</div>
                        <div class="text-orange-700">{{ session.note }}</div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>
<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { 
  Calendar as CalendarIcon, 
  ChevronLeft, 
  ChevronRight, 
  X, 
  Clock, 
  MapPin, 
  User,
  ArrowRight,
  AlertTriangle
} from 'lucide-vue-next';
import { getCalendarWeeks, formatDate as formatDisplayDate } from '@/views/Test/dateUtils.js';
import unifiedApi from '@/services/unifiedClassApi.js';
import { useAuthStore } from '@/stores/useAuthStore';

const authStore = useAuthStore();

const props = defineProps({
  role: {
    type: String,
    required: true, // 'student' or 'teacher'
  },
  userId: {
    type: Number,
    required: true
  }
});

const currentMonth = ref(new Date().getMonth());
const currentYear = ref(new Date().getFullYear());
const sessions = ref([]);
const selectedDate = ref(null);
const registeredScheduleIds = ref(new Set());

// --- HELPER FUNCTION: CHUYỂN NGÀY LOCAL SANG CHUỖI YYYY-MM-DD ---
// Hàm này thay thế hoàn toàn cho .toISOString().split('T')[0]
const toLocalISOString = (date) => {
  if (!date) return '';
  const year = date.getFullYear();
  // Tháng trong JS bắt đầu từ 0 nên phải +1
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

const calendarWeeks = computed(() => {
  return getCalendarWeeks(currentYear.value, currentMonth.value);
});

const calendarDays = computed(() => {
  return calendarWeeks.value.flat();
});

// Lọc session cho modal chi tiết
const selectedDateSessions = computed(() => {
  if (!selectedDate.value) return [];
  // SỬA: Dùng ngày local để so sánh
  const dateStr = toLocalISOString(selectedDate.value);
  return sessions.value
    .filter(s => s.date === dateStr && s.status !== 'CANCELLED')
    .sort((a, b) => (a.startTime || '00:00').localeCompare(b.startTime || '00:00'));
});

const loadSchedule = async () => {
  // Lấy phạm vi tháng
  const start = new Date(currentYear.value, currentMonth.value, 1);
  const end = new Date(currentYear.value, currentMonth.value + 1, 0);
  
  // SỬA: Gửi ngày lên server theo định dạng local (không bị lùi ngày do UTC)
  const startStr = toLocalISOString(start);
  const endStr = toLocalISOString(end);

  try {
    const userId = props.userId || authStore.user?.id || 0;
    
    let scheduleData = [];
    if (props.role === 'student') {
      scheduleData = await unifiedApi.getStudentSchedule(userId, startStr, endStr);
      const regs = await unifiedApi.getStudentRegistrations(userId);
      registeredScheduleIds.value = new Set(regs.map(r => r.scheduleId)); 
    } else {
      scheduleData = await unifiedApi.getTeacherSchedule(userId, startStr, endStr);
      registeredScheduleIds.value = new Set();
    }
    
    // Chuẩn hóa dữ liệu trả về
    sessions.value = scheduleData.map(s => {
      // Trường hợp dữ liệu cũ hoặc format lạ
      if (s.date && s.startTime && s.endTime && typeof s.startTime === 'string' && s.startTime.length <= 5) {
        return { ...s, isRegistered: registeredScheduleIds.value.has(s.id) };
      }
      
      // Tạo Date object (JS tự convert UTC từ server sang giờ Local của máy)
      const startDate = new Date(s.startTime);
      const endDate = new Date(s.endTime);
      
      return {
        ...s,
        // SỬA: Ép cứng ngày hiển thị theo ngày Local của startDate
        date: toLocalISOString(startDate),
        
        // Lấy giờ phút theo giờ Local
        startTime: startDate.toTimeString().substring(0, 5),
        endTime: endDate.toTimeString().substring(0, 5),
        isRegistered: registeredScheduleIds.value.has(s.id)
      };
    });
    
    // Filter cancelled/completed classes and fetch teacher info
    if (sessions.value.length > 0) {
      // Lấy danh sách unique fitnessClassId
      const uniqueFitnessClassIds = [...new Set(sessions.value.map(s => s.fitnessClassId || s.classId).filter(Boolean))];
      
      // Tạo map để lưu teacher theo fitnessClassId (chỉ cho student)
      const teacherMap = new Map();
      
      // Fetch class info và teacher info cho từng fitness class
      for (const fitnessClassId of uniqueFitnessClassIds) {
        try {
          const classRegistrations = await unifiedApi.getTeachersByFitnessClass(fitnessClassId);
          
          // Check if class is cancelled or completed from registration data (không cần fetch getClass nữa)
          const firstReg = classRegistrations[0];
          if (firstReg && firstReg.fitnessClass) {
            const classStatus = firstReg.fitnessClass.status;
            if (classStatus === 'cancelled' || classStatus === 'completed') {
              // Bỏ toàn bộ buổi học của lớp bị hủy/hoàn thành
              sessions.value = sessions.value.filter(s => 
                (s.fitnessClassId || s.classId) !== fitnessClassId
              );
              continue; // Skip to next class
            }
          }
          
          // Fetch teacher info (chỉ cho student schedule)
          if (props.role === 'student') {
            const approvedReg = classRegistrations.find(r => r.status === 'APPROVED') || classRegistrations[0];
            if (approvedReg && approvedReg.teacher) {
              teacherMap.set(fitnessClassId, approvedReg.teacher.fullName || approvedReg.teacher.name);
            }
          }
        } catch (error) {
          console.error(`Error fetching info for class ${fitnessClassId}:`, error);
        }
      }
      
      // Merge teacher info vào sessions (chỉ cho student)
      if (props.role === 'student') {
        sessions.value = sessions.value.map(s => {
          const fitnessClassId = s.fitnessClassId || s.classId;
          const teacherName = teacherMap.get(fitnessClassId);
          return {
            ...s,
            teacherName: teacherName || s.teacherName || 'Chưa có giáo viên'
          };
        });
      }
    }
  } catch (error) {
    console.error('Error loading schedule:', error);
    sessions.value = [];
    registeredScheduleIds.value = new Set();
  }
};

onMounted(() => {
  loadSchedule();
});

watch([currentMonth, currentYear], () => {
  loadSchedule();
});

const prevMonth = () => {
  if (currentMonth.value === 0) {
    currentMonth.value = 11;
    currentYear.value--;
  } else {
    currentMonth.value--;
  }
};

const nextMonth = () => {
  if (currentMonth.value === 11) {
    currentMonth.value = 0;
    currentYear.value++;
  } else {
    currentMonth.value++;
  }
};

const isToday = (date) => {
  if (!date) return false;
  const today = new Date();
  return date.getDate() === today.getDate() &&
         date.getMonth() === today.getMonth() &&
         date.getFullYear() === today.getFullYear();
};

const getSessionsForDate = (date) => {
  if (!date) return [];
  // SỬA: Dùng ngày local để filter hiển thị dấu chấm trên lịch
  const dateStr = toLocalISOString(date); 
  return sessions.value
    .filter(s => s.date === dateStr && s.status !== 'CANCELLED')
    .sort((a, b) => (a.startTime || '00:00').localeCompare(b.startTime || '00:00'));
};

const openDayDetails = (date) => {
  selectedDate.value = date;
};

const formatDate = (date) => {
  if (!date) return '';
  // SỬA: Hiển thị tiêu đề modal đúng ngày local
  return formatDisplayDate(toLocalISOString(date)); 
};

const upcomingSessions = computed(() => {
  const now = new Date();
  
  // 1. Lọc các buổi học có thời gian trong tương lai và không bị hủy
  const futureSessions = sessions.value.filter(s => {
    // Tạo object Date từ date string và time string của session
    const sessionTime = new Date(`${s.date}T${s.startTime}`);
    return sessionTime > now && s.status !== 'CANCELLED';
  });

  // 2. Sắp xếp theo thời gian gần nhất trước
  futureSessions.sort((a, b) => {
    const timeA = new Date(`${a.date}T${a.startTime}`);
    const timeB = new Date(`${b.date}T${b.startTime}`);
    return timeA - timeB;
  });

  // 3. Chỉ lấy 3 buổi gần nhất
  return futureSessions.slice(0, 3);
});
</script>