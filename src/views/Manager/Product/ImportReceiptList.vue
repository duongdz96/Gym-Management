<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex flex-col md:flex-row justify-between items-center gap-4 bg-white p-6 rounded-xl shadow-sm border border-gray-100">
      <div class="shrink-0">
        <h1 class="text-2xl font-bold text-gray-800">Lịch sử nhập kho</h1>
        <p class="text-gray-500 mt-1">Danh sách các phiếu nhập hàng đã lưu</p>
      </div>

      <div class="flex flex-1 justify-center items-center gap-3 flex-wrap px-4">
        <!-- Filters -->
        <select v-model="filterProvider" class="px-3 py-2 border border-gray-300 rounded-lg outline-none text-sm bg-white focus:border-blue-500 w-full md:w-auto">
          <option value="">Tất cả nhà cung cấp</option>
          <option v-for="p in uniqueProviders" :key="p" :value="p">{{ p }}</option>
        </select>

        <select v-model="sortBy" class="px-3 py-2 border border-gray-300 rounded-lg outline-none text-sm bg-white focus:border-blue-500">
          <option value="date">Ngày tạo</option>
          <option value="price">Tổng tiền</option>
        </select>

         <select v-model="sortOrder" class="px-3 py-2 border border-gray-300 rounded-lg outline-none text-sm bg-white focus:border-blue-500">
          <option value="desc">Giảm dần</option>
          <option value="asc">Tăng dần</option>
        </select>
      </div>

      <div class="shrink-0">
        <RouterLink to="/manager/product/import" class="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors shadow-sm font-medium whitespace-nowrap">
          <PlusIcon class="w-4 h-4" />
          Tạo phiếu nhập mới
        </RouterLink>
      </div>
    </div>

    <!-- Receipts Table -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full text-left text-sm">
          <thead class="bg-gray-50 text-gray-600 font-medium border-b border-gray-200">
            <tr>
              <th class="px-6 py-4">Mã phiếu</th>
              <th class="px-6 py-4">Nhà cung cấp</th>
              <th class="px-6 py-4">Ngày tạo</th>
              <th class="px-6 py-4 text-center">Số lượng SP</th>
              <th class="px-6 py-4 text-right">Tổng tiền</th>
              <th class="px-6 py-4 text-center">Thao tác</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-if="filteredAndSortedReceipts.length === 0">
              <td colspan="6" class="px-6 py-12 text-center text-gray-400">
                Chưa có phiếu nhập nào.
              </td>
            </tr>
            <tr v-for="receipt in filteredAndSortedReceipts" :key="receipt.id" class="hover:bg-gray-50 transition-colors">
              <td class="px-6 py-4 font-medium text-gray-900">{{ receipt.id }}</td>
              <td class="px-6 py-4 text-gray-900">{{ receipt.providerName }}</td>
              <td class="px-6 py-4 text-gray-600">{{ new Date(receipt.date).toLocaleDateString('vi-VN') }}</td>
              <td class="px-6 py-4 text-center text-gray-600">{{ calculateTotalQuantity(receipt) }}</td>
              <td class="px-6 py-4 text-right font-medium text-blue-600">{{ formatCurrency(receipt.price) }}</td>
              <td class="px-6 py-4 text-center">
                <RouterLink :to="`/manager/product/history/${receipt.id}`" class="p-1.5 text-gray-500 hover:text-blue-600 hover:bg-blue-50 rounded-md transition-colors inline-block" title="Xem chi tiết">
                  <EyeIcon class="w-4 h-4" />
                </RouterLink>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, computed } from 'vue';
import { useInventoryStore } from '@/stores/useInventoryStore';
import { PlusIcon, EyeIcon } from 'lucide-vue-next';
import { RouterLink } from 'vue-router';

const inventoryStore = useInventoryStore();

// Filter States
const filterProvider = ref("");
const sortBy = ref("date"); // 'date', 'price'
const sortOrder = ref("desc"); // 'asc', 'desc'

const uniqueProviders = computed(() => {
  const providers = new Set(inventoryStore.importReceipts.map(r => r.providerName));
  return Array.from(providers).filter(Boolean);
});

const filteredAndSortedReceipts = computed(() => {
  let result = [...inventoryStore.importReceipts];

  // 1. Filter by Provider
  if (filterProvider.value) {
    result = result.filter(r => r.providerName === filterProvider.value);
  }

  // 2. Sorting
  result.sort((a, b) => {
    let valA, valB;
    if (sortBy.value === 'date') {
      valA = new Date(a.date).getTime();
      valB = new Date(b.date).getTime();
    } else if (sortBy.value === 'price') {
      valA = a.price || 0;
      valB = b.price || 0;
    }

    if (sortOrder.value === 'asc') {
      return valA - valB;
    } else {
      return valB - valA;
    }
  });

  return result;
});

onMounted(() => {
  inventoryStore.fetchImportReceipts();
});

const calculateTotalQuantity = (receipt) => {
  if (!receipt.importedProducts) return 0;
  return receipt.importedProducts.reduce((sum, item) => sum + (item.quantity || 0), 0);
};

const formatCurrency = (value) => {
  if (value === undefined || value === null || isNaN(value)) return '0 ₫';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};
</script>
