<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex justify-between items-center bg-white p-6 rounded-xl shadow-sm border border-gray-100">
      <div>
        <h1 class="text-2xl font-bold text-gray-800">Quản lý phòng học</h1>
        <p class="text-gray-500 mt-1">Danh sách tất cả phòng học trong hệ thống</p>
      </div>
      <div class="flex flex-col md:flex-row gap-3 items-center">
        <!-- Search -->
        <div class="relative">
          <input 
            v-model="search"
            type="text" 
            placeholder="Tìm phòng học..." 
            class="pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none w-full md:w-56 text-sm"
          />
          <SearchIcon class="w-4 h-4 text-gray-400 absolute left-3 top-1/2 -translate-y-1/2" />
        </div>

        <button @click="handleAdd" class="flex items-center gap-2 px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors shadow-sm font-medium whitespace-nowrap">
          <PlusIcon class="w-4 h-4" />
          Thêm phòng
        </button>
      </div>
    </div>

    <!-- Rooms Table -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full text-left text-sm">
          <thead class="bg-gray-50 text-gray-600 font-medium border-b border-gray-200">
            <tr>
              <th class="px-6 py-4 w-16 text-center">ID</th>
              <th class="px-6 py-4">Tên phòng</th>
              <th class="px-6 py-4 text-center">Sức chứa</th>
              <th class="px-6 py-4">Mô tả</th>
              <th class="px-6 py-4 text-center w-24">Thao tác</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-if="filteredRooms.length === 0">
              <td colspan="5" class="px-6 py-12 text-center text-gray-400">
                Chưa có phòng học nào.
              </td>
            </tr>
            <tr v-for="room in filteredRooms" :key="room.id" class="hover:bg-gray-50 transition-colors">
              <td class="px-6 py-4 text-center text-gray-500">#{{ room.id }}</td>
              <td class="px-6 py-4">
                <div class="flex items-center gap-3">
                  <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-indigo-500 to-purple-600 flex items-center justify-center flex-shrink-0">
                    <DoorOpenIcon class="w-5 h-5 text-white" />
                  </div>
                  <div>
                    <p class="font-medium text-gray-900">{{ room.name }}</p>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4 text-center">
                <span class="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium bg-blue-50 text-blue-800">
                  <UsersIcon class="w-3 h-3 mr-1" />
                  {{ room.capacity }} người
                </span>
              </td>
              <td class="px-6 py-4">
                <p class="text-gray-600 line-clamp-2">{{ room.description || 'Không có mô tả' }}</p>
              </td>
              <td class="px-6 py-4 text-center">
                <div class="flex items-center justify-center gap-2">
                  <button @click="handleEdit(room)" class="p-1.5 text-blue-600 hover:bg-blue-50 rounded-md transition-colors" title="Chỉnh sửa">
                    <Edit2Icon class="w-4 h-4" />
                  </button>
                  <button @click="handleDelete(room.id!)" class="p-1.5 text-red-600 hover:bg-red-50 rounded-md transition-colors" title="Xóa">
                    <Trash2Icon class="w-4 h-4" />
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Add/Edit Modal -->
    <Transition name="modal">
      <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4">
        <div class="bg-white rounded-2xl shadow-2xl w-full max-w-lg overflow-hidden transform transition-all">
          <div class="bg-gray-50 px-6 py-4 border-b border-gray-100 flex justify-between items-center">
            <h3 class="text-lg font-semibold text-gray-800">{{ isEditing ? 'Cập nhật phòng học' : 'Thêm phòng học mới' }}</h3>
            <button @click="handleCancel" class="text-gray-400 hover:text-gray-600 transition-colors">
              <XIcon class="w-5 h-5" />
            </button>
          </div>
          <div class="p-6 space-y-4 max-h-[70vh] overflow-y-auto custom-scrollbar">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Tên phòng <span class="text-red-500">*</span></label>
              <input
                v-model="currentRoom.name"
                type="text"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none transition-all"
                placeholder="Ví dụ: Phòng Yoga A"
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Sức chứa <span class="text-red-500">*</span></label>
              <input
                v-model.number="currentRoom.capacity"
                type="number"
                min="1"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none transition-all"
                placeholder="Số người tối đa"
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Mô tả</label>
              <textarea
                v-model="currentRoom.description"
                rows="3"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none transition-all"
                placeholder="Mô tả về phòng học..."
              ></textarea>
            </div>
          </div>
          <div class="bg-gray-50 px-6 py-4 flex justify-end gap-3">
            <button @click="handleCancel" class="px-4 py-2 text-gray-600 hover:bg-gray-200 rounded-lg transition-colors font-medium">Hủy</button>
            <button @click="handleSave" class="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors font-medium shadow-sm">
              {{ isEditing ? 'Cập nhật' : 'Thêm mới' }}
            </button>
          </div>
        </div>
      </div>
    </Transition>

    <!-- Delete Confirmation Modal -->
    <Transition name="modal">
      <div v-if="showDeleteModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4">
        <div class="bg-white rounded-2xl shadow-xl w-full max-w-sm overflow-hidden transform transition-all">
          <div class="p-6 text-center">
            <div class="mx-auto flex items-center justify-center h-12 w-12 rounded-full bg-red-100 mb-4">
              <Trash2Icon class="h-6 w-6 text-red-600" />
            </div>
            <h3 class="text-lg font-medium text-gray-900 mb-2">Xác nhận xóa</h3>
            <p class="text-sm text-gray-500">Bạn có chắc chắn muốn xóa phòng học này không? Hành động này không thể hoàn tác.</p>
          </div>
          <div class="bg-gray-50 px-6 py-4 flex justify-center gap-3">
            <button @click="cancelDelete" class="px-4 py-2 bg-white text-gray-700 border border-gray-300 rounded-lg hover:bg-gray-50 font-medium transition-colors">Hủy bỏ</button>
            <button @click="confirmDelete" class="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 font-medium transition-colors shadow-sm">Xóa bỏ</button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import api from '@/services/api'
