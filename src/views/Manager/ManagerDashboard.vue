<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";

const router = useRouter();
const API_BASE_URL = "http://localhost:8080/api/v1/stats";

// ===================== STATE =====================
const stats = ref({
  totalRevenue: 0,
  totalMembers: 0,
  activeMembers: 0,
  newMembersThisMonth: 0,
  totalClasses: 0,
  totalProducts: 0,
  pendingBills: 0,
  completedBills: 0,
  revenueGrowthRate: 0,
});

const revenueByMonth = ref([]);
const membershipDistribution = ref([]);
const topProducts = ref([]);
const recentActivities = ref([]);
const isLoading = ref(true);

// ===================== FETCH DATA =====================
const fetchDashboardData = async () => {
  isLoading.value = true;

  // Hàm helper để fetch an toàn, nếu lỗi thì trả về giá trị mặc định
  const safeFetch = async (url: string, defaultValue: any) => {
    try {
      const res = await axios.get(url);
      return res.data;
    } catch (error) {
      console.warn(`Failed to fetch ${url}:`, error);
      return defaultValue;
    }
  };

  // Gọi song song nhưng độc lập
  const [statsData, revenueData, distData, productsData, activitiesData] = await Promise.all([
    safeFetch(`${API_BASE_URL}/dashboard`, stats.value),
    safeFetch(`${API_BASE_URL}/revenue-chart`, []),
    safeFetch(`${API_BASE_URL}/membership-distribution`, []),
    safeFetch(`${API_BASE_URL}/products/ranking`, []),
    safeFetch(`${API_BASE_URL}/recent-activities`, [])
  ]);

  // Gán dữ liệu
  stats.value = statsData;
  revenueByMonth.value = revenueData;
  membershipDistribution.value = distData;
  
  // Map và xử lý null cho products
  topProducts.value = productsData.map((p: any) => ({
    name: p.productName,
    sold: p.totalQuantitySold,
    revenue: p.totalRevenue || 0 // Xử lý null thành 0
  }));
  
  recentActivities.value = activitiesData;
  
  isLoading.value = false;
};

// ===================== COMPUTED =====================
const maxRevenue = computed(() => {
  if (revenueByMonth.value.length === 0) return 1000000;
  return Math.max(...revenueByMonth.value.map((m: any) => m.revenue));
});

const chartHeight = 200;

const getBarHeight = (revenue: number) => {
  if (maxRevenue.value === 0) return 0;
  return (revenue / maxRevenue.value) * chartHeight;
};

const formatCurrency = (value: number) => {
  // Xử lý an toàn cho value
  const safeValue = value || 0;
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(safeValue);
};

onMounted(() => {
  fetchDashboardData();
});
</script>

