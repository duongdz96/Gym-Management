<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import axios from 'axios' // (Không dùng đến nếu đã có 'api')
import { RouterLink } from 'vue-router'
import api from '@/services/api'
import { useToast } from "vue-toastification"; // (Tôi thêm vào, bạn đã dùng ở file khác)

// 1. Cập nhật Type
type Product = {
  id: number
  name: string
  type: string
  price: number
  importPrice?: number // <-- Thêm
  brand?: string
  quantity?: number
  status: boolean // <-- Thêm
}

const products = ref<Product[]>([])
const search = ref('')
const productType = ref('')
const toast = useToast()

// 2. Tách hàm fetch
async function fetchProducts() {
  try {
    const res = await api.get("/product/admin/all"); // Sử dụng prefix /api/
    products.value = res.data
  } catch (error) {
    console.error('Failed to load products:', error)
    toast.error("Failed to load products")
  }
}

onMounted(() => {
  fetchProducts()
})

const filteredProducts = computed(() => {
  return products.value.filter(p => {
    const matchesName = p.name.toLowerCase().includes(search.value.toLowerCase())
    const matchesType = productType.value ? p.type === productType.value : true
    return matchesName && matchesType
  })
})

// --- Logic Xoá / Khôi phục ---
const handleDelete = async (id: number) => {
  if (!confirm("Bạn có chắc muốn xoá mềm sản phẩm này?")) return;
  try {
    await api.delete(`/product/${id}`); // Gọi API soft delete
    toast.success("Product deleted successfully");
    fetchProducts(); // Tải lại danh sách
  } catch (error) {
    toast.error("Failed to delete product");
  }
}

const handleRestore = async (id: number) => {
  try {
    await api.post(`/product/${id}/restore`); // Gọi API restore
    toast.success("Product restored successfully");
    fetchProducts(); // Tải lại danh sách
  } catch (error) {
    toast.error("Failed to restore product");
  }
}


// --- Logic Modal ---
const showImportModal = ref(false)
const selectedFile = ref<File | null>(null)
const fileInputRef = ref<HTMLInputElement | null>(null)
const selectedFileName = ref('')
const isImporting = ref(false)

function closeImportModal() {
  showImportModal.value = false
  selectedFile.value = null
  selectedFileName.value = ''
  if (fileInputRef.value) fileInputRef.value.value = ''
}

