<template>
  <div class="p-6 space-y-6">
    <h1 class="text-2xl font-semibold text-gray-800">Mã Giảm Giá (hãy đến trung tâm để sử dụng)</h1>

    <!-- Thông báo nếu không có coupon -->
    <div v-if="coupons.length === 0" class="text-gray-500 italic">
      Hiện chưa có mã giảm giá nào khả dụng.
    </div>

    <!-- Danh sách coupon -->
    <div v-else class="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div
        v-for="coupon in coupons"
        :key="coupon.id"
        class="border rounded-xl shadow-sm p-4 bg-white hover:shadow-md transition"
      >
        <div class="flex justify-between items-center mb-2">
          <h2 class="text-lg font-bold text-emerald-600">{{ coupon.code }}</h2>
          <span
            class="text-sm px-2 py-1 rounded-full"
            :class="{
              'bg-red-100 text-red-600': coupon.isExpired,
              'bg-yellow-100 text-yellow-600': coupon.isAlmostExpired,
              'bg-green-100 text-green-600': !coupon.isExpired && !coupon.isAlmostExpired
            }"
          >
            {{ coupon.label }}
          </span>
        </div>

        <p class="text-gray-700 mb-2">{{ coupon.description }}</p>

        <div class="text-sm text-gray-500 space-y-1">
          <p>Giảm giá: <span class="font-medium text-gray-800">{{ coupon.discount }}{{ coupon.discountType === 'PERCENTAGE' ? '%' : '₫' }}</span></p>
          <p>Hạn dùng: {{ coupon.expiry }}</p>
          <p>Số lượt còn lại: {{ coupon.remainingUses }}</p>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/useAuthStore';
import api from '@/services/api';

const useAuth = useAuthStore();
const coupons = ref([]);

const formatDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return date.toLocaleDateString('vi-VN');
}

const ONE_WEEK_MS = 7 * 24 * 60 * 60 * 1000;

onMounted(async () => {
  const memberId = useAuth.user.id;

  try {
    const res = await api.get(`/issued-coupons/find?memberId=${memberId}`);

    coupons.value = res.data
      // Lọc bỏ coupon đã hết hạn
      .filter(item => new Date(item.coupon.endDate) >= new Date())
      .map(item => {
        const endDate = new Date(item.coupon.endDate);
        const now = new Date();
        const timeLeft = endDate - now;

        const isAlmostExpired = timeLeft <= ONE_WEEK_MS;
        return {
          id: item.coupon.id,
          code: item.coupon.code,
          description: item.coupon.scope ? `Mã giảm cho ${item.coupon.scope}` : '',
          discount: item.coupon.discountValue,
          discountType: item.coupon.discountType, 
          expiry: formatDate(item.coupon.endDate),
          isExpired: false,
          isAlmostExpired: isAlmostExpired,
          label: isAlmostExpired ? 'Sắp hết hạn' : 'Còn hạn',
          remainingUses: item.remainingUses || 0
        };
      });
  } catch (error) {
    console.error('Lỗi khi lấy coupon:', error);
  }
});
</script>

<style scoped>
button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
