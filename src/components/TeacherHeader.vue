<script setup>
import { ref } from "vue";
import { useRoute, useRouter, RouterLink } from "vue-router";
import { useAuthStore } from "@/stores/useAuthStore";
import { useToast } from "vue-toastification";
import { Dumbbell, UserIcon, Settings, LogOut, Clock, Menu, X } from "lucide-vue-next"; // Thêm Menu, X icon

const route = useRoute();
const isMenuOpen = ref(false); // State cho Mobile Menu
const isAccountOpen = ref(false); // State cho Desktop Dropdown
const authStore = useAuthStore();
const router = useRouter();
const toast = useToast();

const openAccountMenu = () => (isAccountOpen.value = true);
const closeAccountMenu = () => (isAccountOpen.value = false);

const handleLogout = () => {
  authStore.logout();
  toast.success("Đăng xuất thành công!");
  router.push("/login");
  isMenuOpen.value = false; // Đóng menu mobile nếu đang mở
};

const handleMobileNavigate = (path) => {
  isMenuOpen.value = false;
  router.push(path);
  console.log("Navigating to:", path);
};
</script>

<template>
  <header class="w-full bg-gradient-to-r from-emerald-600 via-teal-600 to-cyan-600 shadow-lg relative z-50">
    
    <div class="absolute inset-0 bg-gradient-to-r from-emerald-500/20 via-transparent to-cyan-500/20 pointer-events-none"></div>

    <div class="mx-auto max-w-7xl px-6 py-4 flex items-center justify-between relative z-10">
      <div class="flex items-center gap-3">
        <RouterLink to="/teacher" class="inline-flex items-center gap-2 hover:opacity-90 transition">
          <div class="h-8 w-8 rounded-full bg-gradient-to-br from-yellow-400 via-orange-500 to-pink-500 flex items-center justify-center shadow-lg">
            <Dumbbell class="h-5 w-5 text-white" />
          </div>
          <span class="font-bold text-white tracking-wider uppercase">Quản lý Gym</span>
        </RouterLink>
      </div>

      <nav class="hidden md:flex items-center gap-8 text-sm font-medium">
        <RouterLink to="/teacher/registerclass" class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:text-yellow-300 transition-colors duration-300 rounded-md">
          Đăng ký dạy
        </RouterLink>
        <RouterLink to="/teacher/calendar" class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:text-orange-300 transition-colors duration-300 rounded-md">
          Lịch trình
        </RouterLink>
        <RouterLink to="/teacher/attendance" class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:text-pink-300 transition-colors duration-300 rounded-md">
          Điểm danh
        </RouterLink>
      </nav>

      <div class="relative hidden md:block" @mouseenter="openAccountMenu" @mouseleave="closeAccountMenu">
        <button class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:text-cyan-300 transition-colors duration-300 rounded-md">
          <UserIcon class="w-5 h-5" />
          <span>{{ authStore.user?.fullName || 'Tài khoản' }}</span>
        </button>

        <transition name="fade">
          <div v-if="isAccountOpen" class="absolute right-0 mt-2 w-56 bg-white rounded-md shadow-lg border border-gray-200 z-50 overflow-hidden">
            <RouterLink to="/profile" class="flex items-center gap-2 px-4 py-3 text-gray-700 hover:bg-emerald-50 hover:text-emerald-600 transition">
              <UserIcon class="w-4 h-4" /> Hồ sơ cá nhân
            </RouterLink>
            <RouterLink to="/teacher/attendance-history" class="flex items-center gap-2 px-4 py-3 text-gray-700 hover:bg-emerald-50 hover:text-emerald-600 transition">
              <Clock class="w-4 h-4" /> Lịch sử điểm danh
            </RouterLink>
            <RouterLink to="/change-password" class="flex items-center gap-2 px-4 py-3 text-gray-700 hover:bg-emerald-50 hover:text-emerald-600 transition">
              <Settings class="w-4 h-4" /> Thay đổi mật khẩu
            </RouterLink>
            <div class="border-t border-gray-100"></div>
            <button @click="handleLogout" class="flex items-center gap-2 w-full text-left px-4 py-3 text-red-600 hover:bg-red-50 transition">
              <LogOut class="w-4 h-4" /> Đăng xuất
            </button>
          </div>
        </transition>
      </div>

      <button class="md:hidden text-white focus:outline-none p-1 rounded hover:bg-white/10" @click="isMenuOpen = !isMenuOpen">
        <X v-if="isMenuOpen" class="h-6 w-6" />
        <Menu v-else class="h-6 w-6" />
      </button>
    </div>

    <transition name="slide-fade">
      <div v-if="isMenuOpen" class="md:hidden bg-teal-800 text-white border-t border-teal-700 shadow-inner relative z-20">
        <nav class="flex flex-col p-4 space-y-2">

          <a @click.prevent="handleMobileNavigate('/teacher/registerclass')" class="block px-3 py-2 rounded hover:bg-teal-700 hover:text-yellow-300 transition cursor-pointer select-none">
            Đăng ký dạy
          </a>

          <a @click.prevent="handleMobileNavigate('/teacher/calendar')" class="block px-3 py-2 rounded hover:bg-teal-700 hover:text-orange-300 transition cursor-pointer select-none">
            Lịch trình
          </a>

          <a @click.prevent="handleMobileNavigate('/teacher/attendance')" class="block px-3 py-2 rounded hover:bg-teal-700 hover:text-pink-300 transition cursor-pointer select-none">
            Điểm danh
          </a>

          <hr class="border-teal-600 my-2 opacity-50" />

          <div class="px-3 py-2 text-xs font-semibold text-teal-300 uppercase">Tài khoản</div>

          <a @click.prevent="handleMobileNavigate('/profile')" class="flex items-center gap-2 px-3 py-2 rounded hover:bg-teal-700 transition cursor-pointer select-none">
            <UserIcon class="w-4 h-4" /> Hồ sơ cá nhân
          </a>

          <a @click.prevent="handleMobileNavigate('/teacher/attendance-history')" class="flex items-center gap-2 px-3 py-2 rounded hover:bg-teal-700 transition cursor-pointer select-none">
            <Clock class="w-4 h-4" /> Lịch sử điểm danh
          </a>

          <a @click.prevent="handleMobileNavigate('/change-password')" class="flex items-center gap-2 px-3 py-2 rounded hover:bg-teal-700 transition cursor-pointer select-none">
            <Settings class="w-4 h-4" /> Đổi mật khẩu
          </a>

          <button @click="handleLogout" class="flex items-center gap-2 w-full text-left px-3 py-2 rounded text-red-300 hover:bg-red-900/30 transition select-none">
            <LogOut class="w-4 h-4" /> Đăng xuất
          </button>

        </nav>
      </div>
    </transition>
  </header>
</template>

<style scoped>
/* Animation cho Dropdown Desktop */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-5px);
}

/* Animation cho Mobile Menu - Trượt nhẹ xuống */
.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all 0.3s ease-out;
  max-height: 500px;
  /* Ước lượng chiều cao tối đa */
  opacity: 1;
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  max-height: 0;
  opacity: 0;
  overflow: hidden;
}
</style>