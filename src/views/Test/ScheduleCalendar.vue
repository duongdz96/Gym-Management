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
                class="text-xs truncate px-1.5 py-0.5 rounded border relative"
                :class="getSessionClass(session)"
                :title="`${session.startTime} - ${session.className} ${session.status === 'CANCELLED' ? '(Đã hủy)' : ''}`"
              >
                <div class="flex items-center justify-between gap-1">
                  <span class="truncate">{{ session.startTime }} {{ session.className }}</span>
                  <div class="flex items-center gap-0.5 shrink-0">
                    <XCircle v-if="session.status === 'CANCELLED'" class="w-3 h-3 text-red-600" title="Đã hủy" />
                    <StickyNote v-if="session.managerNote" class="w-3 h-3 text-blue-600" title="Có ghi chú" />
                  </div>
                </div>
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
            <div v-if="selectedDateSessions.length === 0"
              class="text-center py-8 text-gray-500 flex items-center justify-center gap-2">
              <Frown class="w-5 h-5 text-gray-500"/>
              Không có lịch nào trong ngày này
            </div>
            
            <div v-else class="space-y-4">
              <div 
                v-for="session in selectedDateSessions" 
                :key="session.id"
                class="flex gap-4 p-4 rounded-xl border-2 transition-colors bg-white group"
                :class="session.status === 'CANCELLED' ? 'border-red-200 bg-red-50' : 'border-gray-100 hover:border-blue-200'"
              >
                <!-- Time Column -->
                <div class="flex flex-col items-center justify-center w-20 rounded-lg font-bold shrink-0"
                  :class="session.status === 'CANCELLED' ? 'bg-red-100 text-red-700' : 'bg-blue-50 text-blue-700'"
                >
                  <span class="text-lg">{{ session.startTime }}</span>
                  <span class="text-xs font-normal" :class="session.status === 'CANCELLED' ? 'text-red-500' : 'text-blue-500'">đến</span>
                  <span class="text-sm">{{ session.endTime }}</span>
                </div>
                
                <!-- Info Column -->
                <div class="flex-1">
                  <div class="flex items-center gap-2 mb-1">
                    <h4 class="font-bold text-gray-800 text-lg group-hover:text-blue-600 transition-colors">
                      {{ session.className }}
                    </h4>
                    <span v-if="session.status === 'CANCELLED'" class="px-2 py-0.5 bg-red-500 text-white text-xs font-bold rounded">
                      ĐÃ HỦY
                    </span>
                    <span v-else-if="session.status === 'TEACHER_ABSENT'" class="px-2 py-0.5 bg-orange-500 text-white text-xs font-bold rounded">
                      GV VẮNG
                    </span>
                  </div>
                  <div class="flex items-center gap-2 text-sm text-gray-600 mt-1">
                    <MapPin class="w-4 h-4 text-gray-400" />
                    {{ session.roomName }}
                  </div>
                  <div class="flex items-center gap-2 text-sm text-gray-600 mt-1">
                    <User class="w-4 h-4 text-gray-400" />
                    {{ session.teacherName }}
                  </div>
                  
                  <!-- Cancellation Reason -->
                  <div v-if="session.cancellationReason" class="mt-3 p-2 bg-red-50 border-l-4 border-red-400 rounded">
                    <div class="text-xs font-semibold text-red-800">Lý do hủy:</div>
                    <div class="text-sm text-red-700">{{ session.cancellationReason }}</div>
                  </div>
                  
                  <!-- Manager Note -->
                  <div v-if="session.managerNote" class="mt-3 p-2 bg-blue-50 border-l-4 border-blue-400 rounded">
                    <div class="text-xs font-semibold text-blue-800">Ghi chú:</div>
                    <div class="text-sm text-blue-700">{{ session.managerNote }}</div>
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
  XCircle,
  StickyNote,
  Frown
} from 'lucide-vue-next';
import { getCalendarWeeks, formatDate as formatDisplayDate } from './dateUtils.js';
import unifiedApi from './unifiedApi.js';
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

const calendarWeeks = computed(() => {
  return getCalendarWeeks(currentYear.value, currentMonth.value);
});

// Flatten the weeks to get a simple array for the grid, simpler for mobile responsive if needed
const calendarDays = computed(() => {
  return calendarWeeks.value.flat();
});

const selectedDateSessions = computed(() => {
  if (!selectedDate.value) return [];
  const dateStr = selectedDate.value.toISOString().split('T')[0];
  return sessions.value.filter(s => s.date === dateStr).sort((a, b) => {
    return (a.startTime || '00:00').localeCompare(b.startTime || '00:00');
  });
});

const loadSchedule = async () => {
  const start = new Date(currentYear.value, currentMonth.value, 1);
  const end = new Date(currentYear.value, currentMonth.value + 1, 0);
  
  const startStr = start.toISOString().split('T')[0];
  const endStr = end.toISOString().split('T')[0];

  try {
    // Get userId from authStore if not provided or use provided userId
    const userId = props.userId || authStore.user?.id || 0;
    
    let scheduleData = [];
    if (props.role === 'student') {
      scheduleData = await unifiedApi.getStudentSchedule(userId, startStr, endStr);
    } else {
      scheduleData = await unifiedApi.getTeacherSchedule(userId, startStr, endStr);
    }
    
    // Ensure sessions have the correct format
    sessions.value = scheduleData.map(s => {
      // If session already has date and startTime/endTime strings, use them
      if (s.date && s.startTime && s.endTime && typeof s.startTime === 'string' && s.startTime.length <= 5) {
        return s;
      }
      
      // Otherwise, extract from ISO strings
      const startDate = new Date(s.startTime);
      const endDate = new Date(s.endTime);
      return {
        ...s,
        date: startDate.toISOString().split('T')[0],
        startTime: startDate.toTimeString().substring(0, 5),
        endTime: endDate.toTimeString().substring(0, 5)
      };
    });
  } catch (error) {
    console.error('Error loading schedule:', error);
    sessions.value = [];
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
  const dateStr = date.toISOString().split('T')[0];
  return sessions.value.filter(s => s.date === dateStr).sort((a, b) => {
    return (a.startTime || '00:00').localeCompare(b.startTime || '00:00');
  });
};

const openDayDetails = (date) => {
  selectedDate.value = date;
};

const getSessionClass = (session) => {
  if (session.status === 'CANCELLED') {
    return 'bg-red-50 text-red-700 border-red-200 line-through';
  }
  if (session.status === 'TEACHER_ABSENT') {
    return 'bg-orange-50 text-orange-700 border-orange-200';
  }
  return 'bg-blue-50 text-blue-700 border-blue-100';
};

const formatDate = (date) => {
  if (!date) return '';
  return formatDisplayDate(date.toISOString().split('T')[0]);
};
</script>
