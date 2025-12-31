<template>
  <div class="p-3 sm:p-6">
    <div class="flex items-center gap-2 sm:gap-3 mb-4 sm:mb-6">
      <CalendarIcon class="w-6 h-6 sm:w-8 sm:h-8 text-green-600" />
      <h1 class="text-xl sm:text-2xl font-bold text-gray-800">Lịch dạy của tôi</h1>
    </div>
    <ScheduleCalendar role="teacher" :user-id="userId" />
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useAuthStore } from '@/stores/useAuthStore';
import ScheduleCalendar from '@/components/common/ScheduleCalendar.vue';
import { Calendar as CalendarIcon } from 'lucide-vue-next';

const authStore = useAuthStore();
const userId = computed(() => authStore.user?.id || 0);
</script>

