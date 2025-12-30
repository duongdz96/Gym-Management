<script setup>
import { ref } from "vue";
import { useRoute, useRouter, RouterLink } from "vue-router";
import { useAuthStore } from "@/stores/useAuthStore";
import { useToast } from "vue-toastification";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const toast = useToast();

const isMenuOpen = ref(false);
const isAccountMenuOpen = ref(false);
const isProductMenuOpen = ref(false);
const isClassMenuOpen = ref(false);
const isContentMenuOpen = ref(false);

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
      
      <div class="flex items-center gap-3">
        <RouterLink to="/manager" class="inline-flex items-center gap-2 hover:opacity-90 transition">
          <div class="h-8 w-8 rounded-full bg-gradient-to-br from-yellow-400 via-orange-500 to-pink-500 flex items-center justify-center shadow-lg">
            <Dumbbell class="h-5 w-5 text-white" />
          </div>
          <span class="font-bold text-white tracking-wider uppercase">
            Gym Management
          </span>
        </RouterLink>
      </div>

      <nav class="hidden md:flex items-center gap-2 text-sm font-medium">
        
        <RouterLink
          to="/manager/staff"
          class="px-3 py-2 rounded-md transition-colors duration-300 uppercase tracking-wider"
          :class="route.path === '/manager/staff' 
            ? 'text-yellow-300 font-bold bg-white/10' 
            : 'text-white hover:text-yellow-200 hover:bg-white/5'"
        >
          Nhân viên
        </RouterLink>

        <RouterLink
          to="/manager/customer"
          class="px-3 py-2 rounded-md transition-colors duration-300 uppercase tracking-wider"
          :class="route.path === '/manager/customer' 
            ? 'text-yellow-300 font-bold bg-white/10' 
            : 'text-white hover:text-yellow-200 hover:bg-white/5'"
        >
          Khách hàng
        </RouterLink>

        <RouterLink
          to="/manager/attendance"
          class="px-3 py-2 rounded-md transition-colors duration-300 uppercase tracking-wider"
          :class="route.path === '/manager/attendance' 
            ? 'text-yellow-300 font-bold bg-white/10' 
            : 'text-white hover:text-yellow-200 hover:bg-white/5'"
        >
          Điểm danh
        </RouterLink>

        <div
          class="relative"
          @mouseenter="isProductMenuOpen = true"
          @mouseleave="isProductMenuOpen = false"
        >
          <button
            class="flex items-center gap-1 px-3 py-2 rounded-md transition-colors duration-300 uppercase tracking-wider"
            :class="route.path.startsWith('/manager/product') 
              ? 'text-yellow-300 font-bold bg-white/10' 
              : 'text-white hover:text-yellow-200 hover:bg-white/5'"
          >
            <Package class="h-4 w-4" /> <span>Sản phẩm</span>
            <ChevronDown class="h-4 w-4" />
          </button>
          
          <transition name="fade">
            <div
              v-show="isProductMenuOpen"
              class="absolute left-0 mt-2 w-60 bg-white rounded-md shadow-xl border border-gray-200 z-50 overflow-hidden"
            >
              <RouterLink
                to="/manager/product/supplier"
                class="block px-4 py-3 text-gray-700 hover:bg-emerald-50 hover:text-emerald-600 transition"
              >
                Quản lý nhà cung cấp
              </RouterLink>
              <RouterLink
                to="/manager/product/list"
                class="block px-4 py-3 text-gray-700 hover:bg-emerald-50 hover:text-emerald-600 transition"
              >
                Quản lý sản phẩm
              </RouterLink>
              <RouterLink
                to="/manager/product/history"
                class="block px-4 py-3 text-gray-700 hover:bg-emerald-50 hover:text-emerald-600 transition"
              >
                Lịch sử nhập kho
              </RouterLink>
            </div>
          </transition>
        </div>
        
        <div
          class="relative"
          @mouseenter="isContentMenuOpen = true"
          @mouseleave="isContentMenuOpen = false"
        >
          <button
            class="flex items-center gap-1 px-3 py-2 rounded-md transition-colors duration-300 uppercase tracking-wider"
            :class="(route.path.startsWith('/manager/banner') || route.path.startsWith('/manager/coupon') || route.path.startsWith('/manager/blog')) 
              ? 'text-yellow-300 font-bold bg-white/10' 
              : 'text-white hover:text-yellow-200 hover:bg-white/5'"
          >
            <FileText class="h-4 w-4" />
            <span>Nội dung</span>
            <ChevronDown class="h-4 w-4" />
          </button>
          
          <transition name="fade">
            <div
              v-show="isContentMenuOpen"
              class="absolute left-0 mt-2 w-60 bg-white rounded-md shadow-xl border border-gray-200 z-50 overflow-hidden"
            >
              <RouterLink
                to="/manager/banner"
                class="block px-4 py-3 text-gray-700 hover:bg-emerald-50 hover:text-emerald-600 transition"
              >
                Quản lý banner
              </RouterLink>
              <RouterLink
                to="/manager/coupon"
                class="block px-4 py-3 text-gray-700 hover:bg-emerald-50 hover:text-emerald-600 transition"
              >
                Quản lý mã giảm giá
              </RouterLink>
              <RouterLink
                to="/manager/blog"
                class="block px-4 py-3 text-gray-700 hover:bg-emerald-50 hover:text-emerald-600 transition"
              >
                Quản lý blog
              </RouterLink>
            </div>
          </transition>
        </div>
        
        <RouterLink
          to="/manager/membership"
          class="px-3 py-2 rounded-md transition-colors duration-300 uppercase tracking-wider"
          :class="route.path.startsWith('/manager/membership') 
            ? 'text-yellow-300 font-bold bg-white/10' 
            : 'text-white hover:text-yellow-200 hover:bg-white/5'"
        >
          Hội viên
        </RouterLink>
        
        <div
          class="relative"
          @mouseenter="isClassMenuOpen = true"
          @mouseleave="isClassMenuOpen = false"
        >
          <button
            class="flex items-center gap-1 px-3 py-2 rounded-md transition-colors duration-300 uppercase tracking-wider"
            :class="(route.path.startsWith('/manager/class') || route.path.startsWith('/manager/room')) 
              ? 'text-yellow-300 font-bold bg-white/10' 
              : 'text-white hover:text-yellow-200 hover:bg-white/5'"
          >
            <LayoutGrid class="h-4 w-4" />
            <span>Lớp học</span>
            <ChevronDown class="h-4 w-4" />
          </button>
          
          <transition name="fade">
            <div
              v-show="isClassMenuOpen"
              class="absolute right-0 mt-2 w-60 bg-white rounded-md shadow-xl border border-gray-200 z-50 overflow-hidden"
            >
              <RouterLink
                to="/manager/room"
                class="block px-4 py-3 text-gray-700 hover:bg-emerald-50 hover:text-emerald-600 transition"
              >
                Quản lý phòng học
              </RouterLink>
              <RouterLink
                to="/manager/class"
                class="block px-4 py-3 text-gray-700 hover:bg-emerald-50 hover:text-emerald-600 transition"
              >
                Quản lý lớp học
              </RouterLink>
              <RouterLink
                to="/manager/teacher-approval"
                class="block px-4 py-3 text-gray-700 hover:bg-emerald-50 hover:text-emerald-600 transition"
              >
                Duyệt giáo viên
              </RouterLink>
            </div>
          </transition>
        </div>
      </nav>

      <div v-if="authStore.user" class="relative hidden md:block"
        @mouseenter="isAccountMenuOpen = true"
        @mouseleave="isAccountMenuOpen = false"
      >
        <button class="flex items-center gap-2 px-3 py-2 text-white uppercase tracking-wider hover:text-cyan-200 transition-colors duration-300 rounded-md">
          <UserIcon class="h-5 w-5" />
          <span class="font-semibold">{{ authStore.user.fullName }}</span>
        </button>
        
        <transition name="fade">
          <div
            v-show="isAccountMenuOpen"
            class="absolute right-0 mt-2 w-56 bg-white rounded-md shadow-xl border border-gray-200 z-50"
          >
            <RouterLink to="/profile" class="flex items-center gap-2 px-4 py-3 text-gray-700 hover:bg-gray-100 hover:text-emerald-600">
              <UserIcon class="w-4 h-4" /> Hồ sơ
            </RouterLink>
            <RouterLink to="/change-password" class="flex items-center gap-2 px-4 py-3 text-gray-700 hover:bg-gray-100 hover:text-emerald-600">
              <Settings class="w-4 h-4" /> Đổi mật khẩu
            </RouterLink>
            <button @click="handleLogout" class="flex items-center gap-2 w-full text-left px-4 py-3 text-gray-700 hover:bg-gray-100 hover:text-red-600">
              <LogOut class="w-4 h-4" /> Đăng xuất
            </button>
          </div>
        </transition>
      </div>

      <div v-else class="hidden md:block">
        <RouterLink :to="{ name: 'login' }" class="px-4 py-2 bg-white text-emerald-600 rounded-full font-bold hover:bg-gray-100 transition shadow-sm">
          Login
        </RouterLink>
      </div>

      <button class="md:hidden text-white focus:outline-none" @click="isMenuOpen = !isMenuOpen">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
        </svg>
      </button>
    </div>

    <div v-if="isMenuOpen" class="md:hidden bg-teal-800 text-white px-6 py-4 space-y-3 shadow-inner">
      <RouterLink to="/manager/staff" class="block hover:text-yellow-300 py-1" @click="isMenuOpen = false">Staff</RouterLink>
      <RouterLink to="/manager/customer" class="block hover:text-yellow-300 py-1" @click="isMenuOpen = false">Customers</RouterLink>
      <RouterLink to="/manager/attendance" class="block hover:text-yellow-300 py-1" @click="isMenuOpen = false">Attendance</RouterLink>
      <RouterLink to="/manager/product/list" class="block hover:text-yellow-300 py-1" @click="isMenuOpen = false">Products</RouterLink>
      <RouterLink to="/manager/membership" class="block hover:text-yellow-300 py-1" @click="isMenuOpen = false">Memberships</RouterLink>
      
      <hr class="border-teal-600 my-2" />
      
      <template v-if="authStore.user">
        <RouterLink to="/manager/profile" class="block hover:text-yellow-300 py-1" @click="isMenuOpen = false">Profile</RouterLink>
        <button @click="handleLogout" class="block text-left w-full hover:text-yellow-300 py-1">Logout</button>
      </template>
      <template v-else>
        <RouterLink :to="{ name: 'login' }" class="block hover:text-yellow-300 py-1" @click="isMenuOpen = false">Login</RouterLink>
      </template>
    </div>
  </header>
</template>

<style scoped>
/* Hiệu ứng Fade đơn giản */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-5px);
}
</style>