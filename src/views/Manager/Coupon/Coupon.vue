<script setup lang="ts">
import { computed, ref, onMounted } from 'vue';
import { RouterLink } from 'vue-router';
import api from '@/services/api';
import { useToast } from "vue-toastification";
import {
    SearchIcon,
    PlusIcon,
    UploadIcon,
    FileSpreadsheetIcon,
    FilterIcon,
    XIcon,
    DownloadIcon,
    FileTextIcon,
    EditIcon,
    TrashIcon,
    TagIcon,
    CalendarIcon,
    UsersIcon,
    PercentIcon,
    DollarSignIcon,
    ArrowUpDownIcon
} from 'lucide-vue-next';

const toast = useToast();

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
const loading = ref(false);

const fetchCoupons = async () => {
    loading.value = true;
    try {
        const res = await api.get("/coupons");
        coupons.value = res.data;
    } catch (error) {
        toast.error('Tải mã giảm giá thất bại');
    } finally {
        loading.value = false;
    }
}

onMounted(async () => {
    await fetchCoupons();
})

const search = ref('')
const couponType = ref('')
const couponStatus = ref('')
const sortBy = ref('') // 'asc' or 'desc'

// Delete confirmation modal
const showDeleteModal = ref(false)
const couponToDelete = ref(null)

const filteredCoupon = computed(() => {
    let result = coupons.value.filter(n => {
        const matchesSearch = (n.code || '').toLowerCase().includes(search.value.toLowerCase())
        const matchesType = couponType.value ? n.discountType === couponType.value : true
        const matchesStatus = couponStatus.value ? n.status === couponStatus.value : true
        return matchesSearch && matchesType && matchesStatus
    })
    
    // Sort by discount value
    if (sortBy.value === 'asc') {
        result.sort((a, b) => a.discountValue - b.discountValue)
    } else if (sortBy.value === 'desc') {
        result.sort((a, b) => b.discountValue - a.discountValue)
    }
    
    return result
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
    const content = 'code,type,value,start date,end date,status,scope\nSUMMER2025,PERCENTAGE,20,2025-09-01,2025-10-01,ACTIVE,Basic\nVIP50,PERCENTAGE,50,2025-09-01,2025-10-01,ACTIVE,VIP\nSTANDARD100K,FIXED_AMOUNT,100000,2025-09-01,2025-10-01,ACTIVE,Standard\n'
    const blob = new Blob([content], {type: 'text/csv;charset=utf-8;'})
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = 'mau_coupon.csv'
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
        toast.warning('Vui lòng chọn file .csv hoặc .xlsx')
        input.value = ''
        return
    }
    selectedFile.value = file
    selectedFileName.value = file.name
}

