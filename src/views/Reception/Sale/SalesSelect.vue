<script setup lang="ts">
import { computed, ref, onMounted  } from "vue";
import { RouterLink } from "vue-router";
import api from '../../../services/api';
type Product = {
  id: number;
  name: string;
  type: string;
  price: number;
  brand?: string;
  quantity?: number;
};

type SoldProduct = {
  product: Product;
  quantity: number;
};
const bill = ref<any>(null);

const products = ref([]);
onMounted(async () => {
  const data = sessionStorage.getItem("currentBill");
  if (data) {
    bill.value = JSON.parse(data);
    cart.value = bill.value.listSoldProduct || [];
  }

  try {
    const res = await api.get("/product");
    products.value = res.data;
  } catch (err) {
    console.error("Error fetching products:", err);
  }
});

const cart = ref<SoldProduct[]>([]);

function addToCart(product: Product) {
  const existing = cart.value.find((item) => item.product.id === product.id);
  if (existing) {
    existing.quantity += 1;
  } else {
    cart.value.push({ product, quantity: 1 });
  }
}


function booking() {
  const oldBill = bill.value || {};
  const newBill  = {
    ...oldBill,
    paymentMethod: null,
    paymentStatus: "PENDING",
    date: new Date().toISOString(),
    receptionist: { id: 3 },
    listSoldProduct: cart.value.map((item) => ({
      product: { 
        id: item.product.id,
        name: item.product.name,
        type: item.product.type,
        price: item.product.price,
        brand: item.product.brand,
        quantity: item.product.quantity
      },
      quantity: item.quantity,
    })),
  };

  bill.value = newBill;
  console.log("Bill preview:", newBill);
  sessionStorage.setItem("currentBill", JSON.stringify(newBill));
}

const total = () =>
  cart.value.reduce((sum, item) => sum + item.product.price * item.quantity, 0);

const search = ref('')
const searchProduct = computed(() => {
    return products.value.filter(p => {
        const matchesSearch = (p.name || '').toLowerCase().includes(search.value.toLowerCase())
        return matchesSearch
    })
})

function removeFromCart(productId: number) {
  cart.value = cart.value.filter(item => item.product.id !== productId);
}

</script>

<template>
  <div class="p-4 space-y-4">
    <div class="flex space-x-4">
      <div class="w-5/7 bg-white border border-gray-200 rounded-lg overflow-hidden">
        <!-- Search bar -->
        <div class="p-4">
          <input
            type="text"
            v-model="search"
            placeholder="Search for product's name..."
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
              <th class="px-4 py-3">Type</th>
              <th class="px-4 py-3">Price</th>
              <th class="px-4 py-3">Brand</th>
              <th class="px-4 py-3">Stock</th>
              <th class="px-4 py-3 text-center">Actions</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
            <tr v-for="p in searchProduct" :key="p.id" class="hover:bg-gray-50">
              <td class="px-4 py-2">{{ p.id }}</td>
              <td class="px-4 py-2">{{ p.name }}</td>
              <td class="px-4 py-2">{{ p.type }}</td>
              <td class="px-4 py-2">{{ p.price.toLocaleString() }} đ</td>
              <td class="px-4 py-2">{{ p.brand }}</td>
              <td class="px-4 py-2">{{ p.quantity }}</td>
              <td class="px-4 py-2 text-center">
                <button
                  @click="addToCart(p)"
                  class="px-2 py-1 rounded bg-green-600 text-white hover:bg-green-700"
                >
                  Select
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="w-2/7 bg-white border border-gray-200 rounded-lg overflow-hidden flex flex-col">
        <h2 class="text-lg font-semibold p-4 border-b border-gray-200">Cart</h2>

        <div class="flex-1 overflow-y-auto p-4">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-4 py-3">Name</th>
                <th class="px-4 py-3">Price</th>
                <th class="px-4 py-3">Quantity</th>
                <th class="px-4 py-3 text-center">Action</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200">
              <tr v-for="item in cart" :key="item.product.id">
                <td class="px-4 py-2">{{ item.product.name }}</td>
                <td class="px-4 py-2">{{ item.product.price.toLocaleString() }} đ</td>
                <td class="px-4 py-2">
                  <input
                    type="number"
                    min="1"
                    v-model.number="item.quantity"
                    class="w-16 border border-gray-300 rounded px-2 py-1"
                  />
                </td>
                <td class="px-4 py-2 text-center">
                  <button
                    @click="removeFromCart(item.product.id)"
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
            Total: {{ total().toLocaleString() }} đ
          </div>
          <RouterLink
            :to="{ name: 'salesBooking' }"
            @click="booking"
            class="w-full px-3 py-2 rounded bg-green-600 text-white hover:bg-green-700"
            :disabled="cart.length === 0"
          >
            Select Staff
          </RouterLink>
        </div>
      </div>
    </div>
  </div>

</template>
