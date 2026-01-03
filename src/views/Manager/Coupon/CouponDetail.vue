<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/services/api'
import { useToast } from 'vue-toastification'
import {
    ArrowLeftIcon,
    TagIcon,
    PercentIcon,
    DollarSignIcon,
    CalendarIcon,
    ActivityIcon,
    UsersIcon,
    CheckCircleIcon,
    XCircleIcon,
    EditIcon
} from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()
const toast = useToast()
const coupon = ref(null)
const issuedCoupons = ref([])
const loading = ref(false)

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  })
}

const fetchCouponDetail = async () => {
  loading.value = true
  try {
    const id = route.params.id;
    const res = await api.get(`/coupons/${id}`);
    coupon.value = res.data.coupon;
    issuedCoupons.value = res.data.issuedCoupons || [];
  } catch (error) {
    toast.error("Lỗi khi lấy thông tin chi tiết mã giảm giá");
  } finally {
    loading.value = false
  }
}

const updateIssuedCouponStatus = async (issuedCouponId, newStatus) => {
  try {
    await api.put(`/issued-coupons/${issuedCouponId}/status?status=${newStatus}`);
    toast.success("Thay đổi trạng thái thành công");
    await fetchCouponDetail(); // Refresh data
  } catch (error) {
    toast.error("Thay đổi trạng thái thất bại");
  }
}

onMounted(fetchCouponDetail)
</script>

