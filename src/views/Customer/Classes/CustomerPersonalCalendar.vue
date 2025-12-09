<template>
  <div class="p-6">
    <div class="flex items-center gap-3 mb-6">
      <CalendarIcon class="w-8 h-8 text-red-600" />
      <h1 class="text-2xl font-bold text-gray-800">Lịch học của tôi</h1>
    </div>
    <ScheduleCalendar role="student" :user-id="userId" />
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

