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

// Định nghĩa Membership
type Membership = {
  id: number;
  startDate: string;
  endDate: string;
  status: string;
  member: Member;
  membershipPlan: {
    id: number;
    name: string;
    duration: string;
  };
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
const memberships = ref<Membership[]>([]);
const selectedMember = ref<Member | null>(null);
const searchMember = ref("");
const toast = useToast();

// Form fields
const selectedPT = ref<PT | null>(null);
const selectedPackage = ref<PTPackage | null>(null);
const remainingSessions = ref<number | null>(null);

// Member's existing packages
const memberExistingPackages = ref<PackageIssued[]>([]);

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

    // Load memberships
    const membershipRes = await api.get("/membership");
    memberships.value = membershipRes.data;
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

const activePTPackages = computed(() => {
  return ptPackages.value.filter(pkg => pkg.status === "Active");
});

// Check if selected member's membership is valid
const selectedMemberMembership = computed(() => {
  if (!selectedMember.value) return null;
  return memberships.value.find(m => m.member.id === selectedMember.value!.id);
});

const isMembershipExpired = computed(() => {
  if (!selectedMemberMembership.value) return true; // No membership = expired
  return selectedMemberMembership.value.status === "Expired";
});

// Check if member already has a PT package assigned
const getMemberPackages = (memberId: number) => {
  return packageIssued.value.filter(pkg => pkg.member.id === memberId);
};

// Functions
const selectMember = (member: Member) => {
  selectedMember.value = member;
  
  // Load all packages for this member
  memberExistingPackages.value = getMemberPackages(member.id);
  
  // Reset form để đăng ký gói mới
  selectedPT.value = null;
  selectedPackage.value = null;
  remainingSessions.value = null;
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

  // Check if membership is expired
  if (isMembershipExpired.value) {
    toast.error("Không thể đăng ký gói PT! Gói membership đã hết hạn. Vui lòng gia hạn membership trước.");
    return;
  }

  try {
    // Payload gửi đi
    const payload = {
      member: { id: selectedMember.value.id },
      pt: { id: selectedPT.value.id },
      ptPackage: { id: selectedPackage.value.id },
      remainingSessions: remainingSessions.value
    };

    // Luôn tạo mới (cho phép member có nhiều gói PT)
    await api.post("/packageissued", payload);
    toast.success("Đăng ký gói PT thành công!");
    
    // Reload data
    const issuedRes = await api.get("/packageissued");
    packageIssued.value = issuedRes.data;
    
    // Reload member's packages
    memberExistingPackages.value = getMemberPackages(selectedMember.value.id);
    
    // Reset form
    selectedPT.value = null;
    selectedPackage.value = null;
    remainingSessions.value = null;

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
            <div :class="[
              'p-4 rounded-lg border',
              isMembershipExpired ? 'bg-red-50 border-red-200' : 'bg-emerald-50 border-emerald-200'
            ]">
              <h3 class="text-lg font-semibold text-gray-900">Member đã chọn</h3>
              <p class="text-gray-700">{{ selectedMember.fullName }}</p>
              <p class="text-gray-600">{{ selectedMember.email }}</p>
              <p v-if="selectedMember.membership" class="text-xs font-medium mt-1" :class="isMembershipExpired ? 'text-red-600' : 'text-emerald-600'">
                Gói: {{ selectedMember.membership }}
              </p>
              <div v-if="selectedMemberMembership" class="mt-2 pt-2 border-t" :class="isMembershipExpired ? 'border-red-200' : 'border-emerald-200'">
                <p class="text-xs text-gray-600">Gói membership: {{ selectedMemberMembership.membershipPlan.name }}</p>
                <p class="text-xs text-gray-600">Hạn sử dụng: {{ new Date(selectedMemberMembership.endDate).toLocaleDateString('vi-VN') }}</p>
                <p class="text-xs font-semibold mt-1" :class="isMembershipExpired ? 'text-red-600' : 'text-emerald-600'">
                  Trạng thái: {{ isMembershipExpired ? 'Đã hết hạn' : 'Còn hạn' }}
                </p>
              </div>
            </div>

            <!-- Warning message if membership is expired -->
            <div v-if="isMembershipExpired" class="bg-red-50 p-4 rounded-lg border-2 border-red-300">
              <div class="flex items-start gap-3">
                <svg class="w-6 h-6 text-red-600 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"></path>
                </svg>
                <div>
                  <h4 class="text-sm font-bold text-red-800 mb-1">Gói membership đã hết hạn!</h4>
                  <p class="text-xs text-red-700">
                    Member này không thể đăng ký gói PT vì gói membership đã hết hạn. 
                    Vui lòng gia hạn membership trước khi đăng ký gói PT.
                  </p>
                </div>
              </div>
            </div>

            <!-- Danh sách gói PT đã đăng ký -->
            <div v-if="memberExistingPackages.length > 0" class="bg-blue-50 p-4 rounded-lg border border-blue-200">
              <h3 class="text-base font-semibold text-gray-900 mb-3">Gói PT đã đăng ký</h3>
              <div class="space-y-2">
                <div 
                  v-for="pkg in memberExistingPackages" 
                  :key="pkg.id"
                  class="bg-white p-3 rounded-lg border border-gray-200"
                >
                  <div class="flex items-start justify-between">
                    <div class="flex-1">
                      <p class="font-medium text-gray-900">{{ pkg.ptPackage.name }}</p>
                      <p class="text-sm text-gray-600">PT: {{ pkg.pt.fullName }}</p>
                      <p class="text-sm text-gray-600">Còn lại: {{ pkg.remainingSessions }}/{{ pkg.ptPackage.sessions }} buổi</p>
                    </div>
                    <span 
                      :class="[
                        'inline-flex items-center px-2 py-1 rounded-full text-xs font-medium',
                        pkg.ptPackage.status === 'Active' ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'
                      ]"
                    >
                      {{ pkg.ptPackage.status === 'Active' ? 'Đang hoạt động' : 'Không hoạt động' }}
                    </span>
                  </div>
                  <div class="mt-2 bg-gray-200 rounded-full h-2">
                    <div
                      class="bg-emerald-600 h-2 rounded-full transition-all"
                      :style="{ width: `${((pkg.ptPackage.sessions - pkg.remainingSessions) / pkg.ptPackage.sessions) * 100}%` }"
                    ></div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Form đăng ký gói mới -->
            <form @submit.prevent="submitPTAssignment" class="space-y-4 bg-white p-4 rounded-lg border-2 border-emerald-200">
              <h3 class="text-base font-semibold text-gray-900">Đăng ký gói PT mới</h3>
              
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
                  <option v-for="pkg in activePTPackages" :key="pkg.id" :value="pkg">
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
                :disabled="isMembershipExpired"
                :class="[
                  'w-full px-4 py-3 rounded-lg font-semibold transition-all',
                  isMembershipExpired
                    ? 'bg-gray-400 text-gray-200 cursor-not-allowed'
                    : 'bg-gradient-to-r from-emerald-600 to-teal-600 text-white hover:shadow-lg'
                ]"
              >
                {{ isMembershipExpired ? 'Không thể đăng ký (Membership hết hạn)' : 'Đăng ký Gói PT' }}
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