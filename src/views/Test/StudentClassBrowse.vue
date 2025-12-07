<template>
  <div class="p-6">
    <!-- Header -->
    <div class="flex justify-between items-center mb-8">
      <div class="flex items-center gap-3">
        <Dumbbell class="w-10 h-10 text-blue-600" />
        <h1 class="text-3xl font-bold bg-gradient-to-r from-blue-600 to-blue-700 bg-clip-text text-transparent">
          Đăng Ký Lớp Học
        </h1>
      </div>
      <div 
        v-if="currentStudent && currentStudent.membershipTier" 
        class="px-6 py-3 rounded-full font-bold text-white flex items-center gap-2"
        :class="{
          'bg-gradient-to-r from-yellow-400 to-orange-500': currentStudent.membershipTier === 'VIP',
          'bg-gradient-to-r from-blue-400 to-blue-500': currentStudent.membershipTier === 'PREMIUM',
          'bg-gradient-to-r from-gray-300 to-gray-400 text-gray-800': currentStudent.membershipTier === 'BASIC'
        }"
      >
        <Crown v-if="currentStudent.membershipTier === 'VIP'" class="w-5 h-5" />
        {{ currentStudent.membershipTier }}
      </div>
    </div>

    <!-- View Mode Toggle -->
    <div class="flex justify-center mb-8">
      <div class="bg-gray-100 p-1 rounded-xl inline-flex">
        <button 
          @click="viewMode = 'list'"
          class="px-6 py-2 rounded-lg font-bold transition-all flex items-center gap-2"
          :class="viewMode === 'list' 
            ? 'bg-white text-blue-600 shadow-sm' 
            : 'text-gray-500 hover:text-gray-700'"
        >
          <LayoutGrid class="w-5 h-5" />
          Danh sách lớp
        </button>
        <button 
          @click="viewMode = 'calendar'"
          class="px-6 py-2 rounded-lg font-bold transition-all flex items-center gap-2"
          :class="viewMode === 'calendar' 
            ? 'bg-white text-blue-600 shadow-sm' 
            : 'text-gray-500 hover:text-gray-700'"
        >
          <CalendarIcon class="w-5 h-5" />
          Lịch học của tôi
        </button>
      </div>
    </div>

    <!-- Calendar View -->
    <div v-if="viewMode === 'calendar'">
      <schedule-calendar role="student" :user-id="currentStudentId || 0" />
    </div>

    <!-- Class List View -->
    <div v-else>
      <!-- Filters -->
      <div class="flex gap-4 mb-6">
        <div class="relative">
          <Filter class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
          <select 
            v-model="filterDifficulty"
            class="pl-10 pr-4 py-3 border-2 border-gray-200 rounded-xl focus:border-blue-600 focus:ring-2 focus:ring-blue-100 outline-none transition-all appearance-none bg-white"
          >
            <option value="">Tất cả độ khó</option>
            <option value="Beginner">Beginner</option>
            <option value="Intermediate">Intermediate</option>
            <option value="Advanced">Advanced</option>
          </select>
        </div>
        
        <div class="relative flex-1">
          <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
          <input 
            v-model="searchQuery" 
            type="text" 
            placeholder="Tìm kiếm lớp học..." 
            class="w-full pl-10 pr-4 py-3 border-2 border-gray-200 rounded-xl focus:border-blue-600 focus:ring-2 focus:ring-blue-100 outline-none transition-all"
          />
        </div>
      </div>

    <!-- Available Classes -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
      <div 
        v-for="cls in filteredClasses" 
        :key="cls.id" 
        class="bg-white rounded-2xl shadow-md hover:shadow-xl transition-all duration-300 hover:-translate-y-1 overflow-hidden"
      >
        <!-- Card Header -->
        <div class="p-5 bg-gradient-to-r from-blue-600 to-blue-700 text-white">
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
          
          <!-- Teacher Info -->
          <div class="flex items-center gap-3 p-3 bg-gray-50 rounded-xl">
            <div class="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center text-blue-600">
              <User class="w-6 h-6" />
            </div>
            <div>
              <div class="font-bold text-gray-800">{{ getTeacher(cls.teacherId).name }}</div>
              <div class="text-xs text-gray-500">{{ getTeacher(cls.teacherId).specialties.join(', ') }}</div>
            </div>
          </div>
          
          <div class="grid grid-cols-2 gap-3 text-sm">
            <div>
              <span class="text-gray-500 font-semibold block flex items-center gap-1"><Users class="w-4 h-4" /> Chỗ trống:</span>
              <span 
                class="font-bold"
                :class="getAvailableSlots(cls) < 5 ? 'text-blue-600' : 'text-gray-800'"
              >
                {{ getAvailableSlots(cls) }}/{{ cls.maxStudents }}
              </span>
            </div>
            <div>
              <span class="text-gray-500 font-semibold block flex items-center gap-1"><MapPin class="w-4 h-4" /> Phòng:</span>
              <span class="text-gray-800">{{ getRoomName(cls.roomId) }}</span>
            </div>
            <div>
              <span class="text-gray-500 font-semibold block flex items-center gap-1"><Clock class="w-4 h-4" /> Thời gian:</span>
              <span class="text-gray-800">{{ cls.startTime }} - {{ cls.endTime }}</span>
            </div>
            <div>
              <span class="text-gray-500 font-semibold block flex items-center gap-1"><Calendar class="w-4 h-4" /> Lịch:</span>
              <div class="flex items-center gap-2">
                <span class="text-gray-800">{{ getScheduleText(cls) }}</span>
                <button 
                  @click.stop="viewSessions(cls)" 
                  class="text-blue-600 hover:text-blue-800 text-xs font-bold underline flex items-center gap-1"
                >
                  <Eye class="w-3 h-3" /> Chi tiết
                </button>
              </div>
            </div>
            <div>
              <span class="text-gray-500 font-semibold block flex items-center gap-1"><Calendar class="w-4 h-4" /> Bắt đầu:</span>
              <span class="text-gray-800">{{ formatDate(cls.startDate) }}</span>
            </div>
            <div>
              <span class="text-gray-500 font-semibold block flex items-center gap-1"><Calendar class="w-4 h-4" /> Kết thúc:</span>
              <span class="text-gray-800">{{ formatDate(cls.endDate) }}</span>
            </div>
          </div>

          <!-- VIP Early Registration Notice -->
          <div v-if="isVIPEarlyAccess(cls)" class="p-3 bg-gradient-to-r from-yellow-50 to-orange-50 border-2 border-yellow-400 rounded-xl text-orange-700 font-semibold text-sm flex items-start gap-2">
            <Crown class="w-5 h-5 flex-shrink-0 mt-0.5" />
            <div>
              <div class="font-bold mb-1">Đăng ký sớm VIP (còn {{ getWeeksUntilStart(cls.startDate) }} tuần)</div>
              <div class="text-xs font-normal">Vui lòng ra quầy Lễ tân để đăng ký sớm cho lớp này</div>
            </div>
          </div>

          <!-- Registration Status -->
          <div v-if="isRegistered(cls.id)" class="p-3 bg-green-50 border-2 border-green-400 rounded-xl text-green-700 font-semibold text-sm text-center flex items-center justify-center gap-2">
            <CheckCircle class="w-4 h-4" />
            Bạn đã đăng ký lớp này
          </div>
        </div>
        
        <!-- Card Footer -->
        <div class="p-4 bg-gray-50 border-t">
          <!-- Cancel Button if Already Registered -->
          <button 
            v-if="isRegistered(cls.id)"
            @click="cancelRegistration(cls)" 
            class="w-full px-4 py-3 bg-gradient-to-r from-red-500 to-red-600 text-white rounded-xl font-semibold hover:shadow-lg transition-all flex items-center justify-center gap-2"
          >
            <XCircle class="w-5 h-5" />
            Hủy đăng ký
          </button>
          
          <!-- VIP Early Access - Go to Reception -->
          <button 
            v-else-if="isVIPEarlyAccess(cls)"
            disabled
            class="w-full px-4 py-3 bg-gradient-to-r from-yellow-400 to-orange-500 text-white rounded-xl font-semibold opacity-75 cursor-not-allowed flex items-center justify-center gap-2"
          >
            <Crown class="w-5 h-5" />
            Ra quầy Lễ tân để đăng ký
          </button>
          
          <!-- Register Button if Not VIP Early Access -->
          <button 
            v-else
            @click="openScheduleSelection(cls)" 
            :disabled="getAvailableSlots(cls) <= 0"
            class="w-full px-4 py-3 bg-gradient-to-r from-blue-600 to-blue-700 text-white rounded-xl font-semibold hover:shadow-lg transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
          >
            <component 
              :is="getRegisterButtonIcon(cls)" 
              class="w-5 h-5"
            />
            {{ getRegisterButtonText(cls) }}
          </button>
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-if="filteredClasses.length === 0" class="text-center py-16">
      <Inbox class="w-16 h-16 text-gray-400 mx-auto mb-4" />
      <p class="text-gray-500 text-lg">Không có lớp học nào khả dụng</p>
    </div>
  </div> 
  <!-- End of v-else for Class List View -->
  <!-- End of v-else for Class List View -->
  <!-- Sessions Detail Modal -->
    <transition
      enter-active-class="transition duration-200 ease-out"
      enter-from-class="opacity-0 scale-95"
      enter-to-class="opacity-100 scale-100"
      leave-active-class="transition duration-150 ease-in"
      leave-from-class="opacity-100 scale-100"
      leave-to-class="opacity-0 scale-95"
    >
      <div 
        v-if="showSessionsModal"
        class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50"
        @click.self="showSessionsModal = false"
      >
        <div class="bg-white rounded-2xl w-full max-w-2xl shadow-2xl overflow-hidden max-h-[80vh] flex flex-col">
          <!-- Modal Header -->
          <div class="p-6 bg-gradient-to-r from-blue-600 to-blue-700 text-white flex justify-between items-center shrink-0">
            <div>
              <h3 class="text-xl font-bold flex items-center gap-2">
                <CalendarIcon class="w-6 h-6" />
                Chi tiết lịch học
              </h3>
              <p class="text-blue-100 text-sm mt-1">{{ selectedClass?.name }}</p>
            </div>
            <button 
              @click="showSessionsModal = false"
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
                class="flex items-center gap-4 p-4 rounded-xl border border-gray-100 hover:border-blue-200 hover:bg-blue-50/50 transition-all"
              >
                <!-- Index -->
                <div class="w-8 h-8 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center font-bold text-sm shrink-0">
                  {{ idx + 1 }}
                </div>
                
                <!-- Date Info -->
                <div class="flex-1">
                  <div class="font-bold text-gray-800 flex items-center gap-2">
                    <CalendarIcon class="w-4 h-4 text-blue-500" />
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
              @click="showSessionsModal = false"
              class="px-6 py-2 bg-gray-200 text-gray-700 font-bold rounded-xl hover:bg-gray-300 transition-colors"
            >
              Đóng
            </button>
          </div>
        </div>
      </div>
    </transition>

    <!-- Schedule Selection Modal -->
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
        @click.self="closeScheduleModal"
      >
        <div class="bg-white rounded-2xl w-full max-w-4xl shadow-2xl overflow-hidden max-h-[90vh] flex flex-col">
          <!-- Modal Header -->
          <div class="p-6 bg-gradient-to-r from-blue-600 to-blue-700 text-white flex justify-between items-center shrink-0">
            <div>
              <h3 class="text-xl font-bold flex items-center gap-2">
                <CalendarIcon class="w-6 h-6" />
                Chọn lịch học
              </h3>
              <p class="text-blue-100 text-sm mt-1">{{ selectedFitnessClass?.name }}</p>
            </div>
            <button 
              @click="closeScheduleModal"
              class="text-white/80 hover:text-white transition-colors bg-white/10 hover:bg-white/20 p-2 rounded-lg"
            >
              <X class="w-6 h-6" />
            </button>
          </div>
          
          <!-- Modal Body -->
          <div class="p-6 overflow-y-auto flex-1">
            <div v-if="loadingSchedules" class="text-center py-8 text-gray-500">
              <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
              Đang tải lịch học...
            </div>
            
            <div v-else-if="availableSchedules.length === 0" class="text-center py-8 text-gray-500">
              <Inbox class="w-16 h-16 text-gray-400 mx-auto mb-4" />
              <p class="text-lg font-semibold">Không có lịch học khả dụng</p>
              <p class="text-sm mt-2">Tất cả các buổi học đã đầy hoặc đã diễn ra</p>
            </div>
            
            <div v-else>
              <div class="mb-4 p-3 bg-blue-50 border border-blue-200 rounded-lg">
                <p class="text-sm text-blue-800">
                  <strong>Hướng dẫn:</strong> Chọn các buổi học bạn muốn đăng ký bằng cách click vào từng dòng. 
                  Bạn có thể chọn nhiều buổi cùng lúc.
                </p>
              </div>
              
              <div class="overflow-x-auto">
                <table class="w-full border-collapse">
                  <thead>
                    <tr class="bg-gray-100 border-b-2 border-gray-300">
                      <th class="p-3 text-left font-bold text-gray-700 w-12">
                        <input 
                          ref="selectAllCheckbox"
                          type="checkbox" 
                          :checked="selectedScheduleIds.length === availableSchedules.filter(s => s.status === 'OPEN' && getScheduleAvailableSlots(s) > 0).length && availableSchedules.filter(s => s.status === 'OPEN' && getScheduleAvailableSlots(s) > 0).length > 0"
                          @change="toggleAllSchedules"
                          class="w-5 h-5 text-blue-600 rounded focus:ring-blue-500"
                        />
                      </th>
                      <th class="p-3 text-left font-bold text-gray-700">Ngày</th>
                      <th class="p-3 text-left font-bold text-gray-700">Thời gian</th>
                      <th class="p-3 text-left font-bold text-gray-700">Phòng</th>
                      <th class="p-3 text-left font-bold text-gray-700">Trạng thái</th>
                      <th class="p-3 text-left font-bold text-gray-700">Chỗ trống</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr 
                      v-for="schedule in availableSchedules" 
                      :key="schedule.id"
                      @click="toggleSchedule(schedule)"
                      class="border-b border-gray-200 hover:bg-blue-50 cursor-pointer transition-colors"
                      :class="{
                        'bg-blue-100': selectedScheduleIds.includes(schedule.id),
                        'opacity-50 cursor-not-allowed': schedule.status === 'CLOSED' || schedule.status === 'CANCELLED' || getScheduleAvailableSlots(schedule) <= 0
                      }"
                    >
                      <td class="p-3">
                        <input 
                          type="checkbox" 
                          :checked="selectedScheduleIds.includes(schedule.id)"
                          :disabled="schedule.status === 'CLOSED' || schedule.status === 'CANCELLED' || getScheduleAvailableSlots(schedule) <= 0"
                          @click.stop="toggleSchedule(schedule)"
                          class="w-5 h-5 text-blue-600 rounded focus:ring-blue-500"
                        />
                      </td>
                      <td class="p-3 font-medium text-gray-800">
                        {{ formatScheduleDate(schedule.startTime || schedule.date) }}
                      </td>
                      <td class="p-3 text-gray-600">
                        <span class="font-medium">{{ formatScheduleTime(schedule.startTime) }}</span>
                        <span class="mx-1">-</span>
                        <span class="font-medium">{{ formatScheduleTime(schedule.endTime) }}</span>
                      </td>
                      <td class="p-3 text-gray-600">
                        {{ getRoomName(schedule.roomId) }}
                      </td>
                      <td class="p-3">
                        <span 
                          class="px-2 py-1 rounded text-xs font-semibold"
                          :class="{
                            'bg-green-100 text-green-700': schedule.status === 'OPEN',
                            'bg-red-100 text-red-700': schedule.status === 'CLOSED' || schedule.status === 'CANCELLED',
                            'bg-gray-100 text-gray-700': !schedule.status
                          }"
                        >
                          {{ schedule.status === 'OPEN' ? 'Mở' : schedule.status === 'CLOSED' ? 'Đóng' : schedule.status === 'CANCELLED' ? 'Hủy' : 'N/A' }}
                        </span>
                      </td>
                      <td class="p-3">
                        <span 
                          class="font-bold"
                          :class="{
                            'text-green-600': getScheduleAvailableSlots(schedule) > 5,
                            'text-orange-600': getScheduleAvailableSlots(schedule) > 0 && getScheduleAvailableSlots(schedule) <= 5,
                            'text-red-600': getScheduleAvailableSlots(schedule) <= 0
                          }"
                        >
                          {{ getScheduleAvailableSlots(schedule) }}/{{ schedule.capacity || 20 }}
                        </span>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
              
              <div v-if="selectedScheduleIds.length > 0" class="mt-4 p-4 bg-green-50 border-2 border-green-200 rounded-lg">
                <p class="text-green-800 font-semibold">
                  Đã chọn <strong>{{ selectedScheduleIds.length }}</strong> buổi học
                </p>
              </div>
            </div>
          </div>
          
          <!-- Modal Footer -->
          <div class="p-4 border-t bg-gray-50 flex justify-end gap-3 shrink-0">
            <button 
              @click="closeScheduleModal"
              class="px-6 py-2 bg-gray-200 text-gray-700 font-bold rounded-xl hover:bg-gray-300 transition-colors"
            >
              Hủy
            </button>
            <button 
              @click="registerSelectedSchedules"
              :disabled="selectedScheduleIds.length === 0 || loadingSchedules"
              class="px-6 py-2 bg-gradient-to-r from-blue-600 to-blue-700 text-white font-bold rounded-xl hover:shadow-lg transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
            >
              <CheckCircle class="w-5 h-5" />
              Đăng ký ({{ selectedScheduleIds.length }})
            </button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useAuthStore } from '@/stores/useAuthStore';
