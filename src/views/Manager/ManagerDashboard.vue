<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();

// ===================== MOCK DATA =====================
const stats = ref({
  totalRevenue: 125000000,
  totalMembers: 342,
  activeMembers: 298,
  newMembersThisMonth: 45,
  totalClasses: 28,
  totalProducts: 156,
  pendingBills: 12,
  completedBills: 234,
});

const revenueByMonth = ref([
  { month: "T1", revenue: 85000000 },
  { month: "T2", revenue: 92000000 },
  { month: "T3", revenue: 88000000 },
  { month: "T4", revenue: 95000000 },
  { month: "T5", revenue: 102000000 },
  { month: "T6", revenue: 98000000 },
  { month: "T7", revenue: 110000000 },
  { month: "T8", revenue: 115000000 },
  { month: "T9", revenue: 108000000 },
  { month: "T10", revenue: 120000000 },
  { month: "T11", revenue: 118000000 },
  { month: "T12", revenue: 125000000 },
]);

const membershipDistribution = ref([
  { tier: "Basic", count: 120, percentage: 35 },
  { tier: "Premium", count: 150, percentage: 44 },
  { tier: "VIP", count: 72, percentage: 21 },
]);

const topProducts = ref([
  { name: "Whey Protein", sold: 145, revenue: 43500000 },
  { name: "Yoga Mat", sold: 89, revenue: 8900000 },
  { name: "Gym Gloves", sold: 76, revenue: 6080000 },
  { name: "Water Bottle", sold: 234, revenue: 7020000 },
  { name: "Resistance Band", sold: 56, revenue: 3360000 },
]);

const recentActivities = ref([
  { type: "member", action: "Nguyễn Văn A đăng ký gói Premium", time: "5 phút trước" },
  { type: "bill", action: "Hóa đơn #1234 đã thanh toán", time: "15 phút trước" },
  { type: "class", action: "Lớp Yoga buổi sáng đã bắt đầu", time: "1 giờ trước" },
  { type: "product", action: "Nhập kho 50 chai Whey Protein", time: "2 giờ trước" },
  { type: "member", action: "Trần Thị B gia hạn membership", time: "3 giờ trước" },
]);

// ===================== COMPUTED =====================
const maxRevenue = computed(() => Math.max(...revenueByMonth.value.map(m => m.revenue)));

const chartHeight = 200;

const getBarHeight = (revenue: number) => {
  return (revenue / maxRevenue.value) * chartHeight;
};

const formatCurrency = (value: number) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

const memberGrowthRate = computed(() => {
  return ((stats.value.newMembersThisMonth / stats.value.totalMembers) * 100).toFixed(1);
});

onMounted(() => {
  console.log("Manager Dashboard loaded");
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

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <!-- Revenue Card -->
      <div class="bg-gradient-to-br from-blue-500 to-blue-600 rounded-2xl shadow-lg p-6 text-white transform hover:scale-105 transition">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-blue-100 text-sm font-medium">Doanh thu tháng này</p>
            <p class="text-3xl font-bold mt-2">{{ formatCurrency(stats.totalRevenue) }}</p>
            <p class="text-blue-100 text-xs mt-2">↗ +12.5% so với tháng trước</p>
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
            <p class="text-purple-100 text-xs mt-2">Tăng trưởng {{ memberGrowthRate }}%</p>
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
          <div
            v-for="item in revenueByMonth"
            :key="item.month"
            class="flex-1 flex flex-col items-center group"
          >
            <div class="relative w-full">
              <div
                class="bg-gradient-to-t from-blue-500 to-blue-400 rounded-t-lg transition-all duration-300 hover:from-blue-600 hover:to-blue-500 cursor-pointer"
                :style="{ height: getBarHeight(item.revenue) + 'px' }"
              >
                <div class="absolute -top-8 left-1/2 transform -translate-x-1/2 opacity-0 group-hover:opacity-100 transition-opacity bg-gray-900 text-white text-xs px-2 py-1 rounded whitespace-nowrap">
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
        <div class="space-y-4">
          <div v-for="tier in membershipDistribution" :key="tier.tier">
            <div class="flex items-center justify-between mb-2">
              <span class="text-sm font-medium text-gray-700">{{ tier.tier }}</span>
              <span class="text-sm font-semibold text-gray-900">{{ tier.count }} ({{ tier.percentage }}%)</span>
            </div>
            <div class="w-full bg-gray-200 rounded-full h-3 overflow-hidden">
              <div
                class="h-full rounded-full transition-all duration-500"
                :class="{
                  'bg-gradient-to-r from-blue-400 to-blue-500': tier.tier === 'Basic',
                  'bg-gradient-to-r from-purple-400 to-purple-500': tier.tier === 'Premium',
                  'bg-gradient-to-r from-yellow-400 to-yellow-500': tier.tier === 'VIP',
                }"
                :style="{ width: tier.percentage + '%' }"
              ></div>
            </div>
          </div>
        </div>

        <!-- Pie Chart Visual -->
        <div class="mt-8 flex justify-center">
          <div class="relative w-48 h-48">
            <svg viewBox="0 0 100 100" class="transform -rotate-90">
              <circle cx="50" cy="50" r="40" fill="none" stroke="#3B82F6" stroke-width="20" 
                :stroke-dasharray="`${membershipDistribution[0].percentage * 2.51} 251`" />
              <circle cx="50" cy="50" r="40" fill="none" stroke="#A855F7" stroke-width="20" 
                :stroke-dasharray="`${membershipDistribution[1].percentage * 2.51} 251`"
                :stroke-dashoffset="`${-membershipDistribution[0].percentage * 2.51}`" />
              <circle cx="50" cy="50" r="40" fill="none" stroke="#FBBF24" stroke-width="20" 
                :stroke-dasharray="`${membershipDistribution[2].percentage * 2.51} 251`"
                :stroke-dashoffset="`${-(membershipDistribution[0].percentage + membershipDistribution[1].percentage) * 2.51}`" />
            </svg>
          </div>
        </div>
      </div>
    </div>

    <!-- Bottom Row -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- Top Products -->
      <div class="bg-white rounded-2xl shadow-lg p-6">
        <h2 class="text-xl font-bold text-gray-900 mb-6">Sản phẩm bán chạy</h2>
        <div class="space-y-4">
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
        <div class="space-y-4">
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
