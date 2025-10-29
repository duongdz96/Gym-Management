<script setup>
import { ref, computed, onMounted, watch } from "vue";
import api from "@/services/api";
import { useAuthStore } from "@/stores/useAuthStore";

const authStore = useAuthStore();

const searchQuery = ref("");
const members = ref([]);

const selectedMember = ref(null);
const isEditingWeight = ref(false);
const tempCurrentWeight = ref(0);

const filteredMembers = computed(() => {
  if (!searchQuery.value) {
    return members.value;
  }
  return members.value.filter(member =>
    member.name.toLowerCase().includes(searchQuery.value.toLowerCase())
  );
});

const selectMember = (member) => {
  selectedMember.value = member;
  isEditingWeight.value = false;
  tempCurrentWeight.value = member.weight;
};

const startEditingWeight = () => {
  if (selectedMember.value) {
    isEditingWeight.value = true;
  }
};

const saveCurrentWeight = async () => {
  if (selectedMember.value) {
    try {
      const payload = {
        weight: tempCurrentWeight.value
      };
      await api.put(`/studentprofile/${selectedMember.value.id}`, payload);
      selectedMember.value.weight = tempCurrentWeight.value;
      isEditingWeight.value = false;
      alert("Weight updated successfully!");
    } catch (error) {
      console.error("Error updating weight:", error);
      alert("Failed to update weight. Please try again.");
    }
  }
};

const cancelEditingWeight = () => {
  tempCurrentWeight.value = selectedMember.value.currentWeight;
  isEditingWeight.value = false;
};

// Watch for changes in filtered members to update selected member
watch(filteredMembers, (newFilteredMembers) => {
  if (selectedMember.value) {
    // Check if current selected member is still in filtered list
    const stillExists = newFilteredMembers.some(member => member.id === selectedMember.value.id);
    if (!stillExists) {
      // If not, select the first available member
      if (newFilteredMembers.length > 0) {
        selectedMember.value = newFilteredMembers[0];
        tempCurrentWeight.value = newFilteredMembers[0].weight;
      } else {
        selectedMember.value = null;
      }
    }
  } else if (newFilteredMembers.length > 0) {
    // If no member selected but filtered list has members, select first
    selectedMember.value = newFilteredMembers[0];
    tempCurrentWeight.value = newFilteredMembers[0].weight;
  }
});

onMounted(async () => {
  try {
    const res = await api.get("/studentprofile");
    const data = Array.isArray(res.data) ? res.data : [];
    // Filter profiles for the current PT
    const ptProfiles = data.filter(profile => profile.staff.id === authStore.user.id);
    // Map to member objects
    members.value = ptProfiles.map(profile => ({
      id: profile.id,
      name: profile.member.fullName,
      email: profile.member.email,
      height: profile.height,
      weight: profile.weight,
      trainingPlan: profile.trainingPlan,
    }));
    console.log("PT Members loaded:", members.value);
  } catch (error) {
    console.error('Failed to load PT members:', error);
    alert("Failed to load members. Please try again.");
  }

  // Auto select first member
  if (members.value.length > 0) {
    selectedMember.value = members.value[0];
    tempCurrentWeight.value = members.value[0].weight;
  }
});
</script>

<template>
  <div class="p-6 space-y-8">
    <!-- Title -->
    <h1 class="text-2xl font-bold text-stone-800">Member List</h1>

    <!-- Layout chính -->
    <div class="flex gap-6 h-screen">
      <!-- Bên trái: Danh sách học viên -->
      <div class="w-1/3 bg-white rounded-xl shadow p-5 overflow-y-auto">
        <h2 class="text-xl font-semibold mb-4">My Members</h2>

        <!-- Search Bar -->
        <div class="mb-4">
          <div class="relative">
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
              </svg>
            </div>
            <input
              v-model="searchQuery"
              type="text"
              placeholder="Search members by name..."
              class="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md leading-5 bg-white placeholder-gray-500 focus:outline-none focus:placeholder-gray-400 focus:ring-1 focus:ring-blue-500 focus:border-blue-500"
            />
          </div>
        </div>

        <div class="space-y-3">
          <div
            v-for="member in filteredMembers"
            :key="member.id"
            @click="selectMember(member)"
            :class="[
              'p-4 rounded-lg cursor-pointer transition-colors border',
              selectedMember?.id === member.id
                ? 'bg-blue-100 border-blue-300'
                : 'bg-gray-50 hover:bg-gray-100',
            ]"
          >
            <div class="font-medium text-gray-900">{{ member.name }}</div>
            <div class="text-sm text-gray-500">{{ member.email }}</div>
            <div class="text-sm text-gray-500">Height: {{ member.height }} cm</div>
            <div class="text-sm text-gray-500">Weight: {{ member.weight }} kg</div>
          </div>
        </div>
      </div>

      <!-- Bên phải: Thông tin chi tiết -->
      <div class="w-2/3 bg-white rounded-xl shadow p-5">
        <h2 class="text-xl font-semibold mb-4">Detailed Information</h2>
        <div v-if="selectedMember" class="space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700"
                >Full Name</label
              >
              <p class="mt-1 text-lg font-semibold text-gray-900">
                {{ selectedMember.name }}
              </p>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700"
                >Email</label
              >
              <p class="mt-1 text-lg text-gray-900">
                {{ selectedMember.email }}
              </p>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700"
                >Weight</label
              >
              <div class="mt-1 flex items-center space-x-2">
                <input
                  v-if="isEditingWeight"
                  v-model.number="tempCurrentWeight"
                  type="number"
                  class="text-lg text-gray-900 border border-gray-300 rounded px-2 py-1 w-20"
                  min="0"
                  step="0.1"
                />
                <span v-else class="text-lg text-gray-900"
                  >{{ selectedMember.weight }} kg</span
                >
                <button
                  v-if="!isEditingWeight"
                  @click="startEditingWeight"
                  class="text-blue-600 hover:text-blue-800 text-sm"
                >
                  Edit
                </button>
                <div v-if="isEditingWeight" class="flex space-x-1">
                  <button
                    @click="saveCurrentWeight"
                    class="text-green-600 hover:text-green-800 text-sm"
                  >
                    Save
                  </button>
                  <button
                    @click="cancelEditingWeight"
                    class="text-red-600 hover:text-red-800 text-sm"
                  >
                    Cancel
                  </button>
                </div>
              </div>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700"
                >Height</label
              >
              <p class="mt-1 text-lg text-gray-900">
                {{ selectedMember.height }} cm
              </p>
            </div>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700"
              >Training Plan</label
            >
            <p class="mt-1 text-lg text-gray-900">
              {{ selectedMember.trainingPlan }}
            </p>
          </div>

        </div>
        <div v-else class="text-center text-gray-500">
          Select a member to view detailed information
                    </div>
          </div>
        </div>

        <!-- No results message -->
        <div v-if="filteredMembers.length === 0 && searchQuery" class="text-center py-8 text-gray-500">
          <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
          </svg>
          <h3 class="mt-2 text-sm font-medium text-gray-900">No members found</h3>
          <p class="mt-1 text-sm text-gray-500">Try adjusting your search terms.</p>
        </div>
      </div>
</template>
