<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'

// Định nghĩa Type cho dữ liệu
type ClassTemplateInfo = {
  id: number;
  name: string;
}

type ClassSchedule = {
  id: number;
  startTime: string;
  endTime: string;
  location: string;
  status: 'OPEN' | 'CLOSED' | 'CANCELLED';
  classTemplate: ClassTemplateInfo;
}

// State
const schedules = ref<ClassSchedule[]>([]);
const selectedTemplate = ref<ClassTemplateInfo | null>(null);
const isLoading = ref(true);
const error = ref<string | null>(null);

const router = useRouter();

onMounted(async () => {
  isLoading.value = true; // Bắt đầu loading

  // Lấy templateId từ sessionStorage (giữ nguyên)
  const storedTemplateJSON = sessionStorage.getItem('selectedTemplate');
  if (!storedTemplateJSON) {
    error.value = "Please select a Class Template first.";
    isLoading.value = false;
    return;
  }
  
  selectedTemplate.value = JSON.parse(storedTemplateJSON);
  const templateId = selectedTemplate.value?.id;

  if (!templateId) {
    error.value = "Invalid Class Template data.";
    isLoading.value = false;
    return;
  }

  try {
    const response = await api.get(`/classschedule/by-template/${templateId}`);
    
    schedules.value = response.data;

  } catch (err) {
    console.error("Failed to fetch schedules:", err);
    error.value = "Failed to load schedule data.";
  } finally {
    isLoading.value = false; // Kết thúc loading
  }
});

const formatDateTime = (isoString: string) => {
  if (!isoString) return 'N/A';
  return new Date(isoString).toLocaleString('vi-VN');
}

function AddNewSchedule() {
  router.push({name: "classschedule.add"});
}
</script>

<template>
  <div class="p-4 md:p-6">
    <div class="bg-white p-6 rounded-lg shadow-md">
      <div class="flex flex-col md:flex-row justify-between items-center mb-4 border-b pb-4">
        <div>
          <button @click="router.back()" class="text-sm text-blue-600 hover:underline mb-2">&larr; Back to Templates</button>
          <h1 v-if="selectedTemplate" class="text-2xl font-bold text-gray-800">
            Schedules for: <span class="text-blue-700">{{ selectedTemplate.name }}</span>
          </h1>
        </div>
        <button
          @click="AddNewSchedule()"
          class="mt-2 md:mt-0 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">
          Add New Schedule
        </button>
      </div>

      <div v-if="isLoading" class="text-center py-8 text-gray-500">Loading...</div>
      <div v-if="error" class="text-center py-8 text-red-600 bg-red-50 p-4 rounded-lg">{{ error }}</div>

      <div v-else class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Location</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Start Time</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">End Time</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
              <th scope="col" class="relative px-6 py-3"><span class="sr-only">Actions</span></th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-if="schedules.length === 0">
              <td colspan="6" class="px-6 py-8 text-center text-gray-500">No schedules found for this template.</td>
            </tr>
            <tr v-for="schedule in schedules" :key="schedule.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ schedule.id }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-700">{{ schedule.location }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-700">{{ formatDateTime(schedule.startTime) }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-700">{{ formatDateTime(schedule.endTime) }}</td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span
                  class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full"
                  :class="{
                    'bg-green-100 text-green-800': schedule.status === 'OPEN',
                    'bg-gray-100 text-gray-800': schedule.status === 'CLOSED',
                    'bg-red-100 text-red-800': schedule.status === 'CANCELLED',
                  }"
                >
                  {{ schedule.status }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <a href="#" class="text-indigo-600 hover:text-indigo-900 mr-3">Edit</a>
                <a href="#" class="text-red-600 hover:text-red-900">Delete</a>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>