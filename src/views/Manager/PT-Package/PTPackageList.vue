<script setup>
import { ref, onMounted } from 'vue';
import { Package, Plus, Edit, Trash2, X, CheckCircle, XCircle } from 'lucide-vue-next';
import api from '@/services/api';
import { useToast } from 'vue-toastification';
import Swal from 'sweetalert2';

const toast = useToast();

const packages = ref([]);
const isLoading = ref(true);
const showModal = ref(false);
const isEditMode = ref(false);

// Form data
const formData = ref({
  id: null,
  name: '',
  sessions: null,
  status: 'Active'
});

const loadPackages = async () => {
  isLoading.value = true;
  try {
    const res = await api.get('/ptpackage');
    packages.value = res.data;
  } catch (error) {
    console.error('Error loading PT packages:', error);
    toast.error('Không thể tải danh sách gói PT');
  } finally {
    isLoading.value = false;
  }
};

const openAddModal = () => {
  isEditMode.value = false;
  formData.value = {
    id: null,
    name: '',
    sessions: null,
    status: 'Active'
  };
  showModal.value = true;
};

const openEditModal = (pkg) => {
  isEditMode.value = true;
  formData.value = {
    id: pkg.id,
    name: pkg.name,
    sessions: pkg.sessions,
    status: pkg.status
  };
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
  formData.value = {
    id: null,
    name: '',
    sessions: null,
    status: 'Active'
  };
};

const submitForm = async () => {
  if (!formData.value.name || !formData.value.sessions) {
    toast.warning('Vui lòng điền đầy đủ thông tin');
    return;
  }

  if (formData.value.sessions <= 0) {
    toast.warning('Số buổi phải lớn hơn 0');
    return;
  }

  try {
    const payload = {
      name: formData.value.name,
      sessions: formData.value.sessions,
      status: formData.value.status
    };

    if (isEditMode.value) {
      await api.put(`/ptpackage/${formData.value.id}`, payload);
      toast.success('Cập nhật gói PT thành công!');
    } else {
      await api.post('/ptpackage', payload);
      toast.success('Thêm gói PT thành công!');
    }

    closeModal();
    await loadPackages();
  } catch (error) {
    console.error('Error submitting PT package:', error);
    toast.error(isEditMode.value ? 'Cập nhật gói PT thất bại' : 'Thêm gói PT thất bại');
  }
};

const deletePackage = async (pkg) => {
  const result = await Swal.fire({
    title: 'Xác nhận xóa?',
    text: `Bạn có chắc muốn xóa gói "${pkg.name}"?`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#EF4444',
    cancelButtonColor: '#6B7280',
    confirmButtonText: 'Xóa',
    cancelButtonText: 'Hủy'
  });
  
  if (!result.isConfirmed) {
    return;
  }

  try {
    await api.delete(`/ptpackage/${pkg.id}`);
    toast.success('Xóa gói PT thành công!');
    await loadPackages();
  } catch (error) {
    console.error('Error deleting PT package:', error);
    toast.error('Xóa gói PT thất bại');
  }
};

const toggleStatus = async (pkg) => {
  try {
    const newStatus = pkg.status === 'Active' ? 'Inactive' : 'Active';
    await api.put(`/ptpackage/${pkg.id}`, {
      ...pkg,
      status: newStatus
    });
    toast.success(`Đã ${newStatus === 'Active' ? 'kích hoạt' : 'vô hiệu hóa'} gói PT`);
    await loadPackages();
  } catch (error) {
    console.error('Error toggling status:', error);
    toast.error('Thay đổi trạng thái thất bại');
  }
};

onMounted(() => {
  loadPackages();
});
</script>

