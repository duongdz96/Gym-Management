<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import api from "@/services/api";
import { useToast } from "vue-toastification";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';
import { Bar } from 'vue-chartjs';

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const toast = useToast();

type Period = 'week' | 'month' | 'year';

type User = {
  id: number;
  fullName: string;
  email: string;
};

type StatCategory = 'users' | 'products' | 'revenue' | 'classes';

const selectedPeriod = ref<Period>('month');
const selectedCategory = ref<StatCategory>('users');
const selectedUser = ref<User | null>(null);
const searchQuery = ref('');
const users = ref<User[]>([]);
const showUserSearch = ref(false);

// Data for charts
const membershipData = ref({
  labels: [] as string[],
  datasets: [{
    label: 'New Membership Registrations',
    data: [] as number[],
    backgroundColor: 'rgba(54, 162, 235, 0.6)',
    borderColor: 'rgba(54, 162, 235, 1)',
    borderWidth: 1
  }]
});

const attendanceData = ref({
  labels: [] as string[],
  datasets: [{
    label: 'Total Check-ins',
    data: [] as number[],
    backgroundColor: 'rgba(255, 99, 132, 0.6)',
    borderColor: 'rgba(255, 99, 132, 1)',
    borderWidth: 1
  }]
});

const productSalesData = ref({
  labels: [] as string[],
  datasets: [{
    label: 'Product Sales',
    data: [] as number[],
    backgroundColor: 'rgba(245, 158, 11, 0.6)',
    borderColor: 'rgba(245, 158, 11, 1)',
    borderWidth: 1
  }]
});

const inventoryData = ref({
  labels: [] as string[],
  datasets: [{
    label: 'Inventory Levels',
    data: [] as number[],
    backgroundColor: 'rgba(139, 69, 19, 0.6)',
    borderColor: 'rgba(139, 69, 19, 1)',
    borderWidth: 1
  }]
});

const revenueData = ref({
  labels: [] as string[],
  datasets: [{
    label: 'Revenue',
    data: [] as number[],
    backgroundColor: 'rgba(34, 197, 94, 0.6)',
    borderColor: 'rgba(34, 197, 94, 1)',
    borderWidth: 1
  }]
});

const expenseData = ref({
  labels: [] as string[],
  datasets: [{
    label: 'Expenses',
    data: [] as number[],
    backgroundColor: 'rgba(239, 68, 68, 0.6)',
    borderColor: 'rgba(239, 68, 68, 1)',
    borderWidth: 1
  }]
});

const classAttendanceData = ref({
  labels: [] as string[],
  datasets: [{
    label: 'Class Attendance',
    data: [] as number[],
    backgroundColor: 'rgba(168, 85, 247, 0.6)',
    borderColor: 'rgba(168, 85, 247, 1)',
    borderWidth: 1
  }]
});

const classPopularityData = ref({
  labels: [] as string[],
  datasets: [{
    label: 'Class Popularity',
    data: [] as number[],
    backgroundColor: 'rgba(236, 72, 153, 0.6)',
    borderColor: 'rgba(236, 72, 153, 1)',
    borderWidth: 1
  }]
});

const loading = ref(false);

// Computed for chart data based on category
const chartData1 = computed(() => {
  if (selectedCategory.value === 'users') return membershipData.value;
  if (selectedCategory.value === 'products') return productSalesData.value;
  if (selectedCategory.value === 'revenue') return revenueData.value;
  if (selectedCategory.value === 'classes') return classAttendanceData.value;
  return membershipData.value;
});

const chartData2 = computed(() => {
  if (selectedCategory.value === 'users') return attendanceData.value;
  if (selectedCategory.value === 'products') return inventoryData.value;
  if (selectedCategory.value === 'revenue') return expenseData.value;
  if (selectedCategory.value === 'classes') return classPopularityData.value;
  return attendanceData.value;
});

