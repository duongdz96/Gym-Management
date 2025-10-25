<script setup>
import { ref, computed, onMounted } from "vue";

const currentDate = ref(new Date());
const selectedDate = ref(null);
const showModal = ref(false);
const selectedEvent = ref(null);

// Schedule data - will be fetched from backend
const schedule = ref([]);

// Function to fetch schedule from backend
async function fetchSchedule() {
  try {
    // TODO: Replace with actual API call
    // const response = await fetch('/api/pt/schedule');
    // schedule.value = await response.json();

    // Sample data for now
    schedule.value = [
      {
        id: 1,
        name: "Lớp Yoga buổi sáng",
        date: "2025-10-17",
        time: "07:00 - 08:00",
        members: 15,
      },
      {
        id: 2,
        name: "Body Pump",
        date: "2025-10-18",
        time: "18:00 - 19:00",
        members: 12,
      },
      {
        id: 3,
        name: "Personal Training",
        date: "2025-10-19",
        time: "10:00 - 11:00",
        members: 1,
      },
      {
        id: 10,
        name: "Zumba",
        date: "2025-10-19",
        time: "11:00 - 12:00",
        members: 25,
      },
      {
        id: 11,
        name: "Kickboxing",
        date: "2025-10-19",
        time: "14:00 - 15:00",
        members: 18,
      },
      {
        id: 12,
        name: "Spinning",
        date: "2025-10-19",
        time: "15:30 - 16:30",
        members: 20,
      },
      {
        id: 13,
        name: "Aerobic",
        date: "2025-10-18",
        time: "17:00 - 18:00",
        members: 22,
      },
      {
        id: 14,
        name: "Morning Run",
        date: "2025-10-18",
        time: "05:30 - 06:30",
        members: 8,
      },
      {
        id: 15,
        name: "Weight Lifting",
        date: "2025-10-18",
        time: "12:00 - 13:00",
        members: 16,
      },
      {
        id: 16,
        name: "Tai Chi",
        date: "2025-10-21",
        time: "16:00 - 17:00",
        members: 10,
      },
      {
        id: 4,
        name: "Yoga buổi tối",
        date: "2025-10-21",
        time: "19:00 - 20:00",
        members: 10,
      },
      {
        id: 5,
        name: "Pilates",
        date: "2025-10-21",
        time: "09:00 - 10:00",
        members: 8,
      },
      {
        id: 6,
        name: "Boxing",
        date: "2025-10-21",
        time: "14:00 - 15:00",
        members: 8,
      },
      {
        id: 7,
        name: "Dance Fitness",
        date: "2025-10-17",
        time: "16:00 - 17:00",
        members: 20,
      },
      {
        id: 8,
        name: "CrossFit",
        date: "2025-10-17",
        time: "17:30 - 18:30",
        members: 15,
      },
      {
        id: 9,
        name: "Swimming",
        date: "2025-10-17",
        time: "18:30 - 19:30",
        members: 12,
      },
    ];
  } catch (error) {
    console.error("Failed to fetch schedule:", error);
  }
}

// Computed properties for calendar
const currentMonth = computed(() => currentDate.value.getMonth());
const currentYear = computed(() => currentDate.value.getFullYear());
const monthName = computed(() => {
  const months = [
    "Tháng 1",
    "Tháng 2",
    "Tháng 3",
    "Tháng 4",
    "Tháng 5",
    "Tháng 6",
    "Tháng 7",
    "Tháng 8",
    "Tháng 9",
    "Tháng 10",
    "Tháng 11",
    "Tháng 12",
  ];
  return months[currentMonth.value];
});

const calendarDays = computed(() => {
  const firstDay = new Date(currentYear.value, currentMonth.value, 1);
  const lastDay = new Date(currentYear.value, currentMonth.value + 1, 0);
  const startDate = new Date(firstDay);
  startDate.setDate(startDate.getDate() - firstDay.getDay());

  const days = [];
  const current = new Date(startDate);

  for (let i = 0; i < 42; i++) {
    const dayData = {
      date: new Date(current),
      day: current.getDate(),
      isCurrentMonth: current.getMonth() === currentMonth.value,
      isToday: current.toDateString() === new Date().toDateString(),
      events: getEventsForDate(current),
    };
    days.push(dayData);
    current.setDate(current.getDate() + 1);
  }

  return days;
});

