<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/services/api'
import { useToast } from 'vue-toastification'

// Type không đổi
type UserDetail = {
  id: number;
  fullName: string;
  email: string;
  dob: string;
  gender: string;
  phone: string;
  role: string;
  position?: string;
  specialize?: string;
  hirePrice?: number;
}

// --- State ---
const user = ref<UserDetail | null>(null) // Dữ liệu gốc (để hiển thị)
const editData = ref<any>(null) // Dữ liệu khi chỉnh sửa (bản sao)
const isLoading = ref(true)
const isSaving = ref(false)
const isEditing = ref(false) // <-- State quan trọng để chuyển chế độ

const route = useRoute()
const router = useRouter()
const toast = useToast()

// --- Lấy dữ liệu ban đầu ---
onMounted(async () => {
  const userId = route.params.id
  if (!userId) {
    toast.error("Không tìm thấy ID nhân viên.")
    router.push('/staff')
    return
  }
  try {
    isLoading.value = true
    const response = await api.get(`/users/${userId}`)
    user.value = response.data
  } catch (err: any) {
    toast.error("Không thể tải thông tin.")
    router.push('/staff')
  } finally {
    isLoading.value = false
  }
})

// --- Hàm Helper ---
const formatDate = (dateString: string) => {
  if (!dateString) return 'N/A'
  try {
    const date = new Date(dateString)
    return date.toLocaleDateString('vi-VN')
  } catch (error) { return dateString }
}

const formatPrice = (price?: number) => {
  if (price === undefined || price === null) return 'N/A'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);
}

// Hàm mới: Chuyển đổi Date (ISO/string) sang YYYY-MM-DD cho input
const formatToInputDate = (dateString: string) => {
  if (!dateString) return '';
  try {
    const date = new Date(dateString);
    // Lấy YYYY-MM-DD từ chuỗi ISO
    return date.toISOString().split('T')[0];
  } catch (e) {
    return ''; // Trả về rỗng nếu ngày không hợp lệ
  }
}

// --- Các hàm xử lý Edit/Save/Cancel ---

// Khi nhấn nút "Edit"
const startEditing = () => {
  if (!user.value) return;
  // Tạo 1 bản sao (deep copy) để chỉnh sửa
  // Chuyển đổi `dob` sang định dạng YYYY-MM-DD
  editData.value = {
    ...JSON.parse(JSON.stringify(user.value)),
    dob: formatToInputDate(user.value.dob) // Quan trọng
  };
  isEditing.value = true;
}

// Khi nhấn nút "Cancel"
const handleCancel = () => {
  isEditing.value = false;
  editData.value = null; // Hủy bản sao
}

// Khi nhấn nút "Save"
const handleSave = async () => {
  if (!editData.value || !user.value) return;

  isSaving.value = true;
  try {
    // Gọi API PUT/PATCH để cập nhật
    // Giả sử endpoint là /users/{id} với method PUT
    const response = await api.put(`/users/${user.value.id}`, editData.value);
    
    // Cập nhật lại dữ liệu gốc (user) với dữ liệu mới
    user.value = response.data; 
    
    isEditing.value = false; // Quay về chế độ "chỉ đọc"
    toast.success("Cập nhật thông tin thành công!");

  } catch (err: any) {
    toast.error("Cập nhật thất bại.");
  } finally {
    isSaving.value = false;
  }
}

