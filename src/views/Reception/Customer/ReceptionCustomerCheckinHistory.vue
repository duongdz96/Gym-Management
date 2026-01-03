<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import api from "../../../services/api";
import { useToast } from "vue-toastification";

// Định nghĩa Member
type Member = {
  id: number;
  fullName: string;
  membership: string;
  joinDate: string;
  status: string;
  faceId: string;
  cardId: string;
};

// Định nghĩa Checkin Record
type CheckinRecord = {
  id: number;
  member: Member;
  checkinTime: string;
  status: "success" | "late" | "absent";
};

// State
const checkinRecords = ref<CheckinRecord[]>([]);
const searchQuery = ref("");
const statusFilter = ref("");
const dateFilter = ref("");
const isLoading = ref(true);

// Lifecycle
onMounted(async () => {
  try {
    isLoading.value = true;
    // Mock data - thay bằng API call thực tế
    checkinRecords.value = [
      {
        id: 1,
        member: {
          id: 1,
          fullName: "Nguyễn Văn A",
          membership: "Premium",
          joinDate: "2024-01-15",
          status: "Active",
          faceId: null,
          cardId: "CARD001"
        },
        checkinTime: "2024-12-14T08:30:00",
        status: "success"
      },
      {
        id: 2,
        member: {
          id: 2,
          fullName: "Trần Thị B",
          membership: "Basic",
          joinDate: "2024-02-20",
          status: "Active",
          faceId: "FACE002",
          cardId: null
        },
        checkinTime: "2024-12-14T09:15:00",
        status: "late"
      },
      {
        id: 3,
        member: {
          id: 3,
          fullName: "Lê Văn C",
          membership: "VIP",
          joinDate: "2024-03-10",
          status: "Active",
          faceId: null,
          cardId: "CARD003"
        },
        checkinTime: "2024-12-14T07:45:00",
        status: "success"
      }
    ];
  } catch (error) {
    useToast().error("Không thể tải dữ liệu check-in");
  } finally {
    isLoading.value = false;
  }
});

// Computed
const filteredRecords = computed(() => {
  return checkinRecords.value.filter(record => {
    const matchesSearch = !searchQuery.value ||
      record.member.fullName.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      record.member.membership.toLowerCase().includes(searchQuery.value.toLowerCase());

    const matchesStatus = !statusFilter.value || record.status === statusFilter.value;

    const matchesDate = !dateFilter.value ||
      record.checkinTime.startsWith(dateFilter.value);

    return matchesSearch && matchesStatus && matchesDate;
  });
});

// Helpers
const formatDate = (dateString: string) => {
  if (!dateString) return 'N/A';
  try {
    return new Date(dateString).toLocaleDateString('vi-VN');
  } catch {
    return dateString;
  }
};

const formatTime = (dateString: string) => {
  if (!dateString) return 'N/A';
  try {
    return new Date(dateString).toLocaleTimeString('vi-VN', {
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch {
    return dateString;
  }
};

const getStatusText = (status: string) => {
  switch (status) {
    case 'success': return 'Thành công';
    case 'late': return 'Muộn';
    case 'absent': return 'Vắng';
    default: return status;
  }
};

const getStatusColor = (status: string) => {
  switch (status) {
    case 'success': return 'bg-green-100 text-green-800';
    case 'late': return 'bg-yellow-100 text-yellow-800';
    case 'absent': return 'bg-red-100 text-red-800';
    default: return 'bg-gray-100 text-gray-800';
  }
};

const getCheckinMethod = (record: CheckinRecord) => {
  if (record.member.faceId) return 'Face ID';
  if (record.member.cardId) return 'Thẻ';
  return 'Manual';
};
</script>

<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <!-- Header -->
    <div class="flex justify-between items-center mb-6">
      <div class="flex items-center gap-3">
        <button
          @click="$router.back()"
          class="p-2 bg-white border border-gray-200 rounded-lg hover:bg-gray-100 text-gray-600 transition shadow-sm"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m15 18-6-6 6-6"/></svg>
        </button>
        <div>
          <h1 class="text-xl font-bold text-gray-800">Lịch Sử Check-in</h1>
          <p class="text-xs text-gray-500">Theo dõi hoạt động check-in của học viên</p>
        </div>
      </div>

      <div class="flex gap-2">
        <span class="px-3 py-1 bg-emerald-50 text-emerald-700 rounded-full text-sm font-semibold">
          {{ filteredRecords.length }} bản ghi
        </span>
      </div>
    </div>

    <!-- Filters -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6 mb-6">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div class="relative">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/></svg>
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Tìm kiếm học viên..."
            class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500"
          />
        </div>

        <div class="relative">
          <select
            v-model="statusFilter"
            class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500 appearance-none bg-white pr-10"
          >
            <option value="">Tất cả trạng thái</option>
            <option value="success">Thành công</option>
            <option value="late">Muộn</option>
            <option value="absent">Vắng</option>
          </select>
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 pointer-events-none"><path d="m6 9 6 6 6-6"/></svg>
        </div>

        <div>
          <input
            v-model="dateFilter"
            type="date"
            class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500"
          />
        </div>

        <div class="flex items-end">
          <button
            @click="() => { searchQuery = ''; statusFilter = ''; dateFilter = '' }"
            class="w-full px-4 py-3 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition font-medium"
          >
            Xóa bộ lọc
          </button>
        </div>
      </div>
    </div>

    <!-- Table -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
      <div v-if="isLoading" class="animate-pulse">
        <div class="h-16 bg-gray-200 mb-4"></div>
        <div class="space-y-3 px-6 pb-6">
          <div v-for="i in 5" :key="i" class="h-12 bg-gray-200 rounded"></div>
        </div>
      </div>

      <div v-else class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Học Viên</th>
              <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Gói Tập</th>
              <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Thời Gian</th>
              <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Phương Thức</th>
              <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Trạng Thái</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
            <tr v-for="record in filteredRecords" :key="record.id" class="hover:bg-gray-50 transition">
              <td class="px-6 py-4">
                <div class="flex items-center">
                  <div class="w-10 h-10 rounded-full bg-emerald-100 text-emerald-600 flex items-center justify-center font-bold text-sm mr-3">
                    {{ record.member.fullName.charAt(0).toUpperCase() }}
                  </div>
                  <div>
                    <div class="text-sm font-semibold text-gray-900">{{ record.member.fullName }}</div>
                    <div class="text-xs text-gray-500">ID: {{ record.member.id }}</div>
                  </div>
                </div>
              </td>

              <td class="px-6 py-4">
                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-emerald-100 text-emerald-800">
                  {{ record.member.membership }}
                </span>
              </td>

              <td class="px-6 py-4 text-sm text-gray-600">
                <div class="font-medium">{{ formatDate(record.checkinTime) }}</div>
                <div class="text-xs text-gray-500">{{ formatTime(record.checkinTime) }}</div>
              </td>

              <td class="px-6 py-4">
                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800">
                  {{ getCheckinMethod(record) }}
                </span>
              </td>

              <td class="px-6 py-4">
                <span :class="['inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium', getStatusColor(record.status)]">
                  {{ getStatusText(record.status) }}
                </span>
              </td>
            </tr>

            <tr v-if="filteredRecords.length === 0">
              <td colspan="5" class="px-6 py-12 text-center text-gray-500">
                <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round" class="mx-auto mb-4 text-gray-300"><path d="M12 2v20M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>
                <p>Không tìm thấy bản ghi check-in nào</p>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>