<script setup lang="ts">
import { ref } from "vue"
import { useRouter } from "vue-router"
import api from "@/services/api"

const router = useRouter()

// form data
const form = ref({
  name: "",
  duration: 1, // in months
  price: 0,
  description: ""
})

const submit = async () => {
  if (!form.value.name || !form.value.duration || !form.value.price) {
    alert("Please fill in all required fields.")
    return
  }

  const payload = {
    name: form.value.name,
    type: "package",
    duration: form.value.duration,
    price: form.value.price,
    description: form.value.description || null,
  }

  console.log("Submitting membership package:", payload)

  try {
    const res = await api.post("/membershiptier", payload)

    if (res.status === 200 || res.status === 201) {
      alert("Membership package has been added successfully!")

      form.value = { name: "", duration: 1, price: 0, description: "" }
      router.push({ name: "membership" })
    }
  } catch (err: any) {
    console.error("Error adding membership package:", err)
    alert("Failed to add membership package. Please try again.")
  }
}
</script>


<template>
  <div class="p-4">
    <div class="flex items-center justify-between mb-4">
      <h1 class="text-xl font-semibold">Add Membership Package</h1>
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
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Package Name</label>
        <input
          v-model="form.name"
          type="text"
          placeholder="e.g. Gold, Silver, Bronze"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        />
      </div>

      <!-- Duration -->
      <div>
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Duration (Months)</label>
        <input
          v-model.number="form.duration"
          type="number"
          min="1"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        />
      </div>

      <!-- Price -->
      <div>
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Price (VND)</label>
        <input
          v-model.number="form.price"
          type="number"
          min="0"
          step="1000"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        />
      </div>

      <!-- Description -->
      <div class="md:col-span-2">
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Description</label>
        <textarea
          v-model="form.description"
          rows="3"
          placeholder="Describe the benefits of this membership package"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
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