<template>
  <div class="p-3 sm:p-6">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3 sm:gap-0 mb-4 sm:mb-8">
      <div class="flex items-center gap-2 sm:gap-3">
        <ClipboardCheck class="w-8 h-8 sm:w-10 sm:h-10 text-green-600" />
        <h1
          class="text-2xl sm:text-3xl font-bold bg-gradient-to-r from-green-600 to-green-700 bg-clip-text text-transparent">
          Điểm Danh
        </h1>
      </div>
      <button @click="goBack"
        class="px-3 sm:px-4 py-2 bg-gray-200 text-gray-700 rounded-xl font-semibold hover:bg-gray-300 transition-colors flex items-center gap-2 text-sm sm:text-base">
        <ArrowLeft class="w-4 h-4 sm:w-5 sm:h-5" />
        Quay lại
      </button>
    </div>

    <!-- Class Info -->
    <div v-if="selectedClass"
      class="mb-4 sm:mb-6 p-3 sm:p-6 bg-gradient-to-r from-green-600 to-green-700 rounded-xl sm:rounded-2xl shadow-md text-white">
      <div class="flex items-center gap-2 sm:gap-3 mb-2">
        <Dumbbell class="w-5 h-5 sm:w-6 sm:h-6" />
        <h1
          class="text-xl sm:text-3xl font-bold bg-gradient-to-r from-green-600 to-green-700 bg-clip-text text-transparent">
          Lịch Dạy & Điểm Danh
        </h1>
      </div>
      <p class="text-green-100 text-sm sm:text-base">{{ selectedClass.description }}</p>
    </div>

    <!-- Calendar View for Session Selection -->
    <div v-if="!selectedSession" class="bg-white rounded-xl sm:rounded-2xl shadow-md p-3 sm:p-6 mb-4 sm:mb-6">
      <h2 class="text-lg sm:text-xl font-bold text-gray-800 mb-3 sm:mb-4 flex items-center gap-2">
        <Calendar class="w-5 h-5 sm:w-6 sm:h-6 text-green-600" />
        Chọn Buổi Học
      </h2>

      <!-- Search & Filter -->
      <div class="mb-3 sm:mb-4 flex flex-col sm:flex-row gap-2 sm:gap-3">
        <div class="relative flex-1">
          <input v-model="sessionSearchQuery" type="text" placeholder="Tìm kiếm lớp học..."
            class="w-full pl-8 sm:pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-green-500 outline-none text-xs sm:text-sm" />
          <Search class="w-3 h-3 sm:w-4 sm:h-4 text-gray-400 absolute left-3 top-1/2 -translate-y-1/2" />
        </div>
        <select v-model="sessionFilterStatus"
          class="px-3 py-2 border border-gray-300 rounded-lg outline-none text-xs sm:text-sm bg-white focus:border-green-500">
          <option value="all">Tất cả buổi học</option>
          <option value="upcoming">Sắp tới</option>
          <option value="today">Hôm nay</option>
        </select>
      </div>

      <!-- Sessions List -->
      <div v-if="filteredSessions.length > 0" class="grid gap-2 sm:gap-3">
        <div v-for="session in filteredSessions" :key="session.id" @click="selectSession(session)"
          class="flex items-center gap-2 sm:gap-4 p-2 sm:p-4 rounded-lg sm:rounded-xl border-2 transition-all cursor-pointer hover:shadow-md"
          :class="session.id === selectedSession?.id
            ? 'border-green-600 bg-green-50'
            : 'border-gray-200 hover:border-green-300'">
          <!-- Date Badge -->
          <div class="flex flex-col items-center justify-center w-14 sm:w-20 rounded-lg font-bold shrink-0 p-2 sm:p-3"
            :class="isToday(session.date) ? 'bg-green-600 text-white' : 'bg-gray-100 text-gray-700'">
            <span class="text-xl sm:text-2xl">{{ new Date(session.date).getDate() }}</span>
            <span class="text-[10px] sm:text-xs">Tháng {{ new Date(session.date).getMonth() + 1 }}</span>
          </div>

          <!-- Session Info -->
          <div class="flex-1 min-w-0">
            <div
              class="text-[10px] sm:text-xs font-bold text-green-700 uppercase mb-1 leading-tight break-all line-clamp-2"
              :title="session.className">
              {{ session.className }}
            </div>

            <div class="font-bold text-gray-800 flex items-center gap-1 sm:gap-2 text-sm sm:text-base">
              <CalendarIcon class="w-3 h-3 sm:w-4 sm:h-4 text-green-500 shrink-0" />
              <span class="truncate">{{ formatDate(session.date) }}</span>
              <span v-if="isToday(session.date)"
                class="px-1.5 sm:px-2 py-0.5 bg-green-500 text-white text-[10px] sm:text-xs font-bold rounded shrink-0">
                HÔM NAY
              </span>
            </div>

            <div class="text-xs sm:text-sm text-gray-500 mt-0.5 sm:mt-1 flex flex-wrap items-center gap-2 sm:gap-4">
              <span class="flex items-center gap-1 shrink-0">
                <Clock class="w-3 h-3 shrink-0" /> {{ formatScheduleTime(session.startTime) }} - {{
                  formatScheduleTime(session.endTime) }}
              </span>
              <span class="flex items-center gap-1 min-w-0">
                <MapPin class="w-3 h-3 shrink-0" />
                <span class="truncate">{{ getRoomName(session.roomId) }}</span>
              </span>
            </div>
          </div>

          <!-- Attendance Stats -->
          <div class="text-right">
            <div class="text-[10px] sm:text-sm text-gray-500">Đã điểm danh</div>
            <div class="text-xl sm:text-2xl font-bold text-green-600">
              {{ getAttendanceCount(session.id) }}/{{ getRegisteredCount(session.id) }}
            </div>
          </div>

          <ChevronRight class="w-5 h-5 sm:w-6 sm:h-6 text-gray-400 shrink-0" />
        </div>
      </div>

      <!-- No Sessions -->
      <div v-else class="text-center py-6 sm:py-8 text-gray-500">
        <Inbox class="w-10 h-10 sm:w-12 sm:h-12 text-gray-400 mx-auto mb-2" />
        <p class="text-sm sm:text-base">Không có buổi học sắp tới</p>
      </div>
    </div>

    <!-- Attendance Tracking -->
    <div v-if="selectedSession" class="space-y-4 sm:space-y-6">
      <!-- Session Info Card -->
      <div class="bg-white rounded-xl sm:rounded-2xl shadow-md p-3 sm:p-6">
        <div class="flex flex-col sm:flex-row justify-between items-start gap-3 sm:gap-0 mb-4">
          <div>
            <h2 class="text-lg sm:text-xl font-bold text-gray-800 flex items-center gap-2">
              <CalendarIcon class="w-5 h-5 sm:w-6 sm:h-6 text-green-600" />
              {{ formatDate(selectedSession.date) }}
            </h2>
            <p class="text-gray-600 mt-1 text-sm sm:text-base">
              {{ formatScheduleTime(selectedSession.startTime) }} - {{ formatScheduleTime(selectedSession.endTime) }}
            </p>
          </div>
          <button @click="handleBackToList"
            class="w-full sm:w-auto px-3 sm:px-4 py-2 bg-gray-200 text-gray-700 rounded-xl font-semibold hover:bg-gray-300 transition-colors text-sm sm:text-base">
            Đổi buổi học
          </button>
        </div>

        <!-- Stats -->
        <div class="grid grid-cols-3 gap-2 sm:gap-4 mt-4">
          <div class="p-3 sm:p-4 bg-blue-50 rounded-xl">
            <div class="text-xs sm:text-sm text-blue-600 font-semibold">Tổng số</div>
            <div class="text-xl sm:text-2xl font-bold text-blue-700">{{ registeredStudents.length }}</div>
          </div>
          <div class="p-3 sm:p-4 bg-green-50 rounded-xl">
            <div class="text-xs sm:text-sm text-green-600 font-semibold">Có mặt</div>
            <div class="text-xl sm:text-2xl font-bold text-green-700">{{ presentCount }}</div>
          </div>
          <div class="p-3 sm:p-4 bg-red-50 rounded-xl">
            <div class="text-xs sm:text-sm text-red-600 font-semibold">Vắng</div>
            <div class="text-xl sm:text-2xl font-bold text-red-700">{{ absentCount }}</div>
          </div>
        </div>
      </div>

      <!-- Student List -->
      <div class="bg-white rounded-xl sm:rounded-2xl shadow-md p-3 sm:p-6">
        <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3 sm:gap-0 mb-4">
          <h3 class="text-base sm:text-lg font-bold text-gray-800">Danh sách học viên</h3>

          <button @click="autoAbsent" :disabled="finalizing"
            class="w-full sm:w-auto px-3 sm:px-4 py-2 bg-gradient-to-r from-orange-600 to-orange-700 text-white rounded-xl font-semibold hover:shadow-lg transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2 text-sm sm:text-base">
            <AlertTriangle class="w-3 h-3 sm:w-4 sm:h-4" />
            Chốt sổ
          </button>
        </div>

        <!-- Search Student -->
        <div class="mb-3 sm:mb-4">
          <div class="relative">
            <input v-model="studentSearchQuery" type="text" placeholder="Tìm kiếm học viên..."
              class="w-full pl-8 sm:pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-green-500 outline-none text-xs sm:text-sm" />
            <Search class="w-3 h-3 sm:w-4 sm:h-4 text-gray-400 absolute left-3 top-1/2 -translate-y-1/2" />
          </div>
        </div>

        <!-- Loading -->
        <div v-if="loadingStudents" class="text-center py-6 sm:py-8">
          <div class="animate-spin rounded-full h-10 w-10 sm:h-12 sm:w-12 border-b-2 border-green-600 mx-auto mb-4">
          </div>
          <p class="text-sm sm:text-base">Đang tải danh sách...</p>
        </div>

        <!-- Students -->
        <div v-else-if="filteredStudents.length > 0" class="space-y-2 sm:space-y-3">
          <div v-for="student in filteredStudents" :key="student.id"
            class="p-2 sm:p-4 border-2 rounded-lg sm:rounded-xl transition-all" :class="getStudentAttendance(student.id)?.status === 'PRESENT'
              ? 'border-green-300 bg-green-50'
              : getStudentAttendance(student.id)?.status === 'ABSENT'
                ? 'border-red-300 bg-red-50'
                : 'border-gray-200'">
            <div class="flex items-center gap-2 sm:gap-4">
              <!-- Avatar -->
              <div
                class="w-10 h-10 sm:w-12 sm:h-12 rounded-full bg-gradient-to-br from-green-500 to-green-600 flex items-center justify-center text-white font-bold text-base sm:text-lg shrink-0">
                {{ student.name.charAt(0) }}
              </div>

              <!-- Student Info -->
              <div class="flex-1 min-w-0">
                <div class="font-bold text-gray-800 text-sm sm:text-base truncate">{{ student.name }}</div>
                <div class="text-xs sm:text-sm text-gray-600 truncate">{{ student.email }}</div>
              </div>

              <!-- Attendance Toggle -->
              <div class="flex items-center gap-1 sm:gap-2 shrink-0">
                <button @click="markPresent(student)"
                  class="px-2 sm:px-4 py-1.5 sm:py-2 rounded-lg font-semibold transition-all" :class="getStudentAttendance(student.id)?.status === 'PRESENT'
                    ? 'bg-green-600 text-white'
                    : 'bg-gray-200 text-gray-700 hover:bg-green-100'">
                  <Check class="w-4 h-4 sm:w-5 sm:h-5" />
                </button>
                <button @click="markAbsent(student)"
                  class="px-2 sm:px-4 py-1.5 sm:py-2 rounded-lg font-semibold transition-all" :class="getStudentAttendance(student.id)?.status === 'ABSENT'
                    ? 'bg-red-600 text-white'
                    : 'bg-gray-200 text-gray-700 hover:bg-red-100'">
                  <X class="w-4 h-4 sm:w-5 sm:h-5" />
                </button>
              </div>
            </div>

            <!-- Notes -->
            <div v-if="getStudentAttendance(student.id)" class="mt-2 sm:mt-3">
              <textarea v-model="getStudentAttendance(student.id).notes" @blur="updateNotes(student)"
                placeholder="Ghi chú..."
                class="w-full px-2 sm:px-3 py-1.5 sm:py-2 border border-gray-300 rounded-lg text-xs sm:text-sm focus:border-green-500 focus:ring-2 focus:ring-green-100 outline-none transition-all"
                rows="2"></textarea>
            </div>
          </div>
        </div>

        <!-- No Students -->
        <div v-else-if="registeredStudents.length === 0" class="text-center py-6 sm:py-8 text-gray-500">
          <Users class="w-10 h-10 sm:w-12 sm:h-12 text-gray-400 mx-auto mb-2" />
          <p class="text-sm sm:text-base">Chưa có học viên đăng ký</p>
        </div>

        <!-- No Search Results -->
        <div v-else class="text-center py-6 sm:py-8 text-gray-500">
          <Search class="w-10 h-10 sm:w-12 sm:h-12 text-gray-400 mx-auto mb-2" />
          <p class="text-sm sm:text-base">Không tìm thấy học viên nào</p>
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
import Swal from 'sweetalert2';
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
  Users,
  Search,
  AlertTriangle
} from 'lucide-vue-next';
import { useToast } from 'vue-toastification';

