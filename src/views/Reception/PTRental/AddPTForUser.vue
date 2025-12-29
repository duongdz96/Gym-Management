<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import api from "../../../services/api";
import { useToast } from "vue-toastification";

// Định nghĩa Member
type Member = {
  id: number;
  fullName: string;
  email: string;
  phone?: string;
};

// Định nghĩa Product cho Gói PT
type ProductPT = {
  id: number;
  name: string;
  type: string;
  price: number;
  importPrice: number | null;
  brand: string | null;
  quantity: number | null;
  status: boolean;
};

// Định nghĩa Staff (PT)
type Staff = {
  id: number;
  fullName: string;
  role?: string;
};

const members = ref<Member[]>([]);
const ptProducts = ref<ProductPT[]>([]);
const ptStaffs = ref<Staff[]>([]);
const selectedMember = ref<Member | null>(null);
const searchMember = ref("");
const toast = useToast();

// Form fields
const selectedPTProduct = ref<ProductPT | null>(null);
const startDate = ref("");
const numberOfSessions = ref(1);
const selectedPTStaff = ref<Staff | null>(null);

onMounted(async () => {
  try {
    // Load members
    const memberRes = await api.get("/member");
    members.value = memberRes.data;

    // Load PT products
    const productRes = await api.get("/product");
    ptProducts.value = productRes.data.filter((p: ProductPT) => p.type === "PT");

    // Load PT staffs
    const staffRes = await api.get("/staff");
    ptStaffs.value = staffRes.data; // Assuming all staffs, or filter if needed
  } catch (err) {
    console.error("Error fetching data:", err);
  }
});

// Computed
const filteredMembers = computed(() => {
  return members.value.filter(m =>
    m.fullName.toLowerCase().includes(searchMember.value.toLowerCase()) ||
    m.email.toLowerCase().includes(searchMember.value.toLowerCase())
  );
});

// Functions
const selectMember = (member: Member) => {
  selectedMember.value = member;
  // Reset form
  selectedPTProduct.value = null;
  startDate.value = "";
  numberOfSessions.value = 1;
  selectedPTStaff.value = null;
};

const submitPTAssignment = async () => {
  if (!selectedMember.value || !selectedPTProduct.value || !startDate.value || !selectedPTStaff.value) {
    toast.warning("Vui lòng điền vào tất cả các trường!");
    return;
  }

  try {
    const payload = {
      member: { id: selectedMember.value.id },
      ptPackage: { id: selectedPTProduct.value.id },
      staff: { id: selectedPTStaff.value.id },
      startDate: startDate.value,
      numberOfSessions: numberOfSessions.value,
      status: "active"
    };

    await api.post("/ptPackageIssued", payload);
    toast.success("Thêm gói PT thành công!");
    // Reset
    selectedMember.value = null;
  } catch (err) {
    console.error("Error assigning PT:", err);
    toast.error("Thêm gói PT thất bại.");
  }
};
</script>

<template>
  <div class="p-6 bg-gray-100 min-h-screen">
    <div class="max-w-7xl mx-auto flex flex-col lg:flex-row gap-6">
      <!-- Bên trái: Danh sách Members -->
      <div class="lg:w-1/2 bg-white rounded-xl shadow-lg overflow-hidden">
        <div class="p-6 border-b border-gray-200">
          <h2 class="text-2xl font-bold text-gray-800 mb-4">Select a Member</h2>
          <div class="relative">
            <input
              type="text"
              v-model="searchMember"
              placeholder="Search member by name or email..."
              class="w-full pl-12 pr-4 py-3 border border-gray-300 rounded-lg bg-gray-50 text-gray-800
                     focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500
                     transition duration-200 ease-in-out"
            />
            <svg class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" width="20" height="20" fill="currentColor" viewBox="0 0 24 24">
              <path d="M15.5 14h-.79l-.28-.27A6.471 6.471 0 0016 9.5a6.5 6.5 0 10-6.5 6.5c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
            </svg>
          </div>
        </div>

        <div class="p-6 max-h-96 overflow-y-auto">
          <div v-if="filteredMembers.length > 0" class="space-y-3">
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
              <div class="font-medium text-gray-900">{{ member.fullName }}</div>
              <div class="text-sm text-gray-500">{{ member.email }}</div>
              <div v-if="member.phone" class="text-sm text-gray-500">{{ member.phone }}</div>
            </div>
          </div>
          <div v-else class="text-center text-gray-500 py-10">
            No members found.
          </div>
        </div>
      </div>

      <!-- Bên phải: Form thêm PT -->
      <div class="lg:w-1/2 bg-white rounded-xl shadow-lg flex flex-col">
        <h2 class="text-2xl font-bold text-gray-800 p-6 border-b border-gray-200">Assign PT Package</h2>

        <div class="flex-1 p-6">
          <div v-if="selectedMember" class="space-y-6">
            <div class="bg-blue-50 p-4 rounded-lg">
              <h3 class="text-lg font-semibold text-gray-900">Selected Member</h3>
              <p class="text-gray-700">{{ selectedMember.fullName }}</p>
              <p class="text-gray-600">{{ selectedMember.email }}</p>
            </div>

            <form @submit.prevent="submitPTAssignment" class="space-y-4">
              <!-- PT Package -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">PT Package</label>
                <select
                  v-model="selectedPTProduct"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
                >
                  <option value="">Select a PT Package</option>
                  <option v-for="pt in ptProducts" :key="pt.id" :value="pt">
                    {{ pt.name }} - {{ pt.price.toLocaleString() }} đ
                  </option>
                </select>
              </div>

              <!-- Start Date -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Start Date</label>
                <input
                  type="date"
                  v-model="startDate"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
                />
              </div>

              <!-- Number of Sessions -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Number of Sessions</label>
                <input
                  type="number"
                  v-model.number="numberOfSessions"
                  min="1"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
                />
              </div>

              <!-- Assigned PT Staff -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Assigned PT Staff</label>
                <select
                  v-model="selectedPTStaff"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
                >
                  <option value="">Select PT Staff</option>
                  <option v-for="staff in ptStaffs" :key="staff.id" :value="staff">
                    {{ staff.fullName }}
                  </option>
                </select>
              </div>

              <!-- Submit Button -->
              <button
                type="submit"
                class="w-full px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-green-500"
              >
                Assign PT Package
              </button>
            </form>
          </div>
          <div v-else class="text-center text-gray-500 py-10">
            Select a member to assign a PT package.
          </div>
        </div>
      </div>
    </div>
  </div>
</template>