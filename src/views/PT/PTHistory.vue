<script setup>
import { ref, onMounted } from "vue";
import api from "@/services/api";
import { useAuthStore } from "@/stores/useAuthStore";
import { useToast } from "vue-toastification";

const authStore = useAuthStore();
const toast = useToast();

const checkinHistory = ref([]);
const teachingHistory = ref([]);

const loadingCheckin = ref(false);
const loadingTeaching = ref(false);

const loadCheckinHistory = async () => {
  loadingCheckin.value = true;
  try {
    const res = await api.get(`/attendance/staff/${authStore.user.id}`);
    checkinHistory.value = res.data;
  } catch (error) {
    console.error("Failed to load checkin history:", error);
    toast.error("Lỗi khi tải lịch sử checkin. Vui lòng thử lại sau!");
  } finally {
    loadingCheckin.value = false;
  }
};

const loadTeachingHistory = async () => {
  loadingTeaching.value = true;
  try {
    const res = await api.get("/appointment");
    const data = Array.isArray(res.data) ? res.data : [];
    // Filter appointments for the current PT and past dates
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const ptAppointments = data.filter(appt =>
      appt.staff.id === authStore.user.id &&
      new Date(appt.startTime) < today
    );
    teachingHistory.value = ptAppointments.map(appt => ({
      id: appt.id,
      className: appt.classTemplate?.name || "Appointment",
      date: appt.startTime.split('T')[0],
      startTime: new Date(appt.startTime).toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' }),
      endTime: new Date(appt.endTime).toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' }),
      status: appt.status
    }));
  } catch (error) {
    console.error("Failed to load teaching history:", error);
    toast.error("Lỗi khi tải lịch sử giảng dạy. Vui lòng thử lại sau!");
  } finally {
    loadingTeaching.value = false;
  }
};

onMounted(() => {
  loadCheckinHistory();
  loadTeachingHistory();
});
</script>

<template>
  <div class="p-6 space-y-8">
    <!-- Title -->
    <h1 class="text-3xl font-bold text-stone-800 mb-6">PT History</h1>

    <!-- Check-in History -->
    <section class="bg-white rounded-xl shadow-lg p-8">
      <h2 class="text-2xl font-semibold text-gray-800 mb-6">Check-in History</h2>
      <div v-if="loadingCheckin" class="text-center py-8 text-gray-500">
        Loading check-in history...
      </div>
      <div v-else-if="checkinHistory.length === 0" class="text-center py-8 text-gray-500">
        No check-in history found.
      </div>
      <div v-else class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Date</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Check-in Time</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Check-out Time</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="record in checkinHistory" :key="record.id">
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ new Date(record.date).toLocaleDateString() }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ new Date(record.checkInTime).toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' }) }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ record.checkOutTime ? new Date(record.checkOutTime).toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' }) : 'N/A' }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ record.checkOutTime ? 'Completed' : 'Active' }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <!-- Teaching History -->
    <section class="bg-white rounded-xl shadow-lg p-8">
      <h2 class="text-2xl font-semibold text-gray-800 mb-6">Teaching History</h2>
      <div v-if="loadingTeaching" class="text-center py-8 text-gray-500">
        Loading teaching history...
      </div>
      <div v-else-if="teachingHistory.length === 0" class="text-center py-8 text-gray-500">
        No teaching history found.
      </div>
      <div v-else class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Class Name</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Date</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Start Time</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">End Time</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="record in teachingHistory" :key="record.id">
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ record.className }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ new Date(record.date).toLocaleDateString() }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ record.startTime }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ record.endTime }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ record.status }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </div>
</template>