// Computed for summary cards
const summaryCards = computed(() => {
  let card1Title = '';
  let card1Value = 0;
  let card1Color = '';
  let card2Title = '';
  let card2Value = 0;
  let card2Color = '';

  if (selectedCategory.value === 'users') {
    card1Title = selectedUser.value ? `${selectedUser.value.fullName}'s Memberships` : 'Total New Memberships';
    card1Value = membershipData.value.datasets[0].data.reduce((a, b) => a + b, 0);
    card1Color = 'text-blue-600';
    card2Title = selectedUser.value ? `${selectedUser.value.fullName}'s Check-ins` : 'Total Check-ins';
    card2Value = attendanceData.value.datasets[0].data.reduce((a, b) => a + b, 0);
    card2Color = 'text-red-600';
  } else if (selectedCategory.value === 'products') {
    card1Title = 'Total Product Sales';
    card1Value = productSalesData.value.datasets[0].data.reduce((a, b) => a + b, 0);
    card1Color = 'text-orange-600';
    card2Title = 'Current Inventory';
    card2Value = inventoryData.value.datasets[0].data.reduce((a, b) => a + b, 0);
    card2Color = 'text-amber-600';
  } else if (selectedCategory.value === 'revenue') {
    card1Title = 'Total Revenue';
    card1Value = revenueData.value.datasets[0].data.reduce((a, b) => a + b, 0);
    card1Color = 'text-green-600';
    card2Title = 'Total Expenses';
    card2Value = expenseData.value.datasets[0].data.reduce((a, b) => a + b, 0);
    card2Color = 'text-red-600';
  } else if (selectedCategory.value === 'classes') {
    card1Title = 'Total Class Attendance';
    card1Value = classAttendanceData.value.datasets[0].data.reduce((a, b) => a + b, 0);
    card1Color = 'text-purple-600';
    card2Title = 'Class Popularity Score';
    card2Value = classPopularityData.value.datasets[0].data.reduce((a, b) => a + b, 0);
    card2Color = 'text-pink-600';
  }

  return {
    card1: { title: card1Title, value: card1Value, color: card1Color },
    card2: { title: card2Title, value: card2Value, color: card2Color },
  };
});

// Computed for chart options
const chartOptions = computed(() => {
  let title1 = '';
  let title2 = '';

  if (selectedCategory.value === 'users') {
    title1 = selectedUser.value
      ? `${selectedUser.value.fullName}'s Membership Registrations (${selectedPeriod.value})`
      : `New Membership Registrations (${selectedPeriod.value})`;
    title2 = selectedUser.value
      ? `${selectedUser.value.fullName}'s Check-ins (${selectedPeriod.value})`
      : `Total User Check-ins (${selectedPeriod.value})`;
  } else if (selectedCategory.value === 'products') {
    title1 = `Product Sales (${selectedPeriod.value})`;
    title2 = `Inventory Levels (${selectedPeriod.value})`;
  } else if (selectedCategory.value === 'revenue') {
    title1 = `Revenue (${selectedPeriod.value})`;
    title2 = `Expenses (${selectedPeriod.value})`;
  } else if (selectedCategory.value === 'classes') {
    title1 = `Class Attendance (${selectedPeriod.value})`;
    title2 = `Class Popularity (${selectedPeriod.value})`;
  }

  return {
    chart1: {
      responsive: true,
      plugins: {
        legend: { position: 'top' as const },
        title: { display: true, text: title1 },
      },
    },
    chart2: {
      responsive: true,
      plugins: {
        legend: { position: 'top' as const },
        title: { display: true, text: title2 },
      },
    },
  };
});

// Functions
const searchUsers = async () => {
  if (!searchQuery.value.trim()) {
    users.value = [];
    return;
  }
  try {
    const res = await api.get('/users/search', {
      params: { q: searchQuery.value, role: 'MEMBER' }
    });
    users.value = res.data;
  } catch (error) {
    console.error("Failed to search users:", error);
    toast.error("Failed to search users.");
  }
};

const selectUser = (user: User) => {
  selectedUser.value = user;
  showUserSearch.value = false;
  searchQuery.value = '';
  users.value = [];
  loadStats();
};

const clearUser = () => {
  selectedUser.value = null;
  loadStats();
};

const hideUserSearch = () => {
  setTimeout(() => {
    showUserSearch.value = false;
  }, 200);
};

const fetchMembershipStats = async () => {
  try {
    const params: any = { period: selectedPeriod.value };
    if (selectedUser.value) {
      params.userId = selectedUser.value.id;
    }
    const res = await api.get(`/memberships/stats`, { params });
    const stats = res.data;

    membershipData.value.labels = stats.labels || [];
    membershipData.value.datasets[0].data = stats.data || [];
  } catch (error) {
    console.error("Failed to fetch membership stats:", error);
    toast.error("Failed to load membership statistics.");
  }
};