<template>
  <div class="p-6 space-y-6 bg-gray-50 min-h-screen">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-3xl font-bold text-gray-900">Dashboard Quản lý</h1>
        <p class="text-gray-600 mt-1">Tổng quan hoạt động phòng gym</p>
      </div>
      <div class="text-right">
        <p class="text-sm text-gray-500">Cập nhật lần cuối</p>
        <p class="text-lg font-semibold text-gray-900">{{ new Date().toLocaleDateString('vi-VN') }}</p>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="isLoading" class="flex justify-center items-center h-64">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
    </div>

    <div v-else class="space-y-6">
      <!-- Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <!-- Revenue Card -->
        <div class="bg-gradient-to-br from-blue-500 to-blue-600 rounded-2xl shadow-lg p-6 text-white transform hover:scale-105 transition">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-blue-100 text-sm font-medium">Doanh thu tháng này</p>
              <p class="text-3xl font-bold mt-2">{{ formatCurrency(stats.totalRevenue) }}</p>
              <p class="text-blue-100 text-xs mt-2">
                <span v-if="stats.revenueGrowthRate > 0">↗ +{{ stats.revenueGrowthRate }}%</span>
                <span v-else-if="stats.revenueGrowthRate < 0">↘ {{ stats.revenueGrowthRate }}%</span>
                <span v-else>- 0%</span>
                so với tháng trước
              </p>
            </div>
            <div class="bg-white bg-opacity-20 rounded-full p-4">
              <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
          </div>
        </div>

        <!-- Members Card -->
        <div class="bg-gradient-to-br from-green-500 to-green-600 rounded-2xl shadow-lg p-6 text-white transform hover:scale-105 transition">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-green-100 text-sm font-medium">Tổng hội viên</p>
              <p class="text-3xl font-bold mt-2">{{ stats.totalMembers }}</p>
              <p class="text-green-100 text-xs mt-2">{{ stats.activeMembers }} đang hoạt động</p>
            </div>
            <div class="bg-white bg-opacity-20 rounded-full p-4">
              <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
              </svg>
            </div>
          </div>
        </div>

        <!-- New Members Card -->
        <div class="bg-gradient-to-br from-purple-500 to-purple-600 rounded-2xl shadow-lg p-6 text-white transform hover:scale-105 transition">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-purple-100 text-sm font-medium">Hội viên mới tháng này</p>
              <p class="text-3xl font-bold mt-2">{{ stats.newMembersThisMonth }}</p>
              <p class="text-purple-100 text-xs mt-2">Tổng số lớp: {{ stats.totalClasses }}</p>
            </div>
            <div class="bg-white bg-opacity-20 rounded-full p-4">
              <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z" />
              </svg>
            </div>
          </div>
        </div>

        <!-- Bills Card -->
        <div class="bg-gradient-to-br from-orange-500 to-orange-600 rounded-2xl shadow-lg p-6 text-white transform hover:scale-105 transition">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-orange-100 text-sm font-medium">Hóa đơn</p>
              <p class="text-3xl font-bold mt-2">{{ stats.completedBills }}</p>
              <p class="text-orange-100 text-xs mt-2">{{ stats.pendingBills }} đang chờ xử lý</p>
            </div>
            <div class="bg-white bg-opacity-20 rounded-full p-4">
              <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
            </div>
          </div>
        </div>
      </div>

      <!-- Charts Row -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- Revenue Chart -->
        <div class="bg-white rounded-2xl shadow-lg p-6">
          <h2 class="text-xl font-bold text-gray-900 mb-6">Doanh thu theo tháng</h2>
          <div class="flex items-end justify-between h-64 gap-2">
            <div v-if="revenueByMonth.length === 0" class="w-full h-full flex items-center justify-center text-gray-400">
              Chưa có dữ liệu doanh thu
            </div>
            <div
              v-else
              v-for="item in revenueByMonth"
              :key="item.month"
              class="flex-1 flex flex-col items-center group"
            >
              <div class="relative w-full h-full flex items-end justify-center">
                <div
                  class="w-full bg-gradient-to-t from-blue-500 to-blue-400 rounded-t-lg transition-all duration-300 hover:from-blue-600 hover:to-blue-500 cursor-pointer"
                  :style="{ height: getBarHeight(item.revenue) + 'px' }"
                >
                  <div class="absolute -top-8 left-1/2 transform -translate-x-1/2 opacity-0 group-hover:opacity-100 transition-opacity bg-gray-900 text-white text-xs px-2 py-1 rounded whitespace-nowrap z-10">
                    {{ formatCurrency(item.revenue) }}
                  </div>
                </div>
              </div>
              <p class="text-xs text-gray-600 mt-2 font-medium">{{ item.month }}</p>
            </div>
          </div>
        </div>

        <!-- Membership Distribution -->
        <div class="bg-white rounded-2xl shadow-lg p-6">
          <h2 class="text-xl font-bold text-gray-900 mb-6">Phân bổ gói thành viên</h2>
          <div v-if="membershipDistribution.length === 0" class="text-center text-gray-400 py-10">
            Chưa có dữ liệu thành viên
          </div>
          <div v-else class="space-y-4">
            <div v-for="tier in membershipDistribution" :key="tier.tier">
              <div class="flex items-center justify-between mb-2">
                <span class="text-sm font-medium text-gray-700">{{ tier.tier }}</span>
                <span class="text-sm font-semibold text-gray-900">{{ tier.count }} ({{ tier.percentage }}%)</span>
              </div>
              <div class="w-full bg-gray-200 rounded-full h-3 overflow-hidden">
                <div
                  class="h-full rounded-full transition-all duration-500"
                  :class="{
                    'bg-gradient-to-r from-blue-400 to-blue-500': tier.tier === 'Platinum',
                    'bg-gradient-to-r from-purple-400 to-purple-500': tier.tier === 'Gold',
                    'bg-gradient-to-r from-yellow-400 to-yellow-500': tier.tier === 'Basic',
                    'bg-gradient-to-r from-green-400 to-green-500': tier.tier === 'Silver',
                    'bg-gradient-to-r from-gray-400 to-gray-500': !['Platinum', 'Gold', 'Basic', 'Silver'].includes(tier.tier)
                  }"
                  :style="{ width: tier.percentage + '%' }"
                ></div>
              </div>
            </div>

            <!-- Pie Chart Visual (Simplified for dynamic data) -->
            <div class="mt-8 flex justify-center">
              <div class="relative w-48 h-48">
                 <!-- Note: SVG Pie chart logic is complex with dynamic data. 
                      For now, using a simple placeholder or we can implement a proper chart library later. 
                      Keeping the CSS circle for visual consistency if data exists. -->
                <svg viewBox="0 0 100 100" class="transform -rotate-90">
                  <circle cx="50" cy="50" r="40" fill="none" stroke="#E5E7EB" stroke-width="20" />
                  <circle 
                    v-if="membershipDistribution.length > 0"
                    cx="50" cy="50" r="40" fill="none" stroke="#3B82F6" stroke-width="20" 
                    :stroke-dasharray="`${membershipDistribution[0].percentage * 2.51} 251`" 
                  />
                  <circle 
                    v-if="membershipDistribution.length > 1"
                    cx="50" cy="50" r="40" fill="none" stroke="#A855F7" stroke-width="20" 
                    :stroke-dasharray="`${membershipDistribution[1].percentage * 2.51} 251`"
                    :stroke-dashoffset="`${-membershipDistribution[0].percentage * 2.51}`" 
                  />
                  <!-- More circles would require more complex calculation logic -->
                </svg>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Bottom Row -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- Top Products -->
        <div class="bg-white rounded-2xl shadow-lg p-6">
          <h2 class="text-xl font-bold text-gray-900 mb-6">Sản phẩm bán chạy</h2>
          <div v-if="topProducts.length === 0" class="text-center text-gray-400 py-4">
            Chưa có dữ liệu sản phẩm
          </div>
          <div v-else class="space-y-4">
            <div
              v-for="(product, index) in topProducts"
              :key="product.name"
              class="flex items-center justify-between p-4 bg-gray-50 rounded-xl hover:bg-gray-100 transition"
            >
              <div class="flex items-center gap-4">
                <div class="w-10 h-10 rounded-full bg-gradient-to-br from-blue-500 to-purple-500 flex items-center justify-center text-white font-bold">
                  {{ index + 1 }}
                </div>
                <div>
                  <p class="font-semibold text-gray-900">{{ product.name }}</p>
                  <p class="text-sm text-gray-500">Đã bán: {{ product.sold }} sản phẩm</p>
                </div>
              </div>
              <div class="text-right">
                <p class="font-bold text-green-600">{{ formatCurrency(product.revenue) }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Recent Activities -->
        <div class="bg-white rounded-2xl shadow-lg p-6">
          <h2 class="text-xl font-bold text-gray-900 mb-6">Hoạt động gần đây</h2>
          <div v-if="recentActivities.length === 0" class="text-center text-gray-400 py-4">
            Chưa có hoạt động nào
          </div>
          <div v-else class="space-y-4">
            <div
              v-for="(activity, index) in recentActivities"
              :key="index"
              class="flex items-start gap-4 p-3 hover:bg-gray-50 rounded-lg transition"
            >
              <div
                class="w-10 h-10 rounded-full flex items-center justify-center flex-shrink-0"
                :class="{
                  'bg-green-100': activity.type === 'member',
                  'bg-blue-100': activity.type === 'bill',
                  'bg-purple-100': activity.type === 'class',
                  'bg-orange-100': activity.type === 'product',
                }"
              >
                <svg class="w-5 h-5" :class="{
                  'text-green-600': activity.type === 'member',
                  'text-blue-600': activity.type === 'bill',
                  'text-purple-600': activity.type === 'class',
                  'text-orange-600': activity.type === 'product',
                }" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path v-if="activity.type === 'member'" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                  <path v-else-if="activity.type === 'bill'" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                  <path v-else-if="activity.type === 'class'" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                  <path v-else stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
                </svg>
              </div>
              <div class="flex-1">
                <p class="text-sm text-gray-900">{{ activity.action }}</p>
                <p class="text-xs text-gray-500 mt-1">{{ activity.time }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Smooth animations */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.space-y-6 > * {
  animation: fadeIn 0.5s ease-out;
}
</style>
