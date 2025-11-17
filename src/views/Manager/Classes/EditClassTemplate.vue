<script setup lang="ts">
import api from '@/services/api'
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
// import api from '...' // Đảm bảo bạn import api client

// --- Định nghĩa Types (để code an toàn hơn) ---
interface Room {
  id: number
  name: string
  location: string
}

interface ClassSchedule {
  id: number
  startTime: string // LocalDateTime thường về dạng string ISO
  endTime: string
  status: string
  room: Room | null // Room có thể null
  // schedulePattern: any // Bỏ qua vì không dùng
}

interface TemplateForm {
  id: number
  name: string
  description: string
  difficultyLevel: string
  status: string
  classSchedule: ClassSchedule[]
}

// --- Khởi tạo ---
const route = useRoute()
const router = useRouter()
const templateId = ref(route.params.id as string)

// State cho form với kiểu dữ liệu rõ ràng
const formData = ref<TemplateForm>({
  id: null,
  name: '',
  description: '',
  difficultyLevel: 'Easy', // Đặt giá trị mặc định
  status: 'Draft',        // Đặt giá trị mặc định
  classSchedule: []
})

const isLoading = ref(true)
const errorMessage = ref('')

// --- Lấy dữ liệu khi mount ---
onMounted(async () => {
  if (!templateId.value) {
    errorMessage.value = "Invalid Template ID."
    isLoading.value = false
    return
  }
  
  try {
    isLoading.value = true
    const res = await api.get(`/classtemplate/${templateId.value}`)
    formData.value = res.data
    console.log(res)
    errorMessage.value = ''
  } catch (err) {
    console.error("Error fetching class template:", err)
    errorMessage.value = "Could not load data. Please try again."
  } finally {
    isLoading.value = false
  }
})

// --- Xử lý Lưu ---
const handleSave = async () => {
  try {
    isLoading.value = true // Hiển thị loading khi đang lưu
    // Gửi TOÀN BỘ object formData, bao gồm cả classSchedule
    await api.put(`/classtemplate/${templateId.value}`, formData.value)
    
    alert('Cập nhật thành công!')
    router.push('/classtemplate') // Quay về trang danh sách
  } catch (err) {
    console.error("Error updating class template:", err)
    alert('Có lỗi xảy ra khi cập nhật!')
  } finally {
    isLoading.value = false
  }
}

// --- Hàm tiện ích (để format ngày giờ) ---
const formatDateTime = (isoString: string) => {
  if (!isoString) return 'N/A'
  try {
    const date = new Date(isoString)
    // Tùy chỉnh format bạn muốn, ví dụ: "14:30 12/11/2025"
    const time = date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })
    const day = date.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' })
    return `${time} ${day}`
  } catch (e) {
    return isoString // Trả về nguyên bản nếu lỗi
  }
}
</script>

<template>
  <div class="max-w-4xl mx-auto p-4 md:p-6">
    <h1 class="text-3xl font-bold text-gray-800 mb-6">
      Edit Class Template
      <span v-if="formData.name" class="text-indigo-600">- {{ formData.name }}</span>
    </h1>

    <div v-if="isLoading" class="text-center py-10">
      <p class="text-lg font-medium text-gray-600">Loading data...</p>
    </div>

    <div v-if="errorMessage && !isLoading" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-md" role="alert">
      <strong class="font-bold">Error: </strong>
      <span class="block sm:inline">{{ errorMessage }}</span>
    </div>

    <form v-if="!isLoading && !errorMessage" @submit.prevent="handleSave">
      
      <div class="bg-white shadow-lg rounded-lg p-6 md:p-8">
        <h2 class="text-xl font-semibold text-gray-700 mb-5 border-b pb-3">Basic Information</h2>
        
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div class="md:col-span-2">
            <label for="name" class="block text-sm font-medium text-gray-700 mb-1">Name</label>
            <input 
              type="text" 
              id="name"
              v-model="formData.name"
              class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              required
            />
          </div>

          <div class="md:col-span-2">
            <label for="description" class="block text-sm font-medium text-gray-700 mb-1">Description</label>
            <textarea 
              id="description"
              v-model="formData.description"
              rows="5"
              class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
            ></textarea>
          </div>

          <div>
            <label for="difficulty" class="block text-sm font-medium text-gray-700 mb-1">Difficulty Level</label>
            <select 
              id="difficulty"
              v-model="formData.difficultyLevel"
              class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
            >
                <option value="BEGINNER">Beginner</option>
                <option value="INTERMEDIATE">Intermediate</option>
                <option value="ADVANCED">Advanced</option>
            </select>
          </div>

          <div>
            <label for="status" class="block text-sm font-medium text-gray-700 mb-1">Status</label>
            <select 
              id="status"
              v-model="formData.status"
              class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
            >
              <option value="ACTIVE">Active</option>
              <option value="INACTIVE">Inactive</option>
            </select>
          </div>
        </div>
      </div>

      <div class="bg-white shadow-lg rounded-lg p-6 md:p-8 mt-6">
        <h2 class="text-xl font-semibold text-gray-700 mb-5 border-b pb-3">Active Schedules (Read-only)</h2>
        
        <div v-if="formData.classSchedule && formData.classSchedule.length > 0">
          <ul class="space-y-4">
            <li v-for="schedule in formData.classSchedule" :key="schedule.id" 
                class="p-4 border rounded-md bg-gray-50 flex justify-between items-center">
              
              <div>
                <p class="font-semibold text-gray-800">
                  Phòng: {{ schedule.room?.name || 'N/A' }} 
                  <span class="font-normal text-gray-600">({{ schedule.room?.location || '...' }})</span>
                </p>
                <p class="text-sm text-gray-600">
                  Từ: <span class="text-blue-600">{{ formatDateTime(schedule.startTime) }}</span>
                </p>
                <p class="text-sm text-gray-600">
                  Đến: <span class="text-blue-600">{{ formatDateTime(schedule.endTime) }}</span>
                </p>
              </div>
              <span 
                :class="{
                  'bg-green-100 text-green-800': schedule.status === 'OPEN',
                  'bg-red-100 text-red-800': schedule.status === 'CANCELLED',
                  'bg-gray-100 text-gray-800': schedule.status === 'CLOSED'
                }"
                class="px-3 py-1 text-xs font-medium rounded-full"
              >
                {{ schedule.status }}
              </span>
            </li>
          </ul>
        </div>
        <div v-else>
          <p class="text-gray-500 italic">This template has no active schedules.</p>
        </div>
      </div>

      <div class="flex justify-end gap-4 mt-8">
        <button
          type="button"
          @click="router.back()"
          class="px-5 py-2 rounded-md bg-white border border-gray-300 text-gray-700 hover:bg-gray-50"
        >
          Cancel
        </button>
        <button
          type="submit"
          class="px-5 py-2 rounded-md bg-indigo-600 text-white hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
        >
          Save Changes
        </button>
      </div>
    </form>
  </div>
</template>