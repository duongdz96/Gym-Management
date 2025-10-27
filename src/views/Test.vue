<script setup>
import { ref, computed, onMounted } from 'vue'
import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'

// ✅ CSS sẽ được import trong template

const selectedEvent = ref(null)
const showFollowModal = ref(false)
const showEventDetail = ref(false)
const calendarRef = ref(null)

// Mock data cho các lớp học đã follow - Tháng 10/2025 với nhiều lớp trong cùng ngày
const myFollowedClasses = ref([
  {
    id: 1,
    followDate: '2025-10-20T10:00:00',
    classTemplate: {
      id: 1,
      title: 'Yoga Buổi Sáng',
      classType: 'Yoga',
      difficultyLevel: 'Beginner',
      status: 'active',
      instructor: 'Anna Nguyen',
      room: 'Phòng 101',
      start: '2025-10-27T08:00:00',
      end: '2025-10-27T09:00:00',
      schedulePattern: {
        daysOfWeek: [1, 3, 5],
        timeStart: '08:00',
        timeEnd: '09:00',
        classStartDate: '2025-10-27',
        classEndDate: '2025-12-27'
      }
    },
    member: { id: 1, name: 'Nguyễn Văn A' }
  },
  {
    id: 2,
    followDate: '2025-10-21T14:30:00',
    classTemplate: {
      id: 2,
      title: 'Zumba Năng Động',
      classType: 'Zumba',
      difficultyLevel: 'Intermediate',
      status: 'active',
      instructor: 'David Le',
      room: 'Phòng 202',
      start: '2025-10-27T17:00:00',
      end: '2025-10-27T18:00:00',
      schedulePattern: {
        daysOfWeek: [1, 3, 5],
        timeStart: '17:00',
        timeEnd: '18:00',
        classStartDate: '2025-10-27',
        classEndDate: '2025-12-27'
      }
    },
    member: { id: 1, name: 'Nguyễn Văn A' }
  },
  {
    id: 3,
    followDate: '2025-10-22T09:15:00',
    classTemplate: {
      id: 3,
      title: 'Pilates Chiều Thứ Sáu',
      classType: 'Pilates',
      difficultyLevel: 'Advanced',
      status: 'active',
      instructor: 'Sarah Tran',
      room: 'Phòng 203',
      start: '2025-10-27T16:00:00',
      end: '2025-10-27T17:30:00',
      schedulePattern: {
        daysOfWeek: [1, 3, 5],
        timeStart: '16:00',
        timeEnd: '17:30',
        classStartDate: '2025-10-27',
        classEndDate: '2025-12-27'
      }
    },
    member: { id: 1, name: 'Nguyễn Văn A' }
  },
  {
    id: 4,
    followDate: '2025-10-23T16:45:00',
    classTemplate: {
      id: 4,
      title: 'Kickboxing Buổi Tối',
      classType: 'Kickboxing',
      difficultyLevel: 'Intermediate',
      status: 'active',
      instructor: 'Minh Pham',
      room: 'Phòng 301',
      start: '2025-10-28T19:00:00',
      end: '2025-10-28T20:00:00',
      schedulePattern: {
        daysOfWeek: [2, 4],
        timeStart: '19:00',
        timeEnd: '20:00',
        classStartDate: '2025-10-28',
        classEndDate: '2025-12-28'
      }
    },
    member: { id: 1, name: 'Nguyễn Văn A' }
  },
  {
    id: 5,
    followDate: '2025-10-24T11:20:00',
    classTemplate: {
      id: 5,
      title: 'Cardio Buổi Sáng',
      classType: 'Cardio',
      difficultyLevel: 'Beginner',
      status: 'active',
      instructor: 'Lisa Hoang',
      room: 'Phòng 102',
      start: '2025-10-28T07:00:00',
      end: '2025-10-28T08:00:00',
      schedulePattern: {
        daysOfWeek: [2, 4],
        timeStart: '07:00',
        timeEnd: '08:00',
        classStartDate: '2025-10-28',
        classEndDate: '2025-12-28'
      }
    },
    member: { id: 1, name: 'Nguyễn Văn A' }
  },
  {
    id: 6,
    followDate: '2025-10-25T13:10:00',
    classTemplate: {
      id: 6,
      title: 'Strength Training',
      classType: 'Strength',
      difficultyLevel: 'Advanced',
      status: 'active',
      instructor: 'Mike Chen',
      room: 'Phòng 401',
      start: '2025-10-28T18:00:00',
      end: '2025-10-28T19:30:00',
      schedulePattern: {
        daysOfWeek: [2, 4],
        timeStart: '18:00',
        timeEnd: '19:30',
        classStartDate: '2025-10-28',
        classEndDate: '2025-12-28'
      }
    },
    member: { id: 1, name: 'Nguyễn Văn A' }
  },
  {
    id: 7,
    followDate: '2025-10-26T15:30:00',
    classTemplate: {
      id: 7,
      title: 'HIIT Cường Độ Cao',
      classType: 'HIIT',
      difficultyLevel: 'Advanced',
      status: 'active',
      instructor: 'Alex Nguyen',
      room: 'Phòng 201',
      start: '2025-10-29T20:00:00',
      end: '2025-10-29T21:00:00',
      schedulePattern: {
        daysOfWeek: [1, 3],
        timeStart: '20:00',
        timeEnd: '21:00',
        classStartDate: '2025-10-29',
        classEndDate: '2025-12-29'
      }
    },
    member: { id: 1, name: 'Nguyễn Văn A' }
  },
  {
    id: 8,
    followDate: '2025-10-27T12:00:00',
    classTemplate: {
      id: 8,
      title: 'Aerobics Nhẹ Nhàng',
      classType: 'Aerobics',
      difficultyLevel: 'Beginner',
      status: 'active',
      instructor: 'Emma Tran',
      room: 'Phòng 103',
      start: '2025-10-29T09:00:00',
      end: '2025-10-29T10:00:00',
      schedulePattern: {
        daysOfWeek: [1, 3],
        timeStart: '09:00',
        timeEnd: '10:00',
        classStartDate: '2025-10-29',
        classEndDate: '2025-12-29'
      }
    },
    member: { id: 1, name: 'Nguyễn Văn A' }
  },
  {
    id: 9,
    followDate: '2025-10-28T08:30:00',
    classTemplate: {
      id: 9,
      title: 'Swimming Pool',
      classType: 'Swimming',
      difficultyLevel: 'Intermediate',
      status: 'active',
      instructor: 'John Smith',
      room: 'Hồ bơi',
      start: '2025-10-30T10:00:00',
      end: '2025-10-30T11:00:00',
      schedulePattern: {
        daysOfWeek: [2, 4],
        timeStart: '10:00',
        timeEnd: '11:00',
        classStartDate: '2025-10-30',
        classEndDate: '2025-12-30'
      }
    },
    member: { id: 1, name: 'Nguyễn Văn A' }
  },
  {
    id: 10,
    followDate: '2025-10-29T14:15:00',
    classTemplate: {
      id: 10,
      title: 'Boxing Training',
      classType: 'Boxing',
      difficultyLevel: 'Advanced',
      status: 'active',
      instructor: 'Maria Garcia',
      room: 'Phòng Boxing',
      start: '2025-10-30T18:00:00',
      end: '2025-10-30T19:00:00',
      schedulePattern: {
        daysOfWeek: [2, 4],
        timeStart: '18:00',
        timeEnd: '19:00',
        classStartDate: '2025-10-30',
        classEndDate: '2025-12-30'
      }
    },
    member: { id: 1, name: 'Nguyễn Văn A' }
  }
])

