<script setup>
import { ref, computed, onMounted, watch } from "vue";
import api from "@/services/api";
import { useAuthStore } from "@/stores/useAuthStore";
import { useToast } from "vue-toastification";

const authStore = useAuthStore();
const toast = useToast();

const searchQuery = ref("");
const members = ref([]);

const selectedMember = ref(null);
const isEditingWeight = ref(false);
const isEditingHeight = ref(false);
const tempCurrentWeight = ref(0);
const tempCurrentHeight = ref(0);
const upcomingSessions = ref([]);
const memberPackages = ref([]);

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
  isEditingHeight.value = false;
  tempCurrentWeight.value = member.weight;
  tempCurrentHeight.value = member.height;
  fetchUpcomingSessions(member.memberId);
  fetchMemberPackages(member.memberId);
};

const startEditingWeight = () => {
  if (selectedMember.value) {
    isEditingWeight.value = true;
  }
};

const startEditingHeight = () => {
  if (selectedMember.value) {
    isEditingHeight.value = true;
  }
};

const saveCurrentWeight = async () => {
  if (selectedMember.value) {
    try {
      // Kiểm tra xem member này có student profile chưa
      const profileRes = await api.get("/studentprofile");
      const profileData = Array.isArray(profileRes.data) ? profileRes.data : [];
      const existingProfile = profileData.find(p => 
        p.member?.id === selectedMember.value.memberId && 
        p.pt?.id === authStore.user.id
      );
      
      if (existingProfile) {
        // Update existing profile
        const payload = { weight: tempCurrentWeight.value };
        await api.put(`/studentprofile/${existingProfile.id}`, payload);
        selectedMember.value.weight = tempCurrentWeight.value;
        isEditingWeight.value = false;
        toast.success("Cân nặng cập nhật thành công!");
      } else {
        // Create new profile - cần tìm ptPackageIssued
        const packageRes = await api.get("/packageissued");
        const packages = Array.isArray(packageRes.data) ? packageRes.data : [];
        const memberPackage = packages.find(pkg => 
          pkg.member?.id === selectedMember.value.memberId && 
          pkg.pt?.id === authStore.user.id
        );
        
        if (!memberPackage) {
          toast.error("Không tìm thấy gói PT cho học viên này!");
          return;
        }
        
        const payload = {
          member: { id: selectedMember.value.memberId },
          pt: { id: authStore.user.id },
          ptPackageIssued: { id: memberPackage.id },
          height: selectedMember.value.height || 0,
          weight: tempCurrentWeight.value,
          trainingPlan: selectedMember.value.trainingPlan || ''
        };
        const createRes = await api.post("/studentprofile", payload);
        selectedMember.value.id = createRes.data.id;
        selectedMember.value.weight = tempCurrentWeight.value;
        isEditingWeight.value = false;
        toast.success("Cân nặng cập nhật thành công!");
      }
    } catch (error) {
      console.error("Error saving weight:", error);
      toast.error("Cân nặng cập nhật thất bại. Vui lòng thử lại sau!");
    }
  }
};

const saveCurrentHeight = async () => {
  if (selectedMember.value) {
    try {
      // Kiểm tra xem member này có student profile chưa
      const profileRes = await api.get("/studentprofile");
      const profileData = Array.isArray(profileRes.data) ? profileRes.data : [];
      const existingProfile = profileData.find(p => 
        p.member?.id === selectedMember.value.memberId && 
        p.pt?.id === authStore.user.id
      );
      
      if (existingProfile) {
        // Update existing profile
        const payload = { height: tempCurrentHeight.value };
        await api.put(`/studentprofile/${existingProfile.id}`, payload);
        selectedMember.value.height = tempCurrentHeight.value;
        isEditingHeight.value = false;
        toast.success("Chiều cao cập nhật thành công!");
      } else {
        // Create new profile - cần tìm ptPackageIssued
        const packageRes = await api.get("/packageissued");
        const packages = Array.isArray(packageRes.data) ? packageRes.data : [];
        const memberPackage = packages.find(pkg => 
          pkg.member?.id === selectedMember.value.memberId && 
          pkg.pt?.id === authStore.user.id
        );
        
        if (!memberPackage) {
          toast.error("Không tìm thấy gói PT cho học viên này!");
          return;
        }
        
        const payload = {
          member: { id: selectedMember.value.memberId },
          pt: { id: authStore.user.id },
          ptPackageIssued: { id: memberPackage.id },
          height: tempCurrentHeight.value,
          weight: selectedMember.value.weight || 0,
          trainingPlan: selectedMember.value.trainingPlan || ''
        };
        const createRes = await api.post("/studentprofile", payload);
        selectedMember.value.id = createRes.data.id;
        selectedMember.value.height = tempCurrentHeight.value;
        isEditingHeight.value = false;
        toast.success("Chiều cao cập nhật thành công!");
      }
    } catch (error) {
      console.error("Error saving height:", error);
      toast.error("Chiều cao cập nhật thất bại. Vui lòng thử lại sau!");
    }
  }
};

