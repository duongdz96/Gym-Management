<script setup>
import { ref } from "vue";
import { RouterLink, useRouter } from "vue-router";
import { useAuthStore } from "@/stores/useAuthStore";
import { UserIcon, ShoppingCart, UserPlus, Calendar, Dumbbell } from "lucide-vue-next";

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
  <header class="w-full bg-gradient-to-r from-gray-900 to-stone-800 shadow-lg relative z-50">
    <div class="mx-auto max-w-7xl px-6 py-4 flex items-center justify-between">
      <!-- Logo -->
      <div class="flex items-center gap-3">
        <RouterLink
          to="/reception"
          class="inline-flex items-center gap-2 hover:opacity-90 transition"
        >
          <Dumbbell class="h-8 w-8 text-emerald-500" />
          <span class="font-bold text-white tracking-wider uppercase">
            Quản lý Gym
          </span>
        </RouterLink>
      </div>

      <!-- Navigation -->
      <nav class="flex items-center gap-8 text-sm font-medium">
        <RouterLink
          :to="{ name: 'salesselect' }"
          class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:bg-emerald-600/20 hover:text-emerald-400 transition rounded-md"
        >
          <ShoppingCart class="h-4 w-4" />
          Bán hàng
        </RouterLink>

        <RouterLink
          :to="{ name: 'add-membership' }"
          class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:bg-emerald-600/20 hover:text-emerald-400 transition rounded-md"
        >
          <UserPlus class="h-4 w-4" />
          Thêm thành viên
        </RouterLink>

        <RouterLink
          to="/reception/class-registration"
          class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:bg-emerald-600/20 hover:text-emerald-400 transition rounded-md"
        >
          <Calendar class="h-4 w-4" />
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
          class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:bg-emerald-600/20 hover:text-emerald-400 transition rounded-md"
          @click="toggleAccountMenu"
        >
          <UserIcon class="w-5 h-5" />
          <span>Tài khoản</span>
        </button>

        <transition name="fade">
          <div
            v-if="isAccountMenuOpen"
            class="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg border border-gray-200 z-50"
          >
            <RouterLink
              to="/profile"
              class="block px-4 py-2 text-gray-800 hover:bg-gray-100"
              @click="isAccountMenuOpen = false"
            >
              Thay đổi hồ sơ
            </RouterLink>
            <RouterLink
              :to="{ name: 'reception.attendance-history' }"
              class="block px-4 py-2 text-gray-800 hover:bg-gray-100"
              @click="isAccountMenuOpen = false"
            >
              Lịch sử điểm danh
            </RouterLink>
            <RouterLink
              to="/change-password"
              class="block px-4 py-2 text-gray-800 hover:bg-gray-100"
              @click="isAccountMenuOpen = false"
            >
              Thay đổi mật khẩu
            </RouterLink>
            <button
              class="block w-full text-left px-4 py-2 text-gray-800 hover:bg-gray-100"
              @click="handleLogout"
            >
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
