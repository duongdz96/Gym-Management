<template>
  <div class="p-6">
    <!-- Header -->
    <div class="flex justify-between items-center mb-8">
      <div class="flex items-center gap-3">
        <ClipboardCheck class="w-10 h-10 text-green-600" />
        <h1 class="text-3xl font-bold bg-gradient-to-r from-green-600 to-green-700 bg-clip-text text-transparent">
          Điểm Danh
        </h1>
      </div>
      <button 
        @click="goBack"
        class="px-4 py-2 bg-gray-200 text-gray-700 rounded-xl font-semibold hover:bg-gray-300 transition-colors flex items-center gap-2"
      >
        <ArrowLeft class="w-5 h-5" />
        Quay lại
      </button>
    </div>

    <!-- Class Info -->
    <div v-if="selectedClass" class="mb-6 p-6 bg-gradient-to-r from-green-600 to-green-700 rounded-2xl shadow-md text-white">
      <div class="flex items-center gap-3 mb-2">
        <Dumbbell class="w-6 h-6" />
        <h2 class="text-2xl font-bold">{{ selectedClass.name }}</h2>
      </div>
      <p class="text-green-100">{{ selectedClass.description }}</p>
    </div>

    <!-- Calendar View for Session Selection -->
    <div v-if="!selectedSession" class="bg-white rounded-2xl shadow-md p-6 mb-6">
      <h2 class="text-xl font-bold text-gray-800 mb-4 flex items-center gap-2">
        <Calendar class="w-6 h-6 text-green-600" />
        Chọn Buổi Học
      </h2>

      <!-- Sessions List -->
      <div v-if="upcomingSessions.length > 0" class="grid gap-3">
        <div 
          v-for="session in upcomingSessions" 
          :key="session.id"
          @click="selectSession(session)"
          class="flex items-center gap-4 p-4 rounded-xl border-2 transition-all cursor-pointer hover:shadow-md"
          :class="session.id === selectedSession?.id 
            ? 'border-green-600 bg-green-50' 
            : 'border-gray-200 hover:border-green-300'"
        >
          <!-- Date Badge -->
          <div class="flex flex-col items-center justify-center w-20 rounded-lg font-bold shrink-0 p-3"
            :class="isToday(session.date) ? 'bg-green-600 text-white' : 'bg-gray-100 text-gray-700'"
          >
            <span class="text-2xl">{{ new Date(session.date).getDate() }}</span>
            <span class="text-xs">Tháng {{ new Date(session.date).getMonth() + 1 }}</span>
          </div>
          
          <!-- Session Info -->
          <div class="flex-1">
            <div class="font-bold text-gray-800 flex items-center gap-2">
              <CalendarIcon class="w-4 h-4 text-green-500" />
              {{ formatDate(session.date) }}
              <span v-if="isToday(session.date)" class="px-2 py-0.5 bg-green-500 text-white text-xs font-bold rounded">
                HÔM NAY
              </span>
            </div>
            <div class="text-sm text-gray-500 mt-1 flex items-center gap-4">
              <span class="flex items-center gap-1">
                <Clock class="w-3 h-3" /> {{ formatScheduleTime(session.startTime) }} - {{ formatScheduleTime(session.endTime) }}
              </span>
              <span class="flex items-center gap-1">
                <MapPin class="w-3 h-3" /> {{ getRoomName(session.roomId) }}
              </span>
            </div>
          </div>
          
          <!-- Attendance Stats -->
          <div class="text-right">
            <div class="text-sm text-gray-500">Đã điểm danh</div>
            <div class="text-2xl font-bold text-green-600">
              {{ getAttendanceCount(session.id) }}/{{ getRegisteredCount(session.id) }}
            </div>
          </div>

          <ChevronRight class="w-6 h-6 text-gray-400" />
        </div>
      </div>

      <!-- No Sessions -->
      <div v-else class="text-center py-8 text-gray-500">
        <Inbox class="w-12 h-12 text-gray-400 mx-auto mb-2" />
        <p>Không có buổi học sắp tới</p>
      </div>
    </div>

    <!-- Attendance Tracking -->
    <div v-if="selectedSession" class="space-y-6">
      <!-- Session Info Card -->
      <div class="bg-white rounded-2xl shadow-md p-6">
        <div class="flex justify-between items-start mb-4">
          <div>
            <h2 class="text-xl font-bold text-gray-800 flex items-center gap-2">
              <CalendarIcon class="w-6 h-6 text-green-600" />
              {{ formatDate(selectedSession.date) }}
            </h2>
            <p class="text-gray-600 mt-1">
              {{ formatScheduleTime(selectedSession.startTime) }} - {{ formatScheduleTime(selectedSession.endTime) }}
            </p>
          </div>
          <button 
            @click="selectedSession = null"
            class="px-4 py-2 bg-gray-200 text-gray-700 rounded-xl font-semibold hover:bg-gray-300 transition-colors"
          >
            Đổi buổi học
          </button>
        </div>

        <!-- Stats -->
        <div class="grid grid-cols-3 gap-4 mt-4">
          <div class="p-4 bg-blue-50 rounded-xl">
            <div class="text-sm text-blue-600 font-semibold">Tổng số</div>
            <div class="text-2xl font-bold text-blue-700">{{ registeredStudents.length }}</div>
          </div>
          <div class="p-4 bg-green-50 rounded-xl">
            <div class="text-sm text-green-600 font-semibold">Có mặt</div>
            <div class="text-2xl font-bold text-green-700">{{ presentCount }}</div>
          </div>
          <div class="p-4 bg-red-50 rounded-xl">
            <div class="text-sm text-red-600 font-semibold">Vắng</div>
            <div class="text-2xl font-bold text-red-700">{{ absentCount }}</div>
          </div>
        </div>
      </div>

      <!-- Student List -->
      <div class="bg-white rounded-2xl shadow-md p-6">
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-lg font-bold text-gray-800">Danh sách học viên</h3>
          <button 
            @click="finalizeSession"
            :disabled="finalizing || sessionFinalized"
            class="px-4 py-2 bg-gradient-to-r from-orange-600 to-orange-700 text-white rounded-xl font-semibold hover:shadow-lg transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
          >
            <Lock class="w-4 h-4" />
            {{ sessionFinalized ? 'Đã khóa' : 'Khóa buổi học' }}
          </button>
        </div>

        <!-- Loading -->
        <div v-if="loadingStudents" class="text-center py-8">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600 mx-auto mb-4"></div>
          Đang tải danh sách...
        </div>

        <!-- Students -->
        <div v-else-if="registeredStudents.length > 0" class="space-y-3">
          <div 
            v-for="student in registeredStudents" 
            :key="student.id"
            class="p-4 border-2 rounded-xl transition-all"
            :class="getStudentAttendance(student.id)?.status === 'PRESENT' 
              ? 'border-green-300 bg-green-50' 
              : getStudentAttendance(student.id)?.status === 'ABSENT'
                ? 'border-red-300 bg-red-50'
                : 'border-gray-200'"
          >
            <div class="flex items-center gap-4">
              <!-- Avatar -->
              <div class="w-12 h-12 rounded-full bg-gradient-to-br from-green-500 to-green-600 flex items-center justify-center text-white font-bold text-lg shrink-0">
                {{ student.name.charAt(0) }}
              </div>

              <!-- Student Info -->
              <div class="flex-1">
                <div class="font-bold text-gray-800">{{ student.name }}</div>
                <div class="text-sm text-gray-600">{{ student.email }}</div>
              </div>

              <!-- Attendance Toggle -->
              <div class="flex items-center gap-2">
                <button 
                  @click="markPresent(student)"
                  :disabled="sessionFinalized"
                  class="px-4 py-2 rounded-lg font-semibold transition-all disabled:opacity-50 disabled:cursor-not-allowed"
                  :class="getStudentAttendance(student.id)?.status === 'PRESENT'
                    ? 'bg-green-600 text-white'
                    : 'bg-gray-200 text-gray-700 hover:bg-green-100'"
                >
                  <Check class="w-5 h-5" />
                </button>
                <button 
                  @click="markAbsent(student)"
                  :disabled="sessionFinalized"
                  class="px-4 py-2 rounded-lg font-semibold transition-all disabled:opacity-50 disabled:cursor-not-allowed"
                  :class="getStudentAttendance(student.id)?.status === 'ABSENT'
                    ? 'bg-red-600 text-white'
                    : 'bg-gray-200 text-gray-700 hover:bg-red-100'"
                >
                  <X class="w-5 h-5" />
                </button>
              </div>
            </div>

            <!-- Notes -->
            <div v-if="getStudentAttendance(student.id)" class="mt-3">
              <textarea 
                v-model="getStudentAttendance(student.id).notes"
                @blur="updateNotes(student)"
                :disabled="sessionFinalized"
                placeholder="Ghi chú..."
                class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:border-green-500 focus:ring-2 focus:ring-green-100 outline-none transition-all disabled:bg-gray-100 disabled:cursor-not-allowed"
                rows="2"
              ></textarea>
            </div>
          </div>
        </div>

        <!-- No Students -->
        <div v-else class="text-center py-8 text-gray-500">
          <Users class="w-12 h-12 text-gray-400 mx-auto mb-2" />
          <p>Chưa có học viên đăng ký</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useAuthStore } from '@/stores/useAuthStore';
