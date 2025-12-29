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

// New class registration modal
const showRegisterModal = ref(false);
const registerDate = ref(null);
const memberSearchQuery = ref("");
const selectedMember = ref(null);
const selectedStartTime = ref("");
const selectedEndTime = ref("");
const availableMembers = ref([]);

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
    const ptAppointments = data.filter(appt => appt.pt && appt.pt.id === authStore.user.id);

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
        // show member name (fallback to package/member or pt if missing)
        name:
          appt.ptPackageIssued?.member?.fullName ||
          appt.ptPackageIssued?.ptPackage?.name ||
          appt.pt?.fullName ||
          "Appointment",
        // appointments are usually 1 member; keep for display
        members: 1,
        status: appt.status,
        // include some useful details for modal if needed
        member: appt.ptPackageIssued?.member || null,
        pt: appt.pt || null,
        remainingSessions: appt.ptPackageIssued?.remainingSessions ?? null,
        raw: appt,
      };
    });

    console.log("PT Schedule loaded:", schedule.value);
  } catch (error) {
    console.error("Failed to fetch PT schedule:", error);
    schedule.value = [];
    toast.error("Không thể tải lịch. Vui lòng thử lại sau");
  }
}

// Function to search schedule by member name
async function searchSchedule() {
  await fetchSchedule(searchQuery.value);
}

