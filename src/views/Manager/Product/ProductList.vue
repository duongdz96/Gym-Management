<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex justify-between items-center bg-white p-6 rounded-xl shadow-sm border border-gray-100">
      <div>
        <h1 class="text-2xl font-bold text-gray-800">Quản lý sản phẩm</h1>
        <p class="text-gray-500 mt-1">Danh sách tất cả sản phẩm trong kho</p>
      </div>
      <div class="flex flex-col md:flex-row gap-3 items-center">
        <!-- Search -->
        <div class="relative">
          <input 
            v-model="searchQuery"
            type="text" 
            placeholder="Tìm sản phẩm..." 
            class="pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500 outline-none w-full md:w-56 text-sm"
          />
          <SearchIcon class="w-4 h-4 text-gray-400 absolute left-3 top-1/2 -translate-y-1/2" />
        </div>

        <!-- Filters -->
        <select v-model="filterType" class="px-3 py-2 border border-gray-300 rounded-lg outline-none text-sm bg-white focus:border-emerald-500">
          <option value="">Tất cả loại</option>
          <option v-for="t in productTypes" :key="t" :value="t">{{ t }}</option>
        </select>

        <select v-model="filterStatus" class="px-3 py-2 border border-gray-300 rounded-lg outline-none text-sm bg-white focus:border-emerald-500">
          <option value="all">Tất cả trạng thái</option>
          <option value="active">Đang kinh doanh</option>
          <option value="inactive">Ngừng kinh doanh</option>
        </select>

         <select v-model="sortBy" class="px-3 py-2 border border-gray-300 rounded-lg outline-none text-sm bg-white focus:border-emerald-500">
          <option value="newest">Mới nhất</option>
          <option value="priceAsc">Giá tăng dần</option>
          <option value="priceDesc">Giá giảm dần</option>
        </select>

        <button @click="openAddModal" class="flex items-center gap-2 px-4 py-2 bg-emerald-600 text-white rounded-lg hover:bg-emerald-700 transition-colors shadow-sm font-medium whitespace-nowrap">
          <PlusIcon class="w-4 h-4" />
          Thêm mới
        </button>
      </div>
    </div>

    <!-- Products Table -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full text-left text-sm">
          <thead class="bg-gray-50 text-gray-600 font-medium border-b border-gray-200">
            <tr>
              <th class="px-6 py-4 w-16 text-center">ID</th>
              <th class="px-6 py-4">Sản phẩm</th>
              <th class="px-6 py-4">Phân loại</th>
              <th class="px-6 py-4 text-center">ĐVT</th>
              <th class="px-6 py-4 text-right">Tồn kho</th>
              <th class="px-6 py-4 text-right">Giá bán</th>
              <th class="px-6 py-4 text-center">Trạng thái</th>
              <th class="px-6 py-4 text-center w-24">Thao tác</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-if="filteredProducts.length === 0">
              <td colspan="7" class="px-6 py-12 text-center text-gray-400">
                Chưa có sản phẩm nào.
              </td>
            </tr>
            <tr v-for="product in filteredProducts" :key="product.id" class="hover:bg-gray-50 transition-colors">
              <td class="px-6 py-4 text-center text-gray-500">#{{ product.id }}</td>
              <td class="px-6 py-4">
                <div class="flex items-center gap-3">
                  <div class="w-10 h-10 rounded-lg bg-gray-100 overflow-hidden flex-shrink-0 border border-gray-200 relative">
                    <img :src="getImageUrl(product.image)" class="w-full h-full object-cover" alt="" />

                  </div>
                  <div>
                    <p class="font-medium text-gray-900 truncate">{{ product.name }}</p>
                    <p class="text-xs text-gray-500">{{ product.brand }}</p>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4">
                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-emerald-50 text-emerald-800">
                  {{ product.type }}
                </span>
              </td>
              <td class="px-6 py-4 text-center text-gray-600">{{ product.unit }}</td>
              <td class="px-6 py-4 text-right">
                <span :class="product.quantity > 5 ? 'text-gray-900' : 'text-red-500 font-bold'">
                  {{ product.quantity }}
                </span>
              </td>
              <td class="px-6 py-4 text-right font-medium text-gray-900">{{ formatCurrency(product.price) }}</td>
              <td class="px-6 py-4 text-center">
                <span :class="!product.status ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'" class="px-2 py-1 rounded-full text-xs font-medium">
                  {{ !product.status ? 'Active' : 'Inactive' }}
                </span>
              </td>
              <td class="px-6 py-4 text-center">
                <div class="flex items-center justify-center gap-2">
                  <button @click="openEditModal(product)" class="p-1.5 text-emerald-600 hover:bg-emerald-50 rounded-md transition-colors" title="Chỉnh sửa">
                    <Edit2Icon class="w-4 h-4" />
                  </button>
                  <button @click="confirmDelete(product)" class="p-1.5 text-red-600 hover:bg-red-50 rounded-md transition-colors" title="Xóa">
                    <Trash2Icon class="w-4 h-4" />
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal (Add/Edit) -->
    <Transition name="modal">
      <div v-if="isModalOpen" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4">
        <div class="bg-white rounded-2xl shadow-2xl w-full max-w-lg overflow-hidden transform transition-all">
          <div class="bg-gray-50 px-6 py-4 border-b border-gray-100 flex justify-between items-center">
            <h3 class="text-lg font-semibold text-gray-800">{{ isEditing ? 'Cập nhật sản phẩm' : 'Thêm sản phẩm mới' }}</h3>
            <button @click="closeModal" class="text-gray-400 hover:text-gray-600 transition-colors">
              <XIcon class="w-5 h-5" />
            </button>
          </div>
          <div class="p-6 space-y-4 max-h-[70vh] overflow-y-auto custom-scrollbar">
             <div class="grid grid-cols-2 gap-4">
              <div class="col-span-2">
                <label class="block text-sm font-medium text-gray-700 mb-1">Tên sản phẩm <span class="text-red-500">*</span></label>
                <input v-model="form.name" type="text" maxlength="50" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500 outline-none transition-all" />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Loại sản phẩm</label>
                <select v-model="form.type" :disabled="isEditingMembership" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500 outline-none transition-all bg-white disabled:bg-gray-100 disabled:cursor-not-allowed">
                  <option value="" disabled>Chọn loại</option>
                  <option v-for="type in availableProductTypes" :key="type" :value="type">{{ type }}</option>
                </select>
                <p v-if="isEditingMembership" class="mt-1 text-xs text-gray-500 italic">
                  Không thể thay đổi loại sản phẩm Membership
                </p>
              </div>
              <div v-if="form.type !== 'Membership'">
                <label class="block text-sm font-medium text-gray-700 mb-1">Đơn vị tính</label>
                <select v-model="form.unit" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500 outline-none transition-all bg-white">
                  <option v-for="u in unitOptions" :key="u" :value="u">{{ u }}</option>
                </select>
              </div>
              <div v-if="form.type !== 'Membership'">
                <label class="block text-sm font-medium text-gray-700 mb-1">Thương hiệu</label>
                <input v-model="form.brand" type="text" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500 outline-none transition-all" />
              </div>
              <div v-if="form.type === 'Membership'" class="col-span-2">
                <div class="p-3 bg-yellow-50 border border-yellow-200 rounded-lg text-sm text-yellow-700">
                  <span class="font-semibold">Lưu ý:</span> Membership là gói thành viên, không cần nhập thương hiệu và đơn vị tính.
                </div>
              </div>
               <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Giá bán (VND)</label>
                <input v-model.number="form.price" type="number" min="0" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500 outline-none transition-all" />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Trạng thái</label>
                <select v-model="form.status" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500 outline-none transition-all bg-white">
                  <option :value="false">Active</option>
                  <option :value="true">Inactive</option>
                </select>
              </div>
              <div class="col-span-2">
                <label class="block text-sm font-medium text-gray-700 mb-1">Ảnh sản phẩm</label>
                 <div class="flex items-center gap-4">
                  <div class="w-20 h-20 rounded-lg border border-gray-300 bg-gray-50 flex items-center justify-center overflow-hidden">
                    <img :src="getImageUrl(form.image)" class="w-full h-full object-cover" alt="Preview" />

                  </div>
                  <div class="flex-1">
                    <input 
                      type="file" 
                      accept="image/*"
                      @change="handleImageUpload"
                      class="block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-emerald-50 file:text-emerald-700 hover:file:bg-emerald-100 transition-colors"
                    />
                    <p class="mt-1 text-xs text-gray-500">Chọn ảnh từ máy tính của bạn.</p>
                  </div>
                </div>
              </div>
             </div>
          </div>
          <div class="bg-gray-50 px-6 py-4 flex justify-end gap-3">
            <button @click="closeModal" class="px-4 py-2 text-gray-600 hover:bg-gray-200 rounded-lg transition-colors font-medium">Hủy</button>
            <button @click="handleSubmit" class="px-4 py-2 bg-emerald-600 text-white rounded-lg hover:bg-emerald-700 transition-colors font-medium shadow-sm">
              {{ isEditing ? 'Cập nhật' : 'Thêm mới' }}
            </button>
          </div>
        </div>
      </div>
    </Transition>
    <!-- Delete Confirmation Modal -->
    <Transition name="modal">
      <div v-if="isDeleteModalOpen" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4">
        <div class="bg-white rounded-2xl shadow-xl w-full max-w-sm overflow-hidden transform transition-all">
          <div class="p-6 text-center">
            <div class="mx-auto flex items-center justify-center h-12 w-12 rounded-full bg-red-100 mb-4">
              <Trash2Icon class="h-6 w-6 text-red-600" />
            </div>
            <h3 class="text-lg font-medium text-gray-900 mb-2">{{ productToDelete?.type === 'Membership' ? 'Xác nhận vô hiệu hóa' : 'Xác nhận xóa' }}</h3>
            <p class="text-sm text-gray-500">
              <span v-if="productToDelete?.type === 'Membership'">
                Bạn có chắc chắn muốn vô hiệu hóa sản phẩm Membership <span class="font-bold text-gray-800">"{{ productToDelete?.name }}"</span> không? Sản phẩm sẽ được đánh dấu là Inactive.
              </span>
              <span v-else>
                Bạn có chắc chắn muốn xóa sản phẩm <span class="font-bold text-gray-800">"{{ productToDelete?.name }}"</span> không? Hành động này không thể hoàn tác.
              </span>
            </p>
          </div>
          <div class="bg-gray-50 px-6 py-4 flex justify-center gap-3">
            <button @click="closeDeleteModal" class="px-4 py-2 bg-white text-gray-700 border border-gray-300 rounded-lg hover:bg-gray-50 font-medium transition-colors">Hủy bỏ</button>
            <button @click="handleDelete" class="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 font-medium transition-colors shadow-sm">
              {{ productToDelete?.type === 'Membership' ? 'Vô hiệu hóa' : 'Xóa bỏ' }}
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { useInventoryStore } from '@/stores/useInventoryStore';
import { PlusIcon, Edit2Icon, Trash2Icon, XIcon, ImageIcon, SearchIcon } from 'lucide-vue-next';
import { useToast } from "vue-toastification";

