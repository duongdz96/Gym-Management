<script setup>
import { ref, onMounted, computed } from "vue"
import api from "@/services/api"
import { useAuthStore } from "@/stores/useAuthStore"
import { useToast } from "vue-toastification"

const authStore = useAuthStore()
const toast = useToast()

const currentMembership = ref(null)
const currentPTPackages = ref([])
const availablePackages = ref([])
const membershipHistory = ref([])
const loading = ref(true)

const formatDate = (dateString) => {
  if (!dateString) return "N/A"
  return new Date(dateString).toLocaleDateString('vi-VN')
}

const formatCurrency = (value) => {
  if (!value) return "0 VND"
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value)
}

const getMembershipStatus = (endDate) => {
  if (!endDate) return "Inactive"
  const now = new Date()
  const end = new Date(endDate)
  return end > now ? "Active" : "Expired"
}

// Fetch current membership
async function fetchCurrentMembership() {
  try {
    const memberId = authStore.user?.id
    if (!memberId) {
      console.error("No member ID found")
      return
    }

    // Get all memberships for this member
    const res = await api.get("/membership")
    const allMemberships = res.data || []
    
    // Filter memberships for current user and get the active one
    const userMemberships = allMemberships.filter(m => m.member?.id === memberId)
    
    if (userMemberships.length > 0) {
      // Sort by end date descending to get the most recent
      userMemberships.sort((a, b) => new Date(b.endDate) - new Date(a.endDate))
      
      // Get the active one or the most recent
      const activeMembership = userMemberships.find(m => m.status === "Active") || userMemberships[0]
      
      currentMembership.value = {
        name: activeMembership.membershipPlan?.name || "N/A",
        price: activeMembership.membershipPlan?.price || 0,
        startDate: activeMembership.startDate,
        expiryDate: activeMembership.endDate,
        status: activeMembership.status || getMembershipStatus(activeMembership.endDate),
        benefits: activeMembership.membershipPlan?.benefits || ""
      }
      
      // Set history (all except current)
      membershipHistory.value = userMemberships.map(m => ({
        id: m.id,
        name: m.membershipPlan?.name || "N/A",
        start: m.startDate,
        end: m.endDate,
        price: m.membershipPlan?.price || 0
      }))
    }
  } catch (err) {
    console.error("Error fetching current membership:", err)
    toast.error("Không thể tải thông tin gói hội viên!")
  }
}

// Fetch PT packages
async function fetchPTPackages() {
  try {
    const memberId = authStore.user?.id
    if (!memberId) {
      console.error("No member ID found")
      return
    }

    // Get PT package issues for this member
    const res = await api.get("/packageissued")
    const allPackages = res.data || []
    
    // Filter packages for current user
    const userPackages = allPackages.filter(pkg => 
      pkg.member?.id === memberId && 
      pkg.ptPackage?.status === "Active"
    )
    
    currentPTPackages.value = userPackages.map(pkg => ({
      id: pkg.id,
      ptName: pkg.pt?.fullName || "Chưa có PT",
      packageName: pkg.ptPackage?.name || "N/A",
      sessionsTotal: pkg.ptPackage?.sessions || 0,
      sessionsRemaining: pkg.remainingSessions || 0,
      sessionsUsed: (pkg.ptPackage?.sessions || 0) - (pkg.remainingSessions || 0)
    }))
  } catch (err) {
    console.error("Error fetching PT packages:", err)
    toast.error("Không thể tải thông tin gói PT!")
  }
}

// Fetch available packages
async function fetchAvailablePackages() {
  try {
    const res = await api.get("/membershipplan")
    availablePackages.value = (res.data || []).map(plan => ({
      id: plan.id,
      name: plan.name,
      price: plan.price,
      duration: plan.duration,
      description: plan.benefits || "Gói thành viên chất lượng",
      status: plan.status
    })).filter(p => p.status === "Active")
  } catch (err) {
    console.error("Error fetching available packages:", err)
    toast.error("Không thể tải danh sách gói!")
  }
}

onMounted(async () => {
  loading.value = true
  await Promise.all([
    fetchCurrentMembership(),
    fetchPTPackages(),
    fetchAvailablePackages()
  ])
  loading.value = false
})
</script>

