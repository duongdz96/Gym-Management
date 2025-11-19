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
  <header class="w-full bg-stone-900">
    <div class="mx-auto max-w-7xl px-6 py-3 flex items-center justify-between">
      <!-- Logo -->
      <div class="flex items-center gap-3">
        <RouterLink to="/" class="inline-flex items-center gap-2">
          <span class="h-8 w-8 rounded-full bg-red-600 inline-block"></span>
          <span class="font-bold text-white tracking-wider uppercase"
            >Gym Management</span
          >
        </RouterLink>
      </div>

      <!-- Navigation -->
      <nav class="hidden md:flex items-center gap-6 text-sm font-medium text-white">
        <a href="#classes" class="uppercase hover:text-red-500">Dịch vụ</a>
        <a href="#clubs" class="uppercase hover:text-red-500">Câu lạc bộ</a>
        <a href="#schedule" class="uppercase hover:text-red-500">Lịch tập</a>
        <a href="#pricing" class="uppercase hover:text-red-500">Bảng giá</a>
        <a href="#gifts" class="uppercase hover:text-red-500">Quà tặng</a>
      </nav>

      <!-- Auth / User -->
      <div class="hidden md:flex items-center">
        <template v-if="!authStore.user">
          <RouterLink
            :to="{ name: 'login' }"
            class="inline-flex items-center rounded-md bg-red-600 px-4 py-2 text-white text-sm font-semibold hover:bg-black"
          >
            Đăng Nhập
          </RouterLink>
        </template>

        <template v-else>
          <div class="relative">
            <button
              @click="isMenuOpen = !isMenuOpen"
              class="flex items-center gap-2 text-white hover:text-red-500"
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