const router = useRouter();
const toast = useToast();
const route = useRoute();
const authStore = useAuthStore();

const classId = ref(null);
const selectedClass = ref(null);
const selectedSession = ref(null);
const upcomingSessions = ref([]);
const registeredStudents = ref([]);
const attendanceRecords = ref([]);
const loadingStudents = ref(false);
const finalizing = ref(false);
const sessionFinalized = ref(false);
const roomsData = ref([]);
const sessionStats = ref({});
const sessionSearchQuery = ref('');
const sessionFilterStatus = ref('all');
const studentSearchQuery = ref('');

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

const filteredSessions = computed(() => {
  let result = [...upcomingSessions.value];

  // Search filter
  if (sessionSearchQuery.value) {
    const query = sessionSearchQuery.value.toLowerCase();
    result = result.filter(s =>
      s.className.toLowerCase().includes(query) ||
      s.roomName.toLowerCase().includes(query)
    );
  }

  // Status filter
  if (sessionFilterStatus.value === 'today') {
    result = result.filter(s => isToday(s.date));
  } else if (sessionFilterStatus.value === 'upcoming') {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    result = result.filter(s => new Date(s.date) > today);
  }

  // Sort by time (gần nhất trước)
  result.sort((a, b) => new Date(a.startTime) - new Date(b.startTime));

  return result;
});

