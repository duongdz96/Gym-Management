<script setup lang="ts">
import { ref } from 'vue';
import api from '@/services/api';
import { useToast } from "vue-toastification";
import { useRouter } from 'vue-router';

const toast = useToast();
const router = useRouter();

// Form data
const oldPassword = ref('');
const newPassword = ref('');
const confirmPassword = ref('');

// Loading state
const isSubmitting = ref(false);

// Submit handler
const handleChangePassword = async () => {
  if (!oldPassword.value || !newPassword.value || !confirmPassword.value) {
    toast.error("Vui lòng điền đầy đủ thông tin");
    return;
  }

  if (newPassword.value !== confirmPassword.value) {
    toast.error("Xác nhận mật khẩu không khớp");
    return;
  }

  isSubmitting.value = true;

  try {
    await api.put('/auth/change-password', {
      oldPassword: oldPassword.value,
      newPassword: newPassword.value,
      confirmPassword: confirmPassword.value
    });

    toast.success("Đổi mật khẩu thành công!");
    // Reset form
    oldPassword.value = '';
    newPassword.value = '';
    confirmPassword.value = '';
    // Optionally redirect
    router.push('/');
  } catch (error: any) {
    if (error.response?.data) {
      toast.error('Có lỗi xảy ra');
    } else {
      toast.error("Đổi mật khẩu thất bại");
    }
  } finally {
    isSubmitting.value = false;
  }
};
</script>

<template>
  <div class="max-w-md mx-auto mt-10 p-6 bg-white rounded-lg shadow-md">
    <h2 class="text-2xl font-bold mb-6 text-center">Đổi mật khẩu</h2>

    <form @submit.prevent="handleChangePassword">
      <div class="mb-4">
        <label class="block mb-1 font-semibold">Mật khẩu cũ</label>
        <input
          type="password"
          v-model="oldPassword"
          class="w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-red-500"
          placeholder="Nhập mật khẩu cũ"
        />
      </div>

      <div class="mb-4">
        <label class="block mb-1 font-semibold">Mật khẩu mới</label>
        <input
          type="password"
          v-model="newPassword"
          class="w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-red-500"
          placeholder="Nhập mật khẩu mới"
        />
      </div>

      <div class="mb-6">
        <label class="block mb-1 font-semibold">Xác nhận mật khẩu mới</label>
        <input
          type="password"
          v-model="confirmPassword"
          class="w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-red-500"
          placeholder="Xác nhận mật khẩu mới"
        />
      </div>

      <button
        type="submit"
        :disabled="isSubmitting"
        class="w-full bg-red-600 text-white font-semibold py-2 px-4 rounded-md hover:bg-black transition-colors"
      >
        {{ isSubmitting ? "Đang xử lý..." : "Đổi mật khẩu" }}
      </button>
    </form>
  </div>
</template>
