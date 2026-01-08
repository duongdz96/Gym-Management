<template>
  <div v-if="show && fitnessClass" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
    @click.self="$emit('close')">
    <div class="bg-white rounded-2xl w-full max-w-3xl max-h-[90vh] overflow-y-auto shadow-2xl animate-fadeIn">
      <!-- Modal Header -->
      <div
        class="sticky top-0 p-6 bg-gradient-to-r from-emerald-600 to-emerald-700 text-white flex justify-between items-start rounded-t-2xl z-50">
        <div class="max-w-[500px]">
          <h2 class="text-2xl font-bold mb-1 truncate">{{ fitnessClass.name }}</h2>
          <div class="flex items-center gap-2 text-emerald-100 text-sm">
            <component :is="getStatusIcon(fitnessClass.status)" class="w-4 h-4" />
            {{ getStatusText(fitnessClass.status) }}
          </div>
        </div>
        <button @click="$emit('close')"
          class="w-10 h-10 bg-white/20 rounded-full hover:bg-white/30 transition-all flex items-center justify-center">
          <X class="w-6 h-6" />
        </button>
      </div>

      <!-- Modal Body -->
      <div class="p-8 space-y-8">
        <!-- Description -->
        <div>
          <h3 class="text-lg font-bold text-gray-800 mb-3 flex items-center gap-2">
            <Info class="w-5 h-5 text-emerald-600" />
            Mô tả
          </h3>
          <p class="text-gray-600 leading-relaxed bg-gray-50 p-4 rounded-xl border border-gray-100">
            {{ fitnessClass.description }}
          </p>
        </div>

        <!-- General Info -->
        <div>
          <h3 class="text-lg font-bold text-gray-800 mb-3 flex items-center gap-2">
            <FileText class="w-5 h-5 text-emerald-600" />
            Thông tin chung
          </h3>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="p-4 bg-gray-50 rounded-xl border border-gray-100 flex items-center gap-3">
              <BarChart3 class="w-8 h-8 text-emerald-500 bg-emerald-100 p-1.5 rounded-lg" />
              <div>
                <div class="text-xs text-gray-500 font-semibold uppercase">Độ khó</div>
                <div class="font-bold text-gray-800">{{ fitnessClass.difficulty }}</div>
              </div>
            </div>
            <div class="p-4 bg-gray-50 rounded-xl border border-gray-100 flex items-center gap-3">
              <Calendar class="w-8 h-8 text-emerald-500 bg-emerald-100 p-1.5 rounded-lg" />
              <div>
                <div class="text-xs text-gray-500 font-semibold uppercase">Số buổi học</div>
                <div class="font-bold text-gray-800">{{ sessions.length }} buổi</div>
              </div>
            </div>
            <div class="p-4 bg-gray-50 rounded-xl border border-gray-100 flex items-center gap-3">
              <MapPin class="w-8 h-8 text-blue-500 bg-blue-100 p-1.5 rounded-lg" />
              <div>
                <div class="text-xs text-gray-500 font-semibold uppercase">Phòng học</div>
                <div class="font-bold text-gray-800">{{ getRoomName(fitnessClass.roomId) }}</div>
              </div>
            </div>
            <div class="p-4 bg-gray-50 rounded-xl border border-gray-100 flex items-center gap-3">
              <GraduationCap class="w-8 h-8 text-purple-500 bg-purple-100 p-1.5 rounded-lg" />
              <div>
                <div class="text-xs text-gray-500 font-semibold uppercase">Giáo viên</div>
                <div class="font-bold text-gray-800">{{ getTeacherName(fitnessClass.teacherId) }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Schedule Info -->
        <div>
          <h3 class="text-lg font-bold text-gray-800 mb-3 flex items-center gap-2">
            <Calendar class="w-5 h-5 text-emerald-600" />
            Lịch học
          </h3>
          <div class="bg-gray-50 rounded-xl border border-gray-100 p-5 space-y-3">
            <div class="flex justify-between border-b border-gray-200 pb-2">
              <span class="text-gray-600">Loại lịch:</span>
              <span class="font-semibold text-gray-800">{{ getPatternTypeText(fitnessClass.patternType) }}</span>
            </div>
            <div class="flex justify-between border-b border-gray-200 pb-2">
              <span class="text-gray-600">Thời gian:</span>
              <span class="font-semibold text-gray-800">{{ fitnessClass.startTime }} - {{ fitnessClass.endTime }}</span>
            </div>
            <div class="flex justify-between border-b border-gray-200 pb-2">
              <span class="text-gray-600">Chi tiết:</span>
              <span class="font-semibold text-gray-800">{{ getScheduleText(fitnessClass) }}</span>
            </div>
            <div v-if="fitnessClass.patternType !== 'no_repeat'"
              class="flex justify-between border-b border-gray-200 pb-2">
              <span class="text-gray-600">Thời gian áp dụng:</span>
              <span class="font-semibold text-gray-800">
                {{ formatDate(fitnessClass.startDate) }} - {{ formatDate(fitnessClass.endDate) }}
              </span>
            </div>
          </div>
        </div>

        <!-- Sessions List -->
        <div>
          <h3 class="text-lg font-bold text-gray-800 mb-3 flex items-center gap-2">
            <Calendar class="w-5 h-5 text-emerald-600" />
            Danh sách buổi học
          </h3>

          <div v-if="loading" class="text-center py-8">
            <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-emerald-600 mx-auto mb-4"></div>
            Đang tải...
          </div>

          <div v-else-if="sessions.length > 0" class="space-y-2 max-h-96 overflow-y-auto">
            <div v-for="(session, idx) in sessions" :key="session.id"
              class="flex items-center gap-4 p-3 rounded-xl border border-gray-200 hover:border-emerald-300 hover:bg-emerald-50 transition-all">
              <div
                class="w-8 h-8 rounded-full bg-emerald-100 text-emerald-600 flex items-center justify-center font-bold text-sm shrink-0">
                {{ idx + 1 }}
              </div>
              <div class="flex-1">
                <div class="font-bold text-gray-800">{{ formatDate(session.date) }}</div>
                <div class="text-sm text-gray-500">{{ formatScheduleTime(session.startTime) }} - {{
                  formatScheduleTime(session.endTime) }}</div>
                <div class="text-sm font-semibold mt-1 flex items-center gap-1 text-gray-600">
                  <Users class="w-4 h-4" />
                  Sĩ số: {{ getSessionEnrollment(session.id) }}/{{ session.capacity || 0 }}
                </div>
                <div v-if="session.note" class="text-xs text-orange-600 mt-1 flex items-center gap-1">
                  <Info class="w-3 h-3" /> {{ session.note }}
                </div>
              </div>
              <div class="flex items-center gap-2">
                <div class="text-xs font-semibold px-2 py-1 rounded" :class="{
                  'bg-blue-100 text-blue-700': session.status === 'RUNNING',  // Thêm màu xanh dương cho Running
                  'bg-green-100 text-green-700': session.status === 'OPEN',
                  'bg-gray-100 text-gray-500': session.status === 'CLOSED',
                  'bg-red-100 text-red-700': session.status === 'CANCELLED'
                }">
                  {{
                    session.status === 'RUNNING' ? 'Đang diễn ra' :
                      session.status === 'OPEN' ? 'Mở' :
                        session.status === 'CANCELLED' ? 'Đã hủy' : 'Đóng'
                  }}
                </div>
                <button @click="$emit('edit-session', session)"
                  class="p-2 bg-purple-100 text-purple-600 rounded-lg hover:bg-purple-200 transition-all"
                  title="Sửa buổi học">
                  <Edit class="w-4 h-4" />
                </button>
              </div>
            </div>
          </div>

          <div v-else class="text-center py-8 bg-gray-50 rounded-xl border border-gray-100 border-dashed">
            <p class="text-gray-500 italic">Chưa có buổi học nào được tạo</p>
          </div>
        </div>

        <!-- Enrolled Students -->
        <div>
          <h3 class="text-lg font-bold text-gray-800 mb-3 flex items-center gap-2">
            <Users class="w-5 h-5 text-emerald-600" />
            Danh sách học viên đã đăng ký ít nhất 1 buổi: ({{ students.length }})
          </h3>

          <div v-if="students.length > 0" class="overflow-x-auto">
            <table class="w-full text-sm text-left">
              <thead class="text-xs text-gray-700 uppercase bg-gray-100">
                <tr>
                  <th class="px-4 py-3 rounded-l-lg">Học viên</th>
                  <th class="px-4 py-3">Email</th>
                  <th class="px-4 py-3">Hạng TV</th>
                  <th class="px-4 py-3 rounded-r-lg">Ngày đăng ký</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="student in students" :key="student.id" class="border-b border-gray-50 hover:bg-gray-50">
                  <td class="px-4 py-3 font-medium text-gray-900">{{ student.name }}</td>
                  <td class="px-4 py-3 text-gray-600">{{ student.email }}</td>
                  <td class="px-4 py-3">
                    <span class="px-2 py-1 rounded-full text-xs font-bold" :class="{
                      'bg-yellow-100 text-yellow-700': student.membershipTier === 'VIP',
                      'bg-emerald-100 text-emerald-700': student.membershipTier === 'PREMIUM',
                      'bg-gray-100 text-gray-700': student.membershipTier === 'BASIC'
                    }">
                      {{ student.membershipTier }}
                    </span>
                  </td>
                  <td class="px-4 py-3 text-gray-600">{{ formatDate(student.registeredAt) }}</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div v-else class="text-center py-8 bg-gray-50 rounded-xl border border-gray-100 border-dashed">
            <p class="text-gray-500 italic">Chưa có học viên nào đăng ký lớp này</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {
  X,
  Info,
  FileText,
  BarChart3,
  Calendar,
  MapPin,
  GraduationCap,
  Users,
  Edit,
  Clock,
  CheckCircle,
  XCircle,
  UserPlus
} from 'lucide-vue-next';
import { formatDate } from '@/views/Test/dateUtils.js';

