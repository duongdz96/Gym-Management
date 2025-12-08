<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex justify-between items-center bg-white p-6 rounded-xl shadow-sm border border-gray-100 print:hidden">
      <div>
        <h1 class="text-2xl font-bold text-gray-800">Chi tiết phiếu nhập</h1>
        <p class="text-gray-500 mt-1">Mã phiếu: <span class="font-medium text-gray-900">{{ receipt?.id }}</span></p>
      </div>
      <div class="flex gap-3">
        <button @click="printReceipt" class="flex items-center gap-2 px-4 py-2 bg-white border border-gray-300 rounded-lg shadow-sm hover:bg-gray-50 transition-colors text-gray-700 font-medium">
          <PrinterIcon class="w-4 h-4" />
          In phiếu
        </button>
        <button @click="downloadPDF" class="flex items-center gap-2 px-4 py-2 bg-white border border-gray-300 rounded-lg shadow-sm hover:bg-gray-50 transition-colors text-gray-700 font-medium">
          <DownloadIcon class="w-4 h-4" />
          Tải PDF
        </button>
        <RouterLink to="/manager/product/history" class="flex items-center gap-2 px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors font-medium">
          <ArrowLeftIcon class="w-4 h-4" />
          Quay lại
        </RouterLink>
      </div>
    </div>

    <div v-if="receipt" class="bg-white p-8 rounded-xl shadow-sm border border-gray-100 print:hidden">
      <!-- Info Section -->
      <div class="grid grid-cols-2 gap-8 mb-8">
        <div>
           <h3 class="font-bold text-gray-800 mb-2">Thông tin nhà cung cấp</h3>
            <div class="text-sm text-gray-600 space-y-1">
              <p><span class="font-medium">Tên:</span> {{ receipt.providerName }}</p>
              <!-- In a real app, you might fetch full supplier details to show address etc. here -->
            </div>
        </div>
        <div class="text-right">
          <h3 class="font-bold text-gray-800 mb-2">Thông tin phiếu</h3>
          <div class="text-sm text-gray-600 space-y-1">
             <p><span class="font-medium">Ngày tạo:</span> {{ new Date(receipt.date).toLocaleDateString('vi-VN') }}</p>
             <p><span class="font-medium">Người lập:</span> Admin</p>
          </div>
        </div>
      </div>

      <!-- Items Table -->
      <table class="w-full mb-8 text-sm border-collapse border border-gray-200">
        <thead>
          <tr class="bg-gray-50">
            <th class="border border-gray-200 px-4 py-2 text-left">STT</th>
            <th class="border border-gray-200 px-4 py-2 text-left">Sản phẩm</th>
            <th class="border border-gray-200 px-4 py-2 text-center">ĐVT</th>
            <th class="border border-gray-200 px-4 py-2 text-right">Số lượng</th>
            <th class="border border-gray-200 px-4 py-2 text-right">Đơn giá nhập</th>
            <th class="border border-gray-200 px-4 py-2 text-right">Thành tiền</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in receipt.importedProducts" :key="index">
            <td class="border border-gray-200 px-4 py-2 text-center">{{ index + 1 }}</td>
             <td class="border border-gray-200 px-4 py-2">
              <p class="font-medium">{{ item.productName }}</p>
              <p class="text-xs text-gray-500">{{ item.productBrand }}</p>
            </td>
            <td class="border border-gray-200 px-4 py-2 text-center">{{ item.productUnit }}</td>
            <td class="border border-gray-200 px-4 py-2 text-right">{{ item.quantity }}</td>
            <td class="border border-gray-200 px-4 py-2 text-right">{{ formatCurrency(item.importPrice) }}</td>
            <td class="border border-gray-200 px-4 py-2 text-right">{{ formatCurrency(item.importPrice * item.quantity) }}</td>
          </tr>
        </tbody>
        <tfoot>
           <tr class="font-bold bg-gray-50">
            <td colspan="5" class="border border-gray-200 px-4 py-2 text-right">Tổng cộng:</td>
            <td class="border border-gray-200 px-4 py-2 text-right text-blue-600">{{ formatCurrency(receipt.price) }}</td>
          </tr>
        </tfoot>
      </table>
    </div>
    
      <div v-else class="text-center py-12 text-gray-500">
        Không tìm thấy thông tin phiếu nhập.
      </div>

     <!-- Print Template (Hidden on Screen, Same as ImportGoods) -->
    <div id="print-area" class="hidden print:block p-8" style="background-color: white; color: black;" v-if="receipt">
      <div class="text-center mb-8">
        <h1 class="text-2xl font-bold uppercase mb-2" style="color: #1a202c;">Phiếu Nhập Hàng</h1>
        <p class="text-sm" style="color: #718096;">Ngày tạo: {{ new Date(receipt.date).toLocaleDateString('vi-VN') }}</p>
      </div>

      <div class="mb-8 grid grid-cols-2 gap-8">
        <div>
          <h3 class="font-bold mb-2" style="color: #2d3748;">Nhà cung cấp:</h3>
          <div class="text-sm space-y-1" style="color: #4a5568;">
            <p><span class="font-medium">Tên:</span> {{ receipt.providerName }}</p>
          </div>
        </div>
        <div class="text-right">
          <h3 class="font-bold mb-2" style="color: #2d3748;">Thông tin phiếu:</h3>
          <div class="text-sm space-y-1" style="color: #4a5568;">
            <p><span class="font-medium">Mã phiếu:</span> #{{ receipt.id }}</p>
            <p><span class="font-medium">Người lập:</span> Admin</p>
          </div>
        </div>
      </div>

      <table class="w-full mb-8 text-sm border-collapse border" style="border-color: #e2e8f0;">
        <thead>
          <tr style="background-color: #f7fafc;">
            <th class="border px-4 py-2 text-left" style="border-color: #e2e8f0;">STT</th>
            <th class="border px-4 py-2 text-left" style="border-color: #e2e8f0;">Sản phẩm</th>
            <th class="border px-4 py-2 text-center" style="border-color: #e2e8f0;">ĐVT</th>
            <th class="border px-4 py-2 text-right" style="border-color: #e2e8f0;">Số lượng</th>
            <th class="border px-4 py-2 text-right" style="border-color: #e2e8f0;">Đơn giá</th>
            <th class="border px-4 py-2 text-right" style="border-color: #e2e8f0;">Thành tiền</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in receipt.importedProducts" :key="index">
            <td class="border px-4 py-2 text-center" style="border-color: #e2e8f0;">{{ index + 1 }}</td>
            <td class="border px-4 py-2" style="border-color: #e2e8f0;">
              <p class="font-medium" style="color: #1a202c;">{{ item.productName }}</p>
              <p class="text-xs" style="color: #718096;">{{ item.productBrand }}</p>
            </td>
            <td class="border px-4 py-2 text-center" style="border-color: #e2e8f0;">{{ item.productUnit }}</td>
            <td class="border px-4 py-2 text-right" style="border-color: #e2e8f0;">{{ item.quantity }}</td>
            <td class="border px-4 py-2 text-right" style="border-color: #e2e8f0;">{{ formatCurrency(item.importPrice) }}</td>
            <td class="border px-4 py-2 text-right" style="border-color: #e2e8f0;">{{ formatCurrency(item.importPrice * item.quantity) }}</td>
          </tr>
        </tbody>
        <tfoot>
          <tr class="font-bold" style="background-color: #f7fafc;">
            <td colspan="5" class="border px-4 py-2 text-right" style="border-color: #e2e8f0;">Tổng cộng:</td>
            <td class="border px-4 py-2 text-right" style="border-color: #e2e8f0;">{{ formatCurrency(receipt.price) }}</td>
          </tr>
        </tfoot>
      </table>

      <!-- Signature Section -->
      <div class="grid grid-cols-3 gap-4 mt-12 text-center text-sm">
        <div>
          <p class="font-bold mb-12">Người lập phiếu</p>
          <p class="italic" style="color: #718096;">(Ký, họ tên)</p>
        </div>
        <div>
          <p class="font-bold mb-12">Người giao hàng</p>
          <p class="italic" style="color: #718096;">(Ký, họ tên)</p>
        </div>
        <div>
          <p class="font-bold mb-12">Quản lý</p>
          <p class="italic" style="color: #718096;">(Ký, họ tên)</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { useInventoryStore } from '@/stores/useInventoryStore';
