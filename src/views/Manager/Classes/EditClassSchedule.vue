<script setup lang="ts">
import api from '@/services/api' // Đảm bảo đúng đường dẫn
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

// --- Định nghĩa Types ---
interface Room {
  id: number
  name: string
  location: string
}

interface ClassTemplate {
  id: number
  name: string
}

interface ScheduleForm {
  id: number
  startTime: string
  endTime: string
  status: 'OPEN' | 'CLOSED' | 'CANCELLED'
  classTemplate: ClassTemplate | null
  room: Room | null
}

// --- Khởi tạo ---
const route = useRoute()
const router = useRouter()
const scheduleId = ref(route.params.id as string)

// State chính cho form
const formData = ref<ScheduleForm | null>(null)

// State riêng cho các dropdown
const allRooms = ref<Room[]>([])
const selectedRoomId = ref<number | null>(null)
const allClassTemplates = ref<ClassTemplate[]>([])
const selectedClassTemplateId = ref<number | null>(null)

// State giao diện
const isLoading = ref(true)
const errorMessage = ref('')

const formatISOToInput = (isoString: string | null): string => {
  if (!isoString) return ''
  return isoString.substring(0, 16)
}

// --- Lấy dữ liệu khi mount ---
onMounted(async () => {
  if (!scheduleId.value) {
    errorMessage.value = "Invalid Schedule ID."
    isLoading.value = false
    return
  }

  try {
    isLoading.value = true
    
    // Yêu cầu của bạn là response /classschedule/{id} đã có room
    // Nhưng chúng ta VẪN CẦN gọi /room để lấy DANH SÁCH TẤT CẢ CÁC PHÒNG
    // và /classtemplate để lấy TẤT CẢ CÁC TEMPLATE cho dropdown
    const [scheduleRes, roomsRes, templatesRes] = await Promise.all([
      api.get(`/classschedule/${scheduleId.value}`),
      api.get('/room'), // Giả sử đây là API lấy TẤT CẢ phòng
      api.get('/classtemplate') // API lấy TẤT CẢ template
    ])

    // Gán dữ liệu vào state
    formData.value = scheduleRes.data
    allRooms.value = roomsRes.data
    allClassTemplates.value = templatesRes.data

    // Cập nhật state riêng cho dropdown
    selectedRoomId.value = scheduleRes.data.room?.id || null
    
    // --- SỬA THEO YÊÊU CẦU CỦA BẠN (Dùng sessionStorage) ---
    // Ưu tiên 1: Lấy ID từ data của schedule (scheduleRes)
    let templateId = scheduleRes.data.classTemplate?.id || null
    
    // Ưu tiên 2: Nếu (1) bị null, thử lấy từ sessionStorage
    if (!templateId) {
      try {
        const storedTemplateJSON = sessionStorage.getItem("selectedTemplate");
        if (storedTemplateJSON) {
          const parsedTemplate = JSON.parse(storedTemplateJSON);
          if (parsedTemplate?.id) {
            templateId = parsedTemplate.id;
            console.log('Fallback to sessionStorage template ID:', templateId)
          }
        }
      } catch (e) {
        console.error("Failed to parse sessionStorage template", e)
      }
    }
    
    selectedClassTemplateId.value = templateId;
    // ------------------------------------------------------

    // Định dạng lại ngày giờ
    if (formData.value) {
      formData.value.startTime = formatISOToInput(formData.value.startTime)
      formData.value.endTime = formatISOToInput(formData.value.endTime)
    }

    errorMessage.value = ''
  } catch (err) {
    console.error("Error fetching data:", err)
    errorMessage.value = "Could not load data. Please try again."
  } finally {
    isLoading.value = false
  }
})