const selectedDateEvents = computed(() => {
  if (!selectedDate.value) return [];
  return getEventsForDate(selectedDate.value);
});

const todayEvents = computed(() => {
  return getEventsForDate(new Date());
});

// Functions
function getEventsForDate(date) {
  const dateStr =
    date.getFullYear() +
    "-" +
    String(date.getMonth() + 1).padStart(2, "0") +
    "-" +
    String(date.getDate()).padStart(2, "0");
  return schedule.value.filter((item) => item.date === dateStr);
}

function previousMonth() {
  currentDate.value = new Date(currentYear.value, currentMonth.value - 1, 1);
}

function nextMonth() {
  currentDate.value = new Date(currentYear.value, currentMonth.value + 1, 1);
}

function selectDate(dayData) {
  selectedDate.value = dayData.date;
  showModal.value = true;
}

function closeModal() {
  showModal.value = false;
  selectedDate.value = null;
}

function selectEvent(event) {
  selectedEvent.value = event;
  showModal.value = false; // Close day modal
}

onMounted(async () => {
  console.log("PT Schedule loaded");
  await fetchSchedule();
});
</script>

<template>
  <div class="p-6 space-y-8">
    <!-- Tiêu đề -->
    <h1 class="text-2xl font-bold text-stone-800">Lịch Học PT</h1>

    <!-- Main Layout -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- Chi tiết ca dạy bên trái -->
      <section class="bg-white rounded-xl shadow p-4 lg:col-span-1">
        <h2 class="text-lg font-semibold mb-3 text-gray-800">
          Thông Tin Chi Tiết Ca Dạy
        </h2>
        <div v-if="!selectedEvent" class="text-center py-8">
          <svg
            class="w-12 h-12 mx-auto text-gray-300 mb-3"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
            ></path>
          </svg>
          <p class="text-sm text-gray-500">Chọn một ca dạy để xem chi tiết</p>
        </div>
        <div v-else class="space-y-3">
          <div
            class="bg-gradient-to-r from-blue-50 to-indigo-50 p-3 rounded-lg"
          >
            <h3 class="text-base font-semibold text-gray-900 mb-2">
              {{ selectedEvent.name }}
            </h3>
            <div class="grid grid-cols-1 gap-2 text-xs">
              <div class="flex items-center">
                <svg
                  class="w-4 h-4 mr-2 text-gray-500"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
                  ></path>
                </svg>
                <span class="font-medium">Ngày:</span>
                <span class="ml-1">{{
                  new Date(selectedEvent.date).toLocaleDateString("vi-VN")
                }}</span>
              </div>
              <div class="flex items-center">
                <svg
                  class="w-4 h-4 mr-2 text-gray-500"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"
                  ></path>
                </svg>
                <span class="font-medium">Thời gian:</span>
                <span class="ml-1">{{ selectedEvent.time }}</span>
              </div>
              <div class="flex items-center">
                <svg
                  class="w-4 h-4 mr-2 text-gray-500"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"
                  ></path>
                </svg>
                <span class="font-medium">Số hội viên:</span>
                <span class="ml-1">{{ selectedEvent.members }}</span>
              </div>
            </div>
          </div>
          <div class="text-center">
            <button
              @click="selectedEvent = null"
              class="bg-red-500 hover:bg-red-600 text-white px-4 py-1.5 rounded-lg transition-colors font-medium text-sm"
            >
              Đóng
            </button>
          </div>
        </div>
      </section>

      <!-- Bên phải: Lịch hôm nay và Calendar -->
      <div class="lg:col-span-2 space-y-6">
        <!-- Lịch dạy hôm nay -->
        <section class="bg-white rounded-xl shadow p-4">
          <h2 class="text-lg font-semibold mb-3 text-gray-800">
            Lịch dạy hôm nay
          </h2>
          <div
            v-if="todayEvents.length === 0"
            class="text-center py-4 text-gray-500"
          >
            Không có lớp học nào hôm nay
          </div>
          <div v-else class="overflow-x-auto">
            <div class="flex space-x-4 pb-2 min-w-max">
              <div
                v-for="event in todayEvents"
                :key="event.id"
                class="flex-shrink-0 w-64 p-3 bg-blue-50 rounded-lg border border-blue-200"
              >
                <div class="flex items-center justify-between mb-2">
                  <div class="flex items-center">
                    <svg
                      class="w-4 h-4 mr-2 text-blue-500"
                      fill="currentColor"
                      viewBox="0 0 20 20"
                    >
                      <path
                        fill-rule="evenodd"
                        d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-12a1 1 0 10-2 0v4a1 1 0 00.293.707l2.828 2.829a1 1 0 101.415-1.415L11 9.586V6z"
                        clip-rule="evenodd"
                      ></path>
                    </svg>
                    <span class="font-medium text-gray-900 text-sm">{{
                      event.name
                    }}</span>
                  </div>
                </div>
                <div class="text-xs text-gray-600">
                  <p>Thời gian: {{ event.time }}</p>
                  <p>Số hội viên: {{ event.members }}</p>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- Calendar -->
        <section class="bg-white rounded-xl shadow p-3">
          <!-- Calendar Header -->
          <div class="flex items-center justify-between mb-4">
            <h2 class="text-lg font-semibold">
              {{ monthName }} {{ currentYear }}
            </h2>
            <div class="flex gap-2">
              <button
                @click="previousMonth"
                class="p-2 hover:bg-gray-100 rounded-lg transition-colors"
              >
                <svg
                  class="w-5 h-5"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M15 19l-7-7 7-7"
                  ></path>
                </svg>
              </button>
              <button
                @click="nextMonth"
                class="p-2 hover:bg-gray-100 rounded-lg transition-colors"
              >
                <svg
                  class="w-5 h-5"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M9 5l7 7-7 7"
                  ></path>
                </svg>
              </button>
            </div>
          </div>

          <!-- Calendar Grid -->
          <div class="grid grid-cols-7 gap-1">
            <!-- Day Headers -->
            <div
              v-for="day in ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7']"
              :key="day"
              class="p-2 text-center font-medium text-gray-500 text-sm"
            >
              {{ day }}
            </div>

            <!-- Calendar Days -->
            <div
              v-for="dayData in calendarDays"
              :key="dayData.date.toISOString()"
              @click="dayData.events.length > 0 && selectDate(dayData)"
              :class="[
                'min-h-[60px] p-1 transition-colors relative hover:bg-gray-50 text-xs',
                dayData.events.length > 0 ? 'cursor-pointer' : '',
                !dayData.isCurrentMonth
                  ? 'text-gray-400 bg-gray-50'
                  : 'text-gray-900',
                dayData.isToday ? 'bg-red-100' : '',
              ]"
            >
              <div class="font-medium mb-1">{{ dayData.day }}</div>
              <!-- Event indicators -->
              <div
                v-if="dayData.events.length > 0"
                class="flex justify-center space-x-1"
              >
                <div
                  v-for="event in dayData.events"
                  :key="event.id"
                  class="w-1 h-1 bg-blue-500 rounded-full"
                ></div>
              </div>
            </div>
          </div>
        </section>
      </div>
    </div>

    <!-- Modal for Day Details -->
    <div
      v-if="showModal"
      class="fixed inset-0 flex items-center justify-center z-50"
    >
      <!-- Overlay -->
      <div class="absolute inset-1 backdrop-blur-sm" @click="closeModal"></div>
      <!-- Modal Content -->
      <div
        class="bg-white rounded-xl shadow-xl max-w-md w-full mx-4 max-h-[80vh] overflow-y-auto relative z-10"
        @click.stop
      >
        <div class="p-6">
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-lg font-semibold text-gray-900">
              Lịch học ngày
              {{ selectedDate ? selectedDate.toLocaleDateString("vi-VN") : "" }}
            </h3>
            <button
              @click="closeModal"
              class="text-gray-400 hover:text-gray-600"
            >
              <svg
                class="w-6 h-6"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M6 18L18 6M6 6l12 12"
                ></path>
              </svg>
            </button>
          </div>

          <div
            v-if="selectedDateEvents.length === 0"
            class="text-center py-8 text-gray-500"
          >
            Không có lớp học nào trong ngày này
          </div>

          <div v-else class="space-y-3">
            <div
              v-for="event in selectedDateEvents"
              :key="event.id"
              @click="selectEvent(event)"
              class="border border-gray-200 rounded-lg p-4 hover:bg-blue-50 hover:border-blue-300 transition-all cursor-pointer shadow-sm hover:shadow-md"
            >
              <div class="flex justify-between items-start">
                <div class="flex-1">
                  <h4 class="font-medium text-gray-900 flex items-center">
                    <svg
                      class="w-4 h-4 mr-2 text-blue-500"
                      fill="currentColor"
                      viewBox="0 0 20 20"
                    >
                      <path
                        fill-rule="evenodd"
                        d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-12a1 1 0 10-2 0v4a1 1 0 00.293.707l2.828 2.829a1 1 0 101.415-1.415L11 9.586V6z"
                        clip-rule="evenodd"
                      ></path>
                    </svg>
                    {{ event.name }}
                  </h4>
                  <p class="text-sm text-gray-600">
                    Thời gian: {{ event.time }}
                  </p>
                  <p class="text-sm text-gray-600">
                    Số hội viên: {{ event.members }}
                  </p>
                </div>
                <svg
                  class="w-5 h-5 text-gray-400"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M9 5l7 7-7 7"
                  ></path>
                </svg>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal for Day Details -->
    <div
      v-if="showModal"
      class="fixed inset-0 flex items-center justify-center z-50"
    >
      <!-- Overlay -->
      <div class="absolute inset-0 backdrop-blur-sm" @click="closeModal"></div>
      <!-- Modal Content -->
      <div
        class="bg-white rounded-xl shadow-xl max-w-md w-full mx-4 max-h-[80vh] overflow-y-auto relative z-10"
        @click.stop
      >
        <div class="p-6">
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-lg font-semibold text-gray-900">
              Lịch học ngày
              {{ selectedDate ? selectedDate.toLocaleDateString("vi-VN") : "" }}
            </h3>
            <button
              @click="closeModal"
              class="text-gray-400 hover:text-gray-600"
            >
              <svg
                class="w-6 h-6"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M6 18L18 6M6 6l12 12"
                ></path>
              </svg>
            </button>
          </div>

          <div
            v-if="selectedDateEvents.length === 0"
            class="text-center py-8 text-gray-500"
          >
            Không có lớp học nào trong ngày này
          </div>

          <div v-else class="space-y-3">
            <div
              v-for="event in selectedDateEvents"
              :key="event.id"
              @click="selectEvent(event)"
              class="border border-gray-200 rounded-lg p-4 hover:bg-blue-50 hover:border-blue-300 transition-all cursor-pointer shadow-sm hover:shadow-md"
            >
              <div class="flex justify-between items-start">
                <div class="flex-1">
                  <h4 class="font-medium text-gray-900 flex items-center">
                    <svg
                      class="w-4 h-4 mr-2 text-blue-500"
                      fill="currentColor"
                      viewBox="0 0 20 20"
                    >
                      <path
                        fill-rule="evenodd"
                        d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-12a1 1 0 10-2 0v4a1 1 0 00.293.707l2.828 2.829a1 1 0 101.415-1.415L11 9.586V6z"
                        clip-rule="evenodd"
                      ></path>
                    </svg>
                    {{ event.name }}
                  </h4>
                  <p class="text-sm text-gray-600">
                    Thời gian: {{ event.time }}
                  </p>
                  <p class="text-sm text-gray-600">
                    Số hội viên: {{ event.members }}
                  </p>
                </div>
                <svg
                  class="w-5 h-5 text-gray-400"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M9 5l7 7-7 7"
                  ></path>
                </svg>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
