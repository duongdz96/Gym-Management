<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { 
    UserIcon, 
    CreditCardIcon, 
    BanknoteIcon, 
    QrCodeIcon, 
    PrinterIcon, 
    ArrowLeftIcon, 
    CheckCircleIcon,
    XIcon
} from 'lucide-vue-next';
import api from '@/services/api';
import Multiselect from 'vue-multiselect';
import { useToast } from "vue-toastification";

const route = useRoute();
const router = useRouter();
const toast = useToast();

// ================= STATE =================
const bill = ref(null);
const members = ref([]);
const selectedMember = ref(null);
const coupon = ref('');
const appliedCoupon = ref(null);
const paymentMethod = ref(null); // 'CASH' | 'BANKING' | 'CARD'
const user = ref({}); // logged in receptionist
const createdBillId = ref(null);
const isPaid = ref(false);
const activeTab = ref('product');
const showQRModal = ref(false);

// QR Payment variables
const bankCode = "MB";
const accountNumber = "2666677888";
const accountName = "VAN NGOC LONG";
const qrUrl = ref("");
const paymentStatus = ref("Pending");
const transferCode = ref("");
let pollInterval = null;

const productsList = computed(() => {
    if (!bill.value || !bill.value.listSoldProduct) return [];
    return bill.value.listSoldProduct.filter(item => 
        !['PT', 'Membership', 'SVC'].includes(item.product.type) && 
        item.product.name
    );
});

const servicesList = computed(() => {
    if (!bill.value || !bill.value.listSoldProduct) return [];
    return bill.value.listSoldProduct.filter(item => 
        ['PT', 'Membership', 'SVC'].includes(item.product.type)
    );
});

const totalPrice = computed(() => {
    if (!bill.value || !bill.value.listSoldProduct) return 0;
    return bill.value.listSoldProduct.reduce((sum, item) => sum + (item.product.price * item.quantity), 0);
});

const finalPrice = computed(() => {
     let total = totalPrice.value;
     if (appliedCoupon.value) {
         if (appliedCoupon.value.discountType === 'PERCENTAGE') {
             total -= (total * appliedCoupon.value.discountValue / 100);
         } else {
             total -= appliedCoupon.value.discountValue;
         }
     }
     return Math.max(0, total);
});

const statusClass = computed(() => {
    return paymentStatus.value === 'Paid' ? 'text-green-600' : 'text-yellow-600';
});

// ================= LIFECYCLE =================
onMounted(async () => {
    const stateBill = history.state.bill;
    if (stateBill) {
        bill.value = JSON.parse(stateBill);
    } else {
        const sessionBill = sessionStorage.getItem('currentBill');
        if (sessionBill) {
            bill.value = JSON.parse(sessionBill);
        } else {
            console.warn("No bill found in state");
        }
    }

    try {
        const res = await api.get('/members');
        members.value = res.data;
    } catch (e) {
        console.error("Failed to load members", e);
    }

    const storedUser = localStorage.getItem('user');
    if (storedUser) {
        user.value = JSON.parse(storedUser);
    }
});

onUnmounted(() => {
  if (pollInterval) clearInterval(pollInterval);
});

// ================= METHODS =================
const goBack = () => {
    router.back();
};

