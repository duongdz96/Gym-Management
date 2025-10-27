<script setup lang="ts">
import { onMounted, ref, computed } from "vue";
import { useRouter } from "vue-router";
import api from "@/services/api";

type Provider = {
  id: number;
  name: string;
  brand: string;
  phone?: string;
};

type Product = {
  id: number;
  name?: string;
  price?: number; // giá bán
};

type ImportedProduct = {
  product: Product;
  quantity: number;
  importPrice: number; // giá nhập (người dùng nhập)
};

type ImportBill = {
  date: string;
  provider: Provider;
  importedProducts: ImportedProduct[];
  price?: number; // tổng giá nhập
};

const router = useRouter();
const importBill = ref<ImportBill | null>(null);

onMounted(() => {
  const data = sessionStorage.getItem("currentImportBill");
  if (data) {
    const parsed = JSON.parse(data);
    parsed.importedProducts = parsed.importedProducts.map((item: any) => ({
      ...item,
      importPrice: item.importPrice || (item.product.price ? item.product.price * 0.75 : 0), // Giá fix
    }));
    importBill.value = parsed;
    console.log("Import bill session:", importBill.value);
  } else {
    alert("No import data found. Redirecting...");
    router.push({ name: "product.import" });
  }
});

function updateQuantity(productId: number, event: Event) {
  const target = event.target as HTMLInputElement;
  const value = Number(target.value);
  const item = importBill.value?.importedProducts.find(i => i.product.id === productId);
  if (item) item.quantity = value;
}

function updateImportPrice(productId: number, event: Event) {
  const target = event.target as HTMLInputElement;
  const value = Number(target.value);
  const item = importBill.value?.importedProducts.find(i => i.product.id === productId);
  if (item) item.importPrice = value;
}


const totalImportPrice = computed(() => {
  if (!importBill.value) return 0;
  return importBill.value.importedProducts.reduce(
    (sum, item) => sum + (item.importPrice || 0) * (item.quantity || 0),
    0
  );
});


const totalSellPrice = computed(() => {
  if (!importBill.value) return 0;
  return importBill.value.importedProducts.reduce(
    (sum, item) => sum + (item.product.price || 0) * (item.quantity || 0),
    0
  );
});

// Lợi nhuận dự kiến
const expectedProfit = computed(() => totalSellPrice.value - totalImportPrice.value);

async function submitImport() {
  if (!importBill.value) return;

  const valid = importBill.value.importedProducts.every(
    (i) => i.quantity > 0 && i.importPrice > 0
  );

  if (!valid) {
    alert("Please fill import price and quantity for all products before submitting!");
    return;
  }

  const payload = {
    ...importBill.value,
    price: totalImportPrice.value, // tổng giá nhập
    manager: { id: 6 },
  };

  console.log("Submitting import bill:", payload);

  try {
    const res = await api.post("/import-bills", payload);
    if (res.status === 200 || res.status === 201) {
      alert("Import bill created successfully!");
      sessionStorage.removeItem("currentImportBill");
      router.push({ name: "product.importproduct" });
    }
  } catch (err) {
    console.error("Error submitting import bill:", err);
    alert("Failed to create import bill.");
  }
}
</script>

<template>
  <div class="p-6 space-y-6">
    <h1 class="text-2xl font-semibold">Import Bill Review</h1>

    <div v-if="importBill" class="bg-white border rounded-lg shadow-sm p-4 space-y-4">
      <div class="flex justify-between items-center border-b pb-2">
        <div>
          <p class="font-semibold text-gray-800">
            Provider: <span class="text-blue-600">{{ importBill.provider.name }}</span>
          </p>
          <p class="text-sm text-gray-500">Brand: {{ importBill.provider.brand }}</p>
          <p class="text-sm text-gray-500">Date: {{ new Date(importBill.date).toLocaleString() }}</p>
        </div>
        <button
          @click="$router.push({ name: 'product.import' })"
          class="px-3 py-1 bg-gray-100 border rounded hover:bg-gray-200"
        >
          ← Back
        </button>
      </div>

      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th class="px-4 py-2 text-left text-sm font-semibold text-gray-600">Product</th>
            <th class="px-4 py-2 text-left text-sm font-semibold text-gray-600">Import Price</th>
            <th class="px-4 py-2 text-left text-sm font-semibold text-gray-600">Sell Price</th>
            <th class="px-4 py-2 text-left text-sm font-semibold text-gray-600">Quantity</th>
            <th class="px-4 py-2 text-right text-sm font-semibold text-gray-600">Subtotal (Import)</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="item in importBill.importedProducts"
            :key="item.product.id"
            class="hover:bg-gray-50"
          >
            <td class="px-4 py-2 text-sm">{{ item.product.name }}</td>

            <!-- Giá nhập -->
            <td class="px-4 py-2 text-sm">
              <input
                type="number"
                min="0"
                step="100"
                class="w-24 border rounded px-2 py-1"
                :value="item.importPrice"
                @input="updateImportPrice(item.product.id, $event)"
              />
            </td>

            <!-- Giá bán -->
            <td class="px-4 py-2 text-sm">
              {{ item.product.price?.toLocaleString() }} đ
            </td>

            <!-- Số lượng -->
            <td class="px-4 py-2 text-sm">
              <input
                type="number"
                min="1"
                class="w-20 border rounded px-2 py-1"
                :value="item.quantity"
                @input="updateQuantity(item.product.id, $event)"
              />
            </td>

            <!-- Subtotal theo giá nhập -->
            <td class="px-4 py-2 text-sm text-right">
              {{ ((item.importPrice || 0) * (item.quantity || 0)).toLocaleString() }} đ
            </td>
          </tr>
        </tbody>
      </table>

      <!-- Tổng cộng -->
      <div class="flex justify-end border-t pt-4">
        <div class="text-right space-y-1">
          <p class="text-gray-700 font-semibold">
            Total import price:
            <span class="text-blue-600">{{ totalImportPrice.toLocaleString() }} đ</span>
          </p>
          <p class="text-gray-700 font-semibold">
            Total selling price:
            <span class="text-green-600">{{ totalSellPrice.toLocaleString() }} đ</span>
          </p>
          <p class="text-gray-700 font-semibold">
            Expected profit:
            <span class="text-red-600">{{ expectedProfit.toLocaleString() }} đ</span>
          </p>
        </div>
      </div>

      <div class="flex justify-end gap-2 pt-4 border-t">
        <button
          @click="$router.push({ name: 'product.import' })"
          class="px-4 py-2 bg-gray-200 text-gray-800 rounded hover:bg-gray-300"
        >
          Cancel
        </button>
        <button
          @click="submitImport"
          class="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700"
        >
          Confirm Import
        </button>
      </div>
    </div>

    <div v-else class="text-gray-500 italic">No import bill found.</div>
  </div>
</template>
