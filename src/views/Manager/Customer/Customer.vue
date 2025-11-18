<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import api from '@/services/api'

type Member = {
  id: number
  fullName: string
  membership: string
  joinDate: string
  status: string
  faceId: string
  cardId: string
}

const members = ref<Member[]>([])
const search = ref('')
const membershipFilter = ref('')

onMounted(async () => {
  try {
    const res = await api.get("/members")
    members.value = res.data
  } catch (err) {
    console.error("Error fetching members:", err)
  }
})

const filteredMembers = computed(() => {
  return members.value.filter(m => {
    const matchesName = m.fullName?.toLowerCase().includes(search.value.toLowerCase())
    const matchesMembership = membershipFilter.value ? m.membership.toLowerCase() === membershipFilter.value : true
    return matchesName && matchesMembership
  })
})
</script>

<template>
  <div class="space-y-4 p-4">
    <!-- Header -->
    <div class="flex justify-between items-center">
      <h1 class="text-xl font-semibold">Member</h1>
      <div>
        <input
          v-model="search"
          type="text"
          placeholder="Search by member name"
          class="px-3 py-2 border rounded-lg mr-2"
        />
        <select v-model="membershipFilter" class="px-3 py-2 border rounded-lg mr-2">
          <option value="">All memberships</option>
          <option value="basic">Basic</option>
          <option value="standard">Standard</option>
          <option value="gold">Gold</option>
          <option value="premium">Premium</option>
          <option value="vip">VIP</option>
        </select>
        <button
          class="px-3 py-2 bg-blue-600 text-white rounded-lg hover:opacity-90 mr-2"
        >
          Add
        </button>
        <RouterLink
          :to="{ name: 'customer.checkin-history' }"
          class="px-3 py-2 bg-green-600 text-white rounded-lg hover:opacity-90"
        >
          Checkin History
        </RouterLink>
      </div>
    </div>

    <!-- Table -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 mt-6">
      <div
        v-for="m in filteredMembers"
        :key="m.id"
        class="bg-white border border-gray-200 rounded-xl shadow-sm hover:shadow-md transition-shadow p-5"
      >
        <div class="flex justify-between items-start">
          <h2 class="text-lg font-semibold text-gray-800">{{ m.fullName }}</h2>
          <span
            class="px-2 py-1 text-xs font-medium rounded-full"
            :class="{
              'bg-green-100 text-green-700': m.status === 'Active',
              'bg-yellow-100 text-yellow-700': m.status === 'Inactive',
              'bg-red-100 text-red-700': m.status === 'Suspended',
            }"
          >
            {{ m.status || 'Unknown' }}
          </span>
        </div>

        <div class="mt-3 space-y-2 text-sm text-gray-600">
          <p><span class="font-medium text-gray-700">ID:</span> {{ m.id }}</p>
          <p><span class="font-medium text-gray-700">Membership:</span> {{ m.membership || '-' }}</p>
          <p>
            <span class="font-medium text-gray-700">Join Date:</span>
            {{ m.joinDate ? new Date(m.joinDate).toLocaleDateString() : '-' }}
          </p>
          <p><span class="font-medium text-gray-700">Face ID:</span> {{ m.faceId || '-' }}</p>
          <p><span class="font-medium text-gray-700">Card ID:</span> {{ m.cardId || '-' }}</p>
        </div>

        <div class="mt-4 flex justify-end gap-2">
          <button
            class="px-3 py-1 rounded-md bg-green-600 text-white text-sm hover:bg-green-700"
          >
            Edit
          </button>
          <button
            class="px-3 py-1 rounded-md bg-red-600 text-white text-sm hover:bg-red-700"
          >
            Delete
          </button>
        </div>
      </div>

      <!-- Nếu không có thành viên nào -->
      <div
        v-if="filteredMembers.length === 0"
        class="col-span-full text-center text-gray-500 py-10 border border-dashed border-gray-300 rounded-lg"
      >
        No members found
      </div>
    </div>

  </div>
</template>
