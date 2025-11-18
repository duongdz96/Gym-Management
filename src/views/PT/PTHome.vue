<script setup>
import { RouterLink } from 'vue-router';
</script>

<template>
  <div class="p-6 bg-gray-100 min-h-screen">
    <div class="max-w-7xl mx-auto">
      <!-- Header -->
      <div class="text-center mb-12">
        <h1 class="text-4xl font-bold text-gray-900 mb-4">PT Dashboard</h1>
        <p class="text-xl text-gray-600">Welcome to your Personal Trainer workspace</p>
      </div>

      <!-- Quick Actions Grid -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        <!-- My Schedule -->
        <RouterLink
          to="/pt/schedule"
          class="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow duration-300 group"
        >
          <div class="flex items-center justify-center w-16 h-16 bg-blue-100 rounded-full mb-4 group-hover:bg-blue-200 transition-colors">
            <svg class="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">My Schedule</h3>
          <p class="text-gray-600">View and manage your training sessions</p>
        </RouterLink>

        <!-- My Members -->
        <RouterLink
          to="/pt/members"
          class="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow duration-300 group"
        >
          <div class="flex items-center justify-center w-16 h-16 bg-green-100 rounded-full mb-4 group-hover:bg-green-200 transition-colors">
            <svg class="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">My Members</h3>
          <p class="text-gray-600">Manage your assigned members and their progress</p>
        </RouterLink>

        <!-- Training History -->
        <RouterLink
          to="/pt/history"
          class="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow duration-300 group"
        >
          <div class="flex items-center justify-center w-16 h-16 bg-purple-100 rounded-full mb-4 group-hover:bg-purple-200 transition-colors">
            <svg class="w-8 h-8 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">Training History</h3>
          <p class="text-gray-600">Review past sessions and member check-ins</p>
        </RouterLink>

        <!-- Profile Settings -->
        <RouterLink
          to="/pt/profile"
          class="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow duration-300 group"
        >
          <div class="flex items-center justify-center w-16 h-16 bg-orange-100 rounded-full mb-4 group-hover:bg-orange-200 transition-colors">
            <svg class="w-8 h-8 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">My Profile</h3>
          <p class="text-gray-600">Update your personal information and availability</p>
        </RouterLink>

        <!-- Today's Sessions -->
        <div class="bg-white rounded-xl shadow-lg p-6">
          <div class="flex items-center justify-center w-16 h-16 bg-cyan-100 rounded-full mb-4">
            <svg class="w-8 h-8 text-cyan-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">Today's Sessions</h3>
          <p class="text-gray-600">You have 3 sessions scheduled today</p>
          <div class="mt-4 space-y-2">
            <div class="text-sm text-gray-500">• 9:00 AM - John Doe</div>
            <div class="text-sm text-gray-500">• 2:00 PM - Jane Smith</div>
            <div class="text-sm text-gray-500">• 5:00 PM - Mike Johnson</div>
          </div>
        </div>

        <!-- Weekly Stats -->
        <div class="bg-white rounded-xl shadow-lg p-6">
          <div class="flex items-center justify-center w-16 h-16 bg-indigo-100 rounded-full mb-4">
            <svg class="w-8 h-8 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">This Week</h3>
          <div class="space-y-2">
            <div class="flex justify-between text-sm">
              <span class="text-gray-600">Sessions:</span>
              <span class="font-semibold text-indigo-600">12</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-gray-600">Members:</span>
              <span class="font-semibold text-indigo-600">8</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-gray-600">Hours:</span>
              <span class="font-semibold text-indigo-600">24</span>
            </div>
          </div>
        </div>

        <!-- Member Progress -->
        <div class="bg-white rounded-xl shadow-lg p-6">
          <div class="flex items-center justify-center w-16 h-16 bg-teal-100 rounded-full mb-4">
            <svg class="w-8 h-8 text-teal-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">Member Progress</h3>
          <p class="text-gray-600">Track your members' fitness goals</p>
          <div class="mt-4 space-y-2">
            <div class="text-sm text-gray-500">• Sarah: Lost 2kg this month</div>
            <div class="text-sm text-gray-500">• Tom: Improved bench press by 10kg</div>
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
            <button class="w-full text-left text-sm text-blue-600 hover:text-blue-800">Start Session</button>
            <button class="w-full text-left text-sm text-blue-600 hover:text-blue-800">Log Workout</button>
            <button class="w-full text-left text-sm text-blue-600 hover:text-blue-800">Send Message</button>
          </div>
        </div>
      </div>

      <!-- Footer Stats or Info -->
      <div class="mt-12 text-center">
        <p class="text-gray-500">Select a section above to start your PT session</p>
      </div>
    </div>
  </div>
</template>