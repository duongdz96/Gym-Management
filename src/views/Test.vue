<template>
  <div class="min-h-screen bg-gray-50 p-8">
    <div class="max-w-5xl mx-auto bg-white rounded-2xl shadow-lg p-8">
      <h1 class="text-3xl font-bold text-gray-800 mb-6 flex items-center gap-2">
        <PackageIcon class="w-6 h-6 text-blue-600" />
        Nhập sản phẩm từ nhà cung cấp
      </h1>

      <!-- Chọn Provider -->
      <div class="mb-8">
        <label class="block text-sm font-medium text-gray-700 mb-2">
          Chọn nhà cung cấp
        </label>
        <Multiselect
          v-model="selectedProvider"
          :options="providers"
          label="name"
          track-by="id"
          placeholder="Chọn nhà cung cấp..."
          @change="onProviderChange"
        />
      </div>

      <!-- Danh sách sản phẩm -->
      <div v-if="filteredProducts.length" class="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div
          v-for="product in filteredProducts"
          :key="product.id"
          class="border rounded-xl bg-white p-5 shadow-sm hover:shadow-md transition flex flex-col justify-between"
        >
          <div>
            <h3 class="text-lg font-semibold text-gray-900 truncate">
              {{ product.name }}
            </h3>
            <p class="text-gray-600 text-sm mt-1">Loại: {{ product.type }}</p>
            <p class="text-gray-600 text-sm">Giá: {{ formatCurrency(product.price) }}</p>
            <p class="text-gray-500 text-xs mt-1">Thương hiệu: {{ product.brand }}</p>
          </div>

          <button
            @click="addProduct(product)"
            class="mt-4 bg-blue-600 hover:bg-blue-700 text-white py-1.5 rounded-lg text-sm font-medium flex items-center justify-center gap-1 transition"
          >
            <PlusCircleIcon class="w-4 h-4" /> Thêm
          </button>
        </div>
      </div>

      <!-- Không có sản phẩm -->
      <div v-else-if="selectedProvider" class="text-gray-500 text-center py-12">
        <PackageXIcon class="w-8 h-8 mx-auto mb-2 text-gray-400" />
        Không có sản phẩm nào cho nhà cung cấp này.
      </div>

      <!-- Hóa đơn tổng kết -->
      <div v-if="selectedProducts.length" class="mt-10 border-t pt-6">
        <h2 class="text-xl font-semibold text-gray-800 mb-4 flex items-center gap-2">
          <ReceiptIcon class="w-5 h-5 text-green-600" /> Hóa đơn tổng kết
        </h2>

        <table class="w-full text-left border-collapse">
          <thead>
            <tr class="border-b text-gray-600">
              <th class="py-2">Tên sản phẩm</th>
              <th class="py-2 text-right">Giá</th>
              <th class="py-2 text-center">Số lượng</th>
              <th class="py-2 text-right">Thành tiền</th>
              <th class="py-2 text-center">Hành động</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="(item, index) in selectedProducts"
              :key="index"
              class="border-b hover:bg-gray-50 transition"
            >
              <td class="py-2">{{ item.name }}</td>
              <td class="py-2 text-right">{{ formatCurrency(item.price) }}</td>
              <td class="py-2 text-center">
                <input
                  type="number"
                  v-model.number="item.quantity"
                  min="1"
                  class="border rounded-md w-16 text-center py-1 text-sm"
                />
              </td>
              <td class="py-2 text-right">
                {{ formatCurrency(item.price * item.quantity) }}
              </td>
              <td class="py-2">
                <div class="flex justify-center">
                    <button
                    @click="removeProduct(item)"
                    class="text-red-600 hover:text-red-800 flex items-center gap-1 text-sm"
                    >
                    <Trash2Icon class="w-4 h-4" />
                    <span>Xóa</span>
                    </button>
                </div>
                </td>
            </tr>
          </tbody>
        </table>

        <div class="mt-4 flex justify-end items-center text-gray-800 text-lg font-semibold">
          Tổng cộng: <span class="ml-2 text-green-600">{{ formatCurrency(totalPrice) }}</span>
        </div>

        <div class="mt-6 text-right">
          <button
            @click="submitOrder"
            class="bg-green-600 hover:bg-green-700 text-white px-6 py-2 rounded-lg font-medium flex items-center gap-2 ml-auto transition"
          >
            <CheckCircle2Icon class="w-5 h-5" /> Xác nhận nhập hàng
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from "vue"
import Multiselect from "vue-multiselect"
import "vue-multiselect/dist/vue-multiselect.min.css"
import {
  PackageIcon,
  PlusCircleIcon,
  Trash2Icon,
  CheckCircle2Icon,
  ReceiptIcon,
  PackageXIcon,
} from "lucide-vue-next"