import unifiedApi from './unifiedApi.js';
import apiService from './apiService.js';
import { formatDate, getWeeksUntilStart, canVIPRegister, canAllRegister } from './dateUtils.js';
import ScheduleCalendar from './ScheduleCalendar.vue';

const authStore = useAuthStore();
const USE_REAL_API = true; // Set to false to use mock data
import { 
  Dumbbell, 
  Search, 
  Filter, 
  Users, 
  MapPin, 
  Clock, 
  Calendar as CalendarIcon, 
  User, 
  Crown, 
  CheckCircle, 
  XCircle, 
  Hand,
  LayoutGrid,
  Eye,
  X,
  Inbox
} from 'lucide-vue-next';

const classes = ref([]);
const roomsData = ref([]);
const teachersData = ref([]);
const registrations = ref([]);
const filterDifficulty = ref('');
const searchQuery = ref('');
const currentStudentId = computed(() => {
  const user = authStore.user;
  return user?.id || null;
});
const viewMode = ref('list'); // 'list' | 'calendar'
const currentStudent = ref({ membershipTier: 'BASIC' });

// Sessions Modal state
const showSessionsModal = ref(false);
const selectedClassSessions = ref([]);
const selectedClass = ref(null);

// Schedule Selection Modal state
const showScheduleModal = ref(false);
const selectedFitnessClass = ref(null);
const availableSchedules = ref([]);
const selectedScheduleIds = ref([]);
const loadingSchedules = ref(false);
const selectAllCheckbox = ref(null);

