<template>
  <div class="p-6 space-y-6">
    <h1 class="text-2xl font-semibold text-gray-800">Mã Giảm Giá</h1>

    <!-- Thông báo nếu không có coupon -->
    <div v-if="coupons.length === 0" class="text-gray-500 italic">
      Hiện chưa có mã giảm giá nào khả dụng.
    </div>

    <!-- Danh sách coupon -->
    <div
      v-else
      class="grid md:grid-cols-2 lg:grid-cols-3 gap-6"
    >
      <div
        v-for="coupon in coupons"
        :key="coupon.id"
        class="border rounded-xl shadow-sm p-4 bg-white hover:shadow-md transition"
      >
        <div class="flex justify-between items-center mb-2">
          <h2 class="text-lg font-bold text-indigo-600">{{ coupon.code }}</h2>
          <span
            class="text-sm px-2 py-1 rounded-full"
            :class="coupon.isExpired ? 'bg-red-100 text-red-600' : 'bg-green-100 text-green-600'"
          >
            {{ coupon.isExpired ? 'Hết hạn' : 'Còn hạn' }}
          </span>
        </div>

        <p class="text-gray-700 mb-2">{{ coupon.description }}</p>

        <div class="text-sm text-gray-500 space-y-1">
          <p>Giảm giá: <span class="font-medium text-gray-800">{{ coupon.discount }}%</span></p>
          <p>Hạn dùng: {{ coupon.expiry }}</p>
        </div>

        <button
          class="mt-4 w-full bg-indigo-500 text-white py-2 rounded-lg hover:bg-indigo-600 transition"
          :disabled="coupon.isExpired"
        >
          Sử dụng mã
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// Fake data coupon
const coupons = ref([
  {
    id: 1,
    code: 'WELCOME10',
    description: 'Giảm 10% cho đơn hàng đầu tiên.',
    discount: 10,
    expiry: '2025-12-31',
    isExpired: false,
  },
  {
    id: 2,
    code: 'HEALTH20',
    description: 'Giảm 20% cho gói khám sức khỏe tổng quát.',
    discount: 20,
    expiry: '2025-08-01',
    isExpired: true,
  },
  {
    id: 3,
    code: 'FITNESS15',
    description: 'Giảm 15% cho dịch vụ tập luyện thể chất.',
    discount: 15,
    expiry: '2025-11-15',
    isExpired: false,
  },
])
</script>

<style scoped>
button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
