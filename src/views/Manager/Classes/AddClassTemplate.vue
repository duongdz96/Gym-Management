<script setup lang="ts">
import { ref } from "vue"
import { useRouter } from "vue-router"
import api from "@/services/api"

const router = useRouter()

const form = ref({
  name: "",
  description: "",
  difficultyLevel: "",
  status: "ACTIVE"
})

const submit = async () => {
  console.log("Submitting class template:", form.value)
  try {
    const res = await api.post("/fitness_class", form.value)
    if (res.status === 200 || res.status === 201) {
      alert("Class Template created successfully!")
      router.push({ name: "classtemplate" })
    }
  } catch (err) {
    console.error(err)
    alert("Failed to create class template.")
  }
}
</script>

<template>
  <div class="p-4">
    <!-- Header -->
    <div class="flex items-center justify-between mb-4">
      <h1 class="text-xl font-semibold">Add Class Template</h1>
      <RouterLink
        :to="{ name: 'classtemplate' }"
        class="px-3 py-2 rounded-md bg-gray-200 dark:bg-gray-800 text-gray-900 dark:text-gray-100"
      >
        Back
      </RouterLink>
    </div>

    <!-- Form -->
    <form
      class="bg-white dark:bg-gray-900 border border-gray-200 dark:border-gray-800 rounded-lg p-4 grid grid-cols-1 md:grid-cols-2 gap-4"
      @submit.prevent="submit"
    >
      <!-- Name -->
      <div class="md:col-span-2">
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Name</label>
        <input
          v-model="form.name"
          type="text"
          placeholder="Enter class name"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        />
      </div>

      <!-- Difficulty Level -->
      <div>
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Difficulty Level</label>
        <select
          v-model="form.difficultyLevel"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        >
          <option disabled value="">Select difficulty</option>
          <option value="BEGINNER">Beginner</option>
          <option value="INTERMEDIATE">Intermediate</option>
          <option value="ADVANCED">Advanced</option>
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
          <option value="ACTIVE">Active</option>
          <option value="INACTIVE">Inactive</option>
        </select>
      </div>

      <!-- Description -->
      <div class="md:col-span-2">
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Description</label>
        <textarea
          v-model="form.description"
          rows="3"
          placeholder="Enter class description..."
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
        ></textarea>
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
