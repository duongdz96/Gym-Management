<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { RouterLink } from "vue-router";
import api from "@/services/api";

type Provider = {
  id: number;
  name: string;
  brand: string;
  phone?: string;
};

type Product = {
  id: number;
  name: string;
  type: string;
  price: number;
  brand: string;
  quantity?: number;
};

type ImportedProduct = {
  product: { id: number };
  quantity: number;
};

type ImportBill = {
  date: string;
  price: number;
  provider: Provider;
  manager: { id: number };
  importedProducts: ImportedProduct[];
};

const providers = ref<Provider[]>([]);
const products = ref<Product[]>([]);
const selectedProvider = ref<Provider | null>(null);
const selectedItems = ref<Product[]>([]);

onMounted(async () => {
  try {
    const resProviders = await api.get("/provider");
    providers.value = resProviders.data;
    console.log("Providers fetched:", providers.value);

    const resProducts = await api.get("/product");
    products.value = resProducts.data;
    console.log("Products fetched:", products.value);
  } catch (err) {
    console.error("Error fetching data:", err);
  }
});

const filteredProducts = computed(() => products.value);

function selectProvider(provider: Provider) {
  selectedProvider.value = provider;
  selectedItems.value = [];
}

function toggleSelectProduct(product: Product) {
  if (!selectedProvider.value) {
    alert("Please select a provider first!");
    return;
  }

  if (selectedProvider.value.brand !== product.brand) {
    alert("This product belongs to another provider!");
    return;
  }

  const existing = selectedItems.value.find((i) => i.id === product.id);
  if (existing) {
    selectedItems.value = selectedItems.value.filter((i) => i.id !== product.id);
  } else {
    selectedItems.value.push(product);
  }
}

function continueImport() {
  if (!selectedProvider.value) {
    alert("Please select a provider before continuing!");
    return;
  }

  const importData = {
    provider: selectedProvider.value,
    date: new Date().toISOString(),
    importedProducts: selectedItems.value.map((p) => ({
      product: { 
        id: p.id ,
        name: p.name,
        type: p.type,
        price: p.price,
        brand: p.brand,
      },
      quantity: 0,
    })),
  };

  sessionStorage.setItem("currentImportBill", JSON.stringify(importData));
  console.log("Preview import bill draft:", importData);
}
</script>

<template>
  <div class="p-4 space-y-4">
    <div class="flex justify-between items-center">
      <h1 class="text-2xl font-semibold">Import Resources</h1>
      <RouterLink
        :to="{ name: 'product.importcheckout' }"
        class="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700"
        :class="{ 'opacity-50 pointer-events-none': selectedItems.length === 0 }"
        @click="continueImport"
      >
        Continue
      </RouterLink>
    </div>

    <div class="flex space-x-4">
      <!-- Provider Table -->
      <div class="w-2/5 bg-white border border-gray-200 rounded-lg overflow-hidden">
        <div class="p-4 border-b border-gray-200 flex justify-between items-center">
          <h2 class="text-lg font-semibold">Providers</h2>
          <button class="px-3 py-1 bg-blue-600 text-white rounded hover:bg-blue-700">
            + Add Provider
          </button>
        </div>

        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-4 py-2 text-left">ID</th>
              <th class="px-4 py-2 text-left">Name</th>
              <th class="px-4 py-2 text-left">Brand</th>
              <th class="px-4 py-2 text-left">Phone</th>
              <th class="px-4 py-2 text-center">Action</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="prov in providers"
              :key="prov.id"
              :class="{
                'bg-blue-50': selectedProvider && selectedProvider.id === prov.id,
                'hover:bg-gray-50': true
              }"
            >
              <td class="px-4 py-2">{{ prov.id }}</td>
              <td class="px-4 py-2">{{ prov.name }}</td>
              <td class="px-4 py-2">{{ prov.brand }}</td>
              <td class="px-4 py-2">{{ prov.phone }}</td>
              <td class="px-4 py-2 text-center">
                <button
                  @click="selectProvider(prov)"
                  class="px-3 py-1 rounded bg-green-600 text-white hover:bg-green-700"
                >
                  Select
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Product Table -->
      <div class="w-3/5 bg-white border border-gray-200 rounded-lg overflow-hidden">
        <div class="p-4 border-b border-gray-200 flex justify-between items-center">
          <h2 class="text-lg font-semibold">
            Products
            <span v-if="selectedProvider" class="text-gray-500">
              (from {{ selectedProvider.name }})
            </span>
          </h2>
        </div>

        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-4 py-2 text-left">ID</th>
              <th class="px-4 py-2 text-left">Name</th>
              <th class="px-4 py-2 text-left">Type</th>
              <th class="px-4 py-2 text-left">Price</th>
              <th class="px-4 py-2 text-left">Brand</th>
              <th class="px-4 py-2 text-left">Stock</th>
              <th class="px-4 py-2 text-center">Action</th>
            </tr>
          </thead>
          <tbody v-if="selectedProvider">
            <tr
              v-for="p in filteredProducts.filter(prod => prod.brand === selectedProvider.brand)"
              :key="p.id"
              class="hover:bg-gray-50"
            >
              <td class="px-4 py-2">{{ p.id }}</td>
              <td class="px-4 py-2">{{ p.name }}</td>
              <td class="px-4 py-2">{{ p.type }}</td>
              <td class="px-4 py-2">{{ p.price.toLocaleString() }} đ</td>
              <td class="px-4 py-2">{{ p.brand }}</td>
              <td class="px-4 py-2">{{ p.quantity }}</td>
              <td class="px-4 py-2 text-center">
                <button
                  @click="toggleSelectProduct(p)"
                  :class="[ 
                    'px-3 py-1 rounded',
                    selectedItems.some((x) => x.id === p.id)
                      ? 'bg-red-600 text-white hover:bg-red-700'
                      : 'bg-green-600 text-white hover:bg-green-700'
                  ]"
                >
                  {{ selectedItems.some((x) => x.id === p.id) ? 'Remove' : 'Import' }}
                </button>
              </td>
            </tr>
          </tbody>
          <tbody v-else>
            <tr>
              <td colspan="7" class="text-center py-6 text-gray-500 italic">
                Please select a provider to view their products.
              </td>
            </tr>
          </tbody>
        </table>

        <!-- Selected Summary -->
        <div v-if="selectedItems.length" class="p-4 border-t border-gray-200">
          <h3 class="font-semibold mb-2">Selected Products:</h3>
          <ul class="list-disc ml-6 text-sm text-gray-700">
            <li v-for="s in selectedItems" :key="s.id">
              {{ s.name }} - {{ s.price.toLocaleString() }} đ
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>
