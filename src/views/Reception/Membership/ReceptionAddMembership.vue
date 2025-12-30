<script setup lang="ts">
import { ref, computed, watch } from "vue";
import { useRouter } from "vue-router";
import api from "@/services/api";
import { useToast } from "vue-toastification";

const router = useRouter();
const toast = useToast();

type MembershipTier = {
  id: number;
  name: string;
  priority: number;
  status: string;
};

type MembershipPlan = {
  id: number;
  name: string;
  duration: string;
  status: string;
  benefits: string;
  price: number;
  membershipTier: MembershipTier;
};

type Member = {
  id: number;
  fullName: string;
  email: string;
  phone: string;
};

type CurrentMembership = {
  id: number;
  startDate: string;
  endDate: string;
  status: string;
  membershipPlan: MembershipPlan;
};

// Tab management
const activeTab = ref<"new" | "existing">("new");

// Data lists
const membershipPlans = ref<MembershipPlan[]>([]);
const members = ref<Member[]>([]);

// Form data for new customer
const newCustomerForm = ref({
  fullName: "",
  email: "",
  dob: "",
  gender: "Male" as string,
  phone: "",
  membershipPlanId: "",
  startDate: new Date().toISOString().split("T")[0],
});

// Email check state
const emailCheckState = ref<{
  checking: boolean;
  exists: boolean;
  existingMember: Member | null;
}>({
  checking: false,
  exists: false,
  existingMember: null,
});

// Existing customer state
const existingCustomerState = ref({
  searchQuery: "",
  selectedMember: null as Member | null,
  currentMembership: null as CurrentMembership | null,
  loadingCurrent: false,
  selectedPlanId: "",
});

// Filtered members for autocomplete
const filteredMembers = computed(() => {
  if (!existingCustomerState.value.searchQuery) return [];
  const query = existingCustomerState.value.searchQuery.toLowerCase();
  return members.value.filter(
    (m) =>
      m.email.toLowerCase().includes(query) ||
      m.phone.includes(query) ||
      m.fullName.toLowerCase().includes(query)
  );
});

// Calculate action type (renew or upgrade)
const actionType = computed(() => {
  if (!existingCustomerState.value.currentMembership || !existingCustomerState.value.selectedPlanId) {
    return null;
  }
  const currentPlanId = existingCustomerState.value.currentMembership.membershipPlan.id;
  const selectedPlanId = parseInt(existingCustomerState.value.selectedPlanId);
  return currentPlanId === selectedPlanId ? "renew" : "upgrade";
});

// Preview end date
const previewEndDate = ref("");

// Helper functions
const addDuration = (startDate: Date, duration: string): Date => {
  const result = new Date(startDate);
  const parts = duration.split(" ");
  const amount = parseInt(parts[0]);
  const unit = parts[1].toLowerCase();

  if (unit.includes("month")) {
    result.setMonth(result.getMonth() + amount);
  } else if (unit.includes("year")) {
    result.setFullYear(result.getFullYear() + amount);
  }
  return result;
};

const getDurationInDays = (duration: string): number => {
  const now = new Date();
  const end = addDuration(now, duration);
  return Math.floor((end.getTime() - now.getTime()) / (1000 * 60 * 60 * 24));
};

const formatDate = (dateString: string): string => {
  if (!dateString) return "";
  const date = new Date(dateString);
  const day = String(date.getDate()).padStart(2, "0");
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const year = date.getFullYear();
  return `${day}/${month}/${year}`;
};

const formatCurrency = (amount: number): string => {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(amount);
};

// Check email when blur
const checkEmail = async () => {
  if (!newCustomerForm.value.email) return;

  emailCheckState.value.checking = true;
  emailCheckState.value.exists = false;
  emailCheckState.value.existingMember = null;

  try {
    const response = await api.get(`/members/check-email`, {
      params: { email: newCustomerForm.value.email },
    });
    
    if (response.status === 200 && response.data) {
      emailCheckState.value.exists = true;
      emailCheckState.value.existingMember = response.data;
    }
  } catch (error: any) {
    if (error.response?.status === 404) {
      // Email không tồn tại - OK
      emailCheckState.value.exists = false;
    } else {
      console.error("Error checking email:", error);
    }
  } finally {
    emailCheckState.value.checking = false;
  }
};