const filteredStudents = computed(() => {
  if (!studentSearchQuery.value) {
    return registeredStudents.value;
  }

  const query = studentSearchQuery.value.toLowerCase();
  return registeredStudents.value.filter(s =>
    s.name.toLowerCase().includes(query) ||
    s.email.toLowerCase().includes(query)
  );
});

const loadClassData = async () => {
  try {
    if (!currentTeacherId.value) {
      toast.error("Bạn chưa đăng nhập hoặc không phải giáo viên.");
      return;
    }

    const regResponse = await api.get(`/class-registrations/teacher/${currentTeacherId.value}`);
    const allRegistrations = regResponse.data;

    const approvedClasses = allRegistrations
      .filter(reg => reg.status === 'APPROVED')
      .map(reg => reg.fitnessClass)
      .filter(fClass => fClass && fClass.status !== 'cancelled' && fClass.status !== 'completed'); // Filter out soft-deleted classes

    if (approvedClasses.length === 0) {
      upcomingSessions.value = [];
      return;
    }

    let allSessionsList = [];
    const now = new Date();
    now.setHours(0, 0, 0, 0);

    const schedulePromises = approvedClasses.map(async (fClass) => {
      try {
        const scheduleResponse = await api.get(`/classschedule/by-fitness_class/${fClass.id}`);
        let schedules = scheduleResponse.data || [];

        if (!Array.isArray(schedules)) {
          schedules = schedules && typeof schedules === 'object' ? [schedules] : [];
        }

        const statsPromises = schedules.map(async (s) => {
          try {
            const [regRes, attRes] = await Promise.all([
              api.get(`/member-registrations/schedule/${s.id}`),
              api.get(`/class-attendance/students/${s.id}`).catch(err => {
                if (err.response && err.response.status === 404) {
                  return { data: [] };
                }
                throw err; // Nếu lỗi khác (500, mạng...) thì vẫn báo lỗi
              })
            ]);

            sessionStats.value[s.id] = {
              registered: regRes.data.length,
              attended: attRes.data.filter(a => a.status === 'PRESENT').length
            };
          } catch (e) {
            sessionStats.value[s.id] = { registered: 0, attended: 0 };
          }
        });
        await Promise.all(statsPromises);
        // ---------------------------------

        const mappedSchedules = schedules.map(s => ({
          id: s.id,
          classId: fClass.id,
          className: fClass.name,
          date: s.startTime ? s.startTime.split('T')[0] : '',
          startTime: s.startTime,
          endTime: s.endTime,
          roomId: s.room?.id,
          roomName: s.room?.name || 'Chưa xếp phòng',
          capacity: s.capacity,
          status: s.status
        }));

        allSessionsList.push(...mappedSchedules.filter(s => new Date(s.date) >= now && s.status !== 'CANCELLED'));
      } catch (err) {
        console.warn(`Lỗi lớp ${fClass.name}:`, err);
      }
    });

    await Promise.all(schedulePromises);
    upcomingSessions.value = allSessionsList.sort((a, b) => new Date(a.startTime) - new Date(b.startTime));
    roomsData.value = await unifiedApi.getRooms();

  } catch (error) {
    toast.error('Lỗi hệ thống');
  }
};