<template>
  <div class="p-6 space-y-8">
    <h1 class="text-2xl font-bold text-stone-800">Gói hội viên</h1>

    <!-- Loading State -->
    <div v-if="loading" class="text-center py-12">
      <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-gray-900"></div>
      <p class="mt-4 text-gray-600">Đang tải dữ liệu...</p>
    </div>

    <template v-else>
      <!-- Gói hiện tại -->
      <section class="bg-white rounded-xl shadow p-5">
        <h2 class="text-xl font-semibold mb-3">Gói hiện tại</h2>
        <div v-if="currentMembership" class="grid md:grid-cols-2 gap-4 text-gray-700">
          <div>
            <p><strong>Tên gói:</strong> {{ currentMembership.name }}</p>
            <p><strong>Giá:</strong> {{ formatCurrency(currentMembership.price) }}</p>
          </div>
          <div>
            <p><strong>Ngày bắt đầu:</strong> {{ formatDate(currentMembership.startDate) }}</p>
            <p><strong>Hết hạn:</strong> {{ formatDate(currentMembership.expiryDate) }}</p>
          </div>
        </div>
        <div v-else class="text-gray-500 text-center py-4">
          Bạn chưa có gói hội viên nào
        </div>
        <p v-if="currentMembership" class="mt-2">
          <strong>Trạng thái:</strong>
          <span
            :class="currentMembership.status === 'Active' ? 'text-emerald-600 font-medium' : 'text-gray-500 font-medium'"
            >{{ currentMembership.status }}</span
          >
        </p>
      </section>

      <!-- Gói PT hiện tại -->
      <section class="bg-white rounded-xl shadow p-5">
        <h2 class="text-xl font-semibold mb-3">Gói PT hiện tại</h2>
        <div v-if="currentPTPackages.length > 0" class="space-y-4">
          <div 
            v-for="ptPackage in currentPTPackages" 
            :key="ptPackage.id"
            class="border border-emerald-200 rounded-lg p-4 bg-gradient-to-r from-emerald-50 to-teal-50"
          >
            <div class="flex items-start justify-between mb-3">
              <div>
                <h3 class="font-semibold text-lg text-gray-800">{{ ptPackage.packageName }}</h3>
                <p class="text-gray-600 mt-1">
                  <strong>Huấn luyện viên:</strong> {{ ptPackage.ptName }}
                </p>
              </div>
              <span class="px-3 py-1 rounded-full text-xs font-semibold bg-emerald-100 text-emerald-700">
                Đang sử dụng
              </span>
            </div>
            
            <div class="space-y-2">
              <div class="flex items-center justify-between text-sm">
                <span class="text-gray-600">Buổi tập đã sử dụng:</span>
                <span class="font-semibold text-emerald-600">
                  {{ ptPackage.sessionsUsed }}/{{ ptPackage.sessionsTotal }} buổi
                </span>
              </div>
              <div class="flex items-center justify-between text-sm">
                <span class="text-gray-600">Buổi còn lại:</span>
                <span class="font-bold text-lg" :class="ptPackage.sessionsRemaining > 5 ? 'text-emerald-600' : 'text-orange-600'">
                  {{ ptPackage.sessionsRemaining }} buổi
                </span>
              </div>
              <div class="mt-2 w-full bg-gray-200 rounded-full h-2.5">
                <div 
                  class="bg-gradient-to-r from-emerald-500 to-teal-500 h-2.5 rounded-full transition-all"
                  :style="{ width: `${(ptPackage.sessionsUsed / ptPackage.sessionsTotal) * 100}%` }"
                ></div>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="text-gray-500 text-center py-4">
          Bạn chưa đăng ký gói PT nào
        </div>
      </section>

      <!-- Danh sách gói khả dụng -->
      <section class="bg-white rounded-xl shadow p-5">
        <h2 class="text-xl font-semibold mb-3">Các gói khác</h2>
        <div v-if="availablePackages.length > 0" class="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
          <div
            v-for="pkg in availablePackages"
            :key="pkg.id"
            class="border rounded-lg p-4 hover:shadow transition"
          >
            <h3 class="font-semibold text-lg mb-1">{{ pkg.name }}</h3>
            <p class="text-gray-600 mb-1 text-sm">{{ pkg.description }}</p>
            <p class="text-sm text-gray-500 mb-1">Thời hạn: {{ pkg.duration }}</p>
            <p class="text-emerald-600 font-medium mb-3">{{ formatCurrency(pkg.price) }}</p>
            <button
              class="bg-emerald-600 text-white px-4 py-2 rounded-lg hover:bg-emerald-700 w-full"
            >
              Xem chi tiết
            </button>
          </div>
        </div>
        <div v-else class="text-gray-500 text-center py-4">
          Hiện không có gói nào khả dụng
        </div>
      </section>

      <!-- Lịch sử gói hội viên -->
      <section class="bg-white rounded-xl shadow p-5">
        <h2 class="text-xl font-semibold mb-3">Lịch sử mua gói</h2>
        <div v-if="membershipHistory.length > 0">
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
                <td class="py-2">{{ formatDate(item.start) }}</td>
                <td class="py-2">{{ formatDate(item.end) }}</td>
                <td class="py-2">{{ formatCurrency(item.price) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        <div v-else class="text-gray-500 text-center py-4">
          Chưa có lịch sử mua gói
        </div>
      </section>
    </template>
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
