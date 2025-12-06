<template>
  <div class="p-6 max-w-4xl mx-auto">
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold text-gray-800 dark:text-gray-100 flex items-center gap-2">
        <MonitorSmartphoneIcon class="w-6 h-6 text-blue-600" />
        Thêm Mã Giảm Giá
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

      <!-- Total Uses -->
      <div class="space-y-2">
        <label class="block text-sm font-semibold text-gray-700 dark:text-gray-300">Số Lần Sử Dụng/Thành Viên</label>
        <div class="relative">
             <input
              v-model.number="form.totalUses"
              type="number"
              min="1"
              placeholder="vd: 3"
              class="w-full pl-10 pr-4 py-2.5 rounded-lg border border-gray-300 dark:border-gray-700 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all bg-gray-50 dark:bg-gray-800"
              required
            />
            <HashIcon class="w-5 h-5 text-gray-400 absolute left-3 top-2.5" />
        </div>
      </div>

      <!-- Assign To Section -->
      <div class="md:col-span-2 grid grid-cols-1 md:grid-cols-2 gap-6 p-4 rounded-xl bg-gray-50 dark:bg-gray-800 border border-dashed border-gray-300 dark:border-gray-700">
          <div class="md:col-span-2">
              <label class="block text-sm font-semibold text-gray-700 dark:text-gray-300 mb-2">Gán Coupon Cho:</label>
              <div class="flex gap-4">
                  <label class="flex items-center gap-2 cursor-pointer">
                      <input type="radio" v-model="assignType" value="tier" class="w-4 h-4 text-blue-600">
                      <span>Hạng Thành Viên</span>
                  </label>
                  <label class="flex items-center gap-2 cursor-pointer">
                      <input type="radio" v-model="assignType" value="user" class="w-4 h-4 text-blue-600">
                      <span>Người Dùng Cụ Thể</span>
                  </label>
              </div>
          </div>

          <!-- Scope (Tier) -->
          <div v-if="assignType === 'tier'" class="md:col-span-2 space-y-2">
            <label class="block text-sm font-semibold text-gray-700 dark:text-gray-300">Chọn Hạng</label>
            <select
              v-model="form.scope"
              class="w-full px-4 py-2.5 rounded-lg border border-gray-300 dark:border-gray-700 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all bg-white dark:bg-gray-900"
            >
              <option disabled value="">-- Chọn Hạng --</option>
              <option v-for="tier in tiers" :key="tier.id" :value="tier.name">
                {{ tier.name }}
              </option>
            </select>
          </div>

          <!-- Users MultiSelect -->
          <div v-if="assignType === 'user'" class="md:col-span-2 space-y-2">
            <label class="block text-sm font-semibold text-gray-700 dark:text-gray-300">Chọn Người Dùng</label>
            <VueMultiselect
                v-model="selectedUsers"
                :options="users"
                :multiple="true"
                :close-on-select="false"
                :clear-on-select="false"
                :preserve-search="true"
                placeholder="Tìm kiếm & chọn người dùng"
                label="fullName"
                track-by="id"
                class="multiselect-custom"
            >
                <template #selection="{ values, isOpen }">
                    <span class="multiselect__single" v-if="values.length && !isOpen">{{ values.length }} người được chọn</span>
                </template>
            </VueMultiselect>
          </div>
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
          {{ loading ? "Đang lưu..." : "Tạo Mã Giảm Giá" }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import api from "@/services/api";
import { useRouter } from "vue-router";
import { useToast } from "vue-toastification";
import VueMultiselect from 'vue-multiselect'
import 'vue-multiselect/dist/vue-multiselect.css'
import {
    ArrowLeftIcon,
    TagIcon,
    PercentIcon,
    DollarSignIcon,
    ActivityIcon,
    CalendarIcon,
    CalendarOffIcon,
    Loader2Icon,
    MonitorSmartphoneIcon,
    HashIcon
} from 'lucide-vue-next';


const router = useRouter();
const toast = useToast();

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

const assignType = ref('tier'); // 'tier' or 'user'
const selectedUsers = ref([]);

const tiers = ref([]);
const users = ref([]);
const loading = ref(false);

onMounted(async () => {
  try {
    const resTiers = await api.get("/membershiptier");
    tiers.value = resTiers.data;

    const resUsers = await api.get("/members");
    users.value = resUsers.data;
  } catch (err) {
    toast.error("Error loading data.");
  }
});

const submit = async () => {
  if (assignType.value === 'tier' && !form.value.scope) {
      toast.error("Please select a Membership Tier.");
      return;
  }
  if (assignType.value === 'user' && selectedUsers.value.length === 0) {
      toast.error("Please select at least one user.");
      return;
  }
  
  // Validate percentage value
  if (form.value.discountType === 'PERCENTAGE' && form.value.discountValue > 100) {
      toast.error("Percentage discount cannot exceed 100%");
      return;
  }

  loading.value = true;
  try {
    const payload = {
        coupon: {
            ...form.value,
            scope: assignType.value === 'tier' ? form.value.scope : null // Clear scope if specific users
        },
        userIds: assignType.value === 'user' ? selectedUsers.value.map(u => u.id) : []
    };

    await api.post("/coupons/add", payload);
    toast.success("Tạo coupon thành công!");
    router.push({ name: 'coupon' });
  } catch (err) {
    toast.error("Có lỗi xảy ra.");
    console.error(err);
  } finally {
    loading.value = false;
  }
};
</script>

<style>
/* Custom override for vue-multiselect to match Tailwind input style */
.multiselect-custom .multiselect__tags {
    min-height: 42px;
    border-radius: 0.5rem; /* rounded-lg */
    border-color: #d1d5db; /* border-gray-300 */
}
.dark .multiselect-custom .multiselect__tags {
    background-color: #1f2937; /* bg-gray-800 */
    border-color: #374151; /* border-gray-700 */
    color: white;
}
.dark .multiselect-custom .multiselect__input,
.dark .multiselect-custom .multiselect__single {
    background-color: transparent;
    color: white;
}
.dark .multiselect-custom .multiselect__content-wrapper {
    background-color: #1f2937;
    border-color: #374151;
    color: white;
}
.dark .multiselect-custom .multiselect__option--highlight {
    background-color: #3b82f6; /* bg-blue-500 */
    color: white;
}
</style>