const fetchAttendanceStats = async () => {
  try {
    const params: any = { period: selectedPeriod.value };
    if (selectedUser.value) {
      params.userId = selectedUser.value.id;
    }
    const res = await api.get(`/attendance/stats`, { params });
    const stats = res.data;

    attendanceData.value.labels = stats.labels || [];
    attendanceData.value.datasets[0].data = stats.data || [];
  } catch (error) {
    console.error("Failed to fetch attendance stats:", error);
    toast.error("Failed to load attendance statistics.");
  }
};

// Placeholder functions for future categories
const fetchProductStats = async () => {
  // TODO: Implement product sales stats
  productSalesData.value.labels = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
  productSalesData.value.datasets[0].data = [12, 19, 3, 5, 2, 3, 9];
};

const fetchInventoryStats = async () => {
  // TODO: Implement inventory stats
  inventoryData.value.labels = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
  inventoryData.value.datasets[0].data = [65, 59, 80, 81, 56, 55, 40];
};

const fetchRevenueStats = async () => {
  // TODO: Implement revenue stats
  revenueData.value.labels = ['Week 1', 'Week 2', 'Week 3', 'Week 4'];
  revenueData.value.datasets[0].data = [12000, 19000, 3000, 5000];
};

const fetchExpenseStats = async () => {
  // TODO: Implement expense stats
  expenseData.value.labels = ['Week 1', 'Week 2', 'Week 3', 'Week 4'];
  expenseData.value.datasets[0].data = [6500, 5900, 8000, 8100];
};

const fetchClassStats = async () => {
  // TODO: Implement class attendance stats
  classAttendanceData.value.labels = ['Yoga', 'Pilates', 'HIIT', 'Boxing'];
  classAttendanceData.value.datasets[0].data = [25, 15, 30, 20];
};

const fetchClassPopularityStats = async () => {
  // TODO: Implement class popularity stats
  classPopularityData.value.labels = ['Yoga', 'Pilates', 'HIIT', 'Boxing'];
  classPopularityData.value.datasets[0].data = [85, 75, 95, 80];
};

const loadStats = async () => {
  loading.value = true;
  try {
    if (selectedCategory.value === 'users') {
      await Promise.all([fetchMembershipStats(), fetchAttendanceStats()]);
    } else if (selectedCategory.value === 'products') {
      await Promise.all([fetchProductStats(), fetchInventoryStats()]);
    } else if (selectedCategory.value === 'revenue') {
      await Promise.all([fetchRevenueStats(), fetchExpenseStats()]);
    } else if (selectedCategory.value === 'classes') {
      await Promise.all([fetchClassStats(), fetchClassPopularityStats()]);
    }
  } catch (error) {
    console.error("Failed to load statistics:", error);
    toast.error("Failed to load statistics.");
  } finally {
    loading.value = false;
  }
};

const changePeriod = async (period: Period) => {
  selectedPeriod.value = period;
  await loadStats();
};

const changeCategory = async (category: StatCategory) => {
  selectedCategory.value = category;
  selectedUser.value = null; // Reset user selection when changing category
  await loadStats();
};

onMounted(async () => {
  await loadStats();
});
</script>

