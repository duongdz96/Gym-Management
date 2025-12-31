<template>
  <div class="p-3 sm:p-6 space-y-4 sm:space-y-6">
    <h1 class="text-xl sm:text-2xl font-semibold text-gray-800">Hồ Sơ Cá Nhân</h1>

    <div class="bg-white shadow-md rounded-xl p-4 sm:p-6 max-w-3xl">
      <div class="flex flex-col sm:flex-row items-center sm:items-start gap-4 sm:gap-6">
        <img
          :src="profile.avatar"
          alt="Avatar"
          class="w-20 h-20 sm:w-24 sm:h-24 rounded-full border object-cover"
        />
        <div class="text-center sm:text-left">
          <h2 class="text-lg sm:text-xl font-bold text-gray-800">{{ profile.name }}</h2>
          <p class="text-gray-500 text-sm sm:text-base">{{ profile.email }}</p>
          <p class="text-gray-500 text-sm sm:text-base">Số điện thoại: {{ profile.phone }}</p>
        </div>
      </div>

      <div class="mt-4 sm:mt-6 border-t pt-4 space-y-2 text-gray-700 text-sm sm:text-base">
        <p><span class="font-medium">Giới tính:</span> {{ profile.gender }}</p>
        <p><span class="font-medium">Ngày sinh:</span> {{ profile.birthdate }}</p>
        <p><span class="font-medium">Địa chỉ:</span> {{ profile.address }}</p>
        <p><span class="font-medium">Thành viên từ:</span> {{ profile.joinedAt }}</p>
      </div>

      <div class="mt-4 sm:mt-6">
        <button
          class="w-full sm:w-auto bg-emerald-500 text-white px-4 py-2 rounded-lg hover:bg-emerald-600 transition text-sm sm:text-base"
        >
          Chỉnh sửa hồ sơ
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// Fake profile data
const profile = ref({
  name: 'Nguyễn Văn A',
  email: 'nguyenvana@example.com',
  phone: '0987 654 321',
  gender: 'Nam',
  birthdate: '1995-04-12',
  address: '123 Đường Lê Lợi, Quận 1, TP.HCM',
  joinedAt: '2023-01-15',
  avatar: 'https://randomuser.me/api/portraits/men/32.jpg',
})
</script>
