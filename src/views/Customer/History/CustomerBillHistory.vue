<script setup lang="ts">
import { ref, onMounted } from 'vue';
import moment from 'moment';
import api from '@/services/api';
import { useAuthStore } from "@/stores/useAuthStore";
import { Eye } from 'lucide-vue-next';

/* ================= TYPES ================= */
interface Product {
    name: string;
    type: string;
    price: number;
}

interface SoldProduct {
    id: number;
    quantity: number;
    soldPrice: number;
    product: Product;
}

interface StaffAssigned {
    id: number;
    trainingSession: number;
    staff: {
        fullName: string;
        position: string;
    };
}

interface Bill {
    id: number;
    paymentMethod: string;
    paymentStatus: string;
    date: string;
    total: number;
    member: { fullName: string };
    receptionist?: { fullName: string };
    listSoldProduct: SoldProduct[];
    listStaffAssigned: StaffAssigned[];
    issuedCoupon?: { coupon: { code: string } };
}

/* ================= HELPERS ================= */
const paymentStatusLabel = (status: string): string => {
    switch (status) {
        case 'PAID':
            return 'Đã thanh toán';
        case 'PENDING':
            return 'Chờ thanh toán';
        case 'CANCELLED':
            return 'Đã hủy';
        default:
            return status;
    }
};

/* ================= STATE ================= */
const authStore = useAuthStore();
const memberId = authStore.user.id;

const bills = ref<Bill[]>([]);
const isLoading = ref(false);
const error = ref<string | null>(null);

const showDetailModal = ref(false);
const selectedBill = ref<Bill | null>(null);

/* ================= METHODS ================= */
const fetchBillHistory = async () => {
    isLoading.value = true;
    try {
        const res = await api.get(`/bills/member/${memberId}`);
        bills.value = res.data;
    } catch (e) {
        console.error(e);
        error.value = 'Không thể tải lịch sử giao dịch';
    } finally {
        isLoading.value = false;
    }
};

const getBillSummary = (bill: Bill): string => {
    const parts: string[] = [];

    bill.listSoldProduct?.forEach(item => {
        parts.push(`${item.quantity} × ${item.product.name}`);
    });

    bill.listStaffAssigned?.forEach(item => {
        parts.push(`${item.trainingSession} buổi PT (${item.staff.fullName})`);
    });

    if (parts.length === 0) return 'Không có chi tiết';

    if (parts.length > 1) {
        return `${parts[0]} và ${parts.length - 1} mục khác`;
    }

    return parts[0];
};

const openDetail = (bill: Bill) => {
    selectedBill.value = bill;
    showDetailModal.value = true;
};

const closeDetail = () => {
    showDetailModal.value = false;
    selectedBill.value = null;
};

/* ================= LIFECYCLE ================= */
onMounted(fetchBillHistory);
</script>

<template>
<div class="p-4 sm:p-6 bg-white rounded-lg shadow-md max-w-7xl mx-auto">
    <h2 class="text-2xl sm:text-3xl font-bold mb-4 sm:mb-6 border-b pb-2">🧾 Lịch sử giao dịch</h2>

    <div v-if="isLoading" class="text-center py-10 text-blue-500 text-sm sm:text-base">
        Đang tải dữ liệu...
    </div>

    <div v-else-if="error" class="text-center py-10 text-red-600">
        {{ error }}
    </div>

    <div v-else-if="bills.length === 0" class="text-center py-10 text-gray-500">
        Bạn chưa có hóa đơn nào
    </div>

    <div v-else class="overflow-x-auto -mx-4 sm:mx-0">
        <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
            <tr>
                <th class="px-3 sm:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Mã HĐ
                </th>
                <th class="px-3 sm:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Ngày
                </th>
                <th class="px-3 sm:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Nội dung
                </th>
                <th class="px-3 sm:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Tổng tiền
                </th>
                <th class="px-3 sm:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Thanh toán
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Trạng thái
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Coupon
                </th>
                <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase">
                    Chi tiết
                </th>
            </tr>
            </thead>

            <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="bill in bills" :key="bill.id">
                <td class="px-3 sm:px-6 py-3 sm:py-4 text-xs sm:text-sm font-semibold text-gray-900 whitespace-nowrap">
                    #{{ bill.id }}
                </td>

                <td class="px-3 sm:px-6 py-3 sm:py-4 text-xs sm:text-sm text-gray-700 whitespace-nowrap">
                    {{ moment(bill.date).format('DD/MM/YYYY HH:mm') }}
                </td>

                <td class="px-3 sm:px-6 py-3 sm:py-4 text-xs sm:text-sm text-gray-700">
                    {{ getBillSummary(bill) }}
                </td>

                <td class="px-3 sm:px-6 py-3 sm:py-4 text-xs sm:text-sm font-semibold text-red-600 whitespace-nowrap">
                    {{ bill.total.toLocaleString('vi-VN') }} đ
                </td>

                <td class="px-3 sm:px-6 py-3 sm:py-4 text-xs sm:text-sm text-gray-700 whitespace-nowrap">
                    {{ bill.paymentMethod }}
                </td>

                <td class="px-3 sm:px-6 py-3 sm:py-4 text-xs sm:text-sm whitespace-nowrap">
                    <span
                        class="px-2 py-1 text-xs font-semibold rounded-full"
                        :class="{
                            'bg-green-100 text-green-800': bill.paymentStatus === 'PAID',
                            'bg-yellow-100 text-yellow-800': bill.paymentStatus === 'PENDING',
                            'bg-red-100 text-red-800': bill.paymentStatus === 'CANCELLED'
                        }"
                    >
                        {{ paymentStatusLabel(bill.paymentStatus) }}
                    </span>
                </td>

                <td class="px-6 py-4 text-sm text-center whitespace-nowrap">
                    {{ bill.issuedCoupon?.coupon.code || '—' }}
                </td>

                <td class="px-6 py-4 text-sm text-center whitespace-nowrap">
                    <button
                        class="text-blue-600 hover:text-blue-800"
                        @click="openDetail(bill)"
                        title="Xem chi tiết"
                    >
                        <Eye class="w-5 h-5" />
                    </button>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
