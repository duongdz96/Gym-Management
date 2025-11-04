<script setup lang="ts">
import { ref, onMounted } from "vue"
import { useRouter, useRoute } from "vue-router"
import api from "@/services/api"

const router = useRouter()
const route = useRoute()

// form data
const form = ref({
  name: "",
  priority: 1,
  status: "Active",
  description: ""
})

const tierId = ref<number>(parseInt(route.params.id as string))

const submit = async () => {
  if (!form.value.name) {
    alert("Please fill in all required fields.")
    return
  }

  const payload = {
    name: form.value.name,
    priority: form.value.priority,
    status: form.value.status
  }

  console.log("Updating membership tier:", payload)

  try {
    const res = await api.put(`/membershiptier/${tierId.value}`, payload)

    if (res.status === 200 || res.status === 201) {
      alert("Membership tier has been updated successfully!")

      router.push({ name: "membership" })
    }
  } catch (err: any) {
    console.error("Error updating membership tier:", err)
    alert("Failed to update membership tier. Please try again.")
  }
}

const loadTier = async () => {
  try {
    const res = await api.get(`/membershiptier/${tierId.value}`)
    const tier = res.data
    form.value = {
      name: tier.name,
      priority: tier.priority,
      status: tier.status,
      description: ""
    }
  } catch (error) {
    console.error('Failed to load membership tier:', error)
    alert("Failed to load membership tier. Please try again.")
  }
}

onMounted(async () => {
  await loadTier()
})
</script>


<template>
  <div class="p-4">
    <div class="flex items-center justify-between mb-4">
      <h1 class="text-xl font-semibold">Edit Membership Tier</h1>
      <RouterLink
        :to="{ name: 'membership' }"
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
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Tier Name</label>
        <input
          v-model="form.name"
          type="text"
          placeholder="e.g. Gold, Silver, Bronze"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        />
      </div>

      <!-- Priority -->
      <div>
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Priority</label>
        <select
          v-model.number="form.priority"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        >
          <option value="1">1 (Highest)</option>
          <option value="2">2</option>
          <option value="3">3</option>
          <option value="4">4</option>
          <option value="5">5 (Lowest)</option>
        </select>
      </div>

      <!-- Status -->
      <div>
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Status</label>
        <select
          v-model="form.status"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        >
          <option value="Active">Active</option>
          <option value="Inactive">Inactive</option>
        </select>
      </div>

      <!-- Description -->
      <div class="md:col-span-2">
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Description</label>
        <textarea
          v-model="form.description"
          rows="3"
          placeholder="Describe the benefits and features of this membership tier"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
        ></textarea>
      </div>

      <!-- Buttons -->
      <div class="md:col-span-2 flex items-center gap-2">
        <button
          type="submit"
          class="px-4 py-2 rounded-md bg-blue-600 hover:bg-blue-700 text-white"
        >
          Update Tier
        </button>
      </div>
    </form>
  </div>
</template>