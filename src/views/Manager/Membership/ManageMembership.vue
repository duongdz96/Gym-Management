<script setup lang="ts">
import { computed, ref, onMounted } from 'vue';
import { RouterLink } from 'vue-router';
import api from '@/services/api';

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

const membershipPlans = ref<MembershipPlan[]>([])
const membershipTiers = ref<MembershipTier[]>([])

const search = ref('')
const membershipType = ref('')
const sortBy = ref('name') // 'name', 'price', 'duration', 'tier'
const sortOrder = ref('asc') // 'asc', 'desc'
const tierSortBy = ref('priority') // 'name', 'priority'
const tierSortOrder = ref('asc') // 'asc', 'desc'

const filteredMembershipPlans = computed(() => {
    let filtered = membershipPlans.value.filter(plan => {
        const matchesSearch = (plan.name || '').toLowerCase().includes(search.value.toLowerCase())
        const matchesType = membershipType.value ? plan.membershipTier.name.toLowerCase().includes(membershipType.value.toLowerCase()) : true
        return matchesSearch && matchesType
    })

    // Sort
    filtered.sort((a, b) => {
        let aValue, bValue

        switch (sortBy.value) {
            case 'price':
                aValue = a.price
                bValue = b.price
                break
            case 'duration':
                aValue = a.duration
                bValue = b.duration
                break
            case 'tier':
                aValue = a.membershipTier.priority
                bValue = b.membershipTier.priority
                break
            default: // name
                aValue = a.name.toLowerCase()
                bValue = b.name.toLowerCase()
        }

        if (sortOrder.value === 'asc') {
            return aValue > bValue ? 1 : aValue < bValue ? -1 : 0
        } else {
            return aValue < bValue ? 1 : aValue > bValue ? -1 : 0
        }
    })

    return filtered
})

const sortedMembershipTiers = computed(() => {
    let sorted = [...membershipTiers.value]

    sorted.sort((a, b) => {
        let aValue, bValue

        if (tierSortBy.value === 'priority') {
            aValue = a.priority
            bValue = b.priority
        } else {
            aValue = a.name.toLowerCase()
            bValue = b.name.toLowerCase()
        }

        if (tierSortOrder.value === 'asc') {
            return aValue > bValue ? 1 : aValue < bValue ? -1 : 0
        } else {
            return aValue < bValue ? 1 : aValue > bValue ? -1 : 0
        }
    })

    return sorted
})

function setSort(field: string) {
    if (sortBy.value === field) {
        sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
    } else {
        sortBy.value = field
        sortOrder.value = 'asc'
    }
}

function setTierSort(field: string) {
    if (tierSortBy.value === field) {
        tierSortOrder.value = tierSortOrder.value === 'asc' ? 'desc' : 'asc'
    } else {
        tierSortBy.value = field
        tierSortOrder.value = 'asc'
    }
}

