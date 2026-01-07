<script setup>
import { ref } from "vue";
import { useRoute, useRouter, RouterLink } from "vue-router";
import { useAuthStore } from "@/stores/useAuthStore";
import { useToast } from "vue-toastification";
import {
  ChevronDown,
  Dumbbell,
  Users,
  Calendar,
  Clock,
  UserIcon,
  Settings,
  LogOut,
  Menu, // Icon Menu hamburger
  X,    // Icon đóng menu
} from "lucide-vue-next";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const toast = useToast();

// State quản lý menu
const isMenuOpen = ref(false);
const isAccountOpen = ref(false);
const isWorkoutOpen = ref(false); // Dùng chung cho cả desktop và mobile

// Actions
const openAccountMenu = () => (isAccountOpen.value = true);
const closeAccountMenu = () => (isAccountOpen.value = false);
const openWorkoutMenu = () => (isWorkoutOpen.value = true);
const closeWorkoutMenu = () => (isWorkoutOpen.value = false);

// Toggle cho mobile (click là mở/đóng)
const toggleMobileWorkout = () => {
  isWorkoutOpen.value = !isWorkoutOpen.value;
};

// Hàm đóng tất cả menu khi chuyển trang
const closeAllMenus = () => {
  isMenuOpen.value = false;
  isWorkoutOpen.value = false;
  isAccountOpen.value = false;
};

const handleLogout = () => {
  authStore.logout();
  toast.success("Đăng xuất thành công!");
  closeAllMenus();
  router.push("/login");
};
</script>