// 3. Cập nhật CSV mẫu
function downloadSampleCSV() {
  const content =
    'name,type,price,importPrice,brand,quantity\n' + // <-- Thêm importPrice
    'Dumbbell,Equipment,150000,100000,ABC Sports,20\n' +
    'Yoga Mat,Accessory,80000,50000,FitnessPro,50\n' +
    'Protein Powder,Supplement,650000,500000,MuscleGain,15\n';

  const blob = new Blob([content], { type: 'text/csv;charset=utf-8;' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = 'product_sample.csv';
  a.click();
  URL.revokeObjectURL(url);
}

function triggerFilePicker() {
  fileInputRef.value?.click();
}

function onFileChange(e: Event) {
  const input = e.target as HTMLInputElement
  const file = input.files?.[0] || null
  if (!file) {
    selectedFile.value = null
    selectedFileName.value = ''
    return
  }
  const allowed = [
    'text/csv',
    'application/vnd.ms-excel',
    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
  ]
  if (!allowed.includes(file.type) && !/\.(csv|xlsx)$/i.test(file.name)) {
    toast.error('Vui lòng chọn file .csv hoặc .xlsx')
    input.value = ''
    return
  }
  selectedFile.value = file
  selectedFileName.value = file.name
}

// 4. Sửa hàm doImport để GỬI file lên
async function doImport() {
  if (!selectedFile.value) {
    toast.error("Please select a file first.");
    return;
  }
  isImporting.value = true;
  
  // Dùng FormData để gửi file
  const formData = new FormData();
  formData.append('file', selectedFile.value);

  try {
    // Giả sử backend có endpoint này
    await api.post('/product/import-csv', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
    
    toast.success("Products imported successfully!");
    closeImportModal();
    fetchProducts(); // Tải lại danh sách sau khi import

  } catch (error) {
    console.error("Error importing file:", error);
    toast.error("Failed to import products.");
  } finally {
    isImporting.value = false;
  }
}
</script>

<template>
  <div class="space-y-4 p-4">
    <div class="flex justify-between items-center">
      <h1 class="text-xl font-semibold">Product</h1>
      <div>
        <input
          v-model="search"
          type="text"
          placeholder="Search by product name"
          class="px-3 py-2 border rounded-lg mr-2"
        />
        <select v-model="productType" class="px-3 py-2 border rounded-lg mr-2">
          <option value="">All types</option>
          <option value="clothes">Clothes</option>
          <option value="powder">Powder</option>
          <option value="drinks">Drinks</option>
          <option value="Equipment">Equipment</option>
          <option value="Accessory">Accessory</option>
          <option value="Supplement">Supplement</option>
        </select>
        <RouterLink
          :to="{ name: 'product.add' }"
          class="px-3 py-2 bg-blue-600 text-white rounded-lg hover:opacity-90 mr-2"
        >
          Add
        </RouterLink>
        <RouterLink
          :to="{ name: 'product.importproduct' }"
          class="px-3 py-2 bg-blue-600 text-white rounded-lg hover:opacity-90"
        >
          Import From Provider
        </RouterLink>
        <button @click="showImportModal = true" class="px-3 py-2 rounded-lg bg-green-600 text-white hover:opacity-90 hover:cursor-pointer ml-2">Import CSV</button>
        
        <div v-if="showImportModal" class="fixed inset-0 z-50 flex items-center justify-center">
          <div class="absolute inset-0 bg-black/50" @click="closeImportModal"></div>
          <div class="relative w-full max-w-lg mx-4 bg-white rounded-lg shadow-lg border border-gray-200">
            <div class="flex items-center justify-between px-4 py-3 border-b border-gray-200">
              <h3 class="text-lg font-medium text-gray-900">Import Products</h3>
              <button
                class="text-gray-500 hover:text-gray-700 hover:cursor-pointer"
                @click="closeImportModal"
              >
                ✕
              </button>
            </div>

            <div class="p-4 space-y-4">
              <div class="text-sm text-gray-700">
                File CSV/XLSX must include columns:
                <code class="px-1 rounded bg-gray-100">name</code>,
                <code class="px-1 rounded bg-gray-100">type</code>,
                <code class="px-1 rounded bg-gray-100">price</code>,
                <code class="px-1 rounded bg-gray-100">importPrice</code>, <code class="px-1 rounded bg-gray-100">brand</code>,
                <code class="px-1 rounded bg-gray-100">quantity</code>
              </div>

              <div class="flex items-center justify-between">
                <button
                  class="rounded-md px-3 py-2 text-white bg-blue-600 hover:bg-blue-700 hover:cursor-pointer"
                  @click="downloadSampleCSV"
                >
                  View CSV Example
                </button>
                <span v-if="selectedFileName" class="text-sm text-gray-600 truncate">{{ selectedFileName }}</span>
                <input
                  type="file"
                  class="hidden"
                  ref="fileInputRef"
                  accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel"
                  @change="onFileChange"
                />
                <button
                  class="px-3 py-2 rounded-md bg-gray-100 text-gray-800 hover:bg-gray-200 hover:cursor-pointer border border-gray-200"
                  @click="triggerFilePicker"
                >
                  Choose File
                </button>
              </div>
            </div>

            <div class="flex items-center justify-end gap-2 px-4 py-3 border-t border-gray-200">
              <button
                class="px-3 py-2 rounded-md border border-gray-200 text-gray-800 hover:bg-gray-100 hover:cursor-pointer"
                @click="closeImportModal"
              >
                Cancel
              </button>
              <button
                class="px-3 py-2 rounded-md bg-green-600 hover:bg-green-700 hover:cursor-pointer disabled:opacity-60 disabled:cursor-not-allowed text-white"
                :disabled="!selectedFile || isImporting"
                @click="doImport"
              >
                {{ isImporting ? 'Importing...' : 'Import' }}
              </button>
            </div>
          </div>
        </div>

      </div>
    </div>

    <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Name</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Type</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Price</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Import Price</th> <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Brand</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Quantity</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th> <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-200 bg-white">
          <tr v-if="products.length === 0">
            <td colspan="9" class="px-4 py-3 text-center text-sm text-gray-500">No products found</td> </tr>
          <tr v-for="p in filteredProducts" :key="p.id" class="hover:bg-gray-50">
            <td class="px-4 py-3 text-sm text-gray-600">{{ p.id }}</td>
            <td class="px-4 py-3 text-sm text-blue-600 hover:underline hover:cursor-pointer">{{ p.name }}</td>
            <td class="px-4 py-3 text-sm text-gray-600">{{ p.type }}</td>
            <td class="px-4 py-3 text-sm text-gray-600">{{ p.price.toFixed(0) }}</td>
            <td class="px-4 py-3 text-sm text-gray-600">{{ p.importPrice?.toFixed(0) || '-' }}</td> <td class="px-4 py-3 text-sm text-gray-600">{{ p.brand || '-' }}</td>
            <td class="px-4 py-3 text-sm text-gray-600">{{ p.quantity ?? '-' }}</td>
            <td class="px-4 py-3 text-sm text-gray-600"> <span 
                :class="[
                  'px-2 py-0.5 rounded-full text-xs font-medium',
                  p.status === false ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'
                ]"
              >
                {{ p.status === false ? 'Active' : 'Deleted' }}
              </span>
            </td>
            <td class="px-4 py-3 text-sm text-center"> <RouterLink
                :to="{ name: 'product.edit', params: { id: p.id } }" class="px-2 py-1 rounded bg-green-600 text-white mr-2 hover:bg-green-700 hover:cursor-pointer"
                v-if="!p.status"
              >
                Edit
              </RouterLink>
              <button
                class="px-2 py-1 rounded bg-red-600 text-white hover:bg-red-700 hover:cursor-pointer"
                v-if="!p.status"
                @click="handleDelete(p.id)"
              >
                Delete
              </button>
              <button
                class="px-2 py-1 rounded bg-blue-600 text-white hover:bg-blue-700 hover:cursor-pointer"
                v-if="p.status"
                @click="handleRestore(p.id)"
              >
                Restore
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>