// Tạo events reactive
const calendarEvents = ref([])

// Function để update events - chỉ hiển thị các lớp đã follow
function updateEvents() {
  const events = []
  
  // Chỉ hiển thị các lớp đã follow
  myFollowedClasses.value.forEach(followed => {
    events.push({
      id: `followed-${followed.id}`,
      title: followed.classTemplate.title,
      start: followed.classTemplate.start,
      end: followed.classTemplate.end,
      backgroundColor: getClassColor(followed.classTemplate.classType),
      borderColor: getClassColor(followed.classTemplate.classType),
      textColor: '#ffffff',
      classNames: [`class-${followed.classTemplate.classType.toLowerCase()}`],
      extendedProps: {
        ...followed.classTemplate,
        isFollowed: true,
        followDate: followed.followDate,
        member: followed.member
      }
    })
  })
  
  calendarEvents.value = events
  
  // Update calendar if it exists
  if (calendarRef.value) {
    calendarRef.value.getApi().removeAllEvents()
    calendarRef.value.getApi().addEventSource(events)
  }
}

// Update events khi component mount
onMounted(() => {
  updateEvents()
  // Force calendar to render events
  setTimeout(() => {
    if (calendarRef.value) {
      calendarRef.value.getApi().removeAllEvents()
      calendarRef.value.getApi().addEventSource(calendarEvents.value)
    }
  }, 100)
})

