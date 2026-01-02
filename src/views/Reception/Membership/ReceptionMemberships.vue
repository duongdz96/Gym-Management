<script setup lang="ts">
import { computed, ref, onMounted } from 'vue';
import api from '@/services/api';
import { useToast } from 'vue-toastification';
import { useRouter } from 'vue-router';

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
const router = useRouter();
const search = ref('')
const statusFilter = ref('')
const toast = useToast();

const filteredMemberships = computed(() => {
    return memberships.value.filter(membership => {
        const matchesSearch = (membership.member.fullName || '').toLowerCase().includes(search.value.toLowerCase()) ||
            (membership.membershipPlan.name || '').toLowerCase().includes(search.value.toLowerCase())
        const matchesStatus = statusFilter.value ? membership.status.toLowerCase() === statusFilter.value.toLowerCase() : true
        return matchesSearch && matchesStatus
    })
})
const goBack = () => {
    router.back();
}

onMounted(async () => {
    console.log("Vao trang reception memberships");
    try {
        // Load memberships
        const res = await api.get("/membership");
        memberships.value = res.data;

    } catch (error) {
        toast.error("Tải hội viên thất bại. Vui lòng thử lại");
    }
})
</script>

<template>
    <div class="space-y-4 p-4">
        <!-- Toolbar -->
        <div class="justify-between flex">
            <button @click="goBack" class="p-2 hover:bg-gray-100 rounded-full transition-colors text-gray-600"
                title="Quay lại">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2"
                    stroke="currentColor" class="w-5 h-5">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M10.5 19.5L3 12m0 0l7.5-7.5M3 12h18" />
                </svg>
            </button>
            <h1 class="text-xl font-semibold">Thành viên khách hàng</h1>
            <div>
                <input type="text" v-model="search" placeholder="Tìm tên thành viên"
                    class="px-3 py-2 border border-gray-200 rounded-lg bg-white focus:outline-none focus:ring-2 focus:ring-gray-200 mr-2">

                <select v-model="statusFilter"
                    class="px-3 py-2 border border-gray-200 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800 mr-2">
                    <option value="">Mọi trạng thái</option>
                    <option value="Active">Còn hạn</option>
                    <option value="Expired">Hết hạn</option>
                    <option value="Upgraded">Nâng cấp</option>
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
                            <span>Tên thành viên</span>
                        </th>
                        <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <span>Gói thành viên</span>
                        </th>
                        <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <span>Bậc</span>
                        </th>
                        <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <span>Ngày bắt đầu</span>
                        </th>
                        <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <span>Ngày kết thúc</span>
                        </th>
                        <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <span>Trạng thái</span>
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
                        <td class="px-4 py-3 text-sm text-gray-600">{{ membership.membershipPlan.membershipTier.name }}
                        </td>
                        <td class="px-4 py-3 text-sm text-gray-600">{{ new
                            Date(membership.startDate).toLocaleDateString() }}</td>
                        <td class="px-4 py-3 text-sm text-gray-600">{{ new Date(membership.endDate).toLocaleDateString()
                        }}</td>
                        <td class="px-4 py-3 text-sm text-center">
                            <span :class="[
                                'px-2 py-1 rounded-full text-xs',
                                {
                                    'bg-green-100 text-green-800': membership.status === 'Active',
                                    'bg-red-100 text-red-800': membership.status === 'Expired',
                                    'bg-blue-100 text-blue-800': membership.status === 'Upgraded'
                                }
                            ]">
                                {{
                                    membership.status === 'Active' ? 'Active' :
                                        membership.status === 'Upgraded' ? 'Upgraded' : 'Inactive'
                                }}
                            </span>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</template>