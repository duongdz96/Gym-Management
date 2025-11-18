<script setup>
import { ref } from "vue";
import { useRoute, useRouter, RouterLink } from "vue-router";
import { useAuthStore } from "@/stores/useAuthStore";
import { useToast } from "vue-toastification";

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
  <header class="w-full bg-stone-900 relative z-50">
    <div class="mx-auto max-w-7xl px-6 py-3 flex items-center justify-between">
      <!-- Logo -->
      <div class="flex items-center gap-3">
        <RouterLink to="/pt" class="inline-flex items-center gap-2">
          <span class="h-8 w-8 rounded-full bg-red-600 inline-block"></span>
          <span class="font-bold text-white tracking-wider uppercase"
            >Gym Management</span
          >
        </RouterLink>
      </div>

      <!-- Navigation (desktop) -->
      <nav class="hidden md:flex items-center gap-6 text-sm font-medium">
        <RouterLink
          to="/pt/schedule"
          class="uppercase tracking-wider hover:text-red-600"
          :class="
            route.path === '/pt/schedule'
              ? 'text-red-600 border-b-2 border-red-600 pb-1'
              : 'text-white'
          "
          >Schedule</RouterLink
        >

        <RouterLink
          to="/pt/profile"
          class="uppercase tracking-wider hover:text-red-600"
          :class="
            route.path === '/pt/profile'
              ? 'text-red-600 border-b-2 border-red-600 pb-1'
              : 'text-white'
          "
          >Profile</RouterLink
        >

        <RouterLink
          to="/pt/members"
          class="uppercase tracking-wider hover:text-red-600"
          :class="
            route.path === '/pt/members'
              ? 'text-red-600 border-b-2 border-red-600 pb-1'
              : 'text-white'
          "
          >Students</RouterLink
        >

        <RouterLink
          to="/pt/history"
          class="uppercase tracking-wider hover:text-red-600"
          :class="
            route.path === '/pt/history'
              ? 'text-red-600 border-b-2 border-red-600 pb-1'
              : 'text-white'
          "
          >History</RouterLink
        >

        <RouterLink
          to="/teacher/registerclass"
          class="uppercase tracking-wider hover:text-red-600"
          :class="
            route.path === '/teacher/registerclass'
              ? 'text-red-600 border-b-2 border-red-600 pb-1'
              : 'text-white'
          "
          >Register to teach</RouterLink
        >
      </nav>

      <!-- Account dropdown (desktop) -->
      <div
        class="relative hidden md:block"
        @mouseenter="openAccountMenu"
        @mouseleave="closeAccountMenu"
      >
        <button class="text-white uppercase tracking-wider hover:text-red-600">
          Account
        </button>

        <transition name="fade">
          <div
            v-if="isAccountOpen"
            class="absolute right-0 mt-2 w-44 bg-white rounded shadow-md z-50 border border-gray-200"
          >
            <RouterLink
              to="/pt/profile"
              class="block px-4 py-2 text-gray-800 hover:bg-gray-100"
              >Profile</RouterLink
            >
            <RouterLink
              to="/pt/setting"
              class="block px-4 py-2 text-gray-800 hover:bg-gray-100"
              >Settings</RouterLink
            >
            <button
            @click="handleLogout"
              class="block w-full text-left px-4 py-2 text-gray-800 hover:bg-gray-100"
            >
              Logout
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
        class="md:hidden bg-stone-800 px-6 py-4 border-t border-stone-700"
      >
        <nav class="flex flex-col gap-4">
          <RouterLink
            to="/pt/schedule"
            class="uppercase tracking-wider text-white hover:text-red-600"
            @click="isMenuOpen = false"
            >Schedule</RouterLink
          >

          <RouterLink
            to="/pt/profile"
            class="uppercase tracking-wider text-white hover:text-red-600"
            @click="isMenuOpen = false"
            >Profile</RouterLink
          >

          <RouterLink
            to="/pt/members"
            class="uppercase tracking-wider text-white hover:text-red-600"
            @click="isMenuOpen = false"
            >Students</RouterLink
          >

          <RouterLink
            to="/pt/history"
            class="uppercase tracking-wider text-white hover:text-red-600"
            @click="isMenuOpen = false"
            >History</RouterLink
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