const cancelEditingWeight = () => {
  tempCurrentWeight.value = selectedMember.value.weight;
  isEditingWeight.value = false;
};

const cancelEditingHeight = () => {
  tempCurrentHeight.value = selectedMember.value.height;
  isEditingHeight.value = false;
};

function formatTimeRange(startIso, endIso) {
  const s = new Date(startIso);
  const e = new Date(endIso);
  const pad = (n) => String(n).padStart(2, "0");
  return `${pad(s.getHours())}:${pad(s.getMinutes())} - ${pad(
    e.getHours()
  )}:${pad(e.getMinutes())}`;
}

const fetchUpcomingSessions = async (memberId) => {
  try {
    const res = await api.get("/appointment");
    const data = Array.isArray(res.data) ? res.data : [];
    // Filter appointments for the member
    const memberAppts = data.filter(appt => appt.ptPackageIssued?.member?.id === memberId);
    // Filter future appointments
    const now = new Date();
    const futureAppts = memberAppts.filter(appt => new Date(appt.startTime) > now);
    // Sort by startTime
    futureAppts.sort((a, b) => new Date(a.startTime) - new Date(b.startTime));
    // Take first 3
    upcomingSessions.value = futureAppts.slice(0, 3);
  } catch (error) {
    console.error("Error fetching upcoming sessions:", error);
    upcomingSessions.value = [];
  }
};

const fetchMemberPackages = async (memberId) => {
  try {
    const res = await api.get("/packageissued");
    const data = Array.isArray(res.data) ? res.data : [];
    
    // Filter packages for the selected member
    const memberIssuedPackages = data.filter(pkg => pkg.member?.id === memberId);
    
    // Map to the expected format
    memberPackages.value = memberIssuedPackages.map(pkg => {
      const totalSessions = pkg.ptPackage?.sessions || 0;
      const remainingSessions = pkg.remainingSessions || 0;
      const usedSessions = totalSessions - remainingSessions;
      
      return {
        id: pkg.id,
        name: pkg.ptPackage?.name || 'Gói PT',
        totalSessions: totalSessions,
        usedSessions: usedSessions,
        remainingSessions: remainingSessions,
        status: pkg.ptPackage?.status === 'Active' ? 'active' : 'expired',
        expiryDate: null // API doesn't provide expiry date
      };
    });
  } catch (error) {
    console.error("Error fetching member packages:", error);
    memberPackages.value = [];
  }
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
        tempCurrentHeight.value = newFilteredMembers[0].height;
        fetchUpcomingSessions(newFilteredMembers[0].memberId);
        fetchMemberPackages(newFilteredMembers[0].memberId);
      } else {
        selectedMember.value = null;
        upcomingSessions.value = [];
        memberPackages.value = [];
      }
    }
  } else if (newFilteredMembers.length > 0) {
    // If no member selected but filtered list has members, select first
    selectedMember.value = newFilteredMembers[0];
    tempCurrentWeight.value = newFilteredMembers[0].weight;    tempCurrentHeight.value = newFilteredMembers[0].height;    fetchUpcomingSessions(newFilteredMembers[0].memberId);
    fetchMemberPackages(newFilteredMembers[0].memberId);
  }
});

