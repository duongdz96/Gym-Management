<template>
  <div class="p-6">
    <!-- Header -->
    <div class="flex items-center gap-3 mb-8">
      <BookOpen class="w-10 h-10 text-green-600" />
      <h1 class="text-3xl font-bold bg-gradient-to-r from-green-600 to-green-700 bg-clip-text text-transparent">
        Lớp Học Của Tôi
      </h1>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="text-center py-16">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600 mx-auto mb-4"></div>
      <p class="text-gray-500">Đang tải danh sách lớp...</p>
    </div>

    <!-- Classes Grid -->
    <div v-else-if="myClasses.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
      <div 
        v-for="cls in myClasses" 
        :key="cls.id"
        class="bg-white rounded-2xl shadow-md hover:shadow-xl transition-all duration-300 hover:-translate-y-1 overflow-hidden"
      >
        <!-- Card Header -->
        <div class="p-5 bg-gradient-to-r from-green-600 to-green-700 text-white">
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
              <span class="text-gray-500 font-semibold block flex items-center gap-1">
                <Users class="w-4 h-4" /> Học viên:
              </span>
              <span class="font-bold text-gray-800">{{ cls.maxStudents }}</span>
            </div>
            <div>
              <span class="text-gray-500 font-semibold block flex items-center gap-1">
                <MapPin class="w-4 h-4" /> Phòng:
              </span>
              <span class="text-gray-800">{{ getRoomName(cls.roomId) }}</span>
            </div>
            <div>
              <span class="text-gray-500 font-semibold block flex items-center gap-1">
                <Clock class="w-4 h-4" /> Thời gian:
              </span>
              <span class="text-gray-800">{{ cls.startTime }} - {{ cls.endTime }}</span>
            </div>
            <div>
              <span class="text-gray-500 font-semibold block flex items-center gap-1">
                <Calendar class="w-4 h-4" /> Lịch:
              </span>
              <span class="text-gray-800">{{ getScheduleText(cls) }}</span>
            </div>
          </div>
        </div>
        
        <!-- Card Footer -->
        <div class="p-4 bg-gray-50 border-t grid grid-cols-2 gap-2">
          <button 
            @click="viewSchedule(cls)"
            class="px-4 py-2 bg-blue-100 text-blue-700 rounded-xl font-semibold hover:bg-blue-200 transition-all flex items-center justify-center gap-2"
          >
            <CalendarIcon class="w-4 h-4" />
            Xem lịch
          </button>
          <button 
            @click="goToAttendance(cls)"
            class="px-4 py-2 bg-gradient-to-r from-green-600 to-green-700 text-white rounded-xl font-semibold hover:shadow-lg transition-all flex items-center justify-center gap-2"
          >
            <CheckSquare class="w-4 h-4" />
            Điểm danh
          </button>
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-else class="text-center py-16">
      <Inbox class="w-16 h-16 text-gray-400 mx-auto mb-4" />
      <p class="text-gray-500 text-lg">Bạn chưa được phân công dạy lớp nào</p>
    </div>

    <!-- Schedule Modal -->
    <transition
      enter-active-class="transition duration-200 ease-out"
      enter-from-class="opacity-0 scale-95"
      enter-to-class="opacity-100 scale-100"
      leave-active-class="transition duration-150 ease-in"
      leave-from-class="opacity-100 scale-100"
      leave-to-class="opacity-0 scale-95"
    >
      <div 
        v-if="showScheduleModal"
        class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50"
        @click.self="showScheduleModal = false"
      >
        <div class="bg-white rounded-2xl w-full max-w-2xl shadow-2xl overflow-hidden max-h-[80vh] flex flex-col">
          <!-- Modal Header -->
          <div class="p-6 bg-gradient-to-r from-green-600 to-green-700 text-white flex justify-between items-center shrink-0">
            <div>
              <h3 class="text-xl font-bold flex items-center gap-2">
                <CalendarIcon class="w-6 h-6" />
                Lịch học
              </h3>
              <p class="text-green-100 text-sm mt-1">{{ selectedClass?.name }}</p>
            </div>
            <button 
              @click="showScheduleModal = false"
              class="text-white/80 hover:text-white transition-colors bg-white/10 hover:bg-white/20 p-2 rounded-lg"
            >
              <X class="w-6 h-6" />
            </button>
          </div>
          
          <!-- Modal Body -->
          <div class="p-6 overflow-y-auto flex-1">
            <div v-if="selectedClassSessions.length === 0" class="text-center py-8 text-gray-500">
              Chưa có lịch học cụ thể.
            </div>
            
            <div v-else class="grid gap-3">
              <div 
                v-for="(session, idx) in selectedClassSessions" 
                :key="session.id"
                class="flex items-center gap-4 p-4 rounded-xl border border-gray-100 hover:border-green-200 hover:bg-green-50/50 transition-all"
              >
                <!-- Index -->
                <div class="w-8 h-8 rounded-full bg-green-100 text-green-600 flex items-center justify-center font-bold text-sm shrink-0">
                  {{ idx + 1 }}
                </div>
                
                <!-- Date Info -->
                <div class="flex-1">
                  <div class="font-bold text-gray-800 flex items-center gap-2">
                    <CalendarIcon class="w-4 h-4 text-green-500" />
                    {{ formatDate(session.date) }}
                  </div>
                  <div class="text-sm text-gray-500 mt-1 flex items-center gap-4">
                    <span class="flex items-center gap-1">
                      <Clock class="w-3 h-3" /> {{ session.startTime }} - {{ session.endTime }}
                    </span>
                    <span class="flex items-center gap-1">
                      <MapPin class="w-3 h-3" /> {{ getRoomName(session.roomId) }}
                    </span>
                  </div>
                </div>
                
                <!-- Status -->
                <div 
                  class="text-xs font-semibold px-2 py-1 rounded"
                  :class="getSessionStatus(session).class"
                >
                  {{ getSessionStatus(session).text }}
                </div>
              </div>
            </div>
          </div>
          
          <!-- Modal Footer -->
          <div class="p-4 border-t bg-gray-50 flex justify-end shrink-0">
            <button 
              @click="showScheduleModal = false"
              class="px-6 py-2 bg-gray-200 text-gray-700 font-bold rounded-xl hover:bg-gray-300 transition-colors"
            >
              Đóng
            </button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/useAuthStore';
