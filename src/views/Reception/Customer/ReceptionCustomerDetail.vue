<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import api from '@/services/api'

// --- Interfaces ---
type MemberDetail = {
  id: number
  email: string
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
  // Optional fields from StudentProfile
  height?: number
  weight?: number
  trainingPlan?: string
  pt?: {
    id: number
    email: string
    fullName: string
    phone: string
  }
}

// --- State ---
const route = useRoute()
const customerId = route.params.id as string
const profile = ref<MemberDetail | null>(null)
const checkinHistory = ref<Array<{date: string, time: string, status: string}>>([])
const memberPackages = ref<Array<any>>([])
const isLoading = ref(true)

// --- Filters ---
const dateFilter = ref('')
const statusFilter = ref('')

// --- Lifecycle ---
onMounted(async () => {
  try {
    isLoading.value = true
    
    // 1. Get basic member info from /membership (always available)
    const membershipRes = await api.get('/membership')
    const memberships = membershipRes.data || []
    const membershipData = memberships.find((m: any) => m.member.id === parseInt(customerId))
    
    if (!membershipData) {
      console.error('Member not found')
      isLoading.value = false
      return
    }
    
    // Initialize profile with membership data
    profile.value = {
      ...membershipData.member
    }
    
    // 2. Try to get additional info from /studentprofile (may not exist)
    try {
      const studentProfileRes = await api.get('/studentprofile')
      const profiles = studentProfileRes.data || []
      const studentProfile = profiles.find((p: any) => p.member.id === parseInt(customerId))
      
      if (studentProfile) {
        // Merge student profile data
        profile.value = {
          ...profile.value,
          height: studentProfile.height,
          weight: studentProfile.weight,
          trainingPlan: studentProfile.trainingPlan,
          pt: studentProfile.pt
        }
      }
    } catch (studentProfileErr) {
      console.warn('No student profile found for this member', studentProfileErr)
    }

    // 3. Fetch check-in history
    try {
      const accessLogRes = await api.get(`/accesslog/${customerId}`)
      const accessLogs = accessLogRes.data || []
      
      checkinHistory.value = accessLogs.map((log: any) => {
        const accessTime = new Date(log.accessTime)
        const year = accessTime.getFullYear()
        const month = (accessTime.getMonth() + 1).toString().padStart(2, '0')
        const day = accessTime.getDate().toString().padStart(2, '0')
        const hours = accessTime.getHours().toString().padStart(2, '0')
        const minutes = accessTime.getMinutes().toString().padStart(2, '0')
        const seconds = accessTime.getSeconds().toString().padStart(2, '0')
        return {
          date: `${year}-${month}-${day}`,
          time: `${hours}:${minutes}:${seconds}`,
          status: 'Thành công'
        }
      }).sort((a: any, b: any) => new Date(b.date + ' ' + b.time).getTime() - new Date(a.date + ' ' + a.time).getTime())
    } catch (accessLogErr) {
      console.warn('No access log found for this member', accessLogErr)
    }

    // 4. Fetch PT packages
    try {
      const packagesRes = await api.get('/packageissued')
      const allPackages = packagesRes.data || []
      
      memberPackages.value = allPackages
        .filter((pkg: any) => pkg.member?.id === parseInt(customerId))
        .map((pkg: any) => {
          const totalSessions = pkg.ptPackage?.sessions || 0
          const remainingSessions = pkg.remainingSessions || 0
          const usedSessions = totalSessions - remainingSessions
          
          return {
            id: pkg.id,
            name: pkg.ptPackage?.name || 'Gói PT',
            totalSessions: totalSessions,
            usedSessions: usedSessions,
            remainingSessions: remainingSessions,
            status: remainingSessions > 0 ? 'active' : 'expired',
            ptName: pkg.pt?.fullName || 'N/A'
          }
        })
    } catch (packagesErr) {
      console.warn('No PT packages found for this member', packagesErr)
    }
    
  } catch (err) {
    console.error('Error fetching member info:', err)
  } finally {
    isLoading.value = false
  }
})

// --- Computed ---
const filteredCheckinHistory = computed(() => {
  return checkinHistory.value.filter(item => {
    const matchesDate = !dateFilter.value || item.date === dateFilter.value
    const matchesStatus = !statusFilter.value || item.status === statusFilter.value
    return matchesDate && matchesStatus
  })
})

// --- Helpers ---
const formatDate = (dateString: string) => {
  if (!dateString) return 'N/A'
  try {
    const date = new Date(dateString);
    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const year = date.getFullYear();
    return `${day}/${month}/${year}`;
  } catch { return dateString }
}

const getInitials = (name: string) => {
  return name ? name.split(' ').map(n => n[0]).join('').slice(-2).toUpperCase() : 'KH';
}

const getStatusColor = (status: string) => {
  switch (status) {
    case 'Thành công': return 'text-green-600 bg-green-50'
    case 'Thất bại': return 'text-red-600 bg-red-50'
    default: return 'text-gray-600 bg-gray-50'
  }
}
</script>