// --- Xử lý Lưu ---
const handleSave = async () => {
  if (!formData.value) return

  try {
    isLoading.value = true

    // 1. Tìm đối tượng Room đầy đủ
    const selectedRoom = allRooms.value.find(r => r.id === selectedRoomId.value) || null
    
    // 2. Tìm đối tượng ClassTemplate đầy đủ
    const selectedTemplate = allClassTemplates.value.find(
      t => t.id === selectedClassTemplateId.value
    ) || null

    // 3. Chuẩn bị payload để gửi đi
    const payload = {
      ...formData.value,
      room: selectedRoom, // Cập nhật lại đối tượng room
      classTemplate: selectedTemplate, // <-- Ghi đè classTemplate
      startTime: formData.value.startTime.length === 16 ? formData.value.startTime + ':00' : formData.value.startTime,
      endTime: formData.value.endTime.length === 16 ? formData.value.endTime + ':00' : formData.value.endTime,
    }

    // 4. Gọi API PUT
    await api.put(`/classschedule/${scheduleId.value}`, payload)
    
    alert('Cập nhật lịch học thành công!')
    router.back()
  } catch (err) {
    console.error("Error updating schedule:", err)
    alert('Có lỗi xảy ra khi cập nhật!')
  } finally {
    isLoading.value = false
  }
}
</script>

<!-- 
  PHẦN TEMPLATE NÀY RẤT QUAN TRỌNG. 
  BẠN ĐÃ BỎ SÓT DROPDOWN "CLASS TEMPLATE" TRONG FILE BẠN PASTE.
-->
<template>
  <div class="max-w-2xl mx-auto p-4 md:p-6">
    <h1 class="text-3xl font-bold text-gray-800 mb-6">
      Edit Class Schedule
      <span class="text-gray-500 font-normal text-2xl">(ID: {{ scheduleId }})</span>
    </h1>

    <div v-if="isLoading" class="text-center py-10">
      <p class="text-lg font-medium text-gray-600">Loading schedule...</p>
    </div>

    <div v-if="errorMessage && !isLoading" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-md" role="alert">
      <strong class="font-bold">Error: </strong>
      <span class="block sm:inline">{{ errorMessage }}</span>
    </div>

    <form v-if="!isLoading && formData" @submit.prevent="handleSave" 
          class="bg-white shadow-lg rounded-lg p-6 md:p-8 space-y-6">

      <!-- ===== BẠN BỊ THIẾU KHỐI NÀY ===== -->
      <div>
        <label for="classTemplate" class="block text-sm font-medium text-gray-700 mb-1">Class Template</label>
        <select 
          id="classTemplate"
          v-model="selectedClassTemplateId" 
          class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
          required
        >
          <option :value="null">-- Select a Template --</option>
          <option v-for="template in allClassTemplates" :key="template.id" :value="template.id">
            {{ template.name }}
          </option>
        </select>
      </div>
      <!-- ================================== -->
      
      <div>
        <label for="startTime" class="block text-sm font-medium text-gray-700 mb-1">Start Time</label>
        <input 
          type="datetime-local" 
          id="startTime"
          v-model="formData.startTime"
          class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
          required
        />
      </div>

      <div>
        <label for="endTime" class="block text-sm font-medium text-gray-700 mb-1">End Time</label>
        <input 
          type="datetime-local" 
          id="endTime"
          v-model="formData.endTime"
          class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
          required
        />
      </div>

      <div>
        <label for="room" class="block text-sm font-medium text-gray-700 mb-1">Room</label>
        <select 
          id="room"
          v-model="selectedRoomId" class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
        >
          <option :value="null">-- No Room Assigned --</option>
          <option v-for="room in allRooms" :key="room.id" :value="room.id">
            {{ room.name }} ({{ room.location }})
          </option>
        </select>
      </div>

      <div>
        <label for="status" class="block text-sm font-medium text-gray-700 mb-1">Status</label>
        <select 
          id="status"
          v-model="formData.status"
          class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
        >
          <option value="OPEN">OPEN</option>
          <option value="CLOSED">CLOSED</option>
          <option value="CANCELLED">CANCELLED</option>
        </select>
      </div>
      
      <div class="border-t pt-6 flex justify-end gap-4 mt-8">
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
          Save Schedule
        </button>
      </div>
    </form>
  </div>
</template>