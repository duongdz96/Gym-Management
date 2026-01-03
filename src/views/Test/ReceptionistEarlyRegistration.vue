<template>
  <div class="p-6">
    <!-- Header -->
    <div class="flex items-center gap-3 mb-8">
      <UserCheck class="w-10 h-10 text-blue-600" />
      <h1 class="text-3xl font-bold bg-gradient-to-r from-blue-600 to-blue-700 bg-clip-text text-transparent">
        Đăng Ký Sớm VIP
      </h1>
    </div>

    <!-- Classes List for Early Registration -->
    <div v-if="!selectedClass" class="bg-white rounded-2xl shadow-md p-6">
      <h2 class="text-xl font-bold text-gray-800 mb-4 flex items-center gap-2">
        <Calendar class="w-6 h-6 text-blue-600" />
        Lớp học có thể đăng ký sớm (VIP)
      </h2>

      <!-- Filters -->
      <div class="flex gap-4 mb-6">
        <div class="relative">
          <Filter class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
          <select 
            v-model="filterDifficulty"
            class="pl-10 pr-4 py-3 border-2 border-gray-200 rounded-xl focus:border-blue-600 focus:ring-2 focus:ring-blue-100 outline-none transition-all appearance-none bg-white"
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
            v-model="classSearchQuery" 
            type="text" 
            placeholder="Tìm kiếm lớp học..." 
            class="w-full pl-10 pr-4 py-3 border-2 border-gray-200 rounded-xl focus:border-blue-600 focus:ring-2 focus:ring-blue-100 outline-none transition-all"
          />
        </div>
      </div>

      <!-- Classes Grid -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
        <div 
          v-for="cls in filteredEarlyClasses" 
          :key="cls.id" 
          class="bg-white rounded-2xl shadow-md hover:shadow-xl transition-all duration-300 hover:-translate-y-1 overflow-hidden border-2 border-yellow-200 cursor-pointer"
          @click="selectClass(cls)"
        >
          <!-- Card Header -->
          <div class="p-5 bg-gradient-to-r from-yellow-400 to-orange-500 text-white">
            <div class="flex justify-between items-start">
              <h3 class="text-xl font-bold">{{ cls.name }}</h3>
              <span class="px-3 py-1 rounded-full text-xs font-semibold bg-white/30">
                {{ cls.difficulty }}
              </span>
            </div>
          </div>
          
          <!-- Card Body -->
          <div class="p-5 space-y-4">
            <p class="text-gray-600 line-clamp-2">{{ cls.description }}</p>
            
            <div class="grid grid-cols-2 gap-3 text-sm">
              <div>
                <span class="text-gray-500 font-semibold block flex items-center gap-1"><Users class="w-4 h-4" /> Chỗ trống:</span>
                <span 
                  class="font-bold"
                  :class="getAvailableSlots(cls) < 5 ? 'text-red-600' : 'text-gray-800'"
                >
                  {{ getAvailableSlots(cls) }}/{{ cls.maxStudents }}
                </span>
              </div>
              <div>
                <span class="text-gray-500 font-semibold block flex items-center gap-1"><MapPin class="w-4 h-4" /> Phòng:</span>
                <span class="text-gray-800">{{ getRoomName(cls.roomId) }}</span>
              </div>
              <div>
                <span class="text-gray-500 font-semibold block flex items-center gap-1"><Clock class="w-4 h-4" /> Thời gian:</span>
                <span class="text-gray-800">{{ cls.startTime }} - {{ cls.endTime }}</span>
              </div>
              <div>
                <span class="text-gray-500 font-semibold block flex items-center gap-1"><Calendar class="w-4 h-4" /> Bắt đầu:</span>
                <span class="text-gray-800">{{ formatDate(cls.startDate) }}</span>
              </div>
            </div>

            <!-- VIP Early Access Badge -->
            <div class="p-3 bg-gradient-to-r from-yellow-50 to-orange-50 border-2 border-yellow-400 rounded-xl text-orange-700 font-semibold text-sm flex items-center gap-2">
              <Crown class="w-5 h-5" />
              Đăng ký sớm VIP (còn {{ getWeeksUntilStart(cls.startDate) }} tuần)
            </div>
          </div>
          
          <!-- Card Footer -->
          <div class="p-4 bg-gray-50 border-t">
            <button 
              class="w-full px-4 py-3 bg-gradient-to-r from-yellow-400 to-orange-500 text-white rounded-xl font-semibold hover:shadow-lg transition-all flex items-center justify-center gap-2"
            >
              <Crown class="w-5 h-5" />
              Chọn lớp này
            </button>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-if="filteredEarlyClasses.length === 0" class="text-center py-16">
        <Inbox class="w-16 h-16 text-gray-400 mx-auto mb-4" />
        <p class="text-gray-500 text-lg">Không có lớp học nào đủ điều kiện đăng ký sớm</p>
      </div>
    </div>

    <!-- Student Search for Selected Class -->
    <div v-if="selectedClass" class="space-y-6">
      <!-- Selected Class Info -->
      <div class="bg-gradient-to-r from-yellow-400 to-orange-500 rounded-2xl shadow-md p-6 text-white">
        <div class="flex justify-between items-start">
          <div class="flex-1">
            <div class="flex items-center gap-3 mb-2">
              <Crown class="w-8 h-8" />
              <h2 class="text-2xl font-bold">{{ selectedClass.name }}</h2>
            </div>
            <p class="text-white/90 mb-3">{{ selectedClass.description }}</p>
            <div class="flex flex-wrap gap-3 text-sm">
              <span class="px-3 py-1 bg-white/20 rounded-full flex items-center gap-1">
                <Clock class="w-4 h-4" />
                {{ selectedClass.startTime }} - {{ selectedClass.endTime }}
              </span>
              <span class="px-3 py-1 bg-white/20 rounded-full flex items-center gap-1">
                <Calendar class="w-4 h-4" />
                Bắt đầu: {{ formatDate(selectedClass.startDate) }}
              </span>
              <span class="px-3 py-1 bg-white/20 rounded-full flex items-center gap-1">
                <Users class="w-4 h-4" />
                Còn {{ getAvailableSlots(selectedClass) }} chỗ
              </span>
            </div>
          </div>
          <button 
            @click="selectedClass = null; searchQuery = ''; searchResults = []; searchAttempted = false;"
            class="w-10 h-10 bg-white/20 rounded-full hover:bg-white/30 transition-all flex items-center justify-center"
          >
            <X class="w-6 h-6" />
          </button>
        </div>
      </div>

      <!-- Student Search -->
      <div class="bg-white rounded-2xl shadow-md p-6">
        <h2 class="text-xl font-bold text-gray-800 mb-4 flex items-center gap-2">
          <Search class="w-6 h-6 text-blue-600" />
          Tìm học viên VIP
        </h2>
        
        <div class="flex gap-4 mb-4">
          <div class="relative flex-1">
            <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
            <input 
              v-model="searchQuery" 
              type="text" 
              placeholder="Tìm theo tên hoặc email..." 
              @keyup.enter="searchStudent"
              class="w-full pl-10 pr-4 py-3 border-2 border-gray-200 rounded-xl focus:border-blue-600 focus:ring-2 focus:ring-blue-100 outline-none transition-all"
            />
          </div>
          <button 
            @click="searchStudent"
            class="px-6 py-3 bg-gradient-to-r from-blue-600 to-blue-700 text-white rounded-xl font-semibold hover:shadow-lg transition-all flex items-center gap-2"
          >
            <Search class="w-5 h-5" />
            Tìm kiếm
          </button>
        </div>

        <!-- Search Results -->
        <div v-if="searchResults.length > 0" class="space-y-3">
          <div 
            v-for="student in searchResults" 
            :key="student.id"
            class="p-4 border-2 rounded-xl transition-all"
            :class="student.membershipTier === 'VIP' ? 'border-yellow-400 bg-yellow-50' : 'border-gray-200 bg-gray-50'"
          >
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-3 flex-1">
                <div class="w-12 h-12 rounded-full bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center text-white font-bold text-lg">
                  {{ student.name.charAt(0) }}
                </div>
                <div class="flex-1">
                  <div class="font-bold text-gray-800">{{ student.name }}</div>
                  <div class="text-sm text-gray-600">{{ student.email }}</div>
                </div>
              </div>
              <div class="flex items-center gap-3">
                <span 
                  class="px-3 py-1 rounded-full text-xs font-bold"
                  :class="{
                    'bg-yellow-100 text-yellow-700': student.membershipTier === 'VIP',
                    'bg-blue-100 text-blue-700': student.membershipTier === 'PREMIUM',
                    'bg-gray-100 text-gray-700': student.membershipTier === 'BASIC'
                  }"
                >
                  <Crown v-if="student.membershipTier === 'VIP'" class="w-3 h-3 inline mr-1" />
                  {{ student.membershipTier }}
                </span>
                
                <!-- Register Button -->
                <button
                  v-if="student.membershipTier === 'VIP'"
                  @click="registerEarly(student)"
                  :disabled="isStudentRegistered(student.id)"
                  class="px-4 py-2 bg-gradient-to-r from-yellow-400 to-orange-500 text-white rounded-lg font-semibold hover:shadow-lg transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
                >
                  <component :is="isStudentRegistered(student.id) ? CheckCircle : Crown" class="w-4 h-4" />
                  {{ isStudentRegistered(student.id) ? 'Đã đăng ký' : 'Đăng ký sớm' }}
                </button>
                <div v-else class="px-4 py-2 bg-gray-100 text-gray-500 rounded-lg font-semibold flex items-center gap-2">
                  <XCircle class="w-4 h-4" />
                  Không đủ điều kiện
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- No Results -->
        <div v-else-if="searchQuery && searchAttempted" class="text-center py-8 bg-gray-50 rounded-xl">
          <Inbox class="w-12 h-12 text-gray-400 mx-auto mb-2" />
          <p class="text-gray-500">Không tìm thấy học viên nào</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import unifiedApi from './unifiedApi.js';
