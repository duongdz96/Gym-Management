<template>
  <div class="p-3 sm:p-6">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-6 sm:mb-8 gap-3">
      <div class="flex items-center gap-2 sm:gap-3">
        <Dumbbell class="w-8 h-8 sm:w-10 sm:h-10 text-emerald-600" />
        <h1 class="text-2xl sm:text-3xl font-bold bg-gradient-to-r from-emerald-600 to-teal-600 bg-clip-text text-transparent">
          Đăng Ký Lớp Học
        </h1>
      </div>
      <div 
        v-if="currentStudent && currentStudent.membershipTier" 
        class="px-4 py-2 sm:px-6 sm:py-3 rounded-full font-bold text-sm sm:text-base text-white flex items-center gap-2"
        :class="{
          'bg-gradient-to-r from-yellow-400 to-orange-500': currentStudent.membershipTier === 'VIP',
          'bg-gradient-to-r from-emerald-400 to-teal-500': currentStudent.membershipTier === 'PREMIUM',
          'bg-gradient-to-r from-gray-300 to-gray-400 text-gray-800': currentStudent.membershipTier === 'BASIC'
        }"
      >
        <Crown v-if="currentStudent.membershipTier === 'VIP'" class="w-5 h-5" />
        {{ currentStudent.membershipTier }}
      </div>
    </div>

    <!-- Filters -->
    <div class="flex flex-col sm:flex-row gap-3 sm:gap-4 mb-4 sm:mb-6">
      <div class="relative flex-1 sm:flex-none">
        <Filter class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 sm:w-5 sm:h-5 text-gray-400" />
        <select 
          v-model="filterDifficulty"
          class="w-full pl-9 sm:pl-10 pr-4 py-2 sm:py-3 text-sm sm:text-base border-2 border-gray-200 rounded-xl focus:border-emerald-600 focus:ring-2 focus:ring-emerald-100 outline-none transition-all appearance-none bg-white"
        >
          <option value="">Tất cả độ khó</option>
          <option value="Beginner">Beginner</option>
          <option value="Intermediate">Intermediate</option>
          <option value="Advanced">Advanced</option>
        </select>
      </div>
      
      <div class="relative flex-1 sm:flex-none">
        <Filter class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 sm:w-5 sm:h-5 text-gray-400" />
        <select 
          v-model="filterRegistrationStatus"
          class="w-full pl-9 sm:pl-10 pr-4 py-2 sm:py-3 text-sm sm:text-base border-2 border-gray-200 rounded-xl focus:border-red-600 focus:ring-2 focus:ring-red-100 outline-none transition-all appearance-none bg-white"
        >
          <option value="">Tất cả lớp</option>
          <option value="registered">Đã đăng ký</option>
          <option value="not_registered">Chưa đăng ký</option>
        </select>
      </div>
      
      <div class="relative flex-1">
        <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 sm:w-5 sm:h-5 text-gray-400" />
        <input 
          v-model="searchQuery" 
          type="text" 
          placeholder="Tìm kiếm lớp học..." 
          class="w-full pl-9 sm:pl-10 pr-4 py-2 sm:py-3 text-sm sm:text-base border-2 border-gray-200 rounded-xl focus:border-emerald-600 focus:ring-2 focus:ring-emerald-100 outline-none transition-all"
        />
      </div>
    </div>

    <!-- Available Classes -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 sm:gap-5">
      <div 
        v-for="cls in filteredClasses" 
        :key="cls.id" 
        class="bg-white rounded-2xl shadow-md hover:shadow-xl transition-all duration-300 hover:-translate-y-1 overflow-hidden"
      >
        <!-- Card Header -->
        <div class="p-4 sm:p-5 bg-gradient-to-r from-emerald-600 to-teal-600 text-white">
          <div class="flex justify-between items-start gap-2">
            <h3 class="text-lg sm:text-xl font-bold trun">{{ cls.name }}</h3>
            <span 
              class="px-2 py-1 sm:px-3 rounded-full text-xs font-semibold flex-shrink-0"
              :class="{
                'bg-green-500': cls.difficulty === 'Beginner',
                'bg-orange-500': cls.difficulty === 'Intermediate',
                'bg-purple-500': cls.difficulty === 'Advanced'
              }"
            >
              {{ cls.difficulty }}
            </span>
          </div>
        </div>
        
        <!-- Card Body -->
        <div class="p-4 sm:p-5 space-y-3 sm:space-y-4">
          <p class="text-sm sm:text-base text-gray-600 line-clamp-2">{{ cls.description }}</p>
          
          <!-- Teacher Info -->
          <div class="flex items-center gap-2 sm:gap-3 p-2 sm:p-3 bg-gray-50 rounded-xl">
  <div class="w-8 h-8 sm:w-10 sm:h-10 rounded-full bg-emerald-100 flex items-center justify-center text-emerald-600 flex-shrink-0">
    <User class="w-5 h-5 sm:w-6 sm:h-6" />
  </div>
  <div class="min-w-0">
    <div class="font-bold text-sm sm:text-base text-gray-800 truncate">{{ getTeacher(cls.teacherId).name }}</div>
    <div class="text-xs text-gray-500 truncate">{{ getTeacher(cls.teacherId).specialties.join(', ') }}</div>
  </div>
