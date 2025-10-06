<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { RouterLink } from "vue-router";
import api from "../../../services/api";
import { useRouter } from "vue-router";

const router = useRouter();
const bill = ref<any>(null);
const coupon = ref("");
const paymentMethod = ref<"CARD" | "CASH" | "BANKING" | null>(null);

onMounted(() => {
  const data = sessionStorage.getItem("currentBill");
  if (data) {
    bill.value = JSON.parse(data);
    console.log(bill);
  }
});

const totalPrice = computed(() => {
  if (!bill.value) return 0;

  const productTotal =
    bill.value.listSoldProduct?.reduce(
      (sum, item) =>
        sum + (item.product?.price || 0) * (item.quantity || 0),
      0
    ) || 0;

  const staffTotal =
    bill.value.listStaffAssigned?.reduce(
      (sum, item) =>
        sum + (item.staff?.hirePrice || 0) * (item.trainingSession || 0),
      0
    ) || 0;

  return productTotal + staffTotal;
});

async function submit() {
  if (!paymentMethod.value) {
    alert("Please select a payment method.");
    return;
  }

  const payload = {
    ...bill.value,
    coupon: coupon.value || null,
    paymentMethod: paymentMethod.value,
    paymentStatus: "PAID",
  };

  console.log("Submit bill:", payload);

  try {
    const res = await api.post("/bills", payload);

    if (res.status === 200 || res.status === 201) {
      alert("Bill has been saved successfully!");

      sessionStorage.removeItem("currentBill");
      bill.value = null;
      coupon.value = "";
      paymentMethod.value = null;

      router.push({ name: "salesselect" });
    }
  } catch (err: any) {
    console.error("Error submitting bill:", err);
    alert("Failed to save bill. Please try again.");
  }
}


</script>

<template>
  <div class="p-6 max-w-3xl mx-auto space-y-8 bg-gray-50 rounded-lg shadow-md">
    <h1 class="text-3xl font-bold text-gray-900 border-b pb-4 mb-6">
      Sales Checkout
    </h1>

    <!-- Bill Detail -->
    <div v-if="bill" class="bg-white rounded-lg shadow-sm p-6">
      <!-- Products -->
      <h2 class="font-semibold mb-4 text-lg border-b pb-2">Selected Products</h2>
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-700">Product Name</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-700">Quantity</th>
            <th class="px-6 py-3 text-right text-sm font-medium text-gray-700">Price</th>
            <th class="px-6 py-3 text-right text-sm font-medium text-gray-700">Subtotal</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
          <tr
            v-for="item in bill.listSoldProduct"
            :key="item.product.id"
            class="border-b"
          >
            <td class="px-6 py-3">{{ item.product.name }}</td>
            <td class="px-6 py-3">{{ item.quantity }}</td>
            <td class="px-6 py-3 text-right">
              {{ item.product.price.toLocaleString() }} đ
            </td>
            <td class="px-6 py-3 text-right">
              {{ (item.product.price * item.quantity).toLocaleString() }} đ
            </td>
          </tr>
        </tbody>
      </table>

      <!-- Staffs -->
      <div v-if="bill.listStaffAssigned?.length" class="mt-8">
        <h2 class="font-semibold mb-4 text-lg border-b pb-2">Assigned Staffs</h2>
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-sm font-medium text-gray-700">Staff Name</th>
              <th class="px-6 py-3 text-left text-sm font-medium text-gray-700">Specialize</th>
              <th class="px-6 py-3 text-right text-sm font-medium text-gray-700">Hire Price</th>
              <th class="px-6 py-3 text-right text-sm font-medium text-gray-700">Sessions</th>
              <th class="px-6 py-3 text-right text-sm font-medium text-gray-700">Subtotal</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr
              v-for="item in bill.listStaffAssigned"
              :key="item.staff.id"
              class="border-b"
            >
              <td class="px-6 py-3">{{ item.staff.fullName }}</td>
              <td class="px-6 py-3">{{ item.staff.specialize }}</td>
              <td class="px-6 py-3 text-right">
                {{ item.staff.hirePrice.toLocaleString() }} đ
              </td>
              <td class="px-6 py-3 text-right">{{ item.trainingSession }}</td>
              <td class="px-6 py-3 text-right">
                {{
                  (item.staff.hirePrice * item.trainingSession).toLocaleString()
                }} đ
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="mt-6 text-right text-xl font-semibold">
        Total: {{ totalPrice.toLocaleString() }} đ
      </div>
    </div>

    <!-- Coupon Code -->
    <div class="flex gap-3 items-center">
      <input
        v-model="coupon"
        type="text"
        placeholder="Enter coupon code (optional)"
        class="flex-1 px-4 py-3 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
      />
      <button
        class="px-4 py-3 rounded-lg bg-indigo-600 text-white hover:bg-indigo-700 transition"
      >
        Check Coupon
      </button>
    </div>

    <!-- Payment Method -->
    <div>
      <h3 class="mb-3 font-semibold text-gray-900">Select Payment Method</h3>
      <div class="flex gap-6">
        <button
          :class="[
            'flex-1 py-3 rounded-lg border font-medium text-center transition',
            paymentMethod === 'CARD'
              ? 'bg-green-600 text-white border-green-600'
              : 'bg-white text-gray-700 border-gray-300 hover:bg-gray-100',
          ]"
          @click="paymentMethod = 'CARD'"
        >
          Credit/Debit Card
        </button>
        <button
          :class="[
            'flex-1 py-3 rounded-lg border font-medium text-center transition',
            paymentMethod === 'CASH'
              ? 'bg-green-600 text-white border-green-600'
              : 'bg-white text-gray-700 border-gray-300 hover:bg-gray-100',
          ]"
          @click="paymentMethod = 'CASH'"
        >
          Cash
        </button>
        <button
          :class="[
            'flex-1 py-3 rounded-lg border font-medium text-center transition',
            paymentMethod === 'BANKING'
              ? 'bg-green-600 text-white border-green-600'
              : 'bg-white text-gray-700 border-gray-300 hover:bg-gray-100',
          ]"
          @click="paymentMethod = 'BANKING'"
        >
          QR Scan
        </button>
      </div>
    </div>

    <!-- Buttons -->
    <div class="flex justify-end gap-3">
      <RouterLink
        :to="{ name: 'salesselect' }"
        class="px-5 py-3 rounded-lg border border-gray-300 text-gray-700 hover:bg-gray-100"
      >
        Cancel
      </RouterLink>
      <button
        @click="submit"
        class="px-5 py-3 rounded-lg bg-green-600 text-white hover:bg-green-700 disabled:opacity-50"
        :disabled="!bill || bill.listSoldProduct.length === 0"
      >
        Submit
      </button>

    </div>
  </div>
</template>
