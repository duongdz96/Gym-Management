<template>
  <div class="p-6">
    <div class="flex items-center gap-3 mb-8">
      <GraduationCap class="w-10 h-10 text-green-600" />
      <h1 class="text-3xl font-bold bg-gradient-to-r from-green-600 to-green-700 bg-clip-text text-transparent">
        Duyệt Giáo Viên
      </h1>
    </div>

    <!-- Tabs -->
    <div class="flex gap-2 mb-6 border-b-2 border-gray-200">
      <button 
        @click="activeTab = 'pending'" 
        :class="activeTab === 'pending' 
          ? 'text-green-600 border-green-600' 
          : 'text-gray-500 border-transparent hover:text-green-600'"
        class="px-6 py-3 font-semibold border-b-3 transition-all -mb-0.5 flex items-center gap-2"
      >
        <Clock class="w-5 h-5" />
        Chờ duyệt ({{ pendingApplications.length }})
      </button>
      <button 
        @click="activeTab = 'approved'" 
        :class="activeTab === 'approved' 
          ? 'text-green-600 border-green-600' 
          : 'text-gray-500 border-transparent hover:text-green-600'"
        class="px-6 py-3 font-semibold border-b-3 transition-all -mb-0.5 flex items-center gap-2"
      >
        <CheckCircle class="w-5 h-5" />
        Đã duyệt ({{ approvedApplications.length }})
      </button>
      <button 
        @click="activeTab = 'rejected'" 
        :class="activeTab === 'rejected' 
          ? 'text-green-600 border-green-600' 
          : 'text-gray-500 border-transparent hover:text-green-600'"
        class="px-6 py-3 font-semibold border-b-3 transition-all -mb-0.5 flex items-center gap-2"
      >
        <XCircle class="w-5 h-5" />
        Đã từ chối ({{ rejectedApplications.length }})
      </button>
    </div>

    <!-- Applications Grouped by Class -->
    <div class="space-y-6">
      <div 
        v-for="group in groupedApplications" 
        :key="group.classId"
        class="bg-white rounded-2xl shadow-md overflow-hidden border-2 border-gray-100"
      >
        <!-- Class Header -->
        <div class="p-5 bg-gradient-to-r from-green-600 to-green-700 text-white">
          <div class="flex items-center gap-2 mb-2">
            <BookOpen class="w-6 h-6" />
            <h3 class="text-xl font-bold">{{ group.className }}</h3>
          </div>
          <p class="text-white/90 text-sm mb-3">{{ group.classDescription }}</p>
          <div class="flex flex-wrap gap-2 text-sm">
            <span class="px-3 py-1 bg-white/20 rounded-full flex items-center gap-1">
              <BarChart3 class="w-4 h-4" />
              {{ group.classDifficulty }}
            </span>
            <span class="px-3 py-1 bg-white/20 rounded-full flex items-center gap-1">
              <Clock class="w-4 h-4" />
              {{ group.classTime }}
            </span>
            <span class="px-3 py-1 bg-white/20 rounded-full flex items-center gap-1">
              <Calendar class="w-4 h-4" />
              {{ group.classSchedule }}
            </span>
            <span class="px-3 py-1 bg-white/20 rounded-full font-bold flex items-center gap-1">
              <Users class="w-4 h-4" />
              {{ group.applications.length }} giáo viên
            </span>
          </div>
        </div>

        <!-- Teachers List -->
        <div class="p-5 space-y-4">
          <div 
            v-for="app in group.applications" 
            :key="app.id"
            class="border-2 rounded-xl p-4 hover:shadow-md transition-all"
            :class="{
              'border-green-200 bg-green-50': app.status === 'pending',
              'border-green-200 bg-green-50': app.status === 'approved',
              'border-red-200 bg-red-50': app.status === 'rejected'
            }"
          >
            <div class="flex items-start gap-4">
              <!-- Teacher Avatar -->
              <div class="w-16 h-16 bg-gradient-to-br from-green-500 to-green-600 rounded-full flex items-center justify-center text-3xl flex-shrink-0">
                {{ getTeacher(app.teacherId).avatar }}
              </div>

              <!-- Teacher Info -->
              <div class="flex-1">
                <div class="flex items-start justify-between mb-2">
                  <div>
                    <h4 class="text-lg font-bold text-gray-800 flex items-center gap-2">
                      {{ getTeacher(app.teacherId).name }}
                    </h4>
                    <p class="text-sm text-gray-600 flex items-center gap-1">
                      <Mail class="w-4 h-4" />
                      {{ getTeacher(app.teacherId).email }}
                    </p>
                  </div>
                  <span 
                    class="px-3 py-1 rounded-full text-xs font-semibold flex items-center gap-1"
                    :class="{
                      'bg-green-500 text-white': app.status === 'pending',
                      'bg-green-500 text-white': app.status === 'approved',
                      'bg-red-500 text-white': app.status === 'rejected'
                    }"
                  >
                    <component 
                      :is="app.status === 'pending' ? Clock : app.status === 'approved' ? CheckCircle : XCircle" 
                      class="w-3 h-3"
                    />
                    {{ getStatusText(app.status) }}
                  </span>
                </div>

                <!-- Specialties -->
                <div class="flex items-center gap-2 mb-3">
                  <Target class="w-4 h-4 text-green-600" />
                  <div class="flex flex-wrap gap-2">
                    <span 
                      v-for="(specialty, idx) in getTeacher(app.teacherId).specialties" 
                      :key="idx"
                      class="px-2 py-1 bg-green-100 text-green-700 rounded-md text-xs font-semibold"
                    >
                      {{ specialty }}
                    </span>
                  </div>
                </div>

                <!-- Bio -->
                <p class="text-sm text-gray-600 mb-3">{{ getTeacher(app.teacherId).bio }}</p>

                <!-- Time Info -->
                <div class="text-xs text-gray-500 mb-3 flex items-center gap-3">
                  <span class="flex items-center gap-1">
                    <Calendar class="w-3 h-3" />
                    Đăng ký: {{ formatDateTime(app.appliedAt) }}
                  </span>
                  <span v-if="app.reviewedAt" class="flex items-center gap-1">
                    <Calendar class="w-3 h-3" />
                    Xét duyệt: {{ formatDateTime(app.reviewedAt) }}
                  </span>
                </div>

                <!-- Conflict Check -->
                <div v-if="app.status === 'pending'" class="mb-3">
                  <div v-if="!conflictMap[app.id] || conflictMap[app.id].length === 0" 
                       class="p-2.5 bg-green-50 border border-green-200 rounded-lg text-green-700 text-sm font-medium flex items-center gap-2">
                    <CheckCircle class="w-4 h-4 text-green-600" />
                    <span>Không có xung đột lịch</span>
                  </div>

                  <div v-else class="p-3 bg-orange-50 border border-orange-200 rounded-lg">
                    <div class="flex items-center gap-2 text-orange-800 font-semibold mb-2 text-sm">
                      <AlertCircle class="w-4 h-4" />
                      <span>Giáo viên đang vướng lịch:</span>
                    </div>
                    
                    <ul class="space-y-2">
                      <li v-for="(conflict, idx) in conflictMap[app.id]" :key="idx" 
                          class="bg-white/60 p-2 rounded border border-orange-100 text-xs text-gray-700">
                        <div class="font-bold text-orange-900 mb-1 flex items-center gap-1">
                          <BookOpen class="w-3 h-3" />
                          {{ conflict.className }}
                        </div>
                        
                        <div class="flex flex-wrap gap-3 pl-1">
                          <span class="flex items-center gap-1 bg-orange-100 px-1.5 py-0.5 rounded text-orange-800 font-medium">
                            <Clock class="w-3 h-3" />
                            {{ conflict.time }}
                          </span>
                          <span class="flex items-center gap-1 text-gray-600">
                            <Calendar class="w-3 h-3" />
                            {{ conflict.date }}
                          </span>
                        </div>
                      </li>
                    </ul>
                  </div>
                </div>

                <!-- Rejection Reason -->
                <div v-if="app.status === 'rejected' && app.rejectionReason" class="p-2 bg-red-100 border border-red-300 rounded-lg text-red-700 text-sm flex items-start gap-2">
                  <XCircle class="w-4 h-4 flex-shrink-0 mt-0.5" />
                  <div>
                    <strong>Lý do từ chối:</strong> {{ app.rejectionReason }}
                  </div>
                </div>

                <!-- Actions -->
                <div v-if="app.status === 'pending'" class="flex gap-2 mt-3">
                  <!-- Show warning if class already has approved teacher -->
                  <div v-if="classHasApprovedTeacher(app.classId)" class="flex-1">
                    <div class="p-2 bg-yellow-100 border border-yellow-300 rounded-lg text-yellow-700 text-sm mb-2 flex items-center gap-2">
                      <AlertCircle class="w-4 h-4" />
                      Lớp đã có giáo viên được duyệt
                    </div>
                    <button 
                      @click="autoReject(app)" 
                      class="w-full px-4 py-2 bg-red-500 text-white rounded-lg text-sm font-semibold hover:bg-red-600 transition-all flex items-center justify-center gap-1"
                    >
                      <XCircle class="w-4 h-4" />
                      Từ chối (Đã chọn giáo viên khác)
                    </button>
                  </div>
                  
                  <!-- Normal actions when no teacher approved yet -->
                  <template v-else>
                    <button 
                      @click="showRejectModal(app)" 
                      class="px-4 py-2 bg-red-500 text-white rounded-lg text-sm font-semibold hover:bg-red-600 transition-all flex items-center gap-1"
                    >
                      <XCircle class="w-4 h-4" />
                      Từ chối
                    </button>
                    <button 
                      @click="approve(app)" 
                      :disabled="conflictMap[app.id] && conflictMap[app.id].length > 0"
                      class="px-4 py-2 bg-green-500 text-white rounded-lg text-sm font-semibold hover:bg-green-600 transition-all flex items-center gap-1 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      <CheckCircle class="w-4 h-4" />
                      Duyệt
                    </button>
                  </template>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-if="groupedApplications.length === 0" class="text-center py-16">
        <AlertCircle class="w-16 h-16 text-gray-400 mx-auto mb-4" />
        <p class="text-gray-500 text-lg">Không có đơn đăng ký nào</p>
      </div>
    </div>

    <!-- Reject Modal -->
    <div 
      v-if="rejectModalApp" 
      class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
      @click.self="rejectModalApp = null"
    >
      <div class="bg-white rounded-2xl w-full max-w-2xl shadow-2xl">
        <div class="p-6 bg-gradient-to-r from-red-500 to-red-600 text-white flex justify-between items-center rounded-t-2xl">
          <h2 class="text-2xl font-bold">Từ chối đơn đăng ký</h2>
          <button 
            @click="rejectModalApp = null" 
            class="w-10 h-10 bg-white/20 rounded-full hover:bg-white/30 transition-all flex items-center justify-center"
          >
            <X class="w-6 h-6" />
          </button>
        </div>
        
        <div class="p-8 space-y-4">
          <p class="text-gray-700"><strong>Giáo viên:</strong> {{ getTeacher(rejectModalApp.teacherId).name }}</p>
          <p class="text-gray-700"><strong>Lớp:</strong> {{ getClass(rejectModalApp.classId).name }}</p>
          
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">Lý do từ chối *</label>
            <select 
              v-model="rejectionReason"
              class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-green-600 focus:ring-2 focus:ring-green-100 outline-none transition-all"
            >
              <option value="">-- Chọn lý do --</option>
              <option value="Lịch trùng với lớp khác">Lịch trùng với lớp khác</option>
              <option value="Không phù hợp với chuyên môn">Không phù hợp với chuyên môn</option>
              <option value="Đã chọn giáo viên khác">Đã chọn giáo viên khác</option>
              <option value="custom">Lý do khác...</option>
            </select>
          </div>
          
          <div v-if="rejectionReason === 'custom'">
            <label class="block text-sm font-semibold text-gray-700 mb-2">Nhập lý do</label>
            <textarea 
              v-model="customRejectionReason" 
              rows="3"
              class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-green-600 focus:ring-2 focus:ring-green-100 outline-none transition-all"
            ></textarea>
          </div>
        </div>
        
        <div class="p-6 bg-gray-50 border-t flex justify-end gap-3 rounded-b-2xl">
          <button 
            @click="rejectModalApp = null" 
            class="px-6 py-3 bg-gray-200 text-gray-700 rounded-xl font-semibold hover:bg-gray-300 transition-all"
          >
            Hủy
          </button>
          <button 
            @click="reject()" 
            :disabled="!canReject"
            class="px-6 py-3 bg-red-500 text-white rounded-xl font-semibold hover:bg-red-600 transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
          >
            <XCircle class="w-5 h-5" />
            Xác nhận từ chối
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useToast } from 'vue-toastification';
import Swal from 'sweetalert2';
import unifiedApi from '@/services/unifiedClassApi.js';
import { formatDateTime } from '@/views/Test/dateUtils.js';
import { 
  GraduationCap, 
  Clock, 
  Calendar, 
  BarChart3, 
  Users, 
  CheckCircle, 
  XCircle, 
  AlertCircle,
  BookOpen,
  Mail,
  Target,
  X
} from 'lucide-vue-next';