</div>

<div class="grid grid-cols-2 gap-4 text-sm mt-4">
  <div class="space-y-1">
    <span class="text-gray-500 font-medium flex items-center gap-1.5 text-xs">
      <MapPin class="w-3.5 h-3.5" /> PHÒNG
    </span>
    <div class="text-gray-800 font-bold bg-gray-100 px-2.5 py-1.5 rounded-lg inline-block">
      {{ getRoomName(cls.roomId) }}
    </div>
  </div>

  <div class="space-y-1">
    <span class="text-gray-500 font-medium flex items-center gap-1.5 text-xs">
      <Clock class="w-3.5 h-3.5" /> GIỜ HỌC
    </span>
    <div class="text-emerald-700 font-bold bg-emerald-50 px-2.5 py-1.5 rounded-lg inline-block border border-emerald-100">
      {{ cls.startTime }} - {{ cls.endTime }}
    </div>
  </div>

  <div class="col-span-2 space-y-1">
    <span class="text-gray-500 font-medium flex items-center gap-1.5 text-xs">
      <Calendar class="w-3.5 h-3.5" /> LỊCH TRÌNH
    </span>
    <div class="flex items-center gap-3 bg-gray-50 p-2 rounded-xl border border-dashed border-gray-300">
      <span class="text-gray-800 font-bold ml-1">{{ getScheduleText(cls) }}</span>
      <button 
        @click.stop="viewSessions(cls)" 
        class="ml-auto bg-white hover:bg-emerald-50 text-emerald-600 border border-emerald-200 px-3 py-1 rounded-full text-xs font-bold transition-colors flex items-center gap-1"
      >
        <Eye class="w-3 h-3" /> Chi tiết buổi học
      </button>
    </div>
  </div>

  <div class="col-span-2 grid grid-cols-2 gap-2 mt-2">
    <div class="flex flex-col p-2 bg-orange-50 rounded-lg border border-orange-100">
      <span class="text-[10px] text-orange-600 font-bold uppercase">Ngày bắt đầu</span>
      <span class="text-gray-800 font-bold">{{ formatDate(cls.startDate) }}</span>
    </div>
    <div class="flex flex-col p-2 bg-blue-50 rounded-lg border border-blue-100">
      <span class="text-[10px] text-blue-600 font-bold uppercase">Ngày kết thúc</span>
      <span class="text-gray-800 font-bold">{{ formatDate(cls.endDate) }}</span>
    </div>
  </div>
