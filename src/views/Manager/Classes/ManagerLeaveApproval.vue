<template>
  <div class="p-6">
    <h2 class="text-2xl font-bold text-gray-800 mb-6">Duyệt Đơn Xin Nghỉ & Quản Lý Buổi Học</h2>

    <!-- Loading -->
    <div v-if="loading" class="text-center py-8">
      <div class="text-gray-500">Đang tải...</div>
    </div>

    <!-- Empty State -->
    <div v-else-if="leaveRequests.length === 0" class="text-center py-16">
      <div class="text-gray-400 text-lg">Không có đơn xin nghỉ nào đang chờ duyệt</div>
    </div>

    <!-- Leave Requests List -->
    <div v-else class="space-y-4">
      <div 
        v-for="request in leaveRequests" 
        :key="request.sessionId"
        class="bg-white rounded-xl shadow-md border-2 border-yellow-200 p-5"
      >
        <div class="flex justify-between items-start mb-4">
          <div>
            <h3 class="text-lg font-bold text-gray-800">{{ request.className }}</h3>
            <div class="text-sm text-gray-600 mt-1">
              Giáo viên: {{ request.teacherName }}
            </div>
            <div class="text-sm text-gray-600">
              Ngày: {{ formatDate(request.sessionDate) }} | {{ request.startTime }} - {{ request.endTime }}
            </div>
            <div class="text-sm text-gray-600">
              Phòng: {{ request.roomName }}
            </div>
          </div>
          <div class="px-3 py-1 bg-yellow-100 text-yellow-800 rounded-lg text-sm font-semibold">
            {{ request.approved ? 'Đã duyệt' : 'Chờ duyệt' }}
          </div>
        </div>

        <div class="bg-yellow-50 border-l-4 border-yellow-400 p-3 mb-4">
          <div class="text-sm font-semibold text-yellow-800 mb-1">Lý do xin nghỉ:</div>
          <div class="text-sm text-yellow-700">{{ request.reason }}</div>
          <div class="text-xs text-yellow-600 mt-2">
            Gửi lúc: {{ formatDateTime(request.requestedAt) }}
          </div>
        </div>

        <!-- Actions -->
        <div class="space-y-3">
          <!-- Approve/Reject (only if not approved yet) -->
          <div v-if="!request.approved" class="flex gap-3">
            <button 
              @click="approveLeave(request)"
              class="flex-1 px-4 py-2 bg-green-600 text-white rounded-lg font-semibold hover:bg-green-700"
            >
              ✓ Duyệt đơn
            </button>
            <button 
              @click="rejectLeave(request)"
              class="flex-1 px-4 py-2 bg-gray-600 text-white rounded-lg font-semibold hover:bg-gray-700"
            >
              ✗ Từ chối
            </button>
          </div>

          <!-- Cancel Session & Add Note (only if approved) -->
          <div v-if="request.approved" class="flex gap-3">
            <button 
              @click="openCancelModal(request)"
              class="flex-1 px-4 py-2 bg-red-600 text-white rounded-lg font-semibold hover:bg-red-700 flex items-center justify-center gap-2"
            >
              <XCircle class="w-5 h-5" />
              Hủy buổi học
            </button>
            <button 
              @click="openNoteModal(request)"
              class="flex-1 px-4 py-2 bg-green-600 text-white rounded-lg font-semibold hover:bg-green-700 flex items-center justify-center gap-2"
            >
              <FileText class="w-5 h-5" />
              Thêm ghi chú
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Cancel Session Modal -->
    <transition
      enter-active-class="transition duration-200 ease-out"
      enter-from-class="opacity-0 scale-95"
      enter-to-class="opacity-100 scale-100"
      leave-active-class="transition duration-150 ease-in"
      leave-from-class="opacity-100 scale-100"
      leave-to-class="opacity-0 scale-95"
    >
      <div 
        v-if="showCancelModal"
        class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50"
        @click.self="showCancelModal = false"
      >
        <div class="bg-white rounded-2xl w-full max-w-md shadow-2xl">
          <div class="p-6 border-b">
            <h3 class="text-xl font-bold text-gray-800">Hủy Buổi Học</h3>
          </div>
          
          <div class="p-6">
            <label class="block text-sm font-semibold text-gray-700 mb-2">Lý do hủy:</label>
            <select 
              v-model="cancellationReason"
              class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-red-600 focus:ring-2 focus:ring-red-100 outline-none mb-3"
            >
              <option value="">-- Chọn lý do --</option>
              <option value="Không tìm được giáo viên thay thế">Không tìm được giáo viên thay thế</option>
              <option value="Sự cố cơ sở vật chất">Sự cố cơ sở vật chất</option>
              <option value="Thiếu học viên đăng ký">Thiếu học viên đăng ký</option>
              <option value="Khác">Khác</option>
            </select>
            
            <textarea 
              v-if="cancellationReason === 'Khác'"
              v-model="customCancellationReason"
              rows="3"
              class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-red-600 focus:ring-2 focus:ring-red-100 outline-none"
              placeholder="Nhập lý do cụ thể..."
            ></textarea>
          </div>
          
          <div class="p-4 border-t bg-gray-50 flex justify-end gap-3">
            <button 
              @click="showCancelModal = false"
              class="px-6 py-2 bg-gray-200 text-gray-700 font-bold rounded-xl hover:bg-gray-300"
            >
              Hủy
            </button>
            <button 
              @click="submitCancellation"
              class="px-6 py-2 bg-red-600 text-white font-bold rounded-xl hover:bg-red-700"
            >
              Xác nhận hủy
            </button>
          </div>
        </div>
      </div>
    </transition>

    <!-- Add Note Modal -->
    <transition
      enter-active-class="transition duration-200 ease-out"
      enter-from-class="opacity-0 scale-95"
      enter-to-class="opacity-100 scale-100"
      leave-active-class="transition duration-150 ease-in"
      leave-from-class="opacity-100 scale-100"
      leave-to-class="opacity-0 scale-95"
    >
      <div 
        v-if="showNoteModal"
        class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50"
        @click.self="showNoteModal = false"
      >
        <div class="bg-white rounded-2xl w-full max-w-md shadow-2xl">
          <div class="p-6 border-b">
            <h3 class="text-xl font-bold text-gray-800">Thêm Ghi Chú</h3>
          </div>
          
          <div class="p-6">
            <label class="block text-sm font-semibold text-gray-700 mb-2">Ghi chú:</label>
            <textarea 
              v-model="managerNote"
              rows="4"
              class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-green-600 focus:ring-2 focus:ring-green-100 outline-none"
              placeholder="Ví dụ: Giáo viên B sẽ dạy thay..."
            ></textarea>
          </div>
          
          <div class="p-4 border-t bg-gray-50 flex justify-end gap-3">
            <button 
              @click="showNoteModal = false"
              class="px-6 py-2 bg-gray-200 text-gray-700 font-bold rounded-xl hover:bg-gray-300"
            >
              Hủy
            </button>
            <button 
              @click="submitNote"
              class="px-6 py-2 bg-green-600 text-white font-bold rounded-xl hover:bg-green-700"
            >
              Lưu ghi chú
            </button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useToast } from 'vue-toastification';
