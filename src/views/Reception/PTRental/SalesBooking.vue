<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import api from "../../../services/api";

// 1. Định nghĩa Product cho Gói PT
type ProductPT = {
  id: number;
  name: string;
  type: string; // Sẽ là "PT"
  price: number;
  // Giữ lại các trường cơ bản từ class Product của bạn
  importPrice: number | null;
  brand: string | null;
  quantity: number | null;
  status: boolean;
};

// 2. Định nghĩa SoldProduct (Chi tiết sản phẩm đã bán)
type SoldProduct = {
  product: ProductPT;
  quantity: number; // Số lượng gói PT
};

const ptProducts = ref<ProductPT[]>([]);
const selectedPTPackages = ref<SoldProduct[]>([]);
const bill = ref<any>({}); // Khởi tạo Bill rỗng, vì ta không dựa vào Bill trước đó nữa
const router = useRouter();
const search = ref("");


onMounted(async () => {
  // Bỏ logic load bill cũ, vì trang này là trang độc lập
  
  try {
    const res = await api.get("/product");
    
    // Lọc chỉ lấy các sản phẩm có type là "PT"
    ptProducts.value = res.data.filter((p: ProductPT) => p.type === "PT"); 
  } catch (err) {
    console.error("Error fetching PT products:", err);
  }
});


// Hàm xử lý chọn/tăng số lượng gói PT
function toggleSelectProduct(product: ProductPT) {
  const existing = selectedPTPackages.value.find((item) => item.product.id === product.id);
  
  // Mặc định tăng số lượng lên 1
  if (existing) {
    existing.quantity += 1;
  } else {
    // Mua gói PT này với số lượng ban đầu là 1
    selectedPTPackages.value.push({ product, quantity: 1 });
  }
}

function proceedToCheckout() {
  const finalListSoldProduct = selectedPTPackages.value;

  if (finalListSoldProduct.length === 0) {
    alert("Please select at least one PT package before continuing!");
    return;
  }
  
  // 3. Tạo cấu trúc Bill mới và chỉ đưa Gói PT đã chọn vào listSoldProduct
  const newBill = {
      // Các trường Bill cơ bản có thể khởi tạo rỗng/null,
      // hoặc được điền ở trang checkout/backend
      id: null,
      paymentMethod: null,
      paymentStatus: null,
      date: new Date(),
      receptionist: { id: 3, name: "Nguyễn Văn Lễ Tân" },
      // total sẽ được tính lại ở bước checkout
      // receptionist, member, issuedCoupon có thể null hoặc được thêm sau
      
      // Chỉ chứa listSoldProduct là các gói PT
      listSoldProduct: finalListSoldProduct.map((item) => ({
        product: item.product, 
        quantity: item.quantity,
      })),
      
      // Đảm bảo không còn listStaffAssigned
      listStaffAssigned: [], // Hoặc bỏ hẳn trường này nếu backend chấp nhận
  }
  
  // Lưu Bill mới vào Session Storage để chuyển sang trang Checkout
  sessionStorage.setItem("currentBill", JSON.stringify(newBill));

  router.push({ name: "salesCheckout" });
}

// 4. Tính tổng tiền chỉ dựa trên các gói PT đã chọn
const totalPrice = computed(() => {
    return selectedPTPackages.value.reduce(
        (sum, item) => sum + (item.product?.price || 0) * (item.quantity || 0),
        0
    ) || 0;
});


// Lọc Product PT
const filteredPTProducts = computed(() => {
  return ptProducts.value.filter((p) =>
    (p.name + " " + p.type)
      .toLowerCase()
      .includes(search.value.toLowerCase())
  );
});

// Xóa gói PT khỏi giỏ hàng
function removeFromCart(productId: number) {
  selectedPTPackages.value = selectedPTPackages.value.filter(item => item.product.id !== productId);
}

// Hàm để tăng/giảm số lượng gói PT (sessions) trong giỏ hàng
function updateQuantity(item: SoldProduct, change: number) {
    const newQuantity = item.quantity + change;
    if (newQuantity >= 1) {
        item.quantity = newQuantity;
    }
}
</script>