// Function to load available members for registration
async function loadAvailableMembers() {
  try {
    const res = await api.get("/studentprofile");
    const data = Array.isArray(res.data) ? res.data : [];
    // Filter profiles for the current PT
    const ptProfiles = data.filter(profile => profile.pt && profile.pt.id === authStore.user.id);
    // Map to member objects
    availableMembers.value = ptProfiles.map(profile => ({
      id: profile.id,
      memberId: profile.member.id,
      name: profile.member.fullName,
      email: profile.member.email,
      height: profile.height,
      weight: profile.weight,
      trainingPlan: profile.trainingPlan,
    }));
    console.log("Available members loaded:", availableMembers.value);
  } catch (error) {
    console.error('Failed to load available members:', error);
    availableMembers.value = [];
    toast.error("Không thể tải thành viên. Vui lòng thử lại sau!");
  }
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

const filteredMembers = computed(() => {
  if (!memberSearchQuery.value) {
    return availableMembers.value;
  }
  return availableMembers.value.filter(member =>
    member.name.toLowerCase().includes(memberSearchQuery.value.toLowerCase()) ||
    member.email.toLowerCase().includes(memberSearchQuery.value.toLowerCase())
  );
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
  if (dayData.events.length > 0) {
    // Show existing events modal
    showModal.value = true;
  } else {
    // Show register new class modal
    registerDate.value = dayData.date;
    showRegisterModal.value = true;
    // Load available members
    loadAvailableMembers();
  }
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

// Register modal functions
function openRegisterModalForDate() {
  registerDate.value = selectedDate.value;
  showRegisterModal.value = true;
  showModal.value = false; // Close the current modal
  // Load available members
  loadAvailableMembers();
}

function closeRegisterModal() {
  showRegisterModal.value = false;
  registerDate.value = null;
  memberSearchQuery.value = "";
  selectedMember.value = null;
  selectedStartTime.value = "";
  selectedEndTime.value = "";
}

function selectMemberForRegistration(member) {
  selectedMember.value = member;
}

function registerNewClass() {
  if (!selectedMember.value || !selectedStartTime.value || !selectedEndTime.value) {
    toast.error("Vui lòng điền đầy đủ thông tin!");
    return;
  }

  // Here you would call the API to register the new class
  // For now, we'll just show a success message and close the modal
  toast.success(`Đã đăng ký lớp cho ${selectedMember.value.name} vào ${registerDate.value.toLocaleDateString('vi-VN')} từ ${selectedStartTime.value} đến ${selectedEndTime.value}`);

  // Close modal and refresh schedule
  closeRegisterModal();
  fetchSchedule();
}

onMounted(async () => {
  console.log("PT Schedule loaded");
  await fetchSchedule();
});
</script>

<template>
  <div class="p-6 space-y-8">
    <!-- Title and Search -->
    <div class="flex flex-col sm:flex-row sm:justify-between sm:items-center gap-4">
      <h1 class="text-xl sm:text-2xl font-bold text-stone-800">Lịch trình & Đăng ký lớp</h1>
      <div class="flex flex-col sm:flex-row items-stretch sm:items-center space-y-2 sm:space-y-0 sm:space-x-2">
        <input
          v-model="searchQuery"
          type="text"
          placeholder="Tìm kiếm theo tên học viên"
          class="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-emerald-500"
        />
        <button
          @click="searchSchedule"
          class="px-4 py-2 bg-emerald-600 text-white rounded-md hover:bg-emerald-700"
        >
          Tìm kiếm
        </button>
        <button
          @click="fetchSchedule"
          class="px-4 py-2 bg-gray-600 text-white rounded-md hover:bg-gray-700"
        >
          Đặt lại
        </button>
      </div>
    </div>

    <!-- Lịch dạy hôm nay -->
    <section class="bg-white rounded-xl shadow p-4">
      <h2 class="text-lg font-semibold mb-3 text-gray-800">Lịch hôm nay</h2>
      <div
        v-if="todayEvents.length === 0"
        class="text-center py-4 text-gray-500"
      >
        Không có lớp nào hôm nay
      </div>
      <div v-else class="overflow-x-auto">
        <div class="flex space-x-4 pb-2 min-w-max sm:min-w-0">
          <div
            v-for="event in todayEvents"
            :key="event.id"
            @click="selectTodayEvent(event)"
            class="flex-shrink-0 w-64 p-3 bg-emerald-50 rounded-lg border border-emerald-200 cursor-pointer hover:bg-emerald-100 transition-colors"
          >
            <div class="flex items-center justify-between mb-2">
              <div class="flex items-center">
                <svg
                  class="w-4 h-4 mr-2 text-emerald-500"
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
    <div class="bg-white rounded-2xl shadow-lg border border-gray-100 overflow-hidden">
      <!-- Header -->
      <div class="p-4 sm:p-6 bg-white border-b border-gray-100 flex flex-col sm:flex-row sm:justify-between sm:items-center gap-4">
        <h2 class="text-lg sm:text-xl font-bold text-gray-800 flex items-center gap-2">
          <svg class="w-5 h-5 sm:w-6 sm:h-6 text-emerald-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
          </svg>
          Lịch Dạy - Tháng {{ currentMonth + 1 }}/{{ currentYear }}
        </h2>
        <div class="flex gap-2 justify-center sm:justify-end">
          <button
            @click="previousMonth"
            class="p-2 hover:bg-gray-100 rounded-lg transition-colors border border-gray-200"
          >
            <svg class="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
            </svg>
          </button>
          <button
            @click="nextMonth"
            class="p-2 hover:bg-gray-100 rounded-lg transition-colors border border-gray-200"
          >
            <svg class="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
            </svg>
          </button>
        </div>
      </div>

      <!-- Calendar Grid -->
      <div class="p-4 overflow-x-auto">
        <!-- Days Header -->
        <div class="grid grid-cols-7 mb-2 min-w-[700px]">
          <div
            v-for="day in ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7']"
            :key="day"
            class="text-center font-semibold text-gray-500 py-2 text-sm uppercase tracking-wider"
          >
            {{ day }}
          </div>
        </div>

        <!-- Days Grid -->
        <div class="grid grid-cols-7 gap-2 min-w-[700px]">
          <div
            v-for="dayData in calendarDays"
            :key="dayData.date.toISOString()"
            class="min-h-[100px] border rounded-xl p-2 transition-all relative group"
            :class="[
              !dayData.isCurrentMonth ? 'bg-gray-50/50 border-transparent' : 'bg-white border-gray-100 hover:border-emerald-300 hover:shadow-md cursor-pointer',
              dayData.isToday ? 'ring-2 ring-emerald-500 ring-offset-1' : '',
              dayData.events.length > 0 ? '' : ''
            ]"
            @click="selectDate(dayData)"
          >
            <template v-if="dayData.isCurrentMonth">
              <span
                class="text-sm font-medium block mb-1"
                :class="dayData.isToday ? 'text-emerald-600 font-bold' : 'text-gray-700'"
              >
                {{ dayData.day }}
              </span>

              <!-- Events -->
              <div class="space-y-1">
                <div
                  v-for="event in dayData.events.slice(0, 3)"
                  :key="event.id"
                  class="text-xs truncate px-1.5 py-0.5 rounded bg-emerald-50 text-emerald-700 border border-emerald-100"
                  :title="`${event.time} - ${event.name}`"
                >
                  {{ event.time }} {{ event.name }}
                </div>
                <div v-if="dayData.events.length > 3" class="text-xs text-gray-500">
                  +{{ dayData.events.length - 3 }} more
                </div>
                <!-- Empty day indicator -->
                <div v-if="dayData.events.length === 0" class="text-xs text-center text-gray-400 mt-2 opacity-0 group-hover:opacity-100 transition-opacity">
                  + Đăng ký lớp
                </div>
              </div>
            </template>
          </div>
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
        v-if="showModal"
        class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50"
        @click.self="closeModal"
      >
        <div class="bg-white rounded-2xl w-full max-w-lg mx-4 shadow-2xl overflow-hidden">
          <div class="p-4 sm:p-6 border-b border-gray-100 flex justify-between items-center bg-gray-50">
            <h3 class="text-lg sm:text-xl font-bold text-gray-800 flex items-center gap-2">
              <svg class="w-4 h-4 sm:w-5 sm:h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
              </svg>
              Lịch trình ngày {{ selectedDate ? selectedDate.toLocaleDateString("vi-VN") : "" }}
            </h3>
            <div class="flex gap-2">
              <button
                @click="openRegisterModalForDate"
                class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors text-sm font-medium"
              >
                + Đăng ký lớp mới
              </button>
              <button
                @click="closeModal"
                class="text-gray-400 hover:text-gray-600 transition-colors"
              >
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                </svg>
              </button>
            </div>
          </div>

          <div class="p-4 sm:p-6 max-h-[60vh] overflow-y-auto">
            <div v-if="selectedDateEvents.length === 0" class="text-center py-8 text-gray-500">
              <div class="mb-2">😴</div>
              Không có lịch nào trong ngày này
            </div>

            <div v-else class="space-y-4">
              <div
                v-for="event in selectedDateEvents"
                :key="event.id"
                class="flex gap-4 p-4 rounded-xl border-2 border-gray-100 hover:border-blue-200 transition-colors bg-white group"
              >
                <!-- Time Column -->
                <div class="flex flex-col items-center justify-center w-20 rounded-lg font-bold shrink-0 bg-blue-50 text-blue-700">
                  <span class="text-lg">{{ event.time.split(' - ')[0] }}</span>
                  <span class="text-xs font-normal text-blue-500">đến</span>
                  <span class="text-sm">{{ event.time.split(' - ')[1] }}</span>
                </div>

                <!-- Info Column -->
                <div class="flex-1">
                  <h4 class="font-bold text-gray-800 text-lg group-hover:text-blue-600 transition-colors">
                    {{ event.name }}
                  </h4>
                  <div class="flex items-center gap-2 text-sm text-gray-600 mt-1">
                    <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"></path>
                    </svg>
                    {{ event.members }} học viên
                  </div>
                  <div class="flex items-center gap-2 text-sm text-gray-600 mt-1">
                    <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                    </svg>
                    Trạng thái: {{ event.status }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </transition>

    <!-- Register New Class Modal -->
    <transition
      enter-active-class="transition duration-200 ease-out"
      enter-from-class="opacity-0 scale-95"
      enter-to-class="opacity-100 scale-100"
      leave-active-class="transition duration-150 ease-in"
      leave-from-class="opacity-0 scale-95"
      leave-to-class="opacity-100 scale-100"
    >
      <div
        v-if="showRegisterModal"
        class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50"
        @click.self="closeRegisterModal"
      >
        <div class="bg-white rounded-2xl w-full max-w-2xl mx-4 shadow-2xl overflow-hidden">
          <div class="p-4 sm:p-6 border-b border-gray-100 flex justify-between items-center bg-gradient-to-r from-blue-600 to-blue-700 text-white">
            <h3 class="text-lg sm:text-xl font-bold flex items-center gap-2">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
              </svg>
              Đăng ký lớp mới - {{ registerDate ? registerDate.toLocaleDateString("vi-VN") : "" }}
            </h3>
            <button
              @click="closeRegisterModal"
              class="text-white/70 hover:text-white transition-colors"
            >
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
              </svg>
            </button>
          </div>

          <div class="p-4 sm:p-6 max-h-[70vh] overflow-y-auto">
            <!-- Member Search -->
            <div class="mb-6">
              <label class="block text-sm font-medium text-gray-700 mb-2">Tìm kiếm học viên</label>
              <div class="relative">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
                  </svg>
                </div>
                <input
                  v-model="memberSearchQuery"
                  type="text"
                  placeholder="Nhập tên hoặc email học viên..."
                  class="block w-full pl-10 pr-3 py-3 border border-gray-300 rounded-xl leading-5 bg-white placeholder-gray-500 focus:outline-none focus:placeholder-gray-400 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                />
              </div>
            </div>

            <!-- Member Selection -->
            <div class="mb-6">
              <label class="block text-sm font-medium text-gray-700 mb-2">Chọn học viên</label>
              <div class="max-h-40 overflow-y-auto border border-gray-200 rounded-xl">
                <div v-if="filteredMembers.length === 0" class="p-4 text-center text-gray-500">
                  Không tìm thấy học viên nào
                </div>
                <div v-else class="divide-y divide-gray-100">
                  <div
                    v-for="member in filteredMembers"
                    :key="member.id"
                    @click="selectMemberForRegistration(member)"
                    :class="[
                      'p-3 cursor-pointer transition-colors hover:bg-blue-50',
                      selectedMember?.id === member.id ? 'bg-blue-100 border-l-4 border-blue-500' : ''
                    ]"
                  >
                    <div class="font-medium text-gray-900">{{ member.name }}</div>
                    <div class="text-sm text-gray-500">{{ member.email }}</div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Time Selection -->
            <div class="grid grid-cols-2 gap-4 mb-6">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Giờ bắt đầu</label>
                <input
                  v-model="selectedStartTime"
                  type="time"
                  class="w-full px-3 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Giờ kết thúc</label>
                <input
                  v-model="selectedEndTime"
                  type="time"
                  class="w-full px-3 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                />
              </div>
            </div>

            <!-- Selected Member Info -->
            <div v-if="selectedMember" class="mb-6 p-4 bg-blue-50 rounded-xl border border-blue-200">
              <h4 class="font-medium text-blue-900 mb-2">Thông tin học viên đã chọn:</h4>
              <div class="grid grid-cols-2 gap-4 text-sm">
                <div>
                  <span class="text-gray-600">Họ tên:</span>
                  <span class="font-medium text-gray-900 ml-2">{{ selectedMember.name }}</span>
                </div>
                <div>
                  <span class="text-gray-600">Email:</span>
                  <span class="font-medium text-gray-900 ml-2">{{ selectedMember.email }}</span>
                </div>
                <div>
                  <span class="text-gray-600">Chiều cao:</span>
                  <span class="font-medium text-gray-900 ml-2">{{ selectedMember.height }} cm</span>
                </div>
                <div>
                  <span class="text-gray-600">Cân nặng:</span>
                  <span class="font-medium text-gray-900 ml-2">{{ selectedMember.weight }} kg</span>
                </div>
              </div>
            </div>

            <!-- Action Buttons -->
            <div class="flex gap-3 pt-4 border-t border-gray-200">
              <button
                @click="closeRegisterModal"
                class="flex-1 px-4 py-3 border border-gray-300 text-gray-700 rounded-xl hover:bg-gray-50 transition-colors"
              >
                Hủy
              </button>
              <button
                @click="registerNewClass"
                :disabled="!selectedMember || !selectedStartTime || !selectedEndTime"
                class="flex-1 px-4 py-3 bg-gradient-to-r from-blue-600 to-blue-700 text-white rounded-xl hover:shadow-lg transition-all disabled:opacity-50 disabled:cursor-not-allowed"
              >
                Đăng ký lớp
              </button>
            </div>
          </div>
        </div>
      </div>
    </transition>

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
