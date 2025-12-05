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
                class="text-xs truncate px-1.5 py-0.5 rounded bg-blue-50 text-blue-700 border border-blue-100"
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
                <div class="flex flex-col items-center justify-center w-20 bg-blue-50 rounded-lg text-blue-700 font-bold shrink-0">
                  <span class="text-lg">{{ session.startTime }}</span>
                  <span class="text-xs text-blue-500 font-normal">đến</span>
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
import { getCalendarWeeks, formatDate as formatDisplayDate } from './dateUtils.js';
import mockApi from './mockData.js';

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
  return sessions.value.filter(s => s.date === dateStr).sort((a, b) => a.startTime.localeCompare(b.startTime));
});

const loadSchedule = async () => {
  const start = new Date(currentYear.value, currentMonth.value, 1);
  const end = new Date(currentYear.value, currentMonth.value + 1, 0);
  
  const startStr = start.toISOString().split('T')[0];
  const endStr = end.toISOString().split('T')[0];

  try {
    if (props.role === 'student') {
      sessions.value = await mockApi.getStudentSchedule(props.userId, startStr, endStr);
    } else {
      sessions.value = await mockApi.getTeacherSchedule(props.userId, startStr, endStr);
    }
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
  return sessions.value.filter(s => s.date === dateStr).sort((a, b) => a.startTime.localeCompare(b.startTime));
};

const openDayDetails = (date) => {
  selectedDate.value = date;
};

const formatDate = (date) => {
  if (!date) return '';
  return formatDisplayDate(date.toISOString().split('T')[0]);
};
</script>
