<script setup>
import { ref } from "vue";
import { RouterLink, useRouter } from "vue-router";
import { useAuthStore } from "@/stores/useAuthStore";
import { UserIcon } from "lucide-vue-next";

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
  <header class="w-full bg-stone-900 shadow-md relative z-50">
    <div class="mx-auto max-w-7xl px-6 py-3 flex items-center justify-between">
      <!-- Logo -->
      <div class="flex items-center gap-3">
        <RouterLink
          to="/reception"
          class="inline-flex items-center gap-2 hover:opacity-90 transition"
        >
          <span class="h-8 w-8 rounded-full bg-red-600 inline-block"></span>
          <span class="font-bold text-white tracking-wider uppercase">
            Gym Management
          </span>
        </RouterLink>
      </div>

      <!-- Navigation -->
      <nav class="flex items-center gap-6 text-sm font-medium">
        <RouterLink
          :to="{ name: 'salesselect' }"
          class="text-white uppercase tracking-wider hover:text-red-500 transition"
        >
          Products Sales
        </RouterLink>

        <RouterLink
          :to="{ name: 'add-membership' }"
          class="text-white uppercase tracking-wider hover:text-red-500 transition"
        >
          Add Membership
        </RouterLink>

        <RouterLink
          to="/reception/class-registration"
          class="text-white uppercase tracking-wider hover:text-red-500 transition"
        >
          Class Registration
        </RouterLink>

        <RouterLink
          to="/customer-management"
          class="text-white uppercase tracking-wider hover:text-red-500 transition"
        >
          Customer Management
        </RouterLink>

        <RouterLink
          to="/attendance-history"
          class="text-white uppercase tracking-wider hover:text-red-500 transition"
        >
          Attendance History
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
          class="flex items-center gap-2 text-white hover:text-red-500 transition"
          @click="toggleAccountMenu"
        >
          <UserIcon class="w-5 h-5" />
          <span class="uppercase tracking-wider">Account</span>
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
              Change Profile
            </RouterLink>
            <RouterLink
              to="/change-password"
              class="block px-4 py-2 text-gray-800 hover:bg-gray-100"
              @click="isAccountMenuOpen = false"
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
