<script setup lang="ts">
import { ref, computed } from 'vue'
import api from '@/services/api'
import { useRouter } from 'vue-router'
import { useToast } from "vue-toastification";

// --- State cho Form ---
const email = ref('')
const password = ref('')
const fullName = ref('')
const dob = ref('') // Input type="date" trả về string YYYY-MM-DD
const gender = ref('Nam') // Giá trị mặc định
const phone = ref('')
const role = ref('STAFF') // Mặc định là STAFF

// --- Các trường dành riêng cho GIÁO VIÊN ---
const position = ref('')
const specialize = ref('')

// --- State xử lý UI ---
const isLoading = ref(false)
const toast = useToast();
const router = useRouter()

const handleSubmit = async () => {
  isLoading.value = true

  let payload: any = {
    email: email.value,
    password: password.value,
    fullName: fullName.value,
    dob: dob.value,
    gender: gender.value,
    phone: phone.value,
    role: role.value,
  }

  // Nếu là giáo viên thì thêm các trường chuyên biệt
  if (role.value === 'TEACHER') {
    payload = {
      ...payload,
      position: position.value,
      specialize: specialize.value,
    }
  }

  try {
    const response = await api.post('/auth/register-employee', payload)
    
    toast.success("Thêm nhân viên thành công!");
    
    // Reset form
    email.value = ''
    password.value = ''
    fullName.value = ''
    dob.value = ''
    phone.value = ''
    position.value = ''
    specialize.value = ''
    
    setTimeout(() => {
      router.push('/manager/staff') 
    }, 1500)

  } catch (err: any) {
    const errorMessage = 'Lỗi khi đăng ký nhân viên!';
    toast.error(errorMessage);
    
  } finally {
    isLoading.value = false
  }
}

const handleCancel = () => {
  router.back() 
}
</script>

<template>
  <div class="p-6 max-w-4xl mx-auto bg-white rounded-lg shadow-md">
    <h1 class="text-2xl font-semibold mb-6">Thêm Nhân Viên Mới</h1>

    <form @submit.prevent="handleSubmit">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        
        <div>
          <label class="block text-sm font-medium text-gray-700">Họ và Tên</label>
          <input v-model="fullName" type="text" maxlength="50" required placeholder="Nhập họ tên đầy đủ" class="mt-1 p-2 w-full border rounded-md shadow-sm focus:ring-emerald-500 focus:border-emerald-500" />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700">Email</label>
          <input v-model="email" type="email" required placeholder="example@gmail.com" class="mt-1 p-2 w-full border rounded-md shadow-sm focus:ring-emerald-500 focus:border-emerald-500" />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700">Mật khẩu</label>
          <input v-model="password" type="password" required placeholder="Nhập mật khẩu" class="mt-1 p-2 w-full border rounded-md shadow-sm focus:ring-emerald-500 focus:border-emerald-500" />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700">Số điện thoại</label>
          <input v-model="phone" type="tel" required placeholder="Nhập số điện thoại" class="mt-1 p-2 w-full border rounded-md shadow-sm focus:ring-emerald-500 focus:border-emerald-500" />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700">Ngày sinh</label>
          <input v-model="dob" type="date" required class="mt-1 p-2 w-full border rounded-md shadow-sm focus:ring-emerald-500 focus:border-emerald-500" />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700">Giới tính</label>
          <select v-model="gender" class="mt-1 p-2 w-full border rounded-md shadow-sm bg-white focus:ring-emerald-500 focus:border-emerald-500">
            <option value="Male">Nam</option>
            <option value="Female">Nữ</option>
            <option value="Other">Khác</option>
          </select>
        </div>

        <div class="md:col-span-2">
          <label class="block text-sm font-medium text-gray-700">Vai trò</label>
          <select v-model="role" class="mt-1 p-2 w-full border rounded-md shadow-sm bg-white focus:ring-emerald-500 focus:border-emerald-500">
            <option value="TEACHER">Giáo viên (Teacher)</option>
            <option value="PT">Huấn luyện viên cá nhân (PT)</option>
            <option value="RECEPTIONIST">Lễ tân</option>
            <option value="MANAGER">Quản lý</option>
          </select>
        </div>

        <template v-if="role === 'TEACHER'">
          <hr class="md:col-span-2 my-2" />
          
          <div class="md:col-span-2 text-sm font-medium text-emerald-600">
            Thông tin dành riêng cho Giáo viên
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700">Chức danh / Vị trí</label>
            <input v-model="position" type="text" placeholder="VD: Huấn luyện viên cao cấp" class="mt-1 p-2 w-full border rounded-md shadow-sm focus:ring-emerald-500 focus:border-emerald-500" />
          </div>
          
          <div>
            <label class="block text-sm font-medium text-gray-700">Chuyên môn</label>
            <input v-model="specialize" type="text" placeholder="VD: Yoga, HIIT, Cardio" class="mt-1 p-2 w-full border rounded-md shadow-sm focus:ring-emerald-500 focus:border-emerald-500" />
          </div>
        </template>
      </div>

      <div class="mt-8 flex justify-end space-x-4">
        <button 
          type="button" 
          @click="handleCancel"
          class="px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 transition"
        >
          Hủy bỏ
        </button>
        <button
          type="submit"
          :disabled="isLoading"
          class="px-4 py-2 bg-emerald-600 text-white rounded-md hover:bg-emerald-700 disabled:opacity-50 transition"
        >
          {{ isLoading ? 'Đang lưu...' : 'Thêm nhân viên' }}
        </button>
      </div>
    </form>
  </div>
</template>