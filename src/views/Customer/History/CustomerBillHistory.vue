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
<div class="p-6 bg-white rounded-lg shadow-md max-w-7xl mx-auto">
    <h2 class="text-3xl font-bold mb-6 border-b pb-2">🧾 Lịch sử giao dịch</h2>

    <div v-if="isLoading" class="text-center py-10 text-blue-500">
        Đang tải dữ liệu...
    </div>

    <div v-else-if="error" class="text-center py-10 text-red-600">
        {{ error }}
    </div>

    <div v-else-if="bills.length === 0" class="text-center py-10 text-gray-500">
        Bạn chưa có hóa đơn nào
    </div>

    <div v-else class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
            <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Mã HĐ
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Ngày
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Nội dung
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Tổng tiền
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
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
                <td class="px-6 py-4 text-sm font-semibold text-gray-900 whitespace-nowrap">
                    #{{ bill.id }}
                </td>

                <td class="px-6 py-4 text-sm text-gray-700 whitespace-nowrap">
                    {{ moment(bill.date).format('DD/MM/YYYY HH:mm') }}
                </td>

                <td class="px-6 py-4 text-sm text-gray-700">
                    {{ getBillSummary(bill) }}
                </td>

                <td class="px-6 py-4 text-sm font-semibold text-red-600 whitespace-nowrap">
                    {{ bill.total.toLocaleString('vi-VN') }} đ
                </td>

                <td class="px-6 py-4 text-sm text-gray-700 whitespace-nowrap">
                    {{ bill.paymentMethod }}
                </td>

                <td class="px-6 py-4 text-sm whitespace-nowrap">
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
<div
    v-if="showDetailModal"
    class="fixed inset-0 bg-black/40 flex items-center justify-center z-50"
>
    <div class="bg-white rounded-lg w-full max-w-2xl p-6">
        <h3 class="text-xl font-bold mb-4">
            Chi tiết hóa đơn #{{ selectedBill?.id }}
        </h3>

        <p class="mb-2">
            <strong>Ngày:</strong>
            {{ moment(selectedBill?.date).format('DD/MM/YYYY HH:mm') }}
        </p>

        <p class="mb-2">
            <strong>Thu ngân:</strong>
            {{ selectedBill?.receptionist?.fullName || '—' }}
        </p>

        <div class="mt-4">
            <h4 class="font-semibold mb-2">Sản phẩm / Dịch vụ</h4>
            <ul class="list-disc pl-5 text-sm text-gray-700">
                <li v-for="item in selectedBill?.listSoldProduct" :key="item.id">
                    {{ item.quantity }} × {{ item.product.name }}
                    ({{ item.soldPrice.toLocaleString('vi-VN') }} đ)
                </li>
            </ul>
        </div>

        <div class="text-right mt-4 text-lg font-bold text-red-600">
            Tổng: {{ selectedBill?.total.toLocaleString('vi-VN') }} đ
        </div>

        <div class="text-right mt-6">
            <button
                class="px-4 py-2 bg-gray-200 rounded hover:bg-gray-300"
                @click="closeDetail"
            >
                Đóng
            </button>
        </div>
    </div>
</div>
</template>
