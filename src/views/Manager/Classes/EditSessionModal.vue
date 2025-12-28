<template>
  <div 
    v-if="show" 
    class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
    @click.self="closeModal"
  >
    <div class="bg-white rounded-2xl w-full max-w-3xl shadow-2xl animate-fadeIn max-h-[90vh] overflow-y-auto">
      <!-- Modal Header -->
      <div class="sticky top-0 p-6 bg-gradient-to-r from-purple-600 to-purple-700 text-white flex justify-between items-center rounded-t-2xl z-10">
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
      
      <!-- Modal Body -->
      <div class="p-8 space-y-6">
        <!-- Session Info Card -->
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

        <!-- Time Range -->
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

        <!-- Room Selection -->
        <div>
          <label class="block text-sm font-semibold text-gray-700 mb-2">
            <MapPin class="w-4 h-4 inline mr-1" />
            Phòng học *
          </label>
          <select 
            v-model="formData.roomId"
            class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-purple-600 focus:ring-2 focus:ring-purple-100 outline-none transition-all"
          >
            <option v-for="room in rooms" :key="room.id" :value="room.id">
              {{ room.name }} (Sức chứa: {{ room.capacity }})
            </option>
          </select>
        </div>

        <!-- Status -->
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

        <!-- Note Field -->
        <div>
          <label class="block text-sm font-semibold text-gray-700 mb-2">
            <FileText class="w-4 h-4 inline mr-1" />
            Ghi chú thay đổi
            <span class="text-gray-400 font-normal text-xs ml-2">(Không bắt buộc)</span>
          </label>
          <textarea 
            v-model="formData.note" 
            rows="3" 
            placeholder="VD: Đổi giáo viên do giáo viên cũ ốm, Lùi giờ 30 phút do sự cố..."
            class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-purple-600 focus:ring-2 focus:ring-purple-100 outline-none transition-all"
          ></textarea>
          <p class="text-xs text-gray-500 mt-1">
            Ghi chú sẽ được hiển thị cho học viên để thông báo thay đổi đột xuất
          </p>
        </div>

        <!-- Validation Warning -->
        <div v-if="!isValid" class="p-4 bg-red-50 border border-red-200 rounded-xl flex items-start gap-3">
          <AlertTriangle class="w-5 h-5 text-red-600 shrink-0 mt-0.5" />
          <p class="text-sm text-red-800">
            Vui lòng điền đầy đủ thông tin bắt buộc và đảm bảo giờ kết thúc sau giờ bắt đầu.
          </p>
        </div>
      </div>
      
      <!-- Modal Footer -->
      <div class="sticky bottom-0 p-6 bg-gray-50 border-t flex justify-end gap-3 rounded-b-2xl">
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
import { ref, computed, watch, onMounted } from 'vue';
import { Calendar, Clock, MapPin, Info, FileText, X, Save, AlertTriangle } from 'lucide-vue-next';
import api from '@/services/api';
import { formatDate as formatDisplayDate } from '@/views/Test/dateUtils.js';

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  session: {
    type: Object,
    default: null
  }
});

const emit = defineEmits(['close', 'updated']);

const formData = ref({
  startTime: '',
  endTime: '',
  roomId: null,
  status: 'OPEN',
  note: ''
});

const rooms = ref([]);
const saving = ref(false);

const isValid = computed(() => {
  if (!formData.value.startTime || !formData.value.endTime || !formData.value.roomId) {
    return false;
  }
  
  // Check if end time is after start time
  const start = new Date(`2000-01-01T${formData.value.startTime}`);
  const end = new Date(`2000-01-01T${formData.value.endTime}`);
  
  return end > start;
});

// Load rooms
onMounted(async () => {
  try {
    const response = await api.get('/room');
    rooms.value = response.data;
  } catch (error) {
    console.error('Error loading rooms:', error);
  }
});

// Watch for prop changes to populate form
watch(() => props.session, (newSession) => {
  if (newSession) {
    // Parse time from startTime and endTime (can be ISO string or time string)
    const parseTime = (timeStr) => {
      if (!timeStr) return '';
      // If it's ISO format (2024-12-28T12:00:00)
      if (timeStr.includes('T')) {
        return new Date(timeStr).toTimeString().substring(0, 5);
      }
      // If it's already time format (12:00)
      if (timeStr.match(/^\d{2}:\d{2}/)) {
        return timeStr.substring(0, 5);
      }
      return timeStr;
    };

    formData.value = {
      startTime: parseTime(newSession.startTime),
      endTime: parseTime(newSession.endTime),
      roomId: newSession.roomId || null,
      status: newSession.status || 'OPEN',
      note: newSession.note || ''
    };
  }
}, { immediate: true });

const formatDate = (dateStr) => {
  if (!dateStr) return 'N/A';
  return formatDisplayDate(dateStr);
};

const closeModal = () => {
  emit('close');
};

const saveChanges = async () => {
  if (!isValid.value || !props.session) return;
  
  saving.value = true;
  try {
    // Build the update payload
    // Need to combine date from session with new time
    const sessionDate = props.session.date; // YYYY-MM-DD format
    
    const startDateTime = `${sessionDate}T${formData.value.startTime}:00`;
    const endDateTime = `${sessionDate}T${formData.value.endTime}:00`;
    
    const updateData = {
      startTime: startDateTime,
      endTime: endDateTime,
      room: { id: formData.value.roomId },
      status: formData.value.status,
      note: formData.value.note || null,
      // Keep other fields from original session
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
      status: formData.value.status,
      note: formData.value.note
    });
    
    closeModal();
  } catch (error) {
    console.error('Error updating session:', error);
    alert('❌ Lỗi khi cập nhật buổi học: ' + (error.response?.data?.message || error.message));
  } finally {
    saving.value = false;
  }
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

