<script setup lang="ts">
import { computed, ref,onMounted } from 'vue';
import { RouterLink } from 'vue-router';
import axios from 'axios'
import api from '@/services/api';


onMounted(async () => {
    try {
        const res = await axios.get(`http://localhost:8080/api/product`)
        console.log(res)
    } finally {
    }
})

type Coupon = {
    id: number,
    code: string,
    discountType: any,
    discountValue: number,
    startDate: string,
    endDate: string,
    status: any,
    scope: string,
}

const coupons = ref<Coupon[]>([])

onMounted(async () => {
  try {
    const res = await api.get("/coupons");
    coupons.value = res.data
  } catch (error) {
    console.error('Failed to load products:', error)
  }
})

const search = ref('')
const couponType = ref('')
const couponStatus = ref('')

const filteredCoupon = computed(() => {
    return coupons.value.filter(n => {
        const matchesSearch = (n.code || '').toLowerCase().includes(search.value.toLowerCase())
        const matchesType = couponType.value ? n.discountType === couponType.value : true
        const matchesStatus = couponStatus.value ? n.status === couponStatus.value : true
        return matchesSearch && matchesType && matchesStatus
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
    const content = 'code,type,value,start date,end date,status,scope\nTEST,1,20,2025-09-01,2025-10-01,1,bronze\nTEST2,2,100000,2025-09-01,2025-10-01,2,silver\n'
    const blob = new Blob([content], {type: 'text/csv;charset=utf-8;'})
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = 'coupon_sample.csv'
    a.click()
    URL.revokeObjectURL(url)
}

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  });
};

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

}
</script>
<template>
    <div class="space-y-4 p-4">
        <!-- Toolbar -->
        <div class=" justify-between flex">
             <h1 class="text-xl font-semibold">Coupon</h1>
             <div>
                <input type="text" v-model="search" placeholder="Search for Coupon code" class="px-3 py-2 border border-gray-200 rounded-lg bg-white focus:outline-none focus:ring-2 focus:ring-gray-200 mr-2">

                <select v-model="couponType" class="px-3 py-2 border border-gray-200 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800 mr-2">
                    <option value="">All type</option>
                 <option :value="1">Percentage</option>
                 <option :value="2">Fixed</option>
                </select>

                <select v-model="couponStatus" class="px-3 py-2 border border-gray-200 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800">
                    <option value="">All status</option>
                 <option :value="1">Active</option>
                 <option :value="2">Inactive</option>
                </select>
             </div>
             <div>
                <RouterLink :to="{ name: 'coupon.add' }" class="px-3 py-2 rounded-lg bg-blue-600 text-white hover:opacity-90">Add</RouterLink>
                <button @click="showImportModal = true" class="px-3 py-2 rounded-lg bg-green-600 text-white hover:opacity-90 hover:cursor-pointer ml-2">Import CSV</button>
             </div>
        </div>
        <!-- Modal -->
         <div v-if="showImportModal" class="fixed inset-0 z-50 flex items-center justify-center">
            <div class="absolute inset-0 bg-black/50" @click="closeImportModal"></div>
            <div class="relative w-full max-w-lg mx-4 bg-white rounded-lg shadow-lg border border-gray-200">
                <div class="flex items-center justify-between px-4 py-3 border-b border-gray-200">
                    <h3 class="text-lg font-medium text-gray-900">Import Coupon</h3>
                    <button class="text-gray-500 hover:text-gray-700 hover:cursor-pointer" @click="closeImportModal">X</button>
                </div>
                <div class="p-4 space-y-4">
                    <div class="text-sm text-gray-700">
                        File CSV/XSXL with column: 
                        <code class="px-1 rounded bg-gray-100">code</code>,
                        <code class="px-1 rounded bg-gray-100">type</code>,
                        <code class="px-1 rounded bg-gray-100">value</code>,
                        <code class="px-1 rounded bg-gray-100">start date</code>,
                        <code class="px-1 rounded bg-gray-100">end date</code>,
                        <code class="px-1 rounded bg-gray-100">status</code>,
                        <code class="px-1 rounded bg-gray-100">scope</code>
                    </div>
                    <div class="flex items-center justify-between">
                        <button class="rounded-md px-3 py-2 text-white bg-amber-500 hover:bg-amber-600 hover:cursor-pointer" @click="downloadSampleCSV">View CSV Example</button>
                        <span v-if="selectedFileName" class="text-sm text-gray-600 truncate">{{ selectedFileName }}</span>
                        <input type="file" class="hidden" ref="fileInputRef" accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel" @change="onFileChange">
                        <button class="px-3 py-2 rounded-md bg-gray-100 text-gray-800 hover:bg-gray-200 hover:cursor-pointer border border-gray-200" @click="triggerFilePicker">Choose File</button>
                    </div>
                </div>
                <div class="flex items-center justify-end gap-2 px-4 py-3 border-t border-gray-200">
                    <button class="px-3 py-2 rounded-md border border-gray-200 text-gray-800 hover:bg-gray-100 hover:cursor-pointer" @click="closeImportModal">Cancel</button>
                    <button class="px-3 py-2 rounded-md bg-green-600 hover:bg-green-700 hover:cursor-pointer disabled:opacity-60 disabled:cursor-not-allowed text-white" :disabled="!selectedFile" @click="doImport">Import</button>
                </div>
            </div>
         </div>
        <!-- Table -->
        <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
            <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                    <tr>
                        <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <span>ID</span>
                        </th>
                        <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <span>Code</span>
                        </th>
                        <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <span>Discount Type</span>
                        </th>
                        <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <span>Discount Value</span>
                        </th>
                        <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <span>Duration</span>
                        </th>
                        <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <span>Status</span>
                        </th>
                        <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <span>Scope</span>
                        </th>
                        <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <span>Actions</span>
                        </th>
                    </tr>
                </thead>
                <tbody class="divide-y divide-gray-200 bg-white">
                    <tr v-if="coupons.length === 0">
                        <td colspan="8" class="px-4 py-3 text-center text-sm text-gray-500">No coupons found</td>
                    </tr>
                    <tr v-for="c in filteredCoupon" :key="c.id" class="hover:bg-gray-50">
                        <td class="px-4 py-3 text-sm text-gray-600">{{ c.id }}</td>
                        <td class="px-4 py-3 text-sm">
                            <RouterLink
                                :to="{ name: 'CouponDetail', params: { id: c.id } }"
                                class="text-blue-600 hover:underline hover:cursor-pointer"
                            >
                                {{ c.code }}
                            </RouterLink>
                        </td>
                        <td class="px-4 py-3 text-sm text-gray-600">{{ c.discountType === 2 ? 'Fixed' : 'Percentage' }}</td>
                        <td class="px-4 py-3 text-sm text-gray-600">{{ c.discountValue }}</td>
                        <td class="px-4 py-3 text-sm text-gray-600">{{ formatDate(c.startDate) }} - {{ formatDate(c.endDate) }}</td>
                        <td class="px-4 py-2 text-sm">
                            <span :class="[
                                'px-2 py-1 rounded-full text-xs', c.status === 1 ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'
                            ]">
                            {{ c.status === 1 ? 'Active' : 'Inactive' }}
                            </span>
                        </td>
                        <td class="px-4 py-3 text-sm text-gray-600">{{ c.scope }}</td>
                        <td class="px-4 py-3 text-sm text-center">
                            <button class="px-2 py-1 rounded bg-green-600 text-white mr-2 hover:cursor-pointer hover:bg-green-700">Edit</button>
                            <button class="px-2 py-1 rounded bg-red-600 text-white hover:cursor-pointer hover:bg-red-700">Delete</button>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>

</template>