onMounted(async () => {
    try {
        // Load membership tiers
        const tierRes = await api.get("/membershiptier");
        membershipTiers.value = tierRes.data;

        // Load membership plans
        const planRes = await api.get("/membershipplan");
        membershipPlans.value = planRes.data;

        console.log("Membership tiers loaded:", membershipTiers.value);
        console.log("Membership plans loaded:", membershipPlans.value);
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
             <h1 class="text-xl font-semibold">Membership Plans</h1>
             <div class="flex items-center gap-2">
                <input type="text" v-model="search" placeholder="Search for package name" class="px-3 py-2 border border-gray-200 rounded-lg bg-white focus:outline-none focus:ring-2 focus:ring-gray-200">

                <select v-model="membershipType" class="px-3 py-2 border border-gray-200 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800">
                    <option value="">All tiers</option>
                    <option v-for="tier in membershipTiers" :key="tier.id" :value="tier.name">{{ tier.name }}</option>
                </select>

                <select v-model="sortBy" class="px-3 py-2 border border-gray-200 rounded-lg bg-white">
                    <option value="name">Sort by Name</option>
                    <option value="price">Sort by Price</option>
                    <option value="duration">Sort by Duration</option>
                    <option value="tier">Sort by Tier</option>
                </select>

                <button @click="sortOrder = sortOrder === 'asc' ? 'desc' : 'asc'" class="px-3 py-2 border border-gray-200 rounded-lg bg-white hover:bg-gray-50">
                    {{ sortOrder === 'asc' ? '↑' : '↓' }}
                </button>
             </div>
             <div>
                <RouterLink :to="{ name: 'membership.add' }" class="px-3 py-2 rounded-lg bg-blue-600 text-white hover:opacity-90 mr-2">Add Plan</RouterLink>
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
                            <button @click="setSort('name')" class="hover:text-gray-700 flex items-center">
                                <span>Name</span>
                                <span v-if="sortBy === 'name'" class="ml-1">{{ sortOrder === 'asc' ? '↑' : '↓' }}</span>
                            </button>
                        </th>
                        <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <button @click="setSort('tier')" class="hover:text-gray-700 flex items-center">
                                <span>Tier</span>
                                <span v-if="sortBy === 'tier'" class="ml-1">{{ sortOrder === 'asc' ? '↑' : '↓' }}</span>
                            </button>
                        </th>
                        <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <button @click="setSort('duration')" class="hover:text-gray-700 flex items-center">
                                <span>Duration</span>
                                <span v-if="sortBy === 'duration'" class="ml-1">{{ sortOrder === 'asc' ? '↑' : '↓' }}</span>
                            </button>
                        </th>
                        <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <button @click="setSort('price')" class="hover:text-gray-700 flex items-center">
                                <span>Price</span>
                                <span v-if="sortBy === 'price'" class="ml-1">{{ sortOrder === 'asc' ? '↑' : '↓' }}</span>
                            </button>
                        </th>
                        <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <span>Benefits</span>
                        </th>
                        <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <span>Status</span>
                        </th>
                        <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <span>Actions</span>
                        </th>
                    </tr>
                </thead>
                <tbody class="divide-y divide-gray-200 bg-white">
                    <tr v-if="membershipPlans.length === 0">
                        <td colspan="7" class="px-4 py-3 text-center text-sm text-gray-500">No membership plans found</td>
                    </tr>
                    <tr v-for="plan in filteredMembershipPlans" :key="plan.id" class="hover:bg-gray-50">
                        <td class="px-4 py-3 text-sm text-gray-600">{{ plan.id }}</td>
                        <td class="px-4 py-3 text-sm text-blue-600 hover:underline hover:cursor-pointer">{{ plan.name }}</td>
                        <td class="px-4 py-3 text-sm text-gray-600">{{ plan.membershipTier.name }}</td>
                        <td class="px-4 py-3 text-sm text-gray-600">{{ plan.duration }}</td>
                        <td class="px-4 py-3 text-sm text-gray-600">{{ plan.price.toLocaleString() }} VND</td>
                        <td class="px-4 py-3 text-sm text-gray-600">{{ plan.benefits }}</td>
                        <td class="px-4 py-3 text-sm text-center">
                            <span :class="[
                                'px-2 py-1 rounded-full text-xs', plan.status === 'Active' ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'
                            ]">
                            {{ plan.status }}
                            </span>
                        </td>
                        <td class="px-4 py-3 text-sm text-center">
                            <RouterLink :to="{ name: 'membership.edit', params: { id: plan.id } }" class="text-blue-600 hover:text-blue-800">Edit</RouterLink>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

        <!-- Membership Tiers Table -->
        <div class="mt-8">
            <div class="flex justify-between items-center mb-4">
                <h2 class="text-lg font-semibold">Membership Tiers</h2>
                <div class="flex items-center gap-2">
                    <select v-model="tierSortBy" class="px-3 py-2 border border-gray-200 rounded-lg bg-white text-sm">
                        <option value="priority">Sort by Priority</option>
                        <option value="name">Sort by Name</option>
                    </select>
                    <button @click="tierSortOrder = tierSortOrder === 'asc' ? 'desc' : 'asc'" class="px-3 py-2 border border-gray-200 rounded-lg bg-white hover:bg-gray-50 text-sm">
                        {{ tierSortOrder === 'asc' ? '↑' : '↓' }}
                    </button>
                </div>
            </div>
            <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
                <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                        <tr>
                            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                <span>ID</span>
                            </th>
                            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                <button @click="setTierSort('name')" class="hover:text-gray-700 flex items-center">
                                    <span>Name</span>
                                    <span v-if="tierSortBy === 'name'" class="ml-1">{{ tierSortOrder === 'asc' ? '↑' : '↓' }}</span>
                                </button>
                            </th>
                            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                <button @click="setTierSort('priority')" class="hover:text-gray-700 flex items-center">
                                    <span>Priority</span>
                                    <span v-if="tierSortBy === 'priority'" class="ml-1">{{ tierSortOrder === 'asc' ? '↑' : '↓' }}</span>
                                </button>
                            </th>
                            <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
                                <span>Status</span>
                            </th>
                            <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
                                <span>Actions</span>
                            </th>
                        </tr>
                    </thead>
                    <tbody class="divide-y divide-gray-200 bg-white">
                        <tr v-if="membershipTiers.length === 0">
                            <td colspan="5" class="px-4 py-3 text-center text-sm text-gray-500">No membership tiers found</td>
                        </tr>
                        <tr v-for="tier in sortedMembershipTiers" :key="tier.id" class="hover:bg-gray-50">
                            <td class="px-4 py-3 text-sm text-gray-600">{{ tier.id }}</td>
                            <td class="px-4 py-3 text-sm text-blue-600 hover:underline hover:cursor-pointer">{{ tier.name }}</td>
                            <td class="px-4 py-3 text-sm text-gray-600">{{ tier.priority }}</td>
                            <td class="px-4 py-3 text-sm text-center">
                                <span :class="[
                                    'px-2 py-1 rounded-full text-xs', tier.status === 'Active' ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'
                                ]">
                                {{ tier.status }}
                                </span>
                            </td>
                            <td class="px-4 py-3 text-sm text-center">
                                <RouterLink :to="{ name: 'membership.edit-tier', params: { id: tier.id } }" class="text-blue-600 hover:text-blue-800">Edit</RouterLink>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</template>