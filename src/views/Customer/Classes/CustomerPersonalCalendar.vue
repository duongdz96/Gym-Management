<template>
  <div class="p-6">
    <!-- Header -->
    <div class="flex items-center gap-3 mb-8">
      <Calendar class="w-10 h-10 text-red-600" />
      <h1 class="text-3xl font-bold bg-gradient-to-r from-red-600 to-red-700 bg-clip-text text-transparent">
        Lịch Học Của Tôi
      </h1>
    </div>

    <!-- Calendar Component -->
    <schedule-calendar 
      v-if="currentStudentId" 
      role="student" 
      :user-id="currentStudentId" 
    />
    
    <!-- No User State -->
    <div v-else class="text-center py-16">
      <Calendar class="w-16 h-16 text-gray-400 mx-auto mb-4" />
      <p class="text-gray-500 text-lg">Vui lòng đăng nhập để xem lịch học</p>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useAuthStore } from '@/stores/useAuthStore';
import ScheduleCalendar from '@/components/common/ScheduleCalendar.vue';
import { Calendar } from 'lucide-vue-next';

const authStore = useAuthStore();

const currentStudentId = computed(() => {
  const user = authStore.user;
  return user?.id || null;
});
</script>
