<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useToast } from "vue-toastification";
import api from '@/services/api'

type ClassTemplateInfo = {
  id: number;
  name: string;
};

type Room = {
  id: number;
  name: string;
  note?: string;
  location?: string;
};

type ClassSchedule = {
  id: number;
  startTime: string;
  endTime: string;
  location: string;
  status: "OPEN" | "CLOSED" | "CANCELLED";
  classTemplate: ClassTemplateInfo;
  schedulePattern?: SchedulePattern;
  room: Room;
};

type SchedulePattern = {
  id?: number;
  daysOfWeek: string;
  timeStart: string;
  timeEnd: string;
  classStartDate: string;
  classEndDate: string;
};

// ===== STATE =====
const schedules = ref<ClassSchedule[]>([]);
const selectedTemplate = ref<ClassTemplateInfo | null>(null);
const schedulePattern = ref<SchedulePattern | null>(null);
const selectedRoom = ref<Room | null>(null);
const isLoading = ref(true);
const error = ref<string | null>(null);
const isAddModalOpen = ref(false);
const isBatchModalOpen = ref(false);
const toast = useToast();

const form = ref({
  classTemplateId: null as number | null,
  schedulePatternId: null as number | null,
  startTime: "",
  endTime: "",
  location: "",
  status: "OPEN",
});

const batchForm = ref({
  classTemplateId: null as number | null,
  schedulePatternId: null as number | null,
  startDate: "",
  endDate: "",
  daysOfWeek: [] as string[],
});

const router = useRouter();

// ====== ON MOUNT ======
onMounted(async () => {
  console.log("📘 ClassSchedule setup()");
  try {
    isLoading.value = true;
    error.value = null;

    // --- Lấy Template ---
    const storedTemplateJSON = sessionStorage.getItem("selectedTemplate");
    if (!storedTemplateJSON) {
      error.value = "Please select a Class Template first.";
      return;
    }

    const parsedTemplate = JSON.parse(storedTemplateJSON);
    if (!parsedTemplate?.id) {
      error.value = "Invalid Class Template data.";
      return;
    }

    selectedTemplate.value = parsedTemplate;
    form.value.classTemplateId = parsedTemplate.id;
    batchForm.value.classTemplateId = parsedTemplate.id;

    // --- Lấy Pattern và Room (nếu có) ---
    const storedPatternJSON = sessionStorage.getItem("selectedPattern");
    if (storedPatternJSON) {
      try {
        const pattern = JSON.parse(storedPatternJSON);
        if (pattern?.id) {
          schedulePattern.value = pattern;
          form.value.schedulePatternId = pattern.id ?? null;
          batchForm.value.schedulePatternId = pattern.id ?? null;
          console.log("Loaded selected pattern:", pattern);
        }
      } catch (e) {
        console.error("Failed to parse selectedPattern:", e);
      } finally {
        sessionStorage.removeItem("selectedPattern");
      }
    }

    const storedRoomJSON = sessionStorage.getItem("selectedRoom");
    if (storedRoomJSON) {
      try {
        const room = JSON.parse(storedRoomJSON);
        if (room?.id) {
          selectedRoom.value = room;
          console.log("Loaded selected room:", room);
        }
      } catch (e) {
        console.error("Failed to parse selectedRoom:", e);
      } finally {
        sessionStorage.removeItem("selectedRoom");
      }
    }

    // --- Kiểm tra modal mở ---
    if (sessionStorage.getItem("openAddModal") === "true") {
      isBatchModalOpen.value = true;
      sessionStorage.removeItem("openAddModal");
    }

    // --- Lấy danh sách schedule ---
    const res = await api.get(`/classschedule/by-template/${parsedTemplate.id}`);
    schedules.value = res.data || [];
    console.log("Loaded schedules:", schedules.value);

  } catch (err) {
    console.error("Failed to fetch schedules:", err);
    error.value = "Failed to load schedule data.";
  } finally {
    isLoading.value = false;
  }
});

// ====== HELPER ======
const formatDateTime = (isoString: string) => {
  if (!isoString) return "N/A";
  return new Date(isoString).toLocaleString("vi-VN");
};

// ====== ACTIONS ======
async function AddNewSchedule() {
  if (!selectedTemplate.value?.id) {
    alert("Please select a class template first!");
    return;
  }

  try {
    const payload = {
      classTemplate: { id: selectedTemplate.value.id },
      startTime: form.value.startTime,
      endTime: form.value.endTime,
      location: form.value.location,
      status: form.value.status || "OPEN",
      schedulePatternId: null,
    };

    console.log("📤 Sending single schedule:", payload);
    await api.post("/classschedule", payload);

    alert("✅ Added new class schedule successfully!");
    isAddModalOpen.value = false;
    await reloadSchedules();

    resetForm(form.value);
  } catch (err) {
    console.error("❌ Failed to add schedule:", err);
    alert("Failed to add schedule!");
  }
}