// Switch to existing customer tab with member info
const switchToExistingCustomer = () => {
  if (emailCheckState.value.existingMember) {
    activeTab.value = "existing";
    existingCustomerState.value.searchQuery = emailCheckState.value.existingMember.email;
    selectMember(emailCheckState.value.existingMember);
  }
};

// Select member from autocomplete
const selectMember = async (member: Member) => {
  existingCustomerState.value.selectedMember = member;
  existingCustomerState.value.searchQuery = `${member.fullName} - ${member.email}`;
  existingCustomerState.value.loadingCurrent = true;

  try {
    const response = await api.get(`/membership/member/${member.id}/current`);
    if (response.status === 200 && response.data) {
      existingCustomerState.value.currentMembership = response.data;
    } else {
      existingCustomerState.value.currentMembership = null;
    }
  } catch (error: any) {
    if (error.response?.status === 204) {
      existingCustomerState.value.currentMembership = null;
    } else {
      console.error("Error loading current membership:", error);
    }
  } finally {
    existingCustomerState.value.loadingCurrent = false;
  }
};

// Calculate preview end date for existing customer
const calculatePreviewEndDate = () => {
  if (!existingCustomerState.value.selectedPlanId || !existingCustomerState.value.currentMembership) {
    previewEndDate.value = "";
    return;
  }

  const plan = membershipPlans.value.find(
    (p) => p.id === parseInt(existingCustomerState.value.selectedPlanId)
  );
  if (!plan) return;

  const today = new Date();
  const currentEndDate = new Date(existingCustomerState.value.currentMembership.endDate);

  if (actionType.value === "renew") {
    // Gia hạn: base date = max(today, currentEndDate)
    const baseDate = currentEndDate > today ? currentEndDate : today;
    const newEndDate = addDuration(baseDate, plan.duration);
    previewEndDate.value = newEndDate.toISOString().split("T")[0];
  } else if (actionType.value === "upgrade") {
    // Nâng cấp: today + duration + extra days from residual value
    let newEndDate = addDuration(today, plan.duration);

    // Calculate extra days if current membership is still active
    if (currentEndDate > today) {
      const currentPlan = existingCustomerState.value.currentMembership.membershipPlan;
      const daysRemaining = Math.floor(
        (currentEndDate.getTime() - today.getTime()) / (1000 * 60 * 60 * 24)
      );
      const oldDurationDays = getDurationInDays(currentPlan.duration);
      const oldPricePerDay = currentPlan.price / oldDurationDays;
      const residualValue = daysRemaining * oldPricePerDay;

      const newDurationDays = getDurationInDays(plan.duration);
      const newPricePerDay = plan.price / newDurationDays;
      const extraDays = residualValue / newPricePerDay;

      // Add extra days in milliseconds
      const extraTimeInMillis = extraDays * 24 * 60 * 60 * 1000;
      newEndDate = new Date(newEndDate.getTime() + extraTimeInMillis);
    }

    previewEndDate.value = newEndDate.toISOString().split("T")[0];
  }
};

// Watch for plan selection changes
watch(
  () => existingCustomerState.value.selectedPlanId,
  () => {
    calculatePreviewEndDate();
  }
);

