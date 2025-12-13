<template>
  <div class="bg-white rounded-2xl shadow-lg border border-gray-100 overflow-hidden">
    <!-- Header -->
    <div class="p-6 bg-white border-b border-gray-100 flex justify-between items-center">
      <h2 class="text-xl font-bold text-gray-800 flex items-center gap-2">
        <CalendarIcon class="w-6 h-6 text-blue-600" />
        Lịch {{ role === 'student' ? 'Học' : 'Dạy' }} - Tháng {{ currentMonth + 1 }}/{{ currentYear }}
      </h2>
      <div class="flex gap-2">
        <button 
          @click="prevMonth"
          class="p-2 hover:bg-gray-100 rounded-lg transition-colors border border-gray-200"
        >
          <ChevronLeft class="w-5 h-5 text-gray-600" />
        </button>
        <button 
          @click="nextMonth"
          class="p-2 hover:bg-gray-100 rounded-lg transition-colors border border-gray-200"
        >
          <ChevronRight class="w-5 h-5 text-gray-600" />
        </button>
      </div>
    </div>

    <!-- Calendar Grid -->
    <div class="p-4">
      <!-- Days Header -->
      <div class="grid grid-cols-7 mb-2">
        <div 
          v-for="day in ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7']" 
          :key="day"
          class="text-center font-semibold text-gray-500 py-2 text-sm uppercase tracking-wider"
        >
          {{ day }}
        </div>
      </div>

      <!-- Days Grid -->
      <div class="grid grid-cols-7 gap-2">
        <div 
          v-for="(date, index) in calendarDays" 
          :key="index"
          class="min-h-[100px] border rounded-xl p-2 transition-all relative group"
          :class="[
            !date ? 'bg-gray-50/50 border-transparent' : 'bg-white border-gray-100 hover:border-blue-300 hover:shadow-md cursor-pointer',
            isToday(date) ? 'ring-2 ring-blue-500 ring-offset-1' : ''
          ]"
          @click="date && openDayDetails(date)"
        >
          <template v-if="date">
            <span 
              class="text-sm font-medium block mb-1"
              :class="isToday(date) ? 'text-blue-600 font-bold' : 'text-gray-700'"
            >
              {{ date.getDate() }}
            </span>
            
            <!-- Events Dots -->
            <div class="space-y-1">
              <div 
                v-for="(session, sIndex) in getSessionsForDate(date)" 
                :key="sIndex"
                class="text-xs truncate px-1.5 py-0.5 rounded"
                :class="session.isRegistered ? 'bg-blue-50 text-blue-700 border border-blue-100' : 'bg-blue-50 text-blue-700 border border-blue-100'"
                :title="`${session.startTime} - ${session.className}`"
              >
                {{ session.startTime }} {{ session.className }}
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
        class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50"
        @click.self="selectedDate = null"
      >
        <div class="bg-white rounded-2xl w-full max-w-lg shadow-2xl overflow-hidden">
          <div class="p-6 border-b border-gray-100 flex justify-between items-center bg-gray-50">
            <h3 class="text-xl font-bold text-gray-800 flex items-center gap-2">
              <Clock class="w-5 h-5 text-blue-600" />
              Lịch trình ngày {{ formatDate(selectedDate) }}
            </h3>
            <button 
              @click="selectedDate = null"
              class="text-gray-400 hover:text-gray-600 transition-colors"
            >
              <X class="w-6 h-6" />
            </button>
          </div>
          
          <div class="p-6 max-h-[60vh] overflow-y-auto">
            <div v-if="selectedDateSessions.length === 0" class="text-center py-8 text-gray-500">
              <div class="mb-2">😴</div>
              Không có lịch nào trong ngày này
            </div>
            
            <div v-else class="space-y-4">
              <div 
                v-for="session in selectedDateSessions" 
                :key="session.id"
                class="flex gap-4 p-4 rounded-xl border-2 border-gray-100 hover:border-blue-200 transition-colors bg-white group"
              >
                <!-- Time Column -->
                <div class="flex flex-col items-center justify-center w-20 rounded-lg font-bold shrink-0"
                  :class="session.isRegistered ? 'bg-blue-50 text-blue-700' : 'bg-blue-50 text-blue-700'"
                >
                  <span class="text-lg">{{ session.startTime }}</span>
                  <span class="text-xs font-normal" :class="session.isRegistered ? 'text-blue-500' : 'text-blue-500'">đến</span>
                  <span class="text-sm">{{ session.endTime }}</span>
                </div>
                
                <!-- Info Column -->
                <div class="flex-1">
                  <h4 class="font-bold text-gray-800 text-lg group-hover:text-blue-600 transition-colors">
                    {{ session.className }}
                  </h4>
                  <div class="flex items-center gap-2 text-sm text-gray-600 mt-1">
                    <MapPin class="w-4 h-4 text-gray-400" />
                    {{ session.roomName }}
                  </div>
                  <div class="flex items-center gap-2 text-sm text-gray-600 mt-1">
                    <User class="w-4 h-4 text-gray-400" />
                    {{ session.teacherName }}
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
  User 
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
    .filter(s => s.date === dateStr)
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
    console.log('📅 loadSchedule called:', { userId, role: props.role, startStr, endStr });
    
    let scheduleData = [];
    if (props.role === 'student') {
      console.log('👨‍🎓 Loading student schedule...');
      scheduleData = await unifiedApi.getStudentSchedule(userId, startStr, endStr);
      const regs = await unifiedApi.getStudentRegistrations(userId);
      registeredScheduleIds.value = new Set(regs.map(r => r.scheduleId)); 
    } else {
      console.log('👨‍🏫 Loading teacher schedule...');
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
    .filter(s => s.date === dateStr)
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
</script>