import unifiedApi from '@/services/unifiedClassApi.js';
import api from '@/services/api.js';
import { formatDate } from '@/views/Test/dateUtils.js';
import { 
  ClipboardCheck,
  ArrowLeft,
  Dumbbell,
  Calendar,
  CalendarIcon,
  Clock,
  MapPin,
  ChevronRight,
  Inbox,
  Check,
  X,
  Lock,
  Users
} from 'lucide-vue-next';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

const classId = ref(route.params.classId);
const selectedClass = ref(null);
const selectedSession = ref(null);
const upcomingSessions = ref([]);
const registeredStudents = ref([]);
const attendanceRecords = ref([]);
const loadingStudents = ref(false);
const finalizing = ref(false);
const sessionFinalized = ref(false);
const roomsData = ref([]);

const currentTeacherId = computed(() => {
  const user = authStore.user;
  return user?.id || null;
});

const presentCount = computed(() => {
  return attendanceRecords.value.filter(a => a.status === 'PRESENT').length;
});

const absentCount = computed(() => {
  return attendanceRecords.value.filter(a => a.status === 'ABSENT').length;
});

const loadClassData = async () => {
  try {
    const cls = await unifiedApi.getClass(classId.value);
    selectedClass.value = cls;
    
    const sessions = await unifiedApi.getSessions(classId.value);
    roomsData.value = await unifiedApi.getRooms();
    
    // Filter upcoming sessions
    const now = new Date();
    upcomingSessions.value = sessions
      .filter(s => {
        const sessionDate = new Date(s.date || s.startTime);
        return sessionDate >= new Date(now.getFullYear(), now.getMonth(), now.getDate());
      })
      .sort((a, b) => new Date(a.date || a.startTime) - new Date(b.date || b.startTime));
  } catch (error) {
    console.error('Error loading class data:', error);
    alert('❌ Lỗi khi tải thông tin lớp học');
  }
};

