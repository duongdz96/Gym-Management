<script setup>
import { ref } from "vue";
import { useRoute, RouterLink, useRouter } from "vue-router";
import { useAuthStore } from "@/stores/useAuthStore";
import { UserIcon } from "lucide-vue-next";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const isAccountOpen = ref(false);

const openAccountMenu = () => (isAccountOpen.value = true);
const closeAccountMenu = () => (isAccountOpen.value = false);

const handleLogout = () => {
  authStore.logout(); // Xóa thông tin user + token khỏi Pinia/localStorage
  router.push("/login");
};
</script>

<template>
  <header class="w-full bg-stone-900 relative z-50 shadow">
    <div class="mx-auto max-w-7xl px-6 py-3 flex items-center justify-between">
      <!-- Logo -->
      <div class="flex items-center gap-3">
        <RouterLink to="/customer" class="inline-flex items-center gap-2">
          <span class="h-8 w-8 rounded-full bg-red-600 inline-block"></span>
          <span class="font-bold text-white tracking-wider uppercase">
            Gym Management
          </span>
        </RouterLink>
      </div>

      <!-- Navigation -->
      <nav class="flex items-center gap-6 text-sm font-medium">
        <RouterLink
          to="/customer/dashboard"
          class="uppercase tracking-wider hover:text-red-600 transition"
          :class="route.path === '/customer/dashboard'
            ? 'text-red-600 border-b-2 border-red-600 pb-1'
            : 'text-white'"
        >
          Dashboard
        </RouterLink>

        <RouterLink
          to="/customer/membership"
          class="uppercase tracking-wider hover:text-red-600 transition"
          :class="route.path === '/customer/membership'
            ? 'text-red-600 border-b-2 border-red-600 pb-1'
            : 'text-white'"
        >
          Membership
        </RouterLink>

        <RouterLink
          to="/customer/class"
          class="uppercase tracking-wider hover:text-red-600 transition"
          :class="route.path === '/customer/class'
            ? 'text-red-600 border-b-2 border-red-600 pb-1'
            : 'text-white'"
        >
          Classes
        </RouterLink>

        <RouterLink
          to="/customer/billhistory"
          class="uppercase tracking-wider hover:text-red-600 transition"
          :class="route.path === '/customer/billhistory'
            ? 'text-red-600 border-b-2 border-red-600 pb-1'
            : 'text-white'"
        >
          Bill History
        </RouterLink>

        <RouterLink
          to="/customer/plan"
          class="uppercase tracking-wider hover:text-red-600 transition"
          :class="route.path === '/customer/plan'
            ? 'text-red-600 border-b-2 border-red-600 pb-1'
            : 'text-white'"
        >
          Training Plan
        </RouterLink>

        <RouterLink
          to="/customer/coupon"
          class="uppercase tracking-wider hover:text-red-600 transition"
          :class="route.path === '/customer/coupon'
            ? 'text-red-600 border-b-2 border-red-600 pb-1'
            : 'text-white'"
        >
          Coupons
        </RouterLink>

        <!-- Account dropdown -->
        
      </nav>
      <div
          class="relative"
          @mouseenter="openAccountMenu"
          @mouseleave="closeAccountMenu"
        >
          <button
            class="flex items-center gap-2 text-white uppercase tracking-wider hover:text-red-600 transition"
          >
            <UserIcon class="w-5 h-5" />
            <span>Account</span>
          </button>

          <transition name="fade">
            <div
              v-if="isAccountOpen"
              class="absolute right-0 mt-2 w-44 bg-white rounded-md shadow-lg border border-gray-200 z-50"
            >
              <RouterLink
                to="/profile"
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
                class="block w-full text-left px-4 py-2 text-gray-800 hover:bg-gray-100"
                @click="handleLogout"
              >
                Logout
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