</div>

          <!-- VIP Early Registration Notice -->
          <div v-if="isVIPEarlyAccess(cls)" class="p-3 bg-gradient-to-r from-amber-50 to-yellow-50 border-2 border-amber-400 rounded-xl text-amber-700 font-semibold text-sm flex items-start gap-2">
            <Crown class="w-5 h-5 flex-shrink-0 mt-0.5" />
            <div>
              <div class="font-bold mb-1">Đăng ký sớm VIP (còn {{ getWeeksUntilStart(cls.startDate) }} tuần)</div>
              <div class="text-xs font-normal" v-if="currentStudent.membershipTier === 'VIP'">Bạn có thể đăng ký sớm cho lớp này</div>
              <div class="text-xs font-normal" v-else>Chỉ thành viên VIP mới có thể đăng ký sớm</div>
            </div>
          </div>

          <!-- Registration Status -->
          <div v-if="isRegistered(cls.id)" class="p-3 bg-green-50 border-2 border-green-400 rounded-xl text-green-700 font-semibold text-sm text-center flex items-center justify-center gap-2">
            <CheckCircle class="w-4 h-4" />
            Bạn đã đăng ký lớp này
          </div>
        </div>
        
        <!-- Card Footer -->
        <div class="p-3 sm:p-4 bg-gray-50 border-t">
          <!-- Cancel Button if Already Registered -->
          <button 
            v-if="isRegistered(cls.id)"
            @click="cancelRegistration(cls)" 
            class="w-full px-3 py-2 sm:px-4 sm:py-3 text-sm sm:text-base bg-gradient-to-r from-rose-500 to-red-600 text-white rounded-xl font-semibold hover:shadow-lg transition-all flex items-center justify-center gap-2"
          >
            <XCircle class="w-4 h-4 sm:w-5 sm:h-5" />
            Hủy đăng ký
          </button>
          
          <!-- Register Button -->
          <button 
            v-else
            @click="openScheduleSelection(cls)" 
            :disabled="!canRegister(cls)"
            class="w-full px-3 py-2 sm:px-4 sm:py-3 text-sm sm:text-base bg-gradient-to-r from-emerald-600 to-teal-600 text-white rounded-xl font-semibold hover:shadow-lg transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
            :class="{
              'from-yellow-400 to-orange-500': isVIPEarlyAccess(cls) && currentStudent.membershipTier === 'VIP'
            }"
          >
            <component 
              :is="getRegisterButtonIcon(cls)" 
              class="w-4 h-4 sm:w-5 sm:h-5"
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
        class="fixed inset-0 z-50 flex items-center justify-center p-3 sm:p-4 bg-black/50"
        @click.self="showSessionsModal = false"
      >
        <div class="bg-white rounded-xl sm:rounded-2xl w-full max-w-2xl shadow-2xl overflow-hidden max-h-[80vh] flex flex-col">
          <!-- Modal Header -->
          <div class="p-4 sm:p-6 bg-gradient-to-r from-emerald-600 to-teal-600 text-white flex justify-between items-center shrink-0">
            <div>
              <h3 class="text-lg sm:text-xl font-bold flex items-center gap-2">
                <CalendarIcon class="w-5 h-5 sm:w-6 sm:h-6" />
                Chi tiết lịch học
              </h3>
              <p class="text-emerald-100 text-xs sm:text-sm mt-1 truncate">{{ selectedClass?.name }}</p>
            </div>
            <button 
              @click="showSessionsModal = false"
              class="text-white/80 hover:text-white transition-colors bg-white/10 hover:bg-white/20 p-2 rounded-lg"
            >
              <X class="w-6 h-6" />
            </button>
          </div>
          
          <!-- Modal Body -->
          <div class="p-4 sm:p-6 overflow-y-auto flex-1">
            <div v-if="selectedClassSessions.length === 0" class="text-center py-8 text-gray-500 text-sm sm:text-base">
              Chưa có lịch học cụ thể.
            </div>
            
            <div v-else class="grid gap-2 sm:gap-3">
              <div 
                v-for="(session, idx) in selectedClassSessions" 
                :key="session.id"
                class="flex items-center gap-2 sm:gap-4 p-3 sm:p-4 rounded-lg sm:rounded-xl border border-gray-100 hover:border-emerald-200 hover:bg-emerald-50/50 transition-all text-sm sm:text-base"
                :class="session.isRegistered ? 'border-emerald-300 bg-emerald-50' : ''"
              >
                <!-- Index -->
                <div class="w-8 h-8 rounded-full" :class="session.isRegistered ? 'bg-emerald-100 text-emerald-600' : 'bg-emerald-100 text-emerald-600'" >
                  <div class="flex items-center justify-center font-bold text-sm shrink-0">
                    {{ idx + 1 }}
                  </div>
                </div>
                
                <!-- Date Info -->
                <div class="flex-1">
                  <div class="font-bold text-gray-800 flex items-center gap-2">
                    <CalendarIcon class="w-4 h-4 text-emerald-500" />
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
                  {{ session.isRegistered ? 'Đã đăng ký' : getSessionStatus(session).text }}
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
        class="fixed inset-0 z-50 flex items-center justify-center p-3 sm:p-4 bg-black/50"
        @click.self="closeScheduleModal"
      >
        <div class="bg-white rounded-xl sm:rounded-2xl w-full max-w-4xl shadow-2xl overflow-hidden max-h-[90vh] flex flex-col">
          <!-- Modal Header -->
          <div class="p-4 sm:p-6 bg-gradient-to-r from-emerald-600 to-teal-600 text-white flex justify-between items-center shrink-0">
            <div class="min-w-0">
              <h3 class="text-lg sm:text-xl font-bold flex items-center gap-2">
                <CalendarIcon class="w-5 h-5 sm:w-6 sm:h-6" />
                Chọn lịch học
              </h3>
              <p class="text-emerald-100 text-xs sm:text-sm mt-1 truncate">{{ selectedFitnessClass?.name }}</p>
            </div>
            <button 
              @click="closeScheduleModal"
              class="text-white/80 hover:text-white transition-colors bg-white/10 hover:bg-white/20 p-2 rounded-lg"
            >
              <X class="w-6 h-6" />
            </button>
          </div>
          
          <!-- Modal Body -->
          <div class="p-4 sm:p-6 overflow-y-auto flex-1">
            <div v-if="loadingSchedules" class="text-center py-8 text-gray-500 text-sm sm:text-base">
              <div class="animate-spin rounded-full h-10 w-10 sm:h-12 sm:w-12 border-b-2 border-emerald-600 mx-auto mb-4"></div>
              Đang tải lịch học...
            </div>
            
            <div v-else-if="availableSchedules.length === 0" class="text-center py-8 text-gray-500">
              <Inbox class="w-16 h-16 text-gray-400 mx-auto mb-4" />
              <p class="text-lg font-semibold">Không có lịch học khả dụng</p>
              <p class="text-sm mt-2">Tất cả các buổi học đã đầy hoặc đã diễn ra</p>
            </div>
            
            <div v-else>
              <div class="mb-4 p-3 bg-emerald-50 border border-emerald-200 rounded-lg">
                <p class="text-sm text-emerald-800">
                  <strong>Hướng dẫn:</strong> Chọn các buổi học bạn muốn đăng ký bằng cách click vào từng dòng. 
                  Bạn có thể chọn nhiều buổi cùng lúc.
                </p>
              </div>
              
              <div class="overflow-x-auto -mx-4 sm:mx-0">
                <table class="w-full border-collapse min-w-[600px]">
                  <thead>
                    <tr class="bg-gray-100 border-b-2 border-gray-300">
                      <th class="p-2 sm:p-3 text-left font-bold text-gray-700 text-xs sm:text-sm w-8 sm:w-12">
                        <input 
                          ref="selectAllCheckbox"
                          type="checkbox" 
                          :checked="selectedScheduleIds.length === availableSchedules.filter(s => canSelectSchedule(s)).length && availableSchedules.filter(s => canSelectSchedule(s)).length > 0"
                          @change="toggleAllSchedules"
                          class="w-5 h-5 text-emerald-600 rounded focus:ring-emerald-500"
                        />
                      </th>
                      <th class="p-2 sm:p-3 text-left font-bold text-gray-700 text-xs sm:text-sm">Ngày</th>
                      <th class="p-2 sm:p-3 text-left font-bold text-gray-700 text-xs sm:text-sm">Thời gian</th>
                      <th class="p-2 sm:p-3 text-left font-bold text-gray-700 text-xs sm:text-sm">Phòng</th>
                      <th class="p-2 sm:p-3 text-left font-bold text-gray-700 text-xs sm:text-sm">Trạng thái</th>
                      <th class="p-2 sm:p-3 text-left font-bold text-gray-700 text-xs sm:text-sm">Chỗ trống</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr 
                      v-for="schedule in availableSchedules" 
                      :key="schedule.id"
                      @click="toggleSchedule(schedule)"
                      class="border-b border-gray-200 hover:bg-emerald-50 cursor-pointer transition-colors"
                      :class="{
                        'bg-emerald-100': selectedScheduleIds.includes(schedule.id),
                        'opacity-50 cursor-not-allowed': !canSelectSchedule(schedule),
                        'bg-emerald-50 border-emerald-200': schedule.isRegistered
                      }"
                    >
                      <td class="p-2 sm:p-3">
                        <input 
                          type="checkbox" 
                          :checked="selectedScheduleIds.includes(schedule.id)"
                          :disabled="!canSelectSchedule(schedule)"
                          @click.stop="toggleSchedule(schedule)"
                          class="w-4 h-4 sm:w-5 sm:h-5 text-emerald-600 rounded focus:ring-emerald-500"
                        />
                      </td>
                      <td class="p-2 sm:p-3 font-medium text-gray-800 text-xs sm:text-sm">
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
                          :class="schedule.isRegistered
                            ? 'bg-emerald-100 text-emerald-700'
                            : schedule.status === 'OPEN'
                              ? 'bg-green-100 text-green-700'
                              : schedule.status === 'CLOSED' || schedule.status === 'CANCELLED'
                                ? 'bg-orange-100 text-orange-700'
                                : 'bg-gray-100 text-gray-700'"
                        >
                          <template v-if="schedule.isRegistered">Đã đăng ký</template>
                          <template v-else>
                            {{ schedule.status === 'OPEN' ? 'Mở' : schedule.status === 'CLOSED' ? 'Đóng' : schedule.status === 'CANCELLED' ? 'Hủy' : 'N/A' }}
                          </template>
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
              class="px-6 py-2 bg-gradient-to-r from-emerald-600 to-teal-600 text-white font-bold rounded-xl hover:shadow-lg transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
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
import { useToast } from 'vue-toastification';
import unifiedApi from '@/services/unifiedClassApi.js';
import apiService from '@/views/Test/apiService.js';
import Swal from 'sweetalert2';
import { formatDate, getWeeksUntilStart, canVIPRegister, canAllRegister } from '@/views/Test/dateUtils.js';
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
  Eye,
  X,
  Inbox
} from 'lucide-vue-next';