const toast = useToast();

// State
const applications = ref([]);
const teachersData = ref([]);
const classesData = ref([]);
const activeTab = ref('pending');
const rejectModalApp = ref(null);
const rejectionReason = ref('');
const customRejectionReason = ref('');
const conflictMap = ref({});

// Computed
const pendingApplications = computed(() => {
  return applications.value.filter(a => a.status === 'pending');
});

const approvedApplications = computed(() => {
  return applications.value.filter(a => a.status === 'approved');
});

const rejectedApplications = computed(() => {
  return applications.value.filter(a => a.status === 'rejected');
});

const filteredApplications = computed(() => {
  if (activeTab.value === 'pending') return pendingApplications.value;
  if (activeTab.value === 'approved') return approvedApplications.value;
  if (activeTab.value === 'rejected') return rejectedApplications.value;
  return [];
});

const groupedApplications = computed(() => {
  const groups = {};
  
  filteredApplications.value.forEach(app => {
    if (!groups[app.classId]) {
      const cls = getClass(app.classId);
      groups[app.classId] = {
        classId: app.classId,
        className: cls.name,
        classDescription: cls.description,
        classDifficulty: cls.difficulty,
        classTime: `${cls.startTime} - ${cls.endTime}`,
        classSchedule: getScheduleText(cls),
        applications: []
      };
    }
    groups[app.classId].applications.push(app);
  });
  
  return Object.values(groups);
});

