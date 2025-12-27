<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import api from "../../../services/api";

// Định nghĩa Staff
type Staff = {
  id: number;
  fullName: string;
  email: string;
  role: string;
};

// Định nghĩa Attendance
type AttendanceRecord = {
  id: number;
  staff: Staff;
  checkInTime: string;
  checkOutTime?: string;
  date: string;
};

const staffs = ref<Staff[]>([]);
const selectedStaff = ref<Staff | null>(null);
const searchStaff = ref("");
const selectedMonth = ref(new Date().getMonth() + 1);
const selectedYear = ref(new Date().getFullYear());
const attendanceHistory = ref<AttendanceRecord[]>([]);

onMounted(async () => {
  try {
    const res = await api.get("/users/staffs");
    staffs.value = res.data;
  } catch (err) {
    console.error("Error fetching staffs:", err);
    staffs.value = [];
  }
});

// Computed
const filteredStaffs = computed(() => {
  return staffs.value.filter(s =>
    s.fullName.toLowerCase().includes(searchStaff.value.toLowerCase()) ||
    s.email.toLowerCase().includes(searchStaff.value.toLowerCase())
  );
});

const groupedAttendance = computed(() => {
  const grouped: { [date: string]: AttendanceRecord[] } = {};
  attendanceHistory.value.forEach(record => {
    const date = new Date(record.date).toLocaleDateString();
    if (!grouped[date]) grouped[date] = [];
    grouped[date].push(record);
  });
  return grouped;
});

// Functions
const selectStaff = async (staff: Staff) => {
  selectedStaff.value = staff;
  await fetchAttendanceHistory();
};

const fetchAttendanceHistory = async () => {
  if (!selectedStaff.value) return;
  try {
    const res = await api.get(`/attendance/staff/${selectedStaff.value.id}`, {
      params: { month: selectedMonth.value, year: selectedYear.value }
    });
    attendanceHistory.value = res.data;
  } catch (err) {
    console.error("Error fetching attendance:", err);
    attendanceHistory.value = [];
  }
};

const formatTime = (timeString: string) => {
  return new Date(timeString).toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
};
</script>

<template>
  <div class="p-6 bg-gray-100 min-h-screen">
    <div class="max-w-7xl mx-auto flex flex-col lg:flex-row gap-6">
      <!-- Bên trái: Danh sách Staffs -->
      <div class="lg:w-1/3 bg-white rounded-xl shadow-lg overflow-hidden">
        <div class="p-6 border-b border-gray-200">
          <h2 class="text-2xl font-bold text-gray-800 mb-4">Select a Staff</h2>
          <div class="relative">
            <input
              type="text"
              v-model="searchStaff"
              placeholder="Search staff by name or email..."
              class="w-full pl-12 pr-4 py-3 border border-gray-300 rounded-lg bg-gray-50 text-gray-800
                     focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500
                     transition duration-200 ease-in-out"
            />
            <svg class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" width="20" height="20" fill="currentColor" viewBox="0 0 24 24">
              <path d="M15.5 14h-.79l-.28-.27A6.471 6.471 0 0016 9.5a6.5 6.5 0 10-6.5 6.5c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
            </svg>
          </div>
        </div>

        <div class="p-6 max-h-96 overflow-y-auto">
          <div v-if="filteredStaffs.length > 0" class="space-y-3">
            <div
              v-for="staff in filteredStaffs"
              :key="staff.id"
              @click="selectStaff(staff)"
              :class="[
                'p-4 rounded-lg cursor-pointer transition-colors border',
                selectedStaff?.id === staff.id
                  ? 'bg-blue-100 border-blue-300'
                  : 'bg-gray-50 hover:bg-gray-100',
              ]"
            >
              <div class="font-medium text-gray-900">{{ staff.fullName }}</div>
              <div class="text-sm text-gray-500">{{ staff.email }}</div>
              <div class="text-sm text-gray-500">{{ staff.role }}</div>
            </div>
          </div>
          <div v-else class="text-center text-gray-500 py-10">
            No staffs found.
          </div>
        </div>
      </div>

      <!-- Bên phải: Lịch sử Attendance -->
      <div class="lg:w-2/3 bg-white rounded-xl shadow-lg flex flex-col">
        <h2 class="text-2xl font-bold text-gray-800 p-6 border-b border-gray-200">Attendance History</h2>

        <div class="flex-1 p-6">
          <div v-if="selectedStaff" class="space-y-6">
            <div class="bg-blue-50 p-4 rounded-lg">
              <h3 class="text-lg font-semibold text-gray-900">Selected Staff</h3>
              <p class="text-gray-700">{{ selectedStaff.fullName }}</p>
              <p class="text-gray-600">{{ selectedStaff.email }}</p>
            </div>

            <!-- Chọn tháng và năm -->
            <div class="flex gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Month</label>
                <select
                  v-model="selectedMonth"
                  @change="fetchAttendanceHistory"
                  class="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option v-for="m in 12" :key="m" :value="m">{{ m }}</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Year</label>
                <select
                  v-model="selectedYear"
                  @change="fetchAttendanceHistory"
                  class="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option v-for="y in 10" :key="y" :value="new Date().getFullYear() - 10 + y">{{ new Date().getFullYear() - 10 + y }}</option>
                </select>
              </div>
            </div>

            <!-- Hiển thị lịch sử -->
            <div v-if="Object.keys(groupedAttendance).length > 0" class="space-y-4">
              <div v-for="(records, date) in groupedAttendance" :key="date" class="border rounded-lg p-4">
                <h4 class="font-semibold text-gray-900 mb-2">{{ date }}</h4>
                <div class="space-y-2">
                  <div v-for="record in records" :key="record.id" class="flex justify-between items-center bg-gray-50 p-2 rounded">
                    <div>
                      <span class="font-medium">Check-in: {{ formatTime(record.checkInTime) }}</span>
                      <span v-if="record.checkOutTime" class="ml-4 font-medium">Check-out: {{ formatTime(record.checkOutTime) }}</span>
                      <span v-else class="ml-4 text-red-500">Not checked out</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="text-center text-gray-500 py-10">
              No attendance records found for this month.
            </div>
          </div>
          <div v-else class="text-center text-gray-500 py-10">
            Select a staff to view attendance history.
          </div>
        </div>
      </div>
    </div>
  </div>
</template>