const authStore = useAuthStore();
const toast = useToast();
const USE_REAL_API = true; // Set to false to use mock data

const classes = ref([]);
const roomsData = ref([]);
const teachersData = ref([]);
const studentsData = ref([]);
const registrations = ref([]);
const registeredScheduleIds = ref(new Set());
const filterDifficulty = ref('');
const filterRegistrationStatus = ref(''); // '' = all, 'registered' = đã đăng ký, 'not_registered' = chưa đăng ký
const searchQuery = ref('');
const currentStudentId = computed(() => {
  const user = authStore.user;
  return user?.id || null;
});
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
const allRegistrations = ref([]);

const viewSessions = async (cls) => {
  selectedClass.value = cls;
  try {
    const sessions = await unifiedApi.getSessions(cls.id);
    selectedClassSessions.value = sessions
      .map(s => ({ ...s, isRegistered: registeredScheduleIds.value.has(s.id) }))
      .sort((a, b) => new Date(a.date) - new Date(b.date));
    showSessionsModal.value = true;
  } catch (error) {
    toast.error('Không thể tải lịch học: ' + error.message);
  }
};

const getSessionStatus = (session) => {
  const now = new Date();
  const sessionDate = new Date(session.date);
  // Reset hours for pure date comparison
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
  const sDate = new Date(sessionDate.getFullYear(), sessionDate.getMonth(), sessionDate.getDate());

  if (sDate < today) {
    return { text: 'Đã diễn ra', class: 'bg-gray-100 text-gray-500' };
  } else if (sDate.getTime() === today.getTime()) {
    // Check time if needed, for simplicity assume 'Today' is Active/Upcoming
    return { text: 'Hôm nay', class: 'bg-emerald-100 text-emerald-700' };
  } else {
    return { text: 'Sắp diễn ra', class: 'bg-green-100 text-green-700' };
  }
};