const selectSession = async (session) => {
  selectedSession.value = session;
  await loadSessionData();
};

const getStudentAttendance = (memberId) => {
  return attendanceRecords.value.find(a => a.memberId === memberId);
};

const loadSessionData = async () => {
  if (!selectedSession.value) return;

  loadingStudents.value = true;
  try {
    const registrations = await api.get(`/member-registrations/schedule/${selectedSession.value.id}`);
    registeredStudents.value = registrations.data.map(r => ({
      id: r.member.id,
      name: r.member.fullName,
      email: r.member.email
    }));

    try {
      const attendance = await api.get(`/class-attendance/students/${selectedSession.value.id}`);
      attendanceRecords.value = attendance.data.map(a => ({
        id: a.id,
        memberId: a.member.id,
        status: a.status,
        notes: a.notes || '',
        checkedInAt: a.checkedInAt
      }));
    } catch (err) {
      if (err.response?.status === 404) {
        attendanceRecords.value = [];
      } else {
        throw err;
      }
    }

    if (sessionStats.value[selectedSession.value.id]) {
      sessionStats.value[selectedSession.value.id].registered = registeredStudents.value.length;
      sessionStats.value[selectedSession.value.id].attended = presentCount.value;
    }

    // Bỏ check sessionFinalized
    // sessionFinalized.value = selectedSession.value.status === 'CLOSED';

  } catch (error) {
    if (error.response?.status !== 404) toast.error('Có lỗi xảy ra');
  } finally {
    loadingStudents.value = false;
  }
};