const checkCoupon = async () => {
    if (!coupon.value) return;
    
    // Member is required for checking coupon in backend
    if (!selectedMember.value || !selectedMember.value.id) {
        toast.warning("Vui lòng chọn khách hàng trước khi áp dụng mã giảm giá");
        return;
    }

    try {
        const res = await api.get('/coupons/check', {
            params: {
                code: coupon.value,
                memberId: selectedMember.value.id
            }
        });

        if (res.data) {
            const couponData = res.data;
            const now = new Date();
            
            // Check status
            if (couponData.status !== 'ACTIVE') {
                toast.error("Mã giảm giá không còn hiệu lực");
                appliedCoupon.value = null;
                return;
            }
            
            // Check start date
            const startDate = new Date(couponData.startDate);
            if (now < startDate) {
                toast.error(`Mã giảm giá chưa có hiệu lực. Có hiệu lực từ ${startDate.toLocaleDateString('vi-VN')}`);
                appliedCoupon.value = null;
                return;
            }
            
            // Check end date
            const endDate = new Date(couponData.endDate);
            if (now > endDate) {
                toast.error(`Mã giảm giá đã hết hạn vào ${endDate.toLocaleDateString('vi-VN')}`);
                appliedCoupon.value = null;
                return;
            }
            
            // All validations passed
            appliedCoupon.value = couponData;
            toast.success("Áp dụng mã giảm giá thành công!");
        } else {
            // Backend returns false if invalid or not found
            toast.error("Mã giảm giá không hợp lệ hoặc không áp dụng cho khách hàng này");
            appliedCoupon.value = null;
        }
    } catch (e) {
        toast.error("Lỗi kiểm tra mã giảm giá");
        appliedCoupon.value = null;
    }
};

function generateQR() {
  transferCode.value = `BILL${Date.now()}`;
  qrUrl.value = `https://img.vietqr.io/image/${bankCode}-${accountNumber}-compact.png?amount=${finalPrice.value}&addInfo=${encodeURIComponent(transferCode.value)}&accountName=${encodeURIComponent(accountName)}`;
  paymentStatus.value = "Pending";
  toast.info("QR đã được tạo. Đang chờ thanh toán...");
  startPolling();
}

function startPolling() {
  if (pollInterval) clearInterval(pollInterval);

  pollInterval = setInterval(async () => {
    try {
      const res = await api.get(
        `/casso/check?addInfo=${encodeURIComponent(transferCode.value)}&amount=${finalPrice.value}`
      );


      if (res.data?.paid) {
        paymentStatus.value = "Paid";
        toast.success("Đã nhận được thanh toán!");
        sessionStorage.removeItem('currentCart');
        sessionStorage.removeItem('currentBill');
        
        if (pollInterval) clearInterval(pollInterval);
        
        // Update bill status to PAID
        if (createdBillId.value) {
          await updateBillStatus(createdBillId.value, "PAID");
          isPaid.value = true;
        }
      }
    } catch (err) {
      console.error("Error during polling:", err);
    }
  }, 3000);
}

const submit = async () => {
    if (!bill.value) return;
    if (!paymentMethod.value) {
        toast.warning("Vui lòng chọn phương thức thanh toán");
        return;
    }

    // Prepare Payload
    try {
        const payload = {
            ...bill.value,
            member: selectedMember.value ? { id: selectedMember.value.id } : null,
            receptionist: user.value && user.value.id ? { id: user.value.id } : bill.value.receptionist,
            paymentMethod: paymentMethod.value,
            paymentStatus: paymentMethod.value === 'CASH' ? 'PAID' : 'PENDING',
            date: new Date(),
            totalPrice: finalPrice.value,
            coupon: appliedCoupon.value ? { id: appliedCoupon.value.id } : null,
            listSoldProduct: bill.value.listSoldProduct.map(item => ({
                product: { id: item.product.id },
                quantity: item.quantity
            }))
        };

        const response = await api.post('/bills', payload);
        
        if (response.data && response.data.id) {
             createdBillId.value = response.data.id;
             
             if (paymentMethod.value === 'CASH') {
                 isPaid.value = true;
                 sessionStorage.removeItem('currentCart');
                 sessionStorage.removeItem('currentBill');
                 toast.success("Thanh toán tiền mặt thành công!");
             } else {
                 showQRModal.value = true;
                 generateQR(); // Call generateQR here
             }
        }
    } catch (e) {
        const status = e.response?.status;
        const msg = e.response?.data?.message || e.message;
        if (status === 403) {
            toast.err(`Lỗi quyền truy cập (403): ${msg}. Vui lòng kiểm tra đăng nhập hoặc quyền hạn.`);
        } else {
            toast.error(`Lỗi tạo hóa đơn (${status}): ${msg}`);
        }
        console.error("Submit Error:", e);
    }
};