// TODO: Replace mockApi with real API calls
// import mockApi from './mockData.js';
import { formatDate } from '@/views/Test/dateUtils.js';
import { XCircle, FileText } from 'lucide-vue-next';

const toast = useToast();

const loading = ref(false);
const leaveRequests = ref([]);
const managerId = ref(1); // Mock manager ID


// Cancel Modal
const showCancelModal = ref(false);
const selectedRequest = ref(null);
const cancellationReason = ref('');
const customCancellationReason = ref('');

// Note Modal
const showNoteModal = ref(false);
const managerNote = ref('');

const formatDateTime = (dateTimeStr) => {
  const date = new Date(dateTimeStr);
  return `${formatDate(date.toISOString().split('T')[0])} ${date.toTimeString().slice(0, 5)}`;
};

const loadLeaveRequests = async () => {
  loading.value = true;
  try {
    // TODO: Replace with real API calls
    /*
    const allSessions = await mockApi.getAllSessions();
    const classes = await mockApi.getClasses();
    const teachers = await mockApi.getTeachers();
    const rooms = await mockApi.getRooms();
    
    // Filter sessions with leave requests (both pending and approved)
    const withLeaveRequest = allSessions.filter(s => s.teacherLeaveRequest);
    
    // Enrich with details
    leaveRequests.value = withLeaveRequest.map(s => {
      const cls = classes.find(c => c.id === s.classId);
      const teacher = teachers.find(t => t.id === s.teacherId);
      const room = rooms.find(r => r.id === s.roomId);
      
      return {
        sessionId: s.id,
        className: cls ? cls.name : 'Unknown',
        teacherName: teacher ? teacher.name : 'Unknown',
        sessionDate: s.date,
        startTime: s.startTime,
        endTime: s.endTime,
        roomName: room ? room.name : 'Unknown',
        reason: s.teacherLeaveReason,
        requestedAt: s.teacherLeaveRequestedAt,
        approved: !!s.teacherLeaveApprovedBy
      };
    });
    */
    leaveRequests.value = []; // Placeholder
  } catch (error) {
    toast.error('Có lỗi xảy ra');
  } finally {
    loading.value = false;
  }
};

