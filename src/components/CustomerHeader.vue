<script setup>
import { ref, computed } from "vue";
import { useRoute, RouterLink, useRouter } from "vue-router";
import { useAuthStore } from "@/stores/useAuthStore";
import { UserIcon, ChevronDown, Dumbbell, Users, Target, Award, Calendar, BookOpen, Ticket, Clock, Settings, LogOut, Bell } from "lucide-vue-next";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const isAccountOpen = ref(false);
const isWorkoutOpen = ref(false);
const isNotificationOpen = ref(false);

const openAccountMenu = () => (isAccountOpen.value = true);
const closeAccountMenu = () => (isAccountOpen.value = false);
const openWorkoutMenu = () => (isWorkoutOpen.value = true);
const closeWorkoutMenu = () => (isWorkoutOpen.value = false);
const openNotificationMenu = () => (isNotificationOpen.value = true);
const closeNotificationMenu = () => (isNotificationOpen.value = false);

// Mock notifications
const notifications = ref([
  {
    id: 1,
    type: 'class',
    title: 'Lớp Yoga sắp diễn ra',
    message: 'Lớp Yoga buổi sáng của bạn sẽ bắt đầu trong 30 phút',
    time: '30 phút trước',
    read: false,
    icon: Dumbbell
  },
  {
    id: 2,
    type: 'membership',
    title: 'Gói hội viên sắp hết hạn',
    message: 'Gói Gold của bạn sẽ hết hạn sau 7 ngày',
    time: '2 giờ trước',
    read: false,
    icon: Award
  },
  {
    id: 3,
    type: 'achievement',
    title: 'Hoàn thành mục tiêu',
    message: 'Chúc mừng! Bạn đã hoàn thành 20 buổi tập trong tháng',
    time: '1 ngày trước',
    read: true,
    icon: Target
  },
  {
    id: 4,
    type: 'system',
    title: 'Cập nhật hệ thống',
    message: 'Chúng tôi đã cải thiện trải nghiệm đăng ký lớp học',
    time: '2 ngày trước',
    read: true,
    icon: Bell
  }
]);

const unreadCount = computed(() => notifications.value.filter(n => !n.read).length);

const markAsRead = (notificationId) => {
  const notification = notifications.value.find(n => n.id === notificationId);
  if (notification) {
    notification.read = true;
  }
};

const markAllAsRead = () => {
  notifications.value.forEach(n => n.read = true);
};

const handleLogout = () => {
  authStore.logout(); // Xóa thông tin user + token khỏi Pinia/localStorage
  router.push("/login");
};
</script>