const selectSession = async (session) => {
  selectedSession.value = session;
  await loadSessionData();
};

const loadSessionData = async () => {
  if (!selectedSession.value) return;
  
  loadingStudents.value = true;
  try {
    // Get registered students for this session
    const registrations = await api.get(`/member-registrations/schedule/${selectedSession.value.id}`);
    registeredStudents.value = registrations.data.map(r => ({
      id: r.member.id,
      name: r.member.fullName,
      email: r.member.email
    }));

    // Get attendance records
    const attendance = await api.get(`/attendance/students/${selectedSession.value.id}`);
    attendanceRecords.value = attendance.data.map(a => ({
      id: a.id,
      memberId: a.member.id,
      status: a.status,
      notes: a.notes || '',
      checkedInAt: a.checkedInAt
    }));

    // Check if session is finalized (all students have attendance records)
    sessionFinalized.value = registeredStudents.value.length > 0 && 
                             attendanceRecords.value.length === registeredStudents.value.length;
  } catch (error) {
    console.error('Error loading session data:', error);
    if (error.response?.status !== 404) {
      alert('❌ Lỗi khi tải dữ liệu buổi học');
    }
  } finally {
    loadingStudents.value = false;
  }
};

const getStudentAttendance = (memberId) => {
  return attendanceRecords.value.find(a => a.memberId === memberId);
};

