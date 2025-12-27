<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import api from '@/services/api'
import { useToast } from 'vue-toastification'

type Membership = {
  id: number
  startDate: string
  endDate: string
  status: string
  member: {
    id: number
    email: string
    password: string
    fullName: string
    dob: string
    gender: string
    phone: string
    role: string
    membership: string
    joinDate: string
    status: string
    faceId: string | null
    cardId: string | null
    deleted: boolean
  }
}

// --- State ---
const memberships = ref<Membership[]>([])
const searchQuery = ref('')
const statusFilter = ref('')
const isLoading = ref(true)

// --- Lifecycle ---
onMounted(async () => {
  try {
    isLoading.value = true
    const res = await api.get('/membership')
    memberships.value = res.data
  } catch (err) {
    console.error('Error fetching memberships:', err)
  } finally {
    isLoading.value = false
  }
})

// --- Computed ---
const filteredMemberships = computed(() => {
  return memberships.value.filter(membership => {
    const member = membership.member
    const matchesSearch = !searchQuery.value ||
      member.fullName.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      member.email.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      member.phone.includes(searchQuery.value)

    const matchesStatus = !statusFilter.value || member.status === statusFilter.value

    return matchesSearch && matchesStatus
  })
})

// --- Helpers ---
const formatDate = (dateString: string) => {
  if (!dateString) return 'N/A'
  try {
    return new Date(dateString).toLocaleDateString('vi-VN')
  } catch { return dateString }
}

const getStatusColor = (status: string) => {
  switch (status) {
    case 'Active': return 'bg-gradient-to-r from-green-50 to-emerald-50 text-green-700 border border-green-200'
    case 'Inactive': return 'bg-gradient-to-r from-gray-50 to-slate-50 text-gray-600 border border-gray-300'
    case 'Expired': return 'bg-gradient-to-r from-red-50 to-orange-50 text-red-700 border border-red-200'
    default: return 'bg-gray-100 text-gray-800'
  }
}

const getMembershipColor = (membership: string) => {
  const membershipLower = membership.toLowerCase()
  switch (membershipLower) {
    case 'vip': return 'bg-gradient-to-r from-purple-100 to-pink-100 text-purple-800 border border-purple-300'
    case 'standard': return 'bg-gradient-to-r from-blue-100 to-cyan-100 text-blue-800 border border-blue-300'
    case 'basic': return 'bg-gradient-to-r from-gray-100 to-slate-100 text-gray-700 border border-gray-300'
    default: return 'bg-emerald-100 text-emerald-800'
  }
}

// --- Actions ---
const deleteMember = async (membership: Membership) => {
  if (!confirm(`Bạn có chắc chắn muốn xóa học viên "${membership.member.fullName}"?`)) {
    return
  }

  try {
    // API call to delete member - adjust endpoint as needed
    await api.delete(`/members/${membership.member.id}`)

    // Remove from local state
    const index = memberships.value.findIndex(m => m.id === membership.id)
    if (index > -1) {
      memberships.value.splice(index, 1)
    }

    useToast().success('Xóa học viên thành công')
  } catch (err) {
    console.error('Error deleting member:', err)
    useToast().error('Không thể xóa học viên. Vui lòng thử lại.')
  }
}
</script>