import unifiedApi from '@/services/unifiedClassApi.js';
import { formatDate } from '@/views/Test/dateUtils.js';
import { 
  BookOpen,
  Users,
  MapPin,
  Clock,
  Calendar,
  CalendarIcon,
  CheckSquare,
  Inbox,
  X
} from 'lucide-vue-next';
import { useToast } from 'vue-toastification';

const router = useRouter();
const toast = useToast();
const authStore = useAuthStore();

const loading = ref(true);
const myClasses = ref([]);
const roomsData = ref([]);
const showScheduleModal = ref(false);
const selectedClass = ref(null);
const selectedClassSessions = ref([]);

const currentTeacherId = computed(() => {
  const user = authStore.user;
  return user?.id || null;
});

const loadData = async () => {
  if (!currentTeacherId.value) {
    toast.error('Vui lòng đăng nhập!');
    return;
  }

  loading.value = true;
  try {
    const allClasses = await unifiedApi.getClasses();
    roomsData.value = await unifiedApi.getRooms();
    
    // Filter classes where this teacher is assigned
    myClasses.value = allClasses.filter(c => c.teacherId === currentTeacherId.value);
  } catch (error) {
    toast.error('Lỗi khi tải danh sách lớp: ' + error.message);
  } finally {
    loading.value = false;
  }
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

const viewSchedule = async (cls) => {
  selectedClass.value = cls;
  try {
    const sessions = await unifiedApi.getSessions(cls.id);
    selectedClassSessions.value = sessions.sort((a, b) => new Date(a.date) - new Date(b.date));
    showScheduleModal.value = true;
  } catch (error) {
    toast.error('Không thể tải lịch học: ' + error.message);
  }
};

const getSessionStatus = (session) => {
  const now = new Date();
  const sessionDate = new Date(session.date);
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
  const sDate = new Date(sessionDate.getFullYear(), sessionDate.getMonth(), sessionDate.getDate());

  if (sDate < today) {
    return { text: 'Đã diễn ra', class: 'bg-gray-100 text-gray-500' };
  } else if (sDate.getTime() === today.getTime()) {
    return { text: 'Hôm nay', class: 'bg-green-100 text-green-700' };
  } else {
    return { text: 'Sắp diễn ra', class: 'bg-blue-100 text-blue-700' };
  }
};

const goToAttendance = (cls) => {
  // Navigate to attendance page with class ID
  router.push({ name: 'TeacherAttendance', params: { classId: cls.id } });
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