const canReject = computed(() => {
  if (rejectionReason.value === 'custom') {
    return customRejectionReason.value.trim().length > 0;
  }
  return rejectionReason.value.length > 0;
});

// Methods
const loadData = async () => {
  applications.value = await unifiedApi.getApplications();
  teachersData.value = await unifiedApi.getTeachers();
  classesData.value = await unifiedApi.getClasses();
  
  // Check conflicts for all pending applications
  for (const app of applications.value) {
    if (app.status === 'pending') {
      try {
        const conflicts = await checkTeacherConflict(app);
        if (conflicts.length > 0) {
          conflictMap.value[app.id] = conflicts;
        }
      } catch (error) {
        // Error checking conflicts
      }
    }
  }
};

const getTeacher = (teacherId) => {
  return teachersData.value.find(t => t.id === teacherId) || {};
};

const getClass = (classId) => {
  return classesData.value.find(c => c.id === classId) || {};
};

const getStatusText = (status) => {
  const map = {
    pending: 'Chờ duyệt',
    approved: 'Đã duyệt',
    rejected: 'Đã từ chối'
  };
  return map[status] || status;
};

const getScheduleText = (cls) => {
  const daysOfWeek = ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'];
  if (cls.patternType === 'weekly') {
    const days = cls.daysOfWeek.map(d => daysOfWeek[d]).join(', ');
    return `Hàng tuần: ${days}`;
  }
  if (cls.patternType === 'monthly') return 'Hàng tháng';
  if (cls.patternType === 'no_repeat') return 'Một lần';
  return 'Tùy chỉnh';
};