const calendarOptions = {
  plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
  initialView: 'dayGridMonth',
  headerToolbar: {
    left: 'prev,next today',
    center: 'title',
    right: 'dayGridMonth,timeGridWeek,timeGridDay',
  },
  selectable: true,
  editable: false,
  eventClick(info) {
    selectedEvent.value = info.event
    showEventDetail.value = true
  },
  events: [], // Start with empty array
}

function getClassColor(classType) {
  const colors = {
    'Yoga': '#8b5cf6',
    'Zumba': '#f59e0b',
    'Pilates': '#06b6d4',
    'Kickboxing': '#ef4444',
    'Cardio': '#10b981',
    'Strength': '#f97316',
    'Aerobics': '#ec4899',
    'HIIT': '#dc2626',
    'Swimming': '#0ea5e9',
    'Boxing': '#7c3aed'
  }
  return colors[classType] || '#6b7280'
}

function formatDate(date) {
  if (!date) return ''
  return new Date(date).toLocaleString('vi-VN', {
    weekday: 'long',
    hour: '2-digit',
    minute: '2-digit',
    hour12: false,
  })
}

function formatDaysOfWeek(days) {
  const dayNames = ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7']
  return days.map(day => dayNames[day]).join(', ')
}

// Removed follow functions as they are not needed for this view

function testCalendar() {
  if (calendarRef.value) {
    const api = calendarRef.value.getApi()
    console.log('Calendar API:', api)
    console.log('Current view:', api.view.type)
    console.log('Current date:', api.getDate())
    console.log('Events in calendar:', api.getEvents().length)
  }
}

function forceRender() {
  if (calendarRef.value) {
    const api = calendarRef.value.getApi()
    api.removeAllEvents()
    api.addEventSource(calendarEvents.value)
    api.render()
    console.log('Force rendered with', calendarEvents.value.length, 'events')
  }
}
</script>

