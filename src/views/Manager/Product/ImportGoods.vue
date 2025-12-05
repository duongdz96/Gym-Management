<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex justify-between items-center bg-white p-6 rounded-xl shadow-sm border border-gray-100 print:hidden">
      <div>
        <h1 class="text-2xl font-bold text-gray-800">Phiếu Nhập Hàng</h1>
        <p class="text-gray-500 mt-1">Tạo phiếu nhập hàng mới từ nhà cung cấp</p>
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
        <button @click="saveImport" class="flex items-center gap-2 px-6 py-2 bg-blue-600 text-white rounded-lg shadow-md hover:bg-blue-700 transition-colors font-medium">
          <SaveIcon class="w-4 h-4" />
          Lưu phiếu
        </button>
      </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 print:hidden">
      <!-- Left Column: Supplier & Product Selection -->
      <div class="lg:col-span-1 space-y-6">
        <!-- Supplier Section -->
        <div class="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-lg font-semibold text-gray-800 flex items-center gap-2">
              <TruckIcon class="w-5 h-5 text-blue-500" />
              Nhà cung cấp
            </h2>
            <button @click="openSupplierModal" class="text-sm text-blue-600 hover:text-blue-800 font-medium flex items-center gap-1">
              <PlusIcon class="w-3 h-3" />
              Thêm mới
            </button>
          </div>
          
          <div class="space-y-4">
            <VueMultiselect
              v-model="selectedSupplier"
              :options="supplierOptions"
              label="name"
              track-by="id"
              placeholder="Tìm kiếm nhà cung cấp..."
              select-label=""
              deselect-label=""
              selected-label="Đã chọn"
            >
              <template #noResult>Không tìm thấy kết quả</template>
            </VueMultiselect>
            
            <div v-if="selectedSupplier" class="p-4 bg-blue-50 rounded-lg border border-blue-100 text-sm space-y-2 animate-fade-in-down">
              <p><span class="font-medium text-blue-900">Địa chỉ:</span> {{ selectedSupplier.address }}</p>
              <p><span class="font-medium text-blue-900">SĐT:</span> {{ selectedSupplier.phone }}</p>
              <p><span class="font-medium text-blue-900">Email:</span> {{ selectedSupplier.email }}</p>
            </div>
          </div>
        </div>

        <!-- Product Selection Section -->
        <div class="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-lg font-semibold text-gray-800 flex items-center gap-2">
              <PackageIcon class="w-5 h-5 text-indigo-500" />
              Thêm sản phẩm
            </h2>
            <button @click="openProductModal" class="text-sm text-indigo-600 hover:text-indigo-800 font-medium flex items-center gap-1">
              <PlusIcon class="w-3 h-3" />
              Thêm mới
            </button>
          </div>

          <div class="space-y-4">
            <VueMultiselect
              v-model="tempSelectedProduct"
              :options="inventoryStore.products"
              label="name"
              track-by="id"
              placeholder="Tìm kiếm sản phẩm..."
              select-label=""
              deselect-label=""
              selected-label="Đã chọn"
              @update:modelValue="addProductToImport"
            >
              <template #option="{ option }">
                <div class="flex justify-between items-center">
                  <span>{{ option.name }}</span>
                  <span class="text-xs text-gray-500">{{ option.brand }}</span>
                </div>
              </template>
              <template #noResult>Không tìm thấy kết quả</template>
            </VueMultiselect>
            <p class="text-xs text-gray-500">Chọn sản phẩm để thêm vào danh sách nhập hàng bên dưới.</p>
          </div>
        </div>
        
        <!-- Summary Card -->
         <div class="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
            <h2 class="text-lg font-semibold text-gray-800 mb-4 flex items-center gap-2">
              <CalculatorIcon class="w-5 h-5 text-green-500" />
              Tổng kết
            </h2>
            <div class="space-y-3 text-sm">
              <div class="flex justify-between">
                <span class="text-gray-600">Tổng số lượng:</span>
                <span class="font-medium">{{ totalQuantity }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-gray-600">Tổng tiền nhập:</span>
                <span class="font-bold text-lg text-blue-600">{{ formatCurrency(totalImportPrice) }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-gray-600">Tổng tiền bán dự kiến:</span>
                <span class="font-medium text-green-600">{{ formatCurrency(totalSellingPrice) }}</span>
              </div>
            </div>
         </div>
      </div>

      <!-- Right Column: Import List -->
      <div class="lg:col-span-2">
        <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
          <div class="p-6 border-b border-gray-100 flex justify-between items-center">
            <h2 class="text-lg font-semibold text-gray-800">Danh sách hàng nhập</h2>
            <span class="bg-gray-100 text-gray-600 text-xs px-2 py-1 rounded-full">{{ importItems.length }} sản phẩm</span>
          </div>
          
          <div class="overflow-x-auto">
            <table class="w-full text-left text-sm">
              <thead class="bg-gray-50 text-gray-600 font-medium border-b border-gray-200">
                <tr>
                  <th class="px-6 py-4">Sản phẩm</th>
                  <th class="px-6 py-4 text-center">ĐVT</th>
                  <th class="px-6 py-4 w-32">Số lượng</th>
                  <th class="px-6 py-4 w-40">Giá nhập</th>
                  <th class="px-6 py-4 w-40">Giá bán</th>
                  <th class="px-6 py-4 w-10"></th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-100">
                <tr v-if="importItems.length === 0">
                  <td colspan="6" class="px-6 py-12 text-center text-gray-400">
                    <div class="flex flex-col items-center justify-center gap-2">
                      <InboxIcon class="w-10 h-10 text-gray-300" />
                      <p>Chưa có sản phẩm nào được chọn</p>
                    </div>
                  </td>
                </tr>
                <tr v-for="(item, index) in importItems" :key="index" class="hover:bg-gray-50 transition-colors group">
                  <td class="px-6 py-4">
                    <div class="flex items-center gap-3">
                      <div class="w-10 h-10 rounded-lg bg-gray-100 overflow-hidden flex-shrink-0 border border-gray-200 relative">
                        <img :src="getImageUrl(item.image)" class="w-full h-full object-cover" alt="" />
                      </div>
                      <div>
                        <p class="font-medium text-gray-900">{{ item.name }}</p>
                        <p class="text-xs text-gray-500">{{ item.brand }}</p>
                      </div>
                    </div>
                  </td>
                  <td class="px-6 py-4 text-center text-gray-500">{{ item.unit }}</td>
                  <td class="px-6 py-4">
                    <input type="number" v-model.number="item.quantity" min="1" class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none text-center transition-all" />
                  </td>
                  <td class="px-6 py-4">
                    <div class="relative">
                      <input type="number" v-model.number="item.import_price" min="0" class="w-full pl-3 pr-8 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none text-right transition-all" />
                      <span class="absolute right-3 top-2 text-gray-400 text-xs">đ</span>
                    </div>
                  </td>
                  <td class="px-6 py-4">
                    <div class="relative">
                      <input type="number" v-model.number="item.price" min="0" class="w-full pl-3 pr-8 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-green-500 outline-none text-right transition-all" />
                      <span class="absolute right-3 top-2 text-gray-400 text-xs">đ</span>
                    </div>
                  </td>
                  <td class="px-6 py-4 text-center">
                    <button @click="removeItem(index)" class="text-gray-400 hover:text-red-500 transition-colors p-1 rounded-md hover:bg-red-50">
                      <Trash2Icon class="w-4 h-4" />
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>

    <!-- Print Template (Hidden on Screen) -->
    <div id="print-area" class="hidden print:block p-8" style="background-color: white; color: black;">
      <div class="text-center mb-8">
        <h1 class="text-2xl font-bold uppercase mb-2" style="color: #1a202c;">Phiếu Nhập Hàng</h1>
        <p class="text-sm" style="color: #718096;">Ngày tạo: {{ new Date().toLocaleDateString('vi-VN') }}</p>
      </div>

      <div class="mb-8 grid grid-cols-2 gap-8">
        <div>
          <h3 class="font-bold mb-2" style="color: #2d3748;">Nhà cung cấp:</h3>
          <div v-if="selectedSupplier" class="text-sm space-y-1" style="color: #4a5568;">
            <p><span class="font-medium">Tên:</span> {{ selectedSupplier.name }}</p>
            <p><span class="font-medium">Địa chỉ:</span> {{ selectedSupplier.address }}</p>
            <p><span class="font-medium">SĐT:</span> {{ selectedSupplier.phone }}</p>
          </div>
          <div v-else class="text-sm italic" style="color: #a0aec0;">Chưa chọn nhà cung cấp</div>
        </div>
        <div class="text-right">
          <h3 class="font-bold mb-2" style="color: #2d3748;">Thông tin phiếu:</h3>
          <div class="text-sm space-y-1" style="color: #4a5568;">
            <p><span class="font-medium">Mã phiếu:</span> #{{ Date.now().toString().slice(-6) }}</p>
            <p><span class="font-medium">Người lập:</span> {{ creatorName }}</p>
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
          <tr v-for="(item, index) in importItems" :key="index">
            <td class="border px-4 py-2 text-center" style="border-color: #e2e8f0;">{{ index + 1 }}</td>
            <td class="border px-4 py-2" style="border-color: #e2e8f0;">
              <p class="font-medium" style="color: #1a202c;">{{ item.name }}</p>
              <p class="text-xs" style="color: #718096;">{{ item.brand }}</p>
            </td>
            <td class="border px-4 py-2 text-center" style="border-color: #e2e8f0;">{{ item.unit }}</td>
            <td class="border px-4 py-2 text-right" style="border-color: #e2e8f0;">{{ item.quantity }}</td>
            <td class="border px-4 py-2 text-right" style="border-color: #e2e8f0;">{{ formatCurrency(item.import_price) }}</td>
            <td class="border px-4 py-2 text-right" style="border-color: #e2e8f0;">{{ formatCurrency(item.import_price * item.quantity) }}</td>
          </tr>
        </tbody>
        <tfoot>
          <tr class="font-bold" style="background-color: #f7fafc;">
            <td colspan="5" class="border px-4 py-2 text-right" style="border-color: #e2e8f0;">Tổng cộng:</td>
            <td class="border px-4 py-2 text-right" style="border-color: #e2e8f0;">{{ formatCurrency(totalImportPrice) }}</td>
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

    <!-- Add Supplier Modal -->
    <Transition name="modal">
      <div v-if="isSupplierModalOpen" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4">
        <div class="bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden transform transition-all">
          <div class="bg-gray-50 px-6 py-4 border-b border-gray-100 flex justify-between items-center">
            <h3 class="text-lg font-semibold text-gray-800">Thêm nhà cung cấp mới</h3>
            <button @click="closeSupplierModal" class="text-gray-400 hover:text-gray-600 transition-colors">
              <XIcon class="w-5 h-5" />
            </button>
          </div>
          <div class="p-6 space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Tên nhà cung cấp <span class="text-red-500">*</span></label>
              <input v-model="newSupplier.name" type="text" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all" placeholder="Nhập tên..." />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Địa chỉ <span class="text-red-500">*</span></label>
              <input v-model="newSupplier.address" type="text" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all" placeholder="Nhập địa chỉ..." />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Số điện thoại</label>
              <input v-model="newSupplier.phone" type="tel" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all" placeholder="Nhập số điện thoại..." />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Email</label>
              <input v-model="newSupplier.email" type="email" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all" placeholder="Nhập email..." />
            </div>
          </div>
          <div class="bg-gray-50 px-6 py-4 flex justify-end gap-3">
            <button @click="closeSupplierModal" class="px-4 py-2 text-gray-600 hover:bg-gray-200 rounded-lg transition-colors font-medium">Hủy</button>
            <button @click="addNewSupplier" class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium shadow-sm">Thêm mới</button>
          </div>
        </div>
      </div>
    </Transition>

    <!-- Add Product Modal -->
    <Transition name="modal">
      <div v-if="isProductModalOpen" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4">
        <div class="bg-white rounded-2xl shadow-2xl w-full max-w-lg overflow-hidden transform transition-all">
          <div class="bg-gray-50 px-6 py-4 border-b border-gray-100 flex justify-between items-center">
            <h3 class="text-lg font-semibold text-gray-800">Thêm sản phẩm mới</h3>
            <button @click="closeProductModal" class="text-gray-400 hover:text-gray-600 transition-colors">
              <XIcon class="w-5 h-5" />
            </button>
          </div>
          <div class="p-6 space-y-4 max-h-[70vh] overflow-y-auto custom-scrollbar">
            <div class="grid grid-cols-2 gap-4">
              <div class="col-span-2">
                <label class="block text-sm font-medium text-gray-700 mb-1">Tên sản phẩm <span class="text-red-500">*</span></label>
                <input v-model="newProduct.name" type="text" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none transition-all" placeholder="Nhập tên sản phẩm..." />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Loại sản phẩm</label>
                <select v-model="newProduct.type" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none transition-all bg-white">
                  <option value="" disabled>Chọn loại</option>
                  <option v-for="type in productTypes" :key="type" :value="type">{{ type }}</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Đơn vị tính</label>
                <select v-model="newProduct.unit" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none transition-all bg-white">
                  <option v-for="u in unitOptions" :key="u" :value="u">{{ u }}</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Thương hiệu</label>
                <input v-model="newProduct.brand" type="text" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none transition-all" placeholder="Nhập thương hiệu..." />
              </div>
              <div class="col-span-2">
                <label class="block text-sm font-medium text-gray-700 mb-1">Ảnh sản phẩm</label>
                <div class="flex items-center gap-4">
                  <div class="w-20 h-20 rounded-lg border border-gray-300 bg-gray-50 flex items-center justify-center overflow-hidden">
                    <img :src="getImageUrl(newProduct.image)" class="w-full h-full object-cover" alt="Preview" />
                  </div>
                  <div class="flex-1">
                    <input 
                      type="file" 
                      accept="image/*"
                      @change="handleImageUpload"
                      class="block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-indigo-50 file:text-indigo-700 hover:file:bg-indigo-100 transition-colors"
                    />
                    <p class="mt-1 text-xs text-gray-500">Chọn ảnh từ máy tính của bạn.</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="bg-gray-50 px-6 py-4 flex justify-end gap-3">
            <button @click="closeProductModal" class="px-4 py-2 text-gray-600 hover:bg-gray-200 rounded-lg transition-colors font-medium">Hủy</button>
            <button @click="addNewProduct" class="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors font-medium shadow-sm">Thêm mới</button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useInventoryStore } from '@/stores/useInventoryStore';
import { useAuthStore } from '@/stores/useAuthStore';
import { 
  PrinterIcon, 
  DownloadIcon, 
  SaveIcon, 
  TruckIcon, 
  PlusIcon, 
  PackageIcon, 
  CalculatorIcon, 
  Trash2Icon,
  InboxIcon,
  ImageIcon,
  XIcon
} from 'lucide-vue-next';
import { useToast } from "vue-toastification";
import VueMultiselect from 'vue-multiselect';
import 'vue-multiselect/dist/vue-multiselect.css';

const inventoryStore = useInventoryStore();
const authStore = useAuthStore();
const toast = useToast();
const router = useRouter();

// Load html2pdf from CDN
onMounted(() => {
  // Load data immediately
  inventoryStore.fetchInitialData();

  if (!window.html2pdf) {
    const script = document.createElement('script');
    script.src = 'https://cdnjs.cloudflare.com/ajax/libs/html2pdf.js/0.10.1/html2pdf.bundle.min.js';
    script.async = true;
    script.onload = () => console.log("html2pdf loaded");
    script.onerror = () => {
      console.error("Failed to load html2pdf");
    };
    document.body.appendChild(script);
  }
});

const productTypes = ['Thực phẩm bổ sung', 'Phụ kiện', 'Quần áo', 'Thiết bị', 'Khác'];
const unitOptions = ['Cái', 'Hộp', 'Đôi', 'Bộ', 'Gói', 'Khác'];

// --- State ---
const selectedSupplier = ref(null);
const tempSelectedProduct = ref(null);
const importItems = ref([]);
const customSuppliers = ref([]); // Store locally created suppliers

const isSupplierModalOpen = ref(false);
const isProductModalOpen = ref(false);

const newSupplier = ref({ name: '', address: '', phone: '', email: '' });
const newProduct = ref({ 
  brand: '', 
  name: '', 
  price: 0, 
  quantity: 0, 
  type: '', 
  import_price: 0, 
  status: false, // Default Active 
  image: '', 
  import_date: new Date().toISOString(), 
  unit: 'Cái' 
});

const imagePreview = ref(null);
const imageFile = ref(null);

// --- Computed ---
const totalQuantity = computed(() => importItems.value.reduce((sum, item) => sum + (item.quantity || 0), 0));
const totalImportPrice = computed(() => importItems.value.reduce((sum, item) => sum + ((item.import_price || 0) * (item.quantity || 0)), 0));
const totalSellingPrice = computed(() => importItems.value.reduce((sum, item) => sum + ((item.price || 0) * (item.quantity || 0)), 0));
const creatorName = computed(() => authStore.user?.full_name || authStore.user?.name || 'Admin');
const supplierOptions = computed(() => [...inventoryStore.suppliers, ...customSuppliers.value]);

// --- Helper ---
const getImageUrl = (imagePath) => {
  if (!imagePath) return 'http://localhost:8080/image/defaults/no-image.png';
  if (imagePath.startsWith('http')) return imagePath;
  if (imagePath.startsWith('data:')) return imagePath;
  
  const cleanPath = imagePath.replace(/\\/g, '/');
  return `http://localhost:8080/${cleanPath}`;
};

// --- Actions ---
const openSupplierModal = () => {
  newSupplier.value = { name: '', address: '', phone: '', email: '' };
  isSupplierModalOpen.value = true;
};
const closeSupplierModal = () => isSupplierModalOpen.value = false;

const addNewSupplier = async () => {
  if (!newSupplier.value.name || !newSupplier.value.address) {
    toast.error("Vui lòng điền đầy đủ các trường bắt buộc!");
    return;
  }
  
  // Save locally first
  const localSupplier = { 
    ...newSupplier.value, 
    id: `NEW_${Date.now()}`, 
    isNew: true 
  };
  customSuppliers.value.push(localSupplier);
  selectedSupplier.value = localSupplier;
  
  closeSupplierModal();
  toast.success("Đã thêm nhà cung cấp (Lưu ý: Chỉ lưu vào hệ thống khi bạn Lưu phiếu)");
};

const openProductModal = () => {
  newProduct.value = { 
    brand: '', 
    name: '', 
    price: 0, 
    quantity: 0, 
    type: '', 
    import_price: 0, 
    status: false, 
    image: '', 
    import_date: new Date().toISOString(), 
    unit: 'Cái' 
  };
  imagePreview.value = null;
  imageFile.value = null;
  isProductModalOpen.value = true;
};
const closeProductModal = () => isProductModalOpen.value = false;

const handleImageUpload = (event) => {
  const file = event.target.files[0];
  if (file) {
    imageFile.value = file;
    const reader = new FileReader();
    reader.onload = (e) => {
      newProduct.value.image = e.target.result; // preview
    };
    reader.readAsDataURL(file);
  }
};

const addNewProduct = async () => {
  if (!newProduct.value.name) {
    toast.error("Vui lòng nhập tên sản phẩm!");
    return;
  }
  
  // Create temporary product object
  const productToAdd = { 
    ...newProduct.value,
    id: `NEW_${Date.now()}`,
    isNew: true,
    imageFile: imageFile.value // Keep file for later upload
  };
  
  // Add directly to import list
  addProductToImport(productToAdd);
  
  closeProductModal();
  toast.success("Đã thêm vào danh sách (Sản phẩm sẽ được tạo khi Lưu phiếu)");
};

const addProductToImport = (product) => {
  if (!product) return;
  
  const existingItem = importItems.value.find(item => item.id === product.id);
  if (existingItem) {
    toast.info("Sản phẩm này đã có trong danh sách!");
  } else {
    importItems.value.push({
      ...product,
      quantity: 1,
      import_price: product.import_price || 0,
      price: product.price || 0
    });
  }
  setTimeout(() => {
    tempSelectedProduct.value = null;
  }, 100);
};

const removeItem = (index) => {
  importItems.value.splice(index, 1);
};


const saveImport = async () => {
  if (!selectedSupplier.value) {
    toast.error("Vui lòng chọn nhà cung cấp!");
    return;
  }
  if (importItems.value.length === 0) {
    toast.error("Vui lòng chọn ít nhất 1 sản phẩm!");
    return;
  }
  
  for (const item of importItems.value) {
    if (item.quantity <= 0) {
      toast.error(`Sản phẩm ${item.name} có số lượng không hợp lệ!`);
      return;
    }
  }

  try {
    // 1. Handle New Supplier
    let finalProviderId = selectedSupplier.value.id;
    if (selectedSupplier.value.isNew) {
      try {
        const { isNew, id, ...supplierData } = selectedSupplier.value;
        const savedSupplier = await inventoryStore.addSupplier(supplierData);
        finalProviderId = savedSupplier.id;
      } catch (err) {
        toast.error("Lỗi khi lưu nhà cung cấp mới!");
        throw err;
      }
    }

    // 2. Handle New Products and Prepare Items
    const finalItems = [];
    for (const item of importItems.value) {
      let finalProductId = item.id;
      
      if (item.isNew) {
        try {
          // Prepare product payload
          const { isNew, id, imageFile, ...productData } = item;
          // Ensure we pass the image file correctly if it exists
          const savedProduct = await inventoryStore.addProduct({
            ...productData,
            imageFile: imageFile
          });
          finalProductId = savedProduct.id;
        } catch (err) {
          toast.error(`Lỗi khi tạo sản phẩm mới: ${item.name}`);
          throw err;
        }
      }
      
      finalItems.push({
        id: finalProductId,
        quantity: item.quantity,
        import_price: item.import_price,
        // Send selling price too
        price: item.price
      });
    }

    const receipt = {
      providerId: finalProviderId,
      items: finalItems
    };

    await inventoryStore.addImportReceipt(receipt);
    toast.success("Lưu phiếu nhập hàng thành công!");
    router.push('/manager/product/history');
  } catch (e) {
    console.error(e);
  }
};

const formatCurrency = (value) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

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
    filename: `phieu-nhap-${Date.now()}.pdf`,
    image: { type: 'jpeg', quality: 0.98 },
    html2canvas: { scale: 2, useCORS: true, logging: true },
    jsPDF: { unit: 'mm', format: 'a4', orientation: 'portrait' }
  };

  if (window.html2pdf) {
    try {
      await window.html2pdf().set(opt).from(element).save();
    } catch (error) {
      console.error("PDF generation error:", error);
      toast.error("Có lỗi xảy ra khi tạo PDF. Vui lòng thử lại.");
    } finally {
      element.className = originalClass;
    }
  } else {
    toast.error("Thư viện PDF chưa tải xong, vui lòng thử lại sau giây lát.");
    element.className = originalClass;
  }
};
</script>

<style scoped>
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.animate-fade-in-down {
  animation: fadeInDown 0.3s ease-out;
}

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: #f1f1f1;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 3px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}

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
