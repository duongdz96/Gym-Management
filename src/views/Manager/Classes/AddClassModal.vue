<template>
  <div 
    v-if="show" 
    class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
    @click.self="$emit('close')"
  >
    <div class="bg-white rounded-2xl w-full max-w-4xl max-h-[90vh] overflow-y-auto shadow-2xl">
      <!-- Modal Header -->
      <div class="sticky top-0 p-6 bg-gradient-to-r from-emerald-600 to-emerald-700 text-white flex justify-between items-center rounded-t-2xl z-50">
        <h2 class="text-2xl font-bold">Tạo Lớp Học Mới</h2>
        <button 
          @click="$emit('close')" 
          class="w-10 h-10 bg-white/20 rounded-full hover:bg-white/30 transition-all flex items-center justify-center"
        >
          <X class="w-6 h-6" />
        </button>
      </div>
      
      <!-- Modal Body -->
      <div class="p-8 pb-32">
        <!-- Steps -->
        <div class="flex justify-between mb-8 relative">
          <!-- Progress Line -->
          <div class="absolute top-5 left-0 right-0 h-1 bg-gray-300" style="z-index: 0;"></div>
          <div 
            v-for="(step, index) in steps" 
            :key="index"
            class="flex flex-col items-center flex-1 relative"
            style="z-index: 1;"
          >
            <div
              class="w-10 h-10 rounded-full flex items-center justify-center font-bold transition-all border-4"
              :class="currentStep === index
                ? 'bg-white border-emerald-600 text-emerald-600 scale-110 shadow-lg'
                : currentStep > index
                  ? 'bg-white border-green-500 text-green-500'
                  : 'bg-white border-gray-300 text-gray-400'"
            >
              {{ index + 1 }}
            </div>
            <div
              class="mt-2 text-sm font-semibold text-center"
              :class="currentStep === index ? 'text-emerald-600' : 'text-gray-500'"
            >
              {{ step }}
            </div>
          </div>
        </div>

        <!-- Step Content -->
        <div class="relative">
          <!-- Step 1: Basic Info -->
          <div v-show="currentStep === 0" class="space-y-4 text-gray-800">
            <h3 class="text-xl font-bold text-gray-800 mb-4 flex items-center gap-2">
              <Info class="w-6 h-6 text-emerald-600" />
              Thông tin cơ bản
            </h3>
            
            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-2">Tên lớp học * (tối đa 50 ký tự)</label>
              <input
                v-model="newClass.name"
                type="text"
                maxlength="50"
                placeholder="VD: Yoga Buổi Sáng"
                class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-emerald-600 focus:ring-2 focus:ring-emerald-100 outline-none transition-all"
                :class="newClass.name && newClass.name.length > 50 ? 'border-red-500' : ''"
              />
              <p v-if="newClass.name && newClass.name.length > 50" class="text-red-500 text-xs mt-1">Tên lớp không được vượt quá 50 ký tự</p>
              <p class="text-gray-500 text-xs mt-1">{{ newClass.name ? newClass.name.length : 0 }}/50 ký tự</p>
            </div>
            
            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-2">Mô tả *</label>
              <textarea
                v-model="newClass.description"
                rows="3"
                placeholder="Mô tả về lớp học..."
                class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-emerald-600 focus:ring-2 focus:ring-emerald-100 outline-none transition-all"
              ></textarea>
            </div>
            
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-semibold text-gray-700 mb-2">Độ khó *</label>
                <select
                  v-model="newClass.difficulty"
                  class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-emerald-600 focus:ring-2 focus:ring-emerald-100 outline-none transition-all"
                >
                  <option value="Beginner">Beginner</option>
                  <option value="Intermediate">Intermediate</option>
                  <option value="Advanced">Advanced</option>
                </select>
              </div>
              
              <div>
                <label class="block text-sm font-semibold text-gray-700 mb-2">Số học viên tối đa *</label>
                <input
                  v-model.number="newClass.maxStudents"
                  type="number"
                  min="1"
                  max="50"
                  class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-emerald-600 focus:ring-2 focus:ring-emerald-100 outline-none transition-all"
                />
              </div>
            </div>
          </div>

          <!-- Step 2: Schedule -->
          <div v-show="currentStep === 1" class="space-y-4 text-gray-800">
            <h3 class="text-xl font-bold text-gray-800 mb-4 flex items-center gap-2">
              <Calendar class="w-6 h-6 text-emerald-600" />
              Lịch học
            </h3>
            
            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-2">Loại lịch *</label>
              <select
                v-model="newClass.patternType"
                @change="onPatternTypeChange"
                class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-emerald-600 focus:ring-2 focus:ring-emerald-100 outline-none transition-all"
              >
                <option value="weekly">Hàng tuần (lặp đến hết tháng hiện tại)</option>
                <option value="monthly">Hàng tháng (lặp đến hết tháng 12)</option>
                <option value="custom_range">Tùy chỉnh (theo khoảng thời gian)</option>
                <option value="no_repeat">Không lặp lại (chọn nhiều ngày)</option>
              </select>
            </div>
            
            <div v-if="newClass.patternType !== 'no_repeat'" class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-semibold text-gray-700 mb-2">Ngày bắt đầu *</label>
                <input
                  v-model="newClass.startDate"
                  type="date"
                  @change="onStartDateChange"
                  class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-emerald-600 focus:ring-2 focus:ring-emerald-100 outline-none transition-all"
                />
              </div>
              
              <div>
                <label class="block text-sm font-semibold text-gray-700 mb-2">
                  {{ newClass.patternType === 'custom_range' ? 'Ngày kết thúc *' : 'Ngày kết thúc (tự động)' }}
                </label>
                <input
                  :value="newClass.endDate"
                  type="date"
                  :disabled="newClass.patternType !== 'custom_range'"
                  @input="newClass.endDate = $event.target.value"
                  class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-emerald-600 focus:ring-2 focus:ring-emerald-100 outline-none transition-all disabled:bg-gray-100 disabled:cursor-not-allowed"
                />
              </div>
            </div>
            
            <!-- Multiple Date Picker for No Repeat -->
            <div v-if="newClass.patternType === 'no_repeat'" class="space-y-3">
              <label class="block text-sm font-semibold text-gray-700">Chọn các ngày *</label>
              <div class="flex gap-2">
                <input
                  v-model="tempDate"
                  type="date"
                  @change="addDate"
                  class="flex-1 px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-emerald-600 focus:ring-2 focus:ring-emerald-100 outline-none transition-all"
                />
                <button
                  @click="addDate"
                  type="button"
                  class="px-6 py-3 bg-gradient-to-r from-emerald-600 to-emerald-700 text-white rounded-xl font-semibold hover:shadow-lg transition-all flex items-center gap-2"
                >
                  <Plus class="w-5 h-5" />
                  Thêm
                </button>
              </div>
              
              <div v-if="newClass.selectedDates && newClass.selectedDates.length > 0" class="flex flex-wrap gap-2">
                <div
                  v-for="(date, idx) in newClass.selectedDates"
                  :key="idx"
                  class="px-4 py-2 bg-gradient-to-r from-emerald-600 to-emerald-700 text-white rounded-full text-sm font-semibold flex items-center gap-2"
                >
                  <Calendar class="w-4 h-4" /> {{ formatDate(date) }}
                  <button 
                    @click="removeDate(idx)" 
                    type="button"
                    class="w-5 h-5 bg-white/20 rounded-full hover:bg-white/30 transition-all flex items-center justify-center"
                  >
                    <X class="w-3 h-3" />
                  </button>
                </div>
              </div>
              <p v-else class="text-gray-500 text-sm italic">Chưa có ngày nào được chọn</p>
            </div>
            
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-semibold text-gray-700 mb-2">Giờ bắt đầu *</label>
                <input
                  v-model="newClass.startTime"
                  type="time"
                  class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-emerald-600 focus:ring-2 focus:ring-emerald-100 outline-none transition-all"
                  :class="newClass.startTime && newClass.endTime && newClass.startTime >= newClass.endTime ? 'border-red-500' : ''"
                />
              </div>
              
              <div>
                <label class="block text-sm font-semibold text-gray-700 mb-2">Giờ kết thúc *</label>
                <input
                  v-model="newClass.endTime"
                  type="time"
                  class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-emerald-600 focus:ring-2 focus:ring-emerald-100 outline-none transition-all"
                  :class="newClass.startTime && newClass.endTime && newClass.startTime >= newClass.endTime ? 'border-red-500' : ''"
                />
              </div>
            </div>
            <p v-if="newClass.startTime && newClass.endTime && newClass.startTime >= newClass.endTime" class="text-red-500 text-sm flex items-center gap-2">
              <AlertTriangle class="w-4 h-4" />
              Giờ bắt đầu phải nhỏ hơn giờ kết thúc
            </p>
            
            <div v-if="newClass.patternType !== 'no_repeat'" class="space-y-2">
              <label class="block text-sm font-semibold text-gray-700">Các ngày trong tuần *</label>
              <div class="flex flex-wrap gap-2">
                <label
                  v-for="(day, index) in daysOfWeek"
                  :key="index"
                  class="flex items-center gap-2 px-4 py-2 border-2 rounded-lg cursor-pointer transition-all hover:border-emerald-600 hover:bg-gray-50"
                  :class="newClass.daysOfWeek.includes(index) ? 'border-emerald-600 bg-emerald-50' : 'border-gray-200'"
                >
                  <input
                    type="checkbox"
                    :value="index"
                    v-model="newClass.daysOfWeek"
                    class="w-4 h-4 text-emerald-600 rounded focus:ring-emerald-500"
                  />
                  <span :class="newClass.daysOfWeek.includes(index) ? 'text-emerald-600 font-bold' : 'text-gray-700'">
                    {{ day }}
                  </span>
                </label>
              </div>
            </div>
            
            <div v-if="previewSessions.length > 0" class="mt-6 p-4 bg-gray-50 rounded-xl border-2 border-gray-200">
              <h4 class="font-bold text-gray-800 mb-3 flex items-center gap-2">
                <Eye class="w-5 h-5 text-emerald-600" />
                Xem trước: {{ previewSessions.length }} buổi học
              </h4>
              <div class="space-y-2 max-h-40 overflow-y-auto">
                <div 
                  v-for="(session, idx) in previewSessions.slice(0, 5)" 
                  :key="idx"
                  class="px-3 py-2 bg-white rounded-lg text-sm text-gray-600 flex items-center gap-2"
                >
                  <Calendar class="w-4 h-4" /> {{ formatDate(session.date) }} - {{ session.startTime }} đến {{ session.endTime }}
                </div>
                <div v-if="previewSessions.length > 5" class="text-center text-sm text-gray-500 italic">
                  ... và {{ previewSessions.length - 5 }} buổi nữa
                </div>
              </div>
            </div>
          </div>

          <!-- Step 3: Room Selection -->
          <div v-show="currentStep === 2" class="space-y-4 text-gray-800">
            <h3 class="text-xl font-bold text-gray-800 mb-4 flex items-center gap-2">
              <MapPin class="w-6 h-6 text-emerald-600" />
              Chọn phòng học
            </h3>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div 
                v-for="room in rooms" 
                :key="room.id"
                @click="selectRoom(room.id)"
                class="p-5 border-2 rounded-xl cursor-pointer transition-all"
                :class="{
                  'border-green-500 bg-green-50': newClass.roomId === room.id,
                  'border-red-300 bg-red-50 cursor-not-allowed opacity-60': isRoomConflicted(room.id) || !isRoomCapacityValid(room),
                  'border-gray-200 hover:border-emerald-600 hover:bg-gray-50': newClass.roomId !== room.id && !isRoomConflicted(room.id) && isRoomCapacityValid(room)
                }"
              >
                <h4 class="font-bold text-gray-800 mb-2">{{ room.name }}</h4>
                <p class="text-sm text-gray-600 mb-3">{{ room.description }}</p>
                <div
                  class="text-sm font-semibold flex items-center gap-1"
                  :class="!isRoomCapacityValid(room) ? 'text-emerald-600' : 'text-gray-700'"
                >
                  <Users class="w-4 h-4" />
                  Sức chứa: {{ room.capacity }} người
                </div>
                
                <div v-if="!isRoomCapacityValid(room)" class="mt-3 p-2 bg-red-100 rounded text-xs text-red-700 font-semibold flex items-center gap-2">
                  <XCircle class="w-4 h-4" />
                  Phòng không đủ chỗ cho {{ newClass.maxStudents }} học viên
                </div>
                
                <div v-else-if="isRoomConflicted(room.id)" class="mt-3 p-2 bg-red-100 rounded text-xs text-red-700 font-semibold">
                  <div class="flex items-center gap-2 mb-1">
                    <AlertTriangle class="w-4 h-4" />
                    Phòng bị trùng lịch
                  </div>
                  <div class="mt-2 space-y-1 ml-6">
                    <div v-for="(conflict, idx) in getRoomConflicts(room.id)" :key="idx" class="flex items-center gap-1">
                      <Calendar class="w-3 h-3" />
                      {{ conflict.date }}: {{ conflict.time }} - {{ conflict.className }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Step 4: Review -->
          <div v-show="currentStep === 3" class="space-y-4 text-gray-800">
            <h3 class="text-xl font-bold text-gray-800 mb-4 flex items-center gap-2">
              <CheckSquare class="w-6 h-6 text-emerald-600" />
              Xác nhận thông tin
            </h3>
            
            <div class="p-5 bg-gray-50 rounded-xl space-y-3">
              <h4 class="font-bold text-gray-800 flex items-center gap-2">
                <Info class="w-5 h-5 text-emerald-600" />
                Thông tin lớp học
              </h4>
              <div class="grid grid-cols-2 gap-3 text-sm">
                <div><span class="text-gray-600">Tên lớp:</span> <span class="font-semibold">{{ newClass.name }}</span></div>
                <div><span class="text-gray-600">Độ khó:</span> <span class="font-semibold">{{ newClass.difficulty }}</span></div>
                <div class="col-span-2"><span class="text-gray-600">Mô tả:</span> <span class="font-semibold">{{ newClass.description }}</span></div>
                <div><span class="text-gray-600">Số học viên:</span> <span class="font-semibold">{{ newClass.maxStudents }}</span></div>
              </div>
            </div>
            
            <div class="p-5 bg-gray-50 rounded-xl space-y-3">
              <h4 class="font-bold text-gray-800 flex items-center gap-2">
                <Calendar class="w-5 h-5 text-emerald-600" />
                Lịch học
              </h4>
              <div class="grid grid-cols-2 gap-3 text-sm">
                <div><span class="text-gray-600">Loại lịch:</span> <span class="font-semibold">{{ getPatternTypeText(newClass.patternType) }}</span></div>
                <div><span class="text-gray-600">Thời gian:</span> <span class="font-semibold">{{ newClass.startTime }} - {{ newClass.endTime }}</span></div>
                <div v-if="newClass.patternType !== 'no_repeat'">
                  <span class="text-gray-600">Từ ngày:</span> <span class="font-semibold">{{ formatDate(newClass.startDate) }}</span>
                </div>
                <div v-if="newClass.patternType !== 'no_repeat'">
                  <span class="text-gray-600">Đến ngày:</span> <span class="font-semibold">{{ formatDate(newClass.endDate) }}</span>
                </div>
                <div><span class="text-gray-600">Tổng số buổi:</span> <span class="font-semibold">{{ previewSessions.length }} buổi</span></div>
              </div>
            </div>
            
            <div class="p-5 bg-gray-50 rounded-xl space-y-3">
              <h4 class="font-bold text-gray-800 flex items-center gap-2">
                <MapPin class="w-5 h-5 text-emerald-600" />
                Phòng học
              </h4>
              <div class="text-sm">
                <span class="text-gray-600">Phòng:</span> <span class="font-semibold">{{ getRoomName(newClass.roomId) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Modal Footer -->
      <div class="sticky bottom-0 p-6 bg-white border-t flex justify-end gap-3 rounded-b-2xl z-50">
        <button 
          v-if="currentStep > 0" 
          @click="currentStep--" 
          class="px-6 py-3 bg-gray-200 text-gray-700 rounded-xl font-semibold hover:bg-gray-300 transition-all"
        >
          ← Quay lại
        </button>
        
        <button
          v-if="currentStep < 3"
          @click="nextStep"
          class="px-6 py-3 bg-gradient-to-r from-emerald-600 to-emerald-700 text-white rounded-xl font-semibold hover:shadow-lg transition-all"
        >
          Tiếp theo →
        </button>
        
        <button 
          v-if="currentStep === 3" 
          @click="createClass" 
          :disabled="creating"
          class="px-6 py-3 bg-gradient-to-r from-green-500 to-green-600 text-white rounded-xl font-semibold hover:shadow-lg transition-all flex items-center gap-2 disabled:opacity-50"
        >
          <CheckCircle class="w-5 h-5" />
          {{ creating ? 'Đang tạo...' : 'Tạo lớp học' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { useToast } from 'vue-toastification';
import unifiedApi from '@/services/unifiedClassApi.js';
import { formatDate } from '@/views/Test/dateUtils.js';
import api from '@/services/api';
import { 
  Plus,
  Calendar,
  Eye,
  X,
  Info,
  CheckSquare,
  AlertTriangle,
  MapPin,
  Users,
  XCircle,
  CheckCircle
} from 'lucide-vue-next';

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  rooms: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(['close', 'created']);

const toast = useToast();
const currentStep = ref(0);
const creating = ref(false);
const steps = ['Thông tin cơ bản', 'Lịch học', 'Chọn phòng', 'Xác nhận'];
const daysOfWeek = ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'];
const previewSessions = ref([]);
const roomConflicts = ref({});
const tempDate = ref('');

const getEmptyClass = () => ({
  name: '',
  description: '',
  difficulty: 'Beginner',
  maxStudents: 20,
  roomId: null,
  patternType: 'weekly',
  startTime: '07:00',
  endTime: '08:30',
  daysOfWeek: [],
  startDate: '',
  endDate: '',
  selectedDates: []
});

const newClass = ref(getEmptyClass());

// Validation helpers
const getTodayString = () => {
  const today = new Date();
  return today.toISOString().split('T')[0];
};

const validateStep = (step) => {
  if (step === 0) {
    const name = newClass.value.name || '';
    if (name.trim().length === 0) {
      toast.error('Vui lòng nhập tên lớp học');
      return false;
    }
    if (name.length > 50) {
      toast.error('Tên lớp không được vượt quá 50 ký tự');
      return false;
    }
    if (!newClass.value.description) {
      toast.error('Vui lòng nhập mô tả lớp học');
      return false;
    }
    if (newClass.value.maxStudents <= 0) {
      toast.error('Số học viên phải lớn hơn 0');
      return false;
    }
    return true;
  }
  
  if (step === 1) {
    if (!newClass.value.startTime || !newClass.value.endTime) {
      toast.error('Vui lòng nhập giờ bắt đầu và kết thúc');
      return false;
    }
    
    if (newClass.value.startTime >= newClass.value.endTime) {
      toast.error('Giờ bắt đầu phải nhỏ hơn giờ kết thúc');
      return false;
    }
    
    if (newClass.value.patternType === 'no_repeat') {
      if (!newClass.value.selectedDates || newClass.value.selectedDates.length === 0) {
        toast.error('Vui lòng chọn ít nhất một ngày');
        return false;
      }
      const today = getTodayString();
      const hasPastDate = newClass.value.selectedDates.some(dateStr => dateStr < today);
      if (hasPastDate) {
        toast.error('Không được chọn ngày trong quá khứ');
        return false;
      }
    } else {
      if (!newClass.value.startDate) {
        toast.error('Vui lòng chọn ngày bắt đầu');
        return false;
      }
      if (!newClass.value.endDate) {
        toast.error('Vui lòng chọn ngày kết thúc');
        return false;
      }
      const today = getTodayString();
      if (newClass.value.startDate < today) {
        toast.error('Ngày bắt đầu không được là quá khứ');
        return false;
      }
      if (newClass.value.startDate > newClass.value.endDate) {
        toast.error('Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc');
        return false;
      }
      if (newClass.value.daysOfWeek.length === 0) {
        toast.error('Vui lòng chọn ít nhất một ngày trong tuần');
        return false;
      }
    }
    return true;
  }
  
  if (step === 2) {
    if (!newClass.value.roomId) {
      toast.error('Vui lòng chọn phòng học');
      return false;
    }
    const room = props.rooms.find(r => r.id === newClass.value.roomId);
    if (!room || !isRoomCapacityValid(room)) {
      toast.error('Phòng không đủ chỗ cho số học viên đã chọn');
      return false;
    }
    if (isRoomConflicted(newClass.value.roomId)) {
      toast.error('Phòng bị trùng lịch, vui lòng chọn phòng khác');
      return false;
    }
    return true;
  }
  
  return true;
};

const nextStep = async () => {
  if (!validateStep(currentStep.value)) {
    return;
  }
  
  currentStep.value++;
  if (currentStep.value === 2) {
    await updatePreview();
  }
};

const onPatternTypeChange = () => {
  previewSessions.value = [];
  roomConflicts.value = {};
  
  if (newClass.value.startDate) {
    onStartDateChange();
  }
  if (newClass.value.patternType === 'no_repeat') {
    newClass.value.selectedDates = [];
    tempDate.value = '';
  }
  if (newClass.value.patternType !== 'no_repeat') {
    newClass.value.daysOfWeek = [];
  }
};

const onStartDateChange = () => {
  if (!newClass.value.startDate) return;
  
  const startDate = new Date(newClass.value.startDate);
  
  if (newClass.value.patternType === 'weekly') {
    const endOfMonth = new Date(startDate.getFullYear(), startDate.getMonth() + 1, 0);
    newClass.value.endDate = endOfMonth.toISOString().split('T')[0];
  } else if (newClass.value.patternType === 'monthly') {
    const endOfYear = new Date(startDate.getFullYear(), 11, 31);
    newClass.value.endDate = endOfYear.toISOString().split('T')[0];
  }
};

const addDate = () => {
  if (!tempDate.value) return;
  
  if (!newClass.value.selectedDates) {
    newClass.value.selectedDates = [];
  }
  
  if (!newClass.value.selectedDates.includes(tempDate.value)) {
    newClass.value.selectedDates.push(tempDate.value);
    newClass.value.selectedDates.sort();
  }
  
  tempDate.value = '';
  updatePreview();
};

const removeDate = (index) => {
  newClass.value.selectedDates.splice(index, 1);
  updatePreview();
};

const isRoomCapacityValid = (room) => {
  return room.capacity >= newClass.value.maxStudents;
};

const selectRoom = (roomId) => {
  const room = props.rooms.find(r => r.id === roomId);
  if (room && isRoomCapacityValid(room) && !isRoomConflicted(roomId)) {
    newClass.value.roomId = roomId;
  }
};

const isRoomConflicted = (roomId) => {
  return roomConflicts.value[roomId] && roomConflicts.value[roomId].length > 0;
};

const getRoomConflicts = (roomId) => {
  return roomConflicts.value[roomId] || [];
};

const getRoomName = (roomId) => {
  const room = props.rooms.find(r => r.id === roomId);
  return room ? room.name : 'Chưa chọn';
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

// Helper function to format datetime in local timezone
const formatLocalDateTime = (date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');
  return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
};

const updatePreview = async () => {
  const sessions = [];
  const start = new Date(newClass.value.startDate);
  const end = new Date(newClass.value.endDate);
  
  if (newClass.value.patternType === 'no_repeat') {
    if (newClass.value.selectedDates && newClass.value.selectedDates.length > 0) {
      newClass.value.selectedDates.forEach(date => {
        const [startHour, startMin] = newClass.value.startTime.split(':').map(Number);
        const [endHour, endMin] = newClass.value.endTime.split(':').map(Number);
        const startTime = new Date(date);
        startTime.setHours(startHour, startMin, 0, 0);
        const endTime = new Date(date);
        endTime.setHours(endHour, endMin, 0, 0);
        
        sessions.push({
          startTime: formatLocalDateTime(startTime),
          endTime: formatLocalDateTime(endTime)
        });
      });
    }
  } else {
    let currentDate = new Date(start);
    while (currentDate <= end) {
      const dayOfWeek = currentDate.getDay();
      if (newClass.value.daysOfWeek.includes(dayOfWeek)) {
        const [startHour, startMin] = newClass.value.startTime.split(':').map(Number);
        const [endHour, endMin] = newClass.value.endTime.split(':').map(Number);
        const startTime = new Date(currentDate);
        startTime.setHours(startHour, startMin, 0, 0);
        const endTime = new Date(currentDate);
        endTime.setHours(endHour, endMin, 0, 0);
        
        sessions.push({
          startTime: formatLocalDateTime(startTime),
          endTime: formatLocalDateTime(endTime)
        });
      }
      currentDate.setDate(currentDate.getDate() + 1);
    }
  }
  
  previewSessions.value = sessions.map((s, idx) => ({
    id: idx,
    classId: 999,
    date: new Date(s.startTime).toISOString().split('T')[0],
    startTime: new Date(s.startTime).toTimeString().substring(0, 5),
    endTime: new Date(s.endTime).toTimeString().substring(0, 5),
    roomId: newClass.value.roomId,
    teacherId: null,
    status: 'scheduled'
  }));
  
  roomConflicts.value = {};
  for (const room of props.rooms) {
    const conflicts = await unifiedApi.checkRoomConflicts(room.id, sessions);
    if (conflicts.length > 0) {
      roomConflicts.value[room.id] = conflicts;
    }
  }
};

const createClass = async () => {
  if (creating.value) return;
  
  creating.value = true;
  try {
    if (newClass.value.patternType === 'no_repeat' && newClass.value.selectedDates && newClass.value.selectedDates.length > 0) {
      const sortedDates = [...newClass.value.selectedDates].sort();
      newClass.value.startDate = sortedDates[0];
      newClass.value.endDate = sortedDates[sortedDates.length - 1];
    }

    const fitnessClassData = {
      name: newClass.value.name,
      description: newClass.value.description,
      difficultyLevel: newClass.value.difficulty,
      status: 'draft'
    };
    const fitnessClassRes = await api.post('/fitness_class', fitnessClassData);
    const fitnessClassId = fitnessClassRes.data.id;

    let daysOfWeekStr = '';
    if (newClass.value.patternType === 'no_repeat') {
      if (newClass.value.selectedDates && newClass.value.selectedDates.length > 0) {
        const firstDate = new Date(newClass.value.selectedDates[0]);
        const dayNames = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];
        daysOfWeekStr = dayNames[firstDate.getDay()];
      }
    } else {
      const dayNames = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];
      daysOfWeekStr = newClass.value.daysOfWeek
        .map(dayIndex => dayNames[dayIndex])
        .join(',');
    }

    const schedulePatternData = {
      daysOfWeek: daysOfWeekStr,
      timeStart: newClass.value.startTime + ':00',
      timeEnd: newClass.value.endTime + ':00',
      classStartDate: newClass.value.startDate,
      classEndDate: newClass.value.endDate
    };
    const patternRes = await api.post('/schedule-patterns', schedulePatternData);
    const patternId = patternRes.data.id;

    const availableRoomsRes = await api.post('/room/available-for-pattern', schedulePatternData);
    const availableRooms = availableRoomsRes.data;
    
    if (availableRooms.length === 0) {
      throw new Error('Không có phòng nào trống trong khoảng thời gian này!');
    }

    const selectedRoom = availableRooms.find(r => r.id === newClass.value.roomId);
    if (!selectedRoom) {
      throw new Error(`Phòng "${getRoomName(newClass.value.roomId)}" đã bị trùng lịch! Vui lòng chọn phòng khác.`);
    }

    const generatePayload = {
      fitnessClass: { id: fitnessClassId },
      schedulePattern: { id: patternId },
      room: { id: newClass.value.roomId },
      capacity: newClass.value.maxStudents
    };

    let generatedSchedules = [];
    
    if (newClass.value.patternType === 'no_repeat' && newClass.value.selectedDates) {
      for (const dateStr of newClass.value.selectedDates) {
        const date = new Date(dateStr);
        const [startHour, startMin] = newClass.value.startTime.split(':').map(Number);
        const [endHour, endMin] = newClass.value.endTime.split(':').map(Number);
        
        const startDateTime = new Date(date);
        startDateTime.setHours(startHour, startMin, 0, 0);
        const endDateTime = new Date(date);
        endDateTime.setHours(endHour, endMin, 0, 0);

        const startTimeISO = formatLocalDateTime(startDateTime);
        const endTimeISO = formatLocalDateTime(endDateTime);

        const singleSchedulePayload = {
          fitnessClass: { id: fitnessClassId },
          schedulePattern: { id: patternId },
          room: { id: newClass.value.roomId },
          capacity: newClass.value.maxStudents,
          startTime: startTimeISO,
          endTime: endTimeISO,
          status: 'OPEN'
        };

        try {
          const scheduleRes = await api.post('/classschedule', singleSchedulePayload);
          generatedSchedules.push(scheduleRes.data);
        } catch (err) {
          throw new Error(`Lỗi khi tạo lịch cho ngày ${formatDate(dateStr)}: ${err.response?.data?.message || err.message}`);
        }
      }
    } else {
      const generateRes = await api.post('/classschedule/generate', generatePayload);
      generatedSchedules = generateRes.data;
    }

    toast.success(`Tạo lớp học thành công! Đã tạo ${generatedSchedules.length} buổi học.`);
    
    currentStep.value = 0;
    newClass.value = getEmptyClass();
    previewSessions.value = [];
    roomConflicts.value = {};
    
    emit('created');
    emit('close');
  } catch (error) {
    toast.error(error.message || 'Có lỗi xảy ra khi tạo lớp học');
  } finally {
    creating.value = false;
  }
};

// Watch for modal visibility
watch(() => props.show, (newVal) => {
  if (newVal) {
    currentStep.value = 0;
    newClass.value = getEmptyClass();
    previewSessions.value = [];
    roomConflicts.value = {};
  }
});

watch(() => newClass.value.startDate, updatePreview);
watch(() => newClass.value.endDate, updatePreview);
watch(() => newClass.value.startTime, updatePreview);
watch(() => newClass.value.endTime, updatePreview);
watch(() => newClass.value.daysOfWeek, updatePreview);
watch(() => newClass.value.selectedDates, updatePreview);
</script>
