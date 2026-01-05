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
  membership?: string;
};

// Định nghĩa PT
type PT = {
  id: number;
  fullName: string;
  email: string;
  phone?: string;
  role?: string;
};

// Định nghĩa PT Package
type PTPackage = {
  id: number;
  name: string;
  sessions: number;
  status: string;
};

// Định nghĩa Package Issued
type PackageIssued = {
  id: number;
  member: Member;
  pt: PT;
  ptPackage: PTPackage;
  remainingSessions: number;
  issueDate?: string;
};

const members = ref<Member[]>([]);
const pts = ref<PT[]>([]);
const ptPackages = ref<PTPackage[]>([]);
const packageIssued = ref<PackageIssued[]>([]);
const selectedMember = ref<Member | null>(null);
const searchMember = ref("");
const toast = useToast();

// Form fields
const selectedPT = ref<PT | null>(null);
const selectedPackage = ref<PTPackage | null>(null);
const remainingSessions = ref<number | null>(null);

onMounted(async () => {
  try {
    // Load members
    const memberRes = await api.get("/members");
    members.value = memberRes.data;

    // Load PTs
    const ptsRes = await api.get("/pts");
    pts.value = ptsRes.data;

    // Load PT packages
    const packagesRes = await api.get("/ptpackage");
    ptPackages.value = packagesRes.data;

    // Load existing package issued
    const issuedRes = await api.get("/packageissued");
    packageIssued.value = issuedRes.data;
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

// Check if member already has a PT package assigned
const getMemberPackage = (memberId: number) => {
  return packageIssued.value.find(pkg => pkg.member.id === memberId);
};

// Functions
const selectMember = (member: Member) => {
  selectedMember.value = member;
  
  // Check if member already has a package and pre-fill the form
  const existingPackage = getMemberPackage(member.id);
  if (existingPackage) {
    selectedPT.value = existingPackage.pt;
    selectedPackage.value = existingPackage.ptPackage;
    remainingSessions.value = existingPackage.remainingSessions;
  } else {
    // Reset form
    selectedPT.value = null;
    selectedPackage.value = null;
    remainingSessions.value = null;
  }
};

const submitPTAssignment = async () => {
  // 1. Validate đầu vào
  if (!selectedMember.value || !selectedPT.value || !selectedPackage.value) {
    toast.warning("Vui lòng chọn đầy đủ Member, PT và Gói tập!");
    return;
  }

  if (!remainingSessions.value || remainingSessions.value <= 0) {
    toast.warning("Vui lòng chọn gói PT trước!");
    return;
  }

  try {
    const existingPackage = getMemberPackage(selectedMember.value.id);
    
    // Payload gửi đi
    const payload = {
      member: { id: selectedMember.value.id },
      pt: { id: selectedPT.value.id },
      ptPackage: { id: selectedPackage.value.id },
      remainingSessions: remainingSessions.value
    };

    if (existingPackage) {
      // 2. Update (PUT)
      await api.put(`/packageissued/${existingPackage.id}`, payload);
      toast.success("Cập nhật gói PT thành công!");
    } else {
      // 3. Create (POST)
      await api.post("/packageissued", payload);
      toast.success("Đăng ký gói PT thành công!");
    }
    
    // 4. Reload data
    const issuedRes = await api.get("/packageissued");
    packageIssued.value = issuedRes.data;
    
    // 5. Reset form (Giữ lại list nhưng clear selection)
    selectedMember.value = null;
    selectedPT.value = null;
    selectedPackage.value = null;
    remainingSessions.value = null;
    searchMember.value = ""; // Reset thanh tìm kiếm nếu muốn

  } catch (err) {
    console.error("Error assigning PT:", err);
    toast.error("Có lỗi xảy ra khi xử lý dữ liệu!");
  }
};
</script>

<template>
  <div class="p-6 bg-gray-100 min-h-screen">
    <div class="max-w-7xl mx-auto flex flex-col lg:flex-row gap-6">
      <!-- Bên trái: Danh sách Members -->
      <div class="lg:w-1/2 bg-white rounded-xl shadow-lg overflow-hidden">
        <div class="p-6 border-b border-gray-200">
          <h2 class="text-2xl font-bold text-gray-800 mb-4">Chọn Member</h2>
          <div class="relative">
            <input
              type="text"
              v-model="searchMember"
              placeholder="Tìm kiếm member theo tên hoặc email..."
              class="w-full pl-12 pr-4 py-3 border border-gray-300 rounded-lg bg-gray-50 text-gray-800
                     focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500
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
                  ? 'bg-emerald-100 border-emerald-300'
                  : 'bg-gray-50 hover:bg-gray-100',
              ]"
            >
              <div class="font-medium text-gray-900">{{ member.fullName }}</div>
              <div class="text-sm text-gray-500">{{ member.email }}</div>
              <div v-if="member.phone" class="text-sm text-gray-500">{{ member.phone }}</div>
            </div>
          </div>
          <div v-else class="text-center text-gray-500 py-10">
            Không tìm thấy member nào.
          </div>
        </div>
      </div>

      <!-- Bên phải: Form thêm PT -->
      <div class="lg:w-1/2 bg-white rounded-xl shadow-lg flex flex-col">
        <h2 class="text-2xl font-bold text-gray-800 p-6 border-b border-gray-200">Đăng ký PT cho Member</h2>

        <div class="flex-1 p-6">
          <div v-if="selectedMember" class="space-y-6">
            <div class="bg-emerald-50 p-4 rounded-lg border border-emerald-200">
              <h3 class="text-lg font-semibold text-gray-900">Member đã chọn</h3>
              <p class="text-gray-700">{{ selectedMember.fullName }}</p>
              <p class="text-gray-600">{{ selectedMember.email }}</p>
              <p v-if="selectedMember.membership" class="text-xs text-emerald-600 font-medium mt-1">
                Gói: {{ selectedMember.membership }}
              </p>
            </div>

            <form @submit.prevent="submitPTAssignment" class="space-y-4">
              <!-- Assigned PT -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Chọn PT</label>
                <select
                  v-model="selectedPT"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-emerald-500"
                  required
                >
                  <option :value="null">Chọn PT</option>
                  <option v-for="pt in pts" :key="pt.id" :value="pt">
                    {{ pt.fullName }} ({{ pt.email }})
                  </option>
                </select>
              </div>

              <!-- PT Package -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Chọn Gói PT</label>
                <select
                  v-model="selectedPackage"
                  @change="remainingSessions = selectedPackage?.sessions || null"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-emerald-500"
                  required
                >
                  <option :value="null">Chọn gói PT</option>
                  <option v-for="pkg in ptPackages" :key="pkg.id" :value="pkg">
                    {{ pkg.name }}
                  </option>
                </select>
              </div>

              <!-- Remaining Sessions -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Số buổi còn lại</label>
                <input
                  type="number"
                  v-model.number="remainingSessions"
                  min="1"
                  placeholder="Chọn gói PT để hiển thị số buổi"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md bg-gray-50 text-gray-700 cursor-not-allowed"
                  readonly
                />
                <p class="text-xs text-gray-500 mt-1">Tự động điền theo số buổi của gói PT đã chọn</p>
              </div>

              <!-- Submit Button -->
              <button
                type="submit"
                class="w-full px-4 py-3 bg-gradient-to-r from-emerald-600 to-teal-600 text-white rounded-lg font-semibold hover:shadow-lg transition-all"
              >
                {{ getMemberPackage(selectedMember.id) ? 'Cập nhật Gói PT' : 'Đăng ký Gói PT' }}
              </button>
            </form>
          </div>
          <div v-else class="text-center text-gray-500 py-10">
            Chọn member để đăng ký PT
          </div>
        </div>
      </div>
    </div>
  </div>
</template>