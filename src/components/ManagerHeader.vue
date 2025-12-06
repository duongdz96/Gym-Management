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

const handleLogout = () => {
  authStore.logout();
  toast.success("Đăng xuất thành công!");
  router.push("/login");
};
</script>

<template>
  <header class="w-full bg-stone-900 relative">
    <div class="mx-auto max-w-7xl px-6 py-3 flex items-center justify-between">
      <!-- Logo -->
      <div class="flex items-center gap-3">
        <RouterLink to="/manager" class="inline-flex items-center gap-2">
          <span class="h-8 w-8 rounded-full bg-red-600 inline-block"></span>
          <span class="font-bold text-white tracking-wider uppercase">
            Gym Management
          </span>
        </RouterLink>
      </div>

      <!-- Navigation (desktop) -->
      <nav class="hidden md:flex items-center gap-6 text-sm font-medium">
        <RouterLink
          to="/manager/staff"
          class="uppercase tracking-wider hover:text-red-600"
          :class="route.path === '/manager/staff' ? 'text-red-600 border-b-2 border-red-600 pb-1' : 'text-white'"
        >
          Staff
        </RouterLink>
        <RouterLink
          to="/manager/customer"
          class="uppercase tracking-wider hover:text-red-600"
          :class="route.path === '/manager/customer' ? 'text-red-600 border-b-2 border-red-600 pb-1' : 'text-white'"
        >
          Customers
        </RouterLink>
        <RouterLink
          to="/manager/attendance"
          class="uppercase tracking-wider hover:text-red-600"
          :class="route.path === '/manager/attendance' ? 'text-red-600 border-b-2 border-red-600 pb-1' : 'text-white'"
        >
          Attendance
        </RouterLink>
        <!-- Product Dropdown -->
        <div
          class="relative"
          @mouseenter="isProductMenuOpen = true"
          @mouseleave="isProductMenuOpen = false"
        >
          <button
            class="uppercase tracking-wider hover:text-red-600 flex items-center gap-1"
            :class="route.path.startsWith('/manager/product') ? 'text-red-600 border-b-2 border-red-600 pb-1' : 'text-white'"
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
            </div>
          </transition>
        </div>
        <RouterLink
          to="/manager/coupon"
          class="uppercase tracking-wider hover:text-red-600"
          :class="route.path === '/manager/coupon' ? 'text-red-600 border-b-2 border-red-600 pb-1' : 'text-white'"
        >
          Mã giảm giá
        </RouterLink>
        <RouterLink
          to="/manager/membership"
          class="uppercase tracking-wider hover:text-red-600"
          :class="route.path.startsWith('/manager/membership') ? 'text-red-600 border-b-2 border-red-600 pb-1' : 'text-white'"
        >
          Memberships
        </RouterLink>
        <RouterLink
          to="/manager/classtemplate"
          class="uppercase tracking-wider hover:text-red-600"
          :class="route.path === '/manager/classtemplate' ? 'text-red-600 border-b-2 border-red-600 pb-1' : 'text-white'"
        >
          Classes
        </RouterLink>
        <RouterLink
          to="/manager/banner"
          class="uppercase tracking-wider hover:text-red-600"
          :class="route.path === '/manager/banner' ? 'text-red-600 border-b-2 border-red-600 pb-1' : 'text-white'"
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
          class="flex items-center gap-2 text-white uppercase tracking-wider hover:text-red-600"
        >
          <span>👤 {{ authStore.user.fullName }}</span>
        </button>
        <transition name="fade">
          <div
            v-show="isAccountMenuOpen"
            class="absolute right-0 mt-2 w-44 bg-white rounded shadow-md z-50"
          >
            <RouterLink
              to="/manager/profile"
              class="block px-4 py-2 text-gray-800 hover:bg-gray-100"
            >
              Profile
            </RouterLink>
            <RouterLink
              to="/change-password"
              class="block px-4 py-2 text-gray-800 hover:bg-gray-100"
            >
              Change Password
            </RouterLink>
            <button
              @click="handleLogout"
              class="block w-full text-left px-4 py-2 text-gray-800 hover:bg-gray-100"
            >
              Logout
            </button>
          </div>
        </transition>
      </div>

      <div v-else class="hidden md:block">
        <RouterLink
          :to="{ name: 'login' }"
          class="text-white uppercase tracking-wider hover:text-red-600"
        >
          Login
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
      class="md:hidden bg-stone-800 text-white px-6 py-3 space-y-2"
    >
      <RouterLink
        to="/manager/staff"
        class="block hover:text-red-600"
        @click="isMenuOpen = false"
      >Staff</RouterLink>
      <RouterLink
        to="/manager/customer"
        class="block hover:text-red-600"
        @click="isMenuOpen = false"
      >Customers</RouterLink>
      <RouterLink
        to="/manager/attendance"
        class="block hover:text-red-600"
        @click="isMenuOpen = false"
      >Attendance</RouterLink>
      <RouterLink
        to="/manager/product"
        class="block hover:text-red-600"
        @click="isMenuOpen = false"
      >Products</RouterLink>
      <RouterLink
        to="/manager/coupon"
        class="block hover:text-red-600"
        @click="isMenuOpen = false"
      >Coupons</RouterLink>
      <RouterLink
        to="/manager/membership"
        class="block hover:text-red-600"
        @click="isMenuOpen = false"
      >Memberships</RouterLink>

      <hr class="border-gray-700 my-2" />

      <template v-if="authStore.user">
        <RouterLink
          to="/manager/profile"
          class="block hover:text-red-600"
          @click="isMenuOpen = false"
        >Profile</RouterLink>
        <RouterLink
          to="/manager/setting"
          class="block hover:text-red-600"
          @click="isMenuOpen = false"
        >Settings</RouterLink>
        <button
          @click="handleLogout"
          class="block text-left w-full hover:text-red-600"
        >
          Logout
        </button>
      </template>

      <template v-else>
        <RouterLink
          :to="{ name: 'login' }"
          class="block hover:text-red-600"
          @click="isMenuOpen = false"
        >Login</RouterLink>
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