const inventoryStore = useInventoryStore();
const toast = useToast();

onMounted(() => {
    inventoryStore.fetchProducts();
});

const isModalOpen = ref(false);
const isEditing = ref(false);
const isDeleteModalOpen = ref(false);
const productToDelete = ref(null);

// Filter States
const searchQuery = ref("");
const filterType = ref("");
const filterStatus = ref("all"); // 'all', 'active', 'inactive'
const sortBy = ref("newest"); // 'newest', 'priceAsc', 'priceDesc'

const filteredProducts = computed(() => {
  let result = [...inventoryStore.products];

  // 1. Filter by Name/Brand
  if (searchQuery.value) {
    const lowerQuery = searchQuery.value.toLowerCase();
    result = result.filter(p => 
      p.name.toLowerCase().includes(lowerQuery) || 
      (p.brand && p.brand.toLowerCase().includes(lowerQuery))
    );
  }

  // 2. Filter by Type
  if (filterType.value) {
    result = result.filter(p => p.type === filterType.value);
  }

  // 3. Filter by Status
  if (filterStatus.value !== 'all') {
    const statusBool = filterStatus.value === 'inactive'; // true = inactive, false = active
    result = result.filter(p => p.status === statusBool);
  }

  // 4. Sorting
  if (sortBy.value === 'priceAsc') {
    result.sort((a, b) => (a.price || 0) - (b.price || 0));
  } else if (sortBy.value === 'priceDesc') {
    result.sort((a, b) => (b.price || 0) - (a.price || 0));
  } else {
    // Default newest (by ID desc)
    result.sort((a, b) => b.id - a.id);
  }

  return result;
});

