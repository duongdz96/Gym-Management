<script setup>
import { ref, onMounted } from "vue";

const members = ref([
  {
    id: 1,
    name: "Nguyễn Thị A",
    email: "a@example.com",
    membership: "Gói 3 tháng",
    status: "Active",
  },
  {
    id: 2,
    name: "Trần Văn B",
    email: "b@example.com",
    membership: "Gói 6 tháng",
    status: "Active",
  },
  {
    id: 3,
    name: "Lê Thị C",
    email: "c@example.com",
    membership: "Gói 1 tháng",
    status: "Expired",
  },
]);

onMounted(() => {
  console.log("PT Members loaded");
});
</script>

<template>
  <div class="p-6 space-y-8">
    <!-- Tiêu đề -->
    <h1 class="text-2xl font-bold text-stone-800">Danh Sách Hội Viên</h1>

    <!-- Danh sách hội viên -->
    <section class="bg-white rounded-xl shadow p-5">
      <h2 class="text-xl font-semibold mb-3">Hội viên của tôi</h2>
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th
                class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
              >
                Họ tên
              </th>
              <th
                class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
              >
                Email
              </th>
              <th
                class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
              >
                Gói tập
              </th>
              <th
                class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
              >
                Trạng thái
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="member in members" :key="member.id">
              <td
                class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900"
              >
                {{ member.name }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ member.email }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ member.membership }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span
                  :class="
                    member.status === 'Active'
                      ? 'text-green-600 bg-green-100'
                      : 'text-red-600 bg-red-100'
                  "
                  class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full"
                  >{{ member.status }}</span
                >
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </div>
</template>
