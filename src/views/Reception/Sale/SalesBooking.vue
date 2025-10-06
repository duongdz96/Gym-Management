<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { RouterLink, useRouter } from "vue-router";
import api from "../../../services/api";

type Staff = {
  id: number;
  fullName: string;
  specialize: string;
  hirePrice: number;
  position: string;
};

type StaffAssigned = {
  staff: Staff;
  trainingSession: number;
};

const staffs = ref<Staff[]>([]);
const selectedStaffs = ref<StaffAssigned[]>([]);
const bill = ref<any>(null);
const router = useRouter();

onMounted(async () => {
  const data = sessionStorage.getItem("currentBill");
  if (data) {
    bill.value = JSON.parse(data);
    selectedStaffs.value = bill.value.listStaffAssigned || [];
  }

  try {
    const res = await api.get("/staffs");
    staffs.value = res.data;
  } catch (err) {
    console.error("Error fetching staff:", err);
  }
});


function toggleSelectStaff(staff: Staff) {
  const existing = selectedStaffs.value.find((item) => item.staff.id === staff.id);
  if (existing) {
    existing.trainingSession += 1;
  } else {
    selectedStaffs.value.push({ staff, trainingSession: 1 });
  }
}

function proceedToCheckout() {
  if (
    (!bill.value?.listSoldProduct || bill.value.listSoldProduct.length === 0) &&
    selectedStaffs.value.length === 0
  ) {
    alert("Please select at least one product or one staff before continuing!");
    return;
  }

  bill.value.listStaffAssigned = selectedStaffs.value.map((item) => ({
    staff: {
      id: item.staff.id,
      fullName: item.staff.fullName,
      specialize: item.staff.specialize,
      hirePrice: item.staff.hirePrice,
      position: item.staff.position,
    },
    trainingSession: item.trainingSession,
  }));
  console.log("bill trong sales booking");
  console.log(bill);
  sessionStorage.setItem("currentBill", JSON.stringify(bill.value));

  router.push({ name: "salesCheckout" });
}


const totalPrice = () =>
  (bill.value?.listSoldProduct?.reduce(
    (sum, item) => sum + (item.product?.price || 0) * (item.quantity || 0),
    0
  ) || 0) +
  (selectedStaffs.value?.reduce(
    (sum, item) => sum + (item.staff?.hirePrice || 0) * (item.trainingSession || 0),
    0
  ) || 0);

const search = ref("");
const filteredStaffs = computed(() => {
  return staffs.value.filter((s) =>
    (s.fullName + " " + s.specialize)
      .toLowerCase()
      .includes(search.value.toLowerCase())
  );
});

function removeFromCart(staffId: number) {
  selectedStaffs.value = selectedStaffs.value.filter(item => item.staff.id !== staffId);
}

</script>

<template>
  <div class="p-4 space-y-4">
    <div class="flex space-x-4">
      <!-- Left: staff table -->
      <div class="w-5/7 bg-white border border-gray-200 rounded-lg overflow-hidden">
        <div class="p-4">
          <input
            type="text"
            v-model="search"
            placeholder="Search staff by name or specialty..."
            class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg bg-white 
                   focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500
                   text-sm text-gray-700"
          />
        </div>

        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-4 py-3">ID</th>
              <th class="px-4 py-3">Name</th>
              <th class="px-4 py-3">Position</th>
              <th class="px-4 py-3">Specialize</th>
              <th class="px-4 py-3">Hire Price</th>
              <th class="px-4 py-3 text-center">Action</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
            <tr
              v-for="s in filteredStaffs"
              :key="s.id"
              class="hover:bg-gray-50"
            >
              <td class="px-4 py-2">{{ s.id }}</td>
              <td class="px-4 py-2">{{ s.fullName }}</td>
              <td class="px-4 py-2">{{ s.position }}</td>
              <td class="px-4 py-2">{{ s.specialize || 'N/A' }}</td>
              <td class="px-4 py-2">{{ s.hirePrice.toLocaleString() }} đ</td>
              <td class="px-4 py-2 text-center">
                <button
                  @click="toggleSelectStaff(s)"
                  class="px-2 py-1 rounded bg-green-600 text-white hover:bg-green-700"
                >
                  Select
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Right: cart -->
      <div class="w-2/7 bg-white border border-gray-200 rounded-lg overflow-hidden flex flex-col">
        <h2 class="text-lg font-semibold p-4 border-b border-gray-200">Selected Staffs</h2>

        <div class="flex-1 overflow-y-auto p-4">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-4 py-3">Name</th>
                <th class="px-4 py-3">Price</th>
                <th class="px-4 py-3">Sessions</th>
                <th class="px-4 py-3 text-center">Action</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200">
              <tr v-for="item in selectedStaffs" :key="item.staff.id">
                <td class="px-4 py-2">{{ item.staff.fullName }}</td>
                <td class="px-4 py-2">{{ item.staff.hirePrice.toLocaleString() }} đ</td>
                <td class="px-4 py-2">
                  <input
                    type="number"
                    min="1"
                    v-model.number="item.trainingSession"
                    class="w-16 border border-gray-300 rounded px-2 py-1"
                  />
                </td>
                <td class="px-4 py-2 text-center">
                  <button
                    @click="removeFromCart(item.staff.id)"
                    class="px-2 py-1 rounded bg-red-600 text-white hover:bg-red-700"
                  >
                    Remove
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="p-4 border-t border-gray-200">
          <div class="mb-2 font-semibold text-right">
            Total: {{ totalPrice().toLocaleString() }} đ
          </div>
          <button
            @click="proceedToCheckout"
            class="w-full px-3 py-2 rounded bg-green-600 text-white hover:bg-green-700"
            >
            Continue
          </button>

        </div>
      </div>
    </div>
  </div>
</template>