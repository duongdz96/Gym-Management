<script setup lang="ts">
import { ref } from "vue"
import { useRouter } from "vue-router"
import api from "@/services/api"
import { useToast } from "vue-toastification"

const router = useRouter()
const toast = useToast();

// form data
const form = ref({
  name: "",
  priority: 1,
  description: ""
})

const submit = async () => {
  if (!form.value.name) {
    toast.warning("Vui lòng điền đầy đủ các trường bắt buộc.")
    return
  }

  const payload = {
    name: form.value.name,
    priority: form.value.priority,
    status: "Active"
  }

  try {
    const res = await api.post("/membershiptier", payload)

    if (res.status === 200 || res.status === 201) {
      toast.success("Thêm hạng thành viên thành công!")

      form.value = { name: "", priority: 1, description: "" }
      router.push({ name: "membership" })
    }
  } catch (err: any) {
    console.error("Lỗi khi thêm hạng thành viên:", err)
    toast.error("Thêm hạng thành viên thất bại. Vui lòng thử lại.")
  }
}
</script>

<template>
  <div class="p-4">
    <div class="flex items-center justify-between mb-4">
      <h1 class="text-xl font-semibold">Thêm Hạng Thành Viên</h1>
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
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Tên hạng</label>
        <input
          v-model="form.name"
          type="text"
          placeholder="Ví dụ: Vàng, Bạc, Đồng"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        />
      </div>

      <div>
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Độ ưu tiên</label>
        <select
          v-model.number="form.priority"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        >
          <option value="1">1 (Cao nhất)</option>
          <option value="2">2</option>
          <option value="3">3</option>
          <option value="4">4</option>
          <option value="5">5 (Thấp nhất)</option>
        </select>
      </div>

      <div class="md:col-span-2">
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Mô tả</label>
        <textarea
          v-model="form.description"
          rows="3"
          placeholder="Mô tả quyền lợi và đặc điểm của hạng thành viên này"
          class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
        ></textarea>
      </div>

      <div class="md:col-span-2 flex items-center gap-2">
        <button
          type="submit"
          class="px-4 py-2 rounded-md bg-emerald-600 hover:bg-emerald-700 text-white"
        >
          Thêm mới
        </button>
      </div>
    </form>
  </div>
</template>