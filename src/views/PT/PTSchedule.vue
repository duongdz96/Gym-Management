<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRouter } from "vue-router";
import api from "@/services/api";
import { useAuthStore } from "@/stores/useAuthStore";
import { useToast } from "vue-toastification";

const router = useRouter();
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
const memberPackages = ref([]);
const selectedPackage = ref(null);
const startTimeError = ref("");
const endTimeError = ref("");

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

function getStatusText(status) {
  const statusMap = {
    'Scheduled': 'Đã đặt lịch',
    'In Progress': 'Đang tập',
    'Completed': 'Hoàn thành',
    'Cancelled': 'Đã hủy'
  };
  return statusMap[status] || status;
}

function formatTimeRange(startIso, endIso) {
  // Convert UTC to local time
  const s = new Date(startIso);
  const e = new Date(endIso);
  const pad = (n) => String(n).padStart(2, "0");
  
  // Get local hours and minutes
  const startHours = s.getHours();
  const startMinutes = s.getMinutes();
  const endHours = e.getHours();
  const endMinutes = e.getMinutes();
  
  return `${pad(startHours)}:${pad(startMinutes)} - ${pad(endHours)}:${pad(endMinutes)}`;
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
    
    console.log('Total appointments from API:', data.length);
    console.log('Current PT ID:', authStore.user.id);
    console.log('First appointment structure:', data[0]); // Log full structure

    // Filter appointments for the current PT (PT is inside ptPackageIssued)
    const ptAppointments = data.filter(appt => {
      const ptIdFromPackage = appt.ptPackageIssued?.pt?.id;
      const isPTMatch = ptIdFromPackage === authStore.user.id;
      
      if (!isPTMatch) {
        console.log('Filtered out appointment:', appt.id, 'PT ID from package:', ptIdFromPackage);
      }
      return isPTMatch;
    });
    
    console.log('PT appointments after filter:', ptAppointments.length);

    // Map backend appointment shape to the calendar event shape expected by this component
    schedule.value = ptAppointments.map((appt) => {
      const start = appt.startTime;
      const end = appt.endTime;
      const dateStr = toDateStr(start);

      const event = {
        id: appt.id,
        // calendar uses a date string YYYY-MM-DD to group events per day
        date: dateStr,
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
      
      console.log('Mapped event:', event.id, 'Date:', dateStr, 'Name:', event.name);
      return event;
    });
    
    console.log('Total events in schedule:', schedule.value.length);

  } catch (error) {
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
  } catch (error) {
    availableMembers.value = [];
    toast.error("Không thể tải thành viên. Vui lòng thử lại sau!");
  }
}

// Function to load member's PT packages
async function loadMemberPackages(memberId) {
  try {
    const res = await api.get("/packageissued");
    const data = Array.isArray(res.data) ? res.data : [];
    
    // Filter packages for the selected member and current PT
    // Note: Allow packages even if ptPackage.status is Inactive, as long as the user has remaining sessions
    memberPackages.value = data.filter(pkg => 
      pkg.member?.id === memberId && 
      pkg.pt?.id === authStore.user.id &&
      pkg.remainingSessions > 0
    );
    
    // Auto-select first package if available
    if (memberPackages.value.length > 0) {
      selectedPackage.value = memberPackages.value[0];
    } else {
      selectedPackage.value = null;
      toast.warning("Học viên này không có gói PT khả dụng!");
    }
    
    console.log("Member packages loaded:", memberPackages.value);
  } catch (error) {
    console.error('Failed to load member packages:', error);
    memberPackages.value = [];
    selectedPackage.value = null;
    toast.error("Không thể tải gói PT của học viên.");
  }
}

