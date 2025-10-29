<script setup>
import { RouterLink } from "vue-router";
</script>

<template>
  <div class="mx-auto max-w-7xl px-6 py-8">
    <h1 class="text-2xl font-bold text-gray-800 mb-6">
      Welcome back, Receptionist 👋
    </h1>

    <!-- Cards -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <!-- View Memberships Card -->
      <div
        class="bg-white shadow-md rounded-xl p-6 hover:shadow-lg transition-shadow"
      >
        <div class="flex items-center justify-between">
          <div>
            <h3 class="text-lg font-semibold text-gray-800">View Memberships</h3>
            <p class="text-gray-500 text-sm">
              View and manage customer memberships
            </p>
          </div>
          <div class="text-green-500">
            <svg
              class="w-8 h-8"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"
              ></path>
            </svg>
          </div>
        </div>
        <div class="mt-4">
          <RouterLink
            :to="{ name: 'reception.memberships' }"
            class="inline-flex items-center px-4 py-2 bg-green-500 text-white text-sm font-medium rounded-lg hover:bg-green-600 transition"
          >
            Xem Danh Sách
          </RouterLink>
        </div>
      </div>

      <!-- Add Membership Card -->
      <div
        class="bg-white shadow-md rounded-xl p-6 hover:shadow-lg transition-shadow"
      >
        <div class="flex items-center justify-between">
          <div>
            <h3 class="text-lg font-semibold text-gray-800">Add Membership</h3>
            <p class="text-gray-500 text-sm">
              Register a new membership for a customer
            </p>
          </div>
          <div class="text-blue-500">
            <svg
              class="w-8 h-8"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M12 6v6m0 0v6m0-6h6m-6 0H6"
              ></path>
            </svg>
          </div>
        </div>
        <div class="mt-4">
          <RouterLink
            :to="{ name: 'add-membership' }"
            class="inline-flex items-center px-4 py-2 bg-blue-500 text-white text-sm font-medium rounded-lg hover:bg-blue-600 transition"
          >
            Thêm Mới
          </RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>
