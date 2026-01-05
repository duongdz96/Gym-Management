<script setup lang="ts">
import { computed, ref, onMounted } from 'vue';
import { RouterLink } from 'vue-router';
import api from '@/services/api';
import { useToast } from 'vue-toastification';

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
const toast = useToast();

const filteredMembershipPlans = computed(() => {
    return membershipPlans.value.filter(plan => {
        const matchesSearch = (plan.name || '').toLowerCase().includes(search.value.toLowerCase())
        const matchesType = membershipType.value ? plan.membershipTier.name.toLowerCase().includes(membershipType.value.toLowerCase()) : true
        return matchesSearch && matchesType
    })
})

onMounted(async () => {
    try {
        // Load membership tiers
        const tierRes = await api.get("/membershiptier");
        membershipTiers.value = tierRes.data;

        // Load membership plans
        const planRes = await api.get("/membershipplan");
        membershipPlans.value = planRes.data;

    } catch (error) {
        toast.error("Failed to load memberships. Please try again.");
    }
})
</script>

<template>
    <div class="space-y-4 p-4">
        <!-- Toolbar -->
        <div class="justify-between flex">
             <h1 class="text-xl font-semibold">Gói hội viên</h1>
             <div>
                <input type="text" v-model="search" placeholder="Tìm kiếm theo tên" class="px-3 py-2 border border-gray-200 rounded-lg bg-white focus:outline-none focus:ring-2 focus:ring-gray-200 mr-2">

                <select v-model="membershipType" class="px-3 py-2 border border-gray-200 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800 mr-2">
                 <option value="">Tất cả các cấp</option>
                 <option value="basic">Cơ bản</option>
                 <option value="standard">Tiêu chuẩn</option>
                 <option value="vip">VIP</option>
                </select>
             </div>
             <div>
                <RouterLink :to="{ name: 'membership.add' }" class="px-3 py-2 rounded-lg bg-blue-600 text-white hover:opacity-90 mr-2">Thêm gói</RouterLink>
                <RouterLink :to="{ name: 'membership.add-tier' }" class="px-3 py-2 rounded-lg bg-green-600 text-white hover:opacity-90">Thêm cấp</RouterLink>
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
                            <span>Tên gói hội viên</span>
                        </th>
                        <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <span>Cấp</span>
                        </th>
                        <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <span>Thời lượng</span>
                        </th>
                        <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <span>Giá</span>
                        </th>
                        <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <span>Lợi ích</span>
                        </th>
                        <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <span>Trạng thái</span>
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
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</template>