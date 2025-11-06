<template>
  <div class="p-4">
    <div class="flex items-center justify-between mb-4">
      <h1 class="text-xl font-semibold">Add Coupon</h1>
      <RouterLink
        :to="{ name: 'coupon' }"
        class="px-3 py-2 rounded-md bg-gray-200 dark:bg-gray-800 text-gray-900 dark:text-gray-100"
      >
        Back
      </RouterLink>
    </div>

    <form
      class="bg-white dark:bg-gray-900 border border-gray-200 dark:border-gray-800 rounded-lg p-4 grid grid-cols-1 md:grid-cols-2 gap-4"
      @submit.prevent="submit"
    >
      <!-- Code -->
      <div>
        <label class="block text-sm font-medium mb-1">Code</label>
        <input
          v-model="form.code"
          type="text"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700"
          required
        />
      </div>

      <!-- Discount Type -->
      <div>
        <label class="block text-sm font-medium mb-1">Discount Type</label>
        <select
          v-model="form.discountType"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700"
          required
        >
          <option value="PERCENTAGE">Percentage</option>
          <option value="FIXED_AMOUNT">Fixed Amount</option>
        </select>
      </div>

      <!-- Discount Value -->
      <div>
        <label class="block text-sm font-medium mb-1">Discount Value</label>
        <input
          v-model.number="form.discountValue"
          type="number"
          min="0"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700"
          required
        />
      </div>

      <!-- Status -->
      <div>
        <label class="block text-sm font-medium mb-1">Status</label>
        <select
          v-model="form.status"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700"
          required
        >
          <option value="ACTIVE">Active</option>
          <option value="INACTIVE">Inactive</option>
        </select>
      </div>

      <!-- Start Date -->
      <div>
        <label class="block text-sm font-medium mb-1">Start Date</label>
        <input
          v-model="form.startDate"
          type="date"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700"
          required
        />
      </div>

      <!-- End Date -->
      <div>
        <label class="block text-sm font-medium mb-1">End Date</label>
        <input
          v-model="form.endDate"
          type="date"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700"
          required
        />
      </div>

      <!-- Scope -->
      <div class="md:col-span-2">
        <label class="block text-sm font-medium mb-1">Scope (Membership Tier)</label>
        <select
          v-model="form.scope"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700"
          required
        >
          <option disabled value="">-- Select Tier --</option>
          <option v-for="tier in tiers" :key="tier.id" :value="tier.name">
            {{ tier.name }}
          </option>
        </select>
      </div>

      <!-- Buttons -->
      <div class="md:col-span-2 flex items-center gap-2">
        <button
          type="submit"
          class="px-4 py-2 rounded-md bg-blue-600 hover:bg-blue-700 text-white disabled:opacity-50"
          :disabled="loading"
        >
          {{ loading ? "Saving..." : "Add" }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import api from "@/services/api";
import { RouterLink } from "vue-router";
import { useToast } from "vue-toastification";

const toast = useToast();

const form = ref({
  code: "",
  discountType: "PERCENTAGE",
  discountValue: 0,
  startDate: "",
  endDate: "",
  status: "ACTIVE",
  scope: "",
});

const tiers = ref([]);
const loading = ref(false);

onMounted(async () => {
  try {
    const res = await api.get("/membershiptier");
    tiers.value = res.data;
  } catch (err) {
    toast.error("Error loading tiers:");
  }
});

const submit = async () => {
  loading.value = true;
  try {
    await api.post("/coupons/add", form.value);
    toast.success ("Coupon created and issued successfully!");
    form.value = {
      code: "",
      discountType: "PERCENTAGE",
      discountValue: 0,
      startDate: "",
      endDate: "",
      status: "ACTIVE",
      scope: "",
    };
  } catch (err) {
    toast.error(err);
  } finally {
    loading.value = false;
  }
};
</script>
