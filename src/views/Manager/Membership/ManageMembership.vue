<script setup lang="ts">
import { computed, ref, onMounted } from 'vue';
import { RouterLink } from 'vue-router';
import api from '@/services/api';

type Membership = {
    id: number,
    name: string,
    type: 'tier' | 'package',
    duration: number, // months
    price: number,
    description: string,
}

const memberships = ref<Membership[]>([])

const search = ref('')
const membershipType = ref('')

const filteredMemberships = computed(() => {
    return memberships.value.filter(m => {
        const matchesSearch = (m.name || '').toLowerCase().includes(search.value.toLowerCase())
        const matchesType = membershipType.value ? m.type === membershipType.value : true
        return matchesSearch && matchesType
    })
})

onMounted(async () => {
    try {
        // Load tiers from /membershiptier
        const tierRes = await api.get("/membershiptier");
        const tiers = tierRes.data.map((tier: any) => ({ ...tier, type: 'tier' }));

        // Load packages from /memberships (assuming packages are stored there)
        const packageRes = await api.get("/memberships");
        const packages = packageRes.data.filter((item: any) => item.type === 'package');

        memberships.value = [...tiers, ...packages];
        console.log("Memberships loaded:", memberships.value);
    } catch (error) {
        console.error('Failed to load memberships:', error);
        alert("Failed to load memberships. Please try again.");
    }
})
</script>

<template>
    <div class="space-y-4 p-4">
        <!-- Toolbar -->
        <div class="justify-between flex">
             <h1 class="text-xl font-semibold">Membership Packages</h1>
             <div>
                <input type="text" v-model="search" placeholder="Search for package name" class="px-3 py-2 border border-gray-200 rounded-lg bg-white focus:outline-none focus:ring-2 focus:ring-gray-200 mr-2">

                <select v-model="membershipType" class="px-3 py-2 border border-gray-200 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800 mr-2">
                    <option value="">All types</option>
                 <option value="tier">Tiers</option>
                 <option value="package">Packages</option>
                </select>
             </div>
             <div>
                <RouterLink :to="{ name: 'membership.add' }" class="px-3 py-2 rounded-lg bg-blue-600 text-white hover:opacity-90 mr-2">Add Package</RouterLink>
                <RouterLink :to="{ name: 'membership.add-tier' }" class="px-3 py-2 rounded-lg bg-green-600 text-white hover:opacity-90">Add Tier</RouterLink>
             </div>
        </div>

        <!-- Table -->
        <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
            <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                    <tr>
                        <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <span>ID</span>
                        </th>
                        <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <span>Name</span>
                        </th>
                        <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <span>Type</span>
                        </th>
                        <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <span>Duration</span>
                        </th>
                        <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <span>Price</span>
                        </th>
                        <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <span>Description</span>
                        </th>
                        <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <span>Actions</span>
                        </th>
                    </tr>
                </thead>
                <tbody class="divide-y divide-gray-200 bg-white">
                    <tr v-if="memberships.length === 0">
                        <td colspan="7" class="px-4 py-3 text-center text-sm text-gray-500">No membership packages found</td>
                    </tr>
                    <tr v-for="m in filteredMemberships" :key="m.id" class="hover:bg-gray-50">
                        <td class="px-4 py-3 text-sm text-gray-600">{{ m.id }}</td>
                        <td class="px-4 py-3 text-sm text-blue-600 hover:underline hover:cursor-pointer">{{ m.name }}</td>
                        <td class="px-4 py-3 text-sm text-gray-600 capitalize">{{ m.type }}</td>
                        <td class="px-4 py-3 text-sm text-gray-600">{{ m.duration }} month(s)</td>
                        <td class="px-4 py-3 text-sm text-gray-600">{{ m.price.toLocaleString() }} VND</td>
                        <td class="px-4 py-3 text-sm text-gray-600">{{ m.description }}</td>
                        <td class="px-4 py-3 text-sm text-center">
                            <button class="px-2 py-1 rounded bg-green-600 text-white mr-2 hover:cursor-pointer hover:bg-green-700">Edit</button>
                            <button class="px-2 py-1 rounded bg-red-600 text-white hover:cursor-pointer hover:bg-red-700">Delete</button>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</template>