// Submit new customer
const submitNewCustomer = async () => {
  if (emailCheckState.value.exists) {
    alert("Email này đã tồn tại. Vui lòng chuyển sang tab Khách Hàng Cũ để gia hạn.");
    return;
  }

  try {
    const plan = membershipPlans.value.find(
      (p) => p.id === parseInt(newCustomerForm.value.membershipPlanId)
    );
    if (!plan) {
      alert("Vui lòng chọn gói thành viên");
      return;
    }

    const startDate = new Date(newCustomerForm.value.startDate);
    const endDate = addDuration(startDate, plan.duration);

    const payload = {
      member: {
        fullName: newCustomerForm.value.fullName,
        email: newCustomerForm.value.email,
        dob: newCustomerForm.value.dob,
        gender: newCustomerForm.value.gender,
        phone: newCustomerForm.value.phone,
        password: "123456", // Default password
        role: "MEMBER",
      },
      membershipPlanId: parseInt(newCustomerForm.value.membershipPlanId),
      startDate: startDate.toISOString().split("T")[0],
      endDate: endDate.toISOString().split("T")[0],
    };

    await api.post("/membership", payload);
    alert("Thêm thành viên mới thành công!");
    router.push({ name: "reception.memberships" });
  } catch (error) {
    console.error("Error adding new customer:", error);
    alert("Có lỗi xảy ra. Vui lòng thử lại.");
  }
};

// Submit existing customer (renew or upgrade)
const submitExistingCustomer = async () => {
  if (!existingCustomerState.value.selectedMember || !existingCustomerState.value.currentMembership) {
    alert("Vui lòng chọn khách hàng");
    return;
  }

  if (!existingCustomerState.value.selectedPlanId) {
    alert("Vui lòng chọn gói thành viên");
    return;
  }

  try {
    const currentMembershipId = existingCustomerState.value.currentMembership.id;
    const newPlanId = parseInt(existingCustomerState.value.selectedPlanId);

    if (actionType.value === "renew") {
      await api.post(`/membership/${currentMembershipId}/renew`, null, {
        params: { newPlanId },
      });
      alert("Gia hạn thành công!");
    } else if (actionType.value === "upgrade") {
      await api.post(`/membership/${currentMembershipId}/upgrade`, null, {
        params: { newPlanId },
      });
      alert("Nâng cấp thành công!");
    }

    router.push({ name: "reception.memberships" });
  } catch (error) {
    console.error("Error processing membership:", error);
    alert("Có lỗi xảy ra. Vui lòng thử lại.");
  }
};

// Load data on mount
const loadData = async () => {
  try {
    const [planRes, memberRes] = await Promise.all([
      api.get("/membershipplan"),
      api.get("/members"),
    ]);
    membershipPlans.value = planRes.data;
    members.value = memberRes.data;
  } catch (error) {
    console.error("Error loading data:", error);
    alert("Không thể tải dữ liệu. Vui lòng thử lại.");
  }
};

loadData();
</script>