const viewSessions = async (cls) => {
  selectedClass.value = cls;
  try {
    const sessions = await unifiedApi.getSessions(cls.id);
    // Enrich with room names if needed, though they are usually same room
    selectedClassSessions.value = sessions.sort((a, b) => new Date(a.date) - new Date(b.date));
    showSessionsModal.value = true;
  } catch (error) {
    alert('Không thể tải lịch học: ' + error.message);
  }
};

const getSessionStatus = (session) => {
  // Check session status first
  if (session.status === 'CANCELLED') {
    return { text: 'Đã hủy', class: 'bg-red-100 text-red-700' };
  }
  if (session.status === 'TEACHER_ABSENT') {
    return { text: 'Giáo viên vắng', class: 'bg-orange-100 text-orange-700' };
  }
  
  // Then check by date
  const now = new Date();
  const sessionDate = new Date(session.date);
  // Reset hours for pure date comparison
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
  const sDate = new Date(sessionDate.getFullYear(), sessionDate.getMonth(), sessionDate.getDate());

  if (sDate < today) {
    return { text: 'Đã diễn ra', class: 'bg-gray-100 text-gray-500' };
  } else if (sDate.getTime() === today.getTime()) {
    // Check time if needed, for simplicity assume 'Today' is Active/Upcoming
    return { text: 'Hôm nay', class: 'bg-blue-100 text-blue-700' };
  } else {
    return { text: 'Sắp diễn ra', class: 'bg-green-100 text-green-700' };
  }
};