<template>
  <div class="p-6 max-w-6xl mx-auto space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div class="flex items-center gap-3">
        <button @click="router.push({ name: 'coupon' })" class="p-2 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors">
          <ArrowLeftIcon class="w-5 h-5 text-gray-600 dark:text-gray-400"/>
        </button>
        <h1 class="text-2xl font-bold text-gray-800 dark:text-gray-100 flex items-center gap-2">
          <TagIcon class="w-7 h-7 text-blue-600"/>
          Chi Tiết Mã Giảm Giá
        </h1>
      </div>
      <button @click="router.push({ name: 'coupon.edit', params: { id: route.params.id } })" class="flex items-center gap-2 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg shadow-lg transition-all">
        <EditIcon class="w-4 h-4"/>
        Sửa Mã Giảm Giá
      </button>
    </div>

    <!-- Coupon Info Card -->
    <div v-if="coupon" class="bg-white dark:bg-gray-900 rounded-2xl shadow-xl border border-gray-200 dark:border-gray-800 overflow-hidden">
      <div class="bg-gradient-to-r from-blue-500 to-purple-600 p-6 text-white">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm opacity-90 mb-1">Mã Coupon</p>
            <h2 class="text-3xl font-bold tracking-wider">{{ coupon.code }}</h2>
          </div>
          <div class="text-right">
            <div class="flex items-center gap-2 justify-end mb-2">
              <PercentIcon v-if="coupon.discountType !== 'FIXED_AMOUNT'" class="w-6 h-6"/>
              <DollarSignIcon v-else class="w-6 h-6"/>
              <span class="text-3xl font-bold">{{ coupon.discountValue }}</span>
              <span v-if="coupon.discountType !== 'FIXED_AMOUNT'" class="text-2xl">%</span>
              <span v-else class="text-lg">VND</span>
            </div>
            <p class="text-sm opacity-90">{{ coupon.discountType === 'FIXED_AMOUNT' ? 'Số tiền cố định' : 'Phần trăm' }} Giảm Giá</p>
          </div>
        </div>
      </div>
      
      <div class="p-6 grid grid-cols-1 md:grid-cols-3 gap-6">
        <div class="flex items-start gap-3">
          <div class="p-2 rounded-lg bg-blue-100 dark:bg-blue-900/30">
            <CalendarIcon class="w-5 h-5 text-blue-600 dark:text-blue-400"/>
          </div>
          <div>
            <p class="text-sm text-gray-500 dark:text-gray-400">Thời Hạn Hiệu Lực</p>
            <p class="font-medium text-gray-900 dark:text-gray-100">{{ formatDate(coupon.startDate) }}</p>
            <p class="text-sm text-gray-600 dark:text-gray-300">đến {{ formatDate(coupon.endDate) }}</p>
          </div>
        </div>

        <div class="flex items-start gap-3">
          <div class="p-2 rounded-lg bg-green-100 dark:bg-green-900/30">
            <ActivityIcon class="w-5 h-5 text-green-600 dark:text-green-400"/>
          </div>
          <div>
            <p class="text-sm text-gray-500 dark:text-gray-400">Trạng Thái</p>
            <span :class="[
              'inline-block px-3 py-1 rounded-full text-sm font-medium border mt-1',
              coupon.status === 'ACTIVE' 
                ? 'bg-green-100 text-green-700 border-green-200 dark:bg-green-900/30 dark:text-green-400 dark:border-green-800' 
                : 'bg-gray-100 text-gray-700 border-gray-200 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-700'
            ]">
              {{ coupon.status === 'ACTIVE' ? 'Đang hoạt động' : 'Không hoạt động' }}
            </span>
          </div>
        </div>

        <div class="flex items-start gap-3">
          <div class="p-2 rounded-lg bg-purple-100 dark:bg-purple-900/30">
            <UsersIcon class="w-5 h-5 text-purple-600 dark:text-purple-400"/>
          </div>
          <div>
            <p class="text-sm text-gray-500 dark:text-gray-400">Phạm Vi</p>
            <p class="font-medium text-gray-900 dark:text-gray-100 capitalize">{{ coupon.scope || 'Người dùng cụ thể' }}</p>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="bg-white dark:bg-gray-900 rounded-2xl shadow-xl border border-gray-200 dark:border-gray-800 p-12 text-center">
      <div class="animate-pulse">
        <div class="h-8 bg-gray-200 dark:bg-gray-700 rounded w-1/3 mx-auto mb-4"></div>
        <div class="h-4 bg-gray-200 dark:bg-gray-700 rounded w-1/2 mx-auto"></div>
      </div>
    </div>

    <!-- Issued Members Table -->
    <div class="bg-white dark:bg-gray-900 rounded-2xl shadow-xl border border-gray-200 dark:border-gray-800 overflow-hidden">
      <div class="px-6 py-4 border-b border-gray-200 dark:border-gray-800">
        <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 flex items-center gap-2">
          <UsersIcon class="w-5 h-5 text-blue-600"/>
          Thành Viên Được Cấp ({{ issuedCoupons.length }})
        </h3>
      </div>

      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50 dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase">#</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase">Thành Viên</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase">Email</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase">Số Lần Còn Lại</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase">Trạng Thái</th>
              <th class="px-6 py-3 text-right text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase">Hành Động</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200 dark:divide-gray-800">
            <tr v-if="issuedCoupons.length === 0">
              <td colspan="6" class="px-6 py-12 text-center text-gray-500 dark:text-gray-400">
                <UsersIcon class="w-12 h-12 mx-auto mb-3 opacity-30"/>
                <p>Chưa có thành viên nào được cấp mã này.</p>
              </td>
            </tr>
            <tr
              v-for="(ic, index) in issuedCoupons"
              :key="ic.id"
              class="hover:bg-gray-50 dark:hover:bg-gray-800/50 transition-colors"
            >
              <td class="px-6 py-4 text-sm text-gray-600 dark:text-gray-300">{{ index + 1 }}</td>
              <td class="px-6 py-4">
                <p class="font-medium text-gray-900 dark:text-gray-100">{{ ic.member?.fullName || 'N/A' }}</p>
              </td>
              <td class="px-6 py-4 text-sm text-gray-600 dark:text-gray-300">{{ ic.member?.email || 'N/A' }}</td>
              <td class="px-6 py-4">
                <span class="px-2 py-1 bg-blue-100 dark:bg-blue-900/30 text-blue-700 dark:text-blue-400 rounded-full text-xs font-medium">
                  {{ ic.remainingUses }} lần còn lại
                </span>
              </td>
              <td class="px-6 py-4">
                <span :class="[
                  'px-2.5 py-1 rounded-full text-xs font-medium border inline-flex items-center gap-1',
                  ic.status === 'AVAILABLE' 
                    ? 'bg-green-100 text-green-700 border-green-200 dark:bg-green-900/30 dark:text-green-400 dark:border-green-800' 
                    : 'bg-gray-100 text-gray-700 border-gray-200 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-700'
                ]">
                  <CheckCircleIcon v-if="ic.status === 'AVAILABLE'" class="w-3 h-3"/>
                  <XCircleIcon v-else class="w-3 h-3"/>
                  {{ ic.status }}
                </span>
              </td>
              <td class="px-6 py-4 text-right">
                <button
                  v-if="ic.status === 'AVAILABLE'"
                  @click="updateIssuedCouponStatus(ic.id, 'UNAVAILABLE')"
                  class="px-3 py-1.5 text-xs bg-red-100 hover:bg-red-200 text-red-700 rounded-lg transition-colors"
                >
                  Đổi trạng thái
                </button>
                <button
                  v-else
                  @click="updateIssuedCouponStatus(ic.id, 'AVAILABLE')"
                  class="px-3 py-1.5 text-xs bg-green-100 hover:bg-green-200 text-green-700 rounded-lg transition-colors"
                >
                  Đổi trạng thái
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