// --- Dữ liệu nhà cung cấp ---
const providers = ref([
  { id: 1, name: "Hoàng Gia Sport", brand: "Nike" },
  { id: 2, name: "Thể Thao Việt", brand: "Adidas" },
  { id: 3, name: "Gym Pro Supplier", brand: "Optimum Nutrition" },
  { id: 4, name: "ABC Fitness", brand: "Under Armour" },
  { id: 5, name: "Thực phẩm thể hình HN", brand: "Dymatize" },
])

// --- Dữ liệu sản phẩm ---
const products = ref([
  { id: 1, name: "T-Shirt Gym", type: "clothes", price: 250000, brand: "Nike", quantity: 83 },
  { id: 2, name: "Protein Powder 2kg", type: "powder", price: 850000, brand: "Optimum Nutrition", quantity: 11 },
  { id: 3, name: "Shorts Training", type: "clothes", price: 180000, brand: "Adidas", quantity: 51 },
  { id: 4, name: "Mass Gainer 5kg", type: "powder", price: 1200000, brand: "Dymatize", quantity: 16 },
  { id: 5, name: "Gym Gloves", type: "clothes", price: 120000, brand: "Under Armour", quantity: 28 },
  { id: 6, name: "Lavie water bottle 1L", type: "drinks", price: 10000, brand: "Lavie", quantity: 0 },
  { id: 7, name: "Red Bull Energy Drink 250ml", type: "drinks", price: 15000, brand: "Red Bull", quantity: 0 },
  { id: 8, name: "Test", type: "powder", price: 200000, brand: "Adidas", quantity: 5 },
  { id: 9, name: "abc", type: "drinks", price: 10000000, brand: "abccc", quantity: 0 },
])

// --- Trạng thái ---
const selectedProvider = ref(null)
const selectedProducts = ref([])

const filteredProducts = computed(() => {
  if (!selectedProvider.value) return []
  return products.value.filter((p) => p.brand === selectedProvider.value.brand)
})

const addProduct = (product) => {
  const exist = selectedProducts.value.find((p) => p.id === product.id)
  if (!exist) {
    selectedProducts.value.push({ ...product, quantity: 1 })
  }
}

const removeProduct = (product) => {
  selectedProducts.value = selectedProducts.value.filter((p) => p.id !== product.id)
}

const totalPrice = computed(() =>
  selectedProducts.value.reduce((sum, p) => sum + p.price * p.quantity, 0)
)

const formatCurrency = (v) => v.toLocaleString("vi-VN") + " ₫"

const onProviderChange = () => {
  selectedProducts.value = [] // reset khi đổi provider
}

watch(selectedProvider, () => {
  selectedProducts.value = []
})

const submitOrder = () => {
  alert(`Bạn đã nhập ${selectedProducts.value.length} sản phẩm, tổng cộng ${formatCurrency(totalPrice.value)}.`)
}
</script>

<style scoped>
.multiselect {
  --tw-ring-color: transparent !important;
}
</style>