<template>
  <header
    class="w-full bg-gradient-to-r from-emerald-600 via-teal-600 to-cyan-600 shadow-lg relative z-50"
  >
    <div
      class="absolute inset-0 bg-gradient-to-r from-emerald-500/20 via-transparent to-cyan-500/20 pointer-events-none"
    ></div>

    <div
      class="mx-auto max-w-7xl px-6 py-4 flex items-center justify-between relative z-10"
    >
      <div class="flex items-center gap-3">
        <RouterLink
          to="/pt"
          class="inline-flex items-center gap-2 hover:opacity-90 transition"
          @click="closeAllMenus"
        >
          <div
            class="h-8 w-8 rounded-full bg-gradient-to-br from-yellow-400 via-orange-500 to-pink-500 flex items-center justify-center shadow-lg"
          >
            <Dumbbell class="h-5 w-5 text-white" />
          </div>
          <span class="font-bold text-white tracking-wider uppercase"
            >Quản lý Gym</span
          >
        </RouterLink>
      </div>

      <nav class="hidden md:flex items-center gap-8 text-sm font-medium">
        <RouterLink
          to="/pt/schedule"
          class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:text-yellow-300 transition-colors duration-300 rounded-md"
        >
          <Calendar class="h-4 w-4" />
          Lịch trình
        </RouterLink>

        <div
          class="relative"
          @mouseenter="openWorkoutMenu"
          @mouseleave="closeWorkoutMenu"
        >
          <button
            class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:text-orange-300 transition-colors duration-300 rounded-md"
          >
            <Dumbbell class="h-4 w-4" />
            <span>Bài tập</span>
            <ChevronDown class="w-4 h-4" />
          </button>

          <transition name="fade">
            <div
              v-if="isWorkoutOpen"
              class="absolute left-0 mt-0 w-56 bg-white rounded-md shadow-xl border border-gray-200 z-50 overflow-hidden"
            >
              <RouterLink
                to="/pt/workout/exercises"
                class="flex items-center gap-2 px-4 py-3 text-gray-700 hover:bg-emerald-50 hover:text-emerald-700 transition"
                @click="closeWorkoutMenu"
              >
                <Dumbbell class="w-4 h-4" />
                Bài tập
              </RouterLink>
              <RouterLink
                to="/pt/workout/routines"
                class="flex items-center gap-2 px-4 py-3 text-gray-700 hover:bg-emerald-50 hover:text-emerald-700 transition"
                @click="closeWorkoutMenu"
              >
                <Users class="w-4 h-4" />
                Cộng đồng
              </RouterLink>
            </div>
          </transition>
        </div>

        <RouterLink
          to="/pt/sessions"
          class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:text-pink-300 transition-colors duration-300 rounded-md"
        >
          <Clock class="h-4 w-4" />
          Buổi tập
        </RouterLink>
      </nav>

      <div
        class="relative hidden md:block"
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
            class="absolute right-0 mt-0 w-56 bg-white rounded-md shadow-xl border border-gray-200 z-50 overflow-hidden"
          >
            <RouterLink
              to="/profile"
              class="flex items-center gap-2 px-4 py-3 text-gray-700 hover:bg-emerald-50 hover:text-emerald-700 transition border-b border-gray-100"
              @click="closeAccountMenu"
            >
              <UserIcon class="w-4 h-4" />
              Hồ sơ cá nhân
            </RouterLink>
            <RouterLink
              to="/pt/attendance-history"
              class="flex items-center gap-2 px-4 py-3 text-gray-700 hover:bg-emerald-50 hover:text-emerald-700 transition"
              @click="closeAccountMenu"
            >
              <Clock class="w-4 h-4" />
              Lịch sử chấm công
            </RouterLink>
            <RouterLink
              to="/pt/setting"
              class="flex items-center gap-2 px-4 py-3 text-gray-700 hover:bg-emerald-50 hover:text-emerald-700 transition"
              @click="closeAccountMenu"
            >
              <Settings class="w-4 h-4" />
              Cài đặt
            </RouterLink>
            <button
              @click="handleLogout"
              class="flex items-center gap-2 w-full text-left px-4 py-3 text-red-600 hover:bg-red-50 transition border-t border-gray-100"
            >
              <LogOut class="w-4 h-4" />
              Đăng xuất
            </button>
          </div>
        </transition>
      </div>

      <button
        class="md:hidden text-white p-2 hover:bg-white/10 rounded-md transition focus:outline-none"
        @click="isMenuOpen = !isMenuOpen"
      >
        <Menu v-if="!isMenuOpen" class="h-6 w-6" />
        <X v-else class="h-6 w-6" />
      </button>
    </div>

    <transition name="expand">
      <div
        v-if="isMenuOpen"
        class="md:hidden bg-emerald-800 border-t border-emerald-700 overflow-hidden"
      >
        <nav class="flex flex-col px-4 py-4 space-y-2">
          <RouterLink
            to="/pt/schedule"
            class="flex items-center gap-3 px-4 py-3 text-white uppercase tracking-wider hover:bg-emerald-700 rounded-md transition"
            @click="closeAllMenus"
          >
            <Calendar class="h-5 w-5" />
            Lịch trình
          </RouterLink>

          <div class="rounded-md overflow-hidden">
            <button
              class="flex items-center justify-between w-full px-4 py-3 text-white uppercase tracking-wider hover:bg-emerald-700 hover:text-orange-300 transition rounded-md"
              @click.stop="toggleMobileWorkout"
            >
              <div class="flex items-center gap-3">
                <Dumbbell class="h-5 w-5" />
                <span>Bài tập</span>
              </div>
              <ChevronDown 
                class="w-5 h-5 transition-transform duration-300" 
                :class="{ 'rotate-180': isWorkoutOpen }"
              />
            </button>
            
            <transition name="expand">
              <div v-if="isWorkoutOpen" class="bg-emerald-900/50 rounded-b-md">
                <RouterLink
                  to="/pt/workout/exercises"
                  class="flex items-center gap-3 px-8 py-3 text-emerald-100 hover:text-white hover:bg-emerald-700/50 transition border-l-4 border-transparent hover:border-orange-400"
                  @click="closeAllMenus"
                >
                  Bài tập
                </RouterLink>
                <RouterLink
                  to="/pt/workout/routines"
                  class="flex items-center gap-3 px-8 py-3 text-emerald-100 hover:text-white hover:bg-emerald-700/50 transition border-l-4 border-transparent hover:border-orange-400"
                  @click="closeAllMenus"
                >
                  Cộng đồng
                </RouterLink>
              </div>
            </transition>
          </div>

          <RouterLink
            to="/pt/sessions"
            class="flex items-center gap-3 px-4 py-3 text-white uppercase tracking-wider hover:bg-emerald-700 rounded-md transition"
            @click="closeAllMenus"
          >
            <Clock class="h-5 w-5" />
            Buổi tập
          </RouterLink>

          <div class="border-t border-emerald-700 my-2 pt-2">
             <div class="px-4 py-2 text-emerald-200 text-xs uppercase font-bold">Tài khoản</div>
             <RouterLink
              to="/profile"
              class="flex items-center gap-3 px-4 py-3 text-white hover:bg-emerald-700 rounded-md transition"
              @click="closeAllMenus"
            >
              <UserIcon class="w-5 h-5" />
              Hồ sơ cá nhân
            </RouterLink>
            <button
              @click="handleLogout"
              class="flex items-center gap-3 w-full text-left px-4 py-3 text-red-200 hover:bg-red-900/30 hover:text-red-100 rounded-md transition"
            >
              <LogOut class="w-5 h-5" />
              Đăng xuất
            </button>
          </div>
        </nav>
      </div>
    </transition>
  </header>
</template>

<style scoped>
/* Fade effect cho Dropdown Desktop */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

/* Expand effect cho Mobile Menu 
  Thay thế translate bằng max-height để không bị lỗi click ảo 
*/
.expand-enter-active,
.expand-leave-active {
  transition: all 0.3s ease-in-out;
  max-height: 100vh; /* Đặt chiều cao đủ lớn */
  opacity: 1;
}

.expand-enter-from,
.expand-leave-to {
  max-height: 0;
  opacity: 0;
  padding-top: 0;
  padding-bottom: 0;
}
</style>