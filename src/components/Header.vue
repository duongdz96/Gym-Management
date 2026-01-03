<script setup>
import { ref } from "vue";
import { ChevronDown, ChevronUp, User } from "lucide-vue-next";
import { RouterLink, useRouter } from "vue-router";
import { useAuthStore } from "@/stores/useAuthStore";
import { useToast } from "vue-toastification";

const router = useRouter();
const authStore = useAuthStore();
const toast = useToast();

const isMenuOpen = ref(false);

const handleLogout = () => {
  authStore.logout();
  toast.success("Đăng xuất thành công!");
  router.push("/login");
};
</script>

<template>
  <header class="w-full bg-gradient-to-r from-emerald-600 via-teal-600 to-cyan-600 shadow-lg relative z-50">
    <div class="absolute inset-0 bg-gradient-to-r from-emerald-500/20 via-transparent to-cyan-500/20"></div>
    <div class="mx-auto max-w-7xl px-6 py-3 flex items-center justify-between relative z-10">
      <!-- Logo -->
      <div class="flex items-center gap-3">
        <RouterLink to="/" class="inline-flex items-center gap-2">
          <span class="h-8 w-8 rounded-full bg-gradient-to-br from-yellow-400 via-orange-500 to-pink-500 inline-block shadow-lg"></span>
          <span class="font-bold text-white tracking-wider uppercase"
            >Gym Management</span
          >
        </RouterLink>
      </div>

      <!-- Navigation -->
      <nav class="hidden md:flex items-center gap-6 text-sm font-medium text-white">
        <a href="#classes" class="uppercase hover:text-yellow-300 transition-colors duration-300">Dịch vụ</a>
        <a href="#clubs" class="uppercase hover:text-orange-300 transition-colors duration-300">Câu lạc bộ</a>
        <a href="#schedule" class="uppercase hover:text-pink-300 transition-colors duration-300">Lịch tập</a>
        <a href="#pricing" class="uppercase hover:text-cyan-300 transition-colors duration-300">Bảng giá</a>
        <a href="#gifts" class="uppercase hover:text-purple-300 transition-colors duration-300">Quà tặng</a>
      </nav>

      <!-- Auth / User -->
      <div class="hidden md:flex items-center">
        <template v-if="!authStore.user">
          <RouterLink
            :to="{ name: 'login' }"
            class="inline-flex items-center rounded-lg bg-gradient-to-r from-orange-500 via-pink-500 to-purple-500 px-4 py-2 text-white text-sm font-semibold hover:shadow-lg hover:scale-105 transition-all duration-300"
          >
            Đăng Nhập
          </RouterLink>
        </template>

        <template v-else>
          <div class="relative">
            <button
              @click="isMenuOpen = !isMenuOpen"
              class="flex items-center gap-2 text-white hover:text-yellow-300 transition-colors duration-300"
            >
              <User class="w-6 h-6" />
              <ChevronDown
                v-if="!isMenuOpen"
                class="w-4 h-4 transition-transform"
              />
              <ChevronUp v-else class="w-4 h-4 transition-transform" />
            </button>

            <div
              v-if="isMenuOpen"
              class="absolute right-0 mt-2 w-56 bg-white shadow-lg rounded-lg overflow-hidden z-20"
            >
              <RouterLink
                to="/profile"
                class="block px-4 py-2 text-gray-800 hover:bg-gray-100"
              >
                Chỉnh sửa thông tin cá nhân
              </RouterLink>
              <RouterLink
                to="/change-password"
                class="block px-4 py-2 text-gray-800 hover:bg-gray-100"
                @click="isMenuOpen = false"
              >
                Đổi mật khẩu
              </RouterLink>
              <button
                @click="handleLogout"
                class="w-full text-left px-4 py-2 text-gray-800 hover:bg-gray-100"
              >
                Đăng xuất
              </button>
            </div>
          </div>
        </template>
      </div>
    </div>
  </header>
</template>