<template>
  <header class="w-full bg-gradient-to-r from-emerald-600 via-teal-600 to-cyan-600 shadow-lg relative z-50">
    <div class="absolute inset-0 bg-gradient-to-r from-emerald-500/20 via-transparent to-cyan-500/20"></div>
    <div class="mx-auto max-w-7xl px-6 py-4 flex items-center justify-between relative z-10">
      <!-- Logo -->
      <div class="flex items-center gap-3">
        <RouterLink to="/customer" class="inline-flex items-center gap-2 hover:opacity-90 transition">
          <div class="h-8 w-8 rounded-full bg-gradient-to-br from-yellow-400 via-orange-500 to-pink-500 flex items-center justify-center shadow-lg">
            <Dumbbell class="h-5 w-5 text-white" />
          </div>
          <span class="font-bold text-white tracking-wider uppercase">
            Quản lý Gym
          </span>
        </RouterLink>
      </div>

      <!-- Navigation -->
      <nav class="flex items-center gap-8 text-sm font-medium">


        <RouterLink
          to="/customer/membership"
          class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:text-yellow-300 transition-colors duration-300 rounded-md"
        >
          <Award class="h-4 w-4" />
          Hội viên
        </RouterLink>

        <RouterLink
          to="/customer/class"
          class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:text-orange-300 transition-colors duration-300 rounded-md"
        >
          <Dumbbell class="h-4 w-4" />
          Lớp học
        </RouterLink>

        <RouterLink
          to="/customer/calendar"
          class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:text-pink-300 transition-colors duration-300 rounded-md"
        >
          <Calendar class="h-4 w-4" />
          Lịch
        </RouterLink>

        <!-- Workout Dropdown -->
        <div
          class="relative"
          @mouseenter="openWorkoutMenu"
          @mouseleave="closeWorkoutMenu"
        >
          <button
            class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:text-cyan-300 transition-colors duration-300 rounded-md"
          >
            <Dumbbell class="h-4 w-4" />
            <span>Tập luyện</span>
            <ChevronDown class="w-4 h-4" />
          </button>

          <transition name="fade">
            <div
              v-if="isWorkoutOpen"
              class="absolute left-0 mt-2 w-56 bg-white rounded-md shadow-lg border border-gray-200 z-50"
            >
              <RouterLink
                to="/customer/workout/exercises"
                class="flex items-center gap-2 px-4 py-2 text-gray-800 hover:bg-gray-100 transition"
              >
                <Dumbbell class="w-4 h-4" />
                Bài Tập
              </RouterLink>
              <RouterLink
                to="/customer/workout/routines"
                class="flex items-center gap-2 px-4 py-2 text-gray-800 hover:bg-gray-100 transition"
              >
                <Users class="w-4 h-4" />
                Cộng Đồng
              </RouterLink>
              <RouterLink
                to="/customer/workout/training-plans"
                class="flex items-center gap-2 px-4 py-2 text-gray-800 hover:bg-gray-100 transition"
              >
                <Target class="w-4 h-4" />
                Lịch Tập Của Tôi
              </RouterLink>
            </div>
          </transition>
        </div>


        <!-- Account dropdown -->
        
      </nav>
      
      <!-- Notification Bell -->
      <div
        class="relative mr-4"
        @mouseenter="openNotificationMenu"
        @mouseleave="closeNotificationMenu"
      >
        <button
          class="relative flex items-center gap-2 px-3 py-2 text-white hover:text-yellow-300 transition-colors duration-300 rounded-md"
        >
          <Bell class="w-5 h-5" />
          <span v-if="unreadCount > 0" class="absolute -top-1 -right-1 w-5 h-5 bg-red-500 text-white text-xs font-bold rounded-full flex items-center justify-center">
            {{ unreadCount }}
          </span>
        </button>

        <transition name="fade">
          <div
            v-if="isNotificationOpen"
            class="absolute right-0 mt-2 w-80 bg-white rounded-lg shadow-xl border border-gray-200 z-50 max-h-96 overflow-hidden flex flex-col"
          >
            <!-- Header -->
            <div class="flex items-center justify-between p-4 border-b border-gray-200">
              <h3 class="font-bold text-gray-800">Thông báo</h3>
              <button 
                v-if="unreadCount > 0"
                @click="markAllAsRead"
                class="text-xs text-emerald-600 hover:text-emerald-700 font-semibold"
              >
                Đánh dấu đã đọc
              </button>
            </div>
            
            <!-- Notifications List -->
            <div class="overflow-y-auto flex-1">
              <div 
                v-for="notification in notifications" 
                :key="notification.id"
                @click="markAsRead(notification.id)"
                class="p-4 border-b border-gray-100 hover:bg-emerald-50 cursor-pointer transition-colors"
                :class="!notification.read ? 'bg-blue-50' : ''"
              >
                <div class="flex items-start gap-3">
                  <div 
                    class="w-10 h-10 rounded-full flex items-center justify-center flex-shrink-0"
                    :class="!notification.read ? 'bg-emerald-100' : 'bg-gray-100'"
                  >
                    <component 
                      :is="notification.icon" 
                      class="w-5 h-5"
                      :class="!notification.read ? 'text-emerald-600' : 'text-gray-600'"
                    />
                  </div>
                  <div class="flex-1 min-w-0">
                    <div class="flex items-start justify-between">
                      <h4 class="font-semibold text-sm text-gray-900">{{ notification.title }}</h4>
                      <span v-if="!notification.read" class="w-2 h-2 bg-blue-500 rounded-full flex-shrink-0 ml-2 mt-1"></span>
                    </div>
                    <p class="text-xs text-gray-600 mt-1">{{ notification.message }}</p>
                    <p class="text-xs text-gray-400 mt-1">{{ notification.time }}</p>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- Footer -->
            <div class="p-3 border-t border-gray-200 bg-gray-50">
              <RouterLink 
                to="/customer/notifications" 
                class="text-sm text-emerald-600 hover:text-emerald-700 font-semibold text-center block"
              >
                Xem tất cả thông báo
              </RouterLink>
            </div>
          </div>
        </transition>
      </div>
      
      <!-- Account dropdown -->
      <div
        class="relative"
        @mouseenter="openAccountMenu"
        @mouseleave="closeAccountMenu"
      >
        <button
          class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:text-cyan-300 transition-colors duration-300 rounded-md"
        >
          <UserIcon class="w-5 h-5" />
          <span>{{ authStore.user?.fullName || 'Tài khoản' }}</span>
        </button>

        <transition name="fade">
          <div
            v-if="isAccountOpen"
            class="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg border border-gray-200 z-50"
          >
            <RouterLink
              to="/profile"
              class="flex items-center gap-2 px-4 py-2 text-gray-800 hover:bg-gray-100"
            >
              <UserIcon class="w-4 h-4" />
              Hồ sơ
            </RouterLink>
            <RouterLink
              to="/change-password"
              class="flex items-center gap-2 px-4 py-2 text-gray-800 hover:bg-gray-100"
            >
              <Settings class="w-4 h-4" />
              Đổi mật khẩu
            </RouterLink>
            <button
              class="flex items-center gap-2 w-full text-left px-4 py-2 text-gray-800 hover:bg-gray-100"
              @click="handleLogout"
            >
              <LogOut class="w-4 h-4" />
              Đăng xuất
            </button>
          </div>
        </transition>
      </div>
    </div>
  </header>
</template>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
