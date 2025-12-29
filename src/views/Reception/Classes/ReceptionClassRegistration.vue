<template>
  <div class="p-6">
    <!-- Header -->
    <div class="flex justify-between items-center mb-8">
      <div class="flex items-center gap-3">
        <UserPlus class="w-10 h-10 text-emerald-600" />
        <h1 class="text-3xl font-bold bg-gradient-to-r from-emerald-600 to-emerald-700 bg-clip-text text-transparent">
          Đăng Ký Lớp Học (Lễ Tân)
        </h1>
      </div>
      <button 
        v-if="currentStep > 1"
        @click="goBack"
        class="px-4 py-2 bg-gray-200 text-gray-700 rounded-xl font-semibold hover:bg-gray-300 transition-colors flex items-center gap-2"
      >
        <ArrowLeft class="w-5 h-5" />
        Quay lại
      </button>
    </div>

    <!-- Progress Steps -->
    <div class="mb-8">
      <div class="flex items-center justify-between max-w-3xl mx-auto">
        <div 
          v-for="step in steps" 
          :key="step.number"
          class="flex-1 flex items-center"
        >
          <div class="flex flex-col items-center flex-1">
            <div
              class="w-10 h-10 rounded-full flex items-center justify-center font-bold transition-all"
              :class="currentStep >= step.number
                ? 'bg-emerald-600 text-white'
                : 'bg-gray-200 text-gray-500'"
            >
              {{ step.number }}
            </div>
            <span
              class="text-xs mt-2 font-semibold text-center"
              :class="currentStep >= step.number ? 'text-emerald-600' : 'text-gray-500'"
            >
              {{ step.label }}
            </span>
          </div>
          <div 
            v-if="step.number < steps.length"
            class="flex-1 h-1 mx-2"
            :class="currentStep > step.number ? 'bg-emerald-600' : 'bg-gray-200'"
          ></div>
        </div>
      </div>
    </div>

    <!-- Step 1: Select Class -->
    <div v-if="currentStep === 1" class="space-y-6">
      <div class="bg-white rounded-2xl shadow-md p-6">
        <h2 class="text-xl font-bold text-gray-800 mb-4 flex items-center gap-2">
          <Dumbbell class="w-6 h-6 text-emerald-600" />
          Chọn Lớp Học
        </h2>

        <!-- Filters -->
        <div class="flex gap-4 mb-6">
          <div class="relative">
            <Filter class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
            <select 
              v-model="filterDifficulty"
              class="pl-10 pr-4 py-3 border-2 border-gray-200 rounded-xl focus:border-emerald-600 focus:ring-2 focus:ring-emerald-100 outline-none transition-all appearance-none bg-white"
            >
              <option value="">Tất cả độ khó</option>
              <option value="Beginner">Beginner</option>
              <option value="Intermediate">Intermediate</option>
              <option value="Advanced">Advanced</option>
            </select>
          </div>
          
          <div class="relative flex-1">
            <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
            <input
              v-model="searchQuery"
              type="text"
              placeholder="Tìm kiếm lớp học..."
              class="w-full pl-10 pr-4 py-3 border-2 border-gray-200 rounded-xl focus:border-emerald-600 focus:ring-2 focus:ring-emerald-100 outline-none transition-all"
            />
          </div>
        </div>

        <!-- Classes Grid -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          <div 
            v-for="cls in filteredClasses" 
            :key="cls.id"
            @click="selectClass(cls)"
            class="bg-white rounded-2xl shadow-md hover:shadow-xl transition-all duration-300 hover:-translate-y-1 overflow-hidden cursor-pointer border-2"
            :class="selectedClass?.id === cls.id ? 'border-emerald-600' : 'border-transparent'"
          >
            <!-- Card Header -->
            <div class="p-5 bg-gradient-to-r from-emerald-600 to-emerald-700 text-white">
              <div class="flex justify-between items-start">
                <h3 class="text-xl font-bold">{{ cls.name }}</h3>
                <span 
                  class="px-3 py-1 rounded-full text-xs font-semibold"
                  :class="{
                    'bg-green-500': cls.difficulty === 'Beginner',
                    'bg-orange-500': cls.difficulty === 'Intermediate',
                    'bg-blue-500': cls.difficulty === 'Advanced'
                  }"
                >
                  {{ cls.difficulty }}
                </span>
              </div>
            </div>
            
            <!-- Card Body -->
            <div class="p-5 space-y-3">
              <p class="text-gray-600 line-clamp-2">{{ cls.description }}</p>
              
              <div class="grid grid-cols-2 gap-2 text-sm">
                <div>
                  <span class="text-gray-500 font-semibold block">Thời gian:</span>
                  <span class="text-gray-800">{{ cls.startTime }} - {{ cls.endTime }}</span>
                </div>
                <div>
                  <span class="text-gray-500 font-semibold block">Bắt đầu:</span>
                  <span class="text-gray-800">{{ formatDate(cls.startDate) }}</span>
                </div>
              </div>

              <!-- VIP Early Access Badge -->
              <div v-if="isVIPEarlyAccess(cls)" class="p-2 bg-gradient-to-r from-yellow-50 to-orange-50 border border-yellow-400 rounded-lg text-orange-700 font-semibold text-xs flex items-center gap-2">
                <Crown class="w-4 h-4" />
                Đăng ký sớm VIP
              </div>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-if="filteredClasses.length === 0" class="text-center py-16">
          <Inbox class="w-16 h-16 text-gray-400 mx-auto mb-4" />
          <p class="text-gray-500 text-lg">Không có lớp học nào khả dụng</p>
        </div>
      </div>

      <div class="flex justify-end">
        <button
          @click="currentStep = 2"
          :disabled="!selectedClass"
          class="px-6 py-3 bg-gradient-to-r from-emerald-600 to-emerald-700 text-white rounded-xl font-semibold hover:shadow-lg transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
        >
          Tiếp theo
          <ArrowRight class="w-5 h-5" />
        </button>
      </div>
    </div>

    <!-- Step 2: Search Member -->
    <div v-if="currentStep === 2" class="space-y-6">
      <div class="bg-white rounded-2xl shadow-md p-6">
        <h2 class="text-xl font-bold text-gray-800 mb-4 flex items-center gap-2">
          <Search class="w-6 h-6 text-emerald-600" />
          Tìm Học Viên
        </h2>

        <!-- Selected Class Info -->
        <div class="mb-6 p-4 bg-emerald-50 border-2 border-emerald-200 rounded-xl">
          <div class="flex items-center gap-3">
            <Dumbbell class="w-6 h-6 text-emerald-600" />
            <div>
              <div class="font-bold text-gray-800">{{ selectedClass.name }}</div>
              <div class="text-sm text-gray-600">{{ selectedClass.startTime }} - {{ selectedClass.endTime }}</div>
            </div>
          </div>
        </div>

        <!-- Search Input -->
        <div class="flex gap-4 mb-6">
          <div class="relative flex-1">
            <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
            <input 
              v-model="memberSearchQuery" 
              type="text" 
              placeholder="Tìm theo tên hoặc email..." 
              @keyup.enter="searchMember"
              class="w-full pl-10 pr-4 py-3 border-2 border-gray-200 rounded-xl focus:border-blue-600 focus:ring-2 focus:ring-blue-100 outline-none transition-all"
            />
          </div>
          <button 
            @click="searchMember"
            class="px-6 py-3 bg-gradient-to-r from-blue-600 to-blue-700 text-white rounded-xl font-semibold hover:shadow-lg transition-all flex items-center gap-2"
          >
            <Search class="w-5 h-5" />
            Tìm kiếm
          </button>
        </div>

        <!-- Search Results -->
        <div v-if="memberSearchResults.length > 0" class="space-y-3">
          <div 
            v-for="member in memberSearchResults" 
            :key="member.id"
            @click="selectMember(member)"
            class="p-4 border-2 rounded-xl transition-all cursor-pointer"
            :class="[
              selectedMember?.id === member.id ? 'border-blue-600 bg-blue-50' : 'border-gray-200 hover:border-blue-300',
              member.membershipTier === 'VIP' ? 'bg-yellow-50' : ''
            ]"
          >
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-3 flex-1">
                <div class="w-12 h-12 rounded-full bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center text-white font-bold text-lg">
                  {{ member.name.charAt(0) }}
                </div>
                <div class="flex-1">
                  <div class="font-bold text-gray-800">{{ member.name }}</div>
                  <div class="text-sm text-gray-600">{{ member.email }}</div>
                </div>
              </div>
              <span 
                class="px-3 py-1 rounded-full text-xs font-bold"
                :class="{
                  'bg-yellow-100 text-yellow-700': member.membershipTier === 'VIP',
                  'bg-blue-100 text-blue-700': member.membershipTier === 'PREMIUM',
                  'bg-gray-100 text-gray-700': member.membershipTier === 'BASIC'
                }"
              >
                <Crown v-if="member.membershipTier === 'VIP'" class="w-3 h-3 inline mr-1" />
                {{ member.membershipTier }}
              </span>
            </div>
          </div>
        </div>

        <!-- No Results -->
        <div v-else-if="memberSearchQuery && memberSearchAttempted" class="text-center py-8 bg-gray-50 rounded-xl">
          <Inbox class="w-12 h-12 text-gray-400 mx-auto mb-2" />
          <p class="text-gray-500">Không tìm thấy học viên nào</p>
        </div>
      </div>

      <div class="flex justify-end">
        <button 
          @click="currentStep = 3"
          :disabled="!selectedMember"
          class="px-6 py-3 bg-gradient-to-r from-blue-600 to-blue-700 text-white rounded-xl font-semibold hover:shadow-lg transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
        >
          Tiếp theo
          <ArrowRight class="w-5 h-5" />
        </button>
      </div>
    </div>

    <!-- Step 3: Select Schedules -->
    <div v-if="currentStep === 3" class="space-y-6">
      <div class="bg-white rounded-2xl shadow-md p-6">
        <h2 class="text-xl font-bold text-gray-800 mb-4 flex items-center gap-2">
          <CalendarIcon class="w-6 h-6 text-blue-600" />
          Chọn Lịch Học
        </h2>

        <!-- Selected Member Info -->
        <div class="mb-6 p-4 bg-green-50 border-2 border-green-200 rounded-xl">
          <div class="flex items-center gap-3">
            <User class="w-6 h-6 text-green-600" />
            <div>
              <div class="font-bold text-gray-800">{{ selectedMember.name }}</div>
              <div class="text-sm text-gray-600">{{ selectedMember.email }}</div>
            </div>
            <span 
              class="ml-auto px-3 py-1 rounded-full text-xs font-bold"
              :class="{
                'bg-yellow-100 text-yellow-700': selectedMember.membershipTier === 'VIP',
                'bg-blue-100 text-blue-700': selectedMember.membershipTier === 'PREMIUM',
                'bg-gray-100 text-gray-700': selectedMember.membershipTier === 'BASIC'
              }"
            >
              {{ selectedMember.membershipTier }}
            </span>
          </div>
        </div>

        <!-- Loading State -->
        <div v-if="loadingSchedules" class="text-center py-8">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
          Đang tải lịch học...
        </div>

        <!-- No Schedules -->
        <div v-else-if="availableSchedules.length === 0" class="text-center py-8">
          <Inbox class="w-16 h-16 text-gray-400 mx-auto mb-4" />
          <p class="text-lg font-semibold text-gray-500">Không có lịch học khả dụng</p>
          <p class="text-sm text-gray-400 mt-2">Tất cả các buổi học đã đầy hoặc đã diễn ra</p>
        </div>

        <!-- Schedules Table -->
        <div v-else>
          <div class="mb-4 p-3 bg-blue-50 border border-blue-200 rounded-lg">
            <p class="text-sm text-blue-800">
              <strong>Hướng dẫn:</strong> Chọn các buổi học bạn muốn đăng ký cho học viên bằng cách click vào từng dòng.
            </p>
          </div>

          <div class="overflow-x-auto">
            <table class="w-full border-collapse">
              <thead>
                <tr class="bg-gray-100 border-b-2 border-gray-300">
                  <th class="p-3 text-left font-bold text-gray-700 w-12">
                    <input 
                      type="checkbox" 
                      :checked="selectedScheduleIds.length === availableSchedules.filter(s => canSelectSchedule(s)).length && availableSchedules.filter(s => canSelectSchedule(s)).length > 0"
                      @change="toggleAllSchedules"
                      class="w-5 h-5 text-blue-600 rounded focus:ring-blue-500"
                    />
                  </th>
                  <th class="p-3 text-left font-bold text-gray-700">Ngày</th>
                  <th class="p-3 text-left font-bold text-gray-700">Thời gian</th>
                  <th class="p-3 text-left font-bold text-gray-700">Phòng</th>
                  <th class="p-3 text-left font-bold text-gray-700">Trạng thái</th>
                  <th class="p-3 text-left font-bold text-gray-700">Chỗ trống</th>
                </tr>
              </thead>
              <tbody>
                <tr 
                  v-for="schedule in availableSchedules" 
                  :key="schedule.id"
                  @click="toggleSchedule(schedule)"
                  class="border-b border-gray-200 hover:bg-blue-50 cursor-pointer transition-colors"
                  :class="{
                    'bg-blue-100': selectedScheduleIds.includes(schedule.id),
                    'opacity-50 cursor-not-allowed': !canSelectSchedule(schedule)
                  }"
                >
                  <td class="p-3">
                    <input 
                      type="checkbox" 
                      :checked="selectedScheduleIds.includes(schedule.id)"
                      :disabled="!canSelectSchedule(schedule)"
                      @click.stop="toggleSchedule(schedule)"
                      class="w-5 h-5 text-blue-600 rounded focus:ring-blue-500"
                    />
                  </td>
                  <td class="p-3 font-medium text-gray-800">
                    {{ formatScheduleDate(schedule.startTime || schedule.date) }}
                  </td>
                  <td class="p-3 text-gray-600">
                    <span class="font-medium">{{ formatScheduleTime(schedule.startTime) }}</span>
                    <span class="mx-1">-</span>
                    <span class="font-medium">{{ formatScheduleTime(schedule.endTime) }}</span>
                  </td>
                  <td class="p-3 text-gray-600">
                    {{ getRoomName(schedule.roomId) }}
                  </td>
                  <td class="p-3">
                    <span 
                      class="px-2 py-1 rounded text-xs font-semibold"
                      :class="{
                        'bg-green-100 text-green-700': schedule.status === 'OPEN',
                        'bg-red-100 text-red-700': schedule.status === 'CLOSED' || schedule.status === 'CANCELLED',
                        'bg-gray-100 text-gray-700': !schedule.status
                      }"
                    >
                      {{ schedule.status === 'OPEN' ? 'Mở' : schedule.status === 'CLOSED' ? 'Đóng' : schedule.status === 'CANCELLED' ? 'Hủy' : 'N/A' }}
                    </span>
                  </td>
                  <td class="p-3">
                    <span 
                      class="font-bold"
                      :class="{
                        'text-green-600': getScheduleAvailableSlots(schedule) > 5,
                        'text-orange-600': getScheduleAvailableSlots(schedule) > 0 && getScheduleAvailableSlots(schedule) <= 5,
                        'text-red-600': getScheduleAvailableSlots(schedule) <= 0
                      }"
                    >
                      {{ getScheduleAvailableSlots(schedule) }}/{{ schedule.capacity || 20 }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <div v-if="selectedScheduleIds.length > 0" class="mt-4 p-4 bg-green-50 border-2 border-green-200 rounded-lg">
            <p class="text-green-800 font-semibold">
              Đã chọn <strong>{{ selectedScheduleIds.length }}</strong> buổi học
            </p>
          </div>
        </div>
      </div>

      <div class="flex justify-end">
        <button 
          @click="registerMember"
          :disabled="selectedScheduleIds.length === 0 || loadingSchedules || registering"
          class="px-6 py-3 bg-gradient-to-r from-green-600 to-green-700 text-white rounded-xl font-semibold hover:shadow-lg transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
        >
          <CheckCircle class="w-5 h-5" />
          {{ registering ? 'Đang đăng ký...' : `Đăng ký (${selectedScheduleIds.length})` }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import unifiedApi from '@/services/unifiedClassApi.js';
import apiService from '@/views/Test/apiService.js';
import { formatDate, getWeeksUntilStart, canVIPRegister, canAllRegister } from '@/views/Test/dateUtils.js';
import { 
  UserPlus,
  ArrowLeft,
  ArrowRight,
  Dumbbell,
  Search,
  Filter,
  Crown,
  Inbox,
  CalendarIcon,
  User,
  CheckCircle,
  MapPin,
  Clock
} from 'lucide-vue-next';
import { useToast } from 'vue-toastification';

const USE_REAL_API = true;

// Step management
const currentStep = ref(1);
const steps = [
  { number: 1, label: 'Chọn lớp' },
  { number: 2, label: 'Tìm học viên' },
  { number: 3, label: 'Chọn lịch' }
];

const toast = useToast();
// Step 1: Class selection
const classes = ref([]);
const roomsData = ref([]);
const filterDifficulty = ref('');
const searchQuery = ref('');
const selectedClass = ref(null);

// Step 2: Member search
const studentsData = ref([]);
const memberSearchQuery = ref('');
const memberSearchResults = ref([]);
const memberSearchAttempted = ref(false);
const selectedMember = ref(null);

// Step 3: Schedule selection
const availableSchedules = ref([]);
const selectedScheduleIds = ref([]);
const loadingSchedules = ref(false);
const registering = ref(false);

const filteredClasses = computed(() => {
  let result = classes.value.filter(c => c.status === 'ready_for_students');
  
  if (filterDifficulty.value) {
    result = result.filter(c => c.difficulty === filterDifficulty.value);
  }
  
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(c => 
      c.name.toLowerCase().includes(query) ||
      c.description.toLowerCase().includes(query)
    );
  }
  
  return result;
});

const isVIPEarlyAccess = (cls) => {
  return canVIPRegister(cls.startDate) && !canAllRegister(cls.startDate);
};

const selectClass = (cls) => {
  selectedClass.value = cls;
};

const searchMember = () => {
  memberSearchAttempted.value = true;
  const query = memberSearchQuery.value.toLowerCase().trim();
  
  if (!query) {
    memberSearchResults.value = [];
    return;
  }
  
  memberSearchResults.value = studentsData.value.filter(s => 
    s.name.toLowerCase().includes(query) ||
    s.email.toLowerCase().includes(query)
  );
};

const selectMember = (member) => {
  selectedMember.value = member;
};

const goBack = () => {
  if (currentStep.value > 1) {
    currentStep.value--;
  }
};

// Watch for step 3 to load schedules
watch(currentStep, async (newStep) => {
  if (newStep === 3 && selectedClass.value) {
    await loadSchedules();
  }
});

const loadSchedules = async () => {
  loadingSchedules.value = true;
  selectedScheduleIds.value = [];
  
  try {
    const schedules = await unifiedApi.getSessions(selectedClass.value.id);
    const now = new Date();
    const filteredSchedules = [];
    
    for (const s of schedules) {
      let scheduleDate;
      if (s.startTime && typeof s.startTime === 'string' && s.startTime.includes('T')) {
        scheduleDate = new Date(s.startTime);
      } else if (s.date) {
        scheduleDate = new Date(s.date);
      } else {
        continue;
      }
      
      const isFuture = scheduleDate > now;
      const isOpen = s.status === 'OPEN' || s.status === 'scheduled';
      
      if (isFuture && isOpen) {
        await updateScheduleSlots(s);
        const hasSlots = getScheduleAvailableSlots(s) > 0;
        if (hasSlots) {
          filteredSchedules.push(s);
        }
      }
    }
    
    availableSchedules.value = filteredSchedules.sort((a, b) => {
      const dateA = new Date(a.startTime || a.date);
      const dateB = new Date(b.startTime || b.date);
      return dateA - dateB;
    });
  } catch (error) {
    toast.error('Lỗi khi tải lịch học: ' + error.message);
  } finally {
    loadingSchedules.value = false;
  }
};

const scheduleSlotsCache = ref({});

const getScheduleAvailableSlots = (schedule) => {
  return scheduleSlotsCache.value[schedule.id] ?? (schedule.capacity || 20);
};

const updateScheduleSlots = async (schedule) => {
  try {
    if (USE_REAL_API) {
      const regs = await apiService.memberRegistration.getBySchedule(schedule.id);
      const capacity = schedule.capacity || 20;
      scheduleSlotsCache.value[schedule.id] = Math.max(0, capacity - regs.length);
    } else {
      scheduleSlotsCache.value[schedule.id] = schedule.capacity || 20;
    }
  } catch (e) {
    scheduleSlotsCache.value[schedule.id] = schedule.capacity || 20;
  }
};

const canSelectSchedule = (schedule) => {
  return schedule.status === 'OPEN' && getScheduleAvailableSlots(schedule) > 0;
};

const toggleSchedule = (schedule) => {
  if (!canSelectSchedule(schedule)) {
    return;
  }
  
  const index = selectedScheduleIds.value.indexOf(schedule.id);
  if (index > -1) {
    selectedScheduleIds.value.splice(index, 1);
  } else {
    selectedScheduleIds.value.push(schedule.id);
  }
};

const toggleAllSchedules = (event) => {
  if (event.target.checked) {
    selectedScheduleIds.value = availableSchedules.value
      .filter(s => canSelectSchedule(s))
      .map(s => s.id);
  } else {
    selectedScheduleIds.value = [];
  }
};

const getRoomName = (roomId) => {
  const room = roomsData.value.find(r => r.id === roomId);
  return room ? room.name : 'Không xác định';
};

const formatScheduleDate = (dateTime) => {
  if (!dateTime) return '';
  try {
    const date = new Date(dateTime);
    if (isNaN(date.getTime())) return '';
    return formatDate(date.toISOString().split('T')[0]);
  } catch (e) {
    return '';
  }
};

const formatScheduleTime = (dateTime) => {
  if (!dateTime) return '';
  try {
    const date = new Date(dateTime);
    if (isNaN(date.getTime())) return '';
    return date.toTimeString().substring(0, 5);
  } catch (e) {
    if (typeof dateTime === 'string' && dateTime.match(/^\d{2}:\d{2}$/)) {
      return dateTime;
    }
    return '';
  }
};

const registerMember = async () => {
  if (!selectedMember.value || selectedScheduleIds.value.length === 0) {
    toast.warning('Vui lòng chọn học viên và ít nhất một buổi học!');
    return;
  }

  if (confirm(`Xác nhận đăng ký ${selectedScheduleIds.value.length} buổi học cho ${selectedMember.value.name}?`)) {
    registering.value = true;
    try {
      await unifiedApi.registerBulkSchedules(selectedMember.value.id, selectedScheduleIds.value);
      toast.success(`Đã đăng ký thành công ${selectedScheduleIds.value.length} buổi học cho ${selectedMember.value.name}!`);
      
      // Reset form
      currentStep.value = 1;
      selectedClass.value = null;
      selectedMember.value = null;
      selectedScheduleIds.value = [];
      memberSearchQuery.value = '';
      memberSearchResults.value = [];
      memberSearchAttempted.value = false;
    } catch (error) {
      toast.error('Lỗi: ' + (error.response?.data?.message || error.message));
    } finally {
      registering.value = false;
    }
  }
};

const loadData = async () => {
  classes.value = await unifiedApi.getClasses();
  roomsData.value = await unifiedApi.getRooms();
  studentsData.value = await unifiedApi.getStudents();
};

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
