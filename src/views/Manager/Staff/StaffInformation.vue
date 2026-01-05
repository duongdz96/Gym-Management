<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/services/api'
import { useToast } from 'vue-toastification'

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

const user = ref<UserDetail | null>(null)
const editData = ref<any>(null)
const isLoading = ref(true)
const isSaving = ref(false)
const isEditing = ref(false)

const route = useRoute()
const router = useRouter()
const toast = useToast()

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

const formatDate = (dateString: string) => {
  if (!dateString) return 'Chưa cập nhật'
  try {
    const date = new Date(dateString)
    return date.toLocaleDateString('vi-VN')
  } catch (error) { return dateString }
}

const formatPrice = (price?: number) => {
  if (price === undefined || price === null) return 'Chưa cập nhật'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);
}

const formatToInputDate = (dateString: string) => {
  if (!dateString) return '';
  try {
    const date = new Date(dateString);
    return date.toISOString().split('T')[0];
  } catch (e) {
    return '';
  }
}

const startEditing = () => {
  if (!user.value) return;
  editData.value = {
    ...JSON.parse(JSON.stringify(user.value)),
    dob: formatToInputDate(user.value.dob)
  };
  isEditing.value = true;
}

const handleCancel = () => {
  isEditing.value = false;
  editData.value = null;
}

const handleSave = async () => {
  if (!editData.value || !user.value) return;

  isSaving.value = true;
  try {
    const response = await api.put(`/users/${user.value.id}`, editData.value);
    user.value = response.data;
    isEditing.value = false;
    toast.success("Cập nhật thông tin thành công!");
  } catch (err: any) {
    toast.error("Cập nhật thất bại.");
  } finally {
    isSaving.value = false;
  }
}

const goBack = () => {
  router.back()
}
</script>

<style>
.form-input {
  width: 100%;
  padding: 0.5rem 0.75rem;
  border: 1px solid #D1D5DB;
  border-radius: 0.375rem;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  background-color: white;
}

.form-input:focus {
  outline: 2px solid transparent;
  outline-offset: 2px;
  border-color: #059669;
  box-shadow: 0 0 0 2px #A7F3D0;
}
</style>

