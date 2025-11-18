<script setup>
import { ref, computed, onMounted } from "vue";
import api from "@/services/api";
import { useAuthStore } from "@/stores/useAuthStore";
import { useToast } from "vue-toastification";

const authStore = useAuthStore();
const toast = useToast();

const currentDate = ref(new Date());
const selectedDate = ref(null);
const showModal = ref(false);
const showTodayModal = ref(false);
const selectedTodayEvent = ref(null);
const searchQuery = ref("");

// Schedule data - will be fetched from backend
const schedule = ref([]);

// Helpers
function toDateStr(iso) {
  const d = new Date(iso);
  return (
    d.getFullYear() +
    "-" +
    String(d.getMonth() + 1).padStart(2, "0") +
    "-" +
    String(d.getDate()).padStart(2, "0")
  );
}

function formatTimeRange(startIso, endIso) {
  const s = new Date(startIso);
  const e = new Date(endIso);
  const pad = (n) => String(n).padStart(2, "0");
  return `${pad(s.getHours())}:${pad(s.getMinutes())} - ${pad(
    e.getHours()
  )}:${pad(e.getMinutes())}`;
}

// Function to fetch schedule from backend
async function fetchSchedule(query = "") {
  try {
    let res;
    if (query) {
      // Search by member name
      res = await api.get(`/appointment/member/${query}`);
    } else {
      // Get all appointments
      res = await api.get("/appointment");
    }
    const data = Array.isArray(res.data) ? res.data : [];

    // Filter appointments for the current PT
    const ptAppointments = data.filter(appt => appt.staff.id === authStore.user.id);

    // Map backend appointment shape to the calendar event shape expected by this component
    schedule.value = ptAppointments.map((appt) => {
      const start = appt.startTime;
      const end = appt.endTime;

      return {
        id: appt.id,
        // calendar uses a date string YYYY-MM-DD to group events per day
        date: toDateStr(start),
        // user-facing time range
        time: formatTimeRange(start, end),
        // show member name (fallback to package/member or staff if missing)
        name:
          appt.ptPackageIssued?.member?.fullName ||
          appt.ptPackageIssued?.ptPackage?.name ||
          appt.staff?.fullName ||
          "Appointment",
        // appointments are usually 1 member; keep for display
        members: 1,
        status: appt.status,
        // include some useful details for modal if needed
        member: appt.ptPackageIssued?.member || null,
        staff: appt.staff || null,
        remainingSessions: appt.ptPackageIssued?.remainingSessions ?? null,
        raw: appt,
      };
    });

    console.log("PT Schedule loaded:", schedule.value);
  } catch (error) {
    console.error("Failed to fetch PT schedule:", error);
    schedule.value = [];
    toast.error("Failed to load schedule. Please try again.");
  }
}

// Function to search schedule by member name
async function searchSchedule() {
  await fetchSchedule(searchQuery.value);
}

// Function to start a class
function startClass(eventId) {
  // Update status locally for UI testing (no backend API yet)
  const event = schedule.value.find(e => e.id === eventId);
  if (event) {
    event.status = 'started';
  }
}

// Function to end a class
function endClass(eventId) {
  // Update status locally for UI testing (no backend API yet)
  const event = schedule.value.find(e => e.id === eventId);
  if (event) {
    event.status = 'ended';
  }
}

