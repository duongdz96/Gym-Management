<template>
  <div class="p-6">
    <!-- Header -->
    <div class="flex justify-between items-center mb-8">
      <div class="flex items-center gap-3">
        <Dumbbell class="w-10 h-10 text-blue-600" />
        <h1 class="text-3xl font-bold bg-gradient-to-r from-blue-600 to-blue-700 bg-clip-text text-transparent">
          Đăng Ký Lớp Học
        </h1>
      </div>
      <div 
        v-if="currentStudent && currentStudent.membershipTier" 
        class="px-6 py-3 rounded-full font-bold text-white flex items-center gap-2"
        :class="{
          'bg-gradient-to-r from-yellow-400 to-orange-500': currentStudent.membershipTier === 'VIP',
          'bg-gradient-to-r from-blue-400 to-blue-500': currentStudent.membershipTier === 'PREMIUM',
          'bg-gradient-to-r from-gray-300 to-gray-400 text-gray-800': currentStudent.membershipTier === 'BASIC'
        }"
      >
        <Crown v-if="currentStudent.membershipTier === 'VIP'" class="w-5 h-5" />
        {{ currentStudent.membershipTier }}
      </div>
    </div>

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
          v-model="searchQuery" 
          type="text" 
          placeholder="Tìm kiếm lớp học..." 
          class="w-full pl-10 pr-4 py-3 border-2 border-gray-200 rounded-xl focus:border-blue-600 focus:ring-2 focus:ring-blue-100 outline-none transition-all"
        />
      </div>
    </div>

    <!-- Available Classes -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
      <div 
        v-for="cls in filteredClasses" 
        :key="cls.id" 
        class="bg-white rounded-2xl shadow-md hover:shadow-xl transition-all duration-300 hover:-translate-y-1 overflow-hidden"
      >
        <!-- Card Header -->
        <div class="p-5 bg-gradient-to-r from-blue-600 to-blue-700 text-white">
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
        <div class="p-5 space-y-4">
          <p class="text-gray-600 line-clamp-2">{{ cls.description }}</p>
          
          <!-- Teacher Info -->
          <div class="flex items-center gap-3 p-3 bg-gray-50 rounded-xl">
            <div class="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center text-blue-600">
              <User class="w-6 h-6" />
            </div>
            <div>
              <div class="font-bold text-gray-800">{{ getTeacher(cls.teacherId).name }}</div>
              <div class="text-xs text-gray-500">{{ getTeacher(cls.teacherId).specialties.join(', ') }}</div>
            </div>
          </div>
          
          <div class="grid grid-cols-2 gap-3 text-sm">
            <div>
              <span class="text-gray-500 font-semibold block flex items-center gap-1"><Users class="w-4 h-4" /> Chỗ trống:</span>
              <span 
                class="font-bold"
                :class="getAvailableSlots(cls) < 5 ? 'text-blue-600' : 'text-gray-800'"
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
              <span class="text-gray-500 font-semibold block flex items-center gap-1"><Calendar class="w-4 h-4" /> Lịch:</span>
              <span class="text-gray-800">{{ getScheduleText(cls) }}</span>
            </div>
            <div>
              <span class="text-gray-500 font-semibold block flex items-center gap-1"><Calendar class="w-4 h-4" /> Bắt đầu:</span>
              <span class="text-gray-800">{{ formatDate(cls.startDate) }}</span>
            </div>
            <div>
              <span class="text-gray-500 font-semibold block flex items-center gap-1"><Calendar class="w-4 h-4" /> Kết thúc:</span>
              <span class="text-gray-800">{{ formatDate(cls.endDate) }}</span>
            </div>
          </div>

          <!-- VIP Early Registration Notice -->
          <div v-if="isVIPEarlyAccess(cls)" class="p-3 bg-gradient-to-r from-yellow-50 to-orange-50 border-2 border-yellow-400 rounded-xl text-orange-700 font-semibold text-sm flex items-start gap-2">
            <Crown class="w-5 h-5 flex-shrink-0 mt-0.5" />
            <div>
              <div class="font-bold mb-1">Đăng ký sớm VIP (còn {{ getWeeksUntilStart(cls.startDate) }} tuần)</div>
              <div class="text-xs font-normal">Vui lòng ra quầy Lễ tân để đăng ký sớm cho lớp này</div>
            </div>
          </div>

          <!-- Registration Status -->
          <div v-if="isRegistered(cls.id)" class="p-3 bg-green-50 border-2 border-green-400 rounded-xl text-green-700 font-semibold text-sm text-center flex items-center justify-center gap-2">
            <CheckCircle class="w-4 h-4" />
            Bạn đã đăng ký lớp này
          </div>
        </div>
        
        <!-- Card Footer -->
        <div class="p-4 bg-gray-50 border-t">
          <!-- Cancel Button if Already Registered -->
          <button 
            v-if="isRegistered(cls.id)"
            @click="cancelRegistration(cls)" 
            class="w-full px-4 py-3 bg-gradient-to-r from-red-500 to-red-600 text-white rounded-xl font-semibold hover:shadow-lg transition-all flex items-center justify-center gap-2"
          >
            <XCircle class="w-5 h-5" />
            Hủy đăng ký
          </button>
          
          <!-- VIP Early Access - Go to Reception -->
          <button 
            v-else-if="isVIPEarlyAccess(cls)"
            disabled
            class="w-full px-4 py-3 bg-gradient-to-r from-yellow-400 to-orange-500 text-white rounded-xl font-semibold opacity-75 cursor-not-allowed flex items-center justify-center gap-2"
          >
            <Crown class="w-5 h-5" />
            Ra quầy Lễ tân để đăng ký
          </button>
          
          <!-- Register Button if Not VIP Early Access -->
          <button 
            v-else
            @click="register(cls)" 
            :disabled="!canRegister(cls)"
            class="w-full px-4 py-3 bg-gradient-to-r from-blue-600 to-blue-700 text-white rounded-xl font-semibold hover:shadow-lg transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
          >
            <component 
              :is="getRegisterButtonIcon(cls)" 
              class="w-5 h-5"
            />
            {{ getRegisterButtonText(cls) }}
          </button>
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-if="filteredClasses.length === 0" class="text-center py-16">
      <Inbox class="w-16 h-16 text-gray-400 mx-auto mb-4" />
      <p class="text-gray-500 text-lg">Không có lớp học nào khả dụng</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import mockApi, { rooms, teachers, students, studentRegistrations } from './mockData.js';
