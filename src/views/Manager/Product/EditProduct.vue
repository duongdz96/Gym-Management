<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/services/api'
import { useToast } from 'vue-toastification'

// Định nghĩa Type, bao gồm tất cả các trường
type ProductForm = {
  id: number | null;
  name: string;
  type: string;
  price: number;
  importPrice?: number;
  brand?: string;
  quantity?: number;
  status: boolean; // false = Active, true = Deleted
}

// --- State ---
// Dùng ref() với các giá trị mặc định cho form
const productData = ref<ProductForm>({
  id: null,
  name: '',
  type: 'Equipment', // Giá trị mặc định
  price: 0,
  importPrice: 0,
  brand: '',
  quantity: 0,
  status: false,
})

const isLoading = ref(true)
const isSaving = ref(false)
const route = useRoute()
const router = useRouter()
const toast = useToast()

// --- Tải dữ liệu ban đầu ---
onMounted(async () => {
  const productId = route.params.id
  if (!productId) {
    toast.error("Không tìm thấy ID sản phẩm.");
    router.push('/product'); // (Giả sử trang list là /product)
    return;
  }

  try {
    isLoading.value = true;
    // Dùng hàm getProductById (lấy cả sp đã xoá)
    // Giả sử backend có endpoint /product/admin/{id}
    // Hoặc sửa service getAvailableProductById
    const response = await api.get(`/product/${productId}`); 
    productData.value = response.data;
  } catch (err: any) {
    console.error("Lỗi khi tải sản phẩm:", err);
    toast.error(err.response?.data?.message || "Không thể tải sản phẩm.");
    router.push('/product');
  } finally {
    isLoading.value = false;
  }
});

// --- Hàm xử lý ---
const handleSubmit = async () => {
  if (!productData.value.id) return;

  isSaving.value = true;
  try {
    const response = await api.put(`/product/${productData.value.id}`, productData.value);
    toast.success("Cập nhật sản phẩm thành công!");
    router.push('/manager/product'); // Quay về trang danh sách

  } catch (err: any) {
    console.error("Lỗi khi cập nhật:", err);
    toast.error(err.response?.data?.message || "Cập nhật thất bại.");
  } finally {
    isSaving.value = false;
  }
}

const handleCancel = () => {
  router.back(); // Quay lại trang trước (trang danh sách)
}
</script>

<style>
.form-input {
  width: 100%;
  padding: 0.5rem 0.75rem;
  border: 1px solid #D1D5DB; /* gray-300 */
  border-radius: 0.375rem; /* rounded-md */
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05); /* shadow-sm */
  background-color: white;
}
.form-input:focus {
  outline: 2px solid transparent;
  outline-offset: 2px;
  border-color: #2563EB; /* blue-600 */
  box-shadow: 0 0 0 2px #BFDBFE; /* ring-blue-200 */
}
</style>

<template>
  <div class="p-6 max-w-4xl mx-auto">
    <div v-if="isLoading" class="text-center py-10">
      <p class="text-lg text-gray-500">Loading product data...</p>
    </div>

    <div v-else class="bg-white rounded-lg shadow-md p-6">
      <h1 class="text-2xl font-semibold mb-6">Edit Product (ID: {{ productData.id }})</h1>

      <form @submit.prevent="handleSubmit">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Product Name</label>
            <input v-model="productData.name" type="text" required class="form-input" />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Type</label>
            <select v-model="productData.type" class="form-input">
              <option value="Equipment">Equipment</option>
              <option value="Accessory">Accessory</option>
              <option value="Supplement">Supplement</option>
              <option value="clothes">Clothes</option>
              <option value="powder">Powder</option>
              <option value="drinks">Drinks</option>
              <option value="PT">PT</option>
              <option value="Membership">Membership</option>
            </select>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Price (VND)</label>
            <input v-model.number="productData.price" type="number" min="0" required class="form-input" />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Import Price (VND)</label>
            <input v-model.number="productData.importPrice" type="number" min="0" class="form-input" />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Brand</label>
            <input v-model="productData.brand" type="text" class="form-input" />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Quantity</label>
            <input v-model.number="productData.quantity" type="number" min="0" class="form-input" />
          </div>

          <div class="md:col-span-2">
            <label class="block text-sm font-medium text-gray-700 mb-1">Status</label>
            <select v-model="productData.status" class="form-input">
              <option :value="false">Active</option>
              <option :value="true">Deleted</option>
            </select>
            <p class="text-xs text-gray-500 mt-1">
              Select 'Delete' to soft delete. Select 'Active' to restore.
            </p>
          </div>

        </div>

        <div class="mt-8 flex justify-end space-x-4">
          <button 
            type="button" 
            @click="handleCancel"
            class="px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 transition"
          >
            Cancel
          </button>
          <button 
            type="submit" 
            :disabled="isSaving"
            class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50 transition"
          >
            {{ isSaving ? 'Saving...' : 'Save Changes' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>