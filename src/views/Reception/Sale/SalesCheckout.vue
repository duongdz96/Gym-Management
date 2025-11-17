<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import api from "../../../services/api";
import Multiselect from "vue-multiselect";
import { useToast } from "vue-toastification";

const router = useRouter();
const toast = useToast();

// ===================== DATA =====================
const bill = ref<any>(null);
const coupon = ref("");
const appliedCoupon = ref<any>(null);
const paymentMethod = ref<"CARD" | "CASH" | "BANKING" | null>(null);
const members = ref<any[]>([]);
const selectedMember = ref<any | null>(null);

// ===================== FETCH MEMBERS =====================
async function fetchMembers() {
  try {
    const res = await api.get("http://localhost:8080/api/members");
    members.value = res.data || [];
  } catch (err) {
    console.error("Lỗi khi lấy danh sách member:", err);
    toast.error("Không thể tải danh sách khách hàng!");
  }
}

// ===================== ON MOUNT =====================
onMounted(() => {
  const data = sessionStorage.getItem("currentBill");
  if (data) {
    bill.value = JSON.parse(data);
  }
  fetchMembers();
});

// ===================== COMPUTED =====================
const totalPrice = computed(() => {
  if (!bill.value) return 0;
  return (
    bill.value.listSoldProduct?.reduce(
      (sum: number, item: any) =>
        sum + (item.product?.price || 0) * (item.quantity || 0),
      0
    ) || 0
  );
});

const finalPrice = computed(() => {
  if (!appliedCoupon.value) return totalPrice.value;

  const { discountType, discountValue } = appliedCoupon.value;
  if (discountType === "PERCENTAGE") {
    return totalPrice.value - (totalPrice.value * discountValue) / 100;
  } else if (discountType === "FIXED_AMOUNT") {
    return Math.max(totalPrice.value - discountValue, 0);
  }
  return totalPrice.value;
});

// ===================== CHECK COUPON =====================
async function checkCoupon() {
  const code = coupon.value?.trim();
  if (!code) {
    toast.warning("Vui lòng nhập mã giảm giá!");
    return;
  }

  if (!selectedMember.value || !selectedMember.value.id) {
    toast.warning("Vui lòng chọn khách hàng trước khi kiểm tra mã!");
    return;
  }

  try {
    // Gọi API mới: /api/coupons/check?code=...&memberId=...
    const res = await api.get("/coupons/check", {
      params: {
        code: code,
        memberId: selectedMember.value.id
      }
    });

    const data = res.data;

    // Backend trả false khi không áp dụng được
    if (data === false || data === "false") {
      toast.error("Mã giảm giá không thể áp dụng cho khách hàng này!");
      appliedCoupon.value = null;
      return;
    }

    // Nếu backend trả object coupon -> áp dụng
    const now = new Date();
    const end = data.endDate ? new Date(data.endDate) : null;
    if (data.status !== "ACTIVE" || (end && end < now)) {
      toast.error("Mã giảm giá đã hết hạn hoặc không còn hiệu lực!");
      appliedCoupon.value = null;
      return;
    }

    appliedCoupon.value = data;
    toast.success(`Áp dụng mã ${data.code} thành công!`);
  } catch (err: any) {
    console.error("Error checking coupon:", err);
    const msg = err?.response?.data || "Lỗi khi kiểm tra mã giảm giá!";
    toast.error(typeof msg === "string" ? msg : "Mã giảm giá không hợp lệ!");
    appliedCoupon.value = null;
  }
}


// ===================== SUBMIT =====================
async function submit() {
  if (!selectedMember.value) {
    toast.warning("Vui lòng chọn khách hàng trước khi thanh toán!");
    return;
  }

  if (!paymentMethod.value) {
    toast.warning("Vui lòng chọn phương thức thanh toán!");
    return;
  }

  const payload = {
    ...bill.value,
    member: {
      id: selectedMember.value.id,
      fullName: selectedMember.value.fullName,
      email: selectedMember.value.email,
      phone: selectedMember.value.phone,
    },
    receptionist: { id: 3, name: "Nguyễn Văn Lễ Tân" },
    coupon: appliedCoupon.value || null,
    paymentMethod: paymentMethod.value,
    paymentStatus: "PAID",
    totalPrice: finalPrice.value,
  };

  try {
    const res = await api.post("/bills", payload);
    if (res.status === 200 || res.status === 201) {
      toast.success("Thanh toán thành công!");
      sessionStorage.removeItem("currentBill");
      bill.value = null;
      coupon.value = "";
      appliedCoupon.value = null;
      paymentMethod.value = null;
      selectedMember.value = null;
      router.push({ name: "salesselect" });
    }
  } catch (err) {
    console.error("Error submitting bill:", err);
    toast.error("Không thể lưu hóa đơn. Vui lòng thử lại!");
  }
}
</script>

