<script setup>
import { ref } from "vue";
import { useRoute, RouterLink } from "vue-router";

const route = useRoute();
const isMenuOpen = ref(false);
const isAccountOpen = ref(false);

const openAccountMenu = () => (isAccountOpen.value = true);
const closeAccountMenu = () => (isAccountOpen.value = false);
</script>

<template>
  <header class="w-full bg-stone-900 relative z-50">
    <div class="mx-auto max-w-7xl px-6 py-3 flex items-center justify-between">
      <!-- Logo -->
      <div class="flex items-center gap-3">
        <RouterLink to="/customer" class="inline-flex items-center gap-2">
          <span class="h-8 w-8 rounded-full bg-red-600 inline-block"></span>
          <span class="font-bold text-white tracking-wider uppercase">Gym Management</span>
        </RouterLink>
      </div>

      <!-- Navigation (desktop) -->
      <nav class="hidden md:flex items-center gap-6 text-sm font-medium">
        <RouterLink
          to="/customer/dashboard"
          class="uppercase tracking-wider hover:text-red-600"
          :class="route.path === '/customer/dashboard' ? 'text-red-600 border-b-2 border-red-600 pb-1' : 'text-white'"
        >Dashboard</RouterLink>

        <RouterLink
          to="/customer/membership"
          class="uppercase tracking-wider hover:text-red-600"
          :class="route.path === '/customer/membership' ? 'text-red-600 border-b-2 border-red-600 pb-1' : 'text-white'"
        >Membership</RouterLink>

        <RouterLink
          to="/customer/class"
          class="uppercase tracking-wider hover:text-red-600"
          :class="route.path === '/customer/class' ? 'text-red-600 border-b-2 border-red-600 pb-1' : 'text-white'"
        >Classes</RouterLink>

        <RouterLink
          to="/customer/plan"
          class="uppercase tracking-wider hover:text-red-600"
          :class="route.path === '/customer/plan' ? 'text-red-600 border-b-2 border-red-600 pb-1' : 'text-white'"
        >Training Plan</RouterLink>

        <RouterLink
          to="/customer/coupon"
          class="uppercase tracking-wider hover:text-red-600"
          :class="route.path === '/customer/coupon' ? 'text-red-600 border-b-2 border-red-600 pb-1' : 'text-white'"
        >Coupons</RouterLink>
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
              to="/customer/profile"
              class="block px-4 py-2 text-gray-800 hover:bg-gray-100"
            >Profile</RouterLink>
            <RouterLink
              to="/customer/setting"
              class="block px-4 py-2 text-gray-800 hover:bg-gray-100"
            >Settings</RouterLink>
            <button
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
        <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none"
          viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M4 6h16M4 12h16M4 18h16" />
        </svg>
      </button>
    </div>

    <!-- Mobile menu -->
    <div v-if="isMenuOpen" class="md:hidden bg-stone-800 text-white px-6 py-3 space-y-2">
      <RouterLink
        to="/customer/dashboard"
        class="block hover:text-red-600"
        @click="isMenuOpen = false"
      >Dashboard</RouterLink>
      <RouterLink
        to="/customer/membership"
        class="block hover:text-red-600"
        @click="isMenuOpen = false"
      >Membership</RouterLink>
      <RouterLink
        to="/customer/class"
        class="block hover:text-red-600"
        @click="isMenuOpen = false"
      >Classes</RouterLink>
      <RouterLink
        to="/customer/plan"
        class="block hover:text-red-600"
        @click="isMenuOpen = false"
      >Training Plan</RouterLink>
      <RouterLink
        to="/customer/coupon"
        class="block hover:text-red-600"
        @click="isMenuOpen = false"
      >Coupons</RouterLink>

      <hr class="border-gray-700 my-2" />

      <RouterLink
        to="/customer/profile"
        class="block hover:text-red-600"
        @click="isMenuOpen = false"
      >Profile</RouterLink>
      <RouterLink
        to="/customer/setting"
        class="block hover:text-red-600"
        @click="isMenuOpen = false"
      >Settings</RouterLink>
      <button class="block text-left w-full hover:text-red-600">Logout</button>
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
