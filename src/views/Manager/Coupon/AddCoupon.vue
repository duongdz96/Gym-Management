<script setup lang="ts">
import { ref } from "vue"
import { useRouter } from "vue-router"

const router = useRouter()

// form data
const form = ref({
  code: "",
  discountType: 1 as number | null,
  discountValue: 0,
  startDate: "",
  endDate: "",
  status: 1 as number | null, 
  scope: ""
})

const submit = () => {
  console.log("Coupon submitted:", form.value)
  router.push({ name: "coupon" })
}
</script>


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
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Code</label>
        <input
          v-model="form.code"
          type="text"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        />
      </div>  

      <!-- Discount Type -->
      <div>
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Discount Type</label>
        <select
          v-model="form.discountType"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        >
          <option :value="1" selected>Percentage</option>
          <option :value="2">Fixed</option>
        </select>
      </div>

      <!-- Discount Value -->
      <div>
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Discount Value</label>
        <input
          v-model.number="form.discountValue"
          type="number"
          min="0"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        />
      </div>

      <!-- Status -->
      <div>
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Status</label>
        <select
          v-model="form.status"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        >
          <option :value="1" selected>Active</option>
          <option :value="2">Inactive</option>
        </select>
      </div>

      <!-- Start Date -->
      <div>
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Start Date</label>
        <input
          v-model="form.startDate"
          type="date"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        />
      </div>

      <!-- End Date -->
      <div>
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">End Date</label>
        <input
          v-model="form.endDate"
          type="date"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        />
      </div>

      <!-- Scope -->
      <div class="md:col-span-2">
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Scope</label>
        <input
          v-model="form.scope"
          type="text"
          placeholder="e.g. gold, bronze, all, product..."
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
      </div>

      <!-- Buttons -->
      <div class="md:col-span-2 flex items-center gap-2">
        <button
          type="submit"
          class="px-4 py-2 rounded-md bg-blue-600 hover:bg-blue-700 text-white"
        >
          Add
        </button>
      </div>
    </form>
  </div>
</template>
