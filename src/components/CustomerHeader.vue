<script setup>
import { ref, computed } from "vue";
import { useRoute, useRouter, RouterLink } from "vue-router";
import { useAuthStore } from "@/stores/useAuthStore";
import { 
  UserIcon, ChevronDown, Dumbbell, Users, Target, Award, 
  Calendar, BookOpen, Ticket, Clock, Settings, LogOut, Bell, 
  Menu, X, // Import thêm icon Menu và X
  DollarSign
} from "lucide-vue-next";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

// State
const isAccountOpen = ref(false);
const isWorkoutOpen = ref(false);
const isNotificationOpen = ref(false);
const isMobileMenuOpen = ref(false); // State cho Mobile Menu

// Actions
const openAccountMenu = () => (isAccountOpen.value = true);
const closeAccountMenu = () => (isAccountOpen.value = false);
const openWorkoutMenu = () => (isWorkoutOpen.value = true);
const closeWorkoutMenu = () => (isWorkoutOpen.value = false);
const openNotificationMenu = () => (isNotificationOpen.value = true);
const closeNotificationMenu = () => (isNotificationOpen.value = false);

// Mock notifications (Giữ nguyên)
const notifications = ref([
  { id: 1, type: 'class', title: 'Lớp Yoga sắp diễn ra', message: 'Lớp Yoga buổi sáng của bạn sẽ bắt đầu trong 30 phút', time: '30 phút trước', read: false, icon: Dumbbell },
  { id: 2, type: 'membership', title: 'Gói hội viên sắp hết hạn', message: 'Gói Gold của bạn sẽ hết hạn sau 7 ngày', time: '2 giờ trước', read: false, icon: Award },
  { id: 3, type: 'achievement', title: 'Hoàn thành mục tiêu', message: 'Chúc mừng! Bạn đã hoàn thành 20 buổi tập trong tháng', time: '1 ngày trước', read: true, icon: Target },
  { id: 4, type: 'system', title: 'Cập nhật hệ thống', message: 'Chúng tôi đã cải thiện trải nghiệm đăng ký lớp học', time: '2 ngày trước', read: true, icon: Bell }
]);

const unreadCount = computed(() => notifications.value.filter(n => !n.read).length);

const markAsRead = (notificationId) => {
  const notification = notifications.value.find(n => n.id === notificationId);
  if (notification) notification.read = true;
};

const markAllAsRead = () => {
  notifications.value.forEach(n => n.read = true);
};

// Logout xử lý chung
const handleLogout = () => {
  authStore.logout();
  isMobileMenuOpen.value = false; // Đóng mobile menu
  router.push("/login");
};

// Hàm điều hướng an toàn cho Mobile (như bài trước)
const handleMobileNavigate = (path) => {
  isMobileMenuOpen.value = false;
  router.push(path);
};
</script>

