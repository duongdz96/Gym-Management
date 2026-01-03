<template>
  <div class="p-6 space-y-6">
    <h1 class="text-2xl font-semibold text-gray-800">Cài Đặt Tài Khoản</h1>

    <div class="bg-white shadow-md rounded-xl p-6 max-w-3xl space-y-6">
      <!-- Thay đổi mật khẩu -->
      <div>
        <h2 class="text-lg font-bold text-gray-700 mb-3">Thay đổi mật khẩu</h2>
        <div class="space-y-3">
          <input
            v-model="settings.currentPassword"
            type="password"
            placeholder="Mật khẩu hiện tại"
            class="w-full border rounded-lg p-2"
          />
          <input
            v-model="settings.newPassword"
            type="password"
            placeholder="Mật khẩu mới"
            class="w-full border rounded-lg p-2"
          />
          <input
            v-model="settings.confirmPassword"
            type="password"
            placeholder="Nhập lại mật khẩu mới"
            class="w-full border rounded-lg p-2"
          />
          <button
            class="bg-emerald-500 text-white px-4 py-2 rounded-lg hover:bg-emerald-600 transition"
          >
            Lưu thay đổi
          </button>
        </div>
      </div>

      <hr />

      <!-- Cài đặt thông báo -->
      <div>
        <h2 class="text-lg font-bold text-gray-700 mb-3">Cài đặt thông báo</h2>
        <div class="space-y-2">
          <label class="flex items-center space-x-2">
            <input
              type="checkbox"
              v-model="settings.emailNotification"
            />
            <span>Nhận thông báo qua email</span>
          </label>
          <label class="flex items-center space-x-2">
            <input
              type="checkbox"
              v-model="settings.smsNotification"
            />
            <span>Nhận thông báo qua SMS</span>
          </label>
        </div>
      </div>

      <hr />

      <!-- Xóa tài khoản -->
      <div>
        <h2 class="text-lg font-bold text-gray-700 mb-3">Xóa tài khoản</h2>
        <p class="text-gray-500 mb-3">
          Nếu bạn không muốn sử dụng dịch vụ nữa, bạn có thể xóa tài khoản vĩnh viễn.
        </p>
        <button
          class="bg-red-500 text-white px-4 py-2 rounded-lg hover:bg-red-600 transition"
        >
          Xóa tài khoản
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'

const settings = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
  emailNotification: true,
  smsNotification: false,
})
</script>