</div>

<!-- MODAL -->
<transition
  enter-active-class="transition duration-200 ease-out"
  enter-from-class="opacity-0 scale-95"
  enter-to-class="opacity-100 scale-100"
  leave-active-class="transition duration-150 ease-in"
  leave-from-class="opacity-100 scale-100"
  leave-to-class="opacity-0 scale-95"
>
  <div
    v-if="showDetailModal"
    class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-3 sm:p-4"
    @click.self="closeDetail"
  >
    <div class="bg-white rounded-xl sm:rounded-2xl w-full max-w-3xl shadow-2xl overflow-hidden max-h-[90vh] flex flex-col">
      <!-- Modal Header -->
      <div class="p-4 sm:p-6 bg-gradient-to-r from-green-600 to-green-700 text-white shrink-0">
        <div class="flex justify-between items-center">
          <div class="min-w-0">
            <h3 class="text-xl sm:text-2xl font-bold flex items-center gap-2">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-7 w-7" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
              Hóa đơn #{{ selectedBill?.id }}
            </h3>
            <p class="text-red-100 text-sm mt-1">Chi tiết giao dịch</p>
          </div>
          <button 
            @click="closeDetail"
            class="text-white/80 hover:text-white transition-colors bg-white/10 hover:bg-white/20 p-2 rounded-lg"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
      </div>
      
      <!-- Modal Body -->
      <div class="p-6 overflow-y-auto flex-1 space-y-6">
        <!-- Bill Info Grid -->
        <div class="grid grid-cols-2 gap-4">
          <div class="bg-blue-50 p-4 rounded-xl border-2 border-blue-200">
            <div class="flex items-center gap-2 text-blue-600 mb-2">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
              <span class="text-sm font-semibold">Ngày giao dịch</span>
            </div>
            <p class="text-lg font-bold text-gray-800">
              {{ moment(selectedBill?.date).format('DD/MM/YYYY HH:mm') }}
            </p>
          </div>

          <div class="bg-green-50 p-4 rounded-xl border-2 border-green-200">
            <div class="flex items-center gap-2 text-green-600 mb-2">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
              <span class="text-sm font-semibold">Thu ngân</span>
            </div>
            <p class="text-lg font-bold text-gray-800">
              {{ selectedBill?.receptionist?.fullName || 'Không có thông tin' }}
            </p>
          </div>

          <div class="bg-purple-50 p-4 rounded-xl border-2 border-purple-200">
            <div class="flex items-center gap-2 text-purple-600 mb-2">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" />
              </svg>
              <span class="text-sm font-semibold">Phương thức</span>
            </div>
            <p class="text-lg font-bold text-gray-800">
              {{ selectedBill?.paymentMethod }}
            </p>
          </div>

          <div class="bg-orange-50 p-4 rounded-xl border-2 border-orange-200">
            <div class="flex items-center gap-2 text-orange-600 mb-2">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <span class="text-sm font-semibold">Trạng thái</span>
            </div>
            <span
              class="inline-block px-3 py-1 text-sm font-bold rounded-full"
              :class="{
                'bg-green-500 text-white': selectedBill?.paymentStatus === 'PAID',
                'bg-yellow-500 text-white': selectedBill?.paymentStatus === 'PENDING',
                'bg-red-500 text-white': selectedBill?.paymentStatus === 'CANCELLED'
              }"
            >
              {{ paymentStatusLabel(selectedBill?.paymentStatus) }}
            </span>
          </div>
        </div>

        <!-- Coupon Info (if exists) -->
        <div v-if="selectedBill?.issuedCoupon" class="bg-gradient-to-r from-yellow-50 to-orange-50 p-4 rounded-xl border-2 border-yellow-300">
          <div class="flex items-center gap-2 text-orange-700 mb-2">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 5v2m0 4v2m0 4v2M5 5a2 2 0 00-2 2v3a2 2 0 110 4v3a2 2 0 002 2h14a2 2 0 002-2v-3a2 2 0 110-4V7a2 2 0 00-2-2H5z" />
            </svg>
            <span class="font-bold">Mã giảm giá đã sử dụng</span>
          </div>
          <p class="text-lg font-mono font-bold text-orange-800">
            {{ selectedBill?.issuedCoupon?.coupon.code }}
          </p>
        </div>

        <!-- Products/Services List -->
        <div class="bg-gray-50 p-5 rounded-xl border-2 border-gray-200">
          <h4 class="font-bold text-lg mb-4 flex items-center gap-2 text-gray-800">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-red-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
            </svg>
            Sản phẩm / Dịch vụ
          </h4>
          
          <div v-if="selectedBill?.listSoldProduct && selectedBill.listSoldProduct.length > 0" class="space-y-3">
            <div 
              v-for="item in selectedBill?.listSoldProduct" 
              :key="item.id"
              class="flex justify-between items-center p-3 bg-white rounded-lg border border-gray-200 hover:shadow-md transition-shadow"
            >
              <div class="flex-1">
                <p class="font-semibold text-gray-800">{{ item.product.name }}</p>
                <p class="text-sm text-gray-500">{{ item.product.type }}</p>
              </div>
              <div class="text-right">
                <p class="text-sm text-gray-600">{{ item.quantity }} × {{ item.soldPrice.toLocaleString('vi-VN') }} đ</p>
                <p class="font-bold text-red-600">{{ (item.quantity * item.soldPrice).toLocaleString('vi-VN') }} đ</p>
              </div>
            </div>
          </div>

          <div v-if="selectedBill?.listStaffAssigned && selectedBill.listStaffAssigned.length > 0" class="space-y-3 mt-3">
            <div 
              v-for="item in selectedBill?.listStaffAssigned" 
              :key="item.id"
              class="flex justify-between items-center p-3 bg-white rounded-lg border border-gray-200 hover:shadow-md transition-shadow"
            >
              <div class="flex-1">
                <p class="font-semibold text-gray-800">Buổi tập PT</p>
                <p class="text-sm text-gray-500">HLV: {{ item.staff.fullName }} ({{ item.staff.position }})</p>
              </div>
              <div class="text-right">
                <p class="font-bold text-red-600">{{ item.trainingSession }} buổi</p>
              </div>
            </div>
          </div>

          <div v-if="(!selectedBill?.listSoldProduct || selectedBill.listSoldProduct.length === 0) && (!selectedBill?.listStaffAssigned || selectedBill.listStaffAssigned.length === 0)" class="text-center py-8 text-gray-500">
            Không có sản phẩm hoặc dịch vụ
          </div>
        </div>

        <!-- Total Amount -->
        <div class="bg-gradient-to-r from-green-600 to-green-700 p-5 rounded-xl text-white">
          <div class="flex justify-between items-center">
            <span class="text-lg font-semibold">Tổng thanh toán</span>
            <span class="text-3xl font-bold">
              {{ selectedBill?.total.toLocaleString('vi-VN') }} đ
            </span>
          </div>
        </div>
      </div>
      
      <!-- Modal Footer -->
      <div class="p-4 border-t bg-gray-50 flex justify-end gap-3 shrink-0">
        <button
          class="px-6 py-2 bg-gray-200 text-gray-700 font-bold rounded-xl hover:bg-gray-300 transition-colors"
          @click="closeDetail"
        >
          Đóng
        </button>
        <button
          class="px-6 py-2 bg-gradient-to-r from-green-600 to-green-700 text-white font-bold rounded-xl hover:shadow-lg transition-all flex items-center gap-2"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z" />
          </svg>
          In hóa đơn
        </button>
      </div>
    </div>
  </div>
</transition>
</template>
