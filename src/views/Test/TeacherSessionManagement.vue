<template>
  <div class="p-6">
    <!-- Header -->
    <div class="flex justify-between items-center mb-8">
      <div class="flex items-center gap-3">
        <CalendarCheck class="w-10 h-10 text-blue-600" />
        <h1 class="text-3xl font-bold bg-gradient-to-r from-blue-600 to-blue-700 bg-clip-text text-transparent">
          Quản Lý Buổi Dạy
        </h1>
      </div>
      <div class="flex items-center gap-2">
        <div class="text-sm text-gray-500">{{ formatDate(selectedDateStr) }}</div>
        <span v-if="isToday" class="px-2 py-1 bg-blue-100 text-blue-700 text-xs font-bold rounded">Hôm nay</span>
      </div>
    </div>

    <!-- Date Picker -->
    <div class="mb-6 flex gap-4 items-center">
      <button 
        @click="changeDate(-1)"
        class="p-2 rounded-lg border border-gray-200 hover:bg-gray-50"
      >
        <ChevronLeft class="w-5 h-5" />
      </button>
      
      <div class="flex flex-col gap-1">
        <input 
          type="date" 
          v-model="selectedDateStr"
          class="px-4 py-2 border-2 border-gray-200 rounded-xl focus:border-blue-600 focus:ring-2 focus:ring-blue-100 outline-none"
        />
      </div>
      
      <button 
        @click="changeDate(1)"
        class="p-2 rounded-lg border border-gray-200 hover:bg-gray-50"
      >
        <ChevronRight class="w-5 h-5" />
      </button>
      
      <button 
        @click="goToToday"
        class="px-4 py-2 bg-blue-600 text-white rounded-xl font-semibold hover:bg-blue-700"
      >
        Hôm nay
      </button>
    </div>

    <!-- Sessions List -->
    <div v-if="loading" class="text-center py-16">
      <div class="text-gray-500">Đang tải...</div>
    </div>

    <div v-else-if="todaySessions.length === 0" class="text-center py-16">
      <Calendar class="w-16 h-16 text-gray-300 mx-auto mb-4" />
      <p class="text-gray-500 text-lg">Không có buổi dạy nào trong ngày này</p>
    </div>

    <div v-else class="grid gap-4">
      <div 
        v-for="session in todaySessions" 
        :key="session.id"
        class="bg-white rounded-2xl shadow-md border border-gray-100 overflow-hidden"
      >
        <!-- Session Header -->
        <div 
          class="p-5 flex justify-between items-center"
          :class="{
            'bg-gradient-to-r from-green-50 to-green-100 border-b-2 border-green-200': session.status === 'CONFIRMED',
            'bg-gradient-to-r from-blue-50 to-blue-100 border-b-2 border-blue-200': session.status === 'SCHEDULED',
            'bg-gradient-to-r from-red-50 to-red-100 border-b-2 border-red-200': session.status === 'CANCELLED',
            'bg-gradient-to-r from-gray-50 to-gray-100 border-b-2 border-gray-200': session.status === 'COMPLETED'
          }"
        >
          <div class="flex items-center gap-4">
            <div class="w-16 h-16 rounded-xl bg-white flex flex-col items-center justify-center shadow-sm">
              <div class="text-2xl font-bold text-blue-600">{{ session.startTime.split(':')[0] }}</div>
              <div class="text-xs text-gray-500">{{ session.startTime.split(':')[1] }}</div>
            </div>
            
            <div>
              <h3 class="text-xl font-bold text-gray-800">{{ getClassName(session.classId) }}</h3>
              <div class="flex items-center gap-4 text-sm text-gray-600 mt-1">
                <span class="flex items-center gap-1">
                  <Clock class="w-4 h-4" /> {{ session.startTime }} - {{ session.endTime }}
                </span>
                <span class="flex items-center gap-1">
                  <MapPin class="w-4 h-4" /> {{ getRoomName(session.roomId) }}
                </span>
              </div>
            </div>
          </div>

          <!-- Status Badge -->
          <div 
            class="px-4 py-2 rounded-xl font-bold text-sm"
            :class="{
              'bg-green-500 text-white': session.status === 'CONFIRMED',
              'bg-blue-500 text-white': session.status === 'SCHEDULED',
              'bg-red-500 text-white': session.status === 'CANCELLED',
              'bg-gray-500 text-white': session.status === 'COMPLETED'
            }"
          >
            {{ getStatusText(session.status) }}
          </div>
        </div>

        <!-- Session Body -->
        <div class="p-5 space-y-4">
          <!-- Leave Request Notice -->
          <div v-if="session.teacherLeaveRequest" class="p-3 bg-yellow-50 border-2 border-yellow-300 rounded-xl">
            <div class="flex items-center gap-2 text-yellow-800 font-semibold mb-1">
              <AlertTriangle class="w-5 h-5" />
              Đã gửi yêu cầu nghỉ
            </div>
            <div class="text-sm text-yellow-700">{{ session.teacherLeaveReason }}</div>
          </div>

          <!-- Actions -->
          <div class="flex gap-3">
            <!-- Confirm Attendance -->
            <button 
              v-if="!session.teacherConfirmed && session.status === 'SCHEDULED' && !session.teacherLeaveRequest"
              @click="confirmAttendance(session)"
              class="flex-1 px-4 py-3 bg-gradient-to-r from-green-500 to-green-600 text-white rounded-xl font-semibold hover:shadow-lg transition-all flex items-center justify-center gap-2"
            >
              <CheckCircle class="w-5 h-5" />
              Xác nhận có mặt
            </button>

            <!-- Request Leave -->
            <button 
              v-if="!session.teacherLeaveRequest && session.status !== 'CANCELLED' && session.status !== 'COMPLETED'"
              @click="openLeaveRequest(session)"
              class="flex-1 px-4 py-3 bg-gradient-to-r from-orange-500 to-orange-600 text-white rounded-xl font-semibold hover:shadow-lg transition-all flex items-center justify-center gap-2"
            >
              <XCircle class="w-5 h-5" />
              Xin nghỉ
            </button>

            <!-- View/Mark Attendance -->
            <button 
              v-if="session.status === 'CONFIRMED' || session.status === 'COMPLETED'"
              @click="openAttendance(session)"
              class="flex-1 px-4 py-3 bg-gradient-to-r from-blue-500 to-blue-600 text-white rounded-xl font-semibold hover:shadow-lg transition-all flex items-center justify-center gap-2"
            >
              <Users class="w-5 h-5" />
              Điểm danh ({{ getAttendanceCount(session.id) }})
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Leave Request Modal -->
    <transition
      enter-active-class="transition duration-200 ease-out"
      enter-from-class="opacity-0 scale-95"
      enter-to-class="opacity-100 scale-100"
      leave-active-class="transition duration-150 ease-in"
      leave-from-class="opacity-100 scale-100"
      leave-to-class="opacity-0 scale-95"
    >
      <div 
        v-if="showLeaveModal"
        class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50"
        @click.self="showLeaveModal = false"
      >
        <div class="bg-white rounded-2xl w-full max-w-md shadow-2xl">
          <div class="p-6 border-b">
            <h3 class="text-xl font-bold text-gray-800">Xin nghỉ buổi dạy</h3>
          </div>
          
          <div class="p-6">
            <label class="block text-sm font-semibold text-gray-700 mb-2">Lý do nghỉ:</label>
            <textarea 
              v-model="leaveReason"
              rows="4"
              class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-blue-600 focus:ring-2 focus:ring-blue-100 outline-none"
              placeholder="Nhập lý do xin nghỉ..."
            ></textarea>
          </div>
          
          <div class="p-4 border-t bg-gray-50 flex justify-end gap-3">
            <button 
              @click="showLeaveModal = false"
              class="px-6 py-2 bg-gray-200 text-gray-700 font-bold rounded-xl hover:bg-gray-300"
            >
              Hủy
            </button>
            <button 
              @click="submitLeaveRequest"
              class="px-6 py-2 bg-orange-600 text-white font-bold rounded-xl hover:bg-orange-700"
            >
              Gửi yêu cầu
            </button>
          </div>
        </div>
      </div>
    </transition>

    <!-- Attendance Modal -->
    <transition
      enter-active-class="transition duration-200 ease-out"
      enter-from-class="opacity-0 scale-95"
      enter-to-class="opacity-100 scale-100"
      leave-active-class="transition duration-150 ease-in"
      leave-from-class="opacity-100 scale-100"
      leave-to-class="opacity-0 scale-95"
    >
      <div 
        v-if="showAttendanceModal"
        class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50"
        @click.self="showAttendanceModal = false"
      >
        <div class="bg-white rounded-2xl w-full max-w-2xl shadow-2xl max-h-[80vh] flex flex-col">
          <div class="p-6 border-b shrink-0">
            <h3 class="text-xl font-bold text-gray-800">Điểm danh học viên</h3>
            <p class="text-sm text-gray-500 mt-1">{{ selectedSession?.date }} - {{ selectedSession?.startTime }}</p>
          </div>
          
          <div class="p-6 overflow-y-auto flex-1">
            <div v-if="attendanceList.length === 0" class="text-center py-8 text-gray-500">
              Chưa có học viên đăng ký
            </div>
            
            <div v-else class="space-y-3">
              <div 
                v-for="attendance in attendanceList" 
                :key="attendance.id"
                class="flex items-center justify-between p-4 rounded-xl border-2 hover:border-blue-200 transition-all"
                :class="{
                  'border-green-200 bg-green-50': attendance.status === 'PRESENT',
                  'border-red-200 bg-red-50': attendance.status === 'ABSENT',
                  'border-gray-200 bg-white': attendance.status === 'REGISTERED'
                }"
              >
                <div class="flex items-center gap-3">
                  <div class="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center text-blue-600 font-bold">
                    {{ attendance.studentName.charAt(0) }}
                  </div>
                  <div>
                    <div class="font-bold text-gray-800">{{ attendance.studentName }}</div>
                    <div class="text-xs text-gray-500">{{ attendance.studentEmail }}</div>
                  </div>
                </div>
                
                <div class="flex gap-2">
                  <button 
                    @click="markAttendance(attendance, 'PRESENT')"
                    class="px-4 py-2 rounded-lg font-semibold transition-all"
                    :class="attendance.status === 'PRESENT' 
                      ? 'bg-green-500 text-white' 
                      : 'bg-gray-100 text-gray-600 hover:bg-green-100'"
                  >
                    Có mặt
                  </button>
                  <button 
                    @click="markAttendance(attendance, 'ABSENT')"
                    class="px-4 py-2 rounded-lg font-semibold transition-all"
                    :class="attendance.status === 'ABSENT' 
                      ? 'bg-red-500 text-white' 
                      : 'bg-gray-100 text-gray-600 hover:bg-red-100'"
                  >
                    Vắng
                  </button>
                </div>
              </div>
            </div>
          </div>
          
          <div class="p-4 border-t bg-gray-50 flex justify-end shrink-0">
            <button 
              @click="showAttendanceModal = false"
              class="px-6 py-2 bg-gray-200 text-gray-700 font-bold rounded-xl hover:bg-gray-300"
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
import { ref, computed, onMounted, watch } from 'vue';
import mockApi, { studentRegistrations, studentAttendances } from './mockData.js';
import { formatDate } from './dateUtils.js';
import { 
  CalendarCheck, 
  Calendar,
  ChevronLeft, 
  ChevronRight, 
  Clock, 
  MapPin, 
  CheckCircle, 
  XCircle, 
  Users,
  AlertTriangle
} from 'lucide-vue-next';
import { useToast } from 'vue-toastification';

