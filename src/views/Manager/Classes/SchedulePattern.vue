<script setup lang="ts">
import { ref, onMounted } from "vue";
import api from "@/services/api"
import { useRouter } from "vue-router";

const router = useRouter();
// =================== STATE ===================
interface SchedulePattern {
  id?: number;
  daysOfWeek: string;
  timeStart: string;
  timeEnd: string;
  classStartDate: string;
  classEndDate: string;
}

interface Room {
  id: number;
  name: string;
  note?: string;
  location?: string;
}

const patterns = ref<SchedulePattern[]>([]);
const availableRooms = ref<Room[]>([]); // <--- NEW: Danh sách phòng rảnh
const isLoadingRooms = ref(false);      // <--- NEW: Trạng thái loading

const showModal = ref(false);
const isEditing = ref(false);
const selectedPattern = ref<SchedulePattern | null>(null);
const selectedRoomId = ref<number | null>(null);

const form = ref<SchedulePattern>({
  daysOfWeek: "",
  timeStart: "",
  timeEnd: "",
  classStartDate: "",
  classEndDate: "",
});

const formatTime = (timeStr: string) => {
  if (!timeStr) return "";
  return timeStr.slice(0, 5); 
};

const formatDate = (dateStr: string) => {
  if (!dateStr) return "";
  const d = new Date(dateStr);
  return d.toLocaleDateString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  }); 
};

// =================== FETCH ===================
// Hàm này có thể bỏ nếu bạn chỉ dùng rooms trong modal chọn phòng
// const loadRooms = async () => { ... } 

const loadPatterns = async () => {
  try {
    const res = await api.get("/schedule-patterns");
    patterns.value = res.data;
  } catch (err) {
    console.error(err);
  }
};

const showSelectRoomModal = async (pattern: SchedulePattern) => {
  selectedPattern.value = pattern;
  selectedRoomId.value = null;
  availableRooms.value = []; // Reset danh sách cũ
  isLoadingRooms.value = true; // Bật loading

  try {
    const res = await api.post("/room/available-for-pattern", pattern);
    availableRooms.value = res.data;
  } catch (err) {
    console.error("Lỗi khi tìm phòng trống:", err);
    alert("Không thể tải danh sách phòng trống.");
  } finally {
    isLoadingRooms.value = false; // Tắt loading
  }
};

// =================== CRUD ===================
const openAdd = () => {
  isEditing.value = false;
  showModal.value = true;
  form.value = {
    daysOfWeek: "",
    timeStart: "",
    timeEnd: "",
    classStartDate: "",
    classEndDate: "",
  };
};

const openEdit = (p: SchedulePattern) => {
  isEditing.value = true;
  showModal.value = true;
  form.value = { ...p };
};

const savePattern = async () => {
  try {
    if (isEditing.value && form.value.id) {
      await api.put(`/schedule-patterns/${form.value.id}`, form.value);
    } else {
      await api.post("/schedule-patterns", form.value);
    }
    showModal.value = false;
    await loadPatterns();
  } catch (e) {
    console.error(e);
    alert("Lỗi khi lưu pattern");
  }
};

const deletePattern = async (id: number) => {
  if (confirm("Bạn có chắc muốn xoá pattern này không?")) {
    try {
      await api.delete(`/schedule-patterns/${id}`);
      await loadPatterns();
    } catch (e) {
      console.error(e);
    }
  }
};

const selectPattern = () => {
  if (!selectedRoomId.value) {
    alert("Vui lòng chọn phòng!");
    return;
  }

  const selectedRoom = availableRooms.value.find(r => r.id === selectedRoomId.value);
  
  sessionStorage.setItem("selectedPattern", JSON.stringify(selectedPattern.value));
  sessionStorage.setItem("selectedRoom", JSON.stringify(selectedRoom));
  sessionStorage.setItem("openAddModal", "true");
  router.push({ name: "classschedule" });
};

onMounted(async () => {
  await loadPatterns();
});
</script>