onMounted(async () => {
  try {
    // Lấy danh sách package issued để biết member nào được gán cho PT hiện tại
    const packageRes = await api.get("/packageissued");
    const packageData = Array.isArray(packageRes.data) ? packageRes.data : [];
    
    // Filter packages for the current PT
    const ptPackages = packageData.filter(pkg => pkg.pt && pkg.pt.id === authStore.user.id);
    
    // Get unique member IDs
    const memberIds = [...new Set(ptPackages.map(pkg => pkg.member?.id).filter(id => id))];
    
    // Fetch student profiles for these members (để lấy height, weight, trainingPlan)
    const profileRes = await api.get("/studentprofile");
    const profileData = Array.isArray(profileRes.data) ? profileRes.data : [];
    
    // Map member information
    members.value = memberIds.map(memberId => {
      const pkg = ptPackages.find(p => p.member?.id === memberId);
      const member = pkg?.member;
      
      // Tìm profile nếu có (để lấy height, weight, trainingPlan)
      const profile = profileData.find(p => p.member?.id === memberId && p.pt?.id === authStore.user.id);
      
      return {
        id: profile?.id || `temp-${memberId}`, // Use profile ID if exists, otherwise temp ID
        memberId: memberId,
        name: member?.fullName || 'N/A',
        email: member?.email || 'N/A',
        height: profile?.height || 0,
        weight: profile?.weight || 0,
        trainingPlan: profile?.trainingPlan || 'Chưa có kế hoạch',
      };
    });
  } catch (error) {
    console.error("Error loading members:", error);
    toast.error("Không thể tải thành viên. Vui lòng thử lại sau");
  }

  // Auto select first member
  if (members.value.length > 0) {
    selectedMember.value = members.value[0];
    tempCurrentWeight.value = members.value[0].weight;
    tempCurrentHeight.value = members.value[0].height;
    fetchUpcomingSessions(members.value[0].memberId);
    fetchMemberPackages(members.value[0].memberId);
  }
});
</script>