const currentTeacherId = ref(1);
const selectedDate = ref(new Date());
const selectedDateStr = ref(new Date().toISOString().split('T')[0]);
const todaySessions = ref([]);
const classes = ref([]);
const rooms = ref([]);
const toast = useToast();
const loading = ref(false);

// Leave Request Modal
const showLeaveModal = ref(false);
const selectedSession = ref(null);
const leaveReason = ref('');

// Attendance Modal
const showAttendanceModal = ref(false);
const attendanceList = ref([]);

// Computed
const isToday = computed(() => {
  const today = new Date().toISOString().split('T')[0];
  return selectedDateStr.value === today;
});

const loadData = async () => {
  loading.value = true;
  try {
    
    // Load classes and rooms for display
    classes.value = await mockApi.getClasses();
    rooms.value = await mockApi.getRooms();
    
    
    // Load sessions for selected date
    const dateStr = selectedDateStr.value;
    const allSessions = await mockApi.getAllSessions();
    
    todaySessions.value = allSessions.filter(s => {
      const matchDate = s.date === dateStr;
      const matchTeacher = s.teacherId === currentTeacherId.value;
      return matchDate && matchTeacher;
    }).sort((a, b) => a.startTime.localeCompare(b.startTime));
    
  } catch (error) {
    toast.error('Lỗi khi tải dữ liệu');
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadData();
});