<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <div class="flex items-center justify-between mb-6">
      <div class="flex items-center gap-3">
        <button
          @click="$router.back()"
          class="p-2 bg-white border border-gray-200 rounded-lg hover:bg-gray-100 text-gray-600 transition shadow-sm"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m15 18-6-6 6-6"/></svg>
        </button>
        <div>
          <h1 class="text-xl font-bold text-gray-800">Chi tiết Khách Hàng</h1>
          <p class="text-xs text-gray-500">Quản lý hồ sơ và lịch sử tập luyện</p>
        </div>
      </div>

      <div v-if="profile" class="flex gap-2">
         <span
            :class="[
              'px-3 py-1 rounded-full text-xs font-semibold border',
              profile.status === 'Active'
                ? 'bg-green-50 text-green-700 border-green-200'
                : 'bg-red-50 text-red-700 border-red-200'
            ]"
          >
            {{ profile.status === 'Active' ? 'Đang hoạt động' : 'Dừng hoạt động' }}
          </span>
      </div>
    </div>

    <div v-if="isLoading" class="animate-pulse grid grid-cols-1 lg:grid-cols-3 gap-6">
       <div class="bg-gray-200 h-96 rounded-xl"></div>
       <div class="bg-gray-200 h-96 rounded-xl lg:col-span-2"></div>
    </div>

    <div v-else-if="profile" class="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">

      <div class="lg:col-span-4 space-y-6">
        <div class="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
          <div class="bg-gradient-to-r from-blue-600 to-indigo-700 h-24"></div>
          <div class="px-6 pb-6">
            <div class="relative flex justify-between items-end -mt-10 mb-4">
              <div class="w-20 h-20 rounded-full border-4 border-white bg-gray-200 flex items-center justify-center text-xl font-bold text-gray-500 shadow-md">
                {{ getInitials(profile.fullName) }}
              </div>
            </div>

            <div class="mb-6">
              <h2 class="text-xl font-bold text-gray-900">{{ profile.fullName }}</h2>
              <p class="text-sm text-gray-500">{{ profile.email }}</p>
            </div>

            <div class="space-y-3 pt-4 border-t border-gray-100">
               <div class="flex justify-between items-center text-sm">
                  <span class="text-gray-500">Số điện thoại</span>
                  <span class="font-medium text-gray-900">{{ profile.phone }}</span>
               </div>
               <div class="flex justify-between items-center text-sm">
                  <span class="text-gray-500">Ngày sinh</span>
                  <span class="font-medium text-gray-900">{{ formatDate(profile.dob) }}</span>
               </div>
               <div class="flex justify-between items-center text-sm">
                  <span class="text-gray-500">Giới tính</span>
                  <span class="font-medium text-gray-900">{{ profile.gender }}</span>
               </div>
               <div class="flex justify-between items-center text-sm">
                  <span class="text-gray-500">Gói tập</span>
                  <span class="inline-block px-2 py-0.5 bg-indigo-50 text-indigo-700 rounded text-xs font-bold uppercase">
                    {{ profile.membership }}
                  </span>
               </div>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
           <h3 class="text-sm font-bold text-gray-900 uppercase mb-4 flex items-center gap-2">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-indigo-600"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M22 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
              PT Phụ trách
           </h3>
           <div v-if="profile.pt" class="flex items-center gap-3 bg-gray-50 p-3 rounded-lg border border-gray-100">
              <div class="w-10 h-10 rounded-full bg-indigo-100 text-indigo-600 flex items-center justify-center font-bold text-xs">
                PT
              </div>
              <div>
                <p class="text-sm font-semibold text-gray-900">{{ profile.pt.fullName }}</p>
                <p class="text-xs text-gray-500">{{ profile.pt.phone }}</p>
              </div>
           </div>
           <div v-else class="text-center py-4 text-gray-500 text-sm bg-gray-50 rounded-lg border border-gray-100">
              Chưa có PT phụ trách
           </div>
        </div>
      </div>

      <div class="lg:col-span-8 space-y-6">

        <div class="grid grid-cols-2 gap-4">
           <div class="bg-white p-5 rounded-xl border border-gray-200 shadow-sm flex items-center justify-between">
              <div>
                 <p class="text-xs text-gray-500 uppercase font-semibold">Cân nặng</p>
                 <p class="text-2xl font-bold text-gray-800 mt-1">
                   <span v-if="profile.weight">{{ profile.weight }} <span class="text-sm font-normal text-gray-400">kg</span></span>
                   <span v-else class="text-base text-gray-400">Chưa cập nhật</span>
                 </p>
              </div>
              <div class="bg-blue-50 p-2 rounded-lg text-blue-600">
                 <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20.42 4.58a5.4 5.4 0 0 0-7.65 0l-.77.78-.77-.78a5.4 5.4 0 0 0-7.65 0C1.46 6.7 1.33 10.28 4 13l8 8 8-8c2.67-2.72 2.54-6.30.42-8.42z"/></svg>
              </div>
           </div>
           <div class="bg-white p-5 rounded-xl border border-gray-200 shadow-sm flex items-center justify-between">
              <div>
                 <p class="text-xs text-gray-500 uppercase font-semibold">Chiều cao</p>
                 <p class="text-2xl font-bold text-gray-800 mt-1">
                   <span v-if="profile.height">{{ profile.height }} <span class="text-sm font-normal text-gray-400">cm</span></span>
                   <span v-else class="text-base text-gray-400">Chưa cập nhật</span>
                 </p>
              </div>
              <div class="bg-teal-50 p-2 rounded-lg text-teal-600">
                 <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m21.55 16.55-1.11 1.11a4.13 4.13 0 0 1-5.83 0L12 15l-2.61 2.61a4.13 4.13 0 0 1-5.83-5.83l1.11-1.11"/><path d="m10.89 10.89 1.11 1.11"/></svg>
              </div>
           </div>
        </div>

        <div v-if="memberPackages.length > 0" class="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <h3 class="text-sm font-bold text-gray-900 uppercase mb-4 flex items-center gap-2">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-indigo-600"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
            Gói PT đã đăng ký
          </h3>
          <div class="space-y-3">
            <div v-for="pkg in memberPackages" :key="pkg.id" class="border rounded-lg p-4 bg-gray-50">
              <div class="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-2 mb-3">
                <div>
                  <p class="text-sm font-medium text-gray-900">{{ pkg.name }}</p>
                  <p class="text-xs text-gray-600">PT: {{ pkg.ptName }}</p>
                  <p class="text-xs text-gray-600">Còn lại: {{ pkg.remainingSessions }} / {{ pkg.totalSessions }} buổi</p>
                </div>
                <span :class="[
                  'inline-flex items-center px-2 py-1 rounded-full text-xs font-medium',
                  pkg.status === 'active' ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'
                ]">
                  {{ pkg.status === 'active' ? 'Đang hoạt động' : 'Hết hạn' }}
                </span>
              </div>
              <div class="bg-gray-200 rounded-full h-2">
                <div
                  class="bg-indigo-600 h-2 rounded-full transition-all"
                  :style="{ width: `${(pkg.usedSessions / pkg.totalSessions) * 100}%` }"
                ></div>
              </div>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-xl shadow-sm border border-gray-200 flex flex-col h-[500px]">
          <div class="px-6 py-4 border-b border-gray-100 bg-gray-50 rounded-t-xl">
             <div class="flex justify-between items-center mb-3">
                <h3 class="font-bold text-gray-800">Lịch sử Check-in</h3>
                <span class="text-xs text-gray-500 bg-white px-2 py-1 rounded border">{{ filteredCheckinHistory.length }} buổi</span>
             </div>
             <div class="flex gap-3">
                <div class="flex-1">
                   <input
                      v-model="dateFilter"
                      type="date"
                      class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                      placeholder="Chọn ngày"
                   />
                </div>
                <div class="flex-1">
                   <select
                      v-model="statusFilter"
                      class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                   >
                      <option value="">Tất cả trạng thái</option>
                      <option value="Thành công">Thành công</option>
                      <option value="Thất bại">Thất bại</option>
                   </select>
                </div>
             </div>
          </div>

          <div class="overflow-y-auto flex-1 p-0">
             <table class="w-full text-left border-collapse">
                <thead class="bg-gray-50 sticky top-0">
                   <tr>
                      <th class="px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wider">Ngày</th>
                      <th class="px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wider">Giờ</th>
                      <th class="px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wider text-right">Trạng thái</th>
                   </tr>
                </thead>
                <tbody class="divide-y divide-gray-100">
                   <tr v-for="(item, idx) in filteredCheckinHistory" :key="idx" class="hover:bg-gray-50 transition">
                      <td class="px-6 py-3 text-sm text-gray-700 font-medium">{{ formatDate(item.date) }}</td>
                      <td class="px-6 py-3 text-sm text-gray-600">{{ item.time }}</td>
                      <td class="px-6 py-3 text-right">
                         <span :class="['inline-flex items-center gap-1 text-xs font-medium px-2 py-0.5 rounded-full', getStatusColor(item.status)]">
                            <span :class="['w-1.5 h-1.5 rounded-full', item.status === 'Thành công' ? 'bg-green-500' : 'bg-red-500']"></span>
                            {{ item.status }}
                         </span>
                      </td>
                   </tr>
                   <tr v-if="filteredCheckinHistory.length === 0">
                      <td colspan="3" class="px-6 py-8 text-center text-gray-500 text-sm">
                         Không có dữ liệu check-in phù hợp
                      </td>
                   </tr>
                </tbody>
             </table>
          </div>
        </div>

      </div>
    </div>

    <div v-else class="text-center py-20">
      <p class="text-gray-500">Không tìm thấy thông tin khách hàng.</p>
    </div>
  </div>
</template>