<template>
  <div class="p-6 bg-green-50 dark:bg-gray-900 min-h-screen">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold text-gray-800 dark:text-gray-100">
        Thêm Mới Thành Viên
      </h1>
      <RouterLink
        :to="{ name: 'reception.memberships' }"
        class="px-4 py-2 rounded-lg bg-emerald-600 hover:bg-emerald-700 text-white font-medium transition-colors"
      >
        Danh sách thành viên
      </RouterLink>
    </div>

    <!-- Tabs -->
    <div class="mb-6">
      <div class="flex gap-2 border-b border-gray-300 dark:border-gray-700">
        <button
          @click="activeTab = 'new'"
          :class="[
            'px-6 py-3 font-medium transition-all',
            activeTab === 'new'
              ? 'border-b-2 border-emerald-600 text-emerald-600'
              : 'text-gray-600 dark:text-gray-400 hover:text-emerald-500',
          ]"
        >
          Khách Hàng Mới
        </button>
        <button
          @click="activeTab = 'existing'"
          :class="[
            'px-6 py-3 font-medium transition-all',
            activeTab === 'existing'
              ? 'border-b-2 border-emerald-600 text-emerald-600'
              : 'text-gray-600 dark:text-gray-400 hover:text-emerald-500',
          ]"
        >
          Khách Hàng Cũ (Gia hạn & Đổi gói)
        </button>
      </div>
    </div>

    <!-- Tab 1: New Customer -->
    <div v-show="activeTab === 'new'" class="space-y-6">
      <form
        @submit.prevent="submitNewCustomer"
        class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6"
      >
        <h2 class="text-lg font-semibold text-gray-800 dark:text-gray-100 mb-4">
          Thông tin khách hàng
        </h2>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <!-- Full Name -->
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
              Họ và tên <span class="text-red-500">*</span>
            </label>
            <input
              v-model="newCustomerForm.fullName"
              type="text"
              required
              class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-emerald-500"
              placeholder="Nhập họ và tên"
            />
          </div>

          <!-- Email -->
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
              Email <span class="text-red-500">*</span>
            </label>
            <input
              v-model="newCustomerForm.email"
              @blur="checkEmail"
              type="email"
              required
              class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-emerald-500"
              placeholder="Nhập email"
            />
            <!-- Email check warning -->
            <div
              v-if="emailCheckState.exists && emailCheckState.existingMember"
              class="mt-2 p-3 bg-yellow-50 dark:bg-yellow-900/20 border border-yellow-300 dark:border-yellow-700 rounded-md"
            >
              <p class="text-sm text-yellow-800 dark:text-yellow-300 mb-2">
                Email này đã thuộc về khách hàng:
                <strong>{{ emailCheckState.existingMember.fullName }}</strong>
              </p>
              <button
                type="button"
                @click="switchToExistingCustomer"
                class="px-3 py-1 rounded bg-emerald-600 hover:bg-emerald-700 text-white text-sm font-medium"
              >
                Chuyển sang Gia hạn
              </button>
            </div>
          </div>

          <!-- DOB -->
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
              Ngày sinh <span class="text-red-500">*</span>
            </label>
            <input
              v-model="newCustomerForm.dob"
              type="date"
              required
              class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-emerald-500"
            />
          </div>

          <!-- Gender -->
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
              Giới tính <span class="text-red-500">*</span>
            </label>
            <select
              v-model="newCustomerForm.gender"
              required
              class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-emerald-500"
            >
              <option value="Male">Nam</option>
              <option value="Female">Nữ</option>
            </select>
          </div>

          <!-- Phone -->
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
              Số điện thoại <span class="text-red-500">*</span>
            </label>
            <input
              v-model="newCustomerForm.phone"
              type="tel"
              required
              class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-emerald-500"
              placeholder="Nhập số điện thoại"
            />
          </div>

          <!-- Membership Plan -->
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
              Gói thành viên <span class="text-red-500">*</span>
            </label>
            <select
              v-model="newCustomerForm.membershipPlanId"
              required
              class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-emerald-500"
            >
              <option value="" disabled>Chọn gói thành viên</option>
              <option v-for="plan in membershipPlans" :key="plan.id" :value="plan.id.toString()">
                {{ plan.name }} - {{ formatCurrency(plan.price) }} ({{ plan.duration }})
              </option>
            </select>
          </div>

          <!-- Start Date -->
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
              Ngày bắt đầu <span class="text-red-500">*</span>
            </label>
            <input
              v-model="newCustomerForm.startDate"
              type="date"
              required
              class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-emerald-500"
            />
          </div>
        </div>

        <!-- Info note -->
        <div class="mt-4 p-3 bg-blue-50 dark:bg-blue-900/20 border border-blue-300 dark:border-blue-700 rounded-md">
          <p class="text-sm text-blue-800 dark:text-blue-300">
            Mật khẩu mặc định cho khách hàng mới: <strong>123456</strong>
          </p>
        </div>

        <!-- Submit Button -->
        <div class="mt-6 flex gap-3">
          <button
            type="submit"
            class="px-6 py-2 rounded-lg bg-emerald-600 hover:bg-emerald-700 text-white font-medium transition-colors"
          >
            Thêm Mới Thành Viên
          </button>
        </div>
      </form>
    </div>

    <!-- Tab 2: Existing Customer -->
    <div v-show="activeTab === 'existing'" class="space-y-6">
      <!-- Search Member -->
      <div class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
        <h2 class="text-lg font-semibold text-gray-800 dark:text-gray-100 mb-4">
          Tìm kiếm khách hàng
        </h2>

        <div class="relative">
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Tìm theo Email hoặc Số điện thoại
          </label>
          <input
            v-model="existingCustomerState.searchQuery"
            type="text"
            placeholder="Nhập email hoặc số điện thoại..."
            class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-emerald-500"
          />

          <!-- Autocomplete dropdown -->
          <div
            v-if="filteredMembers.length > 0 && !existingCustomerState.selectedMember"
            class="absolute z-10 w-full mt-1 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-md shadow-lg max-h-60 overflow-y-auto"
          >
            <button
              v-for="member in filteredMembers"
              :key="member.id"
              @click="selectMember(member)"
              type="button"
              class="w-full text-left px-4 py-2 hover:bg-emerald-50 dark:hover:bg-emerald-900/20 text-gray-900 dark:text-gray-100 border-b border-gray-200 dark:border-gray-600 last:border-b-0"
            >
              <div class="font-medium">{{ member.fullName }}</div>
              <div class="text-sm text-gray-600 dark:text-gray-400">
                {{ member.email }} - {{ member.phone }}
              </div>
            </button>
          </div>
        </div>

        <!-- Clear selection button -->
        <button
          v-if="existingCustomerState.selectedMember"
          @click="
            existingCustomerState.selectedMember = null;
            existingCustomerState.currentMembership = null;
            existingCustomerState.searchQuery = '';
            existingCustomerState.selectedPlanId = '';
            previewEndDate = '';
          "
          type="button"
          class="mt-2 text-sm text-emerald-600 hover:text-emerald-700 font-medium"
        >
          Chọn khách hàng khác
        </button>
      </div>

      <!-- Current Membership Card -->
      <div
        v-if="existingCustomerState.selectedMember"
        class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6"
      >
        <h2 class="text-lg font-semibold text-gray-800 dark:text-gray-100 mb-4">
          Thông tin thành viên hiện tại
        </h2>

        <div v-if="existingCustomerState.loadingCurrent" class="text-center py-4">
          <p class="text-gray-600 dark:text-gray-400">Đang tải thông tin...</p>
        </div>

        <div v-else-if="existingCustomerState.currentMembership" class="space-y-3">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <p class="text-sm text-gray-600 dark:text-gray-400">Họ và tên:</p>
              <p class="font-medium text-gray-900 dark:text-gray-100">
                {{ existingCustomerState.selectedMember.fullName }}
              </p>
            </div>
            <div>
              <p class="text-sm text-gray-600 dark:text-gray-400">Email:</p>
              <p class="font-medium text-gray-900 dark:text-gray-100">
                {{ existingCustomerState.selectedMember.email }}
              </p>
            </div>
          </div>

          <div class="mt-4 p-4 bg-emerald-50 dark:bg-emerald-900/20 border border-emerald-300 dark:border-emerald-700 rounded-lg">
            <h3 class="font-semibold text-emerald-800 dark:text-emerald-300 mb-2">
              Gói hiện tại
            </h3>
            <div class="space-y-2">
              <div class="flex justify-between">
                <span class="text-sm text-gray-700 dark:text-gray-300">Gói:</span>
                <span class="font-medium text-gray-900 dark:text-gray-100">
                  {{ existingCustomerState.currentMembership.membershipPlan.name }}
                </span>
              </div>
              <div class="flex justify-between">
                <span class="text-sm text-gray-700 dark:text-gray-300">Ngày hết hạn:</span>
                <span class="font-medium text-gray-900 dark:text-gray-100">
                  {{ formatDate(existingCustomerState.currentMembership.endDate) }}
                </span>
              </div>
              <div class="flex justify-between">
                <span class="text-sm text-gray-700 dark:text-gray-300">Trạng thái:</span>
                <span
                  :class="[
                    'font-medium px-2 py-1 rounded text-sm',
                    existingCustomerState.currentMembership.status === 'Active'
                      ? 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200'
                      : 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200',
                  ]"
                >
                  {{
                    existingCustomerState.currentMembership.status === "Active"
                      ? "Hoạt động"
                      : "Hết hạn"
                  }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="text-center py-4">
          <p class="text-gray-600 dark:text-gray-400">
            Khách hàng chưa có gói thành viên nào.
          </p>
        </div>
      </div>

      <!-- Select New Plan -->
      <form
        v-if="existingCustomerState.selectedMember && existingCustomerState.currentMembership"
        @submit.prevent="submitExistingCustomer"
        class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6"
      >
        <h2 class="text-lg font-semibold text-gray-800 dark:text-gray-100 mb-4">
          Chọn gói mới
        </h2>

        <div class="space-y-4">
          <!-- Plan Selection -->
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
              Gói thành viên <span class="text-red-500">*</span>
            </label>
            <select
              v-model="existingCustomerState.selectedPlanId"
              required
              class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-emerald-500"
            >
              <option value="" disabled>Chọn gói thành viên</option>
              <option v-for="plan in membershipPlans" :key="plan.id" :value="plan.id.toString()">
                {{ plan.name }} - {{ formatCurrency(plan.price) }} ({{ plan.duration }})
              </option>
            </select>
          </div>

          <!-- Action Type Badge -->
          <div v-if="actionType" class="p-4 rounded-lg" :class="actionType === 'renew' ? 'bg-blue-50 dark:bg-blue-900/20 border border-blue-300 dark:border-blue-700' : 'bg-purple-50 dark:bg-purple-900/20 border border-purple-300 dark:border-purple-700'">
            <div>
              <div>
                <p class="font-semibold" :class="actionType === 'renew' ? 'text-blue-800 dark:text-blue-300' : 'text-purple-800 dark:text-purple-300'">
                  {{ actionType === "renew" ? "Gia hạn gói hiện tại" : "Đổi gói" }}
                </p>
                <p class="text-sm" :class="actionType === 'renew' ? 'text-blue-700 dark:text-blue-400' : 'text-purple-700 dark:text-purple-400'">
                  {{
                    actionType === "renew"
                      ? "Thời gian sẽ được cộng thêm vào ngày hết hạn hiện tại"
                      : "Gói cũ sẽ được quy đổi và cộng vào gói mới"
                  }}
                </p>
              </div>
            </div>
          </div>

          <!-- Preview End Date -->
          <div v-if="previewEndDate" class="p-4 bg-green-50 dark:bg-green-900/20 border border-green-300 dark:border-green-700 rounded-lg">
            <h3 class="font-semibold text-green-800 dark:text-green-300 mb-2">
              Ngày hết hạn mới dự kiến
            </h3>
            <p class="text-lg font-bold text-green-900 dark:text-green-100">
              {{ formatDate(previewEndDate) }}
            </p>
          </div>
        </div>

        <!-- Submit Button -->
        <div class="mt-6 flex gap-3">
          <button
            type="submit"
            class="px-6 py-2 rounded-lg bg-emerald-600 hover:bg-emerald-700 text-white font-medium transition-colors"
          >
            {{ actionType === "renew" ? "Xác nhận Gia hạn" : "Xác nhận Nâng cấp" }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
/* Custom scrollbar for autocomplete */
.overflow-y-auto::-webkit-scrollbar {
  width: 8px;
}
.overflow-y-auto::-webkit-scrollbar-track {
  background: #f1f1f1;
}
.overflow-y-auto::-webkit-scrollbar-thumb {
  background: #059669;
  border-radius: 4px;
}
.overflow-y-auto::-webkit-scrollbar-thumb:hover {
  background: #047857;
}
</style>
