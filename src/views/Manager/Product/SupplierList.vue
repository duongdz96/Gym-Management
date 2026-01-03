<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex flex-col md:flex-row justify-between items-center gap-4 bg-white p-6 rounded-xl shadow-sm border border-gray-100">
      <div class="shrink-0">
        <h1 class="text-2xl font-bold text-gray-800">Quản lý nhà cung cấp</h1>
        <p class="text-gray-500 mt-1">Danh sách tất cả các nhà cung cấp của hệ thống</p>
      </div>
      
      <div class="relative flex-1 max-w-md mx-6">
        <input 
          v-model="searchQuery"
          type="text" 
          placeholder="Tìm kiếm nhà cung cấp..." 
          class="pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none w-full text-sm"
        />
        <SearchIcon class="w-4 h-4 text-gray-400 absolute left-3 top-1/2 -translate-y-1/2" />
      </div>

      <div class="shrink-0">
        <button @click="openAddModal" class="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors shadow-sm font-medium">
          <PlusIcon class="w-4 h-4" />
          Thêm mới
        </button>
      </div>
    </div>

    <!-- Suppliers Table -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full text-left text-sm">
          <thead class="bg-gray-50 text-gray-600 font-medium border-b border-gray-200">
            <tr>
              <th class="px-6 py-4 w-16 text-center">ID</th>
              <th class="px-6 py-4">Tên nhà cung cấp</th>
              <th class="px-6 py-4">Địa chỉ</th>
              <th class="px-6 py-4">Số điện thoại</th>
              <th class="px-6 py-4">Email</th>
              <th class="px-6 py-4 text-center w-24">Thao tác</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-if="filteredSuppliers.length === 0">
              <td colspan="6" class="px-6 py-12 text-center text-gray-400">
                Chưa có nhà cung cấp nào.
              </td>
            </tr>
            <tr v-for="supplier in filteredSuppliers" :key="supplier.id" class="hover:bg-gray-50 transition-colors">
              <td class="px-6 py-4 text-center text-gray-500">#{{ supplier.id }}</td>
              <td class="px-6 py-4 font-medium text-gray-900">{{ supplier.name }}</td>
              <td class="px-6 py-4 text-gray-600">{{ supplier.address }}</td>
              <td class="px-6 py-4 text-gray-600">{{ supplier.phone }}</td>
              <td class="px-6 py-4 text-gray-600">{{ supplier.email || '-' }}</td>
              <td class="px-6 py-4 text-center">
                <div class="flex items-center justify-center gap-2">
                  <button @click="openEditModal(supplier)" class="p-1.5 text-blue-600 hover:bg-blue-50 rounded-md transition-colors" title="Chỉnh sửa">
                    <Edit2Icon class="w-4 h-4" />
                  </button>
                  <button @click="confirmDelete(supplier)" class="p-1.5 text-red-600 hover:bg-red-50 rounded-md transition-colors" title="Xóa">
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
        <div class="bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden transform transition-all">
          <div class="bg-gray-50 px-6 py-4 border-b border-gray-100 flex justify-between items-center">
            <h3 class="text-lg font-semibold text-gray-800">{{ isEditing ? 'Cập nhật nhà cung cấp' : 'Thêm nhà cung cấp mới' }}</h3>
            <button @click="closeModal" class="text-gray-400 hover:text-gray-600 transition-colors">
              <XIcon class="w-5 h-5" />
            </button>
          </div>
          <div class="p-6 space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Tên nhà cung cấp <span class="text-red-500">*</span></label>
              <input v-model="form.name" type="text" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all" />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Địa chỉ <span class="text-red-500">*</span></label>
              <input v-model="form.address" type="text" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all" />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Số điện thoại </label>
              <input v-model="form.phone" type="tel" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all" />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Email</label>
              <input v-model="form.email" type="email" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all" />
            </div>
          </div>
          <div class="bg-gray-50 px-6 py-4 flex justify-end gap-3">
            <button @click="closeModal" class="px-4 py-2 text-gray-600 hover:bg-gray-200 rounded-lg transition-colors font-medium">Hủy</button>
            <button @click="handleSubmit" class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium shadow-sm">
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
            <h3 class="text-lg font-medium text-gray-900 mb-2">Xác nhận xóa</h3>
            <p class="text-sm text-gray-500">Bạn có chắc chắn muốn xóa nhà cung cấp <span class="font-bold text-gray-800">"{{ supplierToDelete?.name }}"</span> không? Hành động này không thể hoàn tác.</p>
          </div>
          <div class="bg-gray-50 px-6 py-4 flex justify-center gap-3">
            <button @click="closeDeleteModal" class="px-4 py-2 bg-white text-gray-700 border border-gray-300 rounded-lg hover:bg-gray-50 font-medium transition-colors">Hủy bỏ</button>
            <button @click="handleDelete" class="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 font-medium transition-colors shadow-sm">Xóa bỏ</button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { useInventoryStore } from '@/stores/useInventoryStore';
import { PlusIcon, Edit2Icon, Trash2Icon, XIcon, SearchIcon } from 'lucide-vue-next';
import { useToast } from "vue-toastification";

const inventoryStore = useInventoryStore();
const toast = useToast();

onMounted(() => {
    inventoryStore.fetchSuppliers();
});

const isModalOpen = ref(false);
const isEditing = ref(false);
const isDeleteModalOpen = ref(false);
const supplierToDelete = ref(null);
const searchQuery = ref("");

const filteredSuppliers = computed(() => {
  if (!searchQuery.value) return inventoryStore.suppliers;
  const lowerQuery = searchQuery.value.toLowerCase();
  return inventoryStore.suppliers.filter(s => 
    s.name.toLowerCase().includes(lowerQuery) || 
    (s.email && s.email.toLowerCase().includes(lowerQuery)) ||
    (s.phone && s.phone.includes(lowerQuery))
  );
});

const form = reactive({
  id: null,
  name: '',
  address: '',
  phone: '',
  email: ''
});

const openAddModal = () => {
  isEditing.value = false;
  resetForm();
  isModalOpen.value = true;
};

const openEditModal = (supplier) => {
  isEditing.value = true;
  Object.assign(form, supplier); // Copy data to form
  isModalOpen.value = true;
};

const closeModal = () => {
  isModalOpen.value = false;
  resetForm();
};

const confirmDelete = (supplier) => {
  supplierToDelete.value = supplier;
  isDeleteModalOpen.value = true;
};

const closeDeleteModal = () => {
  isDeleteModalOpen.value = false;
  supplierToDelete.value = null;
};

const handleDelete = () => {
  if (supplierToDelete.value) {
    inventoryStore.deleteSupplier(supplierToDelete.value.id);
    toast.success("Xóa nhà cung cấp thành công!");
    closeDeleteModal();
  }
};

const resetForm = () => {
  form.id = null;
  form.name = '';
  form.address = '';
  form.phone = '';
  form.email = '';
};

const handleSubmit = () => {
  if (!form.name || !form.address) {
    toast.error("Vui lòng điền đầy đủ các thông tin bắt buộc!");
    return;
  }

  if (isEditing.value) {
    inventoryStore.updateSupplier({ ...form });
    toast.success("Cập nhật thành công!");
  } else {
    // Remove id for new creation (store handles it)
    const { id, ...newSupplier } = form;
    inventoryStore.addSupplier(newSupplier);
    toast.success("Thêm mới thành công!");
  }
  closeModal();
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
</style>