// ====== ADD MULTIPLE ======
async function AddBatchSchedules() {
  if (!selectedTemplate.value?.id) {
    alert("Please select a class template first!");
    return;
  }

  if (!selectedRoom.value?.id) {
    alert("Please select a room first!");
    return;
  }

  try {
    const payload = {
      classTemplate: { id: selectedTemplate.value.id },
      schedulePattern: { id: batchForm.value.schedulePatternId },
      room: { id: selectedRoom.value.id },
    };

    console.log("📤 Sending batch schedules:", payload);
    await api.post("/classschedule/generate", payload);
    toast.success("Added batch class schedules successfully!");
    isBatchModalOpen.value = false;
    await reloadSchedules();

    resetForm(batchForm.value);
  } catch (err) {
    console.error("Failed to add batch schedule:", err);
    toast.error("Failed to add batch schedule!");
  }
}

// ====== UTIL ======
function resetForm(obj: Record<string, any>) {
  for (const key in obj) {
    if (typeof obj[key] === "string") obj[key] = "";
    if (Array.isArray(obj[key])) obj[key] = [];
    if (typeof obj[key] === "number" || obj[key] === null) obj[key] = null;
  }
}

async function reloadSchedules() {
  if (!selectedTemplate.value?.id) return;
  const res = await api.get(`/classschedule/by-template/${selectedTemplate.value.id}`);
  schedules.value = res.data || [];
}

const isDayInPattern = (shortDay) => {
  if (!schedulePattern.value?.daysOfWeek) return false;
  const patternDays = schedulePattern.value.daysOfWeek
    .split(',')
    .map(d => d.trim().slice(0, 3).toUpperCase()); // Lấy 3 ký tự đầu: MON, WED, FRI
  return patternDays.includes(shortDay);
};

</script>
<template>
  <div class="p-4 md:p-6">
    <div class="bg-white p-6 rounded-lg shadow-md">
      <div class="flex flex-col md:flex-row justify-between items-center mb-4 border-b pb-4">
        <div>
          <button
            @click="router.push({ name: 'classtemplate' })"
            class="text-sm text-blue-600 hover:underline mb-2"
          >
            &larr; Back to Templates
          </button>
          <h1 v-if="selectedTemplate" class="text-2xl font-bold text-gray-800">
            Schedules for:
            <span class="text-blue-700">{{ selectedTemplate.name }}</span>
          </h1>
        </div>

        <div class="flex gap-2">
          <button
            @click="isAddModalOpen = true"
            class="mt-2 md:mt-0 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          >
            Add Class
          </button>
          <button
            @click="isBatchModalOpen = true"
            class="mt-2 md:mt-0 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          >
            Add Course
          </button>
        </div>
      </div>

      <div v-if="isLoading" class="text-center py-8 text-gray-500">Loading...</div>
      <div v-if="error" class="text-center py-8 text-red-600 bg-red-50 p-4 rounded-lg">
        {{ error }}
      </div>

      <div v-else class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Location</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Start Time</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">End Time</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
              <th class="px-6 py-3"></th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-if="schedules.length === 0">
              <td colspan="6" class="px-6 py-8 text-center text-gray-500">
                No schedules found for this template.
              </td>
            </tr>
            <tr v-for="schedule in schedules" :key="schedule.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 text-sm font-medium text-gray-900">{{ schedule.id }}</td>
              <td class="px-6 py-4 text-sm text-gray-700">{{ schedule.room.name }}</td>
              <td class="px-6 py-4 text-sm text-gray-700">{{ formatDateTime(schedule.startTime) }}</td>
              <td class="px-6 py-4 text-sm text-gray-700">{{ formatDateTime(schedule.endTime) }}</td>
              <td class="px-6 py-4 text-sm">
                <span
                  class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full"
                  :class="{
                    'bg-green-100 text-green-800': schedule.status === 'OPEN',
                    'bg-gray-100 text-gray-800': schedule.status === 'CLOSED',
                    'bg-red-100 text-red-800': schedule.status === 'CANCELLED',
                  }"
                >
                  {{ schedule.status }}
                </span>
              </td>
              <td class="px-6 py-4 text-right text-sm">
                <a href="#" class="text-indigo-600 hover:text-indigo-900 mr-3">Edit</a>
                <a href="#" class="text-red-600 hover:text-red-900">Delete</a>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>

  <!-- ========== MODAL 1: ADD SINGLE CLASS ========== -->
  <div
    v-if="isAddModalOpen"
    class="fixed inset-0 flex justify-center items-center bg-opacity-50 z-50"
  >
    <div class="bg-white rounded-lg shadow-lg w-full max-w-md p-6">
      <h2 class="text-xl font-bold mb-4">
        Add Class Schedule for {{ selectedTemplate.name }}
      </h2>

      <input
        v-model="form.startTime"
        type="datetime-local"
        placeholder="Start Time"
        class="border p-2 rounded w-full mb-2"
      />
      <input
        v-model="form.endTime"
        type="datetime-local"
        placeholder="End Time"
        class="border p-2 rounded w-full mb-2"
      />
      <input
        v-model="form.location"
        type="text"
        placeholder="Location"
        class="border p-2 rounded w-full mb-2"
      />
      <select v-model="form.status" class="border p-2 w-full rounded">
        <option value="OPEN">OPEN</option>
        <option value="CLOSED">CLOSED</option>
        <option value="CANCELLED">CANCELLED</option>
      </select>

      <div class="flex justify-end space-x-3 mt-4">
        <button
          @click="isAddModalOpen = false"
          class="px-4 py-2 rounded bg-gray-200 hover:bg-gray-300"
        >
          Cancel
        </button>
        <button
          @click="AddNewSchedule()"
          class="px-4 py-2 rounded bg-blue-600 text-white hover:bg-blue-700"
        >
          Save
        </button>
      </div>
    </div>
  </div>

  <!-- ========== MODAL 2: ADD BATCH ========== -->
  <div
  v-if="isBatchModalOpen"
  class="fixed inset-0 flex items-center justify-center bg-opacity-50 z-50"
