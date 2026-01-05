<script setup lang="ts">
import { ref, onMounted } from "vue"
import { useRouter } from "vue-router"
import api from "@/services/api"
import { useToast } from "vue-toastification"

type MembershipTier = {
  id: number,
  name: string,
  priority: number,
  status: string,
}

const router = useRouter()
const toast = useToast();

// form data
const form = ref({
  name: "",
  duration: 1, // tính bằng tháng
  price: 0,
  description: ""
})

const membershipTiers = ref<MembershipTier[]>([])
const selectedTierId = ref<number | null>(null)

const submit = async () => {
  // 1. Kiểm tra điền đầy đủ
  if (!form.value.name || !form.value.duration || !form.value.price || !selectedTierId.value) {
    toast.warning("Vui lòng điền đầy đủ các trường.")
    return
  }

  // 2. Validate Tên gói (Max 50 char)
  if (form.value.name.length > 50) {
    toast.warning("Tên gói không được vượt quá 50 ký tự.")
    return
  }

  // 3. Validate Thời hạn (1 - 12 tháng)
  if (form.value.duration <= 0 || form.value.duration > 12) {
    toast.warning("Thời hạn phải từ 1 đến 12 tháng.")
    return
  }

  // 4. Validate Giá tiền (> 0 và < 1 tỷ)
  if (form.value.price <= 0 || form.value.price >= 1000000000) {
    toast.warning("Giá tiền phải lớn hơn 0 và nhỏ hơn 1 tỷ.")
    return
  }

  const selectedTier = membershipTiers.value.find(tier => tier.id === selectedTierId.value)
  if (!selectedTier) {
    toast.error("Vui lòng chọn hạng thành viên phù hợp.")
    return
  }

  const payload = {
    name: form.value.name,
    duration: `${form.value.duration} Months`,
    price: form.value.price,
    benefits: form.value.description || "",
    membershipTier: selectedTier
  }

  try {
    const res = await api.post("/membershipplan", payload)

    if (res.status === 200 || res.status === 201) {
      toast.success("Gói thành viên đã được thêm thành công!")

      form.value = { name: "", duration: 1, price: 0, description: "" }
      selectedTierId.value = null
      router.push({ name: "membership" })
    }
  } catch (err: any) {
    toast.error("Thêm gói thành viên thất bại. Vui lòng thử lại sau.")
  }
}

onMounted(async () => {
  try {
    const res = await api.get("/membershiptier")
    membershipTiers.value = res.data
  } catch (error) {
    console.error('Failed to load membership tiers:', error)
    toast.error("Tải danh sách hạng thành viên thất bại. Vui lòng thử lại sau.")
  }
})
</script>

<template>
  <div class="p-4">
    <div class="flex items-center justify-between mb-4">
      <h1 class="text-xl font-semibold">Thêm Gói Thành Viên</h1>
      <RouterLink
        :to="{ name: 'membership' }"
        class="px-3 py-2 rounded-md bg-gray-200 dark:bg-gray-800 text-gray-900 dark:text-gray-100"
      >
        Quay lại
      </RouterLink>
    </div>

    <form
      class="bg-white dark:bg-gray-900 border border-gray-200 dark:border-gray-800 rounded-lg p-4 grid grid-cols-1 md:grid-cols-2 gap-4"
      @submit.prevent="submit"
    >
      <div>
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Hạng thành viên</label>
        <select
          v-model="selectedTierId"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        >
          <option :value="null" disabled>Chọn hạng thành viên</option>
          <option v-for="tier in membershipTiers" :key="tier.id" :value="tier.id">
            {{ tier.name }}
          </option>
        </select>
      </div>

      <div>
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Tên gói</label>
        <input
          v-model="form.name"
          type="text"
          maxlength="50"
          placeholder="Ví dụ: Vàng 12 Tháng (Tối đa 50 ký tự)"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        />
        <p class="text-xs text-gray-500 mt-1 text-right">{{ form.name.length }}/50 ký tự</p>
      </div>

      <div>
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Thời hạn (Tháng)</label>
        <input
          v-model.number="form.duration"
          type="number"
          min="1"
          max="12"
          placeholder="Nhập số tháng (1-12)"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        />
      </div>

      <div>
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Giá (VND)</label>
        <input
          v-model.number="form.price"
          type="number"
          min="0"
          max="999999999"
          step="1000"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        />
        <p class="text-xs text-gray-500 mt-1">Nhập giá nhỏ hơn 1 tỷ</p>
      </div>

      <div class="md:col-span-2">
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Mô tả</label>
        <textarea
          v-model="form.description"
          rows="3"
          placeholder="Mô tả các quyền lợi của gói thành viên này"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
        ></textarea>
      </div>

      <div class="md:col-span-2 flex items-center gap-2">
        <button
          type="submit"
          class="px-4 py-2 rounded-md bg-emerald-600 hover:bg-emerald-700 text-white"
        >
          Thêm gói thành viên
        </button>
      </div>
    </form>
  </div>
</template>