const checkTeacherConflict = async (app) => {
  const cls = getClass(app.classId);
  if (!cls) return [];
  
  const conflicts = [];
  
  // Get all approved applications for this teacher (excluding current one)
  const teacherApprovedApps = applications.value.filter(a => 
    a.teacherId === app.teacherId && 
    a.status === 'approved' && 
    a.id !== app.id
  );
  
  // Check if new class schedule conflicts with any approved class
  for (const approvedApp of teacherApprovedApps) {
    const approvedClass = getClass(approvedApp.classId);
    if (!approvedClass) continue;
    
    // Check if schedules overlap
    if (schedulesOverlap(cls, approvedClass)) {
      conflicts.push({
        date: formatSchedule(approvedClass),
        time: `${approvedClass.startTime} - ${approvedClass.endTime}`,
        className: approvedClass.name
      });
    }
  }
  
  return conflicts;
};

const schedulesOverlap = (class1, class2) => {
  const start1 = new Date(class1.startDate);
  const end1 = new Date(class1.endDate);
  const start2 = new Date(class2.startDate);
  const end2 = new Date(class2.endDate);

  const isDateOverlap = start1 <= end2 && end1 >= start2;
  if (!isDateOverlap) return false;

  const time1Start = class1.startTime;
  const time1End = class1.endTime;
  const time2Start = class2.startTime;
  const time2End = class2.endTime;
  
  const isTimeOverlap = time1Start < time2End && time1End > time2Start;
  if (!isTimeOverlap) return false;

  if (class1.patternType === 'weekly' && class2.patternType === 'weekly') {
    const days1 = class1.daysOfWeek || [];
    const days2 = class2.daysOfWeek || [];
    return days1.some(d => days2.includes(d));
  }

  return true;
};