async function simulatePayment() {
  try {
    const payload = {
      data: [
        {
          amount: finalPrice.value,
          description: transferCode.value,
          tid: `SIM${Date.now()}`,
          bank: bankCode,
          cusumBalance: null
        }
      ]
    };
    
    await api.post("/casso/webhook", payload);
    toast.success("Giả lập chuyển khoản thành công!");
    
    // Auto update status in UI if polling doesn't catch it immediately (optional)
    // But polling usually catches it.
  } catch (err) {
    toast.error("Lỗi khi giả lập thanh toán!");
  }
}

async function updateBillStatus(billId, status) {
  try {
    await api.patch(`/bills/${billId}/status`, { paymentStatus: status });
    if (status === 'PAID') {
        isPaid.value = true;
    }
  } catch (err) {
    console.error("Error updating bill status:", err);
  }
}

const handlePrint = () => {
    setTimeout(() => {
        window.print();
    }, 100);
};
</script>

<template>
  <div class="min-h-[calc(100vh-64px)] p-6 bg-gray-50 flex flex-col">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6 shrink-0">
         <div class="flex items-center gap-3">
             <button @click="goBack" class="p-2 bg-white rounded-lg border border-gray-200 hover:bg-gray-50 text-gray-600 transition-colors">
                 <ArrowLeftIcon class="w-5 h-5" />
             </button>
             <div>
                <h1 class="text-2xl font-bold text-gray-900">Thanh toán</h1>
                <p class="text-sm text-gray-500">Hoàn tất đơn hàng và thanh toán</p>
             </div>
         </div>
         <div class="flex gap-3">
             <button @click="handlePrint" class="flex items-center gap-2 px-4 py-2 bg-white border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 font-medium transition-colors">
                 <PrinterIcon class="w-4 h-4" />
                 In phiếu
             </button>
         </div>
    </div>

    <!-- Main Content -->
    <div v-if="bill" class="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
        
        <!-- LEFT: Items Summary -->
        <div class="lg:col-span-8 flex flex-col bg-white rounded-2xl shadow-sm border border-gray-200">
            <!-- TABS HEADER -->
            <div class="flex border-b border-gray-200 bg-white h-[58px] sticky top-0 z-30 rounded-t-2xl">
                <button 
                    @click="activeTab = 'product'"
                    class="flex-1 text-center font-medium text-sm transition-colors border-b-2 h-full flex items-center justify-center"
                    :class="activeTab === 'product' ? 'border-emerald-600 text-emerald-600 bg-emerald-50/50' : 'border-transparent text-gray-500 hover:text-gray-700 hover:bg-gray-50'"
                >
                    Sản phẩm <span v-if="productsList.length" class="ml-1 bg-gray-100 text-gray-600 px-2 py-0.5 rounded-full text-xs">{{ productsList.length }}</span>
                </button>
                <button 
                    @click="activeTab = 'service'"
                    class="flex-1 text-center font-medium text-sm transition-colors border-b-2 h-full flex items-center justify-center"
                    :class="activeTab === 'service' ? 'border-emerald-600 text-emerald-600 bg-emerald-50/50' : 'border-transparent text-gray-500 hover:text-gray-700 hover:bg-gray-50'"
                >
                    Dịch vụ <span v-if="servicesList.length" class="ml-1 bg-gray-100 text-gray-600 px-2 py-0.5 rounded-full text-xs">{{ servicesList.length }}</span>
                </button>
            </div>
            
            <div class="bg-gray-50/30 min-h-[400px]">
                <!-- PRODUCTS TAB -->
                <div v-if="activeTab === 'product'">
                    <table v-if="productsList.length > 0" class="min-w-full divide-y divide-gray-100">
                        <thead class="bg-gray-50 sticky top-[58px] z-20 shadow-sm">
                            <tr>
                                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Tên sản phẩm</th>
                                <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">SL</th>
                                <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Đơn giá</th>
                                <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Thành tiền</th>
                            </tr>
                        </thead>
                        <tbody class="divide-y divide-gray-100 bg-white">
                            <tr v-for="item in productsList" :key="item.product.id" class="hover:bg-gray-50 transition-colors">
                                <td class="px-6 py-4">
                                    <div class="flex items-center gap-3">
                                         <div class="w-10 h-10 rounded bg-gray-100 flex-shrink-0 border border-gray-200 overflow-hidden">
                                             <img v-if="item.product.image" :src="item.product.image" class="w-full h-full object-cover" />
                                         </div>
                                         <div>
                                            <span class="font-medium text-gray-900 block">{{ item.product.name }}</span>
                                            <span class="text-xs text-gray-500">{{ item.product.brand }}</span>
                                         </div>
                                    </div>
                                </td>
                                <td class="px-6 py-4 text-center text-gray-600 font-medium">x{{ item.quantity }}</td>
                                <td class="px-6 py-4 text-right text-gray-600">{{ item.product.price.toLocaleString() }} đ</td>
                                <td class="px-6 py-4 text-right font-bold text-gray-900">{{ (item.product.price * item.quantity).toLocaleString() }} đ</td>
                            </tr>
                        </tbody>
                    </table>
                    <div v-else class="flex flex-col items-center justify-center p-12 text-gray-400">
                        <p>Không có sản phẩm nào trong giỏ</p>
                    </div>
                </div>

                <!-- SERVICES TAB -->
                <div v-if="activeTab === 'service'">
                    <table v-if="servicesList.length > 0" class="min-w-full divide-y divide-gray-100">
                        <thead class="bg-gray-50 sticky top-[58px] z-20 shadow-sm">
                            <tr>
                                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Tên dịch vụ</th>
                                <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">SL</th>
                                <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Đơn giá</th>
                                <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Thành tiền</th>
                            </tr>
                        </thead>
                        <tbody class="divide-y divide-gray-100 bg-white">
                            <tr v-for="item in servicesList" :key="item.product.id" class="hover:bg-gray-50 transition-colors">
                                <td class="px-6 py-4">
                                     <div class="flex items-center gap-3">
                                         <div class="w-10 h-10 rounded bg-gray-100 flex-shrink-0 border border-gray-200 overflow-hidden">
                                             <img v-if="item.product.image" :src="item.product.image" class="w-full h-full object-cover" />
                                         </div>
                                         <div>
                                            <span class="font-medium text-gray-900 block">{{ item.product.name }}</span>
                                            <span class="text-xs text-indigo-500 font-medium">{{ item.product.type }}</span>
                                         </div>
                                    </div>
                                </td>
                                <td class="px-6 py-4 text-center text-gray-600 font-medium">x{{ item.quantity }}</td>
                                <td class="px-6 py-4 text-right text-gray-600">{{ item.product.price.toLocaleString() }} đ</td>
                                <td class="px-6 py-4 text-right font-bold text-gray-900">{{ (item.product.price * item.quantity).toLocaleString() }} đ</td>
                            </tr>
                        </tbody>
                    </table>
                    <div v-else class="flex flex-col items-center justify-center p-12 text-gray-400">
                        <p>Không có dịch vụ nào trong giỏ</p>
                    </div>
                </div>
            </div>
            
            <!-- Totals Footer -->
             <div class="p-6 bg-gray-50 border-t border-gray-200 space-y-3 mt-auto">
                 <div class="flex justify-between text-gray-600">
                     <span>Tạm tính ({{ bill.listSoldProduct.length }} items):</span>
                     <span class="font-medium">{{ totalPrice.toLocaleString() }} đ</span>
                 </div>
                 <div v-if="appliedCoupon" class="flex justify-between text-green-600">
                     <span>Giảm giá ({{ appliedCoupon.code }}):</span>
                     <span class="font-medium">
                         -{{ appliedCoupon.discountType === 'PERCENTAGE' 
                             ? (totalPrice * appliedCoupon.discountValue / 100).toLocaleString() 
                             : appliedCoupon.discountValue.toLocaleString() }} đ
                     </span>
                 </div>
                 <div class="flex justify-between text-2xl font-bold text-gray-900 pt-3 border-t border-gray-200 mt-2">
                     <span>Tổng thanh toán:</span>
                     <span class="text-blue-600">{{ finalPrice.toLocaleString() }} đ</span>
                 </div>
             </div>
        </div>

        <!-- RIGHT: Payment & Info -->
        <div class="lg:col-span-4 flex flex-col gap-6 sticky top-6 h-fit max-h-[calc(100vh-48px)] overflow-y-auto">
            
            <!-- Customer Info -->
            <div class="bg-white rounded-2xl shadow-sm border border-gray-200 p-5 space-y-4">
                 <div class="flex justify-between items-center">
                    <h3 class="font-semibold text-gray-800 text-sm uppercase tracking-wide">Khách hàng <span class="text-gray-400 font-normal normal-case">(Tuỳ chọn)</span></h3>
                 </div>
                 <Multiselect
                    v-model="selectedMember"
                    :options="members"
                    placeholder="Tìm khách hàng..."
                    label="fullName"
                    track-by="id"
                    :disabled="isPaid || !!createdBillId"
                    class="w-full text-sm"
                />
                
                <div v-if="selectedMember" class="bg-blue-50 rounded-lg p-3 space-y-1 text-sm border border-blue-100 animate-fade-in">
                    <p class="flex justify-between"><span class="text-gray-500">Tên:</span> <span class="font-medium text-gray-900">{{ selectedMember.fullName }}</span></p>
                    <p class="flex justify-between"><span class="text-gray-500">SĐT:</span> <span class="font-medium text-gray-900">{{ selectedMember.phone }}</span></p>
                    <p class="flex justify-between"><span class="text-gray-500">Hạng:</span> <span class="font-medium text-blue-600">{{ selectedMember.membership || "Mới" }}</span></p>
                </div>
            </div>

            <!-- Coupon -->
            <div class="bg-white rounded-2xl shadow-sm border border-gray-200 p-5 space-y-3" v-if="!isPaid && !createdBillId">
                <h3 class="font-semibold text-gray-800 text-sm uppercase tracking-wide">Mã giảm giá <span class="text-gray-400 font-normal normal-case">(Tuỳ chọn)</span></h3>
                <div class="flex gap-2">
                    <input
                        v-model="coupon"
                        type="text"
                        placeholder="Nhập mã..."
                        class="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none text-sm"
                    />
                    <button @click="checkCoupon" class="bg-gray-800 text-white px-3 py-2 rounded-lg hover:bg-gray-700 transition-colors text-xs font-medium">
                        Áp dụng
                    </button>
                </div>
            </div>

            <!-- Payment Method -->
            <div class="bg-white rounded-2xl shadow-sm border border-gray-200 p-5 flex flex-col">
                <h3 class="font-semibold text-gray-800 mb-4 text-sm uppercase tracking-wide">Thanh toán</h3>

                <!-- Success State -->
                <div v-if="isPaid" class="flex flex-col items-center justify-center text-center space-y-4 py-8">
                     <div class="w-16 h-16 bg-green-100 text-green-600 rounded-full flex items-center justify-center animate-bounce-short">
                         <CheckCircleIcon class="w-8 h-8" />
                     </div>
                     <div>
                         <h4 class="text-xl font-bold text-gray-900">Thanh toán xong!</h4>
                         <p class="text-gray-500 mt-1 text-sm">Hóa đơn #{{ createdBillId }}</p>
                     </div>
                     <button @click="handlePrint" class="w-full flex items-center justify-center gap-2 px-6 py-3 bg-gray-900 text-white rounded-xl font-bold hover:bg-gray-800 transition-all shadow-lg text-sm">
                         <PrinterIcon class="w-4 h-4" />
                         In lại hóa đơn
                     </button>
                     <button @click="goBack" class="text-blue-600 font-medium hover:underline mt-4 text-sm">
                         Quay lại bán hàng
                     </button>
                </div>

                <!-- Payment Form -->
                <template v-else>
                     <div class="grid grid-cols-2 gap-3 mb-6">
                        <button 
                            @click="paymentMethod = 'CASH'"
                            class="flex flex-col items-center justify-center p-3 rounded-xl border-2 transition-all gap-1"
                            :class="paymentMethod === 'CASH' ? 'border-blue-600 bg-blue-50 text-blue-700' : 'border-gray-200 hover:border-blue-200 hover:bg-gray-50 text-gray-600'"
                        >
                            <BanknoteIcon class="w-6 h-6" />
                            <span class="font-medium text-sm">Tiền mặt</span>
                        </button>
                        <button 
                            @click="paymentMethod = 'BANKING'"
                            class="flex flex-col items-center justify-center p-3 rounded-xl border-2 transition-all gap-1"
                            :class="paymentMethod === 'BANKING' ? 'border-blue-600 bg-blue-50 text-blue-700' : 'border-gray-200 hover:border-blue-200 hover:bg-gray-50 text-gray-600'"
                        >
                            <QrCodeIcon class="w-6 h-6" />
                            <span class="font-medium text-sm">CK Ngân hàng</span>
                        </button>
                     </div>

                     <div v-if="createdBillId && paymentMethod === 'BANKING'" class="mb-4">
                         <button @click="showQRModal = true" class="w-full py-2 bg-yellow-100 text-yellow-700 rounded-lg font-medium text-sm hover:bg-yellow-200 transition-colors">
                             <QrCodeIcon class="w-4 h-4 inline mr-1" />
                             Hiện lại mã QR
                         </button>
                     </div>

                     <div class="mt-auto pt-4 border-t border-gray-100" v-if="!createdBillId || paymentMethod === 'BANKING'">
                        <button 
                            @click="submit"
                            class="w-full py-3 bg-blue-600 text-white rounded-xl font-bold text-base hover:bg-blue-700 active:scale-[0.98] transition-all shadow-lg shadow-blue-600/20 disabled:opacity-50 disabled:cursor-not-allowed"
                            :disabled="!paymentMethod || (createdBillId && paymentMethod === 'BANKING')"
                        >
                            {{ createdBillId ? 'Đang thanh toán...' : 'Xác nhận thanh toán' }}
                        </button>
                     </div>
                </template>
            </div>
        </div>
    </div>
    
    <div v-else class="flex flex-col items-center justify-center h-full text-gray-500">
        <p>Không có thông tin hóa đơn. Vui lòng quay lại tạo đơn hàng.</p>
        <button @click="goBack" class="mt-4 text-blue-600 hover:underline">Quay lại</button>
    </div>

    <!-- QR CODE MODAL -->
    <div v-if="showQRModal" class="fixed inset-0 z-[1000] flex items-center justify-center bg-black/60 backdrop-blur-sm p-4 animate-fade-in">
        <div class="bg-white w-full max-w-sm rounded-2xl shadow-2xl overflow-hidden relative flex flex-col items-center">
             <!-- Close Btn -->
             <button @click="showQRModal = false" class="absolute top-4 right-4 p-2 bg-gray-100 hover:bg-gray-200 rounded-full text-gray-600 transition-colors">
                 <XIcon class="w-5 h-5" />
             </button>

             <div class="bg-blue-600 w-full py-4 text-center">
                 <h3 class="text-white font-bold text-lg">Thanh toán QR</h3>
                 <p class="text-blue-100 text-xs text-opacity-80">Quét mã để thanh toán chuyển khoản</p>
             </div>
             
             <div class="p-8 flex flex-col items-center w-full">
                 <div class="bg-white p-3 rounded-xl border border-gray-200 shadow-sm mb-4">
                     <img :src="qrUrl" class="w-48 h-48 object-contain" alt="QR Code" />
                 </div>
                 
                 <div class="text-center space-y-2 w-full">
                     <div class="flex justify-between w-full text-sm border-b border-gray-100 pb-2">
                        <span class="text-gray-500">Nội dung CK:</span>
                        <span class="font-mono font-bold text-blue-600 select-all cursor-pointer hover:bg-blue-50 px-1 rounded">{{ transferCode }}</span>
                     </div>
                     <div class="flex justify-between w-full text-sm border-b border-gray-100 pb-2">
                        <span class="text-gray-500">Số tiền:</span>
                        <span class="font-bold text-gray-900">{{ finalPrice.toLocaleString() }} đ</span>
                     </div>
                     <div class="flex justify-between w-full text-sm pt-1">
                        <span class="text-gray-500">Trạng thái:</span>
                        <span class="font-bold flex items-center gap-1" :class="statusClass">
                           <span v-if="paymentStatus === 'Pending'" class="animate-pulse w-2 h-2 rounded-full bg-yellow-500"></span>
                           {{ paymentStatus === 'Pending' ? 'Đang chờ...' : 'Thanh toán thành công' }}
                        </span>
                     </div>
                 </div>

                 <!-- Simulate Button -->
                 <button 
                    @click="simulatePayment" 
                    v-if="paymentStatus === 'Pending'"
                    class="mt-8 w-full py-3 bg-gray-900 hover:bg-black text-white rounded-xl font-bold text-sm transition-all shadow-lg flex items-center justify-center gap-2"
                 >
                     <CheckCircleIcon class="w-5 h-5" />
                     (Dev) Giả lập thanh toán thành công
                 </button>

                 <div v-else class="mt-8 w-full">
                      <div class="text-green-600 font-bold text-center mb-4 flex items-center justify-center gap-2">
                          <CheckCircleIcon class="w-6 h-6" /> Đã nhận tiền!
                      </div>
                      <button @click="showQRModal = false" class="w-full py-3 bg-blue-50 text-blue-600 hover:bg-blue-100 rounded-xl font-bold text-sm transition-colors">
                          Đóng
                      </button>
                 </div>
             </div>
        </div>
    </div>
    
    <!-- ================= PRINT TEMPLATE (Hidden on screen) ================= -->
    <div id="print-area" v-if="bill" class="hidden print:block fixed inset-0 z-[9999] bg-white p-4 font-mono text-xs leading-relaxed text-black">
        <div class="max-w-[80mm] mx-auto">
            <!-- HEADER -->
            <div class="text-center border-b border-dashed border-gray-400 pb-3 mb-3">
                <h1 class="text-xl font-bold uppercase tracking-wider mb-1">GYM & POOL CENTER</h1>
                <p>123 Fitness Street, District 1, HCMC</p>
                <p>Hotline: 1800 1234</p>
            </div>
            
            <div class="text-center mb-3">
                <h2 class="text-lg font-bold uppercase">HÓA ĐƠN THANH TOÁN</h2>
                <p v-if="createdBillId">#{{ createdBillId }}</p>
                <p class="text-[10px] text-gray-500">{{ new Date().toLocaleString('vi-VN') }}</p>
            </div>

            <!-- INFO -->
            <div class="mb-3 border-b border-dashed border-gray-400 pb-3 space-y-1">
                <div class="flex justify-between">
                    <span>Thu ngân:</span>
                    <span class="font-bold">{{ user.fullName || 'Admin' }}</span>
                </div>
                <template v-if="selectedMember">
                    <div class="flex justify-between">
                        <span>Khách hàng:</span>
                        <span class="font-bold">{{ selectedMember.fullName }}</span>
                    </div>
                </template>
                <template v-else>
                     <div class="flex justify-between">
                        <span>Khách hàng:</span>
                        <span class="italic text-gray-400">Khách lẻ</span>
                    </div>
                </template>
            </div>

            <!-- LIST ITEMS -->
            <div class="mb-3 border-b border-dashed border-gray-400 pb-3">
                <table class="w-full text-left" v-if="bill">
                    <thead>
                        <tr>
                            <th class="pb-1 uppercase text-[10px]">Tên SP/DV</th>
                            <th class="pb-1 text-center text-[10px]">SL</th>
                            <th class="pb-1 text-right text-[10px]">T.Tiền</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="item in bill.listSoldProduct" :key="item.product.id">
                            <td class="pt-1 pr-1 truncate max-w-[40mm]">{{ item.product.name }}</td>
                            <td class="pt-1 text-center">{{ item.quantity }}</td>
                            <td class="pt-1 text-right font-medium">{{ (item.product.price * item.quantity).toLocaleString() }}</td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <!-- TOTALS -->
            <div class="space-y-1 text-right mb-4">
                <div class="flex justify-between">
                    <span>Tạm tính:</span>
                    <span>{{ totalPrice.toLocaleString() }}</span>
                </div>
                <div v-if="appliedCoupon" class="flex justify-between text-gray-600">
                    <span>Giảm giá ({{ appliedCoupon.code }}):</span>
                    <span>-{{ appliedCoupon.discountType === 'PERCENTAGE' 
                             ? (totalPrice * appliedCoupon.discountValue / 100).toLocaleString() 
                             : appliedCoupon.discountValue.toLocaleString() }}</span>
                </div>
                <div class="flex justify-between text-lg font-bold border-t border-dashed border-gray-400 pt-2 mt-1">
                    <span>TỔNG CỘNG:</span>
                    <span>{{ finalPrice.toLocaleString() }} đ</span>
                </div>
                <div class="flex justify-between text-[10px] italic pt-1">
                    <span>Hình thức TT:</span>
                    <span>{{ paymentMethod === 'CASH' ? 'Tiền mặt' : 'Chuyển khoản' }}</span>
                </div>
            </div>

            <!-- FOOTER -->
            <div class="text-center text-[10px] space-y-1 border-t border-dashed border-gray-400 pt-3">
                <p>Cảm ơn quý khách đã mua hàng!</p>
                <p>Hẹn gặp lại quý khách.</p>
                <p class="italic text-gray-400 mt-2">Powered by GymManagementSystem</p>
                <div class="mt-4 flex flex-col items-center">
                    <!-- Placeholder Barcode -->
                     <svg class="w-full h-8" viewBox="0 0 100 20" preserveAspectRatio="none">
                         <path d="M0 0h1v20H0zm2 0h1v20H2zm2 0h2v20H4zm3 0h1v20H7zm2 0h3v20H9zm4 0h1v20h-1zm2 0h1v20h-1zm2 0h2v20h-2zm3 0h1v20h-1zm2 0h1v20h-1zm2 0h2v20h-2zm4 0h1v20h-1z" fill="#000"/>
                     </svg>
                </div>
            </div>
        </div>
    </div>
  </div>
</template>

<style scoped>
@media print {
  body * {
    visibility: hidden;
  }
  #print-area, #print-area * {
    visibility: visible;
  }
  #print-area {
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
    margin: 0;
    padding: 0;
    background: white;
    display: flex; /* Ensure centering works in print */
    justify-content: center;
  }
  /* Optional: Reset margin for print page */
  @page {
    size: auto;
    margin: 0;
  }
}
</style>

<style scoped>
@import "vue-multiselect/dist/vue-multiselect.css";

.multiselect {
  min-height: 45px;
}

@media print {
    /* Hide everything that is not the invoice */
    /* Implementation note: In a real app, you would likely have a separate printable section or window */
    /* For now we will just print the whole page but hide buttons */
    button, input, select {
        display: none !important;
    }
    .no-print {
        display: none !important;
    }
}
</style>