<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import api from '@/services/api'

type Staff = {
  id: number
  fullName: string
  position: string
  specialize: string
  hirePrice: number
}

const staffs = ref<Staff[]>([])
const search = ref('')
const positionFilter = ref('')

onMounted(async () => {
  try {
    const res = await api.get("/staffs")
    staffs.value = res.data
  } catch (err) {
    console.error("Error fetching staff:", err)
  }
})

const filteredStaffs = computed(() => {
  return staffs.value.filter(s => {
    const matchesName = s.fullName.toLowerCase().includes(search.value.toLowerCase())
    const matchesPosition = positionFilter.value ? s.position === positionFilter.value : true
    return matchesName && matchesPosition
  })
})
</script>

<template>
  <div class="space-y-4 p-4">
    <!-- Header -->
    <div class="flex justify-between items-center">
      <h1 class="text-xl font-semibold">Staff</h1>
      <div>
        <input
          v-model="search"
          type="text"
          placeholder="Search by staff name"
          class="px-3 py-2 border rounded-lg mr-2"
        />
        <select v-model="positionFilter" class="px-3 py-2 border rounded-lg mr-2">
          <option value="">All positions</option>
          <option value="trainer">Trainer</option>
          <option value="receptionist">Receptionist</option>
          <option value="manager">Manager</option>
        </select>
        <button
          class="px-3 py-2 bg-blue-600 text-white rounded-lg hover:opacity-90"
        >
          Add
      </button>
      </div>
    </div>

    <!-- Table -->
    <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Full Name</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Position</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Specialize</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Hire Price</th>
            <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-200 bg-white">
          <tr v-if="staffs.length === 0">
            <td colspan="6" class="px-4 py-3 text-center text-sm text-gray-500">No staff found</td>
          </tr>
          <tr v-for="s in filteredStaffs" :key="s.id" class="hover:bg-gray-50">
            <td class="px-4 py-3 text-sm text-gray-600">{{ s.id }}</td>
            <td class="px-4 py-3 text-sm text-blue-600 hover:underline hover:cursor-pointer">{{ s.fullName }}</td>
            <td class="px-4 py-3 text-sm text-gray-600">{{ s.position }}</td>
            <td class="px-4 py-3 text-sm text-gray-600">{{ s.specialize }}</td>
            <td class="px-4 py-3 text-sm text-gray-600">{{ Number(s.hirePrice).toFixed(0) }}</td>
            <td class="px-4 py-3 text-sm text-center">
              <button class="px-2 py-1 rounded bg-green-600 text-white mr-2 hover:bg-green-700 hover:cursor-pointer">Edit</button>
              <button class="px-2 py-1 rounded bg-red-600 text-white hover:bg-red-700 hover:cursor-pointer">Delete</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