// Khi nhấn nút "Back"
const goBack = () => {
  router.back()
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
      <p class="text-lg text-gray-500">Loading staff information...</p>
    </div>

    <div v-else-if="user" class="bg-white rounded-lg shadow-md overflow-hidden">
      
      <div class="p-6 border-b border-gray-200 bg-gray-50 flex justify-between items-center">
        <div>
          <h1 class="text-2xl font-semibold text-gray-900">
            {{ isEditing ? editData.fullName : user.fullName }}
          </h1>
          <span 
            :class="[
              'px-3 py-0.5 rounded-full text-sm font-medium mt-1 inline-block',
              (isEditing ? editData.role : user.role) === 'MANAGER' ? 'bg-yellow-100 text-yellow-800' :
              (isEditing ? editData.role : user.role) === 'STAFF' ? 'bg-blue-100 text-blue-800' :
              (isEditing ? editData.role : user.role) === 'RECEPTIONIST' ? 'bg-green-100 text-green-800' :
              'bg-gray-100 text-gray-800'
            ]"
          >
            {{ isEditing ? editData.role : user.role }}
          </span>
        </div>

        <div>
          <template v-if="!isEditing">
            <button
              @click="goBack"
              class="px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 transition mr-2"
            >
              Back
            </button>
            <button
              @click="startEditing"
              class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition"
            >
              Edit
            </button>
          </template>
          <template v-else>
             <button
              @click="handleCancel"
              class="px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 transition mr-2"
            >
              Cancel
            </button>
            <button
              @click="handleSave"
              :disabled="isSaving"
              class="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition disabled:opacity-50"
            >
              {{ isSaving ? 'Saving...' : 'Save' }}
            </button>
          </template>
        </div>
      </div>

      <div class="p-6">
        <h3 class="text-lg font-medium text-gray-900 mb-4">Personal Information</h3>
        
        <dl class="grid grid-cols-1 md:grid-cols-2 gap-x-6 gap-y-6">
          
          <div>
            <dt class="text-sm font-medium text-gray-500 mb-1">Full Name</dt>
            <dd v-if="!isEditing" class="mt-1 text-sm text-gray-900">{{ user.fullName }}</dd>
            <input v-else v-model="editData.fullName" type="text" class="form-input" />
          </div>

          <div>
            <dt class="text-sm font-medium text-gray-500 mb-1">Email</dt>
            <dd v-if="!isEditing" class="mt-1 text-sm text-gray-900">{{ user.email }}</dd>
            <input v-else v-model="editData.email" type="email" class="form-input" />
          </div>

          <div>
            <dt class="text-sm font-medium text-gray-500 mb-1">Phone</dt>
            <dd v-if="!isEditing" class="mt-1 text-sm text-gray-900">{{ user.phone }}</dd>
            <input v-else v-model="editData.phone" type="tel" class="form-input" />
          </div>

          <div>
            <dt class="text-sm font-medium text-gray-500 mb-1">Date of Birth</dt>
            <dd v-if="!isEditing" class="mt-1 text-sm text-gray-900">{{ formatDate(user.dob) }}</dd>
            <input v-else v-model="editData.dob" type="date" class="form-input" />
          </div>

          <div>
            <dt class="text-sm font-medium text-gray-500 mb-1">Gender</dt>
            <dd v-if="!isEditing" class="mt-1 text-sm text-gray-900">{{ user.gender }}</dd>
            <select v-else v-model="editData.gender" class="form-input">
              <option value="Male">Male</option>
              <option value="Female">Female</option>
              <option value="Other">Other</option>
            </select>
          </div>

          <div>
            <dt class="text-sm font-medium text-gray-500 mb-1">Role</dt>
            <dd v-if="!isEditing" class="mt-1 text-sm text-gray-900">{{ user.role }}</dd>
            <select v-else v-model="editData.role" class="form-input">
              <option value="STAFF">Staff (Trainer)</option>
              <option value="RECEPTIONIST">Receptionist</option>
              <option value="MANAGER">Manager</option>
            </select>
          </div>

        </dl>

        <template v-if="(isEditing ? editData.role : user.role) === 'STAFF'">
          <hr class="my-6" />
          <h3 class="text-lg font-medium text-gray-900 mb-4">Professional Information</h3>
          <dl class="grid grid-cols-1 md:grid-cols-2 gap-x-6 gap-y-6">
            
            <div>
              <dt class="text-sm font-medium text-gray-500 mb-1">Position</dt>
              <dd v-if="!isEditing" class="mt-1 text-sm text-gray-900">{{ user.position || 'N/A' }}</dd>
              <input v-else v-model="editData.position" type="text" class="form-input" />
            </div>

            <div>
              <dt class="text-sm font-medium text-gray-500 mb-1">Specialize</dt>
              <dd v-if="!isEditing" class="mt-1 text-sm text-gray-900">{{ user.specialize || 'N/A' }}</dd>
              <input v-else v-model="editData.specialize" type="text" class="form-input" />
            </div>

            <div class="md:col-span-2">
              <dt class="text-sm font-medium text-gray-500 mb-1">Hire Price</dt>
              <dd v-if="!isEditing" class="mt-1 text-sm text-gray-900 font-semibold text-blue-600">{{ formatPrice(user.hirePrice) }}</dd>
              <input v-else v-model.number="editData.hirePrice" type="number" min="0" class="form-input" />
            </div>

          </dl>
        </template>
        
      </div>
    </div>
  </div>
</template>