const markPresent = async (student) => {
  const existing = getStudentAttendance(student.id);
  
  try {
    if (existing) {
      // Update existing record
      await api.put('/attendance/students/update', null, {
        params: {
          scheduleId: selectedSession.value.id,
          memberId: student.id,
          teacherId: currentTeacherId.value,
          status: 'PRESENT'
        }
      });
      existing.status = 'PRESENT';
    } else {
      // Create new record
      const response = await api.post('/attendance/students/checkin', {
        classSchedule: { id: selectedSession.value.id },
        member: { id: student.id },
        checkedInBy: { id: currentTeacherId.value },
        status: 'PRESENT',
        notes: ''
      });
      attendanceRecords.value.push({
        id: response.data.id,
        memberId: student.id,
        status: 'PRESENT',
        notes: '',
        checkedInAt: response.data.checkedInAt
      });
    }
  } catch (error) {
    console.error('Error marking present:', error);
    alert('❌ Lỗi khi điểm danh: ' + (error.response?.data?.message || error.message));
  }
};

const markAbsent = async (student) => {
  const existing = getStudentAttendance(student.id);
  
  try {
    if (existing) {
      // Update existing record
      await api.put('/attendance/students/update', null, {
        params: {
          scheduleId: selectedSession.value.id,
          memberId: student.id,
          teacherId: currentTeacherId.value,
          status: 'ABSENT'
        }
      });
      existing.status = 'ABSENT';
    } else {
      // Create new record
      const response = await api.post('/attendance/students/checkin', {
        classSchedule: { id: selectedSession.value.id },
        member: { id: student.id },
        checkedInBy: { id: currentTeacherId.value },
        status: 'ABSENT',
        notes: ''
      });
      attendanceRecords.value.push({
        id: response.data.id,
        memberId: student.id,
        status: 'ABSENT',
        notes: '',
        checkedInAt: response.data.checkedInAt
      });
    }
  } catch (error) {
    console.error('Error marking absent:', error);
    alert('❌ Lỗi khi đánh dấu vắng: ' + (error.response?.data?.message || error.message));
  }
};

const updateNotes = async (student) => {
  const attendance = getStudentAttendance(student.id);
  if (!attendance) return;

  try {
    await api.put('/attendance/students/update', null, {
      params: {
        scheduleId: selectedSession.value.id,
        memberId: student.id,
        teacherId: currentTeacherId.value,
        status: attendance.status
      }
    });
  } catch (error) {
    console.error('Error updating notes:', error);
  }
};

const finalizeSession = async () => {
  if (!confirm('Bạn có chắc muốn khóa buổi học này? Các học viên chưa điểm danh sẽ được đánh dấu vắng.')) {
    return;
  }

  finalizing.value = true;
  try {
    await api.post(`/attendance/students/auto-absent/${selectedSession.value.id}`);
    alert('✅ Đã khóa buổi học thành công!');
    sessionFinalized.value = true;
    await loadSessionData(); // Reload to get updated attendance
  } catch (error) {
    console.error('Error finalizing session:', error);
    alert('❌ Lỗi khi khóa buổi học: ' + (error.response?.data || error.message));
  } finally {
    finalizing.value = false;
  }
};

const getAttendanceCount = (sessionId) => {
  // This would need to be fetched from API in real implementation
  return 0;
};

const getRegisteredCount = (sessionId) => {
  // This would need to be fetched from API in real implementation
  return 0;
};

const getRoomName = (roomId) => {
  const room = roomsData.value.find(r => r.id === roomId);
  return room ? room.name : 'Không xác định';
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

const isToday = (dateStr) => {
  const today = new Date();
  const date = new Date(dateStr);
  return date.getDate() === today.getDate() &&
         date.getMonth() === today.getMonth() &&
         date.getFullYear() === today.getFullYear();
};

const goBack = () => {
  router.back();
};

onMounted(() => {
  loadClassData();
});
</script>