<template>
  <div class="customer-class-schedule">
    <!-- Header -->
    <div class="schedule-header">
      <h1 class="text-3xl font-bold text-gray-800 mb-2">Lịch Học Của Tôi</h1>
      <p class="text-gray-600">Xem lịch học các lớp đã đăng ký</p>
    </div>

    <!-- Calendar Container -->
    <div class="calendar-container bg-white rounded-lg shadow-lg p-6">
      <FullCalendar ref="calendarRef" :options="calendarOptions" />
    </div>
    
    <!-- Debug Info -->
    <div class="mt-4 p-4 bg-yellow-100 rounded-lg">
      <h4 class="font-semibold text-yellow-800">Debug Info:</h4>
      <p class="text-sm text-yellow-700">Số lượng events: {{ calendarEvents.length }}</p>
      <p class="text-sm text-yellow-700">Số lượng lớp đã follow: {{ myFollowedClasses.length }}</p>
      <p class="text-sm text-yellow-700">Calendar API: {{ calendarRef ? 'OK' : 'NULL' }}</p>
      <div class="mt-2 space-x-2">
        <button @click="updateEvents" class="px-3 py-1 bg-yellow-600 text-white rounded text-sm">
          Refresh Events
        </button>
        <button @click="testCalendar" class="px-3 py-1 bg-blue-600 text-white rounded text-sm">
          Test Calendar
        </button>
        <button @click="forceRender" class="px-3 py-1 bg-green-600 text-white rounded text-sm">
          Force Render
        </button>
      </div>
    </div>

    <!-- Event Detail Modal -->
    <div v-if="showEventDetail" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 max-w-md w-full mx-4">
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-xl font-semibold text-gray-800">Chi Tiết Lớp Học</h3>
          <button @click="showEventDetail = false" class="text-gray-500 hover:text-gray-700">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
          </button>
        </div>

        <div v-if="selectedEvent" class="space-y-4">
          <!-- Class Info -->
          <div class="bg-gray-50 rounded-lg p-4">
            <h4 class="font-semibold text-lg text-gray-800 mb-2">{{ selectedEvent.title }}</h4>
            <div class="grid grid-cols-2 gap-3 text-sm">
              <div>
                <span class="font-medium text-gray-600">Loại lớp:</span>
                <span class="ml-2 px-2 py-1 bg-blue-100 text-blue-800 rounded-full text-xs">
                  {{ selectedEvent.extendedProps.classType }}
                </span>
              </div>
              <div>
                <span class="font-medium text-gray-600">Độ khó:</span>
                <span class="ml-2 px-2 py-1 bg-green-100 text-green-800 rounded-full text-xs">
                  {{ selectedEvent.extendedProps.difficultyLevel }}
                </span>
              </div>
              <div>
                <span class="font-medium text-gray-600">Giáo viên:</span>
                <span class="ml-2">{{ selectedEvent.extendedProps.instructor }}</span>
              </div>
              <div>
                <span class="font-medium text-gray-600">Phòng:</span>
                <span class="ml-2">{{ selectedEvent.extendedProps.room }}</span>
              </div>
            </div>
          </div>

          <!-- Schedule Info -->
          <div class="bg-blue-50 rounded-lg p-4">
            <h5 class="font-semibold text-gray-800 mb-2">Thông Tin Lịch Học</h5>
            <div class="space-y-2 text-sm">
              <div>
                <span class="font-medium text-gray-600">Thời gian:</span>
                <span class="ml-2">{{ formatDate(selectedEvent.start) }}</span>
              </div>
              <div>
                <span class="font-medium text-gray-600">Ngày học:</span>
                <span class="ml-2">{{ formatDaysOfWeek(selectedEvent.extendedProps.schedulePattern.daysOfWeek) }}</span>
              </div>
              <div>
                <span class="font-medium text-gray-600">Giờ học:</span>
                <span class="ml-2">{{ selectedEvent.extendedProps.schedulePattern.timeStart }} - {{ selectedEvent.extendedProps.schedulePattern.timeEnd }}</span>
              </div>
              <div>
                <span class="font-medium text-gray-600">Khoá học:</span>
                <span class="ml-2">{{ selectedEvent.extendedProps.schedulePattern.classStartDate }} đến {{ selectedEvent.extendedProps.schedulePattern.classEndDate }}</span>
              </div>
            </div>
          </div>

          <!-- Action Buttons -->
          <div class="flex gap-3 pt-4">
            <button 
              @click="showEventDetail = false"
              class="flex-1 bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded-lg transition-colors"
            >
              Đóng
            </button>
          </div>
        </div>
      </div>
    </div>


    <!-- Legend -->
    <div class="mt-6 bg-white rounded-lg shadow-lg p-4">
      <h3 class="font-semibold text-gray-800 mb-3">Chú Thích Màu Sắc</h3>
      <div class="grid grid-cols-2 md:grid-cols-5 gap-3">
        <div class="flex items-center">
          <div class="w-4 h-4 rounded mr-2" style="background-color: #8b5cf6;"></div>
          <span class="text-sm text-gray-600">Yoga</span>
        </div>
        <div class="flex items-center">
          <div class="w-4 h-4 rounded mr-2" style="background-color: #f59e0b;"></div>
          <span class="text-sm text-gray-600">Zumba</span>
        </div>
        <div class="flex items-center">
          <div class="w-4 h-4 rounded mr-2" style="background-color: #06b6d4;"></div>
          <span class="text-sm text-gray-600">Pilates</span>
        </div>
        <div class="flex items-center">
          <div class="w-4 h-4 rounded mr-2" style="background-color: #ef4444;"></div>
          <span class="text-sm text-gray-600">Kickboxing</span>
        </div>
        <div class="flex items-center">
          <div class="w-4 h-4 rounded mr-2" style="background-color: #10b981;"></div>
          <span class="text-sm text-gray-600">Cardio</span>
        </div>
        <div class="flex items-center">
          <div class="w-4 h-4 rounded mr-2" style="background-color: #f97316;"></div>
          <span class="text-sm text-gray-600">Strength</span>
        </div>
        <div class="flex items-center">
          <div class="w-4 h-4 rounded mr-2" style="background-color: #ec4899;"></div>
          <span class="text-sm text-gray-600">Aerobics</span>
        </div>
        <div class="flex items-center">
          <div class="w-4 h-4 rounded mr-2" style="background-color: #dc2626;"></div>
          <span class="text-sm text-gray-600">HIIT</span>
        </div>
        <div class="flex items-center">
          <div class="w-4 h-4 rounded mr-2" style="background-color: #0ea5e9;"></div>
          <span class="text-sm text-gray-600">Swimming</span>
        </div>
        <div class="flex items-center">
          <div class="w-4 h-4 rounded mr-2" style="background-color: #7c3aed;"></div>
          <span class="text-sm text-gray-600">Boxing</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.customer-class-schedule {
  padding: 2rem;
  background-color: #f8fafc;
  min-height: 100vh;
}

