<template>
  <div class="p-6">
    <!-- Header -->
    <div class="flex items-center gap-3 mb-8">
      <Bell class="w-10 h-10 text-emerald-600" />
      <h1 class="text-3xl font-bold bg-gradient-to-r from-emerald-600 to-teal-600 bg-clip-text text-transparent">
        Thông Báo
      </h1>
    </div>

    <!-- Filters & Actions -->
    <div class="bg-white rounded-2xl shadow-lg p-6 mb-6 border border-emerald-100">
      <div class="flex flex-wrap gap-4 items-center justify-between">
        <!-- Filter Tabs -->
        <div class="flex gap-2">
          <button 
            v-for="tab in filterTabs" 
            :key="tab.value"
            @click="activeFilter = tab.value"
            class="px-4 py-2 rounded-lg font-semibold transition-all"
            :class="activeFilter === tab.value 
              ? 'bg-gradient-to-r from-emerald-600 to-teal-600 text-white' 
              : 'bg-gray-100 text-gray-700 hover:bg-gray-200'"
          >
            {{ tab.label }}
            <span v-if="tab.value === 'unread' && unreadCount > 0" class="ml-2 px-2 py-0.5 bg-red-500 text-white text-xs rounded-full">
              {{ unreadCount }}
            </span>
          </button>
        </div>

        <!-- Actions -->
        <div class="flex gap-2">
          <button 
            v-if="unreadCount > 0"
            @click="markAllAsRead"
            class="px-4 py-2 bg-emerald-100 text-emerald-700 rounded-lg font-semibold hover:bg-emerald-200 transition-all flex items-center gap-2"
          >
            <CheckCheck class="w-4 h-4" />
            Đánh dấu tất cả đã đọc
          </button>
          <button 
            v-if="notifications.length > 0"
            @click="clearAll"
            class="px-4 py-2 bg-red-100 text-red-700 rounded-lg font-semibold hover:bg-red-200 transition-all flex items-center gap-2"
          >
            <Trash2 class="w-4 h-4" />
            Xóa tất cả
          </button>
        </div>
      </div>

      <!-- Type Filter -->
      <div class="mt-4 flex flex-wrap gap-2">
        <button 
          v-for="type in notificationTypes" 
          :key="type.value"
          @click="toggleTypeFilter(type.value)"
          class="px-3 py-1 rounded-full text-sm font-semibold transition-all"
          :class="selectedTypes.includes(type.value)
            ? 'bg-emerald-600 text-white'
            : 'bg-gray-100 text-gray-700 hover:bg-gray-200'"
        >
          <component :is="type.icon" class="w-3 h-3 inline mr-1" />
          {{ type.label }}
        </button>
      </div>
    </div>

    <!-- Notifications List -->
    <div class="space-y-3">
      <div v-if="filteredNotifications.length === 0" class="text-center py-16 bg-white rounded-2xl shadow-lg">
        <Bell class="w-16 h-16 text-gray-400 mx-auto mb-4" />
        <p class="text-gray-500 text-lg font-semibold">Không có thông báo nào</p>
        <p class="text-gray-400 text-sm mt-2">Bạn sẽ nhận được thông báo về lớp học, gói hội viên và nhiều hơn nữa</p>
      </div>

      <transition-group name="list" tag="div" class="space-y-3">
        <div 
          v-for="notification in filteredNotifications" 
          :key="notification.id"
          class="bg-white rounded-2xl shadow-md hover:shadow-xl transition-all duration-300 overflow-hidden"
          :class="!notification.read ? 'border-2 border-emerald-200' : 'border border-gray-200'"
        >
          <div class="p-6">
            <div class="flex items-start gap-4">
              <!-- Icon -->
              <div 
                class="w-12 h-12 rounded-full flex items-center justify-center flex-shrink-0"
                :class="getNotificationColor(notification.type)"
              >
                <component :is="notification.icon" class="w-6 h-6 text-white" />
              </div>

              <!-- Content -->
              <div class="flex-1 min-w-0">
                <div class="flex items-start justify-between mb-2">
                  <h3 class="font-bold text-lg text-gray-900">{{ notification.title }}</h3>
                  <span v-if="!notification.read" class="w-3 h-3 bg-emerald-500 rounded-full flex-shrink-0 ml-2 mt-1"></span>
                </div>
                <p class="text-gray-600 mb-3">{{ notification.message }}</p>
                
                <div class="flex items-center justify-between">
                  <div class="flex items-center gap-4">
                    <span class="text-sm text-gray-500 flex items-center gap-1">
                      <Clock class="w-4 h-4" />
                      {{ notification.time }}
                    </span>
                    <span 
                      class="text-xs font-semibold px-2 py-1 rounded"
                      :class="getTypeClass(notification.type)"
                    >
                      {{ getTypeLabel(notification.type) }}
                    </span>
                  </div>

                  <!-- Actions -->
                  <div class="flex gap-2">
                    <button 
                      v-if="!notification.read"
                      @click="markAsRead(notification.id)"
                      class="px-3 py-1 text-sm bg-emerald-100 text-emerald-700 rounded-lg hover:bg-emerald-200 transition-all font-semibold"
                    >
                      Đánh dấu đã đọc
                    </button>
                    <button 
                      @click="deleteNotification(notification.id)"
                      class="px-3 py-1 text-sm bg-red-100 text-red-700 rounded-lg hover:bg-red-200 transition-all font-semibold flex items-center gap-1"
                    >
                      <Trash2 class="w-3 h-3" />
                      Xóa
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </transition-group>
    </div>

    <!-- Pagination (if needed in future) -->
    <div v-if="filteredNotifications.length > 0" class="mt-6 text-center text-sm text-gray-500">
      Hiển thị {{ filteredNotifications.length }} thông báo
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Bell, Clock, Trash2, CheckCheck, Dumbbell, Award, Target, Users, ShoppingBag, AlertCircle } from 'lucide-vue-next'
import { useToast } from 'vue-toastification'