const availableClasses = computed(() => {
  return classes.value.filter(c => c.status === 'ready_for_students');
});

const filteredClasses = computed(() => {
  let result = availableClasses.value;
  
  if (filterDifficulty.value) {
    result = result.filter(c => c.difficulty === filterDifficulty.value);
  }
  
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(c => 
      c.name.toLowerCase().includes(query) ||
      c.description.toLowerCase().includes(query)
    );
  }
  
  return result;
});

const loadData = async () => {
  if (!currentStudentId.value) {
    alert('Vui lòng đăng nhập!');
    return;
  }
  
  classes.value = await unifiedApi.getClasses();
  roomsData.value = await unifiedApi.getRooms();
  teachersData.value = await unifiedApi.getTeachers();
  registrations.value = await unifiedApi.getStudentRegistrations(currentStudentId.value);
  
  const allStudents = await unifiedApi.getStudents();
  currentStudent.value = allStudents.find(s => s.id === currentStudentId.value) || { membershipTier: 'BASIC' };
  
  // Update available slots for all classes
  for (const cls of classes.value) {
    await updateAvailableSlots(cls);
  }
};

onMounted(() => {
  loadData();
});

const getRoomName = (roomId) => {
  const room = roomsData.value.find(r => r.id === roomId);
  return room ? room.name : 'Không xác định';
};

