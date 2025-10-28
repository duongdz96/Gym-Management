<script setup lang="ts">
import { ref, onMounted } from "vue"
import { useRouter } from "vue-router"
import api from "@/services/api"

type MembershipTier = {
    id: number,
    name: string,
    priority: number,
    status: string,
}

const router = useRouter()

// form data
const form = ref({
  name: "",
  duration: 1, // in months
  price: 0,
  description: ""
})

const membershipTiers = ref<MembershipTier[]>([])
const selectedTierId = ref<number | null>(null)

const submit = async () => {
  if (!form.value.name || !form.value.duration || !form.value.price || !selectedTierId.value) {
    alert("Please fill in all required fields.")
    return
  }

  const selectedTier = membershipTiers.value.find(tier => tier.id === selectedTierId.value)
  if (!selectedTier) {
    alert("Selected tier not found.")
    return
  }

  const payload = {
    name: form.value.name,
    duration: `${form.value.duration} Months`,
    price: form.value.price,
    benefits: form.value.description || "",
    membershipTier: selectedTier
  }

    console.log("Submitting membership plan:", payload)

  try {
    const res = await api.post("/membershipplan", payload)

    if (res.status === 200 || res.status === 201) {
            alert("Membership plan has been added successfully!")

      form.value = { name: "", duration: 1, price: 0, description: "" }
      selectedTierId.value = null
      router.push({ name: "membership" })
    }
  } catch (err: any) {
        console.error("Error adding membership plan:", err)
        alert("Failed to add membership plan. Please try again.")
  }
}

onMounted(async () => {
  try {
    const res = await api.get("/membershiptier")
    membershipTiers.value = res.data
    console.log("Membership tiers loaded:", membershipTiers.value)
  } catch (error) {
    console.error('Failed to load membership tiers:', error)
    alert("Failed to load membership tiers. Please try again.")
  }
})
</script>


<template>
  <div class="p-4">
    <div class="flex items-center justify-between mb-4">
      <h1 class="text-xl font-semibold">Add Membership Plan</h1>
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
      <!-- Tier Selection -->
      <div>
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Membership Tier</label>
        <select
          v-model="selectedTierId"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        >
          <option :value="null" disabled>Select a tier</option>
          <option v-for="tier in membershipTiers" :key="tier.id" :value="tier.id">
            {{ tier.name }}
          </option>
        </select>
      </div>

      <!-- Name -->
      <div>
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Plan Name</label>
        <input
          v-model="form.name"
          type="text"
          placeholder="e.g. Gold 12 Months, Silver 6 Months"
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
          Add Membership Plan
        </button>
      </div>
    </form>
  </div>
</template>