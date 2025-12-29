<script setup>
import { ref, onMounted, computed } from "vue";
import api from "@/services/api";
import { useAuthStore } from "@/stores/useAuthStore";
import { useToast } from "vue-toastification";

const authStore = useAuthStore();
const toast = useToast();

const profile = ref(null);
const isLoading = ref(true);

// Hàm format tiền tệ
const formatCurrency = (value) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

onMounted(async () => {
  try {
    isLoading.value = true;
    const res = await api.get("/pts");
    const staffs = Array.isArray(res.data) ? res.data : [];
    
    // Lưu ý: Logic này đang lấy toàn bộ danh sách rồi mới lọc client-side (xem phần nhận xét bên dưới)
    const currentStaff = staffs.find(staff => staff.id === authStore.user.id);
    
    if (currentStaff) {
      profile.value = {
        id: currentStaff.id,
        fullName: currentStaff.fullName,
        email: currentStaff.email,
        phone: currentStaff.phone,
        dob: new Date(currentStaff.dob).toLocaleDateString('vi-VN'), // Format ngày kiểu VN
        gender: currentStaff.gender,
        position: currentStaff.position,
        specialize: currentStaff.specialize,
        hirePrice: currentStaff.hirePrice,
        avatar: 'https://randomuser.me/api/portraits/men/32.jpg', // Có thể thay bằng logic ảnh thật
      };
    }
  } catch (error) {
    toast.error("Không thể tải thông tin hồ sơ.");
  } finally {
    isLoading.value = false;
  }
});
</script>

<template>
  <div class="max-w-4xl mx-auto p-6">
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-bold text-gray-800">Hồ Sơ Cá Nhân</h1>
      <button 
        v-if="profile"
        class="flex items-center gap-2 bg-indigo-600 text-white px-4 py-2 rounded-lg hover:bg-indigo-700 transition shadow-sm font-medium text-sm"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
        </svg>
        Chỉnh sửa
      </button>
    </div>

    <div v-if="isLoading" class="animate-pulse bg-white shadow-lg rounded-2xl overflow-hidden">
      <div class="h-32 bg-gray-200"></div>
      <div class="px-8 pb-8">
        <div class="relative flex items-end -mt-12 mb-6">
          <div class="w-24 h-24 bg-gray-300 rounded-full border-4 border-white"></div>
        </div>
        <div class="space-y-4">
          <div class="h-6 bg-gray-200 rounded w-1/4"></div>
          <div class="h-4 bg-gray-200 rounded w-1/2"></div>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mt-6">
             <div class="h-24 bg-gray-100 rounded-xl"></div>
             <div class="h-24 bg-gray-100 rounded-xl"></div>
          </div>
        </div>
      </div>
    </div>

    <div v-else-if="profile" class="bg-white shadow-xl rounded-2xl overflow-hidden border border-gray-100">
      
      <div class="h-32 bg-gradient-to-r from-indigo-500 to-purple-600 relative"></div>

      <div class="px-8 pb-8">
        <div class="relative flex flex-col md:flex-row md:items-end -mt-12 mb-8 gap-4">
          <img
            :src="profile.avatar"
            alt="Avatar"
            class="w-24 h-24 rounded-full border-4 border-white shadow-md object-cover bg-white"
          />
          <div class="mb-1">
            <h2 class="text-2xl font-bold text-gray-900">{{ profile.fullName }}</h2>
            <p class="text-indigo-600 font-medium">{{ profile.position }}</p>
          </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
          
          <div class="space-y-6">
            <h3 class="text-gray-900 font-semibold border-b pb-2">Thông tin liên hệ</h3>
            <ul class="space-y-4">
              <li class="flex items-center gap-3 text-gray-600">
                <div class="p-2 bg-indigo-50 text-indigo-600 rounded-lg">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" /></svg>
                </div>
                <span>{{ profile.email }}</span>
              </li>
              <li class="flex items-center gap-3 text-gray-600">
                <div class="p-2 bg-indigo-50 text-indigo-600 rounded-lg">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" /></svg>
                </div>
                <span>{{ profile.phone }}</span>
              </li>
              <li class="flex items-center gap-3 text-gray-600">
                <div class="p-2 bg-indigo-50 text-indigo-600 rounded-lg">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" /></svg>
                </div>
                <span>{{ profile.gender }} - {{ profile.dob }}</span>
              </li>
            </ul>
          </div>

          <div class="space-y-6">
            <h3 class="text-gray-900 font-semibold border-b pb-2">Thông tin công việc</h3>
            <div class="bg-gray-50 rounded-xl p-4 space-y-4 border border-gray-100">
              <div>
                <span class="text-sm text-gray-500 block mb-1">Chuyên môn chính</span>
                <span class="inline-block bg-purple-100 text-purple-700 px-3 py-1 rounded-full text-sm font-medium">
                  {{ profile.specialize }}
                </span>
              </div>
              
              <div>
                <span class="text-sm text-gray-500 block mb-1">Giá thuê (mỗi giờ/buổi)</span>
                <span class="text-xl font-bold text-green-600">
                  {{ formatCurrency(profile.hirePrice) }}
                </span>
              </div>
            </div>
          </div>

        </div>
      </div>
    </div>

    <div v-else class="text-center py-12 bg-white rounded-xl shadow-sm">
      <p class="text-gray-500">Không tìm thấy thông tin hồ sơ.</p>
    </div>
  </div>
</template>