.calendar-container {
  margin-top: 2rem;
}

/* Custom FullCalendar styles */
:deep(.fc-event) {
  border-radius: 8px !important;
  border: none !important;
  padding: 4px 8px !important;
  font-size: 0.8rem !important;
  font-weight: 600 !important;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1) !important;
  transition: all 0.2s ease !important;
  overflow: hidden !important;
  background-color: inherit !important;
}

:deep(.fc-event:hover) {
  transform: translateY(-1px) !important;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15) !important;
}

:deep(.fc-event-title) {
  font-weight: 700 !important;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3) !important;
  white-space: nowrap !important;
  overflow: hidden !important;
  text-overflow: ellipsis !important;
  max-width: 100% !important;
  color: white !important;
}

:deep(.fc-daygrid-event) {
  margin: 2px 0 !important;
}

:deep(.fc-timegrid-event) {
  border-radius: 6px !important;
}

:deep(.fc-event-main) {
  padding: 2px 4px !important;
  overflow: hidden !important;
}

:deep(.fc-event-time) {
  font-size: 0.7rem !important;
  opacity: 0.9 !important;
  color: white !important;
}

/* Force background colors for specific event types */
:deep(.fc-event.class-yoga) {
  background-color: #8b5cf6 !important;
}

:deep(.fc-event.class-zumba) {
  background-color: #f59e0b !important;
}

:deep(.fc-event.class-pilates) {
  background-color: #06b6d4 !important;
}

:deep(.fc-event.class-kickboxing) {
  background-color: #ef4444 !important;
}

:deep(.fc-event.class-cardio) {
  background-color: #10b981 !important;
}

:deep(.fc-event.class-strength) {
  background-color: #f97316 !important;
}

:deep(.fc-event.class-aerobics) {
  background-color: #ec4899 !important;
}

:deep(.fc-event.class-hiit) {
  background-color: #dc2626 !important;
}

:deep(.fc-event.class-swimming) {
  background-color: #0ea5e9 !important;
}

:deep(.fc-event.class-boxing) {
  background-color: #7c3aed !important;
}

:deep(.fc-button-primary) {
  background-color: #3b82f6;
  border-color: #3b82f6;
}

:deep(.fc-button-primary:hover) {
  background-color: #2563eb;
  border-color: #2563eb;
}

:deep(.fc-button-primary:focus) {
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.5);
}

:deep(.fc-today) {
  background-color: #eff6ff;
}

/* Responsive */
@media (max-width: 768px) {
  .customer-class-schedule {
    padding: 1rem;
  }
  
  .schedule-header h1 {
    font-size: 1.5rem;
  }
  
  .calendar-container {
    padding: 1rem;
  }
}
</style>
