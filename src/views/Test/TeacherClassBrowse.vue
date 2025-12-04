<template>
  <div class="p-6">
    <div class="flex items-center gap-3 mb-8">
      <BookOpen class="w-10 h-10 text-blue-600" />
      <h1 class="text-3xl font-bold bg-gradient-to-r from-blue-600 to-blue-700 bg-clip-text text-transparent">
        Đăng Ký Dạy Lớp
      </h1>
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
          
          <div class="grid grid-cols-2 gap-3 text-sm">
            <div>
              <span class="text-gray-500 font-semibold block flex items-center gap-1"><Users class="w-4 h-4" /> Sức chứa:</span>
              <span class="text-gray-800">{{ cls.maxStudents }} học viên</span>
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

          <!-- Conflict Warning -->
          <div v-if="hasConflict(cls)" class="p-3 bg-orange-50 border-2 border-orange-200 rounded-xl text-orange-700 font-semibold text-sm">
            <div class="flex items-center gap-2 mb-1">
              <AlertTriangle class="w-4 h-4" />
              Bạn có lịch trùng với lớp này
            </div>
            <div class="mt-2 space-y-1 font-normal ml-6">
              <div v-for="(conflict, idx) in getConflicts(cls)" :key="idx" class="flex items-center gap-1">
                <Calendar class="w-3 h-3" />
                {{ conflict.date }}: {{ conflict.time }} - {{ conflict.className }}
              </div>
            </div>
          </div>

          <!-- Application Status -->
          <div 
            v-if="getApplicationStatus(cls.id)" 
            class="p-3 border-2 rounded-xl font-semibold text-sm text-center flex items-center justify-center gap-2 relative group cursor-help transition-all"
            :class="{
              'bg-blue-50 border-blue-200 text-blue-700': getApplicationStatus(cls.id) === 'pending',
              'bg-green-50 border-green-200 text-green-700': getApplicationStatus(cls.id) === 'approved',
              'bg-red-50 border-red-200 text-red-700': getApplicationStatus(cls.id) === 'rejected'
            }"
          >
            <component 
              :is="getApplicationStatus(cls.id) === 'pending' ? Clock : getApplicationStatus(cls.id) === 'approved' ? CheckCircle : XCircle" 
              class="w-4 h-4"
            />
            {{ getApplicationStatusText(cls.id) }}
            
            <!-- Tooltip for Rejection Reason -->
            <div 
              v-if="getApplicationStatus(cls.id) === 'rejected' && getRejectionReason(cls.id)"
              class="absolute bottom-full left-1/2 -translate-x-1/2 mb-2 w-64 p-3 bg-gray-800 text-white text-xs rounded-lg shadow-xl opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none z-10 text-left"
            >
              <div class="font-bold mb-1 flex items-center gap-1">
                <XCircle class="w-3 h-3 text-red-400" />
                Lý do từ chối:
              </div>
              {{ getRejectionReason(cls.id) }}
              <!-- Arrow -->
              <div class="absolute top-full left-1/2 -translate-x-1/2 border-8 border-transparent border-t-gray-800"></div>
            </div>
          </div>
        </div>
        
        <!-- Card Footer -->
        <div class="p-4 bg-gray-50 border-t">
          <button 
            @click="applyToTeach(cls)" 
            :disabled="hasConflict(cls) || getApplicationStatus(cls.id)"
            class="w-full px-4 py-3 bg-gradient-to-r from-blue-600 to-blue-700 text-white rounded-xl font-semibold hover:shadow-lg transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
          >
            <component 
              :is="getApplicationStatus(cls.id) ? CheckCircle : Hand" 
              class="w-5 h-5"
            />
            {{ getApplicationStatus(cls.id) ? 'Đã đăng ký' : 'Đăng ký dạy' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-if="filteredClasses.length === 0" class="text-center py-16">
      <Inbox class="w-16 h-16 text-gray-400 mx-auto mb-4" />
      <p class="text-gray-500 text-lg">Không có lớp học nào cần giáo viên</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import mockApi, { rooms, generateSessions, checkTeacherConflicts } from './mockData.js';
import { formatDate } from './dateUtils.js';
import { 
  BookOpen, 
  Search, 
  Filter, 
  Users, 
  MapPin, 
  Clock, 
  Calendar, 
  AlertTriangle, 
  CheckCircle, 
  XCircle, 
  Hand,
  Inbox
} from 'lucide-vue-next';

// State
const classes = ref([]);
const roomsData = ref([]);
const applications = ref([]);
const filterDifficulty = ref('');
const searchQuery = ref('');
const currentTeacherId = ref(1);

// Computed
const availableClasses = computed(() => {
  return classes.value.filter(c => 
    c.status === 'pending_teacher' || 
    (c.status === 'waiting_approval' && c.pendingTeacherId !== currentTeacherId.value) ||
    // Also show classes where this teacher has applied (even if rejected or approved) so they can see status
    applications.value.some(a => a.classId === c.id && a.teacherId === currentTeacherId.value)
  );
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

// Methods
const loadData = async () => {
  classes.value = await mockApi.getClasses();
  roomsData.value = await mockApi.getRooms();
  applications.value = await mockApi.getApplications();
};

const getRoomName = (roomId) => {
  const room = roomsData.value.find(r => r.id === roomId);
  return room ? room.name : 'Không xác định';
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

const hasConflict = (cls) => {
  const sessions = generateSessions(cls);
  const conflicts = checkTeacherConflicts(currentTeacherId.value, sessions, cls.id);
  return conflicts.length > 0;
};

const getConflicts = (cls) => {
  const sessions = generateSessions(cls);
  return checkTeacherConflicts(currentTeacherId.value, sessions, cls.id);
};

const getApplicationStatus = (classId) => {
  const app = applications.value.find(a => 
    a.classId === classId && a.teacherId === currentTeacherId.value
  );
  return app ? app.status : null;
};

const getRejectionReason = (classId) => {
  const app = applications.value.find(a => 
    a.classId === classId && a.teacherId === currentTeacherId.value
  );
  return app ? app.rejectionReason : '';
};

const getApplicationStatusText = (classId) => {
  const status = getApplicationStatus(classId);
  if (status === 'pending') return 'Đang chờ duyệt';
  if (status === 'approved') return 'Đã được duyệt';
  if (status === 'rejected') return 'Đã bị từ chối';
  return '';
};

const applyToTeach = async (cls) => {
  if (hasConflict(cls)) {
    alert('❌ Bạn có lịch trùng với lớp này!');
    return;
  }
  
  if (confirm(`Bạn có chắc muốn đăng ký dạy lớp "${cls.name}"?`)) {
    try {
      await mockApi.applyToTeach(cls.id, currentTeacherId.value);
      alert('✅ Đăng ký thành công! Vui lòng chờ Manager duyệt.');
      await loadData();
    } catch (error) {
      alert('❌ Lỗi: ' + error.message);
    }
  }
};

// Lifecycle
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