async function doImport() {
    if (!selectedFile.value) return;
    
    const formData = new FormData();
    formData.append('file', selectedFile.value);
    
    try {
        const res = await api.post('/coupons/import', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
        
        const result = res.data;
        if (result.success > 0) {
            toast.success(`Nhập thành công ${result.success} mã giảm giá`);
            if (result.errors > 0) {
                toast.warning(`${result.errors} mã giá bị lỗi.`);
            }
            await fetchCoupons(); // Refresh list
        } else {
            (result.errorDetails);
            toast.error('Nhập thất bại: ' + result.errorDetails);
        }
    } catch (error) {
        toast.error('Nhập coupon thất bại');
    } finally {
        closeImportModal();
    }
}

async function deleteCoupon(id) {
    try {
        await api.delete(`/coupons/${id}`);
        coupons.value = coupons.value.filter(c => c.id !== id);
        toast.success("Xoá coupon thành công");
        showDeleteModal.value = false;
        couponToDelete.value = null;
    } catch (e) {
        toast.error("Xoá coupon thất bại");
    }
}

function confirmDelete(coupon) {
    couponToDelete.value = coupon;
    showDeleteModal.value = true;
}

function cancelDelete() {
    showDeleteModal.value = false;
    couponToDelete.value = null;
}
</script>
<template>
    <div class="p-6 max-w-[1600px] mx-auto space-y-6">
        <!-- Header & Actions -->
        <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
             <div>
                 <h1 class="text-2xl font-bold text-gray-800 dark:text-gray-100 flex items-center gap-2">
                    <TagIcon class="w-7 h-7 text-blue-600"/>
                    Quản Lý Mã Giảm Giá
                 </h1>
                 <p class="text-gray-500 dark:text-gray-400 text-sm mt-1">Quản lý mã giảm giá và khuyến mãi</p>
             </div>

             <div class="flex items-center gap-3">
                <button @click="showImportModal = true" class="flex items-center gap-2 px-4 py-2 bg-white border border-gray-300 dark:bg-gray-800 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700 text-gray-700 dark:text-gray-200 rounded-lg shadow-sm transition-all">
                    <UploadIcon class="w-4 h-4"/>
                    <span>Nhập Coupon Bằng CSV</span>
                </button>
                <RouterLink :to="{ name: 'coupon.add' }" class="flex items-center gap-2 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg shadow-lg hover:shadow-blue-500/30 transition-all">
                    <PlusIcon class="w-4 h-4"/>
                    <span>Tạo Mã Giảm Giá</span>
                </RouterLink>
             </div>
        </div>

        <!-- Filters -->
        <div class="bg-white dark:bg-gray-900 p-4 rounded-xl shadow-sm border border-gray-200 dark:border-gray-800 flex flex-col md:flex-row items-center gap-4">
            <div class="relative flex-1 w-full">
                <SearchIcon class="absolute left-3 top-2.5 w-5 h-5 text-gray-400"/>
                <input
                    type="text"
                    v-model="search"
                    placeholder="Tìm kiếm theo mã coupon..."
                    class="w-full pl-10 pr-4 py-2 rounded-lg border border-gray-300 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all"
                >
            </div>
            
            <div class="flex items-center gap-2 w-full md:w-auto">
                <div class="relative w-full md:w-48">
                    <FilterIcon class="absolute left-3 top-2.5 w-4 h-4 text-gray-400"/>
                    <select v-model="couponType" class="w-full pl-9 pr-8 py-2 rounded-lg border border-gray-300 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 appearance-none cursor-pointer">
                        <option value="">Tất cả loại</option>
                        <option value="PERCENTAGE">Phần trăm</option>
                        <option value="FIXED_AMOUNT">Số tiền cố định</option>
                    </select>
                </div>
                
                <div class="relative w-full md:w-48">
                    <ActivityIcon class="absolute left-3 top-2.5 w-4 h-4 text-gray-400"/>
                    <select v-model="couponStatus" class="w-full pl-9 pr-8 py-2 rounded-lg border border-gray-300 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 appearance-none cursor-pointer">
                        <option value="">Tất cả trạng thái</option>
                        <option value="ACTIVE">Đang hoạt động</option>
                        <option value="INACTIVE">Không hoạt động</option>
                    </select>
                </div>
                
                <div class="relative w-full md:w-48">
                    <ArrowUpDownIcon class="absolute left-3 top-2.5 w-4 h-4 text-gray-400"/>
                    <select v-model="sortBy" class="w-full pl-9 pr-8 py-2 rounded-lg border border-gray-300 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 appearance-none cursor-pointer">
                        <option value="">Không sắp xếp</option>
                        <option value="asc">Giá trị: Thấp đến Cao</option>
                        <option value="desc">Giá trị: Cao đến Thấp</option>
                    </select>
                </div>
            </div>
        </div>

        <!-- Table -->
        <div class="bg-white dark:bg-gray-900 border border-gray-200 dark:border-gray-800 rounded-xl shadow-sm overflow-hidden">
            <div class="overflow-x-auto">
                <table class="w-full whitespace-nowrap">
                    <thead class="bg-gray-50 dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700">
                        <tr>
                            <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wider">Thông Tin</th>
                            <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wider">Giảm Giá</th>
                            <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wider">Thời Hạn</th>
                            <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wider">Phạm Vi</th>
                            <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wider">Trạng Thái</th>
                            <th class="px-6 py-4 text-right text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wider">Hành Động</th>
                        </tr>
                    </thead>
                    <tbody class="divide-y divide-gray-200 dark:divide-gray-800">
                        <tr v-if="loading" class="animate-pulse">
                            <td colspan="6" class="px-6 py-4 text-center text-gray-400">Đang tải dữ liệu...</td>
                        </tr>
                        <tr v-else-if="filteredCoupon.length === 0">
                            <td colspan="6" class="px-6 py-8 text-center text-gray-500 dark:text-gray-400">
                                <div class="flex flex-col items-center justify-center gap-2">
                                    <FileSpreadsheetIcon class="w-8 h-8 opacity-50"/>
                                    <span class="block">Không tìm thấy mã giảm giá nào.</span>
                                </div>
                            </td>
                        </tr>
                        <tr v-for="c in filteredCoupon" :key="c.id" class="hover:bg-gray-50 dark:hover:bg-gray-800/50 transition-colors group">
                            <td class="px-6 py-4">
                                <div class="flex flex-col">
                                    <RouterLink 
                                        :to="{ name: 'CouponDetail', params: { id: c.id } }"
                                        class="font-medium text-blue-600 hover:text-blue-800 dark:text-blue-400 dark:hover:text-blue-300 hover:underline cursor-pointer flex items-center gap-2"
                                    >
                                        {{ c.code }}
                                    </RouterLink>
                                    <span class="text-xs text-gray-500">ID: {{ c.id }}</span>
                                </div>
                            </td>
                            <td class="px-6 py-4">
                                <div class="flex items-center gap-2">
                                    <span class="p-1.5 rounded-full bg-blue-100 dark:bg-blue-900/30 text-blue-600 dark:text-blue-400">
                                        <PercentIcon v-if="c.discountType !== 'FIXED_AMOUNT'" class="w-4 h-4"/>
                                        <DollarSignIcon v-else class="w-4 h-4"/>
                                    </span>
                                    <span class="font-medium text-gray-700 dark:text-gray-300">
                                        {{ c.discountValue }} <span v-if="c.discountType !== 'FIXED_AMOUNT'">%</span> <span v-else>VND</span>
                                    </span>
                                </div>
                            </td>
                            <td class="px-6 py-4">
                                <div class="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-300">
                                    <CalendarIcon class="w-4 h-4 text-gray-400"/>
                                    {{ formatDate(c.startDate) }} - {{ formatDate(c.endDate) }}
                                </div>
                            </td>
                            <td class="px-6 py-4">
                                <div class="flex items-center gap-2">
                                    <UsersIcon class="w-4 h-4 text-gray-400"/>
                                    <span class="text-sm text-gray-700 dark:text-gray-300 capitalize">{{ c.scope || 'Người dùng cụ thể' }}</span>
                                </div>
                            </td>
                            <td class="px-6 py-4">
                                <span :class="[
                                    'px-2.5 py-1 rounded-full text-xs font-medium border',
                                    c.status === 'ACTIVE' 
                                        ? 'bg-green-100 text-green-700 border-green-200 dark:bg-green-900/30 dark:text-green-400 dark:border-green-800' 
                                        : 'bg-gray-100 text-gray-700 border-gray-200 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-700'
                                ]">
                                    {{ c.status === 'ACTIVE' ? 'Đang hoạt động' : 'Không hoạt động' }}
                                </span>
                            </td>
                            <td class="px-6 py-4 text-right">
                                <div class="flex items-center justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                                    <RouterLink 
                                        :to="{ name: 'coupon.edit', params: { id: c.id } }" 
                                        class="p-2 rounded-lg text-blue-600 hover:bg-blue-50 dark:hover:bg-blue-900/30 transition-colors"
                                        title="Sửa"
                                    >
                                        <EditIcon class="w-4 h-4"/>
                                    </RouterLink>
                                    <button 
                                        @click="confirmDelete(c)"
                                        class="p-2 rounded-lg text-red-600 hover:bg-red-50 dark:hover:bg-red-900/30 transition-colors"
                                        title="Xóa"
                                    >
                                        <TrashIcon class="w-4 h-4"/>
                                    </button>
                                </div>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Import Modal -->
         <Transition
            enter-active-class="transition duration-200 ease-out"
            enter-from-class="transform scale-95 opacity-0"
            enter-to-class="transform scale-100 opacity-100"
            leave-active-class="transition duration-150 ease-in"
            leave-from-class="transform scale-100 opacity-100"
            leave-to-class="transform scale-95 opacity-0"
         >
            <div v-if="showImportModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
                <div class="absolute inset-0 bg-black/60 backdrop-blur-sm" @click="closeImportModal"></div>
                <div class="relative w-full max-w-lg bg-white dark:bg-gray-900 rounded-2xl shadow-2xl border border-gray-200 dark:border-gray-800 overflow-hidden">
                    <div class="flex items-center justify-between px-6 py-4 border-b border-gray-100 dark:border-gray-800 bg-gray-50/50 dark:bg-gray-800/50">
                        <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 flex items-center gap-2">
                            <UploadIcon class="w-5 h-5 text-blue-600"/>
                            Nhập Mã Giảm Giá
                        </h3>
                        <button class="p-1 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors" @click="closeImportModal">
                            <XIcon class="w-5 h-5 text-gray-500"/>
                        </button>
                    </div>
                    
                    <div class="p-6 space-y-6">
                        <div class="p-4 bg-blue-50 dark:bg-blue-900/20 rounded-xl border border-blue-100 dark:border-blue-800">
                            <p class="text-sm text-blue-800 dark:text-blue-300 font-medium mb-2">Các cột bắt buộc:</p>
                            <div class="flex flex-wrap gap-2">
                                <span v-for="col in ['code', 'type', 'value', 'start date', 'end date', 'status', 'scope']" :key="col" class="px-2 py-1 bg-white dark:bg-gray-800 rounded-md text-xs font-mono text-gray-600 dark:text-gray-300 border border-blue-200 dark:border-blue-700 shadow-sm">
                                    {{ col }}
                                </span>
                            </div>
                        </div>

                        <div class="flex flex-col gap-4">
                             <div 
                                class="border-2 border-dashed border-gray-300 dark:border-gray-700 rounded-xl p-8 flex flex-col items-center justify-center text-center cursor-pointer hover:border-blue-500 hover:bg-blue-50 dark:hover:bg-blue-900/10 transition-all group"
                                @click="triggerFilePicker"
                             >
                                <div class="w-12 h-12 rounded-full bg-blue-100 dark:bg-blue-900/30 flex items-center justify-center mb-3 group-hover:scale-110 transition-transform">
                                    <FileTextIcon class="w-6 h-6 text-blue-600 dark:text-blue-400"/>
                                </div>
                                <p class="text-sm font-medium text-gray-900 dark:text-gray-100">Nhấp để tải lên hoặc kéo thả file</p>
                                <p class="text-xs text-gray-500 dark:text-gray-400 mt-1">CSV, Excel (tối đa 10MB)</p>
                                <input type="file" class="hidden" ref="fileInputRef" accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel" @change="onFileChange">
                             </div>

                             <div v-if="selectedFileName" class="flex items-center justify-between p-3 bg-green-50 dark:bg-green-900/20 rounded-lg border border-green-200 dark:border-green-800">
                                <span class="text-sm font-medium text-green-800 dark:text-green-300 flex items-center gap-2">
                                    <FileSpreadsheetIcon class="w-4 h-4"/>
                                    {{ selectedFileName }}
                                </span>
                                <button @click="selectedFile = null; selectedFileName = ''" class="text-green-600 hover:text-green-800">
                                    <XIcon class="w-4 h-4"/>
                                </button>
                             </div>
                        </div>

                         <div class="flex justify-between items-center pt-2">
                            <button class="text-sm text-blue-600 hover:underline flex items-center gap-1" @click="downloadSampleCSV">
                                <DownloadIcon class="w-4 h-4"/>
                                Tải Mẫu CSV
                            </button>
                        </div>
                    </div>

                    <div class="flex items-center justify-end gap-3 px-6 py-4 border-t border-gray-100 dark:border-gray-800 bg-gray-50/50 dark:bg-gray-800/50">
                        <button class="px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors" @click="closeImportModal">Hủy</button>
                        <button class="px-4 py-2 rounded-lg bg-green-600 hover:bg-green-700 text-white font-medium shadow-lg hover:shadow-green-500/30 disabled:opacity-50 disabled:cursor-not-allowed transition-all" :disabled="!selectedFile" @click="doImport">Nhập Dữ Liệu</button>
                    </div>
                </div>
            </div>
         </Transition>

        <!-- Delete Confirmation Modal -->
        <Transition
            enter-active-class="transition duration-200 ease-out"
            enter-from-class="transform scale-95 opacity-0"
            enter-to-class="transform scale-100 opacity-100"
            leave-active-class="transition duration-150 ease-in"
            leave-from-class="transform scale-100 opacity-100"
            leave-to-class="transform scale-95 opacity-0"
        >
            <div v-if="showDeleteModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
                <div class="absolute inset-0 bg-black/60 backdrop-blur-sm" @click="cancelDelete"></div>
                <div class="relative w-full max-w-md bg-white dark:bg-gray-900 rounded-2xl shadow-2xl border border-gray-200 dark:border-gray-800 overflow-hidden">
                    <div class="p-6">
                        <div class="flex items-center justify-center w-12 h-12 mx-auto mb-4 rounded-full bg-red-100 dark:bg-red-900/30">
                            <TrashIcon class="w-6 h-6 text-red-600 dark:text-red-400"/>
                        </div>
                        <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 text-center mb-2">
                            Xác nhận xóa coupon
                        </h3>
                        <p class="text-sm text-gray-600 dark:text-gray-400 text-center mb-6">
                            Bạn có chắc chắn muốn xóa coupon <span class="font-semibold text-gray-900 dark:text-gray-100">{{ couponToDelete?.code }}</span>? 
                            Hành động này không thể hoàn tác và sẽ xóa tất cả issued coupon liên quan.
                        </p>
                        <div class="flex items-center justify-end gap-3">
                            <button 
                                @click="cancelDelete"
                                class="px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
                            >
                                Hủy
                            </button>
                            <button 
                                @click="deleteCoupon(couponToDelete.id)"
                                class="px-4 py-2 rounded-lg bg-red-600 hover:bg-red-700 text-white font-medium shadow-lg hover:shadow-red-500/30 transition-all"
                            >
                                Xóa coupon
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </Transition>
    </div>

</template>