import { useToast } from 'vue-toastification'
import { PlusIcon, Edit2Icon, Trash2Icon, XIcon, SearchIcon, DoorOpenIcon, UsersIcon } from 'lucide-vue-next'

const toast = useToast()

type Room = {
  id?: number
  name: string
  capacity: number
  description: string
}

const rooms = ref<Room[]>([])
const search = ref('')
const showModal = ref(false)
const isEditing = ref(false)
const currentRoom = ref<Room>({
  name: '',
  capacity: 0,
  description: ''
})

const showDeleteModal = ref(false)
const roomToDelete = ref<number | null>(null)

onMounted(async () => {
  await fetchRooms()
})

const fetchRooms = async () => {
  try {
    const res = await api.get('/room')
    rooms.value = res.data
  } catch (err) {
    console.error('Error fetching rooms:', err)
    toast.error('Không thể tải danh sách phòng')
  }
}

const filteredRooms = computed(() => {
  return rooms.value.filter(room => {
    const matchesName = room.name?.toLowerCase().includes(search.value.toLowerCase())
    const matchesDescription = room.description?.toLowerCase().includes(search.value.toLowerCase())
    return matchesName || matchesDescription
  })
})

const handleAdd = () => {
  isEditing.value = false
  currentRoom.value = {
    name: '',
    capacity: 0,
    description: ''
  }
  showModal.value = true
}

const handleEdit = (room: Room) => {
  isEditing.value = true
  currentRoom.value = { ...room }
  showModal.value = true
}

const handleDelete = (id: number) => {
  roomToDelete.value = id
  showDeleteModal.value = true
}

const confirmDelete = async () => {
  if (roomToDelete.value === null) return
  
  try {
    await api.delete(`/room/${roomToDelete.value}`)
    toast.success('Xóa phòng thành công')
    await fetchRooms()
  } catch (err) {
    console.error('Error deleting room:', err)
    toast.error('Không thể xóa phòng')
  } finally {
    showDeleteModal.value = false
    roomToDelete.value = null
  }
}

const cancelDelete = () => {
  showDeleteModal.value = false
  roomToDelete.value = null
}

const handleSave = async () => {
  if (!currentRoom.value.name || !currentRoom.value.capacity) {
    toast.warning('Vui lòng điền đầy đủ thông tin bắt buộc')
    return
  }

  try {
    if (isEditing.value && currentRoom.value.id) {
      await api.put(`/room/${currentRoom.value.id}`, currentRoom.value)
      toast.success('Cập nhật phòng thành công')
    } else {
      await api.post('/room', currentRoom.value)
      toast.success('Thêm phòng thành công')
    }
    showModal.value = false
    await fetchRooms()
  } catch (err) {
    console.error('Error saving room:', err)
    toast.error('Không thể lưu phòng')
  }
}

const handleCancel = () => {
  showModal.value = false
}
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

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