<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <!-- Header -->
    <div class="flex justify-between items-center mb-6">
      <div class="flex items-center gap-3">
        <button
          @click="$router.back()"
          class="p-2 bg-white border border-gray-200 rounded-lg hover:bg-gray-100 text-gray-600 transition shadow-sm"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m15 18-6-6 6-6"/></svg>
        </button>
        <div>
          <h1 class="text-xl font-bold text-gray-800">Quản Lý Khách Hàng</h1>
          <p class="text-xs text-gray-500">Quản lý thông tin và gói tập của khách hàng</p>
        </div>
      </div>

      <div class="flex gap-2">
        <span class="px-3 py-1 bg-emerald-50 text-emerald-700 rounded-full text-sm font-semibold">
          {{ filteredMemberships.length }} khách hàng
        </span>
      </div>
    </div>

    <!-- Filters -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6 mb-6">
      <div class="flex gap-4">
        <div class="relative flex-1">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/></svg>
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Tìm kiếm theo tên, email, số điện thoại..."
            class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          />
        </div>

        <div class="relative">
          <select
            v-model="statusFilter"
            class="px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 appearance-none bg-white pr-10"
          >
            <option value="">Tất cả trạng thái</option>
            <option value="Active">Đang hoạt động</option>
            <option value="Inactive">Dừng hoạt động</option>
            <option value="Expired">Hết hạn</option>
          </select>
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 pointer-events-none"><path d="m6 9 6 6 6-6"/></svg>
        </div>
      </div>
    </div>

    <!-- Table -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
      <div v-if="isLoading" class="animate-pulse">
        <div class="h-16 bg-gray-200 mb-4"></div>
        <div class="space-y-3 px-6 pb-6">
          <div v-for="i in 5" :key="i" class="h-12 bg-gray-200 rounded"></div>
        </div>
      </div>

      <div v-else class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Khách Hàng</th>
              <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Liên Hệ</th>
              <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Gói Tập</th>
              <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Trạng Thái</th>
              <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Thời Gian</th>
              <th class="px-6 py-4 text-right text-xs font-semibold text-gray-500 uppercase tracking-wider">Thao Tác</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
            <tr v-for="membership in filteredMemberships" :key="membership.id" class="hover:bg-gray-50 transition">
              <td class="px-6 py-4">
                <div class="flex items-center">
                  <div class="w-10 h-10 rounded-full bg-emerald-100 text-emerald-600 flex items-center justify-center font-bold text-sm mr-3">
                    {{ membership.member.fullName.charAt(0).toUpperCase() }}
                  </div>
                  <div>
                    <div class="text-sm font-semibold text-gray-900">{{ membership.member.fullName }}</div>
                    <div class="text-xs text-gray-500">{{ membership.member.email }}</div>
                  </div>
                </div>
              </td>

              <td class="px-6 py-4">
                <div class="text-sm text-gray-900">{{ membership.member.phone }}</div>
                <div class="text-xs text-gray-500">{{ membership.member.gender }}</div>
              </td>

              <td class="px-6 py-4">
                <span :class="['inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium', getMembershipColor(membership.member.membership)]">
                  {{ membership.member.membership }}
                </span>
              </td>

              <td class="px-6 py-4">
                <span :class="['inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium', getStatusColor(membership.member.status)]">
                  {{ membership.member.status === 'Active' ? 'Đang hoạt động' : membership.member.status === 'Inactive' ? 'Dừng hoạt động' : 'Hết hạn' }}
                </span>
              </td>

              <td class="px-6 py-4 text-sm text-gray-600">
                <div class="text-xs">
                  <div>Bắt đầu: {{ formatDate(membership.startDate) }}</div>
                  <div>Kết thúc: {{ formatDate(membership.endDate) }}</div>
                </div>
              </td>

              <td class="px-6 py-4 text-right">
                <div class="flex items-center justify-end gap-2">
                  <RouterLink
                    :to="`customer/${membership.member.id}/edit`"
                    class="p-1.5 text-blue-600 hover:bg-blue-50 rounded-md transition-colors"
                    title="Chỉnh sửa"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="m18.5 2.5 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                  </RouterLink>

                  <RouterLink
                    :to="{ name: 'manager.customer-detail', params: { id: membership.member.id } }"
                    class="p-1.5 text-emerald-600 hover:bg-emerald-50 rounded-md transition-colors"
                    title="Xem chi tiết"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                  </RouterLink>

                  <button
                    @click="deleteMember(membership)"
                    class="p-1.5 text-red-600 hover:bg-red-50 rounded-md transition-colors"
                    title="Xóa"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 6h18"/><path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"/><path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"/><line x1="10" x2="10" y1="11" y2="17"/><line x1="14" x2="14" y1="11" y2="17"/></svg>
                  </button>
                </div>
              </td>
            </tr>

            <tr v-if="filteredMemberships.length === 0">
              <td colspan="7" class="px-6 py-12 text-center text-gray-500">
                <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round" class="mx-auto mb-4 text-gray-300"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M22 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                <p>Không tìm thấy khách hàng nào phù hợp</p>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>