// --- 2. SỬA HÀM ĐIỂM DANH (POST/PUT) ---
const markPresent = async (student) => {
  const existing = getStudentAttendance(student.id);

  try {
    if (existing) {
      // Logic UPDATE (PUT) giữ nguyên vì dùng @RequestParam
      await api.put('/class-attendance/students/update', null, {
        params: {
          scheduleId: selectedSession.value.id,
          memberId: student.id,
          teacherId: currentTeacherId.value,
          status: 'PRESENT'
        }
      });
      existing.status = 'PRESENT';
    } else {
      // Logic CREATE (POST) - CẦN SỬA ĐOẠN NÀY
      const payload = {
        classScheduleId: selectedSession.value.id, // Sửa từ scheduleId -> classScheduleId
        memberId: student.id,                      // Giữ nguyên
        checkedInById: currentTeacherId.value,     // Sửa từ teacherId -> checkedInById
        status: 'PRESENT',
        notes: ''
      };

      const response = await api.post('/class-attendance/students/checkin', payload);

      attendanceRecords.value.push({
        id: response.data.id,
        memberId: response.data.member.id,
        status: 'PRESENT',
        notes: response.data.notes,
        checkedInAt: response.data.checkedInAt
      });
    }
  } catch (error) {
    toast.error('Có lỗi xảy ra');
  }
};