import { formatDate, getWeeksUntilStart, canVIPRegister } from './dateUtils.js';
import { 
  UserCheck,
  Search,
  Crown,
  CheckCircle,
  Calendar,
  Filter,
  Users,
  MapPin,
  Clock,
  XCircle,
  Inbox,
  X
} from 'lucide-vue-next';
import { useToast } from 'vue-toastification';

// State
const toast = useToast();
const classes = ref([]);
const roomsData = ref([]);
const studentsData = ref([]);
const selectedClass = ref(null);
const searchQuery = ref('');
const searchResults = ref([]);
const searchAttempted = ref(false);
const filterDifficulty = ref('');
const classSearchQuery = ref('');

// Computed
const earlyAccessClasses = computed(() => {
  return classes.value.filter(c => {
    if (c.status !== 'ready_for_students') return false;
    return canVIPRegister(c.startDate);
  });
});

const filteredEarlyClasses = computed(() => {
  let result = earlyAccessClasses.value;
  
  if (filterDifficulty.value) {
    result = result.filter(c => c.difficulty === filterDifficulty.value);
  }
  
  if (classSearchQuery.value) {
    const query = classSearchQuery.value.toLowerCase();
    result = result.filter(c => 
      c.name.toLowerCase().includes(query) ||
      c.description.toLowerCase().includes(query)
    );
  }
  
  return result;
});