>
  <div class="bg-white rounded-lg shadow-lg w-full max-w-md p-6">
    <h2 class="text-xl font-bold mb-4">
      Add Class Course for {{ selectedTemplate?.name || 'Template' }}
    </h2>


    <RouterLink
      :to="{ name: 'schedulepattern' }"
      class="block w-full mb-3 px-3 py-2 rounded bg-green-600 text-white hover:bg-green-700 text-center"
    >
      Choose the time for the whole course
    </RouterLink>

    <!-- <div
      v-if="batchForm.schedulePatternId"
      class="mb-3 bg-gray-100 p-3 rounded border border-gray-200"
    >
      <strong>Selected Pattern:</strong>
      <div>ID #{{ batchForm.schedulePatternId }}</div>
    </div> -->

    <div class="mb-3">
      <label class="block mb-1 font-semibold">Class Start Date:</label>
      <p>{{ schedulePattern?.classStartDate || 'Please select time' }}</p>
    </div>

    <div class="mb-3">
      <label class="block mb-1 font-semibold">End Date:</label>
      <p>{{ schedulePattern?.classEndDate || 'Please select time' }}</p>
    </div>

    <!-- Days of week -->
    <div class="mb-3">
      <label class="block mb-1 font-semibold">Days of Week:</label>
      <div class="flex flex-wrap gap-2">
        <label
          v-for="day in ['MON','TUE','WED','THU','FRI','SAT','SUN']"
          :key="day"
          class="flex items-center"
        >
          <input
            type="checkbox"
            :checked="isDayInPattern(day)"
            disabled
            class="mr-1 accent-green-600 cursor-not-allowed"
          />
          {{ day }}
        </label>
      </div>
    </div>

    <!-- Room -->
    <div class="mb-3">
      <label class="block mb-1 font-semibold">Selected Room:</label>
      <div class="bg-blue-50 p-3 rounded border border-blue-200">
        <p v-if="selectedRoom" class="font-semibold">{{ selectedRoom.name }}</p>
        <p v-else class="text-red-500">No room selected</p>
        <p v-if="selectedRoom?.location" class="text-sm text-gray-600">{{ selectedRoom.location }}</p>
      </div>
    </div>

    <!-- Buttons -->
    <div class="flex justify-end mt-4">
      <button
        @click="isBatchModalOpen = false"
        class="mr-2 px-4 py-2 bg-gray-400 hover:bg-gray-500 text-white rounded"
      >
        Cancel
      </button>
      <button
        @click="AddBatchSchedules()"
        class="px-4 py-2 bg-green-600 hover:bg-green-700 text-white rounded"
      >
        Generate
      </button>
    </div>
  </div>
</div>
</template>