<template>
  <div class="p-6 max-w-4xl mx-auto">
    <div v-if="isLoading" class="text-center py-10">
      <p class="text-lg text-gray-500">Đang tải thông tin nhân viên...</p>
    </div>

    <div v-else-if="user" class="bg-white rounded-lg shadow-md overflow-hidden">
      <div class="p-6 border-b border-gray-200 bg-gray-50 flex justify-between items-center gap-4">
        <div class="flex-1 min-w-0">
          <h1 class="text-xl sm:text-2xl font-semibold text-gray-900 truncate"
            :title="isEditing ? editData.fullName : user.fullName">
            {{ isEditing ? editData.fullName : user.fullName }}
          </h1>
          <span :class="[
            'px-3 py-0.5 rounded-full text-sm font-medium mt-1 inline-block',
            (isEditing ? editData.role : user.role) === 'MANAGER' ? 'bg-emerald-100 text-emerald-800' :
              (isEditing ? editData.role : user.role) === 'STAFF' ? 'bg-emerald-200 text-emerald-700' :
                (isEditing ? editData.role : user.role) === 'RECEPTIONIST' ? 'bg-green-100 text-green-800' :
                  'bg-gray-100 text-gray-800'
          ]">
            {{ isEditing ? editData.role : user.role }}
          </span>
        </div>

        <div class="flex shrink-0">
          <template v-if="!isEditing">
            <button @click="goBack"
              class="px-3 sm:px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 transition mr-2 text-sm sm:text-base">
              Quay lại
            </button>
            <button @click="startEditing"
              class="px-3 sm:px-4 py-2 bg-emerald-600 text-white rounded-md hover:bg-emerald-700 transition text-sm sm:text-base">
              Chỉnh sửa
            </button>
          </template>
          <template v-else>
            <button @click="handleCancel"
              class="px-3 sm:px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 transition mr-2 text-sm sm:text-base">
              Hủy
            </button>
            <button @click="handleSave" :disabled="isSaving"
              class="px-3 sm:px-4 py-2 bg-emerald-600 text-white rounded-md hover:bg-emerald-700 transition text-sm sm:text-base disabled:opacity-50">
              {{ isSaving ? 'Đang lưu...' : 'Lưu thay đổi' }}
            </button>
          </template>
        </div>
      </div>

      <div class="p-6">
        <h3 class="text-lg font-medium text-gray-900 mb-4">Thông tin cá nhân</h3>

        <dl class="grid grid-cols-1 md:grid-cols-2 gap-x-6 gap-y-6">
          <div>
            <dt class="text-sm font-medium text-gray-500 mb-1">Họ và Tên</dt>
            <dd v-if="!isEditing" class="mt-1 text-sm text-gray-900">{{ user.fullName }}</dd>
            <input v-else v-model="editData.fullName" type="text" maxlength="50" class="form-input" />
          </div>

          <div>
            <dt class="text-sm font-medium text-gray-500 mb-1">Email</dt>
            <dd v-if="!isEditing" class="mt-1 text-sm text-gray-900">{{ user.email }}</dd>
            <input v-else v-model="editData.email" type="email" class="form-input" />
          </div>

          <div>
            <dt class="text-sm font-medium text-gray-500 mb-1">Số điện thoại</dt>
            <dd v-if="!isEditing" class="mt-1 text-sm text-gray-900">{{ user.phone }}</dd>
            <input v-else v-model="editData.phone" type="tel" class="form-input" />
          </div>

          <div>
            <dt class="text-sm font-medium text-gray-500 mb-1">Ngày sinh</dt>
            <dd v-if="!isEditing" class="mt-1 text-sm text-gray-900">{{ formatDate(user.dob) }}</dd>
            <input v-else v-model="editData.dob" type="date" class="form-input" />
          </div>

          <div>
            <dt class="text-sm font-medium text-gray-500 mb-1">Giới tính</dt>
            <dd v-if="!isEditing" class="mt-1 text-sm text-gray-900">{{ user.gender === 'Male' ? 'Nam' : user.gender === 'Female' ? 'Nữ' : 'Khác' }}</dd>
            <select v-else v-model="editData.gender" class="form-input">
              <option value="Male">Nam</option>
              <option value="Female">Nữ</option>
              <option value="Other">Khác</option>
            </select>
          </div>

          <div>
            <dt class="text-sm font-medium text-gray-500 mb-1">Vai trò</dt>
            <dd v-if="!isEditing" class="mt-1 text-sm text-gray-900">{{ user.role }}</dd>
            <select v-else v-model="editData.role" class="form-input">
              <option value="TEACHER">Giáo viên (Teacher)</option>
              <option value="PT">Huấn luyện viên cá nhân (PT)</option>
              <option value="RECEPTIONIST">Lễ tân</option>
              <option value="MANAGER">Quản lý</option>
            </select>
          </div>
        </dl>

        <template v-if="(isEditing ? editData.role : user.role) === 'STAFF'">
          <hr class="my-6" />
          <h3 class="text-lg font-medium text-gray-900 mb-4">Thông tin chuyên môn</h3>
          <dl class="grid grid-cols-1 md:grid-cols-2 gap-x-6 gap-y-6">
            <div>
              <dt class="text-sm font-medium text-gray-500 mb-1">Vị trí / Chức danh</dt>
              <dd v-if="!isEditing" class="mt-1 text-sm text-gray-900">{{ user.position || 'Chưa cập nhật' }}</dd>
              <input v-else v-model="editData.position" type="text" class="form-input" />
            </div>

            <div>
              <dt class="text-sm font-medium text-gray-500 mb-1">Chuyên môn</dt>
              <dd v-if="!isEditing" class="mt-1 text-sm text-gray-900">{{ user.specialize || 'Chưa cập nhật' }}</dd>
              <input v-else v-model="editData.specialize" type="text" class="form-input" />
            </div>

            <div class="md:col-span-2">
              <dt class="text-sm font-medium text-gray-500 mb-1">Giá thuê (giờ)</dt>
              <dd v-if="!isEditing" class="mt-1 text-sm text-gray-900 font-semibold text-emerald-600">{{
                formatPrice(user.hirePrice) }}</dd>
              <input v-else v-model.number="editData.hirePrice" type="number" min="0" class="form-input" />
            </div>
          </dl>
        </template>
      </div>
    </div>
  </div>
</template>