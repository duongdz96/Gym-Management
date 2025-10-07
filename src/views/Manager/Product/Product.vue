<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { RouterLink } from 'vue-router'
import api from '@/services/api'

type Product = {
  id: number
  name: string
  type: string
  price: number
  brand?: string
  quantity?: number
}

const products = ref<Product[]>([])

const search = ref('')
const productType = ref('')

onMounted(async () => {
  try {
    const res = await api.get("/product");
    products.value = res.data
  } catch (error) {
    console.error('Failed to load products:', error)
  }
})


const filteredProducts = computed(() => {
  return products.value.filter(p => {
    const matchesName = p.name.toLowerCase().includes(search.value.toLowerCase())
    const matchesType = productType.value ? p.type === productType.value : true
    return matchesName && matchesType
  })
})


const showImportModal = ref(false)
const selectedFile = ref(null)
const fileInputRef = ref(null)
const selectedFileName = ref('')

function closeImportModal() {
    showImportModal.value = false
    selectedFile.value = null
    selectedFileName.value = ''
    if (fileInputRef.value) fileInputRef.value.value = ''
}

function downloadSampleCSV() {
  const content =
    'name,type,price,brand,quantity\n' +
    'Dumbbell,Equipment,150000,ABC Sports,20\n' +
    'Yoga Mat,Accessory,80000,FitnessPro,50\n' +
    'Protein Powder,Supplement,650000,MuscleGain,15\n' +
    'Treadmill,Equipment,12000000,BodyTech,5\n' +
    'Jump Rope,Accessory,50000,FitnessPro,40\n';

  const blob = new Blob([content], { type: 'text/csv;charset=utf-8;' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = 'product_sample.csv';
  a.click();
  URL.revokeObjectURL(url);
}


function triggerFilePicker()
{
    fileInputRef.value?.click();
}

function onFileChange(e) {
    const input = e.target
    const file = input.files && input.files[0] ? input.files[0] : null
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
        alert('Vui lòng chọn file .csv hoặc .xlsx')
        input.value = ''
        return
    }
    selectedFile.value = file
    selectedFileName.value = file.name
}

function doImport() {
  if (!selectedFile.value) {
    alert("Please select a file first.");
    return;
  }

  const file = selectedFile.value;
  console.log("Importing file:", file.name);

  const reader = new FileReader();

  reader.onload = (e) => {
    const text = e.target?.result;
    console.log("File content preview:");
    console.log(text);
  };

  reader.onerror = (err) => {
    console.error("Error reading file:", err);
  };

  reader.readAsText(file);
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
        <!-- Modal Import Product -->
        <div v-if="showImportModal" class="fixed inset-0 z-50 flex items-center justify-center">
          <div class="absolute inset-0 bg-black/50" @click="closeImportModal"></div>
          <div class="relative w-full max-w-lg mx-4 bg-white rounded-lg shadow-lg border border-gray-200">
            <!-- Header -->
            <div class="flex items-center justify-between px-4 py-3 border-b border-gray-200">
              <h3 class="text-lg font-medium text-gray-900">Import Products</h3>
              <button
                class="text-gray-500 hover:text-gray-700 hover:cursor-pointer"
                @click="closeImportModal"
              >
                ✕
              </button>
            </div>

            <!-- Body -->
            <div class="p-4 space-y-4">
              <div class="text-sm text-gray-700">
                File CSV/XLSX must include columns:
                <code class="px-1 rounded bg-gray-100">name</code>,
                <code class="px-1 rounded bg-gray-100">type</code>,
                <code class="px-1 rounded bg-gray-100">price</code>,
                <code class="px-1 rounded bg-gray-100">brand</code>,
                <code class="px-1 rounded bg-gray-100">quantity</code>
              </div>

              <div class="flex items-center justify-between">
                <!-- CSV Example -->
                <button
                  class="rounded-md px-3 py-2 text-white bg-blue-600 hover:bg-blue-700 hover:cursor-pointer"
                  @click="downloadSampleCSV"
                >
                  View CSV Example
                </button>

                <!-- File name -->
                <span v-if="selectedFileName" class="text-sm text-gray-600 truncate">{{ selectedFileName }}</span>

                <!-- File picker -->
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

            <!-- Footer -->
            <div class="flex items-center justify-end gap-2 px-4 py-3 border-t border-gray-200">
              <button
                class="px-3 py-2 rounded-md border border-gray-200 text-gray-800 hover:bg-gray-100 hover:cursor-pointer"
                @click="closeImportModal"
              >
                Cancel
              </button>
              <button
                class="px-3 py-2 rounded-md bg-green-600 hover:bg-green-700 hover:cursor-pointer disabled:opacity-60 disabled:cursor-not-allowed text-white"
                :disabled="!selectedFile"
                @click="doImport"
              >
                Import
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
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Brand</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Quantity</th>
            <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-200 bg-white">
          <tr v-if="products.length === 0">
            <td colspan="7" class="px-4 py-3 text-center text-sm text-gray-500">No products found</td>
          </tr>
          <tr v-for="p in filteredProducts" :key="p.id" class="hover:bg-gray-50">
            <td class="px-4 py-3 text-sm text-gray-600">{{ p.id }}</td>
            <td class="px-4 py-3 text-sm text-blue-600 hover:underline hover:cursor-pointer">{{ p.name }}</td>
            <td class="px-4 py-3 text-sm text-gray-600">{{ p.type }}</td>
            <td class="px-4 py-3 text-sm text-gray-600">{{ p.price.toFixed(0) }}</td>
            <td class="px-4 py-3 text-sm text-gray-600">{{ p.brand || '-' }}</td>
            <td class="px-4 py-3 text-sm text-gray-600">{{ p.quantity ?? '-' }}</td>
            <td class="px-4 py-3 text-sm text-center">
              <button class="px-2 py-1 rounded bg-green-600 text-white mr-2 hover:bg-green-700 hover:cursor-pointer">Edit</button>
              <button class="px-2 py-1 rounded bg-red-600 text-white hover:bg-red-700 hover:cursor-pointer">Delete</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
