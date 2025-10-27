<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();

// form data
const form = ref({
  name: "",
  dob: "",
  phone: "",
  address: "",
  membershipPackage: "" as string,
});

// List of membership packages (hardcoded for now)
const membershipPackages = ref([
  { value: "basic", label: "Basic Membership" },
  { value: "premium", label: "Premium Membership" },
  { value: "vip", label: "VIP Membership" },
  { value: "family", label: "Family Membership" },
]);

const submit = () => {
  console.log("Membership submitted:", form.value);
  // Here you would typically send the data to an API
  // For now, just navigate back or show success
  router.push({ name: "reception.home" });
};
</script>

<template>
  <div class="p-4">
    <div class="flex items-center justify-between mb-4">
      <h1 class="text-xl font-semibold">Add Membership</h1>
      <RouterLink
        :to="{ name: 'reception.home' }"
        class="px-3 py-2 rounded-md bg-gray-200 dark:bg-gray-800 text-gray-900 dark:text-gray-100"
      >
        Back
      </RouterLink>
    </div>

    <form
      class="bg-white dark:bg-gray-900 border border-gray-200 dark:border-gray-800 rounded-lg p-4 grid grid-cols-1 md:grid-cols-2 gap-4"
      @submit.prevent="submit"
    >
      <!-- Name -->
      <div>
        <label
          class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1"
          >Tên</label
        >
        <input
          v-model="form.name"
          type="text"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        />
      </div>

      <!-- DOB -->
      <div>
        <label
          class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1"
          >Ngày Sinh</label
        >
        <input
          v-model="form.dob"
          type="date"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        />
      </div>

      <!-- Phone -->
      <div>
        <label
          class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1"
          >Số Điện Thoại</label
        >
        <input
          v-model="form.phone"
          type="tel"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        />
      </div>

      <!-- Membership Package -->
      <div>
        <label
          class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1"
          >Gói Membership</label
        >
        <select
          v-model="form.membershipPackage"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        >
          <option value="" disabled>Chọn gói membership</option>
          <option
            v-for="pkg in membershipPackages"
            :key="pkg.value"
            :value="pkg.value"
          >
            {{ pkg.label }}
          </option>
        </select>
      </div>

      <!-- Address -->
      <div class="md:col-span-2">
        <label
          class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1"
          >Địa Chỉ Nhà</label
        >
        <textarea
          v-model="form.address"
          rows="3"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        ></textarea>
      </div>

      <!-- Buttons -->
      <div class="md:col-span-2 flex items-center gap-2">
        <button
          type="submit"
          class="px-4 py-2 rounded-md bg-blue-600 hover:bg-blue-700 text-white"
        >
          Add Membership
        </button>
      </div>
    </form>
  </div>
</template>
