<script setup>
import { RouterLink } from "vue-router";
</script>

<template>
  <header class="w-full bg-stone-900">
    <div class="mx-auto max-w-7xl px-6 py-3 flex items-center justify-between">
      <!-- Logo -->
      <div class="flex items-center gap-3">
        <RouterLink to="/customer" class="inline-flex items-center gap-2">
          <span class="h-8 w-8 rounded-full bg-red-600 inline-block"></span>
          <span class="font-bold text-white tracking-wider uppercase">Gym Management</span>
        </RouterLink>
      </div>

      <!-- Navigation -->
      <nav class="hidden md:flex items-center gap-6 text-sm font-medium">
        <a href="#dashboard" class="text-white uppercase tracking-wider hover:text-red-600">Dashboard</a>
        <a href="#membership" class="text-white uppercase tracking-wider hover:text-red-600">Membership</a>
        <a href="#classes" class="text-white uppercase tracking-wider hover:text-red-600">Classes</a>
        <a href="#plan" class="text-white uppercase tracking-wider hover:text-red-600">Training Plan</a>
        <a href="#coupons" class="text-white uppercase tracking-wider hover:text-red-600">Coupons</a>
      </nav>
      <div>
        <a href="#account" class="text-white uppercase tracking-wider hover:text-red-600">Account</a>
      </div>
    </div>
  </header>
</template>
