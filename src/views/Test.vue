<!-- <template>
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

  toast.info("QR created. Waiting for payment...");

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
</style> -->


<template>
  <div class="p-6 relative">

    <!-- ================= TABS ================= -->
    <div class="flex space-x-4 border-b pb-2 mb-4">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        @click="activeTab = tab.key"
        class="py-2 px-4 transition-all duration-200"
        :class="activeTab === tab.key ? 'border-b-2 border-blue-600 font-bold' : 'opacity-60'"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- ================= SEARCH + SORT ================= -->
    <div class="flex items-center space-x-4 mb-4">
      <input
        v-model="search"
        type="text"
        placeholder="Tìm kiếm nhanh..."
        class="border px-3 py-2 rounded w-64"
      />

      <select v-model="sortOrder" class="border px-3 py-2 rounded">
        <option value="">Sắp xếp theo giá</option>
        <option value="asc">Giá ↑</option>
        <option value="desc">Giá ↓</option>
      </select>
    </div>

    <!-- ================= LIST WITH TRANSITION ================= -->
    <transition name="fade-slide" mode="out-in">
      <div :key="activeTab">
        <div v-for="item in filteredItems" :key="item.id" class="border p-3 rounded mb-2 flex justify-between items-center">
          <div>
            <p class="font-semibold">{{ item.name }}</p>
            <p class="text-sm text-gray-600">{{ item.price.toLocaleString() }}đ</p>
          </div>

          <button
            class="bg-blue-500 text-white px-3 py-1 rounded"
            @click="addToCart(item)"
          >Add</button>
        </div>
      </div>
    </transition>

    <!-- ================= CART BUTTON ================= -->
    <button
      @click="cartOpen = !cartOpen"
      class="fixed right-6 top-6 bg-blue-600 text-white px-4 py-2 rounded shadow z-50"
    >
      Giỏ Hàng
    </button>

    <!-- ================= CART SIDEBAR WITH OVERLAY ================= -->
    <transition name="slide">
      <div v-if="cartOpen" class="fixed inset-0 z-40 flex">
        <!-- Overlay -->
        <div
          class="absolute inset-0 bg-black/30"
          @click="cartOpen = false"
        ></div>

        <!-- Sidebar -->
        <div
          class="ml-auto w-80 h-full bg-white shadow-2xl p-4 flex flex-col z-50 relative"
          @click.stop
        >
          <h2 class="text-xl font-bold mb-4">Giỏ hàng</h2>

          <div class="flex-1 overflow-y-auto">
            <div v-for="type in cartTypes" :key="type" class="mb-4">
              <h3 class="font-bold mb-2 capitalize">{{ type }}</h3>
              <div
                v-for="item in groupedCart[type]"
                :key="item.cartId"
                class="border-b py-3 flex justify-between items-center"
              >
                <div>
                  <p class="font-semibold">{{ item.name }}</p>
                  <p class="text-sm text-gray-500">{{ item.price.toLocaleString() }}đ</p>
                </div>

                <div class="flex items-center space-x-2">
                  <button class="px-2 border rounded" @click="decrease(item)">-</button>
                  <span>{{ item.qty }}</span>
                  <button class="px-2 border rounded" @click="increase(item)">+</button>

                  <button class="text-red-500 ml-2" @click="removeFromCart(item)">x</button>
                </div>
              </div>
            </div>
          </div>

          <div class="mt-4 border-t pt-4">
            <p class="font-bold mb-2">Tổng: {{ totalPrice.toLocaleString() }}đ</p>

            <button
              class="w-full bg-green-600 text-white py-2 rounded"
              @click="goToCheckout"
            >
              Thanh toán
            </button>
          </div>
        </div>
      </div>
    </transition>
    

  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from "vue";

// ================= TAB CONFIG =================
const tabs = [
  { key: "product", label: "Product" },
  { key: "membership", label: "Membership" },
  { key: "pt", label: "PT Sessions" },
];
const activeTab = ref("product");

// ================= SEARCH + SORT =================
const search = ref("");
const sortOrder = ref("");

// ================= MOCK DATA =================
// Mock theo API mẫu
const allItems = ref([
  { id: 1, name: "T-Shirt Gym", type: "product", price: 250000, brand: "Nike", quantity: 83, status: false, importPrice: null },
  { id: 2, name: "Găng tay", type: "product", price: 150000, brand: "Adidas", quantity: 50, status: true, importPrice: null },
  { id: 10, name: "Gói 1 tháng", type: "membership", price: 600000, brand: null, quantity: 100, status: true, importPrice: null },
  { id: 11, name: "Gói 3 tháng", type: "membership", price: 1500000, brand: null, quantity: 100, status: true, importPrice: null },
  { id: 20, name: "1 buổi PT", type: "pt", price: 200000, brand: null, quantity: 50, status: true, importPrice: null },
  { id: 21, name: "10 buổi PT", type: "pt", price: 1800000, brand: null, quantity: 50, status: true, importPrice: null },
]);

// ================= FILTERED ITEMS =================
const filteredItems = computed(() => {
  let items = allItems.value.filter(i => {
    let matchType = i.type === activeTab.value;
    let matchSearch = i.name.toLowerCase().includes(search.value.toLowerCase());
    return matchType && matchSearch;
  });

  if (sortOrder.value === "asc") items.sort((a,b) => a.price - b.price);
  else if (sortOrder.value === "desc") items.sort((a,b) => b.price - a.price);

  return items;
});

// ================= CART =================
const cart = ref([]);

const cartTypes = ["product", "membership", "pt"];

const addToCart = (item) => {
  const exist = cart.value.find(c => c.id === item.id && c.type === item.type);
  if (exist) exist.qty++;
  else cart.value.push({ ...item, cartId: Date.now() + Math.random(), qty: 1 });
};

const groupedCart = computed(() => {
  let group = { product: [], membership: [], pt: [] };
  cart.value.forEach(i => group[i.type].push(i));
  return group;
});

const increase = (item) => item.qty++;
const decrease = (item) => { if (item.qty > 1) item.qty--; else removeFromCart(item); };
const removeFromCart = (item) => cart.value = cart.value.filter(c => c.cartId !== item.cartId);

const totalQuantity = computed(() => cart.value.reduce((s,i)=>s+i.qty,0));
const totalPrice = computed(() => cart.value.reduce((s,i)=>s+i.qty*i.price,0));

const goToCheckout = () => alert("Đi đến trang thanh toán...");

const cartOpen = ref(false);
const cartSidebarRef = ref(null); // ref cho sidebar

// Hàm kiểm tra click ngoài
const handleClickOutside = (event) => {
  if (cartSidebarRef.value && !cartSidebarRef.value.contains(event.target)) {
    cartOpen.value = false;
  }
};

onMounted(() => {
  document.addEventListener("click", handleClickOutside);
});

onBeforeUnmount(() => {
  document.removeEventListener("click", handleClickOutside);
});
</script>

<style>
/* Fade + translate nhẹ cho tab content */
.fade-slide-enter-active,
.fade-slide-leave-active { transition: all 0.2s ease; }
.fade-slide-enter-from { opacity:0; transform: translateY(2px); }
.fade-slide-leave-to { opacity:0; transform: translateY(2px); }

/* Slide cart sidebar */
.slide-enter-from { transform: translateX(100%); }
.slide-enter-active, .slide-leave-active { transition: all 0.3s ease; }
.slide-leave-to { transform: translateX(100%); }
</style>