const availableClasses = computed(() => {
  return classes.value.filter(c => c.status === 'open');
});

const filteredClasses = computed(() => {
  let result = availableClasses.value;
  
  if (filterDifficulty.value) {
    result = result.filter(c => c.difficulty === filterDifficulty.value);
  }
  
  if (filterRegistrationStatus.value === 'registered') {
    result = result.filter(c => isRegistered(c.id));
  } else if (filterRegistrationStatus.value === 'not_registered') {
    result = result.filter(c => !isRegistered(c.id));
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
    toast.warning('Vui lòng đăng nhập!');
    return;
  }
  
  try {
    const [allClasses, allRooms, allTeachers, allStudents] = await Promise.all([
      unifiedApi.getClasses(),
      unifiedApi.getRooms(),
      unifiedApi.getTeachers(),
      unifiedApi.getStudents()
    ]);

    classes.value = allClasses;
    roomsData.value = allRooms;
    teachersData.value = allTeachers;
    studentsData.value = allStudents;

    const regPromises = studentsData.value.map(s => unifiedApi.getStudentRegistrations(s.id));
    const results = await Promise.all(regPromises);
    allRegistrations.value = results.flat();

    registrations.value = allRegistrations.value.filter(r => r.studentId === currentStudentId.value);
    registeredScheduleIds.value = new Set(registrations.value.map(r => r.scheduleId));
    
    currentStudent.value = allStudents.find(s => s.id === currentStudentId.value) || { membershipTier: 'BASIC' };
    
    for (const cls of classes.value) {
      calculateSlotsFromData(cls);
    }

  } catch (error) {
    console.error("LoadData Error:", error);
    toast.error('Không thể tải dữ liệu hệ thống');
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
  return availableSlotsCache.value[cls.id] ?? cls.maxStudents;
};
const calculateSlotsFromData = (cls) => {
  const capacity = cls.maxStudents || 0;

  const classRegs = allRegistrations.value.filter(r => 
    r.classId === cls.id && r.status === 'active'
  );
  const uniqueStudentIds = new Set(classRegs.map(r => r.studentId));
  
  const enrolledCount = uniqueStudentIds.size;

  availableSlotsCache.value[cls.id] = Math.max(0, capacity - enrolledCount);
};

const isVIPEarlyAccess = (cls) => {
  return canVIPRegister(cls.startDate) && !canAllRegister(cls.startDate);
};

const isRegistered = (classId) => {
  return registrations.value.some(r => r.classId === classId && r.status === 'active');
};

const canRegister = (cls) => { 
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
  if (isVIPEarlyAccess(cls)) {
    if (currentStudent.value.membershipTier === 'VIP') {
      return 'Đăng ký sớm (VIP)';
    } else {
      return 'Chỉ VIP được đăng ký sớm';
    }
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
      const isMine = registeredScheduleIds.value.has(s.id);
      
      if ((isFuture && isOpen) || isMine) {
        // Update slots cache for this schedule
        await updateScheduleSlots(s);
        const hasSlots = getScheduleAvailableSlots(s) > 0 || isMine;
        if (hasSlots) {
          filteredSchedules.push({ ...s, isRegistered: isMine });
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
    toast.error('Lỗi khi tải lịch học: ' + error.message);
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

const canSelectSchedule = (schedule) => {
  if (schedule.isRegistered) return false;
  return schedule.status === 'OPEN' && getScheduleAvailableSlots(schedule) > 0;
};

const toggleSchedule = (schedule) => {
  if (!canSelectSchedule(schedule)) {
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
      .filter(s => canSelectSchedule(s))
      .map(s => s.id);
  } else {
    // Deselect all
    selectedScheduleIds.value = [];
  }
};

// Watch for changes to update indeterminate state
watch([selectedScheduleIds, availableSchedules], () => {
  if (selectAllCheckbox.value) {
    const availableCount = availableSchedules.value.filter(s => canSelectSchedule(s)).length;
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
      const regs = await apiService.memberRegistration.getBySchedule(schedule.id);
      const capacity = schedule.capacity || 20;
      scheduleSlotsCache.value[schedule.id] = Math.max(0, capacity - regs.length);
    } else {
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
    if (typeof dateTime === 'string' && dateTime.match(/^\d{2}:\d{2}$/)) {
      return dateTime;
    }
    return '';
  }
};

const registerSelectedSchedules = async () => {
  if (!currentStudentId.value) {
    toast.warning('Vui lòng đăng nhập!');
    return;
  }
  
  if (selectedScheduleIds.value.length === 0) {
    toast.warning('Vui lòng chọn ít nhất một buổi học!');
    return;
  }

  const className = selectedFitnessClass.value?.name || 'Lớp học';
  
  const result = await Swal.fire({
    title: 'Xác nhận đăng ký?',
    text: `Bạn có chắc muốn đăng ký ${selectedScheduleIds.value.length} buổi học cho lớp "${className}"?`,
    icon: 'question',
    showCancelButton: true,
    confirmButtonColor: '#10B981', // Màu Emerald
    cancelButtonColor: '#EF4444', // Màu Red
    confirmButtonText: 'Đăng ký ngay',
    cancelButtonText: 'Hủy bỏ'
  });
  
  // 5. Xử lý khi người dùng bấm "Đăng ký ngay"
  if (result.isConfirmed) {
    try {
      // Hiện loading
      Swal.fire({
        title: 'Đang xử lý...',
        allowOutsideClick: false, // Không cho click ra ngoài
        didOpen: () => Swal.showLoading()
      });

      // Gọi API
      await unifiedApi.registerBulkSchedules(currentStudentId.value, selectedScheduleIds.value);
      
      // Đóng loading
      Swal.close(); 
      
      // Thông báo thành công
      toast.success(`Đã đăng ký thành công ${selectedScheduleIds.value.length} buổi học!`);
      
      closeScheduleModal();
      await loadData();
      
    } catch (error) {
      Swal.close(); // Đóng loading nếu lỗi
      // Kiểm tra xem backend có trả về message lỗi cụ thể không
      const errorMessage = error.response?.data?.message || 'Không được đăng ký trùng lịch học!';
      toast.error(errorMessage);
    }
  }
};

const register = async (cls) => {
  // This is now replaced by openScheduleSelection
  await openScheduleSelection(cls);
};

const cancelRegistration = async (cls) => {
  if (!currentStudentId.value) {
    toast.warning('Vui lòng đăng nhập!');
    return;
  }
  
  const result = await Swal.fire({
    title: 'Xác nhận hủy?',
    text: `Bạn có chắc muốn hủy đăng ký lớp "${cls.name}"?`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#EF4444',
    cancelButtonColor: '#6B7280',
    confirmButtonText: 'Hủy đăng ký',
    cancelButtonText: 'Không'
  });
  
  if (result.isConfirmed) {
    try {
      await unifiedApi.cancelRegistration(cls.id, currentStudentId.value);
      toast.success('Đã hủy đăng ký!');
      await loadData();
    } catch (error) {
      toast.error('Có lỗi xảy ra');
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