import { PrinterIcon, DownloadIcon, ArrowLeftIcon } from 'lucide-vue-next';
import { useToast } from "vue-toastification";

const route = useRoute();
const inventoryStore = useInventoryStore();
const toast = useToast();

const receiptId = parseInt(route.params.id);
const receipt = computed(() => inventoryStore.importReceipts.find(r => r.id === receiptId));

const formatCurrency = (value) => {
  if (value === undefined || value === null || isNaN(value)) return '0 ₫';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

// Load html2pdf from CDN (duplicated logic, could be extracted to composable)
onMounted(async () => {
  if (inventoryStore.importReceipts.length === 0) {
      await inventoryStore.fetchImportReceipts();
  }

  if (!window.html2pdf) {
    const script = document.createElement('script');
    script.src = 'https://cdnjs.cloudflare.com/ajax/libs/html2pdf.js/0.10.1/html2pdf.bundle.min.js';
    script.async = true;
    script.onload = () => console.log("html2pdf loaded");
    script.onerror = () => console.error("Failed to load html2pdf");
    document.body.appendChild(script);
  }
});

const printReceipt = () => {
  window.print();
};

const downloadPDF = async () => {
  const element = document.getElementById('print-area');
  if (!element) return;
  
  const originalClass = element.className;
  element.className = 'p-8 bg-white text-black'; 

  await new Promise(resolve => setTimeout(resolve, 100));

  const opt = {
    margin: 10,
    filename: `phieu-nhap-${receipt.value?.id || 'detail'}.pdf`,
    image: { type: 'jpeg', quality: 0.98 },
    html2canvas: { scale: 2, useCORS: true, logging: true },
    jsPDF: { unit: 'mm', format: 'a4', orientation: 'portrait' }
  };

  if (window.html2pdf) {
    try {
      await window.html2pdf().set(opt).from(element).save();
    } catch (error) {
      console.error("PDF generation error:", error);
      toast.error("Có lỗi xảy ra khi tạo PDF.");
    } finally {
      element.className = originalClass;
    }
  } else {
    toast.error("Thư viện PDF chưa tải xong.");
    element.className = originalClass;
  }
};
</script>

<style scoped>
@media print {
  @page {
    size: A4;
    margin: 10mm;
  }
  
  body * {
    visibility: hidden;
  }

  #print-area, #print-area * {
    visibility: visible;
  }

  #print-area {
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
    display: block !important;
  }
}
</style>
