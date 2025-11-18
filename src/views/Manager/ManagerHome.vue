<script setup>
import { RouterLink } from 'vue-router';
</script>

<template>
  <div class="p-6 bg-gray-100 min-h-screen">
    <div class="max-w-7xl mx-auto">
      <!-- Header -->
      <div class="text-center mb-12">
        <h1 class="text-4xl font-bold text-gray-900 mb-4">Manager Dashboard</h1>
        <p class="text-xl text-gray-600">Welcome to the Gym Management System</p>
      </div>

      <!-- Quick Actions Grid -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        <!-- Staff Management -->
        <RouterLink
          to="/manager/staff"
          class="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow duration-300 group"
        >
          <div class="flex items-center justify-center w-16 h-16 bg-blue-100 rounded-full mb-4 group-hover:bg-blue-200 transition-colors">
            <svg class="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197m13.5-9a2.5 2.5 0 11-5 0 2.5 2.5 0 015 0z"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">Staff Management</h3>
          <p class="text-gray-600">Manage staff members, roles, and information</p>
        </RouterLink>

        <!-- Customer Management -->
        <RouterLink
          to="/manager/customer"
          class="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow duration-300 group"
        >
          <div class="flex items-center justify-center w-16 h-16 bg-green-100 rounded-full mb-4 group-hover:bg-green-200 transition-colors">
            <svg class="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">Customer Management</h3>
          <p class="text-gray-600">View and manage customer information</p>
        </RouterLink>

        <!-- Attendance Tracking -->
        <RouterLink
          to="/manager/attendance"
          class="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow duration-300 group"
        >
          <div class="flex items-center justify-center w-16 h-16 bg-purple-100 rounded-full mb-4 group-hover:bg-purple-200 transition-colors">
            <svg class="w-8 h-8 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">Attendance Tracking</h3>
          <p class="text-gray-600">Monitor check-in and check-out records</p>
        </RouterLink>

        <!-- Product Management -->
        <RouterLink
          to="/manager/product"
          class="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow duration-300 group"
        >
          <div class="flex items-center justify-center w-16 h-16 bg-orange-100 rounded-full mb-4 group-hover:bg-orange-200 transition-colors">
            <svg class="w-8 h-8 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">Product Management</h3>
          <p class="text-gray-600">Manage gym products and inventory</p>
        </RouterLink>

        <!-- Coupon Management -->
        <RouterLink
          to="/manager/coupon"
          class="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow duration-300 group"
        >
          <div class="flex items-center justify-center w-16 h-16 bg-pink-100 rounded-full mb-4 group-hover:bg-pink-200 transition-colors">
            <svg class="w-8 h-8 text-pink-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">Coupon Management</h3>
          <p class="text-gray-600">Create and manage discount coupons</p>
        </RouterLink>

        <!-- Membership Management -->
        <RouterLink
          to="/manager/membership"
          class="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow duration-300 group"
        >
          <div class="flex items-center justify-center w-16 h-16 bg-indigo-100 rounded-full mb-4 group-hover:bg-indigo-200 transition-colors">
            <svg class="w-8 h-8 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">Membership Management</h3>
          <p class="text-gray-600">Manage membership plans and tiers</p>
        </RouterLink>

        <!-- Class Management -->
        <RouterLink
          to="/manager/classtemplate"
          class="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow duration-300 group"
        >
          <div class="flex items-center justify-center w-16 h-16 bg-teal-100 rounded-full mb-4 group-hover:bg-teal-200 transition-colors">
            <svg class="w-8 h-8 text-teal-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">Class Management</h3>
          <p class="text-gray-600">Manage class templates and schedules</p>
        </RouterLink>

        <!-- Banner Management -->
        <RouterLink
          to="/manager/banner"
          class="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow duration-300 group"
        >
          <div class="flex items-center justify-center w-16 h-16 bg-yellow-100 rounded-full mb-4 group-hover:bg-yellow-200 transition-colors">
            <svg class="w-8 h-8 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5.882V19.24a1.76 1.76 0 01-3.417.592l-2.147-6.15M18 13a3 3 0 100-6M5.436 13.683A4.001 4.001 0 017 6h1.832c4.1 0 7.625-1.234 9.168-3v14c-1.543-1.766-5.067-3-9.168-3H7a3.988 3.988 0 01-1.564-.317z"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">Banner Management</h3>
          <p class="text-gray-600">Manage promotional banners</p>
        </RouterLink>

        <!-- Statistics -->
        <RouterLink
          to="/manager/statistics"
          class="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow duration-300 group"
        >
          <div class="flex items-center justify-center w-16 h-16 bg-cyan-100 rounded-full mb-4 group-hover:bg-cyan-200 transition-colors">
            <svg class="w-8 h-8 text-cyan-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">Statistics</h3>
          <p class="text-gray-600">View customer and gym statistics</p>
        </RouterLink>
      </div>

      <!-- Footer Stats or Info -->
      <div class="mt-12 text-center">
        <p class="text-gray-500">Select a section above to start managing your gym operations</p>
      </div>
    </div>
  </div>
</template>