<script setup>
import { ref, onMounted } from "vue"

const membership = ref({
  name: "Gói 3 tháng - Gym & Yoga",
  expiryDate: "2025-12-31",
  status: "Active",
})

const upcomingClasses = ref([
  { id: 1, name: "Yoga buổi sáng", date: "2025-10-08", time: "07:00 - 08:00" },
  { id: 2, name: "Body Pump", date: "2025-10-09", time: "18:00 - 19:00" },
])

const coupons = ref([
  { code: "FIT10", desc: "Giảm 10% gói tập mới", valid: "2025-10-31" },
  { code: "YOGA50", desc: "Giảm 50% lớp Yoga đặc biệt", valid: "2025-11-15" },
])

const trainingHistory = ref([
  { date: "2025-10-05", activity: "Tập Gym", duration: "60 phút" },
  { date: "2025-10-03", activity: "Yoga", duration: "45 phút" },
])

onMounted(() => {
  // Customer dashboard loaded
})
</script>

<template>
  <div class="p-6 space-y-8">
    <!-- Tiêu đề -->
    <h1 class="text-2xl font-bold text-stone-800">Bảng điều khiển khách hàng</h1>

    <!-- Thông tin hội viên -->
    <section class="bg-white rounded-xl shadow p-5">
      <h2 class="text-xl font-semibold mb-3">Thông tin hội viên</h2>
      <div class="grid md:grid-cols-3 gap-4 text-gray-700">
        <p><strong>Gói tập:</strong> {{ membership.name }}</p>
        <p><strong>Hạn sử dụng:</strong> {{ membership.expiryDate }}</p>
        <p>
          <strong>Trạng thái:</strong>
          <span
            :class="membership.status === 'Active' ? 'text-green-600 font-medium' : 'text-red-600 font-medium'"
            >{{ membership.status }}</span
          >
        </p>
      </div>
    </section>

    <!-- Lớp học sắp tới -->
    <section class="bg-white rounded-xl shadow p-5">
      <h2 class="text-xl font-semibold mb-3">Lớp học sắp tới</h2>
      <table class="min-w-full text-sm text-gray-700">
        <thead>
          <tr class="border-b text-left font-medium">
            <th class="py-2">Tên lớp</th>
            <th class="py-2">Ngày</th>
            <th class="py-2">Thời gian</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="cls in upcomingClasses" :key="cls.id" class="border-b hover:bg-gray-50">
            <td class="py-2">{{ cls.name }}</td>
            <td class="py-2">{{ cls.date }}</td>
            <td class="py-2">{{ cls.time }}</td>
          </tr>
        </tbody>
      </table>
    </section>

    <!-- Coupon -->
    <section class="bg-white rounded-xl shadow p-5">
      <h2 class="text-xl font-semibold mb-3">Coupon hiện có</h2>
      <ul class="divide-y divide-gray-200">
        <li v-for="c in coupons" :key="c.code" class="py-2">
          <p class="font-medium text-red-600">{{ c.code }}</p>
          <p>{{ c.desc }}</p>
          <p class="text-sm text-gray-500">Hạn dùng: {{ c.valid }}</p>
        </li>
      </ul>
    </section>

    <!-- Lịch sử tập luyện -->
    <section class="bg-white rounded-xl shadow p-5">
      <h2 class="text-xl font-semibold mb-3">Lịch sử tập luyện gần đây</h2>
      <table class="min-w-full text-sm text-gray-700">
        <thead>
          <tr class="border-b text-left font-medium">
            <th class="py-2">Ngày</th>
            <th class="py-2">Hoạt động</th>
            <th class="py-2">Thời lượng</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, idx) in trainingHistory" :key="idx" class="border-b hover:bg-gray-50">
            <td class="py-2">{{ item.date }}</td>
            <td class="py-2">{{ item.activity }}</td>
            <td class="py-2">{{ item.duration }}</td>
          </tr>
        </tbody>
      </table>
    </section>
  </div>
</template>

<style scoped>
section {
  transition: all 0.2s ease;
}
section:hover {
  transform: translateY(-2px);
}
</style>
