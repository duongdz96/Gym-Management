<script setup lang="ts">
import { computed, ref, onMounted } from 'vue';
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

type Member = {
    id: number,
    email: string,
    fullName: string,
    dob: string,
    gender: string,
    phone: string,
    role: string,
}

type Membership = {
    id: number,
    startDate: string,
    endDate: string,
    status: string,
    membershipPlan: MembershipPlan,
    member: Member,
}

const memberships = ref<Membership[]>([])

const search = ref('')
const statusFilter = ref('')

const filteredMemberships = computed(() => {
    return memberships.value.filter(membership => {
        const matchesSearch = (membership.member.fullName || '').toLowerCase().includes(search.value.toLowerCase()) ||
                              (membership.membershipPlan.name || '').toLowerCase().includes(search.value.toLowerCase())
        const matchesStatus = statusFilter.value ? membership.status.toLowerCase() === statusFilter.value.toLowerCase() : true
        return matchesSearch && matchesStatus
    })
})

onMounted(async () => {
    try {
        // Load memberships
        const res = await api.get("/membership");
        memberships.value = res.data;

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
             <h1 class="text-xl font-semibold">Customer Memberships</h1>
             <div>
                <input type="text" v-model="search" placeholder="Search by member name or plan name" class="px-3 py-2 border border-gray-200 rounded-lg bg-white focus:outline-none focus:ring-2 focus:ring-gray-200 mr-2">

                <select v-model="statusFilter" class="px-3 py-2 border border-gray-200 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800 mr-2">
                    <option value="">All statuses</option>
                 <option value="Active">Active</option>
                 <option value="Expired">Expired</option>
                </select>
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
                            <span>Member Name</span>
                        </th>
                        <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <span>Plan Name</span>
                        </th>
                        <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <span>Tier</span>
                        </th>
                        <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <span>Start Date</span>
                        </th>
                        <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <span>End Date</span>
                        </th>
                        <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <span>Status</span>
                        </th>
                    </tr>
                </thead>
                <tbody class="divide-y divide-gray-200 bg-white">
                    <tr v-if="memberships.length === 0">
                        <td colspan="7" class="px-4 py-3 text-center text-sm text-gray-500">No memberships found</td>
                    </tr>
                    <tr v-for="membership in filteredMemberships" :key="membership.id" class="hover:bg-gray-50">
                        <td class="px-4 py-3 text-sm text-gray-600">{{ membership.id }}</td>
                        <td class="px-4 py-3 text-sm text-gray-600">{{ membership.member.fullName }}</td>
                        <td class="px-4 py-3 text-sm text-gray-600">{{ membership.membershipPlan.name }}</td>
                        <td class="px-4 py-3 text-sm text-gray-600">{{ membership.membershipPlan.membershipTier.name }}</td>
                        <td class="px-4 py-3 text-sm text-gray-600">{{ new Date(membership.startDate).toLocaleDateString() }}</td>
                        <td class="px-4 py-3 text-sm text-gray-600">{{ new Date(membership.endDate).toLocaleDateString() }}</td>
                        <td class="px-4 py-3 text-sm text-center">
                            <span :class="[
                                'px-2 py-1 rounded-full text-xs', membership.status === 'Active' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                            ]">
                            {{ membership.status }}
                            </span>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</template>