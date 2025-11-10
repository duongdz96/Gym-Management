<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import api from '@/services/api'

const route = useRoute()
const coupon = ref(null)
const issuedCoupons = ref([])

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  })
}

const fetchCouponDetail = async () => {
  try {
    const id = route.params.id;
    const res = await api.get(`/coupons/${id}`);
    coupon.value = res.data.coupon;
    issuedCoupons.value = res.data.issuedCoupons || [];
  } catch (error) {
    console.error("Error fetching coupon:", error);
  }
}

onMounted(fetchCouponDetail)
</script>

<template>
  <div class="p-6 max-w-5xl mx-auto bg-white rounded-lg shadow">
    <h2 class="text-2xl font-bold mb-6 text-gray-800">
      🎟 Coupon Detail
    </h2>

    <div v-if="coupon" class="space-y-3">
      <p><strong>Code:</strong> {{ coupon.code }}</p>
      <p><strong>Discount Type:</strong> {{ coupon.discountType }}</p>
      <p><strong>Value:</strong> {{ coupon.discountValue }}</p>
      <p>
        <strong>Duration:</strong>
        {{ formatDate(coupon.startDate) }} - {{ formatDate(coupon.endDate) }}
      </p>
      <p><strong>Status:</strong> {{ coupon.status }}</p>
      <p><strong>Scope:</strong> {{ coupon.scope }}</p>
    </div>
    <div v-else class="text-gray-500 italic">Đang tải dữ liệu...</div>

    <h3 class="text-xl font-semibold mt-8 mb-4">
      👥 Issued Members
    </h3>

    <table class="min-w-full bg-white border border-gray-200 rounded-lg">
      <thead>
        <tr class="bg-gray-100 text-left text-gray-700 text-sm">
          <th class="py-2 px-4 border-b">#</th>
          <th class="py-2 px-4 border-b">Member Name</th>
          <th class="py-2 px-4 border-b">Email</th>
          <th class="py-2 px-4 border-b">Remaining Uses</th>
          <th class="py-2 px-4 border-b">Status</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="(ic, index) in issuedCoupons"
          :key="ic.id"
          class="border-t text-sm text-gray-600 hover:bg-gray-50"
        >
          <td class="py-2 px-4">{{ index + 1 }}</td>
          <td class="py-2 px-4">{{ ic.member?.fullName || 'N/A' }}</td>
          <td class="py-2 px-4">{{ ic.member?.email || 'N/A' }}</td>
          <td class="py-2 px-4">{{ ic.remainingUses }}</td>
          <td class="py-2 px-4">{{ ic.status }}</td>
        </tr>
        <tr v-if="issuedCoupons.length === 0">
          <td colspan="5" class="py-3 px-4 text-center text-gray-500">
            No members have been issued this coupon.
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

