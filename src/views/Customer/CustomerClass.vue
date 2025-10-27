<script setup>
import { ref, computed } from "vue"

// Mock data: Danh sách lớp học
const classes = ref([
  { id: 1, name: "Yoga buổi sáng", type: "Yoga", time: "06:00 - 07:00", day: "Thứ 2, 4, 6", status: "Available", instructor: "Nguyễn Mai" },
  { id: 2, name: "Zumba năng động", type: "Zumba", time: "18:00 - 19:00", day: "Thứ 3, 5", status: "Full", instructor: "Lê Hưng" },
  { id: 3, name: "Cardio đốt mỡ", type: "Gym", time: "17:00 - 18:00", day: "Hàng ngày", status: "Available", instructor: "Trần Quân" },
  { id: 4, name: "Yoga thư giãn", type: "Yoga", time: "19:00 - 20:00", day: "Thứ 2 - Thứ 7", status: "Available", instructor: "Nguyễn Mai" },
  { id: 5, name: "Body Combat", type: "Gym", time: "07:00 - 08:00", day: "Thứ 7", status: "Full", instructor: "Lê Phong" },
])

// Bộ lọc
const selectedType = ref("All")
const selectedStatus = ref("All")

// Tính toán danh sách đã lọc
const filteredClasses = computed(() => {
  return classes.value.filter(c => {
    const matchType = selectedType.value === "All" || c.type === selectedType.value
    const matchStatus = selectedStatus.value === "All" || c.status === selectedStatus.value
    return matchType && matchStatus
  })
})
</script>

<template>
  <div class="p-6 space-y-8">
    <h1 class="text-2xl font-bold text-stone-800">Lớp học</h1>

    <!-- Bộ lọc -->
    <div class="flex flex-wrap gap-4 items-center">
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">Loại lớp</label>
        <select v-model="selectedType" class="border rounded-md px-3 py-2 text-gray-700 focus:ring-red-500 focus:border-red-500">
          <option>All</option>
          <option>Yoga</option>
          <option>Gym</option>
          <option>Zumba</option>
        </select>
      </div>

      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">Trạng thái</label>
        <select v-model="selectedStatus" class="border rounded-md px-3 py-2 text-gray-700 focus:ring-red-500 focus:border-red-500">
          <option>All</option>
          <option>Available</option>
          <option>Full</option>
        </select>
      </div>
    </div>

    <!-- Danh sách lớp học -->
    <div class="grid sm:grid-cols-2 lg:grid-cols-3 gap-6">
      <div
        v-for="cls in filteredClasses"
        :key="cls.id"
        class="bg-white rounded-xl shadow p-5 border hover:shadow-lg transition"
      >
        <h2 class="text-lg font-semibold text-stone-800 mb-2">{{ cls.name }}</h2>
        <p class="text-gray-600"><strong>Loại:</strong> {{ cls.type }}</p>
        <p class="text-gray-600"><strong>Thời gian:</strong> {{ cls.time }}</p>
        <p class="text-gray-600"><strong>Lịch:</strong> {{ cls.day }}</p>
        <p class="text-gray-600"><strong>HLV:</strong> {{ cls.instructor }}</p>

        <p class="mt-2">
          <strong>Trạng thái:</strong>
          <span
            :class="cls.status === 'Available' ? 'text-green-600 font-medium' : 'text-red-600 font-medium'"
          >{{ cls.status }}</span>
        </p>

        <button
          :disabled="cls.status === 'Full'"
          class="mt-4 w-full px-4 py-2 rounded-lg font-medium text-white transition"
          :class="cls.status === 'Full'
            ? 'bg-gray-400 cursor-not-allowed'
            : 'bg-red-600 hover:bg-red-700'"
        >
          {{ cls.status === 'Full' ? 'Hết chỗ' : 'Đăng ký ngay' }}
        </button>
      </div>
    </div>

    <!-- Không có lớp -->
    <div v-if="filteredClasses.length === 0" class="text-center text-gray-500 py-8">
      Không có lớp học nào phù hợp với bộ lọc của bạn.
    </div>
  </div>
</template>
