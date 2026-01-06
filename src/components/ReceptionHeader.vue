<script setup>
import { ref } from "vue";
import { RouterLink, useRouter } from "vue-router";
import { useAuthStore } from "@/stores/useAuthStore";
import { UserIcon, ShoppingCart, UserPlus, Calendar, Dumbbell, Clock, Settings, LogOut } from "lucide-vue-next";

const router = useRouter();
const authStore = useAuthStore();

const isAccountMenuOpen = ref(false);

const toggleAccountMenu = () => {
  isAccountMenuOpen.value = !isAccountMenuOpen.value;
};

const handleLogout = () => {
  authStore.logout(); // Xóa token + user trong store
  router.push("/login"); // Điều hướng về trang login
};
</script>

<template>
  <header class="w-full bg-gradient-to-r from-emerald-600 via-teal-600 to-cyan-600 shadow-lg relative z-50">
    <div class="absolute inset-0 bg-gradient-to-r from-emerald-500/20 via-transparent to-cyan-500/20"></div>
    <div class="mx-auto max-w-7xl px-6 py-4 flex items-center justify-between relative z-10">
      <!-- Logo -->
      <div class="flex items-center gap-3">
        <RouterLink
          to="/reception"
          class="inline-flex items-center gap-2 hover:opacity-90 transition"
        >
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
          :to="{ name: 'salesselect' }"
          class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:text-yellow-300 transition-colors duration-300 rounded-md"
        >
          <ShoppingCart class="h-4 w-4" />
          Bán hàng
        </RouterLink>

        <RouterLink
          :to="{ name: 'add-membership' }"
          class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:text-orange-300 transition-colors duration-300 rounded-md"
        >
          <UserPlus class="h-4 w-4" />
          Hội viên
        </RouterLink>

        <RouterLink
          :to="{ name: 'reception.pt-registration' }"
          class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:text-pink-300 transition-colors duration-300 rounded-md"
        >
          <Dumbbell class="h-4 w-4" />
          Đăng ký PT
        </RouterLink>

        <!-- Account -->
      </nav>
      <div
        class="relative"
        v-if="authStore.user"
        @mouseenter="isAccountMenuOpen = true"
        @mouseleave="isAccountMenuOpen = false"
      >
        <button
          class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:text-cyan-300 transition-colors duration-300 rounded-md"
          @click="toggleAccountMenu"
        >
          <UserIcon class="w-5 h-5" />
          <span>{{ authStore.user?.fullName || 'Tài khoản' }}</span>
        </button>

        <transition name="fade">
          <div
            v-if="isAccountMenuOpen"
            class="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg border border-gray-200 z-50"
          >
            <RouterLink
              to="/profile"
              class="flex items-center gap-2 px-4 py-2 text-gray-800 hover:bg-gray-100"
              @click="isAccountMenuOpen = false"
            >
              <UserIcon class="w-4 h-4" />
              Thay đổi hồ sơ
            </RouterLink>
            <RouterLink
              :to="{ name: 'reception.attendance-history' }"
              class="flex items-center gap-2 px-4 py-2 text-gray-800 hover:bg-gray-100"
              @click="isAccountMenuOpen = false"
            >
              <Clock class="w-4 h-4" />
              Lịch sử chấm công
            </RouterLink>
            <RouterLink
              to="/change-password"
              class="flex items-center gap-2 px-4 py-2 text-gray-800 hover:bg-gray-100"
              @click="isAccountMenuOpen = false"
            >
              <Settings class="w-4 h-4" />
              Thay đổi mật khẩu
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
  transition: opacity 0.15s ease-in-out;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