// Methods
const loadData = async () => {
  classes.value = await unifiedApi.getClasses();
  roomsData.value = await unifiedApi.getRooms();
  studentsData.value = await unifiedApi.getStudents();
  
  // Load all registrations
  registrationsData.value = [];
  for (const student of studentsData.value) {
    const studentRegs = await unifiedApi.getStudentRegistrations(student.id);
    registrationsData.value.push(...studentRegs);
  }
  
  // Update available slots
  for (const cls of classes.value) {
    await updateAvailableSlots(cls);
  }
};

const selectClass = (cls) => {
  selectedClass.value = cls;
  searchQuery.value = '';
  searchResults.value = [];
  searchAttempted.value = false;
};

const searchStudent = () => {
  searchAttempted.value = true;
  const query = searchQuery.value.toLowerCase().trim();
  
  if (!query) {
    searchResults.value = [];
    return;
  }
  
  searchResults.value = studentsData.value.filter(s => 
    s.name.toLowerCase().includes(query) ||
    s.email.toLowerCase().includes(query)
  );
};

const getRoomName = (roomId) => {
  const room = roomsData.value.find(r => r.id === roomId);
  return room ? room.name : 'Không xác định';
};

const availableSlotsCache = ref({});

const getAvailableSlots = (cls) => {
  return availableSlotsCache.value[cls.id] ?? cls.maxStudents;
};

const updateAvailableSlots = async (cls) => {
  try {
    const registrations = await unifiedApi.getStudentRegistrations(0); // Get all
    const enrolled = registrations.filter(r => r.classId === cls.id && r.status === 'active').length;
    availableSlotsCache.value[cls.id] = Math.max(0, cls.maxStudents - enrolled);
  } catch (error) {
    availableSlotsCache.value[cls.id] = cls.maxStudents;
  }
};

const registrationsData = ref([]);

const isStudentRegistered = (studentId) => {
  if (!selectedClass.value) return false;
  return registrationsData.value.some(r => 
    r.classId === selectedClass.value.id && 
    r.studentId === studentId && 
    r.status === 'active'
  );
};

const registerEarly = async (student) => {
  if (!selectedClass.value) {
    toast.warning('Vui lòng chọn lớp học!');
    return;
  }

  if (student.membershipTier !== 'VIP') {
    toast.error('Chỉ thành viên VIP mới được đăng ký sớm!');
    return;
  }

  if (isStudentRegistered(student.id)) {
    toast.warning('Học viên đã đăng ký lớp này rồi!');
    return;
  }

  if (getAvailableSlots(selectedClass.value) <= 0) {
    toast.error('Lớp đã đầy!');
    return;
  }
  
  if (confirm(`Xác nhận đăng ký sớm lớp "${selectedClass.value.name}" cho ${student.name}?`)) {
    try {
      await unifiedApi.registerStudent(selectedClass.value.id, student.id);
      toast.success('Đăng ký sớm thành công!');
      // Refresh search results to update button states
      searchStudent();
    } catch (error) {
      toast.error('Có lỗi xảy ra');
    }
  }
};

// Initialize
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
