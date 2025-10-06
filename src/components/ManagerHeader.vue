<script setup>
import { ref } from "vue";
import { useRoute, RouterLink } from "vue-router";

const route = useRoute();
const isMenuOpen = ref(false);
</script>

<template>
  <header class="w-full bg-stone-900">
    <div class="mx-auto max-w-7xl px-6 py-3 flex items-center justify-between">
      <!-- Logo -->
      <div class="flex items-center gap-3">
        <RouterLink to="/manager" class="inline-flex items-center gap-2">
          <span class="h-8 w-8 rounded-full bg-red-600 inline-block"></span>
          <span class="font-bold text-white tracking-wider uppercase">Gym Management</span>
        </RouterLink>
      </div>

      <!-- Navigation (desktop) -->
      <nav class="hidden md:flex items-center gap-6 text-sm font-medium">
        <RouterLink
          to="/manager/staff"
          class="uppercase tracking-wider hover:text-red-600"
          :class="route.path === '/manager/staff' ? 'text-red-600 border-b-2 border-red-600 pb-1' : 'text-white'"
        >Staff</RouterLink>

        <RouterLink
          to="/manager/customer"
          class="uppercase tracking-wider hover:text-red-600"
          :class="route.path === '/manager/customer' ? 'text-red-600 border-b-2 border-red-600 pb-1' : 'text-white'"
        >Customers</RouterLink>

        <RouterLink
          to="/manager/attendance"
          class="uppercase tracking-wider hover:text-red-600"
          :class="route.path === '/manager/attendance' ? 'text-red-600 border-b-2 border-red-600 pb-1' : 'text-white'"
        >Attendance</RouterLink>

        <RouterLink
          to="/manager/product"
          class="uppercase tracking-wider hover:text-red-600"
          :class="route.path === '/manager/product' ? 'text-red-600 border-b-2 border-red-600 pb-1' : 'text-white'"
        >Products</RouterLink>

        <RouterLink
          to="/manager/coupon"
          class="uppercase tracking-wider hover:text-red-600"
          :class="route.path === '/manager/coupon' ? 'text-red-600 border-b-2 border-red-600 pb-1' : 'text-white'"
        >Coupons</RouterLink>
      </nav>

      <!-- Account dropdown -->
      <div class="relative hidden md:block group">
        <button class="text-white uppercase tracking-wider hover:text-red-600">Account</button>
        <div
          class="absolute right-0 mt-2 w-44 bg-white rounded shadow-md hidden group-hover:block z-50"
        >
          <RouterLink
            to="/manager/profile"
            class="block px-4 py-2 text-gray-800 hover:bg-gray-100"
          >Profile</RouterLink>
          <RouterLink
            to="/manager/setting"
            class="block px-4 py-2 text-gray-800 hover:bg-gray-100"
          >Settings</RouterLink>
          <button
            class="block w-full text-left px-4 py-2 text-gray-800 hover:bg-gray-100"
          >Logout</button>
        </div>
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
      <RouterLink to="/manager/staff" class="block hover:text-red-600" @click="isMenuOpen = false">Staff</RouterLink>
      <RouterLink to="/manager/customer" class="block hover:text-red-600" @click="isMenuOpen = false">Customers</RouterLink>
      <RouterLink to="/manager/attendance" class="block hover:text-red-600" @click="isMenuOpen = false">Attendance</RouterLink>
      <RouterLink to="/manager/product" class="block hover:text-red-600" @click="isMenuOpen = false">Products</RouterLink>
      <RouterLink to="/manager/coupon" class="block hover:text-red-600" @click="isMenuOpen = false">Coupons</RouterLink>

      <hr class="border-gray-700 my-2" />
      <RouterLink to="/manager/profile" class="block hover:text-red-600" @click="isMenuOpen = false">Profile</RouterLink>
      <RouterLink to="/manager/setting" class="block hover:text-red-600" @click="isMenuOpen = false">Settings</RouterLink>
      <button class="block text-left w-full hover:text-red-600">Logout</button>
    </div>
  </header>
</template>
