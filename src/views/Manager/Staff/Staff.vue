<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import api from '@/services/api'


type User = {
  id: number
  fullName: string
  email: string
  dob: string
  gender: string
  phone: string
  role: string
}

const users = ref<User[]>([])
const search = ref('')
const roleFilter = ref('') // 2. Đổi từ positionFilter thành roleFilter

onMounted(async () => {
  try {
    const res = await api.get("/users/staffs") // Endpoint này có lẽ vẫn trả về staff?
    users.value = res.data
  } catch (err) {
    console.error("Error fetching staff:", err)
  }
})

const filteredUsers = computed(() => {
  return users.value.filter(user => {
    const matchesName = user.fullName.toLowerCase().includes(search.value.toLowerCase())
    const matchesRole = roleFilter.value ? user.role === roleFilter.value : true
    return matchesName && matchesRole
  })
})


const formatDate = (dateString: string) => {
  if (!dateString) return 'N/A'
  try {
    const date = new Date(dateString)
    return date.toLocaleDateString('vi-VN') 
  } catch (error) {
    return dateString 
  }
}
</script>

<template>
  <div class="space-y-4 p-4">
    <div class="flex justify-between items-center">
      <h1 class="text-xl font-semibold">Staff</h1>
      <div>
        <input
          v-model="search"
          type="text"
          placeholder="Search by staff name"
          class="px-3 py-2 border rounded-lg mr-2"
        />
        <select v-model="roleFilter" class="px-3 py-2 border rounded-lg mr-2">
          <option value="">All Roles</option>
          <option value="STAFF">Staff</option>
          <option value="MANAGER">Manager</option>
          <option value="RECEPTIONIST">Receptionist</option>
        </select>
        <RouterLink
          :to= "{name: 'staff.add'}"
          class="px-3 py-2 bg-blue-600 text-white rounded-lg hover:opacity-90 mr-2"
        >
          Add
        </RouterLink>
        <RouterLink
          :to= "{name: 'staff.attendance-history'}"
          class="px-3 py-2 bg-green-600 text-white rounded-lg hover:opacity-90"
        >
          Attendance History
        </RouterLink>
      </div>
    </div>

    <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Full Name</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Email</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Phone</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Gender</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Date of Birth</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Role</th>
            <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-200 bg-white">
          <tr v-if="filteredUsers.length === 0">
            <td colspan="8" class="px-4 py-3 text-center text-sm text-gray-500">No staff found</td>
          </tr>
          <tr v-for="user in filteredUsers" :key="user.id" class="hover:bg-gray-50">
            <td class="px-4 py-3 text-sm text-gray-600">{{ user.id }}</td>
            <td class="px-4 py-3 text-sm text-blue-600 hover:underline hover:cursor-pointer">{{ user.fullName }}</td>
            <td class="px-4 py-3 text-sm text-gray-600">{{ user.email }}</td>
            <td class="px-4 py-3 text-sm text-gray-600">{{ user.phone }}</td>
            <td class="px-4 py-3 text-sm text-gray-600">{{ user.gender }}</td>
            <td class="px-4 py-3 text-sm text-gray-600">{{ formatDate(user.dob) }}</td>
            <td class="px-4 py-3 text-sm text-gray-600">
              <span 
                :class="[
                  'px-2 py-0.5 rounded-full text-xs font-medium',
                  user.role === 'MANAGER' ? 'bg-red-100 text-red-800' :
                  user.role === 'STAFF' ? 'bg-blue-100 text-blue-800' :
                  'bg-gray-100 text-gray-800'
                ]"
              >
                {{ user.role }}
              </span>
            </td>
            <td class="px-4 py-3 text-sm text-center">
              <RouterLink 
                :to="`staff/${user.id}`" 
                class="px-2 py-1 rounded bg-green-600 text-white mr-2 hover:bg-green-700 hover:cursor-pointer"
              >
                Select
              </RouterLink>
              <button class="px-2 py-1 rounded bg-red-600 text-white hover:bg-red-700 hover:cursor-pointer">
                Delete
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>