// Function to start a class
async function startClass(eventId) {
  try {
    const event = schedule.value.find(e => e.id === eventId);
    if (!event) return;
    
    // 1. Update appointment status
    const payload = {
      id: event.id,
      startTime: event.raw.startTime,
      endTime: event.raw.endTime,
      status: "In Progress",
      notificationSent: false,
      ptPackageIssued: {
        id: event.raw.ptPackageIssued.id
      },
      pt: {
        id: authStore.user.id
      }
    };
    
    await api.put(`/appointment/${eventId}`, payload);
    
    // 2. Start training session
    await api.post(`/trainingsession/${eventId}/start`, null, {
      params: {
        note: '' // Empty note, can be customized later
      }
    });
    
    event.status = 'In Progress';
    toast.success("Đã bắt đầu buổi tập!");
    await fetchSchedule();
  } catch (error) {
    console.error('Error starting class:', error);
    if (error.response?.status === 500) {
      toast.error("Lỗi server khi bắt đầu buổi tập. Vui lòng thử lại!");
    } else {
      toast.error("Không thể bắt đầu buổi tập. Vui lòng thử lại!");
    }
  }
}

// Function to end a class
async function endClass(eventId) {
  try {
    const event = schedule.value.find(e => e.id === eventId);
    if (!event) return;
    
    const payload = {
      id: event.id,
      startTime: event.raw.startTime,
      endTime: event.raw.endTime,
      status: "Completed",
      notificationSent: false,
      ptPackageIssued: {
        id: event.raw.ptPackageIssued.id
      },
      pt: {
        id: authStore.user.id
      }
    };
    
    await api.put(`/appointment/${eventId}`, payload);
    event.status = 'Completed';
    toast.success("Đã kết thúc buổi tập!");
    await fetchSchedule();
  } catch (error) {
    console.error('Error ending class:', error);
    toast.error("Không thể kết thúc buổi tập. Vui lòng thử lại!");
  }
}

