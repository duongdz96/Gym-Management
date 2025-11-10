<script setup lang="ts">
import { ref, computed } from 'vue'
import api from '@/services/api'
import { useRouter } from 'vue-router'
import { useToast } from "vue-toastification";
// --- State cho Form ---
const email = ref('')
const password = ref('')
const fullName = ref('')
const dob = ref('') // Input type="date" sẽ trả về string YYYY-MM-DD
const gender = ref('Male') // Giá trị mặc định
const phone = ref('')
const role = ref('STAFF') // Mặc định là STAFF

// --- Các trường dành riêng cho STAFF ---
const position = ref('')
const specialize = ref('')
const hirePrice = ref(0) // Mặc định là 0

// --- State xử lý UI ---
const isLoading = ref(false)
const error = ref<string | null>(null)
const success = ref<string | null>(null)
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

  if (role.value === 'STAFF') {
    payload = {
      ...payload,
      position: position.value,
      specialize: specialize.value,
      hirePrice: hirePrice.value,
    }
  }

  try {
    const response = await api.post('/auth/register-employee', payload)
    
    toast.success("Thêm nhân viên thành công!");
    
    email.value = ''
    password.value = ''
    fullName.value = ''
    dob.value = ''
    phone.value = ''
    position.value = ''
    specialize.value = ''
    hirePrice.value = 0
    
    setTimeout(() => {
      router.push('/manager/staff') 
    }, 1500)

  } catch (err: any) {
    console.error('Lỗi khi đăng ký nhân viên:', err)
    const errorMessage = err.response?.data?.message || 'Lỗi khi đăng ký nhân viên!';
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
    <h1 class="text-2xl font-semibold mb-6">Add New Employee</h1>

    <form @submit.prevent="handleSubmit">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        
        <div>
          <label class="block text-sm font-medium text-gray-700">Full Name</label>
          <input v-model="fullName" type="text" required class="mt-1 p-2 w-full border rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500" />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700">Email</label>
          <input v-model="email" type="email" required class="mt-1 p-2 w-full border rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500" />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700">Password</label>
          <input v-model="password" type="password" required class="mt-1 p-2 w-full border rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500" />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700">Phone</label>
          <input v-model="phone" type="tel" required class="mt-1 p-2 w-full border rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500" />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700">Date of Birth</label>
          <input v-model="dob" type="date" required class="mt-1 p-2 w-full border rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500" />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700">Gender</label>
          <select v-model="gender" class="mt-1 p-2 w-full border rounded-md shadow-sm bg-white focus:ring-blue-500 focus:border-blue-500">
            <option value="Male">Male</option>
            <option value="Female">Female</option>
            <option value="Other">Other</option>
          </select>
        </div>

        <div class="md:col-span-2">
          <label class="block text-sm font-medium text-gray-700">Role</label>
          <select v-model="role" class="mt-1 p-2 w-full border rounded-md shadow-sm bg-white focus:ring-blue-500 focus:border-blue-500">
            <option value="STAFF">PT or Teacher</option>
            <option value="RECEPTIONIST">Receptionist</option>
            <option value="MANAGER">Manager</option>
          </select>
        </div>

        <template v-if="role === 'STAFF'">
          <hr class="md:col-span-2 my-2" />
          
          <div class="md:col-span-2 text-sm font-medium text-gray-600">
            Only for PT or teacher roles
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700">Position</label>
            <input v-model="position" type="text" placeholder="e.g., Senior Trainer" class="mt-1 p-2 w-full border rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500" />
          </div>
          
          <div>
            <label class="block text-sm font-medium text-gray-700">Specialize</label>
            <input v-model="specialize" type="text" placeholder="e.g., Yoga, HIIT, Cardio" class="mt-1 p-2 w-full border rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500" />
          </div>

          <div class="md:col-span-2">
            <label class="block text-sm font-medium text-gray-700">Hire Price</label>
            <input v-model.number="hirePrice" type="number" min="0" class="mt-1 p-2 w-full border rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500" />
          </div>
        </template>
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
          :disabled="isLoading"
          class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50 transition"
        >
          {{ isLoading ? 'Saving...' : 'Add Employee' }}
        </button>
      </div>
    </form>
  </div>
</template>