<template>
  <div class="p-6 bg-gradient-to-br from-emerald-50 via-teal-50 to-cyan-50 min-h-screen">
    <div class="max-w-7xl mx-auto">
      <!-- Header -->
      <div class="flex items-center justify-between mb-8">
        <div class="flex items-center gap-3">
          <div class="w-12 h-12 rounded-full bg-gradient-to-r from-emerald-600 to-teal-600 flex items-center justify-center shadow-lg">
            <Package class="w-6 h-6 text-white" />
          </div>
          <div>
            <h1 class="text-3xl font-bold bg-gradient-to-r from-emerald-600 via-teal-600 to-cyan-600 bg-clip-text text-transparent">
              Quản Lý Gói PT
            </h1>
            <p class="text-gray-600 text-sm">Quản lý các gói Personal Training</p>
          </div>
        </div>
        <button
          @click="openAddModal"
          class="flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-emerald-600 to-teal-600 text-white rounded-xl font-semibold hover:shadow-lg transition-all"
        >
          <Plus class="w-5 h-5" />
          Thêm Gói PT
        </button>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading" class="flex justify-center items-center h-64">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-emerald-600"></div>
      </div>

      <!-- Packages Grid -->
      <div v-else-if="packages.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div
          v-for="pkg in packages"
          :key="pkg.id"
          class="bg-white rounded-2xl shadow-md hover:shadow-xl transition-all duration-300 overflow-hidden"
        >
          <!-- Card Header -->
          <div class="p-6 bg-gradient-to-r from-emerald-600 to-teal-600 text-white">
            <div class="flex items-center justify-between mb-2">
              <h3 class="text-xl font-bold">{{ pkg.name }}</h3>
              <span
                :class="[
                  'px-3 py-1 rounded-full text-xs font-semibold',
                  pkg.status === 'Active' ? 'bg-green-500' : 'bg-gray-400'
                ]"
              >
                {{ pkg.status === 'Active' ? 'Hoạt động' : 'Tạm ngưng' }}
              </span>
            </div>
          </div>

          <!-- Card Body -->
          <div class="p-6">
            <div class="flex items-center gap-3 mb-6">
              <div class="w-16 h-16 rounded-full bg-gradient-to-br from-emerald-100 to-teal-100 flex items-center justify-center">
                <span class="text-2xl font-bold text-emerald-600">{{ pkg.sessions }}</span>
              </div>
              <div>
                <p class="text-sm text-gray-500">Số buổi tập</p>
                <p class="text-lg font-semibold text-gray-900">{{ pkg.sessions }} buổi</p>
              </div>
            </div>

            <!-- Actions -->
            <div class="flex gap-2">
              <button
                @click="toggleStatus(pkg)"
                :class="[
                  'flex-1 px-4 py-2 rounded-lg font-semibold transition-all flex items-center justify-center gap-2',
                  pkg.status === 'Active'
                    ? 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                    : 'bg-emerald-100 text-emerald-700 hover:bg-emerald-200'
                ]"
              >
                <component :is="pkg.status === 'Active' ? XCircle : CheckCircle" class="w-4 h-4" />
                {{ pkg.status === 'Active' ? 'Tắt' : 'Bật' }}
              </button>
              <button
                @click="openEditModal(pkg)"
                class="px-4 py-2 bg-teal-100 text-teal-700 rounded-lg font-semibold hover:bg-teal-200 transition-all"
              >
                <Edit class="w-4 h-4" />
              </button>
              <button
                @click="deletePackage(pkg)"
                class="px-4 py-2 bg-red-100 text-red-700 rounded-lg font-semibold hover:bg-red-200 transition-all"
              >
                <Trash2 class="w-4 h-4" />
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else class="bg-white rounded-2xl shadow-md p-12 text-center">
        <Package class="w-16 h-16 text-gray-400 mx-auto mb-4" />
        <h3 class="text-xl font-semibold text-gray-900 mb-2">Chưa có gói PT nào</h3>
        <p class="text-gray-600 mb-6">Tạo gói PT đầu tiên để bắt đầu</p>
        <button
          @click="openAddModal"
          class="px-6 py-3 bg-gradient-to-r from-emerald-600 to-teal-600 text-white rounded-xl font-semibold hover:shadow-lg transition-all"
        >
          <Plus class="w-5 h-5 inline mr-2" />
          Thêm Gói PT
        </button>
      </div>
    </div>

    <!-- Add/Edit Modal -->
    <transition
      enter-active-class="transition duration-200 ease-out"
      enter-from-class="opacity-0 scale-95"
      enter-to-class="opacity-100 scale-100"
      leave-active-class="transition duration-150 ease-in"
      leave-from-class="opacity-100 scale-100"
      leave-to-class="opacity-0 scale-95"
    >
      <div
        v-if="showModal"
        class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50"
        @click.self="closeModal"
      >
        <div class="bg-white rounded-2xl w-full max-w-lg shadow-2xl overflow-hidden">
          <!-- Modal Header -->
          <div class="p-6 bg-gradient-to-r from-emerald-600 to-teal-600 text-white flex justify-between items-center">
            <h3 class="text-xl font-bold">
              {{ isEditMode ? 'Chỉnh Sửa Gói PT' : 'Thêm Gói PT Mới' }}
            </h3>
            <button
              @click="closeModal"
              class="text-white/80 hover:text-white transition-colors bg-white/10 hover:bg-white/20 p-2 rounded-lg"
            >
              <X class="w-6 h-6" />
            </button>
          </div>

          <!-- Modal Body -->
          <form @submit.prevent="submitForm" class="p-6 space-y-4">
            <!-- Package Name -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">
                Tên gói PT <span class="text-red-500">*</span>
              </label>
              <input
                v-model="formData.name"
                type="text"
                placeholder="Ví dụ: Gói Cơ Bản (12 Buổi)"
                class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                required
              />
            </div>

            <!-- Sessions -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">
                Số buổi tập <span class="text-red-500">*</span>
              </label>
              <input
                v-model.number="formData.sessions"
                type="number"
                min="1"
                placeholder="Ví dụ: 12"
                class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                required
              />
            </div>

            <!-- Status -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">
                Trạng thái
              </label>
              <select
                v-model="formData.status"
                class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
              >
                <option value="Active">Hoạt động</option>
                <option value="Inactive">Tạm ngưng</option>
              </select>
            </div>

            <!-- Actions -->
            <div class="flex gap-3 pt-4">
              <button
                type="button"
                @click="closeModal"
                class="flex-1 px-4 py-3 bg-gray-200 text-gray-700 rounded-lg font-semibold hover:bg-gray-300 transition-all"
              >
                Hủy
              </button>
              <button
                type="submit"
                class="flex-1 px-4 py-3 bg-gradient-to-r from-emerald-600 to-teal-600 text-white rounded-lg font-semibold hover:shadow-lg transition-all"
              >
                {{ isEditMode ? 'Cập nhật' : 'Thêm mới' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </transition>
  </div>
</template>

<style scoped>
/* Animations */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.grid > div {
  animation: fadeIn 0.5s ease-out;
}
</style>
