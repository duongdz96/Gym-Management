<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import api from "@/services/api";
import { useRouter } from "vue-router";
import { useToast } from "vue-toastification";
import { SearchIcon, ShoppingCartIcon, PlusIcon, Trash2Icon, PackageIcon, UserIcon } from 'lucide-vue-next';

const router = useRouter();
const toast = useToast();

type Product = {
  id: number;
  name: string;
  type: string;
  price: number;
  brand?: string;
  quantity?: number; // Stock quantity
  image?: string | null;
  unit?: string;
};

type CartItem = {
  product: Product;
  quantity: number;
};

// State
const activeTab = ref<'product' | 'service'>('product');
const products = ref<Product[]>([]);
const services = ref<Product[]>([]);
const cart = ref<CartItem[]>([]);
const search = ref("");
const isLoading = ref(false);

const API_BASE = "http://localhost:8080";
const defaultImage = `${API_BASE}/image/defaults/no-image.png`;

// Fetch Data
const fetchData = async () => {
    isLoading.value = true;
    try {
        // Fetch Products (Exclude PT, Membership)
        const resProducts = await api.get("/products/exclude-types?types=PT,Membership");
        products.value = processImages(resProducts.data);

        // Fetch Services (Include PT, Membership)
        const resServices = await api.get("/products/include-types?types=PT,Membership");
        services.value = processImages(resServices.data);
    } catch (err) {
        console.error("Error fetching data:", err);
        toast.error("Lỗi khi tải dữ liệu sản phẩm!");
    } finally {
        isLoading.value = false;
    }
};

const processImages = (list: any[]) => {
    return list.map((p: Product) => ({
        ...p,
        image: p.image && !p.image.startsWith('http') ? `${API_BASE}/${p.image}` : (p.image || defaultImage),
    }));
};

onMounted(() => {
    // Check if there is a pending cart in session (optional, but good for UX)
    const savedCart = sessionStorage.getItem("currentCart");
    if(savedCart) {
        try {
            cart.value = JSON.parse(savedCart);
        } catch(e) { /* ignore */ }
    } else {
        // Only clear if no cart, or we might want to respect previous session
        // sessionStorage.clear(); 
    }
    
    fetchData();
});

// Computed
const currentList = computed(() => activeTab.value === 'product' ? products.value : services.value);

const filteredList = computed(() => 
    currentList.value.filter((p) => 
        (p.name || "").toLowerCase().includes(search.value.toLowerCase()) || 
        (p.type || "").toLowerCase().includes(search.value.toLowerCase())
    )
);

const totalAmount = computed(() => 
    cart.value.reduce((sum, item) => sum + item.product.price * item.quantity, 0)
);

// Actions
function addToCart(product: Product) {
    // Check stock for physical products
    if (activeTab.value === 'product') {
        if (!product.quantity || product.quantity <= 0) {
            toast.warning("Sản phẩm tạm hết hàng, vui lòng chọn sản phẩm khác!");
            return;
        }
    }

    const existing = cart.value.find((item) => item.product.id === product.id);
    if (existing) {
        // Check if adding more exceeds stock
        if (activeTab.value === 'product' && existing.quantity >= (product.quantity || 0)) {
            toast.warning("Số lượng trong kho không đủ!");
            return;
        }
        existing.quantity += 1;
    } else {
        cart.value.push({ product, quantity: 1 });
    }
    saveCart();
}

function removeFromCart(productId: number) {
    cart.value = cart.value.filter((item) => item.product.id !== productId);
    saveCart();
}

function updateQuantity(item: CartItem, newQty: number) {
    if(newQty < 1) return;

    // Check stock if increasing quantity for physical products
    if (newQty > item.quantity) {
        const isService = ['PT', 'Membership', 'SVC'].includes(item.product.type);
        if (!isService && newQty > (item.product.quantity || 0)) {
            toast.warning("Số lượng trong kho không đủ!");
            return;
        }
    }

    item.quantity = newQty;
    saveCart();
}

function saveCart() {
    sessionStorage.setItem("currentCart", JSON.stringify(cart.value));
}