const approveLeave = async (request) => {
  if (confirm(`Duyệt đơn xin nghỉ của ${request.teacherName}?`)) {
    try {
      // TODO: await mockApi.approveTeacherLeave(request.sessionId, managerId.value, true);
      toast.success('Đã duyệt đơn xin nghỉ! Bạn có thể hủy buổi học hoặc thêm ghi chú.');
      await loadLeaveRequests();
    } catch (error) {
      toast.error('Có lỗi xảy ra');
    }
  }
};

const rejectLeave = async (request) => {
  if (confirm(`Từ chối đơn xin nghỉ của ${request.teacherName}?`)) {
    try {
      // TODO: await mockApi.approveTeacherLeave(request.sessionId, managerId.value, false);
      toast.success('Đã từ chối đơn xin nghỉ!');
      await loadLeaveRequests();
    } catch (error) {
      toast.error('Có lỗi xảy ra');
    }
  }
};

const openCancelModal = (request) => {
  selectedRequest.value = request;
  cancellationReason.value = '';
  customCancellationReason.value = '';
  showCancelModal.value = true;
};

const submitCancellation = async () => {
  const reason = cancellationReason.value === 'Khác' 
    ? customCancellationReason.value 
    : cancellationReason.value;
    
  if (!reason) {
    toast.warning('Vui lòng chọn/nhập lý do hủy');
    return;
  }
  
  try {
    // TODO: await mockApi.cancelSession(selectedRequest.value.sessionId, managerId.value, reason);
    toast.success('Đã hủy buổi học!');
    showCancelModal.value = false;
    await loadLeaveRequests();
  } catch (error) {
    toast.error('Có lỗi xảy ra');
  }
};

const openNoteModal = (request) => {
  selectedRequest.value = request;
  managerNote.value = '';
  showNoteModal.value = true;
};

const submitNote = async () => {
  if (!managerNote.value.trim()) {
    toast.warning('Vui lòng nhập ghi chú');
    return;
  }
  
  try {
    // TODO: await mockApi.addSessionNote(selectedRequest.value.sessionId, managerId.value, managerNote.value);
    toast.success('Đã thêm ghi chú!');
    showNoteModal.value = false;
    await loadLeaveRequests();
  } catch (error) {
    toast.error('Có lỗi xảy ra');
  }
};

onMounted(() => {
  loadLeaveRequests();
});
</script>
