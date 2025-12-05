<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import api from "@/services/api";
import { useRouter } from "vue-router";
import { useToast } from "vue-toastification";

const router = useRouter();
const toast = useToast();

type Product = {
  id: number;
  name: string;
  type: string;
  price: number;
  brand?: string;
  quantity?: number;
  image?: string | null;
};

type SoldProduct = {
  product: Product;
  quantity: number;
};

const bill = ref<any>(null);
const products = ref<Product[]>([]);
const cart = ref<SoldProduct[]>([]);
const search = ref("");
const isLoading = ref(false);

const API_BASE = "http://localhost:8080";
const defaultImage = `${API_BASE}/image/defaults/no-image.png`;


onMounted(async () => {
  sessionStorage.clear();

  try {
    isLoading.value = true;
    const res = await api.get("/products/exclude-types?types=PT,Membership");
    products.value = res.data?.map((p: Product) => ({
      ...p,
      image: p.image ? `${API_BASE}/${p.image}` : defaultImage,
    }));
  } catch (err) {
    console.error("Error fetching products:", err);
  } finally {
    isLoading.value = false;
  }
});

const searchProduct = computed(() =>
  products.value.filter((p) =>
    (p.name || "").toLowerCase().includes(search.value.toLowerCase())
  )
);

function addToCart(product: Product) {
  const existing = cart.value.find((item) => item.product.id === product.id);
  if (existing) {
    existing.quantity += 1;
  } else {
    cart.value.push({ product, quantity: 1 });
  }
}

function removeFromCart(productId: number) {
  cart.value = cart.value.filter((item) => item.product.id !== productId);
}

const total = () =>
  cart.value.reduce((sum, item) => sum + item.product.price * item.quantity, 0);

function booking() {
  const oldBill = bill.value || {};
  const newBill = {
    ...oldBill,
    paymentMethod: null,
    paymentStatus: "PENDING",
    date: new Date().toISOString(),
    receptionist: { id: 3, name: "Nguyễn Văn Lễ Tân" },
    listSoldProduct: cart.value.map((item) => ({
      product: {
        id: item.product.id,
        name: item.product.name,
        type: item.product.type,
        price: item.product.price,
        brand: item.product.brand,
        quantity: item.product.quantity,
      },
      quantity: item.quantity,
    })),
    total: total(),
  };

  bill.value = newBill;
  sessionStorage.setItem("currentBill", JSON.stringify(newBill));
  return newBill;
}

function checkout() {
  if (cart.value.length === 0) {
    toast.warning("Vui lòng chọn ít nhất một sản phẩm trước khi thanh toán!");
    return;
  }

  const newBill = booking();

  setTimeout(() => {
    router.push({ name: "salesCheckout" });
  }, 800);
}
</script>

<template>
  <div class="p-6 grid grid-cols-3 gap-6">
    <!-- DANH SÁCH SẢN PHẨM -->
    <div
      class="col-span-2 bg-white rounded-xl border border-gray-200 overflow-hidden shadow-sm"
    >
      <div class="p-4 border-b flex items-center justify-between">
        <h2 class="text-lg font-semibold">Danh sách sản phẩm</h2>
        <input
          v-model="search"
          placeholder="Tìm sản phẩm..."
          class="px-3 py-1 border rounded-lg text-sm focus:ring focus:ring-blue-200"
        />
      </div>

      <div v-if="isLoading" class="p-4 text-center text-gray-500">
        Đang tải sản phẩm...
      </div>

      <table v-else class="min-w-full divide-y divide-gray-200 text-sm">
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
          <tr v-for="p in searchProduct" :key="p.id" class="hover:bg-gray-50">
            <td class="px-3 py-2">
              <img
                :src="p.image || defaultImage"
                class="w-12 h-12 object-cover rounded"
                alt="product"
                @error="p.image = defaultImage"
              />
            </td>
            <td class="px-3 py-2">{{ p.name }}</td>
            <td class="px-3 py-2">{{ p.type }}</td>
            <td class="px-3 py-2 text-right">
              {{ p.price.toLocaleString() }} đ
            </td>
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
          <tr v-if="searchProduct.length === 0">
            <td colspan="6" class="py-4 text-center text-gray-500">
              Không tìm thấy sản phẩm
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- GIỎ HÀNG -->
    <div
      class="bg-white rounded-xl border border-gray-200 overflow-hidden shadow-sm flex flex-col"
    >
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
                  :src="item.product.image || defaultImage"
                  class="w-10 h-10 rounded object-cover"
                  alt="cart item"
                  @error="item.product.image = defaultImage"
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
            <tr v-if="cart.length === 0">
              <td colspan="4" class="py-4 text-center text-gray-500">
                Giỏ hàng trống
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
        >
          Thanh toán
        </button>
      </div>
    </div>
  </div>
</template>