const getTeacher = (teacherId) => {
  return teachersData.value.find(t => t.id === teacherId) || { name: 'Chưa có', avatar: '', specialties: [] };
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

const availableSlotsCache = ref({});

const getAvailableSlots = (cls) => {
  // Return cached value or default
  return availableSlotsCache.value[cls.id] ?? cls.maxStudents;
};

const updateAvailableSlots = async (cls) => {
  try {
    const classSchedules = await unifiedApi.getSessions(cls.id);
    if (classSchedules.length === 0) {
      availableSlotsCache.value[cls.id] = cls.maxStudents;
      return;
    }
    
    // Get all registrations for all schedules of this class
    // We need to count registrations per schedule and sum them
    // For simplicity, use the first schedule's capacity
    const firstSchedule = classSchedules[0];
    const capacity = firstSchedule.capacity || cls.maxStudents;
    
    // Count total registrations across all schedules
    // In real app, we'd query by schedule IDs, but for now estimate
    const enrolled = registrations.value.filter(r => r.classId === cls.id).length;
    availableSlotsCache.value[cls.id] = Math.max(0, capacity - enrolled);
  } catch (error) {
    availableSlotsCache.value[cls.id] = cls.maxStudents;
  }
};

const isVIPEarlyAccess = (cls) => {
  return canVIPRegister(cls.startDate) && !canAllRegister(cls.startDate);
};

const isRegistered = (classId) => {
  return registrations.value.some(r => r.classId === classId && r.status === 'active');
};

const canRegister = async (cls) => {
  if (isRegistered(cls.id)) return false;
  const slots = getAvailableSlots(cls);
  if (slots <= 0) return false;
  
  if (isVIPEarlyAccess(cls)) {
    return currentStudent.value.membershipTier === 'VIP';
  }
  
  return canAllRegister(cls.startDate);
};

const getRegisterButtonText = (cls) => {
  if (isRegistered(cls.id)) return 'Đã đăng ký';
  if (getAvailableSlots(cls) <= 0) return 'Đã đầy';
  if (isVIPEarlyAccess(cls) && currentStudent.value.membershipTier !== 'VIP') {
    return 'Chỉ VIP';
  }
  return 'Đăng ký';
};

const getRegisterButtonIcon = (cls) => {
  if (isRegistered(cls.id)) return CheckCircle;
  if (getAvailableSlots(cls) <= 0) return XCircle;
  if (isVIPEarlyAccess(cls) && currentStudent.value.membershipTier !== 'VIP') {
    return Crown;
  }
  return Hand;
};

const openScheduleSelection = async (cls) => {
  selectedFitnessClass.value = cls;
  selectedScheduleIds.value = [];
  loadingSchedules.value = true;
  showScheduleModal.value = true;
  
  try {
    // Get all schedules for this fitness class
    const schedules = await unifiedApi.getSessions(cls.id);
    availableSchedules.value = schedules;
    
    // Filter out past schedules and full schedules
    const now = new Date();
    const filteredSchedules = [];
    
    for (const s of schedules) {
      // Handle both date string and ISO datetime
      let scheduleDate;
      if (s.startTime && typeof s.startTime === 'string' && s.startTime.includes('T')) {
        scheduleDate = new Date(s.startTime);
      } else if (s.date) {
        scheduleDate = new Date(s.date);
      } else {
        continue;
      }
      
      const isFuture = scheduleDate > now;
      const isOpen = s.status === 'OPEN' || s.status === 'scheduled';
      
      if (isFuture && isOpen) {
        // Update slots cache for this schedule
        await updateScheduleSlots(s);
        const hasSlots = getScheduleAvailableSlots(s) > 0;
        if (hasSlots) {
          filteredSchedules.push(s);
        }
      }
    }
    
    availableSchedules.value = filteredSchedules.sort((a, b) => {
      // Sort by date and time
      const dateA = new Date(a.startTime || a.date);
      const dateB = new Date(b.startTime || b.date);
      return dateA - dateB;
    });
  } catch (error) {
    alert('❌ Lỗi khi tải lịch học: ' + error.message);
    closeScheduleModal();
  } finally {
    loadingSchedules.value = false;
  }
};

const closeScheduleModal = () => {
  showScheduleModal.value = false;
  selectedFitnessClass.value = null;
  availableSchedules.value = [];
  selectedScheduleIds.value = [];
};

const toggleSchedule = (schedule) => {
  if (schedule.status === 'CLOSED' || schedule.status === 'CANCELLED' || getScheduleAvailableSlots(schedule) <= 0) {
    return;
  }
  
  const index = selectedScheduleIds.value.indexOf(schedule.id);
  if (index > -1) {
    selectedScheduleIds.value.splice(index, 1);
  } else {
    selectedScheduleIds.value.push(schedule.id);
  }
};

const toggleAllSchedules = (event) => {
  if (event.target.checked) {
    // Select all available schedules
    selectedScheduleIds.value = availableSchedules.value
      .filter(s => s.status === 'OPEN' && getScheduleAvailableSlots(s) > 0)
      .map(s => s.id);
  } else {
    // Deselect all
    selectedScheduleIds.value = [];
  }
};

// Watch for changes to update indeterminate state
watch([selectedScheduleIds, availableSchedules], () => {
  if (selectAllCheckbox.value) {
    const availableCount = availableSchedules.value.filter(s => s.status === 'OPEN' && getScheduleAvailableSlots(s) > 0).length;
    const selectedCount = selectedScheduleIds.value.length;
    selectAllCheckbox.value.indeterminate = selectedCount > 0 && selectedCount < availableCount;
  }
}, { immediate: true });

const scheduleSlotsCache = ref({});

const getScheduleAvailableSlots = (schedule) => {
  // Return cached value or use capacity as fallback
  return scheduleSlotsCache.value[schedule.id] ?? (schedule.capacity || 20);
};

const updateScheduleSlots = async (schedule) => {
  try {
    if (USE_REAL_API) {
      const registrations = await apiService.memberRegistration.getBySchedule(schedule.id);
      const capacity = schedule.capacity || 20;
      scheduleSlotsCache.value[schedule.id] = Math.max(0, capacity - registrations.length);
    } else {
      // For mock data, we'd need to check memberRegistrationsData
      // This is a simplified version
      scheduleSlotsCache.value[schedule.id] = schedule.capacity || 20;
    }
  } catch (e) {
    scheduleSlotsCache.value[schedule.id] = schedule.capacity || 20;
  }
};

const formatScheduleDate = (dateTime) => {
  if (!dateTime) return '';
  try {
    const date = new Date(dateTime);
    if (isNaN(date.getTime())) return '';
    return formatDate(date.toISOString().split('T')[0]);
  } catch (e) {
    return '';
  }
};

const formatScheduleTime = (dateTime) => {
  if (!dateTime) return '';
  try {
    const date = new Date(dateTime);
    if (isNaN(date.getTime())) return '';
    return date.toTimeString().substring(0, 5);
  } catch (e) {
    // If it's already a time string like "18:00"
    if (typeof dateTime === 'string' && dateTime.match(/^\d{2}:\d{2}$/)) {
      return dateTime;
    }
    return '';
  }
};

const registerSelectedSchedules = async () => {
  if (!currentStudentId.value) {
    alert('❌ Vui lòng đăng nhập!');
    return;
  }
  
  if (selectedScheduleIds.value.length === 0) {
    alert('Vui lòng chọn ít nhất một buổi học!');
    return;
  }
  
  if (confirm(`Bạn có chắc muốn đăng ký ${selectedScheduleIds.value.length} buổi học cho lớp "${selectedFitnessClass.value.name}"?`)) {
    try {
      // Use bulk register API
      await unifiedApi.registerBulkSchedules(currentStudentId.value, selectedScheduleIds.value);
      alert(`✅ Đã đăng ký thành công ${selectedScheduleIds.value.length} buổi học!`);
      closeScheduleModal();
      await loadData();
    } catch (error) {
      alert('❌ Lỗi: ' + (error.response?.data?.message || error.message));
    }
  }
};

const register = async (cls) => {
  // This is now replaced by openScheduleSelection
  await openScheduleSelection(cls);
};

const cancelRegistration = async (cls) => {
  if (!currentStudentId.value) {
    alert('❌ Vui lòng đăng nhập!');
    return;
  }
  
  if (confirm(`Bạn có chắc muốn hủy đăng ký lớp "${cls.name}"?`)) {
    try {
      await unifiedApi.cancelRegistration(cls.id, currentStudentId.value);
      alert('✅ Đã hủy đăng ký!');
      await loadData();
    } catch (error) {
      alert('❌ Lỗi: ' + (error.response?.data?.message || error.message));
    }
  }
};
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
