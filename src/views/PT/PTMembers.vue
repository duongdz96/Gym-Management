<script setup>
import { ref, onMounted } from "vue";

const members = ref([
  {
    id: 1,
    name: "Nguyễn Thị A",
    email: "a@example.com",
    membership: "Gói 3 tháng",
    status: "Active",
    initialWeight: 65,
    currentWeight: 62,
    height: 165,
  },
  {
    id: 2,
    name: "Trần Văn B",
    email: "b@example.com",
    membership: "Gói 6 tháng",
    status: "Active",
    initialWeight: 70,
    currentWeight: 68,
    height: 175,
  },
  {
    id: 3,
    name: "Lê Thị C",
    email: "c@example.com",
    membership: "Gói 1 tháng",
    status: "Expired",
    initialWeight: 60,
    currentWeight: 58,
    height: 160,
  },
  {
    id: 4,
    name: "Phạm Văn D",
    email: "d@example.com",
    membership: "Gói 12 tháng",
    status: "Active",
    initialWeight: 75,
    currentWeight: 72,
    height: 180,
  },
  {
    id: 5,
    name: "Hoàng Thị E",
    email: "e@example.com",
    membership: "Gói 6 tháng",
    status: "Expired",
    initialWeight: 55,
    currentWeight: 54,
    height: 155,
  },
  {
    id: 6,
    name: "Đỗ Văn F",
    email: "f@example.com",
    membership: "Gói 3 tháng",
    status: "Active",
    initialWeight: 80,
    currentWeight: 77,
    height: 170,
  },
  {
    id: 7,
    name: "Bùi Thị G",
    email: "g@example.com",
    membership: "Gói 1 tháng",
    status: "Expired",
    initialWeight: 62,
    currentWeight: 61,
    height: 162,
  },
  {
    id: 8,
    name: "Vũ Văn H",
    email: "h@example.com",
    membership: "Gói 6 tháng",
    status: "Active",
    initialWeight: 68,
    currentWeight: 65,
    height: 168,
  },
]);

const selectedMember = ref(null);
const isEditingWeight = ref(false);
const tempCurrentWeight = ref(0);

const selectMember = (member) => {
  selectedMember.value = member;
  isEditingWeight.value = false;
  tempCurrentWeight.value = member.currentWeight;
};

const startEditingWeight = () => {
  if (selectedMember.value && selectedMember.value.status === "Active") {
    isEditingWeight.value = true;
  }
};

const saveCurrentWeight = () => {
  if (selectedMember.value && selectedMember.value.status === "Active") {
    selectedMember.value.currentWeight = tempCurrentWeight.value;
    isEditingWeight.value = false;
  }
};

const cancelEditingWeight = () => {
  tempCurrentWeight.value = selectedMember.value.currentWeight;
  isEditingWeight.value = false;
};

onMounted(() => {
  console.log("PT Members loaded");
  // Auto select first member
  if (members.value.length > 0) {
    selectedMember.value = members.value[0];
    tempCurrentWeight.value = members.value[0].currentWeight;
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
        <div class="space-y-3">
          <div
            v-for="member in members"
            :key="member.id"
            @click="selectMember(member)"
            :class="[
              'p-4 rounded-lg cursor-pointer transition-colors border',
              selectedMember?.id === member.id
                ? 'bg-blue-100 border-blue-300'
                : 'bg-gray-50 hover:bg-gray-100',
              member.status === 'Expired' ? 'opacity-50' : '',
            ]"
          >
            <div class="font-medium text-gray-900">{{ member.name }}</div>
            <div class="text-sm text-gray-500">{{ member.email }}</div>
            <div class="text-sm text-gray-500">{{ member.membership }}</div>
            <span
              :class="
                member.status === 'Active'
                  ? 'text-green-600 bg-green-100'
                  : 'text-red-600 bg-red-100'
              "
              class="px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full mt-2"
            >
              {{ member.status }}
            </span>
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
                >Membership Package</label
              >
              <p class="mt-1 text-lg text-gray-900">
                {{ selectedMember.membership }}
              </p>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700"
                >Initial Weight</label
              >
              <p class="mt-1 text-lg text-gray-900">
                {{ selectedMember.initialWeight }} kg
              </p>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700"
                >Current Weight</label
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
                  >{{ selectedMember.currentWeight }} kg</span
                >
                <button
                  v-if="!isEditingWeight && selectedMember.status === 'Active'"
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
          <!-- Có thể thêm biểu đồ hoặc thông tin khác -->
          <div class="mt-6">
            <h3 class="text-lg font-medium text-gray-900 mb-2">
              Weight Progress
            </h3>
            <div class="bg-gray-100 rounded-lg p-4">
              <div class="flex justify-between text-sm text-gray-600">
                <span>Ban đầu: {{ selectedMember.initialWeight }} kg</span>
                <span>Hiện tại: {{ selectedMember.currentWeight }} kg</span>
              </div>
              <div class="mt-2 bg-gray-200 rounded-full h-4">
                <div
                  class="bg-blue-500 h-4 rounded-full"
                  :style="{
                    width: `${Math.max(
                      0,
                      ((selectedMember.initialWeight -
                        selectedMember.currentWeight) /
                        selectedMember.initialWeight) *
                        100
                    )}%`,
                  }"
                ></div>
              </div>
              <p class="mt-2 text-sm text-gray-600">
                Lost
                {{
                  selectedMember.initialWeight - selectedMember.currentWeight
                }}
                kg
              </p>
            </div>
          </div>
        </div>
        <div v-else class="text-center text-gray-500">
          Select a member to view detailed information
        </div>
      </div>
    </div>
  </div>
</template>
