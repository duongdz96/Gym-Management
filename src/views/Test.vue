<template>
  <div class="p-6 max-w-xl mx-auto">
    <h1 class="text-2xl font-bold mb-4">Test VietQR Payment</h1>

    <div class="mb-4">
      <label class="font-medium">Số tiền:</label>
      <input
        v-model="amount"
        type="number"
        class="border p-2 rounded ml-2 w-40"
      />
    </div>

    <div class="mb-4">
      <label class="font-medium">Nội dung chuyển khoản:</label>
      <input
        v-model="addInfo"
        type="text"
        class="border p-2 rounded ml-2 w-60"
      />
    </div>

    <button
      @click="generateQR"
      class="bg-blue-600 text-white px-4 py-2 rounded"
    >
      Tạo QR
    </button>

    <div v-if="qrUrl" class="mt-6 text-center">
      <p class="font-semibold mb-2">QR chuyển khoản ngân hàng:</p>
      <img :src="qrUrl" alt="vietqr" class="mx-auto border rounded shadow" />

      <div class="mt-4">
        <p class="font-medium">
          Trạng thái thanh toán: 
          <span :class="statusClass">{{ status }}</span>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onUnmounted } from "vue";
import api from "@/services/api";
import { useToast } from "vue-toastification";

// Account info
const bankCode = "MB";
const accountNumber = "2666677888";
const accountName = "VAN NGOC LONG";

// Reactive variables
const amount = ref(150000);
const addInfo = ref("TestPayment812");
const qrUrl = ref("");
const status = ref("Pending");
const toast = useToast();

// Class for UI status
const statusClass = computed(() =>
  status.value === "Paid" ? "text-green-600 font-bold" : "text-red-600 font-bold"
);

let pollInterval: ReturnType<typeof setInterval> | null = null;

// Stop polling when component unmounts
onUnmounted(() => {
  if (pollInterval) clearInterval(pollInterval);
});

// Generate QR and start polling
const generateQR = () => {
  qrUrl.value = `https://img.vietqr.io/image/${bankCode}-${accountNumber}-compact.png?amount=${amount.value}&addInfo=${encodeURIComponent(addInfo.value)}&accountName=${encodeURIComponent(accountName)}`;
  status.value = "Pending";

  toast.info("Mã QR đã tạo. Đang đợi thanh toán...");

  startPolling();
};

// Poll BE every 3 seconds
const startPolling = () => {
  if (pollInterval) clearInterval(pollInterval);

  pollInterval = setInterval(async () => {
    try {
      const res = await api.get(
        `/casso/check?addInfo=${encodeURIComponent(addInfo.value)}&amount=${amount.value}`
      );

      console.log("Polling response:", res.data);

      if (res.data?.paid) {
        status.value = "Paid";

        toast.success("Payment received!");
        console.log("Transaction details:", res.data);

        if (pollInterval) clearInterval(pollInterval);
      }

    } catch (err) {
      console.error("Error during polling:", err);
    }
  }, 3000);
};
</script>

<style>
body {
  font-family: sans-serif;
}
</style>