const productTypes = ['Thực phẩm bổ sung', 'Phụ kiện', 'Quần áo', 'Thiết bị', 'PT', 'Membership', 'Khác'];
const unitOptions = ['Cái', 'Hộp', 'Đôi', 'Bộ', 'Gói', 'Khác'];

// Computed: Kiểm tra có phải đang edit Membership không
const isEditingMembership = computed(() => {
  return isEditing.value && form.type === 'Membership';
});

// Computed: Danh sách loại sản phẩm có thể chọn
const availableProductTypes = computed(() => {
  if (isEditingMembership.value) {
    // Khi edit Membership, chỉ hiển thị Membership
    return ['Membership'];
  }
  // Khi thêm mới hoặc edit sản phẩm khác: loại bỏ Membership
  return productTypes.filter(type => type !== 'Membership');
});

const form = reactive({
  id: null,
  brand: '',
  name: '',
  price: 0,
  quantity: 0,
  type: '',
  import_price: 0,
  status: false, // Default false (Active)
  image: '',
  import_date: null,
  unit: 'Cái'
});

const openAddModal = () => {
  isEditing.value = false;
  resetForm();
  form.status = false;
  form.import_date = new Date().toISOString();
  isModalOpen.value = true;
};

const openEditModal = (product) => {
  isEditing.value = true;
  Object.assign(form, product);
  isModalOpen.value = true;
};