function checkout() {
    if (cart.value.length === 0) {
        toast.warning("Vui lòng chọn ít nhất một sản phẩm/dịch vụ!");
        return;
    }

    // Prepare bill structure for checkout page
    const billData = {
        paymentMethod: null,
        paymentStatus: "PENDING",
        date: new Date().toISOString(),
        // receptionist will be set from auth store in checkout or backend
        listSoldProduct: cart.value.map((item) => ({
            product: { ...item.product },
            quantity: item.quantity,
        })),
        total: totalAmount.value,
    };

    sessionStorage.setItem("currentBill", JSON.stringify(billData));
    router.push({ name: "salesCheckout" });
}

function formatCurrency(value: number) {
    return value.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
}
</script>

<template>
  <div class="p-6 h-[calc(100vh-64px)] flex flex-col gap-6 bg-gray-50">
    
    <div class="grid grid-cols-12 gap-6 h-full">
        <!-- LEFT: Product Selection -->
        <div class="col-span-12 lg:col-span-8 flex flex-col bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
            <!-- Header & Tabs -->
            <div class="border-b border-gray-200">
                <div class="flex items-center justify-between p-4">
                     <h1 class="text-xl font-bold text-gray-800 flex items-center gap-2">
                        <ShoppingBagIcon class="w-6 h-6 text-blue-600"/>
                        Bán hàng & Dịch vụ
                     </h1>
                     <div class="relative w-64">
                        <input
                            v-model="search"
                            type="text"
                            placeholder="Tìm kiếm..."
                            class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none text-sm"
                        />
                        <SearchIcon class="w-4 h-4 text-gray-400 absolute left-3 top-1/2 -translate-y-1/2" />
                     </div>
                </div>
                
                <div class="flex px-4 gap-4">
                    <button 
                        @click="activeTab = 'product'"
                        class="flex items-center gap-2 px-4 py-3 border-b-2 font-medium transition-colors"
                        :class="activeTab === 'product' ? 'border-blue-600 text-blue-600' : 'border-transparent text-gray-500 hover:text-gray-700'"
                    >
                        <PackageIcon class="w-4 h-4" />
                        Sản phẩm
                    </button>
                    <button 
                        @click="activeTab = 'service'"
                        class="flex items-center gap-2 px-4 py-3 border-b-2 font-medium transition-colors"
                        :class="activeTab === 'service' ? 'border-blue-600 text-blue-600' : 'border-transparent text-gray-500 hover:text-gray-700'"
                    >
                        <UserIcon class="w-4 h-4" />
                        Dịch vụ (PT/Gói tập)
                    </button>
                </div>
            </div>

            <!-- Content Area -->
            <div class="flex-1 overflow-y-auto p-4 bg-gray-50">
                <div v-if="isLoading" class="flex justify-center items-center h-40">
                    <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                </div>

                <div v-else-if="filteredList.length === 0" class="flex flex-col items-center justify-center h-40 text-gray-500">
                    <PackageIcon class="w-12 h-12 mb-2 opacity-20" />
                    <p>Không tìm thấy sản phẩm nào</p>
                </div>

                <div v-else class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
                    <div 
                        v-for="item in filteredList" 
                        :key="item.id"
                        class="bg-white rounded-lg border border-gray-200 shadow-sm hover:shadow-md transition-shadow cursor-pointer overflow-hidden flex flex-col group"
                        @click="addToCart(item)"
                    >
                        <div class="aspect-square relative bg-gray-100 overflow-hidden">
                            <img :src="item.image!" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" alt="" />
                            <div class="absolute inset-0 bg-black/0 group-hover:bg-black/10 transition-colors flex items-center justify-center">
                                <span class="bg-white/90 text-gray-900 px-3 py-1 rounded-full text-xs font-bold shadow-sm opacity-0 group-hover:opacity-100 transform translate-y-2 group-hover:translate-y-0 transition-all">
                                    + Thêm
                                </span>
                            </div>
                        </div>
                        <div class="p-3 flex-1 flex flex-col">
                            <h3 class="font-medium text-gray-800 line-clamp-2 mb-1" :title="item.name">{{ item.name }}</h3>
                            <p class="text-xs text-gray-500 mb-2">{{ item.type }}</p>
                            <div class="mt-auto flex justify-between items-center">
                                <span class="font-bold text-blue-600">{{ formatCurrency(item.price) }}</span>
                                <span v-if="activeTab === 'product'" class="text-xs text-gray-500 bg-gray-100 px-2 py-0.5 rounded">
                                    Kho: {{ item.quantity }}
                                </span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- RIGHT: Cart -->
        <div class="col-span-12 lg:col-span-4 flex flex-col bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden h-full">
            <div class="p-4 border-b border-gray-200 bg-gray-50 flex justify-between items-center">
                <h2 class="font-bold text-gray-800 flex items-center gap-2">
                    <ShoppingCartIcon class="w-5 h-5 text-gray-600" />
                    Giỏ hàng <span class="bg-blue-600 text-white text-xs px-2 py-0.5 rounded-full">{{ cart.length }}</span>
                </h2>
                <button v-if="cart.length > 0" @click="cart = []; saveCart()" class="text-xs text-red-500 hover:text-red-700 underline">
                    Xóa tất cả
                </button>
            </div>

            <div class="flex-1 overflow-y-auto p-4 space-y-3">
                <div v-if="cart.length === 0" class="flex flex-col items-center justify-center h-full text-gray-400">
                    <ShoppingCartIcon class="w-12 h-12 mb-2 opacity-20" />
                    <p>Giỏ hàng đang trống</p>
                </div>

                <div v-for="(item, index) in cart" :key="index" class="flex gap-3 bg-white p-3 rounded-lg border border-gray-100 shadow-sm">
                    <img :src="item.product.image!" class="w-16 h-16 rounded-md object-cover bg-gray-100 border border-gray-200" alt="" />
                    <div class="flex-1 min-w-0">
                        <h4 class="font-medium text-gray-800 text-sm truncate" :title="item.product.name">{{ item.product.name }}</h4>
                        <div class="text-xs text-gray-500 mb-2">{{ item.product.type }}</div>
                        <div class="flex justify-between items-center">
                            <div class="flex items-center gap-2 border border-gray-200 rounded-lg bg-gray-50">
                                <button @click="updateQuantity(item, item.quantity - 1)" class="w-6 h-6 flex items-center justify-center text-gray-500 hover:bg-white hover:shadow-sm rounded-l-lg transition-all">-</button>
                                <span class="text-sm font-medium w-4 text-center">{{ item.quantity }}</span>
                                <button @click="updateQuantity(item, item.quantity + 1)" class="w-6 h-6 flex items-center justify-center text-gray-500 hover:bg-white hover:shadow-sm rounded-r-lg transition-all">+</button>
                            </div>
                            <span class="font-bold text-blue-600 text-sm">{{ formatCurrency(item.product.price * item.quantity) }}</span>
                        </div>
                    </div>
                     <button @click="removeFromCart(item.product.id)" class="text-gray-400 hover:text-red-500 self-start p-1 transition-colors">
                        <Trash2Icon class="w-4 h-4" />
                    </button>
                </div>
            </div>

            <div class="p-4 bg-gray-50 border-t border-gray-200 space-y-4">
                <div class="space-y-2 text-sm">
                     <div class="flex justify-between text-gray-600">
                        <span>Tạm tính:</span>
                        <span>{{ formatCurrency(totalAmount) }}</span>
                    </div>
                     <div class="flex justify-between font-bold text-lg text-gray-900 pt-2 border-t border-gray-200">
                        <span>Tổng cộng:</span>
                        <span class="text-blue-600">{{ formatCurrency(totalAmount) }}</span>
                    </div>
                </div>
                <button 
                    @click="checkout"
                    class="w-full py-3 bg-blue-600 text-white rounded-xl font-bold hover:bg-blue-700 active:scale-[0.98] transition-all shadow-lg shadow-blue-600/20 disabled:opacity-50 disabled:cursor-not-allowed"
                    :disabled="cart.length === 0"
                >
                    Thanh toán ngay
                </button>
            </div>
        </div>
    </div>
  </div>
</template>
