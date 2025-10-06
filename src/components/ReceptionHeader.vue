<script setup>
import { RouterLink } from "vue-router";
</script>

<template>
  <header class="w-full bg-stone-900">
    <div class="mx-auto max-w-7xl px-6 py-3 flex items-center justify-between">
      <!-- Logo -->
      <div class="flex items-center gap-3">
        <RouterLink to="/reception" class="inline-flex items-center gap-2">
          <span class="h-8 w-8 rounded-full bg-red-600 inline-block"></span>
          <span class="font-bold text-white tracking-wider uppercase">Gym Management</span>
        </RouterLink>
      </div>

      <!-- Navigation -->
      <nav class="hidden md:flex items-center gap-6 text-sm font-medium">
        <RouterLink :to="{name: 'salesselect'}" class="text-white uppercase tracking-wider hover:text-red-600">Sales</RouterLink>
        <a href="#customermanagement" class="text-white uppercase tracking-wider hover:text-red-600">Customer Management</a>
        <a href="#attendancehistory" class="text-white uppercase tracking-wider hover:text-red-600">Attendance History</a>
      </nav>
      <div>
        <a href="#account" class="text-white uppercase tracking-wider hover:text-red-600">Account</a>
      </div>
    </div>
  </header>
</template>