const closeModal = () => {
  isModalOpen.value = false;
  resetForm();
};

const imagePreview = ref(null);
const imageFile = ref(null);

const handleImageUpload = (event) => {
  const file = event.target.files[0];
  if (file) {
    imageFile.value = file;
    const reader = new FileReader();
    reader.onload = (e) => {
      imagePreview.value = e.target.result;
      form.image = e.target.result; // For local preview
    };
    reader.readAsDataURL(file);
  }
};

const triggerFileInput = () => {
  document.getElementById('fileInput').click();
};

const removeImage = () => {
  imagePreview.value = null;
  imageFile.value = null;
  form.image = '';
};

const getImageUrl = (imagePath) => {
  if (!imagePath) return 'http://localhost:8080/image/defaults/no-image.png';
  if (imagePath.startsWith('http')) return imagePath;
  // If it's a data URL (base64) from local preview, return as is
  if (imagePath.startsWith('data:')) return imagePath;
  
  // Clean backslashes if present (windows paths)
  const cleanPath = imagePath.replace(/\\/g, '/');
  return `http://localhost:8080/${cleanPath}`;
};

const handleSubmit = async () => {
  if (!form.name) {
    toast.error("Vui lòng nhập tên sản phẩm!");
    return;
  }

  try {
      if (isEditing.value) {
        // Khi edit Membership, giữ nguyên type (không cho đổi)
        const updateData = { ...form };
        if (isEditingMembership.value) {
          // Đảm bảo type không bị thay đổi
          const originalProduct = inventoryStore.products.find(p => p.id === form.id);
          if (originalProduct) {
            updateData.type = originalProduct.type;
          }
        }
        await inventoryStore.updateProduct(updateData);
        toast.success("Cập nhật thành công!");
      } else {
        await inventoryStore.addProduct({ ...form, imageFile: imageFile.value });
        toast.success("Thêm mới thành công!");
      }
      closeModal();
      inventoryStore.fetchProducts();
  } catch (error) {
     console.error(error);
  }
};

const confirmDelete = (product) => {
  productToDelete.value = product;
  isDeleteModalOpen.value = true;
};

const closeDeleteModal = () => {
  isDeleteModalOpen.value = false;
  productToDelete.value = null;
};

const handleDelete = async () => {
  if (!productToDelete.value) return;

  try {
    // Nếu là Membership, chỉ update status (soft delete)
    if (productToDelete.value.type === 'Membership') {
      await inventoryStore.updateProduct({
        ...productToDelete.value,
        status: true // Set inactive (soft delete)
      });
      toast.success("Đã vô hiệu hóa sản phẩm Membership!");
    } else {
      // Các loại khác xóa bình thường
      await inventoryStore.deleteProduct(productToDelete.value.id);
      toast.success("Xóa sản phẩm thành công!");
    }
    closeDeleteModal();
    inventoryStore.fetchProducts();
  } catch (error) {
    toast.error("Có lỗi xảy ra khi xóa sản phẩm!");
  }
};

const resetForm = () => {
  form.id = null;
  form.brand = '';
  form.name = '';
  form.price = 0;
  form.quantity = 0;
  form.type = '';
  form.import_price = 0;
  form.status = false;
  form.image = '';
  form.import_date = null;
  form.unit = 'Cái';
  imagePreview.value = null;
  imageFile.value = null;
};

const formatCurrency = (value) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
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
</style>