const toast = useToast()

// Filter states
const activeFilter = ref('all')
const selectedTypes = ref([])

const filterTabs = [
  { label: 'Tất cả', value: 'all' },
  { label: 'Chưa đọc', value: 'unread' },
  { label: 'Đã đọc', value: 'read' }
]

const notificationTypes = [
  { label: 'Lớp học', value: 'class', icon: Dumbbell },
  { label: 'Hội viên', value: 'membership', icon: Award },
  { label: 'Thành tích', value: 'achievement', icon: Target },
  { label: 'Hệ thống', value: 'system', icon: Bell },
  { label: 'Khác', value: 'other', icon: AlertCircle }
]

// Mock notifications data (expanded)
const notifications = ref([
  {
    id: 1,
    type: 'class',
    title: 'Lớp Yoga sắp diễn ra',
    message: 'Lớp Yoga buổi sáng của bạn sẽ bắt đầu trong 30 phút. Vui lòng có mặt đúng giờ để không bỏ lỡ buổi khởi động.',
    time: '30 phút trước',
    date: new Date(Date.now() - 30 * 60 * 1000),
    read: false,
    icon: Dumbbell
  },
  {
    id: 2,
    type: 'membership',
    title: 'Gói hội viên sắp hết hạn',
    message: 'Gói Gold của bạn sẽ hết hạn sau 7 ngày. Gia hạn ngay để không bỏ lỡ các ưu đãi đặc biệt.',
    time: '2 giờ trước',
    date: new Date(Date.now() - 2 * 60 * 60 * 1000),
    read: false,
    icon: Award
  },
  {
    id: 3,
    type: 'achievement',
    title: 'Hoàn thành mục tiêu tháng',
    message: 'Chúc mừng! Bạn đã hoàn thành 20 buổi tập trong tháng này. Tiếp tục phát huy!',
    time: '1 ngày trước',
    date: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000),
    read: true,
    icon: Target
  },
  {
    id: 4,
    type: 'system',
    title: 'Cập nhật hệ thống',
    message: 'Chúng tôi đã cải thiện trải nghiệm đăng ký lớp học. Hãy khám phá ngay!',
    time: '2 ngày trước',
    date: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000),
    read: true,
    icon: Bell
  },
  {
    id: 5,
    type: 'class',
    title: 'Lớp học bị hủy',
    message: 'Lớp Boxing vào tối mai đã bị hủy do giáo viên có việc đột xuất. Chúng tôi sẽ thông báo lịch học bù.',
    time: '3 ngày trước',
    date: new Date(Date.now() - 3 * 24 * 60 * 60 * 1000),
    read: true,
    icon: Dumbbell
  },
  {
    id: 6,
    type: 'achievement',
    title: 'Kỷ lục mới!',
    message: 'Bạn đã đạt kỷ lục mới với 15 buổi tập liên tiếp. Thật tuyệt vời!',
    time: '4 ngày trước',
    date: new Date(Date.now() - 4 * 24 * 60 * 60 * 1000),
    read: true,
    icon: Target
  },
  {
    id: 7,
    type: 'membership',
    title: 'Ưu đãi đặc biệt',
    message: 'Nâng cấp lên gói VIP ngay hôm nay và nhận giảm giá 30%!',
    time: '5 ngày trước',
    date: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000),
    read: true,
    icon: Award
  },
  {
    id: 8,
    type: 'system',
    title: 'Bảo trì hệ thống',
    message: 'Hệ thống sẽ bảo trì từ 2:00 - 4:00 sáng ngày mai. Mong bạn thông cảm.',
    time: '6 ngày trước',
    date: new Date(Date.now() - 6 * 24 * 60 * 60 * 1000),
    read: true,
    icon: Bell
  }
])