<template>
  <div class="p-6">
    <div class="flex justify-between items-center mb-4">
      <h2 class="text-2xl font-semibold">Schedule Pattern List</h2>
      <button
        class="bg-blue-600 text-white px-4 py-2 rounded-lg"
        @click="openAdd"
      >
        + Add Pattern
      </button>
    </div>

    <table class="min-w-full bg-white border border-gray-200 rounded-lg shadow">
      <thead class="bg-gray-100 text-gray-700">
        <tr>
          <th class="py-2 px-4 border">#</th>
          <th class="py-2 px-4 border">Days</th>
          <th class="py-2 px-4 border">Start Time</th>
          <th class="py-2 px-4 border">End Time</th>
          <th class="py-2 px-4 border">Class Start</th>
          <th class="py-2 px-4 border">Class End</th>
          <th class="py-2 px-4 border">Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(p, index) in patterns" :key="p.id" class="text-center">
          <td class="border py-2 px-4">{{ index + 1 }}</td>
          <td class="border py-2 px-4">{{ p.daysOfWeek }}</td>
          <td class="border py-2 px-4">{{ formatTime(p.timeStart) }}</td>
          <td class="border py-2 px-4">{{ formatTime(p.timeEnd) }}</td>
          <td class="border py-2 px-4">{{ formatDate(p.classStartDate) }}</td>
          <td class="border py-2 px-4">{{ formatDate(p.classEndDate) }}</td>
          <td class="border py-2 px-4 space-x-2">
            <button
              class="bg-green-500 text-white px-3 py-1 rounded"
              @click="showSelectRoomModal(p)"
            >
              Select
            </button>
            <button
              class="bg-blue-500 text-white px-3 py-1 rounded"
              @click="openEdit(p)"
            >
              Edit
            </button>
            <button
              class="bg-red-500 text-white px-3 py-1 rounded"
              @click="deletePattern(p.id!)"
            >
              Delete
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <div
      v-if="showModal"
      class="fixed inset-0 flex items-center justify-center bg-black/40"
    >
      <div class="bg-white rounded-xl p-6 w-[500px] shadow-lg">
        <h3 class="text-xl font-semibold mb-4">
          {{ isEditing ? "Edit Pattern" : "Add Pattern" }}
        </h3>

        <div class="grid gap-3">
          <input
            v-model="form.daysOfWeek"
            class="border rounded px-3 py-2"
            placeholder="Days (e.g. MONDAY,WEDNESDAY,FRIDAY)"
          />
          <input
            v-model="form.timeStart"
            class="border rounded px-3 py-2"
            placeholder="Start Time (HH:mm)"
            type="time"
          />
          <input
            v-model="form.timeEnd"
            class="border rounded px-3 py-2"
            placeholder="End Time (HH:mm)"
            type="time"
          />
          <input
            v-model="form.classStartDate"
            class="border rounded px-3 py-2"
            type="date"
          />
          <input
            v-model="form.classEndDate"
            class="border rounded px-3 py-2"
            type="date"
          />
        </div>

        <div class="flex justify-end gap-3 mt-5">
          <button
            class="px-4 py-2 bg-gray-400 text-white rounded"
            @click="showModal = false"
          >
            Cancel
          </button>
          <button
            class="px-4 py-2 bg-blue-600 text-white rounded"
            @click="savePattern"
          >
            Save
          </button>
        </div>
      </div>
    </div>

    <div
      v-if="selectedPattern"
      class="fixed inset-0 flex items-center justify-center bg-black/40 z-50"
    >
      <div class="bg-white rounded-xl p-6 w-[500px] shadow-lg">
        <h3 class="text-xl font-semibold mb-4">Select Room for Pattern</h3>
        
        <div class="mb-4">
          <label class="block mb-1 font-semibold">Pattern Information:</label>
          <div class="bg-gray-50 p-3 rounded">
            <p><strong>Days:</strong> {{ selectedPattern.daysOfWeek }}</p>
            <p><strong>Time:</strong> {{ formatTime(selectedPattern.timeStart) }} - {{ formatTime(selectedPattern.timeEnd) }}</p>
            <p><strong>Period:</strong> {{ formatDate(selectedPattern.classStartDate) }} - {{ formatDate(selectedPattern.classEndDate) }}</p>
          </div>
        </div>

        <div class="mb-4">
          <label class="block mb-2 font-semibold">Select Available Room:</label>
          
          <div v-if="isLoadingRooms" class="text-gray-500 italic py-2">
             Searching for empty rooms...
          </div>

          <select 
            v-else
            v-model="selectedRoomId" 
            class="w-full border rounded px-3 py-2"
            :class="{ 'border-red-500': !selectedRoomId }"
            :disabled="availableRooms.length === 0"
          >
            <option :value="null" disabled>
               {{ availableRooms.length > 0 ? 'Please select a room' : 'No rooms available' }}
            </option>
            <option 
              v-for="room in availableRooms" 
              :key="room.id" 
              :value="room.id"
            >
              {{ room.name }}
              <span v-if="room.location"> - {{ room.location }}</span>
            </option>
          </select>

          <p v-if="!isLoadingRooms && availableRooms.length === 0" class="text-red-500 text-sm mt-2">
            ⚠️ All rooms are occupied at this time/date range.
          </p>
        </div>

        <div class="flex justify-end gap-3 mt-5">
          <button
            class="px-4 py-2 bg-gray-400 text-white rounded"
            @click="selectedPattern = null"
          >
            Cancel
          </button>
          <button
            class="px-4 py-2 bg-blue-600 text-white rounded"
            @click="selectPattern"
            :disabled="isLoadingRooms || !selectedRoomId"
            :class="{'opacity-50 cursor-not-allowed': isLoadingRooms || !selectedRoomId}"
          >
            Confirm
          </button>
        </div>
      </div>
    </div>
  </div>
</template>