<script setup lang="ts">
import { ref, onMounted } from 'vue';
import api from '@/services/api'; // Assuming your API instance is defined
import moment from 'moment'; // Install if needed: npm install moment

// Declare basic Interfaces (Types) for Bill data
// You should define these types in a separate file (e.g., types/bill.ts)
interface Product {
    name: string;
    type: string;
    price: number;
}

interface SoldProduct {
    id: number;
    quantity: number;
    product: Product;
}

interface StaffAssigned {
    id: number;
    trainingSession: number; // Number of training sessions
    staff: { fullName: string; position: string; }; // Assuming only PT's name is needed
}

interface Bill {
    id: number;
    paymentMethod: string;
    paymentStatus: string;
    date: string; // ISO 8601 string
    total: number;
    member: { fullName: string };
    listSoldProduct: SoldProduct[];
    listStaffAssigned: StaffAssigned[];
    issuedCoupon?: { coupon: { code: string } };
}

// --- STATE ---
const memberId = 1; // Current member ID (Replace with actual ID logic)
const bills = ref<Bill[]>([]);
const isLoading = ref(false);
const error = ref<string | null>(null);

// --- METHODS ---
const fetchBillHistory = async () => {
    isLoading.value = true;
    error.value = null;

    try {
        // API Call: GET /api/bills/member/{memberId}
        const response = await api.get(`/bills/member/${memberId}`);
        bills.value = response.data;
        
    } catch (err) {
        console.error("Error fetching bill history:", err);
        error.value = "Failed to load transaction history. Please try again.";
    } finally {
        isLoading.value = false;
    }
};

// Function to create a summary of the bill content for display
const getBillSummary = (bill: Bill): string => {
    const parts: string[] = [];
    let itemCounts = 0;

    // 1. Summarize Products Sold
    if (bill.listSoldProduct && bill.listSoldProduct.length > 0) {
        bill.listSoldProduct.forEach(item => {
            parts.push(`${item.quantity} x ${item.product.name}`);
            itemCounts++;
        });
    }

    // 2. Summarize PT/Staff Services Hired
    if (bill.listStaffAssigned && bill.listStaffAssigned.length > 0) {
        bill.listStaffAssigned.forEach(item => {
            parts.push(`${item.trainingSession} PT Sessions (${item.staff.fullName})`);
            itemCounts++;
        });
    }

    if (itemCounts === 0) {
        return "No product/service details.";
    }
    
    // If there is more than 1 item, display the first item + the count of others
    if (itemCounts > 1) {
        return `${parts[0]} and ${itemCounts - 1} other item(s).`;
    }
    
    return parts[0];
};

// --- LIFECYCLE HOOKS ---
onMounted(() => {
    fetchBillHistory();
});
</script>

<template>
    <div class="bill-history-container p-6 bg-white rounded-lg shadow-md">
        <h2 class="text-3xl font-bold mb-6 text-gray-800 border-b pb-2">🧾 Transaction History</h2>

        <div v-if="isLoading" class="text-center p-8">
            <p class="text-lg text-blue-500">Loading data...</p>
        </div>

        <div v-else-if="error" class="text-center p-8 bg-red-100 border border-red-400 text-red-700 rounded">
            <p>{{ error }}</p>
        </div>

        <div v-else-if="bills.length === 0" class="text-center p-8 bg-yellow-100 border border-yellow-400 text-yellow-700 rounded">
            <p class="text-lg">You have no bills yet.</p>
        </div>

        <div v-else class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                    <tr>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Bill ID</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Transaction Date</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Summary</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Total Amount</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Payment Method</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Coupon Code</th>
                    </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                    <tr v-for="bill in bills" :key="bill.id">
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">#{{ bill.id }}</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                            {{ moment(bill.date).format('MM/DD/YYYY HH:mm') }}
                        </td>
                        <td class="px-6 py-4 text-sm text-gray-700">
                            {{ getBillSummary(bill) }}
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-red-600 font-semibold">
                            {{ bill.total.toLocaleString('en-US', { style: 'currency', currency: 'VND' }) }}
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ bill.paymentMethod }}</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm">
                            <span :class="{
                                'bg-green-100 text-green-800': bill.paymentStatus === 'PAID',
                                'bg-yellow-100 text-yellow-800': bill.paymentStatus === 'PENDING',
                                'bg-red-100 text-red-800': bill.paymentStatus === 'CANCELLED'
                            }" class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full">
                                {{ bill.paymentStatus }}
                            </span>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-blue-600">
                            {{ bill.issuedCoupon?.coupon.code || 'N/A' }}
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</template>

<style scoped>
/* Add custom CSS if needed */
.bill-history-container {
    max-width: 1200px;
    margin: 0 auto;
}
</style>