// Computed
const unreadCount = computed(() => notifications.value.filter(n => !n.read).length)

const filteredNotifications = computed(() => {
  let result = [...notifications.value]

  // Filter by read status
  if (activeFilter.value === 'unread') {
    result = result.filter(n => !n.read)
  } else if (activeFilter.value === 'read') {
    result = result.filter(n => n.read)
  }

  // Filter by type
  if (selectedTypes.value.length > 0) {
    result = result.filter(n => selectedTypes.value.includes(n.type))
  }

  // Sort by date (newest first)
  return result.sort((a, b) => b.date - a.date)
})

// Methods
const toggleTypeFilter = (type) => {
  const index = selectedTypes.value.indexOf(type)
  if (index > -1) {
    selectedTypes.value.splice(index, 1)
  } else {
    selectedTypes.value.push(type)
  }
}

const markAsRead = (notificationId) => {
  const notification = notifications.value.find(n => n.id === notificationId)
  if (notification) {
    notification.read = true
    toast.success('Đã đánh dấu đã đọc')
  }
}

const markAllAsRead = () => {
  notifications.value.forEach(n => n.read = true)
  toast.success('Đã đánh dấu tất cả đã đọc')
}

const deleteNotification = (notificationId) => {
  const index = notifications.value.findIndex(n => n.id === notificationId)
  if (index > -1) {
    notifications.value.splice(index, 1)
    toast.success('Đã xóa thông báo')
  }
}

const clearAll = () => {
  if (confirm('Bạn có chắc muốn xóa tất cả thông báo?')) {
    notifications.value = []
    toast.success('Đã xóa tất cả thông báo')
  }
}

const getNotificationColor = (type) => {
  const colors = {
    class: 'bg-gradient-to-br from-emerald-500 to-teal-500',
    membership: 'bg-gradient-to-br from-amber-500 to-orange-500',
    achievement: 'bg-gradient-to-br from-purple-500 to-pink-500',
    system: 'bg-gradient-to-br from-blue-500 to-cyan-500',
    other: 'bg-gradient-to-br from-gray-500 to-gray-600'
  }
  return colors[type] || colors.other
}

const getTypeClass = (type) => {
  const classes = {
    class: 'bg-emerald-100 text-emerald-700',
    membership: 'bg-amber-100 text-amber-700',
    achievement: 'bg-purple-100 text-purple-700',
    system: 'bg-blue-100 text-blue-700',
    other: 'bg-gray-100 text-gray-700'
  }
  return classes[type] || classes.other
}

const getTypeLabel = (type) => {
  const labels = {
    class: 'Lớp học',
    membership: 'Hội viên',
    achievement: 'Thành tích',
    system: 'Hệ thống',
    other: 'Khác'
  }
  return labels[type] || labels.other
}
</script>

<style scoped>
.list-enter-active,
.list-leave-active {
  transition: all 0.3s ease;
}

.list-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.list-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>
