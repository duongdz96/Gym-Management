<template>
  <div class="p-6 max-w-4xl mx-auto">
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold text-gray-800 dark:text-gray-100 flex items-center gap-2">
        <EditIcon class="w-6 h-6 text-blue-600" />
        Sửa Mã Giảm Giá
      </h1>
      <RouterLink
        :to="{ name: 'coupon' }"
        class="flex items-center gap-2 px-4 py-2 rounded-lg bg-gray-100 hover:bg-gray-200 text-gray-700 transition-colors"
      >
        <ArrowLeftIcon class="w-4 h-4" />
        Quay Lại
      </RouterLink>
    </div>

    <form
      class="bg-white dark:bg-gray-900 shadow-xl rounded-2xl p-6 grid grid-cols-1 md:grid-cols-2 gap-6"
      @submit.prevent="submit"
    >
      <!-- Code -->
      <div class="space-y-2">
        <label class="block text-sm font-semibold text-gray-700 dark:text-gray-300">Mã Coupon</label>
        <div class="relative">
             <input
                v-model="form.code"
                type="text"
                placeholder="vd: SUMMER2025"
                class="w-full pl-10 pr-4 py-2.5 rounded-lg border border-gray-300 dark:border-gray-700 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all bg-gray-50 dark:bg-gray-800"
                required
              />
             <TagIcon class="w-5 h-5 text-gray-400 absolute left-3 top-2.5" />
        </div>
      </div>

      <!-- Discount Type -->
      <div class="space-y-2">
        <label class="block text-sm font-semibold text-gray-700 dark:text-gray-300">Loại Giảm Giá</label>
        <div class="relative">
             <select
              v-model="form.discountType"
              class="w-full pl-10 pr-4 py-2.5 rounded-lg border border-gray-300 dark:border-gray-700 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all bg-gray-50 dark:bg-gray-800 appearance-none"
              required
            >
              <option value="PERCENTAGE">Phần trăm (%)</option>
              <option value="FIXED_AMOUNT">Số tiền cố định (VND)</option>
            </select>
             <PercentIcon class="w-5 h-5 text-gray-400 absolute left-3 top-2.5" />
        </div>
      </div>

      <!-- Discount Value -->
      <div class="space-y-2">
        <label class="block text-sm font-semibold text-gray-700 dark:text-gray-300">Giá Trị Giảm</label>
        <div class="relative">
             <input
              v-model.number="form.discountValue"
              type="number"
              min="0"
              class="w-full pl-10 pr-4 py-2.5 rounded-lg border border-gray-300 dark:border-gray-700 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all bg-gray-50 dark:bg-gray-800"
              required
            />
            <DollarSignIcon class="w-5 h-5 text-gray-400 absolute left-3 top-2.5" />
        </div>
      </div>

      <!-- Status -->
      <div class="space-y-2">
        <label class="block text-sm font-semibold text-gray-700 dark:text-gray-300">Trạng Thái</label>
        <div class="relative">
             <select
              v-model="form.status"
              class="w-full pl-10 pr-4 py-2.5 rounded-lg border border-gray-300 dark:border-gray-700 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all bg-gray-50 dark:bg-gray-800 appearance-none"
              required
            >
              <option value="ACTIVE">Đang hoạt động</option>
              <option value="INACTIVE">Không hoạt động</option>
            </select>
            <ActivityIcon class="w-5 h-5 text-gray-400 absolute left-3 top-2.5" />
        </div>
      </div>

      <!-- Start Date -->
      <div class="space-y-2">
        <label class="block text-sm font-semibold text-gray-700 dark:text-gray-300">Ngày Bắt Đầu</label>
        <div class="relative">
             <input
              v-model="form.startDate"
              type="date"
              class="w-full pl-10 pr-4 py-2.5 rounded-lg border border-gray-300 dark:border-gray-700 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all bg-gray-50 dark:bg-gray-800"
              required
            />
            <CalendarIcon class="w-5 h-5 text-gray-400 absolute left-3 top-2.5" />
        </div>
      </div>

      <!-- End Date -->
      <div class="space-y-2">
        <label class="block text-sm font-semibold text-gray-700 dark:text-gray-300">Ngày Kết Thúc</label>
        <div class="relative">
             <input
              v-model="form.endDate"
              type="date"
              class="w-full pl-10 pr-4 py-2.5 rounded-lg border border-gray-300 dark:border-gray-700 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all bg-gray-50 dark:bg-gray-800"
              required
            />
            <CalendarOffIcon class="w-5 h-5 text-gray-400 absolute left-3 top-2.5" />
        </div>
      </div>


      <!-- Scope -->
      <div class="md:col-span-2 space-y-2">
        <label class="block text-sm font-semibold text-gray-700 dark:text-gray-300">Phạm Vi (Hạng Thành Viên)</label>
        <select
            v-model="form.scope"
            class="w-full px-4 py-2.5 rounded-lg border border-gray-300 dark:border-gray-700 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all bg-gray-50 dark:bg-gray-800"
            >
            <option disabled value="">-- Chọn Hạng --</option>
            <option
                v-for="tier in tiers"
                :key="tier.id"
                :value="tier.name"
            >
                {{ tier.name }}
            </option>
        </select>
      </div>

      <!-- Buttons -->
      <div class="md:col-span-2 flex items-center justify-end gap-3 mt-4">
        <button
            type="button"
             @click="$router.push({ name: 'coupon' })"
            class="px-5 py-2.5 rounded-lg border border-gray-300 text-gray-700 hover:bg-gray-50 transition-colors"
        >
            Hủy
        </button>
        <button
          type="submit"
          class="px-5 py-2.5 rounded-lg bg-blue-600 hover:bg-blue-700 text-white font-medium shadow-lg hover:shadow-blue-500/30 transition-all disabled:opacity-50 flex items-center gap-2"
          :disabled="loading"
        >
          <Loader2Icon v-if="loading" class="w-4 h-4 animate-spin" />
          {{ loading ? "Đang cập nhật..." : "Cập Nhật Mã Giảm Giá" }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import api from "@/services/api";
import { useRoute, useRouter } from "vue-router";
import { useToast } from "vue-toastification";
import {
    ArrowLeftIcon,
    TagIcon,
    PercentIcon,
    DollarSignIcon,
    ActivityIcon,
    CalendarIcon,
    CalendarOffIcon,
    Loader2Icon,
    EditIcon,
    HashIcon
} from 'lucide-vue-next';

const toast = useToast();
const route = useRoute();
const router = useRouter();

const id = route.params.id;

const form = ref({
  code: "",
  discountType: "PERCENTAGE",
  discountValue: 0,
  startDate: "",
  endDate: "",
  status: "ACTIVE",
  scope: "",
  totalUses: 3,
});

const tiers = ref([]);
const loading = ref(false);

onMounted(async () => {
  await loadTiers();
  await loadCoupon();
});

const loadTiers = async () => {
  try {
    const res = await api.get("/membershiptier");
    tiers.value = res.data;
  } catch (err) {
    toast.error("Error loading membership tiers");
  }
};

const loadCoupon = async () => {
  try {
    const res = await api.get(`/coupons/${id}`);
    const coupon = res.data.coupon;

    form.value = {
      code: coupon.code,
      discountType: coupon.discountType,
      discountValue: coupon.discountValue,
      startDate: coupon.startDate?.substring(0, 10) || "",
      endDate: coupon.endDate?.substring(0, 10) || "",
      status: coupon.status,
      scope: coupon.scope,
      totalUses: coupon.totalUses || 3,
    };
  } catch (err) {
    toast.error("Failed to load coupon data");
  }
};

const submit = async () => {
  // Validate percentage value
  if (form.value.discountType === 'PERCENTAGE' && form.value.discountValue > 100) {
      toast.error("Percentage discount cannot exceed 100%");
      return;
  }
  
  loading.value = true;
  try {
    await api.put(`/coupons/${id}`, form.value);
    toast.success("Cập nhật coupon thành công!");
    router.push({ name: "coupon" });
  } catch (err) {
    toast.error("Cập nhật thất bại");
  } finally {
    loading.value = false;
  }
};
</script>
