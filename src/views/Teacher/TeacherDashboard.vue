<template>
  <div class="min-h-screen bg-gradient-to-br from-green-50 to-blue-50 p-4">
    <div class="max-w-7xl mx-auto">
      <!-- Header -->
      <div class="mb-6">
        <h1 class="text-3xl font-bold text-gray-800 flex items-center gap-3">
          <Calendar class="w-8 h-8 text-green-600" />
          Lịch Dạy Của Tôi Trong Tuần
        </h1>
        <p class="text-gray-600 mt-2">Tuần {{ weekNumber }} - {{ currentYear }}</p>
      </div>

      <!-- Week Calendar Card -->
      <div class="bg-white rounded-2xl shadow-lg border border-gray-100 overflow-hidden">
        <!-- Navigation Header -->
        <div class="p-6 bg-white border-b border-gray-100 flex justify-between items-center">
          <h2 class="text-xl font-bold text-gray-800 flex items-center gap-2">
            <CalendarDays class="w-6 h-6 text-green-600" />
            {{ formatWeekRange(weekDays[0], weekDays[6]) }}
          </h2>
          <div class="flex gap-2">
            <button 
              @click="prevWeek"
              class="p-2 hover:bg-gray-100 rounded-lg transition-colors border border-gray-200"
            >
              <ChevronLeft class="w-5 h-5 text-gray-600" />
            </button>
            <button 
              @click="goToCurrentWeek"
              class="px-4 py-2 hover:bg-gray-100 rounded-lg transition-colors border border-gray-200 text-sm font-medium text-gray-700"
            >
              Hôm nay
            </button>
            <button 
              @click="nextWeek"
              class="p-2 hover:bg-gray-100 rounded-lg transition-colors border border-gray-200"
            >
              <ChevronRight class="w-5 h-5 text-gray-600" />
            </button>
          </div>
        </div>

        <!-- Upcoming Sessions Banner -->
        <div v-if="upcomingSessions.length > 0" class="p-4 bg-green-50/50 border-b border-green-100">
          <h3 class="text-sm font-bold text-green-800 uppercase tracking-wide mb-3 flex items-center gap-2">
            <Clock class="w-4 h-4" />
            Sắp diễn ra
          </h3>
          
          <div class="grid grid-cols-1 md:grid-cols-3 gap-3">
            <div 
              v-for="session in upcomingSessions" 
              :key="session.id"
              class="bg-white p-3 rounded-xl border border-green-200 shadow-sm hover:shadow-md transition-all cursor-pointer group"
              @click="openSessionDetail(session)"
            >
              <div class="flex justify-between items-start mb-2">
                <div class="flex flex-col">
                  <span class="text-xs font-bold text-green-600 bg-green-50 px-2 py-0.5 rounded-md w-fit">
                    {{ formatDate(new Date(session.date)) }}
                  </span>
                  <span class="text-lg font-bold text-gray-800 mt-1">
                    {{ session.startTime }}
                  </span>
                </div>
                <div class="p-1.5 bg-gray-50 rounded-lg group-hover:bg-green-500 group-hover:text-white transition-colors">
                  <ArrowRight class="w-4 h-4 text-gray-400 group-hover:text-white" />
                </div>
              </div>

              <div>
                <div class="text-base font-semibold text-gray-700 truncate" :title="session.className">
                  {{ session.className }}
                </div>
                <div class="flex items-center gap-1.5 text-xs text-gray-500 mt-1">
                  <MapPin class="w-3 h-3" />
                  <span class="truncate">{{ session.roomName || 'Chưa xếp phòng' }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Week Grid -->
        <div class="p-6">
          <div class="grid grid-cols-7 gap-4">
            <div 
              v-for="(day, index) in weekDays" 
              :key="index"
              class="flex flex-col"
            >
              <!-- Day Header -->
              <div 
                class="text-center pb-3 mb-3 border-b-2"
                :class="isToday(day) ? 'border-green-500' : 'border-gray-200'"
              >
                <div class="text-xs font-semibold text-gray-500 uppercase">
                  {{ getDayName(day) }}
                </div>
                <div 
                  class="text-2xl font-bold mt-1"
                  :class="isToday(day) ? 'text-green-600' : 'text-gray-800'"
                >
                  {{ day.getDate() }}
                </div>
                <div class="text-xs text-gray-500">
                  Tháng {{ day.getMonth() + 1 }}
                </div>
              </div>

              <!-- Sessions for this day -->
              <div class="space-y-2 flex-1">
                <div v-if="getSessionsForDate(day).length === 0" class="text-center py-8 text-gray-400">
                  <div class="text-2xl mb-1">😴</div>
                  <div class="text-xs">Nghỉ</div>
                </div>
                
                <div 
                  v-for="session in getSessionsForDate(day)" 
                  :key="session.id"
                  class="bg-gradient-to-br from-green-50 to-blue-50 p-3 rounded-xl border border-green-200 hover:shadow-md transition-all cursor-pointer group"
                  @click="openSessionDetail(session)"
                >
                  <div class="flex items-center gap-2 mb-2">
                    <Clock class="w-4 h-4 text-green-600" />
                    <span class="text-sm font-bold text-gray-800">
                      {{ session.startTime }}
                    </span>
                  </div>
                  
                  <div class="text-sm font-semibold text-gray-700 mb-1 line-clamp-2" :title="session.className">
                    {{ session.className }}
                  </div>
                  
                  <div class="flex items-center gap-1.5 text-xs text-gray-600">
                    <MapPin class="w-3 h-3" />
                    <span class="truncate">{{ session.roomName || 'Chưa xếp phòng' }}</span>
                  </div>

                  <!-- Note indicator -->
                  <div v-if="session.note" class="mt-2 flex items-center gap-1 text-xs text-orange-600">
                    <AlertTriangle class="w-3 h-3" />
                    <span class="truncate">Có thông báo</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Session Detail Modal -->
      <transition
        enter-active-class="transition duration-200 ease-out"
        enter-from-class="opacity-0 scale-95"
        enter-to-class="opacity-100 scale-100"
        leave-active-class="transition duration-150 ease-in"
        leave-from-class="opacity-100 scale-100"
        leave-to-class="opacity-0 scale-95"
      >
        <div 
          v-if="selectedSession"
          class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50"
          @click.self="selectedSession = null"
        >
          <div class="bg-white rounded-2xl w-full max-w-2xl shadow-2xl overflow-hidden">
            <div class="p-6 border-b border-gray-100 flex justify-between items-center bg-gradient-to-r from-green-50 to-blue-50">
              <h3 class="text-xl font-bold text-gray-800 flex items-center gap-2">
                <BookOpen class="w-5 h-5 text-green-600" />
                Chi tiết buổi học
              </h3>
              <button 
                @click="selectedSession = null"
                class="text-gray-400 hover:text-gray-600 transition-colors"
              >
                <X class="w-6 h-6" />
              </button>
            </div>
            
            <div class="p-6">
              <!-- Time Info -->
              <div class="flex items-center gap-4 mb-6 p-4 bg-green-50 rounded-xl">
                <div class="flex flex-col items-center justify-center w-20 bg-green-600 text-white rounded-lg font-bold py-3">
                  <span class="text-lg">{{ selectedSession.startTime }}</span>
                  <span class="text-xs font-normal">đến</span>
                  <span class="text-sm">{{ selectedSession.endTime }}</span>
                </div>
                <div class="flex-1">
                  <div class="text-sm text-gray-600 mb-1">{{ formatDate(new Date(selectedSession.date)) }}</div>
                  <h4 class="font-bold text-gray-800 text-xl">
                    {{ selectedSession.className }}
                  </h4>
                </div>
              </div>

              <!-- Details Grid -->
              <div class="space-y-4">
                <div class="flex items-start gap-3 p-3 bg-gray-50 rounded-lg">
                  <MapPin class="w-5 h-5 text-gray-600 shrink-0 mt-0.5" />
                  <div>
                    <div class="text-sm text-gray-500">Phòng học</div>
                    <div class="font-semibold text-gray-800">{{ selectedSession.roomName || 'Chưa xếp phòng' }}</div>
                  </div>
                </div>

                <div class="flex items-start gap-3 p-3 bg-gray-50 rounded-lg">
                  <Users class="w-5 h-5 text-gray-600 shrink-0 mt-0.5" />
                  <div>
                    <div class="text-sm text-gray-500">Trạng thái</div>
                    <div class="font-semibold text-gray-800">
                      <span 
                        class="px-3 py-1 rounded-full text-sm"
                        :class="getStatusClass(selectedSession.status)"
                      >
                        {{ getStatusText(selectedSession.status) }}
                      </span>
                    </div>
                  </div>
                </div>

                <!-- Note Display -->
                <div v-if="selectedSession.note" class="p-4 bg-orange-50 border border-orange-200 rounded-xl">
                  <div class="flex items-start gap-2">
                    <AlertTriangle class="w-5 h-5 text-orange-600 shrink-0 mt-0.5" />
                    <div>
                      <div class="font-semibold text-orange-800 mb-1">Thông báo thay đổi:</div>
                      <div class="text-orange-700">{{ selectedSession.note }}</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { 
  Calendar,
  CalendarDays,
  ChevronLeft, 
  ChevronRight, 
  X, 
  Clock, 
  MapPin, 
  ArrowRight,
  AlertTriangle,
  BookOpen,
  Users
} from 'lucide-vue-next';
import unifiedApi from '@/services/unifiedClassApi.js';
import { useAuthStore } from '@/stores/useAuthStore';

const authStore = useAuthStore();

// State
const currentWeekStart = ref(getWeekStart(new Date()));
const sessions = ref([]);
const selectedSession = ref(null);

// Helper: Get week start (Monday)
function getWeekStart(date) {
  const d = new Date(date);
  const day = d.getDay();
  const diff = d.getDate() - day + (day === 0 ? -6 : 1); // Adjust when day is Sunday
  return new Date(d.setDate(diff));
}

// Helper: Convert local date to YYYY-MM-DD
const toLocalISOString = (date) => {
  if (!date) return '';
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

// Computed: Week days array (7 days)
const weekDays = computed(() => {
  const days = [];
  for (let i = 0; i < 7; i++) {
    const day = new Date(currentWeekStart.value);
    day.setDate(currentWeekStart.value.getDate() + i);
    days.push(day);
  }
  return days;
});

// Computed: Current year
const currentYear = computed(() => {
  return currentWeekStart.value.getFullYear();
});

// Computed: Week number
const weekNumber = computed(() => {
  const firstDayOfYear = new Date(currentYear.value, 0, 1);
  const pastDaysOfYear = (currentWeekStart.value - firstDayOfYear) / 86400000;
  return Math.ceil((pastDaysOfYear + firstDayOfYear.getDay() + 1) / 7);
});

// Computed: Upcoming sessions (next 3)
const upcomingSessions = computed(() => {
  const now = new Date();
  const futureSessions = sessions.value.filter(s => {
    const sessionTime = new Date(`${s.date}T${s.startTime}`);
    return sessionTime > now && s.status !== 'CANCELLED';
  });

  futureSessions.sort((a, b) => {
    const timeA = new Date(`${a.date}T${a.startTime}`);
    const timeB = new Date(`${b.date}T${b.startTime}`);
    return timeA - timeB;
  });

  return futureSessions.slice(0, 3);
});

// Load schedule
const loadSchedule = async () => {
  const start = weekDays.value[0];
  const end = weekDays.value[6];
  
  const startStr = toLocalISOString(start);
  const endStr = toLocalISOString(end);

  try {
    const userId = authStore.user?.id || 0;
    let scheduleData = await unifiedApi.getTeacherSchedule(userId, startStr, endStr);
    
    // Normalize data
    sessions.value = scheduleData.map(s => {
      // Handle old format
      if (s.date && s.startTime && s.endTime && typeof s.startTime === 'string' && s.startTime.length <= 5) {
        return { ...s };
      }
      
      // Convert UTC to local
      const startDate = new Date(s.startTime);
      const endDate = new Date(s.endTime);
      
      return {
        ...s,
        date: toLocalISOString(startDate),
        startTime: startDate.toTimeString().substring(0, 5),
        endTime: endDate.toTimeString().substring(0, 5)
      };
    });

    // Filter cancelled classes
    if (sessions.value.length > 0) {
      const uniqueFitnessClassIds = [...new Set(sessions.value.map(s => s.fitnessClassId || s.classId).filter(Boolean))];
      
      for (const fitnessClassId of uniqueFitnessClassIds) {
        try {
          const classRegistrations = await unifiedApi.getTeachersByFitnessClass(fitnessClassId);
          const firstReg = classRegistrations[0];
          
          if (firstReg && firstReg.fitnessClass) {
            const classStatus = firstReg.fitnessClass.status;
            if (classStatus === 'cancelled' || classStatus === 'completed') {
              sessions.value = sessions.value.filter(s => 
                (s.fitnessClassId || s.classId) !== fitnessClassId
              );
            }
          }
        } catch (error) {
          console.error(`Error fetching info for class ${fitnessClassId}:`, error);
        }
      }
    }
  } catch (error) {
    console.error('Error loading schedule:', error);
    sessions.value = [];
  }
};

onMounted(() => {
  loadSchedule();
});

watch(currentWeekStart, () => {
  loadSchedule();
});

// Navigation
const prevWeek = () => {
  const newDate = new Date(currentWeekStart.value);
  newDate.setDate(newDate.getDate() - 7);
  currentWeekStart.value = newDate;
};

const nextWeek = () => {
  const newDate = new Date(currentWeekStart.value);
  newDate.setDate(newDate.getDate() + 7);
  currentWeekStart.value = newDate;
};

const goToCurrentWeek = () => {
  currentWeekStart.value = getWeekStart(new Date());
};

// Helpers
const isToday = (date) => {
  const today = new Date();
  return date.getDate() === today.getDate() &&
         date.getMonth() === today.getMonth() &&
         date.getFullYear() === today.getFullYear();
};

const getDayName = (date) => {
  const days = ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'];
  return days[date.getDay()];
};

const getSessionsForDate = (date) => {
  const dateStr = toLocalISOString(date);
  return sessions.value
    .filter(s => s.date === dateStr && s.status !== 'CANCELLED')
    .sort((a, b) => (a.startTime || '00:00').localeCompare(b.startTime || '00:00'));
};

const formatDate = (date) => {
  if (!date) return '';
  const days = ['Chủ nhật', 'Thứ hai', 'Thứ ba', 'Thứ tư', 'Thứ năm', 'Thứ sáu', 'Thứ bảy'];
  return `${days[date.getDay()]}, ${date.getDate()}/${date.getMonth() + 1}/${date.getFullYear()}`;
};

const formatWeekRange = (start, end) => {
  if (!start || !end) return '';
  return `${start.getDate()}/${start.getMonth() + 1} - ${end.getDate()}/${end.getMonth() + 1}/${end.getFullYear()}`;
};

const openSessionDetail = (session) => {
  selectedSession.value = session;
};

const getStatusClass = (status) => {
  switch (status?.toUpperCase()) {
    case 'SCHEDULED':
      return 'bg-blue-100 text-blue-800';
    case 'COMPLETED':
      return 'bg-green-100 text-green-800';
    case 'CANCELLED':
      return 'bg-red-100 text-red-800';
    default:
      return 'bg-gray-100 text-gray-800';
  }
};

const getStatusText = (status) => {
  switch (status?.toUpperCase()) {
    case 'SCHEDULED':
      return 'Đã lên lịch';
    case 'COMPLETED':
      return 'Đã hoàn thành';
    case 'CANCELLED':
      return 'Đã hủy';
    default:
      return status || 'Không xác định';
  }
};
</script>
