<script setup>
import { ref, onMounted } from "vue"

const currentMembership = ref({
  name: "Gói 3 tháng - Gym & Yoga",
  price: "1,200,000 VND",
  startDate: "2025-09-01",
  expiryDate: "2025-12-01",
  status: "Active",
})

const availablePackages = ref([
  { id: 1, name: "Gói 1 tháng - Gym", price: "500,000 VND", description: "Phù hợp cho người mới bắt đầu" },
  { id: 2, name: "Gói 6 tháng - Gym & Yoga", price: "2,000,000 VND", description: "Tiết kiệm hơn, thời gian dài hơn" },
  { id: 3, name: "Gói 12 tháng - Premium", price: "3,500,000 VND", description: "Bao gồm mọi lớp học + ưu đãi riêng" },
])

const membershipHistory = ref([
  { id: 1, name: "Gói 1 tháng - Gym", start: "2025-07-01", end: "2025-08-01", price: "500,000 VND" },
  { id: 2, name: "Gói 2 tháng - Yoga", start: "2025-05-01", end: "2025-07-01", price: "900,000 VND" },
])

onMounted(() => {
  console.log("Membership page loaded")
})
</script>

<template>
  <div class="p-6 space-y-8">
    <h1 class="text-2xl font-bold text-stone-800">Gói hội viên</h1>

    <!-- Gói hiện tại -->
    <section class="bg-white rounded-xl shadow p-5">
      <h2 class="text-xl font-semibold mb-3">Gói hiện tại</h2>
      <div class="grid md:grid-cols-2 gap-4 text-gray-700">
        <div>
          <p><strong>Tên gói:</strong> {{ currentMembership.name }}</p>
          <p><strong>Giá:</strong> {{ currentMembership.price }}</p>
        </div>
        <div>
          <p><strong>Ngày bắt đầu:</strong> {{ currentMembership.startDate }}</p>
          <p><strong>Hết hạn:</strong> {{ currentMembership.expiryDate }}</p>
        </div>
      </div>
      <p class="mt-2">
        <strong>Trạng thái:</strong>
        <span
          :class="currentMembership.status === 'Active' ? 'text-green-600 font-medium' : 'text-red-600 font-medium'"
          >{{ currentMembership.status }}</span
        >
      </p>
    </section>

    <!-- Danh sách gói khả dụng -->
    <section class="bg-white rounded-xl shadow p-5">
      <h2 class="text-xl font-semibold mb-3">Các gói khác</h2>
      <div class="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
        <div
          v-for="pkg in availablePackages"
          :key="pkg.id"
          class="border rounded-lg p-4 hover:shadow transition"
        >
          <h3 class="font-semibold text-lg mb-1">{{ pkg.name }}</h3>
          <p class="text-gray-600 mb-1">{{ pkg.description }}</p>
          <p class="text-red-600 font-medium mb-3">{{ pkg.price }}</p>
          <button
            class="bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700 w-full"
          >
            Xem chi tiết
          </button>
        </div>
      </div>
    </section>

    <!-- Lịch sử gói hội viên -->
    <section class="bg-white rounded-xl shadow p-5">
      <h2 class="text-xl font-semibold mb-3">Lịch sử mua gói</h2>
      <table class="min-w-full text-sm text-gray-700">
        <thead>
          <tr class="border-b text-left font-medium">
            <th class="py-2">Tên gói</th>
            <th class="py-2">Bắt đầu</th>
            <th class="py-2">Kết thúc</th>
            <th class="py-2">Giá</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in membershipHistory" :key="item.id" class="border-b hover:bg-gray-50">
            <td class="py-2">{{ item.name }}</td>
            <td class="py-2">{{ item.start }}</td>
            <td class="py-2">{{ item.end }}</td>
            <td class="py-2">{{ item.price }}</td>
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
