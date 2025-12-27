<template>
  <div class="p-6">
    <!-- Header -->
    <div class="flex justify-between items-center mb-8">
      <div class="flex items-center gap-3">
        <Dumbbell class="w-10 h-10 text-emerald-600" />
        <h1 class="text-3xl font-bold bg-gradient-to-r from-emerald-600 to-emerald-700 bg-clip-text text-transparent">
          Quản Lý Lớp Học
        </h1>
      </div>
      <button
        @click="showCreateForm = true"
        class="px-6 py-3 bg-gradient-to-r from-emerald-600 to-emerald-700 text-white rounded-xl font-semibold hover:shadow-lg hover:-translate-y-0.5 transition-all duration-300 flex items-center gap-2"
      >
        <Plus class="w-5 h-5" />
        Tạo Lớp Mới
      </button>
    </div>

    <!-- Filters -->
    <div class="flex gap-4 mb-6">
      <div class="relative">
        <Filter class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
        <select
          v-model="filterStatus"
          class="pl-10 pr-4 py-3 border-2 border-gray-200 rounded-xl focus:border-emerald-600 focus:ring-2 focus:ring-emerald-100 outline-none transition-all appearance-none bg-white"
        >
          <option value="">Tất cả trạng thái</option>
          <option value="draft">Nháp</option>
          <option value="pending_teacher">Chờ giáo viên</option>
          <option value="waiting_approval">Chờ duyệt</option>
          <option value="ready_for_students">Sẵn sàng</option>
        </select>
      </div>
      
      <div class="relative flex-1">
        <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
        <input
          v-model="searchQuery"
          type="text"
          placeholder="Tìm kiếm lớp học..."
          class="w-full pl-10 pr-4 py-3 border-2 border-gray-200 rounded-xl focus:border-emerald-600 focus:ring-2 focus:ring-emerald-100 outline-none transition-all"
        />
      </div>
    </div>

    <!-- Class Grid -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
      <div 
        v-for="cls in filteredClasses" 
        :key="cls.id" 
        class="bg-white rounded-2xl shadow-md hover:shadow-xl transition-all duration-300 hover:-translate-y-1 overflow-hidden"
        :class="{
          'border-2 border-green-400': cls.status === 'ready_for_students',
          'border-2 border-orange-400': cls.status === 'waiting_approval'
        }"
      >
        <!-- Card Header -->
        <div class="p-5 bg-gradient-to-r from-emerald-600 to-emerald-700 text-white">
          <div class="flex justify-between items-start">
            <h3 class="text-xl font-bold">{{ cls.name }}</h3>
            <span class="px-3 py-1 bg-white/20 rounded-full text-xs font-semibold flex items-center gap-1">
              <component :is="getStatusIcon(cls.status)" class="w-3 h-3" />
              {{ getStatusText(cls.status) }}
            </span>
          </div>
        </div>
        
        <!-- Card Body -->
        <div class="p-5">
          <p class="text-gray-600 mb-4 line-clamp-2">{{ cls.description }}</p>
          
          <div class="grid grid-cols-2 gap-3 text-sm">
            <div>
              <span class="text-gray-500 font-semibold block flex items-center gap-1"><BarChart3 class="w-4 h-4" /> Độ khó:</span>
              <span class="text-gray-800">{{ cls.difficulty }}</span>
            </div>
            <div>
              <span class="text-gray-500 font-semibold block flex items-center gap-1"><Users class="w-4 h-4" /> Sức chứa:</span>
              <span class="text-gray-800">{{ getEnrolledCount(cls.id) }}/{{ cls.maxStudents }}</span>
            </div>
            <div>
              <span class="text-gray-500 font-semibold block flex items-center gap-1"><MapPin class="w-4 h-4" /> Phòng:</span>
              <span class="text-gray-800">{{ getRoomName(cls.roomId) }}</span>
            </div>
            <div>
              <span class="text-gray-500 font-semibold block flex items-center gap-1"><GraduationCap class="w-4 h-4" /> Giáo viên:</span>
              <span class="text-gray-800">{{ getTeacherName(cls.teacherId) }}</span>
            </div>
            <div>
              <span class="text-gray-500 font-semibold block flex items-center gap-1"><Clock class="w-4 h-4" /> Thời gian:</span>
              <span class="text-gray-800">{{ cls.startTime }} - {{ cls.endTime }}</span>
            </div>
            <div>
              <span class="text-gray-500 font-semibold block flex items-center gap-1"><Calendar class="w-4 h-4" /> Lịch:</span>
              <span class="text-gray-800">{{ getScheduleText(cls) }}</span>
            </div>
          </div>
        </div>
        
        <!-- Card Footer -->
        <div class="p-4 bg-gray-50 border-t flex gap-2">
          <button 
            @click="viewDetails(cls)" 
            class="flex-1 px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 font-semibold transition-all flex items-center justify-center gap-2"
          >
            <Eye class="w-4 h-4" /> Chi tiết
          </button>
          <button 
            v-if="cls.status === 'waiting_approval'" 
            @click="approveTeacher(cls)" 
            class="flex-1 px-4 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600 font-semibold transition-all flex items-center justify-center gap-2"
          >
            <CheckCircle class="w-4 h-4" /> Duyệt
          </button>
          <button
            @click="deleteClass(cls.id)"
            class="px-4 py-2 bg-emerald-500 text-white rounded-lg hover:bg-emerald-600 font-semibold transition-all flex items-center justify-center"
          >
            <Trash2 class="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>

    <!-- Create Class Modal -->
    <div 
      v-if="showCreateForm" 
      class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
      @click.self="showCreateForm = false"
    >
      <div class="bg-white rounded-2xl w-full max-w-4xl max-h-[90vh] overflow-y-auto shadow-2xl">
        <!-- Modal Header -->
        <div class="sticky top-0 p-6 bg-gradient-to-r from-emerald-600 to-emerald-700 text-white flex justify-between items-center rounded-t-2xl z-50">
          <h2 class="text-2xl font-bold">Tạo Lớp Học Mới</h2>
          <button 
            @click="showCreateForm = false" 
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
                <label class="block text-sm font-semibold text-gray-700 mb-2">Tên lớp học *</label>
                <input
                  v-model="newClass.name"
                  type="text"
                  placeholder="VD: Yoga Buổi Sáng"
                  class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-emerald-600 focus:ring-2 focus:ring-emerald-100 outline-none transition-all"
                />
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
                    class="px-4 py-2 bg-gradient-to-r from-emerald-600 to-emerald-700 text-white rounded-full text-sm font-semibold flex items-center gap-2 animate-slideIn"
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
                  />
                </div>
                
                <div>
                  <label class="block text-sm font-semibold text-gray-700 mb-2">Giờ kết thúc *</label>
                  <input
                    v-model="newClass.endTime"
                    type="time"
                    class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-emerald-600 focus:ring-2 focus:ring-emerald-100 outline-none transition-all"
                  />
                </div>
              </div>
              
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
                  v-for="room in roomsData" 
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
                    <span v-if="!isRoomCapacityValid(room)" class="block text-xs mt-1 ml-5">
                      (Cần tối thiểu {{ newClass.maxStudents }} chỗ)
                    </span>
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
            :disabled="!canProceed"
            class="px-6 py-3 bg-gradient-to-r from-emerald-600 to-emerald-700 text-white rounded-xl font-semibold hover:shadow-lg transition-all disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Tiếp theo →
          </button>
          
          <button 
            v-if="currentStep === 3" 
            @click="createClass" 
            class="px-6 py-3 bg-gradient-to-r from-green-500 to-green-600 text-white rounded-xl font-semibold hover:shadow-lg transition-all flex items-center gap-2"
          >
            <CheckCircle class="w-5 h-5" />
            Tạo lớp học
          </button>
        </div>
      </div>
    </div>
    <!-- Details Modal -->
    <div 
      v-if="showDetailsModal && selectedClass" 
      class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
      @click.self="showDetailsModal = false"
    >
      <div class="bg-white rounded-2xl w-full max-w-3xl max-h-[90vh] overflow-y-auto shadow-2xl animate-fadeIn">
        <!-- Modal Header -->
        <div class="sticky top-0 p-6 bg-gradient-to-r from-emerald-600 to-emerald-700 text-white flex justify-between items-start rounded-t-2xl z-50">
          <div>
            <h2 class="text-2xl font-bold mb-1">{{ selectedClass.name }}</h2>
            <div class="flex items-center gap-2 text-emerald-100 text-sm">
              <component :is="getStatusIcon(selectedClass.status)" class="w-4 h-4" />
              {{ getStatusText(selectedClass.status) }}
            </div>
          </div>
          <button 
            @click="showDetailsModal = false" 
            class="w-10 h-10 bg-white/20 rounded-full hover:bg-white/30 transition-all flex items-center justify-center"
          >
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
              {{ selectedClass.description }}
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
                  <div class="font-bold text-gray-800">{{ selectedClass.difficulty }}</div>
                </div>
              </div>
              <div class="p-4 bg-gray-50 rounded-xl border border-gray-100 flex items-center gap-3">
                <Users class="w-8 h-8 text-green-500 bg-green-100 p-1.5 rounded-lg" />
                <div>
                  <div class="text-xs text-gray-500 font-semibold uppercase">Sĩ số</div>
                  <div class="font-bold text-gray-800">
                    {{ getEnrolledCount(selectedClass.id) }}/{{ selectedClass.maxStudents }}
                  </div>
                </div>
              </div>
              <div class="p-4 bg-gray-50 rounded-xl border border-gray-100 flex items-center gap-3">
                <MapPin class="w-8 h-8 text-emerald-500 bg-emerald-100 p-1.5 rounded-lg" />
                <div>
                  <div class="text-xs text-gray-500 font-semibold uppercase">Phòng học</div>
                  <div class="font-bold text-gray-800">{{ getRoomName(selectedClass.roomId) }}</div>
                </div>
              </div>
              <div class="p-4 bg-gray-50 rounded-xl border border-gray-100 flex items-center gap-3">
                <GraduationCap class="w-8 h-8 text-purple-500 bg-purple-100 p-1.5 rounded-lg" />
                <div>
                  <div class="text-xs text-gray-500 font-semibold uppercase">Giáo viên</div>
                  <div class="font-bold text-gray-800">{{ getTeacherName(selectedClass.teacherId) }}</div>
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
                <span class="font-semibold text-gray-800">{{ getPatternTypeText(selectedClass.patternType) }}</span>
              </div>
              <div class="flex justify-between border-b border-gray-200 pb-2">
                <span class="text-gray-600">Thời gian:</span>
                <span class="font-semibold text-gray-800">{{ selectedClass.startTime }} - {{ selectedClass.endTime }}</span>
              </div>
              <div class="flex justify-between border-b border-gray-200 pb-2">
                <span class="text-gray-600">Chi tiết:</span>
                <span class="font-semibold text-gray-800">{{ getScheduleText(selectedClass) }}</span>
              </div>
              <div v-if="selectedClass.patternType !== 'no_repeat'" class="flex justify-between border-b border-gray-200 pb-2">
                <span class="text-gray-600">Thời gian áp dụng:</span>
                <span class="font-semibold text-gray-800">
                  {{ formatDate(selectedClass.startDate) }} - {{ formatDate(selectedClass.endDate) }}
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

            <div v-if="loadingSessionsDetails" class="text-center py-8">
              <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-emerald-600 mx-auto mb-4"></div>
              Đang tải...
            </div>
            
            <div v-else-if="selectedClassSessions.length > 0" class="space-y-2 max-h-96 overflow-y-auto">
              <div
                v-for="(session, idx) in selectedClassSessions"
                :key="session.id"
                class="flex items-center gap-4 p-3 rounded-xl border border-gray-200 hover:border-emerald-300 hover:bg-emerald-50 transition-all"
              >
                <div class="w-8 h-8 rounded-full bg-emerald-100 text-emerald-600 flex items-center justify-center font-bold text-sm shrink-0">
                  {{ idx + 1 }}
                </div>
                <div class="flex-1">
                  <div class="font-bold text-gray-800">{{ formatDate(session.date) }}</div>
                  <div class="text-sm text-gray-500">{{ formatScheduleTime(session.startTime) }} - {{ formatScheduleTime(session.endTime) }}</div>
                </div>
                <div class="text-xs font-semibold px-2 py-1 rounded" :class="{
                  'bg-green-100 text-green-700': session.status === 'OPEN',
                  'bg-gray-100 text-gray-500': session.status === 'CLOSED'
                }">
                  {{ session.status === 'OPEN' ? 'Mở' : 'Đóng' }}
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
              Danh sách học viên ({{ selectedClassStudents.length }})
            </h3>
            
            <div v-if="selectedClassStudents.length > 0" class="overflow-x-auto">
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
                  <tr v-for="student in selectedClassStudents" :key="student.id" class="border-b border-gray-50 hover:bg-gray-50">
                    <td class="px-4 py-3 font-medium text-gray-900">{{ student.name }}</td>
                    <td class="px-4 py-3 text-gray-600">{{ student.email }}</td>
                    <td class="px-4 py-3">
                      <span 
                        class="px-2 py-1 rounded-full text-xs font-bold"
                        :class="{
                          'bg-yellow-100 text-yellow-700': student.membershipTier === 'VIP',
                          'bg-emerald-100 text-emerald-700': student.membershipTier === 'PREMIUM',
                          'bg-gray-100 text-gray-700': student.membershipTier === 'BASIC'
                        }"
                      >
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
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import unifiedApi from '@/services/unifiedClassApi.js';
import { formatDate } from '@/views/Test/dateUtils.js';
import api from '@/services/api';
import { 
  Dumbbell, 
  Plus, 
  Search, 
  FileText, 
  UserPlus, 
  Clock, 
  CheckCircle, 
  XCircle, 
  BarChart3, 
  Users, 
  MapPin, 
  GraduationCap, 
  Calendar, 
  Eye, 
  Trash2, 
  X, 
  Info, 
  CheckSquare, 
  AlertTriangle,
  Filter
} from 'lucide-vue-next';

