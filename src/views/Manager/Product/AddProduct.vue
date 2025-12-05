<script setup lang="ts">
import api from "@/services/api"
import { ref } from "vue"
import { useRouter } from "vue-router"

const router = useRouter()

type ProductForm = {
  name: string
  type: string
  price: number | null
  brand?: string
  quantity? : number
  unit: string
}

const form = ref<ProductForm>({
  name: "",
  type: "",
  price: null,
  brand: "",
  quantity: 0,
  unit: "",
})

const imageFile = ref<File | null>(null)

function handleFileUpload(event: Event) {
  const target = event.target as HTMLInputElement
  if (target.files && target.files.length > 0) {
    imageFile.value = target.files[0]
  } else {
    imageFile.value = null
  }
}

async function submit() {
  if (!form.value.name || !form.value.type || !form.value.price || !form.value.unit) {
    alert("Please fill in all required fields.")
    return
  }

  const formData = new FormData()
  formData.append('product', JSON.stringify({
    name: form.value.name,
    type: form.value.type,
    price: form.value.price,
    brand: form.value.brand || null,
    unit: form.value.unit,
    quantity: form.value.quantity,
  }))

  if (imageFile.value) {
    formData.append('image', imageFile.value)
  }

  try {
    const res = await api.post("/product", formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })

    if (res.status === 200 || res.status === 201) {
      alert("Product has been added successfully!")

      form.value = { 
        name: "", 
        type: "", 
        price: null, 
        brand: "", 
        unit: "", 
      }
      imageFile.value = null
      // Reset file input manually if needed, but simple ref reset is enough for logic
    }
  } catch (err: any) {
    console.error("Error adding product:", err)
    alert("Failed to add product. Please try again.")
  }
}

</script>

<template>
  <div class="max-w-3xl mx-auto p-6">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold text-gray-800 dark:text-gray-100">
        Add New Product
      </h1>
      <RouterLink
        :to="{ name: 'product' }"
        class="px-3 py-2 rounded-md bg-gray-100 hover:bg-gray-200 dark:bg-gray-800 dark:hover:bg-gray-700 text-gray-900 dark:text-gray-100 transition-colors"
      >
        ← Back
      </RouterLink>
    </div>

    <!-- Form Card -->
    <form
      @submit.prevent="submit"
      class="bg-white dark:bg-gray-900 border border-gray-200 dark:border-gray-700 rounded-xl shadow-sm p-6 space-y-5"
    >
      <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
        <!-- Name -->
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Product Name <span class="text-red-500">*</span>
          </label>
          <input
            v-model="form.name"
            type="text"
            class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none"
            placeholder="e.g. Gym Gloves"
            required
          />
        </div>

        <!-- Type -->
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Type <span class="text-red-500">*</span>
          </label>
          <select
            v-model="form.type"
            class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none"
            required
          >
            <option value="" disabled>Select a type</option>
            <option value="Equipment">Equipment</option>
              <option value="Accessory">Accessory</option>
              <option value="Supplement">Supplement</option>
              <option value="clothes">Clothes</option>
              <option value="powder">Powder</option>
              <option value="drinks">Drinks</option>
              <option value="PT">PT</option>
              <option value="Membership">Membership</option>
          </select>
        </div>

        <!-- Price -->
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Price (VND) <span class="text-red-500">*</span>
          </label>
          <input
            v-model.number="form.price"
            type="number"
            min="0"
            step="1000"
            class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none"
            placeholder="e.g. 100000"
            required
          />
        </div>

        <!-- Brand -->
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Brand
          </label>
          <input
            v-model="form.brand"
            type="text"
            class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none"
            placeholder="e.g. Adidas"
          />
        </div>

        <!-- Unit -->
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Unit <span class="text-red-500">*</span>
          </label>
          <select
            v-model="form.unit"
            class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none"
            required
          >
            <option value="" disabled>Select a unit</option>
            <option value="Cái">Cái</option>
            <option value="Hộp">Hộp</option>
            <option value="Chai">Chai</option>
            <option value="Gói">Gói</option>
            <option value="Lon">Lon</option>
            <option value="Thùng">Thùng</option>
            <option value="Lọ">Lọ</option>
            <option value="Kg">Kg</option>
          </select>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Số lượng
          </label>
          <input
            v-model="form.quantity"
            type="number"
            class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none"
            placeholder="e.g. Adidas"
          />
        </div>

        <!-- Image -->
        <div class="md:col-span-2">
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Product Image
          </label>
          <input
            @change="handleFileUpload"
            type="file"
            accept="image/*"
            class="w-full px-3 py-2 rounded-md border border-gray-300 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none"
          />
          <p class="text-xs text-gray-500 mt-1">If no image is selected, a default image will be used.</p>
        </div>
      </div>

      <!-- Buttons -->
      <div class="flex justify-end gap-3 pt-4 border-t border-gray-200 dark:border-gray-700">
        <RouterLink
          :to="{ name: 'product' }"
          class="px-4 py-2 rounded-md bg-gray-200 hover:bg-gray-300 dark:bg-gray-800 dark:hover:bg-gray-700 text-gray-900 dark:text-gray-100 transition-colors"
        >
          Cancel
        </RouterLink>

        <button
          type="submit"
          class="px-5 py-2 rounded-md bg-blue-600 hover:bg-blue-700 text-white font-medium transition-colors"
        >
          Add Product
        </button>
      </div>
    </form>
  </div>
</template>