watch(selectedDateStr, () => {
  selectedDate.value = new Date(selectedDateStr.value);
  loadData();
});

const changeDate = (days) => {
  const newDate = new Date(selectedDate.value);
  newDate.setDate(newDate.getDate() + days);
  selectedDateStr.value = newDate.toISOString().split('T')[0];
};

const goToToday = () => {
  selectedDateStr.value = new Date().toISOString().split('T')[0];
};

const showDatePicker = () => {
  // For now, user can use prev/next buttons or "Hôm nay" button
  // Can implement custom date picker modal later
};

const getClassName = (classId) => {
  const cls = classes.value.find(c => c.id === classId);
  return cls ? cls.name : 'Unknown';
};

const getRoomName = (roomId) => {
  const room = rooms.value.find(r => r.id === roomId);
  return room ? room.name : 'Unknown';
};

const getStatusText = (status) => {
  const map = {
    'SCHEDULED': 'Chưa xác nhận',
    'CONFIRMED': 'Đã xác nhận',
    'IN_PROGRESS': 'Đang diễn ra',
    'COMPLETED': 'Đã hoàn thành',
    'CANCELLED': 'Đã hủy',
    'TEACHER_ABSENT': 'Giáo viên vắng'
  };
  return map[status] || status;
};

const getAttendanceCount = (sessionId) => {
  const session = todaySessions.value.find(s => s.id === sessionId);
  if (!session) return '0/0';
  
  // Get registrations for this class
  const registrations = studentRegistrations.filter(r => 
    r.classId === session.classId && r.status === 'active'
  );
  const totalStudents = registrations.length;
  
  // Get attendance records
  const attendances = studentAttendances.filter(a => a.sessionId === sessionId);
  const presentCount = attendances.filter(a => a.status === 'PRESENT').length;
  
  return `${presentCount}/${totalStudents}`;
};

