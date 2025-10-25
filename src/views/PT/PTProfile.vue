<script setup>
import { ref, onMounted } from "vue";

const profile = ref({
  name: "Nguyễn Văn PT",
  email: "pt@gym.com",
  phone: "0123-456-789",
  specialization: "Yoga & Fitness",
  experience: "5 năm",
  certifications: ["Yoga Instructor", "Personal Trainer"],
  birthDate: "15/05/1990",
  address: "Phường 1, Thành phố Hồ Chí Minh",
  avatar: "https://via.placeholder.com/150", // Placeholder avatar
});

onMounted(() => {
  console.log("PT Profile loaded");
});
</script>

<template>
  <div class="p-6 space-y-8">
    <!-- Tiêu đề -->
    <h1 class="text-3xl font-bold text-stone-800 mb-6">Thông Tin Cá Nhân</h1>

    <!-- Thông tin cá nhân -->
    <section class="bg-white rounded-xl shadow-lg p-8">
      <h2 class="text-2xl font-semibold mb-6 text-gray-800">Hồ sơ PT</h2>
      <div
        class="flex flex-col md:flex-row items-start space-y-6 md:space-y-0 md:space-x-8"
      >
        <!-- Avatar bên trái -->
        <div class="flex-shrink-0">
          <div
            class="w-32 h-40 rounded-lg border-4 border-gray-200 shadow-md"
            :style="{
              backgroundImage: `url(${profile.avatar})`,
              backgroundSize: 'cover',
              backgroundPosition: 'center',
              backgroundRepeat: 'no-repeat',
            }"
          ></div>
        </div>
        <!-- Thông tin bên phải -->
        <div class="flex-1 grid md:grid-cols-2 gap-6">
          <div>
            <label class="block text-sm font-medium text-gray-700"
              >Họ tên</label
            >
            <p class="mt-1 text-lg text-gray-900 font-medium">
              {{ profile.name }}
            </p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700">Email</label>
            <p class="mt-1 text-lg text-gray-900">{{ profile.email }}</p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700"
              >Số điện thoại</label
            >
            <p class="mt-1 text-lg text-gray-900">{{ profile.phone }}</p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700"
              >Ngày sinh</label
            >
            <p class="mt-1 text-lg text-gray-900">{{ profile.birthDate }}</p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700"
              >Địa chỉ thường trú</label
            >
            <p class="mt-1 text-lg text-gray-900">{{ profile.address }}</p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700"
              >Chuyên môn</label
            >
            <p class="mt-1 text-lg text-gray-900">
              {{ profile.specialization }}
            </p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700"
              >Kinh nghiệm</label
            >
            <p class="mt-1 text-lg text-gray-900">{{ profile.experience }}</p>
          </div>
          <div class="md:col-span-2">
            <label class="block text-sm font-medium text-gray-700"
              >Chứng chỉ</label
            >
            <ul class="mt-1 text-lg text-gray-900 list-disc list-inside">
              <li
                v-for="cert in profile.certifications"
                :key="cert"
                class="mb-1"
              >
                {{ cert }}
              </li>
            </ul>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>