<template>
  <div class="p-6 bg-gray-100 min-h-screen">
    <div class="max-w-7xl mx-auto">
      <!-- Header -->
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-900 mb-4">Manager Statistics</h1>

        <!-- Category Selector -->
        <div class="mb-6">
          <div class="flex gap-4 mb-4">
            <button
              @click="changeCategory('users')"
              :class="[
                'px-4 py-2 rounded-lg font-medium transition-colors',
                selectedCategory === 'users'
                  ? 'bg-blue-600 text-white'
                  : 'bg-white text-gray-700 hover:bg-gray-50 border'
              ]"
            >
              Users
            </button>
            <button
              @click="changeCategory('products')"
              :class="[
                'px-4 py-2 rounded-lg font-medium transition-colors',
                selectedCategory === 'products'
                  ? 'bg-blue-600 text-white'
                  : 'bg-white text-gray-700 hover:bg-gray-50 border'
              ]"
            >
              Products
            </button>
            <button
              @click="changeCategory('revenue')"
              :class="[
                'px-4 py-2 rounded-lg font-medium transition-colors',
                selectedCategory === 'revenue'
                  ? 'bg-blue-600 text-white'
                  : 'bg-white text-gray-700 hover:bg-gray-50 border'
              ]"
            >
              Revenue
            </button>
            <button
              @click="changeCategory('classes')"
              :class="[
                'px-4 py-2 rounded-lg font-medium transition-colors',
                selectedCategory === 'classes'
                  ? 'bg-blue-600 text-white'
                  : 'bg-white text-gray-700 hover:bg-gray-50 border'
              ]"
            >
              Classes
            </button>
          </div>
        </div>

        <!-- User Selector (only for users category) -->
        <div v-if="selectedCategory === 'users'" class="mb-6">
          <div class="flex items-center gap-4">
            <div class="relative">
              <input
                v-model="searchQuery"
                @input="searchUsers"
                @focus="showUserSearch = true"
                @blur="hideUserSearch"
                type="text"
                placeholder="Search for a specific user..."
                class="w-80 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
              <div v-if="showUserSearch && users.length > 0" class="absolute z-10 w-full mt-1 bg-white border border-gray-300 rounded-lg shadow-lg max-h-60 overflow-y-auto">
                <div
                  v-for="user in users"
                  :key="user.id"
                  @click="selectUser(user)"
                  class="px-4 py-2 hover:bg-gray-100 cursor-pointer"
                >
                  <div class="font-medium">{{ user.fullName }}</div>
                  <div class="text-sm text-gray-500">{{ user.email }}</div>
                </div>
              </div>
            </div>
            <button
              v-if="selectedUser"
              @click="clearUser"
              class="px-4 py-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition-colors"
            >
              Clear Selection
            </button>
          </div>
          <div v-if="selectedUser" class="mt-2 text-sm text-gray-600">
            Viewing statistics for: <span class="font-medium">{{ selectedUser.fullName }}</span>
          </div>
        </div>

        <!-- Period Selector -->
        <div class="flex gap-4 mb-6">
          <button
            @click="changePeriod('week')"
            :class="[
              'px-4 py-2 rounded-lg font-medium transition-colors',
              selectedPeriod === 'week'
                ? 'bg-blue-600 text-white'
                : 'bg-white text-gray-700 hover:bg-gray-50'
            ]"
          >
            This Week
          </button>
          <button
            @click="changePeriod('month')"
            :class="[
              'px-4 py-2 rounded-lg font-medium transition-colors',
              selectedPeriod === 'month'
                ? 'bg-blue-600 text-white'
                : 'bg-white text-gray-700 hover:bg-gray-50'
            ]"
          >
            This Month
          </button>
          <button
            @click="changePeriod('year')"
            :class="[
              'px-4 py-2 rounded-lg font-medium transition-colors',
              selectedPeriod === 'year'
                ? 'bg-blue-600 text-white'
                : 'bg-white text-gray-700 hover:bg-gray-50'
            ]"
          >
            This Year
          </button>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="text-center py-12">
        <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
        <p class="mt-2 text-gray-600">Loading statistics...</p>
      </div>

      <!-- Charts -->
      <div v-else class="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <!-- Chart 1 -->
        <div class="bg-white rounded-xl shadow-lg p-6">
          <Bar :data="chartData1" :options="chartOptions.chart1" />
        </div>

        <!-- Chart 2 -->
        <div class="bg-white rounded-xl shadow-lg p-6">
          <Bar :data="chartData2" :options="chartOptions.chart2" />
        </div>
      </div>

      <!-- Summary Cards -->
      <div class="mt-8 grid grid-cols-1 md:grid-cols-2 gap-6">
        <div class="bg-white rounded-xl shadow-lg p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-2">
            {{ summaryCards.card1.title }}
          </h3>
          <p :class="`text-3xl font-bold ${summaryCards.card1.color}`">
            {{ summaryCards.card1.value }}
          </p>
          <p class="text-sm text-gray-600 mt-1">in selected period</p>
        </div>

        <div class="bg-white rounded-xl shadow-lg p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-2">
            {{ summaryCards.card2.title }}
          </h3>
          <p :class="`text-3xl font-bold ${summaryCards.card2.color}`">
            {{ summaryCards.card2.value }}
          </p>
          <p class="text-sm text-gray-600 mt-1">in selected period</p>
        </div>
      </div>
    </div>
  </div>
</template>