const confirmAttendance = async (session) => {
  if (confirm('Xác nhận bạn sẽ có mặt để dạy buổi này?')) {
    try {
      await mockApi.confirmTeacherAttendance(session.id, currentTeacherId.value);
      toast.success('Đã xác nhận có mặt!');
      await loadData();
    } catch (error) {
      toast.error('Có lỗi xảy ra');
    }
  }
};

const openLeaveRequest = (session) => {
  selectedSession.value = session;
  leaveReason.value = '';
  showLeaveModal.value = true;
};

const submitLeaveRequest = async () => {
  if (!leaveReason.value.trim()) {
    toast.warning('Vui lòng nhập lý do nghỉ');
    return;
  }
  
  try {
    await mockApi.requestTeacherLeave(selectedSession.value.id, currentTeacherId.value, leaveReason.value);
    toast.success('Đã gửi yêu cầu nghỉ! Chờ quản lý duyệt.');
    showLeaveModal.value = false;
    await loadData();
  } catch (error) {
    toast.error('Có lỗi xảy ra');
  }
};

const openAttendance = async (session) => {
  selectedSession.value = session;
  try {
    // Initialize attendance if not exists
    await mockApi.initializeSessionAttendance(session.id);
    // Load attendance list
    attendanceList.value = await mockApi.getSessionAttendance(session.id);
    showAttendanceModal.value = true;
  } catch (error) {
    toast.error('Có lỗi xảy ra');
  }
};

const markAttendance = async (attendance, status) => {
  try {
    await mockApi.markStudentAttendance(
      attendance.sessionId, 
      attendance.studentId, 
      status, 
      currentTeacherId.value
    );
    // Reload attendance list
    attendanceList.value = await mockApi.getSessionAttendance(selectedSession.value.id);
    
    // Reload sessions to update count in button
    await loadData();
  } catch (error) {
    toast.error('Có lỗi xảy ra');
  }
};
</script>