<template>
  <div class="p-6 bg-gray-100 min-h-screen">
    <div class="max-w-7xl mx-auto flex flex-col lg:flex-row gap-6">
      <div class="lg:w-3/5 bg-white rounded-xl shadow-lg overflow-hidden">
        <div class="p-6 border-b border-gray-200">
          <h2 class="text-2xl font-bold text-gray-800 mb-4">Available PT Packages</h2>
          <div class="relative">
            <input
              type="text"
              v-model="search"
              placeholder="Search PT package by name..."
              class="w-full pl-12 pr-4 py-3 border border-gray-300 rounded-lg bg-gray-50 text-gray-800 
                     focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500
                     transition duration-200 ease-in-out"
            />
            <svg class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" width="20" height="20" fill="currentColor" viewBox="0 0 24 24">
              <path d="M15.5 14h-.79l-.28-.27A6.471 6.471 0 0016 9.5a6.5 6.5 0 10-6.5 6.5c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
            </svg>
          </div>
        </div>

        <div class="p-6">
          <div v-if="filteredPTProducts.length > 0" class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div 
              v-for="p in filteredPTProducts" 
              :key="p.id" 
              class="bg-white border border-gray-200 rounded-lg p-4 shadow-sm hover:shadow-md 
                     transition duration-200 ease-in-out flex flex-col justify-between"
            >
              <div>
                <h3 class="text-lg font-semibold text-gray-900">{{ p.name }}</h3>
                <p class="text-sm text-gray-600 mb-2">Type: <span class="font-medium">{{ p.type }}</span></p>
                <p class="text-xl font-bold text-blue-600">{{ p.price.toLocaleString() }} đ</p>
              </div>
              <button
                @click="toggleSelectProduct(p)"
                class="mt-4 w-full px-4 py-2 rounded-md bg-green-600 text-white font-semibold 
                       hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-green-500 
                       focus:ring-offset-2 transition duration-200 ease-in-out"
              >
                Add to Cart
              </button>
            </div>
          </div>
          <div v-else class="text-center text-gray-500 py-10">
            No PT packages found.
          </div>
        </div>
      </div>

      <div class="lg:w-2/5 bg-white rounded-xl shadow-lg flex flex-col">
        <h2 class="text-2xl font-bold text-gray-800 p-6 border-b border-gray-200">Selected PT Packages</h2>

        <div class="flex-1 overflow-y-auto p-6">
          <div v-if="selectedPTPackages.length > 0" class="space-y-4">
            <div 
              v-for="item in selectedPTPackages" 
              :key="item.product.id"
              class="flex items-center justify-between border border-gray-200 rounded-lg p-4 shadow-sm"
            >
              <div class="flex-1">
                <p class="font-semibold text-gray-900">{{ item.product.name }}</p>
                <p class="text-gray-600">{{ item.product.price.toLocaleString() }} đ/package</p>
              </div>
              <div class="flex items-center space-x-2">
                <div class="flex border border-gray-300 rounded-md overflow-hidden">
                    <button
                        @click="updateQuantity(item, -1)"
                        class="px-3 py-1 bg-gray-100 text-gray-700 hover:bg-gray-200 transition duration-150"
                    >-</button>
                    <input
                        type="number"
                        min="1"
                        v-model.number="item.quantity"
                        class="w-16 text-center border-x border-gray-300 focus:outline-none focus:border-blue-500"
                    />
                    <button
                        @click="updateQuantity(item, 1)"
                        class="px-3 py-1 bg-gray-100 text-gray-700 hover:bg-gray-200 transition duration-150"
                    >+</button>
                </div>
                <button
                  @click="removeFromCart(item.product.id)"
                  class="p-2 rounded-md bg-red-100 text-red-600 hover:bg-red-200 
                         focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2
                         transition duration-200 ease-in-out"
                >
                  <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5L13.5 3h-5L7.5 4H4v2h16V4z"/>
                  </svg>
                </button>
              </div>
            </div>
          </div>
          <div v-else class="text-center text-gray-500 py-10">
            Your cart is empty. Add some PT packages!
          </div>
        </div>

        <div class="p-6 border-t border-gray-200 bg-gray-50">
          <div class="flex justify-between items-center mb-4">
            <span class="text-xl font-bold text-gray-800">Total:</span>
            <span class="text-2xl font-extrabold text-blue-700">{{ totalPrice.toLocaleString() }} đ</span>
          </div>
          <button
            @click="proceedToCheckout"
            class="w-full px-5 py-3 rounded-lg bg-blue-600 text-white font-semibold text-lg
                   hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 
                   focus:ring-offset-2 transition duration-200 ease-in-out"
          >
            Proceed to Checkout
          </button>
        </div>
      </div>
    </div>
  </div>
</template>