// Function to cancel a class
async function cancelClass(eventId) {
  try {
    const event = schedule.value.find(e => e.id === eventId);
    if (!event) return;
    
    const payload = {
      id: event.id,
      startTime: event.raw.startTime,
      endTime: event.raw.endTime,
      status: "Cancelled",
      notificationSent: false,
      ptPackageIssued: {
        id: event.raw.ptPackageIssued.id
      },
      pt: {
        id: authStore.user.id
      }
    };
    
    await api.put(`/appointment/${eventId}`, payload);
    event.status = 'Cancelled';
    toast.success("Đã hủy buổi tập!");
    await fetchSchedule();
  } catch (error) {
    console.error('Error cancelling class:', error);
    toast.error("Không thể hủy buổi tập. Vui lòng thử lại!");
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

const minTime = computed(() => {
  if (!registerDate.value) return '';
  
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const selectedDateOnly = new Date(registerDate.value);
  selectedDateOnly.setHours(0, 0, 0, 0);
  
  // If selected date is today, minimum time is current time
  if (selectedDateOnly.getTime() === today.getTime()) {
    const now = new Date();
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    return `${hours}:${minutes}`;
  }
  
  // If selected date is in the future, no minimum time restriction
  return '';
});

const isButtonDisabled = computed(() => {
  const disabled = !selectedMember.value || !selectedPackage.value || !selectedStartTime.value || !selectedEndTime.value || !!startTimeError.value || !!endTimeError.value;
  console.log('Button disabled check:', {
    selectedMember: !!selectedMember.value,
    selectedPackage: !!selectedPackage.value,
    selectedStartTime: !!selectedStartTime.value,
    selectedEndTime: !!selectedEndTime.value,
    startTimeError: startTimeError.value,
    endTimeError: endTimeError.value,
    disabled
  });
  return disabled;
});

const isSelectedDatePast = computed(() => {
  if (!selectedDate.value) return false;
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const selectedDateOnly = new Date(selectedDate.value);
  selectedDateOnly.setHours(0, 0, 0, 0);
  return selectedDateOnly < today;
});

// Watch for time changes to validate
watch([selectedStartTime, registerDate], () => {
  startTimeError.value = '';
  
  if (!selectedStartTime.value || !registerDate.value) return;
  
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const selectedDateOnly = new Date(registerDate.value);
  selectedDateOnly.setHours(0, 0, 0, 0);
  
  // If selected date is today, check if start time is after current time
  if (selectedDateOnly.getTime() === today.getTime()) {
    const now = new Date();
    const [startHours, startMinutes] = selectedStartTime.value.split(':').map(Number);
    const startDateTime = new Date();
    startDateTime.setHours(startHours, startMinutes, 0, 0);
    
    // Add 1 minute buffer to avoid edge case issues
    if (startDateTime.getTime() <= now.getTime()) {
      startTimeError.value = 'Giờ bắt đầu phải sau thời điểm hiện tại!';
      console.log('Start time error:', {
        now: now.toLocaleTimeString(),
        selected: startDateTime.toLocaleTimeString(),
        nowTimestamp: now.getTime(),
        selectedTimestamp: startDateTime.getTime()
      });
    } else {
      console.log('Start time OK:', {
        now: now.toLocaleTimeString(),
        selected: startDateTime.toLocaleTimeString()
      });
    }
  }
}, { immediate: true });

watch([selectedStartTime, selectedEndTime], () => {
  endTimeError.value = '';
  
  if (!selectedStartTime.value || !selectedEndTime.value) return;
  
  const [startHours, startMinutes] = selectedStartTime.value.split(':').map(Number);
  const [endHours, endMinutes] = selectedEndTime.value.split(':').map(Number);
  
  if (endHours < startHours || (endHours === startHours && endMinutes <= startMinutes)) {
    endTimeError.value = 'Giờ kết thúc phải sau giờ bắt đầu!';
  }
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
  
  // Check if the selected date is in the past
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const selectedDateOnly = new Date(dayData.date);
  selectedDateOnly.setHours(0, 0, 0, 0);
  
  if (dayData.events.length > 0) {
    // Show existing events modal (allow viewing for any date)
    showModal.value = true;
  } else {
    // For empty dates, only allow registration for current/future dates
    if (selectedDateOnly < today) {
      toast.error("Không thể đăng ký lớp cho ngày trong quá khứ!");
      return;
    }
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

// Function to navigate to member details
function viewMemberDetails(event) {
  if (event.member) {
    // Navigate to PTMembers page - the page will handle selecting the correct member
    router.push({ name: 'PTMembers', state: { selectedMemberId: event.member.id } });
  }
}

// Register modal functions
function openRegisterModalForDate() {
  // Check if the selected date is in the past
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const selectedDateOnly = new Date(selectedDate.value);
  selectedDateOnly.setHours(0, 0, 0, 0);
  
  if (selectedDateOnly < today) {
    toast.error("Không thể đăng ký lớp cho ngày trong quá khứ!");
    return;
  }
  
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
  memberPackages.value = [];
  selectedPackage.value = null;
  startTimeError.value = "";
  endTimeError.value = "";
}

function selectMemberForRegistration(member) {
  selectedMember.value = member;
  loadMemberPackages(member.memberId);
}

async function registerNewClass() {
  if (!selectedMember.value || !selectedStartTime.value || !selectedEndTime.value) {
    toast.error("Vui lòng điền đầy đủ thông tin!");
    return;
  }
  
  if (!selectedPackage.value) {
    toast.error("Vui lòng chọn gói PT!");
    return;
  }

  // Validate time constraints
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const selectedDateOnly = new Date(registerDate.value);
  selectedDateOnly.setHours(0, 0, 0, 0);
  
  // If selected date is today, check if start time is after current time
  if (selectedDateOnly.getTime() === today.getTime()) {
    const now = new Date();
    const [startHours, startMinutes] = selectedStartTime.value.split(':').map(Number);
    const startDateTime = new Date();
    startDateTime.setHours(startHours, startMinutes, 0, 0);
    
    if (startDateTime <= now) {
      toast.error("Giờ bắt đầu phải sau thời điểm hiện tại!");
      return;
    }
  }
  
  // Check if end time is after start time
  const [startHours, startMinutes] = selectedStartTime.value.split(':').map(Number);
  const [endHours, endMinutes] = selectedEndTime.value.split(':').map(Number);
  
  if (endHours < startHours || (endHours === startHours && endMinutes <= startMinutes)) {
    toast.error("Giờ kết thúc phải sau giờ bắt đầu!");
    return;
  }

  try {
    // Format datetime to ISO string
    const startDateTime = new Date(registerDate.value);
    startDateTime.setHours(startHours, startMinutes, 0, 0);
    
    const endDateTime = new Date(registerDate.value);
    endDateTime.setHours(endHours, endMinutes, 0, 0);
    
    // Prepare payload with nested objects (PT is also the staff creating the appointment)
    const payload = {
      ptPackageIssued: { id: selectedPackage.value.id },
      pt: { id: authStore.user.id },
      staff: { id: authStore.user.id },
      startTime: startDateTime.toISOString(),
      endTime: endDateTime.toISOString(),
      status: "Scheduled"
    };
    
    console.log("Creating appointment:", payload);
    console.log("Auth user:", authStore.user);
    console.log("Selected package:", selectedPackage.value);
    
    // Call API
    const response = await api.post("/appointment", payload);
    console.log("Appointment created successfully:", response.data);
    
    toast.success(`Đã đăng ký lớp cho ${selectedMember.value.name} vào ${registerDate.value.toLocaleDateString('vi-VN')} từ ${selectedStartTime.value} đến ${selectedEndTime.value}`);
    
    // Close modal first
    closeRegisterModal();
    
    // Refresh schedule and force calendar update
    await fetchSchedule();
    
    // Force calendar to re-render by updating currentDate
    const tempDate = currentDate.value;
    currentDate.value = new Date(tempDate);
    
    console.log('Schedule refreshed, total events:', schedule.value.length);
  } catch (error) {
    console.error('Error creating appointment:', error);
    toast.error("Không thể đăng ký lớp. Vui lòng thử lại!");
  }
}

onMounted(async () => {
  await fetchSchedule();
});
</script>

<template>
  <div class="p-4 sm:p-6 space-y-4 sm:space-y-8">
    <!-- Title -->
    <h1 class="text-xl sm:text-2xl font-bold text-stone-800">Lịch trình & Đăng ký lớp</h1>

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
              <p>Thời gian: {{ event.time }}</p>
              <p>Số học viên: {{ event.members }}</p>
              <p>Trạng thái: {{ getStatusText(event.status) }}</p>
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
              !dayData.isCurrentMonth ? 'bg-gray-50/50 border-transparent' : dayData.date.setHours(0,0,0,0) < new Date().setHours(0,0,0,0) ? 'bg-gray-100/70 border-gray-200 opacity-60 cursor-not-allowed' : 'bg-white border-gray-100 hover:border-emerald-300 hover:shadow-md cursor-pointer',
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
                <!-- Empty day indicator - only show for current or future dates -->
                <div v-if="dayData.events.length === 0 && new Date(dayData.date).setHours(0,0,0,0) >= new Date().setHours(0,0,0,0)" class="text-xs text-center text-gray-400 mt-2 opacity-0 group-hover:opacity-100 transition-opacity">
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
                :disabled="isSelectedDatePast"
                :class="[
                  'px-4 py-2 rounded-lg transition-colors text-sm font-medium',
                  isSelectedDatePast 
                    ? 'bg-gray-300 text-gray-500 cursor-not-allowed' 
                    : 'bg-blue-600 text-white hover:bg-blue-700'
                ]"
                :title="isSelectedDatePast ? 'Không thể đăng ký lớp cho ngày trong quá khứ' : ''"
              >
                + Đăng ký buổi tập mới
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
                  <h4 
                    @click="viewMemberDetails(event)"
                    class="font-bold text-gray-800 text-lg group-hover:text-blue-600 transition-colors cursor-pointer hover:underline"
                  >
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
                    Trạng thái: {{ getStatusText(event.status) }}
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
              Đăng ký buổi tập mới - {{ registerDate ? registerDate.toLocaleDateString("vi-VN") : "" }}
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

            <!-- Package Selection -->
            <div v-if="selectedMember" class="mb-6">
              <label class="block text-sm font-medium text-gray-700 mb-2">Chọn gói PT</label>
              <div v-if="memberPackages.length === 0" class="p-4 text-center text-gray-500 border border-gray-200 rounded-xl">
                Học viên này không có gói PT khả dụng
              </div>
              <div v-else class="space-y-2">
                <div
                  v-for="pkg in memberPackages"
                  :key="pkg.id"
                  @click="selectedPackage = pkg"
                  :class="[
                    'p-4 border-2 rounded-xl cursor-pointer transition-all',
                    selectedPackage?.id === pkg.id ? 'border-blue-500 bg-blue-50' : 'border-gray-200 hover:border-blue-300'
                  ]"
                >
                  <div class="flex items-center justify-between">
                    <div>
                      <p class="font-medium text-gray-900">{{ pkg.ptPackage?.name }}</p>
                      <p class="text-sm text-gray-600">Còn lại: {{ pkg.remainingSessions }} / {{ pkg.ptPackage?.sessions }} buổi</p>
                    </div>
                    <div v-if="selectedPackage?.id === pkg.id" class="text-blue-600">
                      <svg class="w-6 h-6" fill="currentColor" viewBox="0 0 20 20">
                        <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                      </svg>
                    </div>
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
                  :min="minTime"
                  :class="[
                    'w-full px-3 py-3 border rounded-xl focus:ring-2 focus:ring-blue-500',
                    startTimeError ? 'border-red-500 focus:border-red-500' : 'border-gray-300 focus:border-blue-500'
                  ]"
                />
                <p v-if="startTimeError" class="mt-1 text-sm text-red-600 flex items-center gap-1">
                  <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd" />
                  </svg>
                  {{ startTimeError }}
                </p>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Giờ kết thúc</label>
                <input
                  v-model="selectedEndTime"
                  type="time"
                  :min="selectedStartTime || minTime"
                  :class="[
                    'w-full px-3 py-3 border rounded-xl focus:ring-2 focus:ring-blue-500',
                    endTimeError ? 'border-red-500 focus:border-red-500' : 'border-gray-300 focus:border-blue-500'
                  ]"
                />
                <p v-if="endTimeError" class="mt-1 text-sm text-red-600 flex items-center gap-1">
                  <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd" />
                  </svg>
                  {{ endTimeError }}
                </p>
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
                :disabled="isButtonDisabled"
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
              Chi tiết lớp học
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
                  <h4 
                    @click="viewMemberDetails(selectedTodayEvent); closeTodayModal();"
                    class="font-medium text-gray-900 flex items-center mb-2 cursor-pointer hover:text-blue-600 hover:underline transition-colors"
                  >
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
                      {{ selectedTodayEvent.members }} học viên
                    </div>
                  </div>
                  <div class="mt-2 text-sm text-gray-600">
                    <p>Trạng thái: <span class="font-medium">{{ getStatusText(selectedTodayEvent.status) }}</span></p>
                  </div>
                </div>
              </div>
            </div>

            <div class="flex space-x-2">
              <button
                v-if="selectedTodayEvent.status !== 'In Progress' && selectedTodayEvent.status !== 'Completed' && selectedTodayEvent.status !== 'Cancelled'"
                @click="startClass(selectedTodayEvent.id); closeTodayModal()"
                class="flex-1 px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition-colors"
              >
                Bắt đầu lớp
              </button>
              <button
                v-if="selectedTodayEvent.status === 'In Progress'"
                @click="endClass(selectedTodayEvent.id); closeTodayModal()"
                class="flex-1 px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
              >
                Kết thúc lớp
              </button>
              <button
                v-if="selectedTodayEvent.status !== 'Completed' && selectedTodayEvent.status !== 'Cancelled' && selectedTodayEvent.status !== 'In Progress'"
                @click="cancelClass(selectedTodayEvent.id); closeTodayModal()"
                class="flex-1 px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 transition-colors"
              >
                Hủy buổi tập
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
