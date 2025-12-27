<script setup>
import { ref } from "vue";
import { useRoute, useRouter, RouterLink } from "vue-router";
import { useAuthStore } from "@/stores/useAuthStore";
import { useToast } from "vue-toastification";
import { UserIcon, Clock, Settings, LogOut, Dumbbell } from "lucide-vue-next";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const toast = useToast();

const isMenuOpen = ref(false);
const isAccountMenuOpen = ref(false);
const isProductMenuOpen = ref(false);
const isClassMenuOpen = ref(false);
const isStaffMenuOpen = ref(false);

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
        <RouterLink to="/manager" class="inline-flex items-center gap-2 hover:opacity-90 transition">
          <div class="h-8 w-8 rounded-full bg-gradient-to-br from-yellow-400 via-orange-500 to-pink-500 flex items-center justify-center shadow-lg">
            <Dumbbell class="h-5 w-5 text-white" />
          </div>
          <span class="font-bold text-white tracking-wider uppercase">
            Quản lý Gym
          </span>
        </RouterLink>
      </div>

      <!-- Navigation (desktop) -->
      <nav class="hidden md:flex items-center gap-8 text-sm font-medium">
        <!-- Staff Dropdown -->
        <div
          class="relative"
          @mouseenter="isStaffMenuOpen = true"
          @mouseleave="isStaffMenuOpen = false"
        >
          <button
            class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:text-yellow-300 transition-colors duration-300 rounded-md"
          >
            Nhân viên
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
            </svg>
          </button>

          <transition name="fade">
            <div
              v-show="isStaffMenuOpen"
              class="absolute left-0 mt-2 w-56 bg-white rounded shadow-md z-50 text-gray-800"
            >
              <RouterLink
                to="/manager/staff"
                class="block px-4 py-2 hover:bg-gray-100 hover:text-emerald-600"
              >
                Quản lý nhân viên
              </RouterLink>
              <RouterLink
                to="/manager/leave-approval"
                class="block px-4 py-2 hover:bg-gray-100 hover:text-emerald-600"
              >
                Duyệt nghỉ phép
              </RouterLink>
            </div>
          </transition>
        </div>
        <RouterLink
          to="/manager/customer"
          class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:text-orange-300 transition-colors duration-300 rounded-md"
        >
          Khách hàng
        </RouterLink>
        <!-- Product Dropdown -->
        <div
          class="relative"
          @mouseenter="isProductMenuOpen = true"
          @mouseleave="isProductMenuOpen = false"
        >
          <button
            class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:text-pink-300 transition-colors duration-300 rounded-md"
          >
            Sản phẩm
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
            </svg>
          </button>
          
          <transition name="fade">
            <div
              v-show="isProductMenuOpen"
              class="absolute left-0 mt-2 w-56 bg-white rounded shadow-md z-50 text-gray-800"
            >
              <RouterLink
                to="/manager/product/supplier"
                class="block px-4 py-2 hover:bg-gray-100 hover:text-red-600"
              >
                Quản lý nhà cung cấp
              </RouterLink>
              <RouterLink
                to="/manager/product/list"
                class="block px-4 py-2 hover:bg-gray-100 hover:text-red-600"
              >
                Quản lý sản phẩm
              </RouterLink>
              <RouterLink
                to="/manager/product/history"
                class="block px-4 py-2 hover:bg-gray-100 hover:text-red-600"
              >
                Lịch sử nhập kho
              </RouterLink>
              <RouterLink
                to="/manager/coupon"
                class="block px-4 py-2 hover:bg-gray-100 hover:text-red-600"
              >
                Mã giảm giá
              </RouterLink>
            </div>
          </transition>
        </div>
        <RouterLink
          to="/manager/membership"
          class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:text-cyan-300 transition-colors duration-300 rounded-md"
        >
          Thành viên
        </RouterLink>
        
        <!-- Classes Dropdown -->
        <div
          class="relative"
          @mouseenter="isClassMenuOpen = true"
          @mouseleave="isClassMenuOpen = false"
        >
          <button
            class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:text-purple-300 transition-colors duration-300 rounded-md"
          >
            Lớp học
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
            </svg>
          </button>
          
          <transition name="fade">
            <div
              v-show="isClassMenuOpen"
              class="absolute left-0 mt-2 w-56 bg-white rounded shadow-md z-50 text-gray-800"
            >
              <RouterLink
                to="/manager/room"
                class="block px-4 py-2 hover:bg-gray-100 hover:text-red-600"
              >
                Quản lý phòng học
              </RouterLink>
              <RouterLink
                to="/manager/class"
                class="block px-4 py-2 hover:bg-gray-100 hover:text-red-600"
              >
                Quản lý lớp học
              </RouterLink>

              <RouterLink
                to="/manager/teacher-approval"
                class="block px-4 py-2 hover:bg-gray-100 hover:text-red-600"
              >
                Duyệt giáo viên
              </RouterLink>
            </div>
          </transition>
        </div>
        
        <RouterLink
          to="/manager/banner"
          class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:text-yellow-300 transition-colors duration-300 rounded-md"
        >
          Banner
        </RouterLink>
      </nav>

      <!-- Account dropdown -->
      <div
        v-if="authStore.user"
        class="relative hidden md:block"
        @mouseenter="isAccountMenuOpen = true"
        @mouseleave="isAccountMenuOpen = false"
      >
        <button
          class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:text-orange-300 transition-colors duration-300 rounded-md"
        >
          <UserIcon class="w-5 h-5" />
          <span>{{ authStore.user?.fullName || 'Tài khoản' }}</span>
        </button>
        <transition name="fade">
          <div
            v-show="isAccountMenuOpen"
            class="absolute right-0 mt-2 w-56 bg-white rounded shadow-md z-50"
          >
            <RouterLink
              to="/profile"
              class="flex items-center gap-2 px-4 py-2 text-gray-800 hover:bg-gray-100"
            >
              <UserIcon class="w-4 h-4" />
              Hồ sơ cá nhân
            </RouterLink>
            <RouterLink
              to="/manager/attendance"
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

      <div v-else class="hidden md:block">
        <RouterLink
          :to="{ name: 'login' }"
          class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:text-cyan-300 transition-colors duration-300 rounded-md"
        >
          Đăng nhập
        </RouterLink>
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
    <div
      v-if="isMenuOpen"
      class="md:hidden bg-gradient-to-r from-emerald-700 to-emerald-800 text-white px-6 py-3 space-y-2"
    >
      <div class="space-y-1">
        <div class="font-semibold text-emerald-200 uppercase text-xs tracking-wider">Nhân viên</div>
        <RouterLink
          to="/manager/staff"
          class="block pl-4 hover:text-emerald-400"
          @click="isMenuOpen = false"
        >Quản lý nhân viên</RouterLink>
        <RouterLink
          to="/manager/leave-approval"
          class="block pl-4 hover:text-emerald-400"
          @click="isMenuOpen = false"
        >Duyệt nghỉ phép</RouterLink>
      </div>
      <RouterLink
        to="/manager/customer"
        class="block hover:text-emerald-400"
        @click="isMenuOpen = false"
      >Khách hàng</RouterLink>
      <RouterLink
        to="/manager/product"
        class="block hover:text-emerald-400"
        @click="isMenuOpen = false"
      >Sản phẩm</RouterLink>
      <RouterLink
        to="/manager/coupon"
        class="block hover:text-emerald-400"
        @click="isMenuOpen = false"
      >Mã giảm giá</RouterLink>
      <RouterLink
        to="/manager/membership"
        class="block hover:text-emerald-400"
        @click="isMenuOpen = false"
      >Thành viên</RouterLink>

      <hr class="border-gray-700 my-2" />

      <template v-if="authStore.user">
        <RouterLink
          to="/profile"
          class="block hover:text-emerald-400"
          @click="isMenuOpen = false"
        >Hồ sơ cá nhân</RouterLink>
        <RouterLink
          to="/manager/attendance"
          class="block hover:text-emerald-400"
          @click="isMenuOpen = false"
        >Lịch sử điểm danh</RouterLink>
        <RouterLink
          to="/change-password"
          class="block hover:text-emerald-400"
          @click="isMenuOpen = false"
        >Thay đổi mật khẩu</RouterLink>
        <button
          @click="handleLogout"
          class="block text-left w-full hover:text-emerald-400"
        >
          Đăng xuất
        </button>
      </template>

      <template v-else>
        <RouterLink
          :to="{ name: 'login' }"
          class="block hover:text-emerald-400"
          @click="isMenuOpen = false"
        >Đăng nhập</RouterLink>
      </template>
    </div>
  </header>
</template>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>