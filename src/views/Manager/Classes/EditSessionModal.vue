<template>
  <div 
    v-if="show" 
    class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
    @click.self="closeModal"
  >
    <div class="bg-white rounded-2xl w-full max-w-3xl shadow-2xl animate-fadeIn flex flex-col max-h-[90vh]">
      <div class="sticky top-0 p-6 bg-gradient-to-r from-purple-600 to-purple-700 text-white flex justify-between items-center rounded-t-2xl shrink-0 z-10">
        <div>
          <h2 class="text-2xl font-bold flex items-center gap-2">
            <Calendar class="w-6 h-6" />
            Chỉnh Sửa Buổi Học
          </h2>
          <p class="text-purple-100 text-sm mt-1" v-if="session">
            {{ formatDate(session.date) }}
          </p>
        </div>
        <button 
          @click="closeModal" 
          class="w-10 h-10 bg-white/20 rounded-full hover:bg-white/30 transition-all flex items-center justify-center"
        >
          <X class="w-6 h-6" />
        </button>
      </div>
      
      <div class="p-8 space-y-6 overflow-y-auto custom-scrollbar">
        <div class="p-4 bg-purple-50 border border-purple-200 rounded-xl">
          <div class="grid grid-cols-2 gap-3 text-sm">
            <div>
              <span class="text-gray-600 font-semibold">Lớp học:</span>
              <span class="ml-2 text-gray-800">{{ session?.className || 'N/A' }}</span>
            </div>
            <div>
              <span class="text-gray-600 font-semibold">Ngày:</span>
              <span class="ml-2 text-gray-800">{{ formatDate(session?.date) }}</span>
            </div>
          </div>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">
              <Clock class="w-4 h-4 inline mr-1" />
              Giờ bắt đầu *
            </label>
            <input 
              v-model="formData.startTime" 
              type="time"
              class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-purple-600 focus:ring-2 focus:ring-purple-100 outline-none transition-all"
            />
          </div>

          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">
              <Clock class="w-4 h-4 inline mr-1" />
              Giờ kết thúc *
            </label>
            <input 
              v-model="formData.endTime" 
              type="time"
              class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-purple-600 focus:ring-2 focus:ring-purple-100 outline-none transition-all"
            />
          </div>
        </div>

        <div>
          <label class="block text-sm font-semibold text-gray-700 mb-2">
            <MapPin class="w-4 h-4 inline mr-1" />
            Phòng học *
            <span v-if="loadingRooms" class="text-purple-600 text-xs ml-2 italic">
              (Đang tìm phòng trống...)
            </span>
          </label>
          <select 
            v-model="formData.roomId"
            :disabled="loadingRooms || !formData.startTime || !formData.endTime"
            class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-purple-600 focus:ring-2 focus:ring-purple-100 outline-none transition-all disabled:bg-gray-100 disabled:text-gray-400"
          >
            <option :value="null" disabled>-- Chọn phòng học --</option>
            <option v-for="room in rooms" :key="room.id" :value="room.id">
              {{ room.name }} (Sức chứa: {{ room.capacity }})
            </option>
          </select>
          
          <p v-if="!loadingRooms && rooms.length === 0 && formData.startTime && formData.endTime" class="text-red-500 text-sm mt-1">
            <AlertTriangle class="w-4 h-4 inline mr-1"/>
            Không có phòng nào trống trong khung giờ này.
          </p>
        </div>

        <div>
          <label class="block text-sm font-semibold text-gray-700 mb-2">
            <Info class="w-4 h-4 inline mr-1" />
            Trạng thái *
          </label>
          <select 
            v-model="formData.status"
            class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-purple-600 focus:ring-2 focus:ring-purple-100 outline-none transition-all"
          >
            <option value="OPEN">Mở đăng ký</option>
            <option value="CLOSED">Đã đóng</option>
            <option value="CANCELLED">Đã hủy</option>
          </select>
        </div>

        <div>
          <label class="block text-sm font-semibold text-gray-700 mb-2">
            <FileText class="w-4 h-4 inline mr-1" />
            Ghi chú thay đổi
          </label>
          <textarea 
            v-model="formData.note" 
            rows="3" 
            class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-purple-600 focus:ring-2 focus:ring-purple-100 outline-none transition-all"
          ></textarea>
        </div>

        <div v-if="!isValid" class="p-4 bg-red-50 border border-red-200 rounded-xl flex items-start gap-3">
          <AlertTriangle class="w-5 h-5 text-red-600 shrink-0 mt-0.5" />
          <p class="text-sm text-red-800">
            Vui lòng chọn đầy đủ thông tin. Giờ kết thúc phải sau giờ bắt đầu.
          </p>
        </div>
      </div>
      
      <div class="p-6 bg-gray-50 border-t flex justify-end gap-3 rounded-b-2xl shrink-0">
        <button 
          @click="closeModal" 
          class="px-6 py-3 bg-gray-200 text-gray-700 rounded-xl font-semibold hover:bg-gray-300 transition-all"
        >
          Hủy
        </button>
        <button 
          @click="saveChanges" 
          :disabled="!isValid || saving"
          class="px-6 py-3 bg-gradient-to-r from-purple-600 to-purple-700 text-white rounded-xl font-semibold hover:shadow-lg transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
        >
          <Save class="w-5 h-5" />
          {{ saving ? 'Đang lưu...' : 'Lưu thay đổi' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { Calendar, Clock, MapPin, Info, FileText, X, Save, AlertTriangle } from 'lucide-vue-next';
import api from '@/services/api';
import { formatDate as formatDisplayDate } from '@/views/Test/dateUtils.js';
import { useToast } from 'vue-toastification';

const props = defineProps({
  show: { type: Boolean, default: false },
  session: { type: Object, default: null }
});

const emit = defineEmits(['close', 'updated']);
const toast = useToast();

const formData = ref({
  startTime: '',
  endTime: '',
  roomId: null,
  status: 'OPEN',
  note: ''
});

const rooms = ref([]);
const saving = ref(false);
const loadingRooms = ref(false);

const isValid = computed(() => {
  if (!formData.value.startTime || !formData.value.endTime || !formData.value.roomId) return false;
  
  const start = new Date(`2000-01-01T${formData.value.startTime}`);
  const end = new Date(`2000-01-01T${formData.value.endTime}`);
  
  return end > start;
});

// Hàm format hiển thị ngày
const formatDate = (dateStr) => {
  if (!dateStr) return 'N/A';
  return formatDisplayDate(dateStr);
};

// --- LOGIC MỚI: Gọi API lấy phòng trống ---
const fetchAvailableRooms = async () => {
  // Chỉ gọi khi có đủ 2 mốc thời gian và props.session đã sẵn sàng
  if (!formData.value.startTime || !formData.value.endTime || !props.session) {
    rooms.value = [];
    return;
  }

  // Validate đơn giản trước khi gọi
  const start = new Date(`2000-01-01T${formData.value.startTime}`);
  const end = new Date(`2000-01-01T${formData.value.endTime}`);
  if (end <= start) return; // Không gọi nếu giờ sai

  loadingRooms.value = true;
  try {
    const date = props.session.date; // YYYY-MM-DD
    const params = {
      startTime: `${date}T${formData.value.startTime}:00`,
      endTime: `${date}T${formData.value.endTime}:00`,
      excludeSessionId: props.session.id // Quan trọng: Trừ buổi học hiện tại ra
    };

    const response = await api.get('/room/available', { params });
    rooms.value = response.data;

    // Check logic: Nếu phòng đang chọn không còn nằm trong list mới -> Reset về null
    if (formData.value.roomId) {
      const isStillAvailable = rooms.value.some(r => r.id === formData.value.roomId);
      if (!isStillAvailable) {
        formData.value.roomId = null;
        // Optional: toast.warning('Phòng cũ không còn trống trong giờ mới này!');
      }
    }
  } catch (error) {
    console.error('Error fetching rooms:', error);
    toast.error('Không thể tải danh sách phòng trống');
  } finally {
    loadingRooms.value = false;
  }
};

// --- Watchers ---

// 1. Khi mở modal (session thay đổi) -> Fill data và load phòng lần đầu
watch(() => props.session, (newSession) => {
  if (newSession) {
    const parseTime = (timeStr) => {
      if (!timeStr) return '';
      if (timeStr.includes('T')) return new Date(timeStr).toTimeString().substring(0, 5);
      return timeStr.substring(0, 5);
    };

    formData.value = {
      startTime: parseTime(newSession.startTime),
      endTime: parseTime(newSession.endTime),
      roomId: newSession.roomId || (newSession.room ? newSession.room.id : null),
      status: newSession.status || 'OPEN',
      note: newSession.note || ''
    };
    
    // Gọi ngay lập tức để lấy list phòng cho giờ hiện tại
    fetchAvailableRooms();
  }
}, { immediate: true });

// 2. Khi user thay đổi giờ -> Load lại list phòng
watch([() => formData.value.startTime, () => formData.value.endTime], () => {
  // Dùng debounce nếu muốn (ở đây gọi trực tiếp cho đơn giản)
  fetchAvailableRooms();
});

const closeModal = () => emit('close');

const saveChanges = async () => {
  if (!isValid.value || !props.session) return;
  
  saving.value = true;
  try {
    const sessionDate = props.session.date;
    const startDateTime = `${sessionDate}T${formData.value.startTime}:00`;
    const endDateTime = `${sessionDate}T${formData.value.endTime}:00`;
    
    const updateData = {
      startTime: startDateTime,
      endTime: endDateTime,
      room: { id: formData.value.roomId },
      status: formData.value.status,
      note: formData.value.note || null,
      // Các field khác giữ nguyên logic cũ
      fitnessClass: props.session.fitnessClass ? { id: props.session.fitnessClass.id || props.session.fitnessClassId } : null,
      schedulePattern: props.session.schedulePattern ? { id: props.session.schedulePattern.id } : null,
      capacity: props.session.capacity
    };
    
    await api.put(`/classschedule/${props.session.id}`, updateData);
    
    emit('updated', {
      ...props.session,
      startTime: startDateTime,
      endTime: endDateTime,
      roomId: formData.value.roomId,
      room: rooms.value.find(r => r.id === formData.value.roomId), // Cập nhật cả object room để hiển thị UI bên ngoài
      status: formData.value.status,
      note: formData.value.note
    });
    
    closeModal();
    toast.success('Cập nhật thành công!');
  } catch (error) {
    // Nếu BE trả về 409 hoặc 400
    const msg = error.response?.data || 'Lỗi khi cập nhật buổi học';
    toast.error(msg);
  } finally {
    saving.value = false;
  }
};
</script>

<style scoped>
/* CSS cho scrollbar và animation */
.custom-scrollbar::-webkit-scrollbar { width: 6px; }
.custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #d1d5db; border-radius: 10px; }
.custom-scrollbar::-webkit-scrollbar-thumb:hover { background: #a1a1aa; }

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
.animate-fadeIn { animation: fadeIn 0.3s ease-out; }
</style>