<template>
  <header class="w-full bg-gradient-to-r from-emerald-600 via-teal-600 to-cyan-600 shadow-lg relative z-50">
    <div class="absolute inset-0 bg-gradient-to-r from-emerald-500/20 via-transparent to-cyan-500/20 pointer-events-none"></div>
    
    <div class="mx-auto max-w-7xl px-4 sm:px-6 py-3 sm:py-4 flex items-center justify-between relative z-10">
      
      <div class="flex items-center gap-2 sm:gap-3">
        <RouterLink to="/customer" class="inline-flex items-center gap-2 hover:opacity-90 transition">
          <div class="h-7 w-7 sm:h-8 sm:w-8 rounded-full bg-gradient-to-br from-yellow-400 via-orange-500 to-pink-500 flex items-center justify-center shadow-lg">
            <Dumbbell class="h-4 w-4 sm:h-5 sm:w-5 text-white" />
          </div>
          <span class="font-bold text-sm sm:text-base text-white tracking-wider uppercase">
            Quản lý Gym
          </span>
        </RouterLink>
      </div>

      <nav class="hidden md:flex items-center gap-4 lg:gap-8 text-sm font-medium">
        <RouterLink to="/customer/membership" class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:text-yellow-300 transition-colors duration-300 rounded-md">
          <Award class="h-4 w-4" /> Hội viên
        </RouterLink>

        <RouterLink to="/customer/class" class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:text-orange-300 transition-colors duration-300 rounded-md">
          <Dumbbell class="h-4 w-4" /> Lớp học
        </RouterLink>

        <RouterLink to="/customer/billhistory" class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:text-pink-300 transition-colors duration-300 rounded-md">
          <DollarSign class="h-4 w-4" /> Hoá đơn
        </RouterLink>

        <RouterLink to="/customer/calendar" class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:text-pink-300 transition-colors duration-300 rounded-md">
          <Calendar class="h-4 w-4" /> Lịch
        </RouterLink>

        <div class="relative" @mouseenter="openWorkoutMenu" @mouseleave="closeWorkoutMenu">
          <button class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:text-cyan-300 transition-colors duration-300 rounded-md">
            <Dumbbell class="h-4 w-4" />
            <span>Tập luyện</span>
            <ChevronDown class="w-4 h-4" />
          </button>
          <transition name="fade">
            <div v-if="isWorkoutOpen" class="absolute left-0 mt-2 w-56 bg-white rounded-md shadow-lg border border-gray-200 z-50">
              <RouterLink to="/customer/workout/exercises" class="flex items-center gap-2 px-4 py-2 text-gray-800 hover:bg-gray-100 transition">
                <Dumbbell class="w-4 h-4" /> Bài Tập
              </RouterLink>
              <RouterLink to="/customer/workout/routines" class="flex items-center gap-2 px-4 py-2 text-gray-800 hover:bg-gray-100 transition">
                <Users class="w-4 h-4" /> Cộng Đồng
              </RouterLink>
              <RouterLink to="/customer/workout/training-plans" class="flex items-center gap-2 px-4 py-2 text-gray-800 hover:bg-gray-100 transition">
                <Target class="w-4 h-4" /> Lịch Tập Của Tôi
              </RouterLink>
            </div>
          </transition>
        </div>
      </nav>
      
      <div class="flex items-center gap-2 sm:gap-4">
        
        <div class="relative" @mouseenter="openNotificationMenu" @mouseleave="closeNotificationMenu">
          <button class="relative flex items-center gap-2 px-2 py-2 text-white hover:text-yellow-300 transition-colors duration-300 rounded-md">
            <Bell class="w-5 h-5" />
            <span v-if="unreadCount > 0" class="absolute top-1 right-1 w-2.5 h-2.5 bg-red-500 rounded-full border-2 border-teal-600"></span>
          </button>

          <transition name="fade">
            <div v-if="isNotificationOpen" class="absolute right-0 mt-2 w-80 bg-white rounded-lg shadow-xl border border-gray-200 z-50 max-h-96 overflow-hidden flex flex-col">
              <div class="flex items-center justify-between p-4 border-b border-gray-200">
                <h3 class="font-bold text-gray-800">Thông báo</h3>
                <button v-if="unreadCount > 0" @click="markAllAsRead" class="text-xs text-emerald-600 hover:text-emerald-700 font-semibold">Đánh dấu đã đọc</button>
              </div>
              <div class="overflow-y-auto flex-1">
                <div v-for="notification in notifications" :key="notification.id" @click="markAsRead(notification.id)" class="p-4 border-b border-gray-100 hover:bg-emerald-50 cursor-pointer transition-colors" :class="!notification.read ? 'bg-blue-50' : ''">
                  <div class="flex items-start gap-3">
                    <div class="w-10 h-10 rounded-full flex items-center justify-center flex-shrink-0" :class="!notification.read ? 'bg-emerald-100' : 'bg-gray-100'">
                      <component :is="notification.icon" class="w-5 h-5" :class="!notification.read ? 'text-emerald-600' : 'text-gray-600'" />
                    </div>
                    <div class="flex-1 min-w-0">
                      <p class="font-semibold text-sm text-gray-900">{{ notification.title }}</p>
                      <p class="text-xs text-gray-600 mt-1">{{ notification.message }}</p>
                    </div>
                  </div>
                </div>
              </div>
              <div class="p-3 border-t border-gray-200 bg-gray-50 text-center">
                 <RouterLink to="/customer/notifications" class="text-sm text-emerald-600 font-semibold">Xem tất cả</RouterLink>
              </div>
            </div>
          </transition>
        </div>

        <div class="relative hidden md:block" @mouseenter="openAccountMenu" @mouseleave="closeAccountMenu">
          <button class="flex items-center gap-1 px-2 py-2 text-white hover:text-cyan-300 transition-colors rounded-md text-sm">
            <UserIcon class="w-5 h-5" />
            <span>{{ authStore.user?.fullName || 'Tài khoản' }}</span>
          </button>
          <transition name="fade">
            <div v-if="isAccountOpen" class="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg border border-gray-200 z-50">
              <RouterLink to="/profile" class="flex items-center gap-2 px-4 py-2 text-gray-800 hover:bg-gray-100"><UserIcon class="w-4 h-4"/> Hồ sơ</RouterLink>
              <RouterLink to="/change-password" class="flex items-center gap-2 px-4 py-2 text-gray-800 hover:bg-gray-100"><Settings class="w-4 h-4"/> Đổi mật khẩu</RouterLink>
              <button @click="handleLogout" class="flex items-center gap-2 w-full text-left px-4 py-2 text-gray-800 hover:bg-gray-100"><LogOut class="w-4 h-4"/> Đăng xuất</button>
            </div>
          </transition>
        </div>

        <button 
          class="md:hidden text-white focus:outline-none p-1 rounded hover:bg-white/10" 
          @click="isMobileMenuOpen = !isMobileMenuOpen"
        >
          <X v-if="isMobileMenuOpen" class="h-6 w-6" />
          <Menu v-else class="h-6 w-6" />
        </button>
      </div>
    </div>

    <transition name="slide-fade">
      <div v-if="isMobileMenuOpen" class="md:hidden bg-teal-800 text-white border-t border-teal-700 shadow-inner relative z-20">
        <nav class="flex flex-col p-4 space-y-2 max-h-[80vh] overflow-y-auto">
          
          <a @click.prevent="handleMobileNavigate('/customer/membership')" class="flex items-center gap-2 px-3 py-2 rounded hover:bg-teal-700 hover:text-yellow-300 transition cursor-pointer">
            <Award class="w-4 h-4" /> Hội viên
          </a>
          <a @click.prevent="handleMobileNavigate('/customer/class')" class="flex items-center gap-2 px-3 py-2 rounded hover:bg-teal-700 hover:text-orange-300 transition cursor-pointer">
            <Dumbbell class="w-4 h-4" /> Lớp học
          </a>
          <a @click.prevent="handleMobileNavigate('/customer/calendar')" class="flex items-center gap-2 px-3 py-2 rounded hover:bg-teal-700 hover:text-pink-300 transition cursor-pointer">
            <Calendar class="w-4 h-4" /> Lịch
          </a>

          <div class="px-3 py-2 text-xs font-semibold text-teal-300 uppercase mt-2">Tập luyện</div>
          <div class="pl-4 space-y-1 border-l-2 border-teal-700 ml-2">
            <a @click.prevent="handleMobileNavigate('/customer/workout/exercises')" class="block px-3 py-2 rounded hover:bg-teal-700 transition cursor-pointer text-sm">Bài Tập</a>
            <a @click.prevent="handleMobileNavigate('/customer/workout/routines')" class="block px-3 py-2 rounded hover:bg-teal-700 transition cursor-pointer text-sm">Cộng Đồng</a>
            <a @click.prevent="handleMobileNavigate('/customer/workout/training-plans')" class="block px-3 py-2 rounded hover:bg-teal-700 transition cursor-pointer text-sm">Lịch Tập Của Tôi</a>
          </div>

          <div class="px-3 py-2 text-xs font-semibold text-teal-300 uppercase mt-2">Tài khoản</div>
          <a @click.prevent="handleMobileNavigate('/profile')" class="flex items-center gap-2 px-3 py-2 rounded hover:bg-teal-700 transition cursor-pointer">
            <UserIcon class="w-4 h-4" /> Hồ sơ cá nhân
          </a>
          <a @click.prevent="handleMobileNavigate('/change-password')" class="flex items-center gap-2 px-3 py-2 rounded hover:bg-teal-700 transition cursor-pointer">
            <Settings class="w-4 h-4" /> Đổi mật khẩu
          </a>
          <button @click="handleLogout" class="flex items-center gap-2 w-full text-left px-3 py-2 rounded text-red-300 hover:bg-red-900/30 transition">
            <LogOut class="w-4 h-4" /> Đăng xuất
          </button>

        </nav>
      </div>
    </transition>
  </header>
</template>

<style scoped>
/* Animation */
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

.slide-fade-enter-active, .slide-fade-leave-active { transition: all 0.3s ease-out; max-height: 100vh; opacity: 1; }
.slide-fade-enter-from, .slide-fade-leave-to { max-height: 0; opacity: 0; overflow: hidden; }
</style>