const markAbsent = async (student) => {
  const existing = getStudentAttendance(student.id);

  try {
    if (existing) {
      // UPDATE
      await api.put('/class-attendance/students/update', null, {
        params: {
          scheduleId: selectedSession.value.id,
          memberId: student.id,
          teacherId: currentTeacherId.value,
          status: 'ABSENT'
        }
      });
      existing.status = 'ABSENT';
    } else {
      // CREATE (POST) - CẦN SỬA ĐOẠN NÀY
      const payload = {
        classScheduleId: selectedSession.value.id, // Sửa tên
        memberId: student.id,
        checkedInById: currentTeacherId.value,     // Sửa tên
        status: 'ABSENT',
        notes: ''
      };

      const response = await api.post('/class-attendance/students/checkin', payload);

      attendanceRecords.value.push({
        id: response.data.id,
        memberId: response.data.member.id,
        status: 'ABSENT',
        notes: response.data.notes,
        checkedInAt: response.data.checkedInAt
      });
    }
  } catch (error) {
    toast.error('Có lỗi xảy ra');
  }
};

const updateNotes = async (student) => {
  const attendance = getStudentAttendance(student.id);
  if (!attendance) return;

  try {
    await api.put('/class-attendance/students/update', null, {
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

const autoAbsent = async () => {
  // 1. Thay thế confirm bằng Modal đẹp
  const result = await Swal.fire({
    title: 'Xác nhận kết thúc?',
    text: "Hệ thống sẽ đánh dấu VẮNG và ĐÓNG buổi học này!",
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#d33', 
    cancelButtonColor: '#3085d6',
    confirmButtonText: 'Đồng ý, kết thúc!',
    cancelButtonText: 'Hủy',
    reverseButtons: true
  });

  if (!result.isConfirmed) return;

  finalizing.value = true;
  
  Swal.fire({
    title: 'Đang xử lý...',
    allowOutsideClick: false,
    didOpen: () => { Swal.showLoading(); }
  });

  try {
    const sessionId = selectedSession.value.id;
    await api.post(`/class-attendance/students/auto-absent/${sessionId}`);
    await api.put(`/classschedule/${sessionId}/close`);

    // 3. Thay toast bằng Modal thành công
    await Swal.fire({
      icon: 'success',
      title: 'Thành công!',
      text: 'Đã điểm danh vắng và đóng buổi học.',
      timer: 2000,
      showConfirmButton: false
    });

    await loadSessionData(); 

  } catch (error) {
    Swal.fire('Lỗi!', 'Có lỗi xảy ra khi kết thúc buổi học.', 'error');
  } finally {
    finalizing.value = false;
  }
};

const getAttendanceCount = (sessionId) => {
  return sessionStats.value[sessionId]?.attended || 0;
};

const getRegisteredCount = (sessionId) => {
  return sessionStats.value[sessionId]?.registered || 0;
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

const handleBackToList = async () => {
  selectedSession.value = null;
  await loadClassData();
};

onMounted(() => {
  loadClassData();
});
</script>