<template>
  <div class="p-6 max-w-4xl mx-auto space-y-8 bg-gray-50 rounded-2xl shadow-md">
    <!-- Tiêu đề -->
    <h1 class="text-3xl font-bold text-gray-900 border-b pb-4 mb-6">
      Sales Checkout
    </h1>

    <!-- Bill Detail -->
    <div v-if="bill" class="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
      <!-- Products -->
      <h2 class="font-semibold mb-4 text-lg border-b pb-2 text-gray-800">
        Sản phẩm đã chọn
      </h2>

      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-100">
          <tr>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-700">Tên sản phẩm</th>
            <th class="px-6 py-3 text-center text-sm font-medium text-gray-700">Số lượng</th>
            <th class="px-6 py-3 text-right text-sm font-medium text-gray-700">Giá</th>
            <th class="px-6 py-3 text-right text-sm font-medium text-gray-700">Thành tiền</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
          <tr
            v-for="item in bill.listSoldProduct"
            :key="item.product.id"
            class="hover:bg-gray-50 transition"
          >
            <td class="px-6 py-3">{{ item.product.name }}</td>
            <td class="px-6 py-3 text-center">{{ item.quantity }}</td>
            <td class="px-6 py-3 text-right">{{ item.product.price.toLocaleString() }} đ</td>
            <td class="px-6 py-3 text-right">
              {{ (item.product.price * item.quantity).toLocaleString() }} đ
            </td>
          </tr>
        </tbody>
      </table>

      <!-- Tổng cộng -->
      <div class="mt-6 text-right text-xl font-semibold">
        <template v-if="appliedCoupon">
          <div class="text-gray-500 line-through">
            Tổng cộng: {{ totalPrice.toLocaleString() }} đ
          </div>
          <div class="text-green-600 mt-1">
            Giảm:
            <span v-if="appliedCoupon.discountType === 'PERCENTAGE'">
              {{ appliedCoupon.discountValue }}%
            </span>
            <span v-else>
              {{ appliedCoupon.discountValue.toLocaleString() }} đ
            </span>
          </div>
          <div class="text-blue-700 text-2xl mt-2">
            Thành tiền: {{ finalPrice.toLocaleString() }} đ
          </div>
        </template>
        <template v-else>
          Tổng cộng: {{ totalPrice.toLocaleString() }} đ
        </template>
      </div>
    </div>

    <!-- Chọn khách hàng -->
    <div class="bg-white rounded-2xl shadow-md border border-gray-200 p-8 space-y-4">
      <h3 class="text-2xl font-semibold text-gray-800 mb-4">Thông tin khách hàng</h3>

      <div class="bg-gray-50 border border-gray-300 rounded-xl p-4">
        <Multiselect
          v-model="selectedMember"
          :options="members"
          placeholder="Chọn khách hàng"
          label="fullName"
          track-by="id"
          class="flex-1"
        />
      </div>

      <div
        v-if="selectedMember"
        class="mt-6 bg-gray-50 border border-gray-200 rounded-xl p-4 text-gray-700 space-y-2"
      >
        <p><strong>Họ tên:</strong> {{ selectedMember.fullName }}</p>
        <p><strong>Số điện thoại:</strong> {{ selectedMember.phone }}</p>
        <p><strong>Email:</strong> {{ selectedMember.email }}</p>
        <p>
          <strong>Hạng thành viên:</strong>
          {{ selectedMember.membership || "Khách thường" }}
        </p>
      </div>
    </div>

    <!-- Coupon Code -->
    <div class="bg-white border border-gray-200 rounded-2xl p-6 shadow-sm">
      <h3 class="text-lg font-semibold text-gray-800 mb-3">Mã giảm giá</h3>
      <div class="flex gap-3 items-center">
        <input
          v-model="coupon"
          type="text"
          placeholder="Nhập mã giảm giá (nếu có)"
          class="flex-1 px-4 py-3 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
        <button
          @click="checkCoupon"
          class="px-4 py-3 rounded-lg bg-indigo-600 text-white hover:bg-indigo-700 transition"
        >
          Kiểm tra mã
        </button>
      </div>
      <div v-if="appliedCoupon" class="mt-3 text-green-600 text-sm">
        ✅ Mã "{{ appliedCoupon.code }}" đã được áp dụng.
      </div>
    </div>

    <!-- Payment Method -->
    <div class="bg-white border border-gray-200 rounded-2xl p-6 shadow-sm">
      <h3 class="mb-3 font-semibold text-gray-900 text-lg">Phương thức thanh toán</h3>
      <div class="flex gap-6">
        <button
          :class="[
            'flex-1 py-3 rounded-lg border font-medium text-center transition',
            paymentMethod === 'CARD'
              ? 'bg-green-600 text-white border-green-600'
              : 'bg-white text-gray-700 border-gray-300 hover:bg-gray-100',
          ]"
          @click="paymentMethod = 'CARD'"
        >
          Thẻ (Card)
        </button>
        <button
          :class="[
            'flex-1 py-3 rounded-lg border font-medium text-center transition',
            paymentMethod === 'CASH'
              ? 'bg-green-600 text-white border-green-600'
              : 'bg-white text-gray-700 border-gray-300 hover:bg-gray-100',
          ]"
          @click="paymentMethod = 'CASH'"
        >
          Tiền mặt
        </button>
        <button
          :class="[
            'flex-1 py-3 rounded-lg border font-medium text-center transition',
            paymentMethod === 'BANKING'
              ? 'bg-green-600 text-white border-green-600'
              : 'bg-white text-gray-700 border-gray-300 hover:bg-gray-100',
          ]"
          @click="paymentMethod = 'BANKING'"
        >
          QR Banking
        </button>
      </div>
    </div>

    <!-- Buttons -->
    <div class="flex justify-end gap-3">
      <RouterLink
        :to="{ name: 'salesselect' }"
        class="px-5 py-3 rounded-lg border border-gray-300 text-gray-700 hover:bg-gray-100"
      >
        Hủy
      </RouterLink>
      <button
        @click="submit"
        class="px-5 py-3 rounded-lg bg-green-600 text-white hover:bg-green-700 disabled:opacity-50"
        :disabled="!bill || bill.listSoldProduct.length === 0"
      >
        Xác nhận thanh toán
      </button>
    </div>
  </div>
</template>

<style scoped>
@import "vue-multiselect/dist/vue-multiselect.css";

.multiselect {
  width: 100%;
  min-height: 45px;
}
.multiselect__tags {
  padding: 10px 12px;
}
</style>