// Computed properties for calendar
const currentMonth = computed(() => currentDate.value.getMonth());
const currentYear = computed(() => currentDate.value.getFullYear());
const monthName = computed(() => {
  const months = [
    "Month 1",
    "Month 2",
    "Month 3",
    "Month 4",
    "Month 5",
    "Month 6",
    "Month 7",
    "Month 8",
    "Month 9",
    "Month 10",
    "Month 11",
    "Month 12",
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
  return getEventsForDate(new Date()).filter(event => event.status !== 'ended');
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

function selectTodayEvent(event) {
  selectedTodayEvent.value = event;
  showTodayModal.value = true;
}

function closeTodayModal() {
  showTodayModal.value = false;
  selectedTodayEvent.value = null;
}

onMounted(async () => {
  console.log("PT Schedule loaded");
  await fetchSchedule();
});
</script>

<template>
  <div class="p-6 space-y-8">
    <!-- Title and Search -->
    <div class="flex justify-between items-center">
      <h1 class="text-2xl font-bold text-stone-800">PT Schedule</h1>
      <div class="flex items-center space-x-2">
        <input
          v-model="searchQuery"
          type="text"
          placeholder="Search by member name"
          class="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
        <button
          @click="searchSchedule"
          class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
        >
          Search
        </button>
        <button
          @click="fetchSchedule"
          class="px-4 py-2 bg-gray-600 text-white rounded-md hover:bg-gray-700"
        >
          Reset
        </button>
      </div>
    </div>

    <!-- Lịch dạy hôm nay -->
    <section class="bg-white rounded-xl shadow p-4">
      <h2 class="text-lg font-semibold mb-3 text-gray-800">Today's Schedule</h2>
      <div
        v-if="todayEvents.length === 0"
        class="text-center py-4 text-gray-500"
      >
        No classes today
      </div>
      <div v-else class="overflow-x-auto">
        <div class="flex space-x-4 pb-2 min-w-max">
          <div
            v-for="event in todayEvents"
            :key="event.id"
            @click="selectTodayEvent(event)"
            class="flex-shrink-0 w-64 p-3 bg-blue-50 rounded-lg border border-blue-200 cursor-pointer hover:bg-blue-100 transition-colors"
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
              <p>Time: {{ event.time }}</p>
              <p>Number of members: {{ event.members }}</p>
              <p>Status: {{ event.status }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Calendar -->
    <section class="bg-white rounded-xl shadow p-6">
      <!-- Calendar Header -->
      <div class="flex items-center justify-between mb-4">
        <h2 class="text-lg font-semibold">{{ monthName }} {{ currentYear }}</h2>
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
          v-for="day in ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']"
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
              Schedule for
              {{ selectedDate ? selectedDate.toLocaleDateString("en-US") : "" }}
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
            No classes on this day
          </div>

          <div v-else class="space-y-3">
            <div
              v-for="event in selectedDateEvents"
              :key="event.id"
              class="border border-gray-200 rounded-lg p-4 bg-blue-50 shadow-sm"
            >
              <div class="flex items-start">
                <div class="flex-1">
                  <h4 class="font-medium text-gray-900 flex items-center mb-2">
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
                  <div class="grid grid-cols-2 gap-2 text-sm text-gray-600">
                    <div class="flex items-center">
                      <svg
                        class="w-4 h-4 mr-1 text-gray-500"
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
                      {{ event.time }}
                    </div>
                    <div class="flex items-center">
                      <svg
                        class="w-4 h-4 mr-1 text-gray-500"
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
                      {{ event.members }} members
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal for Today's Class Details -->
    <div
      v-if="showTodayModal"
      class="fixed inset-0 flex items-center justify-center z-50"
    >
      <!-- Overlay -->
      <div class="absolute inset-1 backdrop-blur-sm" @click="closeTodayModal"></div>
      <!-- Modal Content -->
      <div
        class="bg-white rounded-xl shadow-xl max-w-md w-full mx-4 max-h-[80vh] overflow-y-auto relative z-10"
        @click.stop
      >
        <div class="p-6" v-if="selectedTodayEvent">
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-lg font-semibold text-gray-900">
              Class Details
            </h3>
            <button
              @click="closeTodayModal"
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

          <div class="space-y-4">
            <div class="border border-gray-200 rounded-lg p-4 bg-blue-50 shadow-sm">
              <div class="flex items-start">
                <div class="flex-1">
                  <h4 class="font-medium text-gray-900 flex items-center mb-2">
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
                    {{ selectedTodayEvent.name }}
                  </h4>
                  <div class="grid grid-cols-2 gap-2 text-sm text-gray-600">
                    <div class="flex items-center">
                      <svg
                        class="w-4 h-4 mr-1 text-gray-500"
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
                      {{ selectedTodayEvent.time }}
                    </div>
                    <div class="flex items-center">
                      <svg
                        class="w-4 h-4 mr-1 text-gray-500"
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
                      {{ selectedTodayEvent.members }} members
                    </div>
                  </div>
                  <div class="mt-2 text-sm text-gray-600">
                    <p>Status: <span class="font-medium">{{ selectedTodayEvent.status }}</span></p>
                  </div>
                </div>
              </div>
            </div>

            <div class="flex space-x-2">
              <button
                v-if="selectedTodayEvent.status !== 'started' && selectedTodayEvent.status !== 'ended'"
                @click="startClass(selectedTodayEvent.id); closeTodayModal()"
                class="flex-1 px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700"
              >
                Start Class
              </button>
              <button
                v-if="selectedTodayEvent.status === 'started'"
                @click="endClass(selectedTodayEvent.id); closeTodayModal()"
                class="flex-1 px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700"
              >
                End Class
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
