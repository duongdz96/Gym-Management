<script setup>
import { ref, computed } from "vue"

// Mock data: kế hoạch tập luyện
const plans = ref([
  {
    id: 1,
    date: "2025-10-01",
    muscleGroup: "Ngực & Tay sau",
    exercises: ["Bench Press", "Incline Dumbbell Press", "Tricep Dips"],
    trainer: "HLV Trần Quốc Hưng",
    status: "Completed",
  },
  {
    id: 2,
    date: "2025-10-03",
    muscleGroup: "Lưng & Tay trước",
    exercises: ["Pull-up", "Lat Pulldown", "Barbell Row"],
    trainer: "HLV Lê Thảo",
    status: "In Progress",
  },
  {
    id: 3,
    date: "2025-10-05",
    muscleGroup: "Chân & Mông",
    exercises: ["Squat", "Leg Press", "Lunges"],
    trainer: "HLV Nguyễn Khánh",
    status: "Upcoming",
  },
  {
    id: 4,
    date: "2025-10-07",
    muscleGroup: "Cardio & Core",
    exercises: ["Treadmill Run", "Plank", "Mountain Climbers"],
    trainer: "HLV Nguyễn Mai",
    status: "Upcoming",
  },
])

// Bộ lọc theo trạng thái buổi tập
const selectedStatus = ref("All")

const filteredPlans = computed(() => {
  if (selectedStatus.value === "All") return plans.value
  return plans.value.filter((p) => p.status === selectedStatus.value)
})
</script>

<template>
  <div class="p-6 space-y-8">
    <h1 class="text-2xl font-bold text-stone-800">Kế hoạch tập luyện</h1>

    <!-- Bộ lọc -->
    <div>
      <label class="text-sm font-medium text-gray-700 mr-3">Lọc theo trạng thái:</label>
      <select
        v-model="selectedStatus"
        class="border rounded-md px-3 py-2 text-gray-700 focus:ring-red-500 focus:border-red-500"
      >
        <option>All</option>
        <option>Upcoming</option>
        <option>In Progress</option>
        <option>Completed</option>
      </select>
    </div>

    <!-- Danh sách kế hoạch -->
    <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div
        v-for="plan in filteredPlans"
        :key="plan.id"
        class="bg-white border rounded-xl shadow-sm p-5 hover:shadow-lg transition"
      >
        <div class="flex items-center justify-between mb-2">
          <h2 class="text-lg font-semibold text-stone-800">
            Ngày {{ new Date(plan.date).toLocaleDateString('vi-VN') }}
          </h2>
          <span
            class="text-xs font-semibold px-2 py-1 rounded-full"
            :class="{
              'bg-green-100 text-green-700': plan.status === 'Completed',
              'bg-yellow-100 text-yellow-700': plan.status === 'In Progress',
              'bg-blue-100 text-blue-700': plan.status === 'Upcoming'
            }"
          >
            {{ plan.status }}
          </span>
        </div>

        <p class="text-gray-600 mb-2">
          <strong>Nhóm cơ:</strong> {{ plan.muscleGroup }}
        </p>

        <div class="mb-2">
          <strong>Bài tập:</strong>
          <ul class="list-disc list-inside text-gray-600 text-sm mt-1">
            <li v-for="ex in plan.exercises" :key="ex">{{ ex }}</li>
          </ul>
        </div>

        <p class="text-gray-600"><strong>Huấn luyện viên:</strong> {{ plan.trainer }}</p>

        <!-- Nút hành động -->
        <div class="mt-4 flex justify-end">
          <button
            v-if="plan.status === 'Upcoming'"
            class="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-lg text-sm"
          >
            Bắt đầu buổi tập
          </button>
          <button
            v-else-if="plan.status === 'In Progress'"
            class="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-lg text-sm"
          >
            Hoàn thành
          </button>
          <button
            v-else
            class="bg-gray-400 text-white px-4 py-2 rounded-lg text-sm cursor-not-allowed"
          >
            Đã hoàn thành
          </button>
        </div>
      </div>
    </div>

    <!-- Không có kế hoạch -->
    <div v-if="filteredPlans.length === 0" class="text-center text-gray-500 py-8">
      Không có kế hoạch tập luyện phù hợp.
    </div>
  </div>
</template>
