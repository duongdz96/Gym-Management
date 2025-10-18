<script setup lang="ts">
import { ref, onMounted } from "vue";
import api from "@/services/api"
import { useRouter } from "vue-router";

const router = useRouter();
// =================== STATE ===================
interface SchedulePattern {
  id?: number;
  location: string;
  daysOfWeek: string;
  timeStart: string;
  timeEnd: string;
  classStartDate: string;
  classEndDate: string;
}

const patterns = ref<SchedulePattern[]>([]);
const showModal = ref(false);
const isEditing = ref(false);
const form = ref<SchedulePattern>({
  location: "",
  daysOfWeek: "",
  timeStart: "",
  timeEnd: "",
  classStartDate: "",
  classEndDate: "",
});
const selectedId = ref<number | null>(null);

const formatTime = (timeStr: string) => {
  if (!timeStr) return "";
  return timeStr.slice(0, 5); // cắt "09:00:00" -> "09:00"
};

const formatDate = (dateStr: string) => {
  if (!dateStr) return "";
  const d = new Date(dateStr);
  return d.toLocaleDateString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  }); // → "20/10/2025"
};



// =================== FETCH ===================
const loadPatterns = async () => {
  const res = await api.get("/schedule-patterns");
  patterns.value = res.data;
};

// =================== CRUD ===================
const openAdd = () => {
  isEditing.value = false;
  showModal.value = true;
  form.value = {
    location: "",
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
  if (isEditing.value && form.value.id) {
    await api.put(`/schedule-patterns/${form.value.id}`, form.value);
  } else {
    await api.post("/schedule-patterns", form.value);
  }
  showModal.value = false;
  await loadPatterns();
};

const deletePattern = async (id: number) => {
  if (confirm("Bạn có chắc muốn xoá pattern này không?")) {
    await api.delete(`/schedule-patterns/${id}`);
    await loadPatterns();
  }
};

const selectPattern = (pattern) => {
  sessionStorage.setItem("selectedPattern", JSON.stringify(pattern));
  sessionStorage.setItem("openAddModal", "true");
  router.push({ name: "classschedule" });
};

onMounted(loadPatterns);
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

    <!-- Table -->
    <table class="min-w-full bg-white border border-gray-200 rounded-lg shadow">
      <thead class="bg-gray-100 text-gray-700">
        <tr>
          <th class="py-2 px-4 border">#</th>
          <th class="py-2 px-4 border">Location</th>
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
          <td class="border py-2 px-4">{{ p.location }}</td>
          <td class="border py-2 px-4">{{ p.daysOfWeek }}</td>
          <td class="border py-2 px-4">{{ formatTime(p.timeStart) }}</td>
          <td class="border py-2 px-4">{{ formatTime(p.timeEnd) }}</td>
          <td class="border py-2 px-4">{{ formatDate(p.classStartDate) }}</td>
          <td class="border py-2 px-4">{{ formatDate(p.classEndDate) }}</td>
          <td class="border py-2 px-4 space-x-2">
            <button
              class="bg-green-500 text-white px-3 py-1 rounded"
              @click="selectPattern(p)"
            >
              Select
            </button>
            <button
              class="bg-yellow-500 text-white px-3 py-1 rounded"
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

    <!-- Modal Add/Edit -->
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
            v-model="form.location"
            class="border rounded px-3 py-2"
            placeholder="Location"
          />
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
  </div>
</template>
