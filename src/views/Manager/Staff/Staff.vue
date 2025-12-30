<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import api from '@/services/api'
import { useToast } from 'vue-toastification'


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

const deleteStaff = async (user: User) => {
  if (!confirm(`Bạn có chắc chắn muốn xóa nhân viên "${user.fullName}"?`)) {
    return
  }

  try {
    await api.delete(`/users/${user.id}`)
    // Remove from local state
    const index = users.value.findIndex(u => u.id === user.id)
    if (index > -1) {
      users.value.splice(index, 1)
    }

    useToast().success('Xóa nhân viên thành công')
  } catch (err) {
    console.error('Error deleting staff:', err)
    useToast().error('Không thể xóa nhân viên. Vui lòng thử lại.')
  }
}
</script>

<template>
  <div class="space-y-4 p-4">
    <div class="flex justify-between items-center">
      <h1 class="text-xl font-semibold">Quản Lý Nhân Viên</h1>
      <div>
        <input
          v-model="search"
          type="text"
          placeholder="Tìm kiếm theo tên nhân viên"
          class="px-3 py-2 border rounded-lg mr-2"
        />
        <select v-model="roleFilter" class="px-3 py-2 border rounded-lg mr-2">
          <option value="">Tất cả vai trò</option>
          <option value="TEACHER">Giáo viên</option>
          <option value="PT">PT</option>
          <option value="MANAGER">Quản lý</option>
          <option value="RECEPTIONIST">Lễ tân</option>
        </select>
        <RouterLink
          :to= "{name: 'staff.add'}"
          class="px-3 py-2 bg-emerald-600 text-white rounded-lg hover:opacity-90"
        >
          Thêm mới
        </RouterLink>
      </div>
    </div>

    <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Họ tên</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Email</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Số điện thoại</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Giới tính</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Ngày sinh</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Vai trò</th>
            <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">Thao tác</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-200 bg-white">
          <tr v-if="filteredUsers.length === 0">
            <td colspan="8" class="px-4 py-3 text-center text-sm text-gray-500">Không tìm thấy nhân viên nào</td>
          </tr>
          <tr v-for="user in filteredUsers" :key="user.id" class="hover:bg-gray-50">
            <td class="px-4 py-3 text-sm text-gray-600">{{ user.id }}</td>
            <td class="px-4 py-3 text-sm text-emerald-600 hover:underline hover:cursor-pointer">{{ user.fullName }}</td>
            <td class="px-4 py-3 text-sm text-gray-600">{{ user.email }}</td>
            <td class="px-4 py-3 text-sm text-gray-600">{{ user.phone }}</td>
            <td class="px-4 py-3 text-sm text-gray-600">{{ user.gender }}</td>
            <td class="px-4 py-3 text-sm text-gray-600">{{ formatDate(user.dob) }}</td>
            <td class="px-4 py-3 text-sm text-gray-600">
              <span
                :class="[
                  'px-2 py-0.5 rounded-full text-xs font-medium',
                  user.role === 'MANAGER' ? 'bg-blue-100 text-blue-800' :
                  user.role === 'RECEPTIONIST' ? 'bg-purple-100 text-purple-800' :
                  user.role === 'PT' ? 'bg-emerald-100 text-emerald-800' :
                  user.role === 'TEACHER' ? 'bg-amber-100 text-amber-800' :
                  'bg-gray-100 text-gray-800'
                ]"
              >
                {{ user.role }}
              </span>
            </td>
            <td class="px-4 py-3 text-sm text-center">
              <div class="flex items-center justify-center gap-2">
                <RouterLink
                  :to="`staff/${user.id}/edit`"
                  class="p-1.5 text-blue-600 hover:bg-blue-50 rounded-md transition-colors"
                  title="Chỉnh sửa"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="m18.5 2.5 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                </RouterLink>

                <RouterLink
                  :to="`staff/${user.id}`"
                  class="p-1.5 text-emerald-600 hover:bg-emerald-50 rounded-md transition-colors"
                  title="Xem chi tiết"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                </RouterLink>

                <button
                  @click="deleteStaff(user)"
                  class="p-1.5 text-red-600 hover:bg-red-50 rounded-md transition-colors"
                  title="Xóa"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 6h18"/><path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"/><path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"/><line x1="10" x2="10" y1="11" y2="17"/><line x1="14" x2="14" y1="11" y2="17"/></svg>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>