const props = defineProps({
  show: {
    type: Boolean,
    required: true
  },
  fitnessClass: {
    type: Object,
    default: null
  },
  sessions: {
    type: Array,
    default: () => []
  },
  students: {
    type: Array,
    default: () => []
  },
  rooms: {
    type: Array,
    default: () => []
  },
  teachers: {
    type: Array,
    default: () => []
  },
  registrations: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  }
});

defineEmits(['close', 'edit-session']);

const getStatusText = (status) => {
  const statusMap = {
    draft: 'Nháp',
    pending_teacher: 'Chờ giáo viên',
    waiting_approval: 'Chờ duyệt',
    open: 'Đang mở',
    completed: 'Hoàn thành',
    cancelled: 'Đã hủy'
  };
  return statusMap[status] || status;
};

const getStatusIcon = (status) => {
  const iconMap = {
    draft: FileText,
    pending_teacher: UserPlus,
    waiting_approval: Clock,
    open: CheckCircle,
    completed: CheckCircle,
    cancelled: XCircle
  };
  return iconMap[status] || FileText;
};

const getRoomName = (roomId) => {
  const room = props.rooms.find(r => r.id === roomId);
  return room ? room.name : 'Chưa chọn';
};

const getTeacherName = (teacherId) => {
  if (!teacherId) return 'Chưa có';
  const teacher = props.teachers.find(t => t.id === teacherId);
  return teacher ? teacher.name : 'Không xác định';
};

const getPatternTypeText = (type) => {
  const map = {
    weekly: 'Hàng tuần',
    monthly: 'Hàng tháng',
    custom_range: 'Tùy chỉnh',
    no_repeat: 'Không lặp lại'
  };
  return map[type] || type;
};

const getScheduleText = (cls) => {
  if (!cls) return '';
  const daysOfWeek = ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'];
  if (cls.patternType === 'weekly') {
    const days = cls.daysOfWeek?.map(d => daysOfWeek[d]).join(', ') || '';
    return `Hàng tuần: ${days}`;
  }
  if (cls.patternType === 'monthly') return 'Hàng tháng';
  if (cls.patternType === 'no_repeat') {
    return 'Tùy chọn';
  }
  return 'Tùy chỉnh';
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

const getSessionEnrollment = (sessionId) => {
  return props.registrations.filter(r => r.scheduleId === sessionId).length;
};
</script>

<style scoped>
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-fadeIn {
  animation: fadeIn 0.3s ease-out;
}
</style>
