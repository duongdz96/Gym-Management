<script setup>
import { ref } from "vue";
import { useRoute, useRouter, RouterLink } from "vue-router";
import { useAuthStore } from "@/stores/useAuthStore";
import { useToast } from "vue-toastification";
import { Dumbbell, UserIcon, Settings, LogOut, Clock } from "lucide-vue-next";

const route = useRoute();
const isMenuOpen = ref(false);
const isAccountOpen = ref(false);
const authStore = useAuthStore();
const router = useRouter();
const toast = useToast();
const openAccountMenu = () => (isAccountOpen.value = true);
const closeAccountMenu = () => (isAccountOpen.value = false);

const handleLogout = () => {
  authStore.logout();
  toast.success("Đăng xuất thành công!");
  router.push("/login");
};
</script>

<template>
  <header class="w-full bg-gradient-to-r from-emerald-600 via-teal-600 to-cyan-600 shadow-lg relative z-50">
    <div class="absolute inset-0 bg-gradient-to-r from-emerald-500/20 via-transparent to-cyan-500/20"></div>
    <div class="mx-auto max-w-7xl px-6 py-4 flex items-center justify-between relative z-10">
      <!-- Logo -->
      <div class="flex items-center gap-3">
        <RouterLink to="/teacher" class="inline-flex items-center gap-2 hover:opacity-90 transition">
          <div class="h-8 w-8 rounded-full bg-gradient-to-br from-yellow-400 via-orange-500 to-pink-500 flex items-center justify-center shadow-lg">
            <Dumbbell class="h-5 w-5 text-white" />
          </div>
          <span class="font-bold text-white tracking-wider uppercase"
            >Quản lý Gym</span
          >
        </RouterLink>
      </div>

      <!-- Navigation (desktop) -->
      <nav class="hidden md:flex items-center gap-8 text-sm font-medium">
        <RouterLink
          to="/teacher/registerclass"
          class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:text-yellow-300 transition-colors duration-300 rounded-md"
          >Đăng ký dạy</RouterLink
        >
        <RouterLink
          to="/teacher/calendar"
          class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:text-orange-300 transition-colors duration-300 rounded-md"
          >Lịch trình</RouterLink
        >
        <RouterLink
          to="/teacher/attendance"
          class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:text-pink-300 transition-colors duration-300 rounded-md"
          >Điểm danh</RouterLink
        >
      </nav>

      <!-- Account dropdown (desktop) -->
      <div
        class="relative hidden md:block"
        @mouseenter="openAccountMenu"
        @mouseleave="closeAccountMenu"
      >
        <button class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:text-cyan-300 transition-colors duration-300 rounded-md">
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
              Hồ sơ cá nhân
            </RouterLink>
            <RouterLink
              to="/teacher/attendance-history"
              class="flex items-center gap-2 px-4 py-2 text-gray-800 hover:bg-gray-100"
            >
              <Clock class="w-4 h-4" />
              Lịch sử điểm danh
            </RouterLink>
            <RouterLink
              to="/change-password"
              class="flex items-center gap-2 px-4 py-2 text-gray-800 hover:bg-gray-100"
            >
              <Settings class="w-4 h-4" />
              Thay đổi mật khẩu
            </RouterLink>
            <button
              @click="handleLogout"
              class="flex items-center gap-2 w-full text-left px-4 py-2 text-gray-800 hover:bg-gray-100"
            >
              <LogOut class="w-4 h-4" />
              Đăng xuất
            </button>
          </div>
        </transition>
      </div>

      <!-- Mobile menu toggle -->
      <button
        class="md:hidden text-white focus:outline-none"
        @click="isMenuOpen = !isMenuOpen"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          class="h-6 w-6"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M4 6h16M4 12h16M4 18h16"
          />
        </svg>
      </button>
    </div>

    <!-- Mobile menu -->
    <transition name="slide">
      <div
        v-if="isMenuOpen"
        class="md:hidden bg-emerald-800 px-6 py-4 border-t border-emerald-700"
      >
        <nav class="flex flex-col gap-4">
          <RouterLink
            to="/teacher/registerclass"
            class="uppercase tracking-wider text-white hover:text-emerald-400"
            @click="isMenuOpen = false"
            >Đăng ký dạy</RouterLink
          >
          <RouterLink
            to="/teacher/calendar"
            class="uppercase tracking-wider text-white hover:text-emerald-400"
            @click="isMenuOpen = false"
            >Lịch trình</RouterLink
          >
          <RouterLink
            to="/teacher/attendance"
            class="uppercase tracking-wider text-white hover:text-emerald-400"
            @click="isMenuOpen = false"
            >Điểm danh</RouterLink
          >
        </nav>
      </div>
    </transition>
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

.slide-enter-active,
.slide-leave-active {
  transition: transform 0.3s ease;
}

.slide-enter-from,
.slide-leave-to {
  transform: translateY(-100%);
}
</style>
