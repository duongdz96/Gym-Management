<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import api from "@/services/api";

const router = useRouter();

type MembershipTier = {
  id: number,
  name: string,
  priority: number,
  status: string,
}

type MembershipPlan = {
  id: number,
  name: string,
  duration: string,
  status: string,
  benefits: string,
  price: number,
  membershipTier: MembershipTier,
}

type Member = {
  id: number,
  fullName: string,
  email: string,
  phone: string,
}

const membershipPlans = ref<MembershipPlan[]>([]);
const members = ref<Member[]>([]);
const isExistingMember = ref(false);
const selectedMemberId = ref("");

// form data
const form = ref({
  fullName: "",
  email: "",
  dob: "",
  gender: "Male" as string,
  phone: "",
  membershipPlanId: "" as string,
  startDate: "",
});

// Calculate end date based on plan duration
const endDate = ref("");

const submit = async () => {
  try {
    let payload;
    if (isExistingMember.value) {
      if (!selectedMemberId.value) {
        alert("Please select a member");
        return;
      }
      payload = {
        member: {
          id: parseInt(selectedMemberId.value)
        },
        membershipPlanId: parseInt(form.value.membershipPlanId),
        startDate: form.value.startDate,
        endDate: endDate.value,
      };
    } else {
      payload = {
        member: {
          fullName: form.value.fullName,
          email: form.value.email,
          dob: form.value.dob,
          gender: form.value.gender,
          phone: form.value.phone,
          role: "MEMBER",
        },
        membershipPlanId: parseInt(form.value.membershipPlanId),
        startDate: form.value.startDate,
        endDate: endDate.value,
      };
    }
    console.log(payload);

    await api.post("/membership", payload);
    alert("Membership added successfully!");
    router.push({ name: "reception.memberships" });
  } catch (error) {
    console.error("Error adding membership:", error);
    alert("Failed to add membership. Please try again.");
  }
};

// Watch for plan change to calculate end date
const calculateEndDate = () => {
  if (form.value.membershipPlanId && form.value.startDate) {
    const plan = membershipPlans.value.find(p => p.id === parseInt(form.value.membershipPlanId));
    if (plan) {
      const start = new Date(form.value.startDate);
      const durationMatch = plan.duration.match(/(\d+)\s*(Month|Year)s?/i);
      if (durationMatch) {
        const num = parseInt(durationMatch[1]);
        const unit = durationMatch[2].toLowerCase();
        if (unit === 'month') {
          start.setMonth(start.getMonth() + num);
        } else if (unit === 'year') {
          start.setFullYear(start.getFullYear() + num);
        }
        endDate.value = start.toISOString().split('T')[0];
      }
    }
  }
};

onMounted(async () => {
  try {
    // Load membership plans
    const planRes = await api.get("/membershipplan");
    membershipPlans.value = planRes.data;

    // Load members
    const memberRes = await api.get("/members");
    members.value = memberRes.data;
  } catch (error) {
    console.error("Error loading data:", error);
    alert("Failed to load data. Please try again.");
  }
});
</script>

<template>
  <div class="p-4">
    <div class="flex items-center justify-between mb-4">
      <h1 class="text-xl font-semibold">Add Membership</h1>
      <RouterLink
        :to="{ name: 'reception.memberships' }"
        class="px-3 py-2 rounded-md bg-gray-200 dark:bg-gray-800 text-gray-900 dark:text-gray-100"
      >
        Back
      </RouterLink>
    </div>

    <form
      class="bg-white dark:bg-gray-900 border border-gray-200 dark:border-gray-800 rounded-lg p-4 grid grid-cols-1 md:grid-cols-2 gap-4"
      @submit.prevent="submit"
    >
      <!-- Customer Type Selection -->
      <div class="md:col-span-2 mb-4">
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Customer Type</label>
        <div class="flex gap-4">
          <label class="flex items-center gap-2 cursor-pointer">
            <input type="radio" v-model="isExistingMember" :value="false" class="form-radio text-emerald-600" />
            <span>New Customer</span>
          </label>
          <label class="flex items-center gap-2 cursor-pointer">
            <input type="radio" v-model="isExistingMember" :value="true" class="form-radio text-emerald-600" />
            <span>Existing Customer</span>
          </label>
        </div>
      </div>

      <!-- Existing Member Selection -->
      <div v-if="isExistingMember" class="md:col-span-2">
        <label
          class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1"
          >Select Member</label
        >
        <select
          v-model="selectedMemberId"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-emerald-500"
          required
        >
          <option value="" disabled>Select a member</option>
          <option
            v-for="member in members"
            :key="member.id"
            :value="member.id.toString()"
          >
            {{ member.fullName }} - {{ member.phone }}
          </option>
        </select>
      </div>

      <!-- New Member Fields -->
      <template v-else>
        <!-- Full Name -->
        <div>
          <label
            class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1"
            >Full Name</label
          >
          <input
            v-model="form.fullName"
            type="text"
            class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
            required
          />
        </div>

        <!-- Email -->
        <div>
          <label
            class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1"
            >Email</label
          >
          <input
            v-model="form.email"
            type="email"
            class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
            required
          />
        </div>

        <!-- DOB -->
        <div>
          <label
            class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1"
            >Date of Birth</label
          >
          <input
            v-model="form.dob"
            type="date"
            class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
            required
          />
        </div>

        <!-- Gender -->
        <div>
          <label
            class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1"
            >Gender</label
          >
          <select
            v-model="form.gender"
            class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
            required
          >
            <option value="Male">Male</option>
            <option value="Female">Female</option>
          </select>
        </div>

        <!-- Phone -->
        <div>
          <label
            class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1"
            >Phone</label
          >
          <input
            v-model="form.phone"
            type="tel"
            class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
            required
          />
        </div>
      </template>

      <!-- Membership Plan -->
      <div>
        <label
          class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1"
          >Membership Plan</label
        >
        <select
          v-model="form.membershipPlanId"
          @change="calculateEndDate"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        >
          <option value="" disabled>Select a plan</option>
          <option
            v-for="plan in membershipPlans"
            :key="plan.id"
            :value="plan.id.toString()"
          >
            {{ plan.name }} - {{ plan.price.toLocaleString() }} VND
          </option>
        </select>
      </div>

      <!-- Start Date -->
      <div>
        <label
          class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1"
          >Start Date</label
        >
        <input
          v-model="form.startDate"
          @change="calculateEndDate"
          type="date"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        />
      </div>

      <!-- End Date -->
      <div>
        <label
          class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1"
          >End Date</label
        >
        <input
          v-model="endDate"
          type="date"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100"
          readonly
        />
      </div>

      <!-- Buttons -->
      <div class="md:col-span-2 flex items-center gap-2">
        <button
          type="submit"
          class="px-4 py-2 rounded-md bg-emerald-600 hover:bg-emerald-700 text-white"
        >
          Add Membership
        </button>
      </div>
    </form>
  </div>
</template>