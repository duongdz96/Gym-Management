<script setup lang="ts">
import { ref, computed } from "vue";

type Product = {
  id: number;
  name: string;
  type: string;
  price: number;
  brand?: string;
  quantity?: number;
  image?: string;
};

type SoldProduct = {
  product: Product;
  quantity: number;
};

const products = ref<Product[]>([
  {
    id: 1,
    name: "Áo thun Gym Classic",
    type: "Trang phục",
    price: 250000,
    brand: "MuscleFit",
    quantity: 15,
    image: "https://picsum.photos/seed/shirt/100/100",
  },
  {
    id: 2,
    name: "Bình nước thể thao 1L",
    type: "Phụ kiện",
    price: 120000,
    brand: "HydroMax",
    quantity: 20,
    image: "https://picsum.photos/seed/bottle/100/100",
  },
  {
    id: 3,
    name: "Găng tay tập gym",
    type: "Phụ kiện",
    price: 180000,
    brand: "GripPro",
    quantity: 10,
    image: "https://picsum.photos/seed/gloves/100/100",
  },
  {
    id: 4,
    name: "Túi thể thao chống nước",
    type: "Phụ kiện",
    price: 350000,
    brand: "SportBag",
    quantity: 8,
    image: "https://picsum.photos/seed/bag/100/100",
  },
  {
    id: 5,
    name: "Protein Shake 500ml",
    type: "Đồ uống",
    price: 55000,
    brand: "NutriFit",
    quantity: 25,
    image: "https://picsum.photos/seed/shake/100/100",
  },
  {
    id: 6,
    name: "Khăn tập cotton",
    type: "Phụ kiện",
    price: 90000,
    brand: "GymSoft",
    quantity: 30,
    image: "https://picsum.photos/seed/towel/100/100",
  },
]);

const search = ref("");
const cart = ref<SoldProduct[]>([]);

const searchProduct = computed(() => {
  return products.value.filter((p) =>
    p.name.toLowerCase().includes(search.value.toLowerCase())
  );
});

function addToCart(product: Product) {
  const existing = cart.value.find((i) => i.product.id === product.id);
  if (existing) existing.quantity++;
  else cart.value.push({ product, quantity: 1 });
}

function removeFromCart(id: number) {
  cart.value = cart.value.filter((i) => i.product.id !== id);
}

function total() {
  return cart.value.reduce(
    (sum, i) => sum + i.product.price * i.quantity,
    0
  );
}

function checkout() {
  console.log("Checkout bill:", {
    listSoldProduct: cart.value,
    total: total(),
    date: new Date().toISOString(),
    receptionist: { id: 3 },
  });
  alert("Thanh toán thành công!");
  cart.value = [];
}
</script>

<template>
  <div class="p-6 grid grid-cols-3 gap-6">
    <!-- Bảng sản phẩm -->
    <div class="col-span-2 bg-white rounded-xl border border-gray-200 overflow-hidden shadow-sm">
      <div class="p-4 border-b flex items-center justify-between">
        <h2 class="text-lg font-semibold">Danh sách sản phẩm</h2>
        <input
          v-model="search"
          placeholder="Tìm sản phẩm..."
          class="px-3 py-1 border rounded-lg text-sm focus:ring focus:ring-blue-200"
        />
      </div>

      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th class="px-3 py-2 text-left">Ảnh</th>
            <th class="px-3 py-2 text-left">Tên</th>
            <th class="px-3 py-2 text-left">Loại</th>
            <th class="px-3 py-2 text-right">Giá</th>
            <th class="px-3 py-2 text-center">Tồn kho</th>
            <th class="px-3 py-2 text-center">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="p in searchProduct"
            :key="p.id"
            class="hover:bg-gray-50"
          >
            <td class="px-3 py-2">
              <img
                :src="p.image"
                class="w-12 h-12 object-cover rounded"
              />
            </td>
            <td class="px-3 py-2">{{ p.name }}</td>
            <td class="px-3 py-2">{{ p.type }}</td>
            <td class="px-3 py-2 text-right">{{ p.price.toLocaleString() }} đ</td>
            <td class="px-3 py-2 text-center">{{ p.quantity }}</td>
            <td class="px-3 py-2 text-center">
              <button
                @click="addToCart(p)"
                class="px-3 py-1 bg-green-600 text-white text-sm rounded hover:bg-green-700"
              >
                Thêm
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Giỏ hàng -->
    <div class="bg-white rounded-xl border border-gray-200 overflow-hidden shadow-sm flex flex-col">
      <h2 class="p-4 border-b text-lg font-semibold">Giỏ hàng</h2>
      <div class="flex-1 overflow-y-auto p-4">
        <table class="min-w-full divide-y divide-gray-200 text-sm">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-2 py-2 text-left">Sản phẩm</th>
              <th class="px-2 py-2 text-right">Giá</th>
              <th class="px-2 py-2 text-center">SL</th>
              <th class="px-2 py-2 text-center">Xoá</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="item in cart"
              :key="item.product.id"
              class="hover:bg-gray-50"
            >
              <td class="px-2 py-2 flex items-center gap-2">
                <img
                  :src="item.product.image"
                  class="w-10 h-10 rounded object-cover"
                />
                <span>{{ item.product.name }}</span>
              </td>
              <td class="px-2 py-2 text-right">
                {{ item.product.price.toLocaleString() }} đ
              </td>
              <td class="px-2 py-2 text-center">
                <input
                  type="number"
                  min="1"
                  v-model.number="item.quantity"
                  class="w-14 border rounded px-1 py-0.5 text-center"
                />
              </td>
              <td class="px-2 py-2 text-center">
                <button
                  @click="removeFromCart(item.product.id)"
                  class="text-red-600 hover:text-red-800"
                >
                  ✕
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="border-t p-4 space-y-2">
        <div class="text-right font-semibold text-gray-700">
          Tổng: {{ total().toLocaleString() }} đ
        </div>
        <button
          @click="checkout"
          class="w-full py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
          :disabled="cart.length === 0"
        >
          Thanh toán
        </button>
      </div>
    </div>
  </div>
</template>