const router = useRouter();

// State
const classes = ref([]);
const roomsData = ref([]);
const teachersData = ref([]);
const studentsData = ref([]);
const registrationsData = ref([]);
const filterStatus = ref('');
const searchQuery = ref('');
const showCreateForm = ref(false);
const showDetailsModal = ref(false);
const selectedClass = ref(null);
const currentStep = ref(0);
const steps = ['Thông tin cơ bản', 'Lịch học', 'Chọn phòng', 'Xác nhận'];
const daysOfWeek = ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'];
const previewSessions = ref([]);
const roomConflicts = ref({});
const tempDate = ref('');
const loadingSessionsDetails = ref(false);
const selectedClassSessions = ref([]);


const getEmptyClass = () => ({
  name: '',
  description: '',
  difficulty: 'Beginner',
  maxStudents: 20,
  status: 'pending_teacher',
  roomId: null,
  teacherId: null,
  patternType: 'weekly',
  startTime: '07:00',
  endTime: '08:30',
  daysOfWeek: [],
  startDate: '',
  endDate: '',
  selectedDates: [],
  createdBy: 'manager'
});

const newClass = ref(getEmptyClass());

// Computed
const filteredClasses = computed(() => {
  let result = classes.value;
  
  if (filterStatus.value) {
    result = result.filter(c => c.status === filterStatus.value);
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

const canProceed = computed(() => {
  if (currentStep.value === 0) {
    return newClass.value.name && newClass.value.description && 
           newClass.value.difficulty && newClass.value.maxStudents > 0;
  }
  if (currentStep.value === 1) {
    const hasTime = newClass.value.startTime && newClass.value.endTime;
    
    if (newClass.value.patternType === 'no_repeat') {
      return hasTime && newClass.value.selectedDates && newClass.value.selectedDates.length > 0;
    }
    
    const hasDate = newClass.value.startDate;
    const hasEndDate = newClass.value.endDate;
    const hasDays = newClass.value.daysOfWeek.length > 0;
    return hasTime && hasDate && hasEndDate && hasDays;
  }
  if (currentStep.value === 2) {
    if (!newClass.value.roomId) return false;
    const room = roomsData.value.find(r => r.id === newClass.value.roomId);
    return room && isRoomCapacityValid(room) && !isRoomConflicted(newClass.value.roomId);
  }
  return true;
});

const selectedClassStudents = computed(() => {
  if (!selectedClass.value) return [];
  // Get all student registrations and filter by class
  const registrations = registrationsData.value.filter(r => r.classId === selectedClass.value.id && r.status === 'active');
  return registrations.map(r => {
    const student = studentsData.value.find(s => s.id === r.studentId);
    return {
      ...student,
      registeredAt: r.registeredAt
    };
  });
});

// Methods
const loadData = async () => {
  classes.value = await unifiedApi.getClasses();
  roomsData.value = await unifiedApi.getRooms();
  console.log(roomsData.value);
  teachersData.value = await unifiedApi.getTeachers();
  studentsData.value = await unifiedApi.getStudents();
  
  // Load all registrations (for student list in details)
  // In real app, we'd load per class, but for simplicity load all
  registrationsData.value = [];
  for (const student of studentsData.value) {
    const studentRegs = await unifiedApi.getStudentRegistrations(student.id);
    registrationsData.value.push(...studentRegs);
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

const getStatusText = (status) => {
  const statusMap = {
    draft: 'Nháp',
    pending_teacher: 'Chờ giáo viên',
    waiting_approval: 'Chờ duyệt',
    ready_for_students: 'Sẵn sàng',
    cancelled: 'Đã hủy'
  };
  return statusMap[status] || status;
};

const getStatusIcon = (status) => {
  const iconMap = {
    draft: FileText,
    pending_teacher: UserPlus,
    waiting_approval: Clock,
    ready_for_students: CheckCircle,
    cancelled: XCircle
  };
  return iconMap[status] || FileText;
};

const getRoomName = (roomId) => {
  const room = roomsData.value.find(r => r.id === roomId);
  return room ? room.name : 'Chưa chọn';
};

const getTeacherName = (teacherId) => {
  if (!teacherId) return 'Chưa có';
  const teacher = teachersData.value.find(t => t.id === teacherId);
  return teacher ? teacher.name : 'Không xác định';
};

const getEnrolledCount = (classId) => {
  return registrationsData.value.filter(r => r.classId === classId && r.status === 'active').length;
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

const getPatternTypeText = (type) => {
  const map = {
    weekly: 'Hàng tuần',
    monthly: 'Hàng tháng',
    custom_range: 'Tùy chỉnh',
    no_repeat: 'Không lặp lại'
  };
  return map[type] || type;
};

const updatePreview = async () => {
  if (canProceed.value && (currentStep.value === 1 || currentStep.value === 2)) {
    // Generate sessions based on pattern
    const sessions = [];
    const start = new Date(newClass.value.startDate);
    const end = new Date(newClass.value.endDate);
    
    if (newClass.value.patternType === 'no_repeat') {
      // Use selectedDates
      if (newClass.value.selectedDates && newClass.value.selectedDates.length > 0) {
        newClass.value.selectedDates.forEach(date => {
          const [startHour, startMin] = newClass.value.startTime.split(':').map(Number);
          const [endHour, endMin] = newClass.value.endTime.split(':').map(Number);
          const startTime = new Date(date);
          startTime.setHours(startHour, startMin, 0, 0);
          const endTime = new Date(date);
          endTime.setHours(endHour, endMin, 0, 0);
          
          sessions.push({
            startTime: startTime.toISOString(),
            endTime: endTime.toISOString()
          });
        });
      }
    } else {
      // Weekly pattern
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
            startTime: startTime.toISOString(),
            endTime: endTime.toISOString()
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
    console.log('🔍 Checking conflicts for', sessions.length, 'sessions');
    for (const room of roomsData.value) {
      const conflicts = await unifiedApi.checkRoomConflicts(room.id, sessions);
      console.log(`Room ${room.name} (${room.id}):`, conflicts.length, 'conflicts');
      if (conflicts.length > 0) {
        roomConflicts.value[room.id] = conflicts;
        console.log('Conflicts:', conflicts);
      }
    }
    console.log('Final roomConflicts:', roomConflicts.value);
  }
};

const nextStep = async () => {
  if (canProceed.value) {
    currentStep.value++;
    if (currentStep.value === 2) {
      // When moving to room selection step, update preview and load available rooms
      await updatePreview();
      // Also ensure rooms are loaded
      if (roomsData.value.length === 0) {
        roomsData.value = await unifiedApi.getRooms();
      }
    }
  }
};

const selectRoom = (roomId) => {
  const room = roomsData.value.find(r => r.id === roomId);
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

const createClass = async () => {
  try {
    // Step 1: Prepare dates for no_repeat pattern
    if (newClass.value.patternType === 'no_repeat' && newClass.value.selectedDates && newClass.value.selectedDates.length > 0) {
      const sortedDates = [...newClass.value.selectedDates].sort();
      newClass.value.startDate = sortedDates[0];
      newClass.value.endDate = sortedDates[sortedDates.length - 1];
    }

    // Step 2: Create FitnessClass
    const fitnessClassData = {
      name: newClass.value.name,
      description: newClass.value.description,
      difficultyLevel: newClass.value.difficulty,
      status: 'ACTIVE'
    };
    const fitnessClassRes = await api.post('/fitness_class', fitnessClassData);
    const fitnessClassId = fitnessClassRes.data.id;
    console.log('✅ Created FitnessClass:', fitnessClassId);

    // Step 3: Convert daysOfWeek array to backend format (MONDAY,WEDNESDAY,FRIDAY)
    let daysOfWeekStr = '';
    if (newClass.value.patternType === 'no_repeat') {
      // For no_repeat, we'll create individual schedules manually
      // But we still need a pattern for the generate API structure
      // Use the first selected date's day of week
      if (newClass.value.selectedDates && newClass.value.selectedDates.length > 0) {
        const firstDate = new Date(newClass.value.selectedDates[0]);
        const dayNames = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];
        daysOfWeekStr = dayNames[firstDate.getDay()];
      }
    } else {
      // Convert array of day indices [0,1,2] to "MONDAY,TUESDAY,WEDNESDAY"
      const dayNames = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];
      daysOfWeekStr = newClass.value.daysOfWeek
        .map(dayIndex => dayNames[dayIndex])
        .join(',');
    }

    // Step 4: Create SchedulePattern
    const schedulePatternData = {
      daysOfWeek: daysOfWeekStr,
      timeStart: newClass.value.startTime + ':00', // Add seconds
      timeEnd: newClass.value.endTime + ':00',
      classStartDate: newClass.value.startDate,
      classEndDate: newClass.value.endDate
    };
    const patternRes = await api.post('/schedule-patterns', schedulePatternData);
    const patternId = patternRes.data.id;
    console.log('✅ Created SchedulePattern:', patternId);

    // Step 5: Check available rooms for this pattern
    const availableRoomsRes = await api.post('/room/available-for-pattern', schedulePatternData);
    const availableRooms = availableRoomsRes.data;
    
    if (availableRooms.length === 0) {
      throw new Error('Không có phòng nào trống trong khoảng thời gian này!');
    }

    // Check if selected room is available
    const selectedRoom = availableRooms.find(r => r.id === newClass.value.roomId);
    if (!selectedRoom) {
      throw new Error(`Phòng "${getRoomName(newClass.value.roomId)}" đã bị trùng lịch! Vui lòng chọn phòng khác.`);
    }

    // Step 6: Generate ClassSchedules using the generate API
    const generatePayload = {
      fitnessClass: { id: fitnessClassId },
      schedulePattern: { id: patternId },
      room: { id: newClass.value.roomId },
      capacity: newClass.value.maxStudents
    };

    let generatedSchedules = [];
    
    if (newClass.value.patternType === 'no_repeat' && newClass.value.selectedDates) {
      // For no_repeat, create individual schedules manually
      for (const dateStr of newClass.value.selectedDates) {
        const date = new Date(dateStr);
        const [startHour, startMin] = newClass.value.startTime.split(':').map(Number);
        const [endHour, endMin] = newClass.value.endTime.split(':').map(Number);
        
        const startDateTime = new Date(date);
        startDateTime.setHours(startHour, startMin, 0, 0);
        const endDateTime = new Date(date);
        endDateTime.setHours(endHour, endMin, 0, 0);

        // Format as ISO string for backend
        const startTimeISO = startDateTime.toISOString().slice(0, 19);
        const endTimeISO = endDateTime.toISOString().slice(0, 19);

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
          console.error('Error creating single schedule:', err);
          throw new Error(`Lỗi khi tạo lịch cho ngày ${formatDate(dateStr)}: ${err.response?.data?.message || err.message}`);
        }
      }
    } else {
      // For weekly/monthly/custom_range, use generate API
      const generateRes = await api.post('/classschedule/generate', generatePayload);
      generatedSchedules = generateRes.data;
    }

    console.log('✅ Generated ClassSchedules:', generatedSchedules.length);
    alert(`✅ Tạo lớp học thành công! Đã tạo ${generatedSchedules.length} buổi học.`);
    
    showCreateForm.value = false;
    currentStep.value = 0;
    newClass.value = getEmptyClass();
    previewSessions.value = [];
    roomConflicts.value = {};
    await loadData();
  } catch (error) {
    console.error('Error creating class:', error);
    const errorMessage = error.response?.data?.message || error.message || 'Có lỗi xảy ra';
    alert('❌ Lỗi: ' + errorMessage);
  }
};


const viewDetails = async (cls) => {
  selectedClass.value = cls;
  showDetailsModal.value = true;
  loadingSessionsDetails.value = true;
  selectedClassSessions.value = [];
  
  try {
    const sessions = await unifiedApi.getSessions(cls.id);
    selectedClassSessions.value = sessions.sort((a, b) => new Date(a.date) - new Date(b.date));
  } catch (error) {
    console.error('Error loading sessions:', error);
  } finally {
    loadingSessionsDetails.value = false;
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

const approveTeacher = async (cls) => {
  router.push('/test/manager/approve-teachers');
};

const deleteClass = async (classId) => {
  const cls = classes.value.find(c => c.id === classId);
  if (cls && cls.status === 'ready_for_students') {
    alert('❌ Không thể xóa lớp học đã sẵn sàng (đã có giáo viên và có thể có học viên)!');
    return;
  }
  
  if (confirm('Bạn có chắc muốn xóa lớp học này?')) {
    await unifiedApi.deleteClass(classId);
    await loadData();
  }
};

// Watchers
watch(() => newClass.value.patternType, updatePreview);
watch(() => newClass.value.startDate, updatePreview);
watch(() => newClass.value.endDate, updatePreview);
watch(() => newClass.value.startTime, updatePreview);
watch(() => newClass.value.endTime, updatePreview);
watch(() => newClass.value.daysOfWeek, updatePreview);
watch(() => newClass.value.selectedDates, updatePreview);

// Lifecycle
onMounted(() => {
  loadData();
});
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

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-10px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.animate-fadeIn {
  animation: fadeIn 0.3s ease-out;
}

.animate-slideIn {
  animation: slideIn 0.3s ease-out;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