import { formatDate, getWeeksUntilStart, canVIPRegister, canAllRegister } from './dateUtils.js';
import { 
  Dumbbell, 
  Search, 
  Filter, 
  Users, 
  MapPin, 
  Clock, 
  Calendar, 
  User, 
  Crown, 
  CheckCircle, 
  XCircle, 
  Hand,
  Inbox
} from 'lucide-vue-next';

const classes = ref([]);
const roomsData = ref([]);
const teachersData = ref([]);
const registrations = ref([]);
const filterDifficulty = ref('');
const searchQuery = ref('');
const currentStudentId = ref(1);
const currentStudent = ref({ membershipTier: 'BASIC' });

const availableClasses = computed(() => {
  return classes.value.filter(c => c.status === 'ready_for_students');
});

const filteredClasses = computed(() => {
  let result = availableClasses.value;
  
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

const loadData = async () => {
  classes.value = await mockApi.getClasses();
  roomsData.value = await mockApi.getRooms();
  teachersData.value = await mockApi.getTeachers();
  registrations.value = await mockApi.getStudentRegistrations(currentStudentId.value);
  
  const allStudents = await mockApi.getStudents();
  currentStudent.value = allStudents.find(s => s.id === currentStudentId.value) || { membershipTier: 'BASIC' };
};

onMounted(() => {
  loadData();
});

const getRoomName = (roomId) => {
  const room = roomsData.value.find(r => r.id === roomId);
  return room ? room.name : 'Không xác định';
};

const getTeacher = (teacherId) => {
  return teachersData.value.find(t => t.id === teacherId) || { name: 'Chưa có', avatar: '', specialties: [] };
};

const getScheduleText = (cls) => {
  const daysOfWeek = ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'];
  if (cls.patternType === 'weekly') {
    const days = cls.daysOfWeek.map(d => daysOfWeek[d]).join(', ');
    return `${days}`;
  }
  if (cls.patternType === 'monthly') return 'Hàng tháng';
  if (cls.patternType === 'no_repeat') return 'Một lần';
  return 'Tùy chỉnh';
};

const getAvailableSlots = (cls) => {
  const enrolled = studentRegistrations.filter(r => r.classId === cls.id && r.status === 'active').length;
  return cls.maxStudents - enrolled;
};

const isVIPEarlyAccess = (cls) => {
  return canVIPRegister(cls.startDate) && !canAllRegister(cls.startDate);
};

const isRegistered = (classId) => {
  return registrations.value.some(r => r.classId === classId && r.status === 'active');
};

const canRegister = (cls) => {
  if (isRegistered(cls.id)) return false;
  if (getAvailableSlots(cls) <= 0) return false;
  
  if (isVIPEarlyAccess(cls)) {
    return currentStudent.value.membershipTier === 'VIP';
  }
  
  return canAllRegister(cls.startDate);
};

const getRegisterButtonText = (cls) => {
  if (isRegistered(cls.id)) return 'Đã đăng ký';
  if (getAvailableSlots(cls) <= 0) return 'Đã đầy';
  if (isVIPEarlyAccess(cls) && currentStudent.value.membershipTier !== 'VIP') {
    return 'Chỉ VIP';
  }
  return 'Đăng ký';
};

const getRegisterButtonIcon = (cls) => {
  if (isRegistered(cls.id)) return CheckCircle;
  if (getAvailableSlots(cls) <= 0) return XCircle;
  if (isVIPEarlyAccess(cls) && currentStudent.value.membershipTier !== 'VIP') {
    return Crown;
  }
  return Hand;
};

const register = async (cls) => {
  if (!canRegister(cls)) return;
  
  if (confirm(`Bạn có chắc muốn đăng ký lớp "${cls.name}"?`)) {
    try {
      await mockApi.registerStudent(cls.id, currentStudentId.value);
      alert('✅ Đăng ký thành công!');
      await loadData();
    } catch (error) {
      alert('❌ Lỗi: ' + error.message);
    }
  }
};

const cancelRegistration = async (cls) => {
  if (confirm(`Bạn có chắc muốn hủy đăng ký lớp "${cls.name}"?`)) {
    try {
      await mockApi.cancelRegistration(cls.id, currentStudentId.value);
      alert('✅ Đã hủy đăng ký!');
      await loadData();
    } catch (error) {
      alert('❌ Lỗi: ' + error.message);
    }
  }
};
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
