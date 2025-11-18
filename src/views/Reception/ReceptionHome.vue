<script setup>
import { RouterLink } from "vue-router";
</script>

<template>
  <div class="p-6 bg-gray-100 min-h-screen">
    <div class="max-w-7xl mx-auto">
      <!-- Header -->
      <div class="text-center mb-12">
        <h1 class="text-4xl font-bold text-gray-900 mb-4">Reception Dashboard</h1>
        <p class="text-xl text-gray-600">Welcome to the Front Desk workspace</p>
      </div>

      <!-- Quick Actions Grid -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        <!-- View Memberships -->
        <RouterLink
          to="/reception/memberships"
          class="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow duration-300 group"
        >
          <div class="flex items-center justify-center w-16 h-16 bg-green-100 rounded-full mb-4 group-hover:bg-green-200 transition-colors">
            <svg class="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">View Memberships</h3>
          <p class="text-gray-600">View and manage customer memberships</p>
        </RouterLink>

        <!-- Add Membership -->
        <RouterLink
          to="/reception/add-membership"
          class="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow duration-300 group"
        >
          <div class="flex items-center justify-center w-16 h-16 bg-blue-100 rounded-full mb-4 group-hover:bg-blue-200 transition-colors">
            <svg class="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">Add Membership</h3>
          <p class="text-gray-600">Register a new membership for a customer</p>
        </RouterLink>

        <!-- Product Sales -->
        <RouterLink
          to="/reception/salesselect"
          class="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow duration-300 group"
        >
          <div class="flex items-center justify-center w-16 h-16 bg-orange-100 rounded-full mb-4 group-hover:bg-orange-200 transition-colors">
            <svg class="w-8 h-8 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">Product Sales</h3>
          <p class="text-gray-600">Sell gym products and merchandise</p>
        </RouterLink>

        <!-- PT Booking -->
        <RouterLink
          to="/reception/salesbooking"
          class="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow duration-300 group"
        >
          <div class="flex items-center justify-center w-16 h-16 bg-purple-100 rounded-full mb-4 group-hover:bg-purple-200 transition-colors">
            <svg class="w-8 h-8 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">PT Sessions</h3>
          <p class="text-gray-600">Book personal training sessions</p>
        </RouterLink>

        <!-- Customer Check-in -->
        <div class="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow duration-300 group">
          <div class="flex items-center justify-center w-16 h-16 bg-cyan-100 rounded-full mb-4 group-hover:bg-cyan-200 transition-colors">
            <svg class="w-8 h-8 text-cyan-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">Check-in Desk</h3>
          <p class="text-gray-600">Process member check-ins and attendance</p>
          <button class="mt-4 w-full bg-cyan-600 text-white py-2 px-4 rounded-lg hover:bg-cyan-700 transition-colors">
            Start Check-in
          </button>
        </div>

        <!-- Today's Stats -->
        <div class="bg-white rounded-xl shadow-lg p-6">
          <div class="flex items-center justify-center w-16 h-16 bg-indigo-100 rounded-full mb-4">
            <svg class="w-8 h-8 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">Today's Summary</h3>
          <div class="space-y-2">
            <div class="flex justify-between text-sm">
              <span class="text-gray-600">Check-ins:</span>
              <span class="font-semibold text-indigo-600">47</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-gray-600">New Members:</span>
              <span class="font-semibold text-indigo-600">3</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-gray-600">Sales:</span>
              <span class="font-semibold text-indigo-600">$1,250</span>
            </div>
          </div>
        </div>

        <!-- Customer Support -->
        <div class="bg-white rounded-xl shadow-lg p-6">
          <div class="flex items-center justify-center w-16 h-16 bg-teal-100 rounded-full mb-4">
            <svg class="w-8 h-8 text-teal-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.364 5.636l-3.536 3.536m0 5.656l3.536 3.536M9.172 9.172L5.636 5.636m3.536 9.192L5.636 18.364M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">Customer Support</h3>
          <p class="text-gray-600">Help customers with inquiries and issues</p>
          <div class="mt-4 space-y-2">
            <button class="w-full text-left text-sm text-teal-600 hover:text-teal-800">Membership Questions</button>
            <button class="w-full text-left text-sm text-teal-600 hover:text-teal-800">Class Information</button>
            <button class="w-full text-left text-sm text-teal-600 hover:text-teal-800">Technical Support</button>
          </div>
        </div>

        <!-- Quick Actions -->
        <div class="bg-white rounded-xl shadow-lg p-6">
          <div class="flex items-center justify-center w-16 h-16 bg-pink-100 rounded-full mb-4">
            <svg class="w-8 h-8 text-pink-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">Quick Actions</h3>
          <div class="space-y-2">
            <button class="w-full text-left text-sm text-pink-600 hover:text-pink-800">Emergency Contact</button>
            <button class="w-full text-left text-sm text-pink-600 hover:text-pink-800">Facility Status</button>
            <button class="w-full text-left text-sm text-pink-600 hover:text-pink-800">Shift Handover</button>
          </div>
        </div>
      </div>

      <!-- Footer Stats or Info -->
      <div class="mt-12 text-center">
        <p class="text-gray-500">Select a section above to assist our gym members</p>
      </div>
    </div>
  </div>
</template>