const formatSchedule = (cls) => {
  const daysOfWeek = ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'];
  if (cls.patternType === 'weekly') {
    const days = (cls.daysOfWeek || []).map(d => daysOfWeek[d]).join(', ');
    return `Hàng tuần: ${days}`;
  }
  if (cls.patternType === 'monthly') return 'Hàng tháng';
  if (cls.patternType === 'no_repeat') return 'Một lần';
  return 'Tùy chỉnh';
};

const classHasApprovedTeacher = (classId) => {
  // Check if this class already has an approved teacher
  return applications.value.some(a => 
    a.classId === classId && a.status === 'approved'
  );
};

const autoReject = async (app) => {
  const result = await Swal.fire({
    title: 'Xác nhận từ chối?',
    text: 'Từ chối giáo viên này vì lớp đã có giáo viên được duyệt?',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#EF4444',
    cancelButtonColor: '#6B7280',
    confirmButtonText: 'Từ chối',
    cancelButtonText: 'Hủy'
  });
  
  if (result.isConfirmed) {
    try {
      await unifiedApi.rejectTeacher(app.id, 'manager', 'Đã chọn giáo viên khác');
      toast.success('Đã từ chối đơn đăng ký');
      await loadData();
    } catch (error) {
      toast.error('Có lỗi xảy ra');
    }
  }
};

const approve = async (app) => {
  if (conflictMap.value[app.id] && conflictMap.value[app.id].length > 0) {
    toast.error('Không thể duyệt! Giáo viên có lịch trùng với lớp khác.');
    return;
  }
  
  const result = await Swal.fire({
    title: 'Xác nhận duyệt?',
    text: 'Bạn có chắc muốn duyệt giáo viên này?',
    icon: 'question',
    showCancelButton: true,
    confirmButtonColor: '#10B981',
    cancelButtonColor: '#6B7280',
    confirmButtonText: 'Duyệt',
    cancelButtonText: 'Hủy'
  });
  
  if (result.isConfirmed) {
    try {
      await unifiedApi.approveTeacher(app.id, 'manager');
      toast.success('Đã duyệt giáo viên thành công!');
      await loadData();
    } catch (error) {
      // Show detailed error message from backend
      const errorMsg = error.response?.data?.message || error.response?.data || error.message;
      toast.error('Có lỗi xảy ra');
    }
  }
};

const showRejectModal = (app) => {
  rejectModalApp.value = app;
  rejectionReason.value = '';
  customRejectionReason.value = '';
};

const reject = async () => {
  const reason = rejectionReason.value === 'custom' 
    ? customRejectionReason.value 
    : rejectionReason.value;
  
  try {
    await unifiedApi.rejectTeacher(rejectModalApp.value.id, 'manager', reason);
    toast.success('Đã từ chối đơn đăng ký');
    rejectModalApp.value = null;
    await loadData();
  } catch (error) {
    toast.error('Có lỗi xảy ra');
  }
};

// Lifecycle
onMounted(() => {
  loadData();
});
</script>