<template>
  <div class="p-4 sm:p-6 space-y-4 sm:space-y-8">
    <!-- Title -->
    <h1 class="text-xl sm:text-2xl font-bold text-stone-800">Danh sách học viên</h1>

    <!-- Layout chính -->
    <div class="flex flex-col lg:flex-row gap-4 sm:gap-6">
      <!-- Bên trái: Danh sách học viên -->
      <div class="w-full lg:w-1/3 bg-white rounded-xl shadow p-4 sm:p-5 max-h-[400px] lg:max-h-screen overflow-y-auto">
        <h2 class="text-xl font-semibold mb-4">Học viên của tôi</h2>

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
              placeholder="Tìm kiếm học viên theo tên..."
              class="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md leading-5 bg-white placeholder-gray-500 focus:outline-none focus:placeholder-gray-400 focus:ring-1 focus:ring-emerald-500 focus:border-emerald-500"
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
                ? 'bg-emerald-100 border-emerald-300'
                : 'bg-gray-50 hover:bg-gray-100',
            ]"
          >
            <div class="font-medium text-gray-900">{{ member.name }}</div>
            <div class="text-sm text-gray-500">{{ member.email }}</div>
            <div class="text-sm text-gray-500">Chiều cao: {{ member.height }} cm</div>
            <div class="text-sm text-gray-500">Cân nặng: {{ member.weight }} kg</div>
          </div>
        </div>
      </div>

      <!-- Bên phải: Thông tin chi tiết -->
      <div class="w-full lg:w-2/3 bg-white rounded-xl shadow p-4 sm:p-5 max-h-[600px] lg:max-h-screen overflow-y-auto">
        <h2 class="text-xl font-semibold mb-4">Thông tin chi tiết</h2>
        <div v-if="selectedMember" class="space-y-4">
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-3 sm:gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700"
                >Họ và tên</label
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
                >Cân nặng</label
              >
              <div class="mt-1 flex flex-wrap items-center gap-2">
                <input
                  v-if="isEditingWeight"
                  v-model.number="tempCurrentWeight"
                  type="number"
                  class="text-base sm:text-lg text-gray-900 border border-gray-300 rounded px-2 py-1 w-20"
                  min="0"
                  step="0.1"
                />
                <span v-else class="text-base sm:text-lg text-gray-900"
                  >{{ selectedMember.weight }} kg</span
                >
                <button
                  v-if="!isEditingWeight"
                  @click="startEditingWeight"
                  class="text-emerald-600 hover:text-emerald-800 text-xs sm:text-sm"
                >
                  Chỉnh sửa
                </button>
                <div v-if="isEditingWeight" class="flex gap-2">
                  <button
                    @click="saveCurrentWeight"
                    class="text-green-600 hover:text-green-800 text-xs sm:text-sm px-2 py-1 border border-green-600 rounded"
                  >
                    Lưu
                  </button>
                  <button
                    @click="cancelEditingWeight"
                    class="text-red-600 hover:text-red-800 text-xs sm:text-sm px-2 py-1 border border-red-600 rounded"
                  >
                    Hủy
                  </button>
                </div>
              </div>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700"
                >Chiều cao</label
              >
              <div class="mt-1 flex flex-wrap items-center gap-2">
                <input
                  v-if="isEditingHeight"
                  v-model.number="tempCurrentHeight"
                  type="number"
                  class="text-base sm:text-lg text-gray-900 border border-gray-300 rounded px-2 py-1 w-20"
                  min="0"
                  step="1"
                />
                <span v-else class="text-base sm:text-lg text-gray-900"
                  >{{ selectedMember.height }} cm</span
                >
                <button
                  v-if="!isEditingHeight"
                  @click="startEditingHeight"
                  class="text-emerald-600 hover:text-emerald-800 text-xs sm:text-sm"
                >
                  Chỉnh sửa
                </button>
                <div v-if="isEditingHeight" class="flex gap-2">
                  <button
                    @click="saveCurrentHeight"
                    class="text-green-600 hover:text-green-800 text-xs sm:text-sm px-2 py-1 border border-green-600 rounded"
                  >
                    Lưu
                  </button>
                  <button
                    @click="cancelEditingHeight"
                    class="text-red-600 hover:text-red-800 text-xs sm:text-sm px-2 py-1 border border-red-600 rounded"
                  >
                    Hủy
                  </button>
                </div>
              </div>
            </div>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700"
              >Kế hoạch tập luyện</label
            >
            <p class="mt-1 text-lg text-gray-900">
              {{ selectedMember.trainingPlan }}
            </p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2"
              >Gói tập đã đăng ký</label
            >
            <div v-if="memberPackages.length === 0" class="text-gray-500">Chưa đăng ký gói nào</div>
            <div v-else class="space-y-2">
              <div v-for="pkg in memberPackages" :key="pkg.id" class="border rounded-lg p-3 bg-emerald-50">
                <div class="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-2">
                  <div>
                    <p class="text-sm font-medium text-gray-900">{{ pkg.name }}</p>
                    <p class="text-xs text-gray-600">Còn lại: {{ pkg.remainingSessions }} buổi</p>
                  </div>
                  <div class="text-right">
                    <p class="text-sm font-semibold text-emerald-600">{{ pkg.usedSessions }}/{{ pkg.totalSessions }} buổi</p>
                    <span class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-green-100 text-green-800">
                      {{ pkg.status === 'active' ? 'Đang hoạt động' : 'Hết hạn' }}
                    </span>
                  </div>
                </div>
                <div class="mt-2 bg-gray-200 rounded-full h-2">
                  <div
                    class="bg-emerald-600 h-2 rounded-full"
                    :style="{ width: `${(pkg.usedSessions / pkg.totalSessions) * 100}%` }"
                  ></div>
                </div>
              </div>
            </div>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700"
              >Buổi tập sắp tới</label
            >
            <div v-if="upcomingSessions.length === 0" class="mt-1 text-gray-500">Không có buổi tập sắp tới</div>
            <div v-else class="mt-1 space-y-2">
              <div v-for="session in upcomingSessions" :key="session.id" class="border rounded p-2 sm:p-3 bg-gray-50">
                <p class="text-xs sm:text-sm font-medium">{{ new Date(session.startTime).toLocaleDateString() }}</p>
                <p class="text-xs sm:text-sm text-gray-600">{{ formatTimeRange(session.startTime, session.endTime) }}</p>
                <p class="text-xs sm:text-sm text-gray-600">{{ session.ptPackageIssued?.ptPackage?.name || 'Session' }}</p>
              </div>
            </div>
          </div>

        </div>
        <div v-else class="text-center text-gray-500">
          Chọn một học viên để xem thông tin chi tiết
                    </div>
          </div>
        </div>

        <!-- No results message -->
        <div v-if="filteredMembers.length === 0 && searchQuery" class="text-center py-8 text-gray-500">
          <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
          </svg>
          <h3 class="mt-2 text-sm font-medium text-gray-900">Không tìm thấy học viên</h3>
          <p class="mt-1 text-sm text-gray-500">Thử điều chỉnh từ khóa tìm kiếm.</p>
        </div>
      </div>
</template>
