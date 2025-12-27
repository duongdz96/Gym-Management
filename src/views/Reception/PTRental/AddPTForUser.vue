<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import api from "../../../services/api";

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

// Định nghĩa Student Profile
type StudentProfile = {
  id: number;
  height?: number;
  weight?: number;
  trainingPlan?: string;
  member: Member;
  pt: PT;
};

const members = ref<Member[]>([]);
const pts = ref<PT[]>([]);
const studentProfiles = ref<StudentProfile[]>([]);
const selectedMember = ref<Member | null>(null);
const searchMember = ref("");

// Form fields
const selectedPT = ref<PT | null>(null);
const height = ref<number | null>(null);
const weight = ref<number | null>(null);
const trainingPlan = ref("");

onMounted(async () => {
  try {
    // Load members
    const memberRes = await api.get("/members");
    members.value = memberRes.data;

    // Load PTs
    const ptsRes = await api.get("/pts");
    pts.value = ptsRes.data;

    // Load existing student profiles
    const profilesRes = await api.get("/studentprofile");
    studentProfiles.value = profilesRes.data;
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

// Check if member already has a PT assigned
const getMemberProfile = (memberId: number) => {
  return studentProfiles.value.find(profile => profile.member.id === memberId);
};

// Functions
const selectMember = (member: Member) => {
  selectedMember.value = member;
  
  // Check if member already has a profile and pre-fill the form
  const existingProfile = getMemberProfile(member.id);
  if (existingProfile) {
    selectedPT.value = existingProfile.pt;
    height.value = existingProfile.height || null;
    weight.value = existingProfile.weight || null;
    trainingPlan.value = existingProfile.trainingPlan || "";
  } else {
    // Reset form
    selectedPT.value = null;
    height.value = null;
    weight.value = null;
    trainingPlan.value = "";
  }
};

const submitPTAssignment = async () => {
  if (!selectedMember.value || !selectedPT.value) {
    alert("Vui lòng chọn member và PT!");
    return;
  }

  try {
    const existingProfile = getMemberProfile(selectedMember.value.id);
    
    const payload = {
      member: { id: selectedMember.value.id },
      pt: { id: selectedPT.value.id },
      height: height.value,
      weight: weight.value,
      trainingPlan: trainingPlan.value
    };

    if (existingProfile) {
      // Update existing profile
      await api.put(`/api/studentprofile/${existingProfile.id}`, payload);
      alert("Cập nhật PT cho member thành công!");
    } else {
      // Create new profile
      await api.post("/api/studentprofile", payload);
      alert("Đăng ký PT cho member thành công!");
    }
    
    // Reload student profiles
    const profilesRes = await api.get("/studentprofile");
    studentProfiles.value = profilesRes.data;
    
    // Reset
    selectedMember.value = null;
    selectedPT.value = null;
    height.value = null;
    weight.value = null;
    trainingPlan.value = "";
  } catch (err) {
    console.error("Error assigning PT:", err);
    alert("Đăng ký PT thất bại!");
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
            Không tìm thấy member nào.
          </div>
        </div>
      </div>

      <!-- Bên phải: Form thêm PT -->
      <div class="lg:w-1/2 bg-white rounded-xl shadow-lg flex flex-col">
        <h2 class="text-2xl font-bold text-gray-800 p-6 border-b border-gray-200">Đăng ký PT cho Member</h2>

        <div class="flex-1 p-6">
          <div v-if="selectedMember" class="space-y-6">
            <div class="bg-blue-50 p-4 rounded-lg">
              <h3 class="text-lg font-semibold text-gray-900">Member đã chọn</h3>
              <p class="text-gray-700">{{ selectedMember.fullName }}</p>
              <p class="text-gray-600">{{ selectedMember.email }}</p>
              <p v-if="selectedMember.membership" class="text-xs text-blue-600 font-medium mt-1">
                Gói: {{ selectedMember.membership }}
              </p>
            </div>

            <form @submit.prevent="submitPTAssignment" class="space-y-4">
              <!-- Assigned PT -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Chọn PT</label>
                <select
                  v-model="selectedPT"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
                >
                  <option :value="null">Chọn PT</option>
                  <option v-for="pt in pts" :key="pt.id" :value="pt">
                    {{ pt.fullName }} ({{ pt.email }})
                  </option>
                </select>
              </div>

              <!-- Height -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Chiều cao (m) - Tùy chọn</label>
                <input
                  type="number"
                  step="0.01"
                  v-model.number="height"
                  placeholder="Ví dụ: 1.75"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <!-- Weight -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Cân nặng (kg) - Tùy chọn</label>
                <input
                  type="number"
                  step="0.1"
                  v-model.number="weight"
                  placeholder="Ví dụ: 70.5"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <!-- Training Plan -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Kế hoạch tập luyện - Tùy chọn</label>
                <textarea
                  v-model="trainingPlan"
                  rows="4"
                  placeholder="Ví dụ: Tập luyện tăng cơ 4 buổi/tuần, dinh dưỡng Calo thặng dư"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                ></textarea>
              </div>

              <!-- Submit Button -->
              <button
                type="submit"
                class="w-full px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-green-500"
              >
                {{ getMemberProfile(selectedMember.id) ? 'Cập nhật PT' : 'Đăng ký PT' }}
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